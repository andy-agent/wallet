package com.v2ray.ang.composeui.p1.model

import com.v2ray.ang.composeui.navigation.CryptoVpnRouteSpec
import com.v2ray.ang.composeui.navigation.RouteDefinition
import com.v2ray.ang.composeui.common.model.FeatureBullet
import com.v2ray.ang.composeui.common.model.FeatureField
import com.v2ray.ang.composeui.common.model.FeatureListItem
import com.v2ray.ang.composeui.common.model.FeatureMetric

data class OrderCheckoutRouteArgs(val planId: String = "annual_pro")

data class OrderCheckoutUiState(
        val title: String = "订单收银台",
        val subtitle: String = "ORDER CHECKOUT",
        val badge: String = "P1 · FLOW",
        val summary: String = "订单确认与支付前置页，承接套餐、支付方式与开票信息确认。",
        val primaryActionLabel: String = "前往钱包支付确认",
        val secondaryActionLabel: String? = "切换支付方式",
        val heroAccent: String = "order_checkout",
        val metrics: List<FeatureMetric> = listOf(
    FeatureMetric(label = "套餐", value = "年费 Pro"),
    FeatureMetric(label = "金额", value = "149 USDT"),
    FeatureMetric(label = "网络", value = "TRON"),
),
        val fields: List<FeatureField> = listOf(
    FeatureField(key = "invoice", label = "账单邮箱", value = "billing@cryptovpn.app", supportingText = "用于发送支付凭证"),
    FeatureField(key = "remark", label = "订单备注", value = "", supportingText = "给运营或风控的附加说明"),
),
        val highlights: List<FeatureListItem> = listOf(
    FeatureListItem(title = "路由标识", subtitle = "订单确认与支付前置页，承接套餐、支付方式与开票信息确认。", trailing = "order_checkout", badge = "P1"),
    FeatureListItem(title = "导航参数", subtitle = "planId", trailing = "1 个", badge = "Nav"),
    FeatureListItem(title = "表单占位", subtitle = "账单邮箱、订单备注", trailing = "2 项", badge = "Form"),
    FeatureListItem(title = "交付内容", subtitle = "Composable + UiState + Event + ViewModel + Mock Repository 已补齐", trailing = "Ready", badge = "Drop-in"),
),
        val checklist: List<FeatureBullet> = listOf(
    FeatureBullet(title = "ViewModel Stub", detail = "订单收银台 已预留事件分发与 refresh 占位。"),
    FeatureBullet(title = "Mock Repository", detail = "可通过 OrderCheckoutPreviewState / Repository 种子替换真实接口。"),
    FeatureBullet(title = "Preview", detail = "页面已内置 @Preview，可直接在 Android Studio 查看。"),
    FeatureBullet(title = "Navigation Args", detail = "createRoute builder 与 NavGraph 参数解析已补齐。"),
),
        val note: String = "订单收银台 已按 P1 页面补齐，可继续替换为真实业务逻辑与接口数据。",
    )

    sealed interface OrderCheckoutEvent {
        data object Refresh : OrderCheckoutEvent
        data object PrimaryActionClicked : OrderCheckoutEvent
        data object SecondaryActionClicked : OrderCheckoutEvent
        data class FieldChanged(
            val key: String,
            val value: String,
        ) : OrderCheckoutEvent
    }

    val orderCheckoutNavigation: RouteDefinition = CryptoVpnRouteSpec.orderCheckout

    fun orderCheckoutPreviewState(): OrderCheckoutUiState = OrderCheckoutUiState()
