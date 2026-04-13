package com.v2ray.ang.composeui.pages.p0

import android.net.VpnService
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.v2ray.ang.composeui.effects.MotionProfile
import com.v2ray.ang.composeui.effects.TechParticleBackground
import com.v2ray.ang.composeui.navigation.CryptoVpnRouteSpec
import com.v2ray.ang.composeui.p0.model.RegionSpeed
import com.v2ray.ang.composeui.p0.model.VpnConnectionStatus
import com.v2ray.ang.composeui.p0.model.VpnHomeEvent
import com.v2ray.ang.composeui.p0.model.VpnHomeUiState
import com.v2ray.ang.composeui.p0.model.WatchSignal
import com.v2ray.ang.composeui.p0.repository.MockP0Repository
import com.v2ray.ang.composeui.p0.ui.P01BottomIcon
import com.v2ray.ang.composeui.p0.ui.P01BottomIconKind
import com.v2ray.ang.composeui.theme.CryptoVpnTheme
import com.v2ray.ang.composeui.p0.viewmodel.VpnHomeViewModel
import com.v2ray.ang.dto.SubscriptionItem
import com.v2ray.ang.fmt.VlessFmt
import com.v2ray.ang.handler.AngConfigManager
import com.v2ray.ang.handler.MmkvManager
import com.v2ray.ang.handler.SettingsManager
import com.v2ray.ang.handler.V2RayServiceManager
import com.v2ray.ang.payment.data.api.VpnConfigIssueData
import com.v2ray.ang.payment.data.repository.PaymentRepository
import com.v2ray.ang.util.Utils
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

private val OverviewPageBackground = Brush.verticalGradient(
    colors = listOf(Color(0xFFF7FBFF), Color(0xFFEAF5FF), Color(0xFFEAF7FF)),
)
private val OverviewHeaderBlue = Color(0xFF7A8EF6)
private val OverviewInk = Color(0xFF152749)
private val OverviewBody = Color(0xFF6D7E9D)
private val OverviewMuted = Color(0xFFA4B0C6)
private val OverviewBorder = Color(0x145C82D8)
private val OverviewCard = Color(0xF9FFFFFF)
private val OverviewBlue = Color(0xFF4C77FF)
private val OverviewCyan = Color(0xFF25CFF0)
private val OverviewGreen = Color(0xFF38D69F)
private val OverviewOrange = Color(0xFFFF9551)
private val OverviewGradient = Brush.horizontalGradient(listOf(OverviewBlue, OverviewCyan))
private val OverviewPageHorizontal = 20.dp
private val OverviewTopSpacing = 10.dp
private val OverviewCardRadius = 28.dp
private val OverviewInputRadius = 28.dp
private val OverviewSearchHeight = 58.dp
private val OverviewHeroCardHeight = 322.dp
private val OverviewOrbSize = 152.dp
private val OverviewStatCardHeight = 74.dp
private val OverviewActionCardHeight = 98.dp
private val OverviewButtonHeight = 54.dp
private val OverviewAlertMinHeight = 84.dp
private val SearchHeight = OverviewSearchHeight

private data class OverviewAction(
    val label: String,
    val glyph: OverviewGlyph,
    val tint: Color,
    val onClick: () -> Unit,
)

private data class OverviewAlert(
    val title: String,
    val subtitle: String,
    val badge: String,
    val glyph: OverviewGlyph,
    val positive: Boolean,
    val onClick: () -> Unit,
)

private enum class OverviewGlyph {
    SEARCH,
    RECEIVE,
    SEND,
    REGION,
    PLAN,
    STATUS,
    ORDER,
    VLESS,
}

