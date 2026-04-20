package com.v2ray.ang.composeui.pages.p0

import android.graphics.BitmapFactory
import android.widget.Toast
import androidx.compose.foundation.Image
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
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.v2ray.ang.R
import com.v2ray.ang.composeui.navigation.CryptoVpnRouteSpec
import com.v2ray.ang.composeui.p0.model.AssetHolding
import com.v2ray.ang.composeui.p0.model.WalletHomeEvent
import com.v2ray.ang.composeui.p0.model.WalletHomeWalletOption
import com.v2ray.ang.composeui.p0.model.WalletHomeChainOption
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
import java.io.File

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
    val context = LocalContext.current
    val selectedChainId = uiState.selectedChainId
        .takeIf { it.isNotBlank() && it != "all" }
        ?: uiState.walletOptions
            .firstOrNull { it.walletId == uiState.selectedWalletId }
            ?.chainOptions
            ?.firstOrNull()
            ?.chainId
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
        onRefresh = { viewModel.onEvent(WalletHomeEvent.Refresh) },
        onWalletContextSelected = { walletId, chainId ->
            viewModel.onEvent(WalletHomeEvent.WalletContextSelected(walletId, chainId))
        },
        onCopyAddress = {
            if (uiState.currentWalletAddress.isNotBlank()) {
                Toast.makeText(context, "钱包地址已复制", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "当前链暂无可复制地址", Toast.LENGTH_SHORT).show()
            }
        },
        onCreateWallet = { onBottomNav(CryptoVpnRouteSpec.createWalletRoute("create")) },
        onOpenProfile = { onBottomNav(CryptoVpnRouteSpec.profile.pattern) },
        onOpenSecurityCenter = { onBottomNav(CryptoVpnRouteSpec.securityCenter.pattern) },
        onOpenInviteCenter = { onBottomNav(CryptoVpnRouteSpec.inviteCenter.pattern) },
        onClearLocalWallet = {
            viewModel.clearLocalWallet(
                onSuccess = { message -> Toast.makeText(context, message, Toast.LENGTH_SHORT).show() },
                onError = { message -> Toast.makeText(context, message, Toast.LENGTH_SHORT).show() },
            )
        },
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
    onRefresh: () -> Unit,
    onWalletContextSelected: (String, String) -> Unit,
    onCopyAddress: () -> Unit,
    onCreateWallet: () -> Unit,
    onOpenProfile: () -> Unit,
    onOpenSecurityCenter: () -> Unit,
    onOpenInviteCenter: () -> Unit,
    onClearLocalWallet: () -> Unit,
    onReceive: () -> Unit,
    onSend: () -> Unit,
) {
    val clipboardManager = LocalClipboardManager.current
    val searchQuery = remember { mutableStateOf("") }
    var walletMenuExpanded by remember { mutableStateOf(false) }
    var showWalletSelector by remember { mutableStateOf(false) }
    var pendingChainWallet by remember { mutableStateOf<WalletHomeWalletOption?>(null) }
    val selectedChainId = uiState.selectedChainId
        .takeIf { it.isNotBlank() && it != "all" }
        ?: uiState.walletOptions
            .firstOrNull { it.walletId == uiState.selectedWalletId }
            ?.chainOptions
            ?.firstOrNull()
            ?.chainId
        ?: uiState.chains.firstOrNull()?.chainId
        ?: "tron"
    val selectedAssets = uiState.assets.filter { inferChain(it.chainLabel) == selectedChainId }
    val tokenRows = remember(selectedAssets) { buildWalletTokenRows(selectedAssets) }
    val totalValue = uiState.totalPortfolioValueText
    val dailyPnl = remember(selectedAssets) { buildDailyPnl(selectedAssets) }
    val securityCenterRoute = CryptoVpnRouteSpec.securityCenter.pattern
    val historyRoute = CryptoVpnRouteSpec.orderList.pattern
    val effectiveWalletId = uiState.selectedWalletId
        ?: uiState.walletOptions.firstOrNull()?.walletId
        ?: uiState.walletId
    val canOpenTokenManager = !effectiveWalletId.isNullOrBlank() || uiState.walletOptions.isNotEmpty()
    val tokenManagerRoute = if (canOpenTokenManager) {
        CryptoVpnRouteSpec.tokenManagerRoute(
            effectiveWalletId ?: "primary_wallet",
            selectedChainId,
        )
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
                    avatarLabel = uiState.currentWalletLabel.firstOrNull()?.uppercase() ?: "W",
                    onWalletClick = { showWalletSelector = true },
                    menuExpanded = walletMenuExpanded,
                    onMenuExpandedChange = { walletMenuExpanded = it },
                    onCreateWallet = {
                        walletMenuExpanded = false
                        onCreateWallet()
                    },
                    onClearLocalWallet = {
                        walletMenuExpanded = false
                        onClearLocalWallet()
                    },
                    onOpenProfile = {
                        walletMenuExpanded = false
                        onOpenProfile()
                    },
                    onOpenSecurityCenter = {
                        walletMenuExpanded = false
                        onOpenSecurityCenter()
                    },
                    onOpenInviteCenter = {
                        walletMenuExpanded = false
                        onOpenInviteCenter()
                    },
                )

                Spacer(modifier = Modifier.height(14.dp))

                WalletAccountRow(
                    walletLabel = uiState.currentWalletLabel,
                    chainLabel = uiState.currentWalletChainLabel,
                    chainId = uiState.selectedChainId,
                    addressSuffix = uiState.currentWalletAddressSuffix,
                    secondaryLabel = uiState.accountSecondaryLabel,
                    onCopy = {
                        if (uiState.currentWalletAddress.isNotBlank()) {
                            clipboardManager.setText(AnnotatedString(uiState.currentWalletAddress))
                            onCopyAddress()
                        } else {
                            onCopyAddress()
                        }
                    },
                    onSelect = { showWalletSelector = true },
                )

                Spacer(modifier = Modifier.height(10.dp))

                WalletBalanceBlock(
                    totalValue = totalValue,
                    dailyPnl = dailyPnl,
                    updatedLabel = uiState.priceUpdatedLabel,
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
                        label = "交易",
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
                        title = "交易",
                        value = uiState.totalBalanceText.ifBlank { "0 笔" },
                        subtitle = "只显示成功交易",
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
                    onRefresh = onRefresh,
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

    if (showWalletSelector) {
        WalletSelectionDialog(
            walletOptions = uiState.walletOptions,
            onDismiss = { showWalletSelector = false },
            onWalletSelected = { wallet ->
                if (wallet.chainOptions.size <= 1) {
                    wallet.chainOptions.firstOrNull()?.let { chain ->
                        onWalletContextSelected(wallet.walletId, chain.chainId)
                    }
                    showWalletSelector = false
                } else {
                    pendingChainWallet = wallet
                    showWalletSelector = false
                }
            },
        )
    }

    pendingChainWallet?.let { wallet ->
        ChainSelectionDialog(
            wallet = wallet,
            onDismiss = { pendingChainWallet = null },
            onChainSelected = { chain ->
                onWalletContextSelected(wallet.walletId, chain.chainId)
                pendingChainWallet = null
            },
        )
    }
}

@Composable
private fun WalletTopBar(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    avatarLabel: String,
    onWalletClick: () -> Unit,
    menuExpanded: Boolean,
    onMenuExpandedChange: (Boolean) -> Unit,
    onCreateWallet: () -> Unit,
    onClearLocalWallet: () -> Unit,
    onOpenProfile: () -> Unit,
    onOpenSecurityCenter: () -> Unit,
    onOpenInviteCenter: () -> Unit,
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
            Text(avatarLabel, color = WalletAccentDark, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
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

        Box {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .background(WalletAccentDark, CircleShape)
                    .clickable(onClick = { onMenuExpandedChange(true) }),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Rounded.Tune,
                    contentDescription = "钱包菜单",
                    tint = Color.White,
                    modifier = Modifier.size(18.dp),
                )
            }
            DropdownMenu(
                expanded = menuExpanded,
                onDismissRequest = { onMenuExpandedChange(false) },
            ) {
                DropdownMenuItem(
                    text = { Text("创建钱包") },
                    onClick = onCreateWallet,
                )
                DropdownMenuItem(
                    text = { Text("清除本地钱包") },
                    onClick = onClearLocalWallet,
                )
                DropdownMenuItem(
                    text = { Text("个人中心") },
                    onClick = onOpenProfile,
                )
                DropdownMenuItem(
                    text = { Text("安全中心") },
                    onClick = onOpenSecurityCenter,
                )
                DropdownMenuItem(
                    text = { Text("邀请中心") },
                    onClick = onOpenInviteCenter,
                )
            }
        }
    }
}

