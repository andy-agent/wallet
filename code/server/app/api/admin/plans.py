"""
Admin API - Plan module
管理端套餐接口
"""
from decimal import Decimal
from typing import List, Optional

import jwt
import ulid
from fastapi import APIRouter, Depends, Header, Query
from pydantic import BaseModel, Field, validator
from sqlalchemy import func, select, and_
from sqlalchemy.ext.asyncio import AsyncSession

from app.core.config import get_settings
from app.core.database import get_db
from app.core.exceptions import (
    NotFoundException,
    UnauthorizedException,
    ForbiddenException,
    ConflictException,
    ValidationException,
)
from app.core.rate_limit import admin_rate_limit
from app.models.plan import Plan
from app.models.order import Order
from app.schemas.base import Response, Pagination, PaginatedResponse

router = APIRouter(prefix="/plans", tags=["admin-plans"])

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

class PlanCreateRequest(BaseModel):
    """创建套餐请求"""
    code: str = Field(..., min_length=1, max_length=32, description="套餐代码")
    name: str = Field(..., min_length=1, max_length=128, description="套餐名称")
    description: str = Field(default="", max_length=512, description="套餐描述")
    traffic_bytes: int = Field(..., ge=1, description="流量字节数")
    duration_days: int = Field(..., ge=1, description="有效期天数")
    price_usd: Decimal = Field(..., description="价格（USD）")
    supported_assets: List[str] = Field(default=["SOL", "USDT_TRC20"], description="支持的支付方式")
    enabled: bool = Field(default=True, description="是否启用")
    sort_order: int = Field(default=0, description="排序顺序")
    
    @validator('price_usd')
    def validate_price_usd(cls, v):
        if v <= 0:
            raise ValueError('price_usd must be greater than 0')
        return v


class PlanUpdateRequest(BaseModel):
    """更新套餐请求"""
    code: Optional[str] = Field(None, min_length=1, max_length=32, description="套餐代码")
    name: Optional[str] = Field(None, min_length=1, max_length=128, description="套餐名称")
    description: Optional[str] = Field(None, max_length=512, description="套餐描述")
    traffic_bytes: Optional[int] = Field(None, ge=1, description="流量字节数")
    duration_days: Optional[int] = Field(None, ge=1, description="有效期天数")
    price_usd: Optional[Decimal] = Field(None, description="价格（USD）")
    supported_assets: Optional[List[str]] = Field(None, description="支持的支付方式")
    enabled: Optional[bool] = Field(None, description="是否启用")
    sort_order: Optional[int] = Field(None, description="排序顺序")
    
    @validator('price_usd')
    def validate_price_usd(cls, v):
        if v is not None and v <= 0:
            raise ValueError('price_usd must be greater than 0')
        return v
    
    @validator('duration_days')
    def validate_duration_days(cls, v):
        if v is not None and v <= 0:
            raise ValueError('duration_days must be greater than 0')
        return v


class PlanEnableRequest(BaseModel):
    """启用/禁用套餐请求"""
    enabled: bool = Field(..., description="是否启用")


class PlanItem(BaseModel):
    """套餐列表项"""
    id: str = Field(..., description="套餐ID")
    code: str = Field(..., description="套餐代码")
    name: str = Field(..., description="套餐名称")
    description: str = Field(default="", description="套餐描述")
    traffic_bytes: int = Field(..., description="流量字节数")
    duration_days: int = Field(..., description="有效期天数")
    price_usd: str = Field(..., description="价格（USD）")
    supported_assets: List[str] = Field(default=["SOL", "USDT_TRC20"], description="支持的支付方式")
    enabled: bool = Field(..., description="是否启用")
    sort_order: int = Field(..., description="排序顺序")
    created_at: str = Field(..., description="创建时间")
    
    class Config:
        from_attributes = True


class PlanDetailResponse(PlanItem):
    """套餐详情响应"""
    order_count: int = Field(..., description="关联订单数量")


# ============ Endpoints ============

@router.post(
    "",
    response_model=Response[PlanItem],
    summary="创建套餐",
    description="创建新套餐。需要 admin_token 认证。"
)
async def create_plan(
    request: PlanCreateRequest,
    db: AsyncSession = Depends(get_db),
    admin_token: AdminTokenPayload = Depends(verify_admin_token),
    _: None = Depends(admin_rate_limit)
) -> Response[PlanItem]:
    """
    创建套餐
    
    - 需要 admin_token 认证
    - code 必须唯一
    """
    # 检查 code 是否已存在
    result = await db.execute(select(Plan).where(Plan.code == request.code))
    existing = result.scalar_one_or_none()
    if existing:
        raise ConflictException(message=f"套餐代码 '{request.code}' 已存在")
    
    # 创建新套餐
    plan = Plan(
        id=str(ulid.new()),
        code=request.code,
        name=request.name,
        description=request.description,
        traffic_bytes=request.traffic_bytes,
        duration_days=request.duration_days,
        price_usd=request.price_usd,
        supported_assets=request.supported_assets,
        enabled=request.enabled,
        sort_order=request.sort_order
    )
    
    db.add(plan)
    await db.commit()
    await db.refresh(plan)
    
    return Response(
        code="SUCCESS",
        message="success",
        data=PlanItem(
            id=plan.id,
            code=plan.code,
            name=plan.name,
            description=plan.description or "",
            traffic_bytes=plan.traffic_bytes,
            duration_days=plan.duration_days,
            price_usd=str(plan.price_usd),
            supported_assets=plan.supported_assets or ["SOL", "USDT_TRC20"],
            enabled=plan.enabled,
            sort_order=plan.sort_order,
            created_at=str(plan.created_at)
        )
    )


