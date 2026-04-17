package com.app.core.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Security
import androidx.compose.ui.graphics.vector.ImageVector

data class BottomNavItem(
    val route: String,
    val label: String,
    val icon: ImageVector,
)

fun defaultBottomNavItems() = listOf(
    BottomNavItem(AppRoutes.WalletHome, "总览", Icons.Outlined.AccountBalanceWallet),
    BottomNavItem(AppRoutes.VpnHome, "VPN", Icons.Outlined.Security),
    BottomNavItem(AppRoutes.MarketOverview, "市场", Icons.Outlined.Language),
    BottomNavItem(AppRoutes.Profile, "我的", Icons.Outlined.Person),
)
