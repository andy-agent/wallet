# 当前状态

**最后更新**: 2026-04-16

## 系统状态概览

| 组件 | 状态 | 备注 |
|------|------|------|
| Backend API | 🟢 可用 | `api.residential-agent.com` 已固化为唯一 API 入口 |
| DB / Redis | 🟢 已剥离 | 状态服务物理落在服务器二，API 机通过本地隧道端口访问 |
| Android 集成 | 🟡 进入 Compose 并入阶段 | 现有 XML App 保持可用，`vpnui` 并入方案与目录裁决均已冻结 |
| Android 编译/构建 | 🟢 Compose APK 构建已恢复 | `7x4.2` 已修复 Kotlin/AGP 冲突，`assembleFdroidDebug` 可在主线通过 |
| Android 运行验证 | 🟡 Compose 容器安装已验证 | `7x4.3` 已把新 APK 安装到 `emulator-5554`，但页面级真实回归仍待后续 UI 迁移完成 |
| 真实业务 smoke | 🟢 已推进 | 基础接口 smoke 已通过，订单最小链路已通过远程 Solana 服务完成真实 smoke |
| Sol 链侧服务 | 🟢 可用 | `sol.residential-agent.com` 内外健康检查均通过 |
| USDT/TRON 链侧服务 | 🟢 可用 | `usdt.residential-agent.com` 已接真实 TRON RPC，健康/区块/交易查询通过 |
| 链侧客户端接线 | 🟢 已完成最小链路 | `liaojiang-rcb.14` 已关闭，订单最小链路已接远程链侧 |
| Admin 后台 | 🟢 已具备套餐管理闭环 | `admin-web` 已支持新增/编辑套餐，后台可从 `/admin/` 进行套餐运营配置 |

## 本轮完成

- 完成 `liaojiang-vd3i`
  - 后台已补齐套餐管理闭环：
    - `admin-web` 套餐页支持新增、编辑、状态切换
    - backend 新增 `POST /api/admin/v1/plans`
    - backend 新增 `PUT /api/admin/v1/plans/:planId`
    - PostgreSQL 套餐表与 `plan_region_permissions` 已接入写入
  - 客户端套餐已改为消费后台可配置套餐：
    - `GET /api/client/v1/plans` 直接读取后台启用套餐
    - admin 创建启用套餐后客户端可立即拉取
    - admin 停用套餐后客户端套餐列表不再展示
  - 订单收银台流程已调整为：
    - 套餐 -> 节点区域 -> 支付网络 -> 创建订单
    - 进入收银台不再自动创建订单
    - 无节点区域时允许非阻塞继续支付，支付后再补选节点
  - 收银台细节已更新：
    - 文案“创建真实订单中”已改为“正在创建订单”
    - 支付网络展示改为 `sol.solana` / `USDT.solana` / `USDT.tron`
    - Solana 下单能力改为按后端真实配置下发，不再暴露假可用选项
  - 本轮验证已通过：
    - `code/admin-web`: `npm run build`
    - `code/Android/V2rayNG`: `./gradlew --no-daemon -Dkotlin.compiler.execution.strategy=in-process :app:testFdroidDebugUnitTest --tests "com.v2ray.ang.composeui.p1.model.OrderCheckoutContractTest"`
    - `code/backend`: `pnpm --dir code/backend test:e2e -- admin-postgres.e2e-spec.ts wallet.e2e-spec.ts`
  - 代码提交与推送：
    - commit `ca751eb9`
    - branch `codex/android-demock-live-data-v2`

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
- 完成 `liaojiang-rcb.14.2`
  - `sol-agent` 新增最小交易状态查询端点
  - `SolanaClientService` 已对齐 internal auth header
  - `SolanaClientService` 已兼容解包 sol-agent response envelope
  - 服务器一 `sol/usdt` Nginx 已暴露 `/api/` 路由
  - 服务器三 backend 已开启 `SOLANA_SERVICE_ENABLED=true`
  - 真实订单 smoke 已完成:
    - `payment-target.serviceEnabled = true`
    - 订单 `ORD-1775219239579` 最终状态为 `COMPLETED`
