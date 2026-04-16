# Compose UI 真实回归结果表

更新时间：2026-04-12

数据来源：
- launcher-driven 真机截图目录 `/tmp/compose-realify-*`
- 动作级证据目录 `/tmp/compose-realify-20260412-action*`
- [COMPOSE_UI_REALIFICATION_EXECUTION_LOG_2026-04-11.md](/Users/cnyirui/git/projects/liaojiang/docs/COMPOSE_UI_REALIFICATION_EXECUTION_LOG_2026-04-11.md)

摘要：
- 已实际拉起验证页面：52 / 53
- 缺单独页面级运行证据：`InviteShare`
- 已完成动作级验证：`AboutApp` 外链打开、`LegalDocumentDetail` 原文外链打开

| 页面 | 是否实际运行 | 已验证状态 | 已验证动作 | 判定 | 风险点 | 证据 |
|---|---|---|---|---|---|---|
| Splash | 是 | 启动页实际打开 | 未验证主动作 | 部分通过 | 仍需补 loading/error/retry 或真实业务动作 | /tmp/compose-realify-20260412-accept/splash.png |
| EmailLogin | 是 | 页面实际打开 | 未验证主动作 | 部分通过 | 仍需补 loading/error/retry 或真实业务动作 | /tmp/compose-realify-20260412-batch/email_login.png |
| EmailRegister | 是 | 页面实际打开 | 未验证主动作 | 部分通过 | 仍需补 loading/error/retry 或真实业务动作 | /tmp/compose-realify-20260412-route2/email_register.png |
| ResetPassword | 是 | 页面实际打开 | 未验证主动作 | 部分通过 | 仍需补 loading/error/retry 或真实业务动作 | /tmp/compose-realify-20260412-batch/reset_password.png |
| WalletOnboarding | 是 | 页面实际打开 | 未验证主动作 | 部分通过 | 真实能力阻塞，当前仅验证阻塞/空态表达是否真实 | /tmp/compose-realify-20260412-next2/wallet_onboarding.png |
| VpnHome | 是 | 页面实际打开 | 未验证主动作 | 部分通过 | 仍需补 loading/error/retry 或真实业务动作 | /tmp/compose-realify-20260412-p0b/vpn_home.png |
| WalletHome | 是 | 页面实际打开 | 未验证主动作 | 部分通过 | 仍需补 loading/error/retry 或真实业务动作 | /tmp/compose-realify-20260412-p0b/wallet_home.png |
| ForceUpdate | 是 | 页面实际打开 | 未验证主动作 | 部分通过 | 真实能力阻塞，当前仅验证阻塞/空态表达是否真实 | /tmp/compose-realify-20260412-accept/force_update.png |
| OptionalUpdate | 是 | 页面实际打开 | 未验证主动作 | 部分通过 | 真实能力阻塞，当前仅验证阻塞/空态表达是否真实 | /tmp/compose-realify-20260412-accept/optional_update.png |
| Plans | 是 | 页面实际打开 | 未验证主动作 | 部分通过 | 仍需补 loading/error/retry 或真实业务动作 | /tmp/compose-realify-20260412-route/plans.retry.png |
| RegionSelection | 是 | 页面实际打开 | 未验证主动作 | 部分通过 | 仍需补 loading/error/retry 或真实业务动作 | /tmp/compose-realify-20260412-latest/region_selection.png |
| OrderCheckout | 是 | 页面实际打开 | 未验证主动作 | 部分通过 | 仍需补 loading/error/retry 或真实业务动作 | /tmp/compose-realify-20260412-p1/order_checkout_BASIC_1M.png |
| WalletPaymentConfirm | 是 | 页面实际打开 | 未验证主动作 | 部分通过 | 仍需补 loading/error/retry 或真实业务动作 | /tmp/compose-realify-20260412-p1b/wallet_payment_confirm_ORD-1775909049741-BF4BAF37.png |
| OrderResult | 是 | 页面实际打开 | 未验证主动作 | 部分通过 | 仍需补 loading/error/retry 或真实业务动作 | /tmp/compose-realify-20260412-p1c/order_result_ORD-1775909049741-BF4BAF37.png |
| OrderList | 是 | 页面实际打开 | 未验证主动作 | 部分通过 | 仍需补 loading/error/retry 或真实业务动作 | /tmp/compose-realify-20260412-p1b/order_list.png |
| OrderDetail | 是 | 页面实际打开 | 未验证主动作 | 部分通过 | 仍需补 loading/error/retry 或真实业务动作 | /tmp/compose-realify-20260412-p1b/order_detail_ORD-1775909049741-BF4BAF37.png |
| WalletPayment | 是 | 页面实际打开 | 未验证主动作 | 部分通过 | 仍需补 loading/error/retry 或真实业务动作 | /tmp/compose-realify-20260412-latest/wallet_payment.png |
| AssetDetail | 是 | 页面实际打开 | 未验证主动作 | 部分通过 | 真实能力阻塞，当前仅验证阻塞/空态表达是否真实 | /tmp/compose-realify-20260412-growth/asset_detail_USDT_tron.png |
| Receive | 是 | 页面实际打开 | 未验证主动作 | 部分通过 | 真实能力阻塞，当前仅验证阻塞/空态表达是否真实 | /tmp/compose-realify-20260412-accept/receive_USDT_tron.png |
| Send | 是 | 页面实际打开 | 未验证主动作 | 部分通过 | 真实能力阻塞，当前仅验证阻塞/空态表达是否真实 | /tmp/compose-realify-20260412-accept/send_USDT_tron.png |
| SendResult | 是 | 页面实际打开 | 未验证主动作 | 部分通过 | 真实能力阻塞，当前仅验证阻塞/空态表达是否真实 | /tmp/compose-realify-20260412-accept/send_result_TX-9F32.png |
| InviteCenter | 是 | 页面实际打开 | 未验证主动作 | 部分通过 | 仍需补 loading/error/retry 或真实业务动作 | /tmp/compose-realify-20260412-growth/invite_center.png |
| InviteShare | 否 | 未单独重跑 | 未验证主动作 | 部分通过 | 仍需补 loading/error/retry 或真实业务动作 | 无单独证据 |
| CommissionLedger | 是 | 页面实际打开 | 未验证主动作 | 部分通过 | 仍需补 loading/error/retry 或真实业务动作 | /tmp/compose-realify-20260412-growth/commission_ledger.png |
| Withdraw | 是 | 页面实际打开 | 未验证主动作 | 部分通过 | 仍需补 loading/error/retry 或真实业务动作 | /tmp/compose-realify-20260412-accept/withdraw.png |
| Profile | 是 | 页面实际打开 | 未验证主动作 | 部分通过 | 仍需补 loading/error/retry 或真实业务动作 | /tmp/compose-realify-20260412-p2d/profile.png |
| LegalDocuments | 是 | 页面实际打开 | 未验证主动作 | 部分通过 | 真实能力阻塞，当前仅验证阻塞/空态表达是否真实 | /tmp/compose-realify-20260412-p2d/legal_documents.png |
| LegalDocumentDetail | 是 | 页面实际打开+外链动作已验证 | 打开原文已验证 | 部分通过 | 真实能力阻塞，当前仅验证阻塞/空态表达是否真实 | /tmp/compose-realify-20260412-p2d/legal_document_detail_terms_of_service.png |
| AboutApp | 是 | 页面实际打开+外链动作已验证 | 打开外链已验证 | 部分通过 | 仍需补 loading/error/retry 或真实业务动作 | /tmp/compose-realify-20260412-p2d/about_app.png |
| SubscriptionDetail | 是 | 页面实际打开 | 未验证主动作 | 部分通过 | 仍需补 loading/error/retry 或真实业务动作 | /tmp/compose-realify-20260412-route2/subscription_detail_current_subscription.png |
| ExpiryReminder | 是 | 页面实际打开 | 未验证主动作 | 部分通过 | 仍需补 loading/error/retry 或真实业务动作 | /tmp/compose-realify-20260412-batch/expiry_reminder_30.png |
| NodeSpeedTest | 是 | 页面实际打开 | 未验证主动作 | 部分通过 | 仍需补 loading/error/retry 或真实业务动作 | /tmp/compose-realify-20260412-batch/node_speed_test_default_group.png |
| AutoConnectRules | 是 | 页面实际打开 | 未验证主动作 | 部分通过 | 仍需补 loading/error/retry 或真实业务动作 | /tmp/compose-realify-20260412-route5/auto_connect_rules.png |
| CreateWallet | 是 | 页面实际打开 | 未验证主动作 | 部分通过 | 真实能力阻塞，当前仅验证阻塞/空态表达是否真实 | /tmp/compose-realify-20260412-batch/create_wallet_create.png |
| ImportWalletMethod | 是 | 页面实际打开 | 未验证主动作 | 部分通过 | 仍需补 loading/error/retry 或真实业务动作 | /tmp/compose-realify-20260412-batch/import_wallet_method.png |
| ImportMnemonic | 是 | 页面实际打开 | 未验证主动作 | 部分通过 | 真实能力阻塞，当前仅验证阻塞/空态表达是否真实 | /tmp/compose-realify-20260412-batch/import_mnemonic_onboarding.png |
| ImportPrivateKey | 是 | 页面实际打开 | 未验证主动作 | 部分通过 | 真实能力阻塞，当前仅验证阻塞/空态表达是否真实 | /tmp/compose-realify-20260412-batch/import_private_key_ethereum.png |
| BackupMnemonic | 是 | 页面实际打开 | 未验证主动作 | 部分通过 | 真实能力阻塞，当前仅验证阻塞/空态表达是否真实 | /tmp/compose-realify-20260412-batch/backup_mnemonic_primary_wallet.png |
| ConfirmMnemonic | 是 | 页面实际打开 | 未验证主动作 | 部分通过 | 真实能力阻塞，当前仅验证阻塞/空态表达是否真实 | /tmp/compose-realify-20260412-route3/confirm_mnemonic_primary_wallet.png |
| SecurityCenter | 是 | 页面实际打开 | 未验证主动作 | 部分通过 | 仍需补 loading/error/retry 或真实业务动作 | /tmp/compose-realify-20260412-p2extcheck/security_center.png |
| ChainManager | 是 | 页面实际打开 | 未验证主动作 | 部分通过 | 真实能力阻塞，当前仅验证阻塞/空态表达是否真实 | /tmp/compose-realify-20260412-route3/chain_manager_primary_wallet.png |
| AddCustomToken | 是 | 页面实际打开 | 未验证主动作 | 部分通过 | 真实能力阻塞，当前仅验证阻塞/空态表达是否真实 | /tmp/compose-realify-20260412-route3/add_custom_token_base.png |
| WalletManager | 是 | 页面实际打开 | 未验证主动作 | 部分通过 | 真实能力阻塞，当前仅验证阻塞/空态表达是否真实 | /tmp/compose-realify-20260412-route6/wallet_manager_primary_wallet.png |
| AddressBook | 是 | 页面实际打开 | 未验证主动作 | 部分通过 | 真实能力阻塞，当前仅验证阻塞/空态表达是否真实 | /tmp/compose-realify-20260412-route6/address_book_send.png |
| GasSettings | 是 | 页面实际打开 | 未验证主动作 | 部分通过 | 真实能力阻塞，当前仅验证阻塞/空态表达是否真实 | /tmp/compose-realify-20260412-batch/gas_settings_ethereum.png |
| Swap | 是 | 页面实际打开 | 未验证主动作 | 部分通过 | 真实能力阻塞，当前仅验证阻塞/空态表达是否真实 | /tmp/compose-realify-20260412-p2extcheck/swap_USDT_SOL.png |
| Bridge | 是 | 页面实际打开 | 未验证主动作 | 部分通过 | 真实能力阻塞，当前仅验证阻塞/空态表达是否真实 | /tmp/compose-realify-20260412-batch/bridge_tron_solana.png |
| DappBrowser | 是 | 页面实际打开 | 未验证主动作 | 部分通过 | 真实能力阻塞，当前仅验证阻塞/空态表达是否真实 | /tmp/compose-realify-20260412-batch/dapp_browser_jup.ag.png |
| WalletConnectSession | 是 | 页面实际打开 | 未验证主动作 | 部分通过 | 真实能力阻塞，当前仅验证阻塞/空态表达是否真实 | /tmp/compose-realify-20260412-p2extcheck/wallet_connect_session_session_default.png |
| SignMessageConfirm | 是 | 页面实际打开 | 未验证主动作 | 部分通过 | 真实能力阻塞，当前仅验证阻塞/空态表达是否真实 | /tmp/compose-realify-20260412-batch/sign_message_confirm_request_default.png |
| RiskAuthorizations | 是 | 页面实际打开 | 未验证主动作 | 部分通过 | 真实能力阻塞，当前仅验证阻塞/空态表达是否真实 | /tmp/compose-realify-20260412-batch/risk_authorizations.png |
| NftGallery | 是 | 页面实际打开 | 未验证主动作 | 部分通过 | 真实能力阻塞，当前仅验证阻塞/空态表达是否真实 | /tmp/compose-realify-20260412-batch/nft_gallery.png |
| StakingEarn | 是 | 页面实际打开 | 未验证主动作 | 部分通过 | 真实能力阻塞，当前仅验证阻塞/空态表达是否真实 | /tmp/compose-realify-20260412-batch/staking_earn.png |
