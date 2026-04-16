package com.app.data.model

data class MarketTicker(
    val symbol: String,
    val name: String,
    val priceUsd: Double,
    val change24h: Double,
    val marketCapUsd: Double,
    val volume24hUsd: Double,
)
