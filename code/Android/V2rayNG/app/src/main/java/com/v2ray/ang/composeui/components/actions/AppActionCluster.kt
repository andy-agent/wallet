package com.v2ray.ang.composeui.components.actions

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.v2ray.ang.composeui.components.buttons.AppButtonVariant

typealias AppActionClusterAction = ActionClusterAction

@Deprecated("Use ActionCluster directly.")
@Composable
fun AppActionCluster(
    actions: List<AppActionClusterAction>,
    modifier: Modifier = Modifier,
) {
    ActionCluster(actions = actions, modifier = modifier)
}

fun appAction(
    label: String,
    onClick: () -> Unit,
    variant: AppButtonVariant = AppButtonVariant.Secondary,
    leadingIcon: (@Composable () -> Unit)? = null,
): AppActionClusterAction = AppActionClusterAction(
    label = label,
    onClick = onClick,
    variant = variant,
    leadingIcon = leadingIcon,
)
