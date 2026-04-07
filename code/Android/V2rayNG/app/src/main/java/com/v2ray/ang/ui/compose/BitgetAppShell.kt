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
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.v2ray.ang.composeui.components.navigation.BitgetBottomNavigationBar
import com.v2ray.ang.composeui.components.navigation.ShellBottomBarItem
import com.v2ray.ang.composeui.components.shell.BitgetAccountHeader
import com.v2ray.ang.composeui.components.shell.BitgetActionGrid
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
    val navItems = remember {
        listOf(
            ShellBottomBarItem(tab = ShellTab.HOME, title = "Home", icon = Icons.Default.Home),
            ShellBottomBarItem(tab = ShellTab.WALLET, title = "Wallet", icon = Icons.Default.AccountBalanceWallet),
            ShellBottomBarItem(tab = ShellTab.VPN, title = "VPN", icon = Icons.Default.VpnLock),
            ShellBottomBarItem(tab = ShellTab.DISCOVER, title = "Discover", icon = Icons.Default.Language),
            ShellBottomBarItem(tab = ShellTab.PROFILE, title = "Profile", icon = Icons.Default.Person),
        )
    }

    val model = buildShellModel(
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

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        BackgroundDeepest,
                        BackgroundPrimary,
                        BackgroundPrimary,
                    ),
                ),
            ),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(GlowGreen.copy(alpha = 0.22f), Color.Transparent),
                        radius = 900f,
                    ),
                ),
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(GlowBlue.copy(alpha = 0.16f), Color.Transparent),
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
                        badge = model.badge,
                        title = model.title,
                        subtitle = model.subtitle,
                        metrics = model.metrics,
                        primaryActionLabel = model.primaryActionLabel,
                        onPrimaryAction = model.onPrimaryAction,
                        secondaryActionLabel = model.secondaryActionLabel,
                        onSecondaryAction = model.onSecondaryAction,
                    )
                }
                item {
                    BitgetTickerStrip(
                        modifier = Modifier.padding(top = 16.dp),
                        items = model.tickers,
                    )
                }
                item {
                    BitgetSectionTitle(
                        modifier = Modifier.padding(top = 22.dp),
                        title = model.sectionTitle,
                        subtitle = model.sectionSubtitle,
                    )
                }
                item {
                    BitgetActionGrid(
                        modifier = Modifier.padding(top = 14.dp),
                        actions = model.actions,
                    )
                }
                item {
                    BitgetShowcaseCard(
                        modifier = Modifier.padding(top = 18.dp),
                        eyebrow = model.showcaseEyebrow,
                        title = model.showcaseTitle,
                        body = model.showcaseBody,
                        actionLabel = model.showcaseActionLabel,
                        onActionClick = model.onShowcaseAction,
                    )
                }
                items(model.notes) { note ->
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
            title = if (isAuthenticated) "Bitget 风格统一主壳已接管入口" else "先进入新壳，再按需解锁业务页",
            subtitle = if (isAuthenticated) {
                "Home 聚合 VPN、套餐、订单与钱包入口，底层页面与数据桥接保持原样。"
            } else {
                "未登录也能浏览 Bitget 风格主壳；需要账户态的页面会按需引导登录。"
            },
            metrics = listOf(
                ShellMetric("会话", if (isAuthenticated) "已同步" else "未登录"),
                ShellMetric("布局", "Bitget Dark"),
                ShellMetric("入口", "Launcher"),
            ),
            tickers = listOf(
                ShellTickerItem("HOME", "Ready"),
                ShellTickerItem("VPN", "Direct"),
                ShellTickerItem("ORDER", "Linked"),
            ),
            sectionTitle = "主壳动作区",
            sectionSubtitle = "这里负责把现有 VPN、Plans、Orders、Wallet 路由收敛成一个首页交易台式入口。",
            actions = listOf(
                ShellQuickAction("VPN 控制台", "进入原有连接页面", Icons.Default.VpnLock, Primary, onOpenVpnConsole),
                ShellQuickAction("订阅套餐", "查看计划与下单", Icons.Default.CreditCard, Warning, onOpenPlans),
                ShellQuickAction("订单中心", "跳转订单列表", Icons.Default.ReceiptLong, GlowBlue, onOpenOrders),
                ShellQuickAction("钱包主页", "进入 Wallet Home", Icons.Default.AccountBalanceWallet, Info, onOpenWalletHome),
            ),
            primaryActionLabel = if (isAuthenticated) "打开 VPN" else "登录账户",
            onPrimaryAction = if (isAuthenticated) onOpenVpnConsole else onOpenLogin,
            secondaryActionLabel = if (isAuthenticated) "打开钱包" else "查看套餐",
            onSecondaryAction = if (isAuthenticated) onOpenWalletHome else onOpenPlans,
            showcaseEyebrow = "Home Shell",
            showcaseTitle = "Launcher 已进入新壳层",
            showcaseBody = "桌面入口先落到这个 Home 壳层，再从卡片和 quick actions 分发到原有业务页面，不侵入页面文件与数据层。",
            showcaseActionLabel = if (isAuthenticated) "进入订单" else "前往登录",
            onShowcaseAction = if (isAuthenticated) onOpenOrders else onOpenLogin,
            notes = listOf(
                "Home 只做入口聚合，不直接重写 VPN、Wallet、Growth、Profile 页面内容。",
                "壳层状态与业务状态解耦，避免因视觉改造触碰仓储与网络层。",
            ),
        )

        ShellTab.WALLET -> ShellModel(
            badge = sessionBadge,
            title = if (isAuthenticated) "钱包与收付壳层" else "登录后启用钱包收付与资产页",
            subtitle = "Wallet 负责承接钱包主页、收款、转账与资产详情入口，页面实现仍复用原路由。",
            metrics = listOf(
                ShellMetric("资产", if (isAuthenticated) "Wallet On" else "Sign In"),
                ShellMetric("收款", "Receive"),
                ShellMetric("转账", "Send"),
            ),
            tickers = listOf(
                ShellTickerItem("WALLET", "Ready"),
                ShellTickerItem("RECEIVE", "Direct"),
                ShellTickerItem("SEND", "Ready"),
            ),
            sectionTitle = "资产动作区",
            sectionSubtitle = "不改钱包页面源码，只在壳层上补齐钱包入口与 Bitget 风格视觉骨架。",
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
            showcaseTitle = "资产入口已收敛到壳层导航",
            showcaseBody = "钱包分区保留原有 `wallet_home / receive / send / asset_detail` 路由，只调整默认入口和外层视觉风格。",
            showcaseActionLabel = if (isAuthenticated) "打开资产页" else "前往登录",
            onShowcaseAction = if (isAuthenticated) onOpenAssetBook else onOpenLogin,
            notes = listOf(
                "Guest 态下保留钱包外壳，但动作会引导登录。",
                "底层支付与钱包仓储未做修改。",
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
