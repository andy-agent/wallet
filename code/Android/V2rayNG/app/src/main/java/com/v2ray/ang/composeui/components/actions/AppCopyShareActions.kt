package com.v2ray.ang.composeui.components.actions

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.foundation.shape.RoundedCornerShape
import com.v2ray.ang.composeui.components.buttons.AppButtonSize
import com.v2ray.ang.composeui.components.buttons.AppButtonVariant
import com.v2ray.ang.composeui.theme.tokens.OverviewBaselineTokens

@Deprecated("Keep this helper in growth/feature flows only; avoid expanding common usage.")
@Composable
fun AppCopyShareActions(
    primaryLabel: String,
    onPrimaryClick: () -> Unit,
    modifier: Modifier = Modifier,
    secondaryLabel: String? = null,
    onSecondaryClick: (() -> Unit)? = null,
) {
    val baseline = OverviewBaselineTokens.primary
    ActionCluster(
        modifier = modifier,
        actions = listOfNotNull(
            appAction(
                label = primaryLabel,
                onClick = onPrimaryClick,
                variant = AppButtonVariant.Primary,
            ),
            if (!secondaryLabel.isNullOrBlank() && onSecondaryClick != null) {
                appAction(
                    label = secondaryLabel,
                    onClick = onSecondaryClick,
                    variant = AppButtonVariant.Secondary,
                )
            } else {
                null
            },
        ),
        layoutMode = ActionClusterLayoutMode.Row,
        buttonSize = AppButtonSize.Lg,
        buttonMinHeight = baseline.actionButtonHeight,
        spacing = baseline.actionButtonGap,
        buttonShape = RoundedCornerShape(baseline.actionButtonRadius),
    )
}
