package com.v2ray.ang.composeui.common.repository

import com.v2ray.ang.composeui.p2.model.SendRouteArgs
import com.v2ray.ang.payment.data.api.WalletAssetItemData
import com.v2ray.ang.payment.data.api.WalletChainItemData
import com.v2ray.ang.payment.data.api.WalletOverviewData
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SendUiStateFactoryTest {

    @Test
    fun `send ui state exposes onchain balance instead of order payable amount`() {
        val uiState = sampleOverview().toSendUiState(
            args = SendRouteArgs(assetId = "USDT", chainId = "tron"),
            currentOrderId = "ORD-123",
        )

        assertEquals("88.120000", uiState.availableBalance)
        assertEquals("", uiState.balanceSupportingText)
        assertEquals("USDT · TRON", uiState.metrics.first().value)
        assertEquals("后端支持直连 / 代理", uiState.metrics[2].value)
        assertEquals("ORD-123", uiState.fields.first { it.key == "memo" }.value)
        assertEquals("粘贴或扫码 TRON 地址", uiState.fields.first { it.key == "to" }.supportingText)
        assertEquals("USDT", uiState.assetOptions.first { it.selected }.id)
    }

    @Test
    fun `send ui state falls back to a valid asset when route asset does not exist on selected chain`() {
        val uiState = sampleOverview().toSendUiState(
            args = SendRouteArgs(assetId = "TRX", chainId = "solana"),
        )

        assertEquals(SendRouteArgs(assetId = "SOL", chainId = "solana"), uiState.currentRoute)
        assertTrue(uiState.networkOptions.first { it.id == "solana" }.selected)
        assertTrue(uiState.assetOptions.first { it.id == "SOL" }.selected)
        assertFalse(uiState.assetOptions.any { it.id == "TRX" })
        assertEquals("SOL · Solana", uiState.metrics.first().value)
        assertFalse(uiState.fields.any { it.key == "asset" })
    }

    private fun sampleOverview(): WalletOverviewData {
        return WalletOverviewData(
            accountId = "acct-1",
            accountEmail = "system@cnyirui.cn",
            selectedNetworkCode = "TRON",
            chainItems = listOf(
                WalletChainItemData(
                    networkCode = "SOLANA",
                    displayName = "Solana Mainnet",
                    nativeAssetCode = "SOL",
                    directBroadcastEnabled = true,
                    proxyBroadcastEnabled = true,
                    requiredConfirmations = 1,
                    selected = false,
                ),
                WalletChainItemData(
                    networkCode = "TRON",
                    displayName = "TRON Mainnet",
                    nativeAssetCode = "TRX",
                    directBroadcastEnabled = true,
                    proxyBroadcastEnabled = true,
                    requiredConfirmations = 20,
                    selected = true,
                ),
            ),
            assetItems = listOf(
                WalletAssetItemData(
                    assetId = "sol-native",
                    networkCode = "SOLANA",
                    assetCode = "SOL",
                    displayName = "Solana",
                    symbol = "SOL",
                    decimals = 9,
                    isNative = true,
                    walletVisible = true,
                    orderPayable = true,
                    totalPayableAmount = "54.950000",
                    availableBalanceUiAmount = "1.250000000",
                    availableBalanceStatus = "READY",
                ),
                WalletAssetItemData(
                    assetId = "sol-usdt",
                    networkCode = "SOLANA",
                    assetCode = "USDT",
                    displayName = "Tether USD (Solana)",
                    symbol = "USDT",
                    decimals = 6,
                    isNative = false,
                    walletVisible = true,
                    orderPayable = true,
                    totalPayableAmount = "12.000000",
                    availableBalanceUiAmount = "25.500000",
                    availableBalanceStatus = "READY",
                ),
                WalletAssetItemData(
                    assetId = "tron-native",
                    networkCode = "TRON",
                    assetCode = "TRX",
                    displayName = "TRON",
                    symbol = "TRX",
                    decimals = 6,
                    isNative = true,
                    walletVisible = true,
                    orderPayable = false,
                    totalPayableAmount = "6.000000",
                    availableBalanceUiAmount = "15.000000",
                    availableBalanceStatus = "READY",
                ),
                WalletAssetItemData(
                    assetId = "tron-usdt",
                    networkCode = "TRON",
                    assetCode = "USDT",
                    displayName = "Tether USD (TRC20)",
                    symbol = "USDT",
                    decimals = 6,
                    isNative = false,
                    walletVisible = true,
                    orderPayable = true,
                    totalPayableAmount = "54.950000",
                    availableBalanceUiAmount = "88.120000",
                    availableBalanceStatus = "READY",
                ),
            ),
            alerts = emptyList(),
        )
    }
}
