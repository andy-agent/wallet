package com.v2ray.ang.composeui.p1.model

import java.net.URLEncoder
import java.nio.charset.StandardCharsets

private const val SOLANA_USDT_MINT = "Es9vMFrzaCERmJfrF4H2FYD4KCoNkY11McCe8BenwNYB"

fun OrderCheckoutUiState.resolvedPaymentQrText(): String {
    return buildPaymentQrText(
        qrText = qrText,
        orderNo = orderNo,
        networkCode = networkCode,
        assetCode = assetCode,
        collectionAddress = collectionAddress,
        payableAmount = payableAmount,
    )
}

fun WalletPaymentConfirmUiState.resolvedPaymentQrText(): String {
    return buildPaymentQrText(
        qrText = qrText,
        orderNo = orderNo,
        networkCode = networkCode,
        assetCode = assetCode,
        collectionAddress = collectionAddress,
        payableAmount = payableAmount,
    )
}

private fun buildPaymentQrText(
    qrText: String,
    orderNo: String?,
    networkCode: String,
    assetCode: String,
    collectionAddress: String,
    payableAmount: String,
): String {
    val existing = qrText.trim()
    if (existing.isNotEmpty()) {
        return existing
    }

    val normalizedNetwork = networkCode.trim().uppercase()
    val normalizedAsset = assetCode.trim().uppercase()
    val address = collectionAddress.trim()
    val amount = payableAmount.trim()
    if (normalizedNetwork.isEmpty() || address.isEmpty() || amount.isEmpty()) {
        return ""
    }

    return when (normalizedNetwork) {
        "SOLANA" -> {
            val params = buildList {
                add("amount=${encodeQrParam(amount)}")
                if (normalizedAsset == "USDT") {
                    add("spl-token=${encodeQrParam(SOLANA_USDT_MINT)}")
                }
            }.joinToString("&")
            "solana:$address?$params"
        }

        else -> address
    }
}

private fun encodeQrParam(value: String): String =
    URLEncoder.encode(value, StandardCharsets.UTF_8.name())
