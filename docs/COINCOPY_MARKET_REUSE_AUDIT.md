# coincopy 市场能力复用审计

**审计目标**

- 审计 `/Users/cnyirui/git/projects/coincopy` 中与 `market / quote / chart / watchlist / stock` 相关的模型、接口定义与数据契约。
- 明确区分三类资产：
  - 视觉原型 only
  - mock / 静态数据 only
  - 真正可复用的 API / 数据契约
- 输出给 `liaojiang` 的结论只关注“能不能复用为真实契约”，不评审 UI 观感。

**审计范围**

- `coincopy/web`
- `coincopy/web原型/kapa`
- `coincopy/aurora-exchange-recursive-prototype`
- 对 `web原型/kimi`、`web原型/gpt-pro`、`web原型/kimi-formal` 只做归类，不逐页深挖；它们与 HTML 原型家族重复度很高，主要价值也是视觉稿。

## 1. 结论摘要

| 领域 | 最值得复用的来源 | 结论 | 备注 |
|---|---|---|---|
| 市场目录 / 现货报价列表 | `web/lib/api/dto.ts:41`、`web/lib/api/dto.ts:352`、`web原型/kapa/app/api/products/route.ts:19` | 可复用，但要拆成“目录元数据”和“ticker 行情”两层 | `ExchangeAdvancedMarketsItem` 只够做 market catalog，不够做完整 quote board |
| 图表 K 线 | `web原型/kapa/lib/server/trade/public-marketdata.ts:11`、`web原型/kapa/lib/server/trade/public-marketdata.ts:45`、`web原型/kapa/app/api/trade/marketdata/candles/route.ts:15` | 可直接复用 | 这是 `coincopy` 里最接近正式契约的 chart 数据定义 |
| Level2 快照 / 增量 | `web原型/kapa/lib/server/trade/public-marketdata.ts:13`、`web原型/kapa/lib/server/trade/public-marketdata.ts:18`、`web原型/kapa/lib/server/trade/public-marketdata.ts:32`、`web原型/kapa/app/api/trade/marketdata/level2/snapshot/route.ts:14`、`web原型/kapa/app/api/trade/marketdata/level2/deltas/route.ts:16` | 可直接复用 | REST 返回结构已经成型 |
| Level2 WS bootstrap | `web原型/kapa/app/api/trade/marketdata/level2/ws/route.ts:14`、`web/lib/api/mock-backend.ts:570`、`web/app/exchange/advanced-terminal/page.tsx:2637` | 只能部分复用，先统一再搬 | 真实 route 是 `pollingOnly` 分支，mock route 才提供 `wsUrl` |
| Watchlist / Favorites | `web/app/public/prices/page.tsx:53`、`web/app/exchange/advanced-markets/page.tsx:106`、`web/app/exchange/advanced-terminal/page.tsx:129` | 不存在真正可复用的后端模型 | 只有本地 `localStorage` 收藏态，没有服务端 watchlist contract |
| 股票 / equities | `web/lib/api/dto.ts:481`、`web原型/kapa/db/flyway/V3__api_query_views.sql:467`、`web/app/extensions/stocks/page.tsx:28`、`web原型/kapa/app/extensions/stocks/page.tsx:23` | 不能直接复用为真实股票契约 | 当前“stocks”一部分其实是 `reference_indices`，另一部分是硬编码美股 demo |

**最终判断**

- 真正值得复用的主干资产在 `web/lib/api/*` 与 `web原型/kapa/lib/server/trade/*`、`web原型/kapa/app/api/trade/marketdata/*`、`web原型/kapa/db/flyway/*`。
- `watchlist` 没有现成真实契约，必须新设计。
- `stocks` 这个名字在 `coincopy` 里是误导性的：
  - 生产页面明确关闭
  - DTO 实际映射的是 `reference_indices`
  - 旧原型页是硬编码股票列表和 mock 图表

## 2. 真正可复用的 API / 数据契约

### 2.1 页面读模型与查询绑定