@Composable
fun VpnHomeRoute(
    currentRoute: String,
    viewModel: VpnHomeViewModel,
    onBottomNav: (String) -> Unit,
    onWalletHome: () -> Unit,
    onPlans: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val paymentRepository = remember { PaymentRepository(context.applicationContext) }
    val requestVpnPermission = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
    ) { result ->
        if (result.resultCode == android.app.Activity.RESULT_OK) {
            V2RayServiceManager.startVService(context)
        }
        viewModel.onEvent(VpnHomeEvent.Refresh)
    }

    fun importIssuedVpnConfig(config: VpnConfigIssueData): Boolean {
        val profile = VlessFmt.parse(config.configPayload) ?: return false
        val subscriptionId = Utils.getUuid()
        MmkvManager.encodeSubscription(
            subscriptionId,
            SubscriptionItem(
                remarks = "Purchase ${config.regionCode}",
                url = config.configPayload,
                enabled = true,
                lastUpdated = System.currentTimeMillis(),
                autoUpdate = false,
            ),
        )
        profile.subscriptionId = subscriptionId
        profile.remarks = "Purchase ${config.regionCode}"
        profile.description = AngConfigManager.generateDescription(profile)
        val guid = MmkvManager.encodeServerConfig("", profile)
        MmkvManager.setSelectServer(guid)
        return true
    }

    fun startVpnAfterConfigReady() {
        if (SettingsManager.isVpnMode()) {
            val prepareIntent = VpnService.prepare(context)
            if (prepareIntent == null) {
                V2RayServiceManager.startVService(context)
                viewModel.onEvent(VpnHomeEvent.Refresh)
            } else {
                requestVpnPermission.launch(prepareIntent)
            }
        } else {
            V2RayServiceManager.startVService(context)
            viewModel.onEvent(VpnHomeEvent.Refresh)
        }
    }

    fun toggleRealConnection() {
        if (V2RayServiceManager.isRunning()) {
            V2RayServiceManager.stopVService(context)
            viewModel.onEvent(VpnHomeEvent.Refresh)
            return
        }

        if (MmkvManager.getSelectServer().isNullOrEmpty()) {
            scope.launch {
                val subscription = paymentRepository.getSubscription().getOrNull()
                val subscriptionUrl =
                    paymentRepository.getSavedSubscriptionUrl()?.takeIf { it.isNotBlank() }
                        ?: subscription?.subscriptionUrl?.takeIf { it.isNotBlank() }

                if (!subscriptionUrl.isNullOrBlank()) {
                    val imported = paymentRepository.importSubscriptionUrl(
                        subscriptionUrl = subscriptionUrl,
                        remarks = subscription?.planCode?.takeIf { it.isNotBlank() }
                            ?.let { "Purchase $it" }
                            ?: "CryptoVPN Subscription",
                    )
                    if (imported) {
                        startVpnAfterConfigReady()
                        return@launch
                    }
                }
                Toast.makeText(context, "订阅待同步，请重新登录或稍后重试。", Toast.LENGTH_SHORT).show()
                onPlans()
            }
            return
        }

        startVpnAfterConfigReady()
    }

    LaunchedEffect(Unit) {
        while (isActive) {
            delay(1500)
            viewModel.onEvent(VpnHomeEvent.Refresh)
        }
    }

    VpnHomeScreen(
        currentRoute = currentRoute,
        uiState = uiState,
        onToggleConnection = ::toggleRealConnection,
        onSelectRegion = { viewModel.onEvent(VpnHomeEvent.RegionSelected(it)) },
        onBottomNav = onBottomNav,
        onWalletHome = onWalletHome,
        onPlans = onPlans,
    )
}

@Composable
fun VpnHomeScreen(
    currentRoute: String,
    uiState: VpnHomeUiState,
    onToggleConnection: () -> Unit,
    onSelectRegion: (RegionSpeed) -> Unit,
    onBottomNav: (String) -> Unit,
    onWalletHome: () -> Unit,
    onPlans: () -> Unit,
) {
    val actions = listOf(
        OverviewAction("收款", OverviewGlyph.RECEIVE, OverviewBlue) {
            onBottomNav(CryptoVpnRouteSpec.receiveRoute("USDT", "tron"))
        },
        OverviewAction("发送", OverviewGlyph.SEND, OverviewBlue) {
            onBottomNav(CryptoVpnRouteSpec.sendRoute("USDT", "tron"))
        },
        OverviewAction("选区", OverviewGlyph.REGION, OverviewBlue) {
            onBottomNav(CryptoVpnRouteSpec.regionSelection.pattern)
        },
        OverviewAction("买套餐", OverviewGlyph.PLAN, OverviewBlue) {
            onPlans()
        },
    )
    val alerts = buildOverviewAlerts(
        uiState = uiState,
        onBottomNav = onBottomNav,
        onWalletHome = onWalletHome,
    )

    OverviewPageScaffold(
        currentRoute = currentRoute,
        onBottomNav = onBottomNav,
    ) {
        OverviewHeader(alertCount = maxOf(uiState.alertCount, alerts.count { it.badge.isNotBlank() }))
        OverviewSearchBar()
        SecureTunnelCard(
            uiState = uiState,
            onSwitchNode = {
                onSelectRegion(uiState.selectedRegion)
                onBottomNav(CryptoVpnRouteSpec.regionSelection.pattern)
            },
            onReconnect = onToggleConnection,
        )
        OverviewSectionHeader(title = "快捷动作")
        OverviewQuickActions(actions = actions)
        OverviewSectionHeader(title = "实时提醒")
        OverviewAlerts(alerts = alerts)
    }
}

