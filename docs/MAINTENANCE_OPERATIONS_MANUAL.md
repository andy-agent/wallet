# 项目维护作业手册

最后更新: 2026-04-26

本文档面向接手人、维护人和值班人。目标不是介绍项目，而是让维护人只按文档就能判断问题归属、定位代码、验证改动、发布、排障和回滚。

## 0. 文档分层与事实源

### 当前主文档

- 总维护入口: `docs/MAINTENANCE_OPERATIONS_MANUAL.md`
- 模块 runbook: `docs/maintenance/*.md`
- 文档索引: `docs/README.md`

### 历史记录文档

- 开发日志: `docs/development-log.md`
- 当前状态: `docs/current-status.md`
- 交接状态: `handoff/task-state.md`
- 交接进度: `handoff/progress.md`
- 历史 handoff 汇总: `handoff/README.md`, `handoff/SUMMARY.md`
- 旧 FastAPI 总结: `code/PROJECT_SUMMARY.md`, `code/FINAL_SUMMARY.md`

历史记录用于追溯，不能直接覆盖为新结论。维护时新增条目，保留旧上下文。

### 计划文档

- 总实施计划: `docs/implementation-plan.md`
- 计划目录: `docs/plans/`
- 冻结交付包: `final_engineering_delivery_package/`
- 当前规格: `specs/current-spec.md`
- 规格进度: `specs/progress.md`

### 回归/证据目录

- 全局回归: `docs/regression-report.md`, `docs/TEST_REPORT.md`, `docs/FINAL_TEST_REPORT.md`, `docs/REAL_TEST_REPORT.md`, `docs/E2E_TEST_REPORT.md`
- Android 真实设备: `docs/ANDROID_REAL_DEVICE_VISUAL_CHECK.md`, `deliverables/ui-compare-2026-04-26/`
- VPN UI 回归: `vpnui/test/REGRESSION_REPORT.md`
- 自动测试输出: `test-results/`

### 项目记忆

- 本仓库使用 beads (`bd`) 作为任务和长期记忆源。
- 开工前运行 `bd prime`。
- 新维护动作必须创建或认领 beads issue。
- 长期维护约定用 `bd remember --key <key> "<内容>"` 写入。

## 1. 项目全貌

本仓库是多项目混合仓，核心业务是 Ghost / CryptoVPN Android App、账号、套餐订阅、链上支付、钱包、邀请分佣、提现、后台管理和链侧服务。

当前真实环境不是单一服务:

- 主 API: `https://api.residential-agent.com/api`
- Admin Web: `https://api.residential-agent.com/admin/`
- 邀请页兼容入口: `https://vpn.residential-agent.com/invite?code=...`
- Solana 链侧: `https://sol.residential-agent.com/api`
- TRON / TRC20-USDT 链侧: `https://usdt.residential-agent.com/api`

敏感服务器信息在 `环境测试服务器.md`，该文件包含运维入口和凭据类信息。不要把其中的密码、token、私钥内容复制到聊天、日志、PR 或文档摘要中。

## 2. 子项目边界

