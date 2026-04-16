# Android 组件映射表

> 基于 [CRYPTO_VPN_ANDROID_TARGET_UI_VISUAL_SYSTEM_2026-04-16.md](/Users/cnyirui/git/projects/liaojiang/docs/CRYPTO_VPN_ANDROID_TARGET_UI_VISUAL_SYSTEM_2026-04-16.md)。
> 目标：明确未来 Android UI 组件在 Compose 中的命名、是否需要 XML 版本，以及首批替换页面。

## 1. 命名原则

### Compose 命名规则

- 基础组件统一使用 `App` 前缀
- 业务组合组件使用领域前缀，例如 `AssetRow`、`NodeRow`
- 页面骨架统一使用 `App*Page` 或 `App*Scaffold`

### XML 命名规则

- layout 组件文件使用 `view_` 前缀
- shape / background 使用 `bg_` 前缀
- style 使用 `Widget.CryptoVpn.*` 或 `TextAppearance.CryptoVpn.*`

## 2. 基础组件映射

| 目标规范组件 | Compose 建议名 | 是否需要 XML 版本 | XML 建议名 | 首批替换页面 |
|---|---|---|---|---|
| Primary Button | `AppPrimaryButton` | 是 | `view_primary_button.xml` / `Widget.CryptoVpn.Button.Primary` | `InviteCenterPage`, `InviteSharePage`, `VpnHomePage`, `WalletHomePage` |
| Secondary Button | `AppSecondaryButton` | 是 | `view_secondary_button.xml` / `Widget.CryptoVpn.Button.Secondary` | `InviteCenterPage`, `InviteSharePage`, `VpnHomePage`, `WalletHomePage` |
| Ghost Button / Text Button | `AppGhostButton` | 是 | `Widget.CryptoVpn.Button.Ghost` | `InviteCenterPage`, `InviteSharePage`, `VpnHomePage`, `WalletHomePage` |
| App Card | `AppCard` | 是 | `view_app_card.xml` / `bg_card_surface.xml` | `InviteCenterPage`, `InviteSharePage`, `VpnHomePage`, `WalletHomePage` |
| Glass Highlight Card | `AppHighlightCard` | 可后置 | `view_highlight_card.xml` / `bg_card_highlight.xml` | 第一批扩展试点：`VpnHomePage`, `WalletHomePage` |
| Status Chip | `AppStatusChip` | 是 | `view_status_chip.xml` / `bg_status_chip.xml` | `InviteCenterPage`, `InviteSharePage`, `VpnHomePage`, `WalletHomePage` |
| Filter Chip | `AppFilterChip` | 是 | `view_filter_chip.xml` / `bg_filter_chip.xml` | 第二批：`RegionSelectionPage` |
| App Text Field | `AppTextField` | 是 | `Widget.CryptoVpn.TextField` | 第二批：`LoginActivity`, `WithdrawalActivity`, `InvitationCenterActivity` |
| Token Selector Field | `AppTokenSelectorField` | 可后置 | `view_token_selector_field.xml` | 第二批：`SendPage`, `WalletPaymentPage` |
| Page Top Bar | `AppTopBar` | 是 | `view_page_top_bar.xml` / `Widget.CryptoVpn.TopBar` | `InviteCenterPage`, `InviteSharePage`, `VpnHomePage`, `WalletHomePage` |
| Hero Header | `AppHeroHeader` | 可后置 | `view_hero_header.xml` | 第二批：`EmailLoginPage`, `WalletOnboardingPage` |
| Standard List Item | `AppListItem` | 是 | `view_list_item.xml` | 第二批：`ProfilePage`, `LegalDocumentsPage` |
| Metric List Item | `AppMetricListItem` | 是 | `view_metric_list_item.xml` | 第二批：`CommissionLedgerPage`, `AssetDetailPage` |
| Empty State Card | `AppEmptyStateCard` | 是 | `view_empty_state.xml` | 第二批：`LegalDocumentsPage`, `activity_plans.xml` |

## 3. 组合组件映射

| 目标规范组件 | Compose 建议名 | 是否需要 XML 版本 | XML 建议名 | 首批替换页面 |
|---|---|---|---|---|
| LabelValueRow | `AppLabelValueRow` | 是 | `view_label_value_row.xml` | 第一批：`InviteCenterPage`；第二批：`activity_payment.xml`, `activity_user_profile.xml` |
| Metric Card | `AppMetricCard` | 是 | `view_metric_card.xml` | 第一批：`InviteCenterPage`, `InviteSharePage`, `VpnHomePage`, `WalletHomePage` |
| Action Cluster | `AppActionCluster` | 可后置 | `view_action_cluster.xml` | 第一批：`InviteCenterPage`, `InviteSharePage`, `VpnHomePage`, `WalletHomePage` |
| Info Section | `AppInfoSection` | 是 | `view_info_section.xml` | 第一批：`InviteCenterPage`, `InviteSharePage`, `VpnHomePage`, `WalletHomePage` |

## 4. 业务组合组件映射

