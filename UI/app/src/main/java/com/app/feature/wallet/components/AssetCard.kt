package com.app.feature.wallet.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.app.common.components.GradientCard
import com.app.common.components.InfoRow
import com.app.common.components.StatusChip
import com.app.common.widgets.ChainPill
import com.app.common.widgets.TokenIcon
import com.app.core.theme.TextSecondary
import com.app.core.utils.Formatters
import com.app.data.model.Asset

@Composable
fun AssetCard(asset: Asset, onClick: () -> Unit) {
    GradientCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            TokenIcon(
                symbol = asset.symbol,
                chainId = asset.chainId,
                size = 42.dp,
            )
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Text(asset.symbol, style = MaterialTheme.typography.titleMedium)
                Text(asset.name, style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                ChainPill(chainId = asset.chainId)
            }
            StatusChip(text = Formatters.percent(asset.change24h), positive = asset.change24h >= 0)
        }
        Spacer(modifier = Modifier.height(10.dp))
        InfoRow("余额", "${asset.balance}")
        InfoRow("估值", Formatters.money(asset.balance * asset.priceUsd))
        InfoRow("网络", asset.chainId.replaceFirstChar { it.uppercase() })
        Spacer(modifier = Modifier.height(6.dp))
        SecondaryInlineAction("查看详情", onClick)
    }
}

@Composable
private fun SecondaryInlineAction(text: String, onClick: () -> Unit) {
    androidx.compose.material3.TextButton(onClick = onClick) {
        androidx.compose.material3.Text(text)
    }
}
