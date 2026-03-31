"""
FastAPI application entry point
"""
from contextlib import asynccontextmanager

from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware

from app.core.config import get_settings
from app.core.exceptions import (
    AppException,
    app_exception_handler,
    general_exception_handler,
)
from app.core.logging import configure_logging, get_logger

settings = get_settings()
logger = get_logger(__name__)


@asynccontextmanager
async def lifespan(app: FastAPI):
    """Application lifespan events"""
    # Startup
    configure_logging(settings.debug)
    logger.info(
        "application_starting",
        app_name=settings.app_name,
        version=settings.app_version,
        environment=settings.environment,
    )
    
    # Initialize database tables in development
    if settings.is_development:
        from app.core.database import init_db
        init_db()
        logger.info("database_initialized")
    
    yield
    
    # Shutdown
    logger.info("application_shutting_down")


app = FastAPI(
    title=settings.app_name,
    version=settings.app_version,
    description="v2rayNG Payment Bridge API",
    docs_url="/docs" if settings.is_development else None,
    redoc_url="/redoc" if settings.is_development else None,
    lifespan=lifespan,
)

# CORS
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"] if settings.is_development else [],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# Exception handlers
app.add_exception_handler(AppException, app_exception_handler)
app.add_exception_handler(Exception, general_exception_handler)


# Health check
@app.get("/healthz")
async def health_check():
    return {"status": "healthy", "version": settings.app_version}


# Import and register routers
from app.api.client import plans_router, auth_router, orders_router, subscription_router
from app.api.admin import router as admin_router

# Client APIs (public)
app.include_router(plans_router, prefix="/client/v1", tags=["client-plans"])
app.include_router(auth_router, prefix="/client/v1", tags=["client-auth"])
app.include_router(orders_router, prefix="/client/v1", tags=["client-orders"])
app.include_router(subscription_router, prefix="/client/v1", tags=["client-subscription"])

# Admin APIs (protected)
app.include_router(admin_router)


@app.get("/")
async def root():
    return {
        "name": settings.app_name,
        "version": settings.app_version,
        "environment": settings.environment,
    }
