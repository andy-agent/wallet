"""
Ethereum 集成测试 - Ethereum Integration Tests

验证:
1. ERC20 Transfer 事件查询
2. USDT-ERC20 合约交互
3. 确认数计算（基于区块高度）
4. Mock 模式功能
5. 金额精度处理（6位小数）
6. 并发安全性
"""
import pytest
import asyncio
from decimal import Decimal
from datetime import datetime, timezone

from app.integrations.ethereum import (
    EthereumClient,
    ERC20Transfer,
    PaymentDetectionResult,
    USDT_CONTRACT_MAINNET,
    USDT_CONTRACT_SEPOLIA,
    TRANSFER_EVENT_SIGNATURE,
    create_ethereum_client,
    get_ethereum_client_from_config,
)


class TestEthereumClientMockMode:
    """测试 Ethereum 客户端 Mock 模式"""
    
    @pytest.fixture
    def client(self):
        """创建 Mock 模式的 Ethereum 客户端"""
        return EthereumClient(
            rpc_url="https://eth-sepolia.g.alchemy.com/v2/demo",
            usdt_contract=USDT_CONTRACT_SEPOLIA,
            mock_mode=True
        )
    
    def test_mock_mode_initialization(self, client):
        """测试 Mock 模式初始化"""
        assert client.mock_mode is True
        assert client.decimals == 6  # USDT ERC20 是 6 位小数
        assert client.usdt_contract == USDT_CONTRACT_SEPOLIA.lower()
        assert client._mock_transfers == []
        assert client._mock_balances == {}
        assert client._mock_block_number == 1000000
    
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
    async def test_mock_get_block_number(self, client):
        """测试 Mock 获取区块高度"""
        block = await client.get_block_number()
        assert block == 1000000
        
        # 设置新高度
        client.mock_set_block_number(2000000)
        block = await client.get_block_number()
        assert block == 2000000
    
    @pytest.mark.asyncio
    async def test_mock_advance_blocks(self, client):
        """测试 Mock 推进区块"""
        client.mock_set_block_number(1000)
        
        # 推进 1 个区块
        client.mock_advance_blocks(1)
        assert client._mock_block_number == 1001
        
        # 推进多个区块
        client.mock_advance_blocks(10)
        assert client._mock_block_number == 1011
    
    @pytest.mark.asyncio
    async def test_mock_get_erc20_balance(self, client):
        """测试 Mock 获取 ERC20 余额"""
        # 默认余额为 0
        balance = await client.get_erc20_balance("0x1234567890123456789012345678901234567890")
        assert balance == 0.0
        
        # 设置余额
        client.mock_set_balance("0x1234567890123456789012345678901234567890", 100.5)
        balance = await client.get_erc20_balance("0x1234567890123456789012345678901234567890")
        assert balance == 100.5
    
    @pytest.mark.asyncio
    async def test_mock_get_transfer_events(self, client):
        """测试 Mock 获取 ERC20 转账记录"""
        # 创建转账记录
        transfer1 = ERC20Transfer(
            transaction_hash="0x" + "1" * 64,
            from_address="0x" + "a" * 40,
            to_address="0x" + "b" * 40,
            amount=100.0,
            token_contract=USDT_CONTRACT_SEPOLIA,
            confirmations=10,
            timestamp=datetime.now(timezone.utc),
            block_number=1000
        )
        transfer2 = ERC20Transfer(
            transaction_hash="0x" + "2" * 64,
            from_address="0x" + "c" * 40,
            to_address="0x" + "b" * 40,
            amount=50.0,
            token_contract=USDT_CONTRACT_SEPOLIA,
            confirmations=20,
            timestamp=datetime.now(timezone.utc),
            block_number=1001
        )
        transfer3 = ERC20Transfer(
            transaction_hash="0x" + "3" * 64,
            from_address="0x" + "d" * 40,
            to_address="0x" + "e" * 40,  # 不同地址
            amount=200.0,
            token_contract=USDT_CONTRACT_SEPOLIA,
            confirmations=5,
            timestamp=datetime.now(timezone.utc),
            block_number=1002
        )
        
        client.mock_add_transfer(transfer1)
        client.mock_add_transfer(transfer2)
        client.mock_add_transfer(transfer3)
        
        # 查询 addr1 (0xbbbb...) 的转账
        transfers = await client.get_transfer_events("0x" + "b" * 40, limit=10)
        
        assert len(transfers) == 2
        # 应该按时间倒序排列
        assert transfers[0].transaction_hash == "0x" + "2" * 64
        assert transfers[1].transaction_hash == "0x" + "1" * 64
    
    @pytest.mark.asyncio
    async def test_mock_get_transfers_case_insensitive(self, client):
        """测试地址大小写不敏感"""
        transfer = ERC20Transfer(
            transaction_hash="0x" + "1" * 64,
            from_address="0x" + "a" * 40,
            to_address="0x" + "B" * 40,  # 大写
            amount=100.0,
            token_contract=USDT_CONTRACT_SEPOLIA,
            confirmations=10,
            timestamp=datetime.now(timezone.utc),
            block_number=1000
        )
        client.mock_add_transfer(transfer)
        
        # 小写查询
        transfers = await client.get_transfer_events("0x" + "b" * 40)
        assert len(transfers) == 1
        
        # 大写查询
        transfers = await client.get_transfer_events("0x" + "B" * 40)
        assert len(transfers) == 1