| 子项目 | 路径 | 技术栈 | 职责 | 发布产物 | 线上目标位置 | 联动 |
| --- | --- | --- | --- | --- | --- | --- |
| Android App | `code/Android/V2rayNG` | Kotlin, Gradle, Compose, v2rayNG | 移动端 UI、登录、钱包、套餐、支付确认、VPN、邀请、收发款 | APK/AAB，当前常用 fdroid debug/universal APK | APK 下载路径 `/opt/cryptovpn/downloads/cryptovpn-android.apk`，下载 URL 由 Admin Web/`app_versions` 引用 | 主 backend、Sol/TRON 链侧、Admin Web 邀请页 |
| 主 backend | `code/backend` | NestJS, TypeScript, PostgreSQL, Redis | 账号、订单、套餐、VPN 状态、钱包元数据、邀请、佣金、提现、Admin API | `dist/` Node 服务 | 服务器三 `/opt/cryptovpn/backend`, systemd `cryptovpn-backend.service` | Android、Admin Web、Sol Agent、USDT Agent、PostgreSQL、Redis |
| Admin Web | `code/admin-web` | React, TypeScript, Vite, Ant Design | 后台页面、套餐/节点/订单/提现/版本/法务配置、邀请落地页 | `dist/` 静态文件 | 服务器三 `/opt/cryptovpn/admin-web/`, nginx `/admin/` 和 `/invite` | 主 backend Admin API、APK 下载地址 |
| Sol Agent | `code/sol-agent` | NestJS, TypeScript, Solana RPC | Solana 地址/交易/支付检测/签名校验 | `dist/` Node 服务或 compose | 服务器一 `/opt/sol-agent`, systemd `sol-agent.service`, 端口 `4000` | 主 backend `SOLANA_SERVICE_URL` |
| USDT Chain Service | `code/backend-chain-usdt` | NestJS, TypeScript, TRON RPC | TRON/TRC20 查询、广播、区块、地址校验 | `dist/` Node 服务 | 服务器一，端口 `4001`, 域名 `usdt.residential-agent.com` | 主 backend `TRON_SERVICE_URL` |
| 旧 FastAPI Payment Bridge | `code/server` | FastAPI, SQLAlchemy, Alembic | 旧支付桥接、旧订单/worker/Marzban 集成 | Docker API/worker | 旧测试部署 `/opt/payment-bridge/`，不是当前主线事实源 | 只用于历史追溯或迁移参考 |
| 部署配置 | `code/deploy` | Docker Compose, shell | compose 栈、部署模板、环境样例 | `docker-compose*.yml`, `.env.*example` | 本地/服务器执行 | backend/admin/sol-agent |
| UI 比对包 | `deliverables/ui-compare-2026-04-26` | APK, screenshots | V5/V6/V7 前端真机对比产物 | `Ghost-UI-V*-universal.apk` | 真机 `ba2b016` 已安装包名 `.uiv1/.uiv2/.uiv3` | Android UI 评估 |

## 3. 接手必读顺序

1. 运行 `bd prime`，确认当前任务、记忆和会话约束。
2. 读本文件，确认问题属于哪个子项目。
3. 读 `docs/README.md`，找到模块 runbook 和已有证据。
4. 读 `docs/current-status.md` 的最新条目，确认当前线上状态。
5. 读对应模块 runbook，例如 `docs/maintenance/plans-orders-vpn.md`。
6. 如果要碰线上，先读 `环境测试服务器.md` 和 `code/deploy/*.md`，只取必要运维信息，禁止泄露凭据。
7. 开工前 `bd create` 或 `bd update <id> --claim`。
8. 只改当前任务需要的文件，保留历史文档和未关联脏文件。

## 4. 问题归属判断表

| 现象 | 优先归属 | 首查文件/命令 | 继续看 |
| --- | --- | --- | --- |
| App 白屏、页面错位、底部导航异常 | Android App | `code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/composeui/navigation/`, `adb logcat` | `docs/maintenance/android-client.md` |
| 登录/注册/验证码/刷新 token 失败 | Auth & Accounts | `code/backend/src/modules/auth/`, `accounts`, `verification_codes`, `client_sessions` | `docs/maintenance/auth-accounts.md` |
| 套餐不显示、下单失败、支付目标错误 | Plans / Orders / VPN | `code/backend/src/modules/plans/`, `orders/`, `vpn/` | `docs/maintenance/plans-orders-vpn.md` |
| 钱包首页余额不对、收款地址不对、发送失败 | Wallet & Chain | `code/backend/src/modules/wallet/`, Android `PaymentApi.kt`, `sol-agent`, `backend-chain-usdt` | `docs/maintenance/wallet-chain.md` |
| 邀请码绑定、佣金、提现失败 | Growth / Withdrawal | `code/backend/src/modules/referral/`, `withdrawals/`, `admin/withdrawals/` | `docs/maintenance/growth-withdrawal.md` |
| 后台登录、套餐配置、节点管理、版本发布失败 | Admin / Release | `code/admin-web/src/pages/`, `code/backend/src/modules/admin/` | `docs/maintenance/admin-release-market.md` |
| 行情接口、App 版本更新接口异常 | Market / App Version | `code/backend/src/modules/market/`, `app-versions/` | `docs/maintenance/admin-release-market.md` |
| Solana tx 查询/支付校验异常 | Sol Agent | `code/sol-agent/src/modules/payment/`, `transactions/` | `docs/maintenance/wallet-chain.md` |
| TRON tx 查询/广播异常 | USDT Chain | `code/backend-chain-usdt/src/modules/chain/` | `docs/maintenance/wallet-chain.md` |
| 旧部署、旧状态机或旧 FastAPI 行为 | Legacy Server | `code/server/`, `code/PROJECT_SUMMARY.md` | 仅作历史参考 |

