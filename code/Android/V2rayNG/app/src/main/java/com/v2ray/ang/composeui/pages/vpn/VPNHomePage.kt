package com.v2ray.ang.composeui.pages.vpn

import android.app.Application
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.VpnLock
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.v2ray.ang.composeui.theme.Error as AppError
import com.v2ray.ang.composeui.theme.GlowBlue
import com.v2ray.ang.composeui.theme.Info
import com.v2ray.ang.composeui.theme.Primary
import com.v2ray.ang.composeui.theme.Success
import com.v2ray.ang.composeui.theme.TextPrimary
import com.v2ray.ang.composeui.theme.TextSecondary
import com.v2ray.ang.composeui.theme.Warning
import com.v2ray.ang.payment.data.repository.PaymentRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

enum class VPNConnectionStatus {
    DISCONNECTED,
    CONNECTING,
    CONNECTED,
    DISCONNECTING,
}

sealed class VPNHomeState {
    object Idle : VPNHomeState()
    object Loading : VPNHomeState()
    data class Loaded(
        val status: VPNConnectionStatus,
        val selectedRegion: RegionInfo,
        val connectionDuration: String,
        val uploadSpeed: String,
        val downloadSpeed: String,
    ) : VPNHomeState()
    data class Error(val message: String) : VPNHomeState()
}

data class RegionInfo(
    val id: String,
    val name: String,
    val countryCode: String,
    val latency: Int,
)

class VPNHomeViewModel(application: Application) : AndroidViewModel(application) {
    private val _state = MutableStateFlow<VPNHomeState>(VPNHomeState.Idle)
    val state: StateFlow<VPNHomeState> = _state
    private val repository = PaymentRepository(application)

    private val _selectedRegion = MutableStateFlow(
        RegionInfo("us", "美国", "US", 45),
    )
    val selectedRegion: StateFlow<RegionInfo> = _selectedRegion

    private val _connectionStatus = MutableStateFlow(VPNConnectionStatus.DISCONNECTED)
    val connectionStatus: StateFlow<VPNConnectionStatus> = _connectionStatus

    private val _connectionDuration = MutableStateFlow("00:00:00")
    val connectionDuration: StateFlow<String> = _connectionDuration

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        _state.value = VPNHomeState.Loading
        viewModelScope.launch {
            val subscription = repository.getSubscription()
            if (subscription.isSuccess) {
                val data = subscription.getOrNull()
                val active = data?.status.equals("ACTIVE", ignoreCase = true)
                _connectionStatus.value = if (active) {
                    VPNConnectionStatus.CONNECTED
                } else {
                    VPNConnectionStatus.DISCONNECTED
                }
                _connectionDuration.value = if (active) {
                    "剩余${data?.daysRemaining ?: 0}天"
                } else {
                    "00:00:00"
                }
                updateState()
            } else {
                _connectionStatus.value = VPNConnectionStatus.DISCONNECTED
                updateState()
            }
        }
    }

    fun toggleConnection() {
        viewModelScope.launch {
            when (_connectionStatus.value) {
                VPNConnectionStatus.DISCONNECTED -> {
                    _connectionStatus.value = VPNConnectionStatus.CONNECTING
                    updateState()
                    delay(2000)
                    _connectionStatus.value = VPNConnectionStatus.CONNECTED
                    startDurationTimer()
                }

                VPNConnectionStatus.CONNECTED -> {
                    _connectionStatus.value = VPNConnectionStatus.DISCONNECTING
                    updateState()
                    delay(1000)
                    _connectionStatus.value = VPNConnectionStatus.DISCONNECTED
                    _connectionDuration.value = "00:00:00"
                }

                else -> Unit
            }
            updateState()
        }
    }

    private fun startDurationTimer() {
        viewModelScope.launch {
            var seconds = 0
            while (_connectionStatus.value == VPNConnectionStatus.CONNECTED) {
                delay(1000)
                if (_connectionStatus.value == VPNConnectionStatus.CONNECTED) {
                    seconds++
                    val hours = seconds / 3600
                    val minutes = (seconds % 3600) / 60
                    val secs = seconds % 60
                    _connectionDuration.value = String.format("%02d:%02d:%02d", hours, minutes, secs)
                    updateState()
                }
            }
        }
    }

    fun onRegionSelected(region: RegionInfo) {
        _selectedRegion.value = region
        if (_connectionStatus.value == VPNConnectionStatus.CONNECTED) {
            viewModelScope.launch {
                _connectionStatus.value = VPNConnectionStatus.DISCONNECTING
                delay(500)
                _connectionStatus.value = VPNConnectionStatus.DISCONNECTED
                delay(300)
                toggleConnection()
            }
        }
        updateState()
    }

    private fun updateState() {
        _state.value = VPNHomeState.Loaded(
            status = _connectionStatus.value,
            selectedRegion = _selectedRegion.value,
            connectionDuration = _connectionDuration.value,
            uploadSpeed = if (_connectionStatus.value == VPNConnectionStatus.CONNECTED) "1.2 MB/s" else "0 KB/s",
            downloadSpeed = if (_connectionStatus.value == VPNConnectionStatus.CONNECTED) "5.8 MB/s" else "0 KB/s",
        )
    }
}

