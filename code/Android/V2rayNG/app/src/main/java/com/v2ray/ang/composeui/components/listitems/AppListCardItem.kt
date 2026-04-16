package com.v2ray.ang.composeui.components.listitems

import androidx.compose.foundation.clickable
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.v2ray.ang.composeui.components.cards.AppCard
import com.v2ray.ang.composeui.components.cards.AppCardVariant
import com.v2ray.ang.composeui.theme.AppTheme

@Composable
fun AppListCardItem(
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
        AppListRow(
            title = title,
            subtitle = subtitle,
            value = value,
            supportingText = supportingText,
            leading = leading,
            trailing = trailing,
        )
    }
}
