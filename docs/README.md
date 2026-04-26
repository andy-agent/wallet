# Docs Index

最后更新: 2026-04-26

本目录按维护用途分为四类。接手项目时先读“当前主文档”，再按模块进入 runbook，不要直接从历史报告推断当前线上状态。

## 当前主文档

- 总维护作业手册: `docs/MAINTENANCE_OPERATIONS_MANUAL.md`
- 模块 runbook 目录: `docs/maintenance/`
  - Android App: `docs/maintenance/android-client.md`
  - Auth & Accounts: `docs/maintenance/auth-accounts.md`
  - Plans / Orders / VPN: `docs/maintenance/plans-orders-vpn.md`
  - Wallet / Chain: `docs/maintenance/wallet-chain.md`
  - Referral / Commission / Withdrawal: `docs/maintenance/growth-withdrawal.md`
  - Admin / Market / Release: `docs/maintenance/admin-release-market.md`

## 历史记录文档

- `docs/development-log.md`
- `docs/current-status.md`
- `handoff/task-state.md`
- `handoff/progress.md`
- `handoff/README.md`
- `handoff/SUMMARY.md`

## 计划文档

- `docs/implementation-plan.md`
- `docs/plans/2026-04-15-marzban-control-plane-decoupling.md`
- `docs/plans/2026-04-15-wallet-address-persistence-and-qr.md`
- `docs/plans/2026-04-16-wallet-custody-and-send-architecture.md`
- `docs/plans/2026-04-19-wallet-multiwallet-test-design.md`
- `docs/plans/2026-04-19-wallet-multiwallet-test-implementation.md`
- `docs/plans/2026-04-21-wallet-local-first-cache-and-price.md`
- `docs/plans/2026-04-22-vpn-cashier-wallet-ux.md`
- `docs/plans/2026-04-23-vpn-tiered-qos-static-ip-design.md`
- `docs/plans/2026-04-23-vpn-tiered-qos-static-ip-implementation.md`
- `docs/plans/2026-04-26-referral-withdrawal-backfill-plan.md`
- 冻结交付包: `final_engineering_delivery_package/`

## 回归 / 证据目录

- `docs/regression-report.md`
- `docs/TEST_REPORT.md`
- `docs/FINAL_TEST_REPORT.md`
- `docs/REAL_TEST_REPORT.md`
- `docs/E2E_TEST_REPORT.md`
- Android 真机 UI 比对: `deliverables/ui-compare-2026-04-26/`
- 前端重构交接包: `deliverables/app-frontend-refactor-handoff-2026-04-26/`
- VPN UI 回归: `vpnui/test/REGRESSION_REPORT.md`
- 自动测试输出: `test-results/`

## 部署 / 运维文档

- 环境入口: `环境测试服务器.md`
- 主 backend 部署: `code/deploy/BACKEND_DEPLOYMENT.md`
- Admin Web 部署: `code/deploy/ADMIN_WEB_DEPLOYMENT.md`
- Sol Agent 部署: `code/deploy/SOL_AGENT_DEPLOYMENT.md`
- 旧 Docker 部署: `code/deploy/DEPLOYMENT.md`
- Marzban 部署: `docs/MARZBAN_DEPLOYMENT.md`
- VNC 指南: `docs/VNC_SETUP_GUIDE.md`

## SQL / 脚本入口

- 主 backend baseline: `code/backend/migrations/baseline/0001_init.up.sql`
- 主 backend rollback baseline: `code/backend/migrations/baseline/0001_init.down.sql`
- 主 backend seed: `code/backend/migrations/seeds/0001_bootstrap_seed.sql`
- 冻结包 DDL: `final_engineering_delivery_package/10_postgresql_core_ddl.sql`
- 冻结包 seed: `final_engineering_delivery_package/10_postgresql_bootstrap_seed.sql`
- 旧 SQL: `code/sql/001_initial_schema.sql`
- 部署脚本: `code/deploy/deploy-test.sh`
- Marzban 脚本: `code/server/scripts/deploy_marzban.sh`

## 文档维护规则

1. 新增操作手册优先挂到 `docs/MAINTENANCE_OPERATIONS_MANUAL.md` 或 `docs/maintenance/`。
2. 历史文档只追加，不覆盖旧审计意义。
3. 任何线上修复都要写入 `docs/development-log.md`，必要时同步 `docs/current-status.md`。
4. 涉及长期规则时写入 beads memory: `bd remember --key <key> "<content>"`。
5. 文档中不要写入密码、token、私钥、助记词、API key。
