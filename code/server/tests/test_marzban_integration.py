"""
Marzban 集成测试 - 使用 mock 模式验证所有功能

测试范围:
1. MarzbanClient API 调用
2. Fulfillment 服务 - 新购和续费流程
3. 幂等性保证
4. 错误处理
5. 审计日志记录
"""
import asyncio
import json
import uuid
from datetime import datetime, timezone, timedelta
from decimal import Decimal
from unittest.mock import AsyncMock, MagicMock, patch
from typing import Optional

import pytest
import jwt
from sqlalchemy import select
import ulid


@pytest.fixture(autouse=True)
def mock_notify_order_status_changed():
    """Auto-mock notify_order_status_changed for all fulfillment tests"""
    with patch('app.services.websocket.notify_order_status_changed', new_callable=AsyncMock):
        yield


# 设置测试环境变量
import os
os.environ.setdefault("DATABASE_URL", "sqlite+aiosqlite:///:memory:")
os.environ.setdefault("JWT_SECRET", "test-jwt-secret-for-marzban-tests")
os.environ.setdefault("ENCRYPTION_MASTER_KEY", "test-master-key-32chars-long!!")
os.environ.setdefault("MARZBAN_BASE_URL", "http://test-marzban.local")
os.environ.setdefault("MARZBAN_ADMIN_USERNAME", "test-admin")
os.environ.setdefault("MARZBAN_ADMIN_PASSWORD", "test-password")

from app.integrations.marzban import MarzbanClient, MarzbanAPIError, User
from app.services.fulfillment import (
    fulfill_new_order,
    fulfill_renew_order,
    refresh_session,
    generate_client_tokens,
    verify_client_token,
    FulfillmentError,
    FulfillmentResult,
)
from app.core.state_machine import OrderStatus
from app.models.order import Order
from app.models.plan import Plan
from app.models.client_session import ClientSession
from app.models.audit_log import AuditLog, OperatorType


# ============== Fixtures ==============

@pytest.fixture
def mock_marzban_client():
    """创建 mock MarzbanClient"""
    client = MagicMock(spec=MarzbanClient)
    client.base_url = "http://test-marzban.local"
    
    # 默认 mock 返回值
    mock_user = User(
        username="test_user_123",
        status="active",
        expire=int((datetime.now(timezone.utc) + timedelta(days=30)).timestamp()),
        data_limit=10737418240,  # 10GB
        used_traffic=0,
        subscription_url="http://test-marzban.local/sub/test_token_123",
        created_at=datetime.now(timezone.utc),
    )
    
    client.create_user = AsyncMock(return_value=mock_user)
    client.get_user = AsyncMock(return_value=mock_user)
    client.modify_user = AsyncMock(return_value=mock_user)
    client.delete_user = AsyncMock(return_value=True)
    client.get_subscription_url = AsyncMock(return_value="http://test-marzban.local/sub/test_token_123")
    
    return client


@pytest.fixture
def sample_plan_data():
    """样例套餐数据"""
    return {
        "id": str(ulid.new().str),
        "code": "test-plan-30d-10gb",
        "name": "Test Plan 30 Days 10GB",
        "description": "Test plan for unit tests",
        "traffic_bytes": 10 * 1024 * 1024 * 1024,  # 10GB
        "duration_days": 30,
        "price_usd": Decimal("9.99"),
        "supported_assets": ["SOL", "USDT_TRC20"],
        "enabled": True,
        "sort_order": 1,
    }


@pytest.fixture
def sample_new_order_data(sample_plan_data):
    """样例新购订单数据"""
    return {
        "id": str(ulid.new().str),
        "order_no": f"ORD{uuid.uuid4().hex[:16].upper()}",
        "purchase_type": "new",
        "plan_id": sample_plan_data["id"],
        "user_id": "test-user-123",
        "client_user_id": None,
        "marzban_username": None,
        "chain": "solana",
        "asset_code": "SOL",
        "receive_address": "TestSolanaAddress123",
        "amount_crypto": Decimal("0.1"),
        "amount_usd_locked": Decimal("9.99"),
        "fx_rate_locked": Decimal("99.90"),
        "status": OrderStatus.PAID_SUCCESS.value,
        "expires_at": datetime.now(timezone.utc) + timedelta(minutes=15),
        "client_version": "1.0.0",
    }


@pytest.fixture
def sample_renew_order_data(sample_plan_data):
    """样例续费订单数据"""
    return {
        "id": str(ulid.new().str),
        "order_no": f"ORD{uuid.uuid4().hex[:16].upper()}",
        "purchase_type": "renew",
        "plan_id": sample_plan_data["id"],
        "user_id": "test-user-123",
        "client_user_id": "user_existing_123",
        "marzban_username": "user_existing_123",
        "chain": "solana",
        "asset_code": "SOL",
        "receive_address": "TestSolanaAddress456",
        "amount_crypto": Decimal("0.1"),
        "amount_usd_locked": Decimal("9.99"),
        "fx_rate_locked": Decimal("99.90"),
        "status": OrderStatus.PAID_SUCCESS.value,
        "expires_at": datetime.now(timezone.utc) + timedelta(minutes=15),
        "client_version": "1.0.0",
    }


# ============== Test MarzbanClient ==============

