package com.app.data.remote.api

import com.app.data.remote.dto.MarketTickerDto

interface MarketApi {
    suspend fun getOverview(): List<MarketTickerDto>
    suspend fun getTicker(symbol: String): MarketTickerDto?
}
