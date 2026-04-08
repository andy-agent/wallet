package com.v2ray.ang.composeui.pages.vpn

import android.app.Application
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.v2ray.ang.composeui.theme.Error as AppError
import com.v2ray.ang.composeui.theme.TextPrimary
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
    data object Idle : VPNHomeState()
    data object Loading : VPNHomeState()
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
        RegionInfo("us", "美国 - 洛杉矶", "US", 45),
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
                    delay(1800)
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
    val selectedTab = remember { mutableIntStateOf(0) }
    val selectedRange = remember { mutableIntStateOf(3) }

    VpnBitgetBackground {
        Scaffold(
            containerColor = Color.Transparent,
            contentColor = TextPrimary,
            contentWindowInsets = WindowInsets.safeDrawing,
            bottomBar = {
                VpnPrimaryButton(
                    text = connectionStatus.actionLabel(),
                    onClick = viewModel::toggleConnection,
                    enabled = connectionStatus == VPNConnectionStatus.CONNECTED || connectionStatus == VPNConnectionStatus.DISCONNECTED,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = VpnPageHorizontalPadding, vertical = 16.dp),
                )
            },
        ) { innerPadding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(
                    start = VpnPageHorizontalPadding,
                    end = VpnPageHorizontalPadding,
                    top = VpnPageTopPadding,
                    bottom = VpnPageBottomPadding,
                ),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                item {
                    VpnTopChrome(
                        title = "LJ Route",
                        subtitle = selectedRegion.name,
                        actionIcon = Icons.Default.Person,
                        onActionClick = onNavigateToProfile,
                    )
                }
                item {
                    VpnTabStrip(
                        tabs = listOf("概览", "详情"),
                        selectedIndex = selectedTab.intValue,
                        onSelect = { selectedTab.intValue = it },
                    )
                }
                when (val current = state) {
                    is VPNHomeState.Loading,
                    VPNHomeState.Idle,
                    -> {
                        item {
                            VpnLoadingPanel(
                                title = "正在同步 VPN 详情",
                                subtitle = "读取当前订阅、节点和连接状态。",
                            )
                        }
                    }

                    is VPNHomeState.Error -> {
                        item {
                            VpnEmptyPanel(
                                title = "VPN 页面暂不可用",
                                subtitle = current.message,
                                actionText = "查看套餐",
                                onAction = onNavigateToPlans,
                            )
                        }
                    }

                    is VPNHomeState.Loaded -> {
                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(16.dp),
                            ) {
                                VpnValueBlock(
                                    value = connectionStatus.headlineValue(connectionDuration),
                                    change = connectionStatus.headlineChange(selectedRegion.latency),
                                    helper = connectionStatus.helperLabel(),
                                    changeColor = connectionStatus.accentColor(),
                                    modifier = Modifier.weight(1.1f),
                                )
                                VpnMetricColumn(
                                    metrics = listOf(
                                        VpnHeroMetric("当前地区", selectedRegion.countryCode),
                                        VpnHeroMetric("节点延迟", "${selectedRegion.latency}ms"),
                                        VpnHeroMetric("上行速率", current.uploadSpeed),
                                        VpnHeroMetric("下行速率", current.downloadSpeed),
                                    ),
                                    modifier = Modifier.weight(1f),
                                )
                            }
                        }
                        item {
                            VpnRangeSelector(
                                labels = listOf("1小时", "4小时", "12小时", "1天", "更多"),
                                selectedIndex = selectedRange.intValue,
                                trailingIcon = Icons.Default.Tune,
                                onSelect = { selectedRange.intValue = it },
                                onTrailingClick = onNavigateToRegions,
                            )
                        }
                        item {
                            VpnCandleChart(
                                entries = vpnDemoCandles(selectedRegion.latency.toFloat()),
                                calloutLines = listOf(
                                    "节点" to selectedRegion.name,
                                    "状态" to connectionStatus.metricLabel(),
                                    "下行" to current.downloadSpeed,
                                    "时长" to current.connectionDuration,
                                ),
                                rightLabels = listOf("101.9", "90.5", "78.5", "67.1"),
                                bottomLabels = listOf("02/24", "03/06", "03/23", "04/08"),
                            )
                        }
                        item {
                            VpnGlassCard {
                                Text(
                                    text = "线路信息",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = TextPrimary,
                                )
                                VpnLabelValueRow(label = "区域", value = current.selectedRegion.name)
                                VpnLabelValueRow(label = "安全层", value = "Always-on encrypted tunnel")
                                VpnLabelValueRow(label = "连接时长", value = current.connectionDuration)
                                VpnLabelValueRow(label = "状态", value = connectionStatus.metricLabel(), valueColor = connectionStatus.accentColor())
                            }
                        }
                        item {
                            VpnGlassCard(accent = VpnOutline) {
                                Text(
                                    text = "业务入口",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = TextPrimary,
                                )
                                VpnGroupRow(
                                    title = "节点区域",
                                    subtitle = "切换地区并重新建立路线",
                                    onClick = onNavigateToRegions,
                                    leading = {
                                        VpnCodeBadge(text = selectedRegion.countryCode)
                                    },
                                )
                                VpnListDivider()
                                VpnGroupRow(
                                    title = "套餐中心",
                                    subtitle = "查看可购买的 VPN 套餐",
                                    onClick = onNavigateToPlans,
                                    trailing = {
                                        Text(
                                            text = "查看",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = VpnAccent,
                                        )
                                    },
                                )
                                VpnListDivider()
                                VpnGroupRow(
                                    title = "订单记录",
                                    subtitle = "支付、激活和历史都从这里进入",
                                    onClick = onNavigateToOrders,
                                    trailing = {
                                        Text(
                                            text = "打开",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = VpnAccent,
                                        )
                                    },
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun VPNConnectionStatus.accentColor(): Color {
    return when (this) {
        VPNConnectionStatus.CONNECTED -> VpnAccent
        VPNConnectionStatus.CONNECTING -> Color(0xFFFFB14A)
        VPNConnectionStatus.DISCONNECTING -> Color(0xFFFFB14A)
        VPNConnectionStatus.DISCONNECTED -> AppError
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

private fun VPNConnectionStatus.headlineValue(duration: String): String {
    return when (this) {
        VPNConnectionStatus.CONNECTED -> duration
        VPNConnectionStatus.CONNECTING -> "连接中"
        VPNConnectionStatus.DISCONNECTING -> "断开中"
        VPNConnectionStatus.DISCONNECTED -> "--"
    }
}

private fun VPNConnectionStatus.headlineChange(latency: Int): String {
    return when (this) {
        VPNConnectionStatus.CONNECTED -> "+$latency ms · route stable"
        VPNConnectionStatus.CONNECTING -> "正在与当前节点握手"
        VPNConnectionStatus.DISCONNECTING -> "会话正在安全关闭"
        VPNConnectionStatus.DISCONNECTED -> "等待启动新的安全线路"
    }
}

private fun VPNConnectionStatus.helperLabel(): String {
    return when (this) {
        VPNConnectionStatus.CONNECTED -> "夜盘"
        VPNConnectionStatus.CONNECTING -> "准备中"
        VPNConnectionStatus.DISCONNECTING -> "关闭中"
        VPNConnectionStatus.DISCONNECTED -> "待机"
    }
}

private fun VPNConnectionStatus.actionLabel(): String {
    return when (this) {
        VPNConnectionStatus.CONNECTED -> "断开线路"
        VPNConnectionStatus.CONNECTING -> "连接中"
        VPNConnectionStatus.DISCONNECTING -> "断开中"
        VPNConnectionStatus.DISCONNECTED -> "连接"
    }
}

@Preview
@Composable
private fun VPNHomePagePreview() {
    MaterialTheme {
        VPNHomePage()
    }
}
