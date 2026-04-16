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
import com.app.data.model.Transaction
import com.app.core.utils.Formatters

@Composable
fun TransactionItem(item: Transaction, onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick).padding(vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text("${item.direction} · ${item.symbol}", style = MaterialTheme.typography.bodyMedium)
        Text(Formatters.money(item.fiatValue), style = MaterialTheme.typography.labelLarge)
    }
}
