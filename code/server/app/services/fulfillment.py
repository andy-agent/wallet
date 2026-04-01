"""
订单履行服务 - 处理新购开通和续费逻辑

提供与 Marzban VPN 面板的集成，包括：
- 新购开通: 创建 Marzban 用户、生成 Token、返回订阅信息
- 续费处理: 延长到期时间、增加流量配额
"""
import uuid
import logging
from dataclasses import dataclass
from datetime import datetime, timedelta, timezone
from typing import Optional, Tuple

import jwt
from sqlalchemy import select
from sqlalchemy.ext.asyncio import AsyncSession
import ulid

from app.core.config import get_settings
from app.core.database import get_db_context
from app.core.state_machine import (
    OrderStatus, 
    transition_to_fulfilled,
    transition_to_failed,
    DuplicateTransitionError,
    StateTransitionError
)
from app.integrations.marzban import MarzbanClient, MarzbanAPIError, get_marzban_client
from app.models.order import Order
from app.models.client_session import ClientSession
from app.models.audit_log import AuditLog, OperatorType
from app.models.plan import Plan

logger = logging.getLogger(__name__)


@dataclass
class FulfillmentResult:
    """订单履行结果"""
    success: bool
    marzban_username: str
    access_token: str
    refresh_token: str
    expires_at: datetime
    subscription_url: str
    error_code: Optional[str] = None
    error_message: Optional[str] = None


class FulfillmentError(Exception):
    """履行服务异常"""
    def __init__(self, error_code: str, error_message: str):
        self.error_code = error_code
        self.error_message = error_message
        super().__init__(f"[{error_code}] {error_message}")


def generate_client_tokens(user_id: str, username: str) -> Tuple[str, str, datetime]:
    """
    生成客户端访问令牌
    
    Args:
        user_id: 用户ID
        username: Marzban 用户名
        
    Returns:
        Tuple[access_token, refresh_token, expires_at]
    """
    settings = get_settings()
    now = datetime.now(timezone.utc)
    
    # Access token: 30天有效期
    access_expires = now + timedelta(minutes=settings.jwt_access_token_expire_minutes)
    access_payload = {
        "sub": username,
        "user_id": user_id,
        "type": "access",
        "iat": now,
        "exp": access_expires,
        "jti": str(uuid.uuid4()),
    }
    access_token = jwt.encode(
        access_payload,
        settings.jwt_secret,
        algorithm=settings.jwt_algorithm
    )
    
    # Refresh token: 90天有效期
    refresh_expires = now + timedelta(days=settings.jwt_refresh_token_expire_days)
    refresh_payload = {
        "sub": username,
        "user_id": user_id,
        "type": "refresh",
        "iat": now,
        "exp": refresh_expires,
        "jti": str(uuid.uuid4()),
    }
    refresh_token = jwt.encode(
        refresh_payload,
        settings.jwt_secret,
        algorithm=settings.jwt_algorithm
    )
    
    return access_token, refresh_token, access_expires


def verify_client_token(token: str, expected_type: str = "access") -> str:
    """
    验证客户端令牌
    
    Args:
        token: JWT token
        expected_type: 期望的 token 类型 (access 或 refresh)
        
    Returns:
        username: 用户名
        
    Raises:
        FulfillmentError: 验证失败
    """
    settings = get_settings()
    
    try:
        payload = jwt.decode(
            token,
            settings.jwt_secret,
            algorithms=[settings.jwt_algorithm]
        )
        
        # 验证类型
        token_type = payload.get("type")
        if token_type != expected_type:
            raise FulfillmentError(
                "INVALID_TOKEN_TYPE",
                f"Expected {expected_type} token, got {token_type}"
            )
        
        username = payload.get("sub")
        if not username:
            raise FulfillmentError("INVALID_TOKEN", "Token missing subject")
        
        return username
        
    except jwt.ExpiredSignatureError:
        raise FulfillmentError("TOKEN_EXPIRED", "Token has expired")
    except jwt.InvalidTokenError as e:
        raise FulfillmentError("INVALID_TOKEN", str(e))


async def _get_order_with_plan(
    session: AsyncSession, 
    order_id: str,
    for_update: bool = False
) -> Tuple[Order, Plan]:
    """
    获取订单及关联套餐
    
    Args:
        session: 数据库会话
        order_id: 订单ID
        for_update: 是否使用 SELECT FOR UPDATE 加锁
        
    Raises:
        FulfillmentError: 订单不存在
    """
    query = (
        select(Order, Plan)
        .join(Plan, Order.plan_id == Plan.id)
        .where(Order.id == order_id)
    )
    
    if for_update:
        query = query.with_for_update()
    
    result = await session.execute(query)
    row = result.first()
    
    if not row:
        raise FulfillmentError("ORDER_NOT_FOUND", f"Order {order_id} not found")
    
    return row.Order, row.Plan


