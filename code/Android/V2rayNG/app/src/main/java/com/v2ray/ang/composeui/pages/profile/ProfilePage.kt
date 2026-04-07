package com.v2ray.ang.composeui.pages.profile

import android.app.Application
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val ProfileBg = Color(0xFF0A101A)
private val ProfileSurface = Color(0xFF121A28)
private val ProfileSurfaceSoft = Color(0xFF1A2638)
private val ProfilePrimary = Color(0xFF00E5A8)
private val ProfilePrimarySoft = Color(0x3313F1B2)
private val ProfileText = Color(0xFFEAF0F7)
private val ProfileMuted = Color(0xFF8D9AB0)
private val ProfileDanger = Color(0xFFF45B69)

/**
 * 用户信息
 */
data class UserInfo(
    val email: String,
    val nickname: String?,
    val avatarUrl: String?,
    val memberLevel: String,
    val memberExpiry: String?
)

/**
 * 设置项
 */
data class SettingItem(
    val icon: ImageVector,
    val title: String,
    val subtitle: String? = null,
    val badge: String? = null,
    val showArrow: Boolean = true
)

/**
 * 我的页状态
 */
sealed class ProfileState {
    object Idle : ProfileState()
    object Loading : ProfileState()
    data class Loaded(val user: UserInfo) : ProfileState()
    data class Error(val message: String) : ProfileState()
}

/**
 * 我的页ViewModel
 */
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
            val activeOrder = orders.firstOrNull { it.expiredAt != null && it.expiredAt > System.currentTimeMillis() }
            _state.value = ProfileState.Loaded(
                user = UserInfo(
                    email = user.email ?: user.username,
                    nickname = user.username.substringBefore("@"),
                    avatarUrl = null,
                    memberLevel = if (activeOrder != null) "已订阅" else "基础用户",
                    memberExpiry = formatMemberExpiry(activeOrder?.expiredAt)
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

/**
 * 我的页
 * 显示用户信息和设置入口
 */
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
        containerColor = ProfileBg,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Discover / Profile",
                        color = ProfileText,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                actions = {
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings",
                            tint = ProfileText
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = ProfileBg,
                    titleContentColor = ProfileText,
                    actionIconContentColor = ProfileText
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(ProfileBg)
                .padding(paddingValues)
        ) {
            when (val current = state) {
                is ProfileState.Loaded -> {
                    ProfileContent(
                        user = current.user,
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

                is ProfileState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = ProfilePrimary)
                    }
                }

                is ProfileState.Error -> {
                    ProfileErrorView(message = current.message)
                }

                else -> Unit
            }
        }
    }

    if (showLogoutDialog.value) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog.value = false },
            containerColor = ProfileSurface,
            titleContentColor = ProfileText,
            textContentColor = ProfileMuted,
            title = { Text("确认登出") },
            text = { Text("确定要退出登录吗？") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showLogoutDialog.value = false
                        viewModel.logout()
                        onLogout()
                    }
                ) {
                    Text("确认", color = ProfileDanger)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog.value = false }) {
                    Text("取消", color = ProfileMuted)
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
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        UserInfoCard(user = user)

        Spacer(modifier = Modifier.height(12.dp))

        QuickAccessGrid(
            onNavigateToOrders = onNavigateToOrders,
            onNavigateToWallet = onNavigateToWallet,
            onNavigateToInvite = onNavigateToInvite,
            onNavigateToCommission = onNavigateToCommission
        )

        Spacer(modifier = Modifier.height(12.dp))

        SettingsList(
            onNavigateToLegal = onNavigateToLegal,
            onNavigateToSupport = onNavigateToSupport,
            onNavigateToAbout = onNavigateToAbout
        )

        Spacer(modifier = Modifier.height(12.dp))

        LogoutButton(onClick = onLogoutClick)

        Spacer(modifier = Modifier.height(18.dp))

        Text(
            text = "CryptoVPN v1.0.0",
            fontSize = 11.sp,
            color = ProfileMuted,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(10.dp))
    }
}

