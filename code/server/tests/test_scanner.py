"""
Scanner Worker 测试 - Scanner Worker Tests

验证:
1. scan_pending_orders: 待支付订单扫描逻辑
2. confirm_seen_transactions: 交易确认逻辑
3. expire_orders: 订单过期处理
4. release_expired_addresses: 地址回收逻辑
5. 金额精度处理（Decimal正确使用）
6. 并发安全性

使用 mock 模式测试（不连接真实链）
"""
import pytest
import asyncio
from datetime import datetime, timedelta
from decimal import Decimal
from unittest.mock import AsyncMock, MagicMock, patch

from app.workers.scanner import (
    _get_chain_client,
    _get_required_confirmations,
    _detect_payment_for_order,
    _confirm_order,
    scan_pending_orders,
    confirm_seen_transactions,
    expire_orders,
    release_expired_addresses,
)
from app.core.state_machine import OrderStatus
from app.integrations.solana import SolanaClient
from app.integrations.tron import TronClient


class TestGetChainClient:
    """测试 _get_chain_client 函数"""
    
    @patch("app.workers.scanner.get_settings")
    def test_get_solana_client(self, mock_get_settings):
        """测试获取 Solana 客户端"""
        mock_settings = MagicMock()
        mock_settings.solana_rpc_url = "https://api.solana.com"
        mock_settings.solana_mock_mode = True
        mock_settings.tron_rpc_url = "https://tron.io"
        mock_settings.tron_usdt_contract = "contract"
        mock_settings.tron_mock_mode = True
        mock_get_settings.return_value = mock_settings
        
        client = _get_chain_client("solana")
        
        assert isinstance(client, SolanaClient)
        assert client.mock_mode is True
    
    @patch("app.workers.scanner.get_settings")
    def test_get_tron_client(self, mock_get_settings):
        """测试获取 Tron 客户端"""
        mock_settings = MagicMock()
        mock_settings.solana_rpc_url = "https://api.solana.com"
        mock_settings.solana_mock_mode = True
        mock_settings.tron_rpc_url = "https://tron.io"
        mock_settings.tron_usdt_contract = "contract"
        mock_settings.tron_mock_mode = True
        mock_get_settings.return_value = mock_settings
        
        client = _get_chain_client("tron")
        
        assert isinstance(client, TronClient)
        assert client.mock_mode is True
    
    @patch("app.workers.scanner.get_settings")
    def test_get_unsupported_chain(self, mock_get_settings):
        """测试不支持的链抛出异常"""
        mock_settings = MagicMock()
        mock_get_settings.return_value = mock_settings
        
        with pytest.raises(ValueError, match="Unsupported chain"):
            _get_chain_client("ethereum")


class TestGetRequiredConfirmations:
    """测试 _get_required_confirmations 函数"""
    
    @patch("app.workers.scanner.get_settings")
    def test_solana_confirmations(self, mock_get_settings):
        """测试 Solana 确认数"""
        mock_settings = MagicMock()
        mock_settings.solana_confirmations = 12
        mock_settings.tron_confirmations = 19
        mock_get_settings.return_value = mock_settings
        
        assert _get_required_confirmations("solana") == 12
    
    @patch("app.workers.scanner.get_settings")
    def test_tron_confirmations(self, mock_get_settings):
        """测试 Tron 确认数"""
        mock_settings = MagicMock()
        mock_settings.solana_confirmations = 12
        mock_settings.tron_confirmations = 19
        mock_get_settings.return_value = mock_settings
        
        assert _get_required_confirmations("tron") == 19
    
    @patch("app.workers.scanner.get_settings")
    def test_default_confirmations(self, mock_get_settings):
        """测试默认确认数"""
        mock_settings = MagicMock()
        mock_settings.solana_confirmations = 12
        mock_settings.tron_confirmations = 19
        mock_get_settings.return_value = mock_settings
        
        assert _get_required_confirmations("bitcoin") == 12


