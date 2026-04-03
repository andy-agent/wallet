# 当前状态

**最后更新**: 2026-04-03

## 系统状态概览

| 组件 | 状态 | 备注 |
|------|------|------|
| Backend API | 🟢 可用 | NestJS backend 已在 `154.37.208.72:3000` 真实环境跑通 `healthz` |
| PostgreSQL | 🟢 可用 | `cryptovpn_test` 已执行 baseline + seed |
| Android 编译 | 🟢 通过 | 本机 `compileFdroidDebugSources` 与 `assembleFdroidDebug` 已通过 |
| Android 运行 | 🟡 部分验证 | 模拟器安装 APK 成功，`MainActivity` 启动成功，尚未完成登录/下单/邀请/提现全链路 |
| Admin Web | 🟢 可构建 | 骨架已存在，后续由 QA 统一回归 |

## 本轮完成

- `liaojiang-92m` 已关闭，仓库恢复占位任务已回收。
- `liaojiang-7da.1` 已关闭，Android 集成差距复盘与子任务拆解已完成。
- `liaojiang-7da.2` 已关闭，Android 登录/收银台/支付网络契约已收敛：
  - 注册流程不再写死 `123456`，改为真实验证码输入
  - 创建订单可区分 `SOLANA` / `TRON`
  - 订单轮询改为使用 `orderNo + refresh-status`
- 本机 Android 验证已拿到真实证据：
  - `env JAVA_HOME=/opt/homebrew/opt/openjdk@17/libexec/openjdk.jdk/Contents/Home /bin/sh ./gradlew :app:compileFdroidDebugSources`
  - `env JAVA_HOME=/opt/homebrew/opt/openjdk@17/libexec/openjdk.jdk/Contents/Home /bin/sh ./gradlew :app:assembleFdroidDebug`
  - 模拟器 `Medium_Phone_API_36.1` 启动成功
  - `v2rayNG_2.0.17-fdroid_universal.apk` 安装成功
  - `com.v2ray.ang.fdroid/com.v2ray.ang.ui.MainActivity` 启动后成为 resumed activity

## 当前阻塞

### P1 主线

1. `liaojiang-7da.3` Android 邀请/佣金/提现页面仍未接入真实数据加载与交互。
2. `liaojiang-7da.4` 仍缺真实登录、下单、邀请、提现 smoke 验证。
3. `liaojiang-6ag` QA Contract and Regression 仍被 `liaojiang-7da` 阻塞。

### 已知限制

- `gradlew` 在当前环境下直接执行会报 Java runtime 异常，需要显式使用：
  - `env JAVA_HOME=/opt/homebrew/opt/openjdk@17/libexec/openjdk.jdk/Contents/Home /bin/sh ./gradlew ...`
- Android 的 `.gradle/` 和 `build/` 目录当前仍会污染 `git status`，每次验证后需要手动清理生成物。

## 下一步

1. 实现 `InvitationCenterActivity / CommissionLedgerActivity / WithdrawalActivity` 的真实接口绑定，关闭 `liaojiang-7da.3`。
2. 在模拟器上完成登录、下单、邀请、提现最小 smoke，推进 `liaojiang-7da.4`。
3. 关闭 `liaojiang-7da` 后，进入 `liaojiang-6ag` QA 回归。