@Composable
fun VPNHomePage(
    viewModel: VPNHomeViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    onNavigateToRegions: () -> Unit = {},
    onNavigateToPlans: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {},
    onNavigateToOrders: () -> Unit = {},
) {
    val state by viewModel.state.collectAsState()
    val connectionStatus by viewModel.connectionStatus.collectAsState()
    val selectedRegion by viewModel.selectedRegion.collectAsState()
    val connectionDuration by viewModel.connectionDuration.collectAsState()

    val accent = connectionStatus.accentColor()
    val heroTitle = when (connectionStatus) {
        VPNConnectionStatus.CONNECTED -> "Private route is live"
        VPNConnectionStatus.CONNECTING -> "Bootstrapping the secure tunnel"
        VPNConnectionStatus.DISCONNECTING -> "Closing the current route"
        VPNConnectionStatus.DISCONNECTED -> "Ready to launch your next private route"
    }
    val heroSubtitle = when (connectionStatus) {
        VPNConnectionStatus.CONNECTED -> "已锁定 ${selectedRegion.name} 节点，保持一级 VPN tab 内的强 CTA 和清晰订单层级。"
        VPNConnectionStatus.CONNECTING -> "正在与 ${selectedRegion.name} 节点握手，交易台式卡片会在建立后刷新实时速度。"
        VPNConnectionStatus.DISCONNECTING -> "正在安全断开当前节点，稍后可以切换新区域或直接补购套餐。"
        VPNConnectionStatus.DISCONNECTED -> "把 VPN 作为底部一级入口后，连接、套餐、区域、订单都集中在这里完成。"
    }

    VpnBitgetBackground {
        Scaffold(
            containerColor = Color.Transparent,
            contentColor = TextPrimary,
            contentWindowInsets = WindowInsets.safeDrawing,
        ) { innerPadding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(
                    start = VpnPageHorizontalPadding,
                    end = VpnPageHorizontalPadding,
                    top = VpnPageTopPadding,
                    bottom = 36.dp,
                ),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                item {
                    VpnTopChrome(
                        title = "VPN",
                        subtitle = "Primary tab for connection, package selection, regions, and orders.",
                        actionIcon = Icons.Default.Person,
                        onActionClick = onNavigateToProfile,
                    )
                }
                item {
                    VpnSearchStrip(
                        placeholder = "Search regions, packages, or recent orders",
                        trailingIcon = Icons.Default.Tune,
                        onClick = onNavigateToRegions,
                    )
                }
                item {
                    VpnHeroCard(
                        eyebrow = connectionStatus.heroLabel(),
                        title = heroTitle,
                        subtitle = heroSubtitle,
                        accent = accent,
                        metrics = listOf(
                            VpnHeroMetric("State", connectionStatus.metricLabel()),
                            VpnHeroMetric("Region", selectedRegion.name),
                            VpnHeroMetric("Timer", connectionDuration),
                        ),
                    )
                }

                when (val current = state) {
                    is VPNHomeState.Loading,
                    VPNHomeState.Idle,
                    -> {
                        item {
                            VpnLoadingPanel(
                                title = "Syncing subscription desk",
                                subtitle = "正在读取当前订阅与连接桥接状态。",
                            )
                        }
                    }

                    is VPNHomeState.Error -> {
                        item {
                            VpnEmptyPanel(
                                title = "VPN desk is temporarily unavailable",
                                subtitle = current.message,
                                actionText = "Review packages",
                                onAction = onNavigateToPlans,
                            )
                        }
                    }

                    is VPNHomeState.Loaded -> {
                        item {
                            VpnSectionHeading(
                                title = "Connection Desk",
                                subtitle = "Bitget-like hero + deep action cards for the VPN business core.",
                            )
                        }
                        item {
                            ConnectionDeskCard(
                                status = current.status,
                                region = current.selectedRegion,
                                duration = current.connectionDuration,
                                uploadSpeed = current.uploadSpeed,
                                downloadSpeed = current.downloadSpeed,
                                onToggle = viewModel::toggleConnection,
                            )
                        }
                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                            ) {
                                HomeActionCard(
                                    modifier = Modifier.weight(1f),
                                    icon = Icons.Default.Public,
                                    title = "Regions",
                                    subtitle = "Choose the fastest route",
                                    accent = Info,
                                    onClick = onNavigateToRegions,
                                )
                                HomeActionCard(
                                    modifier = Modifier.weight(1f),
                                    icon = Icons.Default.CreditCard,
                                    title = "Packages",
                                    subtitle = "Compare plans and activate",
                                    accent = Warning,
                                    onClick = onNavigateToPlans,
                                )
                            }
                        }
                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                            ) {
                                HomeActionCard(
                                    modifier = Modifier.weight(1f),
                                    icon = Icons.Default.ReceiptLong,
                                    title = "Orders",
                                    subtitle = "Pending payment and history",
                                    accent = GlowBlue,
                                    onClick = onNavigateToOrders,
                                )
                                HomeActionCard(
                                    modifier = Modifier.weight(1f),
                                    icon = Icons.Default.Shield,
                                    title = "Profile",
                                    subtitle = "Session, support, and settings",
                                    accent = Primary,
                                    onClick = onNavigateToProfile,
                                )
                            }
                        }
                        item {
                            SubscriptionBanner(
                                status = current.status,
                                regionName = current.selectedRegion.name,
                                onOpenPlans = onNavigateToPlans,
                                onOpenOrders = onNavigateToOrders,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ConnectionDeskCard(
    status: VPNConnectionStatus,
    region: RegionInfo,
    duration: String,
    uploadSpeed: String,
    downloadSpeed: String,
    onToggle: () -> Unit,
) {
    VpnGlassCard(accent = status.accentColor()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "Connection Console",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary,
                )
                Text(
                    text = "节点 ${region.name} · ${region.latency}ms",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary,
                )
            }
            VpnStatusChip(
                text = status.metricLabel(),
                containerColor = status.accentColor().copy(alpha = 0.16f),
                contentColor = status.accentColor(),
            )
        }

        Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.18f))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            VpnMetricPill(
                modifier = Modifier.weight(1f),
                label = "Upload",
                value = uploadSpeed,
            )
            VpnMetricPill(
                modifier = Modifier.weight(1f),
                label = "Download",
                value = downloadSpeed,
            )
        }

        VpnLabelValueRow(label = "Route timer", value = duration)
        VpnLabelValueRow(label = "Protection layer", value = "Always-on encrypted tunnel")

        VpnPrimaryButton(
            text = status.actionLabel(),
            onClick = onToggle,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun HomeActionCard(
    icon: ImageVector,
    title: String,
    subtitle: String,
    accent: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.62f),
        border = BorderStroke(1.dp, accent.copy(alpha = 0.32f)),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Surface(
                modifier = Modifier.size(40.dp),
                shape = RoundedCornerShape(16.dp),
                color = accent.copy(alpha = 0.16f),
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = accent,
                    )
                }
            }
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary,
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
            )
        }
    }
}

