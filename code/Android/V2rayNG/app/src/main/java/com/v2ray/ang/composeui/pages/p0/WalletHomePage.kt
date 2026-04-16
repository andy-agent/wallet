package com.v2ray.ang.composeui.pages.p0

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalClipboardManager
import com.v2ray.ang.composeui.navigation.CryptoVpnRouteSpec
import com.v2ray.ang.composeui.p0.model.AssetHolding
import com.v2ray.ang.composeui.p0.model.WalletHomeEvent
import com.v2ray.ang.composeui.p0.model.WalletHomeUiState
import com.v2ray.ang.composeui.p0.model.buildWalletPortfolioValue
import com.v2ray.ang.composeui.p0.model.formatWalletAssetValueDisplay
import com.v2ray.ang.composeui.p0.model.walletHomePreviewState
import com.v2ray.ang.composeui.p0.ui.P01Card
import com.v2ray.ang.composeui.p0.ui.P01CardHeader
import com.v2ray.ang.composeui.p0.ui.P01PhoneScaffold
import com.v2ray.ang.composeui.p0.ui.P01SearchField
import com.v2ray.ang.composeui.p0.ui.P01SecondaryButton
import com.v2ray.ang.composeui.p0.viewmodel.WalletHomeViewModel
import com.v2ray.ang.composeui.theme.CryptoVpnTheme

private val WalletCardBorder = Color(0x246880DB)
private val WalletCardBackground = Color(0xF8FFFFFF)
private val WalletSoftBackground = Color(0xFFF4F8FF)
private val WalletAccentBlue = Color(0xFF3D72FF)
private val WalletAccentMint = Color(0xFF22C39D)
private val WalletAccentPink = Color(0xFFF45FA8)
private val WalletAccentDark = Color(0xFF0E2148)
private val WalletTextStrong = Color(0xFF132748)
private val WalletTextBody = Color(0xFF536A92)
private val WalletTextSoft = Color(0xFF7C90B2)
private val WalletLoss = Color(0xFFE25A63)
private val WalletGain = Color(0xFF2CB67D)

@Composable
fun WalletHomeRoute(
    currentRoute: String,
    viewModel: WalletHomeViewModel,
    onBottomNav: (String) -> Unit,
    onReceive: ((String, String) -> Unit)? = null,
    onSend: (() -> Unit)? = null,
) {
    val uiState by viewModel.uiState.collectAsState()
    val selectedChainId = uiState.selectedChainId
        .takeIf { it.isNotBlank() && it != "all" }
        ?: uiState.chains.firstOrNull()?.chainId
        ?: "tron"
    val selectedAssets = uiState.assets.filter { inferChain(it.chainLabel) == selectedChainId }
    val activeAsset = selectedAssets.firstOrNull() ?: uiState.assets.firstOrNull()
    val activeAssetId = activeAsset?.symbol ?: if (selectedChainId == "solana") "SOL" else "USDT"
    val receiveEntryRoute = resolveWalletReceiveRoute(
        walletExists = uiState.walletExists,
        nextAction = uiState.walletNextAction,
        walletId = uiState.walletId,
        assetId = activeAssetId,
        chainId = selectedChainId,
    )
    WalletHomeScreen(
        currentRoute = currentRoute,
        uiState = uiState,
        onBottomNav = onBottomNav,
        onReceive = {
            onReceive?.invoke(activeAssetId, selectedChainId) ?: onBottomNav(receiveEntryRoute)
        },
        onSend = onSend ?: { onBottomNav(CryptoVpnRouteSpec.sendRoute(activeAssetId, selectedChainId)) },
    )
}