**核心文件**

- `web/lib/api/dto.ts:41` 定义 `PublicPricesQuery`
- `web/lib/api/dto.ts:47` 定义 `PublicPricesItem`
- `web/lib/api/dto.ts:176` 定义 `AppSimpleTradeItem`
- `web/lib/api/dto.ts:352` 定义 `ExchangeAdvancedMarketsQuery`
- `web/lib/api/dto.ts:359` 定义 `ExchangeAdvancedMarketsItem`
- `web/lib/api/dto.ts:439` 定义 `ExchangeAdvancedTerminalQuery`
- `web/lib/api/dto.ts:446` 定义 `ExchangeAdvancedTerminalItem`
- `web/lib/api/dto.ts:481` 定义 `ExtensionStocksQuery`
- `web/lib/api/dto.ts:487` 定义 `ExtensionStocksItem`
- `web/lib/api/page-query-map.ts:20` 绑定 `/public/prices -> api.v_public_prices`
- `web/lib/api/page-query-map.ts:29` 绑定 `/app/simple-trade -> api.v_app_simple_trade_quotes`
- `web/lib/api/page-query-map.ts:40` 绑定 `/exchange/advanced-markets -> api.v_exchange_advanced_markets`
- `web/lib/api/page-query-map.ts:44` 绑定 `/exchange/advanced-terminal -> api.v_exchange_advanced_terminal`
- `web/lib/api/page-query-map.ts:47` 绑定 `/extensions/stocks -> api.v_extensions_stocks`
- `web/lib/api/repository.ts:22` 定义 `RouteDtoMap`
- `web/lib/api/repository.ts:120` 定义 `ViewRepository`
- `web/lib/api/query-builder.ts:169` 定义 `buildViewQuery`

**复用判断**

- 这组文件是 `coincopy` 当前最清晰的“前端页面读契约”。
- 经本地逐文件比对，以下文件在 `web/` 与 `web原型/kapa/` 中是同一份拷贝：
  - `lib/api/dto.ts`
  - `lib/api/page-query-map.ts`
  - `lib/api/repository.ts`
  - `lib/client/api.ts`
- 优点：
  - 路由、查询 DTO、返回 item DTO、数据库 view 名称是 1:1 绑定的。
  - `query-builder.ts` 已经把分页、排序、过滤字段白名单化。
  - `repository.ts` 把 `PageResult<T>` 与 route-specific item 类型绑定起来，适合作为 BFF 或 adapter 层。
- 限制：
  - `ExchangeAdvancedMarketsItem` 更像“市场目录”而不是“实时 ticker”。
  - 它没有正式包含 `price / change24hPercent / volume24h / marketCap / sparklineData` 这些真正的 quote 字段。
  - 当前前端多个页面仍在把它临时扩成 `ApiProduct` 再消费：
    - `web/app/page.tsx:20`
    - `web/app/public/prices/page.tsx:42`

**复用建议**

- 可以直接复用成 `liaojiang` 的“市场目录 / 交易对元数据”契约起点。
- 不建议把 `ExchangeAdvancedMarketsItem` 直接当作首页行情卡片或市场列表的最终 quote model。
- 如果要复用到真实行情页，建议新建更明确的 `MarketTickerItem`，把以下字段升格为正式契约：
  - `lastPrice`
  - `change24hPercent`
  - `volume24h`
  - `high24h`
  - `low24h`
  - `sparklineData` 或独立 chart endpoint

### 2.2 `/api/products` 的实际 ticker 适配层

**核心文件**

- `web/lib/client/api.ts:31` 定义 `fetchProducts`
- `web原型/kapa/app/api/products/route.ts:14` 将该 route 视为 `/exchange/advanced-markets`
- `web原型/kapa/app/api/products/route.ts:19` 定义 `ProductRow`
- `web原型/kapa/app/api/products/route.ts:64` 定义 `mapCoinbaseProduct`
- `web原型/kapa/app/api/products/route.ts:187` 实现 `GET`
- `web/docs/backend-integration-map.md:51` 把 `/api/products` 标成 P0 市场读取主入口

