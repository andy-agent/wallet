# 汇率服务实现完成文档

**任务ID**: liaojiang-jfp  
**任务名称**: 汇率服务实现  
**完成时间**: 2026-03-31  

---

## 1. 修改的文件列表

### 新建文件

| 文件路径 | 说明 |
|---------|------|
| `code/server/app/services/fx_rate.py` | 汇率服务核心模块 |
| `code/server/app/api/client/orders.py` | 客户端订单API（创建订单时调用汇率服务） |
| `code/server/tests/test_fx_rate.py` | 汇率服务单元测试 |

### 修改文件

| 文件路径 | 修改内容 |
|---------|---------|
| `code/server/app/core/exceptions.py` | 添加 `FX_RATE_UNAVAILABLE` 错误码 |

---

## 2. 汇率服务测试命令

### 运行单元测试

```bash
cd /Users/cnyirui/git/projects/liaojiang/code/server

# 安装依赖（如未安装）
pip install -r requirements.txt

# 运行所有汇率服务测试
pytest tests/test_fx_rate.py -v

# 运行特定测试类
pytest tests/test_fx_rate.py::TestFXRateServiceCache -v
pytest tests/test_fx_rate.py::TestFXRateServiceFetch -v
pytest tests/test_fx_rate.py::TestConvertUsdToCrypto -v

# 运行集成测试（需要网络连接）
pytest tests/test_fx_rate.py::TestIntegration -v --no-skip
```

### 手动测试汇率API

```python
import asyncio
from decimal import Decimal
from app.services.fx_rate import (
    FXRateService, 
    convert_usd_to_crypto,
    get_sol_usd_rate,
    get_usdt_usd_rate
)

async def test():
    # 测试获取 SOL/USD 汇率
    sol_rate = await get_sol_usd_rate()
    print(f"SOL/USD: {sol_rate}")
    
    # 测试获取 USDT/USD 汇率
    usdt_rate = await get_usdt_usd_rate()
    print(f"USDT/USD: {usdt_rate}")
    
    # 测试金额转换
    amount, error = await convert_usd_to_crypto(Decimal("10.00"), "SOL")
    print(f"10 USD = {amount} SOL")
    
    # 测试汇率服务
    service = FXRateService()
    rate = await service.get_rate("SOL")
    print(f"Service get_rate: {rate}")

asyncio.run(test())
```

### 测试订单创建接口

```bash
# 启动服务器
uvicorn app.main:app --reload

# 创建订单（使用 SOL）
curl -X POST http://localhost:8000/client/v1/orders \
  -H "Content-Type: application/json" \
  -H "X-Device-ID: test-device-001" \
  -H "X-Client-Version: 1.0.0" \
  -d '{
    "plan_id": "your-plan-id",
    "purchase_type": "new",
    "asset_code": "SOL"
  }'

# 创建订单（使用 USDT_TRC20）
curl -X POST http://localhost:8000/client/v1/orders \
  -H "Content-Type: application/json" \
  -H "X-Device-ID: test-device-001" \
  -H "X-Client-Version: 1.0.0" \
  -d '{
    "plan_id": "your-plan-id",
    "purchase_type": "new",
    "asset_code": "USDT_TRC20"
  }'

# 查询订单
curl http://localhost:8000/client/v1/orders/{order_id}

# 查询订单状态
curl http://localhost:8000/client/v1/orders/{order_id}/status
```

---

## 3. 备用源切换逻辑说明

### 数据源优先级

```
┌─────────────────┐
│   CoinGecko     │ ← 主源（优先）
│   (60s TTL)     │
└────────┬────────┘
         │ 失败
         ▼
┌─────────────────┐
│    Binance      │ ← 备用源
│   (60s TTL)     │
└────────┬────────┘
         │ 失败
         ▼
┌─────────────────┐
│    返回错误      │ ← 所有源失败
└─────────────────┘
```

### 切换逻辑代码

```python
async def _get_rate_with_fallback(self, asset_code: str) -> tuple[Optional[Decimal], str]:
    """
    获取汇率（带故障转移）
    
    1. 首先尝试 CoinGecko（主源）
    2. 失败则切换到 Binance（备用源）
    3. 所有源失败返回 None
    """
    # 首先尝试主源
    rate = await self._fetch_from_coingecko(asset_code)
    if rate is not None:
        return rate, "coingecko"
    
    # 主源失败，尝试备用源
    logger.warning(f"CoinGecko 失败，切换到 Binance")
    rate = await self._fetch_from_binance(asset_code)
    if rate is not None:
        return rate, "binance"
    
    # 所有源都失败
    return None, ""
```

### 故障转移触发条件

| 条件 | 处理方式 |
|-----|---------|
| CoinGecko 请求超时 (>10s) | 自动切换到 Binance |
| CoinGecko HTTP 错误 (4xx/5xx) | 自动切换到 Binance |
| CoinGecko 网络异常 | 自动切换到 Binance |
| Binance 也失败 | 返回 None，订单创建返回 503 错误 |

