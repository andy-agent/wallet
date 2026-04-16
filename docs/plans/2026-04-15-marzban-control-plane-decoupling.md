# Marzban Control-Plane Decoupling Implementation Plan

> **For Claude:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task.

**Goal:** Remove Marzban admin credentials from the public API runtime and make user-facing VPN/account flows depend only on business truth plus applied execution state.

**Architecture:** The Node business backend remains the sole product truth source for entitlements, selection, and user-visible VPN state. A control-plane-local controller running on the Marzban side owns Marzban admin credentials, applies desired state to Marzban, and writes back applied state/results. The Android app and public API read business DB truth only, never direct Marzban admin state.

**Tech Stack:** NestJS backend, PostgreSQL runtime state, Android Compose client, Marzban control plane, systemd deployment on `154.37.208.72`.

---

## Problem Statement

The current production chain is unsafe because ordinary user read paths can fail on missing Marzban admin configuration:

- `/api/client/v1/vpn/status` currently reaches into Marzban admin paths
- login warm sync can be broken by Marzban control-plane config errors
- public API runtime historically held `MARZBAN_ADMIN_USERNAME` / `MARZBAN_ADMIN_PASSWORD`
- user-facing flows could expose internal control-plane failures such as `MARZBAN_CONFIG_MISSING`

This couples:

1. product truth
2. control-plane execution
3. user read paths

These must be separated.

## Incident Recap And Verified Root Cause

This plan is grounded in the verified 2026-04-15 production incident rather than generic architecture preference.

What was verified:

- historical production backup `.env.local` pointed to:
  - `127.0.0.1:15432/cryptovpn`
- live `systemd` runtime had drifted to inject:
  - `127.0.0.1:25432/cryptovpn_test`
- Nest loaded `.env.local`, but live `process.env` from `systemd` remained authoritative at runtime
- the public API therefore read `cryptovpn_test` even though repo expectations still said `cryptovpn`
- `ensureBootstrapSystemAccount()` then created a fresh `system@cnyirui.cn` bootstrap account in the wrong database
- user-facing login temporarily returned the test-account context instead of the real paid account in `cryptovpn`

What was also verified:

- real user/order/subscription data was never migrated away
- the real `system@cnyirui.cn` paid account remained present in `cryptovpn`
- the wrong `6b3c...` account came from runtime drift plus bootstrap behavior, not from data loss

This plan therefore addresses two concrete production risks:

1. runtime-source drift
2. public-API dependence on Marzban admin credentials

## Target State

### Business backend responsibilities

The public API runtime owns:

- account auth
- plans / orders / entitlements
- selected region / line / node
- applied execution state cache
- user-visible VPN status
- business-safe error states

The public API runtime must **not**:

- hold Marzban admin credentials
- call Marzban admin APIs inside login or ordinary read paths
- expose Marzban internal config errors to users

### Marzban controller responsibilities

A controller local to the Marzban/control-plane side owns:

- Marzban admin credentials
- applying desired state to Marzban
- generating / reconciling subscription URLs
- writing execution results back to business storage

### Android responsibilities

The Android client reads:

- business truth
- applied execution state
- local caches

The Android client does **not** infer product truth from Marzban internals.

## Data Model

Introduce or normalize these runtime tables:

### `user_vpn_entitlement`

Purpose:
- what the user is allowed to use

Fields:
- `account_id`
- `plan_code`
- `status`
- `allowed_region_codes`
- `allowed_line_codes`
- `default_line_code`
- `expires_at`
- `updated_at`

### `user_vpn_selection`

Purpose:
- what the user currently selected

Fields:
- `account_id`
- `selected_region_code`
- `selected_line_code`
- `selected_node_id`
- `updated_at`

### `vpn_provision_job`

Purpose:
- desired-state work queue from business backend to controller

Fields:
- `job_id`
- `account_id`
- `desired_line_code`
- `desired_node_id`
- `reason` (`ORDER_ACTIVATED`, `USER_SWITCHED_NODE`, `RENEWAL`, `RECONCILE`)
- `status` (`PENDING`, `RUNNING`, `SUCCEEDED`, `FAILED`)
- `attempt_count`
- `last_error`
- `created_at`
- `updated_at`

### `user_vpn_applied_state`

Purpose:
- last successfully applied control-plane result

Fields:
- `account_id`
- `applied_line_code`
- `applied_node_id`
- `subscription_url`
- `marzban_username`
- `sync_status` (`PENDING`, `SYNCING`, `READY`, `FAILED`)
- `last_error`
- `last_synced_at`
- `updated_at`

