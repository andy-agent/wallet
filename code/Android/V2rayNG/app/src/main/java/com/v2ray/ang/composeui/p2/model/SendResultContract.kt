package com.v2ray.ang.composeui.p2.model

import com.v2ray.ang.composeui.navigation.CryptoVpnRouteSpec
import com.v2ray.ang.composeui.navigation.RouteDefinition
import com.v2ray.ang.composeui.common.model.FeatureBullet
import com.v2ray.ang.composeui.common.model.FeatureField
import com.v2ray.ang.composeui.common.model.FeatureListItem
import com.v2ray.ang.composeui.common.model.FeatureMetric

data class SendResultRouteArgs(val txId: String = "TX-9F32")

data class SendResultUiState(
        val title: String = "发送完成",
        val subtitle: String = "TRANSFER RESULT",
        val badge: String = "• 广播成功",
        val summary: String = "交易已广播到链上，可在资产详情中继续追踪确认状态。",
        val primaryActionLabel: String? = "返回钱包首页",
        val secondaryActionLabel: String? = "查看交易哈希",
        val heroAccent: String = "send_result",
        val metrics: List<FeatureMetric> = listOf(
    FeatureMetric(label = "转账", value = "12,500 USDT"),
    FeatureMetric(label = "网络", value = "TRON 网络 · 预计 1 个确认"),
    FeatureMetric(label = "收款地址", value = "TOX...kJ8V"),
    FeatureMetric(label = "矿工费", value = "1.24 USDT"),
    FeatureMetric(label = "手续费", value = "1.24 USDT"),
),
        val fields: List<FeatureField> = emptyList(),
        val highlights: List<FeatureListItem> = listOf(
    FeatureListItem(title = "状态", subtitle = "等待区块确认", trailing = "1 block"),
    FeatureListItem(title = "交易哈希", subtitle = "9A2F...C912", trailing = "链上"),
    FeatureListItem(title = "说明", subtitle = "手续费与到账地址已经锁定", trailing = "已锁定"),
),
        val checklist: List<FeatureBullet> = listOf(
    FeatureBullet(title = "状态", detail = "广播成功"),
    FeatureBullet(title = "确认", detail = "等待区块确认"),
    FeatureBullet(title = "追踪", detail = "可在资产详情持续查看"),
    FeatureBullet(title = "费用", detail = "1.24 USDT"),
),
        val note: String = "链上确认完成后余额会自动同步。",
        val isLoading: Boolean = false,
        val errorMessage: String? = null,
        val emptyMessage: String? = null,
        val blockerTitle: String? = null,
        val blockerMessage: String? = null,
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
