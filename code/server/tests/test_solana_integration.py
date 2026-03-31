"""
Solana 集成测试 - Solana Integration Tests

验证:
1. 交易检测逻辑
2. 确认数计算
3. 金额解析精度
4. Mock 模式功能
5. 并发安全性
"""
import pytest
import asyncio
from decimal import Decimal
from datetime import datetime, timezone

from app.integrations.solana import (
    SolanaClient,
    Transaction,
    PaymentDetectionResult,
)


class TestSolanaClientMockMode:
    """测试 Solana 客户端 Mock 模式"""
    
    @pytest.fixture
    def client(self):
        """创建 Mock 模式的 Solana 客户端"""
        return SolanaClient(
            rpc_url="https://api.devnet.solana.com",
            mock_mode=True
        )
    
    def test_mock_mode_initialization(self, client):
        """测试 Mock 模式初始化"""
        assert client.mock_mode is True
        assert client._mock_transactions == {}
        assert client._mock_balances == {}
    
    @pytest.mark.asyncio
    async def test_mock_get_balance_default(self, client):
        """测试 Mock 模式获取默认余额"""
        balance = await client.get_balance("test_address")
        assert isinstance(balance, float)
        assert balance >= 0.1
    
    @pytest.mark.asyncio
    async def test_mock_set_balance(self, client):
        """测试 Mock 模式设置余额"""
        client.mock_set_balance("addr1", 100.5)
        balance = await client.get_balance("addr1")
        assert balance == 100.5
    
    @pytest.mark.asyncio
    async def test_mock_add_incoming_payment(self, client):
        """测试 Mock 添加入账支付"""
        tx = client.mock_add_incoming_payment(
            address="recv_addr",
            from_addr="send_addr",
            amount=1.5,
            confirmations=32
        )
        
        assert isinstance(tx, Transaction)
        assert tx.to_address == "recv_addr"
        assert tx.from_address == "send_addr"
        assert tx.amount == 1.5
        assert tx.confirmations == 32
        assert len(tx.signature) == 87  # Mock 签名长度
        
        # 验证余额更新
        balance = await client.get_balance("recv_addr")
        assert balance == 1.5
    
    @pytest.mark.asyncio
    async def test_mock_get_transactions(self, client):
        """测试 Mock 获取交易列表"""
        # 添加多笔交易
        tx1 = client.mock_add_incoming_payment("addr1", "from1", 1.0, 10)
        tx2 = client.mock_add_incoming_payment("addr1", "from2", 2.0, 20)
        client.mock_add_incoming_payment("addr2", "from3", 3.0, 30)  # 不同地址
        
        transactions = await client.get_transactions("addr1", limit=10)
        
        assert len(transactions) == 2
        # 应该按时间倒序排列
        assert transactions[0].signature == tx2.signature
        assert transactions[1].signature == tx1.signature
    
    @pytest.mark.asyncio
    async def test_mock_get_transaction_by_signature(self, client):
        """测试 Mock 通过签名获取交易"""
        tx = client.mock_add_incoming_payment("addr1", "from1", 1.0)
        
        found_tx = await client.get_transaction(tx.signature)
        
        assert found_tx is not None
        assert found_tx.signature == tx.signature
        assert found_tx.amount == 1.0
    
    @pytest.mark.asyncio
    async def test_mock_get_transaction_not_found(self, client):
        """测试 Mock 获取不存在的交易"""
        tx = await client.get_transaction("nonexistent_signature")
        assert tx is None
    
    @pytest.mark.asyncio
    async def test_mock_clear_data(self, client):
        """测试 Mock 清除数据"""
        client.mock_add_incoming_payment("addr1", "from1", 1.0)
        client.mock_set_balance("addr1", 10.0)
        
        client.mock_clear_data()
        
        assert client._mock_transactions == {}
        assert client._mock_balances == {}


