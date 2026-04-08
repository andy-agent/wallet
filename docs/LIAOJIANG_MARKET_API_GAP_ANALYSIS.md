# LIAOJIANG 市场数据 API 差距分析

更新时间：2026-04-08

## 1. 审计范围与判定口径

- 本次只审计当前仓库里与“市场/价格/报价/资产行情”相关的后端与服务端能力，不做代码变更。
- “当前主后端”按 `code/backend`（NestJS）认定，旁证来自 `docs/current-status.md` 和 `code/deploy/BACKEND_DEPLOYMENT.md`。
- `code/sol-agent` 与 `code/backend-chain-usdt` 视为链侧服务，只负责链上查询/广播/健康，不应被误判为市场数据服务。
- `code/server`（FastAPI）里存在历史上的汇率能力，但当前部署文档存在漂移；本次将其标记为“遗留但可复用”，不直接算作当前对外可用 market API。
- 这里的“市场数据”至少包括：资产价格、24h 涨跌、K 线/时间序列、订单报价、报价锁定、行情源健康、行情缓存。

## 2. 结论摘要

1. 当前主 API 没有独立的市场数据域，没有 `/api/client/v1/market/*` 或等价接口。
2. 现有“与市场沾边”的能力只有静态套餐价格、硬编码订单 payable quote、钱包资产目录、链上区块/交易查询。
3. 当前订单报价不是由实时或缓存行情推导出来的，而是直接硬编码。
4. 仓库里唯一接近真实行情聚合的实现，是旧 FastAPI `code/server/app/services/fx_rate.py`；它只服务订单金额换算，没有作为当前 Nest 主后端对外暴露。
5. 下一步应该在主后端 `code/backend` 增加“参考行情 + 报价锁定”能力，而不是去做完整交易所式 order book / trade feed。

## 3. 当前已存在的相关接口与能力

### 3.1 当前真实可见的主后端接口

| 接口 | 当前能力 | 结论 |
|---|---|---|
| `GET /api/client/v1/plans` | 返回套餐 `priceUsd`；当前 `PlansService` 直接写死套餐与价格 | 这是商品定价，不是市场数据 |
| `POST /api/client/v1/orders` | 允许客户端指定 `quoteAssetCode` / `quoteNetworkCode` 创建订单 | 具备“报价入口语义”，但没有真正行情计算 |
| `GET /api/client/v1/orders/{orderNo}/payment-target` | 返回 `networkCode`、`assetCode`、`payableAmount`、收款地址 | 是支付目标接口，不是市场行情接口 |
| `GET /api/client/v1/wallet/chains` | 返回链名、确认数、公开 RPC URL | 是链元数据，不是行情 |
| `GET /api/client/v1/wallet/assets/catalog` | 返回资产 symbol、decimals、contract、orderPayable | 是资产目录，不是价格 feed |
| `POST /api/client/v1/wallet/transfer/precheck` | 远程调用链侧做地址/费用预检 | 是转账预检，不是市场数据 |
| `POST /api/client/v1/wallet/transfer/proxy-broadcast` | 代理广播交易 | 是链交互，不是市场数据 |
| `GET /api/healthz` | 聚合 Solana/TRON 链侧健康状态 | 只有 upstream 健康，不含价格 freshness |

### 3.2 当前已接的链侧接口

| 服务 | 接口 | 当前能力 | 结论 |
|---|---|---|---|
| `sol-agent` | `GET /api/v1/transactions/:signature` | 查询 Solana 交易状态 | 是交易状态，不是市场行情 |
| `backend-chain-usdt` | `GET /api/v1/chain/tx/:hash` | 查询 TRON 交易 | 同上 |
| `backend-chain-usdt` | `GET /api/v1/chain/block/current` | 查询当前区块 | 是链状态，不是市场行情 |
| `backend-chain-usdt` | `GET /api/v1/chain/address/validate` | 地址校验 | 非市场能力 |
| `backend-chain-usdt` | `GET /api/v1/chain/contract/usdt` | 返回 USDT 合约信息 | 资产元数据，不是价格 |
| `backend-chain-usdt` | `GET /api/v1/chain/capabilities` | 返回支持 token、能力、限流 | 服务能力发现，不是行情 |

### 3.3 遗留但可复用的历史能力

| 模块 | 当前状态 | 可复用点 |
|---|---|---|
| `code/server/app/services/fx_rate.py` | 历史 FastAPI 能力，非当前主后端对外接口 | 已有 CoinGecko/Binance 双源、60 秒缓存、USD 转加密货币换算逻辑 |
| `code/server/app/api/client/orders.py` | 历史订单创建链路 | 证明仓库曾有“按汇率生成支付金额”的完整服务端做法 |

### 3.4 当前明确不存在的东西