### `vpn_node_catalog`

Purpose:
- business-mapped node catalog

Fields:
- `node_id`
- `region_code`
- `line_code`
- `display_name`
- `expected_country_code`
- `enabled`
- `priority`
- `weight`
- `updated_at`

## User-Facing API Contract

### `/api/client/v1/vpn/status`

Must read only:

- entitlement
- selection
- applied state
- local business node catalog

Must not directly call Marzban admin APIs.

Should return fields such as:

- `selectedRegionCode`
- `selectedRegionName`
- `selectedLineCode`
- `selectedLineName`
- `selectedNodeId`
- `selectedNodeName`
- `connectionState`
- `provisioningState`
- `subscriptionState`
- `stale`
- `businessMessage`

Example business-safe states:

- `NOT_SUBSCRIBED`
- `EXPIRED`
- `SYNCING`
- `READY`
- `FAILED`

### `/api/client/v1/subscriptions/current`

Must read:

- business subscription truth
- applied subscription URL if available

Must not fail just because Marzban admin config is missing.

### Login warm sync

Must require only:

- `me`
- `orders`
- `subscription`

VPN-specific reads are conditional on effective subscription state, and failures must be business-safe.

### No-subscription and expired-subscription policy

These rules are mandatory:

- users without an active subscription must still be able to log in
- users with expired subscriptions must still be able to log in
- app entry must not depend on `/vpn/regions` or any other subscription-gated VPN capability
- Android warm sync must treat:
  - `me`
  - `orders`
  - `subscription`
  as foundational
- Android warm sync must treat:
  - `vpn/status`
  - `vpn/regions`
  - `vpn/nodes`
  - subscription import/bootstrap
  as conditional on effective subscription state

Business-safe states should cover at least:

- `NOT_SUBSCRIBED`
- `EXPIRED`
- `SYNCING`
- `READY`
- `FAILED`

## Controller Design

Deploy a controller local to Marzban that:

1. reads `vpn_provision_job`
2. resolves desired state
3. authenticates to Marzban locally with admin credentials
4. applies user/inbound/host/subscription changes
5. writes `user_vpn_applied_state`
6. marks the job `SUCCEEDED` or `FAILED`

The controller can be:

- a systemd worker on the Marzban server
- or a small internal service colocated with Marzban

The public API must communicate by:

- DB-backed job queue
- or a narrow internal endpoint

Preferred first implementation:
- DB-backed job queue for simplicity and auditability

## File-Level Change Map

This is the intended write scope for the decoupling work.

### Phase 1: remove Marzban from public read hot paths

Backend files expected to change:

- `/Users/cnyirui/git/projects/liaojiang/code/backend/src/modules/vpn/vpn.service.ts`
- `/Users/cnyirui/git/projects/liaojiang/code/backend/src/modules/vpn/vpn.controller.ts`
- `/Users/cnyirui/git/projects/liaojiang/code/backend/src/modules/vpn/vpn.types.ts`
- `/Users/cnyirui/git/projects/liaojiang/code/backend/src/modules/auth/auth.service.ts`
- `/Users/cnyirui/git/projects/liaojiang/code/backend/src/modules/database/postgres-runtime-state.repository.ts`
- `/Users/cnyirui/git/projects/liaojiang/code/backend/src/modules/marzban/marzban.service.ts`
- `/Users/cnyirui/git/projects/liaojiang/code/backend/src/main.ts`

Android files expected to change:

- `/Users/cnyirui/git/projects/liaojiang/code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/payment/data/repository/PaymentRepository.kt`
- `/Users/cnyirui/git/projects/liaojiang/code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/composeui/p0/repository/RealP0Repository.kt`

### Phase 2: desired/applied state schema

Primary backend files expected to change:

- `/Users/cnyirui/git/projects/liaojiang/code/backend/src/modules/database/postgres-runtime-state.repository.ts`
- `/Users/cnyirui/git/projects/liaojiang/code/backend/src/modules/vpn/vpn.service.ts`
- `/Users/cnyirui/git/projects/liaojiang/code/backend/src/modules/vpn/vpn.types.ts`

### Phase 3: controller runtime

Controller-side files should be isolated from the public API runtime, for example under:

- `/Users/cnyirui/git/projects/liaojiang/code/controller/`
- or another dedicated control-plane runtime directory on the Marzban side

### Phase 4: runtime cutover

Deployment/runtime files expected to change:

