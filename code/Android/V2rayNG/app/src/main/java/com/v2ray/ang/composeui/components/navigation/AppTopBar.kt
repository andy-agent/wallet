package com.v2ray.ang.composeui.components.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.material3.Text
import com.v2ray.ang.composeui.theme.AppTheme

enum class AppTopBarMode {
    Standard,
    Hero,
    Compact,
}

@Composable
fun AppTopBar(
    title: String,
    subtitle: String? = null,
    modifier: Modifier = Modifier,
    mode: AppTopBarMode = AppTopBarMode.Standard,
    navigationIcon: (@Composable () -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {},
) {
    val titleStyle = when (mode) {
        AppTopBarMode.Standard -> androidx.compose.material3.MaterialTheme.typography.headlineMedium
        AppTopBarMode.Hero -> androidx.compose.material3.MaterialTheme.typography.headlineLarge
        AppTopBarMode.Compact -> androidx.compose.material3.MaterialTheme.typography.titleLarge
    }.copy(fontWeight = FontWeight.SemiBold)
    val subtitleStyle = when (mode) {
        AppTopBarMode.Hero -> AppTheme.typography.labelL
        AppTopBarMode.Compact -> AppTheme.typography.labelS
        AppTopBarMode.Standard -> AppTheme.typography.labelM
    }
    val verticalSpacing = when (mode) {
        AppTopBarMode.Hero -> AppTheme.spacing.space4
        AppTopBarMode.Compact -> AppTheme.spacing.space4
        else -> AppTheme.spacing.space8
    }
    val subtitleColor = when (mode) {
        AppTopBarMode.Hero -> AppTheme.colors.brandPrimary.copy(alpha = 0.88f)
        else -> AppTheme.colors.textTertiary
    }
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top,
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.space12),
            verticalAlignment = Alignment.Top,
        ) {
            navigationIcon?.invoke()
            Column(verticalArrangement = Arrangement.spacedBy(verticalSpacing)) {
                if (!subtitle.isNullOrBlank()) {
                    Text(
                        text = subtitle,
                        style = subtitleStyle,
                        color = subtitleColor,
                    )
                }
                Text(
                    text = title,
                    style = titleStyle,
                    color = AppTheme.colors.textPrimary,
                )
            }
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.space8),
            verticalAlignment = Alignment.Top,
            content = actions,
        )
    }
}
