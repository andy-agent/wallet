"""
真实区块链节点连接测试 - Real Blockchain Connection Tests

测试目标:
1. Solana 真实节点连接
2. Tron 真实节点连接
3. 真实交易检测功能
4. 确认数计算

使用真实 RPC 节点，不使用 Mock 模式
"""
import pytest
import asyncio
import os
from decimal import Decimal
from datetime import datetime, timezone

# 设置环境变量确保不使用 Mock 模式
os.environ["SOLANA_MOCK_MODE"] = "false"
os.environ["TRON_MOCK_MODE"] = "false"

from app.integrations.solana import SolanaClient, Transaction
from app.integrations.tron import TronClient, TRC20Transfer, USDT_CONTRACT_NILE
from app.core.config import get_settings


# ============================================
# 测试钱包地址 - Test Wallets
# ============================================

# Solana Devnet 测试钱包
# 这些地址在 Devnet 上有交易历史
SOLANA_TEST_ADDRESSES = [
    "9xQeWvG816bUx9EPjHmaT23yvVM2ZWbrrpZb9PusVFin",  # Devnet 活跃地址
    "vines1vzrYbzLMRdu58ou5XTby4qAqVRLmqo36NdECU",  # 系统程序地址
]

# Tron Nile 测试钱包
# 这些地址在 Nile 测试网上有 USDT 交易历史
TRON_TEST_ADDRESSES = [
    "TV6MuMXfmLbBqPZvBHdwFsDnQAePKC2yU5",  # Nile 活跃地址
    "THC4eP4zKRp9vR5bVkeq3kZFVaKQx6hGHj",  # 合约地址
]


# ============================================
# Solana 真实节点测试
# ============================================

@pytest.fixture
async def solana_real_client():
    """创建 Solana 真实客户端"""
    settings = get_settings()
    print(f"\n[SOLANA] RPC URL: {settings.solana_rpc_url}")
    print(f"[SOLANA] Mock Mode: {settings.solana_mock_mode}")
    
    client = SolanaClient(
        rpc_url=settings.solana_rpc_url,
        mock_mode=False  # 强制不使用 Mock
    )
    yield client
    await client.close()