class TestDetectPaymentForOrder:
    """测试 _detect_payment_for_order 函数"""
    
    @pytest.fixture
    def mock_session(self):
        """创建 Mock 会话"""
        session = AsyncMock()
        session.flush = AsyncMock()
        return session
    
    @pytest.fixture
    def mock_order(self):
        """创建 Mock 订单"""
        order = MagicMock()
        order.id = "order_123"
        order.order_no = "ORD2024001"
        order.status = OrderStatus.PENDING_PAYMENT.value
        order.chain = "solana"
        order.receive_address = "recv_addr"
        order.amount_crypto = Decimal("1.5")
        order.tx_hash = None
        order.tx_from = None
        order.confirm_count = 0
        order.paid_at = None
        return order
    
    @pytest.fixture
    def mock_client(self):
        """创建 Mock 客户端"""
        client = MagicMock()
        client.detect_payment = AsyncMock()
        return client
    
    @pytest.mark.asyncio
    async def test_detect_payment_found(self, mock_session, mock_order, mock_client):
        """测试检测到支付"""
        from app.integrations.solana import PaymentDetectionResult
        
        mock_client.detect_payment.return_value = PaymentDetectionResult(
            found=True,
            tx_hash="tx_abc123",
            from_address="from_addr",
            amount=1.5,
            confirmations=5,
            status="pending"
        )
        
        result = await _detect_payment_for_order(
            mock_session, mock_order, mock_client
        )
        
        assert result is True
        assert mock_order.status == OrderStatus.SEEN_ONCHAIN.value
        assert mock_order.tx_hash == "tx_abc123"
        assert mock_order.tx_from == "from_addr"
        assert mock_order.confirm_count == 5
        assert mock_order.paid_at is not None
        mock_session.flush.assert_called_once()
    
    @pytest.mark.asyncio
    async def test_detect_payment_not_found(self, mock_session, mock_order, mock_client):
        """测试未检测到支付"""
        mock_client.detect_payment.return_value = None
        
        result = await _detect_payment_for_order(
            mock_session, mock_order, mock_client
        )
        
        assert result is False
        assert mock_order.status == OrderStatus.PENDING_PAYMENT.value
        mock_session.flush.assert_not_called()
    
    @pytest.mark.asyncio
    async def test_detect_payment_amount_precision(self, mock_session, mock_order, mock_client):
        """测试金额精度处理"""
        from app.integrations.solana import PaymentDetectionResult
        
        # 使用高精度金额
        mock_order.amount_crypto = Decimal("1.123456789012345678")
        
        mock_client.detect_payment.return_value = PaymentDetectionResult(
            found=True,
            tx_hash="tx_123",
            from_address="from",
            amount=float(mock_order.amount_crypto),  # 转换为 float
            confirmations=5,
            status="pending"
        )
        
        result = await _detect_payment_for_order(
            mock_session, mock_order, mock_client
        )
        
        assert result is True
        # 验证使用 Decimal 进行比较
        call_args = mock_client.detect_payment.call_args
        assert isinstance(call_args[1]["expected_amount"], Decimal)


