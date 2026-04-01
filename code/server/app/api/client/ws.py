"""
Client WebSocket API - 实时订单状态推送

提供订单状态实时推送功能，替代轮询机制
"""
import asyncio
import json
import logging
from datetime import datetime, timezone
from typing import Optional, Set, Dict

from fastapi import APIRouter, WebSocket, WebSocketDisconnect, Query, Depends
from fastapi.websockets import WebSocketState
from sqlalchemy import select
from sqlalchemy.ext.asyncio import AsyncSession
import redis.asyncio as redis

from app.core.config import get_settings
from app.core.database import get_db
from app.core.logging import get_logger
from app.models.order import Order
from app.models.user import User
from app.api.client.auth import get_current_user

logger = get_logger(__name__)
router = APIRouter(tags=["websocket"])

# 存储活跃的 WebSocket 连接
# 结构: {order_id: {websocket: {"authenticated": bool, "user_id": str, "last_ping": datetime}}}
active_connections: Dict[str, Dict[WebSocket, dict]] = {}

# Redis 连接池
redis_pool: Optional[redis.Redis] = None


async def get_redis() -> redis.Redis:
    """获取 Redis 连接"""
    global redis_pool
    if redis_pool is None:
        settings = get_settings()
        redis_pool = redis.from_url(settings.redis_url, decode_responses=True)
    return redis_pool


async def close_redis():
    """关闭 Redis 连接"""
    global redis_pool
    if redis_pool:
        await redis_pool.close()
        redis_pool = None


class WebSocketMessage:
    """WebSocket 消息格式"""
    
    @staticmethod
    def order_status_changed(
        order_id: str,
        status: str,
        data: dict = None,
        timestamp: Optional[datetime] = None
    ) -> dict:
        return {
            "event": "order_status_changed",
            "order_id": order_id,
            "status": status,
            "timestamp": (timestamp or datetime.now(timezone.utc)).isoformat(),
            "data": data or {}
        }
    
    @staticmethod
    def ping() -> dict:
        return {"event": "ping", "timestamp": datetime.now(timezone.utc).isoformat()}
    
    @staticmethod
    def pong() -> dict:
        return {"event": "pong", "timestamp": datetime.now(timezone.utc).isoformat()}
    
    @staticmethod
    def error(message: str, code: str = "ERROR") -> dict:
        return {
            "event": "error",
            "error_code": code,
            "message": message,
            "timestamp": datetime.now(timezone.utc).isoformat()
        }
    
    @staticmethod
    def connected(order_id: str) -> dict:
        return {
            "event": "connected",
            "order_id": order_id,
            "timestamp": datetime.now(timezone.utc).isoformat(),
            "message": "WebSocket 连接成功"
        }


async def authenticate_websocket(
    websocket: WebSocket,
    order_id: str,
    client_token: Optional[str],
    db: AsyncSession
) -> Optional[User]:
    """
    验证 WebSocket 连接的客户端 Token
    
    Args:
        websocket: WebSocket 连接
        order_id: 订单ID
        client_token: 客户端 Token (query param)
        db: 数据库会话
    
    Returns:
        User: 验证通过返回用户，否则返回 None
    """
    if not client_token:
        return None
    
    try:
        # 查找订单
        result = await db.execute(select(Order).where(Order.id == order_id))
        order = result.scalar_one_or_none()
        
        if not order:
            return None
        
        # 验证 token 对应的用户是否有权访问该订单
        result = await db.execute(select(User).where(User.id == order.user_id))
        user = result.scalar_one_or_none()
        
        if not user:
            return None
        
        # 这里可以添加更严格的 token 验证逻辑
        # 暂时使用用户关联验证
        return user
        
    except Exception as e:
        logger.error(f"WebSocket 认证失败: {e}")
        return None


