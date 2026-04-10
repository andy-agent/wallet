package com.cryptovpn.ui.p1.model

import com.cryptovpn.navigation.CryptoVpnRouteSpec
import com.cryptovpn.navigation.RouteDefinition
import com.cryptovpn.ui.common.model.FeatureBullet
import com.cryptovpn.ui.common.model.FeatureField
import com.cryptovpn.ui.common.model.FeatureListItem
import com.cryptovpn.ui.common.model.FeatureMetric

data class OrderResultRouteArgs(val orderId: String = "ORD-2025-0001")

data class OrderResultUiState(
        val title: String = "订单已生效",
        val subtitle: String = "ORDER RESULT",
        val badge: String = "P1 · FLOW",
        val summary: String = "订单结果页用于展示开通结果、到期时间与下一步跳转入口。",
        val primaryActionLabel: String = "查看订单中心",
        val secondaryActionLabel: String? = "返回首页",
        val heroAccent: String = "order_result",
        val metrics: List<FeatureMetric> = listOf(
    FeatureMetric(label = "状态", value = "已生效"),
    FeatureMetric(label = "剩余时长", value = "365 天"),
    FeatureMetric(label = "节点权限", value = "97 / 100"),
),
        val fields: List<FeatureField> = emptyList(),
        val highlights: List<FeatureListItem> = listOf(
    FeatureListItem(title = "路由标识", subtitle = "订单结果页用于展示开通结果、到期时间与下一步跳转入口。", trailing = "order_result", badge = "P1"),
    FeatureListItem(title = "导航参数", subtitle = "orderId", trailing = "1 个", badge = "Nav"),
    FeatureListItem(title = "表单占位", subtitle = "当前页面以信息展示与确认动作为主", trailing = "0 项", badge = "Info"),
    FeatureListItem(title = "交付内容", subtitle = "Composable + UiState + Event + ViewModel + Mock Repository 已补齐", trailing = "Ready", badge = "Drop-in"),
),
        val checklist: List<FeatureBullet> = listOf(
    FeatureBullet(title = "ViewModel Stub", detail = "订单已生效 已预留事件分发与 refresh 占位。"),
    FeatureBullet(title = "Mock Repository", detail = "可通过 OrderResultPreviewState / Repository 种子替换真实接口。"),
    FeatureBullet(title = "Preview", detail = "页面已内置 @Preview，可直接在 Android Studio 查看。"),
    FeatureBullet(title = "Navigation Args", detail = "createRoute builder 与 NavGraph 参数解析已补齐。"),
),
        val note: String = "订单已生效 已按 P1 页面补齐，可继续替换为真实业务逻辑与接口数据。",
    )

    sealed interface OrderResultEvent {
        data object Refresh : OrderResultEvent
        data object PrimaryActionClicked : OrderResultEvent
        data object SecondaryActionClicked : OrderResultEvent
        data class FieldChanged(
            val key: String,
            val value: String,
        ) : OrderResultEvent
    }

    val orderResultNavigation: RouteDefinition = CryptoVpnRouteSpec.orderResult

    fun orderResultPreviewState(): OrderResultUiState = OrderResultUiState()
