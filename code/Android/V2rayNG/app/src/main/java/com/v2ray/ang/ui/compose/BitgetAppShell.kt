package com.v2ray.ang.ui.compose

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SouthWest
import androidx.compose.material.icons.filled.VpnLock
import androidx.compose.material.icons.filled.Wallet
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.v2ray.ang.composeui.components.navigation.BitgetBottomNavigationBar
import com.v2ray.ang.composeui.components.navigation.ShellBottomBarItem
import com.v2ray.ang.composeui.components.shell.BitgetHomeActionsSheet
import com.v2ray.ang.composeui.components.shell.BitgetHomeCampaignCard
import com.v2ray.ang.composeui.components.shell.BitgetHomeHighlightCard
import com.v2ray.ang.composeui.components.shell.BitgetHomeHighlightsRow
import com.v2ray.ang.composeui.components.shell.BitgetHomeListCard
import com.v2ray.ang.composeui.components.shell.BitgetHomeListEntry
import com.v2ray.ang.composeui.components.shell.BitgetHomeOperationsHero
import com.v2ray.ang.composeui.components.shell.BitgetHomePortfolioHero
import com.v2ray.ang.composeui.components.shell.BitgetHomeQuickLink
import com.v2ray.ang.composeui.components.shell.BitgetHomeQuickLinks
import com.v2ray.ang.composeui.components.shell.BitgetHomeSheetAction
import com.v2ray.ang.composeui.components.shell.BitgetHomeTopBar
import com.v2ray.ang.composeui.navigation.ShellTab
import com.v2ray.ang.composeui.pages.market.MarketOverviewPage
import com.v2ray.ang.composeui.pages.profile.ProfilePage
import com.v2ray.ang.composeui.pages.vpn.VPNHomePage
import com.v2ray.ang.composeui.pages.wallet.WalletHomePage
import com.v2ray.ang.composeui.theme.BackgroundDeepest
import com.v2ray.ang.composeui.theme.BackgroundPrimary
import com.v2ray.ang.composeui.theme.BackgroundSecondary
import com.v2ray.ang.composeui.theme.GlowBlue
import com.v2ray.ang.composeui.theme.GlowGreen
import com.v2ray.ang.composeui.theme.GlowYellow
import com.v2ray.ang.composeui.theme.Info
import com.v2ray.ang.composeui.theme.Warning