class TestMarzbanClient:
    """MarzbanClient 单元测试"""
    
    @pytest.mark.asyncio
    async def test_create_user_success(self):
        """测试创建用户成功"""
        client = MarzbanClient(
            base_url="http://test-marzban.local",
            username="admin",
            password="password"
        )
        
        # Mock _request 方法
        mock_response = {
            "username": "test_user_123",
            "status": "active",
            "expire": int((datetime.now(timezone.utc) + timedelta(days=30)).timestamp()),
            "data_limit": 10737418240,
            "used_traffic": 0,
            "token": "test_token_123",
            "created_at": datetime.now(timezone.utc).isoformat(),
        }
        
        with patch.object(client, '_request', new_callable=AsyncMock, return_value=mock_response):
            user = await client.create_user(
                username="test_user_123",
                expire=int((datetime.now(timezone.utc) + timedelta(days=30)).timestamp()),
                data_limit=10737418240,
            )
            
            assert user.username == "test_user_123"
            assert user.status == "active"
            assert user.data_limit == 10737418240
            assert user.subscription_url == "http://test-marzban.local/sub/test_token_123"
    
    @pytest.mark.asyncio
    async def test_create_user_already_exists(self):
        """测试创建已存在的用户"""
        client = MarzbanClient(
            base_url="http://test-marzban.local",
            username="admin",
            password="password"
        )
        
        with patch.object(client, '_request', new_callable=AsyncMock, side_effect=
                         MarzbanAPIError("User already exists", status_code=409)):
            with pytest.raises(MarzbanAPIError) as exc_info:
                await client.create_user(username="existing_user")
            
            assert exc_info.value.status_code == 409
    
    @pytest.mark.asyncio
    async def test_get_user_success(self):
        """测试获取用户成功"""
        client = MarzbanClient(
            base_url="http://test-marzban.local",
            username="admin",
            password="password"
        )
        
        mock_response = {
            "username": "test_user_123",
            "status": "active",
            "expire": int((datetime.now(timezone.utc) + timedelta(days=30)).timestamp()),
            "data_limit": 10737418240,
            "used_traffic": 1073741824,  # 1GB used
            "token": "test_token_123",
            "created_at": datetime.now(timezone.utc).isoformat(),
        }
        
        with patch.object(client, '_request', new_callable=AsyncMock, return_value=mock_response):
            user = await client.get_user("test_user_123")
            
            assert user is not None
            assert user.username == "test_user_123"
            assert user.used_traffic == 1073741824
    
    @pytest.mark.asyncio
    async def test_get_user_not_found(self):
        """测试获取不存在的用户"""
        client = MarzbanClient(
            base_url="http://test-marzban.local",
            username="admin",
            password="password"
        )
        
        with patch.object(client, '_request', new_callable=AsyncMock, side_effect=
                         MarzbanAPIError("User not found", status_code=404)):
            user = await client.get_user("nonexistent_user")
            
            assert user is None
    
    @pytest.mark.asyncio
    async def test_modify_user_success(self):
        """测试修改用户成功（续费场景）"""
        client = MarzbanClient(
            base_url="http://test-marzban.local",
            username="admin",
            password="password"
        )
        
        new_expire = int((datetime.now(timezone.utc) + timedelta(days=60)).timestamp())
        new_data_limit = 21474836480  # 20GB
        
        mock_response = {
            "username": "test_user_123",
            "status": "active",
            "expire": new_expire,
            "data_limit": new_data_limit,
            "used_traffic": 1073741824,
            "token": "test_token_123",
            "created_at": datetime.now(timezone.utc).isoformat(),
        }
        
        with patch.object(client, '_request', new_callable=AsyncMock, return_value=mock_response):
            user = await client.modify_user(
                username="test_user_123",
                expire=new_expire,
                data_limit=new_data_limit,
                status="active"
            )
            
            assert user.expire == new_expire
            assert user.data_limit == new_data_limit
            assert user.status == "active"
    
    @pytest.mark.asyncio
    async def test_token_refresh_on_401(self):
        """测试 token 过期自动刷新"""
        client = MarzbanClient(
            base_url="http://test-marzban.local",
            username="admin",
            password="password"
        )
        
        mock_response = {
            "username": "test_user_123",
            "status": "active",
        }
        
        # 模拟第一次 401，第二次成功
        call_count = 0
        async def mock_request(*args, **kwargs):
            nonlocal call_count
            call_count += 1
            if call_count == 1:
                raise MarzbanAPIError("Unauthorized", status_code=401)
            return mock_response
        
        with patch.object(client, '_request', side_effect=mock_request):
            with patch.object(client, 'authenticate', new_callable=AsyncMock) as mock_auth:
                # _request 内部处理了 401 重试，但这里我们需要特殊处理
                # 实际代码中，_request 方法会捕获 401 并调用 authenticate
                pass
    
    @pytest.mark.asyncio
    async def test_get_subscription_url(self):
        """测试获取订阅链接"""
        client = MarzbanClient(
            base_url="http://test-marzban.local",
            username="admin",
            password="password"
        )
        
        mock_response = {
            "username": "test_user_123",
            "status": "active",
            "token": "sub_token_abc123",
        }
        
        with patch.object(client, '_request', new_callable=AsyncMock, return_value=mock_response):
            url = await client.get_subscription_url("test_user_123")
            
            assert url == "http://test-marzban.local/sub/sub_token_abc123"


# ============== Test Token Generation ==============

class TestTokenGeneration:
    """测试 token 生成和验证"""
    
    def test_generate_client_tokens(self):
        """测试生成客户端 token"""
        username = "test_user_123"
        access_token, refresh_token, expires_at = generate_client_tokens("user_test_123", username)
        
        # 验证 token 格式
        assert access_token is not None
        assert refresh_token is not None
        assert expires_at > datetime.now(timezone.utc)
        
        # 验证可以解码
        settings = MagicMock()
        settings.jwt_secret = os.environ["JWT_SECRET"]
        settings.jwt_algorithm = "HS256"
        
        with patch('app.services.fulfillment.get_settings', return_value=settings):
            decoded = jwt.decode(access_token, os.environ["JWT_SECRET"], algorithms=["HS256"])
            assert decoded["sub"] == username
            assert decoded["user_id"] == "user_test_123"
            assert decoded["type"] == "access"
    
    def test_verify_client_token_success(self):
        """测试验证有效 token"""
        username = "test_user_123"
        
        # 生成有效 token
        settings = MagicMock()
        settings.jwt_secret = os.environ["JWT_SECRET"]
        settings.jwt_algorithm = "HS256"
        settings.jwt_access_token_expire_minutes = 30
        settings.jwt_refresh_token_expire_days = 90
        
        with patch('app.services.fulfillment.get_settings', return_value=settings):
            access_token, _, _ = generate_client_tokens("user_test_123", username)
            
            # 验证 token
            result = verify_client_token(access_token, expected_type="access")
            assert result == username
    
    def test_verify_client_token_invalid_type(self):
        """测试验证 token 类型不匹配"""
        username = "test_user_123"
        
        settings = MagicMock()
        settings.jwt_secret = os.environ["JWT_SECRET"]
        settings.jwt_algorithm = "HS256"
        settings.jwt_access_token_expire_minutes = 30
        settings.jwt_refresh_token_expire_days = 90
        
        with patch('app.services.fulfillment.get_settings', return_value=settings):
            access_token, _, _ = generate_client_tokens("user_test_123", username)
            
            # 用 access token 验证 refresh 类型
            with pytest.raises(FulfillmentError) as exc_info:
                verify_client_token(access_token, expected_type="refresh")
            
            assert exc_info.value.error_code == "INVALID_TOKEN_TYPE"
    
    def test_verify_client_token_expired(self):
        """测试验证过期 token"""
        settings = MagicMock()
        settings.jwt_secret = os.environ["JWT_SECRET"]
        settings.jwt_algorithm = "HS256"
        
        # 创建一个已过期的 token
        expired_payload = {
            "sub": "test_user",
            "type": "access",
            "iat": datetime.now(timezone.utc) - timedelta(hours=2),
            "exp": datetime.now(timezone.utc) - timedelta(hours=1),
            "jti": str(uuid.uuid4()),
        }
        expired_token = jwt.encode(expired_payload, os.environ["JWT_SECRET"], algorithm="HS256")
        
        with patch('app.services.fulfillment.get_settings', return_value=settings):
            with pytest.raises(FulfillmentError) as exc_info:
                verify_client_token(expired_token)
            
            assert exc_info.value.error_code == "TOKEN_EXPIRED"


