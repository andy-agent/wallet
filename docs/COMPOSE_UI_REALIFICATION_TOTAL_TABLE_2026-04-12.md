# Compose UI 页面真实化总表

更新时间：2026-04-12

数据来源：
- [COMPOSE_UI_REALIFICATION_AUDIT_2026-04-11.md](/Users/cnyirui/git/projects/liaojiang/docs/COMPOSE_UI_REALIFICATION_AUDIT_2026-04-11.md)
- [COMPOSE_UI_REALIFICATION_EXECUTION_LOG_2026-04-11.md](/Users/cnyirui/git/projects/liaojiang/docs/COMPOSE_UI_REALIFICATION_EXECUTION_LOG_2026-04-11.md)

分类统计：
- A: 0
- B: 12
- C: 17
- D: 24
- 总页数: 53

说明：
- 本表以 2026-04-11 的逐页审计表为基线，并叠加 2026-04-12 已完成的真实运行证据。
- 当前没有任何页面达到 A。
- `是否残留 preview/mock/local` 一列记录的是生产路径仍存在的模板合同、本地聚合、占位路由参数、空壳状态或阻塞态占位，而不是 `@Preview` 本身。

## P0

| 页面 | 路由/文件 | 当前分类 | 数据是否真实 | 动作是否真实 | 状态是否完整 | 是否残留 preview/mock/local | 是否受后端阻塞 | 下一步动作 |
|---|---|---|---|---|---|---|---|---|
| Splash | `splash` / [SplashPage.kt](/Users/cnyirui/git/projects/liaojiang/code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/composeui/pages/p0/SplashPage.kt) | B | 部分。只读本地缓存用户、订单数、token 有效性。 | 部分。启动页真实读取本地态，但完成后固定导航到登录。 | 否。 | 是 | 否 | 接入真实 bootstrap/use case，按 auth/subscription/update 分流。 |
| EmailLogin | `email_login` / [EmailLoginPage.kt](/Users/cnyirui/git/projects/liaojiang/code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/composeui/pages/p0/EmailLoginPage.kt) | B | 部分。登录 seed 和动作真实。 | 是。登录调用真实 API。 | 否。 | 是 | 否 | 扩状态机为 loading/success/error/unavailable。 |
| EmailRegister | `email_register` / [EmailRegisterPage.kt](/Users/cnyirui/git/projects/liaojiang/code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/composeui/pages/p0/EmailRegisterPage.kt) | C | 部分。字段部分来自真实仓库，其余是合同模板。 | 部分。主动作不在当前页完成，而是跳旧 Activity。 | 否。 | 是 | 否 | 将旧登录页注册流迁入 Compose。 |
| ResetPassword | `reset_password` / [ResetPasswordPage.kt](/Users/cnyirui/git/projects/liaojiang/code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/composeui/pages/p0/ResetPasswordPage.kt) | C | 部分。字段部分真实，但主体是合同模板。 | 否。当前按钮只是假导航。 | 否。 | 是 | 否 | 新增 reset API 并接通 Compose。 |
| WalletOnboarding | `wallet_onboarding` / [WalletOnboardingPage.kt](/Users/cnyirui/git/projects/liaojiang/code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/composeui/pages/p0/WalletOnboardingPage.kt) | C | 否。空 `UiState`。 | 否。继续按钮只跳转。 | 否。 | 是 | 否 | 接钱包域真实模型。 |
| VpnHome | `vpn_home` / [VpnHomePage.kt](/Users/cnyirui/git/projects/liaojiang/code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/composeui/pages/p0/VpnHomePage.kt) | B | 部分。订阅/订单真实，但节点列表、本地 watch signal 和钱包汇总是假聚合。 | 部分。连接动作真，首页对象假。 | 否。 | 是 | 否 | 去本地聚合，接真实 VPN status/regions。 |
| WalletHome | `wallet_home` / [WalletHomePage.kt](/Users/cnyirui/git/projects/liaojiang/code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/composeui/pages/p0/WalletHomePage.kt) | B | 部分。用订单缓存伪装资产。 | 部分。收发动作只是跳其他页面。 | 否。 | 是 | 否 | 接真实钱包资产/链账户模型。 |
| ForceUpdate | `force_update` / [ForceUpdatePage.kt](/Users/cnyirui/git/projects/liaojiang/code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/composeui/pages/p0/ForceUpdatePage.kt) | D | 否。只有 BuildConfig。 | 否。按钮空实现。 | 否。 | 是 | 是 | 接真实版本检查与更新动作。 |
| OptionalUpdate | `optional_update` / [OptionalUpdatePage.kt](/Users/cnyirui/git/projects/liaojiang/code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/composeui/pages/p0/OptionalUpdatePage.kt) | D | 否。只有 BuildConfig。 | 否。按钮空实现。 | 否。 | 是 | 是 | 接真实版本检查与更新动作。 |

