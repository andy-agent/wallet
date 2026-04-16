package com.v2ray.ang.composeui.components.inputs

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.v2ray.ang.composeui.theme.AppTheme

@Composable
fun AppTextField(
    value: String,
    label: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    trailing: @Composable (() -> Unit)? = null,
    singleLine: Boolean = true,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.fillMaxWidth(),
        singleLine = singleLine,
        label = label.takeIf { it.isNotBlank() }?.let { labelText -> { Text(labelText) } },
        placeholder = placeholder.takeIf { it.isNotBlank() }?.let { placeholderText -> { Text(placeholderText) } },
        shape = RoundedCornerShape(AppTheme.shapes.radiusSm),
        trailingIcon = trailing,
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = AppTheme.colors.surfaceElevated,
            unfocusedContainerColor = AppTheme.colors.surfaceCard,
            focusedBorderColor = AppTheme.colors.borderFocus,
            unfocusedBorderColor = AppTheme.colors.dividerSubtle,
            focusedTextColor = AppTheme.colors.textPrimary,
            unfocusedTextColor = AppTheme.colors.textPrimary,
            focusedLabelColor = AppTheme.colors.brandPrimary,
            unfocusedLabelColor = AppTheme.colors.textSecondary,
            focusedPlaceholderColor = AppTheme.colors.textTertiary,
            unfocusedPlaceholderColor = AppTheme.colors.textTertiary,
        ),
    )
}
