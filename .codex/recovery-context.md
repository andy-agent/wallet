# Recovery Context

Generated: 2026-04-11T07:02:00Z
Repository: /Users/cnyirui/git/projects/liaojiang

## Critical Path
- main_feature_id: liaojiang-26d
- active_task_id: liaojiang-njb
- active_task_title: Live funding：完成一次真实 SOLANA/USDT 订单付款并回传 txHash
- acceptance_gate: liaojiang-3js

## Live State
- register/login/plans/create-order/payment-target/subscriptions-current/vpn-status are live against api.residential-agent.com
- server1 sol-agent verify endpoint is deployed and server3 backend verify-before-provision flow is deployed
- negative-path proof is complete: mismatch tx now lands in UNDERPAID_REVIEW and leaves subscription NONE

## Blocker Evidence
- server1 collection wallet: ChbtUwZqyi2wePLDTs57UyPPbn8jSpHTbn49FQWuX6wG
- server1 collection wallet balance: 0 SOL
- no funded Solana hot wallet or private key was discovered on server1/server3 during automated scans

## Remaining Blocker
- positive-path closure requires one real funded SOLANA/USDT payment from an external wallet, or explicit approval to spend real funds from a funded wallet once provided
- after obtaining txHash, run submit-client-tx, refresh-status, subscriptions/current, vpn/regions, vpn/config/issue