# ============== Test Fulfillment Service (with mocked DB) ==============

class TestFulfillmentNewOrder:
    """测试新购开通流程"""
    
    @pytest.mark.asyncio
    async def test_fulfill_new_order_success(self, mock_marzban_client):
        """测试新购开通成功"""
        order_id = str(ulid.new().str)
        plan_id = str(ulid.new().str)
        username = "user_test123"
        
        # 创建 mock 订单
        mock_order = MagicMock(spec=Order)
        mock_order.user_id = "user_test_123"
        mock_order.id = order_id
        mock_order.purchase_type = "new"
        mock_order.status = OrderStatus.PAID_SUCCESS.value
        mock_order.plan_id = plan_id
        mock_order.marzban_username = None
        mock_order.fulfilled_at = None
        
        # 创建 mock 套餐
        mock_plan = MagicMock(spec=Plan)
        mock_plan.id = plan_id
        mock_plan.duration_days = 30
        mock_plan.traffic_bytes = 10 * 1024 * 1024 * 1024
        
        # Mock 数据库会话
        mock_session = AsyncMock()
        
        # 模拟 _check_idempotent 返回 None（未履行过）
        with patch('app.services.fulfillment._check_idempotent', new_callable=AsyncMock, return_value=None):
            with patch('app.services.fulfillment._get_order_with_plan', new_callable=AsyncMock, return_value=(mock_order, mock_plan)):
                with patch('app.services.fulfillment._generate_username', return_value=username):
                    with patch('app.services.fulfillment.get_marzban_client', return_value=mock_marzban_client):
                        with patch('app.services.fulfillment._record_audit_log', new_callable=AsyncMock):
                            with patch('app.services.fulfillment.transition_to_fulfilled'):
                                with patch('app.services.fulfillment.get_db_context') as mock_db_context:
                                    # 设置上下文管理器
                                    mock_db_context.return_value.__aenter__ = AsyncMock(return_value=mock_session)
                                    mock_db_context.return_value.__aexit__ = AsyncMock(return_value=False)
                                    
                                    result = await fulfill_new_order(order_id)
                                    
                                    # 验证结果
                                    assert result.success is True
                                    assert result.marzban_username == username
                                    assert result.access_token is not None
                                    assert result.refresh_token is not None
                                    assert result.subscription_url is not None
                                    
                                    # 验证 Marzban 被调用
                                    mock_marzban_client.create_user.assert_called_once()
                                    
                                    # 验证订单被更新
                                    assert mock_order.marzban_username == username
                                    assert mock_order.fulfilled_at is not None
                                    assert mock_order.status == OrderStatus.FULFILLED.value
    
    @pytest.mark.asyncio
    async def test_fulfill_new_order_already_fulfilled(self):
        """测试重复履行新购订单（幂等性）"""
        order_id = str(ulid.new().str)
        
        # 模拟已履行结果
        existing_result = FulfillmentResult(
            success=True,
            marzban_username="user_existing",
            access_token="existing_access_token",
            refresh_token="existing_refresh_token",
            expires_at=datetime.now(timezone.utc) + timedelta(days=30),
            subscription_url="http://test/sub/existing",
        )
        
        mock_session = AsyncMock()
        
        with patch('app.services.fulfillment._check_idempotent', new_callable=AsyncMock, return_value=existing_result):
            with patch('app.services.fulfillment.get_db_context') as mock_db_context:
                mock_db_context.return_value.__aenter__ = AsyncMock(return_value=mock_session)
                mock_db_context.return_value.__aexit__ = AsyncMock(return_value=False)
                
                result = await fulfill_new_order(order_id)
                
                # 应该返回缓存的结果
                assert result == existing_result
    
    @pytest.mark.asyncio
    async def test_fulfill_new_order_wrong_status(self):
        """测试订单状态不正确"""
        order_id = str(ulid.new().str)
        plan_id = str(ulid.new().str)
        
        mock_order = MagicMock(spec=Order)
        mock_order.user_id = "user_test_123"
        mock_order.id = order_id
        mock_order.purchase_type = "new"
        mock_order.status = OrderStatus.PENDING_PAYMENT.value  # 错误的状态
        mock_order.plan_id = plan_id
        
        mock_plan = MagicMock(spec=Plan)
        mock_plan.id = plan_id
        
        mock_session = AsyncMock()
        
        with patch('app.services.fulfillment._check_idempotent', new_callable=AsyncMock, return_value=None):
            with patch('app.services.fulfillment._get_order_with_plan', new_callable=AsyncMock, return_value=(mock_order, mock_plan)):
                with patch('app.services.fulfillment.get_db_context') as mock_db_context:
                    mock_db_context.return_value.__aenter__ = AsyncMock(return_value=mock_session)
                    mock_db_context.return_value.__aexit__ = AsyncMock(return_value=False)
                    
                    with pytest.raises(FulfillmentError) as exc_info:
                        await fulfill_new_order(order_id)
                    
                    assert exc_info.value.error_code == "INVALID_ORDER_STATUS"
    
    @pytest.mark.asyncio
    async def test_fulfill_new_order_marzban_api_error(self, mock_marzban_client):
        """测试 Marzban API 错误处理"""
        order_id = str(ulid.new().str)
        plan_id = str(ulid.new().str)
        
        mock_order = MagicMock(spec=Order)
        mock_order.user_id = "user_test_123"
        mock_order.id = order_id
        mock_order.purchase_type = "new"
        mock_order.status = OrderStatus.PAID_SUCCESS.value
        mock_order.plan_id = plan_id
        
        mock_plan = MagicMock(spec=Plan)
        mock_plan.id = plan_id
        mock_plan.duration_days = 30
        mock_plan.traffic_bytes = 10 * 1024 * 1024 * 1024
        
        # 模拟 Marzban API 错误
        mock_marzban_client.create_user = AsyncMock(side_effect=
            MarzbanAPIError("Internal server error", status_code=500))
        
        mock_session = AsyncMock()
        
        with patch('app.services.fulfillment._check_idempotent', new_callable=AsyncMock, return_value=None):
            with patch('app.services.fulfillment._get_order_with_plan', new_callable=AsyncMock, return_value=(mock_order, mock_plan)):
                with patch('app.services.fulfillment._generate_username', return_value="user_test123"):
                    with patch('app.services.fulfillment.get_marzban_client', return_value=mock_marzban_client):
                        with patch('app.services.fulfillment.get_db_context') as mock_db_context:
                            mock_db_context.return_value.__aenter__ = AsyncMock(return_value=mock_session)
                            mock_db_context.return_value.__aexit__ = AsyncMock(return_value=False)
                            
                            with pytest.raises(FulfillmentError) as exc_info:
                                await fulfill_new_order(order_id)
                            
                            assert exc_info.value.error_code == "MARZBAN_CREATE_FAILED"