## 5. 本地开发命令

### 主 backend

```bash
cd code/backend
pnpm install
pnpm run typecheck
pnpm run build
pnpm run test:e2e
pnpm run start:dev
curl http://localhost:3000/api/healthz
```

### Admin Web

```bash
cd code/admin-web
npm install
npm run dev
npm run build
npm run preview
```

### Android App

```bash
cd code/Android/V2rayNG
JAVA_HOME="/Applications/Android Studio.app/Contents/jbr/Contents/Home" ./gradlew --no-daemon --max-workers=2 -Dkotlin.compiler.execution.strategy=in-process :app:assembleFdroidDebug
adb install -r app/build/outputs/apk/fdroid/debug/app-fdroid-universal-debug.apk
adb shell am start -W -n com.v2ray.ang.fdroid/com.v2ray.ang.ui.ComposeLauncherAlias
adb logcat -d | rg -i 'FATAL EXCEPTION|AndroidRuntime|com.v2ray.ang'
```

### Sol Agent

```bash
cd code/sol-agent
pnpm install
pnpm run typecheck
pnpm run build
pnpm run test:e2e
pnpm run start:dev
curl http://localhost:4000/api/healthz
```

### USDT Chain Service

```bash
cd code/backend-chain-usdt
pnpm install
pnpm run typecheck
pnpm run build
pnpm run test:e2e
pnpm run start:dev
curl http://localhost:3001/api/healthz
```

### 旧 FastAPI 服务

```bash
cd code/server
pip install -r requirements.txt
alembic upgrade head
uvicorn app.main:app --reload
pytest
```

旧服务不是当前主线事实源。除非任务明确要求迁移/排查旧服务，否则不要把旧服务接口当作线上当前口径。

## 6. 标准维护流程

1. 建任务: `bd create ...` 或认领现有任务。
2. 判归属: 用第 4 节定位模块 runbook。
3. 读资产: 当前主文档、模块 runbook、相关计划、最新回归证据。
4. 最小复现: 本地命令、curl、adb 或 SQL 只读查询。
5. 最小修改: 只改问题所在模块，避免跨子项目重构。
6. 本地验证: 至少跑模块 runbook 的验证命令；资金/订单/钱包类必须补 e2e 或只读线上探针。
7. 证据落盘: 更新 `docs/development-log.md`、必要时更新 `docs/current-status.md`、`handoff/progress.md`、证据目录。
8. beads 更新: 记录结论，完成后 `bd close <id> --reason "..."`
9. 推送要求: 按仓库 AGENTS 要求推 git 和 `bd dolt push`。如果网络阻塞，创建 blocked follow-up issue 并说明错误。

## 7. 验证标准

| 改动类型 | 最低验证 | 加强验证 |
| --- | --- | --- |
| backend 业务逻辑 | `pnpm --dir code/backend typecheck` + 定向 spec/e2e | `pnpm --dir code/backend test:e2e` |
| DB/schema/seed | 本地/staging 预演 SQL + rollback path | 线上只读核验、备份、分批执行 |
| Android UI | `:app:compileFdroidDebugKotlin` 或 `:app:assembleFdroidDebug` | 真机安装、截图、logcat |
| Android API 联动 | App 真机流程 + backend e2e | 真实账号/真实订单 smoke |
| Admin Web | `npm --prefix code/admin-web run build` | 部署到 `/admin/` 后 curl + 浏览器检查 |
| Sol/TRON 链侧 | `typecheck/build/test:e2e` + `/api/healthz` | 真实 tx 查询或签名校验 |
| 发布文档 | 链接/路径存在性检查 | 接手人按手册 dry run |

## 8. 发布口径

### 主 backend

