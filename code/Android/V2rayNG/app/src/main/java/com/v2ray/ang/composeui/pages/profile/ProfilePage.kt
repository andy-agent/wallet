package com.v2ray.ang.composeui.pages.profile

import android.app.Application
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.SupervisorAccount
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.v2ray.ang.composeui.bridge.profile.ProfileBridgeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

private val ProfilePageBackground = Color(0xFFF5F7FA)
private val ProfileCardBackground = Color.White
private val ProfileAccent = Color(0xFF00C2A8)
private val ProfileAccentDeep = Color(0xFF0E8E7F)
private val ProfileTextSecondary = Color(0xFF667085)
private val ProfileDivider = Color(0xFFEAECEF)

data class UserInfo(
    val email: String,
    val nickname: String?,
    val avatarUrl: String?,
    val memberLevel: String,
    val memberExpiry: String?
)

data class SettingItem(
    val icon: ImageVector,
    val title: String,
    val subtitle: String? = null,
    val badge: String? = null,
    val showArrow: Boolean = true
)

sealed class ProfileState {
    data object Idle : ProfileState()
    data object Loading : ProfileState()
    data class Loaded(val user: UserInfo) : ProfileState()
    data class Error(val message: String) : ProfileState()
}

class ProfileViewModel(application: Application) : AndroidViewModel(application) {
    private val profileBridgeRepository = ProfileBridgeRepository(application)
    private val _state = MutableStateFlow<ProfileState>(ProfileState.Idle)
    val state: StateFlow<ProfileState> = _state

    init {
        loadUserData()
    }

    private fun loadUserData() {
        viewModelScope.launch {
            _state.value = ProfileState.Loading
            val user = profileBridgeRepository.getCachedCurrentUser()
            if (user == null) {
                _state.value = ProfileState.Error("当前未登录")
                return@launch
            }

            val orders = profileBridgeRepository.getCachedOrders(user.userId)
            val activeOrder =
                orders.firstOrNull { it.expiredAt != null && it.expiredAt > System.currentTimeMillis() }
            _state.value = ProfileState.Loaded(
                user = UserInfo(
                    email = user.email ?: user.username,
                    nickname = user.username.substringBefore("@"),
                    avatarUrl = null,
                    memberLevel = if (activeOrder != null) "已订阅" else "基础用户",
                    memberExpiry = activeOrder?.expiredAt?.toString()
                )
            )
        }
    }

    fun logout() {
        viewModelScope.launch {
            profileBridgeRepository.logout()
            _state.value = ProfileState.Error("已退出登录")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfilePage(
    viewModel: ProfileViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    onNavigateToOrders: () -> Unit = {},
    onNavigateToWallet: () -> Unit = {},
    onNavigateToInvite: () -> Unit = {},
    onNavigateToCommission: () -> Unit = {},
    onNavigateToSettings: () -> Unit = {},
    onNavigateToLegal: () -> Unit = {},
    onNavigateToSupport: () -> Unit = {},
    onNavigateToAbout: () -> Unit = {},
    onLogout: () -> Unit = {}
) {
    val state by viewModel.state.collectAsState()
    val showLogoutDialog = remember { mutableStateOf(false) }

    Scaffold(
        containerColor = ProfilePageBackground,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Profile",
                        fontWeight = FontWeight.SemiBold
                    )
                },
                actions = {
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(ProfilePageBackground)
                .padding(paddingValues)
        ) {
            when (val currentState = state) {
                is ProfileState.Loaded -> {
                    ProfileContent(
                        user = currentState.user,
                        onNavigateToOrders = onNavigateToOrders,
                        onNavigateToWallet = onNavigateToWallet,
                        onNavigateToInvite = onNavigateToInvite,
                        onNavigateToCommission = onNavigateToCommission,
                        onNavigateToLegal = onNavigateToLegal,
                        onNavigateToSupport = onNavigateToSupport,
                        onNavigateToAbout = onNavigateToAbout,
                        onLogoutClick = { showLogoutDialog.value = true }
                    )
                }

                ProfileState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = ProfileAccent)
                    }
                }

                is ProfileState.Error -> ErrorView(currentState.message)
                ProfileState.Idle -> Unit
            }
        }
    }

    if (showLogoutDialog.value) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog.value = false },
            title = { Text("确认登出") },
            text = { Text("确定要退出当前账号吗？") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showLogoutDialog.value = false
                        viewModel.logout()
                        onLogout()
                    }
                ) {
                    Text("确认", color = Color(0xFFEF4444))
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog.value = false }) {
                    Text("取消")
                }
            }
        )
    }
}

