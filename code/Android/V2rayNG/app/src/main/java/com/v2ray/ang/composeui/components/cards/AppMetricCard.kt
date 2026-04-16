package com.v2ray.ang.composeui.components.cards

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.v2ray.ang.composeui.components.chips.AppChipTone

@Deprecated("Use MetricCard directly.")
@Composable
fun AppMetricCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier,
    supportingText: String = "",
    badgeText: String? = null,
    badgeTone: AppChipTone = AppChipTone.Brand,
    emphasized: Boolean = false,
) {
    MetricCard(
        title = title,
        value = value,
        modifier = modifier,
        supportingText = supportingText,
        badgeText = badgeText,
        badgeTone = badgeTone,
        emphasized = emphasized,
    )
}