@Composable
private fun OverviewPageScaffold(
    currentRoute: String,
    onBottomNav: (String) -> Unit,
    content: @Composable ColumnScope.() -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(OverviewPageBackground),
    ) {
        TechParticleBackground(
            motionProfile = MotionProfile.L1,
            modifier = Modifier.fillMaxSize(),
            showNetwork = true,
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(Color(0x22AEEBFF), Color.Transparent),
                        center = Offset(560f, 1080f),
                        radius = 820f,
                    ),
                ),
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(horizontal = OverviewPageHorizontal, vertical = 10.dp),
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                content = content,
            )
            Spacer(modifier = Modifier.height(10.dp))
            OverviewBottomNav(
                currentRoute = currentRoute,
                onBottomNav = onBottomNav,
            )
        }
    }
}

@Composable
private fun OverviewHeader(alertCount: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top,
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = "CONTROL PLANE",
                color = OverviewHeaderBlue,
                fontSize = 14.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 1.6.sp,
            )
            Text(
                text = "网络与资产总览",
                color = OverviewInk,
                fontSize = 30.sp,
                fontWeight = FontWeight.Black,
                lineHeight = 34.sp,
            )
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            }
        }
        Spacer(modifier = Modifier.width(12.dp))
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(18.dp))
                .background(Color(0xFFFFF0E7))
                .border(1.dp, Color(0x14FF9A58), RoundedCornerShape(18.dp))
                .padding(horizontal = 10.dp, vertical = 7.dp),
        ) {
            Text(
                text = "${maxOf(alertCount, 0)} 条实时提醒",
                color = OverviewOrange,
                fontSize = 12.sp,
                fontWeight = FontWeight.ExtraBold,
            )
        }
    }
}

