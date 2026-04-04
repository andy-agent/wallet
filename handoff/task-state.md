# Task State

- Updated At: 2026-04-04 22:51 +08:00
- Repository: /Users/cnyirui/git/projects/liaojiang
- Next Task: `liaojiang-4j0.10` 迁移 vpnui 最终保留目录到 com.v2ray.ang.composeui 骨架
- Counts: `open=5` `in_progress=3` `closed=50` `ready=1`
- Dirty Files:
  - `.codex/recovery-state.json`
  - `docs/current-status.md`
  - `handoff/task-state.md`

## Resume Commands
```bash
bd ready --json
bd list --status=open --json
git status --short
curl -ksS https://api.residential-agent.com/api/healthz
curl -ksS https://api.residential-agent.com/admin/ | head
curl -ksS https://api.residential-agent.com/api/admin/v1/auth/login -H 'Content-Type: application/json' --data '{"username":"admin","password":"admin123"}'
bd show liaojiang-4j0.10
```

## Current Verification

- API:
  - request-code
  - register
  - me
  - plans
  - orders
  - payment-target
  - submit-client-tx
  - refresh-status
  - referral overview
  - commissions summary
  - withdrawals returns expected insufficient-balance business rejection
- Sol chain-side:
  - `sol.residential-agent.com/api/healthz` returns `healthy`
  - real signature query returns confirmed status
- USDT/TRON chain-side:
  - server1 internal `health/capabilities/block/current/tx` verified
  - `usdt.residential-agent.com/api/healthz` returns `healthy + connected`
- Real order-chain smoke:
  - order `ORD-1775219239579` reached `COMPLETED` via remote Solana service
- Admin:
  - `/admin/` entry reachable
  - admin login returns token
  - dashboard / orders / withdrawals API return 200
- App planning:
  - `VPNUI_DIRECTORY_DECISION.md` accepted
  - `VPNUI_INTEGRATION_PLAN.md` accepted
- Android compose migration:
  - `4j0.9` 已完成并入主线
  - `4j0.10` source-only repair commit `09704cc0` 已产出并通过 `:app:compileFdroidDebugSources`
  - 但正式验收未通过：仍缺 `vpnui/ui/components/**`、`vpnui/ui/effects/**`、更完整的 navigation 资产迁移
  - 同一 `codex-liaojiang-4j0-10-kimi` worktree 已第三轮重派修复，当前会话仍在运行

## Remaining Work

- `liaojiang-4j0.10`: migrate vpnui final directories into app skeleton
- `liaojiang-4j0.11`: bridge splash/auth compose pages
- `liaojiang-4j0.12`: bridge vpn/order compose pages
- `liaojiang-4j0.13`: bridge wallet/growth/profile/legal compose pages
- `liaojiang-4j0.2`: final Android real-environment regression after compose migration
