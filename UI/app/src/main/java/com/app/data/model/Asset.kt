package com.app.data.model

data class Asset(
    val id: String,
    val chainId: String,
    val symbol: String,
    val name: String,
    val balance: Double,
    val priceUsd: Double,
    val change24h: Double,
    val address: String,
)
