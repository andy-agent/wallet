# Market 真实 API 需求

## 1. 范围与判读结论

本文只定义当前 Compose `Market` 页面族所需的真实数据接口，不接受任何 mock、sample、硬编码字符串或本地合成 K 线。

当前页面范围：

- `ShellTab.MARKET -> MarketOverviewPage`
- `Routes.marketQuoteDetail(symbol) -> MarketQuoteDetailPage`

本次只使用本地证据：

- 代码：
  - `code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/composeui/pages/market/MarketBitgetModels.kt`
  - `code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/composeui/pages/market/MarketOverviewPage.kt`
  - `code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/composeui/pages/market/MarketQuoteDetailPage.kt`
  - `code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/ui/compose/BitgetAppShell.kt`
  - `code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/composeui/navigation/AppNavGraph.kt`
- 文档：
  - `docs/BITGET_CAPTURE_INVENTORY.md`
  - `docs/BITGET_MARKET_VPN_IA.md`
- 当前设备检查：
  - 2026-04-08 在 `ba2b016` 启动已安装包 `com.finshell.wallet`
  - 顶层可见 tab 为 `首页 / 借钱 / 财富 / 生活 / 我的`
  - 未发现可直达的 `行情` 一级 tab；`财富` 页也是保险/津贴内容，不是市场页

结论：

1. 当前安装态不能作为 `Market` family 的直接页面输入，只能作为“live build 已偏离历史 Bitget market 壳层”的负证据。
2. 当前真实需求必须以现有 Compose `MarketOverviewPage` / `MarketQuoteDetailPage` 结构为准，并用仓库内历史行情详情描述补足字段语义。
3. `Market` 在当前壳层里是访客可读页面，因此市场读接口必须支持匿名只读；只有自选/收藏写操作可以要求登录。

## 2. 当前 mock 模型必须被替换的内容

| 当前 mock 字段/来源 | 问题 | 真实接口要求 |
|---|---|---|
| `marketSampleQuotes` | 样本列表，数据量、排序、标签、自选状态全是本地写死 | 改为真实 overview snapshot + live ticker/ranking 数据 |
| `marketSampleSpotlights` | 焦点卡完全硬编码 | 改为服务端下发的实时/准实时 spotlight 数据 |
| `marketSampleQuoteDetail()` | 详情页所有字段本地拼接 | 改为真实 detail snapshot |
| `buildMarketCandles()` | K 线为本地伪造 | 改为真实 candle history + live candle update |
| `lastPrice = "$71,220.00"` | 已格式化 UI 字符串，不可计算、不可排序 | 传原始数值字段，客户端格式化 |
| `changeAmount = "+1,240.00"` | 同上 | 传原始涨跌额字段 |
| `changePercent = "+1.77%"` | 同上 | 传原始涨跌幅字段 |
| `dayRange = "$69,880 - $71,540"` | 把两个数挤成一个字符串 | 必须拆成 `low24h` / `high24h` |
| `volume24h = "$33.80B"` | 只有展示文本，没有原始量纲 | 至少拆成 `turnover24h`，最好同时给 `baseVolume24h` |
| `marketCap = "$1.40T"` / `peRatio = "--"` | 只有展示层文本 | 传原始值；不适用时传 `null`，不要传 `"--"` |
| `rightLabels` / `bottomLabels` / `calloutLines` | 轴标签与悬浮提示全是 UI 拼接结果 | 后端提供原始 candle + 时间戳 + 原始 OHLCV；轴标签与 hover 文案由客户端生成，必要时可补 axis hint |
| `thesis` | 这是实现说明，不是市场数据 | 明确排除，不允许进入生产接口 |

## 3. 共享真实数据契约

### 3.1 非 negotiable 规则

- 所有页面都必须围绕稳定主键 `instrumentId` 工作，`symbol` 只用于显示和路由别名。
- 金额、价格、成交额、成交量、涨跌幅必须传原始值，不能带 `$`、`,`、`%`、`热度` 等展示修饰。
- 所有 snapshot 和 stream 消息都必须带 `serverTime`。
- `Market` 读接口必须支持匿名访问；收藏写接口允许要求登录。
- 排行榜、spotlight、热度类字段不能只靠 ticker stream 推导，必须有服务端自己的 ranking/curation 输出。

