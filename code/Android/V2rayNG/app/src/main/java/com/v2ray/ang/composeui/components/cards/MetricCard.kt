package com.v2ray.ang.composeui.components.cards

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.material3.Text
import com.v2ray.ang.composeui.components.chips.AppChip
import com.v2ray.ang.composeui.components.chips.AppChipTone
import com.v2ray.ang.composeui.theme.AppTheme

@Composable
fun MetricCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier,
    supportingText: String = "",
    badgeText: String? = null,
    badgeTone: AppChipTone = AppChipTone.Brand,
    emphasized: Boolean = false,
) {
    AppCard(
        modifier = modifier,
        variant = if (emphasized) AppCardVariant.Highlight else AppCardVariant.Default,
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.space8),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = title,
                    style = androidx.compose.material3.MaterialTheme.typography.labelMedium,
                    color = AppTheme.colors.textSecondary,
                )
                if (!badgeText.isNullOrBlank()) {
                    AppChip(text = badgeText, tone = badgeTone)
                }
            }
            Text(
                text = value,
                style = androidx.compose.material3.MaterialTheme.typography.displaySmall,
                color = AppTheme.colors.textPrimary,
            )
            if (supportingText.isNotBlank()) {
                Text(
                    text = supportingText,
                    style = androidx.compose.material3.MaterialTheme.typography.bodySmall,
                    color = AppTheme.colors.textTertiary,
                )
            }
        }
    }
}
