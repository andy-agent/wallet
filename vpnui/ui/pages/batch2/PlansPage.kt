package com.cryptovpn.ui.pages.plans

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.cryptovpn.ui.theme.CryptoVPNTheme

// ============================================
// 套餐页面状态定义
// ============================================
data class PlansPageState(
    val isLoading: Boolean = false,
    val recommendedPlan: Plan? = null,
    val regularPlans: List<Plan> = emptyList(),
    val selectedPlanId: String? = null,
    val errorMessage: String? = null
)

data class Plan(
    val id: String,
    val name: String,
    val description: String,
    val originalPrice: Double,
    val discountedPrice: Double,
    val durationDays: Int,
    val isRecommended: Boolean = false,
    val features: List<PlanFeature>,
    val badge: String? = null
)

data class PlanFeature(
    val text: String,
    val isHighlight: Boolean = false
)

// 示例数据
val samplePlans = listOf(
    Plan(
        id = "pro_yearly",
        name = "专业版年付",
        description = "最受欢迎的选择",
        originalPrice = 299.0,
        discountedPrice = 199.0,
        durationDays = 365,
        isRecommended = true,
        badge = "推荐",
        features = listOf(
            PlanFeature("全球 50+ 节点", true),
            PlanFeature("不限流量", true),
            PlanFeature("5 台设备同时在线", true),
            PlanFeature("专属客服支持", false),
            PlanFeature("智能路由优化", false)
        )
    ),
    Plan(
        id = "pro_quarterly",
        name = "专业版季付",
        description = "灵活选择",
        originalPrice = 89.0,
        discountedPrice = 69.0,
        durationDays = 90,
        features = listOf(
            PlanFeature("全球 50+ 节点", true),
            PlanFeature("不限流量", true),
            PlanFeature("5 台设备同时在线", true),
            PlanFeature("专属客服支持", false)
        )
    ),
    Plan(
        id = "pro_monthly",
        name = "专业版月付",
        description = "随时取消",
        originalPrice = 35.0,
        discountedPrice = 29.0,
        durationDays = 30,
        features = listOf(
            PlanFeature("全球 50+ 节点", true),
            PlanFeature("不限流量", true),
            PlanFeature("3 台设备同时在线", true)
        )
    ),
    Plan(
        id = "basic_yearly",
        name = "基础版年付",
        description = "经济实惠",
        originalPrice = 149.0,
        discountedPrice = 99.0,
        durationDays = 365,
        features = listOf(
            PlanFeature("全球 20+ 节点", true),
            PlanFeature("每月 100GB 流量", true),
            PlanFeature("3 台设备同时在线", true)
        )
    )
)

// ============================================
// 套餐页面
// ============================================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlansPage(
    viewModel: PlansViewModel = hiltViewModel(),
    onBackClick: () -> Unit = {},
    onPlanSelected: (Plan) -> Unit = {},
    onPurchaseClick: (Plan) -> Unit = {}
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            PlansTopBar(onBackClick = onBackClick)
        },
        containerColor = Color(0xFF0B1020)
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (state.isLoading) {
                LoadingContent()
            } else {
                PlansContent(
                    state = state,
                    onPlanSelected = { viewModel.selectPlan(it) },
                    onPurchaseClick = onPurchaseClick
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PlansTopBar(
    onBackClick: () -> Unit
) {
    TopAppBar(
        title = {
            Column {
                Text(
                    text = "选择套餐",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "选择适合您的VPN服务",
                    color = Color(0xFF9CA3AF),
                    fontSize = 14.sp
                )
            }
        },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "返回",
                    tint = Color.White
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color(0xFF0B1020)
        )
    )
}

@Composable
private fun LoadingContent() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            color = Color(0xFF1D4ED8),
            modifier = Modifier.size(48.dp)
        )
    }
}

