package com.v2ray.ang.composeui.components.sections

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun AppInfoSection(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String = "",
    trailing: @Composable (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    InfoSection(
        title = title,
        modifier = modifier,
        subtitle = subtitle,
        trailing = trailing,
        content = content,
    )
}