@Composable
private fun OverviewSearchBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(OverviewSearchHeight)
            .shadow(10.dp, RoundedCornerShape(OverviewInputRadius), ambientColor = Color(0x11000000), spotColor = Color(0x11000000))
            .clip(RoundedCornerShape(OverviewInputRadius))
            .background(Color.White.copy(alpha = 0.96f))
            .border(1.dp, OverviewBorder, RoundedCornerShape(OverviewInputRadius))
            .padding(horizontal = 14.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(Color(0xFFEAF2FF)),
            contentAlignment = Alignment.Center,
        ) {
            OverviewGlyphIcon(
                glyph = OverviewGlyph.SEARCH,
                tint = OverviewBlue,
                modifier = Modifier.size(18.dp),
            )
        }
        Text(
            text = "搜索节点 / 币种 / 订单 / 标签",
            modifier = Modifier.weight(1f),
            color = OverviewMuted,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(18.dp))
                .background(Color(0xFFEAF0FF))
                .padding(horizontal = 12.dp, vertical = 8.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "智能筛选",
                color = OverviewBlue,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

@Composable
private fun SecureTunnelCard(
    uiState: VpnHomeUiState,
    onSwitchNode: () -> Unit,
    onReconnect: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(OverviewHeroCardHeight)
            .shadow(12.dp, RoundedCornerShape(OverviewCardRadius), ambientColor = Color(0x12000000), spotColor = Color(0x12000000))
            .clip(RoundedCornerShape(OverviewCardRadius))
            .background(OverviewCard)
            .border(1.dp, OverviewBorder, RoundedCornerShape(OverviewCardRadius))
            .padding(horizontal = 16.dp, vertical = 16.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = OverviewButtonHeight + 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "Secure Tunnel",
                    color = OverviewInk,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                )
                StatusPill(
                    text = "${connectionChipLabel(uiState)} · ${uiState.vlessRegionLabel}",
                    positive = uiState.connectionStatus != VpnConnectionStatus.DISCONNECTED,
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.Top,
            ) {
                TunnelOrb(
                    primaryText = orbPrimaryText(uiState),
                    secondaryText = orbSecondaryText(uiState),
                    status = uiState.connectionStatus,
                )
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Text(
                        text = uiState.overviewValueText,
                        color = OverviewInk,
                        fontSize = 44.sp,
                        fontWeight = FontWeight.Black,
                        lineHeight = 48.sp,
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = "总资产",
                            color = OverviewBody,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                        )
                        Text(
                            text = "•",
                            color = OverviewMuted,
                            fontSize = 16.sp,
                        )
                        Text(
                            text = overviewCaption(uiState),
                            color = if (isOrdersResolved(uiState)) OverviewGreen else OverviewMuted,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        CompactStatCard(
                            modifier = Modifier.weight(1f),
                            label = "活跃链路",
                            value = uiState.speedNodes.size.toString(),
                        )
                        CompactStatCard(
                            modifier = Modifier.weight(1f),
                            label = "节点健康",
                            value = "${uiState.nodeHealthPercent}%",
                        )
                    }
                }
            }
        }

        Row(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            SecondaryTunnelButton(
                modifier = Modifier.weight(1f),
                text = "切换节点",
                onClick = onSwitchNode,
            )
            PrimaryTunnelButton(
                modifier = Modifier.weight(1f),
                text = primaryTunnelButtonLabel(uiState),
                onClick = onReconnect,
            )
        }
    }
}
@Composable
private fun TunnelOrb(
    primaryText: String,
    secondaryText: String,
    status: VpnConnectionStatus,
) {
    Box(
        modifier = Modifier.size((OverviewOrbSize.value - 10).dp),
        contentAlignment = Alignment.Center,
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val outerStroke = 2.dp.toPx()
            val dash = PathEffect.dashPathEffect(floatArrayOf(6.dp.toPx(), 4.dp.toPx()))
            drawCircle(
                brush = Brush.sweepGradient(listOf(Color(0x334E7CFF), Color(0x225DE8FF), Color(0x334E7CFF))),
                radius = size.minDimension * 0.48f,
                style = Stroke(width = outerStroke, pathEffect = dash),
            )
            drawCircle(
                color = Color(0x22A3C7FF),
                radius = size.minDimension * 0.43f,
                style = Stroke(width = 3.dp.toPx()),
            )
            drawCircle(
                brush = Brush.sweepGradient(listOf(Color(0xFFA580FF), Color(0xFF63C8FF), Color(0xFF4F79FF), Color(0xFFA580FF))),
                radius = size.minDimension * 0.36f,
                style = Stroke(width = 14.dp.toPx()),
            )
            drawCircle(
                color = Color.White,
                radius = size.minDimension * 0.22f,
                style = Fill,
            )
        }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(22.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFF2F7FF)),
                contentAlignment = Alignment.Center,
            ) {
                P01BottomIcon(
                    kind = P01BottomIconKind.VPN,
                    tint = when (status) {
                        VpnConnectionStatus.CONNECTED -> OverviewGreen
                        VpnConnectionStatus.CONNECTING -> OverviewCyan
                        VpnConnectionStatus.DISCONNECTED -> OverviewBlue
                    },
                    modifier = Modifier.size(14.dp),
                )
            }
            Text(
                text = primaryText,
                color = OverviewBlue,
                fontSize = 16.sp,
                fontWeight = FontWeight.Black,
            )
            Text(
                text = secondaryText,
                color = OverviewBody,
                fontSize = 11.sp,
                textAlign = TextAlign.Center,
                lineHeight = 14.sp,
            )
        }
    }
}

@Composable
private fun CompactStatCard(
    modifier: Modifier = Modifier,
    label: String,
    value: String,
    compact: Boolean = true,
) {
    Column(
        modifier = modifier
            .height(58.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0xFFFDFEFF))
            .border(1.dp, OverviewBorder, RoundedCornerShape(20.dp))
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
            Text(
                text = label,
                color = OverviewMuted,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = value,
                color = OverviewInk,
                fontSize = 18.sp,
                fontWeight = FontWeight.Black,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun SecondaryTunnelButton(
    modifier: Modifier = Modifier,
    text: String,
    onClick: () -> Unit,
) {
    Box(
        modifier = modifier
            .height(OverviewButtonHeight)
            .shadow(4.dp, RoundedCornerShape(18.dp), ambientColor = Color(0x0A000000), spotColor = Color(0x0A000000))
            .clip(RoundedCornerShape(18.dp))
            .background(Color(0xFFFDFEFF))
            .border(1.dp, Color(0xFFE7EDF7), RoundedCornerShape(18.dp))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            color = OverviewInk,
            fontSize = 18.sp,
            fontWeight = FontWeight.Black,
            maxLines = 1,
            overflow = TextOverflow.Clip,
        )
    }
}

@Composable
private fun PrimaryTunnelButton(
    modifier: Modifier = Modifier,
    text: String,
    onClick: () -> Unit,
) {
    Box(
        modifier = modifier
            .height(OverviewButtonHeight)
            .shadow(6.dp, RoundedCornerShape(18.dp), ambientColor = Color(0x12000000), spotColor = Color(0x12000000))
            .clip(RoundedCornerShape(18.dp))
            .background(OverviewGradient)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.Black,
            maxLines = 1,
            overflow = TextOverflow.Clip,
        )
    }
}

