package com.app.data.local.entity

import com.app.data.model.Asset

data class AssetEntity(
    val id: String,
    val chainId: String,
    val symbol: String,
    val name: String,
    val balance: Double,
    val priceUsd: Double,
    val change24h: Double,
    val address: String,
)

fun AssetEntity.toModel() = Asset(id, chainId, symbol, name, balance, priceUsd, change24h, address)
fun Asset.toEntity() = AssetEntity(id, chainId, symbol, name, balance, priceUsd, change24h, address)
