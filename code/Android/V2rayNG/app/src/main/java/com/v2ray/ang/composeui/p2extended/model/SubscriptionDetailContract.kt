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
        val summary: String = "当前订阅详情待接口返回。",
        val primaryActionLabel: String = "查看套餐",
        val secondaryActionLabel: String? = "查看提醒",
        val heroAccent: String = "subscription_detail",
        val metrics: List<FeatureMetric> = listOf(
    FeatureMetric(label = "当前计划", value = "待接口返回"),
    FeatureMetric(label = "剩余时间", value = "待接口返回"),
    FeatureMetric(label = "自动续费", value = "待接口返回"),
),
        val fields: List<FeatureField> = emptyList(),
        val highlights: List<FeatureListItem> = listOf(
    FeatureListItem(title = "订阅状态", subtitle = "当前订阅信息尚未同步到页面", trailing = "待同步", badge = "Runtime"),
    FeatureListItem(title = "续费状态", subtitle = "自动续费与扣费周期待接口返回", trailing = "待接口返回", badge = "Billing"),
    FeatureListItem(title = "节点权益", subtitle = "线路权益与可用节点待接口返回", trailing = "待接口返回", badge = "Access"),
),
        val checklist: List<FeatureBullet> = listOf(
    FeatureBullet(title = "数据来源", detail = "订阅编号、周期和续费状态应以后端返回为准。"),
    FeatureBullet(title = "空态策略", detail = "接口未返回前显示待接口返回，不展示演示套餐信息。"),
    FeatureBullet(title = "异常处理", detail = "接口失败时应展示真实错误，不回退为假数据。"),
    FeatureBullet(title = "参数约束", detail = "subscriptionId 仅用于查询真实订阅详情。"),
),
        val note: String = "订阅详情能力已接入路由，当前等待真实订阅数据返回。",
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