### 3.2 推荐基础类型

```ts
type DecimalString = string   // 仅原始十进制值，不带 $ / % / 分隔符
type TimestampMs = number

type InstrumentRef = {
  instrumentId: string
  symbol: string
  displayName: string
  marketType: "CRYPTO" | "US_STOCK" | "ETF" | "INDEX" | string
  quoteCurrency: string
  displayPrecision: number
  marketLabel: string
  sessionLabel?: string | null
  tags: Array<{ key: string; label: string; tone?: "accent" | "positive" | "negative" | "neutral" }>
  categoryKeys: string[]
  favorite: boolean
}
```

```ts
type Ticker24h = {
  lastPrice: DecimalString
  absChange24h: DecimalString
  pctChange24h: DecimalString
  high24h: DecimalString
  low24h: DecimalString
  turnover24h: DecimalString
  baseVolume24h?: DecimalString | null
  marketCap?: DecimalString | null
  peRatio?: DecimalString | null
}

type RankSignals = {
  heatRank?: number | null
  changeRank?: number | null
  turnoverRank?: number | null
  listingRank?: number | null
}
```

```ts
type Candle = {
  openTime: TimestampMs
  closeTime: TimestampMs
  open: DecimalString
  high: DecimalString
  low: DecimalString
  close: DecimalString
  volume?: DecimalString | null
  turnover?: DecimalString | null
  closed: boolean
}
```

## 4. MarketOverviewPage 所需真实接口

### 4.1 页面模块清单

| 模块 | 必需真实字段 | 刷新行为 | 需要的接口形态 |
|---|---|---|---|
| 页面 bootstrap | `categories[]`、`boards[]`、默认选中项、可见标的总数、`serverTime` | 首次进入；切前台时重拉；手动刷新时重拉 | `GET /market/overview` |
| 搜索框 | `query` 对应的真实标的结果；每项至少返回 `instrumentId/symbol/displayName/tags` 和行级 ticker 数据 | 输入 debounce 200-300ms；清空 query 回落 overview | `GET /market/search?q=...` |
| Spotlight strip | `spotlightId`、`instrumentId`、`eyebrow`、`title`、`subtitle`、`primaryMetric`、`secondaryMetric`、点击目标 | 首次进入拉取；30-60s 定时刷新；切前台重拉 | `GET /market/spotlights`，可选 `WS market.spotlights` |
| 顶部分类 tabs | 至少支持当前 UI 需要的 key：`favorites`、`hot`、`new_coin`、`public_chain`、`meme`；每类 label/count | 首次进入拉取；切前台重拉；服务端规则变更时更新 | 可并入 `GET /market/overview` |
| 榜单 tabs | 至少支持当前 UI 需要的 key：`hot`、`gainers`、`volume`、`new_listing`；每个 board 的 `label`、`columnLabel` | 首次进入拉取；配置变更时更新 | 可并入 `GET /market/overview` |
| 列表行数据 | `instrumentId`、`symbol`、`displayName`、`marketLabel`、`favorite`、`tags[]`、`lastPrice`、`absChange24h`、`pctChange24h`、`turnover24h`、`marketCap?`、`peRatio?`、`high24h`、`low24h`、`heatRank?`、`turnoverRank?`、`listingRank?` | 首屏先用 REST snapshot；页面可见时用 live stream 更新；断线 fallback 为 5-10s 轮询 | `GET /market/overview` + `WS market.tickers.batch` + `GET /market/rankings` 或 `WS market.rankings` |
| 自选状态 | `favorite` 布尔值、当前用户收藏集 revision | 首屏拉取；切前台重拉；写后立即回写 | `GET /market/favorites`，`PUT /market/favorites/{instrumentId}`，`DELETE /market/favorites/{instrumentId}` |

### 4.2 Overview 最低可用 REST shape

