package com.app.common.widgets

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.PowerSettingsNew
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.app.core.theme.BluePrimary
import com.app.core.theme.BlueSecondary
import com.app.core.theme.GlowCyan
import com.app.core.theme.MintPositive

@Composable
fun VpnPowerButton(
    active: Boolean,
    onClick: () -> Unit,
) {
    Box(
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .offset(y = 6.dp)
                .size(126.dp)
                .blur(26.dp)
                .background(
                    if (active) MintPositive.copy(alpha = 0.28f) else GlowCyan,
                    CircleShape,
                ),
        )
        Box(
        modifier = Modifier
            .size(108.dp)
            .background(
                Brush.linearGradient(
                    if (active) listOf(MintPositive, BlueSecondary) else listOf(BlueSecondary, BluePrimary),
                ),
                CircleShape,
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
            Box(
                modifier = Modifier
                    .size(82.dp)
                    .background(Color.White.copy(alpha = 0.18f), CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.Outlined.PowerSettingsNew, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimary)
            }
        }
    }
}
