package com.cryptovpn.ui.pages.vpn

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.cryptovpn.ui.theme.CryptoVPNTheme

// ============================================
// VPN 首页状态定义
// ============================================
sealed class VPNHomeState {
    abstract val mode: VPNMode
    abstract val selectedRegion: RegionInfo

    data class NoSubscription(
        override val mode: VPNMode = VPNMode.GLOBAL,
        override val selectedRegion: RegionInfo = RegionInfo.DEFAULT
    ) : VPNHomeState()

    data class ActiveReady(
        val planName: String,
        val remainingDays: Int,
        override val mode: VPNMode = VPNMode.GLOBAL,
        override val selectedRegion: RegionInfo = RegionInfo.DEFAULT
    ) : VPNHomeState()

    data class Connecting(
        val progress: Float = 0f,
        override val mode: VPNMode = VPNMode.GLOBAL,
        override val selectedRegion: RegionInfo = RegionInfo.DEFAULT
    ) : VPNHomeState()

    data class Connected(
        val duration: String = "00:00:00",
        val uploadSpeed: String = "0 KB/s",
        val downloadSpeed: String = "0 KB/s",
        override val mode: VPNMode = VPNMode.GLOBAL,
        override val selectedRegion: RegionInfo = RegionInfo.DEFAULT
    ) : VPNHomeState()

    data class Suspended(
        val reason: String,
        val planName: String,
        val remainingDays: Int,
        override val mode: VPNMode = VPNMode.GLOBAL,
        override val selectedRegion: RegionInfo = RegionInfo.DEFAULT
    ) : VPNHomeState()

    data class Expired(
        val expiredDate: String,
        override val mode: VPNMode = VPNMode.GLOBAL,
        override val selectedRegion: RegionInfo = RegionInfo.DEFAULT
    ) : VPNHomeState()
}

enum class VPNMode {
    GLOBAL,  // 全局模式
    RULE     // 规则模式
}

data class RegionInfo(
    val id: String,
    val name: String,
    val countryCode: String,
    val flag: String,
    val latency: Int = 0
) {
    companion object {
        val DEFAULT = RegionInfo(
            id = "auto",
            name = "智能选择",
            countryCode = "AUTO",
            flag = "🌍",
            latency = 0
        )
    }
}

// ============================================
// VPN 首页
// ============================================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VPNHomePage(
    viewModel: VPNHomeViewModel = hiltViewModel(),
    onMenuClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    onRenewClick: () -> Unit = {},
    onRegionClick: () -> Unit = {},
    onUpgradeClick: () -> Unit = {},
    onPlansClick: () -> Unit = {}
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            VPNHomeTopBar(
                onMenuClick = onMenuClick,
                onSettingsClick = onSettingsClick,
                onProfileClick = onProfileClick
            )
        },
        containerColor = Color(0xFF0B1020)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFF0B1020)),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 订阅状态卡片
            SubscriptionStatusCard(
                state = state,
                onRenewClick = onRenewClick,
                onUpgradeClick = onUpgradeClick
            )

            Spacer(modifier = Modifier.weight(1f))

            // 连接状态可视化区域
            ConnectionStatusArea(
                state = state,
                modifier = Modifier.padding(vertical = 24.dp)
            )

            Spacer(modifier = Modifier.weight(1f))

            // 主连接按钮
            MainConnectButton(
                state = state,
                onClick = { viewModel.toggleConnection() },
                modifier = Modifier.padding(vertical = 16.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // 模式切换
            ModeSwitch(
                currentMode = state.mode,
                onModeChange = { viewModel.switchMode(it) },
                enabled = state is VPNHomeState.ActiveReady || 
                         state is VPNHomeState.Connected ||
                         state is VPNHomeState.NoSubscription
            )

            Spacer(modifier = Modifier.height(32.dp))

            // 底部操作栏
            BottomActionBar(
                onRegionClick = onRegionClick,
                onPlansClick = onPlansClick
            )
        }
    }
}

