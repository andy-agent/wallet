# Task State

- Updated At: 2026-04-05 03:08 +08:00
- Repository: /Users/cnyirui/git/projects/liaojiang
- Next Task: `none-ready`（当前无 unblocked open 任务；主线仅剩 feature/task 父项 in_progress）
- Counts: `open=0` `in_progress=3` `closed=117` `ready=0`
- Dirty Files:
  - `.codex/recovery-state.json`
  - `docs/current-status.md`
  - `handoff/task-state.md`

## Resume Commands
```bash
bd ready --json
bd list --status=in_progress --json
bd show liaojiang-7x4
git status --short
```

## Current Verification

- API:
  - request-code
  - register
  - me
  - plans
  - orders
  - payment-target
  - submit-client-tx
  - refresh-status
  - referral overview
  - commissions summary
  - withdrawals returns expected insufficient-balance business rejection
- Sol chain-side:
  - `sol.residential-agent.com/api/healthz` returns `healthy`
  - real signature query returns confirmed status
- USDT/TRON chain-side:
  - server1 internal `health/capabilities/block/current/tx` verified
  - `usdt.residential-agent.com/api/healthz` returns `healthy + connected`
- Real order-chain smoke:
  - order `ORD-1775219239579` reached `COMPLETED` via remote Solana service
- Admin:
  - `/admin/` entry reachable
  - admin login returns token
  - dashboard / orders / withdrawals API return 200
- App planning:
  - `VPNUI_DIRECTORY_DECISION.md` accepted
  - `VPNUI_INTEGRATION_PLAN.md` accepted
- Android compose migration:
  - `4j0.9` 已完成并入主线
  - `7x4.2` 已验收：主线提交 `96b960e1` 关闭 `android.builtInKotlin` 与显式 Kotlin 插件冲突；`JAVA_HOME=/opt/homebrew/opt/openjdk@17 ./gradlew :app:assembleFdroidDebug` 成功
  - `7x4.3` 已验收：`arm64-v8a` APK 成功安装到 `emulator-5554`，`cmd package resolve-activity --brief com.v2ray.ang.fdroid/com.v2ray.ang.ui.compose.ComposeContainerActivity` 可解析到目标组件
  - `am start` 对该 Activity 返回 `not exported` 安全拒绝，属于非导出 Activity 的预期行为，不影响“类存在于已安装包”结论

## Remaining Work

- `liaojiang-4j0.10`: migrate vpnui final directories into app skeleton (still not accepted)
- `liaojiang-4j0.11`: bridge splash/auth compose pages
- `liaojiang-4j0.12`: bridge vpn/order compose pages
- `liaojiang-4j0.13`: bridge wallet/growth/profile/legal compose pages
- `liaojiang-4j0.2`: final Android real-environment regression after compose migration