@Composable
fun BitgetAppShell(
    selectedTab: ShellTab,
    isAuthenticated: Boolean,
    onTabSelected: (ShellTab) -> Unit,
    onOpenLogin: () -> Unit,
    onOpenVpnConsole: () -> Unit,
    onOpenPlans: () -> Unit,
    onOpenRegions: () -> Unit,
    onOpenOrders: () -> Unit,
    onOpenWalletHome: () -> Unit,
    onOpenReceive: () -> Unit,
    onOpenSend: () -> Unit,
    onOpenAssetDetail: (String) -> Unit,
    onOpenMarketQuote: (String) -> Unit,
    onOpenInviteCenter: () -> Unit,
    onOpenCommission: () -> Unit,
    onOpenWithdraw: () -> Unit,
    onOpenProfile: () -> Unit,
    onOpenLegal: () -> Unit,
    onOpenSettings: () -> Unit,
    onOpenAbout: () -> Unit,
    onOpenSupport: () -> Unit,
    onLogout: () -> Unit,
) {
    fun guarded(action: () -> Unit): () -> Unit = {
        if (isAuthenticated) {
            action()
        } else {
            onOpenLogin()
        }
    }

    val navItems = remember {
        listOf(
            ShellBottomBarItem(tab = ShellTab.HOME, title = "Home", icon = Icons.Default.Home),
            ShellBottomBarItem(tab = ShellTab.MARKET, title = "Market", icon = Icons.Default.Language),
            ShellBottomBarItem(tab = ShellTab.VPN, title = "VPN", icon = Icons.Default.VpnLock),
            ShellBottomBarItem(tab = ShellTab.WALLET, title = "Wallet", icon = Icons.Default.AccountBalanceWallet),
            ShellBottomBarItem(tab = ShellTab.PROFILE, title = "Profile", icon = Icons.Default.Person),
        )
    }

    val backgroundColors = if (selectedTab == ShellTab.HOME) {
        listOf(
            Color(0xFFF8FBFF),
            BackgroundSecondary,
            BackgroundPrimary,
        )
    } else {
        listOf(
            Color(0xFFFBFDFF),
            BackgroundSecondary,
            BackgroundPrimary,
        )
    }
    val firstGlow = if (selectedTab == ShellTab.HOME) {
        Color(0x1435D4E6)
    } else {
        GlowGreen.copy(alpha = 0.16f)
    }
    val secondGlow = if (selectedTab == ShellTab.HOME) {
        Color(0x14FFB14A)
    } else {
        GlowBlue.copy(alpha = 0.12f)
    }

    var showHomeActionsSheet by rememberSaveable(selectedTab) { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = backgroundColors,
                ),
            ),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(firstGlow, Color.Transparent),
                        radius = 900f,
                    ),
                ),
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(secondGlow, Color.Transparent),
                        radius = 1200f,
                    ),
                ),
        )

        Scaffold(
            containerColor = Color.Transparent,
            contentWindowInsets = WindowInsets.safeDrawing,
            bottomBar = {
                BitgetBottomNavigationBar(
                    items = navItems,
                    selectedTab = selectedTab,
                    onItemSelected = onTabSelected,
                )
            },
        ) { innerPadding ->
            if (selectedTab == ShellTab.HOME) {
                val openWalletHomeAction = guarded(onOpenWalletHome)
                val openOrdersAction = guarded(onOpenOrders)
                val openVpnAction = guarded(onOpenVpnConsole)
                val openReceiveAction = guarded(onOpenReceive)
                val openSendAction = guarded(onOpenSend)
                val openInviteAction = guarded(onOpenInviteCenter)
                val openWithdrawAction = guarded(onOpenWithdraw)
                val heroBalance = if (isAuthenticated) "$12,604.58" else "$0.00"
                val heroChange = if (isAuthenticated) "-$17.89  -0.96%" else "登录后同步"
                val heroLabel = if (isAuthenticated) "当日盈亏" else "资产总览"

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                ) {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(
                            start = 20.dp,
                            end = 20.dp,
                            top = 16.dp,
                            bottom = 24.dp,
                        ),
                    ) {
                        item {
                            BitgetHomeTopBar(
                                onAvatarClick = openWalletHomeAction,
                                onProfileClick = onOpenProfile,
                            )
                        }
                        item {
                            BitgetHomePortfolioHero(
                                modifier = Modifier.padding(top = 26.dp),
                                balance = heroBalance,
                                changeText = heroChange,
                                changeLabel = heroLabel,
                                primaryActionLabel = if (isAuthenticated) "去订阅" else "去登录",
                                onPrimaryAction = if (isAuthenticated) onOpenPlans else onOpenLogin,
                            )
                        }
                        item {
                            BitgetHomeQuickLinks(
                                modifier = Modifier.padding(top = 18.dp),
                                actions = listOf(
                                    BitgetHomeQuickLink("套餐", Icons.Default.CreditCard, Info, onOpenPlans),
                                    BitgetHomeQuickLink("钱包", Icons.Default.Wallet, Color(0xFF5AD0FF), openWalletHomeAction),
                                    BitgetHomeQuickLink("订单", Icons.Default.ReceiptLong, GlowYellow, openOrdersAction),
                                    BitgetHomeQuickLink("全部", Icons.Default.Menu, Color(0xFFF2F5F8), { showHomeActionsSheet = true }),
                                ),
                            )
                        }
                        item {
                            BitgetHomeCampaignCard(
                                modifier = Modifier.padding(top = 18.dp),
                                eyebrow = "热门活动",
                                title = "热门地区不用试错，\n一键切到稳定线路",
                                description = "购买套餐、切换地区和查看订单都从同一个 Home 流里完成，保留原有业务路由不改接线。",
                                actionLabel = "立即体验",
                                onActionClick = openVpnAction,
                            )
                        }
                        item {
                            BitgetHomeOperationsHero(
                                modifier = Modifier.padding(top = 18.dp),
                                eyebrow = "运营专区",
                                title = "随开随用 · 稳定覆盖\n让线路\n每天快一点",
                                infoLabel = "快速了解套餐与地区组合",
                                onClick = onOpenPlans,
                            )
                        }
                        item {
                            BitgetHomeHighlightsRow(
                                modifier = Modifier.padding(top = 16.dp),
                                cards = listOf(
                                    BitgetHomeHighlightCard(
                                        metric = "99.95",
                                        unit = "% 在线率",
                                        title = "全球旗舰",
                                        subtitle = "48 个地区可用",
                                        badge = "G",
                                        accentColor = Color(0xFF2D82FF),
                                        onClick = onOpenPlans,
                                    ),
                                    BitgetHomeHighlightCard(
                                        metric = "34",
                                        unit = "ms 延迟",
                                        title = "亚洲加速",
                                        subtitle = "12 条热门线路",
                                        badge = "A",
                                        accentColor = Info,
                                        onClick = onOpenRegions,
                                    ),
                                ),
                            )
                        }
                        item {
                            BitgetHomeListCard(
                                modifier = Modifier.padding(top = 18.dp),
                                title = "热门地区",
                                entries = listOf(
                                    BitgetHomeListEntry(
                                        badge = "JP",
                                        title = "东京 Tokyo",
                                        subtitle = "视频 / 低延迟",
                                        metric = "34 ms",
                                        metricLabel = "热门",
                                        accentColor = Color(0xFFFFB14A),
                                        onClick = onOpenRegions,
                                    ),
                                    BitgetHomeListEntry(
                                        badge = "SG",
                                        title = "新加坡 SG",
                                        subtitle = "办公 / 稳定",
                                        metric = "41 ms",
                                        metricLabel = "推荐",
                                        accentColor = Color(0xFF3FD6E3),
                                        onClick = onOpenRegions,
                                    ),
                                    BitgetHomeListEntry(
                                        badge = "US",
                                        title = "洛杉矶 LA",
                                        subtitle = "跨区 / 大带宽",
                                        metric = "62 ms",
                                        metricLabel = "新上",
                                        accentColor = Color(0xFF6A86FF),
                                        onClick = onOpenPlans,
                                    ),
                                ),
                            )
                        }
                    }

                    BitgetHomeActionsSheet(
                        visible = showHomeActionsSheet,
                        popularActions = listOf(
                            BitgetHomeSheetAction("VPN", Icons.Default.VpnLock, Info) {
                                showHomeActionsSheet = false
                                openVpnAction()
                            },
                            BitgetHomeSheetAction("套餐", Icons.Default.CreditCard, Color(0xFF5AD0FF)) {
                                showHomeActionsSheet = false
                                onOpenPlans()
                            },
                            BitgetHomeSheetAction("订单", Icons.Default.ReceiptLong, GlowYellow) {
                                showHomeActionsSheet = false
                                openOrdersAction()
                            },
                            BitgetHomeSheetAction("邀请", Icons.Default.Groups, Color(0xFF7AB8FF)) {
                                showHomeActionsSheet = false
                                openInviteAction()
                            },
                            BitgetHomeSheetAction("帮助", Icons.Default.Info, Color(0xFFF2F5F8)) {
                                showHomeActionsSheet = false
                                onOpenSupport()
                            },
                            BitgetHomeSheetAction("法务", Icons.Default.Description, Info) {
                                showHomeActionsSheet = false
                                onOpenLegal()
                            },
                            BitgetHomeSheetAction("地区", Icons.Default.Language, Color(0xFF4DD4F5)) {
                                showHomeActionsSheet = false
                                onOpenRegions()
                            },
                        ),
                        assetActions = listOf(
                            BitgetHomeSheetAction("钱包", Icons.Default.AccountBalanceWallet, Color(0xFF5AD0FF)) {
                                showHomeActionsSheet = false
                                openWalletHomeAction()
                            },
                            BitgetHomeSheetAction("收款", Icons.Default.SouthWest, Color(0xFF49D7C3)) {
                                showHomeActionsSheet = false
                                openReceiveAction()
                            },
                            BitgetHomeSheetAction("转账", Icons.Default.Send, Color(0xFF7AB8FF)) {
                                showHomeActionsSheet = false
                                openSendAction()
                            },
                            BitgetHomeSheetAction("提现", Icons.Default.Campaign, GlowYellow) {
                                showHomeActionsSheet = false
                                openWithdrawAction()
                            },
                        ),
                        onDismiss = { showHomeActionsSheet = false },
                    )
                }
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                ) {
                    when {
                        selectedTab == ShellTab.MARKET -> MarketOverviewPage(
                            onOpenQuote = { quote -> onOpenMarketQuote(quote.instrumentId) },
                        )

                        isAuthenticated -> when (selectedTab) {
                            ShellTab.WALLET -> WalletHomePage(
                                onNavigateToReceive = onOpenReceive,
                                onNavigateToSend = onOpenSend,
                                onNavigateToAssetDetail = onOpenAssetDetail,
                                onNavigateToProfile = onOpenProfile,
                            )

                            ShellTab.VPN -> VPNHomePage(
                                onNavigateToRegions = onOpenRegions,
                                onNavigateToPlans = onOpenPlans,
                                onNavigateToProfile = onOpenProfile,
                                onNavigateToOrders = onOpenOrders,
                            )

                            ShellTab.PROFILE -> ProfilePage(
                                onNavigateToOrders = onOpenOrders,
                                onNavigateToWallet = onOpenWalletHome,
                                onNavigateToInvite = onOpenInviteCenter,
                                onNavigateToCommission = onOpenCommission,
                                onNavigateToSettings = onOpenSettings,
                                onNavigateToLegal = onOpenLegal,
                                onNavigateToSupport = onOpenSupport,
                                onNavigateToAbout = onOpenAbout,
                                onLogout = onLogout,
                            )

                            ShellTab.HOME,
                            ShellTab.MARKET -> Unit
                        }

                        else -> ShellGuestTabContent(
                            selectedTab = selectedTab,
                            onOpenWalletHome = onOpenWalletHome,
                            onOpenVpnConsole = onOpenVpnConsole,
                            onOpenProfile = onOpenProfile,
                            onOpenPlans = onOpenPlans,
                            onOpenRegions = onOpenRegions,
                            onOpenReceive = onOpenReceive,
                            onOpenSend = onOpenSend,
                            onOpenLegal = onOpenLegal,
                            onOpenSettings = onOpenSettings,
                            onOpenAbout = onOpenAbout,
                            onOpenSupport = onOpenSupport,
                        )
                    }
                }
            }
        }
    }
}