- 本地构建源: `code/backend`
- 线上目录: `/opt/cryptovpn/backend`
- 线上环境文件: `/opt/cryptovpn/backend/.env.local`
- 线上服务: `cryptovpn-backend.service`
- 入口: `/opt/cryptovpn/backend/dist/main.js`
- 部署参考: `code/deploy/BACKEND_DEPLOYMENT.md`

### Admin Web

- 本地构建源: `code/admin-web`
- 构建产物: `code/admin-web/dist/`
- 线上目录: `/opt/cryptovpn/admin-web/`
- Nginx 入口: `/admin/`, `/invite`
- 部署参考: `code/deploy/ADMIN_WEB_DEPLOYMENT.md`

### Android APK

- 构建源: `code/Android/V2rayNG`
- 常用 debug 产物: `app/build/outputs/apk/fdroid/debug/app-fdroid-universal-debug.apk`
- 线上下载位置: `/opt/cryptovpn/downloads/cryptovpn-android.apk`
- 下载 URL: `https://vpn.residential-agent.com/downloads/cryptovpn-android.apk`
- App 版本接口: `GET /api/client/v1/app-versions/latest`
- 版本配置表: `app_versions`
- 正式签名/商店渠道: 待补充。没有签名材料时不要声称正式发布完成。

### 链侧服务

- Sol Agent: `code/sol-agent`, 服务器一 `/opt/sol-agent`, `sol-agent.service`
- USDT Agent: `code/backend-chain-usdt`, 服务器一，端口 `4001`
- 部署参考: `code/deploy/SOL_AGENT_DEPLOYMENT.md`, `code/backend-chain-usdt/README.md`

## 9. 回滚口径

回滚优先级:

1. 停止继续发布或撤回流量。
2. 应用层回滚到上一份可验证产物。
3. 配置层恢复上一版 `.env.local` 或 nginx 配置。
4. 数据层只做补偿/定向修复，不直接对生产资金域执行物理 downgrade。

禁止写成一键破坏命令。回滚操作模板必须先列出备份和校验命令，再列出需要人工替换的占位符。

示例:

```bash
# 1. 记录当前版本
ssh <host> 'systemctl status cryptovpn-backend.service --no-pager'
ssh <host> 'cd /opt/cryptovpn/backend && ls -lah dist package.json .env.local'

# 2. 准备上一版目录或制品，人工确认 <previous-release-dir>
ssh <host> 'test -d <previous-release-dir>'

# 3. 切换前先备份当前目录元数据，禁止删除数据目录
ssh <host> 'cp -a /opt/cryptovpn/backend/.env.local /opt/cryptovpn/backend/.env.local.rollback.$(date +%Y%m%d%H%M%S)'

# 4. 人工执行文件替换后重启服务
ssh <host> 'systemctl restart cryptovpn-backend.service && systemctl status cryptovpn-backend.service --no-pager'

# 5. 验证
curl https://api.residential-agent.com/api/healthz
```

## 10. 日常巡检

每天或发布后巡检:

```bash
curl https://api.residential-agent.com/api/healthz
curl https://sol.residential-agent.com/api/healthz
curl https://usdt.residential-agent.com/api/healthz
curl -I https://api.residential-agent.com/admin/
curl -I "https://vpn.residential-agent.com/invite?code=CHECK"
curl -I https://vpn.residential-agent.com/downloads/cryptovpn-android.apk
```

数据库只读巡检模板:

```sql
-- 最近订单状态分布
SELECT status, count(*)
FROM orders
WHERE created_at >= now() - interval '24 hours'
GROUP BY status
ORDER BY status;

-- 支付事件是否有重复 tx
SELECT chain, tx_hash, count(*)
FROM order_payment_events
GROUP BY chain, tx_hash
HAVING count(*) > 1;

-- 提现待处理数量
SELECT status, count(*)
FROM commission_withdraw_requests
GROUP BY status
ORDER BY status;
```

## 11. 通用排障入口

1. 先确认是否只有前端问题: 浏览器/真机截图、console/logcat。
2. 再确认 API 是否通: `curl /api/healthz`，目标接口 curl。
3. 再确认数据库状态: 只读查询核心表。
4. 再确认外部依赖: Sol/TRON/Marzban/Redis/Postgres。
5. 最后查发布差异: git diff、最近部署目录、systemd/nginx 日志。

