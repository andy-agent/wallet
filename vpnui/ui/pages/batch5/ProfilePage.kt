package com.cryptovpn.ui.pages.profile

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.cryptovpn.ui.theme.BackgroundDark
import com.cryptovpn.ui.theme.BackgroundMedium
import com.cryptovpn.ui.theme.BackgroundLight
import com.cryptovpn.ui.theme.PrimaryBlue
import com.cryptovpn.ui.theme.SuccessGreen
import com.cryptovpn.ui.theme.ErrorRed
import com.cryptovpn.ui.theme.TextPrimary
import com.cryptovpn.ui.theme.TextSecondary
import com.cryptovpn.ui.theme.TextTertiary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * 我的页面
 * 用户信息、订阅状态、设置入口
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfilePage(
    viewModel: ProfileViewModel = hiltViewModel(),
    onOrdersClick: () -> Unit = {},
    onServiceAgreementClick: () -> Unit = {},
    onPrivacyPolicyClick: () -> Unit = {},
    onAboutClick: () -> Unit = {},
    onEditProfileClick: () -> Unit = {},
    onLogoutClick: () -> Unit = {}
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "我的",
                        color = TextPrimary,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = BackgroundDark
                )
            )
        },
        containerColor = BackgroundDark
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (state) {
                is ProfileState.Loading -> {
                    LoadingContent()
                }
                is ProfileState.Loaded -> {
                    ProfileContent(
                        state = state as ProfileState.Loaded,
                        onOrdersClick = onOrdersClick,
                        onServiceAgreementClick = onServiceAgreementClick,
                        onPrivacyPolicyClick = onPrivacyPolicyClick,
                        onAboutClick = onAboutClick,
                        onEditProfileClick = onEditProfileClick,
                        onLogoutClick = { viewModel.onLogoutClick() }
                    )
                }
                is ProfileState.SessionEvicted -> {
                    // 由全局弹窗处理
                }
                is ProfileState.Error -> {
                    ErrorContent(
                        message = (state as ProfileState.Error).message,
                        onRetry = { viewModel.loadProfile() }
                    )
                }
            }
        }
    }
}

@Composable
private fun LoadingContent() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(color = PrimaryBlue)
    }
}

@Composable
private fun ErrorContent(
    message: String,
    onRetry: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Info,
            contentDescription = null,
            tint = ErrorRed,
            modifier = Modifier.size(64.dp)
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "加载失败",
            color = TextPrimary,
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = message,
            color = TextSecondary,
            fontSize = 14.sp
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Button(
            onClick = onRetry,
            colors = ButtonDefaults.buttonColors(
                containerColor = PrimaryBlue
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("重新加载")
        }
    }
}

@Composable
private fun ProfileContent(
    state: ProfileState.Loaded,
    onOrdersClick: () -> Unit,
    onServiceAgreementClick: () -> Unit,
    onPrivacyPolicyClick: () -> Unit,
    onAboutClick: () -> Unit,
    onEditProfileClick: () -> Unit,
    onLogoutClick: () -> Unit
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        // 用户信息卡片
        UserInfoCard(
            userInfo = state.userInfo,
            onEditClick = onEditProfileClick
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 订阅摘要卡片
        if (state.subscription != null) {
            SubscriptionCard(subscription = state.subscription)
            Spacer(modifier = Modifier.height(16.dp))
        }

        // 菜单列表
        ProfileMenuList(
            onOrdersClick = onOrdersClick,
            onServiceAgreementClick = onServiceAgreementClick,
            onPrivacyPolicyClick = onPrivacyPolicyClick,
            onAboutClick = onAboutClick
        )

        Spacer(modifier = Modifier.height(24.dp))

        // 退出登录按钮
        LogoutButton(onClick = onLogoutClick)

        Spacer(modifier = Modifier.height(24.dp))

        // 版本号
        Text(
            text = "版本 ${state.appVersion}",
            color = TextTertiary,
            fontSize = 12.sp,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }
}

@Composable
private fun UserInfoCard(
    userInfo: UserInfo,
    onEditClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = BackgroundMedium
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 头像
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(PrimaryBlue, PrimaryBlue.copy(alpha = 0.6f))
                        ),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = userInfo.avatarText,
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = userInfo.email,
                    color = TextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = "ID: ${userInfo.userId}",
                    color = TextSecondary,
                    fontSize = 13.sp
                )
            }
            
            // 编辑按钮
            IconButton(
                onClick = onEditClick,
                modifier = Modifier
                    .size(36.dp)
                    .background(
                        color = BackgroundLight,
                        shape = CircleShape
                    )
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "编辑",
                    tint = TextSecondary,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
private fun SubscriptionCard(subscription: SubscriptionInfo) {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            SuccessGreen.copy(alpha = 0.3f),
                            SuccessGreen.copy(alpha = 0.1f)
                        )
                    ),
                    shape = RoundedCornerShape(16.dp)
                )
                .border(
                    width = 1.dp,
                    color = SuccessGreen.copy(alpha = 0.3f),
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(16.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "当前订阅",
                        color = TextSecondary,
                        fontSize = 14.sp
                    )
                    
                    Box(
                        modifier = Modifier
                            .background(
                                color = SuccessGreen.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(4.dp)
                            )
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "生效中",
                            color = SuccessGreen,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = subscription.planName,
                    color = TextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = "到期时间: ${dateFormat.format(subscription.expireDate)}",
                    color = TextSecondary,
                    fontSize = 13.sp
                )
            }
        }
    }
}

