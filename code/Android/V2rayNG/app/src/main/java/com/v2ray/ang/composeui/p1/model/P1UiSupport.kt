package com.v2ray.ang.composeui.p1.model

enum class P1ScreenState {
    Loading,
    Content,
    Empty,
    Error,
    Unavailable,
}

data class P1StateInfo(
    val state: P1ScreenState = P1ScreenState.Loading,
    val title: String = "",
    val message: String = "",
)

data class P1PlanCard(
    val planCode: String,
    val title: String,
    val priceText: String,
    val subtitle: String,
    val badge: String = "",
    val tags: List<String> = emptyList(),
    val featured: Boolean = false,
)

data class P1RegionOption(
    val regionCode: String,
    val title: String,
    val subtitle: String,
    val trailing: String,
    val tier: String,
    val status: String,
    val isAllowed: Boolean,
)

data class P1OrderSummary(
    val orderNo: String,
    val planCode: String,
    val planName: String,
    val status: String,
    val statusText: String,
    val amountText: String,
    val assetCode: String,
    val networkCode: String,
    val createdAt: String,
    val expiresAt: String,
    val collectionAddress: String = "",
    val qrText: String = "",
    val baseAmount: String? = null,
    val uniqueAmountDelta: String? = null,
    val payableAmount: String? = null,
    val txHash: String? = null,
    val paymentMatchedAt: String? = null,
    val subscriptionUrl: String? = null,
)

data class P1DetailLine(
    val label: String,
    val value: String,
)
