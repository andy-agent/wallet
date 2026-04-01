"""
Admin API - Audit Log module
管理端审计日志接口
"""
from datetime import datetime
from typing import List, Optional

from fastapi import APIRouter, Depends, Header, Query
from pydantic import BaseModel, Field
from sqlalchemy import func, select, and_
from sqlalchemy.ext.asyncio import AsyncSession

from app.core.config import get_settings
from app.core.database import get_db
from app.core.exceptions import UnauthorizedException, ForbiddenException
from app.core.rate_limit import admin_rate_limit
from app.models.audit_log import AuditLog, OperatorType
from app.schemas.base import Response, Pagination, PaginatedResponse

router = APIRouter(prefix="/audit-logs", tags=["admin-audit-logs"])

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
        import jwt
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

class AuditLogItem(BaseModel):
    """审计日志列表项"""
    id: str = Field(..., description="日志ID")
    entity_type: str = Field(..., description="实体类型: order, payment, user")
    entity_id: str = Field(..., description="实体ID")
    action: str = Field(..., description="操作类型: created, updated, paid, fulfilled, etc.")
    operator_type: str = Field(..., description="操作者类型: system, admin, worker, client")
    operator_id: Optional[str] = Field(None, description="操作者ID")
    payload_json: Optional[str] = Field(None, description="操作详情(JSON格式)")
    client_ip: Optional[str] = Field(None, description="客户端IP")
    user_agent: Optional[str] = Field(None, description="用户代理")
    created_at: datetime = Field(..., description="操作时间")
    
    class Config:
        from_attributes = True


class AuditLogListResponseData(BaseModel):
    """审计日志列表响应数据"""
    logs: List[AuditLogItem]


# ============ Endpoints ============

@router.get(
    "",
    response_model=PaginatedResponse[AuditLogItem],
    summary="获取审计日志列表",
    description="获取审计日志列表，支持多条件筛选和分页。需要 admin_token 认证。"
)
async def list_audit_logs(
    entity_type: Optional[str] = Query(None, description="按实体类型筛选: order, payment, user"),
    entity_id: Optional[str] = Query(None, description="按实体ID筛选"),
    action: Optional[str] = Query(None, description="按操作类型筛选: created, updated, paid, fulfilled, etc."),
    operator_type: Optional[OperatorType] = Query(None, description="按操作者类型筛选: system, admin, worker, client"),
    created_at_from: Optional[datetime] = Query(None, description="开始时间 (ISO格式)"),
    created_at_to: Optional[datetime] = Query(None, description="结束时间 (ISO格式)"),
    page: int = Query(1, ge=1, description="页码"),
    page_size: int = Query(20, ge=1, le=100, description="每页数量"),
    db: AsyncSession = Depends(get_db),
    admin_token: AdminTokenPayload = Depends(verify_admin_token),
    _: None = Depends(admin_rate_limit)
) -> PaginatedResponse[AuditLogItem]:
    """
    获取审计日志列表（分页）
    
    - 支持按实体类型、实体ID、操作类型、操作者类型、时间范围筛选
    - 支持多条件组合筛选
    - 需要 admin_token 认证
    """
    # 构建查询条件
    conditions = []
    if entity_type:
        conditions.append(AuditLog.entity_type == entity_type)
    if entity_id:
        conditions.append(AuditLog.entity_id == entity_id)
    if action:
        conditions.append(AuditLog.action == action)
    if operator_type:
        conditions.append(AuditLog.operator_type == operator_type)
    if created_at_from:
        conditions.append(AuditLog.created_at >= created_at_from)
    if created_at_to:
        conditions.append(AuditLog.created_at <= created_at_to)
    
    # 查询总数
    count_query = select(func.count(AuditLog.id))
    if conditions:
        count_query = count_query.where(and_(*conditions))
    total_result = await db.execute(count_query)
    total = total_result.scalar()
    
    # 查询审计日志列表
    query = select(AuditLog)
    if conditions:
        query = query.where(and_(*conditions))
    
    query = (
        query
        .order_by(AuditLog.created_at.desc())
        .offset((page - 1) * page_size)
        .limit(page_size)
    )
    
    result = await db.execute(query)
    logs = result.scalars().all()
    
    # 转换为响应模型
    log_items = [
        AuditLogItem(
            id=log.id,
            entity_type=log.entity_type,
            entity_id=log.entity_id,
            action=log.action,
            operator_type=log.operator_type.value,
            operator_id=log.operator_id,
            payload_json=log.payload_json,
            client_ip=log.client_ip,
            user_agent=log.user_agent,
            created_at=log.created_at
        )
        for log in logs
    ]
    
    # 计算总页数
    pages = (total + page_size - 1) // page_size if total > 0 else 1
    
    return PaginatedResponse(
        code="SUCCESS",
        message="success",
        data=log_items,
        pagination=Pagination(
            total=total,
            page=page,
            size=page_size,
            pages=pages
        )
    )


@router.get(
    "/{entity_type}/{entity_id}",
    response_model=Response[List[AuditLogItem]],
    summary="获取单实体审计追踪",
    description="获取指定实体的所有审计日志，按时间倒序排列。需要 admin_token 认证。"
)
async def get_entity_audit_trail(
    entity_type: str,
    entity_id: str,
    db: AsyncSession = Depends(get_db),
    admin_token: AdminTokenPayload = Depends(verify_admin_token),
    _: None = Depends(admin_rate_limit)
) -> Response[List[AuditLogItem]]:
    """
    获取单实体审计追踪
    
    - 返回指定实体的所有审计日志
    - 按时间倒序排列（最新的在前）
    - 包含完整的操作人、时间、变更详情
    - 需要 admin_token 认证
    """
    # 查询指定实体的审计日志
    query = (
        select(AuditLog)
        .where(
            and_(
                AuditLog.entity_type == entity_type,
                AuditLog.entity_id == entity_id
            )
        )
        .order_by(AuditLog.created_at.desc())
    )
    
    result = await db.execute(query)
    logs = result.scalars().all()
    
    # 转换为响应模型
    log_items = [
        AuditLogItem(
            id=log.id,
            entity_type=log.entity_type,
            entity_id=log.entity_id,
            action=log.action,
            operator_type=log.operator_type.value,
            operator_id=log.operator_id,
            payload_json=log.payload_json,
            client_ip=log.client_ip,
            user_agent=log.user_agent,
            created_at=log.created_at
        )
        for log in logs
    ]
    
    return Response(
        code="SUCCESS",
        message="success",
        data=log_items
    )