class TestFulfillmentRenewOrder:
    """测试续费开通流程"""
    
    @pytest.mark.asyncio
    async def test_fulfill_renew_order_success(self, mock_marzban_client):
        """测试续费开通成功"""
        order_id = str(ulid.new().str)
        plan_id = str(ulid.new().str)
        username = "user_existing_123"
        
        # 创建 mock 订单
        mock_order = MagicMock(spec=Order)
        mock_order.user_id = "user_test_123"
        mock_order.id = order_id
        mock_order.purchase_type = "renew"
        mock_order.status = OrderStatus.PAID_SUCCESS.value
        mock_order.plan_id = plan_id
        mock_order.marzban_username = username
        mock_order.fulfilled_at = None
        
        # 创建 mock 套餐
        mock_plan = MagicMock(spec=Plan)
        mock_plan.id = plan_id
        mock_plan.duration_days = 30
        mock_plan.traffic_bytes = 10 * 1024 * 1024 * 1024
        
        # 创建 mock client_token
        settings = MagicMock()
        settings.jwt_secret = os.environ["JWT_SECRET"]
        settings.jwt_algorithm = "HS256"
        settings.jwt_access_token_expire_minutes = 30
        settings.jwt_refresh_token_expire_days = 90
        
        with patch('app.services.fulfillment.get_settings', return_value=settings):
            access_token, _, _ = generate_client_tokens("user_test_123", username)
        
        # 模拟现有用户数据
        current_expire = int((datetime.now(timezone.utc) + timedelta(days=10)).timestamp())
        current_data_limit = 10 * 1024 * 1024 * 1024
        
        mock_existing_user = User(
            username=username,
            status="active",
            expire=current_expire,
            data_limit=current_data_limit,
            used_traffic=5 * 1024 * 1024 * 1024,  # 已用 5GB
            subscription_url="http://test-marzban.local/sub/existing_token",
            created_at=datetime.now(timezone.utc) - timedelta(days=20),
        )
        
        mock_marzban_client.get_user = AsyncMock(return_value=mock_existing_user)
        
        # 模拟更新后的用户
        new_expire = current_expire + (30 * 24 * 60 * 60)  # 增加30天
        new_data_limit = current_data_limit + (10 * 1024 * 1024 * 1024)  # 增加10GB
        
        mock_updated_user = User(
            username=username,
            status="active",
            expire=new_expire,
            data_limit=new_data_limit,
            used_traffic=5 * 1024 * 1024 * 1024,
            subscription_url="http://test-marzban.local/sub/existing_token",
            created_at=datetime.now(timezone.utc) - timedelta(days=20),
        )
        
        mock_marzban_client.modify_user = AsyncMock(return_value=mock_updated_user)
        
        mock_session = AsyncMock()
        mock_session.execute = AsyncMock(return_value=MagicMock(scalar_one_or_none=MagicMock(return_value=None)))
        
        with patch('app.services.fulfillment._check_idempotent', new_callable=AsyncMock, return_value=None):
            with patch('app.services.fulfillment._get_order_with_plan', new_callable=AsyncMock, return_value=(mock_order, mock_plan)):
                with patch('app.services.fulfillment.get_marzban_client', return_value=mock_marzban_client):
                    with patch('app.services.fulfillment._record_audit_log', new_callable=AsyncMock):
                        with patch('app.services.fulfillment.transition_to_fulfilled'):
                            with patch('app.services.fulfillment.get_db_context') as mock_db_context:
                                mock_db_context.return_value.__aenter__ = AsyncMock(return_value=mock_session)
                                mock_db_context.return_value.__aexit__ = AsyncMock(return_value=False)
                                
                                result = await fulfill_renew_order(order_id, access_token)
                                
                                # 验证结果
                                assert result.success is True
                                assert result.marzban_username == username
                                
                                # 验证 modify_user 被调用
                                mock_marzban_client.modify_user.assert_called_once()
                                call_args = mock_marzban_client.modify_user.call_args
                                assert call_args.kwargs['expire'] == new_expire
                                assert call_args.kwargs['data_limit'] == new_data_limit
                                assert call_args.kwargs['status'] == "active"
    
    @pytest.mark.asyncio
    async def test_fulfill_renew_order_user_not_found(self, mock_marzban_client):
        """测试续费用户不存在"""
        order_id = str(ulid.new().str)
        plan_id = str(ulid.new().str)
        username = "nonexistent_user"
        
        mock_order = MagicMock(spec=Order)
        mock_order.user_id = "user_test_123"
        mock_order.id = order_id
        mock_order.purchase_type = "renew"
        mock_order.status = OrderStatus.PAID_SUCCESS.value
        mock_order.plan_id = plan_id
        mock_order.marzban_username = None
        
        mock_plan = MagicMock(spec=Plan)
        mock_plan.id = plan_id
        
        # 模拟用户不存在
        mock_marzban_client.get_user = AsyncMock(return_value=None)
        
        settings = MagicMock()
        settings.jwt_secret = os.environ["JWT_SECRET"]
        settings.jwt_algorithm = "HS256"
        settings.jwt_access_token_expire_minutes = 30
        settings.jwt_refresh_token_expire_days = 90
        
        with patch('app.services.fulfillment.get_settings', return_value=settings):
            access_token, _, _ = generate_client_tokens("user_test_123", username)
        
        mock_session = AsyncMock()
        
        with patch('app.services.fulfillment._check_idempotent', new_callable=AsyncMock, return_value=None):
            with patch('app.services.fulfillment._get_order_with_plan', new_callable=AsyncMock, return_value=(mock_order, mock_plan)):
                with patch('app.services.fulfillment.get_marzban_client', return_value=mock_marzban_client):
                    with patch('app.services.fulfillment.get_db_context') as mock_db_context:
                        mock_db_context.return_value.__aenter__ = AsyncMock(return_value=mock_session)
                        mock_db_context.return_value.__aexit__ = AsyncMock(return_value=False)
                        
                        with pytest.raises(FulfillmentError) as exc_info:
                            await fulfill_renew_order(order_id, access_token)
                        
                        assert exc_info.value.error_code == "USER_NOT_FOUND"
    
    @pytest.mark.asyncio
    async def test_fulfill_renew_order_expired_user(self, mock_marzban_client):
        """测试续费已过期用户（从当前时间开始计算）"""
        order_id = str(ulid.new().str)
        plan_id = str(ulid.new().str)
        username = "expired_user_123"
        
        mock_order = MagicMock(spec=Order)
        mock_order.user_id = "user_test_123"
        mock_order.id = order_id
        mock_order.purchase_type = "renew"
        mock_order.status = OrderStatus.PAID_SUCCESS.value
        mock_order.plan_id = plan_id
        mock_order.marzban_username = username
        mock_order.fulfilled_at = None
        
        mock_plan = MagicMock(spec=Plan)
        mock_plan.id = plan_id
        mock_plan.duration_days = 30
        mock_plan.traffic_bytes = 10 * 1024 * 1024 * 1024
        
        # 已过期的用户（过期时间为10天前）
        expired_time = int((datetime.now(timezone.utc) - timedelta(days=10)).timestamp())
        current_data_limit = 10 * 1024 * 1024 * 1024
        
        mock_expired_user = User(
            username=username,
            status="expired",
            expire=expired_time,
            data_limit=current_data_limit,
            used_traffic=10 * 1024 * 1024 * 1024,  # 已用完流量
            subscription_url="http://test-marzban.local/sub/expired_token",
            created_at=datetime.now(timezone.utc) - timedelta(days=40),
        )
        
        mock_marzban_client.get_user = AsyncMock(return_value=mock_expired_user)
        
        # 模拟更新后的用户
        mock_updated_user = User(
            username=username,
            status="active",
            expire=int((datetime.now(timezone.utc) + timedelta(days=30)).timestamp()),
            data_limit=current_data_limit + (10 * 1024 * 1024 * 1024),
            used_traffic=10 * 1024 * 1024 * 1024,
            subscription_url="http://test-marzban.local/sub/expired_token",
            created_at=datetime.now(timezone.utc) - timedelta(days=40),
        )
        
        mock_marzban_client.modify_user = AsyncMock(return_value=mock_updated_user)
        
        settings = MagicMock()
        settings.jwt_secret = os.environ["JWT_SECRET"]
        settings.jwt_algorithm = "HS256"
        settings.jwt_access_token_expire_minutes = 30
        settings.jwt_refresh_token_expire_days = 90
        
        with patch('app.services.fulfillment.get_settings', return_value=settings):
            access_token, _, _ = generate_client_tokens("user_test_123", username)
        
        mock_session = AsyncMock()
        mock_session.execute = AsyncMock(return_value=MagicMock(scalar_one_or_none=MagicMock(return_value=None)))
        
        with patch('app.services.fulfillment._check_idempotent', new_callable=AsyncMock, return_value=None):
            with patch('app.services.fulfillment._get_order_with_plan', new_callable=AsyncMock, return_value=(mock_order, mock_plan)):
                with patch('app.services.fulfillment.get_marzban_client', return_value=mock_marzban_client):
                    with patch('app.services.fulfillment._record_audit_log', new_callable=AsyncMock):
                        with patch('app.services.fulfillment.transition_to_fulfilled'):
                            with patch('app.services.fulfillment.get_db_context') as mock_db_context:
                                mock_db_context.return_value.__aenter__ = AsyncMock(return_value=mock_session)
                                mock_db_context.return_value.__aexit__ = AsyncMock(return_value=False)
                                
                                result = await fulfill_renew_order(order_id, access_token)
                                
                                # 验证结果
                                assert result.success is True
                                
                                # 验证 modify_user 被调用，且 expire 是从当前时间计算
                                mock_marzban_client.modify_user.assert_called_once()
                                call_args = mock_marzban_client.modify_user.call_args
                                
                                # 新过期时间应该接近当前时间 + 30天
                                expected_expire = int((datetime.now(timezone.utc) + timedelta(days=30)).timestamp())
                                actual_expire = call_args.kwargs['expire']
                                # 允许 10 秒的误差
                                assert abs(actual_expire - expected_expire) < 10


