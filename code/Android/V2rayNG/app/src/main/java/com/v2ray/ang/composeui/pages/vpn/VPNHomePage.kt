package com.v2ray.ang.composeui.pages.vpn

import android.app.Application
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.v2ray.ang.payment.data.repository.PaymentRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * VPN连接状态
 */
enum class VPNConnectionStatus {
    DISCONNECTED,    // 未连接
    CONNECTING,      // 连接中
    CONNECTED,       // 已连接
    DISCONNECTING    // 断开中
}

/**
 * VPN首页状态
 */
sealed class VPNHomeState {
    object Idle : VPNHomeState()
    object Loading : VPNHomeState()
    data class Loaded(
        val status: VPNConnectionStatus,
        val selectedRegion: RegionInfo,
        val connectionDuration: String,
        val uploadSpeed: String,
        val downloadSpeed: String
    ) : VPNHomeState()
    data class Error(val message: String) : VPNHomeState()
}

/**
 * 区域信息
 */
data class RegionInfo(
    val id: String,
    val name: String,
    val countryCode: String,
    val latency: Int
)

/**
 * VPN首页ViewModel
 */
class VPNHomeViewModel(application: Application) : AndroidViewModel(application) {
    private val _state = MutableStateFlow<VPNHomeState>(VPNHomeState.Idle)
    val state: StateFlow<VPNHomeState> = _state
    private val repository = PaymentRepository(application)

    private val _selectedRegion = MutableStateFlow(
        RegionInfo("us", "美国", "US", 45)
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
                    delay(2000)
                    _connectionStatus.value = VPNConnectionStatus.CONNECTED
                    startDurationTimer()
                }
                VPNConnectionStatus.CONNECTED -> {
                    _connectionStatus.value = VPNConnectionStatus.DISCONNECTING
                    delay(1000)
                    _connectionStatus.value = VPNConnectionStatus.DISCONNECTED
                    _connectionDuration.value = "00:00:00"
                }
                else -> {}
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
        // 如果已连接，先断开再连接
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
            downloadSpeed = if (_connectionStatus.value == VPNConnectionStatus.CONNECTED) "5.8 MB/s" else "0 KB/s"
        )
    }
}

/**
 * VPN首页
 * 显示连接状态、区域选择和连接按钮
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VPNHomePage(
    viewModel: VPNHomeViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    onNavigateToRegions: () -> Unit = {},
    onNavigateToPlans: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {}
) {
    val state by viewModel.state.collectAsState()
    val connectionStatus by viewModel.connectionStatus.collectAsState()
    val selectedRegion by viewModel.selectedRegion.collectAsState()
    val connectionDuration by viewModel.connectionDuration.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("CryptoVPN") },
                actions = {
                    IconButton(onClick = onNavigateToProfile) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Profile"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // 连接状态卡片
            ConnectionStatusCard(connectionStatus, connectionDuration)

            Spacer(modifier = Modifier.height(32.dp))

            // 区域选择
            RegionSelector(
                region = selectedRegion,
                onClick = onNavigateToRegions
            )

            Spacer(modifier = Modifier.weight(1f))

            // 连接按钮
            ConnectionButton(
                status = connectionStatus,
                onClick = { viewModel.toggleConnection() }
            )

            Spacer(modifier = Modifier.height(32.dp))

            // 购买套餐提示
            if (connectionStatus == VPNConnectionStatus.DISCONNECTED) {
                PurchasePrompt(onClick = onNavigateToPlans)
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun ConnectionStatusCard(
    status: VPNConnectionStatus,
    duration: String
) {
    val statusColor = when (status) {
        VPNConnectionStatus.CONNECTED -> Color(0xFF22C55E)
        VPNConnectionStatus.CONNECTING, VPNConnectionStatus.DISCONNECTING -> Color(0xFFF59E0B)
        VPNConnectionStatus.DISCONNECTED -> Color(0xFF94A3B8)
    }

    val statusText = when (status) {
        VPNConnectionStatus.CONNECTED -> "已连接"
        VPNConnectionStatus.CONNECTING -> "连接中..."
        VPNConnectionStatus.DISCONNECTING -> "断开中..."
        VPNConnectionStatus.DISCONNECTED -> "未连接"
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 状态指示器
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // 状态点
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .background(statusColor, CircleShape)
                )
                Text(
                    text = statusText,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = statusColor
                )
            }

            if (status == VPNConnectionStatus.CONNECTED) {
                Spacer(modifier = Modifier.height(16.dp))
                
                // 连接时长
                Text(
                    text = duration,
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // 速度显示
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    SpeedIndicator(
                        icon = Icons.Default.ArrowDownward,
                        label = "下载",
                        value = "5.8 MB/s"
                    )
                    SpeedIndicator(
                        icon = Icons.Default.ArrowUpward,
                        label = "上传",
                        value = "1.2 MB/s"
                    )
                }
            }
        }
    }
}

@Composable
private fun SpeedIndicator(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun RegionSelector(
    region: RegionInfo,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 区域图标
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                modifier = Modifier.size(48.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = region.countryCode,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // 区域信息
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = region.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "延迟: ${region.latency}ms",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // 箭头
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "Select",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun ConnectionButton(
    status: VPNConnectionStatus,
    onClick: () -> Unit
) {
    val isConnecting = status == VPNConnectionStatus.CONNECTING || 
                       status == VPNConnectionStatus.DISCONNECTING
    
    val buttonColor = when (status) {
        VPNConnectionStatus.CONNECTED -> Color(0xFFEF4444)
        else -> MaterialTheme.colorScheme.primary
    }

    val buttonText = when (status) {
        VPNConnectionStatus.CONNECTED -> "断开连接"
        VPNConnectionStatus.CONNECTING -> "连接中..."
        VPNConnectionStatus.DISCONNECTING -> "断开中..."
        VPNConnectionStatus.DISCONNECTED -> "连接"
    }

    // 脉冲动画
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (status == VPNConnectionStatus.CONNECTED) 1f else 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    Box(
        modifier = Modifier
            .size(160.dp)
            .scale(if (status == VPNConnectionStatus.DISCONNECTED) scale else 1f),
        contentAlignment = Alignment.Center
    ) {
        // 外圈
        if (status == VPNConnectionStatus.CONNECTED) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .border(
                        width = 4.dp,
                        color = Color(0xFF22C55E).copy(alpha = 0.3f),
                        shape = CircleShape
                    )
            )
        }

        // 按钮
        Button(
            onClick = onClick,
            modifier = Modifier.size(140.dp),
            shape = CircleShape,
            colors = ButtonDefaults.buttonColors(
                containerColor = buttonColor
            ),
            enabled = !isConnecting
        ) {
            if (isConnecting) {
                CircularProgressIndicator(
                    modifier = Modifier.size(32.dp),
                    color = MaterialTheme.colorScheme.onPrimary,
                    strokeWidth = 3.dp
                )
            } else {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = if (status == VPNConnectionStatus.CONNECTED) 
                            Icons.Default.PowerOff else Icons.Default.Power,
                        contentDescription = buttonText,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = buttonText,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

@Composable
private fun PurchasePrompt(onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "升级套餐",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "解锁更多高速节点",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "Go",
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun VPNHomePagePreview() {
    MaterialTheme {
        VPNHomePage()
    }
}
