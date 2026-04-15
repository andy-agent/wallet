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
| Compose copy-card 清理 | 🟡 Batch 1 / 2 已完成 | 共享组件抑制与 mounted 页面说明型卡片删除已落地，后续仍需 contract 默认值与 preview/dev residue 清理 |
| 邀请/分享收口 | 🟡 App 侧已收口，Web 页已随 admin-web 部署 | `InviteLanding` 源码已纳入主线并随 admin-web 发布到服务器三，但 `vpn.residential-agent.com/invite` 是否对外生效仍取决于独立 nginx 路由接线 |
| 真实业务 smoke | 🟢 已推进 | 基础接口 smoke 已通过，订单最小链路已通过远程 Solana 服务完成真实 smoke |
| Sol 链侧服务 | 🟢 可用 | `sol.residential-agent.com` 内外健康检查均通过 |
| USDT/TRON 链侧服务 | 🟢 可用 | `usdt.residential-agent.com` 已接真实 TRON RPC，健康/区块/交易查询通过 |
| 链侧客户端接线 | 🟢 已完成最小链路 | `liaojiang-rcb.14` 已关闭，订单最小链路已接远程链侧 |
| Admin 后台 | 🟢 已具备套餐管理闭环 | `liaojiang-kbf8` 与 `liaojiang-zhy8` 已完成，`/admin` 子路径路由白屏已修复并重部署到线上 |

## 本轮完成

- 完成 `liaojiang-xn6o.5`
  - 共享组件已支持“空文案不渲染”
  - [FeaturePageTemplate.kt](/Users/cnyirui/git/projects/liaojiang/code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/composeui/components/feature/FeaturePageTemplate.kt)、[P2CoreH5Scaffold.kt](/Users/cnyirui/git/projects/liaojiang/code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/composeui/pages/p2/P2CoreH5Scaffold.kt)、[P2ExtendedH5Scaffold.kt](/Users/cnyirui/git/projects/liaojiang/code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/composeui/pages/p2extended/P2ExtendedH5Scaffold.kt)、[P01Chrome.kt](/Users/cnyirui/git/projects/liaojiang/code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/composeui/p0/ui/P01Chrome.kt) 已完成批量抑制
  - 主验收 `:app:compileFdroidDebugKotlin` 通过
- 完成 `liaojiang-xn6o.6`
  - 已生成 [COMPOSE_COPY_CARD_DELETION_MATRIX_2026-04-16.md](/Users/cnyirui/git/projects/liaojiang/docs/COMPOSE_COPY_CARD_DELETION_MATRIX_2026-04-16.md)
  - 已形成统一卡片分类与批次计划：
    - `badge`
    - `hero supportingText`
    - `hero stats`
    - `note/summary`
    - `highlights/checklist`
    - `FeaturePageTemplate sections`
- 完成 `liaojiang-xn6o.7`
  - mounted `p0 / p1 / p2 core / p2extended` 页面说明型 copy 已批量删除
  - 共享抑制生效后，mounted 页面已大范围去除：
    - 页面头部副文案
    - 说明卡 / 提示卡
    - hero stats / note / supportingText
  - 主验收 `:app:compileFdroidDebugKotlin :app:assembleFdroidDebug` 通过
- 完成 `liaojiang-xn6o.8`
  - 收款页已继续删除说明型文案
  - 已删除：
    - “当前网络”卡片中的 `地址尾号`、`校验状态`
    - “收款码”卡片中的状态字（如 `已配置收款地址`）
  - 已新增 [COMPOSE_COPY_CARD_DELETION_LOG.md](/Users/cnyirui/git/projects/liaojiang/docs/COMPOSE_COPY_CARD_DELETION_LOG.md) 作为逐次删除回溯日志
- 完成 `liaojiang-xn6o.9`
  - App 内只保留“邀请中心”作为入口
  - “分享推广链接”已收口为邀请中心内的系统分享动作
  - 邀请码复制动作已补 toast 反馈
  - 长账号标识不再在“我的邀请码”卡片中展示
- 完成 `liaojiang-xn6o.10`
  - 邀请中心/分享页已进一步对齐：
    - `InviteCenter` 优先使用 `share context` 的 `referralCode`
    - `InviteShare` 已删除“分享渠道 / 状态”卡片并补复制反馈
  - `InviteShare` 仍保留代码，但 App 主流程已不再把它作为主要入口
- 完成 `liaojiang-xn6o.11`
  - `code/admin-web` 已新增公开 `/invite` 路由与 [InviteLanding.tsx](/Users/cnyirui/git/projects/liaojiang/code/admin-web/src/pages/InviteLanding.tsx)
  - 邀请落地页已支持：
    - 读取 `?code=`
    - 展示邀请码
    - 下载 App CTA
    - 打开 App CTA
    - 复制邀请码 / 复制邀请链接
  - `code/admin-web` 本地 `npm run build` 通过
- 完成 `liaojiang-xn6o.12` 方案设计
  - 已确定“直接 Web 下载 APK”场景的最小闭环：
    - web `/invite?code=...`
    - App deep link `v2rayng://invite?...`
    - App 本地保存 pending referral
    - 登录/注册后自动调用 `bindReferralCode`
  - 已明确当前不上商店时，无法做到“安装后完全自动归因”

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

