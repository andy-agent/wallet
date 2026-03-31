"""
Tron 集成测试 - Tron Integration Tests

验证:
1. TRC20 转账事件查询
2. USDT 合约交互
3. 确认数计算
4. Mock 模式功能
5. 金额精度处理（6位小数）
6. 并发安全性
"""
import pytest
import asyncio
from decimal import Decimal
from datetime import datetime, timezone

from app.integrations.tron import (
    TronClient,
    TRC20Transfer,
    PaymentDetectionResult,
    USDT_CONTRACT_MAINNET,
    USDT_CONTRACT_NILE,
    create_tron_client,
    get_tron_client_from_config,
)


class TestTronClientMockMode:
    """测试 Tron 客户端 Mock 模式"""
    
    @pytest.fixture
    def client(self):
        """创建 Mock 模式的 Tron 客户端"""
        return TronClient(
            rpc_url="https://nile.trongrid.io",
            usdt_contract=USDT_CONTRACT_NILE,
            mock_mode=True
        )
    
    def test_mock_mode_initialization(self, client):
        """测试 Mock 模式初始化"""
        assert client.mock_mode is True
        assert client.decimals == 6  # USDT TRC20 是 6 位小数
        assert client.usdt_contract == USDT_CONTRACT_NILE
        assert client._mock_transfers == []
        assert client._mock_balances == {}
    
    def test_amount_conversion_methods(self, client):
        """测试金额转换方法"""
        # _from_raw_amount: 原始金额 -> 可读金额
        assert client._from_raw_amount(1_000_000) == 1.0
        assert client._from_raw_amount(100) == 0.0001
        assert client._from_raw_amount(1) == 0.000001
        
        # _to_raw_amount: 可读金额 -> 原始金额
        assert client._to_raw_amount(1.0) == 1_000_000
        assert client._to_raw_amount(0.0001) == 100
        assert client._to_raw_amount(0.000001) == 1
    
    @pytest.mark.asyncio
    async def test_mock_get_trc20_balance(self, client):
        """测试 Mock 获取 TRC20 余额"""
        # 默认余额为 0
        balance = await client.get_trc20_balance("addr1")
        assert balance == 0.0
        
        # 设置余额
        client.mock_set_balance("addr1", 100.5)
        balance = await client.get_trc20_balance("addr1")
        assert balance == 100.5
    
    @pytest.mark.asyncio
    async def test_mock_get_trc20_transfers(self, client):
        """测试 Mock 获取 TRC20 转账记录"""
        # 创建转账记录
        transfer1 = TRC20Transfer(
            transaction_id="tx1",
            from_address="from1",
            to_address="addr1",
            amount=100.0,
            token_contract=USDT_CONTRACT_NILE,
            confirmations=10,
            timestamp=datetime.now(timezone.utc)
        )
        transfer2 = TRC20Transfer(
            transaction_id="tx2",
            from_address="from2",
            to_address="addr1",
            amount=50.0,
            token_contract=USDT_CONTRACT_NILE,
            confirmations=20,
            timestamp=datetime.now(timezone.utc)
        )
        transfer3 = TRC20Transfer(
            transaction_id="tx3",
            from_address="from3",
            to_address="addr2",  # 不同地址
            amount=200.0,
            token_contract=USDT_CONTRACT_NILE,
            confirmations=5,
            timestamp=datetime.now(timezone.utc)
        )
        
        client.mock_add_transfer(transfer1)
        client.mock_add_transfer(transfer2)
        client.mock_add_transfer(transfer3)
        
        # 查询 addr1 的转账
        transfers = await client.get_trc20_transfers("addr1", limit=10)
        
        assert len(transfers) == 2
        # 应该按时间倒序排列
        assert transfers[0].transaction_id == "tx2"
        assert transfers[1].transaction_id == "tx1"
    
    @pytest.mark.asyncio
    async def test_mock_get_trc20_transfers_case_insensitive(self, client):
        """测试地址大小写不敏感"""
        transfer = TRC20Transfer(
            transaction_id="tx1",
            from_address="from1",
            to_address="ADDR1",  # 大写
            amount=100.0,
            token_contract=USDT_CONTRACT_NILE,
            confirmations=10,
            timestamp=datetime.now(timezone.utc)
        )
        client.mock_add_transfer(transfer)
        
        # 小写查询
        transfers = await client.get_trc20_transfers("addr1")
        assert len(transfers) == 1
        
        # 大写查询
        transfers = await client.get_trc20_transfers("ADDR1")
        assert len(transfers) == 1


