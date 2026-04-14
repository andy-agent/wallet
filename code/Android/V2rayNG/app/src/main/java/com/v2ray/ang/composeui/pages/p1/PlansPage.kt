package com.v2ray.ang.composeui.pages.p1

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.v2ray.ang.composeui.navigation.CryptoVpnRouteSpec
import com.v2ray.ang.composeui.p1.model.PlanOptionUi
import com.v2ray.ang.composeui.p1.model.PlansEvent
import com.v2ray.ang.composeui.p1.model.PlansUiState
import com.v2ray.ang.composeui.p1.model.plansPreviewState
import com.v2ray.ang.composeui.p1.viewmodel.PlansViewModel
import com.v2ray.ang.composeui.theme.CryptoVpnTheme

private val SubscriptionBgTop = Color(0xFFF5F6FF)
private val SubscriptionBgBottom = Color(0xFFEFF8FF)
private val SubscriptionTitle = Color(0xFF101828)
private val SubscriptionSubtle = Color(0xFF98A2B3)
private val TagTextColor = Color(0xFF5A78FF)
private val GradientStart = Color(0xFF4C74FF)
private val GradientEnd = Color(0xFF21C7E8)

private data class PlansTypeScale(
    val hero: TextStyle,
    val section: TextStyle,
    val cardTitle: TextStyle,
    val itemTitle: TextStyle,
    val body: TextStyle,
    val caption: TextStyle,
)

@Composable
fun PlansRoute(
    viewModel: PlansViewModel,
    onPrimaryAction: (String) -> Unit = {},
    onSecondaryAction: (() -> Unit)? = null,
    onBottomNav: (String) -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsState()
    PlansScreen(
        uiState = uiState,
        onRefresh = { viewModel.onEvent(PlansEvent.Refresh) },
        onPrimaryAction = { planCode ->
            viewModel.onEvent(PlansEvent.PrimaryActionClicked)
            onPrimaryAction(planCode)
        },
        onSecondaryAction = {
            viewModel.onEvent(PlansEvent.SecondaryActionClicked)
            onSecondaryAction?.invoke()
        },
        onBottomNav = onBottomNav,
    )
}

@Composable
fun PlansScreen(
    uiState: PlansUiState,
    onRefresh: () -> Unit,
    onPrimaryAction: (String) -> Unit,
    onSecondaryAction: () -> Unit,
    onBottomNav: (String) -> Unit = {},
) {
    var selectedPlanCode by rememberSaveable { mutableStateOf<String?>(null) }
    val typeScale = rememberPlansTypeScale()
    LaunchedEffect(uiState.selectedPlanCode, uiState.plans) {
        val availableCodes = uiState.plans.map { it.planCode }
        if (selectedPlanCode == null || selectedPlanCode !in availableCodes) {
            selectedPlanCode = uiState.selectedPlanCode ?: availableCodes.firstOrNull()
        }
    }
    val selectedPlan = remember(uiState.plans, selectedPlanCode) {
        uiState.plans.firstOrNull { it.planCode == selectedPlanCode } ?: uiState.plans.firstOrNull()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        SubscriptionBackground()

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = 680.dp),
            contentPadding = PaddingValues(start = 16.dp, top = 22.dp, end = 16.dp, bottom = 108.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            item {
                SubscriptionHeader(payEnabledText = "支持钱包直付", typeScale = typeScale)
            }

            item {
                CurrentStatusCard(
                    currentPlanName = selectedPlan?.title ?: "未开通",
                    accessDescription = selectedPlan?.description ?: (uiState.note.ifBlank { "等待选择套餐" }),
                    payMethodName = selectedPlan?.paymentMethods?.firstOrNull()?.substringBefore('-') ?: "--",
                    payMethodDescription = selectedPlan?.paymentMethods?.joinToString("/") ?: "等待可用支付网络",
                    remainingDaysText = selectedPlan?.durationText ?: "实时状态",
                    typeScale = typeScale,
                )
            }

            if (uiState.screenState.hasError) {
                item {
                    FrostCard {
                        Text(
                            text = uiState.screenState.unavailableMessage
                                ?: uiState.screenState.errorMessage
                                ?: uiState.screenState.emptyMessage
                                ?: "套餐服务异常",
                            modifier = Modifier.padding(18.dp),
                            color = SubscriptionSubtle,
                            style = typeScale.body,
                        )
                    }
                }
            }

            items(items = uiState.plans, key = { it.planCode }) { plan ->
                SubscriptionPlanCard(
                    plan = plan,
                    selected = plan.planCode == selectedPlan?.planCode,
                    onClick = { selectedPlanCode = plan.planCode },
                    typeScale = typeScale,
                )
            }

            if (uiState.plans.isEmpty()) {
                item {
                    FrostCard {
                        Text(
                            text = uiState.screenState.emptyMessage ?: "当前没有可售套餐。",
                            modifier = Modifier.padding(18.dp),
                            color = SubscriptionSubtle,
                            style = typeScale.body,
                        )
                    }
                }
            }
        }

        BottomPayBar(
            onPayClick = {
                selectedPlan?.let { onPrimaryAction(it.planCode) } ?: onRefresh()
            },
            modifier = Modifier.align(Alignment.BottomCenter),
            enabled = selectedPlan != null || uiState.screenState.hasError,
            typeScale = typeScale,
        )
    }
}

