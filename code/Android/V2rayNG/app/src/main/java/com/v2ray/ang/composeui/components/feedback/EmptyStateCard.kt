package com.v2ray.ang.composeui.components.feedback

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.material3.Text
import com.v2ray.ang.composeui.components.buttons.AppSecondaryButton
import com.v2ray.ang.composeui.components.cards.AppCard
import com.v2ray.ang.composeui.theme.AppTheme

@Composable
fun EmptyStateCard(
    title: String,
    message: String,
    modifier: Modifier = Modifier,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null,
    icon: (@Composable () -> Unit)? = null,
) {
    AppCard(modifier = modifier) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.space12),
        ) {
            icon?.invoke()
            Text(
                text = title,
                style = androidx.compose.material3.MaterialTheme.typography.titleMedium,
                color = AppTheme.colors.textPrimary,
            )
            Text(
                text = message,
                style = androidx.compose.material3.MaterialTheme.typography.bodySmall,
                color = AppTheme.colors.textSecondary,
            )
            if (!actionLabel.isNullOrBlank() && onAction != null) {
                AppSecondaryButton(
                    text = actionLabel,
                    onClick = onAction,
                )
            }
        }
    }
}
