# Android APP 功能盘点与 UI 缺口审计

## 结论先行

当前 `vpnui` 实际接线的 Compose App 以 `MainActivity -> AppNavGraph` 为准。

当前 `/UI` 已采纳的主事实源是：

- `/UI/p0-pack/`

基于这套已采纳页面：

- 可以**直接承接** 10 个 Android 主页面
- 可以**部分承接** 1 个主页面
- 仍然**完全缺失**若干订单结果、增长、法务和系统状态页，不能直接进入整包嵌套

## 判定依据

### 实际入口

- `MainActivity` 直接挂载 `com.cryptovpn.navigation.AppNavGraph`
- 说明当前活跃导航不是 `ui/navigation/AppNavigation.kt`，而是 `navigation/AppNavGraph.kt`

### 活跃功能面

`AppNavGraph` 当前接线的主页面：

1. Splash
2. Force Update
3. Email Login
4. Email Register
5. Reset Password
6. VPN Home
7. Plans
8. Region Selection
9. Order Checkout
10. Order Result
11. Order List
12. Order Detail
13. Wallet Onboarding
14. Wallet Home
15. Asset Detail
16. Receive
17. Send
18. Send Result
19. Wallet Payment Confirm
20. Invite Center
21. Commission Ledger
22. Withdraw
23. Profile
24. Legal Documents List
25. Legal Document Detail

代码依据：

- `vpnui/MainActivity.kt`
- `vpnui/navigation/AppNavGraph.kt`
- `vpnui/navigation/Routes.kt`

## `/UI` 当前覆盖情况

### A. 可直接承接的页面

这些页面已经有较明确的 `/UI` 对应物，可以作为 Compose 重做的主视觉来源：

1. `SplashScreen`
   - 对应 `/UI/p0-pack/01_splash.html`
2. `EmailLoginPage`
   - 对应 `/UI/p0-pack/02_login.html`
3. `WalletOnboardingPage`
   - 对应 `/UI/p0-pack/03_wallet_onboarding.html`
4. `RegionSelectionPage`
   - 对应 `/UI/p0-pack/05_region_selection.html`
5. `PlansPage`
   - 对应 `/UI/p0-pack/06_plans.html`
6. `WalletHomePage`
   - 对应 `/UI/p0-pack/07_wallet_home.html`
7. `AssetDetailPage`
   - 对应 `/UI/p0-pack/08_asset_detail.html`
8. `SendPage`
   - 对应 `/UI/p0-pack/09_send.html`
9. `ReceivePage`
   - 对应 `/UI/p0-pack/10_receive.html`
10. `WalletPaymentConfirmPage`
   - 对应 `/UI/p0-pack/11_wallet_payment_confirm.html`

### B. 只有部分覆盖，不能直接套用的页面

这些页面虽然在 `/UI` 中有相近原型，但**功能模型不完全一致**，嵌套前必须补专用页面：

1. `VPNHomePage`
   - `/UI/p0-pack/04_unified_home.html` 更像“统一控制面 / 总览首页”
   - 能承接视觉方向，但还未拆成专用 VPN 主屏状态与交互

## 真正缺失的页面 UI

以下页面在当前 `/UI` 中没有对应高保真原型，应视为**嵌套前缺口**。

### 1. 启动与认证缺口

1. `ForceUpdatePage`
2. `OptionalUpdateDialog`
3. `EmailRegisterPage`
4. `ResetPasswordPage`

### 2. VPN 交易链路缺口

1. `OrderCheckoutPage`
2. `OrderResultPage`
3. `OrderListPage`
4. `OrderDetailPage`

### 3. 钱包链路缺口

1. `SendResultPage`

### 4. 增长与分佣缺口

1. `InviteCenterPage`
2. `CommissionLedgerPage`
3. `WithdrawPage`

### 5. 我的与法务缺口

1. `ProfilePage`
2. `LegalDocumentsListPage`
3. `LegalDocumentDetailPage`
4. `SessionEvictedDialog`

## 路由外的隐含 UI 缺口

这些不一定已经是独立 route，但从当前 Compose 回调和注释看，嵌套时仍会卡住：

1. App 设置页
   - `VPNHomePage` 的 `onSettingsClick`
2. Wallet 设置页
   - `WalletHomePage` 的 `onSettingsClick`
3. 资产选择器
   - `SendPage` 的 `onAssetSelect`
4. 发送扫码入口
   - `SendPage` 的 `onScanClick`
5. 区块浏览器查看页 / 外跳确认
   - `SendResultPage` 的 `onViewTransactionClick`
   - `AssetDetailPage` 里也有浏览器入口
6. 提现历史页
   - `WithdrawPage` 的 `onViewHistoryClick`
7. 编辑资料页
   - `ProfilePage` 的 `onEditProfileClick`
8. 订阅管理页
   - `ProfilePage` 的 `onManageSubscriptionClick`
9. 关于页
   - `ProfilePage` 的 `onNavigateToAbout`
10. 支持 / 帮助反馈页
   - `OrderDetailPage` 有支持入口
   - 老版 `ProfilePage` 也存在 Support 入口
11. 钱包支付请求页
   - `Routes.kt` 中有 `wallet_payment`
   - 但当前 `AppNavGraph` 并没有接进主导航

## Legacy 中存在但当前主事实源未采用的页面

1. `/UI/pages/05-market-monitor.html`
   - 这是旧版探索稿中的市场监控页
   - 当前 `AppNavGraph` 和活跃 `Routes.kt` 都没有 `market` / `monitor` 主页面
   - 因此它没有进入已采纳的 `p0-pack/`

## 嵌套前的建议顺序

### 第一批：先能跑通主链路

1. `VPNHomePage` 专用 UI
2. `OrderCheckoutPage`
3. `OrderResultPage`
4. `EmailRegister / ResetPassword / ForceUpdate / OptionalUpdate`
5. `ProfilePage`

### 第二批：补完整钱包与订单链路

1. `OrderListPage`
2. `OrderDetailPage`
3. `SendResultPage`
4. `InviteCenterPage`
5. `CommissionLedgerPage`
6. `WithdrawPage`

### 第三批：补增长、法务、系统状态页

1. `LegalDocumentsListPage`
2. `LegalDocumentDetailPage`
3. `SessionEvictedDialog`
4. 设置 / 帮助 / 关于 / 钱包支付请求等隐含系统页

## 最终结论

如果现在就开始把 `/UI` 整体嵌进 Android / Compose：

- 可以先落地 10 个页面的高保真重做
- 1 个页面只能做“部分套壳”
- 还有 16 个以上的页面 / 弹层 / 结果态没有对应 UI

所以，**当前真正的阻塞不是嵌套技术，而是页面覆盖率不足**。

在开始嵌套前，人类需要先确认：

1. `04_unified_home` 最终是单独的总览首页，还是继续拆回 `VPNHomePage`
2. 是否把设置、帮助、关于、提现历史、钱包支付请求页视为本轮必须补齐的正式页面
3. 是否需要把 legacy 的 `market-monitor` 重新纳入 Android 主导航
