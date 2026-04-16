# Wallet Create/Import Route Inventory

更新时间：2026-04-15

## 路由清单

### `wallet_onboarding`
- 页面：[WalletOnboardingPage.kt](/Users/cnyirui/git/projects/liaojiang/code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/composeui/pages/p0/WalletOnboardingPage.kt)
- ViewModel：[WalletOnboardingViewModel.kt](/Users/cnyirui/git/projects/liaojiang/code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/composeui/p0/viewmodel/WalletOnboardingViewModel.kt)
- 仓储：[RealP0Repository.kt](/Users/cnyirui/git/projects/liaojiang/code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/composeui/p0/repository/RealP0Repository.kt)
- 当前状态：真实账号上下文 + 真实钱包生命周期
- 路由流转：
  - 已有钱包：继续进入 `wallet_home`
  - 无钱包且选择创建：进入 `create_wallet/{mode}`
  - 无钱包且选择导入：进入 `import_wallet_method`

### `create_wallet/{mode}`
- 页面：[CreateWalletPage.kt](/Users/cnyirui/git/projects/liaojiang/code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/composeui/pages/p2extended/CreateWalletPage.kt)
- ViewModel：[CreateWalletViewModel.kt](/Users/cnyirui/git/projects/liaojiang/code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/composeui/p2extended/viewmodel/CreateWalletViewModel.kt)
- 仓储：[RealCryptoVpnRepository.kt](/Users/cnyirui/git/projects/liaojiang/code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/composeui/common/repository/RealCryptoVpnRepository.kt)
- 当前状态：真实服务端 `wallet/lifecycle` 写入
- 路由流转：
  - 主按钮：创建钱包生命周期后进入 `backup_mnemonic/{walletId}`
  - 次按钮：进入 `import_wallet_method`
- 当前阻塞：
  - 仍未接入真实链账户生成与助记词派生

### `import_wallet_method`
- 页面：[ImportWalletMethodPage.kt](/Users/cnyirui/git/projects/liaojiang/code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/composeui/pages/p2extended/ImportWalletMethodPage.kt)
- ViewModel：[ImportWalletMethodViewModel.kt](/Users/cnyirui/git/projects/liaojiang/code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/composeui/p2extended/viewmodel/ImportWalletMethodViewModel.kt)
- 仓储：[RealCryptoVpnRepository.kt](/Users/cnyirui/git/projects/liaojiang/code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/composeui/common/repository/RealCryptoVpnRepository.kt)
- 当前状态：真实账号上下文；运行时已去掉 mock 文案
- 路由流转：
  - 主按钮：进入 `import_mnemonic/{source}`
  - 次按钮：返回 `wallet_onboarding`

### `import_mnemonic/{source}`
- 页面：[ImportMnemonicPage.kt](/Users/cnyirui/git/projects/liaojiang/code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/composeui/pages/p2extended/ImportMnemonicPage.kt)
- ViewModel：[ImportMnemonicViewModel.kt](/Users/cnyirui/git/projects/liaojiang/code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/composeui/p2extended/viewmodel/ImportMnemonicViewModel.kt)
- 仓储：[RealCryptoVpnRepository.kt](/Users/cnyirui/git/projects/liaojiang/code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/composeui/common/repository/RealCryptoVpnRepository.kt)
- 当前状态：真实服务端 `wallet/lifecycle` 导入写入；不再显示示例助记词
- 路由流转：
  - 主按钮：导入成功后进入 `wallet_home`
  - 次按钮：返回 `import_wallet_method`
- 当前阻塞：
  - 仍未接入真实助记词校验、地址派生、多链钱包导入

### `backup_mnemonic/{walletId}`
- 页面：[BackupMnemonicPage.kt](/Users/cnyirui/git/projects/liaojiang/code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/composeui/pages/p2extended/BackupMnemonicPage.kt)
- ViewModel：[BackupMnemonicViewModel.kt](/Users/cnyirui/git/projects/liaojiang/code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/composeui/p2extended/viewmodel/BackupMnemonicViewModel.kt)
- 仓储：[RealCryptoVpnRepository.kt](/Users/cnyirui/git/projects/liaojiang/code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/composeui/common/repository/RealCryptoVpnRepository.kt)
- 当前状态：真实服务端 `wallet/lifecycle` 备份确认推进；不再显示假助记词
- 路由流转：
  - 主按钮：确认已完成备份，进入 `confirm_mnemonic/{walletId}`
  - 次按钮：返回 `wallet_onboarding`
- 当前阻塞：
  - 仍未接入真实助记词展示/一次性读取

### `confirm_mnemonic/{walletId}`
- 页面：[ConfirmMnemonicPage.kt](/Users/cnyirui/git/projects/liaojiang/code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/composeui/pages/p2extended/ConfirmMnemonicPage.kt)
- ViewModel：[ConfirmMnemonicViewModel.kt](/Users/cnyirui/git/projects/liaojiang/code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/composeui/p2extended/viewmodel/ConfirmMnemonicViewModel.kt)
- 仓储：[RealCryptoVpnRepository.kt](/Users/cnyirui/git/projects/liaojiang/code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/composeui/common/repository/RealCryptoVpnRepository.kt)
- 当前状态：真实服务端 `wallet/lifecycle` 激活推进；不再显示假抽查题
- 路由流转：
  - 主按钮：确认完成后进入 `wallet_home`
  - 次按钮：返回 `backup_mnemonic/{walletId}`
- 当前阻塞：
  - 仍未接入真实 challenge/verify 助记词题面

### `receive/{assetId}/{chainId}`
- 页面：[ReceivePage.kt](/Users/cnyirui/git/projects/liaojiang/code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/composeui/pages/p2/ReceivePage.kt)
- ViewModel：[ReceiveViewModel.kt](/Users/cnyirui/git/projects/liaojiang/code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/composeui/p2/viewmodel/ReceiveViewModel.kt)
- 仓储：[RealCryptoVpnRepository.kt](/Users/cnyirui/git/projects/liaojiang/code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/composeui/common/repository/RealCryptoVpnRepository.kt)
- 当前状态：真实收款上下文
- 路由流转：
  - 无钱包：导航层分流回 `wallet_onboarding`
  - 有钱包无地址：展示真实空态
  - 有钱包有地址：展示真实地址/分享

## 下一批优先修改文件
- [wallet.service.ts](/Users/cnyirui/git/projects/liaojiang/code/backend/src/modules/wallet/wallet.service.ts)
- [wallet.e2e-spec.ts](/Users/cnyirui/git/projects/liaojiang/code/backend/test/wallet.e2e-spec.ts)
- [P2ExtendedNavGraph.kt](/Users/cnyirui/git/projects/liaojiang/code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/composeui/navigation/P2ExtendedNavGraph.kt)
- [P2CoreNavGraph.kt](/Users/cnyirui/git/projects/liaojiang/code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/composeui/navigation/P2CoreNavGraph.kt)
- [RealCryptoVpnRepository.kt](/Users/cnyirui/git/projects/liaojiang/code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/composeui/common/repository/RealCryptoVpnRepository.kt)

## 仍然阻塞的真实能力
- 真实链账户生成
- 真实助记词展示/一次性读取
- 真实助记词 challenge 校验
- 多链地址派生与写回服务端
