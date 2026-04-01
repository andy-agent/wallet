"""
资金归集功能测试
"""
import pytest
from decimal import Decimal
from datetime import datetime, timezone, timedelta

from app.models.sweep_record import SweepRecord, SweepStatus
from app.models.payment_address import PaymentAddress, AddressStatus
from app.models.order import Order


class TestSweepRecord:
    """SweepRecord 模型测试"""
    
    def test_sweep_record_creation(self):
        """测试创建归集记录"""
        sweep = SweepRecord(
            address_id=1,
            order_id="test_order_001",
            chain="solana",
            asset_code="SOL",
            from_address="from_address_123",
            to_address="to_address_456",
            amount=Decimal("1.5"),
            amount_usd=Decimal("150.00"),
            fee_asset="SOL",
            status=SweepStatus.PENDING.value
        )
        
        assert sweep.chain == "solana"
        assert sweep.status == SweepStatus.PENDING.value
        assert sweep.amount == Decimal("1.5")
    
    def test_sweep_status_enum(self):
        """测试状态枚举"""
        assert SweepStatus.PENDING.value == "pending"
        assert SweepStatus.PROCESSING.value == "processing"
        assert SweepStatus.COMPLETED.value == "completed"
        assert SweepStatus.FAILED.value == "failed"
        assert SweepStatus.RETRYING.value == "retrying"


class TestSweeperConfig:
    """归集配置测试"""
    
    def test_config_defaults(self):
        """测试默认配置"""
        from app.core.config import get_settings
        
        settings = get_settings()
        
        # 检查配置存在且有默认值
        assert hasattr(settings, 'sweeper_enabled')
        assert hasattr(settings, 'sweeper_interval_minutes')
        assert hasattr(settings, 'sweep_threshold_usd')
        assert hasattr(settings, 'sweep_reserve_amount_sol')
        assert hasattr(settings, 'sweep_reserve_amount_trx')
        assert hasattr(settings, 'sweep_max_retry_count')
        assert hasattr(settings, 'sweep_retry_delay_minutes')


class TestEncryption:
    """加密模块测试"""
    
    def test_encrypt_decrypt(self):
        """测试加密解密"""
        from app.core.encryption import encrypt_private_key, decrypt_private_key, clear_fernet_cache
        
        # 清除缓存以确保使用正确的 key
        clear_fernet_cache()
        
        original = "test_private_key_12345"
        encrypted = encrypt_private_key(original)
        
        # 加密后应该不同
        assert encrypted != original
        
        # 解密后应该恢复
        decrypted = decrypt_private_key(encrypted)
        assert decrypted == original
    
    def test_encrypt_different_outputs(self):
        """测试加密产生不同输出（Fernet 包含时间戳）"""
        from app.core.encryption import encrypt_private_key, clear_fernet_cache
        
        clear_fernet_cache()
        
        original = "test_key"
        encrypted1 = encrypt_private_key(original)
        
        # 注意：Fernet 加密包含时间戳，每次加密结果不同
        # 这里我们只验证两次加密结果不同
        import time
        time.sleep(0.1)  # 稍微等待确保时间戳不同
        
        encrypted2 = encrypt_private_key(original)
        
        # 加密结果应该不同（因为包含时间戳）
        assert encrypted1 != encrypted2
        
        # 但解密后应该相同
        from app.core.encryption import decrypt_private_key
        decrypted1 = decrypt_private_key(encrypted1)
        decrypted2 = decrypt_private_key(encrypted2)
        assert decrypted1 == decrypted2 == original


@pytest.mark.asyncio
class TestSweeperFunctions:
    """归集功能测试"""
    
    async def test_get_config(self):
        """测试获取配置"""
        from app.workers.sweeper import _get_config, _clear_config_cache
        
        _clear_config_cache()
        config = _get_config()
        
        assert "enabled" in config
        assert "interval_minutes" in config
        assert "threshold_usd" in config
        assert "max_retry_count" in config
    
    async def test_estimate_sweep_fee_solana(self):
        """测试 Solana 手续费估算"""
        from app.workers.sweeper import _estimate_sweep_fee
        
        fee_info = await _estimate_sweep_fee("solana", "SOL")
        
        assert fee_info["fee_asset"] == "SOL"
        assert fee_info["estimated_fee"] > 0
        assert fee_info["reserve_amount"] > 0
    
    async def test_estimate_sweep_fee_tron(self):
        """测试 Tron 手续费估算"""
        from app.workers.sweeper import _estimate_sweep_fee
        
        fee_info = await _estimate_sweep_fee("tron", "USDT_TRC20")
        
        assert fee_info["fee_asset"] == "TRX"
        assert fee_info["estimated_fee"] > 0
        assert fee_info["reserve_amount"] > 0
    
    async def test_calculate_sweep_amount_below_threshold(self):
        """测试低于阈值时不归集"""
        from app.workers.sweeper import _calculate_sweep_amount
        
        # 余额低于阈值
        result = await _calculate_sweep_amount("solana", "SOL", Decimal("0.5"))
        assert result is None
    
    async def test_calculate_sweep_amount_above_threshold(self):
        """测试高于阈值时计算归集金额"""
        from app.workers.sweeper import _calculate_sweep_amount
        
        # 余额高于阈值
        result = await _calculate_sweep_amount("solana", "SOL", Decimal("10.0"))
        assert result is not None
        assert result > 0


class TestSweepScheduler:
    """调度器集成测试"""
    
    def test_scheduler_imports_sweeper(self):
        """测试调度器能正确导入 sweeper"""
        from app.workers.scheduler import start_scheduler, get_job_status
        from app.workers.sweeper import run_sweeper_cycle
        
        # 确保函数可导入且存在
        assert callable(run_sweeper_cycle)
        assert callable(start_scheduler)
