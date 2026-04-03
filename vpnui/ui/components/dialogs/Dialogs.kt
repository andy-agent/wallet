package com.cryptovpn.ui.components.dialogs

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.cryptovpn.ui.components.buttons.ButtonSize
import com.cryptovpn.ui.components.buttons.DangerButton
import com.cryptovpn.ui.components.buttons.DangerVariant
import com.cryptovpn.ui.components.buttons.PrimaryButton
import com.cryptovpn.ui.components.buttons.SecondaryButton
import com.cryptovpn.ui.theme.*

/**
 * 警告弹窗组件
 * 
 * @param title 标题
 * @param message 消息内容
 * @param confirmText 确认按钮文字
 * @param dismissText 取消按钮文字
 * @param onConfirm 确认回调
 * @param onDismiss 取消回调
 * @param icon 图标（可选）
 * @param isDanger 是否为危险操作
 */
@Composable
fun AlertDialog(
    title: String,
    message: String,
    confirmText: String,
    dismissText: String? = null,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    icon: ImageVector? = null,
    iconColor: Color = Primary,
    isDanger: Boolean = false
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        )
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp),
            shape = AppShape.Dialog,
            color = BackgroundSecondary
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Icon
                if (icon != null) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .background(
                                color = iconColor.copy(alpha = 0.15f),
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            modifier = Modifier.size(28.dp),
                            tint = iconColor
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
                
                // Title
                Text(
                    text = title,
                    style = AppTypography.H4,
                    color = TextPrimary,
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                // Message
                Text(
                    text = message,
                    style = AppTypography.Body,
                    color = TextSecondary,
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Buttons
                if (isDanger) {
                    DangerButton(
                        text = confirmText,
                        onClick = onConfirm,
                        variant = DangerVariant.FILLED
                    )
                    if (dismissText != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        SecondaryButton(
                            text = dismissText,
                            onClick = onDismiss
                        )
                    }
                } else {
                    PrimaryButton(
                        text = confirmText,
                        onClick = onConfirm
                    )
                    if (dismissText != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        SecondaryButton(
                            text = dismissText,
                            onClick = onDismiss
                        )
                    }
                }
            }
        }
    }
}

/**
 * 底部弹窗组件
 * 
 * @param onDismiss 关闭回调
 * @param title 标题
 * @param showCloseButton 是否显示关闭按钮
 * @param content 内容
 */
@Composable
fun BottomSheet(
    onDismiss: () -> Unit,
    title: String? = null,
    showCloseButton: Boolean = true,
    content: @Composable ColumnScope.() -> Unit
) {
    androidx.compose.material3.BottomSheet(
        onDismissRequest = onDismiss,
        containerColor = BackgroundSecondary,
        shape = AppShape.BottomSheet
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            // Drag handle
            Box(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .width(40.dp)
                    .height(4.dp)
                    .background(
                        color = BackgroundTertiary,
                        shape = RoundedCornerShape(2.dp)
                    )
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Header
            if (title != null || showCloseButton) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (title != null) {
                        Text(
                            text = title,
                            style = AppTypography.H4,
                            color = TextPrimary
                        )
                    } else {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                    
                    if (showCloseButton) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "关闭",
                            modifier = Modifier
                                .size(24.dp)
                                .clickable(onClick = onDismiss),
                            tint = TextTertiary
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
            }
            
            // Content
            content()
        }
    }
}

/**
 * 加载弹窗组件
 * 
 * @param message 加载提示文字
 * @param onDismiss 关闭回调（可选）
 */
@Composable
fun LoadingDialog(
    message: String = "加载中...",
    onDismiss: (() -> Unit)? = null
) {
    Dialog(
        onDismissRequest = { onDismiss?.invoke() },
        properties = DialogProperties(
            dismissOnBackPress = onDismiss != null,
            dismissOnClickOutside = onDismiss != null
        )
    ) {
        Surface(
            modifier = Modifier.wrapContentSize(),
            shape = AppShape.Card,
            color = BackgroundSecondary
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Animated loading indicator
                val infiniteTransition = rememberInfiniteTransition(label = "loading")
                val rotation by infiniteTransition.animateFloat(
                    initialValue = 0f,
                    targetValue = 360f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(1000, easing = LinearEasing),
                        repeatMode = RepeatMode.Restart
                    ),
                    label = "rotation"
                )
                
                CircularProgressIndicator(
                    modifier = Modifier.size(48.dp),
                    color = Primary,
                    strokeWidth = 3.dp
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = message,
                    style = AppTypography.Body,
                    color = TextSecondary
                )
            }
        }
    }
}

/**
 * 成功弹窗组件
 * 
 * @param title 标题
 * @param message 消息内容
 * @param buttonText 按钮文字
 * @param onDismiss 关闭回调
 */
@Composable
fun SuccessDialog(
    title: String = "操作成功",
    message: String,
    buttonText: String = "确定",
    onDismiss: () -> Unit
) {
    AlertDialog(
        title = title,
        message = message,
        confirmText = buttonText,
        onConfirm = onDismiss,
        onDismiss = onDismiss,
        icon = androidx.compose.material.icons.Icons.Default.CheckCircle,
        iconColor = Success
    )
}

/**
 * 错误弹窗组件
 * 
 * @param title 标题
 * @param message 错误消息
 * @param buttonText 按钮文字
 * @param onDismiss 关闭回调
 */
@Composable
fun ErrorDialog(
    title: String = "操作失败",
    message: String,
    buttonText: String = "确定",
    onDismiss: () -> Unit
) {
    AlertDialog(
        title = title,
        message = message,
        confirmText = buttonText,
        onConfirm = onDismiss,
        onDismiss = onDismiss,
        icon = androidx.compose.material.icons.Icons.Default.Error,
        iconColor = Error,
        isDanger = true
    )
}

@Preview(name = "Dialogs")
@Composable
fun DialogsPreview() {
    CryptoVPNTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundPrimary)
        ) {
            // Note: Dialogs are not shown in preview
            // They need to be triggered in actual usage
            Text(
                text = "Dialogs Preview\n(Dialogs shown in separate windows)",
                style = AppTypography.H4,
                color = TextPrimary,
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}
