package com.v2ray.ang.composeui.pages.p1

import android.content.Intent
import androidx.compose.ui.platform.LocalContext
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
import com.v2ray.ang.composeui.p0.ui.P01PrimaryButton
import com.v2ray.ang.composeui.p1.model.PlansEvent
import com.v2ray.ang.composeui.p1.model.PlansUiState
import com.v2ray.ang.composeui.p1.model.plansPreviewState
import com.v2ray.ang.composeui.p1.viewmodel.PlansViewModel
import com.v2ray.ang.composeui.theme.CryptoVpnTheme
import com.v2ray.ang.plans.PlansActivity

@Composable
fun PlansRoute(
    viewModel: PlansViewModel,
    onPrimaryAction: () -> Unit = {},
    onSecondaryAction: (() -> Unit)? = null,
    onBottomNav: (String) -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    PlansScreen(
        uiState = uiState,
        onPrimaryAction = {
            viewModel.onEvent(PlansEvent.PrimaryActionClicked)
            context.startActivity(Intent(context, PlansActivity::class.java))
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
    onPrimaryAction: () -> Unit,
    onSecondaryAction: () -> Unit,
    onBottomNav: (String) -> Unit = {},
) {
    val cards = rememberPlans(uiState)
    var selectedPlanIndex by rememberSaveable { mutableIntStateOf(cards.indexOfFirst { it.featured }.coerceAtLeast(0)) }

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
                P01Card(
                    modifier = Modifier
                        .clickable { selectedPlanIndex = index }
                        .then(
                            if (index == selectedPlanIndex) {
                                Modifier
                            } else {
                                Modifier
                            }
                        ),
                ) {
                    P01CardHeader(
                        title = card.title,
                        trailing = {
                            P01Chip(text = card.badge)
                        },
                    )
                    if (card.featured) {
                        androidx.compose.material3.Text(
                            text = card.price,
                            color = Color(0xFF132748),
                            modifier = Modifier.padding(top = 2.dp),
                        )
                    }
                    P01CardCopy(card.copy)
                    androidx.compose.foundation.layout.FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        card.tags.forEach { tag -> P01Chip(text = tag) }
                    }
                }
            }
        }

        P01PrimaryButton(
            text = "使用钱包支付并开通",
            onClick = onPrimaryAction,
            modifier = Modifier.fillMaxWidth(),
        )
    }
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
    val metrics = uiState.metrics.ifEmpty {
        listOf(
            FeatureMetric("月费", "US$8.90"),
            FeatureMetric("年费", "US$58.00"),
            FeatureMetric("团队版", "US$149.00"),
        )
    }
    val highlights = uiState.highlights.ifEmpty {
        listOf(
            FeatureListItem("月度", "适合首次体验，按月续费。", "$8.90", "SOL"),
            FeatureListItem("年度 Pro", "节省 46%，开放高速专线与隐私路由。", "$58.00", "最受欢迎"),
            FeatureListItem("团队版", "最多 5 台设备，适合小团队与多端钱包使用。", "$149.00", "TEAM"),
        )
    }
    return listOf(
        PlanCardUi(
            title = highlights.getOrNull(0)?.title ?: "月度",
            price = metrics.getOrNull(0)?.value ?: "$8.90",
            copy = highlights.getOrNull(0)?.subtitle ?: "适合首次体验，按月续费。",
            badge = metrics.getOrNull(0)?.value ?: "$8.90",
            tags = listOf("SOL", "USDT-SOL", "USDT-TRON"),
            featured = false,
        ),
        PlanCardUi(
            title = highlights.getOrNull(1)?.title ?: "年度 Pro",
            price = metrics.getOrNull(1)?.value ?: "$58.00",
            copy = highlights.getOrNull(1)?.subtitle ?: "节省 46%，开放高速专线与隐私路由。",
            badge = highlights.getOrNull(1)?.badge ?: "最受欢迎",
            tags = listOf("高速专线", "智能切区", "钱包直付"),
            featured = true,
        ),
        PlanCardUi(
            title = highlights.getOrNull(2)?.title ?: "团队版",
            price = metrics.getOrNull(2)?.value ?: "$149.00",
            copy = highlights.getOrNull(2)?.subtitle ?: "最多 5 台设备，适合小团队与多端钱包使用。",
            badge = metrics.getOrNull(2)?.value ?: "$149.00",
            tags = emptyList(),
            featured = false,
        ),
    )
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
