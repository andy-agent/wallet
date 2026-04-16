package com.app.feature.vpn.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.app.data.model.CommissionRecord
import com.app.core.utils.Formatters

@Composable
fun CommissionRow(item: CommissionRecord) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(item.sourceTitle, style = MaterialTheme.typography.bodyMedium)
        Text(Formatters.money(item.amountUsd), style = MaterialTheme.typography.labelLarge)
    }
}