```json
{
  "serverTime": 1775644800000,
  "categories": [
    { "key": "favorites", "label": "自选", "count": 12 },
    { "key": "hot", "label": "热门", "count": 50 },
    { "key": "new_coin", "label": "新币", "count": 24 },
    { "key": "public_chain", "label": "公链", "count": 31 },
    { "key": "meme", "label": "Meme", "count": 18 }
  ],
  "boards": [
    { "key": "hot", "label": "热门", "columnLabel": "涨跌/热度" },
    { "key": "gainers", "label": "涨幅榜", "columnLabel": "24H涨跌" },
    { "key": "volume", "label": "成交额", "columnLabel": "成交额" },
    { "key": "new_listing", "label": "新币榜", "columnLabel": "新热度" }
  ],
  "rows": [
    {
      "instrument": {
        "instrumentId": "us-stock:crwv",
        "symbol": "CRWV",
        "displayName": "CoreWeave",
        "marketType": "US_STOCK",
        "quoteCurrency": "USD",
        "displayPrecision": 2,
        "marketLabel": "US_STOCK",
        "sessionLabel": "夜盘",
        "tags": [{ "key": "night-session", "label": "夜盘", "tone": "accent" }],
        "categoryKeys": ["hot"],
        "favorite": false
      },
      "ticker24h": {
        "lastPrice": "90.01",
        "absChange24h": "9.56",
        "pctChange24h": "11.89",
        "high24h": "92.52",
        "low24h": "83.12",
        "turnover24h": "33800000000",
        "marketCap": "1400000000000",
        "peRatio": "31.8"
      },
      "rankSignals": {
        "heatRank": 99,
        "changeRank": 4,
        "turnoverRank": 2,
        "listingRank": 24
      }
    }
  ]
}
```

### 4.3 Overview live stream 最低要求

- `WS market.tickers.batch`
  - 用于更新列表行 `lastPrice`、`absChange24h`、`pctChange24h`、`high24h`、`low24h`、`turnover24h`、`marketCap`。
  - UI 可做 250-1000ms 合并刷新，但不能只停留在静态 REST。
- `WS market.rankings` 或 `GET /market/rankings`
  - 用于更新 `heatRank`、`changeRank`、`turnoverRank`、`listingRank`。
  - 这类数据 15-60s 刷新即可，不要求逐 tick 推送。

## 5. MarketQuoteDetailPage 所需真实接口

### 5.1 页面模块清单

| 模块 | 必需真实字段 | 刷新行为 | 需要的接口形态 |
|---|---|---|---|
| 顶栏身份区 | `instrumentId`、`symbol`、`displayName`、`marketLabel`、`sessionLabel`、`favorite`、可选 `shareUrl` | 首次进入拉取；收藏写后回写 | `GET /market/instruments/{instrumentId}` + 收藏接口 |
| 主价格区 | `lastPrice`、`absChange24h`、`pctChange24h` | 首次进入拉取；页面可见时 live 更新 | `GET /market/instruments/{instrumentId}` + `WS market.ticker.{instrumentId}` |
| 右侧 KPI 列 | 至少支持当前结构：`high24h`、`low24h`、`turnover24h`、`marketCap?`、`peRatio?`；如是 crypto，可将 `peRatio` 返回 `null` | 首次进入拉取；ticker 更新时同步刷 | 并入 detail snapshot 或 ticker stream |
| 顶部 `行情 / 详情` tabs | 两个 tab 的数据都要在首屏就可切换，不应依赖 mock | 首次进入拉取；5 分钟级别或切前台重拉即可 | 同一个 `GET /market/instruments/{instrumentId}` 返回 overview/detail facts |
| 时间粒度 selector | 至少支持当前 UI：`1h`、`4h`、`12h`、`1d`、`more`；每个 timeframe 的 `key/label` | 首次进入拉取；切换 timeframe 时按需请求 | detail snapshot + candle history REST |
| K 线主图 | `candles[]`，每根 candle 至少有 `openTime/closeTime/open/high/low/close/volume?/turnover?/closed` | 首次进入拉取当前 timeframe 历史；切换 timeframe 重拉；页面可见时 live 更新最新 bar | `GET /market/instruments/{instrumentId}/candles?timeframe=...` + `WS market.candles.{instrumentId}.{timeframe}` |
| 十字光标 hover/card | 悬浮点位的 `openTime/open/high/low/close`，以及可直接算出的 `absChange/pctChange`；如要显示成交量也应一并给出 | 来自同一组 candle 数据；切 bar 时本地计算/展示 | 不需要额外接口，复用 candle history / stream |
| 指标 strip | 至少支持当前 UI 的可选项：`MA`、`BOLL`、`MACD`、`KDJ`、`RSI`、`WR` | 首次进入拉取 capabilities；切换指标不应重新请求整页 | `supportedIndicators` 放在 detail snapshot；指标序列可选并入 candle 接口 |
| `市场概览` 信息表 | 至少支持：`24H 区间`、`24H 成交额`、`总市值`、`涨跌额`、`概念标签` | 首次进入拉取；5 分钟级或切前台重拉 | 并入 detail snapshot |
| `详情` 信息表 | 至少支持：`交易所/市场`、`标的名称`、`所属榜单/分类`、`24H 涨跌`、`24H 区间`、`标签`；如是股票，还应支持行业/板块/发行方等扩展字段 | 首次进入拉取；5 分钟级或切前台重拉 | 并入 detail snapshot |
| 底部 `交易` CTA | `tradeEnabled`、`tradeActionLabel`、`tradeTarget`（deeplink、route key 或 web URL） | 首次进入拉取；切前台校验 | 并入 detail snapshot |
| 当前 `thesis` 说明块 | 不是市场数据，不应继续保留为真实接口需求 | 不允许接入 | 明确排除 |