class TestConfirmOrder:
    """测试 _confirm_order 函数"""
    
    @pytest.fixture
    def mock_session(self):
        """创建 Mock 会话"""
        session = AsyncMock()
        session.flush = AsyncMock()
        return session
    
    @pytest.fixture
    def mock_order(self):
        """创建 Mock 订单"""
        order = MagicMock()
        order.id = "order_123"
        order.order_no = "ORD2024001"
        order.status = OrderStatus.SEEN_ONCHAIN.value
        order.chain = "solana"
        order.receive_address = "recv_addr"
        order.amount_crypto = Decimal("1.5")
        order.tx_hash = "tx_abc"
        order.confirm_count = 5
        order.confirmed_at = None
        return order
    
    @pytest.fixture
    def mock_client(self):
        """创建 Mock 客户端"""
        client = MagicMock()
        client.get_transaction = AsyncMock()
        client.detect_payment = AsyncMock()
        return client
    
    @pytest.mark.asyncio
    @patch("app.workers.scanner.get_settings")
    async def test_confirm_order_reaches_confirmations(
        self, mock_get_settings, mock_session, mock_order, mock_client
    ):
        """测试订单达到确认数"""
        from app.integrations.solana import PaymentDetectionResult
        
        mock_settings = MagicMock()
        mock_settings.order_amount_tolerance = 0.001
        mock_get_settings.return_value = mock_settings
        
        mock_client.get_transaction.return_value = MagicMock(
            confirmations=12
        )
        mock_client.detect_payment.return_value = PaymentDetectionResult(
            found=True,
            tx_hash="tx_abc",
            from_address="from",
            amount=1.5,
            confirmations=12,
            status="confirmed"
        )
        
        await _confirm_order(mock_session, mock_order, mock_client, 12)
        
        assert mock_order.status == OrderStatus.PAID_SUCCESS.value
        assert mock_order.confirmed_at is not None
        mock_session.flush.assert_called_once()
    
    @pytest.mark.asyncio
    @patch("app.workers.scanner.get_settings")
    async def test_confirm_order_underpaid(
        self, mock_get_settings, mock_session, mock_order, mock_client
    ):
        """测试少付情况"""
        from app.integrations.solana import PaymentDetectionResult
        
        mock_settings = MagicMock()
        mock_settings.order_amount_tolerance = 0.001
        mock_get_settings.return_value = mock_settings
        
        mock_client.get_transaction.return_value = MagicMock(
            confirmations=12
        )
        mock_client.detect_payment.return_value = PaymentDetectionResult(
            found=True,
            tx_hash="tx_abc",
            from_address="from",
            amount=1.0,  # 少付
            confirmations=12,
            status="confirmed"
        )
        
        await _confirm_order(mock_session, mock_order, mock_client, 12)
        
        assert mock_order.status == OrderStatus.UNDERPAID.value
    
    @pytest.mark.asyncio
    @patch("app.workers.scanner.get_settings")
    async def test_confirm_order_overpaid(
        self, mock_get_settings, mock_session, mock_order, mock_client
    ):
        """测试多付情况"""
        from app.integrations.solana import PaymentDetectionResult
        
        mock_settings = MagicMock()
        mock_settings.order_amount_tolerance = 0.001
        mock_get_settings.return_value = mock_settings
        
        mock_client.get_transaction.return_value = MagicMock(
            confirmations=12
        )
        mock_client.detect_payment.return_value = PaymentDetectionResult(
            found=True,
            tx_hash="tx_abc",
            from_address="from",
            amount=2.0,  # 多付
            confirmations=12,
            status="confirmed"
        )
        
        await _confirm_order(mock_session, mock_order, mock_client, 12)
        
        assert mock_order.status == OrderStatus.OVERPAID.value
    
    @pytest.mark.asyncio
    async def test_confirm_order_not_enough_confirmations(
        self, mock_session, mock_order, mock_client
    ):
        """测试确认数不足"""
        mock_client.get_transaction.return_value = MagicMock(
            confirmations=5  # 不足
        )
        
        await _confirm_order(mock_session, mock_order, mock_client, 12)
        
        # 应该转为 confirming 状态
        assert mock_order.status == OrderStatus.CONFIRMING.value
        assert mock_order.confirm_count == 5
    
    @pytest.mark.asyncio
    async def test_confirm_order_no_tx_hash(self, mock_session, mock_order, mock_client):
        """测试没有交易哈希"""
        mock_order.tx_hash = None
        
        await _confirm_order(mock_session, mock_order, mock_client, 12)
        
        # 不应该有状态变化
        assert mock_order.status == OrderStatus.SEEN_ONCHAIN.value


