# RCB API Runtime Diff

## Task
- `liaojiang-rcb.23.1`
- Date: 2026-04-08

## Scope
- Compare the current public Node API in `code/backend`
- Against the persistent Python flow in `code/server`
- Focus only on:
  - order creation
  - payment target
  - client tx submission
  - status refresh / state progression
  - subscription current
  - provisioning / fulfillment

## Public Node API (`code/backend`)

### Public endpoints present
- `POST /client/v1/orders`
- `GET /client/v1/orders/:orderNo`
- `GET /client/v1/orders/:orderNo/payment-target`
- `POST /client/v1/orders/:orderNo/submit-client-tx`
- `POST /client/v1/orders/:orderNo/refresh-status`

### Current state source
- `OrdersService` stores state in:
  - `ordersByNo = new Map<string, OrderRecord>()`
  - `idempotencyIndex = new Map<string, string>()`
- order lifecycle is advanced in memory inside `OrdersService`
- `submitClientTx()` mutates memory and marks `PAYMENT_DETECTED`
- `refreshStatus()` drives the state machine in memory and may call `ProvisioningService`

### In-memory assumptions
- no real persistence layer
- `DatabaseModule` is empty
- no DB-backed order repository
- no Redis-backed state or worker queue
- `getPaymentTarget()` returns fixed sample addresses and uses runtime flags, not persistent allocated addresses
- fallback progression logic in `refreshStatus()` is synthetic and memory-driven

## Persistent Python Flow (`code/server`)

### Persistent building blocks
- requires `DATABASE_URL`
- uses `REDIS_URL`
- DB-backed models include orders, plans, payment addresses, users, sessions
- subscription reads fulfilled orders and Marzban-backed user state

### Order / payment path
- `app/api/client/orders.py` creates real DB-backed orders
- allocates addresses from `AddressPoolService`
- locks FX rate and persists order fields
- uses DB session as primary state source

### State machine / workers
- `app/workers/scanner.py`
  - scans pending orders
  - detects chain payments
  - advances state transitions
  - confirms seen transactions
  - expires orders
  - releases addresses
- `app/workers/fulfillment.py`
  - fulfills paid orders
  - creates/renews Marzban users
  - writes fulfilled results back to DB
- `app/workers/scheduler.py`
  - registers scanner / confirm / fulfill / expire / release / sweep jobs

### Subscription / provisioning path
- `app/api/client/subscription.py` reads fulfilled orders from DB
- uses persisted `marzban_username`
- fetches real subscription state from Marzban

## Gap Summary

### Orders
- Node: in-memory `Map`
- Python: DB-backed `Order` model + payment address allocation + status machine

### Payment target
- Node: static address / amount response shape
- Python: allocated address + locked amount + persisted expiry

### Submit tx / refresh status
- Node: direct in-memory mutation + synthetic fallback progression
- Python: worker-driven detection / confirmation / payment-success transitions

### Provisioning
- Node: inline `ProvisioningService` call from memory state progression
- Python: persistent paid queue + fulfillment worker + Marzban result writeback

### Subscription current
- Node public stack has no equivalent persistent subscription source
- Python reads the latest fulfilled order and real Marzban user data

## Main Architectural Conflict
- Public traffic currently hits the Node backend
- The repository’s stateful payment/provisioning implementation lives in the Python stack
- Therefore the system has a split between:
  - public API edge
  - persistent payment/provisioning truth

## Recommendation For `rcb.23.2`
- Keep the Node backend as the public API edge
- Make its key order/payment/provisioning path genuinely stateful
- Use the Python stack as the reference implementation and migration source
- Do not switch `api.residential-agent.com` wholesale to the Python runtime in one step

## Recommended Write Scope For `rcb.23.2`
1. Introduce a real persistence boundary in `code/backend` for orders and related state.
2. Replace `OrdersService` in-memory maps with persistent repository/service code.
3. Replace static `payment-target` generation with persisted payment target / address / amount data.
4. Replace synthetic `refreshStatus()` progression with persisted status transitions.
5. Make provisioning / subscription read from persistent order state rather than transient memory.

## Migration Order
1. Persistent order storage + idempotency
2. Persistent payment target / submitted tx / refresh status
3. Persistent fulfillment result and subscription read path

## `rcb.23.2` Acceptance Implication
- `rcb.23.2` should not try to port every Python capability at once
- it must first eliminate the in-memory main chain for the public order/payment/provisioning flow
