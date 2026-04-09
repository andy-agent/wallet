# Android APP 功能盘点与 UI 缺口审计

## 结论先行

当前 `vpnui` 实际接线的 Compose App 以 `MainActivity -> AppNavGraph` 为准。

- 已有 `/UI` 原型可以直接或近似承接的页面：`01 / 07 / 08 / 09 / 10 / 11`
- 只能部分承接、仍需补专用 UI 的页面：`02 / 03 / 04 / 06`
- 仍然**完全缺失**的页面 / 弹层 / 结果态较多，不能直接进入整包嵌套

另外有一个反向情况：

- `/UI/05-market-monitor.html` 当前在 `vpnui` 的主导航里 **没有对应 route**
- 这说明它现在是产品原型页，不是现有 Android App 已接线功能

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
   - 对应 `/UI/pages/01-splash.html`
2. `WalletHomePage`
   - 对应 `/UI/pages/07-wallet-home.html`
3. `AssetDetailPage`
   - 对应 `/UI/pages/08-asset-detail.html`
4. `SendPage`
   - 对应 `/UI/pages/09-send-asset.html`
5. `ReceivePage`
   - 对应 `/UI/pages/10-receive-asset.html`
6. `WalletPaymentConfirmPage`
   - 对应 `/UI/pages/11-wallet-payment-confirm.html`

### B. 只有部分覆盖，不能直接套用的页面

这些页面虽然在 `/UI` 中有相近原型，但**功能模型不完全一致**，嵌套前必须补专用页面：

1. `EmailLoginPage`
   - `/UI/pages/02-login.html` 当前更像“钱包连接 / 接入页”
   - 但 App 真实登录流是邮箱登录
2. `PlansPage`
   - `/UI/pages/03-plan-guide.html` 更偏套餐引导
   - 还缺标准套餐对比、选择态、状态标签细化
3. `VPNHomePage`
   - `/UI/pages/04-control-plane.html` 是更大的总览控制面
   - 但现有 App 的 VPN 首页还包含连接模式、节点连接、Profile 入口等专用交互
4. `OrderCheckoutPage`
   - `/UI/pages/06-purchase-confirm.html` 只覆盖了摘要确认
   - 还未完整覆盖收款地址、支付状态刷新、复制地址、已支付检查等收银台交互

## 真正缺失的页面 UI

以下页面在当前 `/UI` 中没有对应高保真原型，应视为**嵌套前缺口**。

### 1. 启动与认证缺口

1. `ForceUpdatePage`
2. `OptionalUpdateDialog`
3. `EmailRegisterPage`
4. `ResetPasswordPage`

### 2. VPN 交易链路缺口

1. `RegionSelectionPage`
2. `OrderResultPage`
3. `OrderListPage`
4. `OrderDetailPage`

### 3. 钱包链路缺口

1. `WalletOnboardingPage`
2. `SendResultPage`

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

## 当前 `/UI` 有，但 APP 里没有对应主路由的页面

1. `05-market-monitor.html`
   - 当前 `AppNavGraph` 和活跃 `Routes.kt` 都没有 `market` / `monitor` 主页面
   - 如果产品确认市场监控仍是核心功能，则 Android App 自身也需要先补 route 和信息架构

## 嵌套前的建议顺序

### 第一批：先能跑通主链路

1. `VPNHomePage` 专用 UI
2. `PlansPage`
3. `OrderCheckoutPage`
4. `OrderResultPage`
5. `EmailLogin / Register / ResetPassword`
6. `WalletOnboardingPage`
7. `ProfilePage`

### 第二批：补完整钱包与订单链路

1. `RegionSelectionPage`
2. `OrderListPage`
3. `OrderDetailPage`
4. `SendResultPage`
5. `ForceUpdatePage`
6. `OptionalUpdateDialog`

### 第三批：补增长、法务、系统状态页

1. `InviteCenterPage`
2. `CommissionLedgerPage`
3. `WithdrawPage`
4. `LegalDocumentsListPage`
5. `LegalDocumentDetailPage`
6. `SessionEvictedDialog`

## 最终结论

如果现在就开始把 `/UI` 整体嵌进 Android / Compose：

- 可以先落地 6 个页面的高保真重做
- 4 个页面只能做“部分套壳”
- 还有 15 个以上的页面 / 弹层 / 结果态没有对应 UI

所以，**当前真正的阻塞不是嵌套技术，而是页面覆盖率不足**。

在开始嵌套前，人类需要先确认：

1. `05-market-monitor` 是否要进入 Android 主导航
2. 认证是继续邮箱体系，还是改成钱包接入体系
3. 是否把设置、帮助、关于、提现历史、钱包支付请求页视为本轮必须补齐的正式页面
