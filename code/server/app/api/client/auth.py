"""
Client API - Auth module
客户端认证接口

提供用户注册、登录、Token刷新等功能
"""
import logging
import re
from datetime import datetime, timedelta, timezone
from typing import Optional

import jwt
from fastapi import APIRouter, Depends, Header
from passlib.context import CryptContext
from pydantic import BaseModel, Field, field_validator
from sqlalchemy import select
from sqlalchemy.ext.asyncio import AsyncSession
import ulid

from app.core.config import get_settings
from app.core.database import get_db
from app.core.exceptions import (
    AppException,
    ErrorCode,
    ValidationException,
    UnauthorizedException,
    ConflictException
)
from app.models.user import User
from app.models.client_session import ClientSession
from app.schemas.base import Response

logger = logging.getLogger(__name__)
router = APIRouter(prefix="/auth", tags=["auth"])
settings = get_settings()

# 密码加密上下文
pwd_context = CryptContext(schemes=["bcrypt"], deprecated="auto")


# ========== 密码工具函数 ==========

def verify_password(plain_password: str, hashed_password: str) -> bool:
    """验证密码"""
    # bcrypt 限制密码长度最多72字节
    return pwd_context.verify(plain_password[:72], hashed_password)


def get_password_hash(password: str) -> str:
    """获取密码哈希"""
    # bcrypt 限制密码长度最多72字节
    return pwd_context.hash(password[:72])


def validate_password_strength(password: str) -> tuple[bool, Optional[str]]:
    """
    验证密码强度
    
    要求：
    - 至少8位
    - 包含至少一个大写字母
    - 包含至少一个小写字母
    - 包含至少一个数字
    """
    if len(password) < 8:
        return False, "密码长度至少8位"
    
    if not re.search(r'[A-Z]', password):
        return False, "密码必须包含至少一个大写字母"
    
    if not re.search(r'[a-z]', password):
        return False, "密码必须包含至少一个小写字母"
    
    if not re.search(r'\d', password):
        return False, "密码必须包含至少一个数字"
    
    return True, None


# ========== JWT 工具函数 ==========

def create_access_token(user_id: str, username: str, expires_delta: Optional[timedelta] = None) -> str:
    """创建访问令牌"""
    if expires_delta:
        expire = datetime.now(timezone.utc) + expires_delta
    else:
        expire = datetime.now(timezone.utc) + timedelta(minutes=settings.jwt_access_token_expire_minutes)
    
    to_encode = {
        "sub": username,
        "user_id": user_id,
        "exp": expire,
        "type": "access",
        "iat": datetime.now(timezone.utc)
    }
    return jwt.encode(to_encode, settings.jwt_secret, algorithm=settings.jwt_algorithm)


def create_refresh_token(user_id: str, username: str, expires_delta: Optional[timedelta] = None) -> str:
    """创建刷新令牌"""
    if expires_delta:
        expire = datetime.now(timezone.utc) + expires_delta
    else:
        expire = datetime.now(timezone.utc) + timedelta(days=settings.jwt_refresh_token_expire_days)
    
    to_encode = {
        "sub": username,
        "user_id": user_id,
        "exp": expire,
        "type": "refresh",
        "iat": datetime.now(timezone.utc)
    }
    return jwt.encode(to_encode, settings.jwt_secret, algorithm=settings.jwt_algorithm)


def decode_token(token: str) -> dict:
    """解码JWT令牌"""
    return jwt.decode(token, settings.jwt_secret, algorithms=[settings.jwt_algorithm])


# ========== 依赖函数 ==========

async def get_current_user(
    authorization: str = Header(None, description="Authorization: Bearer {access_token}"),
    db: AsyncSession = Depends(get_db)
) -> User:
    """
    获取当前登录用户
    
    从 Authorization header 验证 JWT access_token
    """
    if not authorization or not authorization.startswith("Bearer "):
        raise UnauthorizedException(
            message="缺少或无效的Authorization头。格式: Bearer <token>"
        )
    
    token = authorization.replace("Bearer ", "")
    
    try:
        # 解码JWT
        payload = decode_token(token)
        
        # 验证token类型
        token_type = payload.get("type")
        if token_type != "access":
            raise ForbiddenException(message=f"无效的token类型，期望'access'，得到'{token_type}'")
        
        # 获取用户信息
        user_id = payload.get("user_id")
        username = payload.get("sub")
        
        if not user_id or not username:
            raise ForbiddenException(message="无效的token: 缺少用户信息")
        
        # 查询用户
        result = await db.execute(select(User).where(User.id == user_id))
        user = result.scalar_one_or_none()
        
        if not user:
            raise UnauthorizedException(message="用户不存在")
        
        if not user.is_active:
            raise ForbiddenException(message="用户已被禁用")
        
        return user
        
    except jwt.ExpiredSignatureError:
        raise UnauthorizedException(message="Token已过期")
    except jwt.InvalidTokenError as e:
        raise ForbiddenException(message=f"无效的token: {str(e)}")