async def _check_idempotent(
    session: AsyncSession,
    order_id: str
) -> Optional[FulfillmentResult]:
    """
    幂等检查: 检查订单是否已履行
    
    Returns:
        如果已履行返回 FulfillmentResult，否则返回 None
    """
    result = await session.execute(
        select(Order, ClientSession)
        .outerjoin(ClientSession, Order.id == ClientSession.order_id)
        .where(Order.id == order_id)
        .where(Order.status == OrderStatus.FULFILLED.value)
    )
    row = result.first()
    
    if not row or not row.Order.fulfilled_at:
        return None
    
    order = row.Order
    client_session = row.ClientSession
    
    if not client_session:
        logger.warning(f"Order {order_id} is fulfilled but no ClientSession found")
        return None
    
    return FulfillmentResult(
        success=True,
        marzban_username=order.marzban_username,
        access_token=client_session.access_token,
        refresh_token=client_session.refresh_token,
        expires_at=client_session.expires_at,
        subscription_url="",  # 续费时从 Marzban 获取
    )


async def _record_audit_log(
    session: AsyncSession,
    entity_type: str,
    entity_id: str,
    action: str,
    payload: Optional[dict] = None,
    operator_type: OperatorType = OperatorType.SYSTEM,
    operator_id: Optional[str] = None,
) -> None:
    """
    记录审计日志
    """
    import json
    
    audit_log = AuditLog(
        id=str(ulid.new().str),
        entity_type=entity_type,
        entity_id=entity_id,
        action=action,
        operator_type=operator_type,
        operator_id=operator_id,
        payload_json=json.dumps(payload) if payload else None,
    )
    session.add(audit_log)


def _generate_username(user_id: str) -> str:
    """
    生成唯一的 Marzban 用户名
    
    格式: {user_id前8位}_{ulid后缀}
    """
    user_prefix = user_id[:8] if user_id else "user"
    suffix = str(ulid.new().str.lower())[-8:]
    return f"{user_prefix}_{suffix}"


