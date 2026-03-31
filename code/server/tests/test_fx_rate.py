"""
汇率服务测试

测试范围：
1. 缓存机制
2. 汇率获取
3. 故障转移
4. USD 到加密货币转换
"""
import asyncio
import pytest
from decimal import Decimal
from datetime import datetime, timezone
from unittest.mock import AsyncMock, patch, MagicMock

from app.services.fx_rate import (
    FXRateService,
    RateCacheEntry,
    convert_usd_to_crypto,
    get_sol_usd_rate,
    get_usdt_usd_rate,
)


class TestRateCacheEntry:
    """测试缓存条目"""
    
    def test_cache_entry_creation(self):
        """测试创建缓存条目"""
        entry = RateCacheEntry(
            rate=Decimal("145.50"),
            timestamp=datetime.now(timezone.utc),
            source="coingecko"
        )
        assert entry.rate == Decimal("145.50")
        assert entry.source == "coingecko"


class TestFXRateServiceCache:
    """测试汇率服务缓存机制"""
    
    @pytest.fixture
    def service(self):
        return FXRateService()
    
    @pytest.mark.asyncio
    async def test_memory_cache_set_and_get(self, service):
        """测试内存缓存设置和获取"""
        entry = RateCacheEntry(
            rate=Decimal("150.00"),
            timestamp=datetime.now(timezone.utc),
            source="test"
        )
        
        # 设置缓存
        await service._set_cached_rate("SOL", entry)
        
        # 获取缓存
        cached = await service._get_cached_rate("SOL")
        
        assert cached is not None
        assert cached.rate == Decimal("150.00")
        assert cached.source == "test"
    
    @pytest.mark.asyncio
    async def test_cache_expiration(self, service):
        """测试缓存过期"""
        # 创建一个过期的缓存条目
        expired_time = datetime.now(timezone.utc) - timedelta(seconds=120)
        entry = RateCacheEntry(
            rate=Decimal("150.00"),
            timestamp=expired_time,
            source="test"
        )
        
        # 设置到内存缓存
        service._memory_cache["fx_rate:sol_usd"] = entry
        
        # 应该返回 None（缓存已过期）
        cached = await service._get_cached_rate("SOL")
        assert cached is None
    
    @pytest.mark.asyncio
    async def test_cache_validity(self, service):
        """测试缓存有效性检查"""
        # 有效缓存（30秒前）
        valid_entry = RateCacheEntry(
            rate=Decimal("150.00"),
            timestamp=datetime.now(timezone.utc) - timedelta(seconds=30),
            source="test"
        )
        assert service._is_cache_valid(valid_entry) is True
        
        # 过期缓存（70秒前）
        expired_entry = RateCacheEntry(
            rate=Decimal("150.00"),
            timestamp=datetime.now(timezone.utc) - timedelta(seconds=70),
            source="test"
        )
        assert service._is_cache_valid(expired_entry) is False


class TestFXRateServiceFetch:
    """测试汇率获取"""
    
    @pytest.fixture
    def service(self):
        return FXRateService()
    
    @pytest.mark.asyncio
    async def test_fetch_from_coingecko_success(self, service):
        """测试从 CoinGecko 成功获取汇率"""
        mock_response = MagicMock()
        mock_response.json.return_value = {
            "solana": {"usd": 145.32, "last_updated_at": 1234567890}
        }
        mock_response.raise_for_status = MagicMock()
        
        with patch("httpx.AsyncClient.get", return_value=mock_response):
            rate = await service._fetch_from_coingecko("SOL")
            
        assert rate is not None
        assert rate == Decimal("145.32")
    
    @pytest.mark.asyncio
    async def test_fetch_from_coingecko_failure(self, service):
        """测试 CoinGecko 失败时返回 None"""
        with patch("httpx.AsyncClient.get", side_effect=Exception("Network error")):
            rate = await service._fetch_from_coingecko("SOL")
            
        assert rate is None
    
    @pytest.mark.asyncio
    async def test_fetch_from_binance_success(self, service):
        """测试从 Binance 成功获取汇率"""
        mock_response = MagicMock()
        mock_response.json.return_value = {"symbol": "SOLUSDT", "price": "145.50000000"}
        mock_response.raise_for_status = MagicMock()
        
        with patch("httpx.AsyncClient.get", return_value=mock_response):
            rate = await service._fetch_from_binance("SOL")
            
        assert rate is not None
        assert rate == Decimal("145.50000000")
    
    @pytest.mark.asyncio
    async def test_fetch_from_binance_usdt_default(self, service):
        """测试 Binance 无法获取 USDT 时返回默认值 1.0"""
        from httpx import HTTPStatusError
        
        mock_response = MagicMock()
        mock_response.status_code = 400
        
        error = HTTPStatusError("Bad Request", request=MagicMock(), response=mock_response)
        
        with patch("httpx.AsyncClient.get", side_effect=error):
            rate = await service._fetch_from_binance("USDT_TRC20")
            
        assert rate == Decimal("1.0")
    
    @pytest.mark.asyncio
    async def test_get_rate_with_fallback_primary_success(self, service):
        """测试主源成功时不使用备用源"""
        with patch.object(service, "_fetch_from_coingecko", return_value=Decimal("150.00")) as mock_primary:
            with patch.object(service, "_fetch_from_binance", return_value=Decimal("149.00")) as mock_backup:
                rate, source = await service._get_rate_with_fallback("SOL")
                
        assert rate == Decimal("150.00")
        assert source == "coingecko"
        mock_primary.assert_called_once_with("SOL")
        mock_backup.assert_not_called()
    
    @pytest.mark.asyncio
    async def test_get_rate_with_fallback_to_backup(self, service):
        """测试主源失败时切换到备用源"""
        with patch.object(service, "_fetch_from_coingecko", return_value=None) as mock_primary:
            with patch.object(service, "_fetch_from_binance", return_value=Decimal("149.00")) as mock_backup:
                rate, source = await service._get_rate_with_fallback("SOL")
                
        assert rate == Decimal("149.00")
        assert source == "binance"
        mock_primary.assert_called_once_with("SOL")
        mock_backup.assert_called_once_with("SOL")
    
    @pytest.mark.asyncio
    async def test_get_rate_with_fallback_both_fail(self, service):
        """测试所有源都失败时返回 None"""
        with patch.object(service, "_fetch_from_coingecko", return_value=None):
            with patch.object(service, "_fetch_from_binance", return_value=None):
                rate, source = await service._get_rate_with_fallback("SOL")
                
        assert rate is None
        assert source == ""