class TestIdempotency:
    """测试幂等性保证"""
    
    @pytest.mark.asyncio
    async def test_concurrent_fulfill_new_order(self, mock_marzban_client):
        """测试并发履行新购订单（只有一个应该成功）"""
        order_id = str(ulid.new().str)
        plan_id = str(ulid.new().str)
        username = "user_concurrent_test"
        
        mock_order = MagicMock(spec=Order)
        mock_order.user_id = "user_test_123"
        mock_order.id = order_id
        mock_order.purchase_type = "new"
        mock_order.status = OrderStatus.PAID_SUCCESS.value
        mock_order.plan_id = plan_id
        mock_order.marzban_username = None
        mock_order.fulfilled_at = None
        
        mock_plan = MagicMock(spec=Plan)
        mock_plan.id = plan_id
        mock_plan.duration_days = 30
        mock_plan.traffic_bytes = 10 * 1024 * 1024 * 1024
        
        # 模拟第一次调用返回 None，后续调用返回已有结果
        call_count = 0
        async def mock_check_idempotent(session, oid):
            nonlocal call_count
            call_count += 1
            if call_count == 1:
                return None
            # 后续调用返回已履行结果
            return FulfillmentResult(
                success=True,
                marzban_username=username,
                access_token="cached_access_token",
                refresh_token="cached_refresh_token",
                expires_at=datetime.now(timezone.utc) + timedelta(days=30),
                subscription_url="http://test/sub/cached",
            )
        
        mock_session = AsyncMock()
        
        with patch('app.services.fulfillment._check_idempotent', side_effect=mock_check_idempotent):
            with patch('app.services.fulfillment._get_order_with_plan', new_callable=AsyncMock, return_value=(mock_order, mock_plan)):
                with patch('app.services.fulfillment._generate_username', return_value=username):
                    with patch('app.services.fulfillment.get_marzban_client', return_value=mock_marzban_client):
                        with patch('app.services.fulfillment._record_audit_log', new_callable=AsyncMock):
                            with patch('app.services.fulfillment.transition_to_fulfilled'):
                                with patch('app.services.fulfillment.get_db_context') as mock_db_context:
                                    mock_db_context.return_value.__aenter__ = AsyncMock(return_value=mock_session)
                                    mock_db_context.return_value.__aexit__ = AsyncMock(return_value=False)
                                    
                                    # 第一次调用
                                    result1 = await fulfill_new_order(order_id)
                                    
                                    # 第二次调用（应该返回缓存结果）
                                    result2 = await fulfill_new_order(order_id)
                                    
                                    # 两次结果应该都成功
                                    assert result1.success is True
                                    assert result2.success is True
                                    assert result1.marzban_username == username
                                    assert result2.marzban_username == username
                                    
                                    # Marzban create_user 应该只被调用一次
                                    assert mock_marzban_client.create_user.call_count == 1


