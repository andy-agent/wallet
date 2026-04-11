# Recovery Context

Generated: 2026-04-11T18:05:00Z
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
  - It is no longer “write overlap with another UI thread”.
  - A new bd task `liaojiang-0jp.8` now tracks Phase 3 runtime verification.
  - The old shell-started route harness remains unreliable on the Oppo phone, but the current launcher-driven path is working:
    - debug-only `ComposeRouteOverrideReceiver` writes a one-shot route override
    - `LaunchSplashActivity` forwards that override
    - `ComposeContainerActivity` consumes it as a fallback start route
    - launcher startup is retried until `com.v2ray.ang.fdroid` reaches foreground
  - Real runtime evidence now exists for:
    - `plans` via `/tmp/compose-realify-20260412-route/plans.retry.png`
    - `email_register` via `/tmp/compose-realify-20260412-route2/email_register.png`
    - `subscription_detail/current_subscription` via `/tmp/compose-realify-20260412-route2/subscription_detail_current_subscription.png`
  - This runtime path also surfaced a real crash:
    - `PlansPage.kt:68`
    - `java.util.NoSuchElementException: List is empty.`
    - fixed by making the plans page render a true empty/error state instead of calling `cards.first()`
