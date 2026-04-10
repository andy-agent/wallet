package com.v2ray.ang.composeui.pages.p0

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import com.v2ray.ang.composeui.navigation.CryptoVpnRouteSpec
import com.v2ray.ang.composeui.p0.ui.P01Card
import com.v2ray.ang.composeui.p0.ui.P01CardCopy
import com.v2ray.ang.composeui.p0.ui.P01CardHeader
import com.v2ray.ang.composeui.p0.ui.P01Chip
import com.v2ray.ang.composeui.p0.ui.P01Header
import com.v2ray.ang.composeui.p0.ui.P01MetricCell
import com.v2ray.ang.composeui.p0.ui.P01MetricGrid
import com.v2ray.ang.composeui.p0.ui.P01List
import com.v2ray.ang.composeui.p0.ui.P01ListRow
import com.v2ray.ang.composeui.p0.ui.P01Orb
import com.v2ray.ang.composeui.p0.ui.P01PhoneScaffold
import com.v2ray.ang.composeui.p0.repository.MockP0Repository
import com.v2ray.ang.composeui.p0.viewmodel.SplashViewModel
import com.v2ray.ang.composeui.theme.CryptoVpnTheme

@Composable
fun SplashRoute(
    viewModel: SplashViewModel,
    onFinished: () -> Unit,
    onBottomNav: (String) -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.readyToNavigate) {
        if (uiState.readyToNavigate) {
            onFinished()
        }
    }

    SplashScreen(
        uiState = uiState,
        onBottomNav = onBottomNav,
    )
}

@Composable
fun SplashScreen(
    uiState: com.v2ray.ang.composeui.p0.model.SplashUiState,
    onBottomNav: (String) -> Unit = {},
) {
    val animatedProgress by animateFloatAsState(
        targetValue = uiState.progress.coerceIn(0f, 1f),
        animationSpec = tween(durationMillis = 420),
        label = "splash_progress",
    )
    val infiniteTransition = rememberInfiniteTransition(label = "splash_scan")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.25f,
        targetValue = 0.7f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = LinearEasing),
        ),
        label = "splash_glow",
    )

    P01PhoneScaffold(
        statusTime = "18:05",
        currentRoute = CryptoVpnRouteSpec.vpnHome.name,
        onBottomNav = onBottomNav,
        showBottomNav = false,
    ) {
        P01Header(
            eyebrow = "CRYPTO • VPN • PRIVATE NETWORK",
            title = "CryptoVPN",
            subtitle = "多链钱包 + 私密高速网络，在一个白底科技化的 Android 体验里完成。",
            chips = listOf("安全自托管", "全球加速", "Solana / TRON"),
        )

        P01Card {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    P01CardHeader(
                        title = "连接钱包与网络",
                        subtitle = "初始化加密模块、节点探测与资产索引，系统准备完成后即可进入主界面。",
                    )
                }
                Box(modifier = Modifier.size(172.dp), contentAlignment = Alignment.Center) {
                    P01Orb()
                }
            }
            P01MetricGrid(
                items = listOf(
                    P01MetricCell("Wallet Layer", "4 链架构"),
                    P01MetricCell("VPN Layer", "62 节点"),
                ),
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp)
                    .background(Color(0x1F6880DB), RoundedCornerShape(999.dp)),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(animatedProgress)
                        .height(10.dp)
                        .background(
                            Brush.horizontalGradient(listOf(Color(0xFF4F7CFF), Color(0xFF20D3EE))),
                            RoundedCornerShape(999.dp),
                        ),
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth(animatedProgress)
                        .height(10.dp)
                        .padding(end = 4.dp),
                    contentAlignment = Alignment.CenterEnd,
                ) {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(
                                        Color.White.copy(alpha = 0.9f),
                                        Color(0xFF20D3EE).copy(alpha = glowAlpha),
                                    ),
                                ),
                                RoundedCornerShape(999.dp),
                            ),
                    )
                }
            }
            P01CardCopy("${uiState.progressHeadline} · ${uiState.progressDetail}")
        }

        P01Card {
            P01CardHeader(title = "系统正在准备")
            P01List {
                P01ListRow(
                    title = "安全自托管",
                    copy = "本地密钥环境与生物识别策略已经装载。",
                    value = if (uiState.authResolved) "已就绪" else "检查中",
                )
                P01ListRow(
                    title = "全球加速",
                    copy = "节点健康探测和智能路由优先级正在同步。",
                    value = uiState.buildStatus,
                )
                P01ListRow(
                    title = "版本",
                    copy = "当前运行模块版本",
                    value = uiState.versionLabel,
                )
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 393, heightDp = 852)
@Composable
private fun SplashScreenPreview() {
    CryptoVpnTheme {
        SplashScreen(
            uiState = SplashViewModel(MockP0Repository()).uiState.value,
        )
    }
}
