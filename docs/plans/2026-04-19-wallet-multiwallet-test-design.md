# Wallet Multi-Wallet Test-Environment Refactor Design

Date: 2026-04-19
Status: Draft
Authors: Codex
Reviewers: TBD
Target: Test environment only

---

## 1. Background

The current wallet domain still behaves as a single-wallet-per-account system even though the product direction now requires:

- multiple wallets per account
- watch-only wallets
- explicit wallet selection for VPN payment
- a multi-chain wallet object instead of a chain-specific or mnemonic-centric UI model

The existing implementation was sufficient for a single self-custody wallet, but it breaks down once wallet becomes a first-class product object.

This design is intentionally scoped for the test environment only. Existing data can be discarded, old interfaces do not need compatibility shims, and destructive evolution is acceptable.

---

## 2. Current State Assessment

### 2.1 Identity and business ownership

The system still treats `accountId + session` as the business identity root:

- auth accounts and sessions are the primary identity model
- orders are owned by `accountId`
- subscriptions are owned by `accountId`
- referral and commission logic are owned by `accountId`

This is visible in:

- `/code/backend/src/modules/orders/orders.types.ts`
- `/code/backend/src/modules/vpn/vpn.types.ts`
- `/code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/payment/data/local/entity/UserEntity.kt`

This is acceptable for the refactor. Wallet does not need to replace the account model in this phase.

### 2.2 Wallet backend model is single-wallet

The current wallet lifecycle model is single-row per account:

- `PersistedWalletLifecycleRecord` has one `accountId -> walletId`
- `RuntimeStateRepository` exposes `findWalletLifecycleByAccountId()` and `upsertWalletLifecycle()`
- PostgreSQL persists `wallet_lifecycles.account_id` as the primary key and uses `ON CONFLICT(account_id)` update semantics

Relevant files:

- `/code/backend/src/modules/wallet/wallet.types.ts`
- `/code/backend/src/modules/database/runtime-state.repository.ts`
- `/code/backend/src/modules/database/postgres-runtime-state.repository.ts`

This means the current model cannot represent:

- multiple self-custody wallets under one account
- a wallet list
- a watch-only wallet object
- wallet-level default selection

### 2.3 Wallet addresses are modeled as account-scoped address rows

Current `PersistedWalletPublicAddressRecord` is still closer to a payment-address cache than to a wallet graph:

- scoped by `accountId`
- optionally linked to a `walletId`
- includes `assetCode`
- only supports `SOLANA` and `TRON`

This is useful for receive/payment flows but is not sufficient as the primary model for a multi-wallet product.

### 2.4 Android local secret model is single-wallet-per-account

Android currently persists wallet secret material by `accountId`, not by wallet object:

- `WalletSecretStore.upsertMnemonic(accountId, walletId, ...)`
- the storage key is the account id
- a conflicting mnemonic from another account is treated as a single-device conflict

Relevant files:

- `/code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/payment/wallet/WalletSecretStore.kt`
- `/code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/payment/wallet/WalletKeyManager.kt`

This prevents the device from holding a natural wallet list for the same account.

### 2.5 Wallet creation/import is client-led today

The current system already generates and imports secrets on the client:

- create wallet: Android generates a mnemonic locally using `WalletMnemonicGenerator`
- import wallet: Android accepts user mnemonic locally
- backend only receives lifecycle metadata, address sync, and encrypted backup payload

Relevant files:

- `/code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/payment/wallet/WalletMnemonicGenerator.kt`
- `/code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/composeui/common/repository/RealCryptoVpnRepository.kt`
- `/code/backend/src/modules/wallet/dto/upsert-wallet-lifecycle.request.ts`

This is important. The refactor should not move mnemonic generation to the backend.

### 2.6 Payment flow already supports local signing and Solana transfer orchestration

The existing codebase already contains a partial local-wallet payment path:

- order creation
- payment target fetch
- wallet transfer precheck/build
- local signer usage
- proxy broadcast
- tx hash submission to the order
- order status refresh until provisioning

Relevant files:

- `/code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/payment/data/repository/PaymentRepository.kt`
- `/code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/composeui/common/repository/RealCryptoVpnRepository.kt`
- `/code/backend/src/modules/orders/orders.controller.ts`
- `/code/backend/src/modules/orders/orders.service.ts`
- `/code/backend/src/modules/wallet/wallet.service.ts`

This means the first payment target of the redesign should be wallet selection and wallet binding, not chain-side payment primitives from scratch.