### 5.2 Detail 最低可用 REST shape

```json
{
  "serverTime": 1775644800000,
  "instrument": {
    "instrumentId": "us-stock:crwv",
    "symbol": "CRWV",
    "displayName": "CoreWeave",
    "marketType": "US_STOCK",
    "quoteCurrency": "USD",
    "displayPrecision": 2,
    "marketLabel": "US_STOCK · 行情",
    "sessionLabel": "夜盘",
    "tags": [
      { "key": "night-session", "label": "夜盘", "tone": "accent" },
      { "key": "ai", "label": "AI", "tone": "positive" }
    ],
    "categoryKeys": ["hot"],
    "favorite": false
  },
  "ticker24h": {
    "lastPrice": "90.01",
    "absChange24h": "9.56",
    "pctChange24h": "11.89",
    "high24h": "92.52",
    "low24h": "83.12",
    "turnover24h": "33800000000",
    "marketCap": "1400000000000",
    "peRatio": "31.8"
  },
  "supportedTimeframes": [
    { "key": "1h", "label": "1小时" },
    { "key": "4h", "label": "4小时" },
    { "key": "12h", "label": "12小时" },
    { "key": "1d", "label": "1天" },
    { "key": "more", "label": "更多" }
  ],
  "supportedIndicators": ["MA", "BOLL", "MACD", "KDJ", "RSI", "WR"],
  "overviewFacts": [
    { "key": "range24h", "label": "24H 区间", "value": { "low": "83.12", "high": "92.52" } },
    { "key": "turnover24h", "label": "24H 成交额", "value": "33800000000" },
    { "key": "marketCap", "label": "总市值", "value": "1400000000000" },
    { "key": "absChange24h", "label": "涨跌额", "value": "9.56" },
    { "key": "conceptTags", "label": "概念标签", "value": ["AI", "夜盘"] }
  ],
  "detailFacts": [
    { "key": "market", "label": "市场", "value": "US_STOCK" },
    { "key": "name", "label": "标的名称", "value": "CoreWeave" },
    { "key": "boards", "label": "所属榜单", "value": ["热门"] },
    { "key": "pctChange24h", "label": "24H 涨跌", "value": "11.89" },
    { "key": "range24h", "label": "24H 区间", "value": { "low": "83.12", "high": "92.52" } },
    { "key": "tags", "label": "标签", "value": ["AI", "夜盘"] }
  ],
  "tradeAction": {
    "enabled": true,
    "label": "交易",
    "target": "app://trade/us-stock:crwv"
  }
}
```

