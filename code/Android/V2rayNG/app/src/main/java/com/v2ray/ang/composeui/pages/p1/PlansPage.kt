package com.v2ray.ang.composeui.pages.p1

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.v2ray.ang.composeui.common.model.FeatureListItem
import com.v2ray.ang.composeui.common.model.FeatureMetric
import com.v2ray.ang.composeui.navigation.CryptoVpnRouteSpec
import com.v2ray.ang.composeui.p0.ui.P01Card
import com.v2ray.ang.composeui.p0.ui.P01CardCopy
import com.v2ray.ang.composeui.p0.ui.P01CardHeader
import com.v2ray.ang.composeui.p0.ui.P01Chip
import com.v2ray.ang.composeui.p0.ui.P01Header
import com.v2ray.ang.composeui.p0.ui.P01MetricCell
import com.v2ray.ang.composeui.p0.ui.P01MetricGrid
import com.v2ray.ang.composeui.p0.ui.P01PhoneScaffold
import com.v2ray.ang.composeui.p1.model.PlansEvent
import com.v2ray.ang.composeui.p1.model.P1PlanCard
import com.v2ray.ang.composeui.p1.model.PlansUiState
import com.v2ray.ang.composeui.p1.model.plansPreviewState
import com.v2ray.ang.composeui.p1.viewmodel.PlansViewModel
import com.v2ray.ang.composeui.theme.CryptoVpnTheme

@Composable
fun PlansRoute(
    viewModel: PlansViewModel,
    onBottomNav: (String) -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsState()
    PlansScreen(
        uiState = uiState,
        onPrimaryAction = {
            viewModel.onEvent(PlansEvent.PrimaryActionClicked)
            val planCode = uiState.selectedPlanCode ?: uiState.plans.firstOrNull()?.planCode
            if (!planCode.isNullOrBlank()) {
                onBottomNav(CryptoVpnRouteSpec.orderCheckoutRoute(planCode))
            }
        },
        onSecondaryAction = {
            viewModel.onEvent(PlansEvent.SecondaryActionClicked)
            onBottomNav(CryptoVpnRouteSpec.regionSelection.pattern)
        },
        onBottomNav = onBottomNav,
    )
}

@Composable
fun PlansScreen(
    uiState: PlansUiState,
    onPrimaryAction: () -> Unit,
    onSecondaryAction: () -> Unit,
    onBottomNav: (String) -> Unit = {},
) {
    val cards = rememberPlans(uiState)
    var selectedPlanIndex by rememberSaveable { mutableIntStateOf(cards.indexOfFirst { it.featured }.coerceAtLeast(0)) }
    val selectedPlan = cards.getOrElse(selectedPlanIndex) { cards.first() }

    P01PhoneScaffold(
        statusTime = "18:08",
        currentRoute = CryptoVpnRouteSpec.plans.name,
        onBottomNav = onBottomNav,
    ) {
        P01Header(
            eyebrow = "SUBSCRIPTION",
            title = "购买你的套餐",
            subtitle = "VPN 是核心服务，钱包是支付与资产层。这里把它们真正融合。",
            chips = listOf("支持钱包直付"),
            trailing = { P1SecureHub(label = plansHubLabel(selectedPlan.title)) },
        )

        P01Card {
            P01CardHeader(
                title = "当前状态",
                trailing = { P01Chip(text = "实时套餐") },
            )
            P01MetricGrid(
                items = listOf(
                    P01MetricCell("当前计划", cards.getOrNull(selectedPlanIndex)?.title ?: "Pro 月付"),
                    P01MetricCell("支付资产", "USDT"),
                ),
            )
            P01CardCopy(uiState.summary)
        }

        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            cards.forEachIndexed { index, card ->
                val isSelected = index == selectedPlanIndex
                P1SelectableCard(
                    selected = isSelected,
                    modifier = Modifier.clickable { selectedPlanIndex = index },
                ) {
                    P01CardHeader(
                        title = card.title,
                        trailing = {
                            P01Chip(
                                text = if (isSelected && !card.featured) "已选择" else card.badge,
                                highlighted = isSelected || card.featured,
                            )
                        },
                    )
                    if (card.featured || isSelected) {
                        androidx.compose.material3.Text(
                            text = card.price,
                            color = if (isSelected) Color(0xFF4276FF) else Color(0xFF132748),
                            modifier = Modifier.padding(top = 2.dp),
                        )
                    }
                    P01CardCopy(card.copy)
                    androidx.compose.foundation.layout.FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        card.tags.forEach { tag ->
                            P01Chip(
                                text = tag,
                                highlighted = isSelected || card.featured,
                            )
                        }
                    }
                }
            }
        }

        P1PrimaryCta(
            text = "使用钱包支付 · ${selectedPlan.title}",
            onClick = onPrimaryAction,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

private fun plansHubLabel(title: String): String = when {
    title.contains("年", ignoreCase = true) || title.contains("Pro", ignoreCase = true) -> "PRO"
    title.contains("月", ignoreCase = true) -> "MONTH"
    title.contains("团队", ignoreCase = true) || title.contains("team", ignoreCase = true) -> "TEAM"
    else -> "PLANS"
}

private data class PlanCardUi(
    val title: String,
    val price: String,
    val copy: String,
    val badge: String,
    val tags: List<String>,
    val featured: Boolean,
)

private fun rememberPlans(uiState: PlansUiState): List<PlanCardUi> {
    return uiState.plans.map {
        PlanCardUi(
            title = it.title,
            price = it.priceText,
            copy = it.subtitle,
            badge = it.badge.ifBlank { it.priceText },
            tags = it.tags,
            featured = it.featured,
        )
    }
}

@Preview(showBackground = true, widthDp = 393, heightDp = 852)
@Composable
private fun PlansPreview() {
    CryptoVpnTheme {
        PlansScreen(
            uiState = plansPreviewState(),
            onPrimaryAction = {},
            onSecondaryAction = {},
        )
    }
}
