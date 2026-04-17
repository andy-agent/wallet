package com.app.core.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.app.core.navigation.BottomNavItem
import com.app.core.theme.AppDimens
import com.app.core.ui.effects.CompactAnimatedBottomBar
import com.app.core.ui.effects.CompactBottomTab
import com.app.core.ui.effects.EffectToggle
import com.app.core.ui.effects.ProductionMotionProfile

@Composable
fun AdaptiveScaffold(
    showNavigation: Boolean,
    currentRoute: String,
    navItems: List<BottomNavItem>,
    onNavigate: (String) -> Unit,
    content: @Composable (PaddingValues) -> Unit,
) {
    if (!showNavigation) {
        content(PaddingValues())
        return
    }

    when (rememberWindowAdaptive()) {
        WindowAdaptive.Expanded, WindowAdaptive.Medium -> {
            Row(modifier = Modifier.fillMaxSize()) {
                NavigationRail {
                    navItems.forEach { item ->
                        NavigationRailItem(
                            selected = currentRoute == item.route,
                            onClick = { onNavigate(item.route) },
                            icon = { androidx.compose.material3.Icon(item.icon, contentDescription = item.label) },
                            label = { Text(item.label) },
                        )
                    }
                }
                content(PaddingValues(start = 0.dp))
            }
        }

        WindowAdaptive.Compact -> {
            val density = LocalDensity.current
            val bottomInset = with(density) { WindowInsets.safeDrawing.getBottom(this).toDp() }
            val contentBottomPadding = bottomInset + AppDimens.bottomBarHeight + 10.dp

            Box(modifier = Modifier.fillMaxSize()) {
                content(PaddingValues(bottom = contentBottomPadding))

                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .navigationBarsPadding()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                ) {
                    CompactAnimatedBottomBar(
                        currentRoute = currentRoute,
                        onRouteSelected = onNavigate,
                        animated = ProductionMotionProfile.isEnabled(EffectToggle.BottomBarMotion),
                        tabs = navItems.map { item ->
                            CompactBottomTab(
                                route = item.route,
                                label = item.label,
                                icon = item.icon,
                            )
                        },
                    )
                }
            }
        }
    }
}
