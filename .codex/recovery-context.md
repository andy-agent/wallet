# Recovery Context

Generated: 2026-04-11T06:50:00Z
Repository: /Users/cnyirui/git/projects/liaojiang

## Critical Path
- main_feature_id: liaojiang-26d
- active_task_id: liaojiang-njb
- active_task_title: Live funding：完成一次真实 SOLANA/USDT 订单付款并回传 txHash
- acceptance_gate: liaojiang-3js

## Live State
- live `register -> login -> plans -> create order -> payment-target -> subscriptions/current -> vpn/status` has been verified against `https://api.residential-agent.com`
- production catalog tables exist on server3 and `/api/client/v1/plans` returns seeded real rows
- server1 `sol-agent` real verify endpoint is deployed and reachable
- server3 backend is deployed with:
  - `SOLANA_ORDER_COLLECTION_ADDRESS=ChbtUwZqyi2wePLDTs57UyPPbn8jSpHTbn49FQWuX6wG`
  - `SOLANA_SERVICE_USE_DEVNET=false`
  - real verify-before-provision flow
- live negative-path proof is complete:
  - direct `sol-agent` verify returns `status=mismatch` for an unrelated real tx against the configured collection address
  - a live client order using that tx now lands in `UNDERPAID_REVIEW`
  - `subscriptions/current` remains `NONE`

## Remaining Blocker
- positive-path closure still requires a real human-controlled SOLANA/USDT payment
- create a fresh order immediately before funding because orders expire in 15 minutes
- once a valid txHash exists, the orchestrator must run:
  - `submit-client-tx`
  - `refresh-status`
  - `subscriptions/current`
  - `vpn/regions`
  - `vpn/config/issue`

## Current Proof Order
- mismatch proof order: `ORD-1775890039875-A8FF9050`

## Next Milestone
- wait for `liaojiang-njb`
- after real funding, finish the positive-path acceptance and then close `liaojiang-26d` / `liaojiang-3js`
