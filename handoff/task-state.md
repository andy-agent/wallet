# Task State

- Updated At: 2026-04-07 23:40 +08:00
- Repository: /Users/cnyirui/git/projects/liaojiang
- Next Task: `liaojiang-4j0.15`（Bitget 风格主题与底部导航外壳，Codex worker 执行中）
- Counts: `open=5` `in_progress=5` `closed=134` `ready=1`
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
  - `4j0.14` 已关闭：
    - Bitget-like IA 已冻结
    - VPN 作为一级底部 Tab，替换 Quote/Market 位
    - Home 保留 dashboard，Wallet 保留资产/支付，Discover 承接增长内容
  - `4j0.21` 已关闭：
    - Bitget APK 功能对比基线已固化到 `docs/BITGET_APK_FEATURE_COMPARISON.md`
    - 结论：Bitget 的钱包/Swap/DApp/NFT/Backup/Card/Earn/Invite 等大部分 shell 语法可借鉴，但我们必须让 VPN 替换其 Quote/Market 位，成为一级底部 Tab
  - `4j0.15` 当前 active worker:
    - 独立 worktree：`codex-liaojiang-4j0-15-codex`
    - 当前状态：壳层相关文件已开始落改动，尚未形成可验收提交
  - `4j0.21` 已关闭：
    - `docs/BITGET_APK_FEATURE_COMPARISON.md` 已生成
    - Bitget 的钱包/Swap/DApp/NFT/Backup/Card/Earn/Invite shell 语法已完成对比
    - 结论已冻结：VPN 替换 Bitget Quote/Market 位，作为一级底部 Tab
  - `4j0.19` 当前 active worker:
    - 独立 worktree：`codex-liaojiang-4j0-19-codex`
    - 当前状态：Growth/Profile/Legal 六个页面已重构并形成提交 `edd642d5`
    - 主控验收结论：暂不通过
      - 原因 1：分佣文案错误写成 `20% / 10%`，应为冻结规则 `25% / 5%`
      - 原因 2：Growth/Withdraw 金额文案使用 `$`，应改为 `USDT` 语义
    - 已在同一 worktree 上重派 fix worker，等待追加修复提交
  - 下一批被上述任务阻塞：
    - `4j0.17` Wallet/Home 页面重构
    - `4j0.18` VPN/购买流程页面重构
    - `4j0.16` UI 重构主线验收

## Remaining Work

- `liaojiang-4j0`: Android 最终阶段 feature 仍 in_progress
- `liaojiang-4j0.15`: Bitget 风格 shell/theme/nav 外壳实现中
- `liaojiang-4j0.19`: 当前在同一 worktree 上做验收退回后的修补
- `liaojiang-4j0.17`: Wallet/Home 页面重构，等待 `4j0.15`
- `liaojiang-4j0.18`: VPN/购买流程页面重构，等待 `4j0.15`
- `liaojiang-4j0.16`: Bitget UI 主线验收，等待 `4j0.15/17/18/19`
- `liaojiang-4j0.2`: 最终真实业务回归仍保留，但现在已被新 UI 重构主线前置
- `liaojiang-rcb`: feature 仍 in_progress，但当前主线优先级低于 Android UI 重构
