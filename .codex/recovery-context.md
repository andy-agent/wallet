# Recovery Context

Generated: 2026-04-11T18:48:00+08:00
Repository: /Users/cnyirui/git/projects/liaojiang

## Active Task
- `liaojiang-c9x` 验证/部署：共享地址自动匹配方案现网验收

## Latest Accepted Work
- `liaojiang-k27` accepted on mainline commit `c13c80e2`
- backend matcher skeleton landed on mainline commit `657a0216`

## Live Deployment State
- server3 `154.37.208.72`
  - backend updated and restarted as `cryptovpn-backend.service`
  - local `sol-agent` created at `/opt/cryptovpn/sol-agent`
  - local `sol-agent` service: `cryptovpn-sol-agent.service`
  - backend now uses `SOLANA_SERVICE_URL=http://127.0.0.1:4000`
  - backend matcher enabled with `PAYMENT_MATCHER_ENABLED=true`
- local chain-side verification on server3:
  - `POST http://127.0.0.1:4000/api/internal/v1/payment/scan-incoming` returns `200 OK`

## Live Regression Evidence
- public app flow succeeded on `https://api.residential-agent.com`
  - register
  - login
  - create order
  - get payment-target
- sample order:
  - `ORD-1775904273430-4F0A9C6D`
  - `baseAmount=9.990000`
  - `payableAmount=9.990003`
  - `uniqueAmountDelta=0.000003`
  - `collectionAddress=EVYe1JoVU9m46o5QLgJdZM6CCG996jfCvYoKu5DTNEjj`
  - `serviceEnabled=true`
- unpaid refresh check:
  - `refresh-status` returns `AWAITING_PAYMENT`
  - `matchedOnchainTxHash=null`
  - `paymentMatchedAt=null`

## Real Blocker
- `liaojiang-njb`
- remaining acceptance evidence requires one funded human-controlled `SOLANA/USDT` payment to:
  - `EVYe1JoVU9m46o5QLgJdZM6CCG996jfCvYoKu5DTNEjj`
- without a real funded payment source, positive auto-match -> provision -> VPN closed-loop cannot be completed

## Next Recovery Action
- if funded payment becomes available:
  1. create a fresh SOLANA/USDT order
  2. pay exact `payableAmount`
  3. wait for matcher tick
  4. verify order moves to `COMPLETED`
  5. verify subscription current + VPN provisioning on live APIs