@Composable
fun WalletHomeScreen(
    currentRoute: String,
    uiState: WalletHomeUiState,
    onBottomNav: (String) -> Unit,
    onReceive: () -> Unit,
    onSend: () -> Unit,
) {
    val clipboardManager = LocalClipboardManager.current
    val searchQuery = remember { mutableStateOf("") }
    val maskedAccount = remember(uiState.accountLabel, uiState.walletDisplayName) {
        maskAccountLabel(uiState.accountLabel, uiState.walletDisplayName)
    }
    val tokenRows = remember(uiState.assets) { buildWalletTokenRows(uiState.assets) }
    val totalValue = remember(uiState.assets) { buildPortfolioValue(uiState.assets) }
    val dailyPnl = remember(uiState.assets) { buildDailyPnl(uiState.assets) }
    val walletManagerRoute = CryptoVpnRouteSpec.walletManagerRoute(uiState.walletId ?: "primary_wallet")
    val securityCenterRoute = CryptoVpnRouteSpec.securityCenter.pattern
    val historyRoute = CryptoVpnRouteSpec.orderList.pattern
    val tokenManagerRoute = if (uiState.walletExists) {
        CryptoVpnRouteSpec.chainManagerRoute(uiState.walletId ?: "primary_wallet")
    } else {
        CryptoVpnRouteSpec.walletOnboarding.pattern
    }

    P01PhoneScaffold(
        currentRoute = currentRoute,
        onBottomNav = onBottomNav,
        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 16.dp, vertical = 12.dp),
    ) {
        BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
            val compact = maxWidth < 360.dp
            val cardSpacing = if (compact) 10.dp else 12.dp
            val actionSpacing = if (compact) 8.dp else 10.dp

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(0.dp),
            ) {
                WalletTopBar(
                    searchQuery = searchQuery.value,
                    onSearchQueryChange = { searchQuery.value = it },
                    onWalletClick = { onBottomNav(walletManagerRoute) },
                    onToolsClick = { onBottomNav(securityCenterRoute) },
                )

                Spacer(modifier = Modifier.height(14.dp))

                WalletAccountRow(
                    maskedAccount = maskedAccount,
                    onCopy = { clipboardManager.setText(AnnotatedString(uiState.accountLabel)) },
                    onSelect = { onBottomNav(walletManagerRoute) },
                )

                Spacer(modifier = Modifier.height(10.dp))

                WalletBalanceBlock(
                    totalValue = totalValue,
                    dailyPnl = dailyPnl,
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(actionSpacing),
                ) {
                    WalletQuickActionPill(
                        modifier = Modifier.weight(1f),
                        label = "转账",
                        iconText = "↗",
                        onClick = onSend,
                    )
                    WalletQuickActionPill(
                        modifier = Modifier.weight(1f),
                        label = "收款",
                        iconText = "↓",
                        onClick = onReceive,
                    )
                    WalletQuickActionPill(
                        modifier = Modifier.weight(1f),
                        label = "交易历史",
                        iconText = "L",
                        onClick = { onBottomNav(historyRoute) },
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(cardSpacing),
                ) {
                    WalletFeatureCard(
                        modifier = Modifier.weight(1f),
                        title = "订单记录",
                        value = uiState.totalBalanceText.ifBlank { "0 笔" },
                        subtitle = "查看最近交易与支付状态",
                        accent = Color(0xFFF4C38E),
                        onClick = { onBottomNav(historyRoute) },
                    )
                    WalletFeatureCard(
                        modifier = Modifier.weight(1f),
                        title = "安全中心",
                        value = if (uiState.walletExists) "已启用" else "待处理",
                        subtitle = "导出备份、清除本地钱包、退出登录",
                        accent = Color(0xFFC7E0AB),
                        onClick = { onBottomNav(securityCenterRoute) },
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                WalletSectionHeader(
                    title = "代币",
                    onManage = { onBottomNav(tokenManagerRoute) },
                )

                Spacer(modifier = Modifier.height(10.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.Transparent),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                ) {
                    tokenRows.forEach { token ->
                        WalletTokenRow(
                            token = token,
                            onClick = {
                                onBottomNav(
                                    CryptoVpnRouteSpec.assetDetailRoute(
                                        token.symbol,
                                        inferChain(token.chainLabel),
                                    ),
                                )
                            },
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                ) {
                    P01SecondaryButton(
                        text = "代币管理",
                        onClick = { onBottomNav(tokenManagerRoute) },
                    )
                }
            }
        }
    }
}

@Composable
private fun WalletTopBar(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onWalletClick: () -> Unit,
    onToolsClick: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(Color(0xFFFF9ED0), Color(0xFFFFD7E8)),
                    ),
                    shape = RoundedCornerShape(14.dp),
                )
                .clickable(onClick = onWalletClick),
            contentAlignment = Alignment.Center,
        ) {
            Text("W", color = WalletAccentDark, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 4.dp, end = 4.dp)
                    .size(8.dp)
                    .background(WalletAccentPink, CircleShape),
            )
        }

        Box(modifier = Modifier.weight(1f)) {
            P01SearchField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                placeholder = "全局搜索",
            )
        }

        Box(
            modifier = Modifier
                .size(38.dp)
                .background(WalletAccentDark, CircleShape)
                .clickable(onClick = onToolsClick),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Rounded.Tune,
                contentDescription = "功能",
                tint = Color.White,
                modifier = Modifier.size(18.dp),
            )
        }
    }
}

