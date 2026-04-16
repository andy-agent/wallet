package com.v2ray.ang.composeui.components.buttons

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Deprecated("Use AppPrimaryButton or AppButton directly.")
@Composable
fun PrimaryGlowButton(
    text: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onClick: () -> Unit,
) {
    AppPrimaryButton(
        text = text,
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
    )
}

@Deprecated("Use AppPrimaryButton or AppButton directly.")
@Composable
fun GradientCTAButton(
    text: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    AppPrimaryButton(
        text = text,
        onClick = onClick,
        modifier = modifier,
    )
}

@Deprecated("Use AppButton directly.")
@Composable
fun SecondaryOutlineButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    selected: Boolean = false,
    icon: @Composable (() -> Unit)? = null,
    label: @Composable () -> Unit,
) {
    AppButton(
        text = "",
        onClick = onClick,
        modifier = modifier,
        variant = if (selected) AppButtonVariant.Primary else AppButtonVariant.Secondary,
        leadingIcon = icon,
        label = {
            ProvideTextStyle(value = androidx.compose.material3.MaterialTheme.typography.bodySmall) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    label()
                }
            }
        },
    )
}