async def fulfill_new_order(order_id: str) -> FulfillmentResult:
    """
    新购开通逻辑
    
    Args:
        order_id: 订单ID
        
    Returns:
        FulfillmentResult: 履行结果
        
    Raises:
        FulfillmentError: 履行失败
    """
    logger.info(f"Starting new order fulfillment for {order_id}")
    
    async with get_db_context() as session:
        # 1. 幂等检查: 订单是否已 fulfilled
        existing = await _check_idempotent(session, order_id)
        if existing:
            logger.info(f"Order {order_id} already fulfilled, returning cached result")
            return existing
        
        try:
            # 2. 获取订单和套餐信息（使用 SELECT FOR UPDATE 加锁）
            order, plan = await _get_order_with_plan(session, order_id, for_update=True)
            
            # 验证订单类型
            if order.purchase_type != "new":
                raise FulfillmentError(
                    "INVALID_ORDER_TYPE",
                    f"Expected purchase_type='new', got '{order.purchase_type}'"
                )
            
            # 验证订单状态 (必须是 paid_success)
            if order.status != OrderStatus.PAID_SUCCESS.value:
                raise FulfillmentError(
                    "INVALID_ORDER_STATUS",
                    f"Order status must be 'paid_success', got '{order.status}'"
                )
            
            # 3. 生成唯一用户名
            username = _generate_username(order.user_id)
            
            # 4. 计算到期时间: now + plan.duration_days
            now = datetime.now(timezone.utc)
            expire_timestamp = int((now + timedelta(days=plan.duration_days)).timestamp())
            
            # 5. 创建 Marzban 用户
            marzban = await get_marzban_client()
            try:
                marzban_user = await marzban.create_user(
                    username=username,
                    expire=expire_timestamp,
                    data_limit=int(plan.traffic_bytes),
                    proxies={
                        "vless": {},
                        "vmess": {}
                    }
                )
            except MarzbanAPIError as e:
                logger.error(f"Failed to create Marzban user: {e}")
                raise FulfillmentError(
                    "MARZBAN_CREATE_FAILED",
                    f"Failed to create Marzban user: {e.message}"
                )
            
            # 6. 获取订阅 URL
            subscription_url = marzban_user.subscription_url
            
            # 7. 创建 ClientSession (access_token + refresh_token)
            access_token, refresh_token, expires_at = generate_client_tokens(order.user_id, username)
            
            client_session = ClientSession(
                id=str(ulid.new().str),
                order_id=order_id,
                user_id=order.user_id,
                marzban_username=username,
                access_token=access_token,
                refresh_token=refresh_token,
                expires_at=expires_at,
            )
            session.add(client_session)
            
            # 8. 更新订单: marzban_username, fulfilled_at, status=fulfilled
            order.marzban_username = username
            order.fulfilled_at = now
            
            # 状态转换
            transition_to_fulfilled(
                order_id=order_id,
                current_status=OrderStatus(order.status),
                marzban_username=username,
                subscription_url=subscription_url,
            )
            order.status = OrderStatus.FULFILLED.value
            
            # WebSocket 通知：订单已履行
            from app.services.websocket import notify_order_status_changed
            await notify_order_status_changed(
                order_id=order_id,
                status="fulfilled",
                tx_hash=order.tx_hash,
                marzban_username=username,
                subscription_url=subscription_url
            )
            
            # 9. 记录审计日志
            await _record_audit_log(
                session=session,
                entity_type="order",
                entity_id=order_id,
                action="fulfilled_new",
                payload={
                    "marzban_username": username,
                    "plan_id": plan.id,
                    "duration_days": plan.duration_days,
                    "traffic_bytes": plan.traffic_bytes,
                    "expires_at": expire_timestamp,
                },
                operator_type=OperatorType.SYSTEM,
            )
            
            await session.commit()
            
            logger.info(f"New order fulfillment completed for {order_id}, username: {username}")
            
            # 9. 返回 FulfillmentResult
            return FulfillmentResult(
                success=True,
                marzban_username=username,
                access_token=access_token,
                refresh_token=refresh_token,
                expires_at=expires_at,
                subscription_url=subscription_url,
            )
            
        except DuplicateTransitionError as e:
            # 幂等性错误，订单已处理
            logger.info(f"Order {order_id} already fulfilled (duplicate transition)")
            await session.rollback()
            # 重新查询已履行的结果
            existing = await _check_idempotent(session, order_id)
            if existing:
                return existing
            raise FulfillmentError("ALREADY_FULFILLED", "Order already fulfilled but no session found")
            
        except FulfillmentError as e:
            await session.rollback()
            # 尝试将订单转移到 failed 状态
            await _try_transition_to_failed(session, order_id, e.error_code, e.error_message)
            raise
            
        except Exception as e:
            await session.rollback()
            logger.exception(f"Unexpected error during fulfillment for {order_id}")
            # 尝试将订单转移到 failed 状态
            await _try_transition_to_failed(session, order_id, "INTERNAL_ERROR", str(e))
            raise FulfillmentError("INTERNAL_ERROR", str(e))


async def _try_transition_to_failed(
    session: AsyncSession,
    order_id: str,
    error_code: str,
    error_message: str
) -> None:
    """
    尝试将订单转移到 failed 状态
    
    这是一个尽最大努力的操作，失败不抛出异常。
    """
    try:
        result = await session.execute(
            select(Order).where(Order.id == order_id).with_for_update()
        )
        order = result.scalar_one_or_none()
        
        if order and order.status == OrderStatus.PAID_SUCCESS.value:
            transition_to_failed(
                order_id=order_id,
                current_status=OrderStatus(order.status),
                error_code=error_code,
                error_message=error_message,
            )
            order.status = OrderStatus.FAILED.value
            order.error_code = error_code
            order.error_message = error_message
            await session.commit()
            logger.info(f"Order {order_id} transitioned to failed: {error_code}")
    except Exception as e:
        logger.error(f"Failed to transition order {order_id} to failed: {e}")
        await session.rollback()


