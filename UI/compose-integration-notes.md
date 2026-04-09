# Android / Compose Integration Notes

## 目标

`/UI` 当前承担的是 **视觉真相层**：

- 先把 PNG 原型拆成稳定页面
- 再把页面还原成可复用的 Compose 区块
- 最后嵌回 `vpnui` 工程

## 画板到 Compose 的建议换算

- PNG 画板：`1080 x 2400`
- Compose 基准宽度建议：`360dp`
- 对应倍率：约 `3x`

这意味着：

- `24px` 约等于 `8dp`
- `36px` 约等于 `12dp`
- `48px` 约等于 `16dp`
- `72px` 约等于 `24dp`

## 主题建议

当前 PNG 已经明确偏向浅色体系，因此 Compose 里不建议继续强套旧 dark token。

建议先新增一套 light token：

- `LightBackgroundBase`
- `LightBackgroundElevated`
- `LightCardSurface`
- `LightBorderSubtle`
- `LightTextPrimary`
- `LightTextSecondary`
- `LightAccentBlue`
- `LightAccentCyan`
- `LightAccentMint`
- `LightAccentViolet`

## 页面落地顺序

1. 先做 `04-control-plane` 和 `05-market-monitor`
2. 再做 `08-asset-detail`、`09-send-asset`、`10-receive-asset`、`11-wallet-payment-confirm`
3. 最后补 `01/02/03/06/07` 的启动、接入和支付前链路页

## Compose Screen 建议

- `SplashScreen`
- `WalletConnectScreen`
- `PlanGuideScreen`
- `ControlPlaneScreen`
- `MarketMonitorScreen`
- `PurchaseConfirmScreen`
- `WalletHomeScreen`
- `AssetDetailScreen`
- `SendAssetScreen`
- `ReceiveAssetScreen`
- `WalletPaymentConfirmScreen`

## 共享 Composable 建议

- `GlassCard`
- `MetricChip`
- `SegmentTabs`
- `SearchFieldShell`
- `RiskPromptCard`
- `CoinListItem`
- `ActionGridItem`
- `BottomNavBar`
- `PrimaryCtaButton`

## 嵌入 `vpnui` 时的注意点

- 背景不要直接照搬 Web 的大面积模糊效果，应改成 Compose 可控的 radial gradient + alpha layer
- 图表区先用占位 Composable，还原版式，再决定接真实 chart 方案
- 发送 / 收款 / 支付确认页都应先把状态模型整理成 data class，再画 UI
- 当前 `/UI` 是高保真静态原型，不代表最终交互动效已经定稿
