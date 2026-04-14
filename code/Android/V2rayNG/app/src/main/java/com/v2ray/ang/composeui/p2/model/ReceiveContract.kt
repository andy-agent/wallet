package com.v2ray.ang.composeui.p2.model

import com.v2ray.ang.composeui.navigation.CryptoVpnRouteSpec
import com.v2ray.ang.composeui.navigation.RouteDefinition
import com.v2ray.ang.composeui.common.model.FeatureBullet
import com.v2ray.ang.composeui.common.model.FeatureField
import com.v2ray.ang.composeui.common.model.FeatureListItem
import com.v2ray.ang.composeui.common.model.FeatureMetric

data class ReceiveRouteArgs(val assetId: String = "USDT", val chainId: String = "tron")

data class ReceiveVariantUi(
    val assetId: String,
    val chainId: String,
    val label: String,
    val selected: Boolean = false,
)

data class ReceiveUiState(
        val title: String = "收款",
        val subtitle: String = "RECEIVE ASSET",
        val badge: String = "--",
        val summary: String = "支持链切换、地址复制、二维码分享与 Memo 提醒。",
        val primaryActionLabel: String = "复制地址",
        val secondaryActionLabel: String? = "分享二维码",
        val heroAccent: String = "receive",
        val metrics: List<FeatureMetric> = listOf(
    FeatureMetric(label = "链", value = "--"),
    FeatureMetric(label = "备选", value = "--"),
    FeatureMetric(label = "地址数", value = "0"),
    FeatureMetric(label = "校验状态", value = "同步中"),
),
        val fields: List<FeatureField> = listOf(
    FeatureField(key = "address", label = "收款地址", value = "--", supportingText = "请确认链一致"),
),
        val highlights: List<FeatureListItem> = emptyList(),
        val variants: List<ReceiveVariantUi> = emptyList(),
        val canShare: Boolean = false,
        val shareText: String = "",
        val checklist: List<FeatureBullet> = emptyList(),
        val note: String = "正在从真实收款服务同步数据。",
        val banner: P2SurfaceBanner = p2LoadingBanner(),
        val feedbackMessage: String? = null,
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