**复用判断**

- 真正能承载 `price / volume / change24hPercent / updatedAt / provider` 的，不是 `ExchangeAdvancedMarketsItem`，而是这里的 `ProductRow`。
- `mapCoinbaseProduct` 已经把 Coinbase 公开市场模型映射成较完整的市场行对象，适合做：
  - 首页价格卡片
  - 公共行情列表
  - 高级市场列表
  - 终端市场下拉

**复用建议**

- 如果 `liaojiang` 也准备先走第三方公开行情，再补自有市场服务，可以直接参考这套 adapter。
- 但不要把 `ProductRow` 内的 snake_case / camelCase 双字段并存状态原封不动搬过去；那更像兼容层，不是干净的共享模型。
- 推荐在目标项目里保留一份干净版输出：
  - `productId`
  - `symbol`
  - `baseSymbol`
  - `quoteSymbol`
  - `productType`
  - `status`
  - `lastPrice`
  - `change24hPercent`
  - `volume24h`
  - `tickSize`
  - `lotSize`
  - `minNotional`
  - `updatedAt`

### 2.3 图表与盘口的正式化契约

**核心文件**

- `web原型/kapa/lib/server/trade/public-marketdata.ts:11` 定义 `PublicTimeframe`
- `web原型/kapa/lib/server/trade/public-marketdata.ts:13` 定义 `PublicMarketLevel`
- `web原型/kapa/lib/server/trade/public-marketdata.ts:18` 定义 `PublicMarketSnapshot`
- `web原型/kapa/lib/server/trade/public-marketdata.ts:32` 定义 `PublicOrderbookDepth`
- `web原型/kapa/lib/server/trade/public-marketdata.ts:45` 定义 `PublicCandleRow`
- `web原型/kapa/lib/server/trade/public-marketdata.ts:100` 定义 `parsePublicMarketSymbol`
- `web原型/kapa/lib/server/trade/public-marketdata.ts:145` 定义 `parsePublicTimeframe`
- `web原型/kapa/lib/server/trade/public-marketdata.ts:170` 定义 `loadPublicOrderbook`
- `web原型/kapa/lib/server/trade/public-marketdata.ts:216` 定义 `loadPublicCandles`
- `web原型/kapa/app/api/trade/marketdata/candles/route.ts:15`
- `web原型/kapa/app/api/trade/marketdata/level2/snapshot/route.ts:14`
- `web原型/kapa/app/api/trade/marketdata/level2/deltas/route.ts:16`
- `web原型/kapa/app/api/trade/marketdata/level2/ws/route.ts:14`
- `web/docs/backend-integration-map.md:159`
- `web/docs/backend-integration-map.md:164`
- `web/docs/backend-integration-map.md:169`
- `web/docs/backend-integration-map.md:174`

**复用判断**

- 这是本次审计里最值得直接抽出来复用的一组资产。
- 原因：
  - timeframe 枚举已经固定。
  - candle row 字段干净，容易跨端共享。
  - level2 snapshot / depth / delta 的字段边界清楚。
  - route 层已经做了输入校验和统一成功包裹。

**需要注意的边界**

- `level2/ws` 目前不是单一稳定契约。
  - 真实 route：`web原型/kapa/app/api/trade/marketdata/level2/ws/route.ts:58` 返回 `pollingOnly: true`，只给 `snapshotHint`。
  - mock route：`web/lib/api/mock-backend.ts:570` 到 `web/lib/api/mock-backend.ts:590` 返回 `wsUrl` 与 `wsBaseMeta`。
  - 页面消费端：`web/app/exchange/advanced-terminal/page.tsx:2637` 到 `web/app/exchange/advanced-terminal/page.tsx:2644` 先判断 `pollingOnly`，否则再走 `wsUrl` bootstrap。
- 这说明 `ws bootstrap` 在仓库里其实有两种形态，还没完全收口成一个共享 interface。

