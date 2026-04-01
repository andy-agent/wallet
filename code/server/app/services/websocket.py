"""
WebSocket 通知服务

用于在订单状态变更时发送实时推送通知
"""
from typing import Optional

from app.api.client.ws import publish_order_status_change
from app.core.logging import get_logger

logger = get_logger(__name__)


async def notify_order_status_changed(
    order_id: str,
    status: str,
    tx_hash: Optional[str] = None,
    confirm_count: int = 0,
    paid_at: Optional[str] = None,
    **extra_data
):
    """
    通知订单状态变更
    
    调用此函数会在 WebSocket 通道中发布消息，所有订阅该订单的客户端都会收到通知。
    轮询机制不受影响，作为降级方案。
    
    Args:
        order_id: 订单ID
        status: 新状态 (paid, confirmed, fulfilled, expired, cancelled 等)
        tx_hash: 交易哈希（可选）
        confirm_count: 确认数（可选）
        paid_at: 支付时间 ISO 格式（可选）
        **extra_data: 其他需要推送的数据
    """
    data = {
        "tx_hash": tx_hash,
        "confirm_count": confirm_count,
        "paid_at": paid_at,
        **extra_data
    }
    
    # 过滤 None 值
    data = {k: v for k, v in data.items() if v is not None}
    
    await publish_order_status_change(order_id, status, data)
    logger.info(f"订单状态变更通知: order_id={order_id}, status={status}")


async def notify_order_paid(
    order_id: str,
    tx_hash: str,
    paid_at: str,
    confirm_count: int = 0
):
    """通知订单已支付"""
    await notify_order_status_changed(
        order_id=order_id,
        status="paid",
        tx_hash=tx_hash,
        confirm_count=confirm_count,
        paid_at=paid_at
    )


async def notify_order_confirmed(
    order_id: str,
    tx_hash: str,
    confirm_count: int
):
    """通知订单已确认（达到确认数）"""
    await notify_order_status_changed(
        order_id=order_id,
        status="confirmed",
        tx_hash=tx_hash,
        confirm_count=confirm_count
    )


async def notify_order_fulfilled(
    order_id: str,
    tx_hash: str,
    fulfilled_at: str
):
    """通知订单已履行（套餐已开通）"""
    await notify_order_status_changed(
        order_id=order_id,
        status="fulfilled",
        tx_hash=tx_hash,
        fulfilled_at=fulfilled_at
    )


async def notify_order_expired(order_id: str):
    """通知订单已过期"""
    await notify_order_status_changed(
        order_id=order_id,
        status="expired"
    )


async def notify_order_cancelled(order_id: str, reason: Optional[str] = None):
    """通知订单已取消"""
    data = {"reason": reason} if reason else {}
    await notify_order_status_changed(
        order_id=order_id,
        status="cancelled",
        **data
    )