@Composable
private fun UserInfoCard(user: UserInfo) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = ProfileSurface),
        border = BorderStroke(1.dp, ProfilePrimarySoft)
    ) {
        Row(
            modifier = Modifier
                .background(
                    Brush.linearGradient(
                        colors = listOf(Color(0x3322F5C6), Color.Transparent, Color(0x202D3E58))
                    )
                )
                .padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = CircleShape,
                color = ProfileSurfaceSoft,
                modifier = Modifier.size(62.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = user.nickname?.take(1)?.uppercase() ?: user.email.take(1).uppercase(),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = ProfilePrimary
                    )
                }
            }

            Spacer(modifier = Modifier.size(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = user.nickname ?: user.email.substringBefore("@"),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = ProfileText
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = user.email,
                    fontSize = 12.sp,
                    color = ProfileMuted
                )
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    color = ProfilePrimarySoft,
                    shape = RoundedCornerShape(999.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            modifier = Modifier.size(12.dp),
                            tint = ProfilePrimary
                        )
                        Spacer(modifier = Modifier.size(4.dp))
                        Text(
                            text = user.memberLevel,
                            fontSize = 11.sp,
                            color = ProfilePrimary,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
                user.memberExpiry?.let { expiry ->
                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                        color = ProfileSurfaceSoft,
                        shape = RoundedCornerShape(999.dp)
                    ) {
                        Text(
                            text = "会员有效期至 $expiry",
                            fontSize = 11.sp,
                            color = ProfileText,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                        )
                    }
                }
            }
        }
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
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = ProfileSurface)
    ) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(
                text = "快捷入口",
                color = ProfileText,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 4.dp)
            )
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                QuickAccessItem(
                    modifier = Modifier.weight(1f),
                    icon = Icons.AutoMirrored.Filled.ReceiptLong,
                    label = "我的订单",
                    onClick = onNavigateToOrders
                )
                QuickAccessItem(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Default.AccountBalanceWallet,
                    label = "我的钱包",
                    onClick = onNavigateToWallet
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                QuickAccessItem(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Default.People,
                    label = "邀请好友",
                    onClick = onNavigateToInvite
                )
                QuickAccessItem(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Default.AttachMoney,
                    label = "佣金收益",
                    onClick = onNavigateToCommission
                )
            }
        }
    }
}

@Composable
private fun QuickAccessItem(
    modifier: Modifier,
    icon: ImageVector,
    label: String,
    onClick: () -> Unit
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(ProfileSurfaceSoft)
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = ProfilePrimary,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.size(8.dp))
        Text(text = label, color = ProfileText, fontSize = 12.sp, fontWeight = FontWeight.Medium)
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
            subtitle = "用户协议、隐私政策"
        ),
        SettingItem(
            icon = Icons.AutoMirrored.Filled.Help,
            title = "帮助与反馈",
            subtitle = "常见问题、联系客服"
        ),
        SettingItem(
            icon = Icons.Default.Info,
            title = "关于我们",
            subtitle = "版本信息、更新日志"
        )
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = ProfileSurface)
    ) {
        Column(modifier = Modifier.padding(vertical = 6.dp)) {
            settings.forEach { item ->
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
            }
        }
    }
}

@Composable
private fun SettingListItem(
    item: SettingItem,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(ProfileSurfaceSoft),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = item.icon,
                contentDescription = item.title,
                tint = ProfilePrimary,
                modifier = Modifier.size(18.dp)
            )
        }

        Spacer(modifier = Modifier.size(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.title,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = ProfileText
            )
            item.subtitle?.let {
                Text(
                    text = it,
                    fontSize = 11.sp,
                    color = ProfileMuted,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        }

        if (item.showArrow) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = ProfileMuted
            )
        }
    }
}

@Composable
private fun LogoutButton(onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = ProfileSurface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.Logout,
                contentDescription = "Logout",
                tint = ProfileDanger,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.size(8.dp))
            Text(
                text = "退出登录",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = ProfileDanger
            )
        }
    }
}

private fun formatMemberExpiry(expiryMillis: Long?): String? {
    if (expiryMillis == null || expiryMillis <= 0L) return null
    return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(expiryMillis))
}

@Composable
private fun ProfileErrorView(message: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 28.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Error,
            contentDescription = null,
            modifier = Modifier.size(54.dp),
            tint = ProfileDanger
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(text = "加载失败", color = ProfileDanger, fontWeight = FontWeight.Bold, fontSize = 18.sp)
        Spacer(modifier = Modifier.height(6.dp))
        Text(text = message, color = ProfileMuted, fontSize = 12.sp)
    }
}

@Preview(showBackground = true)
@Composable
fun ProfilePagePreview() {
    MaterialTheme {
        ProfilePage()
    }
}
