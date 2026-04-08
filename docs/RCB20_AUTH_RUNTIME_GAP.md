# RCB20 Auth Runtime Gap

## Task
- `liaojiang-rcb.20.3.1`
- Date: 2026-04-08

## Reproduced Failure
- Real API `POST /api/client/v1/auth/login/password`
- Account: `system@cnyirui.cn`
- Result: `404 ACCOUNT_NOT_FOUND`
- This is not a password mismatch. The public backend cannot find the account at all.

## Public Node Runtime Facts
- `code/backend/src/modules/auth/auth.service.ts` still stores:
  - accounts
  - accountsByEmail
  - sessionsByAccessToken
  - sessionsByRefreshToken
  - sessionsByAccountId
  - verification codes
  entirely in in-memory `Map`s.
- Public login path resolves account only through `accountsByEmail`.
- Therefore any pre-existing account disappears after restart unless the same process created it.

## Server2 Database Facts
- Current `cryptovpn` Postgres schema contains only:
  - `runtime_state_orders`
  - `runtime_state_subscriptions`
- There is no persisted auth table such as:
  - `users`
  - `client_sessions`
- So the public Node backend currently has no durable source for account lookup.

## Python Stack Facts
- `code/server` still contains a real persistent auth model and API shape:
  - `app/models/user.py`
  - `app/models/client_session.py`
  - `app/api/client/auth.py`
- But this is not the runtime currently serving `api.residential-agent.com`.

## Root Cause
- `system@cnyirui.cn` cannot log in because the public Node backend still runs auth as in-memory-only state.
- That runtime has no persisted account store, and the current `cryptovpn` database also does not yet contain auth/session tables for it to read.
- The account is not “temporarily inaccessible”; it is missing from the active public auth source of truth.

## Required Direction For `rcb.20.3.2`
1. Add a real persistent auth store for the public Node backend.
2. Persist at minimum:
   - account lookup by email
   - password hash
   - account status
   - session / token continuity required by the current public API
3. Restore `system@cnyirui.cn` into that persistent auth store.
4. Keep the existing public login endpoint shape stable.

## Minimum Acceptance For The Fix
- `system@cnyirui.cn` can log in successfully on the real API.
- Login survives process restart.
- Account lookup no longer depends on in-memory-only maps.