- `/Users/cnyirui/git/projects/liaojiang/code/deploy/BACKEND_DEPLOYMENT.md`
- `/opt/cryptovpn/backend/.env.local` on the live host
- `/etc/systemd/system/cryptovpn-backend.service`
- removal of redundant drop-ins such as `/etc/systemd/system/cryptovpn-backend.service.d/env.conf`

## Error Model

Never show users raw internal errors like:

- `MARZBAN_CONFIG_MISSING`
- `MARZBAN_AUTH_FAILED`

Map to business-safe messages:

- `当前未开通订阅`
- `订阅已过期`
- `线路同步中`
- `线路同步失败，请稍后重试`

Internal errors stay in logs and `last_error`.

## Rollout Phases

### Phase 1: Stop-the-bleeding

Acceptance:
- login and overview no longer depend on Marzban admin config

Steps:
1. Refactor `/vpn/status` to read business state only
2. Refactor `/subscriptions/current` to read business state only
3. Remove direct Marzban admin calls from login hot path
4. Return business-safe provisioning states
5. Verify:
   - active subscription account
   - no-subscription account
   - expired subscription account (if dataset available)

Operational checklist:
- confirm public API can start with `MARZBAN_ADMIN_PASSWORD` absent
- confirm `/api/client/v1/auth/login/password` still succeeds
- confirm `/api/client/v1/vpn/status` returns business-safe state instead of control-plane error
- confirm Android no-subscription login enters app instead of showing login failure

### Phase 2: Desired/applied state schema

Acceptance:
- business DB can fully represent desired and applied VPN state

Steps:
1. Add `user_vpn_selection`
2. Add `vpn_provision_job`
3. Add `user_vpn_applied_state`
4. Backfill from current `runtime_state_subscriptions`
5. Expose read models to API services

### Phase 3: Marzban-side controller

Acceptance:
- controller can consume jobs and write applied state

Steps:
1. Create controller runtime on Marzban side
2. Add local env for Marzban admin credentials there only
3. Implement job polling / execution
4. Implement retry / backoff / failure recording
5. Verify one node switch and one activation flow

Operational checklist:
- controller host owns the only live Marzban admin credential copy
- public API host no longer needs those values
- failed controller jobs do not break user login or overview

### Phase 4: Production cutover

Acceptance:
- public API no longer needs Marzban admin env

Steps:
1. Remove `MARZBAN_ADMIN_USERNAME` and `MARZBAN_ADMIN_PASSWORD` from public API host
2. Confirm public API still serves:
   - login
   - overview
   - subscriptions
   - vpn status
3. Confirm controller host still applies provisioning jobs
4. Confirm Android real-device flows remain good

## Rollback Plan

If Phase 1 breaks user reads:
- revert API read-path changes only
- keep schema additions if already migrated

If Phase 3 controller fails:
- leave public API on business read-only mode
- mark provisioning as `FAILED`/`SYNCING`
- do not reintroduce admin credentials to public API as emergency fix

If runtime config drift is detected again:
- stop the service
- inspect the live process environment, not just repo files
- verify:
  - host
  - port
  - dbname
  - `RUNTIME_STATE_BACKEND`
- restore exactly one authoritative runtime source before restart

## Acceptance Criteria

### Security
- public API host does not hold Marzban admin credentials
- user-facing responses never expose raw Marzban internal errors

### Functional
- no-subscription users can log in and enter app
- expired-subscription users can log in and enter app
- active users can still read subscription and VPN state
- selection changes enqueue jobs and eventually produce applied state

### Operational
- startup logs print redacted DB/runtime fingerprint
- one authoritative runtime config source is used in production
- obsolete `cryptovpn_test` runtime path is gone

### Retro / prevention
- the postmortem explains why `6b3c...` appeared while `9e6f...` remained valid
- deployment docs explicitly document that `.env.local` and live `process.env` can diverge under `systemd`
- deployment docs explicitly ban dual live DB sources for the public API

## Beads Mapping

- `liaojiang-5f67`
  - parent architecture plan / execution umbrella
- `liaojiang-5f67.1`
  - refactor user-facing VPN read paths to business truth only
- `liaojiang-5f67.2`
  - build Marzban-side controller / projection pipeline
- `liaojiang-5f67.3`
  - production cutover and credential removal from public API

## Immediate Next Task

Start with `liaojiang-5f67.1`.

Do not continue patching the dangerous hot path by adding more Marzban admin env back into the public API runtime.
