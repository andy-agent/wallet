"""
Client API - Plan module
客户端套餐接口
"""
from typing import List, Optional

from fastapi import APIRouter, Depends
from pydantic import BaseModel, Field
from sqlalchemy import select
from sqlalchemy.ext.asyncio import AsyncSession

from app.core.database import get_db
from app.core.exceptions import NotFoundException
from app.models.plan import Plan
from app.schemas.base import Response
# from app.services.fx_rate import BASE_PRICE_USD

router = APIRouter(prefix="/plans", tags=["plans"])


class PlanItem(BaseModel):
    """套餐列表项"""
    id: str = Field(..., description="套餐ID")
    name: str = Field(..., description="套餐名称")
    description: str = Field(default="", description="套餐描述")
    traffic_bytes: int = Field(..., description="流量字节数")
    duration_days: int = Field(..., description="有效期天数")
    price_usd: str = Field(..., description="价格（USD）")
    supported_assets: List[str] = Field(default=["SOL", "USDT_TRC20"], description="支持的支付方式")
    badge: Optional[str] = Field(default=None, description="标签（如 HOT/NEW）")
    
    class Config:
        from_attributes = True


class PlansResponseData(BaseModel):
    """套餐列表响应数据"""
    plans: List[PlanItem]


@router.get(
    "",
    response_model=Response[PlansResponseData],
    summary="获取套餐列表",
    description="获取所有启用的套餐列表，按 sort_order 排序。公开接口，无需认证。"
)
async def list_plans(
    db: AsyncSession = Depends(get_db)
) -> Response[PlansResponseData]:
    """
    获取启用的套餐列表
    
    - 只返回 enabled=true 的套餐
    - 按 sort_order 升序排列
    - 公开接口，无需认证
    """
    # 查询启用的套餐，按 sort_order 排序
    result = await db.execute(
        select(Plan)
        .where(Plan.enabled == True)
        .order_by(Plan.sort_order.asc())
    )
    plans = result.scalars().all()
    
    # 转换为响应模型
    plan_items = []
    for plan in plans:
        # 根据 sort_order 设置 badge（前两个标记为 HOT）
        badge = None
        if plan.sort_order == 0:
            badge = "HOT"
        elif plan.sort_order == 1:
            badge = "NEW"
        
        plan_items.append(PlanItem(
            id=plan.id,
            name=plan.name,
            description=plan.description or "",
            traffic_bytes=plan.traffic_bytes,
            duration_days=plan.duration_days,
            price_usd=str(plan.price_usd),
            supported_assets=plan.supported_assets or ["SOL", "USDT_TRC20"],
            badge=badge
        ))
    
    return Response(
        code="SUCCESS",
        message="success",
        data=PlansResponseData(plans=plan_items)
    )


@router.get(
    "/{plan_id}",
    response_model=Response[PlanItem],
    summary="获取套餐详情",
    description="根据ID获取单个套餐详情。公开接口，无需认证。"
)
async def get_plan(
    plan_id: str,
    db: AsyncSession = Depends(get_db)
) -> Response[PlanItem]:
    """
    获取单个套餐详情
    
    - 只返回 enabled=true 的套餐
    - 公开接口，无需认证
    """
    result = await db.execute(
        select(Plan)
        .where(Plan.id == plan_id, Plan.enabled == True)
    )
    plan = result.scalar_one_or_none()
    
    if not plan:
        raise NotFoundException(message="套餐不存在或已下架")
    
    # 根据 sort_order 设置 badge
    badge = None
    if plan.sort_order == 0:
        badge = "HOT"
    elif plan.sort_order == 1:
        badge = "NEW"
    
    plan_item = PlanItem(
        id=plan.id,
        name=plan.name,
        description=plan.description or "",
        traffic_bytes=plan.traffic_bytes,
        duration_days=plan.duration_days,
        price_usd=str(BASE_PRICE_USD),  # 统一使用基础价格 3.00
        supported_assets=plan.supported_assets or ["SOL", "USDT_TRC20"],
        badge=badge
    )
    
    return Response(
        code="SUCCESS",
        message="success",
        data=plan_item
    )