class TestConvertUsdToCrypto:
    """测试 USD 到加密货币转换"""
    
    @pytest.mark.asyncio
    async def test_convert_usd_to_sol_success(self):
        """测试成功转换 USD 到 SOL"""
        with patch("app.services.fx_rate.FXRateService.get_rate", return_value=Decimal("145.00")):
            amount, error = await convert_usd_to_crypto(Decimal("29.00"), "SOL")
            
        assert error is None
        assert amount is not None
        # 29 / 145 = 0.2 SOL
        assert amount == Decimal("0.2")
    
    @pytest.mark.asyncio
    async def test_convert_usd_to_usdt_success(self):
        """测试成功转换 USD 到 USDT"""
        with patch("app.services.fx_rate.FXRateService.get_rate", return_value=Decimal("1.0")):
            amount, error = await convert_usd_to_crypto(Decimal("10.00"), "USDT_TRC20")
            
        assert error is None
        assert amount == Decimal("10")
    
    @pytest.mark.asyncio
    async def test_convert_rate_unavailable(self):
        """测试汇率不可用时返回错误"""
        with patch("app.services.fx_rate.FXRateService.get_rate", return_value=None):
            amount, error = await convert_usd_to_crypto(Decimal("10.00"), "SOL")
            
        assert amount is None
        assert error is not None
        assert "无法获取" in error
    
    @pytest.mark.asyncio
    async def test_convert_rate_zero(self):
        """测试汇率为零时返回错误"""
        with patch("app.services.fx_rate.FXRateService.get_rate", return_value=Decimal("0")):
            amount, error = await convert_usd_to_crypto(Decimal("10.00"), "SOL")
            
        assert amount is None
        assert error is not None
        assert "无效" in error
    
    @pytest.mark.asyncio
    async def test_convert_sol_precision(self):
        """测试 SOL 精度（9位小数）"""
        with patch("app.services.fx_rate.FXRateService.get_rate", return_value=Decimal("145.123456789")):
            amount, error = await convert_usd_to_crypto(Decimal("1.00"), "SOL")
            
        assert error is None
        # 检查精度为9位小数
        assert amount == Decimal("0.00689081")
    
    @pytest.mark.asyncio
    async def test_convert_usdt_precision(self):
        """测试 USDT 精度（6位小数）"""
        with patch("app.services.fx_rate.FXRateService.get_rate", return_value=Decimal("1.001")):
            amount, error = await convert_usd_to_crypto(Decimal("1.00"), "USDT_TRC20")
            
        assert error is None
        # 检查精度为6位小数
        assert amount == Decimal("0.999001")


class TestConvenienceFunctions:
    """测试便捷函数"""
    
    @pytest.mark.asyncio
    async def test_get_sol_usd_rate(self):
        """测试获取 SOL/USD 汇率便捷函数"""
        with patch("app.services.fx_rate.FXRateService.get_sol_usd_rate", return_value=Decimal("150.00")):
            rate = await get_sol_usd_rate()
            assert rate == Decimal("150.00")
    
    @pytest.mark.asyncio
    async def test_get_usdt_usd_rate(self):
        """测试获取 USDT/USD 汇率便捷函数"""
        with patch("app.services.fx_rate.FXRateService.get_usdt_usd_rate", return_value=Decimal("1.00")):
            rate = await get_usdt_usd_rate()
            assert rate == Decimal("1.00")
    
    @pytest.mark.asyncio
    async def test_get_usdt_usd_rate_default(self):
        """测试 USDT 汇率失败时返回默认值"""
        with patch("app.services.fx_rate.FXRateService.get_rate", return_value=None):
            rate = await get_usdt_usd_rate()
            assert rate == Decimal("1.0")


class TestIntegration:
    """集成测试（需要网络）"""
    
    @pytest.mark.asyncio
    @pytest.mark.skip(reason="需要网络连接")
    async def test_real_coingecko_api(self):
        """测试真实的 CoinGecko API"""
        service = FXRateService()
        rate = await service._fetch_from_coingecko("SOL")
        
        assert rate is not None
        assert rate > 0
        print(f"实时 SOL/USD 汇率: {rate}")
    
    @pytest.mark.asyncio
    @pytest.mark.skip(reason="需要网络连接")
    async def test_real_binance_api(self):
        """测试真实的 Binance API"""
        service = FXRateService()
        rate = await service._fetch_from_binance("SOL")
        
        assert rate is not None
        assert rate > 0
        print(f"实时 SOL/USD 汇率: {rate}")


# 导入 timedelta 用于测试
from datetime import timedelta
