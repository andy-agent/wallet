package com.app.feature.wallet.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.app.common.components.GradientCard
import com.app.common.components.InfoRow
import com.app.common.components.StatusChip
import com.app.data.model.Asset
import com.app.core.utils.Formatters

@Composable
fun AssetCard(asset: Asset, onClick: () -> Unit) {
    GradientCard(title = asset.symbol, subtitle = asset.name) {
        StatusChip(text = Formatters.percent(asset.change24h), positive = asset.change24h >= 0)
        Spacer(modifier = androidx.compose.ui.Modifier.height(10.dp))
        InfoRow("余额", "${asset.balance}")
        InfoRow("估值", Formatters.money(asset.balance * asset.priceUsd))
        InfoRow("网络", asset.chainId)
        Spacer(modifier = androidx.compose.ui.Modifier.height(6.dp))
        SecondaryInlineAction("查看详情", onClick)
    }
}

@Composable
private fun SecondaryInlineAction(text: String, onClick: () -> Unit) {
    androidx.compose.material3.TextButton(onClick = onClick) {
        androidx.compose.material3.Text(text)
    }
}
