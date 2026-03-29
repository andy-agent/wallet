"""
Base response schemas
"""
from typing import Any, Generic, Optional, TypeVar

from pydantic import BaseModel, Field

T = TypeVar("T")


class Response(BaseModel, Generic[T]):
    """Standard API response"""
    code: str = Field(default="SUCCESS")
    message: str = Field(default="success")
    data: Optional[T] = None


class Pagination(BaseModel):
    """Pagination info"""
    total: int
    page: int
    size: int
    pages: int = 0


class PaginatedResponse(Response, Generic[T]):
    """Paginated response"""
    pagination: Pagination
