package com.v2ray.ang.composeui.components.listitems

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.material3.Text
import com.v2ray.ang.composeui.components.cards.AppCard
import com.v2ray.ang.composeui.components.cards.AppCardVariant
import com.v2ray.ang.composeui.theme.AppTheme

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
    AppCard(
        modifier = modifier.then(
            if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier,
        ),
        variant = if (emphasized) AppCardVariant.Elevated else AppCardVariant.Default,
        contentPadding = AppTheme.spacing.cardPadding,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.space12),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            leading?.invoke()
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.space4),
            ) {
                Text(
                    text = title,
                    style = androidx.compose.material3.MaterialTheme.typography.titleMedium,
                    color = AppTheme.colors.textPrimary,
                )
                if (subtitle.isNotBlank()) {
                    Text(
                        text = subtitle,
                        style = androidx.compose.material3.MaterialTheme.typography.bodySmall,
                        color = AppTheme.colors.textSecondary,
                    )
                }
                if (supportingText.isNotBlank()) {
                    Text(
                        text = supportingText,
                        style = androidx.compose.material3.MaterialTheme.typography.labelSmall,
                        color = AppTheme.colors.textTertiary,
                    )
                }
            }
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.space4),
            ) {
                if (value.isNotBlank()) {
                    Text(
                        text = value,
                        style = androidx.compose.material3.MaterialTheme.typography.titleMedium,
                        color = AppTheme.colors.textPrimary,
                    )
                }
                trailing?.invoke()
            }
        }
    }
}
