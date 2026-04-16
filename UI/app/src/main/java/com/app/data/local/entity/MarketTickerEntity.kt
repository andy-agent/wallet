package com.app.data.local.entity

import com.app.data.model.MarketTicker

data class MarketTickerEntity(
    val symbol: String,
    val name: String,
    val priceUsd: Double,
    val change24h: Double,
    val marketCapUsd: Double,
    val volume24hUsd: Double,
)

fun MarketTickerEntity.toModel() = MarketTicker(symbol, name, priceUsd, change24h, marketCapUsd, volume24hUsd)
fun MarketTicker.toEntity() = MarketTickerEntity(symbol, name, priceUsd, change24h, marketCapUsd, volume24hUsd)
