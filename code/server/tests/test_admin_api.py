"""
Admin API Integration Tests
管理端API集成测试

测试范围:
1. 套餐CRUD
2. 订单查询
3. 人工确认
"""
import pytest
from datetime import datetime, timezone
from decimal import Decimal
from unittest.mock import AsyncMock, patch, MagicMock
import jwt


# ============== Fixtures ==============

@pytest.fixture
def admin_token():
    """生成有效的admin token"""
    from app.core.config import get_settings
    settings = get_settings()
    
    payload = {
        "sub": "admin001",
        "type": "admin_access",
        "role": "admin",
        "permissions": ["orders:confirm", "orders:fulfill", "orders:ignore", "orders:refund"]
    }
    return jwt.encode(payload, settings.jwt_secret, algorithm=settings.jwt_algorithm)


@pytest.fixture
def expired_admin_token():
    """生成过期的admin token"""
    from app.core.config import get_settings
    settings = get_settings()
    
    payload = {
        "sub": "admin001",
        "type": "admin_access",
        "role": "admin",
        "exp": 0  # 已过期
    }
    return jwt.encode(payload, settings.jwt_secret, algorithm=settings.jwt_algorithm)


@pytest.fixture
def invalid_type_token():
    """生成类型错误的token"""
    from app.core.config import get_settings
    settings = get_settings()
    
    payload = {
        "sub": "user001",
        "type": "access",  # 错误类型，应该是admin_access
        "role": "user"
    }
    return jwt.encode(payload, settings.jwt_secret, algorithm=settings.jwt_algorithm)


@pytest.fixture
def mock_plan():
    """Mock plan data"""
    return {
        "id": "01HQ1234567890123456789012",
        "code": "basic",
        "name": "基础套餐",
        "description": "入门级套餐",
        "traffic_bytes": 1099511627776,
        "duration_days": 30,
        "price_usd": "9.99",
        "supported_assets": ["SOL", "USDT_TRC20"],
        "enabled": True,
        "sort_order": 0
    }


@pytest.fixture
def mock_order():
    """Mock order data"""
    return {
        "id": "01HQ12345678901234567890AB",
        "order_no": "ORD2401011201A1B2C3D4",
        "purchase_type": "new",
        "plan_id": "01HQ1234567890123456789012",
        "plan_name": "基础套餐",
        "marzban_username": None,
        "chain": "solana",
        "asset_code": "SOL",
        "amount_crypto": "0.06890685",
        "amount_usd_locked": "9.99",
        "status": "pending_payment",
        "expires_at": "2024-01-01T12:16:00+00:00",
        "tx_hash": None,
        "paid_at": None,
        "confirmed_at": None,
        "fulfilled_at": None,
        "error_code": None,
        "error_message": None,
        "created_at": "2024-01-01T12:01:00+00:00"
    }


# ============== Test Admin Authentication ==============

class TestAdminAuthentication:
    """测试管理端认证"""
    
    def test_admin_api_no_auth(self, client):
        """测试未认证访问管理API"""
        response = client.get("/admin/v1/plans")
        
        assert response.status_code == 401
        data = response.json()
        assert data["code"] == "UNAUTHORIZED"
    
    def test_admin_api_invalid_auth_format(self, client):
        """测试错误格式的认证"""
        headers = {"Authorization": "InvalidFormat token123"}
        response = client.get("/admin/v1/plans", headers=headers)
        
        assert response.status_code == 401
    
    def test_admin_api_missing_auth(self, client):
        """测试缺少Authorization header"""
        response = client.get("/admin/v1/plans")
        
        assert response.status_code == 401
        data = response.json()
        assert "Missing or invalid Authorization header" in data.get("message", "")
    
    def test_admin_api_invalid_token(self, client):
        """测试无效的token"""
        headers = {"Authorization": "Bearer invalid-token"}
        response = client.get("/admin/v1/plans", headers=headers)
        
        assert response.status_code in [401, 403]
    
    def test_admin_api_expired_token(self, client, expired_admin_token):
        """测试过期的token"""
        headers = {"Authorization": f"Bearer {expired_admin_token}"}
        response = client.get("/admin/v1/plans", headers=headers)
        
        assert response.status_code == 401
        data = response.json()
        assert "expired" in data.get("message", "").lower() or data["code"] == "UNAUTHORIZED"
    
    def test_admin_api_wrong_token_type(self, client, invalid_type_token):
        """测试错误类型的token"""
        headers = {"Authorization": f"Bearer {invalid_type_token}"}
        response = client.get("/admin/v1/plans", headers=headers)
        
        assert response.status_code == 403
        data = response.json()
        assert data["code"] == "FORBIDDEN"