- 不存在 `market`/`ticker`/`kline`/`candles`/`orderbook` controller 或 module。
- 不存在任何 market provider 的当前主栈环境变量。
- 不存在为 Android 钱包详情页或 Market 页提供真实价格/涨跌的 API。
- 不存在市场源健康、freshness、stale data 的管理端接口。

## 4. 关键证据

### 4.1 主后端当前只有“静态价格”和“硬编码 payable”

- `code/backend/src/modules/plans/plans.service.ts`
  - 套餐 `priceUsd` 为写死值。
- `code/backend/src/modules/orders/orders.service.ts`
  - `quoteUsdAmount` 直接写死为 `9.99`。
  - `payableAmount` 按资产直接写死为 `0.04500000` 或 `9.990000`。
  - 这说明当前的“报价”不是实时算出来的，只是固定映射。

### 4.2 钱包接口只提供资产目录，不提供价格

- `code/backend/src/modules/wallet/wallet.service.ts`
  - `getChains()` 只返回链名、确认数、RPC URL。
  - `getAssetCatalog()` 只返回 symbol、decimals、contract、`orderPayable`。
  - 没有 `priceUsd`、`change24h`、`candles`、`marketCap` 等字段。

### 4.3 链侧服务只做 raw chain access

- `code/backend/src/modules/health/health.service.ts`
  - 聚合的是链侧健康和区块高度，不是行情源健康。
- `code/sol-agent/src/modules/transactions/transactions.controller.ts`
  - 只暴露交易状态查询。
- `code/backend-chain-usdt/src/modules/chain/chain.controller.ts`
  - 只暴露交易、区块、地址、合约、capabilities。

### 4.4 Android 当前“行情展示”仍是本地占位

- `code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/composeui/pages/wallet/WalletBitgetComponents.kt`
  - `walletReferencePrice()` / `walletReferenceChange()` 直接返回写死价格和涨跌。
- 这意味着当前客户端 UI 上的“链上行情/涨跌”并非服务端下发。

### 4.5 仓库里确实有历史汇率服务，但不在当前主栈

- `code/server/app/services/fx_rate.py`
  - 具备 CoinGecko 主源、Binance 备用源、TTL 60 秒缓存。
- `code/deploy/DEPLOYMENT.md`
  - 仍围绕旧 `code/server` 部署。
- `code/deploy/BACKEND_DEPLOYMENT.md`
  - 当前新栈明确指向 `code/backend`。
- 结论：仓库对“哪个后端是当前线上主后端”仍有文档漂移；市场能力不能直接按旧 FastAPI 文档当成现网事实。

## 5. 主要缺口

### 5.1 缺少独立市场数据域

当前没有统一的 `MarketModule` 或等价域来承接：

- 行情源接入
- 多源 fallback
- 标准化 ticker 响应
- 行情缓存
- freshness / stale 标记
- 行情错误降级

### 5.2 缺少“订单报价锁定”能力

当前订单服务只有“用户选 SOL 还是 USDT”这一层语义，没有真正的：

- 实时价格读取
- 服务端 payable amount 计算
- quote TTL
- quoteId
- 下单时二次校验
- 订单上保存 `priceSource / quotedAt / expiresAt`

这会直接导致：

- SOL 价格变化时 payable 金额失真
- 客户端与服务端无法对齐“这个金额是按哪一刻的价格锁定的”
- 后续无法可靠做 underpaid / overpaid 判断

### 5.3 缺少钱包 / 详情页所需的参考行情接口

当前钱包页、资产详情页、Bitget 风格详情页需要的典型字段包括：

- `priceUsd`
- `change24hPct`
- `high24h`
- `low24h`
- `asOf`
- `source`

这些字段当前主后端完全没有输出。

### 5.4 缺少行情运营与排障面

当前管理端没有以下能力：

- 查看行情源是否可用
- 查看最后一次刷新时间
- 查看缓存 age / stale 状态
- 手动刷新缓存
- 切换主/备源

### 5.5 当前责任边界仍需明确

市场数据不应该落到链侧服务：

- `sol-agent` / `backend-chain-usdt` 负责链上真相，例如 tx、block、address、broadcast。
- 价格聚合、报价锁定、面向客户端的标准化 market API 应留在 `api.residential-agent.com` 的主后端。

如果把价格 feed 塞进链侧服务，会把链访问与市场数据耦合在一起，后续运维和故障定位都会更差。

## 6. 建议下一步实现什么

### 6.1 建议优先做的服务端接口

