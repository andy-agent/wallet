package com.v2ray.ang.composeui.p2.model

import com.v2ray.ang.composeui.navigation.CryptoVpnRouteSpec
import com.v2ray.ang.composeui.navigation.RouteDefinition
import com.v2ray.ang.composeui.common.model.FeatureBullet
import com.v2ray.ang.composeui.common.model.FeatureField
import com.v2ray.ang.composeui.common.model.FeatureListItem
import com.v2ray.ang.composeui.common.model.FeatureMetric

data class SendRouteArgs(val assetId: String = "USDT", val chainId: String = "tron")

data class SendChoiceUi(
    val id: String,
    val label: String,
    val selected: Boolean = false,
)

data class SendUiState(
        val title: String = "发送资产",
        val subtitle: String = "",
        val badge: String = "",
        val summary: String = "",
        val primaryActionLabel: String = "确认并发送",
        val secondaryActionLabel: String? = "返回资产详情",
        val heroAccent: String = "send",
        val availableBalanceLabel: String = "可发送余额",
        val availableBalance: String = "--",
        val balanceSupportingText: String = "",
        val metrics: List<FeatureMetric> = listOf(
    FeatureMetric(label = "当前资产", value = "USDT · TRON"),
    FeatureMetric(label = "可发送余额", value = "--"),
    FeatureMetric(label = "广播能力", value = "已接代理广播"),
    FeatureMetric(label = "预检查", value = "已接发送前校验"),
),
        val fields: List<FeatureField> = listOf(
    FeatureField(key = "to", label = "收款地址", value = "", supportingText = "粘贴或扫码地址"),
    FeatureField(key = "amount", label = "发送数量", value = "", supportingText = "输入数量"),
),
        val highlights: List<FeatureListItem> = emptyList(),
        val checklist: List<FeatureBullet> = emptyList(),
        val note: String = "",
        val networkOptions: List<SendChoiceUi> = emptyList(),
        val assetOptions: List<SendChoiceUi> = emptyList(),
        val currentRoute: SendRouteArgs = SendRouteArgs(),
        val isSubmitting: Boolean = false,
        val banner: P2SurfaceBanner = p2ReadyBanner(),
        val feedbackMessage: String? = null,
        val redirectRoute: String? = null,
    )

    sealed interface SendEvent {
        data object Refresh : SendEvent
        data object PrimaryActionClicked : SendEvent
        data object SecondaryActionClicked : SendEvent
        data class NetworkSelected(
            val chainId: String,
        ) : SendEvent
        data class AssetSelected(
            val assetId: String,
        ) : SendEvent
        data class FieldChanged(
            val key: String,
            val value: String,
        ) : SendEvent
    }

    val sendNavigation: RouteDefinition = CryptoVpnRouteSpec.send

    fun sendPreviewState(): SendUiState = SendUiState()
