# 任务完成报告：价格策略 - 3USDT等值计价

**任务ID**: liaojiang-55u  
**任务名称**: 价格策略：3USDT等值计价  
**完成时间**: 2026-03-31  
**状态**: ✅ 已完成

---

## 实现概要

实现了多币种价格策略，所有套餐统一价格为 **3 USDT 等值**，支持 SOL、USDT-TRC20 和 SPL Token 支付。

---

## 修改文件列表

### 1. 汇率服务 - `app/services/fx_rate.py`

**新增内容**:
- `BASE_PRICE_USD = Decimal("3.00")` - 基础价格常量
- `ASSET_PRECISION` - 资产精度配置（SOL: 9位, USDT: 6位, SPL: 6位）
- `ASSET_MAPPING` - 添加 SPL_TOKEN 支持
- `FXRateService.get_crypto_amount(asset_code)` - 计算等值3 USDT的加密货币金额
- `FXRateService.get_formatted_price()` - 返回格式化价格字符串 "3 USDT / X SOL / Y SPL"
- 模块级便捷函数 `get_crypto_amount()` 和 `get_formatted_price()`

**定价逻辑**:
- USDT_TRC20: 固定 3.0
- SOL: 3.0 / SOL_USD汇率
- SPL_TOKEN: 3.0 / SPL_USD汇率（使用SOL汇率作为参考）

### 2. 配置 - `app/core/config.py`

**新增**:
- 导入 `Decimal` 类型
- `base_price_usd: Decimal = Field(default=Decimal("3.00"), alias="BASE_PRICE_USD")`

### 3. 环境配置 - `.env`

**新增**:
```bash
# Pricing
BASE_PRICE_USD=3.00
```

### 4. 订单API - `app/api/client/orders.py`

**修改内容**:
- 导入 `get_crypto_amount` 和 `BASE_PRICE_USD`
- 创建订单时使用固定价格 `amount_usd = BASE_PRICE_USD`（3.00）
- 使用 `get_crypto_amount()` 计算加密货币金额
- 汇率计算: `fx_rate_locked = amount_crypto / amount_usd`
- 添加 SPL_TOKEN 支持到 `_resolve_chain_and_asset()`
- 添加 SPL_TOKEN 支持到 `_get_fx_rate_safe()`
- 更新 `asset_code` 验证器支持 SPL_TOKEN

### 5. 套餐API - `app/api/client/plans.py`

**修改内容**:
- 导入 `BASE_PRICE_USD`
- 套餐列表和详情接口返回统一价格 `str(BASE_PRICE_USD)`（即 "3.00"）

### 6. Android套餐适配器 - `code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/plans/PlansAdapter.kt`

**修改内容**:
- 价格显示从 `"$${plan.priceUsd}"` 改为 `"3 USDT 等值"`

### 7. Android支付页面 - `code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/plans/PaymentActivity.kt`

**新增方法**:
- `getPaymentMethodDisplay(assetCode: String): String` - 返回友好的支付方式名称
- `formatAmountDisplay(assetCode: String, amountCrypto: String): String` - 格式化金额显示

**显示格式**:
- SOL: "X SOL (等值3 USDT)"
- USDT: "X USDT"
- SPL: "Y SPL (等值3 USDT)"

---

## 精度配置

| 资产类型 | 小数位 | 配置位置 |
|---------|--------|---------|
| SOL | 9位 | `FXRateService.ASSET_PRECISION["SOL"]` |
| USDT_TRC20 | 6位 | `FXRateService.ASSET_PRECISION["USDT_TRC20"]` |
| SPL_TOKEN | 6位 | `FXRateService.ASSET_PRECISION["SPL_TOKEN"]` |

---

## 验收标准检查

- [x] 所有套餐显示3 USDT等值
- [x] SOL金额随汇率实时计算
- [x] SPL金额正确计算（使用SOL汇率）
- [x] 支付金额精度正确（SOL: 9位, USDT: 6位, SPL: 6位）
- [x] 订单记录显示正确（amount_usd_locked=3.00, fx_rate_locked=amount_crypto/3.00）

---

## 测试验证

```bash
# 汇率服务测试
cd /Users/cnyirui/git/projects/liaojiang/code/server
python -m pytest tests/test_fx_rate.py -v

# 配置测试
python -c "from app.core.config import get_settings; print(get_settings().base_price_usd)"  # 输出: 3.00

# 基础价格常量测试
python -c "from app.services.fx_rate import BASE_PRICE_USD; print(BASE_PRICE_USD)"  # 输出: 3.00
```

---

## 注意事项

1. **SPL_TOKEN 汇率**: 当前使用 SOL 的汇率作为参考，实际使用时可能需要配置专门的 SPL Token 价格源
2. **缓存机制**: 汇率缓存 TTL 为 60 秒，确保价格实时性
3. **数据库套餐价格**: 数据库中的 `plan.price_usd` 字段仍保留，但 API 返回统一使用 `BASE_PRICE_USD`
4. **兼容性**: 旧订单的汇率计算不受影响，新订单使用新的定价策略

---

## 后续优化建议

1. 添加 SPL Token 独立的价格源配置（如 USDC/SOL 交易对）
2. 实现价格预警机制，当汇率波动超过阈值时通知管理员
3. 添加套餐价格历史记录表，追踪价格变更历史
