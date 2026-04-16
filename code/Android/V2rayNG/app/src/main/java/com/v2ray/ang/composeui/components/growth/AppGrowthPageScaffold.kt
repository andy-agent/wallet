package com.v2ray.ang.composeui.components.growth

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.v2ray.ang.composeui.components.app.AppPageScaffold
import com.v2ray.ang.composeui.components.app.AppPageBackgroundStyle
import com.v2ray.ang.composeui.components.chips.AppChip
import com.v2ray.ang.composeui.components.chips.AppChipTone
import com.v2ray.ang.composeui.components.navigation.AppTopBar
import com.v2ray.ang.composeui.components.navigation.AppTopBarMode
import com.v2ray.ang.composeui.p0.ui.P01BottomNav
import com.v2ray.ang.composeui.p0.ui.P01HeaderHeroRing
import com.v2ray.ang.composeui.p0.ui.defaultP01Destinations
import com.v2ray.ang.composeui.theme.AppTheme
import androidx.compose.material3.Text

@Deprecated("Growth-specific shell; do not expand as a common foundation scaffold.")
@Composable
fun AppGrowthPageScaffold(
    title: String,
    subtitle: String,
    currentRoute: String,
    onBottomNav: (String) -> Unit,
    modifier: androidx.compose.ui.Modifier = androidx.compose.ui.Modifier,
    note: String = "",
    badge: String? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    AppPageScaffold(
        modifier = modifier,
        backgroundStyle = AppPageBackgroundStyle.Tech,
        bottomBar = {
            P01BottomNav(
                currentRoute = currentRoute,
                destinations = defaultP01Destinations(),
                onNavigate = onBottomNav,
            )
        },
        contentPadding = androidx.compose.foundation.layout.PaddingValues(
            horizontal = AppTheme.spacing.pageHorizontal,
            vertical = AppTheme.spacing.space8,
        ),
    ) { _ ->
        Column(
            modifier = androidx.compose.ui.Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.sectionGap),
        ) {
            AppTopBar(
                title = title,
                subtitle = subtitle,
                mode = AppTopBarMode.Hero,
                actions = { P01HeaderHeroRing() },
            )
            if (!badge.isNullOrBlank()) {
                AppChip(text = badge, tone = AppChipTone.Brand)
            }
            if (note.isNotBlank()) {
                Text(
                    text = note,
                    style = androidx.compose.material3.MaterialTheme.typography.bodySmall,
                    color = AppTheme.colors.textSecondary,
                )
            }
            Spacer(modifier = androidx.compose.ui.Modifier.height(4.dp))
            content()
        }
    }
}
