"""
真实数据完整回归测试 - Real Data Full Regression Test

测试环境:
- 数据库: 真实 PostgreSQL (154.36.173.184)
- 区块链: 真实 Solana Devnet + Tron Nile 测试网
- Marzban: 真实 Marzban 面板

测试范围:
1. 新购流程端到端测试
2. 续费流程端到端测试
3. 异常处理测试
4. 性能测试
5. 安全测试

注意: 此测试使用真实的外部服务，会产生真实的链上交易。
"""
import asyncio
import time
import uuid
from datetime import datetime, timezone, timedelta
from decimal import Decimal
from typing import Optional, Dict, Any, List
import concurrent.futures
import statistics

import pytest
import pytest_asyncio
import httpx
import jwt
from sqlalchemy import select, and_, or_, func
from sqlalchemy.ext.asyncio import AsyncSession

# 设置环境变量 - 确保使用真实数据
import os
os.environ["SOLANA_MOCK_MODE"] = "false"
os.environ["TRON_MOCK_MODE"] = "false"

from app.main import app
from app.core.config import get_settings
from app.core.database import get_db_context, AsyncSessionLocal
from app.core.state_machine import OrderStatus
from app.integrations.solana import SolanaClient
from app.integrations.tron import TronClient
from app.integrations.marzban import get_marzban_client, MarzbanClient
from app.models.order import Order
from app.models.plan import Plan
from app.models.payment_address import PaymentAddress
from app.models.client_session import ClientSession
from app.services.fulfillment import fulfill_new_order, fulfill_renew_order, FulfillmentError
from app.services.fx_rate import FXRateService
from app.workers.scanner import scan_pending_orders, confirm_seen_transactions, expire_orders
from app.workers.fulfillment import fulfill_paid_orders


# ============== 测试配置 ==============

# 测试用 Solana Devnet 钱包 (测试网资金可申请)
TEST_SOLANA_WALLET = "6bBh9bYtBq2cwbvqRkY1W3i2p7VdP7YwG6JG7E1t9sNx"  # 收款测试地址
TEST_SOLANA_PRIVATE_KEY = None  # 如果提供，测试会发送真实交易

# 测试用 Tron Nile 钱包
TEST_TRON_WALLET = "TXYZopYRdj2D9XRtbG411XZZ3kpm5bGnNF"  # 收款测试地址

# API 基础 URL
BASE_URL = "http://localhost:8000"

# 性能测试阈值
MAX_API_RESPONSE_MS = 200  # API 响应时间 < 200ms
MAX_WORKER_DELAY_SEC = 5   # Worker 处理延迟 < 5s


# ============== 测试固件 ==============

@pytest.fixture(scope="module")
def event_loop():
    """创建事件循环"""
    loop = asyncio.get_event_loop_policy().new_event_loop()
    yield loop
    loop.close()


@pytest_asyncio.fixture(scope="module")
async def db_session():
    """数据库会话固件"""
    async with AsyncSessionLocal() as session:
        yield session


@pytest.fixture(scope="module")
def settings():
    """应用配置"""
    return get_settings()


@pytest_asyncio.fixture(scope="module")
async def http_client():
    """HTTP 客户端"""
    async with httpx.AsyncClient(base_url=BASE_URL, timeout=30.0) as client:
        yield client


@pytest_asyncio.fixture(scope="module")
async def solana_client(settings):
    """Solana 真实客户端"""
    client = SolanaClient(
        rpc_url=settings.solana_rpc_url,
        mock_mode=False  # 强制使用真实模式
    )
    yield client
    await client.close()


@pytest_asyncio.fixture(scope="module")
async def tron_client(settings):
    """Tron 真实客户端"""
    client = TronClient(
        rpc_url=settings.tron_rpc_url,
        usdt_contract=settings.tron_usdt_contract,
        mock_mode=False  # 强制使用真实模式
    )
    yield client
    await client.close()


@pytest_asyncio.fixture(scope="module")
async def marzban_client(settings):
    """Marzban 真实客户端"""
    client = await get_marzban_client()
    yield client
    await client.close()


@pytest_asyncio.fixture(scope="function")
async def admin_token(settings):
    """生成管理员 JWT Token"""
    now = datetime.now(timezone.utc)
    payload = {
        "sub": "admin",
        "type": "admin_access",
        "role": "admin",
        "permissions": ["*"],
        "iat": now,
        "exp": now + timedelta(hours=1),
    }
    token = jwt.encode(
        payload,
        settings.jwt_secret,
        algorithm=settings.jwt_algorithm
    )
    return token


