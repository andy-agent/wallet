# Android Wallet Onboarding/Create/Import Route Inventory

Scope: `code/Android/V2rayNG` Compose wallet onboarding/create/import flow only.

## Runtime host

- Real app runtime uses `RealP0Repository` and `RealCryptoVpnRepository` via `ComposeContainerActivity`.
- `LaunchSplashActivity` resolves the initial Compose route from `RealP0Repository.getSplashState()`.
- `MockP0Repository` and `MockCryptoVpnRepository` remain as default parameters and preview helpers, but they are not injected by the real host.

## Route inventory

| Route | Installed in nav graph | Main files | Runtime source | Current transition | Blocker |
| --- | --- | --- | --- | --- | --- |
| `splash` | Yes | `navigation/P0NavGraph.kt`, `ui/compose/LaunchSplashActivity.kt`, `p0/repository/RealP0Repository.kt`, `pages/p0/SplashPage.kt` | Already real data | Launch splash sends real users to `vpn_home` or `email_login`; Compose `splash` route itself still hard-jumps to `email_login` | No wallet existence branch. `nextRoute` never resolves to `wallet_onboarding` or `wallet_home` |
| `email_login` | Yes | `navigation/P0NavGraph.kt`, `pages/p0/EmailLoginPage.kt`, `p0/viewmodel/LoginViewModel.kt`, `p0/repository/RealP0Repository.kt` | Already real data | Login success -> `vpn_home`; secondary button -> `wallet_onboarding` | Wallet onboarding is manual-only; there is no post-login wallet existence decision |
| `wallet_onboarding` | Yes | `navigation/P0NavGraph.kt`, `pages/p0/WalletOnboardingPage.kt`, `p0/viewmodel/WalletOnboardingViewModel.kt`, `p0/repository/RealP0Repository.kt` | Real repository seed, but page body is still preview/static | Continue -> `wallet_home` | Does not branch to `create_wallet` or `import_wallet_method`; page ignores repo summary/warning fields and even marks current route as `wallet_home` |
| `wallet_home` | Yes | `navigation/P0NavGraph.kt`, `pages/p0/WalletHomePage.kt`, `p0/viewmodel/WalletHomeViewModel.kt`, `p0/repository/RealP0Repository.kt`, `payment/data/repository/PaymentRepository.kt` | Already real data | Bottom nav / receive / send / asset detail | Uses `/wallet/overview` when available, otherwise falls back to real orders/payment records; not a wallet existence gate |
| `import_wallet_method` | Yes | `navigation/P2ExtendedNavGraph.kt`, `pages/p2extended/ImportWalletMethodPage.kt`, `p2extended/viewmodel/ImportWalletMethodViewModel.kt`, `common/repository/RealCryptoVpnRepository.kt`, `p2extended/model/ImportWalletMethodContract.kt` | Real repo loaded, but visible page content is preview/static | Primary -> `import_mnemonic/onboarding`; secondary -> `wallet_onboarding` | No inbound transition from onboarding/create path; page advertises private key/keystore but nav only wires mnemonic |
| `import_mnemonic/{source}` | Yes | `navigation/P2ExtendedNavGraph.kt`, `pages/p2extended/ImportMnemonicPage.kt`, `p2extended/viewmodel/ImportMnemonicViewModel.kt`, `common/repository/RealCryptoVpnRepository.kt`, `p2extended/model/ImportMnemonicContract.kt` | Real repository but blocked, with preview-default body content | Primary -> `security_center`; secondary -> `import_wallet_method` | No real mnemonic parse/derive/import. Screen still renders hardcoded mnemonic words and detected chains |
| `backup_mnemonic/{walletId}` | Yes | `navigation/P2ExtendedNavGraph.kt`, `pages/p2extended/BackupMnemonicPage.kt`, `p2extended/viewmodel/BackupMnemonicViewModel.kt`, `common/repository/RealCryptoVpnRepository.kt`, `p2extended/model/BackupMnemonicContract.kt` | Real repository but blocked, with preview-default body content | Primary -> `confirm_mnemonic/primary_wallet`; secondary -> `security_center` | No real seed generation/read path. Screen still shows a hardcoded mnemonic |
| `confirm_mnemonic/{walletId}` | Yes | `navigation/P2ExtendedNavGraph.kt`, `pages/p2extended/ConfirmMnemonicPage.kt`, `p2extended/viewmodel/ConfirmMnemonicViewModel.kt`, `common/repository/RealCryptoVpnRepository.kt`, `p2extended/model/ConfirmMnemonicContract.kt` | Real repository but blocked, with preview-default body content | Primary -> `security_center`; secondary -> `backup_mnemonic/primary_wallet` | No real mnemonic challenge/verify flow. Screen still shows hardcoded answers/candidates |
| `security_center` | Yes | `navigation/P2ExtendedNavGraph.kt`, `pages/p2extended/SecurityCenterPage.kt`, `p2extended/viewmodel/SecurityCenterViewModel.kt`, `common/repository/RealCryptoVpnRepository.kt`, `p2extended/model/SecurityCenterContract.kt` | Real repo loaded, but visible page content is preview/static | Primary -> `chain_manager/primary_wallet`; secondary -> `profile` | Page ignores repo-backed `uiState` and shows fixed security cards; not a real wallet security surface yet |
| `chain_manager/{walletId}` | Yes | `navigation/P2ExtendedNavGraph.kt`, `pages/p2extended/ChainManagerPage.kt`, `p2extended/viewmodel/ChainManagerViewModel.kt`, `common/repository/RealCryptoVpnRepository.kt`, `p2extended/model/ChainManagerContract.kt` | Real repository but blocked, with preview-default body content | Primary -> `add_custom_token/tron`; secondary -> `wallet_home` | No real multi-chain wallet config read/write; page shows static chain rows |
| `create_wallet/{mode}` | No | `navigation/CryptoVpnRouteSpec.kt`, `pages/p2extended/CreateWalletPage.kt`, `p2extended/viewmodel/CreateWalletViewModel.kt`, `common/repository/RealCryptoVpnRepository.kt`, `p2extended/model/CreateWalletContract.kt` | Preview-only in practice; runtime route missing | None | Route spec/page/viewmodel/repo method exist, but `P2ExtendedNavGraph` never registers the route |
| `import_private_key/{chainId}` | No | `navigation/CryptoVpnRouteSpec.kt`, `pages/p2extended/ImportPrivateKeyPage.kt`, `p2extended/viewmodel/ImportPrivateKeyViewModel.kt`, `common/repository/RealCryptoVpnRepository.kt`, `p2extended/model/ImportPrivateKeyContract.kt` | Preview-only in practice; runtime route missing | None | Method advertised by import-method page, but route is not registered anywhere |
| `wallet_manager/{walletId}` | No | `navigation/CryptoVpnRouteSpec.kt`, `pages/p2extended/WalletManagerPage.kt`, `p2extended/viewmodel/WalletManagerViewModel.kt`, `common/repository/RealCryptoVpnRepository.kt`, `p2extended/model/WalletManagerContract.kt` | Preview-only in practice; runtime route missing | None | Route spec/page/viewmodel/repo method exist, but nav graph never registers it |
| `address_book/{mode}` | No | `navigation/CryptoVpnRouteSpec.kt`, `pages/p2extended/AddressBookPage.kt`, `p2extended/viewmodel/AddressBookViewModel.kt`, `common/repository/RealCryptoVpnRepository.kt`, `p2extended/model/AddressBookContract.kt` | Preview-only in practice; runtime route missing | None | Wallet-manager-adjacent route exists, but no nav registration and no inbound transition |

