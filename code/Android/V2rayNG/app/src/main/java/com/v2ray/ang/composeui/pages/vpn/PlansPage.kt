package com.v2ray.ang.composeui.pages.vpn

import android.app.Application
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.v2ray.ang.composeui.bridge.order.VpnOrderBridge
import com.v2ray.ang.composeui.theme.TextPrimary
import com.v2ray.ang.composeui.theme.TextSecondary
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class PlanInfo(
    val id: String,
    val name: String,
    val duration: String,
    val price: String,
    val originalPrice: String?,
    val isRecommended: Boolean,
    val features: List<String>,
    val badge: String? = null,
)

sealed class PlansState {
    data object Idle : PlansState()
    data object Loading : PlansState()
    data class Loaded(val plans: List<PlanInfo>, val selectedPlanId: String?) : PlansState()
    data class Error(val message: String) : PlansState()
}

class PlansViewModel(application: Application) : AndroidViewModel(application) {
    private val _state = MutableStateFlow<PlansState>(PlansState.Idle)
    val state: StateFlow<PlansState> = _state

    private val _selectedPlanId = MutableStateFlow<String?>(null)
    val selectedPlanId: StateFlow<String?> = _selectedPlanId
    private val bridge = VpnOrderBridge(application)

    init {
        loadPlans()
    }

    private fun loadPlans() {
        _state.value = PlansState.Loading
        viewModelScope.launch {
            bridge.loadPlans()
                .onSuccess { plans ->
                    val recommendedId = plans.firstOrNull { it.isRecommended }?.id ?: plans.firstOrNull()?.id
                    _state.value = PlansState.Loaded(
                        plans = plans.map {
                            PlanInfo(
                                id = it.id,
                                name = it.name,
                                duration = it.duration,
                                price = it.price,
                                originalPrice = it.originalPrice,
                                isRecommended = it.isRecommended,
                                features = it.features,
                                badge = it.badge,
                            )
                        },
                        selectedPlanId = recommendedId,
                    )
                    _selectedPlanId.value = recommendedId
                }
                .onFailure { error ->
                    _state.value = PlansState.Error(error.message ?: "加载套餐失败")
                }
        }
    }

    fun selectPlan(planId: String) {
        _selectedPlanId.value = planId
        val currentState = _state.value
        if (currentState is PlansState.Loaded) {
            _state.value = currentState.copy(selectedPlanId = planId)
        }
    }
}