class TestEthereumPaymentDetection:
    """测试 Ethereum 支付检测"""
    
    @pytest.fixture
    def client(self):
        return EthereumClient(
            rpc_url="https://eth-sepolia.g.alchemy.com/v2/demo",
            usdt_contract=USDT_CONTRACT_SEPOLIA,
            mock_mode=True
        )
    
    @pytest.mark.asyncio
    async def test_detect_payment_found(self, client):
        """测试检测到支付"""
        transfer = ERC20Transfer(
            transaction_hash="0x" + "1" * 64,
            from_address="0x" + "a" * 40,
            to_address="0x" + "b" * 40,
            amount=100.0,
            token_contract=USDT_CONTRACT_SEPOLIA,
            confirmations=20,
            timestamp=datetime.now(timezone.utc),
            block_number=1000
        )
        client.mock_add_transfer(transfer)
        
        result = await client.detect_payment("0x" + "b" * 40, expected_amount=100.0)
        
        assert result is not None
        assert result.found is True
        assert result.tx_hash == "0x" + "1" * 64
        assert result.amount == 100.0
        assert result.from_address == "0x" + "a" * 40
        assert result.confirmations == 20
    
    @pytest.mark.asyncio
    async def test_detect_payment_not_found(self, client):
        """测试未检测到支付"""
        result = await client.detect_payment("0x" + "f" * 40, expected_amount=100.0)
        
        assert result is not None  # 返回一个 found=False 的结果
        assert result.found is False
        assert result.status == "pending"
    
    @pytest.mark.asyncio
    async def test_detect_payment_with_tolerance(self, client):
        """测试带容差的支付检测"""
        transfer = ERC20Transfer(
            transaction_hash="0x" + "1" * 64,
            from_address="0x" + "a" * 40,
            to_address="0x" + "b" * 40,
            amount=100.5,  # 实际金额
            token_contract=USDT_CONTRACT_SEPOLIA,
            confirmations=20,
            timestamp=datetime.now(timezone.utc),
            block_number=1000
        )
        client.mock_add_transfer(transfer)
        
        # 1% 容差，期望 100.0，实际 100.5（偏差 0.5%），应该匹配
        result = await client.detect_payment(
            "0x" + "b" * 40, 
            expected_amount=100.0,
            amount_tolerance=0.01
        )
        assert result is not None
        assert result.found is True
    
    @pytest.mark.asyncio
    async def test_detect_payment_outside_tolerance(self, client):
        """测试超出容差的支付"""
        transfer = ERC20Transfer(
            transaction_hash="0x" + "1" * 64,
            from_address="0x" + "a" * 40,
            to_address="0x" + "b" * 40,
            amount=105.0,  # 实际金额
            token_contract=USDT_CONTRACT_SEPOLIA,
            confirmations=20,
            timestamp=datetime.now(timezone.utc),
            block_number=1000
        )
        client.mock_add_transfer(transfer)
        
        # 1% 容差，期望 100.0，实际 105.0（偏差 5%），应该不匹配
        result = await client.detect_payment(
            "0x" + "b" * 40, 
            expected_amount=100.0,
            amount_tolerance=0.01
        )
        assert result.found is False
    
    @pytest.mark.asyncio
    async def test_detect_payment_min_confirmations(self, client):
        """测试最小确认数要求"""
        transfer = ERC20Transfer(
            transaction_hash="0x" + "1" * 64,
            from_address="0x" + "a" * 40,
            to_address="0x" + "b" * 40,
            amount=100.0,
            token_contract=USDT_CONTRACT_SEPOLIA,
            confirmations=5,  # 只有 5 个确认
            timestamp=datetime.now(timezone.utc),
            block_number=1000
        )
        client.mock_add_transfer(transfer)
        
        # 要求 10 个确认
        result = await client.detect_payment(
            "0x" + "b" * 40, 
            expected_amount=100.0,
            min_confirmations=10
        )
        assert result.found is False
        
        # 要求 5 个确认
        result = await client.detect_payment(
            "0x" + "b" * 40, 
            expected_amount=100.0,
            min_confirmations=5
        )
        assert result.found is True


