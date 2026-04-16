package com.app.feature.wallet.components

import androidx.compose.runtime.Composable
import com.app.common.components.GradientCard
import com.app.common.components.InfoRow
import com.app.data.model.Asset
import com.app.core.utils.Formatters

@Composable
fun AssetCard(asset: Asset, onClick: () -> Unit) {
    GradientCard(title = asset.symbol, subtitle = asset.name) {
        InfoRow("余额", "${asset.balance}")
        InfoRow("估值", Formatters.money(asset.balance * asset.priceUsd))
        SecondaryInlineAction("查看详情", onClick)
    }
}

@Composable
private fun SecondaryInlineAction(text: String, onClick: () -> Unit) {
    androidx.compose.material3.TextButton(onClick = onClick) {
        androidx.compose.material3.Text(text)
    }
}
