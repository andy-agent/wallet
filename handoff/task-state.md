# Task State

- Updated At: 2026-04-03 20:34 +08:00
- Repository: /Users/cnyirui/git/projects/liaojiang
- Next Task: `liaojiang-4j0.2` Android 真实环境登录/下单/支付页回归
- Counts: `open=2` `in_progress=2` `closed=89` `ready=2`
- Dirty Files:
  - `.codex/recovery-state.json`
  - `docs/current-status.md`
  - `handoff/task-state.md`
  - `环境测试服务器.md`

## Resume Commands
```bash
bd ready --json
bd list --status=open --json
git status --short
curl -ksS https://api.residential-agent.com/api/healthz
curl -ksS https://sol.residential-agent.com/api/healthz
curl -ksS https://usdt.residential-agent.com/api/healthz
bd show liaojiang-4j0.2
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
- Android:
  - final `compileFdroidDebugSources` passed
  - final `assembleFdroidDebug` passed
  - fresh APK reinstall succeeded on emulator
  - launcher start succeeded, no immediate crash in recent logcat

## Remaining Work

- `liaojiang-4j0.2`: Android real-environment login/order/payment-page regression
- `liaojiang-4j0`: final Android build and real-environment regression feature
- `liaojiang-rcb`: phase-2 chain-side/infra feature remains open for longer-term topology cleanup
