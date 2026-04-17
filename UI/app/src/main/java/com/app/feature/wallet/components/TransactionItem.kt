package com.app.feature.wallet.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.app.core.theme.BorderSubtle
import com.app.core.theme.CardGlassStrong
import com.app.core.theme.TextSecondary
import com.app.data.model.Transaction
import com.app.core.utils.Formatters

@Composable
fun TransactionItem(item: Transaction, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, BorderSubtle, RoundedCornerShape(22.dp))
            .background(CardGlassStrong.copy(alpha = 0.7f), RoundedCornerShape(22.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Column {
            Text("${item.direction} · ${item.symbol}", style = MaterialTheme.typography.bodyMedium)
            Text(Formatters.dateTime(item.timestamp), style = MaterialTheme.typography.bodySmall, color = TextSecondary)
        }
        Column(horizontalAlignment = androidx.compose.ui.Alignment.End) {
            Text(Formatters.money(item.fiatValue), style = MaterialTheme.typography.labelLarge)
            Text(item.status.name, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
        }
    }
}
