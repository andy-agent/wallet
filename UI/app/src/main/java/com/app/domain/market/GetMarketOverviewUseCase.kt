package com.app.domain.market

import com.app.AppGraph
import com.app.data.repository.MarketRepository

class GetMarketOverviewUseCase(
    private val repository: MarketRepository = AppGraph.marketRepository,
) {
    suspend operator fun invoke() = repository.getOverview()
}