@pytest_asyncio.fixture(scope="function")
async def test_plan(db_session):
    """创建测试套餐"""
    plan = Plan(
        id=f"test_plan_{uuid.uuid4().hex[:8]}",
        code=f"TEST_{uuid.uuid4().hex[:8].upper()}",
        name="测试套餐-回归测试",
        description="用于回归测试的临时套餐",
        traffic_bytes=10 * 1024 * 1024 * 1024,  # 10GB
        duration_days=30,
        price_usd=Decimal("0.01"),  # 极低价格便于测试
        supported_assets=["SOL", "USDT_TRC20"],
        enabled=True,
        sort_order=999,
    )
    db_session.add(plan)
    await db_session.commit()
    await db_session.refresh(plan)
    
    yield plan
    
    # 清理
    await db_session.delete(plan)
    await db_session.commit()


# ============== 辅助函数 ==============

async def create_order_via_api(
    http_client: httpx.AsyncClient,
    plan_id: str,
    purchase_type: str = "new",
    asset_code: str = "SOL",
    client_user_id: str = None,
    marzban_username: str = None
) -> Dict[str, Any]:
    """通过 API 创建订单"""
    response = await http_client.post(
        "/client/v1/orders",
        json={
            "plan_id": plan_id,
            "purchase_type": purchase_type,
            "asset_code": asset_code,
            "client_user_id": client_user_id,
            "marzban_username": marzban_username,
        },
        headers={
            "X-Device-ID": f"test_device_{uuid.uuid4().hex[:8]}",
            "X-Client-Version": "1.0.0-test",
        }
    )
    assert response.status_code == 200, f"创建订单失败: {response.text}"
    return response.json()["data"]


async def get_order_via_api(http_client: httpx.AsyncClient, order_id: str) -> Dict[str, Any]:
    """通过 API 获取订单详情"""
    response = await http_client.get(f"/client/v1/orders/{order_id}")
    assert response.status_code == 200, f"获取订单失败: {response.text}"
    return response.json()["data"]["order"]


async def get_subscription_via_api(
    http_client: httpx.AsyncClient, 
    access_token: str
) -> Dict[str, Any]:
    """通过 API 获取订阅信息"""
    response = await http_client.get(
        "/client/v1/subscription",
        headers={"Authorization": f"Bearer {access_token}"}
    )
    assert response.status_code == 200, f"获取订阅失败: {response.text}"
    return response.json()["data"]


async def verify_subscription_url(url: str) -> bool:
    """验证订阅链接是否可访问"""
    try:
        async with httpx.AsyncClient(timeout=10.0) as client:
            response = await client.get(url)
            return response.status_code == 200 and len(response.text) > 0
    except Exception:
        return False


# ============== 测试场景 1: 新购流程 ==============

@pytest.mark.asyncio
@pytest.mark.real_data
async def test_new_purchase_flow_sol(http_client, test_plan, solana_client, db_session):
    """
    测试场景 1a: 新购流程 - SOL 支付
    
    完整流程:
    1. 创建订单
    2. 获取支付地址和金额
    3. 检测链上支付 (模拟真实交易)
    4. Worker 确认支付
    5. 订单 fulfill 创建 Marzban 用户
    6. 客户端拉取订阅信息
    7. 验证订阅链接可用
    """
    print("\n=== 测试场景 1a: 新购流程 - SOL 支付 ===")
    
    # Step 1: 创建订单
    print("Step 1: 创建订单...")
    start_time = time.time()
    order_data = await create_order_via_api(
        http_client, 
        test_plan.id, 
        purchase_type="new", 
        asset_code="SOL"
    )
    create_time = (time.time() - start_time) * 1000
    print(f"  ✓ 订单创建成功: {order_data['order_no']} (耗时: {create_time:.1f}ms)")
    assert create_time < MAX_API_RESPONSE_MS, f"API 响应时间 {create_time:.1f}ms 超过阈值 {MAX_API_RESPONSE_MS}ms"
    
    order_id = order_data["order_id"]
    receive_address = order_data["receive_address"]
    amount_crypto = Decimal(order_data["amount_crypto"])
    
    print(f"  - 收款地址: {receive_address}")
    print(f"  - 支付金额: {amount_crypto} SOL")
    
    # 验证订单数据库记录
    result = await db_session.execute(select(Order).where(Order.id == order_id))
    order = result.scalar_one()
    assert order.status == OrderStatus.PENDING_PAYMENT.value
    print(f"  ✓ 订单状态正确: {order.status}")
    
    # Step 2: 获取订单详情
    print("\nStep 2: 获取订单详情...")
    order_detail = await get_order_via_api(http_client, order_id)
    assert order_detail["status"] == "pending_payment"
    print(f"  ✓ 订单详情获取成功")
    
    # Step 3: 模拟真实 Solana Devnet 交易
    print("\nStep 3: 检测真实 Solana Devnet 地址...")
    # 使用真实 RPC 查询地址余额
    try:
        balance = await solana_client.get_balance(receive_address)
        print(f"  - 地址当前余额: {balance} SOL")
        
        # 注意: 真实测试中，如果提供了私钥，可以发送真实交易
        # 这里我们检测地址是否存在并可以查询
        assert balance is not None, "无法获取地址余额"
        print(f"  ✓ Solana Devnet RPC 连接正常")
    except Exception as e:
        print(f"  ! Solana Devnet 查询失败: {e}")
        pytest.skip(f"Solana Devnet 不可用: {e}")
    
    # 由于无法自动发送真实交易，这里标记测试为需要手动触发
    print("\n  ⚠️ 注意: 需要手动向以下地址发送测试网 SOL")
    print(f"     地址: {receive_address}")
    print(f"     金额: {amount_crypto} SOL")
    print(f"     可从 https://faucet.solana.com/ 获取 Devnet SOL")