@Composable
private fun SubscriptionHeader(
    payEnabledText: String,
    typeScale: PlansTypeScale,
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "SUBSCRIPTION",
                    letterSpacing = 1.4.sp,
                    color = Color(0xFF7D8FB3),
                    style = typeScale.caption,
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "购买你的套餐",
                    color = SubscriptionTitle,
                    style = typeScale.hero,
                )
            }

            StatusPill(
                text = payEnabledText,
                icon = Icons.Outlined.CheckCircle,
            )
        }

        Text(
            text = "VPN 是核心服务，钱包是支付与资产层。这里把它们真正融合。",
            color = SubscriptionSubtle,
            style = typeScale.body,
        )
    }
}

@Composable
private fun StatusPill(
    text: String,
    icon: ImageVector,
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .background(Color(0xFFEFFBF5))
            .padding(horizontal = 10.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color(0xFF22A06B),
            modifier = Modifier.size(14.dp),
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = text,
            color = Color(0xFF22A06B),
            style = MaterialTheme.typography.labelMedium,
        )
    }
}

@Composable
private fun CurrentStatusCard(
    currentPlanName: String,
    accessDescription: String,
    payMethodName: String,
    payMethodDescription: String,
    remainingDaysText: String,
    typeScale: PlansTypeScale,
) {
    FrostCard {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "当前状态",
                    color = SubscriptionTitle,
                    style = typeScale.section,
                )
                Text(
                    text = remainingDaysText,
                    color = SubscriptionSubtle,
                    style = typeScale.caption,
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                StatusInfoBlock(
                    title = "当前计划",
                    value = currentPlanName,
                    subtitle = accessDescription,
                    modifier = Modifier.weight(1f),
                    typeScale = typeScale,
                )
                StatusInfoBlock(
                    title = "支付资产",
                    value = payMethodName,
                    subtitle = payMethodDescription,
                    modifier = Modifier.weight(1f),
                    typeScale = typeScale,
                )
            }
        }
    }
}

