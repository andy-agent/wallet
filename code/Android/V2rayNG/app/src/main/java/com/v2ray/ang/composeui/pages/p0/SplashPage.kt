package com.v2ray.ang.composeui.pages.p0

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.v2ray.ang.composeui.p0.ui.P01Orb
import com.v2ray.ang.composeui.p0.model.SplashUiState
import com.v2ray.ang.composeui.p0.repository.MockP0Repository
import com.v2ray.ang.composeui.p0.viewmodel.SplashViewModel
import com.v2ray.ang.composeui.theme.CryptoVpnTheme

private val SplashBgTop = Color(0xFFF8FBFF)
private val SplashBgBottom = Color(0xFFEEF4FF)
private val SplashPanel = Color(0xF7FFFFFF)
private val SplashPanelSoft = Color(0xFFF5F9FF)
private val SplashBorder = Color(0x15627FF2)
private val SplashDivider = Color(0x14627FF2)
private val SplashTextStrong = Color(0xFF19345F)
private val SplashTextBody = Color(0xFF4D6289)
private val SplashTextSoft = Color(0xFF7A8DB2)
private val SplashBlue = Color(0xFF4877FF)
private val SplashCyan = Color(0xFF25C8F2)
private val SplashBlueDeep = Color(0xFF3353D2)
private val SplashMint = Color(0xFF35C892)
private const val MainCardProgress = 0.72f

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

    SplashScreen(uiState = uiState)
}

@Composable
fun SplashScreen(uiState: SplashUiState) {
    SplashScaffold {
        SplashHeader()
        SplashMainCard()
        SplashStatusCard(uiState = uiState)
    }
}

@Composable
private fun SplashScaffold(
    content: @Composable ColumnScope.() -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(SplashBgTop, SplashBgBottom),
                ),
            )
            .drawBehind {
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(SplashBlue.copy(alpha = 0.10f), Color.Transparent),
                        center = Offset(size.width * 0.88f, size.height * 0.06f),
                        radius = size.minDimension * 0.30f,
                    ),
                    radius = size.minDimension * 0.30f,
                    center = Offset(size.width * 0.88f, size.height * 0.06f),
                )
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(SplashCyan.copy(alpha = 0.06f), Color.Transparent),
                        center = Offset(size.width * 0.12f, size.height * 0.92f),
                        radius = size.minDimension * 0.34f,
                    ),
                    radius = size.minDimension * 0.34f,
                    center = Offset(size.width * 0.12f, size.height * 0.92f),
                )
            },
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            content = content,
        )
    }
}

@Composable
private fun SplashHeader() {
    Column(
        modifier = Modifier.padding(top = 2.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Text(
            text = "CRYPTO • VPN • PRIVATE NETWORK",
            color = SplashTextSoft,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 2.sp,
        )
        Text(
            text = "CryptoVPN",
            color = SplashTextStrong,
            fontSize = 34.sp,
            fontWeight = FontWeight.ExtraBold,
            lineHeight = 36.sp,
        )
        Text(
            text = "多链钱包 + 私密高速网络",
            color = SplashTextBody,
            fontSize = 14.sp,
            lineHeight = 20.sp,
        )
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            SplashChip("安全自托管")
            SplashChip("全球加速")
            SplashChip("Solana / TRON / ETH")
        }
    }
}

@Composable
private fun SplashChip(text: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .background(Color.White.copy(alpha = 0.72f))
            .border(1.dp, SplashBorder, RoundedCornerShape(999.dp))
            .padding(horizontal = 14.dp, vertical = 8.dp),
    ) {
        Text(
            text = text,
            color = SplashBlue,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
private fun SplashMainCard() {
    SplashCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Text(
                    text = "连接钱包与网络",
                    color = SplashTextStrong,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                    lineHeight = 29.sp,
                )
                Text(
                    text = "初始化加密模块、节点探测与资产索引，系统准备完成后即可进入主界面。",
                    color = SplashTextBody,
                    fontSize = 13.sp,
                    lineHeight = 22.sp,
                )
            }
            SplashSecureGraphic(
                modifier = Modifier.size(138.dp),
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            SplashMetricCard(
                modifier = Modifier.weight(1f),
                label = "WALLET LAYER",
                value = "多链架构",
            )
            SplashMetricCard(
                modifier = Modifier.weight(1f),
                label = "VPN LAYER",
                value = "全球节点",
            )
        }

        SplashStaticProgressBar(progress = MainCardProgress)
    }
}