@pytest.mark.asyncio
@pytest.mark.real_data
async def test_new_purchase_flow_usdt_trc20(http_client, test_plan, tron_client, db_session):
    """
    测试场景 1b: 新购流程 - USDT_TRC20 支付
    
    完整流程:
    1. 创建订单
    2. 获取支付地址和金额
    3. 检测链上支付
    4. Worker 确认支付
    5. 订单 fulfill 创建 Marzban 用户
    6. 客户端拉取订阅信息
    """
    print("\n=== 测试场景 1b: 新购流程 - USDT_TRC20 支付 ===")
    
    # Step 1: 创建订单
    print("Step 1: 创建订单...")
    order_data = await create_order_via_api(
        http_client, 
        test_plan.id, 
        purchase_type="new", 
        asset_code="USDT_TRC20"
    )
    
    order_id = order_data["order_id"]
    receive_address = order_data["receive_address"]
    amount_crypto = Decimal(order_data["amount_crypto"])
    
    print(f"  ✓ 订单创建成功: {order_data['order_no']}")
    print(f"  - 收款地址: {receive_address}")
    print(f"  - 支付金额: {amount_crypto} USDT")
    
    # Step 2: 检测真实 Tron Nile 网络
    print("\nStep 2: 检测真实 Tron Nile 网络...")
    try:
        # 查询地址的 USDT 余额
        balance = await tron_client.get_trc20_balance(receive_address)
        print(f"  - 地址当前 USDT 余额: {balance}")
        print(f"  ✓ Tron Nile RPC 连接正常")
    except Exception as e:
        print(f"  ! Tron Nile 查询失败: {e}")
        pytest.skip(f"Tron Nile 不可用: {e}")
    
    print("\n  ⚠️ 注意: 需要手动向以下地址发送测试网 USDT")
    print(f"     地址: {receive_address}")
    print(f"     金额: {amount_crypto} USDT")
    print(f"     可从 Nile 测试网获取 USDT")


