package com.v2ray.ang.composeui.p0.model

import org.junit.Assert.assertEquals
import org.junit.Test

class WalletOnboardingNavigationTest {

    @Test
    fun `continue route opens create wallet when no wallet exists and create is selected`() {
        val uiState = WalletOnboardingUiState(
            selectedMode = WalletCreationMode.CREATE,
        )

        assertEquals("create_wallet/create", uiState.resolveContinueRoute())
    }

    @Test
    fun `continue route opens import wallet method when no wallet exists and import is selected`() {
        val uiState = WalletOnboardingUiState(
            selectedMode = WalletCreationMode.IMPORT,
        )

        assertEquals("import_wallet_method", uiState.resolveContinueRoute())
    }

    @Test
    fun `continue route opens wallet home when wallet is ready`() {
        val uiState = WalletOnboardingUiState(
            walletExists = true,
            walletId = "wallet_1",
            walletNextAction = "READY",
        )

        assertEquals("wallet_home", uiState.resolveContinueRoute())
        assertEquals("进入钱包", uiState.resolveWalletActionLabel())
    }

    @Test
    fun `continue route prioritizes backup mnemonic when wallet requires backup`() {
        val uiState = WalletOnboardingUiState(
            walletExists = true,
            walletId = "wallet_1",
            walletNextAction = "BACKUP_MNEMONIC",
        )

        assertEquals("backup_mnemonic/wallet_1", uiState.resolveContinueRoute())
        assertEquals("备份助记词", uiState.resolveWalletActionLabel())
    }

    @Test
    fun `continue route prioritizes confirm mnemonic when wallet requires confirmation`() {
        val uiState = WalletOnboardingUiState(
            walletExists = true,
            walletId = "wallet_1",
            walletNextAction = "CONFIRM_MNEMONIC",
        )

        assertEquals("confirm_mnemonic/wallet_1", uiState.resolveContinueRoute())
        assertEquals("确认助记词", uiState.resolveWalletActionLabel())
    }
}
