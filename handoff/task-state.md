# Task State

- Updated At: 2026-04-03 19:56 +08:00
- Repository: /Users/cnyirui/git/projects/liaojiang
- Next Task: `liaojiang-rcb.14.2.1` 实现订单支付检测最小链路接入远程链侧服务
- Counts: `open=2` `in_progress=3` `closed=78` `ready=1`
- Dirty Files:
  - `.codex/recovery-state.json`
  - `docs/current-status.md`
  - `handoff/task-state.md`
  - `docs/current-status.md`
  - `环境测试服务器.md`

## Resume Commands
```bash
bd ready --json
bd list --status=open --json
git status --short
curl -ksS https://api.residential-agent.com/api/healthz
curl -ksS https://sol.residential-agent.com/health
curl -ksS https://usdt.residential-agent.com/health
bd show liaojiang-rcb.14.2
```

## Current Verification

- API:
  - request-code
  - register
  - me
  - plans
  - orders
  - payment-target
  - referral overview
  - commissions summary
  - withdrawals returns expected insufficient-balance business rejection
- Sol chain-side:
  - `sol.residential-agent.com/health` returns `healthy`
- USDT/TRON chain-side:
  - server1 internal `health/capabilities/block/current/tx` verified
  - `usdt.residential-agent.com/health` returns `healthy + connected`

## Remaining Work

- `liaojiang-rcb.14.2.1`: implement backend order-chain minimal path integration
- `liaojiang-rcb.14.2.2`: deploy backend and run real order-chain smoke on `api.residential-agent.com`
- Android final build/regression remains intentionally deferred to the last stage