@Composable
private fun WalletAccountRow(
    walletLabel: String,
    chainLabel: String,
    chainId: String,
    addressSuffix: String,
    secondaryLabel: String,
    onCopy: () -> Unit,
    onSelect: () -> Unit,
) {
    val displayLabel = buildString {
        append(walletLabel.ifBlank { "未选择钱包" })
        if (chainLabel.isNotBlank()) {
            append(" · ")
            append(chainLabel)
        }
        if (addressSuffix.isNotBlank()) {
            append(" · ...")
            append(addressSuffix)
        }
    }
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(2.dp),
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
                ChainBadgeIcon(chainId = chainId, size = 20.dp)
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = displayLabel,
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
                contentDescription = "复制钱包地址",
                tint = WalletTextSoft,
                modifier = Modifier
                    .size(18.dp)
                    .clickable(onClick = onCopy),
            )
        }
        if (secondaryLabel.isNotBlank()) {
            Text(
                text = secondaryLabel,
                color = WalletTextSoft,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
private fun WalletSelectionDialog(
    walletOptions: List<WalletHomeWalletOption>,
    onDismiss: () -> Unit,
    onWalletSelected: (WalletHomeWalletOption) -> Unit,
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
        ),
    ) {
        P01Card {
            Text(
                text = "选择钱包",
                color = WalletTextStrong,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
            )
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                walletOptions.forEach { wallet ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onWalletSelected(wallet) }
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(2.dp),
                        ) {
                            Text(wallet.walletName, color = WalletTextStrong, style = MaterialTheme.typography.titleMedium)
                            Text(wallet.walletKind, color = WalletTextSoft, style = MaterialTheme.typography.bodySmall)
                        }
                        if (wallet.isDefault) {
                            Text("默认", color = WalletAccentBlue, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ChainSelectionDialog(
    wallet: WalletHomeWalletOption,
    onDismiss: () -> Unit,
    onChainSelected: (WalletHomeChainOption) -> Unit,
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
        ),
    ) {
        P01Card {
            Text(
                text = "选择链",
                color = WalletTextStrong,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = wallet.walletName,
                color = WalletTextSoft,
                style = MaterialTheme.typography.bodySmall,
            )
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                wallet.chainOptions.forEach { chain ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onChainSelected(chain) }
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Row(
                            modifier = Modifier.weight(1f),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            ChainBadgeIcon(chainId = chain.chainId, size = 24.dp)
                            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                Text(chain.label, color = WalletTextStrong, style = MaterialTheme.typography.titleMedium)
                                Text("...${chain.addressSuffix}", color = WalletTextSoft, style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ChainBadgeIcon(
    chainId: String,
    size: androidx.compose.ui.unit.Dp,
) {
    val iconRes = chainIconRes(chainId)
    if (iconRes != null) {
        Image(
            painter = painterResource(id = iconRes),
            contentDescription = chainId,
            modifier = Modifier.size(size),
        )
    } else {
        Box(
            modifier = Modifier
                .size(size)
                .background(WalletSoftBackground, CircleShape)
                .border(1.dp, WalletCardBorder, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = chainId.take(1).uppercase(),
                color = WalletTextStrong,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

@Composable
private fun WalletBalanceBlock(
    totalValue: String,
    dailyPnl: WalletDailyPnlUi,
    updatedLabel: String,
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
                color = when {
                    dailyPnl.percentText == "暂无报价" -> WalletTextSoft
                    dailyPnl.positive -> WalletGain
                    else -> WalletLoss
                },
                style = MaterialTheme.typography.titleSmall,
            )
            Text(
                text = updatedLabel.ifBlank { "价格待同步" },
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
    onRefresh: () -> Unit,
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
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
            Row(
                modifier = Modifier
                    .background(WalletSoftBackground, RoundedCornerShape(999.dp))
                    .border(1.dp, WalletCardBorder, RoundedCornerShape(999.dp))
                    .clickable(onClick = onRefresh)
                    .padding(horizontal = 10.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "刷新",
                    color = WalletTextStrong,
                    style = MaterialTheme.typography.labelMedium,
                )
            }
            Row(
                modifier = Modifier
                    .background(WalletSoftBackground, RoundedCornerShape(999.dp))
                    .border(1.dp, WalletCardBorder, RoundedCornerShape(999.dp))
                    .clickable(onClick = onManage)
                    .padding(horizontal = 10.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector = Icons.Rounded.Tune,
                    contentDescription = "代币管理",
                    tint = WalletTextStrong,
                    modifier = Modifier.size(14.dp),
                )
                Text(
                    text = "管理",
                    color = WalletTextStrong,
                    style = MaterialTheme.typography.labelMedium,
                )
            }
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
        TokenRowIcon(token)
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
            Text(
                text = token.tokenName,
                color = WalletTextBody,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = "${token.unitPriceText} · ${token.changeText}",
                color = if (token.changeText == "暂无报价") WalletTextSoft else if (token.changePositive) WalletGain else WalletLoss,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
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

private data class WalletTokenRowUi(
    val symbol: String,
    val chainLabel: String,
    val tokenName: String,
    val balanceText: String,
    val valueText: String,
    val unitPriceText: String,
    val changeText: String,
    val changePositive: Boolean,
    val priceStatusText: String,
    val iconChainId: String,
    val iconLocalPath: String? = null,
)

private data class WalletDailyPnlUi(
    val percentText: String,
    val positive: Boolean,
)

private fun buildWalletTokenRows(assets: List<AssetHolding>): List<WalletTokenRowUi> {
    return assets.map { asset ->
        WalletTokenRowUi(
            symbol = asset.symbol,
            chainLabel = asset.chainLabel,
            tokenName = asset.detailText.ifBlank { asset.symbol },
            balanceText = asset.balanceText,
            valueText = asset.valueText,
            unitPriceText = asset.unitPriceText,
            changeText = asset.changeText,
            changePositive = asset.changePositive,
            priceStatusText = asset.priceStatusText,
            iconChainId = inferChain(asset.chainLabel),
            iconLocalPath = asset.iconLocalPath,
        )
    }
}

@Composable
private fun TokenRowIcon(token: WalletTokenRowUi) {
    val bitmap = token.iconLocalPath
        ?.takeIf { it.isNotBlank() && File(it).exists() }
        ?.let { BitmapFactory.decodeFile(it)?.asImageBitmap() }
    if (bitmap != null) {
        Image(
            bitmap = bitmap,
            contentDescription = token.symbol,
            modifier = Modifier.size(42.dp),
        )
        return
    }
    ChainBadgeIcon(
        chainId = token.iconChainId,
        size = 42.dp,
    )
}

private fun buildDailyPnl(assets: List<AssetHolding>): WalletDailyPnlUi {
    val priced = assets.mapNotNull { asset ->
        val pct = asset.changeText.removeSuffix("%").replace("+", "").toDoubleOrNull()
        val value = asset.valueText.replace("$", "").replace(",", "").toDoubleOrNull()
        if (pct == null || value == null) null else pct to value
    }
    if (priced.isEmpty()) {
        return WalletDailyPnlUi(percentText = "暂无报价", positive = true)
    }
    val total = priced.sumOf { it.second }
    if (total <= 0.0) {
        return WalletDailyPnlUi(percentText = "暂无报价", positive = true)
    }
    val weighted = priced.sumOf { (pct, value) -> pct * value } / total
    return WalletDailyPnlUi(
        percentText = "%+.2f%%".format(weighted),
        positive = weighted >= 0,
    )
}

private fun inferChain(chainLabel: String): String = when {
    chainLabel.contains("sol", ignoreCase = true) -> "solana"
    chainLabel.contains("eth", ignoreCase = true) -> "ethereum"
    chainLabel.contains("bsc", ignoreCase = true) -> "bsc"
    chainLabel.contains("polygon", ignoreCase = true) -> "polygon"
    chainLabel.contains("arbitrum", ignoreCase = true) -> "arbitrum"
    chainLabel.contains("optimism", ignoreCase = true) -> "optimism"
    chainLabel.contains("avalanche", ignoreCase = true) -> "avalanche"
    chainLabel.contains("base", ignoreCase = true) -> "base"
    else -> "tron"
}

private fun chainIconRes(chainId: String): Int? = when (chainId.lowercase()) {
    "solana" -> R.drawable.chain_solana
    "tron" -> R.drawable.chain_tron
    "ethereum" -> R.drawable.chain_ethereum
    "bsc", "smartchain" -> R.drawable.chain_bsc
    "polygon" -> R.drawable.chain_polygon
    "arbitrum" -> R.drawable.chain_arbitrum
    "optimism" -> R.drawable.chain_optimism
    "avalanche", "avalanche_c" -> R.drawable.chain_avalanche
    "base" -> R.drawable.chain_base
    else -> null
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
            onRefresh = {},
            onWalletContextSelected = { _, _ -> },
            onCopyAddress = {},
            onCreateWallet = {},
            onOpenProfile = {},
            onOpenSecurityCenter = {},
            onOpenInviteCenter = {},
            onClearLocalWallet = {},
            onReceive = {},
            onSend = {},
        )
    }
}
