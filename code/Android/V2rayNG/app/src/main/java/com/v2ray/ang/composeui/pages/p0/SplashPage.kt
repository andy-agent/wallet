package com.v2ray.ang.composeui.pages.p0

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.v2ray.ang.composeui.p0.model.SplashUiState
import com.v2ray.ang.composeui.p0.repository.MockP0Repository
import com.v2ray.ang.composeui.p0.viewmodel.SplashViewModel
import com.v2ray.ang.composeui.theme.CryptoVpnTheme
import kotlin.math.roundToInt

private val SplashBgTop = Color(0xFFF8FBFF)
private val SplashBgBottom = Color(0xFFEDF4FF)
private val SplashCard = Color(0xF7FFFFFF)
private val SplashCardSoft = Color(0xFFF4F8FF)
private val SplashCardTint = Color(0xFFF8FBFF)
private val SplashBorder = Color(0x1A5F7FF6)
private val SplashTextStrong = Color(0xFF13284A)
private val SplashTextBody = Color(0xFF536888)
private val SplashTextSoft = Color(0xFF7A8BA8)
private val SplashBlue = Color(0xFF4877FF)
private val SplashBlueDeep = Color(0xFF3354D5)
private val SplashCyan = Color(0xFF23C8F2)
private val SplashMint = Color(0xFF35C892)

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
    val animatedProgress by animateFloatAsState(
        targetValue = uiState.progress.coerceIn(0f, 1f),
        animationSpec = tween(durationMillis = 420),
        label = "splash_progress",
    )

    SplashScaffold {
        SplashBrandHeader()
        SplashPrimaryCard()
        SplashStatusCard(
            uiState = uiState,
            progress = animatedProgress,
        )
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
                        colors = listOf(SplashBlue.copy(alpha = 0.08f), Color.Transparent),
                        center = Offset(size.width * 0.84f, size.height * 0.12f),
                        radius = size.minDimension * 0.34f,
                    ),
                    radius = size.minDimension * 0.34f,
                    center = Offset(size.width * 0.84f, size.height * 0.12f),
                )
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(SplashCyan.copy(alpha = 0.06f), Color.Transparent),
                        center = Offset(size.width * 0.16f, size.height * 0.94f),
                        radius = size.minDimension * 0.38f,
                    ),
                    radius = size.minDimension * 0.38f,
                    center = Offset(size.width * 0.16f, size.height * 0.94f),
                )
            },
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            content = content,
        )
    }
}

@Composable
private fun SplashBrandHeader() {
    Column(
        modifier = Modifier.padding(top = 4.dp, bottom = 2.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        SplashEyebrow(text = "ANDROID APP INITIALIZING")
        Text(
            text = "CryptoVPN",
            color = SplashTextStrong,
            fontSize = 26.sp,
            fontWeight = FontWeight.ExtraBold,
            lineHeight = 29.sp,
        )
        Text(
            text = "启动后先完成安全自托管、钱包桥接与私密链路准备，再进入主界面。",
            color = SplashTextBody,
            fontSize = 13.sp,
            lineHeight = 20.sp,
        )
    }
}

@Composable
private fun SplashPrimaryCard() {
    SplashPanel {
        SplashEyebrow(text = "PRIMARY INIT MODULE")
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Text(
                    text = "连接钱包与网络",
                    color = SplashTextStrong,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 31.sp,
                )
                Text(
                    text = "先装载本地安全环境，再接入多链钱包桥与全球节点路由。主结构保持固定，实时进度在下方状态卡更新。",
                    color = SplashTextBody,
                    fontSize = 13.sp,
                    lineHeight = 21.sp,
                )
            }
            SplashSecurityRing(modifier = Modifier.size(118.dp))
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            SplashFeatureTile(
                modifier = Modifier.weight(1f),
                label = "多链架构",
                value = "Wallet Bridge",
                detail = "EVM / Solana / TRON",
            )
            SplashFeatureTile(
                modifier = Modifier.weight(1f),
                label = "全球节点",
                value = "Secure Relay",
                detail = "智能接入与低延迟路由",
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(SplashCardSoft)
                .border(1.dp, SplashBorder, RoundedCornerShape(18.dp))
                .padding(horizontal = 16.dp, vertical = 14.dp),
        ) {
            Text(
                text = "初始化顺序固定：本地安全校验、账户与缓存同步、链路探测完成后再跳转首页。",
                color = SplashTextBody,
                fontSize = 12.sp,
                lineHeight = 19.sp,
            )
        }
    }
}

