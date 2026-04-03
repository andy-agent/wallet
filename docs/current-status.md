# 当前状态

**最后更新**: 2026-04-03

## 系统状态概览

| 组件 | 状态 | 备注 |
|------|------|------|
| Backend API | 🟢 可用 | `api.residential-agent.com` 已返回新 backend 的 `/api/healthz` |
| Android 集成 | 🟢 已完成 | `liaojiang-7da` 已关闭 |
| Android 编译/构建 | 🟢 通过 | `compileFdroidDebugSources` 与 `assembleFdroidDebug` 均通过 |
| Android 运行验证 | 🟢 已拿到证据 | 模拟器安装 APK 成功，`MainActivity` 启动成功 |
| 真实业务 smoke | 🟢 已推进 | `request-code / register / me / plans / orders / payment-target / referral / commissions` 已通过，提现拿到预期业务拒绝 |
| QA 回归 | 🟡 待推进 | `liaojiang-6ag` 已成为下一主线 ready 任务 |

## 本轮完成

- 关闭 `liaojiang-7da.3`
  - Kimi 产出已回收
  - 邀请中心、佣金账本、提现页面接入真实 repository 方法
  - 新增 `item_commission_ledger.xml`、`item_withdrawal.xml`
- 关闭 `liaojiang-7da.4.1`
  - 修复公网 API 暴露
  - 服务器三新增并启用 `api.residential-agent.com / vpn.residential-agent.com` Nginx API 网关
  - Cloudflare 删除了 `vpn.residential-agent.com -> 38.58.59.142` 的错误 A 记录
  - 新增 `api.residential-agent.com -> 154.37.208.72`
- 关闭 `liaojiang-7da.4`
  - Android compile / assemble 通过
  - 模拟器运行证据已取得
  - 真实 backend smoke 已推进到业务接口级
- 关闭 `liaojiang-7da`
  - Android 集成主特性完成

## 当前真实环境结论

### API 域名

- API 主入口已切为：
  - `https://api.residential-agent.com/`
- Android 代码已同步切换到 `api` 子域：
  - [PaymentConfig.kt](/Users/cnyirui/git/projects/liaojiang/code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/payment/PaymentConfig.kt)

### 已验证通过的真实链路

- `POST /api/client/v1/auth/register/email/request-code`
- `POST /api/client/v1/auth/register/email`
- `GET /api/client/v1/me`
- `GET /api/client/v1/plans`
- `POST /api/client/v1/orders`
- `GET /api/client/v1/orders/{orderNo}/payment-target`
- `GET /api/client/v1/referral/overview`
- `GET /api/client/v1/commissions/summary`
- `POST /api/client/v1/withdrawals`
  - 当前返回 `WITHDRAW_INSUFFICIENT_AVAILABLE_BALANCE`
  - 这是预期业务拒绝，不是网络或路由错误

## 新增部署任务

- 已新增：
  - `liaojiang-2f0.1` 三机角色拆分与 `api/sol/usdt` 子域编排
- 当前实际运行仍偏临时集中：
  - API 面已在服务器三跑通
  - 多机角色拆分将在后续任务中收敛为正式拓扑

## 下一步

1. 进入 `liaojiang-6ag`，做 QA Contract and Regression。
2. 同步整理三机角色拆分方案，推进 `liaojiang-2f0.1`。
3. 清理 Android 构建产物后提交本轮集成与环境变更。
