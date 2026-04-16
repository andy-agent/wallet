package com.v2ray.ang.composeui.p1.model

fun OrderCheckoutUiState.resolvedPaymentQrText(): String {
    return qrText.asTrustedPaymentQrPayload()
}

fun WalletPaymentConfirmUiState.resolvedPaymentQrText(): String {
    return qrText.asTrustedPaymentQrPayload()
}

private fun String.asTrustedPaymentQrPayload(): String = trim()