@Composable
private fun WalletAccountRow(
    maskedAccount: String,
    onCopy: () -> Unit,
    onSelect: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            modifier = Modifier
                .weight(1f)
                .clickable(onClick = onSelect),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = maskedAccount,
                color = WalletTextBody,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(modifier = Modifier.width(2.dp))
            Icon(
                imageVector = Icons.Rounded.KeyboardArrowDown,
                contentDescription = "切换钱包",
                tint = WalletTextSoft,
                modifier = Modifier.size(18.dp),
            )
        }
        Icon(
            imageVector = Icons.Rounded.ContentCopy,
            contentDescription = "复制账户",
            tint = WalletTextSoft,
            modifier = Modifier
                .size(18.dp)
                .clickable(onClick = onCopy),
        )
    }
}

@Composable
private fun WalletBalanceBlock(
    totalValue: String,
    dailyPnl: WalletDailyPnlUi,
) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = totalValue,
            color = WalletTextStrong,
            style = MaterialTheme.typography.displaySmall.copy(fontSize = 22.sp),
            fontWeight = FontWeight.Bold,
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = dailyPnl.percentText,
                color = if (dailyPnl.positive) WalletGain else WalletLoss,
                style = MaterialTheme.typography.titleSmall,
            )
            Text(
                text = "当日盈亏",
                color = WalletTextBody,
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}

