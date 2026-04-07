package com.v2ray.ang.composeui.pages.legal

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * 会话失效原因
 */
enum class SessionEvictedReason {
    TOKEN_EXPIRED,      // Token过期
    LOGIN_ELSEWHERE,    // 其他设备登录
    ACCOUNT_DISABLED,   // 账户被禁用
    PASSWORD_CHANGED,   // 密码已修改
    SECURITY_CONCERN    // 安全原因
}

/**
 * 会话失效弹窗状态
 */
sealed class SessionEvictedState {
    object Hidden : SessionEvictedState()
    data class Visible(
        val reason: SessionEvictedReason,
        val message: String
    ) : SessionEvictedState()
}

/**
 * 会话失效弹窗ViewModel
 */
class SessionEvictedViewModel : ViewModel() {
    private val _state = MutableStateFlow<SessionEvictedState>(SessionEvictedState.Hidden)
    val state: StateFlow<SessionEvictedState> = _state

    fun showDialog(reason: SessionEvictedReason) {
        val message = when (reason) {
            SessionEvictedReason.TOKEN_EXPIRED -> "您的登录已过期，请重新登录"
            SessionEvictedReason.LOGIN_ELSEWHERE -> "您的账户已在其他设备登录"
            SessionEvictedReason.ACCOUNT_DISABLED -> "您的账户已被禁用，请联系客服"
            SessionEvictedReason.PASSWORD_CHANGED -> "密码已修改，请使用新密码登录"
            SessionEvictedReason.SECURITY_CONCERN -> "检测到异常登录，请重新验证身份"
        }
        _state.value = SessionEvictedState.Visible(reason, message)
    }

    fun dismissDialog() {
        _state.value = SessionEvictedState.Hidden
    }
}

/**
 * 全局会话失效弹窗
 * 当用户会话失效时显示，强制用户重新登录
 */
@Composable
fun SessionEvictedDialog(
    viewModel: SessionEvictedViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    reason: SessionEvictedReason = SessionEvictedReason.TOKEN_EXPIRED,
    onConfirm: () -> Unit = {},
    onDismiss: () -> Unit = {}
) {
    val state by viewModel.state.collectAsState()

    // 自动显示对话框
    LaunchedEffect(Unit) {
        viewModel.showDialog(reason)
    }

    when (val currentState = state) {
        is SessionEvictedState.Visible -> {
            AlertDialog(
                onDismissRequest = { 
                    // 会话失效弹窗不允许通过点击外部或返回键关闭
                    // 用户必须点击确认按钮
                },
                icon = {
                    SessionEvictedIcon(reason = currentState.reason)
                },
                title = {
                    Text(
                        text = getDialogTitle(currentState.reason),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                },
                text = {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = currentState.message,
                            fontSize = 14.sp,
                            color = LegalTextSecondary,
                            textAlign = TextAlign.Center,
                            lineHeight = 21.sp
                        )

                        AdditionalInfo(reason = currentState.reason)
                    }
                },
                confirmButton = {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Button(
                            onClick = {
                                viewModel.dismissDialog()
                                onConfirm()
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Text(
                                text = getConfirmButtonText(currentState.reason),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        TextButton(
                            onClick = onDismiss,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("稍后处理", color = LegalTextSecondary)
                        }
                    }
                },
                shape = RoundedCornerShape(28.dp),
                containerColor = LegalCardBackground,
                iconContentColor = MaterialTheme.colorScheme.primary,
                titleContentColor = Color(0xFF101828),
                textContentColor = LegalTextSecondary,
                properties = androidx.compose.ui.window.DialogProperties(
                    dismissOnBackPress = false,
                    dismissOnClickOutside = false
                )
            )
        }
        else -> {}
    }
}

@Composable
private fun SessionEvictedIcon(reason: SessionEvictedReason) {
    val (icon, color) = when (reason) {
        SessionEvictedReason.TOKEN_EXPIRED -> Pair(
            Icons.Default.Schedule,
            MaterialTheme.colorScheme.primary
        )
        SessionEvictedReason.LOGIN_ELSEWHERE -> Pair(
            Icons.Default.Devices,
            MaterialTheme.colorScheme.primary
        )
        SessionEvictedReason.ACCOUNT_DISABLED -> Pair(
            Icons.Default.Block,
            Color(0xFFEF4444)
        )
        SessionEvictedReason.PASSWORD_CHANGED -> Pair(
            Icons.Default.Lock,
            MaterialTheme.colorScheme.primary
        )
        SessionEvictedReason.SECURITY_CONCERN -> Pair(
            Icons.Default.Security,
            Color(0xFFF59E0B)
        )
    }

    Surface(
        shape = RoundedCornerShape(20.dp),
        color = color.copy(alpha = 0.1f),
        modifier = Modifier.size(72.dp)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(34.dp)
            )
        }
    }
}