@Composable
private fun StatusInfoBlock(
    title: String,
    value: String,
    subtitle: String,
    modifier: Modifier = Modifier,
    typeScale: PlansTypeScale,
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .background(Color.White.copy(alpha = 0.72f))
            .padding(horizontal = 14.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Text(
            text = title,
            color = SubscriptionSubtle,
            style = typeScale.caption,
        )
        Text(
            text = value,
            color = SubscriptionTitle,
            style = typeScale.cardTitle,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Text(
            text = subtitle,
            color = TagTextColor,
            style = typeScale.caption,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun SubscriptionPlanCard(
    plan: PlanOptionUi,
    selected: Boolean,
    onClick: () -> Unit,
    typeScale: PlansTypeScale,
) {
    val cardBrush = if (selected) {
        Brush.linearGradient(
            listOf(Color(0xFFF2F8FF), Color(0xFFEFFBFF)),
        )
    } else {
        Brush.linearGradient(
            listOf(Color.White.copy(alpha = 0.84f), Color.White.copy(alpha = 0.80f)),
        )
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.78f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Column(
            modifier = Modifier
                .background(cardBrush)
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top,
            ) {
                Text(
                    text = plan.title,
                    color = SubscriptionTitle,
                    style = typeScale.cardTitle,
                )
                if (plan.badge != null) {
                    Text(
                        text = plan.badge,
                        color = TagTextColor,
                        style = typeScale.caption,
                    )
                }
            }

            Text(
                text = plan.priceText,
                color = SubscriptionTitle,
                style = typeScale.hero,
            )

            Text(
                text = plan.description,
                color = SubscriptionSubtle,
                style = typeScale.body,
            )

            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                buildList {
                    add(plan.durationText)
                    add(plan.maxSessionsText)
                    addAll(plan.paymentMethods)
                }.forEach { tag ->
                    SmallTag(text = tag)
                }
            }
        }
    }
}

@Composable
private fun SmallTag(text: String) {
    Text(
        text = text,
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .background(Color(0xFFF2F6FF))
            .padding(horizontal = 10.dp, vertical = 6.dp),
        color = TagTextColor,
        style = MaterialTheme.typography.labelMedium,
    )
}

@Composable
private fun BottomPayBar(
    onPayClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean,
    typeScale: PlansTypeScale,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.White.copy(alpha = 0.22f))
            .navigationBarsPadding()
            .padding(horizontal = 16.dp, vertical = 12.dp),
    ) {
        Button(
            onClick = onPayClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            enabled = enabled,
            shape = RoundedCornerShape(18.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
            contentPadding = PaddingValues(),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Brush.horizontalGradient(listOf(GradientStart, GradientEnd))),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "使用钱包支付并开通",
                    color = Color.White,
                    style = typeScale.itemTitle,
                )
            }
        }
    }
}

@Composable
private fun FrostCard(content: @Composable () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.82f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Box(
            modifier = Modifier.background(
                brush = Brush.linearGradient(
                    listOf(
                        Color.White.copy(alpha = 0.60f),
                        Color(0xFFF3FBFF).copy(alpha = 0.56f),
                    ),
                ),
            ),
        ) {
            content()
        }
    }
}

@Composable
private fun SubscriptionBackground() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(colors = listOf(SubscriptionBgTop, SubscriptionBgBottom))),
    ) {
        SoftGlow(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .size(220.dp),
            color = Color(0xFF9B8CFF).copy(alpha = 0.18f),
        )
        SoftGlow(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 90.dp)
                .size(260.dp),
            color = Color(0xFF61DFFF).copy(alpha = 0.16f),
        )
        SubscriptionDots()
    }
}

@Composable
private fun SoftGlow(
    modifier: Modifier,
    color: Color,
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(999.dp))
            .background(color)
            .blur(42.dp),
    )
}

@Composable
private fun SubscriptionDots() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val points = listOf(
            Offset(size.width * 0.06f, size.height * 0.12f),
            Offset(size.width * 0.18f, size.height * 0.54f),
            Offset(size.width * 0.30f, size.height * 0.86f),
            Offset(size.width * 0.52f, size.height * 0.42f),
            Offset(size.width * 0.78f, size.height * 0.62f),
            Offset(size.width * 0.90f, size.height * 0.20f),
        )
        points.forEachIndexed { index, point ->
            drawCircle(
                color = if (index % 2 == 0) Color(0xFF5ED8F8).copy(alpha = 0.22f) else Color(0xFF9A90FF).copy(alpha = 0.20f),
                radius = if (index % 2 == 0) 4f else 3f,
                center = point,
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF5F7FF)
@Composable
private fun PlansPreview() {
    CryptoVpnTheme {
        Surface {
            PlansScreen(
                uiState = plansPreviewState(),
                onRefresh = {},
                onPrimaryAction = {},
                onSecondaryAction = {},
            )
        }
    }
}

@Composable
private fun rememberPlansTypeScale(): PlansTypeScale {
    val typography = MaterialTheme.typography
    return remember(typography) {
        PlansTypeScale(
            hero = typography.headlineLarge,
            section = typography.headlineMedium,
            cardTitle = typography.titleLarge,
            itemTitle = typography.titleMedium,
            body = typography.bodyMedium,
            caption = typography.labelMedium,
        )
    }
}