async def fulfill_renew_order(order_id: str, client_token: str) -> FulfillmentResult:
    """
    续费开通逻辑
    
    Args:
        order_id: 订单ID
        client_token: 客户端访问令牌（用于验证原用户身份）
        
    Returns:
        FulfillmentResult: 履行结果
        
    Raises:
        FulfillmentError: 履行失败
    """
    logger.info(f"Starting renew order fulfillment for {order_id}")
    
    async with get_db_context() as session:
        # 1. 幂等检查
        existing = await _check_idempotent(session, order_id)
        if existing:
            logger.info(f"Order {order_id} already fulfilled, returning cached result")
            return existing
        
        try:
            # 2. 验证 client_token，获取原用户
            username = verify_client_token(client_token, expected_type="access")
            
            # 获取订单和套餐信息（使用 SELECT FOR UPDATE 加锁）
            order, plan = await _get_order_with_plan(session, order_id, for_update=True)
            
            # 验证订单类型
            if order.purchase_type != "renew":
                raise FulfillmentError(
                    "INVALID_ORDER_TYPE",
                    f"Expected purchase_type='renew', got '{order.purchase_type}'"
                )
            
            # 验证订单状态
            if order.status != OrderStatus.PAID_SUCCESS.value:
                raise FulfillmentError(
                    "INVALID_ORDER_STATUS",
                    f"Order status must be 'paid_success', got '{order.status}'"
                )
            
            # 验证用户名匹配
            if order.marzban_username and order.marzban_username != username:
                raise FulfillmentError(
                    "USERNAME_MISMATCH",
                    f"Token username '{username}' does not match order marzban_username '{order.marzban_username}'"
                )
            
            # 3. 查询 Marzban 用户当前状态
            marzban = await get_marzban_client()
            try:
                current_user = await marzban.get_user(username)
            except MarzbanAPIError as e:
                logger.error(f"Failed to get Marzban user: {e}")
                raise FulfillmentError(
                    "MARZBAN_GET_FAILED",
                    f"Failed to get Marzban user: {e.message}"
                )
            
            if not current_user:
                raise FulfillmentError(
                    "USER_NOT_FOUND",
                    f"Marzban user '{username}' not found"
                )
            
            # 4. 计算新到期时间: max(now, current_expire) + plan.duration_days
            now = datetime.now(timezone.utc)
            now_timestamp = int(now.timestamp())
            
            current_expire = current_user.expire or now_timestamp
            # 如果已过期，从当前时间开始算；如果未过期，从原到期时间续期
            base_timestamp = max(now_timestamp, current_expire)
            new_expire_timestamp = base_timestamp + (plan.duration_days * 24 * 60 * 60)
            
            # 5. 计算新流量: current_data_limit + plan.traffic_bytes
            current_data_limit = current_user.data_limit or 0
            new_data_limit = current_data_limit + int(plan.traffic_bytes)
            
            # 6. 调用 MarzbanClient.modify_user 更新
            try:
                updated_user = await marzban.modify_user(
                    username=username,
                    expire=new_expire_timestamp,
                    data_limit=new_data_limit,
                    status="active"  # 续费时确保用户状态为 active
                )
            except MarzbanAPIError as e:
                logger.error(f"Failed to modify Marzban user: {e}")
                raise FulfillmentError(
                    "MARZBAN_MODIFY_FAILED",
                    f"Failed to modify Marzban user: {e.message}"
                )
            
            # 7. 刷新 ClientSession
            access_token, refresh_token, expires_at = generate_client_tokens(order.user_id, username)
            
            # 查找现有会话并更新，或创建新会话
            result = await session.execute(
                select(ClientSession)
                .where(ClientSession.marzban_username == username)
                .where(ClientSession.revoked_at.is_(None))
                .order_by(ClientSession.created_at.desc())
            )
            existing_session = result.scalar_one_or_none()
            
            if existing_session:
                # 吊销旧会话
                existing_session.revoked_at = now
                # 创建新会话
                new_session = ClientSession(
                    id=str(ulid.new().str),
                    order_id=order_id,
                    user_id=order.user_id,
                    marzban_username=username,
                    access_token=access_token,
                    refresh_token=refresh_token,
                    expires_at=expires_at,
                )
                session.add(new_session)
            else:
                # 创建新会话
                new_session = ClientSession(
                    id=str(ulid.new().str),
                    order_id=order_id,
                    user_id=order.user_id,
                    marzban_username=username,
                    access_token=access_token,
                    refresh_token=refresh_token,
                    expires_at=expires_at,
                )
                session.add(new_session)
            
            # 8. 更新订单状态
            order.marzban_username = username
            order.fulfilled_at = now
            order.client_user_id = username
            
            transition_to_fulfilled(
                order_id=order_id,
                current_status=OrderStatus(order.status),
                marzban_username=username,
                subscription_url=updated_user.subscription_url,
            )
            order.status = OrderStatus.FULFILLED.value
            
            # WebSocket 通知：订单已履行
            from app.services.websocket import notify_order_status_changed
            await notify_order_status_changed(
                order_id=order_id,
                status="fulfilled",
                tx_hash=order.tx_hash,
                marzban_username=username,
                subscription_url=updated_user.subscription_url
            )
            
            # 9. 记录审计日志
            await _record_audit_log(
                session=session,
                entity_type="order",
                entity_id=order_id,
                action="fulfilled_renew",
                payload={
                    "marzban_username": username,
                    "plan_id": plan.id,
                    "duration_days": plan.duration_days,
                    "traffic_bytes": plan.traffic_bytes,
                    "previous_expire": current_expire,
                    "new_expire": new_expire_timestamp,
                    "previous_data_limit": current_data_limit,
                    "new_data_limit": new_data_limit,
                },
                operator_type=OperatorType.SYSTEM,
            )
            
            await session.commit()
            
            logger.info(f"Renew order fulfillment completed for {order_id}, username: {username}")
            
            # 返回 FulfillmentResult
            return FulfillmentResult(
                success=True,
                marzban_username=username,
                access_token=access_token,
                refresh_token=refresh_token,
                expires_at=expires_at,
                subscription_url=updated_user.subscription_url,
            )
            
        except DuplicateTransitionError as e:
            logger.info(f"Order {order_id} already fulfilled (duplicate transition)")
            await session.rollback()
            existing = await _check_idempotent(session, order_id)
            if existing:
                return existing
            raise FulfillmentError("ALREADY_FULFILLED", "Order already fulfilled but no session found")
            
        except FulfillmentError as e:
            await session.rollback()
            # 尝试将订单转移到 failed 状态
            await _try_transition_to_failed(session, order_id, e.error_code, e.error_message)
            raise
            
        except Exception as e:
            await session.rollback()
            logger.exception(f"Unexpected error during renew fulfillment for {order_id}")
            # 尝试将订单转移到 failed 状态
            await _try_transition_to_failed(session, order_id, "INTERNAL_ERROR", str(e))
            raise FulfillmentError("INTERNAL_ERROR", str(e))


