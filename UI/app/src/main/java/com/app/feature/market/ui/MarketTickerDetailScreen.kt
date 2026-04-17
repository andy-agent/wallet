package com.app.feature.market.ui

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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.app.common.components.GlassOutlinePanel
import com.app.common.components.GradientCard
import com.app.common.components.InfoRow
import com.app.common.components.SectionHeader
import com.app.common.widgets.ChainPill
import com.app.common.widgets.TokenIcon
import com.app.common.widgets.TokenPriceChart
import com.app.common.widgets.chainIdForSymbol
import com.app.core.theme.AppDimens
import com.app.core.theme.MintPositive
import com.app.core.theme.RedNegative
import com.app.core.theme.TextSecondary
import com.app.core.ui.AppScaffold
import com.app.core.utils.Formatters
import com.app.data.model.RiskSignal
import com.app.data.model.TokenPricePoint
import com.app.feature.market.viewmodel.MarketViewModel

@Composable
fun MarketTickerDetailScreen(
    symbol: String,
    viewModel: MarketViewModel = viewModel(),
    onBack: () -> Unit = {},
) {
    val ticker = viewModel.ticker(symbol) ?: return
    var signals by remember(symbol) { mutableStateOf(emptyList<RiskSignal>()) }
    var points by remember(symbol) { mutableStateOf(emptyList<TokenPricePoint>()) }
    var aiSummary by remember(symbol) { mutableStateOf("") }

    LaunchedEffect(symbol) {
        viewModel.loadRiskSignals(symbol) { signals = it }
        viewModel.loadPriceSeries(symbol) { points = it }
        viewModel.loadAiSummary(symbol) { aiSummary = it }
    }

    AppScaffold(title = "${ticker.symbol} / ${ticker.name}", onBack = onBack) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = AppDimens.screenHorizontal, vertical = AppDimens.screenTop),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item {
                GradientCard(
                    title = "市场详情",
                    subtitle = "统一指数图表容器 + 资产图标体系",
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            TokenIcon(symbol = ticker.symbol, chainId = chainIdForSymbol(ticker.symbol), size = 54.dp)
                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text(
                                    text = Formatters.money(ticker.priceUsd),
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.Bold,
                                )
                                ChainPill(chainId = chainIdForSymbol(ticker.symbol))
                            }
                        }
                        Text(
                            text = Formatters.percent(ticker.change24h),
                            style = MaterialTheme.typography.titleMedium,
                            color = if (ticker.change24h >= 0.0) MintPositive else RedNegative,
                            fontWeight = FontWeight.SemiBold,
                        )
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                        TickerMeta(label = "市值", value = Formatters.compact(ticker.marketCapUsd), modifier = Modifier.weight(1f))
                        TickerMeta(label = "24h 成交", value = Formatters.compact(ticker.volume24hUsd), modifier = Modifier.weight(1f))
                    }
                }
            }
            item {
                TokenPriceChart(
                    points = points,
                    symbol = ticker.symbol,
                    chainId = chainIdForSymbol(ticker.symbol),
                    title = ticker.name,
                )
            }
            item {
                GradientCard(title = "盘口概览", subtitle = "核心市场字段") {
                    InfoRow("币种", ticker.symbol)
                    InfoRow("名称", ticker.name)
                    InfoRow("市值", Formatters.compact(ticker.marketCapUsd))
                    InfoRow("24h 成交", Formatters.compact(ticker.volume24hUsd))
                }
            }
            item { SectionHeader("风险信号") }
            items(signals, key = { it.id }) { signal ->
                MarketRiskCard(signal = signal)
            }
            item {
                GradientCard(title = "AI 判断", subtitle = "结合资金、情绪和波动率") {
                    Text(text = aiSummary, style = MaterialTheme.typography.bodyLarge)
                }
            }
        }
    }
}

@Composable
private fun TickerMeta(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    GlassOutlinePanel(
        modifier = modifier,
        radius = 22.dp,
        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 14.dp, vertical = 12.dp),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(text = label, style = MaterialTheme.typography.labelMedium, color = TextSecondary)
            Text(text = value, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
private fun MarketRiskCard(
    signal: RiskSignal,
) {
    val accentColor = if (signal.positive) MintPositive else RedNegative
    GlassOutlinePanel(
        modifier = Modifier.fillMaxWidth(),
        radius = 22.dp,
        contentPadding = androidx.compose.foundation.layout.PaddingValues(14.dp),
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
            TokenIcon(symbol = if (signal.positive) "SOL" else "BTC", size = 38.dp)
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(text = signal.title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                Text(text = signal.description, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
            }
            Text(
                text = if (signal.positive) "利多" else "提醒",
                style = MaterialTheme.typography.labelLarge,
                color = accentColor,
                fontWeight = FontWeight.SemiBold,
            )
        }
    }
}
