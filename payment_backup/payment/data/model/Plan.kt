package com.v2ray.ang.payment.data.model

import com.google.gson.annotations.SerializedName

/**
 * 套餐数据模型
 */
data class Plan(
    val id: String,
    val name: String,
    val description: String,
    @SerializedName("traffic_bytes")
    val trafficBytes: Long,
    @SerializedName("duration_days")
    val durationDays: Int,
    @SerializedName("price_usd")
    val priceUsd: String,
    @SerializedName("supported_assets")
    val supportedAssets: List<String>,
    val badge: String?
) {
    fun getTrafficDisplay(): String {
        return when {
            trafficBytes >= 1099511627776 -> "${trafficBytes / 1099511627776}TB"
            trafficBytes >= 1073741824 -> "${trafficBytes / 1073741824}GB"
            else -> "${trafficBytes / 1048576}MB"
        }
    }

    fun getDurationDisplay(): String {
        return when {
            durationDays >= 365 -> "${durationDays / 365}年"
            durationDays >= 30 -> "${durationDays / 30}个月"
            else -> "${durationDays}天"
        }
    }

    fun supportsSol(): Boolean = supportedAssets.contains("SOL")
    fun supportsUsdtTrc20(): Boolean = supportedAssets.contains("USDT_TRC20")
}

data class PlansResponse(
    val code: String,
    val message: String,
    val data: PlansData?
)

data class PlansData(
    val plans: List<Plan>
)