@Composable
fun PlansPage(
    viewModel: PlansViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    onNavigateBack: () -> Unit = {},
    onNavigateToCheckout: (String) -> Unit = {},
) {
    val state by viewModel.state.collectAsState()
    val selectedPlanId by viewModel.selectedPlanId.collectAsState()
    val loadedState = state as? PlansState.Loaded
    val selectedPlan = loadedState?.plans?.firstOrNull { it.id == selectedPlanId }

    VpnBitgetBackground {
        Scaffold(
            containerColor = Color.Transparent,
            contentColor = TextPrimary,
            contentWindowInsets = WindowInsets.safeDrawing,
            bottomBar = {
                selectedPlan?.let { plan ->
                    Surface(
                        color = VpnSurface,
                        border = BorderStroke(1.dp, VpnOutline),
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = VpnPageHorizontalPadding, vertical = 16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = plan.name,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = TextPrimary,
                                )
                                Text(
                                    text = "${plan.price} · ${plan.duration}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextSecondary,
                                )
                            }
                            VpnPrimaryButton(
                                text = "继续下单",
                                onClick = { onNavigateToCheckout(plan.id) },
                            )
                        }
                    }
                }
            },
        ) { innerPadding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(
                    start = VpnPageHorizontalPadding,
                    end = VpnPageHorizontalPadding,
                    top = VpnPageTopPadding,
                    bottom = VpnPageBottomPadding,
                ),
                verticalArrangement = Arrangement.spacedBy(18.dp),
            ) {
                item {
                    VpnCenterTopBar(
                        title = "套餐中心",
                        onBack = onNavigateBack,
                        rightIcon = Icons.Default.HelpOutline,
                        onRightIconClick = {},
                    )
                }
                item {
                    PlansHeroBanner()
                }

                when (val current = state) {
                    is PlansState.Loading,
                    PlansState.Idle,
                    -> {
                        item {
                            VpnLoadingPanel(
                                title = "正在拉取套餐",
                                subtitle = "保留现有 plans bridge，只替换为 Bitget 风格展示。",
                            )
                        }
                    }

                    is PlansState.Error -> {
                        item {
                            VpnEmptyPanel(
                                title = "套餐暂不可用",
                                subtitle = current.message,
                            )
                        }
                    }

                    is PlansState.Loaded -> {
                        item {
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                items(current.plans.take(3), key = { it.id }) { plan ->
                                    FeaturedPlanTile(
                                        plan = plan,
                                        isSelected = plan.id == selectedPlanId,
                                        onClick = { viewModel.selectPlan(plan.id) },
                                    )
                                }
                            }
                        }
                        selectedPlan?.let { plan ->
                            item {
                                SelectedPlanPanel(plan = plan)
                            }
                        }
                        item {
                            VpnSectionHeading(
                                title = "稳定币套餐",
                                subtitle = "保留统一深色底、大圆角分组卡和亮青 CTA 层级。",
                            )
                        }
                        item {
                            VpnGlassCard(accent = VpnOutline, contentPadding = PaddingValues(vertical = 6.dp)) {
                                current.plans.forEachIndexed { index, plan ->
                                    PlanListRow(
                                        plan = plan,
                                        isSelected = plan.id == selectedPlanId,
                                        onClick = { viewModel.selectPlan(plan.id) },
                                    )
                                    if (index != current.plans.lastIndex) {
                                        VpnListDivider()
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PlansHeroBanner() {
    Surface(
        shape = RoundedCornerShape(28.dp),
        color = Color.Transparent,
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF0E2940),
                            Color(0xFF0A202B),
                            Color(0xFF0B1517),
                        ),
                    ),
                )
                .padding(20.dp),
        ) {
            Column(
                modifier = Modifier.align(Alignment.TopStart),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Text(
                    text = "随充随用",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextSecondary,
                )
                Text(
                    text = "给 VPN\n选一个更稳的周期",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                )
            }

            VpnStatusChip(
                text = "快速了解套餐权益",
                modifier = Modifier.align(Alignment.BottomStart),
                containerColor = Color(0xFF113152),
                contentColor = VpnAccent,
            )

            Surface(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .size(108.dp),
                shape = RoundedCornerShape(30.dp),
                color = VpnAccent.copy(alpha = 0.14f),
            ) {
                Box(contentAlignment = Alignment.Center) {
                    VpnCodeBadge(
                        text = "VPN",
                        modifier = Modifier.size(76.dp),
                        backgroundColor = VpnAccent.copy(alpha = 0.2f),
                        contentColor = VpnAccent,
                    )
                }
            }
        }
    }
}

@Composable
private fun FeaturedPlanTile(
    plan: PlanInfo,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    VpnGlassCard(
        modifier = Modifier
            .width(178.dp)
            .clickable(onClick = onClick),
        accent = if (isSelected) VpnAccent else VpnOutline,
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
    ) {
        if (plan.isRecommended) {
            VpnStatusChip(
                text = "HOT",
                containerColor = VpnAccentSoft,
                contentColor = VpnAccent,
            )
        }
        Text(
            text = plan.price,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
        )
        Text(
            text = plan.name,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = TextPrimary,
        )
        Text(
            text = plan.duration,
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary,
        )
    }
}

@Composable
private fun SelectedPlanPanel(plan: PlanInfo) {
    VpnGlassCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                VpnCodeBadge(text = "V", backgroundColor = VpnAccentSoft, contentColor = VpnAccent)
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = plan.name,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                    )
                    Text(
                        text = plan.duration,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                    )
                }
            }
            Text(
                text = plan.price,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = VpnAccent,
            )
        }
        VpnLineChart(values = vpnDemoLine(plan.duration.length.toFloat()))
        VpnRangeSelector(
            labels = listOf("1周", "1个月", "3个月", "1年"),
            selectedIndex = planSelectionIndex(plan),
        )
        plan.features.forEach { feature ->
            VpnLabelValueRow(
                label = "权益",
                value = feature,
            )
        }
    }
}

private fun planSelectionIndex(plan: PlanInfo): Int {
    return when {
        plan.duration.contains("12") || plan.duration.contains("1年") -> 3
        plan.duration.contains("3") -> 2
        plan.duration.contains("1") -> 1
        else -> 0
    }
}

@Composable
private fun PlanListRow(
    plan: PlanInfo,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    VpnGroupRow(
        title = plan.name,
        subtitle = plan.features.firstOrNull() ?: plan.duration,
        selected = isSelected,
        onClick = onClick,
        leading = {
            VpnCodeBadge(
                text = plan.name.take(1),
                backgroundColor = if (isSelected) VpnAccentSoft else VpnSurfaceStrong,
                contentColor = if (isSelected) VpnAccent else TextPrimary,
            )
        },
        trailing = {
            Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = plan.price,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                )
                plan.originalPrice?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                        textDecoration = TextDecoration.LineThrough,
                    )
                }
                Text(
                    text = plan.duration,
                    style = MaterialTheme.typography.bodySmall,
                    color = if (isSelected) VpnAccent else TextSecondary,
                )
            }
        },
    )
}

@Preview
@Composable
private fun PlansPagePreview() {
    MaterialTheme {
        PlansPage()
    }
}
