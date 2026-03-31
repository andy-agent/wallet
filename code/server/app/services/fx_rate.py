"""
汇率服务 - Foreign Exchange Rate Service

提供加密货币与 USD 之间的汇率转换：
- SOL/USD 汇率获取
- USDT/USD 汇率获取（通常为 1.0）
- USD 到加密货币的金额转换

数据源:
- 主源: CoinGecko API
- 备用源: Binance API

缓存机制:
- TTL: 60 秒
- 支持内存缓存或 Redis（优先 Redis）
"""
import asyncio
import logging
from dataclasses import dataclass
from datetime import datetime, timedelta, timezone
from typing import Optional, Dict, Any, Callable
from decimal import Decimal, ROUND_HALF_UP

import httpx
from app.core.config import get_settings

logger = logging.getLogger(__name__)


@dataclass
class RateCacheEntry:
    """汇率缓存条目"""
    rate: Decimal
    timestamp: datetime
    source: str  # 数据来源标识


class FXRateService:
    """
    汇率服务
    
    支持多个数据源，自动故障转移：
    1. CoinGecko API（主源）
    2. Binance API（备用源）
    
    缓存策略：
    - 内存缓存：默认使用
    - Redis 缓存：如果配置了 redis_url 则优先使用
    """
    
    # CoinGecko API 配置
    COINGECKO_BASE_URL = "https://api.coingecko.com/api/v3"
    COINGECKO_TIMEOUT = 10.0
    
    # Binance API 配置
    BINANCE_BASE_URL = "https://api.binance.com/api/v3"
    BINANCE_TIMEOUT = 10.0
    
    # 缓存 TTL（秒）
    CACHE_TTL_SECONDS = 60
    
    # 支持的资产映射
    ASSET_MAPPING = {
        "SOL": {"coingecko_id": "solana", "binance_symbol": "SOLUSDT"},
        "USDT_TRC20": {"coingecko_id": "tether", "binance_symbol": "USDTUSD"},
        "USDT": {"coingecko_id": "tether", "binance_symbol": "USDTUSD"},
    }
    
    def __init__(self):
        self._memory_cache: Dict[str, RateCacheEntry] = {}
        self._settings = get_settings()
        self._redis_client: Optional[Any] = None
        self._redis_available: bool = False
        
        # 尝试初始化 Redis
        self._init_redis()
    
    def _init_redis(self) -> None:
        """尝试初始化 Redis 连接"""
        try:
            import redis.asyncio as redis
            if self._settings.redis_url:
                self._redis_client = redis.from_url(
                    self._settings.redis_url,
                    decode_responses=True
                )
                self._redis_available = True
                logger.info("FXRateService: Redis 缓存已启用")
        except ImportError:
            logger.warning("FXRateService: redis 包未安装，使用内存缓存")
        except Exception as e:
            logger.warning(f"FXRateService: Redis 连接失败，使用内存缓存: {e}")
    
    def _get_cache_key(self, asset_code: str) -> str:
        """生成缓存键"""
        return f"fx_rate:{asset_code.lower()}_usd"
    
    def _is_cache_valid(self, entry: RateCacheEntry) -> bool:
        """检查缓存是否有效"""
        now = datetime.now(timezone.utc)
        age = (now - entry.timestamp).total_seconds()
        return age < self.CACHE_TTL_SECONDS
    
    async def _get_from_redis(self, key: str) -> Optional[RateCacheEntry]:
        """从 Redis 获取缓存"""
        if not self._redis_available or not self._redis_client:
            return None
        
        try:
            import json
            data = await self._redis_client.get(key)
            if data:
                parsed = json.loads(data)
                entry = RateCacheEntry(
                    rate=Decimal(parsed["rate"]),
                    timestamp=datetime.fromisoformat(parsed["timestamp"]),
                    source=parsed["source"]
                )
                if self._is_cache_valid(entry):
                    return entry
        except Exception as e:
            logger.warning(f"FXRateService: Redis 读取失败: {e}")
        return None
    
    async def _set_to_redis(self, key: str, entry: RateCacheEntry) -> None:
        """设置 Redis 缓存"""
        if not self._redis_available or not self._redis_client:
            return
        
        try:
            import json
            data = {
                "rate": str(entry.rate),
                "timestamp": entry.timestamp.isoformat(),
                "source": entry.source
            }
            await self._redis_client.setex(
                key,
                self.CACHE_TTL_SECONDS,
                json.dumps(data)
            )
        except Exception as e:
            logger.warning(f"FXRateService: Redis 写入失败: {e}")
    
    async def _get_cached_rate(self, asset_code: str) -> Optional[RateCacheEntry]:
        """获取缓存的汇率（优先 Redis，其次内存）"""
        cache_key = self._get_cache_key(asset_code)
        
        # 先尝试 Redis
        entry = await self._get_from_redis(cache_key)
        if entry:
            logger.debug(f"FXRateService: 从 Redis 获取 {asset_code} 汇率: {entry.rate}")
            return entry
        
        # 再尝试内存缓存
        entry = self._memory_cache.get(cache_key)
        if entry and self._is_cache_valid(entry):
            logger.debug(f"FXRateService: 从内存获取 {asset_code} 汇率: {entry.rate}")
            return entry
        
        return None
    
    async def _set_cached_rate(self, asset_code: str, entry: RateCacheEntry) -> None:
        """设置缓存汇率（同时更新 Redis 和内存）"""
        cache_key = self._get_cache_key(asset_code)
        
        # 更新内存缓存
        self._memory_cache[cache_key] = entry
        
        # 更新 Redis
        await self._set_to_redis(cache_key, entry)
    
    async def _fetch_from_coingecko(self, asset_code: str) -> Optional[Decimal]:
        """
        从 CoinGecko 获取汇率
        
        Args:
            asset_code: 资产代码 (e.g., 'SOL', 'USDT_TRC20')
            
        Returns:
            Decimal: 汇率，失败返回 None
        """
        mapping = self.ASSET_MAPPING.get(asset_code)
        if not mapping:
            logger.error(f"FXRateService: 不支持的资产代码: {asset_code}")
            return None
        
        coin_id = mapping["coingecko_id"]
        url = f"{self.COINGECKO_BASE_URL}/simple/price"
        
        params = {
            "ids": coin_id,
            "vs_currencies": "usd",
            "include_last_updated_at": "true"
        }
        
        try:
            async with httpx.AsyncClient(timeout=self.COINGECKO_TIMEOUT) as client:
                response = await client.get(url, params=params)
                response.raise_for_status()
                data = response.json()
                
                if coin_id in data and "usd" in data[coin_id]:
                    rate = Decimal(str(data[coin_id]["usd"]))
                    logger.info(f"FXRateService: CoinGecko {asset_code}/USD = {rate}")
                    return rate
                else:
                    logger.error(f"FXRateService: CoinGecko 响应格式异常: {data}")
                    return None
                    
        except httpx.TimeoutException:
            logger.warning(f"FXRateService: CoinGecko 请求超时")
            return None
        except httpx.HTTPStatusError as e:
            logger.warning(f"FXRateService: CoinGecko HTTP 错误: {e.response.status_code}")
            return None
        except Exception as e:
            logger.warning(f"FXRateService: CoinGecko 请求失败: {e}")
            return None
    
    async def _fetch_from_binance(self, asset_code: str) -> Optional[Decimal]:
        """
        从 Binance 获取汇率
        
        Args:
            asset_code: 资产代码 (e.g., 'SOL', 'USDT_TRC20')
            
        Returns:
            Decimal: 汇率，失败返回 None
        """
        mapping = self.ASSET_MAPPING.get(asset_code)
        if not mapping:
            logger.error(f"FXRateService: 不支持的资产代码: {asset_code}")
            return None
        
        symbol = mapping["binance_symbol"]
        url = f"{self.BINANCE_BASE_URL}/ticker/price"
        
        params = {"symbol": symbol}
        
        try:
            async with httpx.AsyncClient(timeout=self.BINANCE_TIMEOUT) as client:
                response = await client.get(url, params=params)
                response.raise_for_status()
                data = response.json()
                
                if "price" in data:
                    rate = Decimal(str(data["price"]))
                    logger.info(f"FXRateService: Binance {asset_code}/USD = {rate}")
                    return rate
                else:
                    logger.error(f"FXRateService: Binance 响应格式异常: {data}")
                    return None
                    
        except httpx.TimeoutException:
            logger.warning(f"FXRateService: Binance 请求超时")
            return None
        except httpx.HTTPStatusError as e:
            # 如果 symbol 不存在，尝试替代方案
            if e.response.status_code == 400 and asset_code == "USDT_TRC20":
                # USDT 对 USD 通常是 1:1
                logger.info(f"FXRateService: Binance 无 USDT/USD，使用默认值 1.0")
                return Decimal("1.0")
            logger.warning(f"FXRateService: Binance HTTP 错误: {e.response.status_code}")
            return None
        except Exception as e:
            logger.warning(f"FXRateService: Binance 请求失败: {e}")
            return None
    
    async def _get_rate_with_fallback(self, asset_code: str) -> tuple[Optional[Decimal], str]:
        """
        获取汇率（带故障转移）
        
        Returns:
            tuple: (汇率, 数据源标识)
        """
        # 首先尝试主源：CoinGecko
        rate = await self._fetch_from_coingecko(asset_code)
        if rate is not None:
            return rate, "coingecko"
        
        # 主源失败，尝试备用源：Binance
        logger.warning(f"FXRateService: CoinGecko 失败，切换到 Binance")
        rate = await self._fetch_from_binance(asset_code)
        if rate is not None:
            return rate, "binance"
        
        # 所有源都失败
        return None, ""
    
    async def get_rate(self, asset_code: str) -> Optional[Decimal]:
        """
        获取指定资产的 USD 汇率
        
        Args:
            asset_code: 资产代码 (e.g., 'SOL', 'USDT_TRC20')
            
        Returns:
            Decimal: 汇率，失败返回 None
            
        Example:
            >>> rate = await service.get_rate("SOL")
            >>> print(rate)  # 例如: 145.32
        """
        # 检查缓存
        cached = await self._get_cached_rate(asset_code)
        if cached:
            logger.debug(f"FXRateService: 使用缓存汇率 {asset_code}/USD = {cached.rate}")
            return cached.rate
        
        # 获取新汇率
        rate, source = await self._get_rate_with_fallback(asset_code)
        
        if rate is not None:
            # 更新缓存
            entry = RateCacheEntry(
                rate=rate,
                timestamp=datetime.now(timezone.utc),
                source=source
            )
            await self._set_cached_rate(asset_code, entry)
        
        return rate
    
    async def get_sol_usd_rate(self) -> Optional[Decimal]:
        """
        获取 SOL/USD 汇率
        
        Returns:
            Decimal: SOL 对 USD 的汇率
        """
        return await self.get_rate("SOL")
    
    async def get_usdt_usd_rate(self) -> Optional[Decimal]:
        """
        获取 USDT/USD 汇率
        
        USDT 理论上与 USD 1:1，但在实际交易中可能有微小偏差
        
        Returns:
            Decimal: USDT 对 USD 的汇率（通常为 1.0 左右）
        """
        rate = await self.get_rate("USDT_TRC20")
        if rate is None:
            # 如果无法获取，返回理论值 1.0
            logger.warning("FXRateService: 无法获取 USDT/USD，使用默认值 1.0")
            return Decimal("1.0")
        return rate


