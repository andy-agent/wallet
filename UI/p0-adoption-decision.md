# P0 UI 采用结论

## 结论

采用用户提供的 `cryptovpn_p0_ui_pack.zip` 作为当前 `/UI` 的**主事实源**。

仓库原有 `/UI/pages` 保留，但降级为 legacy 参考，不再作为后续 Android / Compose 嵌套的首选页面来源。

## 对比结果

### 页面与 Android 功能匹配度

ZIP 包的 11 页中：

- 10 页与当前 Android / Compose 实际功能直接匹配
- 1 页可部分承接主屏总览

对应关系：

1. `01_splash` -> `SplashScreen`
2. `02_login` -> `EmailLoginPage`
3. `03_wallet_onboarding` -> `WalletOnboardingPage`
4. `04_unified_home` -> `VPNHomePage` 的统一总览方向
5. `05_region_selection` -> `RegionSelectionPage`
6. `06_plans` -> `PlansPage`
7. `07_wallet_home` -> `WalletHomePage`
8. `08_asset_detail` -> `AssetDetailPage`
9. `09_send` -> `SendPage`
10. `10_receive` -> `ReceivePage`
11. `11_wallet_payment_confirm` -> `WalletPaymentConfirmPage`

原有 `/UI/pages` 的问题：

- 只有 6 页能直接承接 Android 主功能
- `03-plan-guide`、`06-purchase-confirm` 不是最贴近实际 App route 的命名和结构
- `05-market-monitor` 不在当前 Android 主导航里

### 视觉与实现密度

对比结果：

- 原有 `/UI/pages`：11 页，`animation:` 0 次，`@keyframes` 0 个
- ZIP 包：11 页，`animation:` 55 次，`@keyframes` 44 个

这说明 ZIP 包不仅页面范围更贴近目标功能，也包含更完整的动态层、粒子、发光、脉冲和扫描感效果。

### 交付形态

ZIP 包同时包含：

- 动态 HTML
- 对应 PNG
- 总览 PNG
- 独立入口 `prototype_index.html`

这比原有 `/UI/pages` 更适合作为“视觉事实源 + 交付包”。

## 采用方式

已将 ZIP 内容导入：

- `/UI/p0-pack/`

并将以下内容保留：

- `/UI/pages/` 旧版原型
- `/UI/legacy-index.html` 旧版入口
- `/UI/app-ui-gap-audit.md` 缺口审计
- `/UI/compose-integration-notes.md` Compose 接入说明

## 后续使用原则

1. 页面视觉以 `/UI/p0-pack/` 为准
2. 嵌套 Android / Compose 时，以 `vpnui/MainActivity -> AppNavGraph` 的实际路由为准
3. 旧版 `/UI/pages` 只作为参考，不作为主设计源
