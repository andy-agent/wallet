# Progress Handoff

- Accepted milestone: `liaojiang-rcb.17`
- Accepted scope:
  - backend 新增 `tron-client` 模块与 `TRON_SERVICE_*` 配置
  - wallet TRON `transfer/precheck` 与 `transfer/proxy-broadcast` 已接远程客户端并保留明确 fallback
  - `/api/healthz` 现已聚合 Solana + TRON 链侧健康，并覆盖 disabled / healthy / degraded 场景
- Verification:
  - `pnpm --dir code/backend typecheck`
  - `pnpm --dir code/backend build`
  - `pnpm --dir code/backend test:e2e`
  - 全部通过
- Next task:
  - `liaojiang-4j0.2` 仍被真人测试账号/验证码邮箱阻塞
  - 当前没有新的 `bd ready` 任务