@Composable
private fun WalletQuickActionPill(
    modifier: Modifier = Modifier,
    label: String,
    iconText: String,
    onClick: () -> Unit,
) {
    Row(
        modifier = modifier
            .background(Color.White.copy(alpha = 0.84f), RoundedCornerShape(999.dp))
            .border(1.dp, WalletCardBorder, RoundedCornerShape(999.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 11.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .background(WalletAccentDark, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Text(iconText, color = Color.White, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = label,
            color = WalletTextStrong,
            style = MaterialTheme.typography.labelLarge,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun WalletFeatureCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    subtitle: String,
    accent: Color,
    onClick: () -> Unit,
) {
    P01Card(
        modifier = modifier.clickable(onClick = onClick),
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .background(accent.copy(alpha = 0.24f), RoundedCornerShape(12.dp)),
        )
        Text(
            text = title,
            color = WalletTextBody,
            style = MaterialTheme.typography.bodySmall,
        )
        Text(
            text = value,
            color = WalletTextStrong,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
        )
        Text(
            text = subtitle,
            color = WalletTextSoft,
            style = MaterialTheme.typography.bodySmall,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun WalletSectionHeader(
    title: String,
    onManage: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = title,
            color = WalletTextStrong,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
        )
        Box(
            modifier = Modifier
                .size(34.dp)
                .background(WalletSoftBackground, RoundedCornerShape(12.dp))
                .border(1.dp, WalletCardBorder, RoundedCornerShape(12.dp))
                .clickable(onClick = onManage),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Rounded.Tune,
                contentDescription = "代币管理",
                tint = WalletTextStrong,
                modifier = Modifier.size(16.dp),
            )
        }
    }
}

@Composable
private fun WalletTokenRow(
    token: WalletTokenRowUi,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White.copy(alpha = 0.62f), RoundedCornerShape(20.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 4.dp, vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .background(token.iconBackground, CircleShape)
                .border(1.dp, token.iconBorder, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = token.iconText,
                color = Color.White,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            Text(
                text = token.symbol,
                color = WalletTextStrong,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = token.marketText,
                    color = WalletTextBody,
                    style = MaterialTheme.typography.bodySmall,
                )
                Text(
                    text = token.changeText,
                    color = if (token.positive) WalletGain else WalletLoss,
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        }
        Column(
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            Text(
                text = token.balanceText,
                color = WalletTextStrong,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = token.valueText,
                color = WalletTextSoft,
                style = MaterialTheme.typography.bodySmall,
            )
        }
    }
}

private data class WalletTokenMeta(
    val symbol: String,
    val chainLabel: String,
    val marketText: String,
    val changeText: String,
    val positive: Boolean,
    val iconText: String,
    val iconBackground: Color,
    val iconBorder: Color,
)

private data class WalletTokenRowUi(
    val symbol: String,
    val chainLabel: String,
    val marketText: String,
    val changeText: String,
    val positive: Boolean,
    val balanceText: String,
    val valueText: String,
    val iconText: String,
    val iconBackground: Color,
    val iconBorder: Color,
)

private data class WalletDailyPnlUi(
    val percentText: String,
    val positive: Boolean,
)

private fun buildWalletTokenRows(assets: List<AssetHolding>): List<WalletTokenRowUi> {
    val defaults = listOf(
        WalletTokenMeta("SOL", "Solana", "$85.49", "+2.72%", true, "S", Color(0xFF111827), Color(0xFF2C3C5B)),
        WalletTokenMeta("USDC", "USDC", "$1", "0.00%", true, "$", Color(0xFF2A7BE4), Color(0xFF7FB1F7)),
        WalletTokenMeta("USDT", "USDT", "$1", "-0.02%", false, "T", Color(0xFF14A38B), Color(0xFF80D4C7)),
    )

    return defaults.map { meta ->
        val asset = assets.firstOrNull { it.symbol.equals(meta.symbol, ignoreCase = true) }
        WalletTokenRowUi(
            symbol = meta.symbol,
            chainLabel = asset?.chainLabel ?: meta.chainLabel,
            marketText = meta.marketText,
            changeText = meta.changeText,
            positive = meta.positive,
            balanceText = extractDisplayNumber(asset?.balanceText),
            valueText = extractDisplayUsd(asset?.valueText),
            iconText = meta.iconText,
            iconBackground = meta.iconBackground,
            iconBorder = meta.iconBorder,
        )
    }
}

private fun buildPortfolioValue(assets: List<AssetHolding>): String {
    return buildWalletPortfolioValue(assets)
}

private fun buildDailyPnl(assets: List<AssetHolding>): WalletDailyPnlUi {
    val positive = assets.count { it.changePositive } >= (assets.size / 2)
    return WalletDailyPnlUi(
        percentText = if (positive) "(+0.00%)" else "(-0.00%)",
        positive = positive,
    )
}

private fun extractDisplayNumber(raw: String?): String {
    val match = Regex("""-?\d+(?:[.,]\d+)?""").find(raw.orEmpty())?.value ?: return "0.00"
    return match.replace(",", "")
}

private fun extractDisplayUsd(raw: String?): String {
    return formatWalletAssetValueDisplay(raw)
}

private fun maskAccountLabel(accountLabel: String, walletDisplayName: String?): String {
    val trimmed = accountLabel.trim().ifBlank { "当前账户" }
    val masked = if (trimmed.contains("@")) {
        val parts = trimmed.split("@", limit = 2)
        val local = parts[0]
        val domain = parts[1]
        val localPrefix = local.take(3)
        val localSuffix = local.takeLast(2).takeIf { local.length > 5 }.orEmpty()
        val maskedLocal = if (local.length <= 3) {
            "$localPrefix***"
        } else {
            "$localPrefix***$localSuffix"
        }
        val domainHead = domain.take(4)
        "$maskedLocal@$domainHead..."
    } else {
        trimmed.take(4) + "***"
    }
    val suffix = walletDisplayName?.take(4)?.takeIf { it.isNotBlank() } ?: "主钱包"
    return "$masked（$suffix）"
}

private fun inferChain(chainLabel: String): String = when {
    chainLabel.contains("sol", ignoreCase = true) -> "solana"
    chainLabel.contains("eth", ignoreCase = true) -> "ethereum"
    chainLabel.contains("base", ignoreCase = true) -> "base"
    else -> "tron"
}

private fun resolveWalletReceiveRoute(
    walletExists: Boolean,
    nextAction: String,
    walletId: String?,
    assetId: String,
    chainId: String,
): String {
    return when {
        !walletExists -> CryptoVpnRouteSpec.walletOnboarding.pattern
        nextAction.equals("BACKUP_MNEMONIC", ignoreCase = true) && !walletId.isNullOrBlank() ->
            CryptoVpnRouteSpec.backupMnemonicRoute(walletId)
        nextAction.equals("CONFIRM_MNEMONIC", ignoreCase = true) && !walletId.isNullOrBlank() ->
            CryptoVpnRouteSpec.confirmMnemonicRoute(walletId)
        else -> CryptoVpnRouteSpec.receiveRoute(assetId, chainId)
    }
}

@Preview(showBackground = true, widthDp = 393, heightDp = 852)
@Composable
private fun WalletHomePreview() {
    CryptoVpnTheme {
        WalletHomeScreen(
            currentRoute = CryptoVpnRouteSpec.walletHome.name,
            uiState = walletHomePreviewState(),
            onBottomNav = {},
            onReceive = {},
            onSend = {},
        )
    }
}
