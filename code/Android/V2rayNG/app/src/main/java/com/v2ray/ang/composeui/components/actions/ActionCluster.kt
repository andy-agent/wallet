package com.v2ray.ang.composeui.components.actions

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.foundation.shape.RoundedCornerShape
import com.v2ray.ang.composeui.components.buttons.AppButton
import com.v2ray.ang.composeui.components.buttons.AppButtonSize
import com.v2ray.ang.composeui.components.buttons.AppButtonVariant
import com.v2ray.ang.composeui.theme.AppTheme

data class ActionClusterAction(
    val label: String,
    val onClick: () -> Unit,
    val variant: AppButtonVariant = AppButtonVariant.Secondary,
    val leadingIcon: (@Composable () -> Unit)? = null,
)

enum class ActionClusterLayoutMode {
    Auto,
    Row,
    Stack,
}

@Composable
fun ActionCluster(
    actions: List<ActionClusterAction>,
    modifier: Modifier = Modifier,
    layoutMode: ActionClusterLayoutMode = ActionClusterLayoutMode.Auto,
    buttonSize: AppButtonSize = AppButtonSize.Md,
    spacing: Dp = AppTheme.spacing.space12,
    buttonShape: Shape? = null,
) {
    if (actions.isEmpty()) return
    val resolvedShape = buttonShape ?: RoundedCornerShape(AppTheme.shapes.radiusPill)
    val resolvedMode = when (layoutMode) {
        ActionClusterLayoutMode.Auto -> if (actions.size <= 2) ActionClusterLayoutMode.Row else ActionClusterLayoutMode.Stack
        else -> layoutMode
    }
    if (resolvedMode == ActionClusterLayoutMode.Row) {
        Row(
            modifier = modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(spacing),
        ) {
            actions.forEach { action ->
                AppButton(
                    text = action.label,
                    onClick = action.onClick,
                    modifier = Modifier.weight(1f),
                    variant = action.variant,
                    size = buttonSize,
                    shape = resolvedShape,
                    leadingIcon = action.leadingIcon,
                )
            }
        }
    } else {
        Column(
            modifier = modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(spacing),
        ) {
            actions.forEach { action ->
                AppButton(
                    text = action.label,
                    onClick = action.onClick,
                    modifier = Modifier.fillMaxWidth(),
                    variant = action.variant,
                    size = buttonSize,
                    shape = resolvedShape,
                    leadingIcon = action.leadingIcon,
                )
            }
        }
    }
}
