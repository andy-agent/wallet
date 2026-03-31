"""
Custom exceptions and error handlers
"""
from enum import Enum
from typing import Any, Dict, Optional

from fastapi import Request
from fastapi.responses import JSONResponse


class ErrorCode(str, Enum):
    # General
    SUCCESS = "SUCCESS"
    INVALID_REQUEST = "INVALID_REQUEST"
    UNAUTHORIZED = "UNAUTHORIZED"
    FORBIDDEN = "FORBIDDEN"
    NOT_FOUND = "NOT_FOUND"
    INTERNAL_ERROR = "INTERNAL_ERROR"
    SERVICE_UNAVAILABLE = "SERVICE_UNAVAILABLE"
    
    # Business
    INVALID_PLAN_ID = "INVALID_PLAN_ID"
    UNSUPPORTED_ASSET = "UNSUPPORTED_ASSET"
    INVALID_PURCHASE_TYPE = "INVALID_PURCHASE_TYPE"
    INVALID_TOKEN = "INVALID_TOKEN"
    DUPLICATE_ORDER = "DUPLICATE_ORDER"
    ORDER_NOT_FOUND = "ORDER_NOT_FOUND"
    ORDER_EXPIRED = "ORDER_EXPIRED"
    ADDRESS_POOL_EMPTY = "ADDRESS_POOL_EMPTY"
    FULFILL_FAILED = "FULFILL_FAILED"
    FX_RATE_UNAVAILABLE = "FX_RATE_UNAVAILABLE"
    INSUFFICIENT_FUNDS = "INSUFFICIENT_FUNDS"


class AppException(Exception):
    """Base application exception"""
    
    def __init__(
        self,
        code: ErrorCode,
        message: str,
        status_code: int = 400,
        data: Optional[Dict[str, Any]] = None
    ):
        self.code = code
        self.message = message
        self.status_code = status_code
        self.data = data or {}
        super().__init__(message)


class ValidationException(AppException):
    """Validation error"""
    def __init__(self, message: str, data: Optional[Dict] = None):
        super().__init__(ErrorCode.INVALID_REQUEST, message, 400, data)


class NotFoundException(AppException):
    """Resource not found"""
    def __init__(self, message: str = "资源不存在"):
        super().__init__(ErrorCode.NOT_FOUND, message, 404)


class UnauthorizedException(AppException):
    """Unauthorized"""
    def __init__(self, message: str = "未认证"):
        super().__init__(ErrorCode.UNAUTHORIZED, message, 401)


class ForbiddenException(AppException):
    """Forbidden"""
    def __init__(self, message: str = "无权限"):
        super().__init__(ErrorCode.FORBIDDEN, message, 403)


class ConflictException(AppException):
    """Conflict (duplicate, etc.)"""
    def __init__(self, message: str, data: Optional[Dict] = None):
        super().__init__(ErrorCode.DUPLICATE_ORDER, message, 409, data)


# Exception handlers
async def app_exception_handler(request: Request, exc: AppException):
    return JSONResponse(
        status_code=exc.status_code,
        content={
            "code": exc.code,
            "message": exc.message,
            "data": exc.data
        }
    )


async def general_exception_handler(request: Request, exc: Exception):
    return JSONResponse(
        status_code=500,
        content={
            "code": ErrorCode.INTERNAL_ERROR,
            "message": "服务器内部错误",
            "data": None
        }
    )