@Composable
private fun OverviewSectionHeader(title: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = title,
            color = OverviewInk,
            fontSize = 18.sp,
            fontWeight = FontWeight.Black,
        )
    }
}

@Composable
private fun OverviewQuickActions(actions: List<OverviewAction>) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        actions.forEach { action ->
            Column(
                modifier = Modifier
                    .weight(1f)
                    .height(OverviewActionCardHeight)
                    .shadow(6.dp, RoundedCornerShape(22.dp), ambientColor = Color(0x0F000000), spotColor = Color(0x0F000000))
                    .clip(RoundedCornerShape(22.dp))
                    .background(Color.White.copy(alpha = 0.96f))
                    .border(1.dp, OverviewBorder, RoundedCornerShape(22.dp))
                    .clickable(onClick = action.onClick)
                    .padding(vertical = 14.dp, horizontal = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(action.tint.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center,
                ) {
                    OverviewGlyphIcon(
                        glyph = action.glyph,
                        tint = action.tint,
                        modifier = Modifier.size(18.dp),
                    )
                }
                Text(
                    text = action.label,
                    color = OverviewInk,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                )
            }
        }
    }
}

@Composable
private fun OverviewAlerts(alerts: List<OverviewAlert>) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        alerts.forEach { alert ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .defaultMinSize(minHeight = OverviewAlertMinHeight)
                    .shadow(4.dp, RoundedCornerShape(22.dp), ambientColor = Color(0x0E000000), spotColor = Color(0x0E000000))
                    .clip(RoundedCornerShape(22.dp))
                    .background(Color.White.copy(alpha = 0.96f))
                    .border(1.dp, OverviewBorder, RoundedCornerShape(22.dp))
                    .clickable(onClick = alert.onClick)
                    .padding(horizontal = 14.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(
                            if (alert.positive) {
                                Color(0xFFEAFBF3)
                            } else {
                                Color(0xFFFFF2EA)
                            },
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    OverviewGlyphIcon(
                        glyph = alert.glyph,
                        tint = if (alert.positive) OverviewGreen else OverviewOrange,
                        modifier = Modifier.size(16.dp),
                    )
                }
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Text(
                        text = alert.title,
                        color = OverviewInk,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black,
                    )
                    Text(
                        text = alert.subtitle,
                        color = OverviewBody,
                        fontSize = 12.sp,
                        lineHeight = 16.sp,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                StatusPill(
                    text = alert.badge,
                    positive = alert.positive,
                )
            }
        }
    }
}

