package com.v2ray.ang.ui.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material.icons.filled.SouthWest
import androidx.compose.material.icons.filled.VpnLock
import androidx.compose.material.icons.filled.Wallet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.v2ray.ang.composeui.components.navigation.BitgetBottomNavigationBar
import com.v2ray.ang.composeui.components.navigation.ShellBottomBarItem
import com.v2ray.ang.composeui.components.shell.BitgetAccountHeader
import com.v2ray.ang.composeui.components.shell.BitgetActionGrid
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
import com.v2ray.ang.composeui.components.shell.BitgetSectionTitle
import com.v2ray.ang.composeui.components.shell.BitgetShowcaseCard
import com.v2ray.ang.composeui.components.shell.BitgetTickerStrip
import com.v2ray.ang.composeui.components.shell.ShellMetric
import com.v2ray.ang.composeui.components.shell.ShellQuickAction
import com.v2ray.ang.composeui.components.shell.ShellTickerItem
import com.v2ray.ang.composeui.navigation.ShellTab
import com.v2ray.ang.composeui.theme.BackgroundDeepest
import com.v2ray.ang.composeui.theme.BackgroundPrimary
import com.v2ray.ang.composeui.theme.GlowBlue
import com.v2ray.ang.composeui.theme.GlowGreen
import com.v2ray.ang.composeui.theme.GlowYellow
import com.v2ray.ang.composeui.theme.Info
import com.v2ray.ang.composeui.theme.Primary
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
    onOpenAssetBook: () -> Unit,
    onOpenInviteCenter: () -> Unit,
    onOpenCommission: () -> Unit,
    onOpenWithdraw: () -> Unit,
    onOpenProfile: () -> Unit,
    onOpenLegal: () -> Unit,
    onOpenSettings: () -> Unit,
    onOpenAbout: () -> Unit,
    onOpenSupport: () -> Unit,
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
            ShellBottomBarItem(tab = ShellTab.WALLET, title = "Wallet", icon = Icons.Default.AccountBalanceWallet),
            ShellBottomBarItem(tab = ShellTab.VPN, title = "VPN", icon = Icons.Default.VpnLock),
            ShellBottomBarItem(tab = ShellTab.DISCOVER, title = "Discover", icon = Icons.Default.Language),
            ShellBottomBarItem(tab = ShellTab.PROFILE, title = "Profile", icon = Icons.Default.Person),
        )
    }

    val model = if (selectedTab == ShellTab.HOME) {
        null
    } else {
        buildShellModel(
            selectedTab = selectedTab,
            isAuthenticated = isAuthenticated,
            onOpenLogin = onOpenLogin,
            onOpenVpnConsole = onOpenVpnConsole,
            onOpenPlans = onOpenPlans,
            onOpenRegions = onOpenRegions,
            onOpenOrders = onOpenOrders,
            onOpenWalletHome = onOpenWalletHome,
            onOpenReceive = onOpenReceive,
            onOpenSend = onOpenSend,
            onOpenAssetBook = onOpenAssetBook,
            onOpenInviteCenter = onOpenInviteCenter,
            onOpenCommission = onOpenCommission,
            onOpenWithdraw = onOpenWithdraw,
            onOpenProfile = onOpenProfile,
            onOpenLegal = onOpenLegal,
            onOpenSettings = onOpenSettings,
            onOpenAbout = onOpenAbout,
            onOpenSupport = onOpenSupport,
        )
    }

    val backgroundColors = if (selectedTab == ShellTab.HOME) {
        listOf(
            Color(0xFF091112),
            Color(0xFF0A1011),
            BackgroundPrimary,
        )
    } else {
        listOf(
            BackgroundDeepest,
            BackgroundPrimary,
            BackgroundPrimary,
        )
    }
    val firstGlow = if (selectedTab == ShellTab.HOME) {
        Color(0x221AD6F1)
    } else {
        GlowGreen.copy(alpha = 0.22f)
    }
    val secondGlow = if (selectedTab == ShellTab.HOME) {
        Color(0x14F39B5C)
    } else {
        GlowBlue.copy(alpha = 0.16f)
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
                model?.let { shellModel ->
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        contentPadding = PaddingValues(
                            start = 20.dp,
                            end = 20.dp,
                            top = 16.dp,
                            bottom = 18.dp,
                        ),
                    ) {
                        item {
                            BitgetAccountHeader(
                                badge = shellModel.badge,
                                title = shellModel.title,
                                subtitle = shellModel.subtitle,
                                metrics = shellModel.metrics,
                                primaryActionLabel = shellModel.primaryActionLabel,
                                onPrimaryAction = shellModel.onPrimaryAction,
                                secondaryActionLabel = shellModel.secondaryActionLabel,
                                onSecondaryAction = shellModel.onSecondaryAction,
                            )
                        }
                        item {
                            BitgetTickerStrip(
                                modifier = Modifier.padding(top = 16.dp),
                                items = shellModel.tickers,
                            )
                        }
                        item {
                            BitgetSectionTitle(
                                modifier = Modifier.padding(top = 22.dp),
                                title = shellModel.sectionTitle,
                                subtitle = shellModel.sectionSubtitle,
                            )
                        }
                        item {
                            BitgetActionGrid(
                                modifier = Modifier.padding(top = 14.dp),
                                actions = shellModel.actions,
                            )
                        }
                        item {
                            BitgetShowcaseCard(
                                modifier = Modifier.padding(top = 18.dp),
                                eyebrow = shellModel.showcaseEyebrow,
                                title = shellModel.showcaseTitle,
                                body = shellModel.showcaseBody,
                                actionLabel = shellModel.showcaseActionLabel,
                                onActionClick = shellModel.onShowcaseAction,
                            )
                        }
                        items(shellModel.notes) { note ->
                            Text(
                                modifier = Modifier.padding(top = 14.dp),
                                text = "• $note",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                }
            }
        }
    }
}