禁止从“看起来像 UI 问题”直接改后端状态；也禁止从“订单没完成”直接改订单为 `COMPLETED`。

## 12. 数据修复通用原则

所有数据修复按这个结构写入模块 runbook 或临时修复单:

1. 备份: `CREATE TABLE ... AS SELECT ... WHERE ...`
2. 预览: `SELECT ... WHERE ...` 并记录数量和主键。
3. 定向修复: 带主键、状态和时间边界的 `UPDATE` / `INSERT`。
4. 复核: 再查业务读接口和关联表。
5. 回滚路径: 从备份表恢复原值或写反向补偿。
6. 记录: `docs/development-log.md` 和 beads issue。

## 13. 线上操作禁忌

- 禁止在生产执行无 `WHERE` 的 `UPDATE` / `DELETE`。
- 禁止直接改资金、订单、佣金、提现状态绕过状态机，除非有明确补偿方案和审计记录。
- 禁止在生产执行 `0001_init.down.sql` 或任何会 drop 资金域表的脚本。
- 禁止把 `环境测试服务器.md` 中的密码、token、私钥、API key 复制到聊天、日志或公开 PR。
- 禁止在未备份的情况下覆盖 `/opt/cryptovpn/backend/.env.local`。
- 禁止把未签名、未校验 sha256 的 APK 标成正式版本。
- 禁止把 `code/server` 旧 FastAPI 行为当成当前主 backend 线上口径。
- 禁止在 Android 发布包中引入 mock 数据作为最终验收依据。

## 14. 交接模板

```text
当前 accepted task:
- beads id:
- 目标:
- 已完成:

当前 active worker task:
- 正在改的子项目:
- 涉及路径:
- 未完成点:

验证证据:
- 本地命令:
- 线上探针:
- 截图/日志路径:

发布状态:
- 未发布 / 已发布到:
- 版本/commit:
- 回滚入口:

数据动作:
- 是否执行 SQL:
- 备份表:
- 回滚语句位置:

阻塞:
- 需要谁输入:
- 禁止继续做什么:

下一步:
- 下一个 bd ready:
- 推荐先读文档:
```

## 15. 模块 runbook 索引

- Android App: `docs/maintenance/android-client.md`
- Auth & Accounts: `docs/maintenance/auth-accounts.md`
- Plans / Orders / VPN: `docs/maintenance/plans-orders-vpn.md`
- Wallet / Chain: `docs/maintenance/wallet-chain.md`
- Referral / Commission / Withdrawal: `docs/maintenance/growth-withdrawal.md`
- Admin / Market / Release: `docs/maintenance/admin-release-market.md`

## 16. 待补充信息

- 正式 Android 签名材料位置、签名命令、渠道发布路径。
- 服务器一 USDT agent 线上目录和 systemd unit 精确名称。
- 主 backend 发布时上一版制品保留目录规范。
- Postgres/Redis 隧道保活脚本和监控位置。
- Marzban 当前 API 凭据轮换 runbook。

## 17. 推断边界

以下内容是根据现有仓库文档、源码和部署说明综合推断，维护时需要在真实环境中复核后再执行:

- `code/server` 被标为 legacy，是根据 `docs/current-status.md`、`code/deploy/BACKEND_DEPLOYMENT.md` 和当前 NestJS `code/backend` 真实部署口径推断。
- Android APK 线上下载路径 `/opt/cryptovpn/downloads/cryptovpn-android.apk` 是根据 `code/deploy/ADMIN_WEB_DEPLOYMENT.md` 和 nginx 示例推断；正式签名与渠道发布仍待补充。
- USDT agent 的线上服务名未在仓库中精确固化，手册只写“服务器一、端口 4001、`usdt.residential-agent.com`”，执行 systemd 操作前必须先 `systemctl list-units | rg 'usdt|chain'` 复核。
- Market 模块未列独立数据库表，是根据 `code/backend/migrations/baseline/0001_init.up.sql` 和 `code/backend/src/modules/market/` 的 provider 结构推断。
- 回滚模板中的 `<host>`、`<key>`、`<previous-dist>`、`<previous-apk>` 都是占位符，不能原样执行。