@Composable
private fun SubscriptionBanner(
    status: VPNConnectionStatus,
    regionName: String,
    onOpenPlans: () -> Unit,
    onOpenOrders: () -> Unit,
) {
    VpnGlassCard(accent = Warning) {
        Text(
            text = "Subscription Desk",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
            color = TextPrimary,
        )
        Text(
            text = if (status == VPNConnectionStatus.CONNECTED) {
                "当前已接入 $regionName，仍可在套餐页补购更长周期，订单状态会继续通过现有桥接同步。"
            } else {
                "未连接时优先引导用户到套餐和订单页，符合 Bitget 风格强 CTA 与清晰二级详情层级。"
            },
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary,
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            VpnSecondaryButton(
                text = "Open Orders",
                onClick = onOpenOrders,
                modifier = Modifier.weight(1f),
            )
            VpnPrimaryButton(
                text = "Review Packages",
                onClick = onOpenPlans,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

private fun VPNConnectionStatus.accentColor(): Color {
    return when (this) {
        VPNConnectionStatus.CONNECTED -> Success
        VPNConnectionStatus.CONNECTING -> Info
        VPNConnectionStatus.DISCONNECTING -> Warning
        VPNConnectionStatus.DISCONNECTED -> AppError
    }
}

private fun VPNConnectionStatus.heroLabel(): String {
    return when (this) {
        VPNConnectionStatus.CONNECTED -> "VPN LIVE"
        VPNConnectionStatus.CONNECTING -> "VPN PENDING"
        VPNConnectionStatus.DISCONNECTING -> "VPN CLOSING"
        VPNConnectionStatus.DISCONNECTED -> "VPN READY"
    }
}

private fun VPNConnectionStatus.metricLabel(): String {
    return when (this) {
        VPNConnectionStatus.CONNECTED -> "Connected"
        VPNConnectionStatus.CONNECTING -> "Connecting"
        VPNConnectionStatus.DISCONNECTING -> "Closing"
        VPNConnectionStatus.DISCONNECTED -> "Idle"
    }
}

private fun VPNConnectionStatus.actionLabel(): String {
    return when (this) {
        VPNConnectionStatus.CONNECTED -> "Disconnect Route"
        VPNConnectionStatus.CONNECTING -> "Connection in Progress"
        VPNConnectionStatus.DISCONNECTING -> "Disconnecting"
        VPNConnectionStatus.DISCONNECTED -> "Launch Secure Route"
    }
}

@Preview
@Composable
private fun VPNHomePagePreview() {
    MaterialTheme {
        VPNHomePage()
    }
}
