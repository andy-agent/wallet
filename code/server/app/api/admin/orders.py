"""
Admin API - Order module
管理端订单接口
"""
from datetime import datetime, timedelta
from decimal import Decimal
from typing import List, Optional

import jwt
from fastapi import APIRouter, Depends, Header, Query
from pydantic import BaseModel, Field
from sqlalchemy import func, select, and_
from sqlalchemy.ext.asyncio import AsyncSession

from app.core.config import get_settings
from app.core.database import get_db
from app.core.exceptions import NotFoundException, UnauthorizedException, ForbiddenException
from app.core.rate_limit import admin_rate_limit, strict_rate_limit
from app.models.order import Order
from app.models.plan import Plan
from app.schemas.base import Response, Pagination, PaginatedResponse

router = APIRouter(prefix="/orders", tags=["admin-orders"])

settings = get_settings()


# ============ JWT Admin Auth ============

class AdminTokenPayload(BaseModel):
    """Admin JWT token payload"""
    admin_id: str
    role: str
    permissions: List[str]


def verify_admin_token(authorization: str = Header(None)) -> AdminTokenPayload:
    """
    Verify admin JWT token from Authorization header
    
    Expected format: Bearer <jwt_token>
    
    Args:
        authorization: Authorization header value
        
    Returns:
        AdminTokenPayload: Decoded admin token payload
        
    Raises:
        UnauthorizedException: If token is missing or invalid
        ForbiddenException: If token validation fails
    """
    if not authorization or not authorization.startswith("Bearer "):
        raise UnauthorizedException(message="Missing or invalid Authorization header. Expected: Bearer <token>")
    
    token = authorization.replace("Bearer ", "")
    
    try:
        payload = jwt.decode(
            token,
            settings.jwt_secret,
            algorithms=[settings.jwt_algorithm]
        )
        
        # Verify token type is admin
        token_type = payload.get("type")
        if token_type != "admin_access":
            raise ForbiddenException(message="Invalid token type. Admin access required.")
        
        # Extract admin identity
        admin_id = payload.get("sub")
        if not admin_id:
            raise ForbiddenException(message="Invalid token: missing admin identity")
        
        return AdminTokenPayload(
            admin_id=admin_id,
            role=payload.get("role", "admin"),
            permissions=payload.get("permissions", [])
        )
        
    except jwt.ExpiredSignatureError:
        raise UnauthorizedException(message="Token has expired")
    except jwt.InvalidTokenError as e:
        raise ForbiddenException(message=f"Invalid token: {str(e)}")


# ============ Schemas ============

class OrderListItem(BaseModel):
    """订单列表项"""
    id: str = Field(..., description="订单ID")
    order_no: str = Field(..., description="订单编号")
    purchase_type: str = Field(..., description="购买类型: new/renew")
    plan_id: str = Field(..., description="套餐ID")
    plan_name: str = Field(..., description="套餐名称")
    marzban_username: Optional[str] = Field(None, description="Marzban用户名")
    chain: str = Field(..., description="链: solana/tron")
    asset_code: str = Field(..., description="资产代码")
    amount_crypto: str = Field(..., description="加密货币金额")
    amount_usd_locked: str = Field(..., description="锁定USD金额")
    status: str = Field(..., description="订单状态")
    expires_at: datetime = Field(..., description="过期时间")
    tx_hash: Optional[str] = Field(None, description="交易哈希")
    paid_at: Optional[datetime] = Field(None, description="支付时间")
    confirmed_at: Optional[datetime] = Field(None, description="确认时间")
    fulfilled_at: Optional[datetime] = Field(None, description="开通时间")
    error_code: Optional[str] = Field(None, description="错误代码")
    error_message: Optional[str] = Field(None, description="错误信息")
    created_at: datetime = Field(..., description="创建时间")
    
    class Config:
        from_attributes = True


class OrderListResponseData(BaseModel):
    """订单列表响应数据"""
    orders: List[OrderListItem]


class OrderDetailResponse(OrderListItem):
    """订单详情响应"""
    # 支付信息
    receive_address: str = Field(..., description="收款地址")
    fx_rate_locked: str = Field(..., description="锁定汇率")
    confirm_count: int = Field(default=0, description="确认数")
    
    # 链上信息
    tx_from: Optional[str] = Field(None, description="付款地址")
    
    # 客户端信息
    user_id: str = Field(..., description="用户ID")
    client_version: str = Field(..., description="客户端版本")


