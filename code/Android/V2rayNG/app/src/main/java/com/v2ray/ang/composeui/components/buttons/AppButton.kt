package com.v2ray.ang.composeui.components.buttons

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
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

enum class AppButtonSize {
    Sm,
    Md,
    Lg,
}

@Composable
fun AppButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    variant: AppButtonVariant = AppButtonVariant.Primary,
    size: AppButtonSize = AppButtonSize.Md,
    enabled: Boolean = true,
    loading: Boolean = false,
    contentPadding: PaddingValues = defaultAppButtonPadding(size),
    leadingIcon: (@Composable () -> Unit)? = null,
    label: (@Composable () -> Unit)? = null,
) {
    val shape = RoundedCornerShape(AppTheme.shapes.radiusPill)
    val minHeight = when (size) {
        AppButtonSize.Sm -> 40.dp
        AppButtonSize.Md -> 48.dp
        AppButtonSize.Lg -> 52.dp
    }
    val textStyle = AppTheme.typography.labelL
    val indicator: @Composable (() -> Unit)? = if (loading) {
        {
            CircularProgressIndicator(
                modifier = Modifier.defaultMinSize(minWidth = 16.dp, minHeight = 16.dp),
                strokeWidth = 2.dp,
                color = if (variant == AppButtonVariant.Primary) AppTheme.colors.textOnPrimary else AppTheme.colors.brandPrimary,
            )
        }
    } else {
        null
    }
    when (variant) {
        AppButtonVariant.Primary -> {
            Button(
                onClick = onClick,
                enabled = enabled && !loading,
                modifier = modifier
                    .defaultMinSize(minHeight = minHeight)
                    .background(
                        brush = AppTheme.gradients.primaryGradient,
                        shape = shape,
                    ),
                shape = shape,
                contentPadding = contentPadding,
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
                        indicator?.invoke() ?: leadingIcon?.invoke()
                        label?.invoke() ?: Text(text = text)
                    }
                }
            }
        }

        AppButtonVariant.Secondary -> {
            OutlinedButton(
                onClick = onClick,
                enabled = enabled && !loading,
                modifier = modifier.defaultMinSize(minHeight = minHeight),
                shape = shape,
                border = androidx.compose.foundation.BorderStroke(
                    width = 1.dp,
                    color = AppTheme.colors.dividerSubtle,
                ),
                contentPadding = contentPadding,
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = AppTheme.colors.surfaceElevated,
                    contentColor = AppTheme.colors.brandPrimary,
                    disabledContainerColor = AppTheme.colors.surfaceCard,
                    disabledContentColor = AppTheme.colors.textDisabled,
                ),
            ) {
                ProvideTextStyle(textStyle) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        indicator?.invoke() ?: leadingIcon?.invoke()
                        label?.invoke() ?: Text(text = text)
                    }
                }
            }
        }

        AppButtonVariant.Ghost -> {
            TextButton(
                onClick = onClick,
                enabled = enabled && !loading,
                modifier = modifier.defaultMinSize(minHeight = minHeight),
                shape = shape,
                contentPadding = contentPadding,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = AppTheme.colors.brandPrimary,
                    disabledContentColor = AppTheme.colors.textDisabled,
                ),
            ) {
                ProvideTextStyle(MaterialTheme.typography.labelLarge) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        indicator?.invoke() ?: leadingIcon?.invoke()
                        label?.invoke() ?: Text(text = text)
                    }
                }
            }
        }
    }
}

@Composable
private fun defaultAppButtonPadding(size: AppButtonSize): PaddingValues = when (size) {
    AppButtonSize.Sm -> PaddingValues(horizontal = AppTheme.spacing.space12, vertical = AppTheme.spacing.space8)
    AppButtonSize.Md -> PaddingValues(horizontal = AppTheme.spacing.space16, vertical = AppTheme.spacing.space12)
    AppButtonSize.Lg -> PaddingValues(horizontal = AppTheme.spacing.space20, vertical = AppTheme.spacing.space12)
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