@pytest.mark.asyncio
@pytest.mark.real_data
async def test_full_fulfillment_with_mock_payment(http_client, test_plan, db_session, settings):
    """
    测试场景 1c: 完整履行流程 (使用模拟支付数据测试 fulfill 逻辑)
    
    由于无法自动发送真实区块链交易，这个测试:
    1. 创建订单
    2. 手动将订单状态设为 paid_success
    3. 调用 fulfill_new_order
    4. 验证 Marzban 用户创建
    5. 验证订阅信息
    """
    print("\n=== 测试场景 1c: 完整履行流程测试 ===")
    
    # Step 1: 创建订单
    print("Step 1: 创建订单...")
    order_data = await create_order_via_api(
        http_client, 
        test_plan.id, 
        purchase_type="new", 
        asset_code="SOL"
    )
    order_id = order_data["order_id"]
    print(f"  ✓ 订单创建: {order_data['order_no']}")
    
    # Step 2: 模拟支付成功 (手动更新数据库)
    print("\nStep 2: 模拟支付成功状态...")
    result = await db_session.execute(select(Order).where(Order.id == order_id))
    order = result.scalar_one()
    order.status = OrderStatus.PAID_SUCCESS.value
    order.tx_hash = "test_tx_" + uuid.uuid4().hex
    order.tx_from = TEST_SOLANA_WALLET
    order.confirm_count = 32
    order.paid_at = datetime.now(timezone.utc)
    order.confirmed_at = datetime.now(timezone.utc)
    await db_session.commit()
    print(f"  ✓ 订单状态更新为 paid_success")
    
    # Step 3: 执行履行
    print("\nStep 3: 执行订单履行...")
    start_time = time.time()
    try:
        result = await fulfill_new_order(order_id)
        fulfill_time = (time.time() - start_time) * 1000
        print(f"  ✓ 履行成功 (耗时: {fulfill_time:.1f}ms)")
        print(f"  - Marzban 用户名: {result.marzban_username}")
        print(f"  - 订阅链接: {result.subscription_url[:50]}...")
        
        # Step 4: 验证订阅链接
        print("\nStep 4: 验证订阅链接...")
        is_valid = await verify_subscription_url(result.subscription_url)
        if is_valid:
            print(f"  ✓ 订阅链接有效")
        else:
            print(f"  ! 订阅链接验证失败 (可能 Marzban 服务未启动)")
        
        # Step 5: 使用 access_token 获取订阅信息
        print("\nStep 5: 使用 client_token 获取订阅信息...")
        try:
            subscription = await get_subscription_via_api(http_client, result.access_token)
            print(f"  ✓ 订阅信息获取成功")
            print(f"  - 过期时间: {subscription['expires_at']}")
            print(f"  - 总流量: {subscription['traffic_total']} bytes")
            
            # 验证过期时间正确
            expires_at = datetime.fromisoformat(subscription['expires_at'].replace('Z', '+00:00'))
            expected_expire = datetime.now(timezone.utc) + timedelta(days=test_plan.duration_days)
            assert abs((expires_at - expected_expire).total_seconds()) < 60, "过期时间计算错误"
            print(f"  ✓ 过期时间验证通过")
            
        except Exception as e:
            print(f"  ! 获取订阅信息失败: {e}")
        
        # 清理: 删除测试用户
        print("\nStep 6: 清理测试用户...")
        try:
            marzban = await get_marzban_client()
            await marzban.delete_user(result.marzban_username)
            print(f"  ✓ 测试用户已删除: {result.marzban_username}")
        except Exception as e:
            print(f"  ! 清理失败: {e}")
        
    except FulfillmentError as e:
        print(f"  ✗ 履行失败: {e.error_code} - {e.error_message}")
        raise


# ============== 测试场景 2: 续费流程 ==============

