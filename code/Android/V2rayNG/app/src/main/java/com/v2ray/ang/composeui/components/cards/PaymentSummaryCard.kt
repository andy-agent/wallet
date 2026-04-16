package com.v2ray.ang.composeui.components.cards

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.material3.Text
import com.v2ray.ang.composeui.components.chips.AppChipTone
import com.v2ray.ang.composeui.components.rows.LabelValueRow
import com.v2ray.ang.composeui.theme.AppTheme

data class PaymentSummaryField(
    val label: String,
    val value: String,
    val supportingText: String = "",
    val badgeText: String? = null,
    val badgeTone: AppChipTone = AppChipTone.Neutral,
)

@Composable
fun PaymentSummaryCard(
    title: String,
    fields: List<PaymentSummaryField>,
    modifier: Modifier = Modifier,
    subtitle: String = "",
) {
    AppCard(modifier = modifier, variant = AppCardVariant.Elevated) {
        Column(verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.space12)) {
            Text(
                text = title,
                style = androidx.compose.material3.MaterialTheme.typography.titleMedium,
                color = AppTheme.colors.textPrimary,
            )
            if (subtitle.isNotBlank()) {
                Text(
                    text = subtitle,
                    style = androidx.compose.material3.MaterialTheme.typography.bodySmall,
                    color = AppTheme.colors.textSecondary,
                )
            }
            fields.forEach { field ->
                LabelValueRow(
                    label = field.label,
                    value = field.value,
                    supportingText = field.supportingText,
                    badgeText = field.badgeText,
                    badgeTone = field.badgeTone,
                )
            }
        }
    }
}
