package com.cryptovpn.ui.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.NewReleases
import androidx.compose.material.icons.filled.SystemUpdate
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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.cryptovpn.ui.theme.*

/**
 * 可选更新弹窗数据
 */
data class OptionalUpdateData(
    val version: String,
    val versionCode: Int,
    val updateContent: List<String>,
    val downloadUrl: String,
    val fileSize: String = "25.6 MB"
)

/**
 * 可选更新弹窗
 * 
 * @param updateData 更新数据
 * @param onDismiss 关闭弹窗
 * @param onLaterClick 稍后更新
 * @param onUpdateNowClick 立即更新
 */
@Composable
fun OptionalUpdateDialog(
    updateData: OptionalUpdateData,
    onDismiss: () -> Unit = {},
    onLaterClick: () -> Unit = {},
    onUpdateNowClick: () -> Unit = {}
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
            usePlatformDefaultWidth = false
        )
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .wrapContentHeight(),
            shape = RoundedCornerShape(20.dp),
            color = DarkNavyCard
        ) {
            Column(
                modifier = Modifier.padding(0.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 顶部装饰区域
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .background(
                            color = PrimaryBlue.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    // 关闭按钮
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.align(Alignment.TopEnd)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "关闭",
                            tint = TextSecondaryGray,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    
                    // 更新图标
                    Surface(
                        modifier = Modifier.size(64.dp),
                        shape = CircleShape,
                        color = PrimaryBlue.copy(alpha = 0.2f)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.SystemUpdate,
                                contentDescription = "更新",
                                tint = PrimaryBlue,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // 标题
                Text(
                    text = "发现新版本",
                    color = TextPrimaryWhite,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                // 版本信息
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = SuccessGreen.copy(alpha = 0.2f)
                    ) {
                        Text(
                            text = "v${updateData.version}",
                            color = SuccessGreen,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    Text(
                        text = updateData.fileSize,
                        color = TextSecondaryGray,
                        fontSize = 12.sp
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // 更新内容
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                ) {
                    Text(
                        text = "更新内容",
                        color = TextPrimaryWhite,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    updateData.updateContent.forEach { content ->
                        UpdateContentItem(text = content)
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // 按钮区域
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .padding(bottom = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // 稍后更新按钮
                    OutlinedButton(
                        onClick = onLaterClick,
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = TextSecondaryGray
                        ),
                        border = ButtonDefaults.outlinedButtonBorder.copy(
                            brush = androidx.compose.ui.graphics.SolidColor(DarkNavyBackground)
                        )
                    ) {
                        Text(
                            text = "稍后更新",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    // 立即更新按钮
                    Button(
                        onClick = onUpdateNowClick,
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PrimaryBlue
                        )
                    ) {
                        Text(
                            text = "立即更新",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

/**
 * 更新内容项
 */
@Composable
private fun UpdateContentItem(text: String) {
    Row(
        modifier = Modifier.padding(vertical = 3.dp),
        verticalAlignment = Alignment.Top
    ) {
        // 新版本标记
        Icon(
            imageVector = Icons.Default.NewReleases,
            contentDescription = null,
            tint = SuccessGreen,
            modifier = Modifier.size(14.dp)
        )
        
        Spacer(modifier = Modifier.width(8.dp))
        
        Text(
            text = text,
            color = TextSecondaryGray,
            fontSize = 13.sp,
            lineHeight = 18.sp
        )
    }
}

/**
 * 可选更新弹窗预览
 */
@Preview(name = "Optional Update Dialog", showBackground = true)
@Composable
fun OptionalUpdateDialogPreview() {
    CryptoVPNTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f)),
            contentAlignment = Alignment.Center
        ) {
            OptionalUpdateDialog(
                updateData = OptionalUpdateData(
                    version = "1.2.0",
                    versionCode = 12,
                    updateContent = listOf(
                        "优化VPN连接速度和稳定性",
                        "新增5个亚洲服务器节点",
                        "修复部分设备连接断开问题",
                        "改进用户界面交互体验"
                    ),
                    downloadUrl = "https://example.com/download",
                    fileSize = "25.6 MB"
                )
            )
        }
    }
}

/**
 * 可选更新弹窗 - 简洁版本（用于Compose函数调用）
 */
@Composable
fun ShowOptionalUpdateDialog(
    showDialog: Boolean,
    updateData: OptionalUpdateData? = null,
    onDismiss: () -> Unit = {},
    onLaterClick: () -> Unit = {},
    onUpdateNowClick: () -> Unit = {}
) {
    if (showDialog && updateData != null) {
        OptionalUpdateDialog(
            updateData = updateData,
            onDismiss = onDismiss,
            onLaterClick = onLaterClick,
            onUpdateNowClick = onUpdateNowClick
        )
    }
}
