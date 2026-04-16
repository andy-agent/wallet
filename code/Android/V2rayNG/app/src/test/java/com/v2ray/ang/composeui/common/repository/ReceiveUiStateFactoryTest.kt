package com.v2ray.ang.composeui.common.repository

import com.v2ray.ang.payment.data.api.WalletAssetItemData
import com.v2ray.ang.payment.data.api.WalletChainItemData
import com.v2ray.ang.payment.data.api.WalletPublicAddressData
import com.v2ray.ang.payment.data.api.WalletReceiveContextData
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ReceiveUiStateFactoryTest {

    @Test
    fun `toReceiveUiState keeps USDT route asset across multi-chain variants`() {
        val uiState = sampleReceiveContext().toReceiveUiState()

        val tronVariant = uiState.variants.first { it.chainId == "tron" }
        val solanaVariant = uiState.variants.first { it.chainId == "solana" }

        assertEquals("USDT", tronVariant.assetId)
        assertEquals("USDT", solanaVariant.assetId)
        assertEquals("TTronReceiveAddress", tronVariant.address)
        assertEquals("TTronReceiveAddress", tronVariant.qrContent)
        assertEquals("USDT · TRON\nTTronReceiveAddress", tronVariant.shareText)
        assertTrue(tronVariant.canShare)
        assertFalse(solanaVariant.selected)
        assertEquals("", solanaVariant.address)
    }

    @Test
    fun `previewVariantSelection switches current network before reload finishes`() {
        val preview = sampleReceiveContext()
            .toReceiveUiState()
            .previewVariantSelection(assetId = "USDT", chainId = "solana")

        assertEquals("USDT · Solana", preview.badge)
        assertEquals("USDT · Solana", preview.metrics.first().value)
        assertEquals("--", preview.fields.first().value)
        assertEquals("暂无地址可分享", preview.primaryActionLabel)
        assertFalse(preview.canShare)
        assertTrue(preview.variants.first { it.chainId == "solana" }.selected)
        assertFalse(preview.variants.first { it.chainId == "tron" }.selected)
    }

    private fun sampleReceiveContext(): WalletReceiveContextData {
        return WalletReceiveContextData(
            selectedNetworkCode = "TRON",
            selectedAssetCode = "USDT",
            chainItems = listOf(
                WalletChainItemData(
                    networkCode = "SOLANA",
                    displayName = "Solana",
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
                    requiredConfirmations = 1,
                    selected = true,
                ),
            ),
            assetItems = listOf(
                WalletAssetItemData(
                    assetId = "usdt-tron",
                    networkCode = "TRON",
                    assetCode = "USDT",
                    displayName = "USDT",
                    symbol = "USDT",
                    decimals = 6,
                    isNative = false,
                    walletVisible = true,
                    orderPayable = true,
                    selected = true,
                ),
            ),
            addresses = listOf(
                WalletPublicAddressData(
                    addressId = "addr-tron",
                    accountId = "acct-1",
                    networkCode = "TRON",
                    assetCode = "USDT",
                    address = "TTronReceiveAddress",
                    isDefault = true,
                    createdAt = "2026-04-16T00:00:00Z",
                ),
            ),
            defaultAddress = "TTronReceiveAddress",
            canShare = true,
            walletExists = true,
            receiveState = "READY",
            status = "已配置收款地址",
            note = "收款地址。",
            shareText = "USDT · TRON\nTTronReceiveAddress",
        )
    }
}
