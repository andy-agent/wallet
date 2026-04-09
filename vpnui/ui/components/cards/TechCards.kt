package com.cryptovpn.ui.components.cards

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.weight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import com.cryptovpn.ui.theme.AuroraPurple
import com.cryptovpn.ui.theme.DividerLight
import com.cryptovpn.ui.theme.ElectricBlue
import com.cryptovpn.ui.theme.ElectricCyan
import com.cryptovpn.ui.theme.LayerWhite
import com.cryptovpn.ui.theme.SignalGreen
import com.cryptovpn.ui.theme.SurfaceCloud
import com.cryptovpn.ui.theme.TextMuted

@Composable
fun TechCard(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Surface(
        modifier = modifier,
        color = LayerWhite.copy(alpha = 0.86f),
        shape = RoundedCornerShape(26.dp),
        border = BorderStroke(1.dp, DividerLight),
        tonalElevation = 0.dp,
        shadowElevation = 10.dp,
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            content()
        }
    }
}

@Composable
fun GradientHeroCard(
    title: String,
    value: String,
    subtitle: String,
    modifier: Modifier = Modifier,
    accent: String? = null,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = LayerWhite.copy(alpha = 0.90f),
        shape = RoundedCornerShape(30.dp),
        tonalElevation = 0.dp,
        shadowElevation = 14.dp,
    ) {
        Column(
            modifier = Modifier
                .background(
                    Brush.linearGradient(
                        listOf(
                            ElectricBlue.copy(alpha = 0.10f),
                            ElectricCyan.copy(alpha = 0.08f),
                            AuroraPurple.copy(alpha = 0.10f),
                        ),
                    ),
                )
                .padding(22.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text(title, style = MaterialTheme.typography.titleMedium)
                if (!accent.isNullOrEmpty()) {
                    Text(
                        text = accent,
                        color = SignalGreen,
                        style = MaterialTheme.typography.labelLarge,
                    )
                }
            }
            Text(
                value,
                style = MaterialTheme.typography.headlineLarge,
            )
            Text(
                subtitle,
                color = TextMuted,
                style = MaterialTheme.typography.bodySmall,
            )
        }
    }
}

@Composable
fun SettingTileCard(
    title: String,
    summary: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    TechCard(modifier = modifier) {
        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp), modifier = Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.titleMedium)
                Text(
                    summary,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMuted,
                )
            }
            Spacer(modifier = Modifier.padding(horizontal = 4.dp))
            Switch(checked = checked, onCheckedChange = onCheckedChange)
        }
    }
}

@Composable
fun MiniMetricPill(
    label: String,
    value: String,
) {
    Surface(
        color = SurfaceCloud.copy(alpha = 0.85f),
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(1.dp, DividerLight),
    ) {
        Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)) {
            Text(label, style = MaterialTheme.typography.bodySmall, color = TextMuted)
            Text(value, style = MaterialTheme.typography.titleMedium)
        }
    }
}
