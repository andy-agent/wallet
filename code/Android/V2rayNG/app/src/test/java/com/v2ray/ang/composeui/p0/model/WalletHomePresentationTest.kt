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
        assertEquals("$88.12", tronUsdt.valueText)
        assertEquals("$1.00", tronUsdt.unitPriceText)
        assertEquals("固定报价", tronUsdt.priceStatusText)
        assertEquals("7 笔交易", uiState.totalBalanceText)
        assertEquals("$132.65", buildWalletPortfolioValue(uiState.assets))
        assertFalse(tronUsdt.balanceText.contains("58.99"))
        assertFalse(tronUsdt.valueText.contains("58.99"))
    }

    @Test
    fun `portfolio value sums only explicit dollar quotes`() {
        val assets = listOf(
            AssetHolding(
                tokenKey = "tron:native:USDT",
                symbol = "USDT",
                chainLabel = "TRON",
                balanceText = "88.12 USDT",
                valueText = "累计 58.99 USDT",
                unitPriceText = "$1.00",
                changeText = "CONFIRMED",
                changePositive = true,
            ),
            AssetHolding(
                tokenKey = "solana:native:SOL",
                symbol = "SOL",
                chainLabel = "Solana",
                balanceText = "1.25 SOL",
                valueText = "$12.34",
                unitPriceText = "$9.87",
                changeText = "READY",
                changePositive = true,
            ),
            AssetHolding(
                tokenKey = "tron:native:TRX",
                symbol = "TRX",
                chainLabel = "TRON",
                balanceText = "15 TRX",
                valueText = "$0.66",
                unitPriceText = "$0.04",
                changeText = "READY",
                changePositive = true,
            ),
        )

        assertEquals("$13.00", buildWalletPortfolioValue(assets))
        assertEquals("累计 58.99 USDT", formatWalletAssetValueDisplay("累计 58.99 USDT"))
    }

    @Test
    fun `wallet home preserves sub cent usd prices`() {
        val uiState = sampleOverview().copy(
            assetItems = sampleOverview().assetItems + WalletAssetItemData(
                assetId = "sol-andy",
                networkCode = "SOLANA",
                assetCode = "ANDY",
                displayName = "ANDY",
                symbol = "ANDY",
                decimals = 9,
                isNative = false,
                contractAddress = "8zFP8GeszFz7FvuHesguekTxDjm4KLsJEYBZTKyMLEoE",
                walletVisible = true,
                orderPayable = false,
                publicAddressCount = 1,
                orderCount = 0,
                totalPayableAmount = "0.000000",
                availableBalanceUiAmount = "0",
                availableBalanceStatus = "READY",
                unitPriceUsd = "0.000288",
                valueUsd = "0.00",
                priceChangePct24h = "6.40",
                priceStatus = "READY",
                priceUpdatedAt = "2026-04-21T08:48:00Z",
                isCustom = true,
                customTokenId = "custom-andy",
            ),
        ).toWalletHomeUiState(sampleLifecycle())

        val andy = uiState.assets.first { it.symbol == "ANDY" }
        assertEquals("$0.00028800", andy.unitPriceText)
        assertEquals("+6.40%", andy.changeText)
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
                    unitPriceUsd = "12.34",
                    valueUsd = "15.43",
                    priceChangePct24h = "2.10",
                    priceStatus = "READY",
                    priceUpdatedAt = "2026-04-20T08:00:00Z",
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
                    unitPriceUsd = "1.00",
                    valueUsd = "25.50",
                    priceChangePct24h = "0.00",
                    priceStatus = "FIXED",
                    priceUpdatedAt = "2026-04-20T08:00:00Z",
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
                    unitPriceUsd = "0.24",
                    valueUsd = "3.60",
                    priceChangePct24h = "-1.50",
                    priceStatus = "READY",
                    priceUpdatedAt = "2026-04-20T08:00:00Z",
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
                    unitPriceUsd = "1.00",
                    valueUsd = "88.12",
                    priceChangePct24h = "0.00",
                    priceStatus = "FIXED",
                    priceUpdatedAt = "2026-04-20T08:00:00Z",
                ),
            ),
            totalPortfolioValueUsd = "132.65",
            priceUpdatedAt = "2026-04-20T08:00:00Z",
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
