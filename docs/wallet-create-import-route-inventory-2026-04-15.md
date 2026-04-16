# Wallet Create/Import Route Inventory (2026-04-15)

Scope: Android wallet onboarding/create/import flow only.

Parent issue: `liaojiang-5ne`
Inventory issue: `liaojiang-5ne.10`

## Route-by-route inventory

### `wallet_onboarding`
- Route spec:
  - `code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/composeui/navigation/CryptoVpnRouteSpec.kt`
- Nav registration:
  - `code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/composeui/navigation/P0NavGraph.kt`
- Page / VM / state:
  - `code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/composeui/pages/p0/WalletOnboardingPage.kt`
  - `code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/composeui/p0/viewmodel/WalletOnboardingViewModel.kt`
  - `code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/composeui/p0/model/P0Models.kt`
  - `code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/composeui/p0/repository/RealP0Repository.kt`
- Current runtime path:
  - Uses real account/subscription context from `RealP0Repository.getWalletOnboardingState()`
  - Page body is still mostly hardcoded marketing/onboarding copy
  - Continue action currently routes directly to `wallet_home`
- Status:
  - `real repository but blocked state`
- Blockers:
  - No wallet existence / lifecycle endpoint
  - No branching from selected mode into `create_wallet` or `import_wallet_method`
  - `WalletOnboardingEvent.ContinueClicked` is not used to drive route selection

### `wallet_home`
- Route spec / nav:
  - `CryptoVpnRouteSpec.walletHome`
  - `P0NavGraph.kt`
- Page / VM / repo:
  - `pages/p0/WalletHomePage.kt`
  - `p0/viewmodel/WalletHomeViewModel.kt`
  - `p0/repository/RealP0Repository.kt`
- Current runtime path:
  - Uses real backend `wallet/overview`
  - Receive button navigates directly to `receive/{assetId}/{chainId}`
- Status:
  - `already real data` for overview
  - `blocked` for receive gating
- Blockers:
  - No wallet existence split before entering receive flow

### `receive/{assetId}/{chainId}`
- Route spec / nav:
  - `CryptoVpnRouteSpec.receive`
  - `P2CoreNavGraph.kt`
- Page / VM / repo:
  - `pages/p2/ReceivePage.kt`
  - `p2/viewmodel/ReceiveViewModel.kt`
  - `composeui/common/repository/RealCryptoVpnRepository.kt`
- Current runtime path:
  - Uses real backend `wallet/receive-context`
  - If context missing, falls back to local placeholder state
- Status:
  - `already real data` when receive context exists
  - `real repository but blocked state` for wallet existence split
- Blockers:
  - No server-truth wallet lifecycle state to distinguish:
    - no wallet
    - wallet exists but selected chain has no address

### `create_wallet/{mode}`
- Route spec:
  - `CryptoVpnRouteSpec.createWallet`
- Page / VM / contract:
  - `pages/p2extended/CreateWalletPage.kt`
  - `p2extended/viewmodel/CreateWalletViewModel.kt`
  - `p2extended/model/CreateWalletContract.kt`
  - `composeui/common/repository/RealCryptoVpnRepository.kt#getCreateWalletState`
- Nav registration:
  - Missing. Route exists in `CryptoVpnRouteSpec` but is not installed in `P2ExtendedNavGraph.kt`
- Current runtime path:
  - Not reachable from live nav graph
  - Contract/page are feature-template placeholders
  - Real repository returns a blocked placeholder state
- Status:
  - `real repository but blocked state`
- Blockers:
  - Route not registered
  - No backend create-wallet API
  - No real submit action in `CreateWalletViewModel`

### `import_wallet_method`
- Route spec / nav:
  - `CryptoVpnRouteSpec.importWalletMethod`
  - `P2ExtendedNavGraph.kt`
- Page / VM / repo:
  - `pages/p2extended/ImportWalletMethodPage.kt`
  - `p2extended/viewmodel/ImportWalletMethodViewModel.kt`
  - `p2extended/model/ImportWalletMethodContract.kt`
  - `composeui/common/repository/RealCryptoVpnRepository.kt#getImportWalletMethodState`
- Current runtime path:
  - Reachable
  - Uses real account/local context in repository
  - Page UI still hardcodes import options and process steps
  - Primary action always routes to `import_mnemonic/onboarding`
- Status:
  - `real repository but blocked state`
- Blockers:
  - Options are UI hardcoded, not backend-driven
  - No real import-method availability matrix from backend

### `import_mnemonic/{source}`
- Route spec / nav:
  - `CryptoVpnRouteSpec.importMnemonic`
  - `P2ExtendedNavGraph.kt`