- 完成 `liaojiang-rcb.17.2`
  - `code/backend` 新增 `tron-client` 模块与 `TRON_SERVICE_*` 配置
  - wallet TRON `transfer/precheck` / `transfer/proxy-broadcast` 已优先使用远程链侧服务
  - 远程链侧不可用时保留明确 fallback 语义
- 完成 `liaojiang-rcb.17.3`
  - `/api/healthz` 现已聚合 Solana + TRON 链侧健康状态
  - disabled / healthy / degraded 输出与 e2e 覆盖已补齐
- 完成 `liaojiang-rcb.17.4`
  - 主线已完成 diff 审核与 backend `typecheck / build / test:e2e` 验收
  - `pnpm --dir code/backend test:e2e` 当前为 `6` suites / `11` tests 全绿
- 完成 `liaojiang-4j0.1`
  - Android 最终构建通过
  - APK 已重新安装到模拟器
  - launcher 启动成功
  - 最近 logcat 未见 immediate crash
- 完成 `liaojiang-f93`
  - backend admin 最小域已补齐
  - admin-web 已对齐现有 backend admin 契约
  - 后台已部署到 `https://api.residential-agent.com/admin/`
  - 真实环境已验证：
    - `POST /api/admin/v1/auth/login`
    - `GET /api/admin/v1/dashboard/summary`
    - `GET /api/admin/v1/orders`
    - `GET /api/admin/v1/withdrawals`
- 完成 `liaojiang-4j0.7`
  - [VPNUI_DIRECTORY_DECISION.md](/Users/cnyirui/git/projects/liaojiang/docs/VPNUI_DIRECTORY_DECISION.md) 已生成
  - 已明确 `batch1~5` 逻辑废弃/仅参考，业务域目录为最终并入源
- 完成 `liaojiang-4j0.8`
  - [VPNUI_INTEGRATION_PLAN.md](/Users/cnyirui/git/projects/liaojiang/docs/VPNUI_INTEGRATION_PLAN.md) 已生成
  - 已冻结 `vpnui` 作为 Compose UI 资产包并入现有 `V2rayNG` 的推荐方案
- 完成 `liaojiang-7x4.2`
  - 主线已并入 `fix(android): disable built-in kotlin to unblock fdroid debug assemble`（`96b960e1`）
  - `JAVA_HOME=/opt/homebrew/opt/openjdk@17 ./gradlew :app:assembleFdroidDebug` 在主线通过
  - 已恢复可产出最新 `fdroidDebug` APK 的本地构建能力
- 完成 `liaojiang-7x4.3`
  - `v2rayNG_2.0.17-fdroid_arm64-v8a.apk` 已安装到 `emulator-5554`
  - `am start -W -n com.v2ray.ang.fdroid/com.v2ray.ang.ui.compose.ComposeContainerActivity` 不再返回 “Activity class does not exist”，而是按非导出 Activity 预期返回 `not exported`
  - `am start` 的 `not exported` 拒绝为非导出 Activity 预期行为，作为“组件存在”证据不构成失败

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
- `POST /api/client/v1/orders/{orderNo}/submit-client-tx`
- `POST /api/client/v1/orders/{orderNo}/refresh-status`
- `GET /api/client/v1/referral/overview`
- `GET /api/client/v1/commissions/summary`
- `POST /api/client/v1/withdrawals`
  - 当前返回 `WITHDRAW_INSUFFICIENT_AVAILABLE_BALANCE`
  - 这是预期业务拒绝，不是网络或路由错误
- 真实订单链路证据:
  - SOLANA 订单 `ORD-1775219239579`
  - `payment-target.serviceEnabled = true`
  - 提交真实主网签名后状态推进到 `COMPLETED`
