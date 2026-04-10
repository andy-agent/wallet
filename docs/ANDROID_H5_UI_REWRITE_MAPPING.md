# Android H5 UI Rewrite Mapping

Updated: 2026-04-10

## Objective

Use `/Users/cnyirui/git/projects/liaojiang/UI` as the only visual and interaction source of truth.
Rebuild the real Android app UI in `/Users/cnyirui/git/projects/liaojiang/code/Android/V2rayNG/app` against that H5 baseline.
The current active Compose shell is not a reusable baseline; it is a replacement target.

## Approved H5 Scope

Approved H5 page count: `40`

### P0

- `splash`
- `email_login`
- `email_register`
- `force_update`
- `optional_update`
- `reset_password`
- `vpn_home`
- `wallet_home`
- `wallet_onboarding`

### P1

- `plans`
- `region_selection`
- `order_checkout`
- `wallet_payment_confirm`
- `order_result`
- `order_list`
- `order_detail`

### P2 Core

- `about_app`
- `asset_detail`
- `commission_ledger`
- `invite_center`
- `invite_share`
- `legal_document_detail`
- `legal_documents`
- `profile`
- `receive`
- `send`
- `send_result`
- `withdraw`

### P2 Extended

- `add_custom_token`
- `backup_mnemonic`
- `bridge`
- `chain_manager`
- `confirm_mnemonic`
- `dapp_browser`
- `import_mnemonic`
- `import_wallet_method`
- `security_center`
- `sign_message_confirm`
- `swap`
- `wallet_connect_session`

## Route Decisions

Current Android `CryptoVpnRouteSpec` contains `53` named route definitions.
Approved H5 covers `40` pages.

### Keep And Rebuild

Keep and rebuild the existing route families that already align with H5:

- `splash`
- `email_login`
- `wallet_onboarding`
- `vpn_home`
- `wallet_home`
- `force_update`
- `optional_update`
- `email_register`
- `reset_password`
- `plans`
- `region_selection`
- `order_checkout`
- `wallet_payment_confirm`
- `order_result`
- `order_list`
- `order_detail`
- `asset_detail`
- `receive`
- `send`
- `send_result`
- `invite_center`
- `commission_ledger`
- `withdraw`
- `profile`
- `legal_documents`
- `legal_document_detail`
- `import_wallet_method`
- `import_mnemonic`
- `backup_mnemonic`
- `confirm_mnemonic`
- `security_center`
- `chain_manager`
- `add_custom_token`
- `swap`
- `bridge`
- `dapp_browser`
- `wallet_connect_session`
- `sign_message_confirm`

### Add To Android Route Spec

These H5-confirmed pages do not exist in the current Android route spec and must be added:

- `about_app`
- `invite_share`

### Remove From Active Navigation

These Android routes exist today but have no approved H5 source and must be removed from the active app navigation tree:

- `wallet_payment`
- `subscription_detail`
- `expiry_reminder`
- `node_speed_test`
- `auto_connect_rules`
- `create_wallet`
- `import_private_key`
- `wallet_manager`
- `address_book`
- `gas_settings`
- `risk_authorizations`
- `nft_gallery`
- `staking_earn`

## Replacement Scope

### Replace Entirely

The following current Compose UI areas are replacement targets and must not be reused as visual baselines:

- `composeui/theme/**`
- `composeui/components/app/**`
- `composeui/components/buttons/**`
- `composeui/components/cards/**`
- `composeui/components/feature/**`
- `composeui/components/inputs/**`
- `composeui/components/listitems/**`
- `composeui/components/navigation/**`
- `composeui/effects/**`
- `composeui/pages/**`
- `composeui/navigation/**`

### Keep But Refactor For Real Data

Keep these as business/data integration boundaries, but refactor as needed to serve the new H5-driven screens:

- `composeui/common/repository/CryptoVpnRepository.kt`
- `composeui/common/repository/RealCryptoVpnRepository.kt`
- `composeui/p0/repository/P0Repository.kt`
- `composeui/p0/repository/RealP0Repository.kt`
- `composeui/**/viewmodel/**`
- `composeui/**/model/**`

### Keep As External Runtime Providers

- `payment/data/repository/PaymentRepository.kt`
- `payment/data/local/**`
- `handler/V2RayServiceManager.kt`
- `viewmodel/MainViewModel.kt`
- `handler/UpdateCheckerManager.kt`
- `handler/MmkvManager.kt`
- `handler/SettingsManager.kt`

## Real Data Source Mapping

### Auth / Account

- Primary source: `PaymentRepository`
- Used for: login, register seed, reset password seed, cached user, token validity, me profile

### VPN Runtime

- Primary sources: `MainViewModel`, `V2RayServiceManager`, `MmkvManager`
- Used for: connection state, server/subscription groups, selected node, latency testing, service running state

### Plans / Orders / Subscription / Invite / Commission / Withdraw

- Primary source: `PaymentRepository`
- Concrete methods already present:
  - `getPlans()`
  - `getOrder(orderNo)`
  - `getSubscription()`
  - `getMe()`
  - `getReferralOverview()`
  - `getCommissionSummary()`
  - `getCommissionLedger()`
  - `getWithdrawals()`

### Wallet Home / Asset Detail / Receive / Send / Send Result

- Primary sources:
  - `PaymentRepository`
  - `LocalPaymentRepository`
  - cached order/payment history as the current real wallet-adjacent data source
- Note:
  - This module does not currently expose a standalone on-chain wallet backend.
  - H5-backed wallet screens must be wired to the existing real order/payment cache and local module state, not to mock preview seeds.

### Legal

- Primary source: current in-app legal document set inside `RealCryptoVpnRepository`
- Action:
  - Keep the local legal content source, but reshape presentation and routing to H5.

### Update Pages

- Primary source: `UpdateCheckerManager`
- Used for: force update / optional update runtime checks and version text

### P2 Extended Local Wallet Flows

- Primary sources:
  - local app storage / MMKV-backed state
  - current real module repositories where available
- Rule:
  - no `MockCryptoVpnRepository` runtime dependency on active screens
  - if a page has no network backend, bind it to local persisted state and real user actions instead of preview seeds

## Navigation And Bottom Bar Rules

- Bottom bar visual and structure must follow the approved H5 implementation, not the current Compose bottom bar.
- Only routes backed by approved H5 pages may appear in active primary navigation.
- `about_app` and `invite_share` must become reachable from the Android app because they are part of approved H5 flow.

## Acceptance Targets For This Rewrite

- Real Android launcher enters the H5-based Compose shell.
- No active screen uses old English copy, old iconography, old bottom bar, or old layout rhythm as baseline.
- No active routed page depends on mock repository output.
- Removed routes are not registered in active nav graphs.
- `assemblePlaystoreDebug` passes.
- App launches and key paths are reachable:
  - splash -> login -> vpn home
  - plans -> checkout -> payment confirm -> order result
  - wallet home -> asset detail -> receive/send
  - invite center -> invite share / commission / withdraw
  - profile -> legal docs / about app