@router.get(
    "",
    response_model=PaginatedResponse[PlanItem],
    summary="查询套餐列表",
    description="获取套餐列表，支持分页。需要 admin_token 认证。"
)
async def list_plans(
    enabled: Optional[bool] = Query(None, description="按启用状态筛选"),
    page: int = Query(1, ge=1, description="页码"),
    size: int = Query(20, ge=1, le=100, description="每页数量"),
    db: AsyncSession = Depends(get_db),
    admin_token: AdminTokenPayload = Depends(verify_admin_token),
    _: None = Depends(admin_rate_limit)
) -> PaginatedResponse[PlanItem]:
    """
    查询套餐列表（分页）
    
    - 支持按启用状态筛选
    - 按 sort_order 升序排列
    - 需要 admin_token 认证
    """
    # 构建查询条件
    conditions = []
    if enabled is not None:
        conditions.append(Plan.enabled == enabled)
    
    # 查询总数
    count_query = select(func.count(Plan.id))
    if conditions:
        count_query = count_query.where(and_(*conditions))
    total_result = await db.execute(count_query)
    total = total_result.scalar()
    
    # 查询套餐列表
    query = select(Plan)
    if conditions:
        query = query.where(and_(*conditions))
    
    query = (
        query
        .order_by(Plan.sort_order.asc(), Plan.created_at.desc())
        .offset((page - 1) * size)
        .limit(size)
    )
    
    result = await db.execute(query)
    plans = result.scalars().all()
    
    # 转换为响应模型
    plan_items = [
        PlanItem(
            id=plan.id,
            code=plan.code,
            name=plan.name,
            description=plan.description or "",
            traffic_bytes=plan.traffic_bytes,
            duration_days=plan.duration_days,
            price_usd=str(plan.price_usd),
            supported_assets=plan.supported_assets or ["SOL", "USDT_TRC20"],
            enabled=plan.enabled,
            sort_order=plan.sort_order,
            created_at=str(plan.created_at)
        )
        for plan in plans
    ]
    
    # 计算总页数
    pages = (total + size - 1) // size if total > 0 else 1
    
    return PaginatedResponse(
        code="SUCCESS",
        message="success",
        data=plan_items,
        pagination=Pagination(
            total=total,
            page=page,
            size=size,
            pages=pages
        )
    )


@router.get(
    "/{plan_id}",
    response_model=Response[PlanDetailResponse],
    summary="获取套餐详情",
    description="获取单个套餐详情，包含关联订单数量。需要 admin_token 认证。"
)
async def get_plan(
    plan_id: str,
    db: AsyncSession = Depends(get_db),
    admin_token: AdminTokenPayload = Depends(verify_admin_token),
    _: None = Depends(admin_rate_limit)
) -> Response[PlanDetailResponse]:
    """
    获取套餐详情
    
    - 包含关联订单数量
    - 需要 admin_token 认证
    """
    # 查询套餐
    result = await db.execute(select(Plan).where(Plan.id == plan_id))
    plan = result.scalar_one_or_none()
    
    if not plan:
        raise NotFoundException(message="套餐不存在")
    
    # 查询关联订单数量
    order_count_result = await db.execute(
        select(func.count(Order.id)).where(Order.plan_id == plan_id)
    )
    order_count = order_count_result.scalar() or 0
    
    return Response(
        code="SUCCESS",
        message="success",
        data=PlanDetailResponse(
            id=plan.id,
            code=plan.code,
            name=plan.name,
            description=plan.description or "",
            traffic_bytes=plan.traffic_bytes,
            duration_days=plan.duration_days,
            price_usd=str(plan.price_usd),
            supported_assets=plan.supported_assets or ["SOL", "USDT_TRC20"],
            enabled=plan.enabled,
            sort_order=plan.sort_order,
            created_at=str(plan.created_at),
            order_count=order_count
        )
    )


