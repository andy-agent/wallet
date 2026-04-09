# Master Pack UI 缺口审计

## 结论

基于 `cryptovpn_ui_master_pack_p0_p1_p2.zip`：

- 对 `vpnui/MainActivity -> AppNavGraph` 当前已接线的 **25 个主页面**，这个包已经做到 **页面级全覆盖**
- 对已显式存在的补充 UI 模块，`OptionalUpdateDialog` 已覆盖
- 仍然**明确缺少**的现有 UI 模块只有 `SessionEvictedDialog`

但如果把范围扩展到“代码里已经暴露需求、但还没正式接路由的页面/弹层/系统页”，仍然还有一批缺口。

## 覆盖情况

### 1. 当前主导航已接线页面

当前 `AppNavGraph` 已接线页面共 25 个，master pack 已全部覆盖：

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

代码入口：

- `vpnui/MainActivity.kt`
- `vpnui/navigation/AppNavGraph.kt`

### 2. 已显式存在的补充 UI 模块

master pack 已覆盖：

1. `OptionalUpdateDialog`

master pack 未覆盖：

1. `SessionEvictedDialog`

## 仍缺少的页面

### A. 按当前已实现 UI / 页面模块来算

如果以仓库中已经存在的页面或弹层模块为准，当前只剩下这 1 个明确缺口：

1. `SessionEvictedDialog`

代码依据：

- `vpnui/ui/pages/legal/SessionEvictedDialog.kt`

### B. 按已声明 route 但未接入主导航来算

还有 1 个路由级缺口：

1. `wallet_payment`

代码依据：

- `vpnui/navigation/Routes.kt` 中声明了 `WALLET_PAYMENT`
- 但 `vpnui/navigation/AppNavGraph.kt` 当前没有对应 composable
- master pack 也没有对应页面图

### C. 按产品/交互暴露出的隐含页面来算

这些页面或系统 UI 在代码里已经暴露出明确需求，但 master pack 里仍没有独立页面图：

1. App 设置页
2. Wallet 设置页
3. 关于页
4. 编辑资料页
5. 订阅管理页
6. 提现历史页
7. 支持 / 帮助 / 反馈页
8. 区块浏览器 / 交易详情外跳确认页

这些是从以下代码入口反推出来的：

- `VPNHomePage` 的 `onSettingsClick`
- `WalletHomePage` 的 `onSettingsClick`
- `WithdrawPage` 的 `onViewHistoryClick`
- `ProfilePage` 的 `onEditProfileClick`
- `ProfilePage` 的 `onManageSubscriptionClick`
- `ProfilePage` 的 `onNavigateToAbout`
- `SendResultPage` 的 `onViewTransactionClick`
- `OrderDetailPage` 的支持入口

## master pack 额外新增但当前主导航未接线的页面

P2 额外补了以下高级钱包能力设计：

1. Import Wallet
2. Import Mnemonic
3. Backup Phrase
4. Confirm Phrase
5. Security Center
6. Chain Manager
7. Add Token
8. Swap
9. Bridge
10. DApp Browser
11. WalletConnect
12. Sign Request

这说明这个包已经超出当前 `AppNavGraph` 的范围，开始覆盖下一阶段的钱包能力。

## 需要注意的限制

master pack 的 P1 / P2 目前主要是：

- PNG 成稿
- gallery 入口页

不像当前 `/UI/p0-pack/` 那样带完整动态 HTML 页面。

所以如果后续要做 Compose 还原：

- 视觉事实源可以直接参考这些 PNG
- 但如果你需要浏览器里可运行的动态参考，P1 / P2 目前还没有对应 HTML 版本

## 最终答案

如果只问“这个增量包之后，还缺哪些页面”：

### 页面级明确缺口

1. `SessionEvictedDialog`
2. `wallet_payment`

### 交互/系统页缺口

1. App 设置页
2. Wallet 设置页
3. 关于页
4. 编辑资料页
5. 订阅管理页
6. 提现历史页
7. 支持 / 帮助 / 反馈页
8. 区块浏览器 / 外跳确认页

### 可选产品缺口

1. `market-monitor`
   - 旧版 legacy 里有
   - 当前 Android 主导航里没有
   - 是否要恢复，取决于产品是否还保留这个模块