- Page / VM / repo:
  - `pages/p2extended/ImportMnemonicPage.kt`
  - `p2extended/viewmodel/ImportMnemonicViewModel.kt`
  - `p2extended/model/ImportMnemonicContract.kt`
  - `composeui/common/repository/RealCryptoVpnRepository.kt#getImportMnemonicState`
- Current runtime path:
  - Reachable
  - Page UI hardcodes 12 mnemonic words and chain chips
  - Real repository marks state as blocked
  - Primary action routes to `security_center`
- Status:
  - `real repository but blocked state`
- Blockers:
  - No backend import-mnemonic API
  - No submit/validation action in viewmodel
  - Route transition after import is placeholder

### `backup_mnemonic/{walletId}`
- Route spec / nav:
  - `CryptoVpnRouteSpec.backupMnemonic`
  - `P2ExtendedNavGraph.kt`
- Page / VM / repo:
  - `pages/p2extended/BackupMnemonicPage.kt`
  - `p2extended/viewmodel/BackupMnemonicViewModel.kt`
  - `p2extended/model/BackupMnemonicContract.kt`
  - `composeui/common/repository/RealCryptoVpnRepository.kt#getBackupMnemonicState`
- Current runtime path:
  - Reachable from nav graph, but current live create-wallet route is not reachable
  - Page UI hardcodes mnemonic words and rules
  - Repository returns blocked placeholder state
  - Primary action routes to `confirm_mnemonic/primary_wallet`
- Status:
  - `real repository but blocked state`
- Blockers:
  - No server-side stored/generated mnemonic
  - No real walletId source feeding the route

### `confirm_mnemonic/{walletId}`
- Route spec / nav:
  - `CryptoVpnRouteSpec.confirmMnemonic`
  - `P2ExtendedNavGraph.kt`
- Page / VM / repo:
  - `pages/p2extended/ConfirmMnemonicPage.kt`
  - `p2extended/viewmodel/ConfirmMnemonicViewModel.kt`
  - `p2extended/model/ConfirmMnemonicContract.kt`
  - `composeui/common/repository/RealCryptoVpnRepository.kt#getConfirmMnemonicState`
- Current runtime path:
  - Reachable from nav graph, but not reached by a real create-wallet path today
  - Repository returns blocked placeholder state
  - Primary action routes to `security_center`
- Status:
  - `real repository but blocked state`
- Blockers:
  - No backend mnemonic confirmation endpoint
  - No real challenge/word positions

### `import_private_key/{chainId}`
- Route spec:
  - `CryptoVpnRouteSpec.importPrivateKey`
- Page / VM / repo:
  - `pages/p2extended/ImportPrivateKeyPage.kt`
  - `p2extended/viewmodel/ImportPrivateKeyViewModel.kt`
  - `p2extended/model/ImportPrivateKeyContract.kt`
  - `composeui/common/repository/RealCryptoVpnRepository.kt#getImportPrivateKeyState`
- Nav registration:
  - Missing from `P2ExtendedNavGraph.kt`
- Current runtime path:
  - Not reachable in live nav graph
  - Repository returns blocked placeholder
- Status:
  - `real repository but blocked state`
- Blockers:
  - Route not registered
  - No backend private-key import API

### `security_center`
- Route spec / nav:
  - `CryptoVpnRouteSpec.securityCenter`
  - `P2ExtendedNavGraph.kt`
- Repo:
  - `RealCryptoVpnRepository.kt#getSecurityCenterState`
- Current runtime path:
  - Reachable
  - Uses real account/session + local state
- Status:
  - `already real data` for account/session context
  - downstream wallet actions blocked because wallet create/import not real yet

### `chain_manager/{walletId}`
- Route spec / nav:
  - `CryptoVpnRouteSpec.chainManager`
  - `P2ExtendedNavGraph.kt`
- Repo:
  - `RealCryptoVpnRepository.kt#getChainManagerState`
- Current runtime path:
  - Reachable
  - Repository returns blocked placeholder
- Status:
  - `real repository but blocked state`
- Blockers:
  - No real wallet chain config API

### `wallet_manager/{walletId}`
- Route spec:
  - `CryptoVpnRouteSpec.walletManager`
- Page / VM / repo:
  - `pages/p2extended/WalletManagerPage.kt`
  - `p2extended/viewmodel/WalletManagerViewModel.kt`
  - `p2extended/model/WalletManagerContract.kt`
  - `RealCryptoVpnRepository.kt#getWalletManagerState`
- Nav registration:
  - Missing from `P2ExtendedNavGraph.kt`
- Current runtime path:
  - Not reachable
  - Repository returns blocked placeholder
- Status:
  - `real repository but blocked state`