class TestSolanaRealConnection:
    """Solana 真实节点连接测试"""
    
    @pytest.mark.asyncio
    async def test_solana_node_connection(self):
        """测试 Solana 节点连接"""
        settings = get_settings()
        
        print(f"\n{'='*60}")
        print("SOLANA 真实节点连接测试")
        print(f"{'='*60}")
        print(f"RPC URL: {settings.solana_rpc_url}")
        print(f"Mock Mode: {settings.solana_mock_mode}")
        
        # 验证配置
        assert settings.solana_mock_mode is False, "必须使用真实节点，禁用 Mock 模式"
        assert "solana.com" in settings.solana_rpc_url, "必须使用官方 Solana RPC"
        
        client = SolanaClient(
            rpc_url=settings.solana_rpc_url,
            mock_mode=False
        )
        
        try:
            # 测试获取余额 - 使用活跃地址
            test_address = SOLANA_TEST_ADDRESSES[0]
            print(f"\n测试地址: {test_address}")
            
            balance = await client.get_balance(test_address)
            print(f"余额: {balance} SOL")
            
            # 余额应该是数字
            assert isinstance(balance, float), "余额应该是浮点数"
            assert balance >= 0, "余额应该大于等于 0"
            
            print("✅ Solana 节点连接成功!")
            
        finally:
            await client.close()
    
    @pytest.mark.asyncio
    async def test_solana_get_transactions(self):
        """测试获取 Solana 交易历史"""
        settings = get_settings()
        
        print(f"\n{'='*60}")
        print("SOLANA 交易历史查询测试")
        print(f"{'='*60}")
        
        client = SolanaClient(
            rpc_url=settings.solana_rpc_url,
            mock_mode=False
        )
        
        try:
            # 使用活跃地址查询交易
            test_address = SOLANA_TEST_ADDRESSES[0]
            print(f"查询地址: {test_address}")
            
            transactions = await client.get_transactions(test_address, limit=5)
            print(f"找到 {len(transactions)} 笔交易")
            
            # 验证交易数据结构
            for i, tx in enumerate(transactions[:3]):
                print(f"\n交易 {i+1}:")
                print(f"  签名: {tx.signature[:20]}...")
                print(f"  从: {tx.from_address[:15]}...")
                print(f"  到: {tx.to_address[:15]}...")
                print(f"  金额: {tx.amount} SOL")
                print(f"  确认数: {tx.confirmations}")
                print(f"  时间: {tx.timestamp}")
                
                assert isinstance(tx, Transaction), "应该是 Transaction 对象"
                assert tx.signature, "交易应该有签名"
                assert isinstance(tx.amount, float), "金额应该是浮点数"
                assert isinstance(tx.confirmations, int), "确认数应该是整数"
            
            print("\n✅ 交易查询成功!")
            
        finally:
            await client.close()
    
    @pytest.mark.asyncio
    async def test_solana_get_transaction_by_signature(self):
        """测试通过签名获取单笔交易"""
        settings = get_settings()
        
        print(f"\n{'='*60}")
        print("SOLANA 单笔交易查询测试")
        print(f"{'='*60}")
        
        client = SolanaClient(
            rpc_url=settings.solana_rpc_url,
            mock_mode=False
        )
        
        try:
            # 先获取交易列表
            test_address = SOLANA_TEST_ADDRESSES[0]
            transactions = await client.get_transactions(test_address, limit=1)
            
            if not transactions:
                pytest.skip("该地址没有交易记录")
            
            # 获取第一笔交易的签名
            signature = transactions[0].signature
            print(f"查询签名: {signature[:30]}...")
            
            # 通过签名查询交易详情
            tx = await client.get_transaction(signature)
            
            assert tx is not None, "应该找到交易"
            assert tx.signature == signature, "签名应该匹配"
            
            print(f"交易详情:")
            print(f"  从: {tx.from_address}")
            print(f"  到: {tx.to_address}")
            print(f"  金额: {tx.amount} SOL")
            print(f"  确认数: {tx.confirmations}")
            
            print("\n✅ 单笔交易查询成功!")
            
        finally:
            await client.close()
    
    @pytest.mark.asyncio
    async def test_solana_confirmations_calculation(self):
        """测试 Solana 确认数计算"""
        settings = get_settings()
        
        print(f"\n{'='*60}")
        print("SOLANA 确认数计算测试")
        print(f"{'='*60}")
        
        client = SolanaClient(
            rpc_url=settings.solana_rpc_url,
            mock_mode=False
        )
        
        try:
            test_address = SOLANA_TEST_ADDRESSES[0]
            transactions = await client.get_transactions(test_address, limit=10)
            
            print(f"分析 {len(transactions)} 笔交易的确认数:")
            
            for i, tx in enumerate(transactions[:5]):
                print(f"  交易 {i+1}: {tx.confirmations} 确认")
                
                # 确认数应该是非负整数
                assert isinstance(tx.confirmations, int), "确认数应该是整数"
                assert tx.confirmations >= 0, "确认数应该大于等于 0"
                
                # 已确认的交易应该有一定数量的确认
                if tx.confirmations > 0:
                    print(f"    ✅ 已确认交易 ({tx.confirmations} 确认)")
            
            print("\n✅ 确认数计算正确!")
            
        finally:
            await client.close()


# ============================================
# Tron 真实节点测试
# ============================================

@pytest.fixture
async def tron_real_client():
    """创建 Tron 真实客户端"""
    settings = get_settings()
    print(f"\n[TRON] RPC URL: {settings.tron_rpc_url}")
    print(f"[TRON] USDT Contract: {settings.tron_usdt_contract}")
    print(f"[TRON] Mock Mode: {settings.tron_mock_mode}")
    
    client = TronClient(
        rpc_url=settings.tron_rpc_url,
        usdt_contract=settings.tron_usdt_contract,
        mock_mode=False  # 强制不使用 Mock
    )
    yield client
    await client.close()


