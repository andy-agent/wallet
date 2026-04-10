package com.v2ray.ang.composeui.p2.model

import com.v2ray.ang.composeui.navigation.CryptoVpnRouteSpec
import com.v2ray.ang.composeui.navigation.RouteDefinition
import com.v2ray.ang.composeui.common.model.FeatureBullet
import com.v2ray.ang.composeui.common.model.FeatureField
import com.v2ray.ang.composeui.common.model.FeatureListItem
import com.v2ray.ang.composeui.common.model.FeatureMetric

data class ReceiveRouteArgs(val assetId: String = "USDT", val chainId: String = "tron")

data class ReceiveUiState(
        val title: String = "收款",
        val subtitle: String = "RECEIVE ASSET",
        val badge: String = "USDT · TRON",
        val summary: String = "支持链切换、地址复制、二维码分享与 Memo 提醒。",
        val primaryActionLabel: String = "复制地址",
        val secondaryActionLabel: String? = "分享二维码",
        val heroAccent: String = "receive",
        val metrics: List<FeatureMetric> = listOf(
    FeatureMetric(label = "链", value = "USDT · TRON"),
    FeatureMetric(label = "备选", value = "USDT · Solana"),
    FeatureMetric(label = "备选", value = "SOL"),
    FeatureMetric(label = "校验状态", value = "已验证"),
),
        val fields: List<FeatureField> = listOf(
    FeatureField(key = "address", label = "收款地址", value = "TQ2xP9v7m5aE2sH1cV4Z9Q6wB8Lk3N5xY7", supportingText = "请确认链一致"),
),
        val highlights: List<FeatureListItem> = listOf(
    FeatureListItem(title = "收款地址", subtitle = "TQ2xP9v7m5aE2sH1cV4Z9Q6wB8Lk3N5xY7", trailing = "已校验"),
    FeatureListItem(title = "操作", subtitle = "复制地址", trailing = "执行"),
    FeatureListItem(title = "操作", subtitle = "分享二维码", trailing = "执行"),
    FeatureListItem(title = "提醒", subtitle = "错误链充值可能造成资产丢失", trailing = "风险"),
),
        val checklist: List<FeatureBullet> = listOf(
    FeatureBullet(title = "地址", detail = "TQ2xP9v7m5aE2sH1cV4Z9Q6wB8Lk3N5xY7"),
    FeatureBullet(title = "网络", detail = "USDT · TRON"),
    FeatureBullet(title = "备选链", detail = "USDT · Solana / SOL"),
    FeatureBullet(title = "提示", detail = "请确认链一致"),
),
        val note: String = "错误链充值可能造成资产丢失",
    )

    sealed interface ReceiveEvent {
        data object Refresh : ReceiveEvent
        data object PrimaryActionClicked : ReceiveEvent
        data object SecondaryActionClicked : ReceiveEvent
        data class FieldChanged(
            val key: String,
            val value: String,
        ) : ReceiveEvent
    }

    val receiveNavigation: RouteDefinition = CryptoVpnRouteSpec.receive

    fun receivePreviewState(): ReceiveUiState = ReceiveUiState()
