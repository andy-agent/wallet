package com.v2ray.ang.composeui.components.navigation

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.v2ray.ang.composeui.theme.ControlPlaneIntent
import com.v2ray.ang.composeui.theme.ControlPlaneLayer
import com.v2ray.ang.composeui.theme.ControlPlaneTokens
import com.v2ray.ang.composeui.navigation.ShellTab

data class BottomNavItem(
    val route: String,
    val title: String,
    val icon: ImageVector,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CryptoTopAppBar(
    title: String,
    onBackClick: (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {},
) {
    TopAppBar(
        title = { Text(text = title) },
        navigationIcon = {
            if (onBackClick != null) {
                IconButton(onClick = onBackClick) {
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                }
            }
        },
        actions = actions,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CryptoCenterAlignedTopAppBar(
    title: String,
    onBackClick: (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {},
) {
    CenterAlignedTopAppBar(
        title = { Text(text = title) },
        navigationIcon = {
            if (onBackClick != null) {
                IconButton(onClick = onBackClick) {
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                }
            }
        },
        actions = actions,
    )
}

data class ShellBottomBarItem(
    val tab: ShellTab,
    val title: String,
    val icon: ImageVector,
    val selectedIcon: ImageVector = icon,
    val intent: ControlPlaneIntent = ControlPlaneIntent.Infra,
)

@Composable
fun BottomNavigationBar(
    items: List<BottomNavItem>,
    selectedRoute: String,
    onItemSelected: (String) -> Unit,
) {
    NavigationBar {
        items.forEach { item ->
            NavigationBarItem(
                selected = item.route == selectedRoute,
                onClick = { onItemSelected(item.route) },
                icon = { Icon(imageVector = item.icon, contentDescription = item.title) },
                label = { Text(text = item.title) },
            )
        }
    }
}

@Composable
fun BitgetBottomNavigationBar(
    items: List<ShellBottomBarItem>,
    selectedTab: ShellTab,
    onItemSelected: (ShellTab) -> Unit,
    modifier: Modifier = Modifier,
) {
    val selectedItem = items.firstOrNull { it.tab == selectedTab } ?: items.firstOrNull() ?: return
    val selectedPalette = ControlPlaneTokens.intent(selectedItem.intent)
    val layer0 = ControlPlaneTokens.layer(ControlPlaneLayer.Level0)
    val layer1 = ControlPlaneTokens.layer(ControlPlaneLayer.Level1)
    val layer2 = ControlPlaneTokens.layer(ControlPlaneLayer.Level2)
    val layer3 = ControlPlaneTokens.layer(ControlPlaneLayer.Level3)
    val shellShape = RoundedCornerShape(26.dp)
    val itemShape = RoundedCornerShape(18.dp)
    val iconShape = RoundedCornerShape(14.dp)

    Surface(
        modifier = modifier
            .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 12.dp)
            .navigationBarsPadding(),
        shape = shellShape,
        color = layer0.container,
        tonalElevation = 0.dp,
        shadowElevation = 16.dp,
        border = BorderStroke(1.dp, layer2.outline),
    ) {
        Box(
            modifier = Modifier.background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        layer0.container,
                        layer1.container,
                        layer2.container,
                    ),
                ),
            ),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                selectedPalette.container.copy(alpha = 0.82f),
                                Color.Transparent,
                                layer1.container.copy(alpha = 0.94f),
                            ),
                        )
                    ),
            )

            Column(
                modifier = Modifier
                    .padding(horizontal = 10.dp, vertical = 10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(2.dp),
                    ) {
                        Text(
                            text = "GLOBAL CONTROL RAIL",
                            style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.2.sp),
                            color = selectedPalette.accent,
                            fontWeight = FontWeight.Bold,
                        )
                        Text(
                            text = "Selected: ${selectedItem.title}",
                            style = MaterialTheme.typography.titleSmall,
                            color = BottomRailTextPrimary,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = selectedPalette.container,
                        border = BorderStroke(1.dp, selectedPalette.border),
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(7.dp)
                                    .background(selectedPalette.accent, CircleShape),
                            )
                            Text(
                                text = selectedItem.intent.railLabel(),
                                style = MaterialTheme.typography.labelMedium,
                                color = selectedPalette.accent,
                                fontWeight = FontWeight.Bold,
                            )
                        }
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    selectedPalette.border.copy(alpha = 0.78f),
                                    layer3.outline,
                                    Color.Transparent,
                                ),
                            ),
                        ),
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .selectableGroup(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    items.forEach { item ->
                        BottomRailItem(
                            item = item,
                            selected = item.tab == selectedTab,
                            onClick = { onItemSelected(item.tab) },
                            itemShape = itemShape,
                            iconShape = iconShape,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RowScope.BottomRailItem(
    item: ShellBottomBarItem,
    selected: Boolean,
    onClick: () -> Unit,
    itemShape: RoundedCornerShape,
    iconShape: RoundedCornerShape,
) {
    val intentPalette = ControlPlaneTokens.intent(item.intent)
    val layer1 = ControlPlaneTokens.layer(ControlPlaneLayer.Level1)
    val layer2 = ControlPlaneTokens.layer(ControlPlaneLayer.Level2)
    val layer3 = ControlPlaneTokens.layer(ControlPlaneLayer.Level3)
    val animationSpec = tween<Color>(
        durationMillis = ControlPlaneTokens.Motion.stateChange.durationMillis,
        easing = ControlPlaneTokens.Motion.stateChange.easing,
    )
    val containerColor by animateColorAsState(
        targetValue = if (selected) Color.White else layer1.container,
        animationSpec = animationSpec,
        label = "bottomRailContainer",
    )
    val borderColor by animateColorAsState(
        targetValue = if (selected) intentPalette.border else layer2.outline,
        animationSpec = animationSpec,
        label = "bottomRailBorder",
    )
    val iconContainerColor by animateColorAsState(
        targetValue = if (selected) intentPalette.container else layer2.container,
        animationSpec = animationSpec,
        label = "bottomRailIconContainer",
    )
    val iconTint by animateColorAsState(
        targetValue = if (selected) intentPalette.accent else BottomRailIconMuted,
        animationSpec = animationSpec,
        label = "bottomRailIconTint",
    )
    val labelColor by animateColorAsState(
        targetValue = if (selected) BottomRailTextPrimary else BottomRailTextSecondary,
        animationSpec = animationSpec,
        label = "bottomRailText",
    )
    val indicatorColor by animateColorAsState(
        targetValue = if (selected) intentPalette.accent else layer3.outline,
        animationSpec = animationSpec,
        label = "bottomRailIndicator",
    )
    val shadowElevation by animateDpAsState(
        targetValue = if (selected) 6.dp else 0.dp,
        animationSpec = tween(
            durationMillis = ControlPlaneTokens.Motion.stateChange.durationMillis,
            easing = ControlPlaneTokens.Motion.stateChange.easing,
        ),
        label = "bottomRailShadow",
    )

    Surface(
        modifier = Modifier.weight(1f),
        shape = itemShape,
        color = containerColor,
        tonalElevation = 0.dp,
        shadowElevation = shadowElevation,
        border = BorderStroke(1.dp, borderColor),
    ) {
        Box(
            modifier = Modifier.fillMaxWidth(),
        ) {
            if (selected) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    intentPalette.container.copy(alpha = 0.72f),
                                    Color.Transparent,
                                ),
                            ),
                        ),
                )
            }
            Column(
                modifier = Modifier
                    .selectable(
                        selected = selected,
                        onClick = onClick,
                        role = Role.Tab,
                    )
                    .padding(horizontal = 4.dp, vertical = 9.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Box(
                    modifier = Modifier
                        .width(if (selected) 28.dp else 16.dp)
                        .height(if (selected) 3.dp else 2.dp)
                        .background(indicatorColor, CircleShape),
                )

                Box(
                    modifier = Modifier
                        .size(if (selected) 38.dp else 36.dp)
                        .background(iconContainerColor, iconShape),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = if (selected) item.selectedIcon else item.icon,
                        contentDescription = item.title,
                        tint = iconTint,
                    )
                }

                Text(
                    text = item.title,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = labelColor,
                    fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium,
                    style = MaterialTheme.typography.labelMedium,
                )
            }
        }
    }
}

private fun ControlPlaneIntent.railLabel(): String = when (this) {
    ControlPlaneIntent.Infra -> "INFRA"
    ControlPlaneIntent.Settlement -> "SETTLEMENT"
    ControlPlaneIntent.Finance -> "FINANCE"
    ControlPlaneIntent.Neutral -> "PROFILE"
}

private val BottomRailTextPrimary = ControlPlaneTokens.Ink
private val BottomRailTextSecondary = ControlPlaneTokens.InkSecondary
private val BottomRailIconMuted = ControlPlaneTokens.InkTertiary
