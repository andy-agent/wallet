package com.v2ray.ang.composeui.p2.model

import com.v2ray.ang.composeui.navigation.CryptoVpnRouteSpec
import com.v2ray.ang.composeui.navigation.RouteDefinition
import com.v2ray.ang.composeui.common.model.FeatureBullet
import com.v2ray.ang.composeui.common.model.FeatureField
import com.v2ray.ang.composeui.common.model.FeatureListItem
import com.v2ray.ang.composeui.common.model.FeatureMetric

data class SendResultRouteArgs(val txId: String = "")

data class SendResultUiState(
        val title: String = "发送完成",
        val subtitle: String = "TRANSFER RESULT",
        val badge: String = "• 待同步",
        val summary: String = "等待真实发送结果同步。",
        val primaryActionLabel: String = "返回钱包首页",
        val secondaryActionLabel: String? = "查看交易哈希",
        val heroAccent: String = "send_result",
        val metrics: List<FeatureMetric> = listOf(
    FeatureMetric(label = "转账", value = "待接口返回"),
    FeatureMetric(label = "网络", value = "待接口返回"),
    FeatureMetric(label = "收款地址", value = "待接口返回"),
    FeatureMetric(label = "矿工费", value = "待链上返回"),
    FeatureMetric(label = "手续费", value = "待链上返回"),
),
        val fields: List<FeatureField> = emptyList(),
        val highlights: List<FeatureListItem> = listOf(
    FeatureListItem(title = "状态", subtitle = "等待真实广播结果", trailing = "待同步"),
    FeatureListItem(title = "交易哈希", subtitle = "待接口返回", trailing = "链上"),
    FeatureListItem(title = "说明", subtitle = "真实手续费与到账地址以链上结果为准", trailing = "待同步"),
),
        val checklist: List<FeatureBullet> = listOf(
    FeatureBullet(title = "状态", detail = "等待真实发送状态返回。"),
    FeatureBullet(title = "确认", detail = "确认数由链上返回后再展示。"),
    FeatureBullet(title = "追踪", detail = "可在资产详情持续查看真实交易状态。"),
    FeatureBullet(title = "费用", detail = "网络费与手续费未返回时不得展示演示值。"),
),
        val note: String = "未同步到真实交易结果前，仅显示空态或阻塞态。",
        val banner: P2SurfaceBanner = p2ReadyBanner(),
        val feedbackMessage: String? = null,
    )

    sealed interface SendResultEvent {
        data object Refresh : SendResultEvent
        data object PrimaryActionClicked : SendResultEvent
        data object SecondaryActionClicked : SendResultEvent
        data class FieldChanged(
            val key: String,
            val value: String,
        ) : SendResultEvent
    }

    val sendResultNavigation: RouteDefinition = CryptoVpnRouteSpec.sendResult

    fun sendResultPreviewState(): SendResultUiState = SendResultUiState()