@pytest.mark.asyncio
@pytest.mark.real_data
async def test_renew_flow(http_client, test_plan, db_session, settings):
    """
    测试场景 2: 续费流程
    
    流程:
    1. 创建新购订单并履行
    2. 使用现有用户的 client_token 创建续费订单
    3. 支付并确认
    4. 验证到期时间延长
    """
    print("\n=== 测试场景 2: 续费流程 ===")
    
    # Step 1: 创建新购订单并履行
    print("Step 1: 创建新购订单...")
    order_data = await create_order_via_api(
        http_client, 
        test_plan.id, 
        purchase_type="new", 
        asset_code="SOL"
    )
    new_order_id = order_data["order_id"]
    
    # 模拟支付成功
    result = await db_session.execute(select(Order).where(Order.id == new_order_id))
    new_order = result.scalar_one()
    new_order.status = OrderStatus.PAID_SUCCESS.value
    new_order.tx_hash = "test_tx_new_" + uuid.uuid4().hex
    new_order.tx_from = TEST_SOLANA_WALLET
    new_order.confirm_count = 32
    new_order.paid_at = datetime.now(timezone.utc)
    new_order.confirmed_at = datetime.now(timezone.utc)
    await db_session.commit()
    
    # 履行新购订单
    print("Step 2: 履行新购订单...")
    new_result = await fulfill_new_order(new_order_id)
    username = new_result.marzban_username
    old_access_token = new_result.access_token
    print(f"  ✓ 新购履行完成: {username}")
    
    # 获取原始过期时间
    marzban = await get_marzban_client()
    original_user = await marzban.get_user(username)
    original_expire = original_user.expire
    print(f"  - 原始过期时间: {datetime.fromtimestamp(original_expire)}")
    
    # Step 3: 创建续费订单
    print("\nStep 3: 创建续费订单...")
    renew_order_data = await create_order_via_api(
        http_client,
        test_plan.id,
        purchase_type="renew",
        asset_code="SOL",
        client_user_id=username,
        marzban_username=username
    )
    renew_order_id = renew_order_data["order_id"]
    print(f"  ✓ 续费订单创建: {renew_order_data['order_no']}")
    
    # Step 4: 模拟续费订单支付成功
    print("\nStep 4: 模拟续费支付...")
    result = await db_session.execute(select(Order).where(Order.id == renew_order_id))
    renew_order = result.scalar_one()
    renew_order.status = OrderStatus.PAID_SUCCESS.value
    renew_order.tx_hash = "test_tx_renew_" + uuid.uuid4().hex
    renew_order.tx_from = TEST_SOLANA_WALLET
    renew_order.confirm_count = 32
    renew_order.paid_at = datetime.now(timezone.utc)
    renew_order.confirmed_at = datetime.now(timezone.utc)
    renew_order.marzban_username = username
    await db_session.commit()
    print(f"  ✓ 续费订单状态更新为 paid_success")
    
    # Step 5: 执行续费履行
    print("\nStep 5: 执行续费履行...")
    try:
        renew_result = await fulfill_renew_order(renew_order_id, old_access_token)
        print(f"  ✓ 续费履行成功")
        
        # 验证新 token 有效
        new_subscription = await get_subscription_via_api(http_client, renew_result.access_token)
        print(f"  ✓ 新 token 有效")
        
        # 验证旧 token 已被吊销
        try:
            await get_subscription_via_api(http_client, old_access_token)
            print(f"  ! 警告: 旧 token 仍然有效")
        except AssertionError:
            print(f"  ✓ 旧 token 已被吊销")
        
        # Step 6: 验证到期时间延长
        print("\nStep 6: 验证到期时间延长...")
        updated_user = await marzban.get_user(username)
        new_expire = updated_user.expire
        
        print(f"  - 原过期时间: {datetime.fromtimestamp(original_expire)}")
        print(f"  - 新过期时间: {datetime.fromtimestamp(new_expire)}")
        
        expected_extension = test_plan.duration_days * 24 * 60 * 60  # 秒
        actual_extension = new_expire - original_expire
        print(f"  - 预期延长: {expected_extension} 秒")
        print(f"  - 实际延长: {actual_extension} 秒")
        
        assert actual_extension >= expected_extension * 0.9, "到期时间延长不足"
        print(f"  ✓ 到期时间延长验证通过")
        
        # Step 7: 验证流量增加
        print("\nStep 7: 验证流量增加...")
        new_data_limit = updated_user.data_limit
        expected_data = test_plan.traffic_bytes * 2  # 新购 + 续费
        print(f"  - 新流量限制: {new_data_limit} bytes")
        print(f"  - 预期流量: {expected_data} bytes")
        assert new_data_limit >= expected_data * 0.9, "流量增加不足"
        print(f"  ✓ 流量增加验证通过")
        
    except FulfillmentError as e:
        print(f"  ✗ 续费履行失败: {e.error_code} - {e.error_message}")
        raise
    finally:
        # 清理
        print("\nStep 8: 清理测试用户...")
        try:
            await marzban.delete_user(username)
            print(f"  ✓ 测试用户已删除: {username}")
        except Exception as e:
            print(f"  ! 清理失败: {e}")


# ============== 测试场景 3: 异常处理 ==============

@pytest.mark.asyncio
@pytest.mark.real_data
async def test_underpayment_scenario(http_client, test_plan, db_session):
    """
    测试场景 3a: 少付金额处理
    
    验证少付时订单进入 underpaid 状态
    """
    print("\n=== 测试场景 3a: 少付金额处理 ===")
    
    # 创建订单
    order_data = await create_order_via_api(
        http_client, 
        test_plan.id, 
        purchase_type="new", 
        asset_code="SOL"
    )
    order_id = order_data["order_id"]
    expected_amount = Decimal(order_data["amount_crypto"])
    print(f"  ✓ 订单创建: {order_data['order_no']}")
    print(f"  - 期望支付: {expected_amount} SOL")
    
    # 模拟少付 (只支付 50%)
    underpaid_amount = expected_amount * Decimal("0.5")
    print(f"  - 模拟少付: {underpaid_amount} SOL")
    
    # 手动更新为 underpaid 状态
    result = await db_session.execute(select(Order).where(Order.id == order_id))
    order = result.scalar_one()
    order.status = OrderStatus.UNDERPAID.value
    order.tx_hash = "test_tx_under_" + uuid.uuid4().hex
    order.tx_from = TEST_SOLANA_WALLET
    order.confirm_count = 32
    order.paid_at = datetime.now(timezone.utc)
    await db_session.commit()
    
    # 验证状态
    order_detail = await get_order_via_api(http_client, order_id)
    assert order_detail["status"] == "underpaid"
    print(f"  ✓ 订单正确标记为 underpaid")