async def refresh_session(
    refresh_token: str,
    client_token: Optional[str] = None
) -> FulfillmentResult:
    """
    刷新客户端会话
    
    Args:
        refresh_token: 刷新令牌
        client_token: 可选的客户端访问令牌（用于双重验证）
        
    Returns:
        FulfillmentResult: 新的令牌信息
        
    Raises:
        FulfillmentError: 刷新失败
    """
    async with get_db_context() as session:
        # 验证 refresh token
        username = verify_client_token(refresh_token, expected_type="refresh")
        
        # 查找有效的会话
        result = await session.execute(
            select(ClientSession)
            .where(ClientSession.refresh_token == refresh_token)
            .where(ClientSession.revoked_at.is_(None))
        )
        client_session = result.scalar_one_or_none()
        
        if not client_session:
            raise FulfillmentError("INVALID_REFRESH_TOKEN", "Refresh token not found or revoked")
        
        # 双重验证: 如果提供了 access token，验证用户名匹配
        if client_token:
            access_username = verify_client_token(client_token, expected_type="access")
            if access_username != username:
                raise FulfillmentError("TOKEN_MISMATCH", "Access token and refresh token do not match")
        
        # 检查会话是否过期
        now = datetime.now(timezone.utc)
        if client_session.expires_at < now:
            raise FulfillmentError("SESSION_EXPIRED", "Session has expired, please re-purchase")
        
        # 吊销旧会话
        client_session.revoked_at = now
        
        # 生成新令牌
        new_access_token, new_refresh_token, new_expires_at = generate_client_tokens(client_session.user_id, username)
        
        # 创建新会话
        new_session = ClientSession(
            id=str(ulid.new().str),
            order_id=client_session.order_id,
            user_id=client_session.user_id,
            marzban_username=username,
            access_token=new_access_token,
            refresh_token=new_refresh_token,
            expires_at=new_expires_at,
        )
        session.add(new_session)
        
        # 记录审计日志
        await _record_audit_log(
            session=session,
            entity_type="client_session",
            entity_id=str(client_session.id),
            action="refreshed",
            payload={"marzban_username": username},
            operator_type=OperatorType.CLIENT,
            operator_id=username,
        )
        
        await session.commit()
        
        logger.info(f"Session refreshed for user: {username}")
        
        return FulfillmentResult(
            success=True,
            marzban_username=username,
            access_token=new_access_token,
            refresh_token=new_refresh_token,
            expires_at=new_expires_at,
            subscription_url="",  # 客户端应该缓存订阅 URL
        )
