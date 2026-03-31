"""
Rate limiting utilities for FastAPI endpoints
基于内存的简单速率限制实现（适合单实例部署）
"""
import time
from dataclasses import dataclass, field
from typing import Dict, Optional, Callable
from functools import wraps

from fastapi import Request, HTTPException, Depends


@dataclass
class RateLimitEntry:
    """Rate limit tracking entry"""
    requests: int = 0
    window_start: float = field(default_factory=time.time)


class RateLimiter:
    """
    In-memory rate limiter using sliding window algorithm
    
    Note: This is suitable for single-instance deployments.
    For multi-instance deployments, use Redis-backed rate limiting.
    """
    
    def __init__(self, requests: int, window_seconds: int):
        """
        Initialize rate limiter
        
        Args:
            requests: Maximum number of requests allowed in the window
            window_seconds: Time window in seconds
        """
        self.requests = requests
        self.window_seconds = window_seconds
        self._storage: Dict[str, RateLimitEntry] = {}
    
    def _get_key(self, identifier: str) -> str:
        """Generate storage key for identifier"""
        return f"rate_limit:{identifier}"
    
    def _cleanup_expired(self, now: float) -> None:
        """Remove expired entries from storage"""
        expired_keys = [
            key for key, entry in self._storage.items()
            if now - entry.window_start > self.window_seconds
        ]
        for key in expired_keys:
            del self._storage[key]
    
    def is_allowed(self, identifier: str) -> tuple[bool, Optional[int]]:
        """
        Check if request is allowed
        
        Args:
            identifier: Unique identifier for the client (e.g., IP + endpoint)
            
        Returns:
            tuple: (is_allowed, retry_after_seconds)
        """
        now = time.time()
        key = self._get_key(identifier)
        
        # Cleanup expired entries occasionally (1% chance)
        if hash(key) % 100 == 0:
            self._cleanup_expired(now)
        
        entry = self._storage.get(key)
        
        # No entry or window expired - start new window
        if not entry or now - entry.window_start > self.window_seconds:
            self._storage[key] = RateLimitEntry(requests=1, window_start=now)
            return True, None
        
        # Check if within limit
        if entry.requests < self.requests:
            entry.requests += 1
            return True, None
        
        # Rate limit exceeded
        retry_after = int(self.window_seconds - (now - entry.window_start)) + 1
        return False, retry_after
    
    def reset(self, identifier: str) -> None:
        """Reset rate limit for an identifier"""
        key = self._get_key(identifier)
        self._storage.pop(key, None)


# Global rate limiter instances for different use cases
_admin_limiter = RateLimiter(requests=30, window_seconds=60)  # 30 requests/minute for admin
_strict_limiter = RateLimiter(requests=10, window_seconds=60)  # 10 requests/minute for sensitive ops
_public_limiter = RateLimiter(requests=100, window_seconds=60)  # 100 requests/minute for public APIs


def get_client_identifier(request: Request, suffix: str = "") -> str:
    """
    Generate unique identifier for rate limiting
    
    Uses X-Forwarded-For header if available (for behind-proxy deployments),
    otherwise uses client host.
    """
    forwarded_for = request.headers.get("x-forwarded-for")
    if forwarded_for:
        client_ip = forwarded_for.split(",")[0].strip()
    else:
        client_ip = request.client.host if request.client else "unknown"
    
    # Include path suffix to separate limits for different endpoints
    return f"{client_ip}:{suffix}" if suffix else client_ip


def rate_limit(
    requests: int = 30,
    window_seconds: int = 60,
    identifier_func: Optional[Callable[[Request], str]] = None
):
    """
    Rate limiting dependency factory for FastAPI
    
    Args:
        requests: Maximum number of requests allowed in the window
        window_seconds: Time window in seconds
        identifier_func: Optional function to generate client identifier
        
    Usage:
        @router.get("/endpoint")
        async def endpoint(
            rate_limit: None = Depends(rate_limit(requests=10, window_seconds=60))
        ):
            return {"message": "success"}
    """
    limiter = RateLimiter(requests=requests, window_seconds=window_seconds)
    
    async def dependency(request: Request) -> None:
        if identifier_func:
            identifier = identifier_func(request)
        else:
            identifier = get_client_identifier(request, request.url.path)
        
        allowed, retry_after = limiter.is_allowed(identifier)
        
        if not allowed:
            raise HTTPException(
                status_code=429,
                detail=f"Rate limit exceeded. Try again in {retry_after} seconds.",
                headers={"Retry-After": str(retry_after)}
            )
    
    return dependency


# Pre-configured rate limiters for common use cases

async def admin_rate_limit(request: Request) -> None:
    """
    Standard rate limit for admin endpoints: 30 requests per minute
    """
    identifier = get_client_identifier(request, f"admin:{request.url.path}")
    allowed, retry_after = _admin_limiter.is_allowed(identifier)
    
    if not allowed:
        raise HTTPException(
            status_code=429,
            detail=f"Rate limit exceeded. Try again in {retry_after} seconds.",
            headers={"Retry-After": str(retry_after)}
        )


async def strict_rate_limit(request: Request) -> None:
    """
    Strict rate limit for sensitive operations: 10 requests per minute
    Used for manual confirm, refund, and other critical admin actions.
    """
    identifier = get_client_identifier(request, f"strict:{request.url.path}")
    allowed, retry_after = _strict_limiter.is_allowed(identifier)
    
    if not allowed:
        raise HTTPException(
            status_code=429,
            detail=f"Rate limit exceeded. Try again in {retry_after} seconds.",
            headers={"Retry-After": str(retry_after)}
        )


async def public_rate_limit(request: Request) -> None:
    """
    Standard rate limit for public endpoints: 100 requests per minute
    """
    identifier = get_client_identifier(request, f"public:{request.url.path}")
    allowed, retry_after = _public_limiter.is_allowed(identifier)
    
    if not allowed:
        raise HTTPException(
            status_code=429,
            detail=f"Rate limit exceeded. Try again in {retry_after} seconds.",
            headers={"Retry-After": str(retry_after)}
        )


# Client rate limiter: 60 requests per minute for authenticated client endpoints
_client_limiter = RateLimiter(requests=60, window_seconds=60)


async def client_rate_limit(request: Request) -> None:
    """
    Rate limit for client authenticated endpoints: 60 requests per minute
    Used for subscription retrieval and other client APIs.
    """
    identifier = get_client_identifier(request, f"client:{request.url.path}")
    allowed, retry_after = _client_limiter.is_allowed(identifier)
    
    if not allowed:
        raise HTTPException(
            status_code=429,
            detail=f"Rate limit exceeded. Try again in {retry_after} seconds.",
            headers={"Retry-After": str(retry_after)}
        )
