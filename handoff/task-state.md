# Task State

- Updated At: 2026-04-03 12:10 +08:00
- Repository: /Users/cnyirui/git/projects/liaojiang
- Next Task: `liaojiang-7da.3` Android 邀请佣金提现页面接入真实接口
- Counts: `open=6` `in_progress=4` `closed=45` `ready=0`
- Dirty Files:
  - `.codex/recovery-state.json`
  - `code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/payment/data/repository/PaymentRepository.kt`
  - `code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/payment/ui/activity/LoginActivity.kt`
  - `code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/plans/PaymentActivity.kt`
  - `code/Android/V2rayNG/app/src/main/res/layout/activity_login.xml`
  - `code/Android/V2rayNG/app/src/main/res/layout/activity_payment.xml`
  - `code/Android/V2rayNG/app/src/main/res/values/strings.xml`
  - `docs/current-status.md`

## Resume Commands
```bash
bd ready --json
bd list --status in_progress --json
git status --short
env JAVA_HOME=/opt/homebrew/opt/openjdk@17/libexec/openjdk.jdk/Contents/Home /bin/sh code/Android/V2rayNG/gradlew :app:compileFdroidDebugSources
```

## Current Verification

- Android compile passed: `:app:compileFdroidDebugSources`
- Android assemble passed: `:app:assembleFdroidDebug`
- Emulator booted: `Medium_Phone_API_36.1`
- APK installed and launched:
  - package `com.v2ray.ang.fdroid`
  - activity `com.v2ray.ang.ui.MainActivity`

## Remaining Work

- `liaojiang-7da.3`: wire invitation/commission/withdrawal activities to repository methods
- `liaojiang-7da.4`: complete real smoke for login/order/invitation/withdrawal
- `liaojiang-7da`: close Android integration feature
- `liaojiang-6ag`: QA contract and regression
