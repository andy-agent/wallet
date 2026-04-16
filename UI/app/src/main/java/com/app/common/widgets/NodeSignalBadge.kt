package com.app.common.widgets

import androidx.compose.runtime.Composable
import com.app.common.components.StatusChip

@Composable
fun NodeSignalBadge(latencyMs: Int) {
    val positive = latencyMs < 80
    StatusChip(text = "$latencyMs ms", positive = positive)
}