@router.put(
    "/{plan_id}",
    response_model=Response[PlanItem],
    summary="更新套餐",
    description="更新套餐信息。需要 admin_token 认证。"
)
async def update_plan(
    plan_id: str,
    request: PlanUpdateRequest,
    db: AsyncSession = Depends(get_db),
    admin_token: AdminTokenPayload = Depends(verify_admin_token),
    _: None = Depends(admin_rate_limit)
) -> Response[PlanItem]:
    """
    更新套餐
    
    - code 修改时必须唯一
    - 需要 admin_token 认证
    """
    # 查询套餐
    result = await db.execute(select(Plan).where(Plan.id == plan_id))
    plan = result.scalar_one_or_none()
    
    if not plan:
        raise NotFoundException(message="套餐不存在")
    
    # 如果修改 code，检查唯一性
    if request.code is not None and request.code != plan.code:
        result = await db.execute(select(Plan).where(Plan.code == request.code))
        existing = result.scalar_one_or_none()
        if existing:
            raise ConflictException(message=f"套餐代码 '{request.code}' 已存在")
        plan.code = request.code
    
    # 更新字段
    if request.name is not None:
        plan.name = request.name
    if request.description is not None:
        plan.description = request.description
    if request.traffic_bytes is not None:
        plan.traffic_bytes = request.traffic_bytes
    if request.duration_days is not None:
        plan.duration_days = request.duration_days
    if request.price_usd is not None:
        plan.price_usd = request.price_usd
    if request.supported_assets is not None:
        plan.supported_assets = request.supported_assets
    if request.enabled is not None:
        plan.enabled = request.enabled
    if request.sort_order is not None:
        plan.sort_order = request.sort_order
    
    await db.commit()
    await db.refresh(plan)
    
    return Response(
        code="SUCCESS",
        message="success",
        data=PlanItem(
            id=plan.id,
            code=plan.code,
            name=plan.name,
            description=plan.description or "",
            traffic_bytes=plan.traffic_bytes,
            duration_days=plan.duration_days,
            price_usd=str(plan.price_usd),
            supported_assets=plan.supported_assets or ["SOL", "USDT_TRC20"],
            enabled=plan.enabled,
            sort_order=plan.sort_order,
            created_at=str(plan.created_at)
        )
    )


@router.patch(
    "/{plan_id}/enable",
    response_model=Response[PlanItem],
    summary="启用/禁用套餐",
    description="启用或禁用套餐。需要 admin_token 认证。"
)
async def enable_plan(
    plan_id: str,
    request: PlanEnableRequest,
    db: AsyncSession = Depends(get_db),
    admin_token: AdminTokenPayload = Depends(verify_admin_token),
    _: None = Depends(admin_rate_limit)
) -> Response[PlanItem]:
    """
    启用/禁用套餐
    
    - 需要 admin_token 认证
    """
    # 查询套餐
    result = await db.execute(select(Plan).where(Plan.id == plan_id))
    plan = result.scalar_one_or_none()
    
    if not plan:
        raise NotFoundException(message="套餐不存在")
    
    # 更新启用状态
    plan.enabled = request.enabled
    
    await db.commit()
    await db.refresh(plan)
    
    return Response(
        code="SUCCESS",
        message="success",
        data=PlanItem(
            id=plan.id,
            code=plan.code,
            name=plan.name,
            description=plan.description or "",
            traffic_bytes=plan.traffic_bytes,
            duration_days=plan.duration_days,
            price_usd=str(plan.price_usd),
            supported_assets=plan.supported_assets or ["SOL", "USDT_TRC20"],
            enabled=plan.enabled,
            sort_order=plan.sort_order,
            created_at=str(plan.created_at)
        )
    )


@router.delete(
    "/{plan_id}",
    response_model=Response[dict],
    summary="删除套餐",
    description="删除套餐。如果套餐有关联订单，则无法删除。需要 admin_token 认证。"
)
async def delete_plan(
    plan_id: str,
    db: AsyncSession = Depends(get_db),
    admin_token: AdminTokenPayload = Depends(verify_admin_token),
    _: None = Depends(admin_rate_limit)
) -> Response[dict]:
    """
    删除套餐
    
    - 有关联订单的套餐无法删除
    - 需要 admin_token 认证
    """
    # 查询套餐
    result = await db.execute(select(Plan).where(Plan.id == plan_id))
    plan = result.scalar_one_or_none()
    
    if not plan:
        raise NotFoundException(message="套餐不存在")
    
    # 检查是否有关联订单
    order_count_result = await db.execute(
        select(func.count(Order.id)).where(Order.plan_id == plan_id)
    )
    order_count = order_count_result.scalar() or 0
    
    if order_count > 0:
        raise ConflictException(
            message=f"无法删除：该套餐存在 {order_count} 个关联订单"
        )
    
    # 删除套餐
    await db.delete(plan)
    await db.commit()
    
    return Response(
        code="SUCCESS",
        message="success",
        data={"deleted": True, "plan_id": plan_id}
    )
