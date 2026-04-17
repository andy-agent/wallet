package com.app.core.ui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.app.core.navigation.BottomNavItem
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
            androidx.compose.material3.Scaffold(
                bottomBar = {
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
                },
                containerColor = Color.Transparent,
            ) { padding ->
                content(padding)
            }
        }
    }
}
