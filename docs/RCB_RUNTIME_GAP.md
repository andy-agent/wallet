# RCB Runtime Gap

## Task
- `liaojiang-rcb.22`
- Date: 2026-04-08

## What Was Verified

### Server3 live runtime
- `cryptovpn-backend.service` runs:
  - `/usr/bin/node /opt/cryptovpn/backend/dist/main.js`
- service is active and serves `*:3000`
- server3 still has:
  - local postgres listener on `127.0.0.1:5432`
  - local redis listener on `127.0.0.1:6379`
  - tunnel listeners on `127.0.0.1:15432` and `127.0.0.1:16379`
- `/etc/systemd/system/cryptovpn-db-tunnel.service` still exists on server3

### Server2 live runtime
- server2 is directly reachable with the documented root password login
- server2 exposes:
  - `0.0.0.0:5432`
  - `0.0.0.0:6379`
- server2 still runs `cryptovpn-reverse-db-tunnel.service`
- `fail2ban` currently bans `154.37.208.72`, which explains why server3 cannot SSH to server2
- from server3:
  - `38.58.59.142:22` is rejected
  - `38.58.59.142:5432` times out
  - `38.58.59.142:6379` times out

## Codebase Findings

### `code/backend`
- current Nest backend is the deployed public API edge
- local source shows `DatabaseModule` is empty
- many auth/order/wallet/vpn flows are implemented in-memory
- deployed `/opt/cryptovpn/backend/dist` shows no meaningful DB/Redis implementation path in runtime code

### `code/server`
- Python stack is stateful
- has `DATABASE_URL`
- has `REDIS_URL`
- includes:
  - order scanner workers
  - fulfillment workers
  - scheduler
  - persistent models / DB-backed flow

## Runtime Divergence
- The current public runtime on server3 is **Node/Nest in-memory oriented**
- The persistent payment/provisioning implementation in the repository lives in **Python `code/server`**
- The old tunnel story and the current server2 data placement therefore describe an intended target topology, not the actual runtime truth of the public API

## Main Conclusion
- `rcb` is currently blocked not by “one last tunnel swap”, but by a **runtime split-brain**:
  - public traffic hits Node `code/backend`
  - persistent payment/provisioning logic exists in Python `code/server`
  - server2/server3 topology cannot be honestly finalized until one of these becomes the single real stateful mainline

## Recommended Direction For `rcb.23`
- Recommendation: **keep the current public Node backend as the API entrypoint, and make it truly stateful**
- Rationale:
  - Android and current real-environment validation already point at the Node public API
  - wholesale cutover of `api.residential-agent.com` to the Python stack would introduce much larger contract risk
  - the safer path is to align the current public backend with persistent Postgres/Redis-backed order/payment/provisioning behavior
- Use `code/server` as:
  - state-machine reference
  - worker/reference implementation
  - migration source for scanner / fulfillment / scheduler logic

## What `rcb.23` Must Decide
1. Which order/payment/provisioning state is authoritative after alignment.
2. Whether Node directly owns persistence, or Node delegates specific flows to Python services while staying the public edge.
3. Which currently deployed local services on server3 are real dependencies vs stale leftovers.

## What `rcb.24` Must Not Do Prematurely
- Do not retire `15432/16379` just because they look temporary.
- Do not finalize server2/server3 topology before the public runtime has a confirmed stateful main chain.
- Otherwise the topology change becomes cosmetic and does not satisfy “功能都必须实现”.
