package com.v2ray.ang.composeui.components.inputs

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun GlassTextField(
    value: String,
    label: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    trailing: @Composable (() -> Unit)? = null,
    singleLine: Boolean = true,
) {
    AppTextField(
        value = value,
        label = label,
        onValueChange = onValueChange,
        modifier = modifier,
        placeholder = placeholder,
        trailing = trailing,
        singleLine = singleLine,
    )
}
