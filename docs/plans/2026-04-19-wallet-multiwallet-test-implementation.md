# Wallet Multi-Wallet Test-Environment Refactor Implementation Plan

> **For Claude:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task.

**Goal:** Replace the current single-wallet wallet domain with a destructive-evolution multi-wallet model for the test environment, including watch-only wallets and Solana wallet selection for VPN payment.

**Architecture:** Preserve account/session/order/subscription as the business backbone, replace the wallet domain with `Wallet + WalletKeySlot + WalletChainAccount`, and move Android local secret routing from `accountId` scope to `walletId + keySlotId`. Keep mnemonic/private-key generation or import on the client, and treat server wallet APIs as metadata/state APIs rather than secret-generation APIs.

**Tech Stack:** NestJS backend, PostgreSQL runtime state repository, Android Kotlin + Room + MMKV + Android Keystore, Compose UI, existing Solana payment orchestration.

---

**Explicit constraints for this plan:**

- keep server-side encrypted backup for self-custody wallets
- backup scope is per wallet, not per account
- the server may encrypt and store ciphertext, but must not store the AGE decryption identity
- the AGE decryption identity is held only on an offline recovery machine
- no user-facing wallet recovery flow is implemented in this phase
- losing mnemonic/private-key material remains unrecoverable from the app in this phase

---

### Task 1: Define the new backend wallet domain types

**Files:**
- Modify: `/Users/cnyirui/git/projects/liaojiang/code/backend/src/modules/wallet/wallet.types.ts`
- Modify: `/Users/cnyirui/git/projects/liaojiang/code/backend/src/modules/database/runtime-state.types.ts`
- Test: `/Users/cnyirui/git/projects/liaojiang/code/backend/src/modules/database/postgres-runtime-state.repository.spec.ts`

**Step 1: Write the failing tests**

Add repository or type-level tests asserting:

- multiple wallets can exist under one account
- only one default wallet is allowed
- watch-only wallets cannot have key slots
- signable chain accounts require a key slot

**Step 2: Run test to verify it fails**

Run:

```bash
cd /Users/cnyirui/git/projects/liaojiang/code/backend
pnpm test -- postgres-runtime-state.repository.spec.ts
```

Expected: failure because the current repository and schema only support one wallet lifecycle row per account.

**Step 3: Write minimal implementation**

Replace the lifecycle-centric wallet types with:

- `PersistedWalletRecord`
- `PersistedWalletKeySlotRecord`
- `PersistedWalletChainAccountRecord`
- supporting enums for wallet kind, chain family, capability, derivation type

Keep order and subscription types untouched.

**Step 4: Run test to verify it passes**

Run:

```bash
cd /Users/cnyirui/git/projects/liaojiang/code/backend
pnpm test -- postgres-runtime-state.repository.spec.ts
```

Expected: type/schema-focused assertions pass.

**Step 5: Commit**

```bash
git add /Users/cnyirui/git/projects/liaojiang/code/backend/src/modules/wallet/wallet.types.ts /Users/cnyirui/git/projects/liaojiang/code/backend/src/modules/database/runtime-state.types.ts /Users/cnyirui/git/projects/liaojiang/code/backend/src/modules/database/postgres-runtime-state.repository.spec.ts
git commit -m "refactor: define multi-wallet backend domain types"
```

### Task 2: Replace runtime-state wallet persistence with multi-wallet tables

**Files:**
- Modify: `/Users/cnyirui/git/projects/liaojiang/code/backend/src/modules/database/runtime-state.repository.ts`
- Modify: `/Users/cnyirui/git/projects/liaojiang/code/backend/src/modules/database/postgres-runtime-state.repository.ts`
- Modify: `/Users/cnyirui/git/projects/liaojiang/code/backend/src/modules/database/file-runtime-state.repository.ts`
- Test: `/Users/cnyirui/git/projects/liaojiang/code/backend/src/modules/database/postgres-runtime-state.repository.spec.ts`

**Step 1: Write the failing tests**

Add tests for:

- create two wallets for the same account
- set one default wallet and reject a second default
- list chain accounts for a wallet
- create a watch-only wallet with zero key slots

**Step 2: Run test to verify it fails**

Run:

