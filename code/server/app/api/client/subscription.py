"""
Client API - Subscription module
客户端订阅接口
"""
import logging
from datetime import datetime
from typing import List, Optional

import jwt
from fastapi import APIRouter, Depends, Header
from pydantic import BaseModel, Field
from sqlalchemy import select
from sqlalchemy.ext.asyncio import AsyncSession

from app.core.config import get_settings
from app.core.database import get_db
from app.core.exceptions import UnauthorizedException, ForbiddenException, NotFoundException
from app.core.rate_limit import client_rate_limit
from app.integrations.marzban import get_marzban_client, MarzbanAPIError
from app.models.client_session import ClientSession
from app.schemas.base import Response

logger = logging.getLogger(__name__)
router = APIRouter(prefix="/subscription", tags=["subscription"])
settings = get_settings()


# ============ Schemas ============

class NodeInfo(BaseModel):
    """节点信息"""
    name: str = Field(..., description="节点名称")
    protocol: str = Field(..., description="协议类型")
    address: str = Field(..., description="服务器地址")
    port: int = Field(..., description="端口")


class SubscriptionResponseData(BaseModel):
    """订阅信息响应数据"""
    subscription_url: str = Field(..., description="订阅链接 URL")
    expires_at: datetime = Field(..., description="账户过期时间")
    traffic_total: int = Field(..., description="总流量（字节）")
    traffic_used: int = Field(..., description="已使用流量（字节）")
    traffic_remaining: int = Field(..., description="剩余流量（字节）")
    nodes: Optional[List[NodeInfo]] = Field(default=None, description="节点信息列表（可选）")


class ClientTokenPayload(BaseModel):
    """客户端 JWT token payload"""
    username: str
    exp: datetime
    type: str


# ============ Token Verification ============

async def verify_client_token(
    authorization: str = Header(None),
    db: AsyncSession = Depends(get_db)
) -> ClientTokenPayload:
    """
    验证客户端 JWT token
    
    验证流程：
    1. 从 Authorization header 提取 Bearer token
    2. 验证 JWT 签名和过期时间
    3. 验证 token 类型为 "access"
    4. 从 ClientSession 表验证 token 未被吊销
    
    Expected format: Bearer <jwt_token>
    
    Args:
        authorization: Authorization header value
        db: 数据库会话
        
    Returns:
        ClientTokenPayload: 解析后的 token payload
        
    Raises:
        UnauthorizedException: Token 过期或无效
        ForbiddenException: Token 验证失败（类型错误、被吊销等）
    """
    if not authorization or not authorization.startswith("Bearer "):
        raise UnauthorizedException(
            message="Missing or invalid Authorization header. Expected: Bearer <token>"
        )
    
    token = authorization.replace("Bearer ", "")
    
    try:
        # 1. 验证 JWT 签名和过期时间
        payload = jwt.decode(
            token,
            settings.jwt_secret,
            algorithms=[settings.jwt_algorithm]
        )
        
        # 2. 验证 token 类型
        token_type = payload.get("type")
        if token_type != "access":
            raise ForbiddenException(
                message=f"Invalid token type. Expected 'access', got '{token_type}'"
            )
        
        # 3. 获取用户名
        username = payload.get("sub")
        if not username:
            raise ForbiddenException(message="Invalid token: missing username (sub)")
        
        # 4. 从数据库验证 token 未被吊销
        result = await db.execute(
            select(ClientSession)
            .where(ClientSession.access_token == token)
            .where(ClientSession.marzban_username == username)
        )
        client_session = result.scalar_one_or_none()
        
        if not client_session:
            raise ForbiddenException(message="Invalid token: session not found")
        
        if client_session.revoked_at is not None:
            raise ForbiddenException(message="Token has been revoked")
        
        # 5. 验证会话是否过期
        now = datetime.utcnow()
        if client_session.expires_at < now:
            raise UnauthorizedException(message="Session has expired")
        
        return ClientTokenPayload(
            username=username,
            exp=datetime.fromtimestamp(payload.get("exp")),
            type=token_type
        )
        
    except jwt.ExpiredSignatureError:
        raise UnauthorizedException(message="Token has expired")
    except jwt.InvalidTokenError as e:
        raise ForbiddenException(message=f"Invalid token: {str(e)}")


# ============ Endpoints ============

@router.get(
    "",
    response_model=Response[SubscriptionResponseData],
    summary="获取订阅信息",
    description="客户端拉取订阅信息，包含订阅链接、过期时间、流量使用情况。需要 client_token 认证。"
)
async def get_subscription(
    client_token: ClientTokenPayload = Depends(verify_client_token),
    db: AsyncSession = Depends(get_db),
    _: None = Depends(client_rate_limit)
) -> Response[SubscriptionResponseData]:
    """
    获取订阅信息
    
    - 返回订阅链接 URL
    - 返回账户过期时间
    - 返回流量使用情况（总量、已用、剩余）
    - 需要有效的 client_token
    - Token 过期返回 401
    - Token 无效或被吊销返回 401/403
    """
    username = client_token.username
    
    try:
        # 查询 Marzban 用户信息
        marzban = await get_marzban_client()
        marzban_user = await marzban.get_user(username)
        
        if not marzban_user:
            logger.error(f"Marzban user not found: {username}")
            raise NotFoundException(message="User not found in Marzban")
        
        # 计算流量信息
        traffic_total = marzban_user.data_limit if marzban_user.data_limit else 0
        traffic_used = marzban_user.used_traffic
        traffic_remaining = max(0, traffic_total - traffic_used) if traffic_total > 0 else 0
        
        # 转换过期时间
        expires_at = datetime.utcnow()
        if marzban_user.expire:
            expires_at = datetime.fromtimestamp(marzban_user.expire)
        
        # 构建响应数据
        subscription_data = SubscriptionResponseData(
            subscription_url=marzban_user.subscription_url,
            expires_at=expires_at,
            traffic_total=traffic_total,
            traffic_used=traffic_used,
            traffic_remaining=traffic_remaining,
            nodes=None  # 可选字段，暂时返回 None
        )
        
        logger.info(f"Subscription info retrieved for user: {username}")
        
        return Response(
            code="SUCCESS",
            message="success",
            data=subscription_data
        )
        
    except MarzbanAPIError as e:
        logger.error(f"Failed to get Marzban user info for {username}: {e}")
        raise UnauthorizedException(message=f"Failed to retrieve subscription info: {e.message}")
