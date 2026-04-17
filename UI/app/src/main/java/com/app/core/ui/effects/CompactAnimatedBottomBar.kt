package com.app.core.ui.effects

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import com.app.core.theme.BorderSubtle
import com.app.core.theme.CardGlassStrong
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
    CompactBottomTab("market_overview", "钱包", Icons.Rounded.Wallet),
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
    Column {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(BorderSubtle.copy(alpha = 0.72f))
                .size(height = 1.dp, width = 0.dp),
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(CardGlassStrong.copy(alpha = 0.98f))
                .padding(horizontal = 10.dp, vertical = 2.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            tabs.forEach { tab ->
                val selected = currentRoute == tab.route
                val scale = animateFloatAsState(
                    targetValue = if (selected && animated) 1.1f else 1f,
                    animationSpec = spring(stiffness = 520f, dampingRatio = 0.72f),
                    label = "tab-scale",
                )
                val background = animateColorAsState(
                    targetValue = if (selected) MaterialTheme.colorScheme.primary.copy(alpha = 0.14f) else Color.Transparent,
                    animationSpec = spring(stiffness = 420f),
                    label = "tab-bg",
                )
                val iconTint = animateColorAsState(
                    targetValue = if (selected) MaterialTheme.colorScheme.primary else TextTertiary,
                    animationSpec = spring(stiffness = 420f),
                    label = "tab-tint",
                )
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .defaultMinSize(minHeight = 40.dp)
                        .clickable { onRouteSelected(tab.route) }
                        .padding(horizontal = 3.dp, vertical = 3.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(2.dp),
                ) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .scale(scale.value)
                            .background(background.value, RoundedCornerShape(7.dp)),
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
                        color = if (selected) TextPrimary else TextTertiary,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium,
                        textAlign = TextAlign.Center,
                    )
                }
            }
        }
    }
}