- Blockers:
  - Route not registered
  - No backend wallet manager/list endpoint

## Route transitions

- `email_login` -> `wallet_onboarding`
  - from `P0NavGraph.kt`
- `wallet_onboarding` -> currently `wallet_home`
  - should become:
    - create mode -> `create_wallet/{mode}`
    - import mode -> `import_wallet_method`
- `wallet_home` -> `receive/{assetId}/{chainId}`
  - should become:
    - no wallet -> `wallet_onboarding`
    - wallet exists -> `receive/...`
- `import_wallet_method` -> `import_mnemonic/{source}`
- `import_mnemonic` -> currently `security_center`
  - should become:
    - successful import -> `wallet_home` or `wallet_manager/{walletId}`
- `create_wallet` -> should go to `backup_mnemonic/{walletId}`
  - route currently missing from nav graph
- `backup_mnemonic` -> `confirm_mnemonic/{walletId}`
- `confirm_mnemonic` -> currently `security_center`
  - should become:
    - successful confirmation -> `wallet_home` or `wallet_manager/{walletId}`

## Mock-source summary

- Pure page hardcoded UI content still present in:
  - `WalletOnboardingPage.kt`
  - `ImportWalletMethodPage.kt`
  - `ImportMnemonicPage.kt`
  - `BackupMnemonicPage.kt`
- Contract default/preview states still define placeholder copy and template metrics in:
  - `CreateWalletContract.kt`
  - `ImportWalletMethodContract.kt`
  - `ImportMnemonicContract.kt`
  - `BackupMnemonicContract.kt`
  - `ConfirmMnemonicContract.kt`
  - `WalletManagerContract.kt`
  - `ChainManagerContract.kt`
  - `ImportPrivateKeyContract.kt`
- Real repository methods already exist, but many return explicit blocked placeholders:
  - `getCreateWalletState`
  - `getImportMnemonicState`
  - `getBackupMnemonicState`
  - `getConfirmMnemonicState`
  - `getChainManagerState`
  - `getWalletManagerState`
  - `getImportPrivateKeyState`

## Files to change next

### Android navigation / flow split
- `code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/composeui/navigation/P0NavGraph.kt`
- `code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/composeui/navigation/P2CoreNavGraph.kt`
- `code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/composeui/navigation/P2ExtendedNavGraph.kt`

### Android repositories / viewmodels
- `code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/composeui/p0/repository/RealP0Repository.kt`
- `code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/composeui/common/repository/CryptoVpnRepository.kt`
- `code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/composeui/common/repository/RealCryptoVpnRepository.kt`
- `code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/composeui/p0/viewmodel/WalletOnboardingViewModel.kt`
- `code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/composeui/p2extended/viewmodel/CreateWalletViewModel.kt`
- `code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/composeui/p2extended/viewmodel/ImportWalletMethodViewModel.kt`
- `code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/composeui/p2extended/viewmodel/ImportMnemonicViewModel.kt`
- `code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/composeui/p2extended/viewmodel/BackupMnemonicViewModel.kt`
- `code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/composeui/p2extended/viewmodel/ConfirmMnemonicViewModel.kt`

### Android UI pages still needing real state binding
- `code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/composeui/pages/p0/WalletOnboardingPage.kt`
- `code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/composeui/pages/p2extended/CreateWalletPage.kt`
- `code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/composeui/pages/p2extended/ImportWalletMethodPage.kt`
- `code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/composeui/pages/p2extended/ImportMnemonicPage.kt`
- `code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/composeui/pages/p2extended/BackupMnemonicPage.kt`
- `code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/composeui/pages/p2extended/ConfirmMnemonicPage.kt`
- `code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/composeui/pages/p2extended/WalletManagerPage.kt`

### Backend server-truth work required before Android can be real
- `code/backend/src/modules/wallet/wallet.controller.ts`
- `code/backend/src/modules/wallet/wallet.service.ts`
- `code/backend/src/modules/wallet/wallet.module.ts`
- `code/backend/src/modules/database/runtime-state.repository.ts`
- `code/backend/src/modules/database/runtime-state.types.ts`
- `code/backend/src/modules/database/postgres-runtime-state.repository.ts`
- `code/backend/src/modules/database/file-runtime-state.repository.ts`

## Primary blockers

1. Backend has no durable wallet lifecycle truth.
2. Backend `wallet/public-addresses` is currently in-memory `Map`, not persisted runtime state.
3. `create_wallet`, `wallet_manager`, and `import_private_key` routes are defined but not registered in the nav graph.
4. Android onboarding/create/import pages still use hardcoded placeholder UI content even when the repository is no longer mock.
5. ViewModels do not submit any real create/import/confirm actions yet.