@Composable
private fun AdditionalInfo(reason: SessionEvictedReason) {
    when (reason) {
        SessionEvictedReason.LOGIN_ELSEWHERE -> {
            Surface(
                color = Color(0xFFF2F4F7),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = "如果这不是您的操作，建议立即修改密码",
                    fontSize = 12.sp,
                    color = LegalTextSecondary,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    textAlign = TextAlign.Center
                )
            }
        }
        SessionEvictedReason.ACCOUNT_DISABLED -> {
            Surface(
                color = Color(0xFFEF4444).copy(alpha = 0.1f),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = "客服邮箱: support@cryptovpn.app",
                    fontSize = 12.sp,
                    color = Color(0xFFEF4444),
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    textAlign = TextAlign.Center
                )
            }
        }
        SessionEvictedReason.SECURITY_CONCERN -> {
            Surface(
                color = Color(0xFFF59E0B).copy(alpha = 0.1f),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = "为了您的账户安全，请重新登录",
                    fontSize = 12.sp,
                    color = Color(0xFFF59E0B),
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    textAlign = TextAlign.Center
                )
            }
        }
        else -> {}
    }
}

private fun getDialogTitle(reason: SessionEvictedReason): String {
    return when (reason) {
        SessionEvictedReason.TOKEN_EXPIRED -> "登录已过期"
        SessionEvictedReason.LOGIN_ELSEWHERE -> "其他设备登录"
        SessionEvictedReason.ACCOUNT_DISABLED -> "账户已禁用"
        SessionEvictedReason.PASSWORD_CHANGED -> "密码已修改"
        SessionEvictedReason.SECURITY_CONCERN -> "安全提醒"
    }
}

private fun getConfirmButtonText(reason: SessionEvictedReason): String {
    return when (reason) {
        SessionEvictedReason.TOKEN_EXPIRED -> "重新登录"
        SessionEvictedReason.LOGIN_ELSEWHERE -> "重新登录"
        SessionEvictedReason.ACCOUNT_DISABLED -> "联系客服"
        SessionEvictedReason.PASSWORD_CHANGED -> "去登录"
        SessionEvictedReason.SECURITY_CONCERN -> "验证身份"
    }
}

@Preview(showBackground = true)
@Composable
fun SessionEvictedDialogTokenExpiredPreview() {
    MaterialTheme {
        SessionEvictedDialog(reason = SessionEvictedReason.TOKEN_EXPIRED)
    }
}

@Preview(showBackground = true)
@Composable
fun SessionEvictedDialogLoginElsewherePreview() {
    MaterialTheme {
        SessionEvictedDialog(reason = SessionEvictedReason.LOGIN_ELSEWHERE)
    }
}

@Preview(showBackground = true)
@Composable
fun SessionEvictedDialogAccountDisabledPreview() {
    MaterialTheme {
        SessionEvictedDialog(reason = SessionEvictedReason.ACCOUNT_DISABLED)
    }
}
