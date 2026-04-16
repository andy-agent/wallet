package com.app.data.remote.dto

import com.app.data.model.Asset

data class AssetDto(
    val id: String,
    val chainId: String,
    val symbol: String,
    val name: String,
    val balance: Double,
    val priceUsd: Double,
    val change24h: Double,
    val address: String,
)

fun AssetDto.toModel() = Asset(id, chainId, symbol, name, balance, priceUsd, change24h, address)
