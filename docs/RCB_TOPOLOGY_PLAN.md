# RCB Topology Plan

## Task
- `liaojiang-rcb.18`
- Date: 2026-04-08

## Current Topology
- `server1 (38.58.59.119)`:
  - serves `sol.residential-agent.com`
  - serves `usdt.residential-agent.com`
  - chain-side runtime is already split out and externally healthy
- `server2 (38.58.59.142)`:
  - physical state-service host for PostgreSQL and Redis
  - not intended to expose wallet HTTP services
- `server3 (154.37.208.72)`:
  - serves `api.residential-agent.com`
  - runs `cryptovpn-backend.service`
  - currently still exposes local tunnel listeners:
    - `127.0.0.1:15432`
    - `127.0.0.1:16379`

## Verified Evidence
- Local environment source already states that server3 currently reaches server2 through reverse SSH tunnel ports `15432/16379`.
- Live server3 inspection confirms:
  - `cryptovpn-backend.service` is active
  - backend listens on `*:3000`
  - local tunnel listeners still exist on `127.0.0.1:15432` and `127.0.0.1:16379`
- Therefore the remaining `rcb` gap is not chain-side service deployment or API gateway health; it is the backend-to-state-service topology.

## Gap To Close
- The three-machine split is only partially formalized.
- The chain-side split is real, but the backend still depends on a temporary tunnel path for state services.
- As long as the reverse tunnel is the only trusted path, server3 loses DB/Redis if that tunnel drops.

## Target Topology
- Keep responsibilities unchanged:
  - `server1`: chain-side SOL / USDT services
  - `server2`: PostgreSQL + Redis only
  - `server3`: API + admin only
- Replace the temporary reverse tunnel path with a stable, auditable server3 -> server2 path.
- Recommended target:
  - persistent private link between server2 and server3
  - server2 exposes `22/5432/6379` only on that private path
  - server3 backend points directly to server2 private address and standard ports

## Recommended Implementation Shape
- Preferred option: WireGuard or equivalent persistent private network between server2 and server3.
- Backend cutover target:
  - stop depending on `127.0.0.1:15432`
  - stop depending on `127.0.0.1:16379`
  - use server2 private address on `5432` / `6379`

## Cutover Sequence
1. Confirm server2 and server3 can be administered directly.
2. Establish stable private connectivity between server2 and server3.
3. On server2:
   - bind PostgreSQL and Redis to the private address
   - restrict access to server3 only
4. On server3:
   - update backend DB/Redis target away from tunnel ports
   - restart `cryptovpn-backend.service`
5. Verify:
   - backend local health
   - public API health
   - SOL agent health
   - USDT agent health
   - minimal payment-target / order smoke
6. Only after verification, retire the reverse SSH tunnel path.

## Rollback
- If direct topology fails, revert backend DB/Redis target to the local tunnel listeners and restore the tunnel.
- No domain, nginx, or chain-side rollback is required for that fallback.

## Remaining Risk
- If server2 still cannot be administered directly, implementation may be forced to stop at “temporary tunnel hardening” instead of full replacement.
- `liaojiang-rcb.19` should treat “no stable server2 access” or “no viable private path can be established” as the explicit blocker condition.