class TestEthereumPrecision:
    """测试 Ethereum 金额精度（6位小数）"""
    
    @pytest.fixture
    def client(self):
        return EthereumClient(
            rpc_url="https://eth-sepolia.g.alchemy.com/v2/demo",
            usdt_contract=USDT_CONTRACT_SEPOLIA,
            mock_mode=True
        )
    
    @pytest.mark.asyncio
    async def test_usdt_6_decimals_precision(self, client):
        """测试 USDT 6位小数精度"""
        transfer = ERC20Transfer(
            transaction_hash="0x" + "1" * 64,
            from_address="0x" + "a" * 40,
            to_address="0x" + "b" * 40,
            amount=0.000001,  # 最小精度单位
            token_contract=USDT_CONTRACT_SEPOLIA,
            confirmations=20,
            timestamp=datetime.now(timezone.utc),
            block_number=1000
        )
        client.mock_add_transfer(transfer)
        
        result = await client.detect_payment("0x" + "b" * 40, expected_amount=0.000001)
        
        assert result is not None
        assert result.found is True
        assert result.amount == 0.000001
    
    @pytest.mark.asyncio
    async def test_large_amount_precision(self, client):
        """测试大金额精度"""
        large_amount = 999999999.999999  # 6位小数
        
        transfer = ERC20Transfer(
            transaction_hash="0x" + "1" * 64,
            from_address="0x" + "a" * 40,
            to_address="0x" + "b" * 40,
            amount=large_amount,
            token_contract=USDT_CONTRACT_SEPOLIA,
            confirmations=20,
            timestamp=datetime.now(timezone.utc),
            block_number=1000
        )
        client.mock_add_transfer(transfer)
        
        result = await client.detect_payment("0x" + "b" * 40, expected_amount=large_amount)
        
        assert result is not None
        assert result.found is True
        assert abs(result.amount - large_amount) < 0.000001


class TestEthereumConcurrency:
    """测试 Ethereum 客户端并发安全性"""
    
    @pytest.fixture
    def client(self):
        return EthereumClient(
            rpc_url="https://eth-sepolia.g.alchemy.com/v2/demo",
            usdt_contract=USDT_CONTRACT_SEPOLIA,
            mock_mode=True
        )
    
    @pytest.mark.asyncio
    async def test_concurrent_balance_reads(self, client):
        """测试并发余额读取"""
        client.mock_set_balance("0x" + "a" * 40, 1000.0)
        
        async def read_balance():
            return await client.get_erc20_balance("0x" + "a" * 40)
        
        tasks = [read_balance() for _ in range(10)]
        results = await asyncio.gather(*tasks)
        
        assert all(r == 1000.0 for r in results)
    
    @pytest.mark.asyncio
    async def test_concurrent_payment_detection(self, client):
        """测试并发支付检测"""
        transfer = ERC20Transfer(
            transaction_hash="0x" + "1" * 64,
            from_address="0x" + "a" * 40,
            to_address="0x" + "b" * 40,
            amount=100.0,
            token_contract=USDT_CONTRACT_SEPOLIA,
            confirmations=20,
            timestamp=datetime.now(timezone.utc),
            block_number=1000
        )
        client.mock_add_transfer(transfer)
        
        async def detect():
            return await client.detect_payment("0x" + "b" * 40, expected_amount=100.0)
        
        tasks = [detect() for _ in range(10)]
        results = await asyncio.gather(*tasks)
        
        assert all(r is not None and r.found for r in results)


