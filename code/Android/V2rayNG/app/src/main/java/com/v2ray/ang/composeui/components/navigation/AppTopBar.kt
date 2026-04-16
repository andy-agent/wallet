package com.v2ray.ang.composeui.components.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.material3.Text
import com.v2ray.ang.composeui.theme.AppTheme

@Composable
fun AppTopBar(
    title: String,
    subtitle: String? = null,
    modifier: Modifier = Modifier,
    leading: (@Composable () -> Unit)? = null,
    trailing: (@Composable () -> Unit)? = null,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top,
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.space12),
            verticalAlignment = Alignment.Top,
        ) {
            leading?.invoke()
            Column(verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.space8)) {
                if (!subtitle.isNullOrBlank()) {
                    Text(
                        text = subtitle.uppercase(),
                        style = androidx.compose.material3.MaterialTheme.typography.labelMedium,
                        color = AppTheme.colors.textTertiary,
                    )
                }
                Text(
                    text = title,
                    style = androidx.compose.material3.MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = AppTheme.colors.textPrimary,
                )
            }
        }
        trailing?.invoke()
    }
}
