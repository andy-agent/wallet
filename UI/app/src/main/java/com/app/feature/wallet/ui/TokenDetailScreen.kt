package com.app.feature.wallet.ui

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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.app.common.components.GlassOutlinePanel
import com.app.common.components.GradientCard
import com.app.common.components.InfoRow
import com.app.common.components.PrimaryButton
import com.app.common.components.SecondaryButton
import com.app.common.components.SectionHeader
import com.app.common.widgets.ChainPill
import com.app.common.widgets.TokenIcon
import com.app.common.widgets.TokenPriceChart
import com.app.core.theme.AppDimens
import com.app.core.theme.MintPositive
import com.app.core.theme.RedNegative
import com.app.core.theme.TextSecondary
import com.app.core.ui.AppScaffold
import com.app.core.utils.Formatters
import com.app.feature.wallet.components.TransactionItem
import com.app.feature.wallet.viewmodel.WalletViewModel
import com.app.data.model.TokenPricePoint

@Composable
fun TokenDetailScreen(
    symbol: String,
    viewModel: WalletViewModel = viewModel(),
    onBack: () -> Unit = {},
    onSend: () -> Unit = {},
    onReceive: () -> Unit = {},
) {
    val asset = viewModel.token(symbol) ?: return
    var points by remember(symbol) { mutableStateOf(emptyList<TokenPricePoint>()) }

    LaunchedEffect(symbol) {
        viewModel.priceSeries(symbol) { points = it }
    }

    AppScaffold(title = "资产详情", onBack = onBack) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = AppDimens.screenHorizontal, vertical = AppDimens.screenTop),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item {
                GradientCard(title = "${asset.symbol} / ${asset.name}", subtitle = "链上资产明细") {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            TokenIcon(symbol = asset.symbol, chainId = asset.chainId, size = 54.dp)
                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text(
                                    text = Formatters.money(asset.priceUsd),
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.Bold,
                                )
                                ChainPill(chainId = asset.chainId)
                            }
                        }
                        Text(
                            text = Formatters.percent(asset.change24h),
                            style = MaterialTheme.typography.titleMedium,
                            color = if (asset.change24h >= 0.0) MintPositive else RedNegative,
                            fontWeight = FontWeight.SemiBold,
                        )
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                        TokenMetric(label = "持仓数量", value = String.format("%.4f %s", asset.balance, asset.symbol), modifier = Modifier.weight(1f))
                        TokenMetric(label = "持仓估值", value = Formatters.money(asset.balance * asset.priceUsd), modifier = Modifier.weight(1f))
                    }
                }
            }
            item {
                TokenPriceChart(
                    points = points,
                    symbol = asset.symbol,
                    chainId = asset.chainId,
                    title = asset.name,
                )
            }
            item {
                GradientCard(title = "链上信息", subtitle = "网络、地址与结算入口") {
                    InfoRow("网络", asset.chainId)
                    InfoRow("钱包地址", asset.address.take(10) + "..." + asset.address.takeLast(6))
                    InfoRow("24h 变动", Formatters.percent(asset.change24h))
                }
            }
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                    SecondaryButton(text = "发送", onClick = onSend, modifier = Modifier.weight(1f))
                    PrimaryButton(text = "收款", onClick = onReceive, modifier = Modifier.weight(1f))
                }
            }
            item { SectionHeader("交易记录") }
            items(viewModel.transactionsOf(symbol)) { item ->
                TransactionItem(item, onClick = { })
            }
        }
    }
}

@Composable
private fun TokenMetric(
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
