"""
Client API - Order module
客户端订单接口

提供订单创建、查询、取消等功能
"""
import logging
from datetime import datetime, timedelta, timezone
from decimal import Decimal

from fastapi import APIRouter, Depends, Header
from pydantic import BaseModel, Field, field_validator
from sqlalchemy import select
from sqlalchemy.ext.asyncio import AsyncSession
import ulid

from app.core.config import get_settings
from app.core.database import get_db
from app.core.exceptions import (
    AppException, 
    ErrorCode, 
    NotFoundException,
    ValidationException
)
from app.models.order import Order
from app.models.plan import Plan
from app.models.payment_address import PaymentAddress
from app.schemas.base import Response
from app.services.address_pool import AddressPoolService
from app.services.fx_rate import (
    FXRateService, 
    convert_usd_to_crypto,
    get_sol_usd_rate,
    get_usdt_usd_rate
)

logger = logging.getLogger(__name__)
router = APIRouter(prefix="/orders", tags=["orders"])


# ========== 请求/响应模型 ==========

class CreateOrderRequest(BaseModel):
    """创建订单请求"""
    plan_id: str = Field(..., description="套餐ID")
    purchase_type: str = Field(..., description="购买类型: new(新购) | renew(续费)")
    asset_code: str = Field(..., description="支付资产: SOL | USDT_TRC20")
    # 续费专用
    client_user_id: str = Field(default=None, description="续费时的客户端用户ID")
    marzban_username: str = Field(default=None, description="续费时的 Marzban 用户名")
    
    @field_validator('purchase_type')
    @classmethod
    def validate_purchase_type(cls, v: str) -> str:
        if v not in ['new', 'renew']:
            raise ValueError('purchase_type 必须是 new 或 renew')
        return v
    
    @field_validator('asset_code')
    @classmethod
    def validate_asset_code(cls, v: str) -> str:
        supported = ['SOL', 'USDT_TRC20']
        if v not in supported:
            raise ValueError(f'asset_code 必须是 {supported} 之一')
        return v


class OrderResponseData(BaseModel):
    """订单响应数据"""
    order_id: str = Field(..., description="订单ID")
    order_no: str = Field(..., description="订单号（展示用）")
    plan_id: str = Field(..., description="套餐ID")
    purchase_type: str = Field(..., description="购买类型")
    
    # 支付信息
    chain: str = Field(..., description="区块链")
    asset_code: str = Field(..., description="支付资产")
    receive_address: str = Field(..., description="收款地址")
    amount_crypto: str = Field(..., description="需支付的加密货币金额")
    amount_usd: str = Field(..., description="USD 金额")
    fx_rate: str = Field(..., description="锁定汇率")
    
    # 状态
    status: str = Field(..., description="订单状态")
    expires_at: datetime = Field(..., description="过期时间")
    created_at: datetime = Field(..., description="创建时间")
    
    # 续费信息（仅续费订单）
    client_user_id: str = Field(default=None, description="客户端用户ID")
    marzban_username: str = Field(default=None, description="Marzban 用户名")


class OrderDetailResponse(BaseModel):
    """订单详情响应"""
    order: OrderResponseData


class OrderStatusResponse(BaseModel):
    """订单状态响应"""
    order_id: str
    status: str
    tx_hash: str = Field(default=None, description="交易哈希")
    confirm_count: int = Field(default=0, description="确认数")
    paid_at: datetime = Field(default=None, description="支付时间")


# ========== API 端点 ==========