class TestTronPaymentDetection:
    """测试 Tron 支付检测"""
    
    @pytest.fixture
    def client(self):
        return TronClient(
            rpc_url="https://nile.trongrid.io",
            usdt_contract=USDT_CONTRACT_NILE,
            mock_mode=True
        )
    
    @pytest.mark.asyncio
    async def test_detect_payment_found(self, client):
        """测试检测到支付"""
        transfer = TRC20Transfer(
            transaction_id="tx1",
            from_address="from1",
            to_address="addr1",
            amount=100.0,
            token_contract=USDT_CONTRACT_NILE,
            confirmations=20,
            timestamp=datetime.now(timezone.utc)
        )
        client.mock_add_transfer(transfer)
        
        result = await client.detect_payment("addr1", expected_amount=100.0)
        
        assert result is not None
        assert result.found is True
        assert result.tx_hash == "tx1"
        assert result.amount == 100.0
        assert result.from_address == "from1"
        assert result.confirmations == 20
    
    @pytest.mark.asyncio
    async def test_detect_payment_not_found(self, client):
        """测试未检测到支付"""
        result = await client.detect_payment("empty_addr", expected_amount=100.0)
        
        assert result is not None  # Tron 返回一个 found=False 的结果
        assert result.found is False
        assert result.status == "pending"
    
    @pytest.mark.asyncio
    async def test_detect_payment_with_tolerance(self, client):
        """测试带容差的支付检测"""
        transfer = TRC20Transfer(
            transaction_id="tx1",
            from_address="from1",
            to_address="addr1",
            amount=100.5,  # 实际金额
            token_contract=USDT_CONTRACT_NILE,
            confirmations=20,
            timestamp=datetime.now(timezone.utc)
        )
        client.mock_add_transfer(transfer)
        
        # 1% 容差，期望 100.0，实际 100.5（偏差 0.5%），应该匹配
        result = await client.detect_payment(
            "addr1", 
            expected_amount=100.0,
            amount_tolerance=0.01
        )
        assert result is not None
        assert result.found is True
    
    @pytest.mark.asyncio
    async def test_detect_payment_outside_tolerance(self, client):
        """测试超出容差的支付"""
        transfer = TRC20Transfer(
            transaction_id="tx1",
            from_address="from1",
            to_address="addr1",
            amount=105.0,  # 实际金额
            token_contract=USDT_CONTRACT_NILE,
            confirmations=20,
            timestamp=datetime.now(timezone.utc)
        )
        client.mock_add_transfer(transfer)
        
        # 1% 容差，期望 100.0，实际 105.0（偏差 5%），应该不匹配
        result = await client.detect_payment(
            "addr1", 
            expected_amount=100.0,
            amount_tolerance=0.01
        )
        assert result.found is False
    
    @pytest.mark.asyncio
    async def test_detect_payment_min_confirmations(self, client):
        """测试最小确认数要求"""
        transfer = TRC20Transfer(
            transaction_id="tx1",
            from_address="from1",
            to_address="addr1",
            amount=100.0,
            token_contract=USDT_CONTRACT_NILE,
            confirmations=5,  # 只有 5 个确认
            timestamp=datetime.now(timezone.utc)
        )
        client.mock_add_transfer(transfer)
        
        # 要求 10 个确认
        result = await client.detect_payment(
            "addr1", 
            expected_amount=100.0,
            min_confirmations=10
        )
        assert result.found is False
        
        # 要求 5 个确认
        result = await client.detect_payment(
            "addr1", 
            expected_amount=100.0,
            min_confirmations=5
        )
        assert result.found is True


