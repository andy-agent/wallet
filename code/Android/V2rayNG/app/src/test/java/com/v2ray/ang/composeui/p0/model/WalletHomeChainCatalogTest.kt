package com.v2ray.ang.composeui.p0.model

import com.v2ray.ang.payment.data.api.WalletChainAccountData
import com.v2ray.ang.payment.data.api.WalletSummaryData
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class WalletHomeChainCatalogTest {

    @Test
    fun `buildWalletHomeChainOptions seeds self custody wallets when cached chain graph is empty`() {
        val options = buildWalletHomeChainOptions(
            wallet = sampleWallet(),
            chainAccounts = emptyList(),
        )

        assertEquals(
            listOf(
                "ethereum",
                "bsc",
                "polygon",
                "arbitrum",
                "base",
                "optimism",
                "avalanche",
                "solana",
                "tron",
            ),
            options.map { it.chainId },
        )
        assertTrue(options.all { it.address.isBlank() })
        assertTrue(options.all { it.addressSuffix == "----" })
    }

    @Test
    fun `buildWalletHomeChainOptions preserves cached chain accounts and fills missing self custody chains`() {
        val evmAddress = "0x1234567890abcdef1234567890abcdef12345678"
        val solanaAddress = "So11111111111111111111111111111111111111112"

        val options = buildWalletHomeChainOptions(
            wallet = sampleWallet(),
            chainAccounts = listOf(
                sampleChainAccount(
                    networkCode = "ETHEREUM",
                    chainFamily = "EVM",
                    address = evmAddress,
                ),
                sampleChainAccount(
                    networkCode = "SOLANA",
                    chainFamily = "SOLANA",
                    address = solanaAddress,
                ),
            ),
        )

        assertEquals(evmAddress, options.first { it.chainId == "ethereum" }.address)
        assertEquals(evmAddress, options.first { it.chainId == "base" }.address)
        assertEquals(solanaAddress, options.first { it.chainId == "solana" }.address)
        assertEquals("5678", options.first { it.chainId == "optimism" }.addressSuffix)
        assertEquals("1112", options.first { it.chainId == "solana" }.addressSuffix)
    }

    @Test
    fun `walletHomeChainLabel renders human readable labels for seeded chains`() {
        assertEquals("Ethereum", walletHomeChainLabel("ETHEREUM"))
        assertEquals("BSC", walletHomeChainLabel("BSC"))
        assertEquals("Polygon", walletHomeChainLabel("POLYGON"))
        assertEquals("Arbitrum", walletHomeChainLabel("ARBITRUM"))
        assertEquals("Base", walletHomeChainLabel("BASE"))
        assertEquals("Optimism", walletHomeChainLabel("OPTIMISM"))
        assertEquals("Avalanche", walletHomeChainLabel("AVALANCHE_C"))
        assertEquals("Solana", walletHomeChainLabel("SOLANA"))
        assertEquals("TRON", walletHomeChainLabel("TRON"))
    }

    private fun sampleWallet() = WalletSummaryData(
        walletId = "wallet-main",
        walletName = "Main Wallet",
        walletKind = "SELF_CUSTODY",
        sourceType = "IMPORTED_MNEMONIC",
        isDefault = true,
        isArchived = false,
        deviceCapabilitySummary = null,
        createdAt = "2026-04-23T00:00:00Z",
        updatedAt = "2026-04-23T00:00:00Z",
    )

    private fun sampleChainAccount(
        networkCode: String,
        chainFamily: String,
        address: String,
    ) = WalletChainAccountData(
        chainAccountId = "acct-$networkCode",
        walletId = "wallet-main",
        keySlotId = "slot-$networkCode",
        chainFamily = chainFamily,
        networkCode = networkCode,
        address = address,
        capability = "SIGN_AND_PAY",
        isEnabled = true,
        isDefaultReceive = true,
        createdAt = "2026-04-23T00:00:00Z",
        updatedAt = "2026-04-23T00:00:00Z",
    )
}
