package com.app.data.repository

import com.app.data.model.MarketSignal
import com.app.data.model.MarketTicker
import com.app.data.model.RiskSignal
import com.app.data.model.TokenPricePoint
import kotlinx.coroutines.flow.StateFlow

interface MarketRepository {
    val overview: StateFlow<List<MarketTicker>>
    suspend fun getOverview(): List<MarketTicker>
    suspend fun getTicker(symbol: String): MarketTicker?
    suspend fun getAbnormalMovers(): List<MarketSignal>
    suspend fun getHotRisers(): List<MarketTicker>
    suspend fun getHotFallers(): List<MarketTicker>
    suspend fun getWatchlist(): List<MarketTicker>
    suspend fun getRiskSignals(symbol: String): List<RiskSignal>
    suspend fun getPriceSeries(symbol: String): List<TokenPricePoint>
    suspend fun getAiSummary(symbol: String): String
}
