package com.v2ray.ang.composeui.components.buttons

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.v2ray.ang.composeui.theme.AppTheme

enum class AppButtonVariant {
    Primary,
    Secondary,
    Ghost,
}

@Composable
fun AppButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    variant: AppButtonVariant = AppButtonVariant.Primary,
    enabled: Boolean = true,
    leadingIcon: (@Composable () -> Unit)? = null,
    label: (@Composable () -> Unit)? = null,
) {
    val shape = RoundedCornerShape(AppTheme.shapes.radiusPill)
    val minHeight = 50.dp
    val textStyle = AppTheme.typography.labelL
    when (variant) {
        AppButtonVariant.Primary -> {
            Button(
                onClick = onClick,
                enabled = enabled,
                modifier = modifier
                    .defaultMinSize(minHeight = minHeight)
                    .background(
                        brush = AppTheme.gradients.primaryGradient,
                        shape = shape,
                    ),
                shape = shape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent,
                    contentColor = AppTheme.colors.textOnPrimary,
                    disabledContainerColor = AppTheme.colors.textDisabled.copy(alpha = 0.24f),
                    disabledContentColor = AppTheme.colors.textOnPrimary.copy(alpha = 0.75f),
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp),
            ) {
                ProvideTextStyle(textStyle) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        leadingIcon?.invoke()
                        label?.invoke() ?: Text(text = text, modifier = Modifier.padding(vertical = 6.dp))
                    }
                }
            }
        }

        AppButtonVariant.Secondary -> {
            OutlinedButton(
                onClick = onClick,
                enabled = enabled,
                modifier = modifier.defaultMinSize(minHeight = minHeight),
                shape = shape,
                border = androidx.compose.foundation.BorderStroke(
                    width = 1.dp,
                    color = AppTheme.colors.dividerSubtle,
                ),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = AppTheme.colors.surfaceElevated,
                    contentColor = AppTheme.colors.brandPrimary,
                    disabledContainerColor = AppTheme.colors.surfaceCard,
                    disabledContentColor = AppTheme.colors.textDisabled,
                ),
            ) {
                ProvideTextStyle(textStyle) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        leadingIcon?.invoke()
                        label?.invoke() ?: Text(text = text, modifier = Modifier.padding(vertical = 6.dp))
                    }
                }
            }
        }

        AppButtonVariant.Ghost -> {
            TextButton(
                onClick = onClick,
                enabled = enabled,
                modifier = modifier.defaultMinSize(minHeight = minHeight),
                shape = shape,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = AppTheme.colors.brandPrimary,
                    disabledContentColor = AppTheme.colors.textDisabled,
                ),
            ) {
                ProvideTextStyle(MaterialTheme.typography.labelLarge) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        leadingIcon?.invoke()
                        label?.invoke() ?: Text(text = text, modifier = Modifier.padding(vertical = 6.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun AppPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    leadingIcon: (@Composable () -> Unit)? = null,
) {
    AppButton(
        text = text,
        onClick = onClick,
        modifier = modifier,
        variant = AppButtonVariant.Primary,
        enabled = enabled,
        leadingIcon = leadingIcon,
    )
}

@Composable
fun AppSecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    leadingIcon: (@Composable () -> Unit)? = null,
) {
    AppButton(
        text = text,
        onClick = onClick,
        modifier = modifier,
        variant = AppButtonVariant.Secondary,
        enabled = enabled,
        leadingIcon = leadingIcon,
    )
}

@Composable
fun AppGhostButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    leadingIcon: (@Composable () -> Unit)? = null,
) {
    AppButton(
        text = text,
        onClick = onClick,
        modifier = modifier,
        variant = AppButtonVariant.Ghost,
        enabled = enabled,
        leadingIcon = leadingIcon,
    )
}
