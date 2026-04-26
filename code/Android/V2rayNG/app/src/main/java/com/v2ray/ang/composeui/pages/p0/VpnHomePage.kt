package com.v2ray.ang.composeui.pages.p0

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.VpnService
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.v2ray.ang.AppConfig
import com.v2ray.ang.R
import com.v2ray.ang.composeui.navigation.CryptoVpnRouteSpec
import com.v2ray.ang.composeui.p0.model.RegionSpeed
import com.v2ray.ang.composeui.p0.model.VpnConnectionStatus
import com.v2ray.ang.composeui.p0.model.VpnHomeEvent
import com.v2ray.ang.composeui.p0.model.VpnHomeUiState
import com.v2ray.ang.composeui.p0.model.vpnHomePreviewState
import com.v2ray.ang.composeui.p0.viewmodel.VpnHomeViewModel
import com.v2ray.ang.composeui.theme.CryptoVpnTheme
import com.v2ray.ang.handler.MmkvManager
import com.v2ray.ang.handler.SettingsManager
import com.v2ray.ang.handler.V2RayServiceManager
import com.v2ray.ang.payment.data.repository.PaymentRepository
import com.v2ray.ang.util.MessageUtil
import com.v2ray.ang.util.Utils
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

private const val OverviewFrameWidth = 390f
private const val OverviewFrameHeight = 844f