// ============================================
// 顶部导航栏
// ============================================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun VPNHomeTopBar(
    onMenuClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onProfileClick: () -> Unit
) {
    TopAppBar(
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.VpnKey,
                    contentDescription = null,
                    tint = Color(0xFF1D4ED8),
                    modifier = Modifier.size(28.dp)
                )
                Text(
                    text = "CryptoVPN",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        navigationIcon = {
            IconButton(onClick = onMenuClick) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "菜单",
                    tint = Color.White
                )
            }
        },
        actions = {
            IconButton(onClick = onSettingsClick) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "设置",
                    tint = Color.White
                )
            }
            IconButton(onClick = onProfileClick) {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = "个人中心",
                    tint = Color.White
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color(0xFF0B1020)
        )
    )
}

// ============================================
// 订阅状态卡片
// ============================================
@Composable
private fun SubscriptionStatusCard(
    state: VPNHomeState,
    onRenewClick: () -> Unit,
    onUpgradeClick: () -> Unit
) {
    val cardBackground = when (state) {
        is VPNHomeState.NoSubscription -> Color(0xFF1F2937)
        is VPNHomeState.ActiveReady -> Brush.linearGradient(
            colors = listOf(Color(0xFF1D4ED8), Color(0xFF3B82F6))
        )
        is VPNHomeState.Connecting -> Brush.linearGradient(
            colors = listOf(Color(0xFF1D4ED8), Color(0xFF3B82F6))
        )
        is VPNHomeState.Connected -> Brush.linearGradient(
            colors = listOf(Color(0xFF22C55E), Color(0xFF16A34A))
        )
        is VPNHomeState.Suspended -> Brush.linearGradient(
            colors = listOf(Color(0xFFF59E0B), Color(0xFFD97706))
        )
        is VPNHomeState.Expired -> Brush.linearGradient(
            colors = listOf(Color(0xFFEF4444), Color(0xFFDC2626))
        )
    }

    val showRenewButton = state is VPNHomeState.ActiveReady && 
                          (state as? VPNHomeState.ActiveReady)?.remainingDays?.let { it <= 7 } == true

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = cardBackground as? Brush ?: SolidColor(cardBackground as Color)
                )
                .padding(16.dp)
        ) {
            when (state) {
                is VPNHomeState.NoSubscription -> {
                    NoSubscriptionContent(onUpgradeClick = onUpgradeClick)
                }
                is VPNHomeState.ActiveReady -> {
                    ActiveSubscriptionContent(
                        planName = state.planName,
                        remainingDays = state.remainingDays,
                        showRenewButton = showRenewButton,
                        onRenewClick = onRenewClick
                    )
                }
                is VPNHomeState.Connecting -> {
                    ConnectingContent()
                }
                is VPNHomeState.Connected -> {
                    ConnectedContent(
                        duration = state.duration,
                        uploadSpeed = state.uploadSpeed,
                        downloadSpeed = state.downloadSpeed
                    )
                }
                is VPNHomeState.Suspended -> {
                    SuspendedContent(
                        reason = state.reason,
                        planName = state.planName,
                        remainingDays = state.remainingDays,
                        onRenewClick = onRenewClick
                    )
                }
                is VPNHomeState.Expired -> {
                    ExpiredContent(
                        expiredDate = state.expiredDate,
                        onRenewClick = onRenewClick
                    )
                }
            }
        }
    }
}

@Composable
private fun NoSubscriptionContent(onUpgradeClick: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "暂无有效套餐",
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "购买套餐即可开始安全上网",
            color = Color(0xFF9CA3AF),
            fontSize = 14.sp
        )
        Spacer(modifier = Modifier.height(12.dp))
        Button(
            onClick = onUpgradeClick,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF1D4ED8)
            ),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text("选择套餐")
        }
    }
}

