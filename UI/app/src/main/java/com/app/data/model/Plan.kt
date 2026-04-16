package com.app.data.model

data class Plan(
    val id: String,
    val title: String,
    val durationLabel: String,
    val priceUsd: Double,
    val trafficGb: Int,
    val deviceLimit: Int,
    val recommended: Boolean,
)