**复用建议**

- 可以直接复用：
  - `PublicTimeframe`
  - `PublicMarketLevel`
  - `PublicMarketSnapshot`
  - `PublicOrderbookDepth`
  - `PublicCandleRow`
- 但建议先把 `level2/ws` 拆成两个明确模型：
  - `PollingBootstrap`
  - `WebSocketBootstrap`
- 不建议继续让页面本地自己定义一套解析接口：
  - `web/app/exchange/advanced-terminal/page.tsx:418`
  - `web/app/exchange/advanced-terminal/page.tsx:423`
  - `web/app/exchange/advanced-terminal/page.tsx:444`
  - `web/app/exchange/advanced-terminal/page.tsx:456`
  - `web/components/trading/AdvancedTerminalChart.tsx:31`

### 2.4 Provider 适配层与数据库视图层

**核心文件**

- `web原型/kapa/lib/providers/coinbase-market.ts:23` 定义 `CoinbaseMarketProduct`
- `web原型/kapa/lib/providers/coinbase-market.ts:79` 定义 `CoinbaseCandle`
- `web原型/kapa/lib/providers/coinbase-market.ts:116` 定义 granularity 转换
- `web原型/kapa/lib/providers/coinbase-market.ts:143` 定义可交易产品过滤
- `web原型/kapa/lib/providers/coinbase-market.ts:154` 定义 `fetchCoinbaseMarketProducts`
- `web原型/kapa/lib/providers/coinbase-market.ts:178` 定义 `fetchCoinbaseMarketOrderbook`
- `web原型/kapa/lib/providers/coinbase-market.ts:206` 定义 `fetchCoinbaseMarketCandles`
- `web原型/kapa/db/flyway/V1__create_schema.sql:262` 定义 `market.products`
- `web原型/kapa/db/flyway/V1__create_schema.sql:313` 定义 `market.reference_indices`
- `web原型/kapa/db/flyway/V2__seed_minimal_data.sql:113` 种子 `market.products`
- `web原型/kapa/db/flyway/V2__seed_minimal_data.sql:143` 种子 `market.reference_indices`
- `web原型/kapa/db/flyway/V3__api_query_views.sql:21` 定义 `api.v_public_prices`
- `web原型/kapa/db/flyway/V3__api_query_views.sql:146` 定义 `api.v_app_simple_trade_quotes`
- `web原型/kapa/db/flyway/V3__api_query_views.sql:362` 定义 `api.v_exchange_advanced_markets`
- `web原型/kapa/db/flyway/V3__api_query_views.sql:435` 定义 `api.v_exchange_advanced_terminal`
- `web原型/kapa/db/flyway/V3__api_query_views.sql:467` 定义 `api.v_extensions_stocks`

**复用判断**

- 如果 `liaojiang` 后端也是 Postgres/BFF 路线，这一层非常适合作为建模参考。
- `market.products` 与几个 `api.v_*` 视图能支撑：
  - 市场目录
  - 公共价格页
  - 简单交易报价
  - 成交明细
- `market.reference_indices` 也有价值，但它是“参考指数 / 标记价格指数”模型，不是股票主数据。

**复用建议**

- `market.products` 与 `api.v_public_prices` / `api.v_exchange_advanced_markets` 可以直接参考。
- `api.v_extensions_stocks` 只能在你需要“指数 / composite / mark index 列表”时复用。
- 如果目标需求是真实 equities / stock board，不要复用 `reference_indices` 假装成股票主表。

### 2.5 可有限复用的事件包裹

**核心文件**

- `web原型/kapa/exchange/contracts/schema-registry/subjects.v1.json`
- `web原型/kapa/exchange/contracts/schema-registry/schemas/v1/event-envelope.schema.json`

**复用判断**

- 这组文件可作为事件总线 envelope 约束参考。
- 但它只定义了非常宽泛的通用壳：
  - `eventType`
  - `schemaVersion`
  - `symbol`
  - `symbolSeq`
  - `tsEngineNs`
  - `payload`