@Composable
private fun ActiveSubscriptionContent(
    planName: String,
    remainingDays: Int,
    showRenewButton: Boolean,
    onRenewClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    color = Color.White.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = planName,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "剩余 $remainingDays 天",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }

        if (showRenewButton) {
            Button(
                onClick = onRenewClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "续费",
                    color = Color(0xFF1D4ED8),
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun ConnectingContent() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                color = Color.White,
                strokeWidth = 2.dp
            )
            Text(
                text = "正在连接...",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun ConnectedContent(
    duration: String,
    uploadSpeed: String,
    downloadSpeed: String
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(Color.White, CircleShape)
            )
            Text(
                text = "已连接",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = duration,
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 14.sp
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            SpeedIndicator(
                icon = Icons.Default.ArrowUpward,
                label = "上传",
                value = uploadSpeed
            )
            SpeedIndicator(
                icon = Icons.Default.ArrowDownward,
                label = "下载",
                value = downloadSpeed
            )
        }
    }
}

@Composable
private fun SpeedIndicator(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = Color.White.copy(alpha = 0.8f),
            modifier = Modifier.size(16.dp)
        )
        Text(
            text = "$label: $value",
            color = Color.White.copy(alpha = 0.8f),
            fontSize = 12.sp
        )
    }
}

@Composable
private fun SuspendedContent(
    reason: String,
    planName: String,
    remainingDays: Int,
    onRenewClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = "服务暂停",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = reason,
            color = Color.White.copy(alpha = 0.8f),
            fontSize = 14.sp
        )
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = onRenewClick,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.White
            ),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(
                text = "恢复服务",
                color = Color(0xFFF59E0B),
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun ExpiredContent(
    expiredDate: String,
    onRenewClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "套餐已过期",
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "过期时间: $expiredDate",
            color = Color.White.copy(alpha = 0.8f),
            fontSize = 14.sp
        )
        Spacer(modifier = Modifier.height(12.dp))
        Button(
            onClick = onRenewClick,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.White
            ),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(
                text = "立即续费",
                color = Color(0xFFEF4444),
                fontWeight = FontWeight.Medium
            )
        }
    }
}

// ============================================
// 连接状态可视化区域
// ============================================
@Composable
private fun ConnectionStatusArea(
    state: VPNHomeState,
    modifier: Modifier = Modifier
) {
    val statusColor = when (state) {
        is VPNHomeState.NoSubscription -> Color(0xFF64748B)
        is VPNHomeState.ActiveReady -> Color(0xFF64748B)
        is VPNHomeState.Connecting -> Color(0xFF3B82F6)
        is VPNHomeState.Connected -> Color(0xFF22C55E)
        is VPNHomeState.Suspended -> Color(0xFFF59E0B)
        is VPNHomeState.Expired -> Color(0xFFEF4444)
    }

    val statusText = when (state) {
        is VPNHomeState.NoSubscription -> "未连接"
        is VPNHomeState.ActiveReady -> "准备就绪"
        is VPNHomeState.Connecting -> "连接中..."
        is VPNHomeState.Connected -> "已安全连接"
        is VPNHomeState.Suspended -> "服务暂停"
        is VPNHomeState.Expired -> "服务已过期"
    }

    val isPulsing = state is VPNHomeState.Connecting || state is VPNHomeState.Connected

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (isPulsing) 1.1f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 地球图标
        Box(
            modifier = Modifier
                .size(120.dp)
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                },
            contentAlignment = Alignment.Center
        ) {
            // 外圈光环
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .border(
                        width = 2.dp,
                        color = statusColor.copy(alpha = 0.3f),
                        shape = CircleShape
                    )
            )

            // 内圈
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .background(statusColor.copy(alpha = 0.1f), CircleShape)
                    .border(
                        width = 2.dp,
                        color = statusColor,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = state.selectedRegion.flag,
                    fontSize = 40.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 区域名称
        Text(
            text = state.selectedRegion.name,
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(8.dp))

        // 状态文字
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(statusColor, CircleShape)
            )
            Text(
                text = statusText,
                color = statusColor,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
        }

        // 延迟显示
        if (state.selectedRegion.latency > 0) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "延迟: ${state.selectedRegion.latency}ms",
                color = Color(0xFF9CA3AF),
                fontSize = 14.sp
            )
        }
    }
}

