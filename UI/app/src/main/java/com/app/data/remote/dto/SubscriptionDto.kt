package com.app.data.remote.dto

import com.app.data.model.Subscription

data class SubscriptionDto(
    val id: String,
    val url: String,
    val lastUpdatedAt: Long,
    val expiresAt: Long,
    val trafficUsedGb: Double,
    val trafficTotalGb: Double,
    val autoRenew: Boolean,
)

fun SubscriptionDto.toModel() = Subscription(id, url, lastUpdatedAt, expiresAt, trafficUsedGb, trafficTotalGb, autoRenew)
