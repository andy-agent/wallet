package com.v2ray.ang.composeui.p2extended.model

import com.v2ray.ang.composeui.navigation.CryptoVpnRouteSpec
import com.v2ray.ang.composeui.navigation.RouteDefinition
import com.v2ray.ang.composeui.common.model.FeatureBullet
import com.v2ray.ang.composeui.common.model.FeatureField
import com.v2ray.ang.composeui.common.model.FeatureListItem
import com.v2ray.ang.composeui.common.model.FeatureMetric

data class SubscriptionDetailRouteArgs(val subscriptionId: String = "pro_mesh_30d")

data class SubscriptionDetailUiState(
        val title: String = "订阅详情",
        val subtitle: String = "SUBSCRIPTION DETAIL",
        val badge: String = "P2 · EXTENDED",
        val summary: String = "订阅详情页展示当前计划、剩余时长、自动续费与节点权益。",
        val primaryActionLabel: String = "前往套餐页",
        val secondaryActionLabel: String? = "查看到期提醒",
        val heroAccent: String = "subscription_detail",
        val metrics: List<FeatureMetric> = listOf(
    FeatureMetric(label = "当前计划", value = "Pro Mesh 30D"),
    FeatureMetric(label = "剩余时间", value = "18 天"),
    FeatureMetric(label = "自动续费", value = "开启"),
),
        val fields: List<FeatureField> = emptyList(),
        val highlights: List<FeatureListItem> = listOf(
    FeatureListItem(title = "路由标识", subtitle = "订阅详情页展示当前计划、剩余时长、自动续费与节点权益。", trailing = "subscription_detail", badge = "P2 扩展页"),
    FeatureListItem(title = "导航参数", subtitle = "subscriptionId", trailing = "1 个", badge = "Nav"),
    FeatureListItem(title = "表单占位", subtitle = "当前页面以信息展示与确认动作为主", trailing = "0 项", badge = "Info"),
    FeatureListItem(title = "交付内容", subtitle = "Composable + UiState + Event + ViewModel + Mock Repository 已补齐", trailing = "Ready", badge = "Drop-in"),
),
        val checklist: List<FeatureBullet> = listOf(
    FeatureBullet(title = "ViewModel Stub", detail = "订阅详情 已预留事件分发与 refresh 占位。"),
    FeatureBullet(title = "Mock Repository", detail = "可通过 SubscriptionDetailPreviewState / Repository 种子替换真实接口。"),
    FeatureBullet(title = "Preview", detail = "页面已内置 @Preview，可直接在 Android Studio 查看。"),
    FeatureBullet(title = "Navigation Args", detail = "createRoute builder 与 NavGraph 参数解析已补齐。"),
),
        val note: String = "订阅详情 已按 P2 扩展页 页面补齐，可继续替换为真实业务逻辑与接口数据。",
    )

    sealed interface SubscriptionDetailEvent {
        data object Refresh : SubscriptionDetailEvent
        data object PrimaryActionClicked : SubscriptionDetailEvent
        data object SecondaryActionClicked : SubscriptionDetailEvent
        data class FieldChanged(
            val key: String,
            val value: String,
        ) : SubscriptionDetailEvent
    }

    val subscriptionDetailNavigation: RouteDefinition = CryptoVpnRouteSpec.subscriptionDetail

    fun subscriptionDetailPreviewState(): SubscriptionDetailUiState = SubscriptionDetailUiState()
