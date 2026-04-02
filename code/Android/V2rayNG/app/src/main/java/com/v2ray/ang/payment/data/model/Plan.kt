package com.v2ray.ang.payment.data.model

import com.google.gson.annotations.SerializedName

data class Plan(
    @SerializedName("planId")
    val id: String,
    @SerializedName("planCode")
    val planCode: String,
    val name: String,
    val description: String? = null,
    @SerializedName("billingCycleMonths")
    val billingCycleMonths: Int,
    @SerializedName("priceUsd")
    val priceUsd: String,
    @SerializedName("maxActiveSessions")
    val maxActiveSessions: Int,
    @SerializedName("regionAccessPolicy")
    val regionAccessPolicy: String,
    @SerializedName("includesAdvancedRegions")
    val includesAdvancedRegions: Boolean,
    @SerializedName("allowedRegionIds")
    val allowedRegionIds: List<String> = emptyList(),
    @SerializedName("displayOrder")
    val displayOrder: Int,
    val status: String? = null
) {
    val badge: String?
        get() = when (displayOrder) {
            1 -> "HOT"
            2 -> "NEW"
            else -> null
        }

    fun getTrafficDisplay(): String = "不限流量"

    fun getDurationDisplay(): String {
        return when {
            billingCycleMonths >= 12 && billingCycleMonths % 12 == 0 -> "${billingCycleMonths / 12}年"
            billingCycleMonths > 0 -> "${billingCycleMonths}个月"
            else -> "未知"
        }
    }

    fun supportsSol(): Boolean = true

    fun supportsUsdtTrc20(): Boolean = true
}