class TestTronRealConnection:
    """Tron 真实节点连接测试"""
    
    @pytest.mark.asyncio
    async def test_tron_node_connection(self):
        """测试 Tron 节点连接"""
        settings = get_settings()
        
        print(f"\n{'='*60}")
        print("TRON 真实节点连接测试")
        print(f"{'='*60}")
        print(f"RPC URL: {settings.tron_rpc_url}")
        print(f"USDT Contract: {settings.tron_usdt_contract}")
        print(f"Mock Mode: {settings.tron_mock_mode}")
        
        # 验证配置
        assert settings.tron_mock_mode is False, "必须使用真实节点，禁用 Mock 模式"
        assert "trongrid.io" in settings.tron_rpc_url, "必须使用 TronGrid RPC"
        assert settings.tron_usdt_contract == USDT_CONTRACT_NILE, "必须使用 Nile 测试网 USDT 合约"
        
        client = TronClient(
            rpc_url=settings.tron_rpc_url,
            usdt_contract=settings.tron_usdt_contract,
            mock_mode=False
        )
        
        try:
            # 测试获取 USDT 余额
            test_address = TRON_TEST_ADDRESSES[0]
            print(f"\n测试地址: {test_address}")
            
            balance = await client.get_trc20_balance(test_address)
            print(f"USDT 余额: {balance} USDT")
            
            # 余额应该是数字
            assert isinstance(balance, float), "余额应该是浮点数"
            assert balance >= 0, "余额应该大于等于 0"
            
            print("✅ Tron 节点连接成功!")
            
        finally:
            await client.close()
    
    @pytest.mark.asyncio
    async def test_tron_get_trc20_transfers(self):
        """测试获取 Tron TRC20 转账记录"""
        settings = get_settings()
        
        print(f"\n{'='*60}")
        print("TRON TRC20 转账记录查询测试")
        print(f"{'='*60}")
        
        client = TronClient(
            rpc_url=settings.tron_rpc_url,
            usdt_contract=settings.tron_usdt_contract,
            mock_mode=False
        )
        
        try:
            # 使用活跃地址查询转账记录
            test_address = TRON_TEST_ADDRESSES[0]
            print(f"查询地址: {test_address}")
            
            transfers = await client.get_trc20_transfers(test_address, limit=5)
            print(f"找到 {len(transfers)} 笔转账记录")
            
            # 验证转账数据结构
            for i, transfer in enumerate(transfers[:3]):
                print(f"\n转账 {i+1}:")
                print(f"  交易 ID: {transfer.transaction_id[:20]}...")
                print(f"  从: {transfer.from_address[:15]}...")
                print(f"  到: {transfer.to_address[:15]}...")
                print(f"  金额: {transfer.amount} USDT")
                print(f"  确认数: {transfer.confirmations}")
                print(f"  合约: {transfer.token_contract[:15]}...")
                
                assert isinstance(transfer, TRC20Transfer), "应该是 TRC20Transfer 对象"
                assert transfer.transaction_id, "交易应该有 ID"
                assert isinstance(transfer.amount, float), "金额应该是浮点数"
                assert isinstance(transfer.confirmations, int), "确认数应该是整数"
                assert transfer.token_contract == settings.tron_usdt_contract, "合约地址应该匹配"
            
            print("\n✅ TRC20 转账记录查询成功!")
            
        finally:
            await client.close()
    
    @pytest.mark.asyncio
    async def test_tron_confirmations_calculation(self):
        """测试 Tron 确认数计算"""
        settings = get_settings()
        
        print(f"\n{'='*60}")
        print("TRON 确认数计算测试")
        print(f"{'='*60}")
        
        client = TronClient(
            rpc_url=settings.tron_rpc_url,
            usdt_contract=settings.tron_usdt_contract,
            mock_mode=False
        )
        
        try:
            test_address = TRON_TEST_ADDRESSES[0]
            transfers = await client.get_trc20_transfers(test_address, limit=10)
            
            print(f"分析 {len(transfers)} 笔转账的确认数:")
            
            for i, transfer in enumerate(transfers[:5]):
                print(f"  转账 {i+1}: {transfer.confirmations} 确认")
                
                # 确认数应该是非负整数
                assert isinstance(transfer.confirmations, int), "确认数应该是整数"
                assert transfer.confirmations >= 0, "确认数应该大于等于 0"
                
                if transfer.confirmations > 0:
                    print(f"    ✅ 已确认转账 ({transfer.confirmations} 确认)")
            
            print("\n✅ 确认数计算正确!")
            
        finally:
            await client.close()
    
    @pytest.mark.asyncio
    async def test_tron_usdt_precision(self):
        """测试 USDT 金额精度（6位小数）"""
        settings = get_settings()
        
        print(f"\n{'='*60}")
        print("TRON USDT 金额精度测试")
        print(f"{'='*60}")
        
        client = TronClient(
            rpc_url=settings.tron_rpc_url,
            usdt_contract=settings.tron_usdt_contract,
            mock_mode=False
        )
        
        try:
            test_address = TRON_TEST_ADDRESSES[0]
            transfers = await client.get_trc20_transfers(test_address, limit=10)
            
            print(f"检查 {len(transfers)} 笔转账的金额精度:")
            
            for i, transfer in enumerate(transfers[:5]):
                amount_str = str(transfer.amount)
                decimal_places = len(amount_str.split('.')[1]) if '.' in amount_str else 0
                
                print(f"  转账 {i+1}: {transfer.amount} USDT ({decimal_places} 位小数)")
                
                # USDT 应该是 6 位小数精度
                assert decimal_places <= 6, f"USDT 金额不应超过 6 位小数，实际 {decimal_places} 位"
            
            print("\n✅ USDT 金额精度正确!")
            
        finally:
            await client.close()