- 它没有具体定义 `market.level2.snapshot`、`market.ticker.updated` 的 payload 细项。

**复用建议**

- 可以复用 envelope 思路。
- 不要把它当成现成的市场数据 payload schema。

## 3. 仅 mock / 静态数据，可参考但不要当正式契约

### 3.1 mock backend

**文件**

- `web/lib/api/mock-backend.ts:7` 定义 `sampleProducts`
- `web/lib/api/mock-backend.ts:480` 返回 `/api/products`
- `web/lib/api/mock-backend.ts:544` 返回 `/api/trade/marketdata/level2/snapshot`
- `web/lib/api/mock-backend.ts:555` 返回 `/api/trade/marketdata/level2/deltas`
- `web/lib/api/mock-backend.ts:570` 返回 `/api/trade/marketdata/level2/ws`

**判断**

- 这层有“接口壳”，但没有真实业务可信度。
- 适合：
  - 本地联调
  - 前端开发时占位
  - 回归脚本跑通 happy path
- 不适合：
  - 当成正式市场契约源头
  - 反向推导真实字段语义

### 3.2 价格模拟器与静态数据

**文件**

- `web/lib/mock/prices.ts:40` 初始化价格
- `web/lib/mock/prices.ts:117` 定义 `PriceSimulator`
- `web/lib/mock/prices.ts:177` 生成盘口
- `web/lib/mock/prices.ts:229` 生成最近成交
- `web/lib/mock/prices.ts:259` 生成价格历史
- `web/lib/mock/data.ts:7` 从 `@/types` 引入大量 domain type
- `web/lib/mock/data.ts:32` 定义 `mockAssets`
- `web/lib/mock/data.ts:534` 定义 `mockStakingProducts`
- `web/lib/mock/data.ts:648` 定义 `mockDerivativeContracts`
- `web/lib/mock/data.ts:706` 定义 `mockPositions`

**判断**

- 这批资产全是开发假数据。
- 更关键的是，`web/lib/mock/data.ts` 引用了 `Asset / Market / StakingProduct / DerivativeContract / Position` 等类型，但 `web/types.ts` 实际只有非常宽泛的 `Dict` 与少量接口：
  - `web/types.ts:11` `type Dict = Record<string, any>`
  - `web/types.ts:48` `AssetPrice`
  - `web/types.ts:98` `OrderBook`
  - `web/types.ts:103` `RecentTrade`
- 也就是说，这套 mock 类型体系本身都不是一套干净、完整、可落地的共享契约。

**复用建议**

- 最多拿它做 UI 占位或 Storybook 假数据。
- 不建议把任何 `mockAssets / mockDerivativeContracts / mockPositions` 当成正式模型基线。

### 3.3 旧版股票原型页

**文件**

- `web原型/kapa/app/extensions/stocks/page.tsx:23` 定义 `Stock`
- `web原型/kapa/app/extensions/stocks/page.tsx:38` 定义 `StockDetail`
- `web原型/kapa/app/extensions/stocks/page.tsx:67` 定义硬编码 `stocks`
- `web原型/kapa/app/extensions/stocks/page.tsx:82` 定义硬编码 `stockDetails`
- `web原型/kapa/app/extensions/stocks/page.tsx:220` 明确写着 `Generate mock chart data`

**判断**

- 这是典型的 UI demo 数据结构：
  - 硬编码美股列表
  - 硬编码详情
  - 本地生成 chart
- 它不能证明 `coincopy` 已有真实股票 API 契约。

**复用建议**

- 只能参考页面信息架构和字段候选。
- 不能直接搬为后端或共享前端 contract。

## 4. 视觉原型 only

### 4.1 HTML 原型族

**文件**