## Repository methods involved

### P0Repository / RealP0Repository

- `getSplashState()`
- `getLoginSeed()`
- `login(email, password)`
- `getWalletOnboardingState()`
- `getWalletHomeState()`

### CryptoVpnRepository / RealCryptoVpnRepository

- `getCreateWalletState(args)`
- `getImportWalletMethodState()`
- `getImportMnemonicState(args)`
- `getImportPrivateKeyState(args)`
- `getBackupMnemonicState(args)`
- `getConfirmMnemonicState(args)`
- `getSecurityCenterState()`
- `getChainManagerState(args)`
- `getWalletManagerState(args)`
- `getAddressBookState(args)`

### PaymentRepository methods actually feeding the real branches

- `getMe()`
- `getSubscription()`
- `warmSyncAfterLogin(...)`
- `clearAuth()`
- `getWalletOverview()`
- `getCachedCurrentUser()`
- `getCachedOrders(...)`
- `getCurrentUserId()`

## Missing links / route blockers

- No runtime wallet existence check sends users into wallet onboarding or wallet home.
- `wallet_onboarding` does not branch by selected mode.
- `create_wallet`, `import_private_key`, `wallet_manager`, and `address_book` are not installed in any nav graph.
- `import_wallet_method`, `import_mnemonic`, `backup_mnemonic`, `confirm_mnemonic`, `security_center`, and `chain_manager` are installed, but most screens still render fixed preview-like body content.
- Wallet-domain pages depend on real account/session context, but wallet-engine capabilities are still missing:
  - wallet create/save/derive
  - mnemonic parse/import
  - mnemonic generate/read/verify
  - multi-wallet list/switch
  - chain config persistence

## Files to change next

- `code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/composeui/navigation/P0NavGraph.kt`
- `code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/composeui/navigation/P2ExtendedNavGraph.kt`
- `code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/composeui/p0/repository/RealP0Repository.kt`
- `code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/composeui/pages/p0/WalletOnboardingPage.kt`
- `code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/composeui/pages/p2extended/ImportWalletMethodPage.kt`
- `code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/composeui/pages/p2extended/ImportMnemonicPage.kt`
- `code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/composeui/pages/p2extended/BackupMnemonicPage.kt`
- `code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/composeui/pages/p2extended/ConfirmMnemonicPage.kt`
- `code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/composeui/pages/p2extended/SecurityCenterPage.kt`
- `code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/composeui/pages/p2extended/ChainManagerPage.kt`
- `code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/composeui/common/repository/RealCryptoVpnRepository.kt`