### 5.3 Candle history / stream 最低要求

`GET /market/instruments/{instrumentId}/candles?timeframe=1h&limit=200`

```json
{
  "serverTime": 1775644800000,
  "instrumentId": "us-stock:crwv",
  "timeframe": "1h",
  "candles": [
    {
      "openTime": 1775641200000,
      "closeTime": 1775644799999,
      "open": "84.90",
      "high": "92.52",
      "low": "83.12",
      "close": "90.01",
      "volume": "1820440",
      "turnover": "160232991.40",
      "closed": true
    }
  ],
  "indicatorSeries": {
    "MA": [],
    "BOLL": [],
    "MACD": [],
    "KDJ": [],
    "RSI": [],
    "WR": []
  }
}
```

`WS market.candles.{instrumentId}.{timeframe}`

- 用于推送最新未收盘 bar 的更新和收盘确认。
- 切 timeframe 时客户端应取消旧订阅并订阅新 timeframe。
- 断线重连后必须先补一次最近 N 根 candle，避免图表断档。

## 6. 刷新策略总表

| 数据类型 | 首次加载 | 可见态刷新 | 切前台/恢复 | 断线 fallback |
|---|---|---|---|---|
| Overview snapshot | 必须 | 不依赖持续轮询 | 必须重拉 | 30-60s 轮询 |
| 搜索结果 | query 变化时 | debounce 触发 | 可不主动重拉 | 保持最后结果 |
| Ticker 行情 | 先用 REST | WebSocket / push，UI 250-1000ms 合并刷新 | 重新订阅 | 5-10s 轮询 |
| Ranking / 热度 / 新币榜 | 先用 REST | 15-60s 级刷新即可 | 重拉 | 30-60s 轮询 |
| Spotlight | 先用 REST | 30-60s 级刷新即可 | 重拉 | 30-60s 轮询 |
| Detail snapshot | 必须 | 不要求逐秒 REST | 切前台重拉 | 5 分钟轮询 |
| Candle history | timeframe 进入时必须 | 当前 timeframe 的最新 bar 需 live 更新 | 重新拉最近 N 根 | 5-10s 补拉最近 N 根 |
| Favorites | 首次进入拉取 | 写后立即本地回写并等待服务端确认 | 重拉 | 失败则回滚本地状态 |

## 7. 明确排除项

以下内容不算真实市场接口，必须排除：

- `marketSampleQuotes`
- `marketSampleSpotlights`
- `marketSampleQuoteDetail()`
- `buildMarketCandles()`
- 任何只返回 `"$71,220.00"`、`"+1.77%"`、`"热度 99"`、`"$69,880 - $71,540"` 这类展示字符串的接口
- 任何把 `MarketQuoteDetail.thesis` 之类实现说明文案当成真实市场内容下发的接口
- 任何要求登录才能读取 `MarketOverviewPage` / `MarketQuoteDetailPage` 的只读行情接口

## 8. 最小落地结论

如果只保留当前 Compose `Market` 两页，生产环境最少需要这些真实接口：

1. `GET /market/overview`
2. `GET /market/search`
3. `GET /market/spotlights`
4. `GET /market/favorites`
5. `PUT /market/favorites/{instrumentId}`
6. `DELETE /market/favorites/{instrumentId}`
7. `GET /market/instruments/{instrumentId}`
8. `GET /market/instruments/{instrumentId}/candles?timeframe=...`
9. `WS market.tickers.batch`
10. `WS market.ticker.{instrumentId}`
11. `WS market.candles.{instrumentId}.{timeframe}`
12. `GET /market/rankings` 或等价 push stream

缺少上述任一类数据后，对应页面会退化为：

- 没有 `overview/search/spotlights/rankings`：`MarketOverviewPage` 只剩假列表
- 没有 `detail/ticker/candles`：`MarketQuoteDetailPage` 只剩假详情
- 没有公开只读权限：当前 `Market` tab 的访客可读设计会直接失效
