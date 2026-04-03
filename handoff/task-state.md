# Task State

- Updated At: 2026-04-03 12:50 +08:00
- Repository: /Users/cnyirui/git/projects/liaojiang
- Next Task: `liaojiang-6ag` S5 QA Contract and Regression
- Counts: `open=5` `in_progress=0` `closed=49` `ready=1`
- Dirty Files:
  - `.codex/recovery-state.json`
  - `code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/payment/PaymentConfig.kt`
  - `code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/payment/ui/activity/InvitationCenterActivity.kt`
  - `code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/payment/ui/activity/CommissionLedgerActivity.kt`
  - `code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/payment/ui/activity/WithdrawalActivity.kt`
  - `code/Android/V2rayNG/app/src/main/res/layout/activity_commission_ledger.xml`
  - `code/Android/V2rayNG/app/src/main/res/layout/activity_withdrawal.xml`
  - `code/Android/V2rayNG/app/src/main/res/layout/item_commission_ledger.xml`
  - `code/Android/V2rayNG/app/src/main/res/layout/item_withdrawal.xml`
  - `code/Android/V2rayNG/app/src/main/res/values/strings.xml`
  - `docs/current-status.md`
  - `环境测试服务器.md`

## Resume Commands
```bash
bd ready --json
bd list --status=open --json
git status --short
env JAVA_HOME=/opt/homebrew/opt/openjdk@17/libexec/openjdk.jdk/Contents/Home /bin/sh code/Android/V2rayNG/gradlew :app:compileFdroidDebugSources
curl -k https://api.residential-agent.com/api/healthz
```

## Current Verification

- Android:
  - `:app:compileFdroidDebugSources` passed
  - `:app:assembleFdroidDebug` passed
- Emulator:
  - APK installed
  - `MainActivity` launched
- Real API smoke via `api.residential-agent.com`:
  - request-code
  - register
  - me
  - plans
  - orders
  - payment-target
  - referral overview
  - commissions summary
  - withdrawals returns expected insufficient-balance business rejection

## Remaining Work

- `liaojiang-6ag`: QA contract and regression
- `liaojiang-2f0.1`: three-server role split plus `api/sol/usdt/wallet` subdomain planning
- `liaojiang-2f0`: top-level first-release delivery
