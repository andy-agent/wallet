package com.v2ray.ang.composeui.components.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
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
    val shellShape = RoundedCornerShape(32.dp)
    val itemShape = RoundedCornerShape(24.dp)
    val iconShape = RoundedCornerShape(16.dp)

    Surface(
        modifier = modifier
            .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 12.dp)
            .navigationBarsPadding(),
        shape = shellShape,
        color = Color.White.copy(alpha = 0.98f),
        tonalElevation = 0.dp,
        shadowElevation = 18.dp,
        border = androidx.compose.foundation.BorderStroke(1.dp, BottomChromeBorder),
    ) {
        Column(
            modifier = Modifier
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.White,
                            BottomChromeSurface,
                            BottomChromeSurfaceStrong,
                        ),
                    ),
                )
                .padding(horizontal = 10.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
                    .height(1.dp)
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                Color.Transparent,
                                BottomChromeScanLine,
                                BottomChromeAccent.copy(alpha = 0.45f),
                                BottomChromeScanLine,
                                Color.Transparent,
                            ),
                        )
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
                    val selected = item.tab == selectedTab
                    val iconTint = if (selected) BottomChromeSelectedIcon else BottomChromeUnselectedIcon
                    val labelColor = if (selected) BottomChromeSelectedText else BottomChromeUnselectedText
                    val labelWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium
                    val itemBorder = if (selected) BottomChromeSelectedBorder else Color.Transparent
                    val itemBackground = if (selected) {
                        Brush.verticalGradient(
                            colors = listOf(
                                BottomChromeSelectedSurface,
                                BottomChromeSelectedSurfaceStrong,
                            ),
                        )
                    } else {
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Transparent,
                            ),
                        )
                    }
                    val iconBackground = if (selected) {
                        Brush.linearGradient(
                            colors = listOf(
                                BottomChromeIconChipSelected,
                                BottomChromeIconChipSelectedStrong,
                            ),
                        )
                    } else {
                        Brush.verticalGradient(
                            colors = listOf(
                                BottomChromeIconChip,
                                BottomChromeIconChipStrong,
                            ),
                        )
                    }

                    Surface(
                        modifier = Modifier.weight(1f),
                        shape = itemShape,
                        color = Color.Transparent,
                        tonalElevation = 0.dp,
                        border = androidx.compose.foundation.BorderStroke(1.dp, itemBorder),
                    ) {
                        Column(
                            modifier = Modifier
                                .background(itemBackground)
                                .selectable(
                                    selected = selected,
                                    onClick = { onItemSelected(item.tab) },
                                    role = Role.Tab,
                                )
                                .padding(horizontal = 4.dp, vertical = 8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(6.dp),
                        ) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.CenterHorizontally)
                                    .height(3.dp)
                                    .fillMaxWidth(if (selected) 0.34f else 0.2f)
                                    .background(
                                        color = if (selected) BottomChromeAccent else BottomChromeTrack,
                                        shape = CircleShape,
                                    ),
                            )

                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .background(brush = iconBackground, shape = iconShape),
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
                                fontWeight = labelWeight,
                            )
                        }
                    }
                }
            }
        }
    }
}

private val BottomChromeSurface = Color(0xFFF7FBFE)
private val BottomChromeSurfaceStrong = Color(0xFFF1F7FC)
private val BottomChromeBorder = Color(0xFFD9E6F1)
private val BottomChromeScanLine = Color(0xFFE7EFF7)
private val BottomChromeTrack = Color(0xFFD3DDEA)
private val BottomChromeAccent = Color(0xFF1CB7D0)
private val BottomChromeSelectedSurface = Color(0xFFF2FBFF)
private val BottomChromeSelectedSurfaceStrong = Color(0xFFE9F7FD)
private val BottomChromeSelectedBorder = Color(0xFFC4E6F0)
private val BottomChromeIconChip = Color(0xFFF4F7FB)
private val BottomChromeIconChipStrong = Color(0xFFEAF0F7)
private val BottomChromeIconChipSelected = Color(0xFFE9FAFE)
private val BottomChromeIconChipSelectedStrong = Color(0xFFD8F3FB)
private val BottomChromeSelectedIcon = Color(0xFF0E2335)
private val BottomChromeUnselectedIcon = Color(0xFF7A8899)
private val BottomChromeSelectedText = Color(0xFF0E2234)
private val BottomChromeUnselectedText = Color(0xFF7F8D9C)
