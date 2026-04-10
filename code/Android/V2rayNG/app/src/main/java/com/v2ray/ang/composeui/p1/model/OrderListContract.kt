package com.v2ray.ang.composeui.p1.model

import com.v2ray.ang.composeui.navigation.CryptoVpnRouteSpec
import com.v2ray.ang.composeui.navigation.RouteDefinition
import com.v2ray.ang.composeui.common.model.FeatureBullet
import com.v2ray.ang.composeui.common.model.FeatureField
import com.v2ray.ang.composeui.common.model.FeatureListItem
import com.v2ray.ang.composeui.common.model.FeatureMetric

data class OrderListUiState(
        val title: String = "订单中心",
        val subtitle: String = "ORDER LIST",
        val badge: String = "P1 · FLOW",
        val summary: String = "订单列表页聚合当前账号下的套餐订单、续费记录与历史支付。",
        val primaryActionLabel: String = "查看最新订单详情",
        val secondaryActionLabel: String? = "返回首页",
        val heroAccent: String = "order_list",
        val metrics: List<FeatureMetric> = listOf(
    FeatureMetric(label = "订单总数", value = "12"),
    FeatureMetric(label = "生效中", value = "3"),
    FeatureMetric(label = "待续费", value = "1"),
),
        val fields: List<FeatureField> = listOf(
    FeatureField(key = "search", label = "搜索订单", value = "ORD-2025", supportingText = "可按订单号或计划名过滤"),
),
        val highlights: List<FeatureListItem> = listOf(
    FeatureListItem(title = "路由标识", subtitle = "订单列表页聚合当前账号下的套餐订单、续费记录与历史支付。", trailing = "order_list", badge = "P1"),
    FeatureListItem(title = "导航参数", subtitle = "当前页面无必填导航参数", trailing = "0 个", badge = "Nav"),
    FeatureListItem(title = "表单占位", subtitle = "搜索订单", trailing = "1 项", badge = "Form"),
    FeatureListItem(title = "交付内容", subtitle = "Composable + UiState + Event + ViewModel + Mock Repository 已补齐", trailing = "Ready", badge = "Drop-in"),
),
        val checklist: List<FeatureBullet> = listOf(
    FeatureBullet(title = "ViewModel Stub", detail = "订单中心 已预留事件分发与 refresh 占位。"),
    FeatureBullet(title = "Mock Repository", detail = "可通过 OrderListPreviewState / Repository 种子替换真实接口。"),
    FeatureBullet(title = "Preview", detail = "页面已内置 @Preview，可直接在 Android Studio 查看。"),
    FeatureBullet(title = "Navigation Args", detail = "当前页面无必填参数，但已纳入统一 RouteSpec 台账。"),
),
        val note: String = "订单中心 已按 P1 页面补齐，可继续替换为真实业务逻辑与接口数据。",
    )

    sealed interface OrderListEvent {
        data object Refresh : OrderListEvent
        data object PrimaryActionClicked : OrderListEvent
        data object SecondaryActionClicked : OrderListEvent
        data class FieldChanged(
            val key: String,
            val value: String,
        ) : OrderListEvent
    }

    val orderListNavigation: RouteDefinition = CryptoVpnRouteSpec.orderList

    fun orderListPreviewState(): OrderListUiState = OrderListUiState()
