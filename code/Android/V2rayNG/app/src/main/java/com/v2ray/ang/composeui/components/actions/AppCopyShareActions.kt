package com.v2ray.ang.composeui.components.actions

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.v2ray.ang.composeui.components.buttons.AppButtonVariant

@Composable
fun AppCopyShareActions(
    primaryLabel: String,
    onPrimaryClick: () -> Unit,
    modifier: Modifier = Modifier,
    secondaryLabel: String? = null,
    onSecondaryClick: (() -> Unit)? = null,
) {
    AppActionCluster(
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
    )
}