class TestTronPrecision:
    """测试 Tron 金额精度（6位小数）"""
    
    @pytest.fixture
    def client(self):
        return TronClient(
            rpc_url="https://nile.trongrid.io",
            usdt_contract=USDT_CONTRACT_NILE,
            mock_mode=True
        )
    
    @pytest.mark.asyncio
    async def test_usdt_6_decimals_precision(self, client):
        """测试 USDT 6位小数精度"""
        transfer = TRC20Transfer(
            transaction_id="tx1",
            from_address="from1",
            to_address="addr1",
            amount=0.000001,  # 最小精度单位
            token_contract=USDT_CONTRACT_NILE,
            confirmations=20,
            timestamp=datetime.now(timezone.utc)
        )
        client.mock_add_transfer(transfer)
        
        result = await client.detect_payment("addr1", expected_amount=0.000001)
        
        assert result is not None
        assert result.found is True
        assert result.amount == 0.000001
    
    @pytest.mark.asyncio
    async def test_large_amount_precision(self, client):
        """测试大金额精度"""
        large_amount = 999999999.999999  # 6位小数
        
        transfer = TRC20Transfer(
            transaction_id="tx1",
            from_address="from1",
            to_address="addr1",
            amount=large_amount,
            token_contract=USDT_CONTRACT_NILE,
            confirmations=20,
            timestamp=datetime.now(timezone.utc)
        )
        client.mock_add_transfer(transfer)
        
        result = await client.detect_payment("addr1", expected_amount=large_amount)
        
        assert result is not None
        assert result.found is True
        assert abs(result.amount - large_amount) < 0.000001


class TestTronConcurrency:
    """测试 Tron 客户端并发安全性"""
    
    @pytest.fixture
    def client(self):
        return TronClient(
            rpc_url="https://nile.trongrid.io",
            usdt_contract=USDT_CONTRACT_NILE,
            mock_mode=True
        )
    
    @pytest.mark.asyncio
    async def test_concurrent_balance_reads(self, client):
        """测试并发余额读取"""
        client.mock_set_balance("addr1", 1000.0)
        
        async def read_balance():
            return await client.get_trc20_balance("addr1")
        
        tasks = [read_balance() for _ in range(10)]
        results = await asyncio.gather(*tasks)
        
        assert all(r == 1000.0 for r in results)
    
    @pytest.mark.asyncio
    async def test_concurrent_payment_detection(self, client):
        """测试并发支付检测"""
        transfer = TRC20Transfer(
            transaction_id="tx1",
            from_address="from1",
            to_address="addr1",
            amount=100.0,
            token_contract=USDT_CONTRACT_NILE,
            confirmations=20,
            timestamp=datetime.now(timezone.utc)
        )
        client.mock_add_transfer(transfer)
        
        async def detect():
            return await client.detect_payment("addr1", expected_amount=100.0)
        
        tasks = [detect() for _ in range(10)]
        results = await asyncio.gather(*tasks)
        
        assert all(r is not None and r.found for r in results)


class TestTronDataClasses:
    """测试 Tron 数据类"""
    
    def test_trc20_transfer_dataclass(self):
        """测试 TRC20Transfer 数据类"""
        now = datetime.now(timezone.utc)
        transfer = TRC20Transfer(
            transaction_id="tx123",
            from_address="from_addr",
            to_address="to_addr",
            amount=100.5,
            token_contract=USDT_CONTRACT_NILE,
            confirmations=15,
            timestamp=now
        )
        
        assert transfer.transaction_id == "tx123"
        assert transfer.from_address == "from_addr"
        assert transfer.to_address == "to_addr"
        assert transfer.amount == 100.5
        assert transfer.confirmations == 15
        assert transfer.timestamp == now
    
    def test_payment_detection_result_dataclass(self):
        """测试 PaymentDetectionResult 数据类"""
        result = PaymentDetectionResult(
            found=True,
            tx_hash="tx123",
            from_address="from",
            amount=100.0,
            confirmations=20,
            status="confirmed"
        )
        
        assert result.found is True
        assert result.tx_hash == "tx123"
        assert result.status == "confirmed"
    
    def test_payment_detection_result_defaults(self):
        """测试 PaymentDetectionResult 默认值"""
        result = PaymentDetectionResult(found=False)
        
        assert result.found is False
        assert result.tx_hash is None
        assert result.from_address is None
        assert result.amount is None
        assert result.confirmations == 0
        assert result.status == "pending"