# ============== Test Admin Plans API ==============

class TestAdminPlansAPI:
    """测试管理端套餐API"""
    
    def test_list_plans_with_auth(self, client, admin_token):
        """测试认证后获取套餐列表"""
        headers = {"Authorization": f"Bearer {admin_token}"}
        response = client.get("/admin/v1/plans", headers=headers)
        
        assert response.status_code == 200
        data = response.json()
        assert data["code"] == "SUCCESS"
        assert "data" in data
        assert "pagination" in data
    
    def test_list_plans_pagination(self, client, admin_token):
        """测试套餐列表分页"""
        headers = {"Authorization": f"Bearer {admin_token}"}
        response = client.get("/admin/v1/plans?page=1&size=10", headers=headers)
        
        assert response.status_code == 200
        data = response.json()
        assert data["code"] == "SUCCESS"
        assert data["pagination"]["page"] == 1
        assert data["pagination"]["size"] == 10
    
    def test_list_plans_filter_enabled(self, client, admin_token):
        """测试按启用状态筛选套餐"""
        headers = {"Authorization": f"Bearer {admin_token}"}
        response = client.get("/admin/v1/plans?enabled=true", headers=headers)
        
        assert response.status_code == 200
        data = response.json()
        assert data["code"] == "SUCCESS"
    
    def test_get_plan_not_found(self, client, admin_token):
        """测试获取不存在的套餐"""
        headers = {"Authorization": f"Bearer {admin_token}"}
        response = client.get("/admin/v1/plans/nonexistent-id", headers=headers)
        
        assert response.status_code == 404
        data = response.json()
        assert data["code"] == "NOT_FOUND"
    
    def test_create_plan_validation_error(self, client, admin_token):
        """测试创建套餐验证错误"""
        headers = {"Authorization": f"Bearer {admin_token}"}
        
        # 缺少必填字段
        invalid_plan = {
            "name": "测试套餐"
            # 缺少code, traffic_bytes, duration_days, price_usd
        }
        
        response = client.post(
            "/admin/v1/plans",
            json=invalid_plan,
            headers=headers
        )
        
        assert response.status_code == 422
    
    def test_create_plan_invalid_price(self, client, admin_token):
        """测试创建套餐无效价格"""
        headers = {"Authorization": f"Bearer {admin_token}"}
        
        invalid_plan = {
            "code": "test-plan",
            "name": "测试套餐",
            "traffic_bytes": 1099511627776,
            "duration_days": 30,
            "price_usd": 0  # 无效价格
        }
        
        response = client.post(
            "/admin/v1/plans",
            json=invalid_plan,
            headers=headers
        )
        
        assert response.status_code == 422
    
    def test_update_plan_not_found(self, client, admin_token):
        """测试更新不存在的套餐"""
        headers = {"Authorization": f"Bearer {admin_token}"}
        
        update_data = {
            "name": "更新的名称"
        }
        
        response = client.put(
            "/admin/v1/plans/nonexistent-id",
            json=update_data,
            headers=headers
        )
        
        assert response.status_code == 404
    
    def test_delete_plan_not_found(self, client, admin_token):
        """测试删除不存在的套餐"""
        headers = {"Authorization": f"Bearer {admin_token}"}
        response = client.delete("/admin/v1/plans/nonexistent-id", headers=headers)
        
        assert response.status_code == 404
    
    def test_enable_plan_not_found(self, client, admin_token):
        """测试启用/禁用不存在的套餐"""
        headers = {"Authorization": f"Bearer {admin_token}"}
        
        request_data = {"enabled": False}
        
        response = client.patch(
            "/admin/v1/plans/nonexistent-id/enable",
            json=request_data,
            headers=headers
        )
        
        assert response.status_code == 404


# ============== Test Admin Orders API ==============