@Composable
private fun ProfileContent(
    user: UserInfo,
    onNavigateToOrders: () -> Unit,
    onNavigateToWallet: () -> Unit,
    onNavigateToInvite: () -> Unit,
    onNavigateToCommission: () -> Unit,
    onNavigateToLegal: () -> Unit,
    onNavigateToSupport: () -> Unit,
    onNavigateToAbout: () -> Unit,
    onLogoutClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Spacer(modifier = Modifier.height(4.dp))
        UserInfoCard(user = user)
        StatusOverviewCard(user = user)
        SectionTitle(title = "快捷入口", caption = "常用功能集中管理")
        QuickAccessGrid(
            onNavigateToOrders = onNavigateToOrders,
            onNavigateToWallet = onNavigateToWallet,
            onNavigateToInvite = onNavigateToInvite,
            onNavigateToCommission = onNavigateToCommission
        )
        SectionTitle(title = "服务与支持", caption = "协议、帮助与应用信息")
        SettingsList(
            onNavigateToLegal = onNavigateToLegal,
            onNavigateToSupport = onNavigateToSupport,
            onNavigateToAbout = onNavigateToAbout
        )
        LogoutCard(onClick = onLogoutClick)
        Text(
            text = "CryptoVPN v1.0.0",
            fontSize = 12.sp,
            color = ProfileTextSecondary,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun UserInfoCard(user: UserInfo) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = ProfileCardBackground),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(Color(0xFF103B43), Color(0xFF1C5C57), Color(0xFF00C2A8))
                    )
                )
                .padding(20.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(18.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = CircleShape,
                        color = Color.White.copy(alpha = 0.16f),
                        modifier = Modifier.size(68.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = user.nickname?.take(1)?.uppercase()
                                    ?: user.email.take(1).uppercase(),
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = user.nickname ?: user.email.substringBefore("@"),
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = user.email,
                            fontSize = 13.sp,
                            color = Color.White.copy(alpha = 0.78f)
                        )
                    }
                    Surface(
                        shape = RoundedCornerShape(999.dp),
                        color = Color.White.copy(alpha = 0.14f)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.VerifiedUser,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "安全",
                                fontSize = 12.sp,
                                color = Color.White,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    ProfileBadge(
                        icon = Icons.Default.Star,
                        text = user.memberLevel
                    )
                    ProfileBadge(
                        icon = Icons.Default.Shield,
                        text = "账号中心"
                    )
                }
            }
        }
    }
}

@Composable
private fun ProfileBadge(icon: ImageVector, text: String) {
    Surface(
        shape = RoundedCornerShape(999.dp),
        color = Color.White.copy(alpha = 0.14f)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = text,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White
            )
        }
    }
}

@Composable
private fun StatusOverviewCard(user: UserInfo) {
    val statusText = if (user.memberLevel == "已订阅") "服务有效" else "尚未激活"
    val caption = if (user.memberLevel == "已订阅") {
        "当前订阅状态正常，可继续使用全部线路与账户能力。"
    } else {
        "开通订阅后可解锁完整网络能力与更高优先级支持。"
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = ProfileCardBackground),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "账户概览",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF101828)
                    )
                    Text(
                        text = caption,
                        fontSize = 13.sp,
                        color = ProfileTextSecondary,
                        lineHeight = 18.sp
                    )
                }
                Surface(
                    shape = RoundedCornerShape(999.dp),
                    color = ProfileAccent.copy(alpha = 0.12f)
                ) {
                    Text(
                        text = statusText,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                        fontSize = 12.sp,
                        color = ProfileAccentDeep,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                SummaryStat(
                    modifier = Modifier.weight(1f),
                    label = "会员",
                    value = user.memberLevel
                )
                SummaryStat(
                    modifier = Modifier.weight(1f),
                    label = "身份",
                    value = user.nickname ?: "访客"
                )
            }
        }
    }
}

@Composable
private fun SummaryStat(modifier: Modifier = Modifier, label: String, value: String) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .background(Color(0xFFF8FAFB))
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            color = ProfileTextSecondary
        )
        Text(
            text = value,
            fontSize = 15.sp,
            color = Color(0xFF101828),
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun SectionTitle(title: String, caption: String) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = title,
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF101828)
        )
        Text(
            text = caption,
            fontSize = 13.sp,
            color = ProfileTextSecondary
        )
    }
}

