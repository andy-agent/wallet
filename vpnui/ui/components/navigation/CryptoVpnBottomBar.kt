package com.cryptovpn.ui.components.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Language
import androidx.compose.material.icons.rounded.Stars
import androidx.compose.material.icons.rounded.Wallet
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.cryptovpn.navigation.Routes
import com.cryptovpn.ui.components.buttons.SecondaryOutlineButton
import com.cryptovpn.ui.theme.ElectricBlue
import com.cryptovpn.ui.theme.LayerWhite
import com.cryptovpn.ui.theme.SurfaceCloud
import com.cryptovpn.ui.theme.TextMuted
import com.cryptovpn.ui.theme.TextStrong

@Immutable
data class BottomTab(
    val route: String,
    val label: String,
    val icon: ImageVector,
)

val defaultTabs = listOf(
    BottomTab(Routes.VPN_HOME, "Home", Icons.Rounded.Home),
    BottomTab(Routes.PLANS, "VPN", Icons.Rounded.Language),
    BottomTab(Routes.WALLET_HOME, "Wallet", Icons.Rounded.Wallet),
    BottomTab(Routes.INVITE_CENTER, "Earn", Icons.Rounded.Stars),
    BottomTab(Routes.PROFILE, "Profile", Icons.Rounded.AccountCircle),
)

@Composable
fun CryptoVpnBottomBar(
    currentRoute: String,
    onRouteSelected: (String) -> Unit,
    tabs: List<BottomTab> = defaultTabs,
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding() + 10.dp),
        color = LayerWhite.copy(alpha = 0.92f),
        shape = RoundedCornerShape(28.dp),
        tonalElevation = 0.dp,
        shadowElevation = 12.dp,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(SurfaceCloud.copy(alpha = 0.5f))
                .padding(horizontal = 8.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            tabs.forEach { item ->
                val selected = currentRoute == item.route
                SecondaryOutlineButton(
                    onClick = { onRouteSelected(item.route) },
                    selected = selected,
                    icon = {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.label,
                            tint = if (selected) ElectricBlue else TextMuted,
                        )
                    },
                    label = {
                        Text(
                            text = item.label,
                            color = if (selected) TextStrong else TextMuted,
                            style = NavigationBarItemDefaults.colors().run {
                                androidx.compose.material3.MaterialTheme.typography.bodySmall
                            },
                        )
                    },
                )
            }
        }
    }
}