| 优先级 | 建议接口 | 作用 | Owner |
|---|---|---|---|
| P0 | `GET /api/client/v1/market/tickers?symbols=SOL,TRX,USDT` | 钱包首页/资产详情/支付方式页拉取当前参考价格和 24h 涨跌 | 主后端 `code/backend` |
| P0 | `GET /api/client/v1/market/tickers/{symbol}` | 单资产详情页拉取 `priceUsd`、`change24hPct`、`high24h`、`low24h`、`source`、`asOf` | 主后端 `code/backend` |
| P0 | `POST /api/client/v1/market/order-quote` | 根据 `planCode + quoteAssetCode + quoteNetworkCode` 返回锁定报价 | 主后端 `code/backend`，由 `market + orders` 共同负责 |
| P1 | `GET /api/client/v1/market/overview` | 聚合少量 featured assets / 快照，给 Wallet/VPN 首页使用 | 主后端 `code/backend` |
| P1 | `GET /api/client/v1/market/candles/{symbol}?interval=1h&limit=24` | 仅当详情页保留小图表时提供时间序列 | 主后端 `code/backend` |
| P1 | `GET /api/admin/v1/market/sources/health` | 运营查看行情源、缓存 age、stale、错误状态 | 主后端 `code/backend` Admin |
| P1 | `POST /api/admin/v1/market/cache/refresh` | 手动刷新行情缓存 | 主后端 `code/backend` Admin |

### 6.2 `order-quote` 推荐返回结构

```json
{
  "quoteId": "mq_01...",
  "planCode": "BASIC_1M",
  "quoteAssetCode": "SOL",
  "quoteNetworkCode": "SOLANA",
  "planUsdAmount": "9.99",
  "assetPriceUsd": "175.80",
  "payableAmount": "0.056826",
  "estimatedFee": "0.000005",
  "source": "coingecko",
  "quotedAt": "2026-04-08T10:00:00Z",
  "expiresAt": "2026-04-08T10:05:00Z",
  "stale": false
}
```

### 6.3 `POST /api/client/v1/orders` 应同步调整

不是新增端点，但建议把当前创建订单改为：

- 入参接受 `quoteId`
- 服务端用 `quoteId` 找回已锁定报价
- 订单表保存：
  - `planUsdAmount`
  - `assetPriceUsd`
  - `payableAmount`
  - `priceSource`
  - `quotedAt`
  - `quoteExpiresAt`

否则新增 `order-quote` 也只是表面补丁，不能真正解决价格漂移。

## 7. 不建议现在做什么

基于当前产品方向，以下能力不应作为下一步 server-side 优先项：

- `GET /api/client/v1/market/orderbook`
- `GET /api/client/v1/market/trades`
- 实时撮合或深度数据
- 高频 WebSocket 行情流
- 在链侧服务里新增市场数据接口

原因：

- 当前产品已经明确把 Bitget 的 `Market/Quote` 核心位替换成 `VPN` 业务位。
- 现阶段真正缺的是“参考行情 + 正确支付报价”，不是交易所级 market stack。

## 8. Ownership 建议

| 责任 | 推荐 Owner | 说明 |
|---|---|---|
| 行情源接入、标准化、缓存、fallback | 主后端 `code/backend` | 统一对外输出 market API |
| 报价锁定、订单价格持久化 | 主后端 `code/backend` Orders/Market | 必须与订单状态机在同一服务内 |
| 链上交易/区块/地址/广播 | `code/sol-agent` / `code/backend-chain-usdt` | 保持链侧边界，不承担市场职责 |
| 行情源监控、缓存刷新、配置运营 | 主后端 Admin + Deploy/Ops | 统一运维视角 |
| 钱包页移除本地假价格、改为消费 API | Android 客户端 | 不是本任务范围，但需要后续配合 |
| 旧 `code/server` FXRate 逻辑复用 | 主后端迁移者 | 推荐“迁移逻辑到 Nest”，不推荐恢复双后端并存 |

## 9. 建议落地顺序

1. 在 `code/backend` 新增 `market` 域，只做 ticker + quote，不做 full exchange market。
2. 先打通 `GET /market/tickers` 与 `POST /market/order-quote`。
3. 把 `POST /orders` 改成消费 `quoteId`，去掉硬编码 payable。
4. 再补 admin 侧的 `market/sources/health` 和 `cache/refresh`。
5. 最后再决定是否真的需要 `candles`；如果详情页最终不画图，可以直接不做。

## 10. 最终判断

当前 liaojiang 的“市场相关服务端能力”本质上还停留在：

- 静态套餐价格
- 静态 payable quote
- 链资产目录
- 链上交易/区块查询

离“可支撑钱包详情页、支付报价、参考行情卡片”的最小市场 API 还差一层完整的主后端市场聚合与报价锁定能力。最合理的下一步不是扩链侧，也不是重启旧 FastAPI 作为第二套对外接口，而是在当前 `code/backend` 内补齐一个最小、可缓存、可观测、可锁定的 market/quote 模块。
