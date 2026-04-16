package com.v2ray.ang.composeui.components.growth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.material3.Text
import com.v2ray.ang.composeui.components.chips.AppChip
import com.v2ray.ang.composeui.components.chips.AppChipTone
import com.v2ray.ang.composeui.components.cards.AppCard
import com.v2ray.ang.composeui.components.cards.AppCardVariant
import com.v2ray.ang.composeui.theme.AppTheme

data class AppHeroStat(
    val label: String,
    val value: String,
)

@Composable
fun AppHeroValueCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier,
    supportingText: String = "",
    highlight: String? = null,
    stats: List<AppHeroStat> = emptyList(),
) {
    AppCard(
        modifier = modifier,
        variant = AppCardVariant.Highlight,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = AppTheme.gradients.heroGlowGradient,
                    shape = RoundedCornerShape(AppTheme.shapes.radiusXl),
                )
                .padding(AppTheme.spacing.cardPadding),
            verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.space12),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.space8),
                ) {
                    Text(
                        text = title,
                        style = androidx.compose.material3.MaterialTheme.typography.labelLarge,
                        color = AppTheme.colors.textOnPrimary.copy(alpha = 0.78f),
                    )
                    Text(
                        text = value,
                        style = androidx.compose.material3.MaterialTheme.typography.displaySmall,
                        color = AppTheme.colors.textOnPrimary,
                    )
                    if (supportingText.isNotBlank()) {
                        Text(
                            text = supportingText,
                            style = androidx.compose.material3.MaterialTheme.typography.bodySmall,
                            color = AppTheme.colors.textOnPrimary.copy(alpha = 0.88f),
                        )
                    }
                }
                if (!highlight.isNullOrBlank()) {
                    AppChip(
                        text = highlight,
                        tone = AppChipTone.Info,
                    )
                }
            }
            val visibleStats = stats.filter { it.label.isNotBlank() || it.value.isNotBlank() }
            if (visibleStats.isNotEmpty()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.itemGap),
                ) {
                    visibleStats.take(2).forEach { stat ->
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .background(
                                    color = AppTheme.colors.surfaceGlowWeak,
                                    shape = RoundedCornerShape(AppTheme.shapes.radiusMd),
                                )
                                .padding(horizontal = AppTheme.spacing.space12, vertical = AppTheme.spacing.space12),
                            verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.space4),
                        ) {
                            Text(
                                text = stat.label,
                                style = androidx.compose.material3.MaterialTheme.typography.labelSmall,
                                color = AppTheme.colors.textOnPrimary.copy(alpha = 0.78f),
                            )
                            Text(
                                text = stat.value,
                                style = androidx.compose.material3.MaterialTheme.typography.titleMedium,
                                color = AppTheme.colors.textOnPrimary,
                            )
                        }
                    }
                }
            }
        }
    }
}
