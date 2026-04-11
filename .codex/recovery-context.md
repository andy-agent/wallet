# Recovery Context

Generated: 2026-04-11T06:19:00Z
Repository: /Users/cnyirui/git/projects/liaojiang

## Critical Path
- main_feature_id: liaojiang-26d
- active_task_id: liaojiang-g9e
- active_task_title: Backend/链侧：补齐 Solana 订单实单核验能力
- acceptance_gate: liaojiang-3js

## Live State
- live register/login/plans/order creation already verified against `https://api.residential-agent.com`
- production catalog tables `plans / vpn_regions / plan_region_permissions / vpn_nodes / system_configs` now exist on server3 and `/api/client/v1/plans` returns seeded real data
- current remaining gap is payment truth:
  - backend still needs real receiving address instead of synthetic `buildCollectionAddress`
  - backend still needs real SOLANA tx verification before provisioning

## Active Workers
- Cicero (`019d7b29-c201-7ed3-970f-08193ec29a80`)
  - scope: `code/sol-agent`
  - branch: `codex/liaojiang-g9e-sol`
  - worktree: `/Users/cnyirui/.config/superpowers/worktrees/liaojiang/codex-liaojiang-g9e-sol`
- Maxwell (`019d7b29-c2c3-7461-b020-a565d81200ac`)
  - scope: `code/backend`
  - branch: `codex/liaojiang-g9e-backend`
  - worktree: `/Users/cnyirui/.config/superpowers/worktrees/liaojiang/codex-liaojiang-g9e-backend`

## Server Facts
- server1: `38.58.59.119` hosts `sol-agent` and `usdt-agent`
- server3: `154.37.208.72` hosts public backend/API
- generated candidate Solana treasury public key for config bootstrap:
  - `5UMXzzLfAwW9wNFPzCStNERsYHoWG5xdwaR555Uht78q`
  - secret is intentionally not written here

## Next Milestone
- accept worker code for `g9e`
- deploy `sol-agent` to server1 and backend to server3
- verify live flow through `register -> login -> plans -> create order -> submit tx -> refresh-status -> subscription -> vpn`
