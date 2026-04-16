package com.app.feature.wallet.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.app.common.components.*
import com.app.common.widgets.*
import com.app.core.theme.AppDimens
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
        LazyColumn(modifier = Modifier.fillMaxSize().padding(padding).padding(18.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            item { GradientCard(title = "${asset.symbol} / ${asset.name}", subtitle = Formatters.money(asset.priceUsd)) {
                InfoRow("余额", asset.balance.toString())
                InfoRow("24h", Formatters.percent(asset.change24h))
            } }
            item { TokenPriceChart(points) }
            item { Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) { MetricPill("估值", Formatters.money(asset.balance * asset.priceUsd)); MetricPill("网络", asset.chainId); MetricPill("地址", asset.address.take(8) + "…") } }
            item { Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) { SecondaryButton(text = "发送", onClick = onSend, modifier = Modifier.weight(1f)); PrimaryButton(text = "收款", onClick = onReceive, modifier = Modifier.weight(1f)) } }
            item { SectionHeader("交易记录") }
            items(viewModel.transactionsOf(symbol)) { item -> TransactionItem(item, onClick = { }) }
        }
    }
}
