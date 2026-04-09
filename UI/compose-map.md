# Compose Mapping

这份映射用于后续把 `/UI` 原型迁移进 Android / Compose。

## 共享区块

- `StatusBarShell`: 顶部状态栏占位
- `TopHeroSection`: 眉标、标题、副标题、右侧状态徽章
- `SearchFieldCard`: 搜索框
- `PrimarySegmentTabs`: 一级 tab
- `SecondarySegmentTabs`: 二级 tab
- `GlassSurfaceCard`: 主要白色卡片
- `MetricMiniCard`: 小指标卡
- `LeaderboardListItem`: 榜单/币种列表项
- `RiskPromptCard`: 风险提示 / 异动 / AI 分析区
- `BottomActionBar`: 底部 CTA
- `BottomNavigationBar`: 底栏导航

## 页面建议拆分

### `04-control-plane`

- `OverviewHeroCard`
- `QuickActionGrid`
- `RealtimeAlertList`
- `HomeBottomNav`

### `05-market-monitor`

- `MarketSearchHeader`
- `MarketPrimaryTabs`
- `MarketSectionCard`
- `MarketWatchlistCard`

### `08-asset-detail`

- `AssetPriceHero`
- `AssetTrendChart`
- `RiskSignalList`
- `AiInsightCard`

### `09-send-asset`

- `AssetSelectorField`
- `WalletAddressField`
- `NetworkSegmentTabs`
- `SendAmountCard`
- `SecurityChecklistCard`

### `10-receive-asset`

- `ReceiveTokenTabs`
- `QrReceiveCard`
- `ReceiveAddressCard`

### `11-wallet-payment-confirm`

- `PaymentOrderSummaryCard`
- `BalanceBenefitRow`
- `PaymentRiskPromptCard`
