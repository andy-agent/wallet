package com.v2ray.ang.composeui.components.actions

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.v2ray.ang.composeui.components.buttons.AppButton
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
) {
    if (actions.isEmpty()) return
    val resolvedMode = when (layoutMode) {
        ActionClusterLayoutMode.Auto -> if (actions.size <= 2) ActionClusterLayoutMode.Row else ActionClusterLayoutMode.Stack
        else -> layoutMode
    }
    if (resolvedMode == ActionClusterLayoutMode.Row) {
        Row(
            modifier = modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.space12),
        ) {
            actions.forEach { action ->
                AppButton(
                    text = action.label,
                    onClick = action.onClick,
                    modifier = Modifier.weight(1f),
                    variant = action.variant,
                    leadingIcon = action.leadingIcon,
                )
            }
        }
    } else {
        Column(
            modifier = modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.space12),
        ) {
            actions.forEach { action ->
                AppButton(
                    text = action.label,
                    onClick = action.onClick,
                    modifier = Modifier.fillMaxWidth(),
                    variant = action.variant,
                    leadingIcon = action.leadingIcon,
                )
            }
        }
    }
}