| 目标规范组件 | Compose 建议名 | 是否需要 XML 版本 | XML 建议名 | 首批替换页面 |
|---|---|---|---|---|
| AssetRow | `AssetRow` | 暂不需要 | 无 | 第一批扩展试点：`WalletHomePage`；第二批：`AssetDetailPage` |
| NodeRow | `NodeRow` | 暂不需要 | 无 | 第二批：`RegionSelectionPage` |
| PaymentSummaryCard | `PaymentSummaryCard` | 是 | `view_payment_summary_card.xml` | 第二批：`WalletPaymentConfirmPage`, `activity_payment.xml` |
| OrderSummaryCard | `OrderSummaryCard` | 是 | `view_order_summary_card.xml` | 第二批：`OrderCheckoutPage`, `OrderDetailPage`, `activity_payment.xml` |
| SecurityStatusCard | `SecurityStatusCard` | 暂不需要 | 无 | 第二批：`SecurityCenterPage` |
| QrAddressCard | `QrAddressCard` | 是 | `view_qr_address_card.xml` | 第二批：`ReceivePage`, `WalletPaymentConfirmPage`, `activity_payment.xml` |

## 5. 页面骨架映射

| 页面骨架类型 | Compose 建议名 | 是否需要 XML 版本 | XML 对应策略 | 首批替换页面 |
|---|---|---|---|---|
| Splash / Brand Page | `AppSplashPageShell` | 暂不动 | 暂不统一 | 不进入第一阶段 |
| Hero Form Page | `AppHeroFormPage` | 可后置 | `view_hero_form_container.xml` | 第二批：`EmailLoginPage`, `WalletOnboardingPage`, `activity_login.xml` |
| Dashboard Page | `AppDashboardPage` | 暂不需要 | 暂不统一 | 第一批扩展试点：`VpnHomePage`, `WalletHomePage` |
| Selection List Page | `AppSelectionListPage` | 可后置 | `view_selection_page_shell.xml` | 第二批：`RegionSelectionPage` |
| Subscription / Pricing Page | `AppPricingPage` | 是 | `view_pricing_page_shell.xml` | 第二批：`PlansPage`, `activity_plans.xml` |
| Asset Detail Page | `AppAssetDetailPage` | 暂不需要 | 暂不统一 | 第二批：`AssetDetailPage` |
| Transaction Form Page | `AppTransactionFormPage` | 是 | `view_transaction_form_shell.xml` | 第二批：`SendPage`, `ReceivePage`, `activity_withdrawal.xml` |
| Confirmation Page | `AppConfirmationPage` | 是 | `view_confirmation_page_shell.xml` | 第二批：`WalletPaymentConfirmPage`, `OrderCheckoutPage`, `activity_payment.xml` |

## 6. XML 版本需求判断规则

### 需要 XML 版本

满足以下任一条件就需要 XML 版本：

- 当前已有 XML 页面正在使用这类视觉结构
- 中短期内不会把该业务页面迁到 Compose
- 这个组件在支付、邀请、个人中心、提现等 legacy Activity 中重复出现

### 可后置或暂不需要 XML 版本

满足以下条件可先只做 Compose：

- 组件当前只服务 Compose 页面
- 组件造型高度定制，XML 复用价值低
- 第一阶段不会触达对应 legacy 页面

## 7. 首批替换页面建议

### 第一批试点

- `code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/composeui/pages/p2/InviteCenterPage.kt`
- `code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/composeui/pages/p2/InviteSharePage.kt`
- `code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/composeui/pages/p0/VpnHomePage.kt`
- `code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/composeui/pages/p0/WalletHomePage.kt`

原因：

- 逻辑薄
- 风险低
- 同属一套骨架
- 可同时验证卡片、状态标签、指标卡、复制/分享动作区、顶部栏
- 同时补入 Dashboard 类型页面，验证网络总览与资产总览在同一视觉系统下的复用度

### 第二批页面

- Compose：
  - `RegionSelectionPage`
  - `WalletPaymentConfirmPage`
  - `OrderCheckoutPage`
  - `ProfilePage`
  - `AssetDetailPage`
- XML：
  - `activity_payment.xml`
  - `activity_user_profile.xml`
  - `activity_invitation_center.xml`
  - `activity_withdrawal.xml`

## 8. 第一阶段建议优先创建的 Compose 组件

### P0

- `AppPrimaryButton`
- `AppSecondaryButton`
- `AppGhostButton`
- `AppCard`
- `AppStatusChip`
- `AppTopBar`
- `AppLabelValueRow`
- `AppMetricCard`
- `AppInfoSection`

### P1

- `AppActionCluster`
- `AppFilterChip`
- `AppEmptyStateCard`
- `PaymentSummaryCard`
- `QrAddressCard`

### P2

- `AppTextField`
- `AppTokenSelectorField`
- `AppHeroHeader`
- `AppListItem`
- `AppMetricListItem`

## 9. 与当前工程骨架的关系

### 推荐主入口

- `AppPageScaffold`

### 当前骨架适配方式

- `P01PhoneScaffold`：作为主骨架兼容层
- `P2CorePageScaffold`：包一层统一视觉接口
- `P2ExtendedPageScaffold`：暂不删除，只逐步替换内部视觉组件

### 不建议继续扩散的旧命名

- 不再新增 `P01* / P2Core* / P2Extended*` 风格的新基础视觉组件
- 新组件统一用 `App*`

## 10. 组件映射落地顺序

1. 先定义 token
2. 再创建 `AppButton / AppCard / AppChip / AppTopBar`
3. 再创建 `AppLabelValueRow / AppMetricCard / AppInfoSection`
4. 先把 `InviteCenterPage / InviteSharePage` 切到新组件，再把 `VpnHomePage / WalletHomePage` 作为扩展试点接入
5. 再评估 XML 对应版本的最小集合
