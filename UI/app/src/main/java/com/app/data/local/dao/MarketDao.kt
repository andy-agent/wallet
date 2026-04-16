package com.app.data.local.dao

import com.app.data.local.entity.MarketTickerEntity
import kotlinx.coroutines.flow.StateFlow

interface MarketDao {
    val items: StateFlow<List<MarketTickerEntity>>
    fun replaceAll(items: List<MarketTickerEntity>)
    fun getAll(): List<MarketTickerEntity>
    fun find(symbol: String): MarketTickerEntity?
}