@Composable
private fun ProfileMenuList(
    onOrdersClick: () -> Unit,
    onServiceAgreementClick: () -> Unit,
    onPrivacyPolicyClick: () -> Unit,
    onAboutClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = BackgroundMedium
        )
    ) {
        Column {
            // 我的订单
            MenuItem(
                icon = Icons.Default.Receipt,
                iconColor = PrimaryBlue,
                title = "我的订单",
                onClick = onOrdersClick,
                showDivider = true
            )
            
            // 服务协议
            MenuItem(
                icon = Icons.Default.Description,
                iconColor = SuccessGreen,
                title = "服务协议",
                onClick = onServiceAgreementClick,
                showDivider = true
            )
            
            // 隐私政策
            MenuItem(
                icon = Icons.Default.Security,
                iconColor = SuccessGreen,
                title = "隐私政策",
                onClick = onPrivacyPolicyClick,
                showDivider = true
            )
            
            // 关于
            MenuItem(
                icon = Icons.Default.Info,
                iconColor = TextSecondary,
                title = "关于",
                onClick = onAboutClick,
                showDivider = false
            )
        }
    }
}

@Composable
private fun MenuItem(
    icon: ImageVector,
    iconColor: Color,
    title: String,
    onClick: () -> Unit,
    showDivider: Boolean
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 图标
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(
                        color = iconColor.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(8.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(20.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Text(
                text = title,
                color = TextPrimary,
                fontSize = 15.sp,
                modifier = Modifier.weight(1f)
            )
            
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                contentDescription = null,
                tint = TextTertiary,
                modifier = Modifier.size(16.dp)
            )
        }
        
        if (showDivider) {
            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 16.dp),
                color = BackgroundLight
            )
        }
    }
}

@Composable
private fun LogoutButton(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = ErrorRed.copy(alpha = 0.15f)
        ),
        border = androidx.compose.foundation.BorderStroke(
            width = 1.dp,
            color = ErrorRed.copy(alpha = 0.3f)
        )
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ExitToApp,
            contentDescription = null,
            tint = ErrorRed,
            modifier = Modifier.size(20.dp)
        )
        
        Spacer(modifier = Modifier.width(8.dp))
        
        Text(
            text = "退出登录",
            color = ErrorRed,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Preview
@Composable
fun ProfilePagePreview() {
    MaterialTheme {
        ProfileContent(
            state = ProfileState.Loaded(
                userInfo = UserInfo(
                    email = "user@example.com",
                    userId = "USER123456",
                    avatarText = "U"
                ),
                subscription = SubscriptionInfo(
                    planName = "年度套餐",
                    expireDate = Date(System.currentTimeMillis() + 365 * 24 * 60 * 60 * 1000)
                ),
                appVersion = "1.0.0"
            ),
            onOrdersClick = {},
            onServiceAgreementClick = {},
            onPrivacyPolicyClick = {},
            onAboutClick = {},
            onEditProfileClick = {},
            onLogoutClick = {}
        )
    }
}

@Preview
@Composable
fun ProfilePageLoadingPreview() {
    MaterialTheme {
        LoadingContent()
    }
}
