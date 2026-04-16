# Wallet Address Persistence And QR Follow-Up Plan

**Goal:** Make wallet receive addresses persistent on the backend, cache them safely on Android, and ensure Receive / Checkout / WalletPaymentConfirm QR content derives from persisted truth rather than process memory or placeholder payloads.

**Source Tasks:** `liaojiang-5ne.5`, `liaojiang-5ne.12`

---

## Task Mapping

### `liaojiang-5ne.16`
Persist wallet public addresses in backend runtime state.

Scope:
- add backend persistent address table/model
- add repository methods for wallet public addresses
- remove WalletService process-memory-only address truth

Acceptance:
- wallet public addresses survive backend restart
- receive-context reads persisted addresses
- wallet overview/address count reads persisted addresses

### `liaojiang-5ne.15`
Expose persistent wallet address APIs and business-safe read models.

Scope:
- `/wallet/public-addresses`
- `/wallet/receive-context`
- READY / NO_ADDRESS / NO_WALLET state derivation from persisted data
- no user-facing dependency on process-memory address state

Acceptance:
- same account sees same receive address after restart
- receive-context returns stable chain/address state

### `liaojiang-5ne.13`
Android wallet address local cache strategy.

Scope:
- local cache for receive-context / wallet address display
- local-first display, server overwrite
- wallet overview and receive page read-through behavior

Acceptance:
- cold start can show last known receive address state from cache
- remote sync overwrites cache deterministically

### `liaojiang-5ne.14`
Checkout and payment-confirm QR must derive from persisted truth.

Scope:
- checkout QR payload source
- WalletPaymentConfirm QR payload source
- receive QR payload remains address-only
- payment QR uses true payment target / persisted truth only

Acceptance:
- receive QR encodes bare address only
- checkout/payment-confirm QR encodes real payment payload
- no placeholder or synthetic payload remains

### `liaojiang-5ne.17`
True-device regression for persisted receive and payment QR flows.

Required evidence:
- READY receive QR for persisted address
- receive chain switch linkage
- checkout `USDT · TRON` real QR
- WalletPaymentConfirm real QR

Acceptance:
- all evidence collected on real device
- `liaojiang-5ne.5` and `liaojiang-5ne.12` can close after proof review

---

## Execution Order

1. `liaojiang-5ne.16`
2. `liaojiang-5ne.15`
3. `liaojiang-5ne.13`
4. `liaojiang-5ne.14`
5. `liaojiang-5ne.17`

## Notes

- `Receive` QR semantics:
  - address only
  - no fixed amount
- `Checkout` / `WalletPaymentConfirm` QR semantics:
  - real payment payload only
- server remains truth source
- Android cache is display acceleration only