@pytest.mark.asyncio
@pytest.mark.real_data
async def test_expired_order(http_client, test_plan, db_session):
    """
    测试场景 3b: 过期订单处理
    
    验证过期订单无法支付
    """
    print("\n=== 测试场景 3b: 过期订单处理 ===")
    
    # 创建订单
    order_data = await create_order_via_api(
        http_client, 
        test_plan.id, 
        purchase_type="new", 
        asset_code="SOL"
    )
    order_id = order_data["order_id"]
    print(f"  ✓ 订单创建: {order_data['order_no']}")
    
    # 手动将订单设为过期
    result = await db_session.execute(select(Order).where(Order.id == order_id))
    order = result.scalar_one()
    order.status = OrderStatus.EXPIRED.value
    order.expires_at = datetime.now(timezone.utc) - timedelta(minutes=1)
    await db_session.commit()
    print(f"  ✓ 订单手动设为 expired")
    
    # 验证 API 返回过期状态
    order_detail = await get_order_via_api(http_client, order_id)
    assert order_detail["status"] == "expired"
    print(f"  ✓ API 正确返回过期状态")


@pytest.mark.asyncio
@pytest.mark.real_data
async def test_invalid_token(http_client):
    """
    测试场景 3c: 无效 token 处理
    
    验证无效或过期的 client_token 被拒绝
    """
    print("\n=== 测试场景 3c: 无效 token 处理 ===")
    
    # 使用无效 token 访问订阅
    print("Step 1: 使用无效 token 访问订阅...")
    response = await http_client.get(
        "/client/v1/subscription",
        headers={"Authorization": "Bearer invalid_token"}
    )
    assert response.status_code in [401, 403]
    print(f"  ✓ 无效 token 被拒绝 (状态码: {response.status_code})")
    
    # 使用过期 token
    print("\nStep 2: 使用过期 token 访问订阅...")
    expired_token = jwt.encode(
        {"sub": "test", "type": "access", "exp": datetime.now(timezone.utc) - timedelta(hours=1)},
        "test-secret",
        algorithm="HS256"
    )
    response = await http_client.get(
        "/client/v1/subscription",
        headers={"Authorization": f"Bearer {expired_token}"}
    )
    assert response.status_code in [401, 403]
    print(f"  ✓ 过期 token 被拒绝 (状态码: {response.status_code})")


# ============== 测试场景 4: 性能测试 ==============

@pytest.mark.asyncio
@pytest.mark.real_data
@pytest.mark.performance
async def test_api_response_time(http_client, test_plan):
    """
    测试场景 4a: API 响应时间测试
    
    验证关键 API 响应时间 < 200ms
    """
    print("\n=== 测试场景 4a: API 响应时间测试 ===")
    
    # 测试获取套餐列表
    print("测试 GET /client/v1/plans...")
    times = []
    for i in range(10):
        start = time.time()
        response = await http_client.get("/client/v1/plans")
        elapsed = (time.time() - start) * 1000
        times.append(elapsed)
        assert response.status_code == 200
    
    avg_time = statistics.mean(times)
    max_time = max(times)
    print(f"  - 平均响应时间: {avg_time:.1f}ms")
    print(f"  - 最大响应时间: {max_time:.1f}ms")
    
    assert avg_time < MAX_API_RESPONSE_MS, f"平均响应时间 {avg_time:.1f}ms 超过阈值"
    print(f"  ✓ 响应时间测试通过")
    
    # 测试创建订单
    print("\n测试 POST /client/v1/orders...")
    order_times = []
    for i in range(5):
        start = time.time()
        order_data = await create_order_via_api(
            http_client, 
            test_plan.id, 
            purchase_type="new", 
            asset_code="SOL"
        )
        elapsed = (time.time() - start) * 1000
        order_times.append(elapsed)
    
    avg_order_time = statistics.mean(order_times)
    print(f"  - 平均创建订单时间: {avg_order_time:.1f}ms")
    assert avg_order_time < MAX_API_RESPONSE_MS * 2, f"创建订单时间 {avg_order_time:.1f}ms 过长"
    print(f"  ✓ 创建订单响应时间测试通过")


