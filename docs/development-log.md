# 开发日志

## 2026-04-26

### 今日工作

1. **建立多项目维护作业文档体系**
   - 新增主维护作业手册: [MAINTENANCE_OPERATIONS_MANUAL.md](/Users/cnyirui/git/projects/liaojiang/docs/MAINTENANCE_OPERATIONS_MANUAL.md)
   - 新增文档索引: [README.md](/Users/cnyirui/git/projects/liaojiang/docs/README.md)
   - 新增模块 runbook:
     - [Android App](/Users/cnyirui/git/projects/liaojiang/docs/maintenance/android-client.md)
     - [Auth & Accounts](/Users/cnyirui/git/projects/liaojiang/docs/maintenance/auth-accounts.md)
     - [Plans / Orders / VPN](/Users/cnyirui/git/projects/liaojiang/docs/maintenance/plans-orders-vpn.md)
     - [Wallet / Chain](/Users/cnyirui/git/projects/liaojiang/docs/maintenance/wallet-chain.md)
     - [Referral / Commission / Withdrawal](/Users/cnyirui/git/projects/liaojiang/docs/maintenance/growth-withdrawal.md)
     - [Admin / Market / Release](/Users/cnyirui/git/projects/liaojiang/docs/maintenance/admin-release-market.md)

2. **纳入现有资产索引**
   - 将 `docs/current-status.md`、`handoff/task-state.md`、`handoff/progress.md`、`docs/plans/`、`final_engineering_delivery_package/`、`deliverables/`、`test-results/`、`code/deploy/`、`code/backend/migrations/` 等纳入维护文档入口。
   - 明确当前主文档、历史记录文档、计划文档、回归/证据目录的区别，避免接手人从旧报告误判当前线上口径。

3. **维护约束**
   - 模块 runbook 均包含改哪里、联动哪里、验证什么、常见坑、接口清单、关键表、发布前检查项、关键源码/脚本、SQL、排障顺序、数据修复模板、线上禁忌和回滚示例。
   - 数据修复统一采用“先备份、再预览、再定向修复、再回滚路径”的结构。
   - 敏感运维信息仍只引用 `环境测试服务器.md`，不在新增文档中复制密码、token、私钥或助记词。

## 2026-03-31

### 今日工作

1. **整理技术文档**
   - 创建了完整的 [技术规格文档](TECHNICAL_SPECIFICATION.md)
   - 记录了系统架构、数据库模型、API 契约
   - 整理了已知问题和待修复项清单

2. **问题识别**
   - 发现服务端订单创建需要登录，但客户端未实现登录功能
   - 发现 CreateOrderRequest 字段不匹配
   - 发现 LoginActivity 只是一个占位符

3. **关键决策点**
   - 需要决定: 支持匿名购买 vs 强制先登录
   - 匿名购买: 首次购买自动创建账号
   - 强制登录: 必须先注册/登录才能购买

### 技术债务

- 服务端 Pydantic 模型需要修复 null 值验证
- 客户端需要完整的用户认证 UI
- 需要初始化区块链收款地址

### 下一步

参考 [当前状态](current-status.md) 中的优先级进行修复。

## 2026-04-07

### 今日工作

1. **完成 backend TRON 远程接线收尾**
   - 新增 `code/backend/src/modules/tron-client/`，补齐 `TRON_SERVICE_*` 配置
   - `wallet` TRON 主路径已支持远程地址校验与远程 proxy broadcast
   - 远程服务不可用时保留明确 fallback，不破坏现有 SOLANA 路径

2. **完成 backend 链侧聚合健康验收**
   - `/api/healthz` 现聚合 Solana + TRON 两个链侧健康摘要
   - 覆盖 disabled / healthy / degraded 三种可解释输出
   - `app.e2e-spec.ts` 与 `wallet.e2e-spec.ts` 已补齐对应验收覆盖

3. **完成 `liaojiang-rcb.17` 里程碑验收**
   - `liaojiang-rcb.17.2` / `liaojiang-rcb.17.3` / `liaojiang-rcb.17.4` 已在主线验收
   - 验证命令：
     - `pnpm --dir code/backend typecheck`
     - `pnpm --dir code/backend build`
     - `pnpm --dir code/backend test:e2e`
   - 均通过

### 当前结论

- `liaojiang-rcb.17` 的 backend follow-up 已完成，本轮没有新的 `bd ready` 工程任务
- 当前剩余主阻塞回到 Android `liaojiang-4j0.2`，仍需要真实回归账号或可取验证码邮箱

## [20260412-083427-6bd3480d-426c] Session Start | 2026-04-12T08:34:29+08:00
- CWD: /Users/cnyirui/git/projects/liaojiang
- Branch: codex/android-demock-live-data-v2
- Status:      195 files modified
