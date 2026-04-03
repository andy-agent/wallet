package com.cryptovpn.ui.pages.common

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.cryptovpn.ui.theme.BackgroundDark
import com.cryptovpn.ui.theme.BackgroundMedium
import com.cryptovpn.ui.theme.PrimaryBlue
import com.cryptovpn.ui.theme.WarningYellow
import com.cryptovpn.ui.theme.TextPrimary
import com.cryptovpn.ui.theme.TextSecondary

/**
 * 会话失效原因类型
 */
enum class SessionEvictionReason(
    val title: String,
    val message: String
) {
    LOGIN_ON_OTHER_DEVICE(
        title = "账号在其他设备登录",
        message = "您的账号已在其他设备登录，当前会话已失效。如非本人操作，请立即修改密码。"
    ),
    SESSION_EXPIRED(
        title = "登录已过期",
        message = "您的登录状态已过期，请重新登录以继续使用。"
    ),
    ACCOUNT_DISABLED(
        title = "账号已被禁用",
        message = "您的账号已被禁用，如有疑问请联系客服。"
    ),
    SECURITY_VIOLATION(
        title = "安全异常",
        message = "检测到异常登录行为，为保障账号安全，会话已被终止。"
    ),
    SERVER_MAINTENANCE(
        title = "系统维护",
        message = "系统正在进行维护，请稍后重新登录。"
    )
}

/**
 * 全局会话失效弹窗
 * 当账号在其他设备登录或会话过期时显示
 * 阻断所有交互，强制用户跳转登录页
 */
@Composable
fun SessionEvictedDialog(
    viewModel: SessionEvictedViewModel = hiltViewModel(),
    onNavigateToLogin: () -> Unit = {}
) {
    val state by viewModel.state.collectAsState()
    
    SessionEvictedDialogContent(
        isVisible = state.isVisible,
        reason = state.reason,
        onConfirm = {
            viewModel.dismiss()
            onNavigateToLogin()
        }
    )
}

/**
 * 会话失效弹窗内容
 * 可独立使用，不依赖ViewModel
 */
@Composable
fun SessionEvictedDialogContent(
    isVisible: Boolean,
    reason: SessionEvictionReason = SessionEvictionReason.LOGIN_ON_OTHER_DEVICE,
    onConfirm: () -> Unit = {}
) {
    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn(animationSpec = tween(200)) + 
                scaleIn(
                    initialScale = 0.8f,
                    animationSpec = tween(300)
                ),
        exit = fadeOut(animationSpec = tween(200)) +
               scaleOut(
                   targetScale = 0.8f,
                   animationSpec = tween(200)
               )
    ) {
        // 遮罩层
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.7f))
                .clickable(enabled = false) { 
                    // 阻断所有点击事件
                },
            contentAlignment = Alignment.Center
        ) {
            // 弹窗卡片
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .padding(16.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = BackgroundMedium
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 8.dp
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // 警告图标
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .background(
                                color = WarningYellow.copy(alpha = 0.15f),
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = null,
                            tint = WarningYellow,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(20.dp))
                    
                    // 标题
                    Text(
                        text = reason.title,
                        color = TextPrimary,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    // 说明文字
                    Text(
                        text = reason.message,
                        color = TextSecondary,
                        fontSize = 15.sp,
                        lineHeight = 22.sp,
                        textAlign = TextAlign.Center
                    )
                    
                    Spacer(modifier = Modifier.height(28.dp))
                    
                    // 去登录按钮
                    Button(
                        onClick = onConfirm,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PrimaryBlue
                        )
                    ) {
                        Text(
                            text = "去登录",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}

/**
 * 会话失效弹窗状态
 */
data class SessionEvictedState(
    val isVisible: Boolean = false,
    val reason: SessionEvictionReason = SessionEvictionReason.LOGIN_ON_OTHER_DEVICE
)

/**
 * 会话失效弹窗ViewModel
 * 用于管理弹窗的显示状态和原因
 */
@Composable
fun rememberSessionEvictedState(): SessionEvictedState {
    return SessionEvictedState()
}

// 辅助函数，用于阻断点击事件
private inline fun Modifier.clickable(
    enabled: Boolean = true,
    crossinline onClick: () -> Unit
): Modifier = this.then(
    androidx.compose.foundation.clickable(
        enabled = enabled,
        onClick = { onClick() }
    )
)

@Preview
@Composable
fun SessionEvictedDialogPreview() {
    MaterialTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundDark)
        ) {
            // 背景内容（模拟被阻断的页面）
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Text(
                    text = "我的页面",
                    color = TextPrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                repeat(5) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp)
                            .padding(vertical = 4.dp)
                            .background(
                                color = BackgroundMedium,
                                shape = RoundedCornerShape(8.dp)
                            )
                    )
                }
            }
            
            // 弹窗
            SessionEvictedDialogContent(
                isVisible = true,
                reason = SessionEvictionReason.LOGIN_ON_OTHER_DEVICE,
                onConfirm = {}
            )
        }
    }
}

@Preview
@Composable
fun SessionEvictedDialogSessionExpiredPreview() {
    MaterialTheme {
        SessionEvictedDialogContent(
            isVisible = true,
            reason = SessionEvictionReason.SESSION_EXPIRED,
            onConfirm = {}
        )
    }
}

@Preview
@Composable
fun SessionEvictedDialogAccountDisabledPreview() {
    MaterialTheme {
        SessionEvictedDialogContent(
            isVisible = true,
            reason = SessionEvictionReason.ACCOUNT_DISABLED,
            onConfirm = {}
        )
    }
}