```bash
cd /Users/cnyirui/git/projects/liaojiang/code/backend
pnpm test -- postgres-runtime-state.repository.spec.ts
```

Expected: failure because `wallet_lifecycles` is still keyed by `account_id`.

**Step 3: Write minimal implementation**

Replace the lifecycle table family with new tables:

- `wallets`
- `wallet_key_slots`
- `wallet_chain_accounts`
- optionally rename or replace wallet secret backup persistence to be wallet-scoped

Update repository interfaces to expose:

- `listWalletsByAccountId`
- `findWalletById`
- `insertWallet`
- `updateWallet`
- `setDefaultWallet`
- `listWalletKeySlots`
- `listWalletChainAccounts`

Because this is test-only destructive evolution, remove legacy single-wallet repository entry points instead of preserving compatibility.

**Step 4: Run test to verify it passes**

Run:

```bash
cd /Users/cnyirui/git/projects/liaojiang/code/backend
pnpm test -- postgres-runtime-state.repository.spec.ts
```

Expected: repository tests pass against the new schema.

**Step 5: Commit**

```bash
git add /Users/cnyirui/git/projects/liaojiang/code/backend/src/modules/database/runtime-state.repository.ts /Users/cnyirui/git/projects/liaojiang/code/backend/src/modules/database/postgres-runtime-state.repository.ts /Users/cnyirui/git/projects/liaojiang/code/backend/src/modules/database/file-runtime-state.repository.ts /Users/cnyirui/git/projects/liaojiang/code/backend/src/modules/database/postgres-runtime-state.repository.spec.ts
git commit -m "refactor: replace single-wallet persistence with multi-wallet tables"
```

### Task 3: Rebuild backend wallet service and APIs around wallets instead of lifecycle

**Files:**
- Modify: `/Users/cnyirui/git/projects/liaojiang/code/backend/src/modules/wallet/wallet.service.ts`
- Modify: `/Users/cnyirui/git/projects/liaojiang/code/backend/src/modules/wallet/wallet.controller.ts`
- Modify: `/Users/cnyirui/git/projects/liaojiang/code/backend/src/modules/wallet/wallet.module.ts`
- Create: `/Users/cnyirui/git/projects/liaojiang/code/backend/src/modules/wallet/dto/create-mnemonic-wallet.request.ts`
- Create: `/Users/cnyirui/git/projects/liaojiang/code/backend/src/modules/wallet/dto/import-watch-wallet.request.ts`
- Create: `/Users/cnyirui/git/projects/liaojiang/code/backend/src/modules/wallet/dto/import-private-key-wallet.request.ts`
- Test: `/Users/cnyirui/git/projects/liaojiang/code/backend/test/wallet-postgres.e2e-spec.ts`

**Step 1: Write the failing tests**

Add end-to-end tests covering:

- list wallets
- create mnemonic wallet metadata
- import watch-only wallet
- set default wallet
- get wallet chain accounts
- self-custody backup records are wallet-scoped
- watch-only wallets do not create backup records

Do not include private-key import in the first test batch if it slows down the first closed loop.

**Step 2: Run test to verify it fails**

Run:

```bash
cd /Users/cnyirui/git/projects/liaojiang/code/backend
pnpm test -- wallet-postgres.e2e-spec.ts
```

Expected: failure because the current controller still exposes `/wallet/lifecycle` and single-wallet semantics.

**Step 3: Write minimal implementation**

Implement new wallet endpoints:

- `GET /client/v1/wallets`
- `GET /client/v1/wallets/:walletId`
- `GET /client/v1/wallets/:walletId/chain-accounts`
- `POST /client/v1/wallets/create-mnemonic`
- `POST /client/v1/wallets/import/watch-only`
- `PATCH /client/v1/wallets/:walletId`
- `POST /client/v1/wallets/:walletId/set-default`

Important:

- do not generate mnemonic material on the backend
- accept client-submitted chain-account metadata for create/import flows
- keep secret backup APIs wallet-scoped
- keep encrypted server backup for self-custody wallets
- do not implement any user-facing wallet recovery flow

**Step 4: Run test to verify it passes**

Run:

```bash
cd /Users/cnyirui/git/projects/liaojiang/code/backend
pnpm test -- wallet-postgres.e2e-spec.ts
```

Expected: wallet e2e flows pass.

**Step 5: Commit**

