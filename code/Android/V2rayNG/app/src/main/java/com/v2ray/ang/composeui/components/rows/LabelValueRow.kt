package com.v2ray.ang.composeui.components.rows

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.material3.Text
import com.v2ray.ang.composeui.components.chips.AppChip
import com.v2ray.ang.composeui.components.chips.AppChipTone
import com.v2ray.ang.composeui.theme.AppTheme

enum class LabelValueRowLayoutMode {
    Inline,
    Stacked,
}

enum class LabelValueDisplayMode {
    Full,
    LongCompact,
}

@Composable
fun LabelValueRow(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    supportingText: String = "",
    badgeText: String? = null,
    badgeTone: AppChipTone = AppChipTone.Neutral,
    layoutMode: LabelValueRowLayoutMode = LabelValueRowLayoutMode.Inline,
    valueDisplayMode: LabelValueDisplayMode = LabelValueDisplayMode.Full,
) {
    val displayValue = when (valueDisplayMode) {
        LabelValueDisplayMode.Full -> value
        LabelValueDisplayMode.LongCompact -> compactLongValue(value)
    }

    if (layoutMode == LabelValueRowLayoutMode.Stacked) {
        Column(
            modifier = modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.space8),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top,
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.space4),
                ) {
                    Text(
                        text = label,
                        style = androidx.compose.material3.MaterialTheme.typography.labelMedium,
                        color = AppTheme.colors.textSecondary,
                    )
                    if (supportingText.isNotBlank()) {
                        Text(
                            text = supportingText,
                            style = androidx.compose.material3.MaterialTheme.typography.bodySmall,
                            color = AppTheme.colors.textTertiary,
                        )
                    }
                }
                if (!badgeText.isNullOrBlank()) {
                    AppChip(text = badgeText, tone = badgeTone)
                }
            }
            Text(
                text = displayValue,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = AppTheme.colors.bgSubtle,
                        shape = RoundedCornerShape(AppTheme.shapes.radiusMd),
                    )
                    .padding(
                        horizontal = AppTheme.spacing.space12,
                        vertical = AppTheme.spacing.space12,
                    ),
                style = androidx.compose.material3.MaterialTheme.typography.bodyMedium,
                color = AppTheme.colors.textPrimary,
                maxLines = if (valueDisplayMode == LabelValueDisplayMode.LongCompact) 1 else 3,
                overflow = TextOverflow.Ellipsis,
            )
        }
        return
    }

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.space12),
        verticalAlignment = Alignment.Top,
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.space4),
        ) {
            Text(
                text = label,
                style = androidx.compose.material3.MaterialTheme.typography.labelMedium,
                color = AppTheme.colors.textSecondary,
            )
            if (supportingText.isNotBlank()) {
                Text(
                    text = supportingText,
                    style = androidx.compose.material3.MaterialTheme.typography.bodySmall,
                    color = AppTheme.colors.textTertiary,
                )
            }
        }
        Column(
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.space4),
        ) {
            Text(
                text = displayValue,
                style = androidx.compose.material3.MaterialTheme.typography.titleMedium,
                color = AppTheme.colors.textPrimary,
                textAlign = TextAlign.End,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            if (!badgeText.isNullOrBlank()) {
                AppChip(text = badgeText, tone = badgeTone)
            }
        }
    }
}

private fun compactLongValue(value: String): String {
    val normalized = value.trim()
    if (normalized.length <= 22) {
        return normalized
    }
    val leading = normalized.take(10)
    val trailing = normalized.takeLast(8)
    return "$leading...$trailing"
}