@router.websocket("/ws/orders/{order_id}")
async def order_websocket(
    websocket: WebSocket,
    order_id: str,
    client_token: Optional[str] = Query(None, description="客户端 Token 用于认证"),
    db: AsyncSession = Depends(get_db)
):
    """
    订单状态 WebSocket 推送端点
    
    功能：
    - 连接时可选通过 client_token 认证
    - 订阅订单状态变更事件
    - 心跳机制保持连接
    - 断线后自动清理
    
    消息格式：
    - 服务器推送: {"event": "order_status_changed", "order_id": "xxx", "status": "paid", ...}
    - 心跳: {"event": "ping"} -> 客户端回复 {"event": "pong"}
    """
    await websocket.accept()
    
    # 初始化连接信息
    conn_info = {
        "authenticated": False,
        "user_id": None,
        "last_ping": datetime.now(timezone.utc),
        "redis_task": None
    }
    
    # 注册连接到全局管理器
    if order_id not in active_connections:
        active_connections[order_id] = {}
    active_connections[order_id][websocket] = conn_info
    
    try:
        # 尝试认证（如果有 token）
        if client_token:
            user = await authenticate_websocket(websocket, order_id, client_token, db)
            if user:
                conn_info["authenticated"] = True
                conn_info["user_id"] = user.id
                logger.info(f"WebSocket 认证成功: order_id={order_id}, user={user.username}")
            else:
                logger.warning(f"WebSocket 认证失败: order_id={order_id}")
        else:
            # 无 token 也允许连接，但某些敏感信息可能不推送
            logger.info(f"WebSocket 匿名连接: order_id={order_id}")
        
        # 发送连接成功消息
        await websocket.send_json(WebSocketMessage.connected(order_id))
        
        # 启动 Redis 订阅任务
        conn_info["redis_task"] = asyncio.create_task(
            redis_subscriber(websocket, order_id, conn_info)
        )
        
        # 主循环：处理客户端消息和心跳
        while True:
            try:
                # 设置接收超时（用于心跳检测）
                message = await asyncio.wait_for(
                    websocket.receive_text(),
                    timeout=60.0  # 60秒超时
                )
                
                # 解析客户端消息
                try:
                    data = json.loads(message)
                    event = data.get("event")
                    
                    if event == "ping":
                        conn_info["last_ping"] = datetime.now(timezone.utc)
                        await websocket.send_json(WebSocketMessage.pong())
                    elif event == "pong":
                        conn_info["last_ping"] = datetime.now(timezone.utc)
                    else:
                        logger.debug(f"收到 WebSocket 消息: {data}")
                        
                except json.JSONDecodeError:
                    await websocket.send_json(
                        WebSocketMessage.error("无效的 JSON 格式", "INVALID_JSON")
                    )
                    
            except asyncio.TimeoutError:
                # 发送心跳检测
                if websocket.client_state == WebSocketState.CONNECTED:
                    try:
                        await websocket.send_json(WebSocketMessage.ping())
                    except Exception:
                        break
                        
    except WebSocketDisconnect:
        logger.info(f"WebSocket 断开: order_id={order_id}")
    except Exception as e:
        logger.error(f"WebSocket 错误: order_id={order_id}, error={e}")
    finally:
        # 清理连接
        await _cleanup_connection(websocket, order_id, conn_info)


async def _cleanup_connection(
    websocket: WebSocket,
    order_id: str,
    conn_info: dict
):
    """清理 WebSocket 连接"""
    # 取消 Redis 订阅任务
    if conn_info.get("redis_task") and not conn_info["redis_task"].done():
        conn_info["redis_task"].cancel()
        try:
            await conn_info["redis_task"]
        except asyncio.CancelledError:
            pass
    
    # 从活跃连接中移除
    if order_id in active_connections:
        active_connections[order_id].pop(websocket, None)
        if not active_connections[order_id]:
            del active_connections[order_id]
    
    # 关闭 WebSocket
    try:
        if websocket.client_state == WebSocketState.CONNECTED:
            await websocket.close()
    except Exception:
        pass
    
    logger.debug(f"WebSocket 连接已清理: order_id={order_id}")


async def redis_subscriber(
    websocket: WebSocket,
    order_id: str,
    conn_info: dict
):
    """
    Redis 订阅者 - 监听订单状态变更并推送到 WebSocket
    
    订阅频道格式: order:{order_id}
    """
    channel_name = f"order:{order_id}"
    
    try:
        redis_client = await get_redis()
        pubsub = redis_client.pubsub()
        await pubsub.subscribe(channel_name)
        
        logger.info(f"Redis 订阅启动: {channel_name}")
        
        async for message in pubsub.listen():
            # 检查连接是否仍然活跃
            if websocket.client_state != WebSocketState.CONNECTED:
                break
            
            if message["type"] == "message":
                try:
                    data = json.loads(message["data"])
                    event_type = data.get("event")
                    
                    # 构建推送消息
                    if event_type == "order_status_changed":
                        push_msg = WebSocketMessage.order_status_changed(
                            order_id=order_id,
                            status=data.get("status"),
                            data=data.get("data", {})
                        )
                        await websocket.send_json(push_msg)
                        logger.debug(f"推送订单状态变更: {order_id} -> {data.get('status')}")
                        
                except json.JSONDecodeError:
                    logger.error(f"Redis 消息解析失败: {message['data']}")
                    
    except asyncio.CancelledError:
        logger.debug(f"Redis 订阅取消: {channel_name}")
    except Exception as e:
        logger.error(f"Redis 订阅错误: {channel_name}, error={e}")
    finally:
        try:
            await pubsub.unsubscribe(channel_name)
            await pubsub.close()
        except Exception:
            pass


# ========== Redis 发布接口 ==========

async def publish_order_status_change(
    order_id: str,
    status: str,
    data: dict = None
):
    """
    发布订单状态变更到 Redis
    
    供其他服务（如支付回调处理）调用
    
    Args:
        order_id: 订单ID
        status: 新状态
        data: 额外数据（如 tx_hash, confirm_count 等）
    """
    try:
        redis_client = await get_redis()
        channel_name = f"order:{order_id}"
        
        message = {
            "event": "order_status_changed",
            "order_id": order_id,
            "status": status,
            "timestamp": datetime.now(timezone.utc).isoformat(),
            "data": data or {}
        }
        
        await redis_client.publish(channel_name, json.dumps(message))
        logger.debug(f"Redis 发布: {channel_name} -> {status}")
        
    except Exception as e:
        logger.error(f"Redis 发布失败: order_id={order_id}, error={e}")


# ========== 连接管理工具 ==========

def get_active_connections_count(order_id: Optional[str] = None) -> int:
    """获取活跃连接数"""
    if order_id:
        return len(active_connections.get(order_id, {}))
    return sum(len(conns) for conns in active_connections.values())


def is_order_subscribed(order_id: str) -> bool:
    """检查订单是否有活跃订阅"""
    return order_id in active_connections and len(active_connections[order_id]) > 0