@Composable
private fun SplashCard(
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(20.dp, RoundedCornerShape(30.dp))
            .clip(RoundedCornerShape(30.dp))
            .background(SplashPanel)
            .border(1.dp, SplashBorder, RoundedCornerShape(30.dp))
            .padding(horizontal = 18.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        content = content,
    )
}

@Composable
private fun SplashMetricCard(
    modifier: Modifier = Modifier,
    label: String,
    value: String,
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(SplashPanelSoft)
            .border(1.dp, SplashBorder, RoundedCornerShape(20.dp))
            .padding(horizontal = 14.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = label,
            color = SplashTextSoft,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.8.sp,
        )
        Text(
            text = value,
            color = SplashTextStrong,
            fontSize = 20.sp,
            fontWeight = FontWeight.ExtraBold,
            lineHeight = 24.sp,
        )
    }
}

@Composable
private fun SplashSecureGraphic(
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            P01Orb(modifier = Modifier.fillMaxSize())
        }
        Text(
            text = "SECURE",
            color = SplashBlue,
            fontSize = 14.sp,
            fontWeight = FontWeight.ExtraBold,
        )
    }
}

@Composable
private fun SplashStaticProgressBar(progress: Float) {
    val transition = rememberInfiniteTransition(label = "splash_progress")
    val progressDrift = transition.animateFloat(
        initialValue = progress - 0.04f,
        targetValue = progress + 0.04f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "splash_progress_drift",
    )
    val scanOffset = transition.animateFloat(
        initialValue = -0.32f,
        targetValue = 1.16f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "splash_progress_scan",
    )
    val animatedProgress = progressDrift.value.coerceIn(0f, 1f)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(7.dp)
            .clip(RoundedCornerShape(999.dp))
            .background(SplashBlue.copy(alpha = 0.10f)),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(animatedProgress)
                .height(7.dp)
                .clip(RoundedCornerShape(999.dp))
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(SplashBlue, SplashCyan),
                    ),
                )
                .drawWithContent {
                    drawContent()
                    val scanWidth = size.width * 0.42f
                    drawRoundRect(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.White.copy(alpha = 0.58f),
                                Color.Transparent,
                            ),
                        ),
                        topLeft = Offset(
                            x = size.width * scanOffset.value - scanWidth,
                            y = 0f,
                        ),
                        size = Size(scanWidth, size.height),
                        cornerRadius = CornerRadius(size.height / 2f, size.height / 2f),
                    )
                },
        )
    }
}

@Composable
private fun SplashStatusCard(uiState: SplashUiState) {
    SplashCard {
        Text(
            text = "系统正在准备",
            color = SplashTextStrong,
            fontSize = 20.sp,
            fontWeight = FontWeight.ExtraBold,
            lineHeight = 24.sp,
        )
        SplashStatusRow(
            title = "安全自托管",
            detail = "本地密钥环境与生物识别策略已经装载。",
            value = securityStatus(uiState),
            valueColor = if (uiState.authResolved) SplashTextStrong else SplashBlueDeep,
        )
        HorizontalDivider(color = SplashDivider)
        SplashStatusRow(
            title = "全球加速",
            detail = "节点健康探测和智能路由优先级正在同步。",
            value = networkStatus(uiState),
            valueColor = if (uiState.readyToNavigate) SplashMint else SplashBlue,
        )
    }
}

@Composable
private fun SplashStatusRow(
    title: String,
    detail: String,
    value: String,
    valueColor: Color,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top,
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Text(
                text = title,
                color = SplashTextStrong,
                fontSize = 14.sp,
                fontWeight = FontWeight.ExtraBold,
            )
            Text(
                text = detail,
                color = SplashTextBody,
                fontSize = 12.sp,
                lineHeight = 18.sp,
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = value,
            color = valueColor,
            fontSize = 14.sp,
            fontWeight = FontWeight.ExtraBold,
        )
    }
}

private fun securityStatus(uiState: SplashUiState): String {
    return when {
        uiState.authResolved -> "已就绪"
        uiState.progress >= 0.24f -> "装载中"
        else -> "待装载"
    }
}

private fun networkStatus(uiState: SplashUiState): String {
    return when {
        uiState.readyToNavigate -> "已同步"
        uiState.progress >= 0.58f -> "同步中"
        uiState.progress >= 0.18f -> "连接中"
        else -> "待连接"
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
