package com.v2ray.ang.composeui.pages.vpn

import android.app.Application
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.v2ray.ang.composeui.bridge.order.VpnOrderBridge
import com.v2ray.ang.composeui.theme.BackgroundSecondary
import com.v2ray.ang.composeui.theme.BorderDefault
import com.v2ray.ang.composeui.theme.GlowBlue
import com.v2ray.ang.composeui.theme.Primary
import com.v2ray.ang.composeui.theme.TextPrimary
import com.v2ray.ang.composeui.theme.TextSecondary
import com.v2ray.ang.composeui.theme.Warning
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
    object Idle : PlansState()
    object Loading : PlansState()
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
                        selectedPlanId = plans.firstOrNull { it.isRecommended }?.id,
                    )
                    _selectedPlanId.value = plans.firstOrNull { it.isRecommended }?.id
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
                if (selectedPlan != null) {
                    PlanSelectionBar(
                        plan = selectedPlan,
                        onContinue = { onNavigateToCheckout(selectedPlan.id) },
                    )
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
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                item {
                    VpnTopChrome(
                        title = "Plans",
                        subtitle = "Bitget-style package deck with strong CTA and clear pricing hierarchy.",
                        onBack = onNavigateBack,
                    )
                }
                item {
                    VpnHeroCard(
                        eyebrow = "PACKAGE DESK",
                        title = "Choose the route package that fits your traffic demand",
                        subtitle = "保留现有 plans bridge 和 order 创建逻辑，只把视觉层切成深色行情卡片式层级。",
                        accent = Warning,
                        metrics = listOf(
                            VpnHeroMetric("Guarantee", "7-Day Refund"),
                            VpnHeroMetric("Selection", "${loadedState?.plans?.size ?: 0} Plans"),
                            VpnHeroMetric("Core", "VPN First"),
                        ),
                    )
                }

                when (val current = state) {
                    is PlansState.Loading,
                    PlansState.Idle,
                    -> {
                        item {
                            VpnLoadingPanel(
                                title = "Loading package deck",
                                subtitle = "正在读取当前可售 VPN 套餐。",
                            )
                        }
                    }

                    is PlansState.Error -> {
                        item {
                            VpnEmptyPanel(
                                title = "Plans unavailable",
                                subtitle = current.message,
                            )
                        }
                    }

                    is PlansState.Loaded -> {
                        item {
                            VpnSectionHeading(
                                title = "Available Packages",
                                subtitle = "Recommended plans stay visually elevated, while the checkout bridge remains unchanged.",
                            )
                        }
                        current.plans.forEach { plan ->
                            item(plan.id) {
                                BitgetPlanCard(
                                    plan = plan,
                                    isSelected = plan.id == selectedPlanId,
                                    onSelect = { viewModel.selectPlan(plan.id) },
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun BitgetPlanCard(
    plan: PlanInfo,
    isSelected: Boolean,
    onSelect: () -> Unit,
) {
    val accent = when {
        isSelected -> Primary
        plan.isRecommended -> Warning
        else -> GlowBlue
    }
    VpnGlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onSelect),
        accent = accent,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top,
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = plan.name,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                )
                if (plan.isRecommended) {
                    VpnStatusChip(
                        text = "HOT",
                        containerColor = Warning.copy(alpha = 0.16f),
                        contentColor = Warning,
                    )
                }
            }
            if (isSelected) {
                VpnStatusChip(text = "SELECTED")
            } else if (!plan.badge.isNullOrBlank()) {
                VpnStatusChip(
                    text = plan.badge,
                    containerColor = GlowBlue.copy(alpha = 0.18f),
                    contentColor = GlowBlue,
                )
            }
        }

        Text(
            text = plan.duration,
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary,
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom,
        ) {
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = plan.price,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                )
                Text(
                    text = " / ${plan.duration}",
                    modifier = Modifier.padding(start = 6.dp, bottom = 4.dp),
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                )
            }
            plan.originalPrice?.let { original ->
                Text(
                    text = original,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary,
                    textDecoration = TextDecoration.LineThrough,
                )
            }
        }

        Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.18f))

        plan.features.forEach { feature ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Surface(
                    modifier = Modifier.width(20.dp),
                    shape = RoundedCornerShape(10.dp),
                    color = accent.copy(alpha = 0.16f),
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = accent,
                        )
                    }
                }
                Text(
                    text = feature,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary,
                )
            }
        }

        Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.18f))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector = if (plan.isRecommended) Icons.Default.LocalFireDepartment else Icons.Default.Verified,
                    contentDescription = null,
                    tint = accent,
                )
                Text(
                    text = if (plan.isRecommended) "Preferred package for quick purchase" else "Stable package for repeat orders",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                )
            }
            if (!isSelected) {
                VpnSecondaryButton(
                    text = "Choose",
                    onClick = onSelect,
                )
            }
        }
    }
}

@Composable
private fun PlanSelectionBar(
    plan: PlanInfo,
    onContinue: () -> Unit,
) {
    Surface(
        color = BackgroundSecondary.copy(alpha = 0.98f),
        border = BorderStroke(1.dp, BorderDefault.copy(alpha = 0.9f)),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = VpnPageHorizontalPadding, vertical = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(modifier = Modifier.weight(1f)) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = plan.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary,
                    )
                    Text(
                        text = "${plan.price} · ${plan.duration}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary,
                    )
                }
            }
            VpnPrimaryButton(
                text = "Continue to Checkout",
                onClick = onContinue,
            )
        }
    }
}

@Preview
@Composable
private fun PlansPagePreview() {
    MaterialTheme {
        PlansPage()
    }
}
