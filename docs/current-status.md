# 当前状态

**最后更新**: 2026-04-03

## 系统状态概览

| 组件 | 状态 | 备注 |
|------|------|------|
| Backend API | 🟢 可用 | `api.residential-agent.com` 已固化为唯一 API 入口 |
| DB / Redis | 🟢 已剥离 | 状态服务物理落在服务器二，API 机通过本地隧道端口访问 |
| Android 集成 | 🟢 已完成 | `liaojiang-7da` 已关闭 |
| Android 编译/构建 | 🟢 通过 | `compileFdroidDebugSources` 与 `assembleFdroidDebug` 均通过 |
| Android 运行验证 | 🟢 已拿到证据 | 模拟器安装 APK 成功，`MainActivity` 启动成功 |
| 真实业务 smoke | 🟢 已推进 | `request-code / register / me / plans / orders / payment-target / referral / commissions` 已通过，提现拿到预期业务拒绝 |
| Sol 链侧服务 | 🟢 可用 | `sol.residential-agent.com` 内外健康检查均通过 |
| USDT/TRON 链侧服务 | 🟢 可用 | `usdt.residential-agent.com` 已接真实 TRON RPC，健康/区块/交易查询通过 |
| 链侧客户端接线 | 🟡 进行中 | `liaojiang-rcb.14.1` 已完成，下一步进入 `liaojiang-rcb.14.2` |

## 本轮完成

- 完成 `liaojiang-rcb.13.1`
  - 回收 Kimi 产出并并入主线
  - `backend-chain-usdt` 增补 `mockMode` 真实语义与 e2e 覆盖
  - 本地 `typecheck / build / test:e2e` 通过
  - 非 mock 模式下本地 `block/current` 与 `tx/:hash` 已验证不再走占位逻辑
- 完成 `liaojiang-rcb.13.2`
  - 服务器一 `/opt/usdt-agent` 已同步最新代码并重建
  - `usdt-agent` systemd 服务重启成功
  - 内网 `health / capabilities / block/current / tx` 均返回 200
- 完成 `liaojiang-rcb.13.3`
  - 服务器一 `TRON_API_KEY` 已补齐
  - `Trongrid 401` 已消失
  - `usdt.residential-agent.com/health` 已恢复为 `connected`
- 完成 `liaojiang-rcb.14.1`
  - 回收 Kimi 产出并并入主线
  - `code/backend` 已补齐 wallet 相关远程链侧客户端接线
  - `transferPrecheck / proxyBroadcast` 已具备 Solana 远程调用与降级行为
  - 本地 `pnpm --dir code/backend typecheck`
  - 本地 `pnpm --dir code/backend build`
  - 均通过

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

### 三机拆分现状

- 服务器三 `154.37.208.72`
  - `api.residential-agent.com`
  - backend / nginx / 对外 API 接入
- 服务器二 `38.58.59.142`
  - PostgreSQL / Redis 物理落点
- 服务器一 `38.58.59.119`
  - `sol.residential-agent.com`
  - `usdt.residential-agent.com`
- 规划边界已明确：
  - `sol.residential-agent.com` = Solana 链侧服务
  - `usdt.residential-agent.com` = TRON / TRC20 链侧服务
  - 通用账本与结算仍留在 API 层

### 链侧服务现状

- `sol.residential-agent.com`
  - 外网 `/health` 返回 `200`
  - 服务器一内网 `http://127.0.0.1:4000/api/healthz` 返回 `healthy`
  - `sol-agent` systemd 服务为 `active`
- `usdt.residential-agent.com`
  - 外网 `/health` 返回 `200`
  - 服务器一内网 `/api/healthz` 返回 `healthy + connected`
  - 受保护接口验证通过：
    - `/api/v1/chain/capabilities`
    - `/api/v1/chain/block/current`
    - `/api/v1/chain/tx/{hash}`

## 当前主线任务

- 当前主线已从 Android/QA 切换到二期链侧收口：
  - `liaojiang-rcb.14.2`
- 该任务目标是：
  - 让 API 的订单支付检测最小链路真正接入远程链侧服务
  - 在 `api.residential-agent.com` 上完成一次真实环境 smoke

## 下一步

1. 进入 `liaojiang-rcb.14.2`，把订单支付检测最小链路切到远程链侧服务。
2. 在 `api.residential-agent.com` 上完成远程链侧真实 smoke。
3. Android 构建与最终回归继续放在最后阶段，不提前拉回主线。
