"""
Admin Order Actions API - 异常订单处理
"""
from typing import Optional

from fastapi import APIRouter, Depends, Header
from pydantic import BaseModel, Field
from sqlalchemy import select
from sqlalchemy.ext.asyncio import AsyncSession

from app.core.config import get_settings
from app.core.database import get_db
from app.core.exceptions import UnauthorizedException, NotFoundException, ValidationException
from app.core.logging import get_logger
from app.models.order import Order
from app.schemas.base import Response
from app.services.fulfillment import FulfillmentService
from app.core.state_machine import state_machine, OrderStatus

logger = get_logger(__name__)
router = APIRouter()
settings = get_settings()


async def verify_admin_token(admin_token: str = Header(...)) -> str:
    """验证 admin token"""
    if not settings.admin_token or admin_token != settings.admin_token:
        raise UnauthorizedException(message="无效的 admin_token")
    return admin_token


class ManualConfirmRequest(BaseModel):
    """人工确认支付请求"""
    tx_hash: str = Field(..., description="交易哈希")
    amount_crypto: str = Field(..., description="实际支付金额")
    note: Optional[str] = Field(None, description="备注")


class MarkIgnoreRequest(BaseModel):
    """标记忽略请求"""
    reason: str = Field(..., description="忽略原因")


class RetryFulfillResponse(BaseModel):
    """重试开通响应"""
    success: bool
    message: str
    marzban_username: Optional[str] = None


@router.post("/orders/{order_id}/manual-confirm", response_model=Response[dict])
async def manual_confirm_payment(
    order_id: str,
    request: ManualConfirmRequest,
    db: AsyncSession = Depends(get_db),
    admin_token: str = Depends(verify_admin_token)
):
    """
    人工确认支付（用于异常单处理）
    
    适用场景:
    - 链上交易已确认但系统未检测到
    - 金额不匹配需要人工确认
    - 用户提供了支付证明
    """
    # 查询订单
    result = await db.execute(select(Order).where(Order.id == order_id))
    order = result.scalar_one_or_none()
    
    if not order:
        raise NotFoundException(message="订单不存在")
    
    # 检查订单状态
    if order.status in ["fulfilled", "expired"]:
        raise ValidationException(message=f"订单状态为 {order.status}，无法人工确认")
    
    # 更新订单信息
    order.tx_hash = request.tx_hash
    order.amount_crypto = request.amount_crypto
    order.confirm_count = 999  # 人工确认直接给高确认数
    order.confirmed_at = func.now()
    
    # 状态转换
    try:
        await state_machine.transition_to_paid_success(
            order=order,
            tx_hash=request.tx_hash,
            from_address="manual",
            amount=request.amount_crypto,
            confirm_count=999
        )
        
        # 记录审计日志
        logger.info(
            "order_manual_confirmed",
            order_id=order_id,
            tx_hash=request.tx_hash,
            amount=request.amount_crypto,
            note=request.note,
            admin_token=admin_token[:8] + "..."
        )
        
        await db.commit()
        
        return Response(
            code="SUCCESS",
            message="人工确认成功，订单已标记为已支付",
            data={
                "order_id": order_id,
                "status": "paid_success",
                "tx_hash": request.tx_hash,
                "note": request.note
            }
        )
        
    except Exception as e:
        await db.rollback()
        logger.error("manual_confirm_failed", order_id=order_id, error=str(e))
        raise ValidationException(message=f"确认失败: {str(e)}")


