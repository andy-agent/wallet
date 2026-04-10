package com.v2ray.ang.composeui.p1.model

import com.v2ray.ang.composeui.navigation.CryptoVpnRouteSpec
import com.v2ray.ang.composeui.navigation.RouteDefinition
import com.v2ray.ang.composeui.common.model.FeatureBullet
import com.v2ray.ang.composeui.common.model.FeatureField
import com.v2ray.ang.composeui.common.model.FeatureListItem
import com.v2ray.ang.composeui.common.model.FeatureMetric

data class OrderDetailRouteArgs(val orderId: String = "ORD-2025-0001")

data class OrderDetailUiState(
        val title: String = "订单详情",
        val subtitle: String = "ORDER DETAIL",
        val badge: String = "P1 · FLOW",
        val summary: String = "展示单笔订单的支付信息、计划权益、开通时间与后续操作入口。",
        val primaryActionLabel: String = "返回订单中心",
        val secondaryActionLabel: String? = "重新购买同款",
        val heroAccent: String = "order_detail",
        val metrics: List<FeatureMetric> = listOf(
    FeatureMetric(label = "订单金额", value = "149 USDT"),
    FeatureMetric(label = "状态", value = "已支付"),
    FeatureMetric(label = "计费周期", value = "年费"),
),
        val fields: List<FeatureField> = emptyList(),
        val highlights: List<FeatureListItem> = listOf(
    FeatureListItem(title = "路由标识", subtitle = "展示单笔订单的支付信息、计划权益、开通时间与后续操作入口。", trailing = "order_detail", badge = "P1"),
    FeatureListItem(title = "导航参数", subtitle = "orderId", trailing = "1 个", badge = "Nav"),
    FeatureListItem(title = "表单占位", subtitle = "当前页面以信息展示与确认动作为主", trailing = "0 项", badge = "Info"),
    FeatureListItem(title = "交付内容", subtitle = "Composable + UiState + Event + ViewModel + Mock Repository 已补齐", trailing = "Ready", badge = "Drop-in"),
),
        val checklist: List<FeatureBullet> = listOf(
    FeatureBullet(title = "ViewModel Stub", detail = "订单详情 已预留事件分发与 refresh 占位。"),
    FeatureBullet(title = "Mock Repository", detail = "可通过 OrderDetailPreviewState / Repository 种子替换真实接口。"),
    FeatureBullet(title = "Preview", detail = "页面已内置 @Preview，可直接在 Android Studio 查看。"),
    FeatureBullet(title = "Navigation Args", detail = "createRoute builder 与 NavGraph 参数解析已补齐。"),
),
        val note: String = "订单详情 已按 P1 页面补齐，可继续替换为真实业务逻辑与接口数据。",
    )

    sealed interface OrderDetailEvent {
        data object Refresh : OrderDetailEvent
        data object PrimaryActionClicked : OrderDetailEvent
        data object SecondaryActionClicked : OrderDetailEvent
        data class FieldChanged(
            val key: String,
            val value: String,
        ) : OrderDetailEvent
    }

    val orderDetailNavigation: RouteDefinition = CryptoVpnRouteSpec.orderDetail

    fun orderDetailPreviewState(): OrderDetailUiState = OrderDetailUiState()