## P1

| 页面 | 路由/文件 | 当前分类 | 数据是否真实 | 动作是否真实 | 状态是否完整 | 是否残留 preview/mock/local | 是否受后端阻塞 | 下一步动作 |
|---|---|---|---|---|---|---|---|---|
| Plans | `plans` / [PlansPage.kt](/Users/cnyirui/git/projects/liaojiang/code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/composeui/pages/p1/PlansPage.kt) | B | 部分真实。主套餐列表真实，但页面仍保留状态机缺口。 | 部分真实。下单链路已接，但页内成功/失败反馈不完整。 | 否。 | 是 | 否 | 当前页直接完成真实选套餐和下单，并补 loading/error/retry。 |
| RegionSelection | `region_selection` / [RegionSelectionPage.kt](/Users/cnyirui/git/projects/liaojiang/code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/composeui/pages/p1/RegionSelectionPage.kt) | C | 否。 | 否。 | 否。 | 是 | 否 | 接真实区域/节点与选择动作。 |
| OrderCheckout | `order_checkout/{planId}` / [OrderCheckoutPage.kt](/Users/cnyirui/git/projects/liaojiang/code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/composeui/pages/p1/OrderCheckoutPage.kt) | C | 部分真实。 | 否。 | 否。 | 是 | 否 | 绑定真实 `paymentTarget`，移除模板支付块。 |
| WalletPaymentConfirm | `wallet_payment_confirm/{orderId}` / [WalletPaymentConfirmPage.kt](/Users/cnyirui/git/projects/liaojiang/code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/composeui/pages/p1/WalletPaymentConfirmPage.kt) | C | 部分真实。 | 否。 | 否。 | 是 | 否 | 仅渲染真实支付确认字段并补错误/空态。 |
| OrderResult | `order_result/{orderId}` / [OrderResultPage.kt](/Users/cnyirui/git/projects/liaojiang/code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/composeui/pages/p1/OrderResultPage.kt) | C | 部分真实。 | 部分真实。 | 否。 | 是 | 否 | 按真实 `order.status` 分支渲染并补状态机。 |
| OrderList | `order_list` / [OrderListPage.kt](/Users/cnyirui/git/projects/liaojiang/code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/composeui/pages/p1/OrderListPage.kt) | C | 部分真实。 | 否。 | 否。 | 是 | 否 | 定义真实列表 item model，补 empty/error/retry。 |
| OrderDetail | `order_detail/{orderId}` / [OrderDetailPage.kt](/Users/cnyirui/git/projects/liaojiang/code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/composeui/pages/p1/OrderDetailPage.kt) | C | 部分真实。 | 否。 | 否。 | 是 | 否 | 直接绑定真实订单字段并补动作。 |
| WalletPayment | `wallet_payment` / [WalletPaymentPage.kt](/Users/cnyirui/git/projects/liaojiang/code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/composeui/pages/p1/WalletPaymentPage.kt) | C | 部分真实。 | 否。 | 否。 | 是 | 否 | 先决定保留与否，再接真实钱包对象。 |

## P2 Core