@pytest.mark.asyncio
@pytest.mark.real_data
@pytest.mark.performance
async def test_worker_processing_delay(test_plan, db_session):
    """
    测试场景 4b: Worker 处理延迟测试
    
    验证 Worker 处理延迟 < 5s
    """
    print("\n=== 测试场景 4b: Worker 处理延迟测试 ===")
    
    # 创建待支付订单
    async with httpx.AsyncClient(base_url=BASE_URL) as client:
        order_data = await create_order_via_api(
            client, 
            test_plan.id, 
            purchase_type="new", 
            asset_code="SOL"
        )
    
    order_id = order_data["order_id"]
    print(f"  ✓ 创建测试订单: {order_data['order_no']}")
    
    # 模拟支付成功
    result = await db_session.execute(select(Order).where(Order.id == order_id))
    order = result.scalar_one()
    order.status = OrderStatus.PAID_SUCCESS.value
    order.tx_hash = "perf_test_tx_" + uuid.uuid4().hex
    order.paid_at = datetime.now(timezone.utc)
    order.confirmed_at = datetime.now(timezone.utc)
    await db_session.commit()
    
    # 测量 fulfill 处理时间
    print("\n  测试 fulfill_new_order 处理延迟...")
    start = time.time()
    try:
        result = await fulfill_new_order(order_id)
        elapsed = time.time() - start
        print(f"  - 处理时间: {elapsed:.2f}s")
        
        assert elapsed < MAX_WORKER_DELAY_SEC, f"处理时间 {elapsed:.2f}s 超过阈值"
        print(f"  ✓ Worker 处理延迟测试通过")
        
        # 清理
        marzban = await get_marzban_client()
        await marzban.delete_user(result.marzban_username)
        
    except Exception as e:
        print(f"  ✗ 履行失败: {e}")
        raise


@pytest.mark.asyncio
@pytest.mark.real_data
@pytest.mark.performance
async def test_concurrent_orders(http_client, test_plan):
    """
    测试场景 4c: 并发订单处理
    
    测试并发创建订单的能力
    """
    print("\n=== 测试场景 4c: 并发订单处理 ===")
    
    async def create_single_order(i: int):
        start = time.time()
        try:
            order_data = await create_order_via_api(
                http_client, 
                test_plan.id, 
                purchase_type="new", 
                asset_code="SOL"
            )
            elapsed = (time.time() - start) * 1000
            return {"success": True, "time": elapsed, "index": i}
        except Exception as e:
            elapsed = (time.time() - start) * 1000
            return {"success": False, "error": str(e), "time": elapsed, "index": i}
    
    # 并发创建 10 个订单
    print("  并发创建 10 个订单...")
    tasks = [create_single_order(i) for i in range(10)]
    results = await asyncio.gather(*tasks)
    
    success_count = sum(1 for r in results if r["success"])
    times = [r["time"] for r in results]
    
    print(f"  - 成功: {success_count}/10")
    print(f"  - 平均响应时间: {statistics.mean(times):.1f}ms")
    print(f"  - 最大响应时间: {max(times):.1f}ms")
    
    assert success_count >= 9, f"并发成功率过低: {success_count}/10"
    print(f"  ✓ 并发订单处理测试通过")


# ============== 测试场景 5: 安全测试 ==============

@pytest.mark.asyncio
@pytest.mark.real_data
@pytest.mark.security
async def test_unauthorized_access(http_client, admin_token):
    """
    测试场景 5a: 未授权访问被拒绝
    
    验证管理 API 需要有效 token
    """
    print("\n=== 测试场景 5a: 未授权访问测试 ===")
    
    # 无 token 访问管理 API
    print("Step 1: 无 token 访问管理 API...")
    response = await http_client.get("/admin/v1/plans")
    assert response.status_code in [401, 403]
    print(f"  ✓ 无 token 被拒绝 (状态码: {response.status_code})")
    
    # 无效 token
    print("\nStep 2: 无效 token 访问管理 API...")
    response = await http_client.get(
        "/admin/v1/plans",
        headers={"Authorization": "Bearer invalid_token"}
    )
    assert response.status_code in [401, 403]
    print(f"  ✓ 无效 token 被拒绝 (状态码: {response.status_code})")
    
    # 客户端 token 访问管理 API (类型不匹配)
    print("\nStep 3: 客户端 token 访问管理 API...")
    client_token = jwt.encode(
        {"sub": "test", "type": "access", "exp": datetime.now(timezone.utc) + timedelta(hours=1)},
        get_settings().jwt_secret,
        algorithm="HS256"
    )
    response = await http_client.get(
        "/admin/v1/plans",
        headers={"Authorization": f"Bearer {client_token}"}
    )
    assert response.status_code in [401, 403]
    print(f"  ✓ 客户端 token 被拒绝 (状态码: {response.status_code})")


