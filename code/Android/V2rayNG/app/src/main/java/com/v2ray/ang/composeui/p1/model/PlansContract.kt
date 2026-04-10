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
        val badge: String = "P1 · FLOW",
        val summary: String = "套餐页展示月付、年付与终身档位，为 VPN 续费与首次购买提供入口。",
        val primaryActionLabel: String = "选择年费 Pro",
        val secondaryActionLabel: String? = "查看节点能力",
        val heroAccent: String = "plans",
        val metrics: List<FeatureMetric> = listOf(
    FeatureMetric(label = "月费", value = "US$8.90"),
    FeatureMetric(label = "年费", value = "US$58.00"),
    FeatureMetric(label = "支付网络", value = "USDT / TRON"),
),
        val fields: List<FeatureField> = emptyList(),
        val highlights: List<FeatureListItem> = listOf(
    FeatureListItem(title = "路由标识", subtitle = "套餐页展示月付、年付与终身档位，为 VPN 续费与首次购买提供入口。", trailing = "plans", badge = "P1"),
    FeatureListItem(title = "导航参数", subtitle = "当前页面无必填导航参数", trailing = "0 个", badge = "Nav"),
    FeatureListItem(title = "表单占位", subtitle = "当前页面以信息展示与确认动作为主", trailing = "0 项", badge = "Info"),
    FeatureListItem(title = "交付内容", subtitle = "Composable + UiState + Event + ViewModel + Mock Repository 已补齐", trailing = "Ready", badge = "Drop-in"),
),
        val checklist: List<FeatureBullet> = listOf(
    FeatureBullet(title = "ViewModel Stub", detail = "购买你的套餐 已预留事件分发与 refresh 占位。"),
    FeatureBullet(title = "Mock Repository", detail = "可通过 PlansPreviewState / Repository 种子替换真实接口。"),
    FeatureBullet(title = "Preview", detail = "页面已内置 @Preview，可直接在 Android Studio 查看。"),
    FeatureBullet(title = "Navigation Args", detail = "当前页面无必填参数，但已纳入统一 RouteSpec 台账。"),
),
        val note: String = "购买你的套餐 已按 P1 页面补齐，可继续替换为真实业务逻辑与接口数据。",
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

    fun plansPreviewState(): PlansUiState = PlansUiState()