### 缓存策略

| 配置项 | 值 | 说明 |
|-------|---|------|
| 缓存 TTL | 60 秒 | 避免频繁请求外部 API |
| 缓存级别 | Redis → 内存 | 优先使用 Redis，降级到内存 |
| 缓存键格式 | `fx_rate:{asset}_usd` | 如 `fx_rate:sol_usd` |

---

## 4. 汇率服务核心功能

### 提供的接口

| 函数/方法 | 用途 | 示例 |
|----------|------|------|
| `FXRateService.get_rate(asset)` | 获取指定资产的 USD 汇率 | `await service.get_rate("SOL")` |
| `FXRateService.get_sol_usd_rate()` | 获取 SOL/USD 汇率 | `await service.get_sol_usd_rate()` |
| `FXRateService.get_usdt_usd_rate()` | 获取 USDT/USD 汇率 | `await service.get_usdt_usd_rate()` |
| `convert_usd_to_crypto(amount, asset)` | USD 转加密货币 | `await convert_usd_to_crypto(Decimal("10"), "SOL")` |
| `get_sol_usd_rate()` | 便捷函数：SOL/USD | `await get_sol_usd_rate()` |
| `get_usdt_usd_rate()` | 便捷函数：USDT/USD | `await get_usdt_usd_rate()` |

### 支持的资产

| 资产代码 | CoinGecko ID | Binance Symbol | 精度 |
|---------|-------------|----------------|-----|
| SOL | solana | SOLUSDT | 9位小数 |
| USDT_TRC20 | tether | USDTUSD | 6位小数 |

---

## 5. 订单创建流程

```
客户端请求
    │
    ▼
┌─────────────────────────┐
│ 1. 验证套餐是否存在      │
└──────────┬──────────────┘
           │
           ▼
┌─────────────────────────┐
│ 2. 获取实时汇率          │
│    - 检查缓存（60s TTL） │
│    - 缓存未命中则请求 API│
│    - 主源失败则切换备用  │
└──────────┬──────────────┘
           │
           ▼
┌─────────────────────────┐
│ 3. 计算加密货币金额      │
│    amount_crypto = usd / rate
└──────────┬──────────────┘
           │
           ▼
┌─────────────────────────┐
│ 4. 分配收款地址          │
│    - 从地址池分配        │
│    - 锁定地址给订单      │
└──────────┬──────────────┘
           │
           ▼
┌─────────────────────────┐
│ 5. 创建订单记录          │
│    - 锁定汇率 fx_rate_locked
│    - 设置过期时间        │
└──────────┬──────────────┘
           │
           ▼
    返回订单信息给客户端
```

---

## 6. 异常处理

| 场景 | 返回错误码 | 状态码 | 说明 |
|-----|-----------|-------|------|
| 汇率服务不可用 | `SERVICE_UNAVAILABLE` | 503 | 所有数据源都失败 |
| 金额计算失败 | `INTERNAL_ERROR` | 500 | 系统内部错误 |
| 地址池为空 | `ADDRESS_POOL_EMPTY` | 409 | 支付地址不足 |
| 套餐不存在 | `NOT_FOUND` | 404 | 套餐 ID 无效 |

---

## 7. 配置参数

汇率服务使用以下配置（来自 `app/core/config.py`）：

| 配置项 | 环境变量 | 默认值 | 说明 |
|-------|---------|-------|------|
| `redis_url` | `REDIS_URL` | `redis://localhost:6379/0` | Redis 连接 |
| `order_expire_minutes` | - | 15 | 订单过期时间 |

硬编码配置（`app/services/fx_rate.py`）：

| 配置项 | 值 | 说明 |
|-------|---|------|
| `COINGECKO_TIMEOUT` | 10 秒 | CoinGecko API 超时 |
| `BINANCE_TIMEOUT` | 10 秒 | Binance API 超时 |
| `CACHE_TTL_SECONDS` | 60 秒 | 缓存有效期 |

---

## 8. 日志说明

汇率服务会记录以下日志：

| 级别 | 内容 | 示例 |
|-----|------|------|
| INFO | 汇率获取成功 | `CoinGecko SOL/USD = 145.32` |
| WARNING | 数据源失败/切换 | `CoinGecko 失败，切换到 Binance` |
| ERROR | 汇率获取失败 | `无法获取 SOL/USD 汇率` |
| DEBUG | 缓存命中 | `从 Redis 获取 SOL 汇率: 145.32` |

---

## 9. 后续优化建议

1. **监控告警**: 当汇率服务持续失败时发送告警
2. **更多数据源**: 添加 CoinMarketCap 等第三方数据源
3. **汇率偏差检查**: 当不同数据源汇率偏差过大时告警
4. **预加载缓存**: 启动时预加载热门汇率到缓存
5. **WebSocket 实时汇率**: 对于高频场景考虑 WebSocket 订阅

---

**文档结束**