@Composable
private fun SplashStatusCard(
    uiState: SplashUiState,
    progress: Float,
) {
    val percent = (progress * 100).roundToInt()
    SplashPanel(background = SplashCardTint) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top,
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                SplashEyebrow(text = "SYSTEM STATUS")
                Text(
                    text = "系统正在准备",
                    color = SplashTextStrong,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 25.sp,
                )
            }
            SplashLiveBadge(text = if (uiState.readyToNavigate) "READY" else "$percent%")
        }

        SplashProgressBar(progress = progress)

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(Color.White.copy(alpha = 0.82f))
                .border(1.dp, SplashBorder, RoundedCornerShape(20.dp))
                .padding(horizontal = 16.dp, vertical = 15.dp),
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = uiState.progressHeadline,
                    color = SplashTextStrong,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 18.sp,
                )
                Text(
                    text = uiState.progressDetail,
                    color = SplashTextBody,
                    fontSize = 12.sp,
                    lineHeight = 19.sp,
                )
            }
        }

        SplashStatusRow(
            title = "安全自托管",
            detail = "本地密钥环境与生物识别策略",
            value = securityStateLabel(uiState),
            valueColor = if (uiState.authResolved) SplashMint else SplashTextStrong,
        )
        SplashStatusRow(
            title = "全球加速",
            detail = uiState.buildStatus.ifBlank { "节点探测与智能路由优先级同步中" },
            value = relayStateLabel(uiState),
            valueColor = if (uiState.readyToNavigate) SplashMint else SplashTextStrong,
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(SplashCardSoft)
                .border(1.dp, SplashBorder, RoundedCornerShape(18.dp))
                .padding(horizontal = 14.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "运行模块 ${uiState.versionLabel}",
                color = SplashTextSoft,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
            )
            Text(
                text = if (uiState.readyToNavigate) "进入主界面" else "准备中",
                color = SplashBlue,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

@Composable
private fun SplashPanel(
    modifier: Modifier = Modifier,
    background: Color = SplashCard,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .shadow(18.dp, RoundedCornerShape(28.dp))
            .clip(RoundedCornerShape(28.dp))
            .background(background)
            .border(1.dp, SplashBorder, RoundedCornerShape(28.dp))
            .padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        content = content,
    )
}

@Composable
private fun SplashFeatureTile(
    modifier: Modifier = Modifier,
    label: String,
    value: String,
    detail: String,
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(SplashCardSoft)
            .border(1.dp, SplashBorder, RoundedCornerShape(20.dp))
            .padding(horizontal = 14.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Text(
            text = label,
            color = SplashTextSoft,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
        )
        Text(
            text = value,
            color = SplashTextStrong,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            lineHeight = 21.sp,
        )
        Text(
            text = detail,
            color = SplashTextBody,
            fontSize = 12.sp,
            lineHeight = 18.sp,
        )
    }
}

@Composable
private fun SplashSecurityRing(
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .clip(CircleShape)
            .background(
                Brush.radialGradient(
                    colors = listOf(Color.White, Color(0xFFEFF5FF)),
                ),
            )
            .border(1.dp, SplashBorder, CircleShape),
            contentAlignment = Alignment.Center,
    ) {
        Canvas(modifier = Modifier.fillMaxSize().padding(12.dp)) {
            val outerRadius = size.minDimension * 0.46f
            val center = center
            drawCircle(
                color = SplashBlue.copy(alpha = 0.14f),
                radius = outerRadius,
                center = center,
                style = Stroke(width = 11.dp.toPx()),
            )
            drawCircle(
                color = SplashCyan.copy(alpha = 0.20f),
                radius = size.minDimension * 0.31f,
                center = center,
                style = Stroke(width = 7.dp.toPx()),
            )
            drawCircle(
                color = SplashBlueDeep.copy(alpha = 0.10f),
                radius = size.minDimension * 0.18f,
                center = center,
            )
            drawCircle(
                color = SplashCyan,
                radius = 4.dp.toPx(),
                center = Offset(size.width * 0.78f, size.height * 0.26f),
            )
        }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            Text(
                text = "SECURE",
                color = SplashBlueDeep,
                fontSize = 13.sp,
                fontWeight = FontWeight.ExtraBold,
            )
            Text(
                text = "LINK",
                color = SplashTextSoft,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

@Composable
private fun SplashProgressBar(progress: Float) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(10.dp)
            .clip(RoundedCornerShape(999.dp))
            .background(SplashBlue.copy(alpha = 0.12f)),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(progress)
                .height(10.dp)
                .clip(RoundedCornerShape(999.dp))
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(SplashBlue, SplashCyan),
                    ),
                ),
        )
        Box(
            modifier = Modifier
                .fillMaxWidth(progress)
                .height(10.dp)
                .padding(end = 2.dp),
            contentAlignment = Alignment.CenterEnd,
        ) {
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(Color.White, SplashCyan),
                        ),
                        CircleShape,
                    ),
            )
        }
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
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(Color.White.copy(alpha = 0.76f))
            .border(1.dp, SplashBorder, RoundedCornerShape(18.dp))
            .padding(horizontal = 14.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(5.dp),
        ) {
            Text(
                text = title,
                color = SplashTextStrong,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
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
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
private fun SplashLiveBadge(text: String) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .background(SplashBlue.copy(alpha = 0.10f))
            .border(1.dp, SplashBlue.copy(alpha = 0.08f), RoundedCornerShape(999.dp))
            .padding(horizontal = 11.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Canvas(modifier = Modifier.size(8.dp)) {
            drawCircle(color = SplashCyan)
        }
        Text(
            text = text,
            color = SplashBlue,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
private fun SplashEyebrow(text: String) {
    Text(
        text = text,
        color = SplashTextSoft,
        fontSize = 10.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 1.8.sp,
    )
}

private fun securityStateLabel(uiState: SplashUiState): String {
    return when {
        uiState.authResolved -> "已就绪"
        uiState.progress >= 0.78f -> "校验中"
        uiState.progress >= 0.24f -> "装载中"
        else -> "等待中"
    }
}

private fun relayStateLabel(uiState: SplashUiState): String {
    return when {
        uiState.readyToNavigate -> "已同步"
        uiState.progress >= 0.58f -> "探测中"
        uiState.progress >= 0.18f -> "排队中"
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