class TestTronFactoryFunctions:
    """测试 Tron 工厂函数"""
    
    def test_create_tron_client_default(self):
        """测试默认创建客户端"""
        client = create_tron_client(mock_mode=True)
        
        assert client.mock_mode is True
        assert client.usdt_contract == USDT_CONTRACT_NILE  # 默认 Nile 测试网
    
    def test_create_tron_client_mainnet(self):
        """测试创建主网客户端"""
        client = create_tron_client(mock_mode=True, use_mainnet=True)
        
        assert client.mock_mode is True
        assert client.usdt_contract == USDT_CONTRACT_MAINNET
        assert "api.trongrid.io" in client.rpc_url
    
    def test_create_tron_client_custom_params(self):
        """测试创建自定义参数客户端"""
        client = create_tron_client(
            rpc_url="https://custom.tron.io",
            usdt_contract="custom_contract",
            mock_mode=True
        )
        
        assert client.rpc_url == "https://custom.tron.io"
        assert client.usdt_contract == "custom_contract"


class TestTronMockHelpers:
    """测试 Mock 辅助方法"""
    
    def test_mock_helpers_require_mock_mode(self):
        """测试 Mock 辅助方法需要 Mock 模式"""
        client = TronClient(
            rpc_url="https://nile.trongrid.io",
            usdt_contract=USDT_CONTRACT_NILE,
            mock_mode=False  # 非 Mock 模式
        )
        
        transfer = TRC20Transfer(
            transaction_id="tx1",
            from_address="from1",
            to_address="addr1",
            amount=100.0,
            token_contract=USDT_CONTRACT_NILE,
            confirmations=20,
            timestamp=datetime.now(timezone.utc)
        )
        
        with pytest.raises(RuntimeError, match="mock mode"):
            client.mock_add_transfer(transfer)
        
        with pytest.raises(RuntimeError, match="mock mode"):
            client.mock_set_balance("addr1", 100.0)
        
        with pytest.raises(RuntimeError, match="mock mode"):
            client.mock_clear_transfers()
        
        with pytest.raises(RuntimeError, match="mock mode"):
            client.mock_clear_balances()
    
    @pytest.mark.asyncio
    async def test_mock_clear_methods(self):
        """测试 Mock 清除方法"""
        client = TronClient(
            rpc_url="https://nile.trongrid.io",
            usdt_contract=USDT_CONTRACT_NILE,
            mock_mode=True
        )
        
        # 添加数据
        transfer = TRC20Transfer(
            transaction_id="tx1",
            from_address="from1",
            to_address="addr1",
            amount=100.0,
            token_contract=USDT_CONTRACT_NILE,
            confirmations=20,
            timestamp=datetime.now(timezone.utc)
        )
        client.mock_add_transfer(transfer)
        client.mock_set_balance("addr1", 100.0)
        
        # 清除转账
        client.mock_clear_transfers()
        transfers = await client.get_trc20_transfers("addr1")
        assert len(transfers) == 0
        
        # 余额应该还在
        balance = await client.get_trc20_balance("addr1")
        assert balance == 100.0
        
        # 清除余额
        client.mock_clear_balances()
        balance = await client.get_trc20_balance("addr1")
        assert balance == 0.0


class TestTronClientLifecycle:
    """测试 Tron 客户端生命周期"""
    
    @pytest.mark.asyncio
    async def test_client_close_mock_mode(self):
        """测试 Mock 模式关闭"""
        client = TronClient(
            rpc_url="https://nile.trongrid.io",
            usdt_contract=USDT_CONTRACT_NILE,
            mock_mode=True
        )
        
        # Mock 模式下关闭应该安全
        await client.close()
    
    @pytest.mark.asyncio
    async def test_client_context_manager(self):
        """测试异步上下文管理器"""
        client = TronClient(
            rpc_url="https://nile.trongrid.io",
            usdt_contract=USDT_CONTRACT_NILE,
            mock_mode=True
        )
        
        async with client:
            balance = await client.get_trc20_balance("test")
            assert isinstance(balance, float)


class TestTronConstants:
    """测试 Tron 常量"""
    
    def test_usdt_contract_addresses(self):
        """测试 USDT 合约地址"""
        # 主网合约地址
        assert USDT_CONTRACT_MAINNET == "TR7NHqjeKQxGTCi8q8ZY4pL8otSzgjLj6t"
        assert len(USDT_CONTRACT_MAINNET) == 34  # Tron 地址长度
        
        # Nile 测试网合约地址
        assert USDT_CONTRACT_NILE == "TXYZopYRdj2D9XRtbG411XZZ3kpm5bGnNF"
        assert len(USDT_CONTRACT_NILE) == 34