```bash
git add /Users/cnyirui/git/projects/liaojiang/code/backend/src/modules/wallet/wallet.service.ts /Users/cnyirui/git/projects/liaojiang/code/backend/src/modules/wallet/wallet.controller.ts /Users/cnyirui/git/projects/liaojiang/code/backend/src/modules/wallet/wallet.module.ts /Users/cnyirui/git/projects/liaojiang/code/backend/src/modules/wallet/dto/create-mnemonic-wallet.request.ts /Users/cnyirui/git/projects/liaojiang/code/backend/src/modules/wallet/dto/import-watch-wallet.request.ts /Users/cnyirui/git/projects/liaojiang/code/backend/src/modules/wallet/dto/import-private-key-wallet.request.ts /Users/cnyirui/git/projects/liaojiang/code/backend/test/wallet-postgres.e2e-spec.ts
git commit -m "feat: add multi-wallet backend APIs"
```

### Task 4: Bind VPN orders to explicit payer wallets

**Files:**
- Modify: `/Users/cnyirui/git/projects/liaojiang/code/backend/src/modules/orders/orders.types.ts`
- Modify: `/Users/cnyirui/git/projects/liaojiang/code/backend/src/modules/database/runtime-state.types.ts`
- Modify: `/Users/cnyirui/git/projects/liaojiang/code/backend/src/modules/orders/dto/create-order.request.ts`
- Modify: `/Users/cnyirui/git/projects/liaojiang/code/backend/src/modules/orders/dto/submit-client-tx.request.ts`
- Modify: `/Users/cnyirui/git/projects/liaojiang/code/backend/src/modules/orders/orders.service.ts`
- Test: `/Users/cnyirui/git/projects/liaojiang/code/backend/test/wallet.e2e-spec.ts`

**Step 1: Write the failing tests**

Add tests asserting:

- order creation can accept selected payer wallet metadata
- submit-client-tx rejects mismatched payer wallet or payer chain account
- watch-only wallets cannot be used for payment submission

**Step 2: Run test to verify it fails**

Run:

```bash
cd /Users/cnyirui/git/projects/liaojiang/code/backend
pnpm test -- wallet.e2e-spec.ts
```

Expected: failure because orders currently only rely on `accountId` plus `txHash`.

**Step 3: Write minimal implementation**

Add order fields:

- `payerWalletId`
- `payerChainAccountId`
- `submittedFromAddress`

Enforce validation during payment submission:

- wallet belongs to account
- chain account belongs to wallet
- chain account network matches order network
- `submittedFromAddress` matches chain-account address
- watch-only wallets are rejected

**Step 4: Run test to verify it passes**

Run:

```bash
cd /Users/cnyirui/git/projects/liaojiang/code/backend
pnpm test -- wallet.e2e-spec.ts
```

Expected: payer-wallet binding tests pass.

**Step 5: Commit**

```bash
git add /Users/cnyirui/git/projects/liaojiang/code/backend/src/modules/orders/orders.types.ts /Users/cnyirui/git/projects/liaojiang/code/backend/src/modules/database/runtime-state.types.ts /Users/cnyirui/git/projects/liaojiang/code/backend/src/modules/orders/dto/create-order.request.ts /Users/cnyirui/git/projects/liaojiang/code/backend/src/modules/orders/dto/submit-client-tx.request.ts /Users/cnyirui/git/projects/liaojiang/code/backend/src/modules/orders/orders.service.ts /Users/cnyirui/git/projects/liaojiang/code/backend/test/wallet.e2e-spec.ts
git commit -m "feat: bind orders to explicit payer wallets"
```

### Task 5: Rebuild Android wallet metadata storage and secret routing

**Files:**
- Modify: `/Users/cnyirui/git/projects/liaojiang/code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/payment/data/local/database/PaymentDatabase.kt`
- Create: `/Users/cnyirui/git/projects/liaojiang/code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/payment/data/local/entity/LocalWalletEntity.kt`
- Create: `/Users/cnyirui/git/projects/liaojiang/code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/payment/data/local/entity/LocalWalletChainAccountEntity.kt`
- Create: `/Users/cnyirui/git/projects/liaojiang/code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/payment/data/local/dao/LocalWalletDao.kt`
- Create: `/Users/cnyirui/git/projects/liaojiang/code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/payment/data/local/dao/LocalWalletChainAccountDao.kt`
- Modify: `/Users/cnyirui/git/projects/liaojiang/code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/payment/wallet/WalletSecretStore.kt`
- Modify: `/Users/cnyirui/git/projects/liaojiang/code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/payment/wallet/WalletKeyManager.kt`
- Test: `/Users/cnyirui/git/projects/liaojiang/code/Android/V2rayNG/app/src/test/java/com/v2ray/ang/payment/data/repository/PaymentRepositoryTest.kt`

