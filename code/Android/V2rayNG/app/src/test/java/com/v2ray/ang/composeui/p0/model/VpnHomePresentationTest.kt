package com.v2ray.ang.composeui.p0.model

import com.v2ray.ang.payment.data.api.WalletAssetItemData
import com.v2ray.ang.payment.data.api.WalletChainItemData
import com.v2ray.ang.payment.data.api.WalletOverviewData
import org.junit.Assert.assertEquals
import org.junit.Test

class VpnHomePresentationTest {

    @Test
    fun `vpn overview summarizes wallet onchain balances across assets`() {
        assertEquals(
            "58.99 USDT · 1.25 SOL",
            resolveVpnOverviewValueText(sampleOverview(includeSolBalance = true), ordersSyncUnavailable = false),
        )
    }

    @Test
    fun `vpn overview keeps stable balance text instead of using order usd totals`() {
        assertEquals(
            "58.99 USDT",
            resolveVpnOverviewValueText(sampleOverview(includeSolBalance = false), ordersSyncUnavailable = false),
        )
    }

    @Test
    fun `vpn overview falls back to placeholder when wallet overview is unavailable`() {
        assertEquals("--", resolveVpnOverviewValueText(walletOverview = null, ordersSyncUnavailable = true))
        assertEquals("余额待同步", resolveVpnOverviewValueText(walletOverview = null, ordersSyncUnavailable = false))
    }

    private fun sampleOverview(includeSolBalance: Boolean): WalletOverviewData {
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
                ),
                WalletChainItemData(
                    networkCode = "TRON",
                    displayName = "TRON Mainnet",
                    nativeAssetCode = "TRX",
                    directBroadcastEnabled = true,
                    proxyBroadcastEnabled = true,
                    requiredConfirmations = 20,
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
                    availableBalanceUiAmount = if (includeSolBalance) "1.250000000" else "0.000000000",
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
                    totalPayableAmount = "12.000000",
                    availableBalanceUiAmount = "58.990000",
                    availableBalanceStatus = "READY",
                ),
            ),
            alerts = emptyList(),
        )
    }
}
