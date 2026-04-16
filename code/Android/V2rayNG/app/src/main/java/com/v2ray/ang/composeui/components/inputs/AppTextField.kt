package com.v2ray.ang.composeui.components.inputs

import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.VisualTransformation
import com.v2ray.ang.composeui.theme.AppTheme

@Composable
fun AppTextField(
    value: String,
    label: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    supportingText: String = "",
    enabled: Boolean = true,
    readOnly: Boolean = false,
    isError: Boolean = false,
    leading: @Composable (() -> Unit)? = null,
    trailing: @Composable (() -> Unit)? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    singleLine: Boolean = true,
    maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        enabled = enabled,
        readOnly = readOnly,
        isError = isError,
        singleLine = singleLine,
        maxLines = maxLines,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        visualTransformation = visualTransformation,
        label = label.takeIf { it.isNotBlank() }?.let { labelText -> { Text(labelText) } },
        placeholder = placeholder.takeIf { it.isNotBlank() }?.let { placeholderText -> { Text(placeholderText) } },
        supportingText = supportingText.takeIf { it.isNotBlank() }?.let { text -> { Text(text) } },
        shape = RoundedCornerShape(AppTheme.shapes.radiusSm),
        leadingIcon = leading,
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
            errorBorderColor = AppTheme.colors.error,
            errorLabelColor = AppTheme.colors.error,
            errorSupportingTextColor = AppTheme.colors.error,
            focusedPlaceholderColor = AppTheme.colors.textTertiary,
            unfocusedPlaceholderColor = AppTheme.colors.textTertiary,
        ),
    )
}
