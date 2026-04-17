package com.app.common.widgets

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.app.core.theme.CardGlassStrong
import com.app.core.theme.TextSecondary

@Composable
fun MetricPill(label: String, value: String) {
    Surface(
        color = CardGlassStrong.copy(alpha = 0.66f),
        shape = RoundedCornerShape(22.dp),
        shadowElevation = 2.dp,
    ) {
        Column(Modifier.padding(horizontal = 14.dp, vertical = 10.dp)) {
            Text(label, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
            Text(value, style = MaterialTheme.typography.labelLarge)
        }
    }
}
