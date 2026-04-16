package com.app.data.remote.dto

import com.app.data.model.MarketTicker

data class MarketTickerDto(
    val symbol: String,
    val name: String,
    val priceUsd: Double,
    val change24h: Double,
    val marketCapUsd: Double,
    val volume24hUsd: Double,
)

fun MarketTickerDto.toModel() = MarketTicker(symbol, name, priceUsd, change24h, marketCapUsd, volume24hUsd)