**Step 1: Write the failing tests**

Add tests covering:

- multiple wallets can be cached locally for one account
- wallet secrets are keyed by `walletId + keySlotId`, not `accountId`
- device-signable state resolves from local secret presence
- local recovery UI is absent and not reintroduced in this phase

**Step 2: Run test to verify it fails**

Run:

```bash
cd /Users/cnyirui/git/projects/liaojiang/code/Android/V2rayNG
./gradlew :app:testFdroidDebugUnitTest --tests "com.v2ray.ang.payment.data.repository.PaymentRepositoryTest"
```

Expected: failure because current local storage still assumes a single mnemonic per account.

**Step 3: Write minimal implementation**

- add local wallet metadata entities and DAOs
- keep encrypted secret material behind `WalletSecretStore`
- change secret storage keying to `walletId:keySlotId`
- update `WalletKeyManager` to resolve signer by wallet context instead of account-only context

**Step 4: Run test to verify it passes**

Run:

```bash
cd /Users/cnyirui/git/projects/liaojiang/code/Android/V2rayNG
./gradlew :app:testFdroidDebugUnitTest --tests "com.v2ray.ang.payment.data.repository.PaymentRepositoryTest"
```

Expected: local wallet storage tests pass.

**Step 5: Commit**

```bash
git add /Users/cnyirui/git/projects/liaojiang/code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/payment/data/local/database/PaymentDatabase.kt /Users/cnyirui/git/projects/liaojiang/code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/payment/data/local/entity/LocalWalletEntity.kt /Users/cnyirui/git/projects/liaojiang/code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/payment/data/local/entity/LocalWalletChainAccountEntity.kt /Users/cnyirui/git/projects/liaojiang/code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/payment/data/local/dao/LocalWalletDao.kt /Users/cnyirui/git/projects/liaojiang/code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/payment/data/local/dao/LocalWalletChainAccountDao.kt /Users/cnyirui/git/projects/liaojiang/code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/payment/wallet/WalletSecretStore.kt /Users/cnyirui/git/projects/liaojiang/code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/payment/wallet/WalletKeyManager.kt /Users/cnyirui/git/projects/liaojiang/code/Android/V2rayNG/app/src/test/java/com/v2ray/ang/payment/data/repository/PaymentRepositoryTest.kt
git commit -m "refactor: make Android wallet storage wallet-scoped"
```

### Task 6: Replace single-wallet repository flows in Android with wallet list flows

**Files:**
- Modify: `/Users/cnyirui/git/projects/liaojiang/code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/payment/data/repository/PaymentRepository.kt`
- Modify: `/Users/cnyirui/git/projects/liaojiang/code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/composeui/common/repository/CryptoVpnRepository.kt`
- Modify: `/Users/cnyirui/git/projects/liaojiang/code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/composeui/common/repository/RealCryptoVpnRepository.kt`
- Test: `/Users/cnyirui/git/projects/liaojiang/code/Android/V2rayNG/app/src/test/java/com/v2ray/ang/composeui/common/repository/RealCryptoVpnRepositorySourceTest.kt`

**Step 1: Write the failing tests**

Add tests covering:

- wallet list retrieval
- default wallet selection
- watch-only wallet detection
- self-custody-without-local-secret device-read-only state

**Step 2: Run test to verify it fails**

Run:

```bash
cd /Users/cnyirui/git/projects/liaojiang/code/Android/V2rayNG
./gradlew :app:testFdroidDebugUnitTest --tests "com.v2ray.ang.composeui.common.repository.RealCryptoVpnRepositorySourceTest"
```

Expected: failure because repository APIs still assume a single wallet lifecycle.

**Step 3: Write minimal implementation**