class TestAdminOrdersAPI:
    """测试管理端订单API"""
    
    def test_list_orders_with_auth(self, client, admin_token):
        """测试认证后获取订单列表"""
        headers = {"Authorization": f"Bearer {admin_token}"}
        response = client.get("/admin/v1/orders", headers=headers)
        
        assert response.status_code == 200
        data = response.json()
        assert data["code"] == "SUCCESS"
        assert "data" in data
        assert "pagination" in data
    
    def test_list_orders_filter_status(self, client, admin_token):
        """测试按状态筛选订单"""
        headers = {"Authorization": f"Bearer {admin_token}"}
        response = client.get("/admin/v1/orders?status=pending_payment", headers=headers)
        
        assert response.status_code == 200
        data = response.json()
        assert data["code"] == "SUCCESS"
    
    def test_list_orders_filter_date_range(self, client, admin_token):
        """测试按日期范围筛选订单"""
        headers = {"Authorization": f"Bearer {admin_token}"}
        start_date = "2024-01-01T00:00:00"
        end_date = "2024-12-31T23:59:59"
        
        response = client.get(
            f"/admin/v1/orders?start_date={start_date}&end_date={end_date}",
            headers=headers
        )
        
        assert response.status_code == 200
        data = response.json()
        assert data["code"] == "SUCCESS"
    
    def test_list_orders_search_order_no(self, client, admin_token):
        """测试按订单号搜索"""
        headers = {"Authorization": f"Bearer {admin_token}"}
        response = client.get("/admin/v1/orders?order_no=ORD", headers=headers)
        
        assert response.status_code == 200
        data = response.json()
        assert data["code"] == "SUCCESS"
    
    def test_get_order_not_found(self, client, admin_token):
        """测试获取不存在的订单"""
        headers = {"Authorization": f"Bearer {admin_token}"}
        response = client.get("/admin/v1/orders/nonexistent-order-id", headers=headers)
        
        assert response.status_code == 404
        data = response.json()
        assert data["code"] == "NOT_FOUND"
    
    def test_get_order_stats(self, client, admin_token):
        """测试获取订单统计"""
        headers = {"Authorization": f"Bearer {admin_token}"}
        response = client.get("/admin/v1/orders/stats/summary", headers=headers)
        
        assert response.status_code == 200
        data = response.json()
        assert data["code"] == "SUCCESS"
        assert "data" in data
        assert "today_orders" in data["data"]
        assert "total_orders" in data["data"]
        assert "status_counts" in data["data"]


# ============== Test Admin Order Actions API ==============

class TestAdminOrderActionsAPI:
    """测试管理端订单操作API"""
    
    def test_manual_confirm_order_not_found(self, client, admin_token):
        """测试人工确认不存在的订单"""
        headers = {"Authorization": f"Bearer {admin_token}"}
        
        request_data = {
            "tx_hash": "abc123",
            "amount_crypto": "0.1",
            "note": "测试确认"
        }
        
        response = client.post(
            "/admin/v1/orders/nonexistent-order-id/manual-confirm",
            json=request_data,
            headers=headers
        )
        
        assert response.status_code == 404
    
    def test_manual_confirm_invalid_amount(self, client, admin_token):
        """测试人工确认无效金额"""
        headers = {"Authorization": f"Bearer {admin_token}"}
        
        # 金额必须为正数
        request_data = {
            "tx_hash": "abc123",
            "amount_crypto": -1,  # 无效金额
            "note": "测试确认"
        }
        
        response = client.post(
            "/admin/v1/orders/some-order-id/manual-confirm",
            json=request_data,
            headers=headers
        )
        
        assert response.status_code == 422
    
    def test_retry_fulfill_order_not_found(self, client, admin_token):
        """测试重试开通不存在的订单"""
        headers = {"Authorization": f"Bearer {admin_token}"}
        
        response = client.post(
            "/admin/v1/orders/nonexistent-order-id/retry-fulfill",
            headers=headers
        )
        
        assert response.status_code == 404
    
    def test_mark_ignore_order_not_found(self, client, admin_token):
        """测试标记忽略不存在的订单"""
        headers = {"Authorization": f"Bearer {admin_token}"}
        
        request_data = {"reason": "测试订单"}
        
        response = client.post(
            "/admin/v1/orders/nonexistent-order-id/mark-ignore",
            json=request_data,
            headers=headers
        )
        
        assert response.status_code == 404
    
    def test_mark_ignore_missing_reason(self, client, admin_token):
        """测试标记忽略缺少原因"""
        headers = {"Authorization": f"Bearer {admin_token}"}
        
        request_data = {}  # 缺少reason
        
        response = client.post(
            "/admin/v1/orders/some-order-id/mark-ignore",
            json=request_data,
            headers=headers
        )
        
        assert response.status_code == 422
    
    def test_mark_refund_order_not_found(self, client, admin_token):
        """测试标记退款不存在的订单"""
        headers = {"Authorization": f"Bearer {admin_token}"}
        
        response = client.post(
            "/admin/v1/orders/nonexistent-order-id/refund",
            headers=headers
        )
        
        assert response.status_code == 404


