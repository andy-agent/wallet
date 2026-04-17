package com.app.feature.market.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.app.common.components.GlassOutlinePanel
import com.app.common.components.GradientCard
import com.app.common.components.SearchBar
import com.app.common.components.SectionHeader
import com.app.common.widgets.TokenIcon
import com.app.core.theme.AppDimens
import com.app.core.theme.MintPositive
import com.app.core.theme.RedNegative
import com.app.core.theme.TextSecondary
import com.app.core.ui.AppScaffold
import com.app.core.utils.Formatters
import com.app.data.model.MarketSignal
import com.app.data.model.MarketTicker
import com.app.feature.market.viewmodel.MarketViewModel

@Composable
fun MarketOverviewScreen(
    viewModel: MarketViewModel = viewModel(),
    onOpenTicker: (String) -> Unit = {},
) {
    val state by viewModel.uiState.collectAsState()
    var query by rememberSaveable { mutableStateOf("") }

    AppScaffold(title = "市场监控") { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = AppDimens.screenHorizontal, vertical = AppDimens.screenTop),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item {
                GradientCard(title = "市场雷达", subtitle = "资产图标、链路标签、异常波动统一展示") {
                    Text(
                        text = "今日观察：上涨 ${state.hotRisers.size} 只，下跌 ${state.hotFallers.size} 只，异常波动 ${state.abnormalMovers.size} 条。",
                        style = MaterialTheme.typography.bodyLarge,
                        color = TextSecondary,
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        MarketMetaPill(label = "监控池", value = state.watchlist.size.toString())
                        MarketMetaPill(label = "异常池", value = state.abnormalMovers.size.toString())
                    }
                }
            }
            item {
                SearchBar(
                    value = query,
                    onValueChange = { query = it },
                    placeholder = "搜索币种 / 合约 / 标签",
                )
            }
            item { SectionHeader("异常波动") }
            items(state.abnormalMovers.filter { it.matches(query) }, key = { it.id }) { signal ->
                MarketSignalRow(signal = signal, onClick = { onOpenTicker(signal.symbol) })
            }
            item { SectionHeader("热门上涨榜") }
            items(state.hotRisers.filter { it.matches(query) }.take(5), key = { it.symbol }) { ticker ->
                MarketTickerRow(
                    ticker = ticker,
                    emphasis = "热度上升",
                    onClick = { onOpenTicker(ticker.symbol) },
                )
            }
            item { SectionHeader("热门下跌榜") }
            items(state.hotFallers.filter { it.matches(query) }.take(5), key = { it.symbol }) { ticker ->
                MarketTickerRow(
                    ticker = ticker,
                    emphasis = "风险下探",
                    onClick = { onOpenTicker(ticker.symbol) },
                )
            }
            item { SectionHeader("我的监控") }
            items(state.watchlist.filter { it.matches(query) }, key = { it.symbol }) { ticker ->
                MarketTickerRow(
                    ticker = ticker,
                    emphasis = "关注资产",
                    onClick = { onOpenTicker(ticker.symbol) },
                )
            }
        }
    }
}

@Composable
private fun MarketSignalRow(
    signal: MarketSignal,
    onClick: () -> Unit,
) {
    val severityColor = when (signal.severity) {
        com.app.data.model.SignalSeverity.High -> RedNegative
        com.app.data.model.SignalSeverity.Medium -> androidx.compose.ui.graphics.Color(0xFFFFB44F)
        com.app.data.model.SignalSeverity.Low -> MintPositive
    }
    GlassOutlinePanel(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        radius = 24.dp,
        contentPadding = androidx.compose.foundation.layout.PaddingValues(14.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            TokenIcon(symbol = signal.symbol, size = 40.dp)
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(text = signal.symbol, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                Text(text = signal.title, style = MaterialTheme.typography.bodyMedium)
                Text(text = signal.description, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
            }
            Text(
                text = signal.severity.name,
                style = MaterialTheme.typography.labelLarge,
                color = severityColor,
                fontWeight = FontWeight.SemiBold,
            )
        }
    }
}

@Composable
private fun MarketTickerRow(
    ticker: MarketTicker,
    emphasis: String,
    onClick: () -> Unit,
) {
    val trendColor = if (ticker.change24h >= 0.0) MintPositive else RedNegative
    GlassOutlinePanel(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        radius = 24.dp,
        contentPadding = androidx.compose.foundation.layout.PaddingValues(14.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            TokenIcon(symbol = ticker.symbol, size = 42.dp)
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text(text = ticker.symbol, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                Text(text = ticker.name, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                Text(text = emphasis, style = MaterialTheme.typography.labelMedium, color = trendColor)
            }
            Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(text = Formatters.money(ticker.priceUsd), style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                Text(text = Formatters.percent(ticker.change24h), style = MaterialTheme.typography.labelLarge, color = trendColor)
            }
        }
    }
}

@Composable
private fun MarketMetaPill(
    label: String,
    value: String,
) {
    GlassOutlinePanel(
        radius = 999.dp,
        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 8.dp),
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
            Text(text = label, style = MaterialTheme.typography.labelMedium, color = TextSecondary)
            Text(text = value, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.SemiBold)
        }
    }
}

private fun MarketTicker.matches(query: String): Boolean {
    if (query.isBlank()) return true
    return symbol.contains(query, ignoreCase = true) || name.contains(query, ignoreCase = true)
}

private fun MarketSignal.matches(query: String): Boolean {
    if (query.isBlank()) return true
    return symbol.contains(query, ignoreCase = true) ||
        title.contains(query, ignoreCase = true) ||
        description.contains(query, ignoreCase = true)
}
