package com.v2ray.ang.composeui.pages.splash

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.v2ray.ang.composeui.theme.BackgroundDeepest
import com.v2ray.ang.composeui.theme.Primary
import com.v2ray.ang.composeui.theme.TextPrimary
import com.v2ray.ang.composeui.theme.TextSecondary
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * 启动页状态
 */
sealed class SplashState {
    object Idle : SplashState()
    object Loading : SplashState()
    data class Loaded(val shouldUpdate: Boolean, val isForceUpdate: Boolean) : SplashState()
    data class Error(val message: String) : SplashState()
}

/**
 * 启动页ViewModel
 */
class SplashViewModel : ViewModel() {
    private val _state = MutableStateFlow<SplashState>(SplashState.Idle)
    val state: StateFlow<SplashState> = _state

    private val _progress = MutableStateFlow(0f)
    val progress: StateFlow<Float> = _progress

    private val _statusText = MutableStateFlow("正在初始化...")
    val statusText: StateFlow<String> = _statusText

    fun startInitialization() {
        viewModelScope.launch {
            _state.value = SplashState.Loading
            
            // 模拟初始化过程
            val steps = listOf(
                "正在检查网络连接..." to 0.2f,
                "正在加载配置..." to 0.4f,
                "正在验证版本..." to 0.6f,
                "正在检查更新..." to 0.8f,
                "准备就绪" to 1.0f
            )
            
            steps.forEach { (text, progress) ->
                delay(400)
                _statusText.value = text
                _progress.value = progress
            }
            
            delay(300)
            _state.value = SplashState.Loaded(shouldUpdate = false, isForceUpdate = false)
        }
    }
}

/**
 * 启动页
 * 显示Logo、进度条和初始化状态文字
 */
@Composable
fun SplashScreen(
    viewModel: SplashViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    onNavigateToHome: () -> Unit = {},
    onNavigateToLogin: () -> Unit = {},
    onShowForceUpdate: () -> Unit = {},
    onShowOptionalUpdate: () -> Unit = {}
) {
    val state by viewModel.state.collectAsState()
    val progress by viewModel.progress.collectAsState()
    val statusText by viewModel.statusText.collectAsState()

    // Logo动画
    val infiniteTransition = rememberInfiniteTransition(label = "logo_pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    // 启动初始化
    LaunchedEffect(Unit) {
        viewModel.startInitialization()
    }

    // 状态处理
    LaunchedEffect(state) {
        when (state) {
            is SplashState.Loaded -> {
                val loadedState = state as SplashState.Loaded
                when {
                    loadedState.isForceUpdate -> onShowForceUpdate()
                    loadedState.shouldUpdate -> onShowOptionalUpdate()
                    else -> onNavigateToHome()
                }
            }
            else -> {}
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDeepest),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Logo区域
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .scale(scale),
                contentAlignment = Alignment.Center
            ) {
                // Logo图标
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    shape = MaterialTheme.shapes.extraLarge,
                    color = Primary.copy(alpha = 0.2f)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = "CV",
                            fontSize = 48.sp,
                            fontWeight = FontWeight.Bold,
                            color = Primary
                        )
                    }
                }
                
                // 外圈发光效果
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        shape = MaterialTheme.shapes.extraLarge,
                        color = Primary.copy(alpha = 0.1f)
                    ) {}
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // App名称
            Text(
                text = "CryptoVPN",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            Spacer(modifier = Modifier.height(8.dp))

            // 副标题
            Text(
                text = "安全、快速的VPN服务",
                fontSize = 14.sp,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(48.dp))

            // 进度条
            if (state is SplashState.Loading || state is SplashState.Idle) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                            .width(200.dp)
                            .height(4.dp),
                        color = Primary,
                        trackColor = Primary.copy(alpha = 0.2f),
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // 状态文字
                    Text(
                        text = statusText,
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }
            }
        }

        // 版本号
        Text(
            text = "v1.0.0",
            fontSize = 12.sp,
            color = TextSecondary.copy(alpha = 0.6f),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp)
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0B1020)
@Composable
fun SplashScreenPreview() {
    MaterialTheme {
        SplashScreen()
    }
}
