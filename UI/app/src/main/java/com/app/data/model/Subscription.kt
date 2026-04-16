package com.app.data.model

data class Subscription(
    val id: String,
    val url: String,
    val lastUpdatedAt: Long,
    val expiresAt: Long,
    val trafficUsedGb: Double,
    val trafficTotalGb: Double,
    val autoRenew: Boolean,
)
