package com.v2ray.ang.composeui.pages.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

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
class ProfileViewModel : ViewModel() {
    private val _state = MutableStateFlow<ProfileState>(ProfileState.Idle)
    val state: StateFlow<ProfileState> = _state

    init {
        loadUserData()
    }

    private fun loadUserData() {
        val user = UserInfo(
            email = "user@example.com",
            nickname = "CryptoUser",
            avatarUrl = null,
            memberLevel = "VIP会员",
            memberExpiry = "2024-12-31"
        )
        _state.value = ProfileState.Loaded(user)
    }

    fun logout() {
        // 处理登出逻辑
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
        topBar = {
            TopAppBar(
                title = { Text("我的") },
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
                .padding(paddingValues)
        ) {
            when (state) {
                is ProfileState.Loaded -> {
                    val user = (state as ProfileState.Loaded).user
                    ProfileContent(
                        user = user,
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
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                is ProfileState.Error -> {
                    ErrorView(message = (state as ProfileState.Error).message)
                }
                else -> {}
            }
        }
    }

    // 登出确认对话框
    if (showLogoutDialog.value) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog.value = false },
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
    ) {
        // 用户信息卡片
        UserInfoCard(user = user)

        Spacer(modifier = Modifier.height(16.dp))

        // 快捷入口
        QuickAccessRow(
            onNavigateToOrders = onNavigateToOrders,
            onNavigateToWallet = onNavigateToWallet,
            onNavigateToInvite = onNavigateToInvite,
            onNavigateToCommission = onNavigateToCommission
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 设置列表
        SettingsList(
            onNavigateToLegal = onNavigateToLegal,
            onNavigateToSupport = onNavigateToSupport,
            onNavigateToAbout = onNavigateToAbout
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 登出按钮
        LogoutButton(onClick = onLogoutClick)

        Spacer(modifier = Modifier.height(24.dp))

        // 版本号
        Text(
            text = "CryptoVPN v1.0.0",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
            modifier = Modifier.fillMaxWidth(),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun UserInfoCard(user: UserInfo) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primary
        )
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 头像
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.2f),
                modifier = Modifier.size(64.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = user.nickname?.take(1) ?: user.email.take(1).uppercase(),
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // 用户信息
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = user.nickname ?: user.email.substringBefore("@"),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = user.email,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.2f),
                    shape = MaterialTheme.shapes.small
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = user.memberLevel,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun QuickAccessRow(
    onNavigateToOrders: () -> Unit,
    onNavigateToWallet: () -> Unit,
    onNavigateToInvite: () -> Unit,
    onNavigateToCommission: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            QuickAccessItem(
                icon = Icons.Default.ReceiptLong,
                label = "我的订单",
                onClick = onNavigateToOrders
            )
            QuickAccessItem(
                icon = Icons.Default.AccountBalanceWallet,
                label = "我的钱包",
                onClick = onNavigateToWallet
            )
            QuickAccessItem(
                icon = Icons.Default.People,
                label = "邀请好友",
                onClick = onNavigateToInvite
            )
            QuickAccessItem(
                icon = Icons.Default.AttachMoney,
                label = "佣金收益",
                onClick = onNavigateToCommission
            )
        }
    }
}

@Composable
private fun QuickAccessItem(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
            modifier = Modifier.size(48.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = label,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurface
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
            subtitle = "用户协议、隐私政策"
        ),
        SettingItem(
            icon = Icons.Default.Help,
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
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column {
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
                if (index < settings.size - 1) {
                    Divider(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                    )
                }
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
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = item.icon,
            contentDescription = item.title,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
            item.subtitle?.let {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = it,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        
        if (item.showArrow) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun LogoutButton(onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clickable(onClick = onClick),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.Logout,
                contentDescription = "Logout",
                tint = Color(0xFFEF4444),
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "退出登录",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFFEF4444)
            )
        }
    }
}

@Composable
private fun ErrorView(message: String) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Error,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.error
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "加载失败",
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.error
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = message,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ProfilePagePreview() {
    MaterialTheme {
        ProfilePage()
    }
}