class OrderStatsItem(BaseModel):
    """状态统计项"""
    status: str = Field(..., description="状态")
    count: int = Field(..., description="数量")


class OrderStatsResponseData(BaseModel):
    """订单统计数据响应"""
    today_orders: int = Field(..., description="今日订单数")
    today_paid_amount_usd: str = Field(..., description="今日支付金额(USD)")
    total_orders: int = Field(..., description="总订单数")
    total_paid_amount_usd: str = Field(..., description="总支付金额(USD)")
    status_counts: List[OrderStatsItem] = Field(..., description="各状态数量")


# ============ Endpoints ============

@router.get(
    "",
    response_model=PaginatedResponse[OrderListItem],
    summary="获取订单列表",
    description="获取订单列表，支持分页和筛选。需要 admin_token 认证。"
)
async def list_orders(
    status: Optional[str] = Query(None, description="按状态筛选"),
    order_no: Optional[str] = Query(None, description="按订单号模糊搜索"),
    tx_hash: Optional[str] = Query(None, description="按交易哈希搜索"),
    start_date: Optional[datetime] = Query(None, description="开始日期 (ISO格式)"),
    end_date: Optional[datetime] = Query(None, description="结束日期 (ISO格式)"),
    page: int = Query(1, ge=1, description="页码"),
    size: int = Query(20, ge=1, le=100, description="每页数量"),
    db: AsyncSession = Depends(get_db),
    admin_token: AdminTokenPayload = Depends(verify_admin_token),
    _: None = Depends(admin_rate_limit)
) -> PaginatedResponse[OrderListItem]:
    """
    获取订单列表（分页）
    
    - 支持按状态、订单号、交易哈希、日期范围筛选
    - 需要 admin_token 认证
    """
    # 构建查询条件
    conditions = []
    if status:
        conditions.append(Order.status == status)
    if order_no:
        conditions.append(Order.order_no.ilike(f"%{order_no}%"))
    if tx_hash:
        conditions.append(Order.tx_hash.ilike(f"%{tx_hash}%"))
    if start_date:
        conditions.append(Order.created_at >= start_date)
    if end_date:
        conditions.append(Order.created_at <= end_date)
    
    # 查询总数
    count_query = select(func.count(Order.id))
    if conditions:
        count_query = count_query.where(and_(*conditions))
    total_result = await db.execute(count_query)
    total = total_result.scalar()
    
    # 查询订单列表（关联套餐名称）
    query = (
        select(Order, Plan.name.label("plan_name"))
        .outerjoin(Plan, Order.plan_id == Plan.id)
    )
    if conditions:
        query = query.where(and_(*conditions))
    
    query = (
        query
        .order_by(Order.created_at.desc())
        .offset((page - 1) * size)
        .limit(size)
    )
    
    result = await db.execute(query)
    rows = result.all()
    
    # 转换为响应模型
    orders = []
    for order, plan_name in rows:
        orders.append(OrderListItem(
            id=order.id,
            order_no=order.order_no,
            purchase_type=order.purchase_type,
            plan_id=order.plan_id,
            plan_name=plan_name or "未知套餐",
            marzban_username=order.marzban_username,
            chain=order.chain,
            asset_code=order.asset_code,
            amount_crypto=str(order.amount_crypto),
            amount_usd_locked=str(order.amount_usd_locked),
            status=order.status,
            expires_at=order.expires_at,
            tx_hash=order.tx_hash,
            paid_at=order.paid_at,
            confirmed_at=order.confirmed_at,
            fulfilled_at=order.fulfilled_at,
            error_code=order.error_code,
            error_message=order.error_message,
            created_at=order.created_at
        ))
    
    # 计算总页数
    pages = (total + size - 1) // size if total > 0 else 1
    
    return PaginatedResponse(
        code="SUCCESS",
        message="success",
        data=orders,
        pagination=Pagination(
            total=total,
            page=page,
            size=size,
            pages=pages
        )
    )