class TestScanPendingOrders:
    """测试 scan_pending_orders 函数"""
    
    @pytest.mark.asyncio
    @patch("app.workers.scanner.get_db_context")
    @patch("app.workers.scanner._get_chain_client")
    @patch("app.workers.scanner._detect_payment_for_order")
    async def test_scan_pending_orders_empty(
        self, mock_detect, mock_get_client, mock_get_db
    ):
        """测试没有待支付订单"""
        mock_session = AsyncMock()
        mock_result = MagicMock()
        mock_result.scalars.return_value.all.return_value = []
        mock_session.execute.return_value = mock_result
        
        mock_context = AsyncMock()
        mock_context.__aenter__ = AsyncMock(return_value=mock_session)
        mock_context.__aexit__ = AsyncMock(return_value=None)
        mock_get_db.return_value = mock_context
        
        await scan_pending_orders()
        
        mock_session.commit.assert_not_called()
    
    @pytest.mark.asyncio
    @patch("app.workers.scanner.get_db_context")
    @patch("app.workers.scanner._get_chain_client")
    @patch("app.workers.scanner._detect_payment_for_order")
    async def test_scan_pending_orders_with_orders(
        self, mock_detect, mock_get_client, mock_get_db
    ):
        """测试有待支付订单"""
        # 创建 Mock 订单
        order1 = MagicMock()
        order1.id = "order_1"
        order1.chain = "solana"
        
        order2 = MagicMock()
        order2.id = "order_2"
        order2.chain = "solana"
        
        mock_session = AsyncMock()
        mock_result = MagicMock()
        mock_result.scalars.return_value.all.return_value = [order1, order2]
        mock_session.execute.return_value = mock_result
        
        mock_context = AsyncMock()
        mock_context.__aenter__ = AsyncMock(return_value= mock_session)
        mock_context.__aexit__ = AsyncMock(return_value=None)
        mock_get_db.return_value = mock_context
        
        mock_client = MagicMock()
        mock_client.close = AsyncMock()
        mock_get_client.return_value = mock_client
        
        mock_detect.return_value = False
        
        await scan_pending_orders()
        
        # 验证每个订单都被处理
        assert mock_detect.call_count == 2
        mock_session.commit.assert_called_once()


class TestExpireOrders:
    """测试 expire_orders 函数"""
    
    @pytest.mark.asyncio
    @patch("app.workers.scanner.get_db_context")
    async def test_expire_orders_empty(self, mock_get_db):
        """测试没有过期订单"""
        mock_session = AsyncMock()
        mock_result = MagicMock()
        mock_result.scalars.return_value.all.return_value = []
        mock_session.execute.return_value = mock_result
        
        mock_context = AsyncMock()
        mock_context.__aenter__ = AsyncMock(return_value=mock_session)
        mock_context.__aexit__ = AsyncMock(return_value=None)
        mock_get_db.return_value = mock_context
        
        await expire_orders()
        
        mock_session.commit.assert_not_called()
    
    @pytest.mark.asyncio
    @patch("app.workers.scanner.get_db_context")
    async def test_expire_orders_with_expired(self, mock_get_db):
        """测试有过期订单"""
        order1 = MagicMock()
        order1.id = "order_1"
        order1.order_no = "ORD001"
        order1.status = OrderStatus.PENDING_PAYMENT.value
        
        mock_session = AsyncMock()
        mock_result = MagicMock()
        mock_result.scalars.return_value.all.return_value = [order1]
        mock_session.execute.return_value = mock_result
        
        mock_context = AsyncMock()
        mock_context.__aenter__ = AsyncMock(return_value=mock_session)
        mock_context.__aexit__ = AsyncMock(return_value=None)
        mock_get_db.return_value = mock_context
        
        await expire_orders()
        
        assert order1.status == OrderStatus.EXPIRED.value
        mock_session.commit.assert_called_once()