@router.post(
    "",
    response_model=Response[OrderResponseData],
    summary="创建订单",
    description="创建新订单，系统会分配收款地址并锁定汇率"
)
async def create_order(
    request: CreateOrderRequest,
    x_device_id: str = Header(..., description="设备ID"),
    x_client_version: str = Header(..., description="客户端版本"),
    db: AsyncSession = Depends(get_db)
) -> Response[OrderResponseData]:
    """
    创建新订单
    
    流程：
    1. 验证套餐存在且启用
    2. 获取实时汇率（带缓存和故障转移）
    3. 计算加密货币金额
    4. 分配收款地址
    5. 创建订单记录
    """
    settings = get_settings()
    
    # 1. 验证套餐
    result = await db.execute(
        select(Plan).where(Plan.id == request.plan_id, Plan.enabled == True)
    )
    plan = result.scalar_one_or_none()
    
    if not plan:
        raise NotFoundException("套餐不存在或已下架")
    
    # 2. 确定链和资产
    chain, asset_code = _resolve_chain_and_asset(request.asset_code)
    
    # 3. 获取汇率并锁定价格
    fx_rate, rate_error = await _get_fx_rate_safe(asset_code)
    
    if fx_rate is None:
        # 汇率获取失败，返回优雅错误
        logger.error(f"创建订单失败: {rate_error}")
        raise AppException(
            code=ErrorCode.SERVICE_UNAVAILABLE,
            message=f"汇率服务暂不可用: {rate_error}",
            status_code=503,
            data={"error": rate_error}
        )
    
    # 4. 计算加密货币金额
    amount_usd = plan.price_usd
    amount_crypto, convert_error = await _convert_amount_safe(
        amount_usd, asset_code, fx_rate
    )
    
    if amount_crypto is None:
        logger.error(f"金额转换失败: {convert_error}")
        raise AppException(
            code=ErrorCode.INTERNAL_ERROR,
            message="金额计算失败，请稍后重试",
            status_code=500
        )
    
    # 5. 分配收款地址
    address_service = AddressPoolService(db)
    try:
        address = await address_service.allocate_address(chain, asset_code, str(ulid.new().str))
    except AppException as e:
        if e.code == ErrorCode.ADDRESS_POOL_EMPTY:
            raise AppException(
                code=ErrorCode.ADDRESS_POOL_EMPTY,
                message="支付地址暂时不足，请稍后重试",
                status_code=409
            )
        raise
    
    # 6. 创建订单
    order_id = str(ulid.new().str)
    order_no = _generate_order_no()
    expires_at = datetime.now(timezone.utc) + timedelta(
        minutes=settings.order_expire_minutes
    )
    
    order = Order(
        id=order_id,
        order_no=order_no,
        purchase_type=request.purchase_type,
        plan_id=request.plan_id,
        client_user_id=request.client_user_id,
        marzban_username=request.marzban_username,
        chain=chain,
        asset_code=asset_code,
        receive_address=address.address,
        amount_crypto=amount_crypto,
        amount_usd_locked=amount_usd,
        fx_rate_locked=fx_rate,
        status="pending_payment",
        expires_at=expires_at,
        client_device_id=x_device_id,
        client_version=x_client_version,
    )
    
    db.add(order)
    await db.flush()
    
    logger.info(
        f"订单创建成功: {order_no}, asset={asset_code}, "
        f"amount={amount_crypto}, rate={fx_rate}"
    )
    
    return Response(
        code="SUCCESS",
        message="订单创建成功",
        data=OrderResponseData(
            order_id=order_id,
            order_no=order_no,
            plan_id=request.plan_id,
            purchase_type=request.purchase_type,
            chain=chain,
            asset_code=asset_code,
            receive_address=address.address,
            amount_crypto=str(amount_crypto),
            amount_usd=str(amount_usd),
            fx_rate=str(fx_rate),
            status="pending_payment",
            expires_at=expires_at,
            created_at=order.created_at,
            client_user_id=request.client_user_id,
            marzban_username=request.marzban_username,
        )
    )


@router.get(
    "/{order_id}",
    response_model=Response[OrderDetailResponse],
    summary="获取订单详情",
    description="根据订单ID获取订单详情"
)
async def get_order(
    order_id: str,
    db: AsyncSession = Depends(get_db)
) -> Response[OrderDetailResponse]:
    """获取订单详情"""
    result = await db.execute(
        select(Order).where(Order.id == order_id)
    )
    order = result.scalar_one_or_none()
    
    if not order:
        raise NotFoundException("订单不存在")
    
    return Response(
        code="SUCCESS",
        message="success",
        data=OrderDetailResponse(
            order=OrderResponseData(
                order_id=order.id,
                order_no=order.order_no,
                plan_id=order.plan_id,
                purchase_type=order.purchase_type,
                chain=order.chain,
                asset_code=order.asset_code,
                receive_address=order.receive_address,
                amount_crypto=str(order.amount_crypto),
                amount_usd=str(order.amount_usd_locked),
                fx_rate=str(order.fx_rate_locked),
                status=order.status,
                expires_at=order.expires_at,
                created_at=order.created_at,
                client_user_id=order.client_user_id,
                marzban_username=order.marzban_username,
            )
        )
    )