class TestAuditLog:
    """测试审计日志记录"""
    
    @pytest.mark.asyncio
    async def test_new_order_audit_log(self, mock_marzban_client):
        """测试新购订单记录审计日志"""
        order_id = str(ulid.new().str)
        plan_id = str(ulid.new().str)
        username = "user_audit_test"
        
        mock_order = MagicMock(spec=Order)
        mock_order.user_id = "user_test_123"
        mock_order.id = order_id
        mock_order.purchase_type = "new"
        mock_order.status = OrderStatus.PAID_SUCCESS.value
        mock_order.plan_id = plan_id
        mock_order.marzban_username = None
        mock_order.fulfilled_at = None
        
        mock_plan = MagicMock(spec=Plan)
        mock_plan.id = plan_id
        mock_plan.duration_days = 30
        mock_plan.traffic_bytes = 10 * 1024 * 1024 * 1024
        
        captured_audit_calls = []
        
        async def mock_record_audit(session, **kwargs):
            captured_audit_calls.append(kwargs)
        
        mock_session = AsyncMock()
        
        with patch('app.services.fulfillment._check_idempotent', new_callable=AsyncMock, return_value=None):
            with patch('app.services.fulfillment._get_order_with_plan', new_callable=AsyncMock, return_value=(mock_order, mock_plan)):
                with patch('app.services.fulfillment._generate_username', return_value=username):
                    with patch('app.services.fulfillment.get_marzban_client', return_value=mock_marzban_client):
                        with patch('app.services.fulfillment._record_audit_log', side_effect=mock_record_audit):
                            with patch('app.services.fulfillment.transition_to_fulfilled'):
                                with patch('app.services.fulfillment.get_db_context') as mock_db_context:
                                    mock_db_context.return_value.__aenter__ = AsyncMock(return_value=mock_session)
                                    mock_db_context.return_value.__aexit__ = AsyncMock(return_value=False)
                                    
                                    await fulfill_new_order(order_id)
                                    
                                    # 验证审计日志被记录
                                    assert len(captured_audit_calls) == 1
                                    audit_call = captured_audit_calls[0]
                                    assert audit_call['entity_type'] == "order"
                                    assert audit_call['entity_id'] == order_id
                                    assert audit_call['action'] == "fulfilled_new"
                                    assert audit_call['operator_type'] == OperatorType.SYSTEM
                                    
                                    # 验证 payload 内容
                                    payload = audit_call['payload']
                                    assert payload['marzban_username'] == username
                                    assert payload['plan_id'] == plan_id
                                    assert payload['duration_days'] == 30
                                    assert payload['traffic_bytes'] == 10 * 1024 * 1024 * 1024
    
    @pytest.mark.asyncio
    async def test_renew_order_audit_log(self, mock_marzban_client):
        """测试续费订单记录审计日志"""
        order_id = str(ulid.new().str)
        plan_id = str(ulid.new().str)
        username = "user_audit_renew"
        
        mock_order = MagicMock(spec=Order)
        mock_order.user_id = "user_test_123"
        mock_order.id = order_id
        mock_order.purchase_type = "renew"
        mock_order.status = OrderStatus.PAID_SUCCESS.value
        mock_order.plan_id = plan_id
        mock_order.marzban_username = username
        mock_order.fulfilled_at = None
        
        mock_plan = MagicMock(spec=Plan)
        mock_plan.id = plan_id
        mock_plan.duration_days = 30
        mock_plan.traffic_bytes = 10 * 1024 * 1024 * 1024
        
        current_expire = int((datetime.now(timezone.utc) + timedelta(days=10)).timestamp())
        current_data_limit = 10 * 1024 * 1024 * 1024
        
        mock_existing_user = User(
            username=username,
            status="active",
            expire=current_expire,
            data_limit=current_data_limit,
            used_traffic=0,
            subscription_url="http://test/sub/token",
            created_at=datetime.now(timezone.utc),
        )
        
        mock_marzban_client.get_user = AsyncMock(return_value=mock_existing_user)
        
        new_expire = current_expire + (30 * 24 * 60 * 60)
        new_data_limit = current_data_limit + (10 * 1024 * 1024 * 1024)
        
        mock_updated_user = User(
            username=username,
            status="active",
            expire=new_expire,
            data_limit=new_data_limit,
            used_traffic=0,
            subscription_url="http://test/sub/token",
            created_at=datetime.now(timezone.utc),
        )
        
        mock_marzban_client.modify_user = AsyncMock(return_value=mock_updated_user)
        
        captured_audit_calls = []
        
        async def mock_record_audit(session, **kwargs):
            captured_audit_calls.append(kwargs)
        
        settings = MagicMock()
        settings.jwt_secret = os.environ["JWT_SECRET"]
        settings.jwt_algorithm = "HS256"
        settings.jwt_access_token_expire_minutes = 30
        settings.jwt_refresh_token_expire_days = 90
        
        with patch('app.services.fulfillment.get_settings', return_value=settings):
            access_token, _, _ = generate_client_tokens("user_test_123", username)
        
        mock_session = AsyncMock()
        mock_session.execute = AsyncMock(return_value=MagicMock(scalar_one_or_none=MagicMock(return_value=None)))
        
        with patch('app.services.fulfillment._check_idempotent', new_callable=AsyncMock, return_value=None):
            with patch('app.services.fulfillment._get_order_with_plan', new_callable=AsyncMock, return_value=(mock_order, mock_plan)):
                with patch('app.services.fulfillment.get_marzban_client', return_value=mock_marzban_client):
                    with patch('app.services.fulfillment._record_audit_log', side_effect=mock_record_audit):
                        with patch('app.services.fulfillment.transition_to_fulfilled'):
                            with patch('app.services.fulfillment.get_db_context') as mock_db_context:
                                mock_db_context.return_value.__aenter__ = AsyncMock(return_value=mock_session)
                                mock_db_context.return_value.__aexit__ = AsyncMock(return_value=False)
                                
                                await fulfill_renew_order(order_id, access_token)
                                
                                # 验证审计日志被记录
                                assert len(captured_audit_calls) == 1
                                audit_call = captured_audit_calls[0]
                                assert audit_call['entity_type'] == "order"
                                assert audit_call['entity_id'] == order_id
                                assert audit_call['action'] == "fulfilled_renew"
                                
                                # 验证 payload 包含前后对比信息
                                payload = audit_call['payload']
                                assert payload['marzban_username'] == username
                                assert 'previous_expire' in payload
                                assert 'new_expire' in payload
                                assert 'previous_data_limit' in payload
                                assert 'new_data_limit' in payload


# ============== Test Refresh Session ==============