- 后台真实链路证据:
  - `https://api.residential-agent.com/admin/` 可访问
  - admin login 返回真实 token
  - dashboard / orders / withdrawals admin API 返回 200
  - plans 管理 API 已验证：
    - `POST /api/admin/v1/plans`
    - `PUT /api/admin/v1/plans/:planId`
  - client 动态套餐链路已验证：
    - `GET /api/client/v1/plans` 可反映后台启用/停用结果

### 三机拆分现状

- 服务器三 `154.37.208.72`
  - `api.residential-agent.com`
  - backend / nginx / 对外 API 接入
- 服务器二 `38.58.59.142`
  - Marzban 控制面服务器
  - 本机运行 `Marzban + Nginx + PostgreSQL + Redis`
  - `vpn.residential-agent.com` 已在 2026-04-13 重新指向该机
- 服务器四 `38.246.249.106`
  - 首台 `Marzban-node`
  - 已接入 `38.58.59.142` 控制面
  - 当前 `status=connected`
  - 暂未加入自动分流 host 池

### VPN 控制面当前边界

- Android 生产主流程已切到 `subscriptionUrl -> 订阅导入 -> 本地节点连接`
- Node 后端负责：
  - auth / orders / payment / overview 业务数据
  - `subscriptionUrl` 与订阅状态下发
- Marzban 负责：
  - VPN 用户
  - 节点配置
  - 订阅链接
- 旧版 raw VLESS `issueVpnConfig/configPayload` 仅保留兼容接口，不再作为 Android 主流程
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
  - 外网 `/api/healthz` 返回 `200`
  - 服务器一内网 `http://127.0.0.1:4000/api/healthz` 返回 `healthy`
  - `sol-agent` systemd 服务为 `active`
  - 外网交易状态查询端点可返回真实签名状态
- `usdt.residential-agent.com`
  - 外网 `/health` 返回 `200`
  - 外网 `/api/healthz` 返回 `200`
  - 服务器一内网 `/api/healthz` 返回 `healthy + connected`
  - 受保护接口验证通过：
    - `/api/v1/chain/capabilities`
    - `/api/v1/chain/block/current`
    - `/api/v1/chain/tx/{hash}`

## 当前主线任务

### App 主线顺序

1. `liaojiang-4j0.7`
   - `vpnui` 唯一页面目录裁决
   - 已完成
2. `liaojiang-4j0.8`
   - `vpnui` 并入现有 `V2rayNG` 的组合方案
   - 已完成
3. `liaojiang-4j0.9`
   - 接入 Compose runtime 与 `ComposeContainerActivity`
   - 已完成
4. `liaojiang-4j0.10`
   - 迁移 `vpnui` 最终保留目录到 `com.v2ray.ang.composeui` 骨架
5. `liaojiang-4j0.11`
   - 桥接 Splash/Auth Compose 页面到现有登录与版本检查逻辑
6. `liaojiang-4j0.12`
   - 桥接 VPN/订单 Compose 页面到现有订阅与订单逻辑
7. `liaojiang-4j0.13`
   - 桥接钱包/增长/个人/法务 Compose 页面到现有数据层
8. `liaojiang-4j0.5`
   - 基于上述桥接结果形成可回归的 Android 页面改造落地
9. `liaojiang-4j0.2`
   - 最终 Android 真实环境登录/下单/支付页回归

### 当前 beads 状态

- `liaojiang-rcb.17` 已完成实现、验收与 continuity 刷新
- 当前没有新的 `bd ready` 任务
- 当前实质 blocker 为 `liaojiang-4j0.2`：仍缺 Android 真实回归账号凭据或可取验证码邮箱

## 下一步

1. 恢复 `liaojiang-4j0.2`，继续 Android 真实登录/下单/支付页回归，重点验证后台动态套餐在真实环境的端到端购买效果。
2. 若 `bd` 后续重新放出 Android UI 迁移子任务，则继续 `4j0.10 / 4j0.11 / 4j0.12 / 4j0.13` 的 Compose 桥接。
3. 若运营侧需要更完整套餐能力，可继续补套餐删除、发布审核、区域联动校验与审计日志展示，但这不阻塞当前动态套餐购买链路。
