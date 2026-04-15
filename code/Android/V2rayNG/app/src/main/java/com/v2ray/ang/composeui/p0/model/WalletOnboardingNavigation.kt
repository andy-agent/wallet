package com.v2ray.ang.composeui.p0.model

import com.v2ray.ang.composeui.navigation.CryptoVpnRouteSpec

fun WalletOnboardingUiState.resolveWalletActionLabel(): String = when {
    walletExists && walletNextAction.equals("BACKUP_MNEMONIC", ignoreCase = true) -> "备份助记词"
    walletExists && walletNextAction.equals("CONFIRM_MNEMONIC", ignoreCase = true) -> "确认助记词"
    walletExists -> "进入钱包"
    else -> "创建或导入"
}

fun WalletOnboardingUiState.resolveContinueRoute(): String = when {
    walletExists &&
        walletNextAction.equals("BACKUP_MNEMONIC", ignoreCase = true) &&
        !walletId.isNullOrBlank() -> CryptoVpnRouteSpec.backupMnemonicRoute(walletId)

    walletExists &&
        walletNextAction.equals("CONFIRM_MNEMONIC", ignoreCase = true) &&
        !walletId.isNullOrBlank() -> CryptoVpnRouteSpec.confirmMnemonicRoute(walletId)

    walletExists -> CryptoVpnRouteSpec.walletHome.pattern
    selectedMode == WalletCreationMode.CREATE -> CryptoVpnRouteSpec.createWalletRoute("create")
    else -> CryptoVpnRouteSpec.importWalletMethod.pattern
}