# ========== 请求/响应模型 ==========

class RegisterRequest(BaseModel):
    """注册请求"""
    username: str = Field(..., min_length=3, max_length=64, description="用户名")
    password: str = Field(..., min_length=8, max_length=128, description="密码")
    email: Optional[str] = Field(default=None, description="邮箱（可选）")
    
    @field_validator('username')
    @classmethod
    def validate_username(cls, v: str) -> str:
        # 用户名只能包含字母、数字、下划线
        if not re.match(r'^[a-zA-Z0-9_]+$', v):
            raise ValueError('用户名只能包含字母、数字和下划线')
        return v
    
    @field_validator('email')
    @classmethod
    def validate_email(cls, v: Optional[str]) -> Optional[str]:
        if v is None:
            return v
        # 简单的邮箱格式验证
        if not re.match(r'^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$', v):
            raise ValueError('邮箱格式无效')
        return v.lower()


class LoginRequest(BaseModel):
    """登录请求"""
    username: str = Field(..., description="用户名")
    password: str = Field(..., description="密码")


class TokenResponseData(BaseModel):
    """Token响应数据"""
    user_id: str = Field(..., description="用户ID")
    username: str = Field(..., description="用户名")
    access_token: str = Field(..., description="访问令牌")
    refresh_token: str = Field(..., description="刷新令牌")
    expires_at: datetime = Field(..., description="访问令牌过期时间")


class RefreshResponseData(BaseModel):
    """刷新令牌响应数据"""
    access_token: str = Field(..., description="新的访问令牌")
    expires_at: datetime = Field(..., description="过期时间")


# ========== API 端点 ==========

@router.post(
    "/register",
    response_model=Response[TokenResponseData],
    summary="用户注册",
    description="注册新用户，返回用户信息和Token"
)
async def register(
    request: RegisterRequest,
    x_client_version: str = Header(default="unknown", description="客户端版本"),
    db: AsyncSession = Depends(get_db)
) -> Response[TokenResponseData]:
    """
    用户注册
    
    - 验证用户名唯一性
    - 验证密码强度
    - 使用 bcrypt 加密密码
    - 返回用户信息和 JWT Token
    """
    # 验证密码强度
    is_valid, error_msg = validate_password_strength(request.password)
    if not is_valid:
        raise ValidationException(message=error_msg)
    
    # 检查用户名是否已存在
    result = await db.execute(
        select(User).where(User.username == request.username)
    )
    if result.scalar_one_or_none():
        raise ConflictException(message="用户名已存在")
    
    # 检查邮箱是否已存在（如果提供）
    if request.email:
        result = await db.execute(
            select(User).where(User.email == request.email)
        )
        if result.scalar_one_or_none():
            raise ConflictException(message="邮箱已被注册")
    
    # 创建用户
    user_id = str(ulid.new().str)
    password_hash = get_password_hash(request.password)
    
    user = User(
        id=user_id,
        username=request.username,
        password_hash=password_hash,
        email=request.email,
        is_active=True,
    )
    
    db.add(user)
    await db.flush()
    
    # 生成Token
    access_token = create_access_token(user_id, request.username)
    refresh_token = create_refresh_token(user_id, request.username)
    expires_at = datetime.now(timezone.utc) + timedelta(
        minutes=settings.jwt_access_token_expire_minutes
    )
    
    # 创建客户端会话
    session_id = str(ulid.new().str)
    refresh_expires = datetime.now(timezone.utc) + timedelta(
        days=settings.jwt_refresh_token_expire_days
    )
    
    client_session = ClientSession(
        id=session_id,
        user_id=user_id,
        access_token=access_token,
        refresh_token=refresh_token,
        expires_at=refresh_expires,
    )
    
    db.add(client_session)
    await db.flush()
    
    logger.info(f"用户注册成功: {request.username}, id={user_id}")
    
    return Response(
        code="SUCCESS",
        message="注册成功",
        data=TokenResponseData(
            user_id=user_id,
            username=request.username,
            access_token=access_token,
            refresh_token=refresh_token,
            expires_at=expires_at,
        )
    )