// ============================================
// 主连接按钮
// ============================================
@Composable
private fun MainConnectButton(
    state: VPNHomeState,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val buttonColor = when (state) {
        is VPNHomeState.NoSubscription -> Color(0xFF64748B)
        is VPNHomeState.ActiveReady -> Color(0xFF1D4ED8)
        is VPNHomeState.Connecting -> Color(0xFF3B82F6)
        is VPNHomeState.Connected -> Color(0xFF22C55E)
        is VPNHomeState.Suspended -> Color(0xFFF59E0B)
        is VPNHomeState.Expired -> Color(0xFFEF4444)
    }

    val buttonText = when (state) {
        is VPNHomeState.NoSubscription -> "选择套餐"
        is VPNHomeState.ActiveReady -> "点击连接"
        is VPNHomeState.Connecting -> "连接中..."
        is VPNHomeState.Connected -> "断开连接"
        is VPNHomeState.Suspended -> "恢复服务"
        is VPNHomeState.Expired -> "立即续费"
    }

    val isEnabled = state !is VPNHomeState.Connecting

    val infiniteTransition = rememberInfiniteTransition(label = "button_pulse")
    val buttonScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = when (state) {
            is VPNHomeState.ActiveReady -> 1.05f
            is VPNHomeState.Connected -> 1.02f
            else -> 1f
        },
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "button_scale"
    )

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        // 外圈光晕效果
        if (state is VPNHomeState.ActiveReady || state is VPNHomeState.Connected) {
            Box(
                modifier = Modifier
                    .size(140.dp)
                    .background(
                        color = buttonColor.copy(alpha = 0.2f),
                        shape = CircleShape
                    )
            )
        }

        // 主按钮
        Button(
            onClick = onClick,
            enabled = isEnabled,
            modifier = Modifier
                .size(120.dp)
                .scale(if (state is VPNHomeState.ActiveReady || state is VPNHomeState.Connected) buttonScale else 1f),
            shape = CircleShape,
            colors = ButtonDefaults.buttonColors(
                containerColor = buttonColor,
                disabledContainerColor = buttonColor.copy(alpha = 0.5f)
            ),
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = 8.dp,
                pressedElevation = 4.dp
            )
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                when (state) {
                    is VPNHomeState.Connecting -> {
                        CircularProgressIndicator(
                            modifier = Modifier.size(32.dp),
                            color = Color.White,
                            strokeWidth = 3.dp
                        )
                    }
                    is VPNHomeState.Connected -> {
                        Icon(
                            imageVector = Icons.Default.PowerSettingsNew,
                            contentDescription = null,
                            modifier = Modifier.size(40.dp)
                        )
                    }
                    else -> {
                        Icon(
                            imageVector = Icons.Default.PowerSettingsNew,
                            contentDescription = null,
                            modifier = Modifier.size(40.dp)
                        )
                    }
                }
                if (state !is VPNHomeState.Connecting) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = buttonText,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

// ============================================
// 模式切换
// ============================================
@Composable
private fun ModeSwitch(
    currentMode: VPNMode,
    onModeChange: (VPNMode) -> Unit,
    enabled: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 48.dp)
            .background(Color(0xFF1F2937), RoundedCornerShape(24.dp))
            .padding(4.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        ModeOption(
            text = "全局模式",
            isSelected = currentMode == VPNMode.GLOBAL,
            onClick = { onModeChange(VPNMode.GLOBAL) },
            enabled = enabled,
            icon = Icons.Default.Public
        )
        ModeOption(
            text = "规则模式",
            isSelected = currentMode == VPNMode.RULE,
            onClick = { onModeChange(VPNMode.RULE) },
            enabled = enabled,
            icon = Icons.Default.Rule
        )
    }
}

