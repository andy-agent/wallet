# RCB20 Live Payment Guide

## Scope
- Task: `liaojiang-rcb.20.1`
- Generated: 2026-04-08
- Purpose: provide the non-sensitive payment instructions needed for the final human-controlled funding step.

## Fresh Order
- `orderNo`: `ORD-1775650877315-64DA3EFB`
- Payment rail: `SOLANA`
- Asset: `USDT`
- Payable amount: `9.990000`
- Unique amount delta: `0.002454`
- Expires at: `2026-04-08T12:36:17.315Z`

## Target
- Collection address: `So11111111111111111111111111111111111wujgtk`
- QR text: `SOLANA:ORD-1775650877315-64DA3EFB:9.990000`

## What The Human Payment Step Must Do
1. Use the same authenticated account session that created this order.
2. Send the required small real payment on the `SOLANA` rail to the collection address above.
3. Preserve the real chain transaction hash returned by the wallet after broadcast.

## What The Orchestrator Needs After Funding
- `orderNo`: `ORD-1775650877315-64DA3EFB`
- `networkCode`: `SOLANA`
- `txHash`: the real chain transaction hash from the completed payment

## Next Acceptance Step
- After funding, the orchestrator should run:
  - `submit-client-tx`
  - `refresh-status`
  - subscription readback
  - VPN issuance / status readback
- Expected final outcome: order reaches `COMPLETED` and the subscription / VPN result is readable.

## Safety Note
- This guide intentionally excludes access tokens, passwords, wallet secrets, or any other credential material.
