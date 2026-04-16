package com.v2ray.ang.composeui.p0.model

import com.v2ray.ang.payment.data.api.WalletAssetItemData
import com.v2ray.ang.payment.data.api.WalletChainItemData
import com.v2ray.ang.payment.data.api.WalletLifecycleData
import com.v2ray.ang.payment.data.api.WalletOverviewData
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

class WalletHomePresentationTest {

    @Test
    fun `wallet home uses onchain balances instead of cumulative payable totals`() {
        val uiState = sampleOverview().toWalletHomeUiState(sampleLifecycle())

        val tronUsdt = uiState.assets.first {
            it.symbol == "USDT" && it.chainLabel == "TRON"
        }

        assertEquals("88.12 USDT", tronUsdt.balanceText)
        assertEquals("Tether USD (TRC20)", tronUsdt.valueText)
        assertEquals("7 笔交易", uiState.totalBalanceText)
        assertEquals("估值待同步", buildWalletPortfolioValue(uiState.assets))
        assertFalse(tronUsdt.balanceText.contains("58.99"))
        assertFalse(tronUsdt.valueText.contains("58.99"))
    }

    @Test
    fun `portfolio value sums only explicit dollar quotes`() {
        val assets = listOf(
            AssetHolding(
                symbol = "USDT",
                chainLabel = "TRON",
                balanceText = "88.12 USDT",
                valueText = "累计 58.99 USDT",
                changeText = "CONFIRMED",
                changePositive = true,
            ),
            AssetHolding(
                symbol = "SOL",
                chainLabel = "Solana",
                balanceText = "1.25 SOL",
                valueText = "$12.34",
                changeText = "READY",
                changePositive = true,
            ),
            AssetHolding(
                symbol = "TRX",
                chainLabel = "TRON",
                balanceText = "15 TRX",
                valueText = "$0.66",
                changeText = "READY",
                changePositive = true,
            ),
        )

        assertEquals("$13.00", buildWalletPortfolioValue(assets))
        assertEquals("累计 58.99 USDT", formatWalletAssetValueDisplay("累计 58.99 USDT"))
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
                    assetCount = 2,
                    orderCount = 3,
                    hasConfiguredAddress = true,
                ),
                WalletChainItemData(
                    networkCode = "TRON",
                    displayName = "TRON Mainnet",
                    nativeAssetCode = "TRX",
                    directBroadcastEnabled = true,
                    proxyBroadcastEnabled = true,
                    requiredConfirmations = 20,
                    assetCount = 2,
                    orderCount = 4,
                    hasConfiguredAddress = true,
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
                    publicAddressCount = 1,
                    orderCount = 2,
                    totalPayableAmount = "12.000000",
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
                    publicAddressCount = 1,
                    orderCount = 1,
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
                    publicAddressCount = 1,
                    orderCount = 1,
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
                    publicAddressCount = 1,
                    orderCount = 3,
                    totalPayableAmount = "58.990000",
                    availableBalanceUiAmount = "88.120000",
                    availableBalanceStatus = "READY",
                    lastOrderStatus = "CONFIRMED",
                ),
            ),
            alerts = emptyList(),
        )
    }

    private fun sampleLifecycle(): WalletLifecycleData {
        return WalletLifecycleData(
            accountId = "acct-1",
            walletExists = true,
            receiveState = "READY",
            lifecycleStatus = "ACTIVE",
            sourceType = "LOCAL",
            walletId = "wallet-1",
            displayName = "Primary Wallet",
            nextAction = "READY",
        )
    }
}