---

## 3. Scope and Assumptions

### 3.1 Test-environment assumptions

- existing wallet-related test data can be dropped
- old wallet tables and old wallet APIs do not need compatibility behavior
- destructive schema changes are acceptable
- old Android local wallet caches can be cleared
- only the latest test client needs to work

### 3.2 In scope

1. Multiple wallets per account
2. Wallet kinds:
   - `SELF_CUSTODY`
   - `WATCH_ONLY`
3. Multi-chain unified wallet objects
4. First-chain-family support:
   - `EVM`
   - `SOLANA`
   - `TRON`
5. Explicit wallet selection for VPN payment
6. Device-level secret storage keyed by wallet objects instead of account id

### 3.3 Out of scope

- BTC
- wallet-based login
- backward compatibility for old wallet APIs
- historical data migration
- swap / bridge / DApp browser redesign
- fully generic multi-device wallet sync semantics
- appending imported private keys into an existing mnemonic wallet in phase 1

---

## 4. Design Goals

1. Make wallet a first-class product object.
2. Support a wallet list under a single account.
3. Represent watch-only wallets explicitly rather than as fallback public addresses.
4. Allow the user to pick a specific wallet for payment.
5. Support a multi-chain unified wallet object while preserving chain-specific signer differences.
6. Keep account/session/order/subscription as the main business backbone.

---

## 5. Recommended Architecture

### 5.1 Service-side model

Use three service-side wallet domain objects:

- `Wallet`
- `WalletKeySlot`
- `WalletChainAccount`

### 5.2 Android local model

Use two Android local logical models:

- `LocalWalletRecord`
- `LocalWalletSecretRecord`

Important: `LocalWalletSecretRecord` is a logical model, not necessarily a Room row. Secret material should continue to live behind `WalletSecretStore` and Android Keystore encryption. Room should only store wallet list metadata and caches.

### 5.3 Why this split is correct

`Wallet` is the product object the user sees.

`WalletKeySlot` is the signer source abstraction:

- one EVM signer can back multiple EVM networks
- Solana signer is distinct
- TRON signer is distinct

`WalletChainAccount` is the network/account view:

- address
- network
- capability
- enabled state

This is the minimum model that can cleanly express:

- multi-wallet
- unified multi-chain wallet
- watch-only wallets
- explicit wallet payment routing

---

## 6. Target Data Model

### 6.1 Service-side `Wallet`

Fields:

- `walletId`
- `accountId`
- `walletName`
- `walletKind`
  - `SELF_CUSTODY`
  - `WATCH_ONLY`
- `sourceType`
  - `CREATED`
  - `IMPORTED_MNEMONIC`
  - `IMPORTED_PRIVATE_KEY`
  - `WATCH_IMPORTED`
- `isDefault`
- `isArchived`
- `createdAt`
- `updatedAt`

Rules:

- only one default wallet per account
- archived wallets cannot be used for payment

### 6.2 Service-side `WalletKeySlot`

Fields:

- `keySlotId`
- `walletId`
- `slotCode`
  - `EVM_0`
  - `SOLANA_0`
  - `TRON_0`
- `chainFamily`
  - `EVM`
  - `SOLANA`
  - `TRON`
- `derivationType`
  - `MNEMONIC`
  - `PRIVATE_KEY`
- `derivationPath`
- `createdAt`
- `updatedAt`

Rules:

- only `SELF_CUSTODY` wallets can have key slots
- `slotCode` must be unique inside a wallet

### 6.3 Service-side `WalletChainAccount`

Fields:

- `chainAccountId`
- `walletId`
- `keySlotId` nullable
- `chainFamily`
  - `EVM`
  - `SOLANA`
  - `TRON`
- `networkCode`
  - `ETHEREUM`
  - `BSC`
  - `POLYGON`
  - `ARBITRUM`
  - `BASE`
  - `OPTIMISM`
  - `AVALANCHE_C`
  - `SOLANA`
  - `TRON`
- `address`
- `capability`
  - `SIGN_AND_PAY`
  - `WATCH_ONLY`
- `isEnabled`
- `isDefaultReceive`
- `createdAt`
- `updatedAt`

Rules:

- for `SIGN_AND_PAY`, `keySlotId` must be present
- for `WATCH_ONLY`, `keySlotId` must be absent
- same wallet cannot duplicate the same `networkCode + address`

### 6.4 Android `LocalWalletRecord`

Fields:

