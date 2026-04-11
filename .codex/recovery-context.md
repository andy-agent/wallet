# Recovery Context

Generated: 2026-04-11T07:45:00Z
Repository: /Users/cnyirui/git/projects/liaojiang

## Critical Path
- main_feature_id: liaojiang-26d
- active_task_id: liaojiang-njb
- active_task_title: Live funding：完成一次真实 SOLANA/USDT 订单付款并回传 txHash
- acceptance_gate: liaojiang-3js

## Current Accepted Work
- liaojiang-d10 accepted on mainline
- liaojiang-9bk accepted on mainline
- liaojiang-0ij accepted on mainline
- liaojiang-tqu accepted on mainline
- liaojiang-mwe accepted on mainline
- liaojiang-vfy.1 accepted on mainline
- liaojiang-vfy accepted on mainline

## Live State
- register/login/plans/create-order/payment-target/subscriptions-current/vpn-status are live against api.residential-agent.com
- server1 sol-agent verify endpoint is deployed and server3 backend verify-before-provision flow is deployed
- negative-path proof is complete: mismatch tx lands in UNDERPAID_REVIEW and leaves subscription NONE
- Android fdroidDebug installs on adb device ba2b016
- debug-only Compose route harness is integrated for adb-driven route launches
- representative route-level device verification has been accepted in bd

## Remaining Blocker
- positive-path closure still requires one real funded SOLANA/USDT payment and resulting txHash
- no funded Solana wallet was discovered on server1/server3 during automated scans
- after txHash is available, run submit-client-tx, refresh-status, subscriptions/current, vpn/regions, and vpn/config/issue