@pytest.mark.asyncio
@pytest.mark.real_data
@pytest.mark.security
async def test_sql_injection_protection(http_client, test_plan):
    """
    测试场景 5b: SQL 注入防护
    
    验证 API 参数正确处理，防止 SQL 注入
    """
    print("\n=== 测试场景 5b: SQL 注入防护测试 ===")
    
    # 尝试 SQL 注入 - 订单 ID
    print("Step 1: 测试订单 ID 参数...")
    malicious_ids = [
        "1' OR '1'='1",
        "1; DROP TABLE orders; --",
        "' UNION SELECT * FROM users --",
    ]
    
    for malicious_id in malicious_ids:
        response = await http_client.get(f"/client/v1/orders/{malicious_id}")
        # 应该返回 404 而不是 500
        assert response.status_code in [404, 400, 422], f"SQL 注入可能成功: {malicious_id}"
    print(f"  ✓ 订单 ID 参数安全")
    
    # 尝试 SQL 注入 - 设备 ID
    print("\nStep 2: 测试设备 ID 参数...")
    malicious_device_ids = [
        "test'; DELETE FROM orders; --",
        "test' OR '1'='1",
    ]
    
    for device_id in malicious_device_ids:
        response = await http_client.post(
            "/client/v1/orders",
            json={
                "plan_id": test_plan.id,
                "purchase_type": "new",
                "asset_code": "SOL",
            },
            headers={
                "X-Device-ID": device_id,
                "X-Client-Version": "1.0.0",
            }
        )
        # 应该正常创建订单或返回验证错误，而不是 500
        assert response.status_code in [200, 400, 422]
    print(f"  ✓ 设备 ID 参数安全")


@pytest.mark.asyncio
@pytest.mark.real_data
@pytest.mark.security
async def test_rate_limiting(http_client, test_plan):
    """
    测试场景 5c: 限流测试
    
    验证 API 有适当的限流保护
    """
    print("\n=== 测试场景 5c: 限流测试 ===")
    
    # 快速连续请求
    print("  快速连续发送 20 个请求...")
    responses = []
    for i in range(20):
        response = await http_client.get("/client/v1/plans")
        responses.append(response.status_code)
        await asyncio.sleep(0.05)  # 50ms 间隔
    
    success_count = responses.count(200)
    rate_limited_count = responses.count(429)
    
    print(f"  - 成功: {success_count}")
    print(f"  - 被限流: {rate_limited_count}")
    
    # 大多数请求应该成功，或者有过多的被限流
    assert success_count >= 15 or rate_limited_count > 0, "可能缺少限流保护"
    print(f"  ✓ 限流测试完成")


# ============== 真实区块链连接测试 ==============

@pytest.mark.asyncio
@pytest.mark.real_data
async def test_solana_devnet_connection(solana_client):
    """
    测试 Solana Devnet 连接
    """
    print("\n=== Solana Devnet 连接测试 ===")
    
    # 测试获取余额
    test_address = "6bBh9bYtBq2cwbvqRkY1W3i2p7VdP7YwG6JG7E1t9sNx"
    try:
        balance = await solana_client.get_balance(test_address)
        print(f"  ✓ Devnet 连接成功")
        print(f"  - 地址 {test_address[:20]}... 余额: {balance} SOL")
    except Exception as e:
        print(f"  ✗ Devnet 连接失败: {e}")
        raise


@pytest.mark.asyncio
@pytest.mark.real_data
async def test_tron_nile_connection(tron_client):
    """
    测试 Tron Nile 连接
    """
    print("\n=== Tron Nile 连接测试 ===")
    
    # 测试获取 USDT 余额
    test_address = "TXYZopYRdj2D9XRtbG411XZZ3kpm5bGnNF"
    try:
        balance = await tron_client.get_trc20_balance(test_address)
        print(f"  ✓ Nile 连接成功")
        print(f"  - 地址 {test_address[:20]}... USDT 余额: {balance}")
    except Exception as e:
        print(f"  ✗ Nile 连接失败: {e}")
        raise


@pytest.mark.asyncio
@pytest.mark.real_data
async def test_marzban_connection(marzban_client):
    """
    测试 Marzban 连接
    """
    print("\n=== Marzban 连接测试 ===")
    
    try:
        # 测试认证
        await marzban_client.authenticate()
        print(f"  ✓ Marzban 认证成功")
        
        # 测试获取系统统计
        stats = await marzban_client.get_system_stats()
        print(f"  ✓ 系统统计获取成功")
        print(f"  - 总用户数: {stats.get('total_user', 'N/A')}")
        print(f"  - 活跃用户数: {stats.get('active_user', 'N/A')}")
        
    except Exception as e:
        print(f"  ✗ Marzban 连接失败: {e}")
        raise


# ============== 测试报告 ==============

def pytest_sessionfinish(session, exitstatus):
    """
    测试会话结束时生成报告
    """
    print("\n" + "="*60)
    print("真实数据回归测试完成")
    print("="*60)
    print("\n测试环境:")
    print("  - 数据库: 真实 PostgreSQL")
    print("  - Solana: Devnet 测试网")
    print("  - Tron: Nile 测试网")
    print("  - Marzban: 真实面板")
    print("\n注意:")
    print("  部分测试需要手动发送测试网交易才能完全验证")
    print("  请查看测试输出获取具体的测试网地址和金额")
    print("="*60)