# ============================================
# 支付检测测试
# ============================================

class TestRealPaymentDetection:
    """真实支付检测测试"""
    
    @pytest.mark.asyncio
    async def test_solana_detect_payment_real(self):
        """测试 Solana 真实支付检测"""
        settings = get_settings()
        
        print(f"\n{'='*60}")
        print("SOLANA 真实支付检测测试")
        print(f"{'='*60}")
        
        client = SolanaClient(
            rpc_url=settings.solana_rpc_url,
            mock_mode=False
        )
        
        try:
            # 使用一个有交易历史的地址
            test_address = SOLANA_TEST_ADDRESSES[0]
            
            # 先获取交易历史
            transactions = await client.get_transactions(test_address, limit=5)
            
            if not transactions:
                pytest.skip("该地址没有交易记录")
            
            # 使用历史交易中的金额进行支付检测
            for tx in transactions[:3]:
                print(f"\n测试检测支付:")
                print(f"  地址: {test_address[:20]}...")
                print(f"  期望金额: {tx.amount} SOL")
                
                # 检测支付
                result = await client.detect_payment(
                    address=test_address,
                    expected_amount=tx.amount
                )
                
                if result and result.found:
                    print(f"  ✅ 检测到支付!")
                    print(f"     交易哈希: {result.tx_hash[:20]}...")
                    print(f"     发送方: {result.from_address[:15]}...")
                    print(f"     金额: {result.amount} SOL")
                    print(f"     确认数: {result.confirmations}")
                    print(f"     状态: {result.status}")
                    
                    assert result.found is True, "应该找到支付"
                    assert result.tx_hash, "应该有交易哈希"
                    assert result.amount > 0, "金额应该大于 0"
                    assert result.confirmations >= 0, "确认数应该非负"
                    break
            else:
                print("  ⚠️ 未找到匹配的支付")
            
            print("\n✅ Solana 支付检测测试完成!")
            
        finally:
            await client.close()
    
    @pytest.mark.asyncio
    async def test_tron_detect_payment_real(self):
        """测试 Tron 真实支付检测"""
        settings = get_settings()
        
        print(f"\n{'='*60}")
        print("TRON 真实支付检测测试")
        print(f"{'='*60}")
        
        client = TronClient(
            rpc_url=settings.tron_rpc_url,
            usdt_contract=settings.tron_usdt_contract,
            mock_mode=False
        )
        
        try:
            # 使用一个有 USDT 交易历史的地址
            test_address = TRON_TEST_ADDRESSES[0]
            
            # 先获取转账历史
            transfers = await client.get_trc20_transfers(test_address, limit=5)
            
            if not transfers:
                pytest.skip("该地址没有 USDT 转账记录")
            
            # 使用历史转账中的金额进行支付检测
            for transfer in transfers[:3]:
                print(f"\n测试检测支付:")
                print(f"  地址: {test_address[:20]}...")
                print(f"  期望金额: {transfer.amount} USDT")
                
                # 检测支付 (使用 1% 容差)
                result = await client.detect_payment(
                    address=test_address,
                    expected_amount=transfer.amount,
                    amount_tolerance=0.01
                )
                
                if result and result.found:
                    print(f"  ✅ 检测到支付!")
                    print(f"     交易哈希: {result.tx_hash[:20]}...")
                    print(f"     发送方: {result.from_address[:15]}...")
                    print(f"     金额: {result.amount} USDT")
                    print(f"     确认数: {result.confirmations}")
                    print(f"     状态: {result.status}")
                    
                    assert result.found is True, "应该找到支付"
                    assert result.tx_hash, "应该有交易哈希"
                    assert result.amount > 0, "金额应该大于 0"
                    assert result.confirmations >= 0, "确认数应该非负"
                    break
            else:
                print("  ⚠️ 未找到匹配的支付")
            
            print("\n✅ Tron 支付检测测试完成!")
            
        finally:
            await client.close()


