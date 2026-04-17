package com.app.feature.wallet.ui

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.app.common.components.*
import com.app.common.widgets.*
import com.app.core.ui.AppScaffold
import com.app.feature.wallet.components.*
import com.app.feature.wallet.viewmodel.WalletViewModel
import com.app.core.utils.Formatters

@Composable
fun TokenDetailScreen(
    symbol: String,
    viewModel: WalletViewModel = viewModel(),
    onBack: () -> Unit = {},
    onSend: () -> Unit = {},
    onReceive: () -> Unit = {},
) {
    val asset = viewModel.token(symbol) ?: return
    var points by remember { mutableStateOf(emptyList<com.app.data.model.TokenPricePoint>()) }
    LaunchedEffect(symbol) { viewModel.priceSeries(symbol) { points = it } }
    AppScaffold(title = "资产详情", onBack = onBack) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 18.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item {
                GradientCard(title = "${asset.symbol} / ${asset.name}", subtitle = asset.chainId) {
                    Text(Formatters.money(asset.priceUsd), style = MaterialTheme.typography.headlineLarge)
                    Spacer(Modifier.height(8.dp))
                    StatusChip(text = Formatters.percent(asset.change24h), positive = asset.change24h >= 0)
                    Spacer(Modifier.height(12.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        MetricPill("余额", asset.balance.toString())
                        MetricPill("估值", Formatters.money(asset.balance * asset.priceUsd))
                    }
                }
            }
            item { TokenPriceChart(points) }
            item {
                GradientCard(title = "链上信息", subtitle = "地址与网络") {
                    InfoRow("网络", asset.chainId)
                    InfoRow("地址", asset.address.take(10) + "…")
                }
            }
            item { Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) { SecondaryButton(text = "发送", onClick = onSend, modifier = Modifier.weight(1f)); PrimaryButton(text = "收款", onClick = onReceive, modifier = Modifier.weight(1f)) } }
            item { SectionHeader("交易记录") }
            items(viewModel.transactionsOf(symbol)) { item -> TransactionItem(item, onClick = { }) }
        }
    }
}
