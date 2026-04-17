package com.app.core.ui.effects

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material.icons.rounded.GridView
import androidx.compose.material.icons.rounded.Language
import androidx.compose.material.icons.rounded.Toll
import androidx.compose.material.icons.rounded.Wallet
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.app.common.components.GlassOutlinePanel
import com.app.core.theme.AppDimens
import com.app.core.theme.BorderSubtle
import com.app.core.theme.TextPrimary
import com.app.core.theme.TextTertiary

data class CompactBottomTab(
    val route: String,
    val label: String,
    val icon: ImageVector,
)

val compactPreviewTabs = listOf(
    CompactBottomTab("wallet_home", "总览", Icons.Rounded.GridView),
    CompactBottomTab("vpn_home", "VPN", Icons.Rounded.Language),
    CompactBottomTab("market_overview", "市场", Icons.Rounded.Wallet),
    CompactBottomTab("invite_center", "增长", Icons.Rounded.Toll),
    CompactBottomTab("profile", "我的", Icons.Rounded.AccountCircle),
)

@Composable
fun CompactAnimatedBottomBar(
    currentRoute: String,
    onRouteSelected: (String) -> Unit,
    animated: Boolean,
    tabs: List<CompactBottomTab> = compactPreviewTabs,
) {
    GlassOutlinePanel(
        modifier = Modifier.fillMaxWidth(),
        radius = 28.dp,
        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            tabs.forEach { tab ->
                val selected = currentRoute == tab.route
                val scale = animateFloatAsState(
                    targetValue = if (selected && animated) 1.08f else 1f,
                    animationSpec = spring(stiffness = 520f, dampingRatio = 0.72f),
                    label = "tab-scale",
                )
                val tabBackground = animateColorAsState(
                    targetValue = if (selected) Color.White.copy(alpha = 0.52f) else Color.Transparent,
                    animationSpec = spring(stiffness = 420f),
                    label = "tab-surface",
                )
                val tabBorder = animateColorAsState(
                    targetValue = if (selected) BorderSubtle.copy(alpha = 0.92f) else Color.Transparent,
                    animationSpec = spring(stiffness = 420f),
                    label = "tab-border",
                )
                val iconBackground = animateColorAsState(
                    targetValue = if (selected) MaterialTheme.colorScheme.primary.copy(alpha = 0.16f) else Color.White.copy(alpha = 0.34f),
                    animationSpec = spring(stiffness = 420f),
                    label = "tab-icon-bg",
                )
                val iconTint = animateColorAsState(
                    targetValue = if (selected) MaterialTheme.colorScheme.primary else TextTertiary,
                    animationSpec = spring(stiffness = 420f),
                    label = "tab-tint",
                )
                val labelTint = animateColorAsState(
                    targetValue = if (selected) TextPrimary else TextTertiary,
                    animationSpec = spring(stiffness = 420f),
                    label = "tab-label",
                )
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .defaultMinSize(minHeight = AppDimens.bottomBarHeight - 10.dp)
                        .border(1.dp, tabBorder.value, RoundedCornerShape(20.dp))
                        .background(tabBackground.value, RoundedCornerShape(20.dp))
                        .clickable { onRouteSelected(tab.route) }
                        .padding(horizontal = 4.dp, vertical = 7.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Box(
                        modifier = Modifier
                            .size(30.dp)
                            .scale(scale.value)
                            .background(iconBackground.value, RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = tab.icon,
                            contentDescription = tab.label,
                            tint = iconTint.value,
                            modifier = Modifier.size(17.dp),
                        )
                    }
                    Text(
                        text = tab.label,
                        color = labelTint.value,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium,
                        textAlign = TextAlign.Center,
                    )
                }
            }
        }
    }
}
