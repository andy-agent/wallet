package com.v2ray.ang.composeui.components.growth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.v2ray.ang.composeui.components.chips.AppChip
import com.v2ray.ang.composeui.components.chips.AppChipTone
import com.v2ray.ang.composeui.components.cards.AppCard
import com.v2ray.ang.composeui.components.cards.AppCardVariant
import com.v2ray.ang.composeui.theme.AppTheme
import com.v2ray.ang.composeui.theme.tokens.OverviewBaselineTokens

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
    val baseline = OverviewBaselineTokens.primary
    val heroShape = RoundedCornerShape(baseline.heroRadius)
    AppCard(
        modifier = modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = baseline.heroSummaryMinHeight),
        variant = AppCardVariant.Highlight,
        shape = heroShape,
        contentPadding = 0.dp,
        shadowElevation = baseline.heroShadow,
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .defaultMinSize(minHeight = baseline.heroSummaryMinHeight)
                .background(
                    brush = AppTheme.gradients.heroGlowGradient,
                    shape = heroShape,
                )
                .padding(horizontal = baseline.heroPadding, vertical = baseline.heroPadding),
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(baseline.sectionGap),
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
                            style = AppTheme.typography.titleL,
                            color = AppTheme.colors.textOnPrimary.copy(alpha = 0.82f),
                        )
                        Text(
                            text = value,
                            style = AppTheme.typography.metricL,
                            color = AppTheme.colors.textOnPrimary,
                        )
                        if (supportingText.isNotBlank()) {
                            Text(
                                text = supportingText,
                                style = AppTheme.typography.bodyM,
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
                        horizontalArrangement = Arrangement.spacedBy(baseline.actionButtonGap),
                    ) {
                        visibleStats.take(2).forEach { stat ->
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .defaultMinSize(minHeight = baseline.metricSurfaceMinHeight)
                                    .background(
                                        color = AppTheme.colors.surfaceGlowWeak,
                                        shape = RoundedCornerShape(baseline.metricSurfaceRadius),
                                    )
                                    .padding(horizontal = AppTheme.spacing.space12, vertical = AppTheme.spacing.space8),
                                verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.space4),
                            ) {
                                Text(
                                    text = stat.label,
                                    style = AppTheme.typography.labelS,
                                    color = AppTheme.colors.textOnPrimary.copy(alpha = 0.78f),
                                )
                                Text(
                                    text = stat.value,
                                    style = AppTheme.typography.titleM,
                                    color = AppTheme.colors.textOnPrimary,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
