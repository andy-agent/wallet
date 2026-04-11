package com.v2ray.ang.composeui.p1.model

import com.v2ray.ang.composeui.navigation.CryptoVpnRouteSpec
import com.v2ray.ang.composeui.navigation.RouteDefinition
import com.v2ray.ang.composeui.common.model.FeatureBullet
import com.v2ray.ang.composeui.common.model.FeatureField
import com.v2ray.ang.composeui.common.model.FeatureListItem
import com.v2ray.ang.composeui.common.model.FeatureMetric

data class PlansUiState(
    val title: String = "购买你的套餐",
    val subtitle: String = "PLANS",
    val badge: String = "P1 · REAL",
    val summary: String = "套餐页直接读取真实 plan 列表，并将选中的真实 planCode 带入下单页。",
    val primaryActionLabel: String = "继续到支付",
    val secondaryActionLabel: String? = "查看可用区域",
    val heroAccent: String = "plans",
    val stateInfo: P1StateInfo = P1StateInfo(),
    val metrics: List<FeatureMetric> = emptyList(),
    val fields: List<FeatureField> = emptyList(),
    val highlights: List<FeatureListItem> = emptyList(),
    val plans: List<P1PlanCard> = emptyList(),
    val selectedPlanCode: String? = null,
    val checklist: List<FeatureBullet> = emptyList(),
    val note: String = "",
)

    sealed interface PlansEvent {
        data object Refresh : PlansEvent
        data object PrimaryActionClicked : PlansEvent
        data object SecondaryActionClicked : PlansEvent
        data class FieldChanged(
            val key: String,
            val value: String,
        ) : PlansEvent
    }

    val plansNavigation: RouteDefinition = CryptoVpnRouteSpec.plans

    fun plansPreviewState(): PlansUiState = PlansUiState(
        stateInfo = P1StateInfo(P1ScreenState.Content),
        metrics = listOf(
            FeatureMetric("套餐数量", "3"),
            FeatureMetric("默认支付", "USDT / SOLANA"),
            FeatureMetric("真实数据", "Preview"),
        ),
        plans = listOf(
            P1PlanCard("basic_1m", "月卡", "US$9.99", "适合先体验再续费", "LIVE", listOf("1个月", "USDT", "SOLANA")),
            P1PlanCard("basic_12m", "年卡", "US$58.00", "高速节点与长期订阅", "HOT", listOf("12个月", "USDT", "SOLANA"), featured = true),
        ),
        selectedPlanCode = "basic_12m",
    )