@Composable
private fun ModeOption(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    enabled: Boolean,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    val backgroundColor = if (isSelected) Color(0xFF1D4ED8) else Color.Transparent
    val textColor = if (isSelected) Color.White else Color(0xFF9CA3AF)

    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(backgroundColor)
            .clickable(enabled = enabled) { onClick() }
            .padding(horizontal = 20.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = textColor,
            modifier = Modifier.size(18.dp)
        )
        Text(
            text = text,
            color = textColor,
            fontSize = 14.sp,
            fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal
        )
    }
}

// ============================================
// 底部操作栏
// ============================================
@Composable
private fun BottomActionBar(
    onRegionClick: () -> Unit,
    onPlansClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        BottomActionItem(
            icon = Icons.Default.Place,
            label = "切换区域",
            onClick = onRegionClick
        )
        BottomActionItem(
            icon = Icons.Default.Upgrade,
            label = "升级套餐",
            onClick = onPlansClick
        )
    }
}

@Composable
private fun BottomActionItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .background(Color(0xFF1F2937), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = Color(0xFF1D4ED8),
                modifier = Modifier.size(28.dp)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = label,
            color = Color(0xFF9CA3AF),
            fontSize = 14.sp
        )
    }
}

// ============================================
// 预览
// ============================================
@Preview(name = "VPN首页 - 无套餐", showBackground = true, backgroundColor = 0xFF0B1020)
@Composable
fun VPNHomePagePreview_NoSubscription() {
    CryptoVPNTheme {
        VPNHomePageContentPreview(VPNHomeState.NoSubscription())
    }
}

@Preview(name = "VPN首页 - 准备就绪", showBackground = true, backgroundColor = 0xFF0B1020)
@Composable
fun VPNHomePagePreview_ActiveReady() {
    CryptoVPNTheme {
        VPNHomePageContentPreview(
            VPNHomeState.ActiveReady(
                planName = "专业版",
                remainingDays = 30
            )
        )
    }
}

@Preview(name = "VPN首页 - 连接中", showBackground = true, backgroundColor = 0xFF0B1020)
@Composable
fun VPNHomePagePreview_Connecting() {
    CryptoVPNTheme {
        VPNHomePageContentPreview(VPNHomeState.Connecting())
    }
}

@Preview(name = "VPN首页 - 已连接", showBackground = true, backgroundColor = 0xFF0B1020)
@Composable
fun VPNHomePagePreview_Connected() {
    CryptoVPNTheme {
        VPNHomePageContentPreview(
            VPNHomeState.Connected(
                duration = "01:23:45",
                uploadSpeed = "1.2 MB/s",
                downloadSpeed = "5.6 MB/s"
            )
        )
    }
}

@Composable
private fun VPNHomePageContentPreview(state: VPNHomeState) {
    Scaffold(
        topBar = {
            VPNHomeTopBar({}, {}, {})
        },
        containerColor = Color(0xFF0B1020)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFF0B1020)),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            SubscriptionStatusCard(
                state = state,
                onRenewClick = {},
                onUpgradeClick = {}
            )

            Spacer(modifier = Modifier.weight(1f))

            ConnectionStatusArea(
                state = state,
                modifier = Modifier.padding(vertical = 24.dp)
            )

            Spacer(modifier = Modifier.weight(1f))

            MainConnectButton(
                state = state,
                onClick = {},
                modifier = Modifier.padding(vertical = 16.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            ModeSwitch(
                currentMode = state.mode,
                onModeChange = {},
                enabled = true
            )

            Spacer(modifier = Modifier.height(32.dp))

            BottomActionBar({}, {})
        }
    }
}