- 完成 `liaojiang-kbf8`
  - 已复现 `https://api.residential-agent.com/admin/plans` 白屏问题
  - 根因已定位为：
    - admin-web 线上挂载在 `/admin/`
    - 前端使用裸 `BrowserRouter`
    - 未设置 `basename=/admin`
    - 登录失效跳转仍硬编码到根路径 `/login`
  - 已修复：
    - `vite` 生产构建默认 `base=/admin/`
    - `BrowserRouter` 增加 `basename`
    - 401 未登录跳转改为基于 `BASE_URL`
    - 退出登录跳转与 `/admin` 子路径保持一致
  - 代码提交与推送：
    - commit `8501ba7b`

- 完成 `liaojiang-zhy8`
  - 已将最新 admin-web 构建产物重新部署到服务器三：
    - 目标主机 `154.37.208.72`
    - 目标目录 `/opt/cryptovpn/admin-web/`
  - 已执行：
    - `rsync --delete` 同步静态文件
    - `nginx -t`
    - `systemctl reload nginx`
  - 已补齐之前遗漏的 `InviteLanding` 源码与前端 API 封装，确保 HEAD 快照可独立构建
  - 本轮线上验收证据：
    - `https://api.residential-agent.com/admin/plans` 当前引用 `/admin/assets/index-B6HL9BUR.js`
    - 新 bundle 已包含 `/admin` 子路径路由基准逻辑
  - 代码提交与推送：
    - commit `036dcd62`

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
- 完成 `liaojiang-o9m8`
  - 已打通后台套餐配置到用户订阅返回链路：
    - `web admin plans -> /api/admin/v1/plans -> /api/client/v1/plans -> /api/client/v1/orders -> provisioning -> Marzban -> /api/client/v1/subscriptions/current`
  - `/api/client/v1/me` 不再固定返回 `subscription: null`
  - `/api/client/v1/subscriptions/current` 与 `/api/client/v1/me.subscription` 现可返回后台套餐名称 `planName`
  - Android 客户端已对齐优先消费 `planName` 展示“当前套餐 / 当前计划”
  - 线上后台地址已确认：
    - `https://api.residential-agent.com/admin/login`
    - `https://api.residential-agent.com/admin/plans`
  - 线上 admin 前端 bundle 已确认调用当前 backend admin 接口：
    - `/admin/v1/auth/login`
    - `/admin/v1/plans`
    - `/admin/v1/dashboard/summary`
    - `/admin/v1/orders`
    - `/admin/v1/withdrawals`
  - 本轮验证已通过：
    - `npm run test:e2e -- --no-cache admin-postgres.e2e-spec.ts vpn-postgres.e2e-spec.ts`
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
- 推广分享默认 web 链接当前由 backend 生成到：
  - `https://vpn.residential-agent.com/invite?code=...`
- 代码侧已补齐 `admin-web` 的公开 `/invite` 页面，但线上是否真正可用仍取决于：
  - `vpn.residential-agent.com/invite` 是否切到该前端
  - `BrowserRouter` 的 `index.html` fallback 是否配置正确

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
  - `https://api.residential-agent.com/admin/login` 可访问
  - `https://api.residential-agent.com/admin/plans` 可访问
  - `https://api.residential-agent.com/admin/plans` 不再命中旧白屏 bundle
  - 当前线上页面引用：
    - `/admin/assets/index-B6HL9BUR.js`
  - admin login 返回真实 token
  - dashboard / orders / withdrawals admin API 返回 200
  - plans 管理 API 已验证：
    - `POST /api/admin/v1/plans`
    - `PUT /api/admin/v1/plans/:planId`
  - client 动态套餐链路已验证：
    - `GET /api/client/v1/plans` 可反映后台启用/停用结果
    - `GET /api/client/v1/subscriptions/current` 可返回 `planName`
    - `GET /api/client/v1/me` 可返回真实 `subscription`

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
- `liaojiang-xn6o.5 / .6 / .7 / .8 / .9 / .10 / .11` 已完成
- `liaojiang-xn6o.12` 当前为“直接下载 APK 归因”设计已完成、实现待接入
- 当前实质 blocker 仍是人工联动验证：
  - Android 真实登录
  - 创建真实订单
  - 收款 / 发送
  - 推广分享 Web 落地页线上接线

## 下一步

1. 把 `admin-web` 的 `/invite` 页面接到 `vpn.residential-agent.com/invite?code=...`，并验证直接打开不为空。
2. 实现 `liaojiang-xn6o.12` 最小闭环：
   - Android `invite` deep link
   - pending referral 本地保存
   - 登录/注册后自动绑定
3. 继续按 [COMPOSE_COPY_CARD_DELETION_MATRIX_2026-04-16.md](/Users/cnyirui/git/projects/liaojiang/docs/COMPOSE_COPY_CARD_DELETION_MATRIX_2026-04-16.md) 推进：
   - contract 默认值清理
   - preview/dev residue 清理
4. 恢复 `liaojiang-4j0.2` / `liaojiang-xn6o.1.1` 人工联动验证，继续真实登录/下单/支付/收发款回归。
