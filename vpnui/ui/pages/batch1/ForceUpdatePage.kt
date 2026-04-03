package com.cryptovpn.ui.pages

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Update
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cryptovpn.ui.theme.*

/**
 * 强制更新页 ViewModel State
 */
data class ForceUpdateUiState(
    val currentVersion: String = "1.0.0",
    val newVersion: String = "1.2.0",
    val updateLogs: List<String> = listOf(
        "优化VPN连接稳定性",
        "新增更多服务器节点",
        "修复已知问题",
        "提升应用性能"
    ),
    val isDownloading: Boolean = false,
    val downloadProgress: Float = 0f
)

/**
 * 强制更新页
 * 
 * @param uiState 页面状态
 * @param onUpdateClick 立即更新按钮点击
 */
@Composable
fun ForceUpdatePage(
    uiState: ForceUpdateUiState = ForceUpdateUiState(),
    onUpdateClick: () -> Unit = {}
) {
    // 脉冲发光动画
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulseScale"
    )
    
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowAlpha"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkNavyBackground)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            // 更新图标 - 带脉冲发光效果
            Box(
                modifier = Modifier.size(120.dp),
                contentAlignment = Alignment.Center
            ) {
                // 发光层
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .scale(pulseScale)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    PrimaryBlue.copy(alpha = glowAlpha),
                                    PrimaryBlue.copy(alpha = 0f)
                                )
                            ),
                            shape = CircleShape
                        )
                )
                
                // 图标背景
                Surface(
                    modifier = Modifier.size(80.dp),
                    shape = CircleShape,
                    color = PrimaryBlue.copy(alpha = 0.2f)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Update,
                            contentDescription = "更新",
                            tint = PrimaryBlue,
                            modifier = Modifier.size(40.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // 标题
            Text(
                text = "发现新版本",
                color = TextPrimaryWhite,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 版本信息
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = DarkNavyCard
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = uiState.currentVersion,
                        color = TextSecondaryGray,
                        fontSize = 14.sp
                    )
                    Text(
                        text = " → ",
                        color = PrimaryBlue,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = uiState.newVersion,
                        color = SuccessGreen,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // 更新说明卡片
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = DarkNavyCard
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Text(
                        text = "更新内容",
                        color = TextPrimaryWhite,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    uiState.updateLogs.forEach { log ->
                        UpdateLogItem(text = log)
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // 下载进度（如果正在下载）
            if (uiState.isDownloading) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    LinearProgressIndicator(
                        progress = { uiState.downloadProgress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp),
                        color = PrimaryBlue,
                        trackColor = DarkNavyBackground,
                        drawStopIndicator = {}
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "正在下载 ${(uiState.downloadProgress * 100).toInt()}%",
                        color = TextSecondaryGray,
                        fontSize = 12.sp
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // 立即更新按钮
            Button(
                onClick = onUpdateClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryBlue,
                    disabledContainerColor = PrimaryBlue.copy(alpha = 0.5f)
                ),
                enabled = !uiState.isDownloading
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Download,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (uiState.isDownloading) "下载中..." else "立即更新",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 提示文字
            Text(
                text = "此版本为重要更新，必须升级后才能继续使用",
                color = WarningYellow.copy(alpha = 0.8f),
                fontSize = 12.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}

/**
 * 更新日志项
 */
@Composable
private fun UpdateLogItem(text: String) {
    Row(
        modifier = Modifier.padding(vertical = 4.dp),
        verticalAlignment = Alignment.Top
    ) {
        // 圆点标记
        Box(
            modifier = Modifier
                .size(6.dp)
                .background(PrimaryBlue, CircleShape)
                .padding(top = 6.dp)
        )
        
        Spacer(modifier = Modifier.width(10.dp))
        
        Text(
            text = text,
            color = TextSecondaryGray,
            fontSize = 14.sp,
            lineHeight = 20.sp
        )
    }
}

/**
 * 强制更新页预览 - 默认状态
 */
@Preview(name = "Force Update - Default", showBackground = true)
@Composable
fun ForceUpdatePagePreview() {
    CryptoVPNTheme {
        ForceUpdatePage(
            uiState = ForceUpdateUiState()
        )
    }
}

/**
 * 强制更新页预览 - 下载中状态
 */
@Preview(name = "Force Update - Downloading", showBackground = true)
@Composable
fun ForceUpdatePageDownloadingPreview() {
    CryptoVPNTheme {
        ForceUpdatePage(
            uiState = ForceUpdateUiState(
                isDownloading = true,
                downloadProgress = 0.65f
            )
        )
    }
}
