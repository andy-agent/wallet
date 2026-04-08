# RCB State Service Cutover

## Task
- `liaojiang-rcb.24.1`
- Date: 2026-04-08

## Current Situation
- `rcb.23` made the public Node backend persistence-capable for the first time, but only through a file-backed `RuntimeStateRepository`.
- `server2` is reachable by credentialed admin login and already runs the intended shared state services:
  - PostgreSQL on `5432`
  - Redis on `6379`
- `server3` still carries stale tunnel artifacts:
  - `cryptovpn-db-tunnel.service`
  - listeners on `127.0.0.1:15432` and `127.0.0.1:16379`
- `server2` still carries the reverse side:
  - `cryptovpn-reverse-db-tunnel.service`
- `server3` currently cannot reach `server2` directly because:
  - `fail2ban` on server2 bans `154.37.208.72` for SSH
  - `ufw` on server2 does not currently allow `5432/6379` from server3

## Cutover Decision

### Primary state service
- The formal state service for the public runtime should be:
  - **PostgreSQL on server2**

### Secondary state service
- Redis on server2 is not the first critical blocker for the public order/payment/provisioning main chain.
- It should be opened and prepared at the same time as Postgres, but the public runtime cutover can be judged primarily on Postgres-backed state first.

### File-backed repository status
- The file-backed `RuntimeStateRepository` is accepted only as a transition adapter for dev/test and short-lived stabilization.
- It is **not** the target production state service.

## Why This Is The Right Route
- `server2` already hosts the intended shared state services.
- `code/backend` now has a persistence boundary that can be swapped under the public API edge.
- `code/backend` does not yet need Redis to prove the critical public order/payment/provisioning state is no longer in-memory.
- Moving first to server2-backed Postgres gives a single durable source of truth without requiring a full public-runtime swap.

## Required Implementation Direction For `rcb.24`
1. Make `RuntimeStateRepository` pluggable:
   - production path -> server2-backed Postgres repository
   - fallback path -> current file adapter for local/dev only
2. Open a direct server3 -> server2 path:
   - unban `154.37.208.72` on server2 fail2ban
   - explicitly allow `154.37.208.72` to reach `22/tcp`, `5432/tcp`, `6379/tcp`
3. Point the public backend to server2 state services.
4. Verify public order/payment/subscription flow against the new direct path.
5. Remove:
   - `cryptovpn-db-tunnel.service` on server3
   - `cryptovpn-reverse-db-tunnel.service` on server2

## Scope Boundary
- Do not switch the public API domain to the Python runtime in this phase.
- Do not keep the file-backed adapter as the production mainline after cutover.
- Do not require Redis-backed business logic before validating the Postgres-backed critical path.

## Rollback
- If server2 direct access or Postgres-backed runtime fails:
  - switch the backend back to file-backed runtime state
  - re-enable the tunnel services
  - keep the public API domain unchanged

## Acceptance Link To `rcb.20`
- `rcb.20` should only pass after:
  - the public runtime uses the direct path, not `15432/16379`
  - the critical public order/payment/subscription flow remains healthy
  - the live order -> payment-target -> submit-client-tx -> refresh-status -> subscription/VPN checks still pass