- add payment repository methods for list wallets, get wallet details, set default wallet
- update `CryptoVpnRepository` interface to be wallet-object-centric
- rewrite `RealCryptoVpnRepository` wallet management paths to consume the new API

**Step 4: Run test to verify it passes**

Run:

```bash
cd /Users/cnyirui/git/projects/liaojiang/code/Android/V2rayNG
./gradlew :app:testFdroidDebugUnitTest --tests "com.v2ray.ang.composeui.common.repository.RealCryptoVpnRepositorySourceTest"
```

Expected: repository source tests pass with the new wallet model.

**Step 5: Commit**

```bash
git add /Users/cnyirui/git/projects/liaojiang/code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/payment/data/repository/PaymentRepository.kt /Users/cnyirui/git/projects/liaojiang/code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/composeui/common/repository/CryptoVpnRepository.kt /Users/cnyirui/git/projects/liaojiang/code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/composeui/common/repository/RealCryptoVpnRepository.kt /Users/cnyirui/git/projects/liaojiang/code/Android/V2rayNG/app/src/test/java/com/v2ray/ang/composeui/common/repository/RealCryptoVpnRepositorySourceTest.kt
git commit -m "refactor: make Android wallet repository multi-wallet aware"
```

### Task 7: Build wallet list, detail, create, and watch-only UI flows

**Files:**
- Modify: `/Users/cnyirui/git/projects/liaojiang/code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/composeui/p2extended/model/WalletManagerContract.kt`
- Modify: `/Users/cnyirui/git/projects/liaojiang/code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/composeui/p2extended/viewmodel/WalletManagerViewModel.kt`
- Modify: `/Users/cnyirui/git/projects/liaojiang/code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/composeui/pages/p2extended/WalletManagerPage.kt`
- Modify: `/Users/cnyirui/git/projects/liaojiang/code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/composeui/pages/p2extended/CreateWalletPage.kt`
- Modify: `/Users/cnyirui/git/projects/liaojiang/code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/composeui/pages/p2extended/ImportWalletMethodPage.kt`
- Modify: `/Users/cnyirui/git/projects/liaojiang/code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/composeui/navigation/P2ExtendedNavGraph.kt`
- Modify: `/Users/cnyirui/git/projects/liaojiang/code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/composeui/navigation/CryptoVpnRouteSpec.kt`

**Step 1: Write the failing tests**

Add focused navigation or contract tests for:

- wallet list shows more than one wallet
- setting default wallet changes the active wallet
- watch-only wallet entry is flagged as view-only

Use existing Compose contract-style tests as reference.

**Step 2: Run test to verify it fails**

Run:

```bash
cd /Users/cnyirui/git/projects/liaojiang/code/Android/V2rayNG
./gradlew :app:testFdroidDebugUnitTest --tests "com.v2ray.ang.composeui.p0.model.WalletOnboardingNavigationTest"
```

Expected: failure or missing assertions because the UI still assumes one wallet.

**Step 3: Write minimal implementation**

- turn wallet manager into a real wallet list page
- add flows for create mnemonic wallet and import watch-only wallet
- support default wallet switch
- update nav graph to route into the new wallet object flows

**Step 4: Run test to verify it passes**

Run:

```bash
cd /Users/cnyirui/git/projects/liaojiang/code/Android/V2rayNG
./gradlew :app:testFdroidDebugUnitTest
```

Expected: unit tests pass for the touched Compose contract/repository areas.

**Step 5: Commit**

```bash
git add /Users/cnyirui/git/projects/liaojiang/code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/composeui/p2extended/model/WalletManagerContract.kt /Users/cnyirui/git/projects/liaojiang/code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/composeui/p2extended/viewmodel/WalletManagerViewModel.kt /Users/cnyirui/git/projects/liaojiang/code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/composeui/pages/p2extended/WalletManagerPage.kt /Users/cnyirui/git/projects/liaojiang/code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/composeui/pages/p2extended/CreateWalletPage.kt /Users/cnyirui/git/projects/liaojiang/code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/composeui/pages/p2extended/ImportWalletMethodPage.kt /Users/cnyirui/git/projects/liaojiang/code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/composeui/navigation/P2ExtendedNavGraph.kt /Users/cnyirui/git/projects/liaojiang/code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/composeui/navigation/CryptoVpnRouteSpec.kt
git commit -m "feat: add multi-wallet management UI"
```

