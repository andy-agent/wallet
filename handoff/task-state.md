# Task State

- Updated At: 2026-04-08 00:37 +08:00
- Repository: /Users/cnyirui/git/projects/liaojiang
- Next Task: `liaojiang-4j0.2`（真实账户登录 / 下单 / 支付页最终回归）
- Counts: `open=3` `in_progress=3` `closed=141` `ready=0`
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
  - `4j0.15` 已验收关闭：
    - Bitget 风格主壳 / 底部导航 / ShellTab 已并入主线
    - 主线 `:app:assembleFdroidDebug` 已通过
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
  - `4j0.17` 已验收关闭：
    - wallet/home Bitget 风格重构已并入主线
    - 主线 `:app:compileFdroidDebugSources` 已通过
  - `4j0.18` 已验收关闭：
    - VPN/Plans/Region/Order/Payment Bitget 风格重构已并入主线
    - 隔离 worktree `:app:compileFdroidDebugSources` 通过
  - `4j0.16` 已验收关闭：
    - 真机默认图标、首页主壳、五 Tab、套餐入口均已验证
    - 剩余 live 业务回归留给 `4j0.2`
  - Git 侧新增基础设施风险：
    - `git push` 被拒，因为历史里含有 `BitgetWallet9400_bgwapp.apk`（208MB）超出 GitHub 100MB 限制
    - 需要在主线任务收口后清理历史再恢复推送

## Remaining Work

- `liaojiang-4j0`: Android 最终阶段 feature 仍 in_progress
- `liaojiang-4j0.2`: 最终真实业务回归仍未完成
  - 真实 blocker：
    - 测试账号已存在
    - 真机已装最新主线 APK
    - 但这台 OPlus 设备上安装确认与 Compose 输入焦点对自动化不稳定，无法可信地无人值守完成“登录 -> 下单 -> 支付页”最后一段
    - 这已转为需要人类接手的真机交互 blocker
- `liaojiang-rcb`: feature 仍 in_progress，但按用户要求需在主线完成后再推进二期 beads 拆解
