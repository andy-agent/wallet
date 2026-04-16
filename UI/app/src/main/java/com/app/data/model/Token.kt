package com.app.data.model

data class Token(
    val symbol: String,
    val chainId: String,
    val name: String,
    val balance: Double,
    val priceUsd: Double,
    val change24h: Double,
)
