"""
Admin API routes
管理端API路由
"""
from fastapi import APIRouter

from app.api.admin.orders import router as orders_router

router = APIRouter(prefix="/admin/v1")

router.include_router(orders_router)