- `aurora-exchange-recursive-prototype/public/prices/watchlists.html:6`
- `aurora-exchange-recursive-prototype/public/prices/watchlists.html:214`
- `aurora-exchange-recursive-prototype/public/prices/watchlists.html:357`
- `aurora-exchange-recursive-prototype/extensions/stocks/stock-detail.html:6`
- `aurora-exchange-recursive-prototype/extensions/stocks/stock-detail.html:182`
- `aurora-exchange-recursive-prototype/extensions/stocks/stock-detail.html:325`

**判断**

- 这些页面的价值在于：
  - 页面树
  - 交互文案
  - 信息架构
  - “建议接口”占位说明
- 但它们没有任何正式 TS/JSON/SQL 契约定义。
- 例如：
  - `watchlists.html` 只是说明“自选资产、价格提醒、最近浏览与快捷返回”
  - `stock-detail.html` 只是说明“个股行情、基本面摘要、风险与交易入口”

**复用建议**

- 仅用于产品结构、埋点、页面拆分参考。
- 不要把 HTML 原型里的“建议接口”表格当成真实接口定义。

### 4.2 已淘汰的生产股票页边界说明

**文件**

- `web/app/extensions/stocks/page.tsx:28`
- `web/app/extensions/stocks/page.tsx:36`
- `web/app/extensions/stocks/page.tsx:37`
- `web/app/extensions/stocks/page.tsx:60`

**判断**

- 当前 `coincopy/web` 已经明确声明：
  - 没有生产可用的 equities market contract
  - mock watchlists / synthetic candles / fake order interactions 已移除
- 这是重要证据：即使仓库里仍残留股票原型，也不能把它当成可复用正式资产。

## 5. 明确不建议复用的项

### 5.1 watchlist 相关实现

**文件**

- `web/app/public/prices/page.tsx:53`
- `web/app/public/prices/page.tsx:92`
- `web/app/public/prices/page.tsx:116`
- `web/app/exchange/advanced-markets/page.tsx:106`
- `web/app/exchange/advanced-markets/page.tsx:108`
- `web/app/exchange/advanced-markets/page.tsx:125`
- `web/app/exchange/advanced-terminal/page.tsx:129`
- `web/app/exchange/advanced-terminal/page.tsx:140`
- `web/app/exchange/advanced-terminal/page.tsx:162`

**判断**

- 仓库里现有“watchlist / favorites”全部是本地状态。
- 在共享 API 层检索 `watchlist|favorite` 无命中：
  - `web/lib/api/**`
  - `web原型/kapa/lib/api/**`
- 所以这里没有任何真正可搬走的 watchlist DTO、repository、route contract。

**结论**

- `liaojiang` 需要新设计 watchlist 模型，至少补齐：
  - `watchlistId`
  - `ownerUserId`
  - `name`
  - `items[]`
  - `sortOrder`
  - `alerts[]`
  - `createdAt / updatedAt`

### 5.2 `ExtensionStocksItem`

**文件**

- `web/lib/api/dto.ts:481`
- `web/lib/api/dto.ts:487`
- `web/lib/api/page-query-map.ts:47`
- `web原型/kapa/db/flyway/V3__api_query_views.sql:467`
- `web原型/kapa/db/flyway/V1__create_schema.sql:313`

**判断**

- 名字叫 `stocks`，实际落库是 `market.reference_indices`。
- `v_extensions_stocks` 只挑 `index / mark / composite` 三类 `index_type`。
- 这更像：
  - 指数列表
  - 标记价格索引
  - composite index
- 不是：
  - 股票标的主数据
  - 证券静态资料
  - 美股行情 / 盘口 / K 线

**结论**

- 不要直接复用为股票交易 contract。
- 如果目标是股票业务，应新建：
  - `EquityInstrument`
  - `EquityQuote`
  - `EquityCandle`
  - `EquityWatchlistItem`

### 5.3 页面内局部接口

**文件**

- `web/app/exchange/advanced-terminal/page.tsx:418`
- `web/app/exchange/advanced-terminal/page.tsx:423`
- `web/app/exchange/advanced-terminal/page.tsx:444`
- `web/app/exchange/advanced-terminal/page.tsx:456`
- `web/components/trading/AdvancedTerminalChart.tsx:31`
- `web/components/trading/AdvancedTerminalChart.tsx:77`