@router.post("/orders/{order_id}/retry-fulfill", response_model=Response[RetryFulfillResponse])
async def retry_fulfill_order(
    order_id: str,
    db: AsyncSession = Depends(get_db),
    admin_token: str = Depends(verify_admin_token)
):
    """
    重试开通账号
    
    适用场景:
    - 之前开通失败（网络问题等）
    - Marzban 服务暂时不可用
    - 需要人工触发重新开通
    """
    # 查询订单
    result = await db.execute(select(Order).where(Order.id == order_id))
    order = result.scalar_one_or_none()
    
    if not order:
        raise NotFoundException(message="订单不存在")
    
    # 检查订单状态
    if order.status != "paid_success":
        raise ValidationException(
            message=f"订单状态为 {order.status}，只有 paid_success 状态可以重试开通"
        )
    
    # 检查是否已开通（幂等）
    if order.status == "fulfilled":
        return Response(
            code="SUCCESS",
            message="订单已开通，无需重试",
            data=RetryFulfillResponse(
                success=True,
                message="订单已开通",
                marzban_username=order.marzban_username
            )
        )
    
    # 执行开通
    fulfillment_service = FulfillmentService(db)
    
    try:
        if order.purchase_type == "new":
            result = await fulfillment_service.fulfill_new_order(order_id)
        else:
            # 续费需要 client_token，从现有 session 获取
            from sqlalchemy import select
            from app.models.client_session import ClientSession
            
            session_result = await db.execute(
                select(ClientSession)
                .where(ClientSession.order_id == order_id)
                .order_by(ClientSession.created_at.desc())
            )
            session = session_result.scalar_one_or_none()
            
            if not session:
                raise ValidationException(message="未找到客户端会话，无法续费")
            
            result = await fulfillment_service.fulfill_renew_order(
                order_id, 
                session.access_token
            )
        
        if result.success:
            # 更新状态
            await state_machine.transition_to_fulfilled(order)
            await db.commit()
            
            logger.info(
                "order_fulfill_retried",
                order_id=order_id,
                marzban_username=result.marzban_username,
                admin_token=admin_token[:8] + "..."
            )
            
            return Response(
                code="SUCCESS",
                message="开通成功",
                data=RetryFulfillResponse(
                    success=True,
                    message="账号开通成功",
                    marzban_username=result.marzban_username
                )
            )
        else:
            raise ValidationException(message=f"开通失败: {result.error_message}")
            
    except Exception as e:
        await db.rollback()
        logger.error("retry_fulfill_failed", order_id=order_id, error=str(e))
        raise ValidationException(message=f"重试开通失败: {str(e)}")


@router.post("/orders/{order_id}/mark-ignore", response_model=Response[dict])
async def mark_order_ignore(
    order_id: str,
    request: MarkIgnoreRequest,
    db: AsyncSession = Depends(get_db),
    admin_token: str = Depends(verify_admin_token)
):
    """
    标记订单为忽略
    
    适用场景:
    - 测试订单
    - 恶意订单
    - 无法处理的异常单
    """
    # 查询订单
    result = await db.execute(select(Order).where(Order.id == order_id))
    order = result.scalar_one_or_none()
    
    if not order:
        raise NotFoundException(message="订单不存在")
    
    # 检查是否已开通
    if order.status == "fulfilled":
        raise ValidationException(message="已开通的订单不能标记为忽略")
    
    # 记录原状态
    original_status = order.status
    
    # 更新为失败状态
    await state_machine.transition_to_failed(
        order=order,
        error_code="MANUAL_IGNORE",
        error_message=request.reason
    )
    
    # 释放地址
    if order.receive_address:
        from app.services.address_pool import AddressPoolService
        address_service = AddressPoolService(db)
        await address_service.release_address_by_order(order_id)
    
    await db.commit()
    
    logger.info(
        "order_marked_ignore",
        order_id=order_id,
        original_status=original_status,
        reason=request.reason,
        admin_token=admin_token[:8] + "..."
    )
    
    return Response(
        code="SUCCESS",
        message="订单已标记为忽略",
        data={
            "order_id": order_id,
            "original_status": original_status,
            "reason": request.reason
        }
    )


@router.post("/orders/{order_id}/refund", response_model=Response[dict])
async def mark_order_refund(
    order_id: str,
    db: AsyncSession = Depends(get_db),
    admin_token: str = Depends(verify_admin_token)
):
    """
    标记订单为待退款
    
    适用场景:
    - 用户要求退款
    - 支付错误需要退回
    - 服务无法提供
    """
    # 查询订单
    result = await db.execute(select(Order).where(Order.id == order_id))
    order = result.scalar_one_or_none()
    
    if not order:
        raise NotFoundException(message="订单不存在")
    
    # 检查是否已开通
    if order.status == "fulfilled":
        raise ValidationException(message="已开通的订单请先吊销账号再退款")
    
    # 更新状态
    order.error_code = "PENDING_REFUND"
    order.error_message = "订单标记为待退款，请人工处理"
    
    await db.commit()
    
    logger.info(
        "order_marked_refund",
        order_id=order_id,
        tx_hash=order.tx_hash,
        amount=str(order.amount_crypto),
        admin_token=admin_token[:8] + "..."
    )
    
    return Response(
        code="SUCCESS",
        message="订单已标记为待退款，请人工处理退款",
        data={
            "order_id": order_id,
            "tx_hash": order.tx_hash,
            "amount_crypto": str(order.amount_crypto),
            "receive_address": order.receive_address
        }
    )


from sqlalchemy import func