### Task 8: Bind Solana VPN payment to selected wallet

**Files:**
- Modify: `/Users/cnyirui/git/projects/liaojiang/code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/payment/data/repository/PaymentRepository.kt`
- Modify: `/Users/cnyirui/git/projects/liaojiang/code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/composeui/common/repository/RealCryptoVpnRepository.kt`
- Modify: `/Users/cnyirui/git/projects/liaojiang/code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/composeui/pages/p1/OrderCheckoutPage.kt`
- Modify: `/Users/cnyirui/git/projects/liaojiang/code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/composeui/pages/p1/WalletPaymentConfirmPage.kt`
- Modify: `/Users/cnyirui/git/projects/liaojiang/code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/composeui/p1/model/OrderCheckoutContract.kt`
- Modify: `/Users/cnyirui/git/projects/liaojiang/code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/composeui/p1/model/WalletPaymentConfirmContract.kt`
- Test: `/Users/cnyirui/git/projects/liaojiang/code/backend/test/wallet-postgres.e2e-spec.ts`
- Test: `/Users/cnyirui/git/projects/liaojiang/code/Android/V2rayNG/app/src/test/java/com/v2ray/ang/payment/data/repository/PaymentRepositorySourceTest.kt`

**Step 1: Write the failing tests**

Add tests for:

- order checkout includes wallet selection state
- selected payer wallet is carried through create order and submit tx
- watch-only wallets are disabled in payment selection
- order detail/payment confirm shows payer wallet information

**Step 2: Run test to verify it fails**

Run:

```bash
cd /Users/cnyirui/git/projects/liaojiang/code/backend
pnpm test -- wallet-postgres.e2e-spec.ts
cd /Users/cnyirui/git/projects/liaojiang/code/Android/V2rayNG
./gradlew :app:testFdroidDebugUnitTest --tests "com.v2ray.ang.payment.data.repository.PaymentRepositorySourceTest"
```

Expected: failure because payment flows do not yet bind selected wallets.

**Step 3: Write minimal implementation**

- expose wallet list/eligible chain accounts to checkout UI
- require an explicit payer wallet for Solana wallet payment
- pass `payerWalletId`, `payerChainAccountId`, and `submittedFromAddress`
- use the selected wallet's signer instead of any implicit active signer

**Step 4: Run test to verify it passes**

Run:

```bash
cd /Users/cnyirui/git/projects/liaojiang/code/backend
pnpm test -- wallet-postgres.e2e-spec.ts
cd /Users/cnyirui/git/projects/liaojiang/code/Android/V2rayNG
./gradlew :app:testFdroidDebugUnitTest
```

Expected: Solana wallet payment tests pass end-to-end in the test environment.

**Step 5: Commit**

```bash
git add /Users/cnyirui/git/projects/liaojiang/code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/payment/data/repository/PaymentRepository.kt /Users/cnyirui/git/projects/liaojiang/code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/composeui/common/repository/RealCryptoVpnRepository.kt /Users/cnyirui/git/projects/liaojiang/code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/composeui/pages/p1/OrderCheckoutPage.kt /Users/cnyirui/git/projects/liaojiang/code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/composeui/pages/p1/WalletPaymentConfirmPage.kt /Users/cnyirui/git/projects/liaojiang/code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/composeui/p1/model/OrderCheckoutContract.kt /Users/cnyirui/git/projects/liaojiang/code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/composeui/p1/model/WalletPaymentConfirmContract.kt /Users/cnyirui/git/projects/liaojiang/code/backend/test/wallet-postgres.e2e-spec.ts /Users/cnyirui/git/projects/liaojiang/code/Android/V2rayNG/app/src/test/java/com/v2ray/ang/payment/data/repository/PaymentRepositorySourceTest.kt
git commit -m "feat: bind Solana VPN payment to selected wallet"
```

Plan complete and saved to `docs/plans/2026-04-19-wallet-multiwallet-test-implementation.md`. Two execution options:

1. Subagent-Driven (this session) - I dispatch fresh subagent per task, review between tasks, fast iteration

2. Parallel Session (separate) - Open new session with executing-plans, batch execution with checkpoints

Which approach?
