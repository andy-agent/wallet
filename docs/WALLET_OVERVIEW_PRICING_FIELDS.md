# Wallet Overview Pricing Fields

## Purpose
说明 `wallet/overview` 与 Android 钱包首页、`TokenManager` 的价格/估值字段口径。

## Backend Fields

`GET /api/client/v1/wallet/overview`

顶层新增：

- `totalPortfolioValueUsd`
- `priceUpdatedAt`

每个 `assetItem` 新增：

- `unitPriceUsd`
- `valueUsd`
- `priceChangePct24h`
- `priceStatus`
- `priceUpdatedAt`

`GET /api/client/v1/wallet/balances`

每个 `item` 同样返回：

- `unitPriceUsd`
- `valueUsd`
- `priceChangePct24h`
- `priceStatus`
- `priceUpdatedAt`

## Status Semantics

- `READY`
  - 已拿到真实市场价格
- `FIXED`
  - 固定价资产，例如 `USDT`
- `UNAVAILABLE`
  - 当前没有可用报价

## Pricing Source

- 原生币：走 backend `market` 模块
- 固定价资产：沿用 `usdPriceMode=fixed`
- 地址型 token：走 backend `market` 模块的按地址报价能力

## Cache Policy

- 价格缓存：`60s`
- `wallet/overview?forceRefresh=true`
- `wallet/balances?forceRefresh=true`
  会强制跳过价格缓存

## Android Display Rules

- 钱包首页只展示当前钱包 + 当前链资产
- 顶部估值显示 `totalPortfolioValueUsd`
- 顶部文案显示 `priceUpdatedAt`
- token 行显示：
  - 图标
  - 名称 / 符号
  - 单价
  - 24h 涨跌
  - 持仓数量
  - 持仓估值
- `0` 余额自定义代币仍显示在首页
- `UNAVAILABLE` token 不隐藏，只显示 `暂无报价`
