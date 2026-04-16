package com.app.common.widgets

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.clickable
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.app.common.components.StatusChip

@Composable
fun TabSwitcher(
    options: List<String>,
    selected: String,
    onSelect: (String) -> Unit,
) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        options.forEach { item ->
            androidx.compose.foundation.layout.Box(
                modifier = Modifier.clickable { onSelect(item) },
            ) {
                StatusChip(text = item, positive = if (item == selected) null else null)
            }
        }
    }
}
