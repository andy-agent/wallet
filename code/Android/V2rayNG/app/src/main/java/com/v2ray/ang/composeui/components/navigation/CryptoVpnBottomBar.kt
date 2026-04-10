package com.v2ray.ang.composeui.components.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.v2ray.ang.composeui.theme.ElectricBlue
import com.v2ray.ang.composeui.theme.LayerWhite
import com.v2ray.ang.composeui.theme.NavInactive
import com.v2ray.ang.composeui.theme.TextSoft
import com.v2ray.ang.composeui.theme.TextStrong

@Immutable
data class BottomTab(
    val route: String,
    val label: String,
    val icon: ImageVector,
)

val defaultTabs = listOf(
    BottomTab("vpn_home", "总览", Icons.Rounded.GridView),
    BottomTab("plans", "VPN", Icons.Rounded.Language),
    BottomTab("wallet_home", "钱包", Icons.Rounded.Wallet),
    BottomTab("invite_center", "增长", Icons.Rounded.Toll),
    BottomTab("profile", "我的", Icons.Rounded.AccountCircle),
)

@Composable
fun CryptoVpnBottomBar(
    currentRoute: String,
    onRouteSelected: (String) -> Unit,
    tabs: List<BottomTab> = defaultTabs,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .drawBehind {
                drawLine(
                    color = LayerWhite.copy(alpha = 0.0f).copy(alpha = 0f), // no-op keeps shape simple
                    start = Offset.Zero,
                    end = Offset.Zero,
                    strokeWidth = 0f,
                )
                drawLine(
                    color = NavInactive.copy(alpha = 0.30f),
                    start = Offset(0f, 0f),
                    end = Offset(size.width, 0f),
                    strokeWidth = 1.dp.toPx(),
                )
            }
            .background(LayerWhite.copy(alpha = 0.98f))
            .padding(horizontal = 6.dp, vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        tabs.forEach { item ->
            val selected = currentRoute == item.route
            Column(
                modifier = Modifier
                    .weight(1f)
                    .defaultMinSize(minHeight = 44.dp)
                    .clickable { onRouteSelected(item.route) }
                    .padding(horizontal = 4.dp, vertical = 3.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                Box(
                    modifier = Modifier
                        .size(22.dp)
                        .background(
                            color = if (selected) ElectricBlue.copy(alpha = 0.10f) else LayerWhite.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(10.dp),
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label,
                        tint = if (selected) ElectricBlue else NavInactive,
                        modifier = Modifier.size(16.dp),
                    )
                }
                Text(
                    text = item.label,
                    color = if (selected) TextStrong else TextSoft,
                    style = androidx.compose.material3.MaterialTheme.typography.labelSmall,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}
