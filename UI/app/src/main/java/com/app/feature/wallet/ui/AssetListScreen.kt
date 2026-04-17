package com.app.feature.wallet.ui

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.app.common.components.GlassOutlinePanel
import com.app.common.components.GradientCard
import com.app.common.components.SectionHeader
import com.app.common.widgets.ChainPill
import com.app.common.widgets.TokenIcon
import com.app.core.theme.AppDimens
import com.app.core.theme.MintPositive
import com.app.core.theme.RedNegative
import com.app.core.theme.TextSecondary
import com.app.core.ui.AppScaffold
import com.app.core.utils.Formatters
import com.app.data.model.Asset
import com.app.feature.wallet.viewmodel.WalletViewModel

@Composable
fun AssetListScreen(
    viewModel: WalletViewModel = viewModel(),
    onBack: () -> Unit = {},
    onOpenToken: (String) -> Unit = {},
) {
    val state by viewModel.uiState.collectAsState()
    val totalValue = state.assets.sumOf { it.balance * it.priceUsd }
    val gainers = state.assets.count { it.change24h >= 0.0 }

    AppScaffold(title = "钱包首页", onBack = onBack) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = AppDimens.screenHorizontal, vertical = AppDimens.screenTop),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            item {
                GradientCard(
                    title = "多链资产总览",
                    subtitle = "统一展示 token 与 chain 图标体系",
                ) {
                    Text(
                        text = Formatters.money(totalValue),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        AssetMetaPill("资产数量", state.assets.size.toString())
                        AssetMetaPill("上涨资产", gainers.toString())
                    }
                }
            }
            item { SectionHeader("多链资产") }
            items(state.assets, key = { it.id }) { asset ->
                AssetListRow(asset = asset, onClick = { onOpenToken(asset.symbol) })
            }
        }
    }
}

@Composable
private fun AssetListRow(
    asset: Asset,
    onClick: () -> Unit,
) {
    val accentColor = if (asset.change24h >= 0.0) MintPositive else RedNegative
    GlassOutlinePanel(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        radius = 26.dp,
        contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            TokenIcon(
                symbol = asset.symbol,
                chainId = asset.chainId,
                size = 46.dp,
            )
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text(
                            text = asset.symbol,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                        )
                        Text(
                            text = asset.name,
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary,
                        )
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = Formatters.money(asset.balance * asset.priceUsd),
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold,
                        )
                        Text(
                            text = Formatters.percent(asset.change24h),
                            style = MaterialTheme.typography.labelLarge,
                            color = accentColor,
                        )
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    ChainPill(chainId = asset.chainId)
                    Text(
                        text = "${String.format("%.4f", asset.balance)} ${asset.symbol}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary,
                    )
                }
            }
        }
    }
}

@Composable
private fun AssetMetaPill(
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
