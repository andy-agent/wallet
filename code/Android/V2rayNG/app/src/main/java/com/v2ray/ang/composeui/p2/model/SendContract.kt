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
        val subtitle: String = "SEND ASSET",
        val badge: String = "待接发送动作",
        val summary: String = "发送页展示真实链/资产目录；Android 端发送动作尚未接入。",
        val primaryActionLabel: String = "确认并发送",
        val secondaryActionLabel: String? = "返回资产详情",
        val heroAccent: String = "send",
        val availableBalanceLabel: String = "可发送余额",
        val availableBalance: String = "--",
        val balanceSupportingText: String = "当前未接入真实链上余额。",
        val metrics: List<FeatureMetric> = listOf(
    FeatureMetric(label = "当前资产", value = "USDT · TRON"),
    FeatureMetric(label = "可发送余额", value = "--"),
    FeatureMetric(label = "广播能力", value = "待接发送动作"),
    FeatureMetric(label = "预检查", value = "Android 未调用"),
),
        val fields: List<FeatureField> = listOf(
    FeatureField(key = "asset", label = "选择资产", value = "USDT · TRON", supportingText = "可切换链和资产"),
    FeatureField(key = "to", label = "收款地址", value = "", supportingText = "请填写目标地址"),
    FeatureField(key = "amount", label = "发送数量", value = "", supportingText = "当前未接入真实链上余额"),
),
        val highlights: List<FeatureListItem> = listOf(
    FeatureListItem(title = "Android 状态", subtitle = "未接入 transfer/precheck/proxy-broadcast 调用", trailing = "", badge = "BLOCKED"),
    FeatureListItem(title = "后端能力", subtitle = "能力待检查", trailing = "", badge = "REAL"),
    FeatureListItem(title = "资产目录", subtitle = "待加载", trailing = "", badge = "CAT"),
),
        val checklist: List<FeatureBullet> = listOf(
    FeatureBullet(title = "余额状态", detail = "当前未接入真实链上余额"),
    FeatureBullet(title = "预检查接口", detail = "Android 未调用"),
    FeatureBullet(title = "广播能力", detail = "待接发送动作"),
    FeatureBullet(title = "数据源", detail = "wallet/overview"),
),
        val note: String = "发送页不再展示订单聚合金额。",
        val networkOptions: List<SendChoiceUi> = emptyList(),
        val assetOptions: List<SendChoiceUi> = emptyList(),
        val currentRoute: SendRouteArgs = SendRouteArgs(),
        val isSubmitting: Boolean = false,
        val banner: P2SurfaceBanner = p2ReadyBanner(),
        val feedbackMessage: String? = "后端已提供转账接口，但 Android 端尚未接入签名、预检查和广播动作。",
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
