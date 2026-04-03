package com.cryptovpn.ui.pages

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cryptovpn.ui.theme.*
import kotlinx.coroutines.delay

/**
 * 启动页加载状态
 */
sealed class SplashState {
    object Loading : SplashState()
    object RouteLogin : SplashState()
    object RouteHome : SplashState()
    object ForceUpdate : SplashState()
    object OptionalUpdate : SplashState()
    object NetworkError : SplashState()
}

/**
 * 启动页 ViewModel State
 */
data class SplashUiState(
    val state: SplashState = SplashState.Loading,
    val progress: Float = 0f,
    val statusText: String = "正在初始化...",
    val version: String = "v1.0.0",
    val updateInfo: UpdateInfo? = null
)

data class UpdateInfo(
    val version: String,
    val versionCode: Int,
    val updateLogs: List<String>,
    val downloadUrl: String
)

/**
 * 启动页
 * 
 * @param uiState 页面状态
 * @param onNavigateToLogin 导航到登录页
 * @param onNavigateToHome 导航到首页
 * @param onNavigateToForceUpdate 导航到强制更新页
 * @param onShowOptionalUpdate 显示可选更新弹窗
 */
@Composable
fun SplashScreen(
    uiState: SplashUiState = SplashUiState(),
    onNavigateToLogin: () -> Unit = {},
    onNavigateToHome: () -> Unit = {},
    onNavigateToForceUpdate: () -> Unit = {},
    onShowOptionalUpdate: (UpdateInfo) -> Unit = {}
) {
    // Logo呼吸动画
    val infiniteTransition = rememberInfiniteTransition(label = "breathing")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    // 进度条动画
    val progressAnimation by animateFloatAsState(
        targetValue = uiState.progress,
        animationSpec = tween(500, easing = LinearEasing),
        label = "progress"
    )

    // 状态变更处理
    LaunchedEffect(uiState.state) {
        when (uiState.state) {
            is SplashState.RouteLogin -> onNavigateToLogin()
            is SplashState.RouteHome -> onNavigateToHome()
            is SplashState.ForceUpdate -> onNavigateToForceUpdate()
            is SplashState.OptionalUpdate -> uiState.updateInfo?.let { onShowOptionalUpdate(it) }
            else -> {}
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkNavyBackground),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(horizontal = 40.dp)
        ) {
            // Logo图标
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .scale(scale),
                contentAlignment = Alignment.Center
            ) {
                // 使用渐变色背景作为Logo占位
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    shape = RoundedCornerShape(20.dp),
                    color = PrimaryBlue
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = "CV",
                            color = Color.White,
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 品牌名
            Text(
                text = "CryptoVPN",
                color = TextPrimaryWhite,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            // 副标题
            Text(
                text = "安全、快速的VPN服务",
                color = TextSecondaryGray,
                fontSize = 14.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(48.dp))

            // 进度条
            LinearProgressIndicator(
                progress = { progressAnimation },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp),
                color = PrimaryBlue,
                trackColor = DarkNavyCard,
                drawStopIndicator = {}
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 状态文字
            Text(
                text = uiState.statusText,
                color = TextSecondaryGray,
                fontSize = 12.sp,
                textAlign = TextAlign.Center
            )
        }

        // 版本号 - 底部
        Text(
            text = uiState.version,
            color = TextSecondaryGray.copy(alpha = 0.6f),
            fontSize = 12.sp,
            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 32.dp)
        )
    }
}

/**
 * 启动页预览 - Loading状态
 */
@Preview(name = "Splash - Loading", showBackground = true)
@Composable
fun SplashScreenLoadingPreview() {
    CryptoVPNTheme {
        SplashScreen(
            uiState = SplashUiState(
                state = SplashState.Loading,
                progress = 0.6f,
                statusText = "正在连接服务器...",
                version = "v1.0.0"
            )
        )
    }
}

/**
 * 启动页预览 - 网络错误状态
 */
@Preview(name = "Splash - Network Error", showBackground = true)
@Composable
fun SplashScreenErrorPreview() {
    CryptoVPNTheme {
        SplashScreen(
            uiState = SplashUiState(
                state = SplashState.NetworkError,
                progress = 1f,
                statusText = "网络连接失败，请检查网络设置",
                version = "v1.0.0"
            )
        )
    }
}

/**
 * 模拟启动页ViewModel
 */
class SplashViewModel {
    private val _uiState = mutableStateOf(SplashUiState())
    val uiState: State<SplashUiState> = _uiState

    suspend fun initialize() {
        // 模拟初始化流程
        updateProgress(0.2f, "正在检查更新...")
        delay(500)
        
        updateProgress(0.5f, "正在加载配置...")
        delay(500)
        
        updateProgress(0.8f, "正在验证登录状态...")
        delay(500)
        
        updateProgress(1f, "准备就绪")
        delay(200)
        
        // 根据业务逻辑决定路由
        _uiState.value = _uiState.value.copy(state = SplashState.RouteLogin)
    }

    private fun updateProgress(progress: Float, status: String) {
        _uiState.value = _uiState.value.copy(
            progress = progress,
            statusText = status
        )
    }
}