class TestSolanaPaymentDetection:
    """测试 Solana 支付检测"""
    
    @pytest.fixture
    def client(self):
        return SolanaClient(
            rpc_url="https://api.devnet.solana.com",
            mock_mode=True
        )
    
    @pytest.mark.asyncio
    async def test_detect_payment_found(self, client):
        """测试检测到支付"""
        client.mock_add_incoming_payment(
            address="recv_addr",
            from_addr="send_addr",
            amount=1.5,
            confirmations=15
        )
        
        result = await client.detect_payment("recv_addr", expected_amount=1.5)
        
        assert result is not None
        assert result.found is True
        assert result.amount == 1.5
        assert result.from_address == "send_addr"
        assert result.confirmations == 15
        assert result.status == "confirmed"  # >= 12 confirmations
    
    @pytest.mark.asyncio
    async def test_detect_payment_not_found(self, client):
        """测试未检测到支付"""
        result = await client.detect_payment("empty_addr", expected_amount=1.0)
        assert result is None
    
    @pytest.mark.asyncio
    async def test_detect_payment_amount_mismatch(self, client):
        """测试金额不匹配"""
        client.mock_add_incoming_payment("addr1", "from1", 1.0)
        
        # 期望 2.0，实际只有 1.0
        result = await client.detect_payment("addr1", expected_amount=2.0)
        assert result is None
    
    @pytest.mark.asyncio
    async def test_detect_payment_within_tolerance(self, client):
        """测试金额在容差范围内"""
        # 0.1% 容差
        client.mock_add_incoming_payment("addr1", "from1", 1.0005)  # 偏差 0.05%
        
        result = await client.detect_payment("addr1", expected_amount=1.0)
        assert result is not None
        assert result.found is True
    
    @pytest.mark.asyncio
    async def test_detect_payment_outside_tolerance(self, client):
        """测试金额超出容差范围"""
        # 5% 偏差，超出 0.1% 容差
        client.mock_add_incoming_payment("addr1", "from1", 1.05)
        
        result = await client.detect_payment("addr1", expected_amount=1.0)
        assert result is None
    
    @pytest.mark.asyncio
    async def test_detect_payment_with_memo(self, client):
        """测试带 memo 的支付检测"""
        client.mock_add_incoming_payment(
            "addr1", "from1", 1.0, memo="order_123"
        )
        
        # 正确的 memo
        result = await client.detect_payment("addr1", 1.0, memo="order_123")
        assert result is not None
        
        # 错误的 memo
        result = await client.detect_payment("addr1", 1.0, memo="order_456")
        assert result is None
    
    @pytest.mark.asyncio
    async def test_detect_payment_pending_status(self, client):
        """测试待确认状态"""
        client.mock_add_incoming_payment("addr1", "from1", 1.0, confirmations=5)
        
        result = await client.detect_payment("addr1", expected_amount=1.0)
        
        assert result is not None
        assert result.status == "pending"  # < 12 confirmations
    
    @pytest.mark.asyncio
    async def test_detect_payment_confirmed_status(self, client):
        """测试已确认状态"""
        client.mock_add_incoming_payment("addr1", "from1", 1.0, confirmations=12)
        
        result = await client.detect_payment("addr1", expected_amount=1.0)
        
        assert result is not None
        assert result.status == "confirmed"  # >= 12 confirmations


class TestSolanaTransactionParsing:
    """测试交易解析"""
    
    def test_transaction_dataclass(self):
        """测试 Transaction 数据类"""
        tx = Transaction(
            signature="sig123",
            from_address="from_addr",
            to_address="to_addr",
            amount=1.5,
            confirmations=10,
            timestamp=datetime.now(timezone.utc),
            memo="test_memo"
        )
        
        assert tx.signature == "sig123"
        assert tx.amount == 1.5
        assert tx.memo == "test_memo"
    
    def test_payment_detection_result_dataclass(self):
        """测试 PaymentDetectionResult 数据类"""
        result = PaymentDetectionResult(
            found=True,
            tx_hash="tx123",
            from_address="from",
            amount=1.0,
            confirmations=15,
            status="confirmed"
        )
        
        assert result.found is True
        assert result.tx_hash == "tx123"
        assert result.status == "confirmed"


