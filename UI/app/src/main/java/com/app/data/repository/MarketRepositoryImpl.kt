package com.app.data.repository

import com.app.data.local.dao.MarketDao
import com.app.data.local.entity.toEntity
import com.app.data.local.entity.toModel
import com.app.data.model.MarketSignal
import com.app.data.model.MarketTicker
import com.app.data.model.RiskSignal
import com.app.data.model.TokenPricePoint
import com.app.data.remote.dto.toModel
import com.app.data.remote.mock.MockMarketDataSource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.runBlocking

class MarketRepositoryImpl(
    private val marketDao: MarketDao,
    private val remote: MockMarketDataSource,
) : MarketRepository {
    private val overviewState = MutableStateFlow<List<MarketTicker>>(emptyList())
    override val overview: StateFlow<List<MarketTicker>> = overviewState.asStateFlow()

    init {
        val seeded = runBlocking { remote.getOverview() }.map { it.toModel() }
        marketDao.replaceAll(seeded.map { it.toEntity() })
        overviewState.value = seeded
    }

    override suspend fun getOverview(): List<MarketTicker> = overview.value
    override suspend fun getTicker(symbol: String): MarketTicker? = marketDao.find(symbol)?.toModel() ?: remote.getTicker(symbol)?.toModel()
    override suspend fun getAbnormalMovers(): List<MarketSignal> = remote.abnormalSignals()
    override suspend fun getHotRisers(): List<MarketTicker> = overview.value.filter { it.change24h > 0 }.sortedByDescending { it.change24h }.take(3)
    override suspend fun getHotFallers(): List<MarketTicker> = overview.value.filter { it.change24h < 0 }.sortedBy { it.change24h }.take(3)
    override suspend fun getWatchlist(): List<MarketTicker> = remote.watchlist()
    override suspend fun getRiskSignals(symbol: String): List<RiskSignal> = remote.riskSignals(symbol)
    override suspend fun getPriceSeries(symbol: String): List<TokenPricePoint> = remote.priceSeries(symbol)
    override suspend fun getAiSummary(symbol: String): String = remote.aiSummary(symbol)
}
