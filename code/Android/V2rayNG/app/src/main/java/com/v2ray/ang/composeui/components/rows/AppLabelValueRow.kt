package com.v2ray.ang.composeui.components.rows

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.v2ray.ang.composeui.components.chips.AppChipTone

@Deprecated("Use LabelValueRow directly.")
@Composable
fun AppLabelValueRow(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    supportingText: String = "",
    badgeText: String? = null,
    badgeTone: AppChipTone = AppChipTone.Neutral,
    layoutMode: LabelValueRowLayoutMode = LabelValueRowLayoutMode.Inline,
    valueDisplayMode: LabelValueDisplayMode = LabelValueDisplayMode.Full,
) {
    LabelValueRow(
        label = label,
        value = value,
        modifier = modifier,
        supportingText = supportingText,
        badgeText = badgeText,
        badgeTone = badgeTone,
        layoutMode = layoutMode,
        valueDisplayMode = valueDisplayMode,
    )
}