async def convert_usd_to_crypto(amount_usd: Decimal, asset_code: str) -> tuple[Optional[Decimal], Optional[str]]:
    """
    将 USD 金额转换为加密货币金额
    
    Args:
        amount_usd: USD 金额
        asset_code: 目标资产代码 (e.g., 'SOL', 'USDT_TRC20')
        
    Returns:
        tuple: (加密货币金额, 错误信息)
        - 成功: (crypto_amount, None)
        - 失败: (None, error_message)
        
    Example:
        >>> amount, error = await convert_usd_to_crypto(Decimal("10.00"), "SOL")
        >>> if error:
        ...     print(f"转换失败: {error}")
        ... else:
        ...     print(f"需支付 {amount} SOL")
    """
    service = FXRateService()
    
    try:
        # 获取汇率
        rate = await service.get_rate(asset_code)
        
        if rate is None:
            error_msg = f"无法获取 {asset_code}/USD 汇率，请稍后重试"
            logger.error(f"FXRateService: {error_msg}")
            return None, error_msg
        
        if rate <= 0:
            error_msg = f"汇率无效: {rate}"
            logger.error(f"FXRateService: {error_msg}")
            return None, error_msg
        
        # 计算加密货币金额: crypto = usd / rate
        # 使用适当的精度
        if asset_code in ["USDT_TRC20", "USDT"]:
            # USDT 通常保留 6 位小数
            precision = Decimal("0.000001")
        else:
            # SOL 等保留 9 位小数
            precision = Decimal("0.000000001")
        
        crypto_amount = (amount_usd / rate).quantize(precision, rounding=ROUND_HALF_UP)
        
        logger.info(
            f"FXRateService: 转换 {amount_usd} USD -> {crypto_amount} {asset_code} "
            f"(汇率: {rate})"
        )
        
        return crypto_amount, None
        
    except Exception as e:
        error_msg = f"汇率转换异常: {str(e)}"
        logger.exception(f"FXRateService: {error_msg}")
        return None, error_msg


# 便捷函数：获取 SOL/USD 汇率
async def get_sol_usd_rate() -> Optional[Decimal]:
    """获取 SOL/USD 汇率"""
    service = FXRateService()
    return await service.get_sol_usd_rate()


# 便捷函数：获取 USDT/USD 汇率
async def get_usdt_usd_rate() -> Optional[Decimal]:
    """获取 USDT/USD 汇率"""
    service = FXRateService()
    return await service.get_usdt_usd_rate()