private data class ShellGuestAction(
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val accentColor: Color,
    val onClick: () -> Unit,
)

private data class ShellGuestTabState(
    val badge: String,
    val title: String,
    val description: String,
    val accentColor: Color,
    val primaryActionLabel: String,
    val onPrimaryAction: () -> Unit,
    val secondaryActionLabel: String,
    val onSecondaryAction: () -> Unit,
    val actions: List<ShellGuestAction>,
)

@Composable
private fun ShellGuestTabContent(
    selectedTab: ShellTab,
    onOpenWalletHome: () -> Unit,
    onOpenVpnConsole: () -> Unit,
    onOpenProfile: () -> Unit,
    onOpenPlans: () -> Unit,
    onOpenRegions: () -> Unit,
    onOpenReceive: () -> Unit,
    onOpenSend: () -> Unit,
    onOpenLegal: () -> Unit,
    onOpenSettings: () -> Unit,
    onOpenAbout: () -> Unit,
    onOpenSupport: () -> Unit,
) {
    if (selectedTab == ShellTab.HOME || selectedTab == ShellTab.MARKET) return

    val state = when (selectedTab) {
        ShellTab.WALLET -> ShellGuestTabState(
            badge = "Wallet Family",
            title = "Wallet tab 先展示新的资产 family 守卫",
            description = "访客态直接保留 Wallet 总览、收款与发送入口位。登录后继续留在 Wallet tab，并切到真实资产页，不再回退到旧 ShellModel 占位。",
            accentColor = GlowGreen,
            primaryActionLabel = "登录查看资产",
            onPrimaryAction = onOpenWalletHome,
            secondaryActionLabel = "收款入口",
            onSecondaryAction = onOpenReceive,
            actions = listOf(
                ShellGuestAction("Wallet 总览", "登录后进入 Wallet Home 与资产概览", Icons.Default.Wallet, GlowGreen, onOpenWalletHome),
                ShellGuestAction("收款入口", "登录后直接打开 Receive 页面", Icons.Default.SouthWest, Color(0xFF49D7C3), onOpenReceive),
                ShellGuestAction("发送入口", "登录后继续进入 Send 流程", Icons.Default.Send, GlowBlue, onOpenSend),
                ShellGuestAction("支持帮助", "遇到问题时先查看帮助入口", Icons.Default.Info, Info, onOpenSupport),
            ),
        )

        ShellTab.VPN -> ShellGuestTabState(
            badge = "VPN Family",
            title = "VPN tab 直接承接控制台 family",
            description = "访客态保留 VPN 控制台、地区与套餐守卫。登录后继续留在 VPN tab，进入真实连接首页，不再落回旧壳层动作卡。",
            accentColor = GlowBlue,
            primaryActionLabel = "登录后继续",
            onPrimaryAction = onOpenVpnConsole,
            secondaryActionLabel = "查看地区",
            onSecondaryAction = onOpenRegions,
            actions = listOf(
                ShellGuestAction("VPN 控制台", "登录后进入新的 VPN 首页", Icons.Default.VpnLock, GlowBlue, onOpenVpnConsole),
                ShellGuestAction("地区列表", "登录后切到 Region Selection", Icons.Default.Language, Info, onOpenRegions),
                ShellGuestAction("套餐列表", "先了解套餐和结算节奏", Icons.Default.CreditCard, Warning, onOpenPlans),
                ShellGuestAction("支持帮助", "查看使用说明与问题反馈", Icons.Default.Info, GlowGreen, onOpenSupport),
            ),
        )

        ShellTab.PROFILE -> ShellGuestTabState(
            badge = "Guest Mode",
            title = "Profile tab 改成设置与账户入口",
            description = "Profile 不再显示旧 ShellModel 列表。登录后进入设置页，访客态继续保留设置、关于和法务入口。",
            accentColor = Warning,
            primaryActionLabel = "登录账户",
            onPrimaryAction = onOpenProfile,
            secondaryActionLabel = "打开设置",
            onSecondaryAction = onOpenSettings,
            actions = listOf(
                ShellGuestAction("设置中心", "继续使用原有设置接线", Icons.Default.Settings, Warning, onOpenSettings),
                ShellGuestAction("关于应用", "打开 About 页面", Icons.Default.Info, GlowBlue, onOpenAbout),
                ShellGuestAction("Legal 文档", "查看条款与隐私说明", Icons.Default.Description, Info, onOpenLegal),
            ),
        )

        ShellTab.HOME,
        ShellTab.MARKET -> return
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            start = 20.dp,
            end = 20.dp,
            top = 18.dp,
            bottom = 24.dp,
        ),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(30.dp),
                color = BackgroundPrimary.copy(alpha = 0.92f),
                tonalElevation = 0.dp,
                shadowElevation = 0.dp,
                border = BorderStroke(1.dp, state.accentColor.copy(alpha = 0.28f)),
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 22.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                ) {
                    Text(
                        text = state.badge,
                        style = MaterialTheme.typography.labelMedium,
                        color = state.accentColor,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Text(
                        text = state.title,
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    Text(
                        text = state.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        Button(
                            modifier = Modifier.weight(1f),
                            onClick = state.onPrimaryAction,
                        ) {
                            Text(text = state.primaryActionLabel)
                        }
                        OutlinedButton(
                            modifier = Modifier.weight(1f),
                            onClick = state.onSecondaryAction,
                        ) {
                            Text(text = state.secondaryActionLabel)
                        }
                    }
                }
            }
        }

        items(state.actions, key = { it.title }) { action ->
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(26.dp),
                color = BackgroundPrimary.copy(alpha = 0.78f),
                tonalElevation = 0.dp,
                shadowElevation = 0.dp,
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(onClick = action.onClick)
                        .padding(horizontal = 18.dp, vertical = 18.dp),
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .background(
                                color = action.accentColor.copy(alpha = 0.18f),
                                shape = CircleShape,
                            ),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = action.icon,
                            contentDescription = action.title,
                            tint = action.accentColor,
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = action.title,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = action.subtitle,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }
        }
    }
}