@router.post(
    "/login",
    response_model=Response[TokenResponseData],
    summary="用户登录",
    description="用户登录，验证用户名密码，返回Token"
)
async def login(
    request: LoginRequest,
    x_client_version: str = Header(default="unknown", description="客户端版本"),
    db: AsyncSession = Depends(get_db)
) -> Response[TokenResponseData]:
    """
    用户登录
    
    - 验证用户名密码
    - 检查用户是否激活
    - 返回 JWT Token
    """
    # 查询用户
    result = await db.execute(
        select(User).where(User.username == request.username)
    )
    user = result.scalar_one_or_none()
    
    if not user:
        # 不透露用户名是否存在，统一返回认证失败
        raise UnauthorizedException(message="用户名或密码错误")
    
    # 验证密码
    if not verify_password(request.password, user.password_hash):
        raise UnauthorizedException(message="用户名或密码错误")
    
    # 检查用户状态
    if not user.is_active:
        raise ForbiddenException(message="用户已被禁用")
    
    # 生成Token
    access_token = create_access_token(user.id, user.username)
    refresh_token = create_refresh_token(user.id, user.username)
    expires_at = datetime.now(timezone.utc) + timedelta(
        minutes=settings.jwt_access_token_expire_minutes
    )
    
    # 创建客户端会话
    session_id = str(ulid.new().str)
    refresh_expires = datetime.now(timezone.utc) + timedelta(
        days=settings.jwt_refresh_token_expire_days
    )
    
    client_session = ClientSession(
        id=session_id,
        user_id=user.id,
        access_token=access_token,
        refresh_token=refresh_token,
        expires_at=refresh_expires,
    )
    
    db.add(client_session)
    await db.flush()
    
    logger.info(f"用户登录成功: {request.username}, id={user.id}")
    
    return Response(
        code="SUCCESS",
        message="登录成功",
        data=TokenResponseData(
            user_id=user.id,
            username=user.username,
            access_token=access_token,
            refresh_token=refresh_token,
            expires_at=expires_at,
        )
    )


@router.post(
    "/refresh",
    response_model=Response[RefreshResponseData],
    summary="刷新Token",
    description="使用refresh_token获取新的access_token"
)
async def refresh_token(
    authorization: str = Header(..., description="Authorization: Bearer {refresh_token}"),
    db: AsyncSession = Depends(get_db)
) -> Response[RefreshResponseData]:
    """
    刷新访问令牌
    
    - 使用 refresh_token 换取新的 access_token
    - refresh_token 从 Authorization header 获取
    """
    if not authorization or not authorization.startswith("Bearer "):
        raise UnauthorizedException(
            message="缺少或无效的Authorization头。格式: Bearer <refresh_token>"
        )
    
    token = authorization.replace("Bearer ", "")
    
    try:
        # 解码 refresh token
        payload = decode_token(token)
        
        # 验证token类型
        token_type = payload.get("type")
        if token_type != "refresh":
            raise ForbiddenException(message=f"无效的token类型，期望'refresh'，得到'{token_type}'")
        
        # 获取用户信息
        user_id = payload.get("user_id")
        username = payload.get("sub")
        
        if not user_id or not username:
            raise ForbiddenException(message="无效的token: 缺少用户信息")
        
        # 查询用户
        result = await db.execute(select(User).where(User.id == user_id))
        user = result.scalar_one_or_none()
        
        if not user:
            raise UnauthorizedException(message="用户不存在")
        
        if not user.is_active:
            raise ForbiddenException(message="用户已被禁用")
        
        # 验证refresh_token是否在数据库中且未被吊销
        result = await db.execute(
            select(ClientSession).where(
                ClientSession.refresh_token == token,
                ClientSession.user_id == user_id
            )
        )
        session = result.scalar_one_or_none()
        
        if not session:
            raise ForbiddenException(message="无效的refresh_token")
        
        if session.revoked_at is not None:
            raise ForbiddenException(message="token已被吊销")
        
        # 生成新的access_token
        new_access_token = create_access_token(user_id, username)
        expires_at = datetime.now(timezone.utc) + timedelta(
            minutes=settings.jwt_access_token_expire_minutes
        )
        
        # 更新会话的access_token
        session.access_token = new_access_token
        
        await db.flush()
        
        logger.info(f"Token刷新成功: {username}")
        
        return Response(
            code="SUCCESS",
            message="刷新成功",
            data=RefreshResponseData(
                access_token=new_access_token,
                expires_at=expires_at,
            )
        )
        
    except jwt.ExpiredSignatureError:
        raise UnauthorizedException(message="Refresh token已过期，请重新登录")
    except jwt.InvalidTokenError as e:
        raise ForbiddenException(message=f"无效的token: {str(e)}")


# 导出错误码到异常模块
ErrorCode.USERNAME_EXISTS = "USERNAME_EXISTS"
ErrorCode.EMAIL_EXISTS = "EMAIL_EXISTS"
ErrorCode.INVALID_PASSWORD = "INVALID_PASSWORD"
ErrorCode.USER_NOT_FOUND = "USER_NOT_FOUND"
ErrorCode.USER_INACTIVE = "USER_INACTIVE"