private data class ShellModel(
    val badge: String,
    val title: String,
    val subtitle: String,
    val metrics: List<ShellMetric>,
    val tickers: List<ShellTickerItem>,
    val sectionTitle: String,
    val sectionSubtitle: String,
    val actions: List<ShellQuickAction>,
    val primaryActionLabel: String,
    val onPrimaryAction: () -> Unit,
    val secondaryActionLabel: String?,
    val onSecondaryAction: (() -> Unit)?,
    val showcaseEyebrow: String,
    val showcaseTitle: String,
    val showcaseBody: String,
    val showcaseActionLabel: String,
    val onShowcaseAction: () -> Unit,
    val notes: List<String>,
)

private fun buildShellModel(
    selectedTab: ShellTab,
    isAuthenticated: Boolean,
    onOpenLogin: () -> Unit,
    onOpenVpnConsole: () -> Unit,
    onOpenPlans: () -> Unit,
    onOpenRegions: () -> Unit,
    onOpenOrders: () -> Unit,
    onOpenWalletHome: () -> Unit,
    onOpenReceive: () -> Unit,
    onOpenSend: () -> Unit,
    onOpenAssetBook: () -> Unit,
    onOpenInviteCenter: () -> Unit,
    onOpenCommission: () -> Unit,
    onOpenWithdraw: () -> Unit,
    onOpenProfile: () -> Unit,
    onOpenLegal: () -> Unit,
    onOpenSettings: () -> Unit,
    onOpenAbout: () -> Unit,
    onOpenSupport: () -> Unit,
): ShellModel {
    val sessionBadge = if (isAuthenticated) "Account Live" else "Guest Mode"
    return when (selectedTab) {
        ShellTab.HOME -> ShellModel(
            badge = sessionBadge,
            title = if (isAuthenticated) "首页切到钱包优先的资产分发层" else "先浏览新首页，再按需解锁钱包与 VPN",
            subtitle = if (isAuthenticated) {
                "Home 先展示资产总览、收发入口和账户概览，再把 VPN、订单与套餐分发到原有业务页。"
            } else {
                "未登录也能浏览 Bitget 风格首页层级；涉及钱包和订单的动作会按需引导登录。"
            },
            metrics = listOf(
                ShellMetric("资产层", if (isAuthenticated) "Wallet Ready" else "Guest"),
                ShellMetric("收付", "Receive / Send"),
                ShellMetric("桥接", "Preserved"),
            ),
            tickers = listOf(
                ShellTickerItem("HOME", "Assets"),
                ShellTickerItem("WALLET", "Live"),
                ShellTickerItem("VPN", "Linked"),
            ),
            sectionTitle = "首页快捷操作",
            sectionSubtitle = "首页优先露出钱包资产动作，再把 VPN、订单等存量路由收敛进统一的 Bitget 风格入口。",
            actions = listOf(
                ShellQuickAction("钱包主页", "进入 Wallet Home", Icons.Default.AccountBalanceWallet, Info, onOpenWalletHome),
                ShellQuickAction("收款地址", "直接打开 Receive", Icons.Default.SouthWest, Primary, onOpenReceive),
                ShellQuickAction("发送资产", "进入 Send 流程", Icons.Default.Send, GlowBlue, onOpenSend),
                ShellQuickAction("VPN 控制台", "保持原有连接页面", Icons.Default.VpnLock, Warning, onOpenVpnConsole),
            ),
            primaryActionLabel = if (isAuthenticated) "打开钱包" else "登录账户",
            onPrimaryAction = if (isAuthenticated) onOpenWalletHome else onOpenLogin,
            secondaryActionLabel = if (isAuthenticated) "查看订单" else "查看套餐",
            onSecondaryAction = if (isAuthenticated) onOpenOrders else onOpenPlans,
            showcaseEyebrow = "Home Overview",
            showcaseTitle = "首页先给出资产卡与收发起点",
            showcaseBody = "Launcher 先落到新的 Home 资产分发层，再从快捷动作进入 Wallet、Receive、Send、VPN 等原有业务路由，不触碰 bridge 和数据层。",
            showcaseActionLabel = if (isAuthenticated) "打开收款" else "前往登录",
            onShowcaseAction = if (isAuthenticated) onOpenReceive else onOpenLogin,
            notes = listOf(
                "Home 仍是壳层入口，但视觉顺序改成 Bitget Wallet 常见的资产总览 -> 快捷操作 -> 路由分发。",
                "业务状态与视觉状态继续解耦，避免因改样式触碰仓储与网络层。",
            ),
        )

        ShellTab.WALLET -> ShellModel(
            badge = sessionBadge,
            title = if (isAuthenticated) "Wallet 作为资产中心与收付中枢" else "登录后启用钱包资产、收款与发送起点",
            subtitle = "Wallet 负责承接钱包主页、资产详情、收款、发送等入口，底层页面继续复用原路由与 placeholder。",
            metrics = listOf(
                ShellMetric("资产", if (isAuthenticated) "Visible" else "Sign In"),
                ShellMetric("明细", "Asset Detail"),
                ShellMetric("收付", "Ready"),
            ),
            tickers = listOf(
                ShellTickerItem("WALLET", "Assets"),
                ShellTickerItem("RECEIVE", "Direct"),
                ShellTickerItem("SEND", "Bridge"),
            ),
            sectionTitle = "钱包快捷操作",
            sectionSubtitle = "Wallet tab 直接承接资产主页、收付款与资产详情入口，同时保留原有页面与导航回调。",
            actions = listOf(
                ShellQuickAction("打开钱包", "进入钱包主页", Icons.Default.Wallet, Primary, onOpenWalletHome),
                ShellQuickAction("收款地址", "直接打开 Receive", Icons.Default.SouthWest, GlowGreen, onOpenReceive),
                ShellQuickAction("发送资产", "进入 Send 流程", Icons.Default.Send, Info, onOpenSend),
                ShellQuickAction("资产明细", "查看资产账本", Icons.Default.AccountBalanceWallet, Warning, onOpenAssetBook),
            ),
            primaryActionLabel = if (isAuthenticated) "进入钱包" else "登录账户",
            onPrimaryAction = if (isAuthenticated) onOpenWalletHome else onOpenLogin,
            secondaryActionLabel = if (isAuthenticated) "收款" else "查看支持",
            onSecondaryAction = if (isAuthenticated) onOpenReceive else onOpenSupport,
            showcaseEyebrow = "Wallet Surface",
            showcaseTitle = "资产首页、详情与收发起点已并入统一风格",
            showcaseBody = "钱包分区继续保留 `wallet_home / receive / send / asset_detail` 路由，只重构外层视觉、文案层级和默认入口顺序。",
            showcaseActionLabel = if (isAuthenticated) "打开资产页" else "前往登录",
            onShowcaseAction = if (isAuthenticated) onOpenAssetBook else onOpenLogin,
            notes = listOf(
                "Guest 态下保留钱包外壳，但所有资产动作仍会引导登录。",
                "底层支付与钱包仓储未做修改，只有 Compose 表层重排。",
            ),
        )

        ShellTab.VPN -> ShellModel(
            badge = sessionBadge,
            title = if (isAuthenticated) "VPN 控制与订阅交易舱" else "登录后进入 VPN 控制台与订单流",
            subtitle = "VPN tab 直连 VPN、Regions、Plans、Orders，维持核心购买与连接流程的原有页面。",
            metrics = listOf(
                ShellMetric("状态", if (isAuthenticated) "Ready" else "Guarded"),
                ShellMetric("套餐", "Plans"),
                ShellMetric("订单", "Orders"),
            ),
            tickers = listOf(
                ShellTickerItem("VPN", "Console"),
                ShellTickerItem("PLANS", "Live"),
                ShellTickerItem("REGION", "Fast"),
            ),
            sectionTitle = "VPN 动作区",
            sectionSubtitle = "这里专门承接连接、地区、套餐和订单操作，确保底部导航里有独立 VPN 标签。",
            actions = listOf(
                ShellQuickAction("VPN 控制台", "进入连接主页面", Icons.Default.VpnLock, Primary, onOpenVpnConsole),
                ShellQuickAction("地区选择", "快速切到 Region Selection", Icons.Default.Language, Info, onOpenRegions),
                ShellQuickAction("订阅套餐", "查看 Plans 列表", Icons.Default.CreditCard, Warning, onOpenPlans),
                ShellQuickAction("订单中心", "查看已下单与待支付订单", Icons.Default.ReceiptLong, GlowBlue, onOpenOrders),
            ),
            primaryActionLabel = if (isAuthenticated) "进入 VPN" else "登录账户",
            onPrimaryAction = if (isAuthenticated) onOpenVpnConsole else onOpenLogin,
            secondaryActionLabel = "查看套餐",
            onSecondaryAction = onOpenPlans,
            showcaseEyebrow = "VPN Desk",
            showcaseTitle = "连接与订阅流量都从壳层统一承接",
            showcaseBody = "从这里可进入 VPN 首页、地区选择、套餐页和订单页；连接、订单结果与支付结果回跳也会回到新壳。",
            showcaseActionLabel = if (isAuthenticated) "打开订单" else "前往登录",
            onShowcaseAction = if (isAuthenticated) onOpenOrders else onOpenLogin,
            notes = listOf(
                "保持 VPN 业务页原样，壳层只负责统一入口和回跳目的地。",
                "未登录态点击受保护动作会先进入认证，再回到目标页。",
            ),
        )

        ShellTab.DISCOVER -> ShellModel(
            badge = sessionBadge,
            title = if (isAuthenticated) "Discover 与增长分发层" else "Discover 聚合增长、法务与支持入口",
            subtitle = "Discover 负责把 Growth 相关页面和公共信息入口收束到独立底部标签里。",
            metrics = listOf(
                ShellMetric("增长", "Invite"),
                ShellMetric("返佣", "Ledger"),
                ShellMetric("状态", if (isAuthenticated) "Live" else "Locked"),
            ),
            tickers = listOf(
                ShellTickerItem("DISCOVER", "Ready"),
                ShellTickerItem("GROWTH", "Linked"),
                ShellTickerItem("WITHDRAW", "Guarded"),
            ),
            sectionTitle = "Discover 动作区",
            sectionSubtitle = "增长、返佣、提现、法务与支持入口统一在 Discover 内分发，不改现有 Growth 页面源码。",
            actions = listOf(
                ShellQuickAction("邀请中心", "打开 Invite Center", Icons.Default.Groups, Primary, onOpenInviteCenter),
                ShellQuickAction("佣金台账", "查看返佣流水", Icons.Default.ShowChart, GlowBlue, onOpenCommission),
                ShellQuickAction("提现申请", "进入 Withdraw", Icons.Default.CreditCard, Warning, onOpenWithdraw),
                ShellQuickAction("法务文档", "查看条款与隐私", Icons.Default.Description, Info, onOpenLegal),
            ),
            primaryActionLabel = if (isAuthenticated) "打开邀请中心" else "登录账户",
            onPrimaryAction = if (isAuthenticated) onOpenInviteCenter else onOpenLogin,
            secondaryActionLabel = "支持帮助",
            onSecondaryAction = onOpenSupport,
            showcaseEyebrow = "Discover Deck",
            showcaseTitle = "Growth 页面通过 Discover 统一暴露",
            showcaseBody = "邀请中心、佣金台账和提现流程仍由现有 growth/profile/legal 页面承接，壳层只负责更强的视觉骨架和入口组织。",
            showcaseActionLabel = if (isAuthenticated) "打开佣金台账" else "查看法务",
            onShowcaseAction = if (isAuthenticated) onOpenCommission else onOpenLegal,
            notes = listOf(
                "Discover 不是新业务页，而是既有 Growth 与公共入口的壳层分发器。",
                "支持与法务保留访客可见，受保护增长页仍按需登录。",
            ),
        )

        ShellTab.PROFILE -> ShellModel(
            badge = sessionBadge,
            title = if (isAuthenticated) "账户设置与资料入口" else "访客也可进入设置与法务入口",
            subtitle = "把个人资料、设置、法务、关于等入口沉到底部导航的最后一个标签。",
            metrics = listOf(
                ShellMetric("配置", "Settings"),
                ShellMetric("文档", "Legal"),
                ShellMetric("会话", if (isAuthenticated) "Profile On" else "Guest"),
            ),
            tickers = listOf(
                ShellTickerItem("PROFILE", "Compose"),
                ShellTickerItem("LEGAL", "Ready"),
                ShellTickerItem("ABOUT", "Legacy"),
            ),
            sectionTitle = "账户动作区",
            sectionSubtitle = "资料页与设置页仍保留各自业务实现，外壳负责统一入口与视觉语言。",
            actions = listOf(
                ShellQuickAction("个人资料", "打开原有 Profile", Icons.Default.Person, Primary, onOpenProfile),
                ShellQuickAction("设置中心", "打开原生 Settings", Icons.Default.Settings, Warning, onOpenSettings),
                ShellQuickAction("关于应用", "打开 About", Icons.Default.Info, GlowBlue, onOpenAbout),
                ShellQuickAction("法务文档", "查看条款与隐私", Icons.Default.Description, Info, onOpenLegal),
            ),
            primaryActionLabel = if (isAuthenticated) "打开资料页" else "登录账户",
            onPrimaryAction = if (isAuthenticated) onOpenProfile else onOpenLogin,
            secondaryActionLabel = "支持帮助",
            onSecondaryAction = onOpenSupport,
            showcaseEyebrow = "Account Layer",
            showcaseTitle = "顶部账户区与底部导航已成统一外壳",
            showcaseBody = "从这里可以进入设置、法务、关于或个人资料。即便未登录，也不会破坏现有业务路由与遗留 Activity 的接线。",
            showcaseActionLabel = if (isAuthenticated) "打开设置" else "查看法务",
            onShowcaseAction = if (isAuthenticated) onOpenSettings else onOpenLegal,
            notes = listOf(
                "访客态保留设置、关于、法务入口。",
                "登录后仍可继续进入原有 Profile 页面完成登出等动作。",
            ),
        )
    }
}