@Composable
private fun StatusPill(
    text: String,
    positive: Boolean,
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(
                if (positive) {
                    Color(0xFFE9FBF3)
                } else {
                    Color(0xFFEAF0FF)
                },
            )
            .padding(horizontal = 10.dp, vertical = 6.dp),
    ) {
        Text(
            text = text,
            color = if (positive) Color(0xFF1DAA73) else OverviewBlue,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
private fun OverviewBottomNav(
    currentRoute: String,
    onBottomNav: (String) -> Unit,
) {
    val activeRoute = resolveBottomRoute(currentRoute)
    val items = listOf(
        Triple("vpn_home", "总览", P01BottomIconKind.OVERVIEW),
        Triple("plans", "VPN", P01BottomIconKind.VPN),
        Triple("wallet_home", "钱包", P01BottomIconKind.WALLET),
        Triple("invite_center", "增长", P01BottomIconKind.GROWTH),
        Triple("profile", "我的", P01BottomIconKind.PROFILE),
    )
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .shadow(8.dp, RoundedCornerShape(28.dp), ambientColor = Color(0x12000000), spotColor = Color(0x12000000))
            .clip(RoundedCornerShape(28.dp))
            .background(Color.White.copy(alpha = 0.96f))
            .border(1.dp, OverviewBorder, RoundedCornerShape(28.dp))
            .padding(horizontal = 8.dp, vertical = 3.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        items.forEach { (route, label, kind) ->
            val active = activeRoute == route
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(18.dp))
                    .background(if (active) Color(0xFFF0F5FF) else Color.Transparent)
                    .clickable { onBottomNav(route) }
                    .padding(vertical = 4.dp, horizontal = 4.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .clip(CircleShape)
                        .background(if (active) Color(0xFFEAF0FF) else Color.Transparent),
                    contentAlignment = Alignment.Center,
                ) {
                    P01BottomIcon(
                        kind = kind,
                        tint = if (active) OverviewBlue else OverviewMuted,
                    )
                }
                Text(
                    text = label,
                    color = if (active) OverviewBlue else OverviewBody,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}

@Composable
private fun OverviewGlyphIcon(
    glyph: OverviewGlyph,
    tint: Color,
    modifier: Modifier = Modifier,
) {
    Canvas(modifier = modifier) {
        val stroke = Stroke(
            width = 1.9.dp.toPx(),
            cap = StrokeCap.Round,
        )
        when (glyph) {
            OverviewGlyph.SEARCH -> {
                drawCircle(
                    color = tint,
                    radius = size.minDimension * 0.28f,
                    center = Offset(size.width * 0.42f, size.height * 0.42f),
                    style = stroke,
                )
                drawLine(
                    color = tint,
                    start = Offset(size.width * 0.6f, size.height * 0.6f),
                    end = Offset(size.width * 0.84f, size.height * 0.84f),
                    strokeWidth = stroke.width,
                    cap = StrokeCap.Round,
                )
            }

            OverviewGlyph.RECEIVE -> {
                drawLine(tint, Offset(size.width / 2f, size.height * 0.18f), Offset(size.width / 2f, size.height * 0.74f), stroke.width, cap = StrokeCap.Round)
                drawLine(tint, Offset(size.width * 0.32f, size.height * 0.52f), Offset(size.width / 2f, size.height * 0.74f), stroke.width, cap = StrokeCap.Round)
                drawLine(tint, Offset(size.width * 0.68f, size.height * 0.52f), Offset(size.width / 2f, size.height * 0.74f), stroke.width, cap = StrokeCap.Round)
                drawRoundRect(
                    color = tint,
                    topLeft = Offset(size.width * 0.22f, size.height * 0.78f),
                    size = Size(size.width * 0.56f, size.height * 0.1f),
                    cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx()),
                    style = stroke,
                )
            }

            OverviewGlyph.SEND -> {
                drawLine(tint, Offset(size.width * 0.16f, size.height * 0.5f), Offset(size.width * 0.82f, size.height * 0.2f), stroke.width, cap = StrokeCap.Round)
                drawLine(tint, Offset(size.width * 0.16f, size.height * 0.5f), Offset(size.width * 0.82f, size.height * 0.78f), stroke.width, cap = StrokeCap.Round)
                drawLine(tint, Offset(size.width * 0.16f, size.height * 0.5f), Offset(size.width * 0.52f, size.height * 0.52f), stroke.width, cap = StrokeCap.Round)
            }

            OverviewGlyph.REGION -> {
                drawRoundRect(
                    color = tint,
                    topLeft = Offset(size.width * 0.18f, size.height * 0.2f),
                    size = Size(size.width * 0.64f, size.height * 0.6f),
                    cornerRadius = CornerRadius(5.dp.toPx(), 5.dp.toPx()),
                    style = stroke,
                )
                drawLine(tint, Offset(size.width * 0.5f, size.height * 0.2f), Offset(size.width * 0.5f, size.height * 0.8f), stroke.width, cap = StrokeCap.Round)
                drawLine(tint, Offset(size.width * 0.18f, size.height * 0.5f), Offset(size.width * 0.82f, size.height * 0.5f), stroke.width, cap = StrokeCap.Round)
            }

            OverviewGlyph.PLAN -> {
                drawRoundRect(
                    color = tint,
                    topLeft = Offset(size.width * 0.16f, size.height * 0.24f),
                    size = Size(size.width * 0.68f, size.height * 0.52f),
                    cornerRadius = CornerRadius(5.dp.toPx(), 5.dp.toPx()),
                    style = stroke,
                )
                drawCircle(
                    color = tint,
                    radius = 1.7.dp.toPx(),
                    center = Offset(size.width * 0.68f, size.height * 0.5f),
                    style = Fill,
                )
                drawLine(tint, Offset(size.width * 0.24f, size.height * 0.36f), Offset(size.width * 0.56f, size.height * 0.36f), stroke.width, cap = StrokeCap.Round)
            }

            OverviewGlyph.STATUS -> {
                drawCircle(color = tint, radius = size.minDimension * 0.16f, center = Offset(size.width * 0.3f, size.height * 0.3f), style = Fill)
                drawLine(tint, Offset(size.width * 0.22f, size.height * 0.74f), Offset(size.width * 0.44f, size.height * 0.54f), stroke.width, cap = StrokeCap.Round)
                drawLine(tint, Offset(size.width * 0.44f, size.height * 0.54f), Offset(size.width * 0.78f, size.height * 0.22f), stroke.width, cap = StrokeCap.Round)
            }

            OverviewGlyph.ORDER -> {
                drawRoundRect(
                    color = tint,
                    topLeft = Offset(size.width * 0.22f, size.height * 0.2f),
                    size = Size(size.width * 0.56f, size.height * 0.6f),
                    cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx()),
                    style = stroke,
                )
                drawLine(tint, Offset(size.width * 0.32f, size.height * 0.4f), Offset(size.width * 0.68f, size.height * 0.4f), stroke.width, cap = StrokeCap.Round)
                drawLine(tint, Offset(size.width * 0.32f, size.height * 0.58f), Offset(size.width * 0.56f, size.height * 0.58f), stroke.width, cap = StrokeCap.Round)
            }

            OverviewGlyph.VLESS -> {
                drawLine(tint, Offset(size.width * 0.18f, size.height * 0.34f), Offset(size.width * 0.38f, size.height * 0.78f), stroke.width, cap = StrokeCap.Round)
                drawLine(tint, Offset(size.width * 0.38f, size.height * 0.78f), Offset(size.width * 0.82f, size.height * 0.18f), stroke.width, cap = StrokeCap.Round)
                drawLine(tint, Offset(size.width * 0.58f, size.height * 0.18f), Offset(size.width * 0.82f, size.height * 0.18f), stroke.width, cap = StrokeCap.Round)
            }
        }
    }
}

private fun buildOverviewAlerts(
    uiState: VpnHomeUiState,
    onBottomNav: (String) -> Unit,
    onWalletHome: () -> Unit,
): List<OverviewAlert> {
    val mappedSignals = uiState.watchSignals.take(3).map { signal ->
        val glyph = when (signal.symbol) {
            "NODE", "VPN" -> OverviewGlyph.STATUS
            "ORDER", "SOL", "USDT" -> OverviewGlyph.ORDER
            else -> OverviewGlyph.VLESS
        }
        OverviewAlert(
            title = when (signal.symbol) {
                "NODE" -> "节点状态已同步"
                "VPN" -> "VPN 状态已确认"
                "SUB" -> "订阅有效期已同步"
                "VLESS" -> "VLESS 配置已同步"
                else -> "${signal.symbol} 数据更新"
            },
            subtitle = listOf(signal.reason, signal.volumeText.takeIf { it.isNotBlank() }).filterNotNull().joinToString(" · ").ifBlank {
                uiState.latestOrderLabel
            },
            badge = signal.changeText.ifBlank { if (signal.isPositive) "已就绪" else "待处理" },
            glyph = glyph,
            positive = signal.isPositive,
            onClick = {
                when (signal.symbol) {
                    "NODE", "VPN" -> onBottomNav(CryptoVpnRouteSpec.regionSelection.pattern)
                    "ORDER", "SOL", "USDT", "SUB" -> onBottomNav(CryptoVpnRouteSpec.orderList.pattern)
                    else -> onWalletHome()
                }
            },
        )
    }

    if (mappedSignals.size >= 3) {
        return mappedSignals
    }

    val fallbacks = listOf(
        OverviewAlert(
            title = "节点配置状态",
            subtitle = uiState.configStatusLabel,
            badge = if (uiState.canConnect) "已就绪" else "待导入",
            glyph = OverviewGlyph.STATUS,
            positive = uiState.canConnect,
            onClick = { onBottomNav(CryptoVpnRouteSpec.regionSelection.pattern) },
        ),
        OverviewAlert(
            title = "订单同步状态",
            subtitle = uiState.latestOrderLabel,
            badge = when {
                uiState.latestOrderLabel.contains("待同步") -> "待同步"
                uiState.latestOrderLabel.contains("暂无") -> "无记录"
                else -> "已同步"
            },
            glyph = OverviewGlyph.ORDER,
            positive = isOrdersResolved(uiState),
            onClick = { onBottomNav(CryptoVpnRouteSpec.orderList.pattern) },
        ),
        OverviewAlert(
            title = "VLESS 有效期",
            subtitle = "当前到期时间 ${uiState.vlessExpiryLabel}",
            badge = uiState.vlessRegionLabel,
            glyph = OverviewGlyph.VLESS,
            positive = uiState.vlessExpiryLabel != "待签发" && uiState.vlessExpiryLabel != "待同步",
            onClick = onWalletHome,
        ),
    )

    return (mappedSignals + fallbacks).take(3)
}

private fun resolveBottomRoute(currentRoute: String): String = when {
    currentRoute == CryptoVpnRouteSpec.vpnHome.name || currentRoute == CryptoVpnRouteSpec.vpnHome.pattern -> "vpn_home"
    currentRoute == CryptoVpnRouteSpec.plans.name || currentRoute == CryptoVpnRouteSpec.plans.pattern || currentRoute.startsWith("region_selection") -> "plans"
    currentRoute == CryptoVpnRouteSpec.walletHome.name || currentRoute == CryptoVpnRouteSpec.walletHome.pattern || currentRoute.startsWith("receive") || currentRoute.startsWith("send") || currentRoute.startsWith("asset_detail") -> "wallet_home"
    currentRoute.startsWith("invite") || currentRoute.startsWith("commission") || currentRoute.startsWith("withdraw") -> "invite_center"
    else -> "profile"
}

private fun connectionChipLabel(uiState: VpnHomeUiState): String = when {
    uiState.connectionStatus == VpnConnectionStatus.CONNECTED -> "已连接"
    uiState.connectionStatus == VpnConnectionStatus.CONNECTING -> "连接中"
    uiState.canConnect -> "待连接"
    else -> "待同步"
}

private fun orbPrimaryText(uiState: VpnHomeUiState): String = when {
    uiState.connectionStatus == VpnConnectionStatus.CONNECTED -> "已连接"
    uiState.connectionStatus == VpnConnectionStatus.CONNECTING -> "连接中"
    uiState.canConnect -> "待连接"
    else -> "待同步"
}

private fun orbSecondaryText(uiState: VpnHomeUiState): String = when {
    uiState.connectionStatus == VpnConnectionStatus.CONNECTED ->
        uiState.vlessRegionLabel.takeIf { it.isNotBlank() } ?: "当前线路"
    uiState.connectionStatus == VpnConnectionStatus.CONNECTING -> "正在建立安全隧道"
    uiState.canConnect ->
        "${uiState.vlessRegionLabel.takeIf { it.isNotBlank() } ?: "当前线路"} 已就绪"
    else -> "节点下发中"
}

private fun primaryTunnelButtonLabel(uiState: VpnHomeUiState): String = when {
    uiState.connectionStatus == VpnConnectionStatus.CONNECTED -> "断开连接"
    uiState.connectionStatus == VpnConnectionStatus.CONNECTING -> "连接中"
    uiState.canConnect -> "连接"
    else -> "待同步"
}

private fun overviewCaption(uiState: VpnHomeUiState): String {
    return if (uiState.latestOrderLabel.contains("待同步")) {
        "订单待同步"
    } else if (uiState.latestOrderLabel.contains("暂无")) {
        "暂无订单"
    } else {
        uiState.latestOrderLabel
    }
}

private fun isOrdersResolved(uiState: VpnHomeUiState): Boolean {
    return !uiState.latestOrderLabel.contains("暂无") && !uiState.latestOrderLabel.contains("待同步")
}

@Preview(showBackground = true, widthDp = 393, heightDp = 852)
@Composable
private fun VpnHomePreview() {
    CryptoVpnTheme {
        VpnHomeScreen(
            currentRoute = CryptoVpnRouteSpec.vpnHome.name,
            uiState = VpnHomeViewModel(MockP0Repository()).uiState.value,
            onToggleConnection = {},
            onSelectRegion = {},
            onBottomNav = {},
            onWalletHome = {},
            onPlans = {},
        )
    }
}