class TestSolanaPrecision:
    """测试 Solana 金额精度"""
    
    @pytest.fixture
    def client(self):
        return SolanaClient(
            rpc_url="https://api.devnet.solana.com",
            mock_mode=True
        )
    
    @pytest.mark.asyncio
    async def test_small_amount_detection(self, client):
        """测试小额支付检测"""
        # 0.000001 SOL (最小精度)
        client.mock_add_incoming_payment("addr1", "from1", 0.000001)
        
        result = await client.detect_payment("addr1", expected_amount=0.000001)
        assert result is not None
        assert result.amount == 0.000001
    
    @pytest.mark.asyncio
    async def test_precision_with_many_decimals(self, client):
        """测试多位小数的精度处理"""
        # Solana 使用 9 位小数 (lamports)
        client.mock_add_incoming_payment("addr1", "from1", 0.123456789)
        
        result = await client.detect_payment("addr1", expected_amount=0.123456789)
        assert result is not None
        assert abs(result.amount - 0.123456789) < 1e-9


class TestSolanaConcurrency:
    """测试 Solana 客户端并发安全性"""
    
    @pytest.fixture
    def client(self):
        return SolanaClient(
            rpc_url="https://api.devnet.solana.com",
            mock_mode=True
        )
    
    @pytest.mark.asyncio
    async def test_concurrent_balance_reads(self, client):
        """测试并发余额读取"""
        client.mock_set_balance("addr1", 100.0)
        
        async def read_balance():
            return await client.get_balance("addr1")
        
        # 并发读取
        tasks = [read_balance() for _ in range(10)]
        results = await asyncio.gather(*tasks)
        
        assert all(r == 100.0 for r in results)
    
    @pytest.mark.asyncio
    async def test_concurrent_payment_detection(self, client):
        """测试并发支付检测"""
        client.mock_add_incoming_payment("addr1", "from1", 1.0)
        
        async def detect():
            return await client.detect_payment("addr1", expected_amount=1.0)
        
        # 并发检测
        tasks = [detect() for _ in range(10)]
        results = await asyncio.gather(*tasks)
        
        assert all(r is not None and r.found for r in results)
    
    @pytest.mark.asyncio
    async def test_concurrent_add_and_read(self, client):
        """测试并发添加和读取"""
        async def add_payment(i):
            client.mock_add_incoming_payment("addr1", f"from{i}", float(i))
            return await client.get_balance("addr1")
        
        # 并发添加多笔支付
        tasks = [add_payment(i) for i in range(5)]
        results = await asyncio.gather(*tasks)
        
        # 所有操作应该成功完成
        assert len(results) == 5
        # 最终余额应该是所有支付的总和
        final_balance = await client.get_balance("addr1")
        assert final_balance == sum(range(5))  # 0+1+2+3+4 = 10


class TestSolanaClientLifecycle:
    """测试 Solana 客户端生命周期"""
    
    @pytest.mark.asyncio
    async def test_client_context_manager(self):
        """测试异步上下文管理器"""
        async with SolanaClient(
            rpc_url="https://api.devnet.solana.com",
            mock_mode=True
        ) as client:
            assert client.mock_mode is True
            balance = await client.get_balance("test")
            assert isinstance(balance, float)
    
    @pytest.mark.asyncio
    async def test_client_close(self):
        """测试客户端关闭"""
        client = SolanaClient(
            rpc_url="https://api.devnet.solana.com",
            mock_mode=True
        )
        await client.close()
        # Mock 模式下关闭应该安全


class TestSolanaMockHelpers:
    """测试 Mock 辅助方法"""
    
    def test_mock_helpers_require_mock_mode(self):
        """测试 Mock 辅助方法需要 Mock 模式"""
        client = SolanaClient(
            rpc_url="https://api.devnet.solana.com",
            mock_mode=False  # 非 Mock 模式
        )
        
        with pytest.raises(RuntimeError, match="mock mode"):
            client.mock_set_balance("addr1", 100.0)
        
        with pytest.raises(RuntimeError, match="mock mode"):
            client.mock_add_incoming_payment("addr1", "from1", 1.0)
        
        with pytest.raises(RuntimeError, match="mock mode"):
            client.mock_clear_data()
