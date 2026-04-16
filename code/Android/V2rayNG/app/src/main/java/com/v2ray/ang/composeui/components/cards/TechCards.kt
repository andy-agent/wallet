package com.v2ray.ang.composeui.components.cards

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.v2ray.ang.composeui.components.chips.AppChip
import com.v2ray.ang.composeui.components.chips.AppChipTone
import com.v2ray.ang.composeui.theme.AppTheme

@Composable
fun TechCard(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    AppCard(modifier = modifier, variant = AppCardVariant.Default) {
        content()
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
    AppCard(
        modifier = modifier.fillMaxWidth(),
        variant = AppCardVariant.Highlight,
    ) {
        Column(
            modifier = Modifier
                .background(
                    brush = AppTheme.gradients.cardGlowGradient,
                )
                .padding(AppTheme.spacing.space4),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text(title, style = MaterialTheme.typography.titleMedium)
                if (!accent.isNullOrEmpty()) {
                    AppChip(text = accent, tone = AppChipTone.Success)
                }
            }
            Text(
                value,
                style = MaterialTheme.typography.headlineLarge,
            )
            Text(
                subtitle,
                color = AppTheme.colors.textSecondary,
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
            Column(verticalArrangement = Arrangement.spacedBy(4.dp), modifier = Modifier) {
                Text(title, style = MaterialTheme.typography.titleMedium)
                Text(
                    summary,
                    style = MaterialTheme.typography.bodySmall,
                    color = AppTheme.colors.textSecondary,
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
    AppCard(
        variant = AppCardVariant.Elevated,
        contentPadding = AppTheme.spacing.space12,
    ) {
        Column {
            Text(label, style = MaterialTheme.typography.bodySmall, color = AppTheme.colors.textSecondary)
            Text(value, style = MaterialTheme.typography.titleMedium)
        }
    }
}