private data class OverviewHotspot(
    val x: Float,
    val y: Float,
    val width: Float,
    val height: Float,
    val onClick: () -> Unit,
)

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
    val appContext = context.applicationContext
    val scope = rememberCoroutineScope()
    val paymentRepository = remember { PaymentRepository(appContext) }
    var runtimeConnectionStatus by remember { mutableStateOf<VpnConnectionStatus?>(null) }
    val requestVpnPermission = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
    ) { result ->
        if (result.resultCode == android.app.Activity.RESULT_OK) {
            runtimeConnectionStatus = VpnConnectionStatus.CONNECTING
            V2RayServiceManager.startVService(context)
        }
        viewModel.onEvent(VpnHomeEvent.Refresh)
    }

    DisposableEffect(appContext) {
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                when (intent?.getIntExtra("key", 0)) {
                    AppConfig.MSG_STATE_RUNNING,
                    AppConfig.MSG_STATE_START_SUCCESS -> {
                        runtimeConnectionStatus = VpnConnectionStatus.CONNECTED
                        viewModel.onEvent(VpnHomeEvent.Refresh)
                    }

                    AppConfig.MSG_STATE_NOT_RUNNING,
                    AppConfig.MSG_STATE_START_FAILURE,
                    AppConfig.MSG_STATE_STOP_SUCCESS -> {
                        runtimeConnectionStatus = VpnConnectionStatus.DISCONNECTED
                        viewModel.onEvent(VpnHomeEvent.Refresh)
                    }
                }
            }
        }

        val filter = IntentFilter(AppConfig.BROADCAST_ACTION_ACTIVITY)
        ContextCompat.registerReceiver(appContext, receiver, filter, Utils.receiverFlags())
        MessageUtil.sendMsg2Service(appContext, AppConfig.MSG_REGISTER_CLIENT, "")

        onDispose {
            runCatching { appContext.unregisterReceiver(receiver) }
            MessageUtil.sendMsg2Service(appContext, AppConfig.MSG_UNREGISTER_CLIENT, "")
        }
    }

    val effectiveUiState = runtimeConnectionStatus?.let { status ->
        if (uiState.connectionStatus == status) {
            uiState
        } else {
            uiState.copy(connectionStatus = status)
        }
    } ?: uiState

    fun startVpnAfterConfigReady() {
        if (SettingsManager.isVpnMode()) {
            val prepareIntent = VpnService.prepare(context)
            if (prepareIntent == null) {
                runtimeConnectionStatus = VpnConnectionStatus.CONNECTING
                V2RayServiceManager.startVService(context)
                viewModel.onEvent(VpnHomeEvent.Refresh)
            } else {
                requestVpnPermission.launch(prepareIntent)
            }
        } else {
            runtimeConnectionStatus = VpnConnectionStatus.CONNECTING
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
                Toast.makeText(context, "请重新登录或稍后重试。", Toast.LENGTH_SHORT).show()
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
        uiState = effectiveUiState,
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
    val openRegionSelection = {
        onSelectRegion(uiState.selectedRegion)
        onBottomNav(CryptoVpnRouteSpec.regionSelectionRoute())
    }

    OverviewReferenceScreen(
        currentRoute = currentRoute,
        onToggleConnection = onToggleConnection,
        onOpenWallet = onWalletHome,
        onOpenPlans = onPlans,
        onOpenRegionSelection = openRegionSelection,
        onBottomNav = onBottomNav,
    )
}

@Composable
private fun OverviewReferenceScreen(
    currentRoute: String,
    onToggleConnection: () -> Unit,
    onOpenWallet: () -> Unit,
    onOpenPlans: () -> Unit,
    onOpenRegionSelection: () -> Unit,
    onBottomNav: (String) -> Unit,
) {
    val inviteRoute = CryptoVpnRouteSpec.inviteCenter.pattern
    val inviteShareRoute = CryptoVpnRouteSpec.inviteShare.pattern
    val commissionRoute = CryptoVpnRouteSpec.commissionLedger.pattern
    val securityRoute = CryptoVpnRouteSpec.securityCenter.pattern
    val profileRoute = CryptoVpnRouteSpec.profile.pattern
    val homeRoute = if (currentRoute == CryptoVpnRouteSpec.vpnHome.pattern) {
        currentRoute
    } else {
        CryptoVpnRouteSpec.vpnHome.pattern
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState()),
    ) {
        BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
            val scale = maxWidth / OverviewFrameWidth.dp
            val imageHeight = OverviewFrameHeight.dp * scale
            val interactionSource = remember { MutableInteractionSource() }
            val hotspots = listOf(
                OverviewHotspot(36f, 227f, 96f, 34f, onOpenWallet),
                OverviewHotspot(146f, 227f, 96f, 34f, onOpenWallet),
                OverviewHotspot(256f, 227f, 96f, 34f, onOpenWallet),
                OverviewHotspot(133f, 308f, 124f, 124f, onToggleConnection),
                OverviewHotspot(36f, 421f, 43f, 24f, onOpenRegionSelection),
                OverviewHotspot(329f, 291f, 30f, 30f, onOpenRegionSelection),
                OverviewHotspot(24f, 634f, 62f, 58f, onOpenPlans),
                OverviewHotspot(95f, 634f, 60f, 58f) { onBottomNav(inviteShareRoute) },
                OverviewHotspot(164f, 634f, 62f, 58f, onOpenPlans),
                OverviewHotspot(234f, 634f, 62f, 58f) { onBottomNav(commissionRoute) },
                OverviewHotspot(304f, 634f, 62f, 58f, onOpenPlans),
                OverviewHotspot(24f, 684f, 62f, 58f) { onBottomNav(inviteRoute) },
                OverviewHotspot(95f, 684f, 60f, 58f) { onBottomNav(profileRoute) },
                OverviewHotspot(164f, 684f, 62f, 58f) { onBottomNav(inviteRoute) },
                OverviewHotspot(234f, 684f, 62f, 58f) { onBottomNav(securityRoute) },
                OverviewHotspot(304f, 684f, 62f, 58f) { onBottomNav(profileRoute) },
                OverviewHotspot(45f, 779f, 72f, 22f, onOpenPlans),
                OverviewHotspot(0f, 801f, 78f, 43f) { onBottomNav(homeRoute) },
                OverviewHotspot(78f, 801f, 78f, 43f, onOpenPlans),
                OverviewHotspot(156f, 801f, 78f, 43f, onOpenWallet),
                OverviewHotspot(234f, 801f, 78f, 43f) { onBottomNav(inviteRoute) },
                OverviewHotspot(312f, 801f, 78f, 43f) { onBottomNav(profileRoute) },
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(imageHeight),
            ) {
                // The locked preview is the single source of truth for pixel-perfect 1:1 home recovery.
                Image(
                    painter = painterResource(id = R.drawable.overview_super_home_reference),
                    contentDescription = "CryptoVPN 总览页",
                    contentScale = ContentScale.FillBounds,
                    modifier = Modifier.fillMaxSize(),
                )
                hotspots.forEach { hotspot ->
                    OverviewHotspotButton(
                        hotspot = hotspot,
                        scale = scale,
                        interactionSource = interactionSource,
                    )
                }
            }
        }
    }
}

@Composable
private fun OverviewHotspotButton(
    hotspot: OverviewHotspot,
    scale: Float,
    interactionSource: MutableInteractionSource,
) {
    Box(
        modifier = Modifier
            .offset(
                x = hotspot.x.scaled(scale),
                y = hotspot.y.scaled(scale),
            )
            .size(
                width = hotspot.width.scaled(scale),
                height = hotspot.height.scaled(scale),
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = hotspot.onClick,
            ),
    )
}

private fun Float.scaled(scale: Float): Dp = (this * scale).dp

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun VpnHomePreview() {
    CryptoVpnTheme {
        VpnHomeScreen(
            currentRoute = CryptoVpnRouteSpec.vpnHome.name,
            uiState = vpnHomePreviewState(),
            onToggleConnection = {},
            onSelectRegion = {},
            onBottomNav = {},
            onWalletHome = {},
            onPlans = {},
        )
    }
}