| 页面 | 路由/文件 | 当前分类 | 数据是否真实 | 动作是否真实 | 状态是否完整 | 是否残留 preview/mock/local | 是否受后端阻塞 | 下一步动作 |
|---|---|---|---|---|---|---|---|---|
| AssetDetail | `asset_detail/{assetId}/{chainId}` / [AssetDetailPage.kt](/Users/cnyirui/git/projects/liaojiang/code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/composeui/pages/p2/AssetDetailPage.kt) | D | 否。以本地订单缓存聚合冒充钱包资产。 | 否。 | 否。 | 是 | 是 | 接真实钱包资产域，删 preview fallback 和页面硬编码余额。 |
| Receive | `receive/{assetId}/{chainId}` / [ReceivePage.kt](/Users/cnyirui/git/projects/liaojiang/code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/composeui/pages/p2/ReceivePage.kt) | D | 否。显示的是派生标签，不是真链地址。 | 否。 | 否。 | 是 | 是 | 接真实钱包地址、二维码和 copy/share 动作。 |
| Send | `send/{assetId}/{chainId}` / [SendPage.kt](/Users/cnyirui/git/projects/liaojiang/code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/composeui/pages/p2/SendPage.kt) | D | 否。仅把真实订单上下文当表单种子。 | 否。 | 否。 | 是 | 是 | 接真实转账、预检查、广播和失败处理。 |
| SendResult | `send_result/{txId}` / [SendResultPage.kt](/Users/cnyirui/git/projects/liaojiang/code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/composeui/pages/p2/SendResultPage.kt) | D | 否。结果核心对象仍是硬编码 `TX-9F32`。 | 否。 | 否。 | 是 | 是 | 绑定真实 tx 结果、确认数、失败态。 |
| InviteCenter | `invite_center` / [InviteCenterPage.kt](/Users/cnyirui/git/projects/liaojiang/code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/composeui/pages/p2/InviteCenterPage.kt) | B | 是。真实 `referral/overview`。 | 部分。页面主要是展示；绑定/分享动作未见真实实现。 | 部分。 | 是 | 否 | 补邀请绑定/分享动作与错误态。 |
| InviteShare | `invite_share` / [InviteSharePage.kt](/Users/cnyirui/git/projects/liaojiang/code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/composeui/pages/p2/InviteSharePage.kt) | C | 部分。邀请码真实，但分享对象和二维码语义错位。 | 否。 | 否。 | 是 | 否 | 生成真实分享链接与二维码，并接 copy/share。 |
| CommissionLedger | `commission_ledger` / [CommissionLedgerPage.kt](/Users/cnyirui/git/projects/liaojiang/code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/composeui/pages/p2/CommissionLedgerPage.kt) | B | 是。真实 summary + ledger。 | 部分。只读展示。 | 部分。 | 是 | 否 | 对齐真实分页/筛选/错误态。 |
| Withdraw | `withdraw` / [WithdrawPage.kt](/Users/cnyirui/git/projects/liaojiang/code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/composeui/pages/p2/WithdrawPage.kt) | B | 是。真实 summary + withdrawals。 | 部分。提现动作未真实绑定。 | 部分。 | 是 | 否 | 绑定 `createWithdrawal` 与表单校验。 |
| Profile | `profile` / [ProfilePage.kt](/Users/cnyirui/git/projects/liaojiang/code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/composeui/pages/p2/ProfilePage.kt) | B | 部分。真实 `me` + 本地订单缓存。 | 部分。页面跳安全中心/法务，但无真实账号动作。 | 部分。 | 是 | 否 | 用真实 profile domain，补退出/刷新等动作。 |
| LegalDocuments | `legal_documents` / [LegalDocumentsPage.kt](/Users/cnyirui/git/projects/liaojiang/code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/composeui/pages/p2/LegalDocumentsPage.kt) | C | 否。来自本地资源/配置。 | 部分。文档跳详情可用。 | 部分。 | 是 | 否 | 接真实 legal docs endpoint 或明确产品要求本地资源。 |
| LegalDocumentDetail | `legal_document_detail/{documentId}` / [LegalDocumentDetailPage.kt](/Users/cnyirui/git/projects/liaojiang/code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/composeui/pages/p2/LegalDocumentDetailPage.kt) | C | 否。来自本地资源/配置。 | 部分。外链动作已真实验证。 | 部分。 | 是 | 否 | 接真实 legal doc detail endpoint 或明确产品要求本地资源。 |
| AboutApp | `about_app` / [AboutAppPage.kt](/Users/cnyirui/git/projects/liaojiang/code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/composeui/pages/p2/AboutAppPage.kt) | B | 部分。版本/渠道来自 BuildConfig，账号来自缓存。 | 部分。帮助链接是真实 URL，但无真实版本策略。 | 部分。 | 是 | 否 | 若产品要求动态版本，接 app version endpoint。 |

## P2 Extended