class TestEthereumDataClasses:
    """测试 Ethereum 数据类"""
    
    def test_erc20_transfer_dataclass(self):
        """测试 ERC20Transfer 数据类"""
        now = datetime.now(timezone.utc)
        transfer = ERC20Transfer(
            transaction_hash="0x" + "1" * 64,
            from_address="0x" + "a" * 40,
            to_address="0x" + "b" * 40,
            amount=100.5,
            token_contract=USDT_CONTRACT_SEPOLIA,
            confirmations=15,
            timestamp=now,
            block_number=1000
        )
        
        assert transfer.transaction_hash == "0x" + "1" * 64
        assert transfer.from_address == "0x" + "a" * 40
        assert transfer.to_address == "0x" + "b" * 40
        assert transfer.amount == 100.5
        assert transfer.confirmations == 15
        assert transfer.timestamp == now
        assert transfer.block_number == 1000
    
    def test_payment_detection_result_dataclass(self):
        """测试 PaymentDetectionResult 数据类"""
        result = PaymentDetectionResult(
            found=True,
            tx_hash="0x" + "1" * 64,
            from_address="0x" + "a" * 40,
            amount=100.0,
            confirmations=20,
            status="confirmed"
        )
        
        assert result.found is True
        assert result.tx_hash == "0x" + "1" * 64
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


class TestEthereumFactoryFunctions:
    """测试 Ethereum 工厂函数"""
    
    def test_create_ethereum_client_default(self):
        """测试默认创建客户端"""
        client = create_ethereum_client(mock_mode=True)
        
        assert client.mock_mode is True
        assert client.usdt_contract == USDT_CONTRACT_SEPOLIA.lower()  # 默认 Sepolia 测试网
    
    def test_create_ethereum_client_mainnet(self):
        """测试创建主网客户端"""
        client = create_ethereum_client(mock_mode=True, use_mainnet=True)
        
        assert client.mock_mode is True
        assert client.usdt_contract == USDT_CONTRACT_MAINNET.lower()
    
    def test_create_ethereum_client_custom_params(self):
        """测试创建自定义参数客户端"""
        client = create_ethereum_client(
            rpc_url="https://custom.eth.io",
            usdt_contract="0x1234567890123456789012345678901234567890",
            mock_mode=True
        )
        
        assert client.rpc_url == "https://custom.eth.io"
        assert client.usdt_contract == "0x1234567890123456789012345678901234567890".lower()