@Composable
private fun QuickAccessGrid(
    onNavigateToOrders: () -> Unit,
    onNavigateToWallet: () -> Unit,
    onNavigateToInvite: () -> Unit,
    onNavigateToCommission: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = ProfileCardBackground),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                QuickAccessItem(
                    modifier = Modifier.weight(1f),
                    icon = Icons.AutoMirrored.Filled.ReceiptLong,
                    label = "我的订单",
                    accent = Color(0xFFE8FFF7),
                    onClick = onNavigateToOrders
                )
                QuickAccessItem(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Default.AccountBalanceWallet,
                    label = "我的钱包",
                    accent = Color(0xFFEFF4FF),
                    onClick = onNavigateToWallet
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                QuickAccessItem(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Default.SupervisorAccount,
                    label = "邀请好友",
                    accent = Color(0xFFFFF4EA),
                    onClick = onNavigateToInvite
                )
                QuickAccessItem(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Default.AttachMoney,
                    label = "佣金收益",
                    accent = Color(0xFFFFF5F5),
                    onClick = onNavigateToCommission
                )
            }
        }
    }
}

@Composable
private fun QuickAccessItem(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    label: String,
    accent: Color,
    onClick: () -> Unit
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0xFFF8FAFB))
            .clickable(onClick = onClick)
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = accent,
            modifier = Modifier.size(46.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = Color(0xFF101828),
                    modifier = Modifier.size(22.dp)
                )
            }
        }
        Text(
            text = label,
            fontSize = 14.sp,
            color = Color(0xFF101828),
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun SettingsList(
    onNavigateToLegal: () -> Unit,
    onNavigateToSupport: () -> Unit,
    onNavigateToAbout: () -> Unit
) {
    val settings = listOf(
        SettingItem(
            icon = Icons.Default.Description,
            title = "法务文档",
            subtitle = "用户协议、隐私政策与退款规则",
            badge = "Legal"
        ),
        SettingItem(
            icon = Icons.AutoMirrored.Filled.Help,
            title = "帮助与反馈",
            subtitle = "常见问题、联系客服",
            badge = "Support"
        ),
        SettingItem(
            icon = Icons.Default.Info,
            title = "关于我们",
            subtitle = "版本信息、更新日志",
            badge = "About"
        )
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = ProfileCardBackground),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
            settings.forEachIndexed { index, item ->
                SettingListItem(
                    item = item,
                    onClick = {
                        when (item.title) {
                            "法务文档" -> onNavigateToLegal()
                            "帮助与反馈" -> onNavigateToSupport()
                            "关于我们" -> onNavigateToAbout()
                        }
                    }
                )
                if (index < settings.lastIndex) {
                    HorizontalDivider(color = ProfileDivider)
                }
            }
        }
    }
}

@Composable
private fun SettingListItem(item: SettingItem, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            shape = RoundedCornerShape(14.dp),
            color = ProfileAccent.copy(alpha = 0.12f),
            modifier = Modifier.size(42.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = item.icon,
                    contentDescription = item.title,
                    tint = ProfileAccentDeep,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
            Text(
                text = item.title,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF101828)
            )
            item.subtitle?.let {
                Text(
                    text = it,
                    fontSize = 12.sp,
                    color = ProfileTextSecondary,
                    lineHeight = 17.sp
                )
            }
        }
        item.badge?.let {
            Surface(
                shape = RoundedCornerShape(999.dp),
                color = Color(0xFFF2F4F7)
            ) {
                Text(
                    text = it,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    fontSize = 11.sp,
                    color = ProfileTextSecondary,
                    fontWeight = FontWeight.Medium
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
        }
        if (item.showArrow) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = ProfileTextSecondary
            )
        }
    }
}

@Composable
private fun LogoutCard(onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = ProfileCardBackground),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "会话管理",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF101828)
            )
            Text(
                text = "退出登录将清除当前会话，需要重新输入账号信息。",
                fontSize = 13.sp,
                lineHeight = 18.sp,
                color = ProfileTextSecondary
            )
            Button(
                onClick = onClick,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("退出登录")
            }
        }
    }
}

@Composable
private fun ErrorView(message: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = Color.White
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 28.dp, vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "无法加载 Profile",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF101828)
                )
                Text(
                    text = message,
                    fontSize = 14.sp,
                    color = ProfileTextSecondary,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ProfilePagePreview() {
    MaterialTheme {
        ProfileContent(
            user = UserInfo(
                email = "hello@example.com",
                nickname = "Alice",
                avatarUrl = null,
                memberLevel = "已订阅",
                memberExpiry = null
            ),
            onNavigateToOrders = {},
            onNavigateToWallet = {},
            onNavigateToInvite = {},
            onNavigateToCommission = {},
            onNavigateToLegal = {},
            onNavigateToSupport = {},
            onNavigateToAbout = {},
            onLogoutClick = {}
        )
    }
}
