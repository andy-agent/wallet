package com.app.feature.wallet.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.app.data.model.Asset
import com.app.core.utils.Formatters

@Composable
fun TokenItem(asset: Asset, onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick).padding(vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text("${asset.symbol} · ${asset.chainId}", style = MaterialTheme.typography.labelLarge)
        Text(Formatters.money(asset.balance * asset.priceUsd), style = MaterialTheme.typography.labelLarge)
    }
}
