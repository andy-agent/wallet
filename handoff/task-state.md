# Task State

- Updated At: 2026-04-07 13:29 +08:00
- Repository: /Users/cnyirui/git/projects/liaojiang
- Next Task: `liaojiang-4j0.2`（human-needed blocker：等待 Android 真实回归账号凭据或可取验证码邮箱）
- Counts: `open=0` `in_progress=3` `closed=124` `ready=0`
- Dirty Files:
  - .codex/bd-orchestrator-state.json
  - .codex/recovery-context.md
  - .codex/recovery-state.json
  - code/backend/.env.example
  - code/backend/src/modules/health/health.module.ts
  - code/backend/src/modules/health/health.service.ts
  - code/backend/src/modules/tron-client/
  - code/backend/src/modules/wallet/wallet.module.ts
  - code/backend/src/modules/wallet/wallet.service.ts
  - code/backend/test/app.e2e-spec.ts
  - code/backend/test/wallet.e2e-spec.ts
  - docs/current-status.md
  - docs/development-log.md
  - handoff/progress.md
  - handoff/task-state.md

## Resume Commands
```bash
bd ready --json
bd list --status=in_progress --json
bd stats
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
- Backend chain-side client acceptance:
  - `pnpm --dir code/backend typecheck` passed
  - `pnpm --dir code/backend build` passed
  - `pnpm --dir code/backend test:e2e` passed (`6` suites / `11` tests)
  - backend now exposes aggregated Solana + TRON chain-side health from `/api/healthz`
  - wallet TRON precheck / proxy-broadcast paths now prefer remote service when enabled and fall back explicitly when unavailable
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
  - `7x4.3` 已验收：`arm64-v8a` APK 成功安装到 `emulator-5554`，设备包信息确认 `com.v2ray.ang.fdroid` versionCode=`5071701`
  - `am start` 对该 Activity 返回 `not exported` 安全拒绝，属于非导出 Activity 的预期行为，不影响“类存在于已安装包”结论
  - `4j0.2` 本轮验收追加证据：
    - `MainActivity` 抽屉 `Purchase Plan` 入口可在应用内拉起 `LoginActivity`
    - `uiautomator dump` 已捕获登录页 email/password 表单，说明最终 APK 的登录入口可达
    - 设备内无 `payment_prefs` token/session，也无本地 payment 数据库用户缓存
    - 当前仓库/本地环境未提供可复用客户端测试账号或可取验证码邮箱，无法继续完成真实登录 -> 下单 -> 支付页回归

## Remaining Work

- `liaojiang-4j0`: Android 最终阶段 feature 仍 in_progress，当前实质阻塞仍是 `4j0.2`
- `liaojiang-4j0.2`: 已验证到真实登录页入口，但仍阻塞于测试账号凭据/验证码邮箱来源
- `liaojiang-rcb`: `17.x` 链侧接线与健康可观测性里程碑已完成并验收，当前没有新的 ready bead
