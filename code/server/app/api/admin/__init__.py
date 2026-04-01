"""
Admin API routes
管理端API路由
"""
from fastapi import APIRouter

from app.api.admin.orders import router as orders_router
from app.api.admin.plans import router as plans_router
from app.api.admin.audit_logs import router as audit_logs_router

router = APIRouter(prefix="/admin/v1")

router.include_router(orders_router)
router.include_router(plans_router)
router.include_router(audit_logs_router)
