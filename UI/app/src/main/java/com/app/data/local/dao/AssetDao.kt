package com.app.data.local.dao

import com.app.data.local.entity.AssetEntity
import kotlinx.coroutines.flow.StateFlow

interface AssetDao {
    val items: StateFlow<List<AssetEntity>>
    fun replaceAll(items: List<AssetEntity>)
    fun getAll(): List<AssetEntity>
    fun findBySymbol(symbol: String): AssetEntity?
}
