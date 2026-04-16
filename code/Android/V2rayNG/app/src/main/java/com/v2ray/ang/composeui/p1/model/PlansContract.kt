package com.v2ray.ang.composeui.p1.model

import com.v2ray.ang.composeui.navigation.CryptoVpnRouteSpec
import com.v2ray.ang.composeui.navigation.RouteDefinition

data class PlanOptionUi(
    val planCode: String,
    val title: String,
    val description: String,
    val priceText: String,
    val durationText: String,
    val maxSessionsText: String,
    val badge: String? = null,
    val paymentMethods: List<String> = emptyList(),
)

data class PlansUiState(
    val title: String = "选择你的套餐",
    val subtitle: String = "PLANS",
    val badge: String = "P1 · LIVE",
    val summary: String = "加载套餐中…",
    val primaryActionLabel: String = "继续结算",
    val secondaryActionLabel: String? = "查看可用地区",
    val heroAccent: String = "plans",
    val screenState: P1ScreenState = P1ScreenState(isLoading = true),
    val plans: List<PlanOptionUi> = emptyList(),
    val selectedPlanCode: String? = null,
    val currentPlanName: String = "套餐尚未购买",
    val currentPlanDescription: String = "购买后将在这里显示当前计划",
    val currentPlanStatusText: String = "尚未购买",
    val note: String = "",
)

sealed interface PlansEvent {
    data object Refresh : PlansEvent
    data object PrimaryActionClicked : PlansEvent
    data object SecondaryActionClicked : PlansEvent
}

val plansNavigation: RouteDefinition = CryptoVpnRouteSpec.plans

fun plansPreviewState(): PlansUiState = PlansUiState(
    summary = "",
    screenState = P1ScreenState(),
    plans = listOf(
        PlanOptionUi(
            planCode = "",
            title = "",
            description = "",
            priceText = "待接口返回",
            durationText = "待接口返回",
            maxSessionsText = "待接口返回",
            paymentMethods = listOf("待接口返回"),
        ),
        PlanOptionUi(
            planCode = "",
            title = "",
            description = "",
            priceText = "待接口返回",
            durationText = "待接口返回",
            maxSessionsText = "待接口返回",
            badge = "BLOCKED",
            paymentMethods = listOf("待接口返回"),
        ),
    ),
    selectedPlanCode = null,
    note = "",
)