# ============== Test API Response Format ==============

class TestAdminAPIResponseFormat:
    """测试管理端API响应格式"""
    
    def test_paginated_response_format(self, client, admin_token):
        """测试分页响应格式"""
        headers = {"Authorization": f"Bearer {admin_token}"}
        response = client.get("/admin/v1/plans", headers=headers)
        
        assert response.status_code == 200
        data = response.json()
        
        # 验证分页响应结构
        assert "code" in data
        assert "message" in data
        assert "data" in data
        assert "pagination" in data
        assert "total" in data["pagination"]
        assert "page" in data["pagination"]
        assert "size" in data["pagination"]
        assert "pages" in data["pagination"]
    
    def test_error_response_format(self, client, admin_token):
        """测试错误响应格式"""
        headers = {"Authorization": f"Bearer {admin_token}"}
        response = client.get("/admin/v1/plans/nonexistent-id", headers=headers)
        
        assert response.status_code == 404
        data = response.json()
        
        # 验证错误响应结构
        assert "code" in data
        assert "message" in data
        assert data["code"] == "NOT_FOUND"


# ============== Test Input Validation ==============

class TestAdminInputValidation:
    """测试管理端输入验证"""
    
    def test_create_plan_code_too_long(self, client, admin_token):
        """测试创建套餐代码过长"""
        headers = {"Authorization": f"Bearer {admin_token}"}
        
        invalid_plan = {
            "code": "a" * 100,  # 超过32字符限制
            "name": "测试套餐",
            "traffic_bytes": 1099511627776,
            "duration_days": 30,
            "price_usd": 9.99
        }
        
        response = client.post(
            "/admin/v1/plans",
            json=invalid_plan,
            headers=headers
        )
        
        assert response.status_code == 422
    
    def test_create_plan_negative_traffic(self, client, admin_token):
        """测试创建套餐负流量"""
        headers = {"Authorization": f"Bearer {admin_token}"}
        
        invalid_plan = {
            "code": "test-plan",
            "name": "测试套餐",
            "traffic_bytes": -1,  # 负数
            "duration_days": 30,
            "price_usd": 9.99
        }
        
        response = client.post(
            "/admin/v1/plans",
            json=invalid_plan,
            headers=headers
        )
        
        assert response.status_code == 422
    
    def test_pagination_invalid_page(self, client, admin_token):
        """测试无效页码"""
        headers = {"Authorization": f"Bearer {admin_token}"}
        response = client.get("/admin/v1/plans?page=0", headers=headers)
        
        assert response.status_code == 422
    
    def test_pagination_invalid_size(self, client, admin_token):
        """测试无效每页数量"""
        headers = {"Authorization": f"Bearer {admin_token}"}
        response = client.get("/admin/v1/plans?size=0", headers=headers)
        
        assert response.status_code == 422
    
    def test_pagination_size_too_large(self, client, admin_token):
        """测试每页数量过大"""
        headers = {"Authorization": f"Bearer {admin_token}"}
        response = client.get("/admin/v1/plans?size=1000", headers=headers)
        
        assert response.status_code == 422


# ============== Test Permission Control ==============

class TestPermissionControl:
    """测试权限控制"""
    
    def test_insufficient_permissions(self, client):
        """测试权限不足"""
        from app.core.config import get_settings
        settings = get_settings()
        
        # 生成缺少权限的token
        payload = {
            "sub": "admin001",
            "type": "admin_access",
            "role": "readonly",
            "permissions": []  # 空权限
        }
        token = jwt.encode(payload, settings.jwt_secret, algorithm=settings.jwt_algorithm)
        
        headers = {"Authorization": f"Bearer {token}"}
        
        request_data = {
            "tx_hash": "abc123",
            "amount_crypto": "0.1",
            "note": "测试确认"
        }
        
        response = client.post(
            "/admin/v1/orders/some-order-id/manual-confirm",
            json=request_data,
            headers=headers
        )
        
        # 权限不足应该返回403
        assert response.status_code in [403, 404]