**判断**

- 这些接口和 parser 是“消费端适配结构”，不是 canonical contract。
- 它们存在的原因是共享层没有把 chart / level2 的 TS 类型抽出来。

**结论**

- 不建议直接复制这些 page-local interface。
- 应优先从 `web原型/kapa/lib/server/trade/public-marketdata.ts` 抽共享模型，再让页面 import。

### 5.4 `web/types.ts`

**文件**

- `web/types.ts:11`
- `web/types.ts:48`
- `web/types.ts:98`
- `web/types.ts:103`

**判断**

- 这是一个非常宽泛的 UI 类型桶，核心模式是 `Record<string, any>` 继承。
- 它缺少严格的行情 / 图表 / 股票 domain typing。
- 与 `web/lib/mock/data.ts` 的导入集合也不自洽。

**结论**

- 不建议把它当共享市场模型来源。

## 6. 对 `liaojiang` 的复用建议

### 6.1 可以直接借鉴的资产

1. `web/lib/api/dto.ts`
   用于快速建立只读页面 DTO 命名方式，尤其是：
   - `PublicPricesItem`
   - `AppSimpleTradeItem`
   - `ExchangeAdvancedTerminalItem`

2. `web/lib/api/page-query-map.ts` + `query-builder.ts` + `repository.ts`
   适合保留“route -> view -> dto”绑定思路，尤其适合 Postgres 读模型项目。

3. `web原型/kapa/lib/server/trade/public-marketdata.ts`
   这是最适合直接升格成共享库的图表 / 盘口 contract 文件。

4. `web原型/kapa/app/api/products/route.ts` + `lib/providers/coinbase-market.ts`
   适合用作外部行情 provider adapter 模板。

5. `web原型/kapa/db/flyway/V1__create_schema.sql` + `V3__api_query_views.sql`
   适合参考市场产品表、参考指数表、API query view 的建模方式。

### 6.2 只能参考，不能照搬的资产

1. `ExtensionStocksItem`
   只能参考字段命名，不可当股票 contract。

2. `/api/products` 当前 `ProductRow`
   可以参考字段集合，但建议删掉 snake_case/camelCase 双写兼容字段后再落地。

3. `level2/ws`
   必须先统一成单一 bootstrap 协议。

### 6.3 明确需要新建的契约

1. Watchlist
   `coincopy` 里没有真实服务端 watchlist model。

2. Equity / stock
   `coincopy` 里没有真实 equities instrument / quote / candle / eligibility contract。

3. Market ticker
   现有 `ExchangeAdvancedMarketsItem` 不够表达完整 quote，建议独立为正式 ticker model。

## 7. 最终判定

- **可直接复用的主干**：有，而且质量最高的是 `web/lib/api/*` 的页面读契约框架，以及 `web原型/kapa/lib/server/trade/public-marketdata.ts` 的 chart / level2 契约。
- **watchlist**：没有现成真实模型，只有前端本地收藏态，必须新建。
- **stocks**：没有现成真实 equities contract；当前 `stocks` 命名下混杂了 reference index、停用页面和旧版 mock 原型，不应直接搬进 `liaojiang`。
- **不建议复用**：HTML 视觉原型、`web/lib/mock/*`、`web/types.ts`、页面内局部 parser 接口。

如果只允许挑一批资产落地到 `liaojiang`，建议优先顺序如下：

1. `web原型/kapa/lib/server/trade/public-marketdata.ts`
2. `web原型/kapa/app/api/trade/marketdata/*`
3. `web/lib/api/dto.ts`
4. `web/lib/api/page-query-map.ts`
5. `web/lib/api/query-builder.ts`
6. `web/lib/api/repository.ts`
7. `web原型/kapa/app/api/products/route.ts`
8. `web原型/kapa/lib/providers/coinbase-market.ts`