# ============================================
# 配置验证测试
# ============================================

class TestConfiguration:
    """配置验证测试"""
    
    def test_solana_configuration(self):
        """验证 Solana 配置"""
        settings = get_settings()
        
        print(f"\n{'='*60}")
        print("SOLANA 配置验证")
        print(f"{'='*60}")
        print(f"RPC URL: {settings.solana_rpc_url}")
        print(f"确认数要求: {settings.solana_confirmations}")
        print(f"Mock 模式: {settings.solana_mock_mode}")
        
        # 验证不使用 Mock 模式
        assert settings.solana_mock_mode is False, "SOLANA_MOCK_MODE 必须为 false"
        
        # 验证使用正确的 RPC URL
        assert "solana.com" in settings.solana_rpc_url, "必须使用官方 Solana RPC"
        
        print("\n✅ Solana 配置验证通过!")
    
    def test_tron_configuration(self):
        """验证 Tron 配置"""
        settings = get_settings()
        
        print(f"\n{'='*60}")
        print("TRON 配置验证")
        print(f"{'='*60}")
        print(f"RPC URL: {settings.tron_rpc_url}")
        print(f"USDT 合约: {settings.tron_usdt_contract}")
        print(f"确认数要求: {settings.tron_confirmations}")
        print(f"Mock 模式: {settings.tron_mock_mode}")
        
        # 验证不使用 Mock 模式
        assert settings.tron_mock_mode is False, "TRON_MOCK_MODE 必须为 false"
        
        # 验证使用正确的 RPC URL
        assert "trongrid.io" in settings.tron_rpc_url, "必须使用 TronGrid RPC"
        
        # 验证使用正确的 USDT 合约地址
        assert settings.tron_usdt_contract == USDT_CONTRACT_NILE, "必须使用 Nile 测试网 USDT 合约"
        
        print("\n✅ Tron 配置验证通过!")


# ============================================
# 主运行函数
# ============================================

if __name__ == "__main__":
    """直接运行测试"""
    pytest.main([
        __file__,
        "-v",
        "--tb=short",
        "-s"
    ])