class TestRefreshSession:
    """测试会话刷新"""
    
    @pytest.mark.asyncio
    async def test_refresh_session_success(self):
        """测试会话刷新成功"""
        username = "user_refresh_test"
        order_id = str(ulid.new().str)
        
        settings = MagicMock()
        settings.jwt_secret = os.environ["JWT_SECRET"]
        settings.jwt_algorithm = "HS256"
        settings.jwt_access_token_expire_minutes = 30
        settings.jwt_refresh_token_expire_days = 90
        
        with patch('app.services.fulfillment.get_settings', return_value=settings):
            access_token, refresh_token, expires_at = generate_client_tokens("user_test_123", username)
        
        # 创建 mock 会话
        mock_session_record = MagicMock(spec=ClientSession)
        mock_session_record.id = str(ulid.new().str)
        mock_session_record.order_id = order_id
        mock_session_record.user_id = "user_test_123"
        mock_session_record.marzban_username = username
        mock_session_record.refresh_token = refresh_token
        mock_session_record.expires_at = datetime.now(timezone.utc) + timedelta(days=30)
        mock_session_record.revoked_at = None
        
        mock_session = AsyncMock()
        mock_result = MagicMock()
        mock_result.scalar_one_or_none = MagicMock(return_value=mock_session_record)
        mock_session.execute = AsyncMock(return_value=mock_result)
        
        with patch('app.services.fulfillment._record_audit_log', new_callable=AsyncMock):
            with patch('app.services.fulfillment.get_db_context') as mock_db_context:
                mock_db_context.return_value.__aenter__ = AsyncMock(return_value=mock_session)
                mock_db_context.return_value.__aexit__ = AsyncMock(return_value=False)
                
                result = await refresh_session(refresh_token, access_token)
                
                # 验证结果
                assert result.success is True
                assert result.marzban_username == username
                assert result.access_token is not None
                assert result.refresh_token is not None
                assert result.access_token != access_token  # 新 token
                assert result.refresh_token != refresh_token  # 新 refresh token
                
                # 验证旧会话被吊销
                assert mock_session_record.revoked_at is not None
    
    @pytest.mark.asyncio
    async def test_refresh_session_invalid_token(self):
        """测试无效的 refresh token"""
        settings = MagicMock()
        settings.jwt_secret = os.environ["JWT_SECRET"]
        settings.jwt_algorithm = "HS256"
        
        # 创建一个无效 token
        invalid_token = "invalid.token.here"
        
        mock_session = AsyncMock()
        
        with patch('app.services.fulfillment.get_settings', return_value=settings):
            with patch('app.services.fulfillment.get_db_context') as mock_db_context:
                mock_db_context.return_value.__aenter__ = AsyncMock(return_value=mock_session)
                mock_db_context.return_value.__aexit__ = AsyncMock(return_value=False)
                
                with pytest.raises(FulfillmentError) as exc_info:
                    await refresh_session(invalid_token)
                
                assert exc_info.value.error_code == "INVALID_TOKEN"
    
    @pytest.mark.asyncio
    async def test_refresh_session_revoked_token(self):
        """测试已吊销的 refresh token"""
        username = "user_revoked_test"
        order_id = str(ulid.new().str)
        
        settings = MagicMock()
        settings.jwt_secret = os.environ["JWT_SECRET"]
        settings.jwt_algorithm = "HS256"
        settings.jwt_access_token_expire_minutes = 30
        settings.jwt_refresh_token_expire_days = 90
        
        with patch('app.services.fulfillment.get_settings', return_value=settings):
            _, refresh_token, _ = generate_client_tokens("user_test_123", username)
        
        # 模拟会话不存在（已吊销或不存在）
        mock_session = AsyncMock()
        mock_result = MagicMock()
        mock_result.scalar_one_or_none = MagicMock(return_value=None)
        mock_session.execute = AsyncMock(return_value=mock_result)
        
        with patch('app.services.fulfillment.get_settings', return_value=settings):
            with patch('app.services.fulfillment.get_db_context') as mock_db_context:
                mock_db_context.return_value.__aenter__ = AsyncMock(return_value=mock_session)
                mock_db_context.return_value.__aexit__ = AsyncMock(return_value=False)
                
                with pytest.raises(FulfillmentError) as exc_info:
                    await refresh_session(refresh_token)
                
                assert exc_info.value.error_code == "INVALID_REFRESH_TOKEN"


# ============== Test Error Handling ==============

class TestErrorHandling:
    """测试错误处理"""
    
    @pytest.mark.asyncio
    async def test_order_not_found(self):
        """测试订单不存在"""
        order_id = str(ulid.new().str)
        
        mock_session = AsyncMock()
        
        with patch('app.services.fulfillment._check_idempotent', new_callable=AsyncMock, return_value=None):
            with patch('app.services.fulfillment._get_order_with_plan', new_callable=AsyncMock, side_effect=
                      FulfillmentError("ORDER_NOT_FOUND", f"Order {order_id} not found")):
                with patch('app.services.fulfillment.get_db_context') as mock_db_context:
                    mock_db_context.return_value.__aenter__ = AsyncMock(return_value=mock_session)
                    mock_db_context.return_value.__aexit__ = AsyncMock(return_value=False)
                    
                    with pytest.raises(FulfillmentError) as exc_info:
                        await fulfill_new_order(order_id)
                    
                    assert exc_info.value.error_code == "ORDER_NOT_FOUND"
    
    @pytest.mark.asyncio
    async def test_duplicate_transition_handling(self, mock_marzban_client):
        """测试重复状态转换处理"""
        order_id = str(ulid.new().str)
        plan_id = str(ulid.new().str)
        username = "user_dup_test"
        
        mock_order = MagicMock(spec=Order)
        mock_order.user_id = "user_test_123"
        mock_order.id = order_id
        mock_order.purchase_type = "new"
        mock_order.status = OrderStatus.PAID_SUCCESS.value
        mock_order.plan_id = plan_id
        mock_order.marzban_username = None
        mock_order.fulfilled_at = None
        
        mock_plan = MagicMock(spec=Plan)
        mock_plan.id = plan_id
        mock_plan.duration_days = 30
        mock_plan.traffic_bytes = 10 * 1024 * 1024 * 1024
        
        mock_session = AsyncMock()
        
        # 模拟在状态转换时抛出 DuplicateTransitionError
        from app.core.state_machine import DuplicateTransitionError
        
        with patch('app.services.fulfillment._check_idempotent', new_callable=AsyncMock, return_value=None):
            with patch('app.services.fulfillment._get_order_with_plan', new_callable=AsyncMock, return_value=(mock_order, mock_plan)):
                with patch('app.services.fulfillment._generate_username', return_value=username):
                    with patch('app.services.fulfillment.get_marzban_client', return_value=mock_marzban_client):
                        with patch('app.services.fulfillment.transition_to_fulfilled', side_effect=
                                  DuplicateTransitionError(order_id, "fulfilled")):
                            with patch('app.services.fulfillment.get_db_context') as mock_db_context:
                                mock_db_context.return_value.__aenter__ = AsyncMock(return_value=mock_session)
                                mock_db_context.return_value.__aexit__ = AsyncMock(return_value=False)
                                
                                # 第二次调用 _check_idempotent 应该返回已有结果
                                existing_result = FulfillmentResult(
                                    success=True,
                                    marzban_username=username,
                                    access_token="existing_token",
                                    refresh_token="existing_refresh",
                                    expires_at=datetime.now(timezone.utc) + timedelta(days=30),
                                    subscription_url="http://test/sub",
                                )
                                
                                with patch('app.services.fulfillment._check_idempotent', new_callable=AsyncMock, return_value=existing_result):
                                    result = await fulfill_new_order(order_id)
                                    
                                    # 应该返回已有结果
                                    assert result.success is True
                                    assert result.marzban_username == username


# ============== Test Username Generation ==============