| 页面 | 路由/文件 | 当前分类 | 数据是否真实 | 动作是否真实 | 状态是否完整 | 是否残留 preview/mock/local | 是否受后端阻塞 | 下一步动作 |
|---|---|---|---|---|---|---|---|---|
| SubscriptionDetail | `subscription_detail/{subscriptionId}` | B | 是。真实订阅上下文。 | 部分。路由当前未挂生产图。 | 否。 | 是 | 否 | 挂生产路由，补续费 CTA 和状态机。 |
| ExpiryReminder | `expiry_reminder/{daysLeft}` | B | 是。真实订阅 + 套餐价格。 | 部分。路由当前未挂生产图。 | 否。 | 是 | 否 | 挂生产路由，补续费 CTA 和状态机。 |
| NodeSpeedTest | `node_speed_test/{nodeGroupId}` | C | 否。`localServerSnapshots()` + 本地测速缓存。 | 否。 | 部分。 | 是 | 否 | 接真实测速/探测结果。 |
| AutoConnectRules | `auto_connect_rules` | C | 否。基于 tokenValid + 本地订单数。 | 否。 | 部分。 | 是 | 否 | 接本地持久化或后端策略。 |
| CreateWallet | `create_wallet/{mode}` | D | 否。仅本地钱包流程文案。 | 否。 | 否。 | 是 | 是 | 接真实本地钱包引擎、密钥保管、结果持久化。 |
| ImportWalletMethod | `import_wallet_method` | C | 部分。真实账户缓存 + 本地状态。 | 否。 | 部分。 | 是 | 否 | 接真实钱包导入流。 |
| ImportMnemonic | `import_mnemonic/{source}` | D | 否。主体助记词/链列表仍硬编码。 | 否。 | 否。 | 是 | 是 | 接真实助记词解析、派生、导入。 |
| ImportPrivateKey | `import_private_key/{chainId}` | D | 否。仅本地输入流程说明。 | 否。 | 否。 | 是 | 是 | 接真实私钥验证、地址派生、保存。 |
| BackupMnemonic | `backup_mnemonic/{walletId}` | D | 否。页面写死 12 个词。 | 否。 | 否。 | 是 | 是 | 接真实钱包种子读取与展示。 |
| ConfirmMnemonic | `confirm_mnemonic/{walletId}` | D | 否。抽查答案硬编码。 | 否。 | 否。 | 是 | 是 | 接真实助记词确认流程。 |
| SecurityCenter | `security_center` | C | 部分。仓库有真实会话/账户上下文，但页面主体硬编码。 | 否。 | 否。 | 是 | 否 | 接真实设备/授权/风险源并删硬编码卡片。 |
| ChainManager | `chain_manager/{walletId}` | D | 否。链列表硬编码。 | 否。 | 否。 | 是 | 是 | 接真实钱包链配置读写。 |
| AddCustomToken | `add_custom_token/{chainId}` | D | 否。仅本地手动录入流程。 | 否。 | 否。 | 是 | 是 | 接链上 metadata 查询和本地资产清单持久化。 |
| WalletManager | `wallet_manager/{walletId}` | D | 否。假定只有 1 个钱包。 | 否。 | 否。 | 是 | 是 | 接真实多钱包列表、切换、重命名、删除。 |
| AddressBook | `address_book/{mode}` | D | 否。明确未接持久化数据。 | 否。 | 否。 | 是 | 是 | 接本地地址簿数据库。 |
| GasSettings | `gas_settings/{chainId}` | D | 否。只显示本地空态。 | 否。 | 部分。 | 是 | 是 | 后端/钱包侧补估算服务，再接 UI。 |
| Swap | `swap/{fromAsset}/{toAsset}` | D | 否。仅本地钱包上下文。 | 否。 | 否。 | 是 | 是 | 补 DEX quote/execute 能力。 |
| Bridge | `bridge/{fromChainId}/{toChainId}` | D | 否。仅本地桥接状态。 | 否。 | 否。 | 是 | 是 | 补 bridge quote/execute。 |
| DappBrowser | `dapp_browser/{entry}` | D | 否。仅真实登录态 + 本地浏览上下文，页面列表硬编码。 | 否。 | 否。 | 是 | 是 | 接真实浏览器会话。 |
| WalletConnectSession | `wallet_connect_session/{sessionId}` | D | 否。仅本地会话上下文。 | 否。 | 否。 | 是 | 是 | 接真实 WC 会话仓储。 |
| SignMessageConfirm | `sign_message_confirm/{requestId}` | D | 否。仅本地签名前确认文案。 | 否。 | 否。 | 是 | 是 | 接真实签名请求模型。 |
| RiskAuthorizations | `risk_authorizations` | D | 否。明确本地空态。 | 否。 | 部分。 | 是 | 是 | 接真实授权记录。 |
| NftGallery | `nft_gallery` | D | 否。明确未接真实 NFT 数据源。 | 否。 | 部分。 | 是 | 是 | 接真实 NFT 索引。 |
| StakingEarn | `staking_earn` | D | 否。明确未接真实质押数据源。 | 否。 | 部分。 | 是 | 是 | 接真实 staking 域。 |
