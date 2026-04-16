package com.app.feature.market.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.app.common.components.*
import com.app.common.widgets.*
import com.app.core.theme.AppDimens
import com.app.core.ui.AppScaffold
import com.app.feature.market.components.*
import com.app.feature.market.viewmodel.MarketViewModel
import com.app.core.utils.Formatters

import androidx.compose.runtime.saveable.rememberSaveable

@Composable
fun MarketOverviewScreen(
    viewModel: MarketViewModel = viewModel(),
    onOpenTicker: (String) -> Unit = {},
) {
    val state by viewModel.uiState.collectAsState()
    var query by rememberSaveable { mutableStateOf("") }
    AppScaffold(title = "市场监控") { padding ->
        LazyColumn(modifier = Modifier.fillMaxSize().padding(padding).padding(18.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            item { SearchBar(value = query, onValueChange = { query = it }, placeholder = "搜索币种 / 合约 / 标签") }
            item { SectionHeader("异常波动") }
            items(state.abnormalMovers) { signal -> SignalCard(signal) }
            item { SectionHeader("热门上涨榜") }
            items(state.hotRisers.take(3).mapIndexed { index, ticker -> index to ticker }) { pair -> RankingListItem(pair.first + 1, pair.second) { onOpenTicker(pair.second.symbol) } }
            item { SectionHeader("热门下跌榜") }
            items(state.hotFallers.take(3).mapIndexed { index, ticker -> index to ticker }) { pair -> RankingListItem(pair.first + 1, pair.second) { onOpenTicker(pair.second.symbol) } }
            item { SectionHeader("我的监控") }
            items(state.watchlist) { ticker -> WatchlistRow(ticker) { onOpenTicker(ticker.symbol) } }
        }
    }
}