class TestEthereumMockHelpers:
    """测试 Mock 辅助方法"""
    
    def test_mock_helpers_require_mock_mode(self):
        """测试 Mock 辅助方法需要 Mock 模式"""
        client = EthereumClient(
            rpc_url="https://eth-sepolia.g.alchemy.com/v2/demo",
            usdt_contract=USDT_CONTRACT_SEPOLIA,
            mock_mode=False  # 非 Mock 模式
        )
        
        transfer = ERC20Transfer(
            transaction_hash="0x" + "1" * 64,
            from_address="0x" + "a" * 40,
            to_address="0x" + "b" * 40,
            amount=100.0,
            token_contract=USDT_CONTRACT_SEPOLIA,
            confirmations=20,
            timestamp=datetime.now(timezone.utc),
            block_number=1000
        )
        
        with pytest.raises(RuntimeError, match="mock mode"):
            client.mock_add_transfer(transfer)
        
        with pytest.raises(RuntimeError, match="mock mode"):
            client.mock_set_balance("0x" + "a" * 40, 100.0)
        
        with pytest.raises(RuntimeError, match="mock mode"):
            client.mock_clear_transfers()
        
        with pytest.raises(RuntimeError, match="mock mode"):
            client.mock_clear_balances()
    
    @pytest.mark.asyncio
    async def test_mock_clear_methods(self):
        """测试 Mock 清除方法"""
        client = EthereumClient(
            rpc_url="https://eth-sepolia.g.alchemy.com/v2/demo",
            usdt_contract=USDT_CONTRACT_SEPOLIA,
            mock_mode=True
        )
        
        # 添加数据
        transfer = ERC20Transfer(
            transaction_hash="0x" + "1" * 64,
            from_address="0x" + "a" * 40,
            to_address="0x" + "b" * 40,
            amount=100.0,
            token_contract=USDT_CONTRACT_SEPOLIA,
            confirmations=20,
            timestamp=datetime.now(timezone.utc),
            block_number=1000
        )
        client.mock_add_transfer(transfer)
        client.mock_set_balance("0x" + "b" * 40, 100.0)
        
        # 清除转账
        client.mock_clear_transfers()
        transfers = await client.get_transfer_events("0x" + "b" * 40)
        assert len(transfers) == 0
        
        # 余额应该还在
        balance = await client.get_erc20_balance("0x" + "b" * 40)
        assert balance == 100.0
        
        # 清除余额
        client.mock_clear_balances()
        balance = await client.get_erc20_balance("0x" + "b" * 40)
        assert balance == 0.0
    
    @pytest.mark.asyncio
    async def test_mock_create_incoming_transfer(self):
        """测试 Mock 创建转入转账"""
        client = EthereumClient(
            rpc_url="https://eth-sepolia.g.alchemy.com/v2/demo",
            usdt_contract=USDT_CONTRACT_SEPOLIA,
            mock_mode=True
        )
        client.mock_set_block_number(2000)
        
        transfer = client.mock_create_incoming_transfer(
            to_address="0x" + "b" * 40,
            from_address="0x" + "a" * 40,
            amount=500.0,
            confirmations=12
        )
        
        assert transfer.to_address == "0x" + "b" * 40
        assert transfer.from_address == "0x" + "a" * 40
        assert transfer.amount == 500.0
        assert transfer.confirmations == 12
        
        # 验证余额已更新
        balance = await client.get_erc20_balance("0x" + "b" * 40)
        assert balance == 500.0


class TestEthereumClientLifecycle:
    """测试 Ethereum 客户端生命周期"""
    
    @pytest.mark.asyncio
    async def test_client_close_mock_mode(self):
        """测试 Mock 模式关闭"""
        client = EthereumClient(
            rpc_url="https://eth-sepolia.g.alchemy.com/v2/demo",
            usdt_contract=USDT_CONTRACT_SEPOLIA,
            mock_mode=True
        )
        
        # Mock 模式下关闭应该安全
        await client.close()
    
    @pytest.mark.asyncio
    async def test_client_context_manager(self):
        """测试异步上下文管理器"""
        client = EthereumClient(
            rpc_url="https://eth-sepolia.g.alchemy.com/v2/demo",
            usdt_contract=USDT_CONTRACT_SEPOLIA,
            mock_mode=True
        )
        
        async with client:
            balance = await client.get_erc20_balance("0x" + "a" * 40)
            assert isinstance(balance, float)


class TestEthereumConstants:
    """测试 Ethereum 常量"""
    
    def test_usdt_contract_addresses(self):
        """测试 USDT 合约地址"""
        # 主网合约地址
        assert USDT_CONTRACT_MAINNET == "0xdAC17F958D2ee523a2206206994597C13D831ec7"
        assert len(USDT_CONTRACT_MAINNET) == 42  # Ethereum 地址长度（含 0x）
        
        # Sepolia 测试网合约地址
        assert USDT_CONTRACT_SEPOLIA == "0xaA8E23Fb1079EA71e0a56F48a2aA51851D8433D0"
        assert len(USDT_CONTRACT_SEPOLIA) == 42
    
    def test_transfer_event_signature(self):
        """测试 Transfer 事件签名"""
        assert TRANSFER_EVENT_SIGNATURE == "0xddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef"
        assert len(TRANSFER_EVENT_SIGNATURE) == 66  # 32字节哈希 + 0x前缀
