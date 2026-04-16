package com.app.common.widgets

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.app.common.components.StatusChip

@Composable
fun NetworkBadge(text: String) {
    StatusChip(text)
}