- `walletId`
- `accountId`
- `walletName`
- `walletKind`
- `isDefault`
- `isArchived`
- `updatedAt`

This belongs in Room.

### 6.5 Android `LocalWalletSecretRecord`

Logical fields:

- `walletId`
- `keySlotId`
- `secretKind`
  - `MNEMONIC`
  - `PRIVATE_KEY`
- `chainFamily`
- `encryptedBlob`
- `createdAt`
- `updatedAt`

This should be stored by `WalletSecretStore` using Android Keystore encryption, not exposed as plain Room data.

---

## 7. Chain Support Strategy

### 7.1 First release chain scope

EVM family:

- `ETHEREUM`
- `BSC`
- `POLYGON`
- `ARBITRUM`
- `BASE`
- `OPTIMISM`
- `AVALANCHE_C`

Other families:

- `SOLANA`
- `TRON`

### 7.2 Unified mnemonic wallet behavior

When the user creates or imports a mnemonic wallet, the system should create:

Key slots:

- `EVM_0`
- `SOLANA_0`
- `TRON_0`

Chain accounts:

- all EVM networks backed by `EVM_0`
- `SOLANA` backed by `SOLANA_0`
- `TRON` backed by `TRON_0`

### 7.3 Watch-only wallet behavior

Phase 1 watch-only rule:

- each imported watch-only wallet is single-network
- it creates one `Wallet`
- zero `WalletKeySlot`
- one `WalletChainAccount` with `WATCH_ONLY`

This keeps the first iteration simple while still unlocking the feature.

### 7.4 Private-key wallet behavior

Phase 1 private-key rule:

- importing a private key creates a new wallet
- the wallet is single-chain self-custody
- no mixing of imported private-key slots into an existing mnemonic wallet in this phase

---

## 8. API Redesign

The existing `/wallet/lifecycle` and `/wallet/public-addresses` API family should not remain the primary wallet management API in the new model.

### 8.1 New APIs

Query:

- `GET /client/v1/wallets`
- `GET /client/v1/wallets/:walletId`
- `GET /client/v1/wallets/:walletId/chain-accounts`

Mutations:

- `POST /client/v1/wallets/create-mnemonic`
- `POST /client/v1/wallets/import/mnemonic`
- `POST /client/v1/wallets/import/private-key`
- `POST /client/v1/wallets/import/watch-only`
- `PATCH /client/v1/wallets/:walletId`
- `POST /client/v1/wallets/:walletId/set-default`

### 8.2 Secret material responsibility

The server must not become the source of newly generated mnemonic material.

Recommended behavior:

- client generates mnemonic locally
- client submits wallet creation metadata to the server
- client stores secret material locally
- client optionally uploads encrypted backup through a dedicated backup API

This matches the current system much better than a backend-generated mnemonic model.

---

## 9. Android Local Storage Redesign

### 9.1 Keep secret material in the existing encrypted store pattern

The current `WalletSecretStore` pattern is acceptable:

- encrypted secret payload
- Android Keystore-managed encryption key
- secret material not exposed in Room

Recommended change:

- move storage keying from `accountId` to `walletId:keySlotId`
- support multiple wallet secret records for one account

### 9.2 Room responsibilities

Room should store:

- wallet list metadata
- selected/default wallet
- chain-account cache if needed for display

Room should not store plaintext private material.

### 9.3 Device-signable state

Device-signable state is local-only:

- if a local secret exists for the selected wallet and chain family, it is signable on this device
- otherwise it is view-only on this device

This does not change server-side wallet kind.

---

## 10. Payment Flow Redesign

### 10.1 Preserve current order ownership

Orders and subscriptions should remain account-owned:

- order belongs to `accountId`
- subscription belongs to `accountId`

The wallet refactor should only change payment binding, not ownership of the business object.

### 10.2 New order payment binding fields

Add to order model:

- `payerWalletId`
- `payerChainAccountId`
- `submittedFromAddress`

Purpose:

- audit
- UI display
- multiple-wallet reconciliation

### 10.3 First payment target

Phase 1 payment target:

- Solana VPN payment using an explicitly selected wallet

This is appropriate because the current codebase already contains:

- order creation
- order payment target fetch
- Solana precheck/build/proxy-broadcast
- local signer usage
- submitted tx hash handling
- order confirmation and provisioning

### 10.4 Required flow