@router.get(
    "/{order_id}/status",
    response_model=Response[OrderStatusResponse],
    summary="获取订单状态",
    description="获取订单支付状态（轻量级查询）"
)
async def get_order_status(
    order_id: str,
    db: AsyncSession = Depends(get_db)
) -> Response[OrderStatusResponse]:
    """获取订单状态"""
    result = await db.execute(
        select(Order).where(Order.id == order_id)
    )
    order = result.scalar_one_or_none()
    
    if not order:
        raise NotFoundException("订单不存在")
    
    return Response(
        code="SUCCESS",
        message="success",
        data=OrderStatusResponse(
            order_id=order.id,
            status=order.status,
            tx_hash=order.tx_hash,
            confirm_count=order.confirm_count or 0,
            paid_at=order.paid_at,
        )
    )


@router.post(
    "/{order_id}/cancel",
    response_model=Response[dict],
    summary="取消订单",
    description="取消待支付的订单"
)
async def cancel_order(
    order_id: str,
    db: AsyncSession = Depends(get_db)
) -> Response[dict]:
    """取消订单"""
    result = await db.execute(
        select(Order).where(Order.id == order_id)
    )
    order = result.scalar_one_or_none()
    
    if not order:
        raise NotFoundException("订单不存在")
    
    if order.status != "pending_payment":
        raise ValidationException("只有待支付订单可以取消")
    
    # 更新状态
    order.status = "cancelled"
    
    # 释放地址
    address_service = AddressPoolService(db)
    result = await db.execute(
        select(PaymentAddress).where(
            PaymentAddress.address == order.receive_address,
            PaymentAddress.chain == order.chain
        )
    )
    address = result.scalar_one_or_none()
    if address:
        await address_service.release_address(address.id)
    
    await db.flush()
    
    return Response(
        code="SUCCESS",
        message="订单已取消",
        data={"order_id": order_id, "status": "cancelled"}
    )


# ========== 辅助函数 ==========

def _resolve_chain_and_asset(asset_code: str) -> tuple[str, str]:
    """
    解析资产代码对应的链和标准化资产代码
    
    Returns:
        tuple: (chain, normalized_asset_code)
    """
    mapping = {
        "SOL": ("solana", "SOL"),
        "USDT_TRC20": ("tron", "USDT_TRC20"),
    }
    return mapping.get(asset_code, ("solana", asset_code))


async def _get_fx_rate_safe(asset_code: str) -> tuple[Optional[Decimal], Optional[str]]:
    """
    安全地获取汇率
    
    Returns:
        tuple: (汇率, 错误信息)
        - 成功: (rate, None)
        - 失败: (None, error_message)
    """
    try:
        service = FXRateService()
        
        if asset_code == "SOL":
            rate = await service.get_sol_usd_rate()
        elif asset_code in ["USDT_TRC20", "USDT"]:
            rate = await service.get_usdt_usd_rate()
        else:
            rate = await service.get_rate(asset_code)
        
        if rate is None:
            return None, "无法获取汇率"
        
        return rate, None
        
    except Exception as e:
        logger.exception(f"获取汇率异常: {e}")
        return None, f"汇率服务异常: {str(e)}"


async def _convert_amount_safe(
    amount_usd: Decimal, 
    asset_code: str,
    fx_rate: Decimal
) -> tuple[Optional[Decimal], Optional[str]]:
    """
    安全地转换金额
    
    Returns:
        tuple: (crypto_amount, error_message)
    """
    try:
        from decimal import ROUND_HALF_UP
        
        if fx_rate <= 0:
            return None, "汇率无效"
        
        # 计算加密货币金额
        if asset_code in ["USDT_TRC20", "USDT"]:
            precision = Decimal("0.000001")  # USDT 6位小数
        else:
            precision = Decimal("0.000000001")  # SOL 9位小数
        
        amount_crypto = (amount_usd / fx_rate).quantize(precision, rounding=ROUND_HALF_UP)
        
        return amount_crypto, None
        
    except Exception as e:
        logger.exception(f"金额转换异常: {e}")
        return None, f"金额计算失败: {str(e)}"


def _generate_order_no() -> str:
    """生成订单号"""
    # 使用 ULID 前 10 位 + 时间戳
    from datetime import datetime
    ulid_str = str(ulid.new().str)
    now = datetime.now()
    return f"ORD{now.strftime('%y%m%d%H%M')}{ulid_str[:8].upper()}"