@router.get(
    "/{order_id}",
    response_model=Response[OrderDetailResponse],
    summary="获取订单详情",
    description="获取单个订单的详细信息，包含支付信息、链上信息、开通信息。需要 admin_token 认证。"
)
async def get_order(
    order_id: str,
    db: AsyncSession = Depends(get_db),
    admin_token: AdminTokenPayload = Depends(verify_admin_token),
    _: None = Depends(admin_rate_limit)
) -> Response[OrderDetailResponse]:
    """
    获取订单详情
    
    - 包含支付信息、链上信息、开通信息
    - 需要 admin_token 认证
    """
    # 查询订单（关联套餐名称）
    query = (
        select(Order, Plan.name.label("plan_name"))
        .outerjoin(Plan, Order.plan_id == Plan.id)
        .where(Order.id == order_id)
    )
    result = await db.execute(query)
    row = result.one_or_none()
    
    if not row:
        raise NotFoundException(message="订单不存在")
    
    order, plan_name = row
    
    order_detail = OrderDetailResponse(
        id=order.id,
        order_no=order.order_no,
        purchase_type=order.purchase_type,
        plan_id=order.plan_id,
        plan_name=plan_name or "未知套餐",
        marzban_username=order.marzban_username,
        chain=order.chain,
        asset_code=order.asset_code,
        amount_crypto=str(order.amount_crypto),
        amount_usd_locked=str(order.amount_usd_locked),
        status=order.status,
        expires_at=order.expires_at,
        tx_hash=order.tx_hash,
        paid_at=order.paid_at,
        confirmed_at=order.confirmed_at,
        fulfilled_at=order.fulfilled_at,
        error_code=order.error_code,
        error_message=order.error_message,
        created_at=order.created_at,
        # 详情额外字段
        receive_address=order.receive_address,
        fx_rate_locked=str(order.fx_rate_locked),
        confirm_count=order.confirm_count or 0,
        tx_from=order.tx_from,
        user_id=order.user_id,
        client_version=order.client_version
    )
    
    return Response(
        code="SUCCESS",
        message="success",
        data=order_detail
    )


@router.get(
    "/stats/summary",
    response_model=Response[OrderStatsResponseData],
    summary="获取订单统计",
    description="获取订单统计数据，包括今日订单数、支付金额、各状态数量。需要 admin_token 认证。"
)
async def get_order_stats(
    db: AsyncSession = Depends(get_db),
    admin_token: AdminTokenPayload = Depends(verify_admin_token),
    _: None = Depends(admin_rate_limit)
) -> Response[OrderStatsResponseData]:
    """
    获取订单统计
    
    - 今日订单数、支付金额
    - 总订单数、总支付金额
    - 各状态订单数量
    - 需要 admin_token 认证
    """
    # 今日开始时间
    today_start = datetime.now().replace(hour=0, minute=0, second=0, microsecond=0)
    
    # 今日订单数
    today_count_query = select(func.count(Order.id)).where(Order.created_at >= today_start)
    today_result = await db.execute(today_count_query)
    today_orders = today_result.scalar() or 0
    
    # 今日支付金额（已确认或已完成的订单）
    today_paid_query = select(func.coalesce(func.sum(Order.amount_usd_locked), Decimal("0"))).where(
        and_(
            Order.created_at >= today_start,
            Order.status.in_(["confirmed", "fulfilled"])
        )
    )
    today_paid_result = await db.execute(today_paid_query)
    today_paid_amount = today_paid_result.scalar() or Decimal("0")
    
    # 总订单数
    total_count_query = select(func.count(Order.id))
    total_result = await db.execute(total_count_query)
    total_orders = total_result.scalar() or 0
    
    # 总支付金额
    total_paid_query = select(func.coalesce(func.sum(Order.amount_usd_locked), Decimal("0"))).where(
        Order.status.in_(["confirmed", "fulfilled"])
    )
    total_paid_result = await db.execute(total_paid_query)
    total_paid_amount = total_paid_result.scalar() or Decimal("0")
    
    # 各状态数量统计
    status_count_query = (
        select(Order.status, func.count(Order.id).label("count"))
        .group_by(Order.status)
    )
    status_result = await db.execute(status_count_query)
    status_counts = [
        OrderStatsItem(status=status, count=count)
        for status, count in status_result.all()
    ]
    
    return Response(
        code="SUCCESS",
        message="success",
        data=OrderStatsResponseData(
            today_orders=today_orders,
            today_paid_amount_usd=str(today_paid_amount),
            total_orders=total_orders,
            total_paid_amount_usd=str(total_paid_amount),
            status_counts=status_counts
        )
    )