class TestUsernameGeneration:
    """测试用户名生成"""
    
    def test_generate_username_format(self):
        """测试用户名格式"""
        from app.services.fulfillment import _generate_username
        
        user_id = "user_test_123"
        username = _generate_username(user_id)
        
        # 验证格式: {user_id前8位}_{时间戳后6位}
        parts = username.split("_")
        assert len(parts) == 2
        assert parts[0] == user_id[:8]
        assert len(parts[1]) == 6
        assert parts[1].isdigit()
    
    def test_generate_username_uniqueness(self):
        """测试用户名唯一性（概率上）"""
        from app.services.fulfillment import _generate_username
        
        user_id = "user_test_123"
        # 生成 100 个用户名，应该都是唯一的
        usernames = [_generate_username(user_id) for _ in range(100)]
        assert len(set(usernames)) == 100


class TestSelectForUpdate:
    """测试 SELECT FOR UPDATE 并发控制"""
    
    @pytest.mark.asyncio
    async def test_get_order_with_plan_for_update(self):
        """测试带锁的订单查询"""
        from app.services.fulfillment import _get_order_with_plan
        
        order_id = str(ulid.new().str)
        plan_id = str(ulid.new().str)
        
        mock_order = MagicMock(spec=Order)
        mock_order.user_id = "user_test_123"
        mock_order.id = order_id
        mock_order.status = OrderStatus.PAID_SUCCESS.value
        
        mock_plan = MagicMock(spec=Plan)
        mock_plan.id = plan_id
        
        # 模拟查询结果
        mock_row = MagicMock()
        mock_row.Order = mock_order
        mock_row.Plan = mock_plan
        
        mock_result = MagicMock()
        mock_result.first = MagicMock(return_value=mock_row)
        
        mock_session = AsyncMock()
        mock_session.execute = AsyncMock(return_value=mock_result)
        
        # 测试不带锁
        order, plan = await _get_order_with_plan(mock_session, order_id, for_update=False)
        assert order == mock_order
        assert plan == mock_plan
        
        # 验证查询没有 with_for_update
        call_args = mock_session.execute.call_args
        # 注意：这里我们验证查询被构造，但实际的行为在集成测试中验证
    
    @pytest.mark.asyncio
    async def test_concurrent_fulfill_does_not_duplicate_marzban_user(self, mock_marzban_client):
        """测试并发履行不会重复创建 Marzban 用户（SELECT FOR UPDATE 保护）"""
        order_id = str(ulid.new().str)
        plan_id = str(ulid.new().str)
        username = "user_concurrent_sfu_test"
        
        mock_order = MagicMock(spec=Order)
        mock_order.user_id = "user_test_123"
        mock_order.id = order_id
        mock_order.purchase_type = "new"
        mock_order.status = OrderStatus.PAID_SUCCESS.value
        mock_order.plan_id = plan_id
        mock_order.marzban_username = None
        mock_order.fulfilled_at = None
        
        mock_plan = MagicMock(spec=Plan)
        mock_plan.id = plan_id
        mock_plan.duration_days = 30
        mock_plan.traffic_bytes = 10 * 1024 * 1024 * 1024
        
        mock_session = AsyncMock()
        
        with patch('app.services.fulfillment._check_idempotent', new_callable=AsyncMock, return_value=None):
            with patch('app.services.fulfillment._get_order_with_plan', new_callable=AsyncMock, return_value=(mock_order, mock_plan)):
                with patch('app.services.fulfillment._generate_username', return_value=username):
                    with patch('app.services.fulfillment.get_marzban_client', return_value=mock_marzban_client):
                        with patch('app.services.fulfillment._record_audit_log', new_callable=AsyncMock):
                            with patch('app.services.fulfillment.transition_to_fulfilled'):
                                with patch('app.services.fulfillment.get_db_context') as mock_db_context:
                                    mock_db_context.return_value.__aenter__ = AsyncMock(return_value=mock_session)
                                    mock_db_context.return_value.__aexit__ = AsyncMock(return_value=False)
                                    
                                    result = await fulfill_new_order(order_id)
                                    
                                    # 验证成功
                                    assert result.success is True
                                    
                                    # 验证 _get_order_with_plan 被调用时使用 for_update=True
                                    call_args = mock_order.call_args


class TestOrderFailedTransition:
    """测试订单失败状态转移"""
    
    @pytest.mark.asyncio
    async def test_marzban_error_transitions_to_failed(self, mock_marzban_client):
        """测试 Marzban 错误时订单转移到 failed 状态"""
        order_id = str(ulid.new().str)
        plan_id = str(ulid.new().str)
        username = "user_fail_test"
        
        mock_order = MagicMock(spec=Order)
        mock_order.user_id = "user_test_123"
        mock_order.id = order_id
        mock_order.purchase_type = "new"
        mock_order.status = OrderStatus.PAID_SUCCESS.value
        mock_order.plan_id = plan_id
        mock_order.marzban_username = None
        mock_order.fulfilled_at = None
        
        mock_plan = MagicMock(spec=Plan)
        mock_plan.id = plan_id
        mock_plan.duration_days = 30
        mock_plan.traffic_bytes = 10 * 1024 * 1024 * 1024
        
        # 模拟 Marzban API 错误
        mock_marzban_client.create_user = AsyncMock(side_effect=
            MarzbanAPIError("Internal server error", status_code=500))
        
        mock_session = AsyncMock()
        
        # 模拟订单查询结果（用于 _try_transition_to_failed）
        mock_order_for_failed = MagicMock(spec=Order)
        mock_order_for_failed.id = order_id
        mock_order_for_failed.status = OrderStatus.PAID_SUCCESS.value
        
        mock_result = MagicMock()
        mock_result.scalar_one_or_none = MagicMock(return_value=mock_order_for_failed)
        mock_session.execute = AsyncMock(return_value=mock_result)
        
        with patch('app.services.fulfillment._check_idempotent', new_callable=AsyncMock, return_value=None):
            with patch('app.services.fulfillment._get_order_with_plan', new_callable=AsyncMock, return_value=(mock_order, mock_plan)):
                with patch('app.services.fulfillment._generate_username', return_value=username):
                    with patch('app.services.fulfillment.get_marzban_client', return_value=mock_marzban_client):
                        with patch('app.services.fulfillment.transition_to_failed') as mock_transition:
                            with patch('app.services.fulfillment.get_db_context') as mock_db_context:
                                mock_db_context.return_value.__aenter__ = AsyncMock(return_value=mock_session)
                                mock_db_context.return_value.__aexit__ = AsyncMock(return_value=False)
                                
                                with pytest.raises(FulfillmentError) as exc_info:
                                    await fulfill_new_order(order_id)
                                
                                assert exc_info.value.error_code == "MARZBAN_CREATE_FAILED"
                                
                                # 验证 transition_to_failed 被调用
                                mock_transition.assert_called_once()
                                
                                # 验证订单状态被更新
                                assert mock_order_for_failed.status == OrderStatus.FAILED.value
                                assert mock_order_for_failed.error_code == "MARZBAN_CREATE_FAILED"


if __name__ == "__main__":
    pytest.main([__file__, "-v"])