1. User selects plan
2. User selects payment asset/network
3. User selects payer wallet
4. App validates that the wallet has a signable chain account for the selected network
5. App creates order
6. App fetches payment target
7. App builds transfer
8. App signs transfer with the selected wallet key slot
9. App broadcasts transfer
10. App submits tx hash with payer wallet binding
11. App refreshes order status until `COMPLETED`
12. VPN provisioning continues through the existing path

### 10.5 Required payment validations

Server must validate:

1. `payerWalletId` belongs to the current account
2. `payerChainAccountId` belongs to `payerWalletId`
3. selected chain account network matches order payment network
4. `submittedFromAddress` equals the chain account address
5. archived wallets cannot pay
6. watch-only wallets cannot pay

---

## 11. UI and UX Changes

### 11.1 Required pages in phase 1

- wallet list page
- create mnemonic wallet page
- import watch-only wallet page
- wallet detail page
- wallet selection in payment flow

Private-key import can stay optional in phase 1.

### 11.2 Required UI distinctions

The UI must clearly distinguish:

- watch-only wallet
- self-custody wallet with a local signer on this device
- self-custody wallet that exists on the account but has no local signer on this device

Suggested copy:

- watch-only: "View only, cannot pay"
- self-custody without local key: "Key not available on this device, view only until imported"

### 11.3 Wallet selection requirement

Payment screens must show:

- wallet name
- network
- address prefix/suffix
- asset
- payable amount

Wallet choice cannot be implicit in phase 1.

---

## 12. Test-Environment Reset Strategy

Because this is test-environment-only and destructive evolution is allowed:

### 12.1 Service-side reset

The implementation can:

- drop old wallet lifecycle/public-address/secret-backup structures
- create new wallet tables from scratch
- delete existing wallet test data

### 12.2 Android-side reset

The implementation can:

- clear old `WalletSecretStore` entries
- clear old wallet-related Room caches
- invalidate old default-wallet assumptions

### 12.3 Consequence

No compatibility behavior is required for:

- old wallet APIs
- old local wallet cache formats
- old wallet UI flows

---

## 13. Implementation Phases

### Phase 1: New wallet domain model and base APIs

- introduce new wallet tables and service-side types
- replace single-wallet lifecycle logic with multi-wallet services
- expose wallet list/detail APIs

### Phase 2: Android local wallet list and local secret routing

- add local wallet metadata storage
- re-key local secret storage by wallet and key slot
- implement wallet-aware signer resolution

### Phase 3: Wallet management UI

- wallet list page
- wallet detail page
- default wallet switching
- create mnemonic wallet
- import watch-only wallet

### Phase 4: Solana payment wallet selection

- bind `payerWalletId`
- bind `payerChainAccountId`
- wallet-aware payment selection
- wallet-aware local signing
- order detail/payment confirmation wallet display

---

## 14. Validation Strategy

### 14.1 Unit tests

- unique default wallet selection
- mnemonic wallet auto-generation of EVM/Solana/TRON key slots
- watch-only wallet invariants
- wallet device-signable state resolution

### 14.2 Backend integration tests

- create multiple wallets under one account
- import a watch-only wallet
- create mnemonic wallet and verify generated chain accounts
- create order and bind payer wallet fields
- Solana payment succeeds only for the selected signable wallet
- watch-only wallet payment submission is rejected

### 14.3 Android integration tests

- wallet list rendering
- default wallet switching behavior
- wallet detail chain-account display
- payment wallet picker disables watch-only wallets
- selected wallet signer is actually used for Solana payment

### 14.4 Device regression

1. create wallet A
2. create wallet B
3. switch default wallet
4. import one watch-only wallet
5. pay one Solana order with wallet A
6. pay another Solana order with wallet B
7. remove local secret for wallet B and verify it becomes device-read-only

---

## 15. Key Risks

- signer abstraction drift between EVM / Solana / TRON
- user confusion between watch-only and self-custody-without-local-secret
- accidental fallback to address-only payment routing if wallet binding is not enforced everywhere
- wallet and payment UI drift if wallet selection is not made explicit

---

## 16. Final Recommendation

This refactor is appropriate for the current system and should proceed in the test environment.

The approved implementation shape should be:

- destructive evolution
- no compatibility layer
- no historical migration
- client-generated secret material
- service-side multi-wallet model
- Android local wallet metadata plus encrypted wallet secret storage
- phase-1 Solana payment wallet selection

This gives the project a clean wallet foundation while avoiding unnecessary scope expansion into login, BTC, or generalized chain integrations.
