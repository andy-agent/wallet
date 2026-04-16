package com.app.common.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.app.core.theme.MintPositive
import com.app.core.theme.RedNegative

@Composable
fun StatusChip(
    text: String,
    positive: Boolean? = null,
) {
    val color = when (positive) {
        true -> MintPositive
        false -> RedNegative
        null -> MaterialTheme.colorScheme.primary
    }
    Surface(
        color = color.copy(alpha = 0.12f),
        shape = RoundedCornerShape(100.dp),
    ) {
        Text(
            text,
            modifier = androidx.compose.ui.Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            style = MaterialTheme.typography.labelLarge,
            color = color,
        )
    }
}
