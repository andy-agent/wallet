# Recovery Context

Generated: 2026-04-11T22:35:00Z
Repository: /Users/cnyirui/git/projects/liaojiang

## Critical Path
- next_task_id: liaojiang-0jp
- next_task_title: Android app：移除运行时剩余 mock 数据并接入真实落地数据

## Counters
- in_progress: 4
- ready: 1
- open: 1
- closed: 401
- dirty: 0

## Next Milestone
- Compose page-by-page reality audit is complete and direct rework is underway under `liaojiang-0jp`.
- Output artifacts:
  - [COMPOSE_UI_REALIFICATION_AUDIT_2026-04-11.md](/Users/cnyirui/git/projects/liaojiang/docs/COMPOSE_UI_REALIFICATION_AUDIT_2026-04-11.md)
  - [COMPOSE_UI_REALIFICATION_EXECUTION_LOG_2026-04-11.md](/Users/cnyirui/git/projects/liaojiang/docs/COMPOSE_UI_REALIFICATION_EXECUTION_LOG_2026-04-11.md)
- Audit totals across 46 pages remain:
  - A=0
  - B=11
  - C=17
  - D=18
- Clean worktree progress now exists beyond the audit:
  - `codex/liaojiang-0jp-p0`
    - commits `26cfdb1c`, `e0c2b314`
    - P0 Compose register/reset/login/home state/actions moved to real repository flows
    - compile passed: `:app:compileFdroidDebugKotlin`
  - `codex/liaojiang-0jp-p1`
    - commits `c0ea18d3`, `1c189fa2`, `12c06264`
    - P1 plans/order/payment flows removed hardcoded plan/order defaults and switched to real models
    - compile passed: `:app:compileFdroidDebugKotlin`
  - `codex/liaojiang-0jp-p2core`
    - commit `d9ce058a`
    - P2 Core asset/invite/commission/withdraw/profile/legal/about pages now use real states or explicit blockers instead of preview/local fake semantics
    - compile passed after wiring `WithdrawViewModel` appContext in `P2CoreNavGraph`
  - `codex/liaojiang-0jp-integrate`
    - commits `9ff8602f`, `acfdeeda`, `506ec757`
    - integration branch now contains P0/P1 plus P2 Core and a first P2 Extended rendering slice that swaps hardcoded pseudo-business copy for `uiState`-driven rendering on `SecurityCenter` / `Swap` / `Bridge` / `DappBrowser` / `WalletConnectSession` / `SignMessageConfirm` / `ImportMnemonic` / `BackupMnemonic` / `ImportWalletMethod`
    - compile passed: `:app:compileFdroidDebugKotlin`
- Current blocker changed:
  - `liaojiang-0jp.4`, `.5`, `.6`, `.7` are now accepted and closed.
  - The remaining active subtask is `liaojiang-0jp.8`, which owns Phase 3 runtime verification and any defects found by those runs.
  - The launcher-driven verification path is established and working:
    - debug-only `ComposeRouteOverrideReceiver` writes a one-shot route override
    - `LaunchSplashActivity` forwards that override
    - `ComposeContainerActivity` consumes it as a fallback start route
    - launcher startup is retried until `com.v2ray.ang.fdroid` reaches foreground
  - Verified runtime/screenshots already exist for a broad route set including:
    - `email_register`
    - `plans`
    - `region_selection`
    - `order_checkout/BASIC_1M`
    - `order_list`
    - `order_detail/ORD-1775909049741-BF4BAF37`
    - `wallet_payment_confirm/ORD-1775909049741-BF4BAF37`
    - `order_result/ORD-1775909049741-BF4BAF37`
    - `wallet_payment`
    - `wallet_onboarding`
    - `vpn_home`
    - `wallet_home`
    - `profile`
    - `about_app`
    - `legal_documents`
    - `legal_document_detail/terms_of_service`
    - `subscription_detail/current_subscription`
    - `auto_connect_rules`
    - `chain_manager/primary_wallet`
    - `add_custom_token/base`
    - `confirm_mnemonic/primary_wallet`
    - `wallet_manager/primary_wallet`
    - `address_book/send`
    - `receive/USDT/tron`
    - `send/USDT/tron`
    - `send_result/TX-9F32`
    - `withdraw`
    - `force_update`
    - `optional_update`
  - Action-level proof also exists:
    - `about_app` external link opens system browser to `https://github.com/2dust/v2rayNG`
  - Current remaining blocker is not the route-launch mechanism; it is finishing the last residual template cleanup on some P2Extended pages and closing the remaining coverage gap across all audited routes.