@Composable
private fun PlansContent(
    state: PlansPageState,
    onPlanSelected: (String) -> Unit,
    onPurchaseClick: (Plan) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        // 推荐套餐
        state.recommendedPlan?.let { recommendedPlan ->
            item {
                RecommendedPlanCard(
                    plan = recommendedPlan,
                    isSelected = state.selectedPlanId == recommendedPlan.id,
                    onSelect = { onPlanSelected(recommendedPlan.id) },
                    onPurchaseClick = { onPurchaseClick(recommendedPlan) }
                )
            }
        }

        // 其他套餐标题
        if (state.regularPlans.isNotEmpty()) {
            item {
                Text(
                    text = "其他套餐",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }

        // 普通套餐列表
        items(state.regularPlans) { plan ->
            RegularPlanCard(
                plan = plan,
                isSelected = state.selectedPlanId == plan.id,
                onSelect = { onPlanSelected(plan.id) },
                onPurchaseClick = { onPurchaseClick(plan) }
            )
        }

        // 底部间距
        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

// ============================================
// 推荐套餐卡片
// ============================================
@Composable
private fun RecommendedPlanCard(
    plan: Plan,
    isSelected: Boolean,
    onSelect: () -> Unit,
    onPurchaseClick: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "glow")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.6f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow_alpha"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onSelect),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 8.dp else 2.dp
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF1D4ED8),
                            Color(0xFF3B82F6),
                            Color(0xFF60A5FA)
                        )
                    )
                )
        ) {
            // 光晕效果
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color.White.copy(alpha = glowAlpha),
                                Color.Transparent
                            ),
                            center = Alignment.TopCenter
                        )
                    )
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                // 推荐标签
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Surface(
                        color = Color(0xFFF59E0B),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = plan.badge ?: "推荐",
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            color = Color(0xFF0B1020),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // 选中指示器
                    if (isSelected) {
                        Surface(
                            color = Color.White,
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "已选中",
                                tint = Color(0xFF1D4ED8),
                                modifier = Modifier
                                    .size(24.dp)
                                    .padding(4.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 套餐名称
                Text(
                    text = plan.name,
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = plan.description,
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 14.sp,
                    modifier = Modifier.padding(top = 4.dp)
                )

                Spacer(modifier = Modifier.height(20.dp))

                // 价格区域
                PriceSection(
                    originalPrice = plan.originalPrice,
                    discountedPrice = plan.discountedPrice,
                    durationDays = plan.durationDays,
                    isRecommended = true
                )

                Spacer(modifier = Modifier.height(20.dp))

                // 权益列表
                PlanFeaturesList(
                    features = plan.features,
                    isRecommended = true
                )

                Spacer(modifier = Modifier.height(20.dp))

                // 购买按钮
                Button(
                    onClick = onPurchaseClick,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp),
                    elevation = ButtonDefaults.buttonElevation(
                        defaultElevation = 4.dp
                    )
                ) {
                    Text(
                        text = "立即购买",
                        color = Color(0xFF1D4ED8),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
            }
        }
    }
}

// ============================================
// 普通套餐卡片
// ============================================
@Composable
private fun RegularPlanCard(
    plan: Plan,
    isSelected: Boolean,
    onSelect: () -> Unit,
    onPurchaseClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onSelect),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1F2937)
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 4.dp else 0.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column {
                    Text(
                        text = plan.name,
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = plan.description,
                        color = Color(0xFF9CA3AF),
                        fontSize = 12.sp,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }

                // 选中指示器
                if (isSelected) {
                    Surface(
                        color = Color(0xFF1D4ED8),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "已选中",
                            tint = Color.White,
                            modifier = Modifier
                                .size(20.dp)
                                .padding(3.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 价格区域
            PriceSection(
                originalPrice = plan.originalPrice,
                discountedPrice = plan.discountedPrice,
                durationDays = plan.durationDays,
                isRecommended = false
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 权益列表
            PlanFeaturesList(
                features = plan.features,
                isRecommended = false
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 购买按钮
            OutlinedButton(
                onClick = onPurchaseClick,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color(0xFF1D4ED8)
                ),
                shape = RoundedCornerShape(10.dp),
                border = androidx.compose.foundation.BorderStroke(
                    width = 1.dp,
                    color = Color(0xFF1D4ED8)
                )
            ) {
                Text(
                    text = "购买",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }
        }
    }
}

// ============================================
// 价格区域
// ============================================
@Composable
private fun PriceSection(
    originalPrice: Double,
    discountedPrice: Double,
    durationDays: Int,
    isRecommended: Boolean
) {
    val textColor = if (isRecommended) Color.White else Color.White
    val secondaryColor = if (isRecommended) Color.White.copy(alpha = 0.7f) else Color(0xFF9CA3AF)

    Row(
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "¥${discountedPrice.toInt()}",
            color = textColor,
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold
        )

        if (originalPrice > discountedPrice) {
            Text(
                text = "¥${originalPrice.toInt()}",
                color = if (isRecommended) Color.White.copy(alpha = 0.5f) else Color(0xFF6B7280),
                fontSize = 16.sp,
                textDecoration = TextDecoration.LineThrough
            )
        }
    }

    val dailyPrice = discountedPrice / durationDays
    Text(
        text = "约 ¥${String.format("%.2f", dailyPrice)}/天 · ${durationDays}天",
        color = secondaryColor,
        fontSize = 14.sp,
        modifier = Modifier.padding(top = 4.dp)
    )

    // 节省金额
    if (originalPrice > discountedPrice) {
        val savings = originalPrice - discountedPrice
        val savingsPercent = ((savings / originalPrice) * 100).toInt()
        Surface(
            color = if (isRecommended) Color(0xFF22C55E) else Color(0xFF22C55E).copy(alpha = 0.2f),
            shape = RoundedCornerShape(4.dp),
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Text(
                text = "节省 ¥${savings.toInt()} (${savingsPercent}%)",
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                color = if (isRecommended) Color.White else Color(0xFF22C55E),
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

// ============================================
// 权益列表
// ============================================
@Composable
private fun PlanFeaturesList(
    features: List<PlanFeature>,
    isRecommended: Boolean
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        features.forEach { feature ->
            FeatureItem(
                feature = feature,
                isRecommended = isRecommended
            )
        }
    }
}

@Composable
private fun FeatureItem(
    feature: PlanFeature,
    isRecommended: Boolean
) {
    val iconColor = if (feature.isHighlight) {
        if (isRecommended) Color(0xFF22C55E) else Color(0xFF22C55E)
    } else {
        if (isRecommended) Color.White.copy(alpha = 0.7f) else Color(0xFF9CA3AF)
    }

    val textColor = if (feature.isHighlight) {
        if (isRecommended) Color.White else Color.White
    } else {
        if (isRecommended) Color.White.copy(alpha = 0.8f) else Color(0xFF9CA3AF)
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            imageVector = if (feature.isHighlight) Icons.Default.CheckCircle else Icons.Outlined.CheckCircle,
            contentDescription = null,
            tint = iconColor,
            modifier = Modifier.size(18.dp)
        )
        Text(
            text = feature.text,
            color = textColor,
            fontSize = 14.sp,
            fontWeight = if (feature.isHighlight) FontWeight.Medium else FontWeight.Normal
        )
    }
}

// ============================================
// 预览
// ============================================
@Preview(name = "套餐页 - 完整", showBackground = true, backgroundColor = 0xFF0B1020)
@Composable
fun PlansPagePreview() {
    CryptoVPNTheme {
        PlansPageContentPreview(
            state = PlansPageState(
                isLoading = false,
                recommendedPlan = samplePlans[0],
                regularPlans = samplePlans.subList(1, samplePlans.size),
                selectedPlanId = null
            )
        )
    }
}

@Preview(name = "套餐页 - 加载中", showBackground = true, backgroundColor = 0xFF0B1020)
@Composable
fun PlansPagePreview_Loading() {
    CryptoVPNTheme {
        PlansPageContentPreview(
            state = PlansPageState(isLoading = true)
        )
    }
}

@Preview(name = "推荐套餐卡片", showBackground = true, backgroundColor = 0xFF0B1020)
@Composable
fun RecommendedPlanCardPreview() {
    CryptoVPNTheme {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            RecommendedPlanCard(
                plan = samplePlans[0],
                isSelected = true,
                onSelect = {},
                onPurchaseClick = {}
            )
        }
    }
}

@Preview(name = "普通套餐卡片", showBackground = true, backgroundColor = 0xFF0B1020)
@Composable
fun RegularPlanCardPreview() {
    CryptoVPNTheme {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            RegularPlanCard(
                plan = samplePlans[1],
                isSelected = false,
                onSelect = {},
                onPurchaseClick = {}
            )
        }
    }
}

@Composable
private fun PlansPageContentPreview(state: PlansPageState) {
    Scaffold(
        topBar = {
            PlansTopBar {}
        },
        containerColor = Color(0xFF0B1020)
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (state.isLoading) {
                LoadingContent()
            } else {
                PlansContent(
                    state = state,
                    onPlanSelected = {},
                    onPurchaseClick = {}
                )
            }
        }
    }
}
