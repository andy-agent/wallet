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


@Composable
fun MarketTickerDetailScreen(
    symbol: String,
    viewModel: MarketViewModel = viewModel(),
    onBack: () -> Unit = {},
) {
    val ticker = viewModel.ticker(symbol) ?: return
    var signals by remember { mutableStateOf(emptyList<com.app.data.model.RiskSignal>()) }
    var points by remember { mutableStateOf(emptyList<com.app.data.model.TokenPricePoint>()) }
    var aiSummary by remember { mutableStateOf("") }
    LaunchedEffect(symbol) {
        viewModel.loadRiskSignals(symbol) { signals = it }
        viewModel.loadPriceSeries(symbol) { points = it }
        viewModel.loadAiSummary(symbol) { aiSummary = it }
    }
    AppScaffold(title = "${ticker.symbol} / ${ticker.name}", onBack = onBack) { padding ->
        LazyColumn(modifier = Modifier.fillMaxSize().padding(padding).padding(18.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            item { GradientCard(title = Formatters.money(ticker.priceUsd), subtitle = Formatters.percent(ticker.change24h)) { InfoRow("市值", Formatters.compact(ticker.marketCapUsd)); InfoRow("24h", Formatters.compact(ticker.volume24hUsd)) } }
            item { TokenPriceChart(points) }
            item { SectionHeader("风险信号") }
            items(signals) { signal -> GradientCard(title = signal.title, subtitle = if (signal.positive) "积极" else "提醒") { Text(signal.description) } }
            item { GradientCard(title = "AI 判断", subtitle = "结合资金、情绪和波动率") { Text(aiSummary) } }
        }
    }
}