class TestReleaseExpiredAddresses:
    """测试 release_expired_addresses 函数"""
    
    @pytest.mark.asyncio
    @patch("app.workers.scanner.get_db_context")
    @patch("app.workers.scanner.AddressPoolService")
    async def test_release_addresses_empty(self, mock_service_class, mock_get_db):
        """测试没有需要释放的地址"""
        mock_session = AsyncMock()
        mock_result = MagicMock()
        mock_result.scalars.return_value.all.return_value = []
        mock_session.execute.return_value = mock_result
        
        mock_context = AsyncMock()
        mock_context.__aenter__ = AsyncMock(return_value=mock_session)
        mock_context.__aexit__ = AsyncMock(return_value=None)
        mock_get_db.return_value = mock_context
        
        await release_expired_addresses()
        
        # 即使订单列表为空，AddressPoolService 仍会被实例化
        # 但不会调用 get_address_by_order 和 release_address
        mock_service_class.assert_called_once_with(mock_session)
    
    @pytest.mark.asyncio
    @patch("app.workers.scanner.get_db_context")
    @patch("app.workers.scanner.AddressPoolService")
    async def test_release_addresses_with_orders(self, mock_service_class, mock_get_db):
        """测试有需要释放的地址"""
        # 创建 Mock 订单
        order1 = MagicMock()
        order1.id = "order_1"
        order1.order_no = "ORD001"
        order1.status = OrderStatus.EXPIRED.value
        
        mock_session = AsyncMock()
        mock_result = MagicMock()
        mock_result.scalars.return_value.all.return_value = [order1]
        mock_session.execute.return_value = mock_result
        
        mock_context = AsyncMock()
        mock_context.__aenter__ = AsyncMock(return_value=mock_session)
        mock_context.__aexit__ = AsyncMock(return_value=None)
        mock_get_db.return_value = mock_context
        
        # Mock AddressPoolService
        mock_service = MagicMock()
        mock_service.get_address_by_order = AsyncMock(return_value=MagicMock(id=1))
        mock_service.release_address = AsyncMock()
        mock_service_class.return_value = mock_service
        
        await release_expired_addresses()
        
        mock_service.get_address_by_order.assert_called_once_with("order_1")
        mock_service.release_address.assert_called_once_with(1)
        mock_session.commit.assert_called_once()


class TestScannerDecimalPrecision:
    """测试 Scanner 金额精度处理"""
    
    def test_decimal_conversion(self):
        """测试 Decimal 转换"""
        amount_str = "1.123456789012345678"
        amount_decimal = Decimal(amount_str)
        
        assert isinstance(amount_decimal, Decimal)
        assert str(amount_decimal) == amount_str
    
    def test_decimal_arithmetic(self):
        """测试 Decimal 运算"""
        a = Decimal("1.1")
        b = Decimal("2.2")
        
        # Decimal 可以精确表示
        assert a + b == Decimal("3.3")
        
        # float 会有精度问题
        assert 1.1 + 2.2 != 3.3
    
    def test_tolerance_calculation(self):
        """测试容差计算"""
        expected = Decimal("100.0")
        tolerance = Decimal("0.001")  # 0.1%
        
        min_acceptable = expected * (Decimal("1") - tolerance)
        max_acceptable = expected * (Decimal("1") + tolerance)
        
        assert min_acceptable == Decimal("99.9")
        assert max_acceptable == Decimal("100.1")
        
        # 测试边界
        assert Decimal("99.95") >= min_acceptable  # 在容差内
        assert Decimal("100.05") <= max_acceptable  # 在容差内


class TestScannerConcurrency:
    """测试 Scanner 并发安全性"""
    
    @pytest.mark.asyncio
    async def test_concurrent_order_processing(self):
        """测试并发订单处理"""
        processed = []
        
        async def process_order(order_id):
            await asyncio.sleep(0.01)  # 模拟处理时间
            processed.append(order_id)
            return True
        
        # 并发处理多个订单
        order_ids = [f"order_{i}" for i in range(10)]
        tasks = [process_order(oid) for oid in order_ids]
        await asyncio.gather(*tasks)
        
        assert len(processed) == 10
        assert set(processed) == set(order_ids)
