package com.app.feature.market.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.AppGraph
import com.app.data.model.MarketSignal
import com.app.data.model.MarketTicker
import com.app.data.model.RiskSignal
import com.app.data.model.TokenPricePoint
import com.app.data.repository.MarketRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class MarketUiState(
    val overview: List<MarketTicker> = emptyList(),
    val abnormalMovers: List<MarketSignal> = emptyList(),
    val hotRisers: List<MarketTicker> = emptyList(),
    val hotFallers: List<MarketTicker> = emptyList(),
    val watchlist: List<MarketTicker> = emptyList(),
)

class MarketViewModel(
    private val repository: MarketRepository = AppGraph.marketRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(MarketUiState())
    val uiState: StateFlow<MarketUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            _uiState.value = MarketUiState(
                overview = repository.getOverview(),
                abnormalMovers = repository.getAbnormalMovers(),
                hotRisers = repository.getHotRisers(),
                hotFallers = repository.getHotFallers(),
                watchlist = repository.getWatchlist(),
            )
        }
    }

    fun ticker(symbol: String): MarketTicker? = uiState.value.overview.firstOrNull { it.symbol.equals(symbol, true) }
    fun loadRiskSignals(symbol: String, onDone: (List<RiskSignal>) -> Unit) { viewModelScope.launch { onDone(repository.getRiskSignals(symbol)) } }
    fun loadPriceSeries(symbol: String, onDone: (List<TokenPricePoint>) -> Unit) { viewModelScope.launch { onDone(repository.getPriceSeries(symbol)) } }
    fun loadAiSummary(symbol: String, onDone: (String) -> Unit) { viewModelScope.launch { onDone(repository.getAiSummary(symbol)) } }
}
