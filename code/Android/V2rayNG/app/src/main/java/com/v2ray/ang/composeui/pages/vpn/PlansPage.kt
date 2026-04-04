package com.v2ray.ang.composeui.pages.vpn

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * 套餐信息
 */
data class PlanInfo(
    val id: String,
    val name: String,
    val duration: String,
    val price: String,
    val originalPrice: String?,
    val isRecommended: Boolean,
    val features: List<String>,
    val badge: String? = null
)

/**
 * 套餐页状态
 */
sealed class PlansState {
    object Idle : PlansState()
    object Loading : PlansState()
    data class Loaded(val plans: List<PlanInfo>, val selectedPlanId: String?) : PlansState()
    data class Error(val message: String) : PlansState()
}

/**
 * 套餐页ViewModel
 */
class PlansViewModel : ViewModel() {
    private val _state = MutableStateFlow<PlansState>(PlansState.Idle)
    val state: StateFlow<PlansState> = _state

    private val _selectedPlanId = MutableStateFlow<String?>(null)
    val selectedPlanId: StateFlow<String?> = _selectedPlanId

    init {
        loadPlans()
    }

    private fun loadPlans() {
        val plans = listOf(
            PlanInfo(
                id = "monthly",
                name = "月度套餐",
                duration = "1个月",
                price = "$9.99",
                originalPrice = null,
                isRecommended = false,
                features = listOf(
                    "无限流量",
                    "50+ 服务器节点",
                    "5 设备同时连接",
                    "24/7 客服支持"
                )
            ),
            PlanInfo(
                id = "quarterly",
                name = "季度套餐",
                duration = "3个月",
                price = "$26.99",
                originalPrice = "$29.97",
                isRecommended = true,
                features = listOf(
                    "无限流量",
                    "100+ 服务器节点",
                    "10 设备同时连接",
                    "24/7 优先客服",
                    "专属高速线路"
                ),
                badge = "省10%"
            ),
            PlanInfo(
                id = "yearly",
                name = "年度套餐",
                duration = "12个月",
                price = "$89.99",
                originalPrice = "$119.88",
                isRecommended = false,
                features = listOf(
                    "无限流量",
                    "200+ 服务器节点",
                    "无限设备连接",
                    "24/7 VIP客服",
                    "专属高速线路",
                    "静态IP选项"
                ),
                badge = "省25%"
            )
        )
        _state.value = PlansState.Loaded(plans, null)
    }

    fun selectPlan(planId: String) {
        _selectedPlanId.value = planId
        val currentState = _state.value
        if (currentState is PlansState.Loaded) {
            _state.value = currentState.copy(selectedPlanId = planId)
        }
    }
}

/**
 * 套餐页
 * 显示各种VPN套餐供用户选择
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlansPage(
    viewModel: PlansViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    onNavigateBack: () -> Unit = {},
    onNavigateToCheckout: (String) -> Unit = {}
) {
    val state by viewModel.state.collectAsState()
    val selectedPlanId by viewModel.selectedPlanId.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("选择套餐") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        },
        bottomBar = {
            if (selectedPlanId != null) {
                BottomBar(
                    onContinue = { onNavigateToCheckout(selectedPlanId!!) }
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            // 页面说明
            Text(
                text = "选择适合您的套餐",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "所有套餐均包含7天无理由退款保障",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(24.dp))

            // 套餐列表
            when (state) {
                is PlansState.Loaded -> {
                    val plans = (state as PlansState.Loaded).plans
                    plans.forEach { plan ->
                        PlanCard(
                            plan = plan,
                            isSelected = plan.id == selectedPlanId,
                            onSelect = { viewModel.selectPlan(plan.id) }
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
                is PlansState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                else -> {}
            }

            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
private fun PlanCard(
    plan: PlanInfo,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    val borderColor = when {
        isSelected -> MaterialTheme.colorScheme.primary
        plan.isRecommended -> Color(0xFFF59E0B)
        else -> MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
    }

    val backgroundColor = when {
        isSelected -> MaterialTheme.colorScheme.primary.copy(alpha = 0.05f)
        plan.isRecommended -> Color(0xFFF59E0B).copy(alpha = 0.05f)
        else -> MaterialTheme.colorScheme.surface
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onSelect)
            .border(
                width = if (isSelected || plan.isRecommended) 2.dp else 1.dp,
                color = borderColor,
                shape = MaterialTheme.shapes.large
            ),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            // 顶部：名称和标签
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = plan.name,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    
                    if (plan.isRecommended) {
                        Spacer(modifier = Modifier.width(8.dp))
                        RecommendedBadge()
                    }
                }

                plan.badge?.let {
                    DiscountBadge(text = it)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 价格
            Row(
                verticalAlignment = Alignment.Bottom
            ) {
                Text(
                    text = plan.price,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "/${plan.duration}",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                plan.originalPrice?.let { original ->
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = original,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textDecoration = TextDecoration.LineThrough
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))

            Spacer(modifier = Modifier.height(16.dp))

            // 功能列表
            plan.features.forEach { feature ->
                FeatureItem(feature)
                Spacer(modifier = Modifier.height(8.dp))
            }

            // 选择指示器
            if (isSelected) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Selected",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun RecommendedBadge() {
    Surface(
        color = Color(0xFFF59E0B),
        shape = MaterialTheme.shapes.small
    ) {
        Text(
            text = "推荐",
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.White,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@Composable
private fun DiscountBadge(text: String) {
    Surface(
        color = MaterialTheme.colorScheme.primary,
        shape = MaterialTheme.shapes.small
    ) {
        Text(
            text = text,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@Composable
private fun FeatureItem(feature: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = Icons.Default.Check,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = feature,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun BottomBar(onContinue: () -> Unit) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 8.dp
    ) {
        Button(
            onClick = onContinue,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .height(52.dp),
            shape = MaterialTheme.shapes.medium
        ) {
            Text(
                text = "继续",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PlansPagePreview() {
    MaterialTheme {
        PlansPage()
    }
}
