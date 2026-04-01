"""
Client API endpoints
客户端接口
"""
from app.api.client.plans import router as plans_router
from app.api.client.auth import router as auth_router
from app.api.client.orders import router as orders_router
from app.api.client.subscription import router as subscription_router
from app.api.client.ws import router as ws_router

__all__ = ["plans_router", "auth_router", "orders_router", "subscription_router", "ws_router"]
