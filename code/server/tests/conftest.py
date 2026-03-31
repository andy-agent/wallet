"""
Pytest configuration
"""
import os
import sys
import types

import pytest
from fastapi.testclient import TestClient

# Minimal env bootstrap for settings import in tests.
os.environ.setdefault("DATABASE_URL", "postgresql+asyncpg://user:pass@localhost:5432/testdb")
os.environ.setdefault("JWT_SECRET", "test-secret")
os.environ.setdefault("ENCRYPTION_MASTER_KEY", "test-master-key")
os.environ.setdefault("MARZBAN_BASE_URL", "http://localhost")
os.environ.setdefault("MARZBAN_ADMIN_USERNAME", "admin")
os.environ.setdefault("MARZBAN_ADMIN_PASSWORD", "password")

# Provide a tiny jwt stub when PyJWT is unavailable in the test environment.
if "jwt" not in sys.modules:
    import jwt as real_jwt
    sys.modules["jwt"] = real_jwt

from app.main import app


@pytest.fixture
def client():
    """Test client"""
    return TestClient(app)
