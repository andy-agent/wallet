package com.v2ray.ang.composeui.components.listitems

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Deprecated("Use AppListRow or AppListCardItem explicitly.", ReplaceWith("AppListCardItem(title, modifier, subtitle, value, supportingText, emphasized, leading, trailing, onClick)"))
@Composable
fun AppListItem(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String = "",
    value: String = "",
    supportingText: String = "",
    emphasized: Boolean = false,
    leading: (@Composable () -> Unit)? = null,
    trailing: (@Composable () -> Unit)? = null,
    onClick: (() -> Unit)? = null,
) {
    AppListCardItem(
        title = title,
        modifier = modifier,
        subtitle = subtitle,
        value = value,
        supportingText = supportingText,
        emphasized = emphasized,
        leading = leading,
        trailing = trailing,
        onClick = onClick,
    )
}
