package com.v2ray.ang.composeui.p2.model

import com.v2ray.ang.composeui.navigation.CryptoVpnRouteSpec
import com.v2ray.ang.composeui.navigation.RouteDefinition
import com.v2ray.ang.composeui.common.model.FeatureBullet
import com.v2ray.ang.composeui.common.model.FeatureField
import com.v2ray.ang.composeui.common.model.FeatureListItem
import com.v2ray.ang.composeui.common.model.FeatureMetric

data class SendRouteArgs(val assetId: String = "USDT", val chainId: String = "tron")

data class SendUiState(
        val title: String = "发送资产",
        val subtitle: String = "SEND ASSET",
        val badge: String = "风险校验中",
        val summary: String = "把链、地址、Gas 与隐私网络状态放在一个确认面板里。",
        val primaryActionLabel: String? = "确认并发送",
        val secondaryActionLabel: String? = "返回资产详情",
        val heroAccent: String = "send",
        val metrics: List<FeatureMetric> = listOf(
    FeatureMetric(label = "发送资产", value = "USDT · TRON 网络优先"),
    FeatureMetric(label = "发送数量", value = "580"),
    FeatureMetric(label = "预估手续费", value = "1.24 USDT"),
    FeatureMetric(label = "预计到账", value = "~ 38 秒"),
),
        val fields: List<FeatureField> = listOf(
    FeatureField(key = "asset", label = "选择资产", value = "USDT · TRON 网络优先", supportingText = "TRON · Fee 更低 / Solana"),
    FeatureField(key = "to", label = "收款地址", value = "TQ2x...8Lk2 · 或扫描二维码", supportingText = "目标地址"),
    FeatureField(key = "amount", label = "发送数量", value = "580", supportingText = "≈ $580.00 USDT"),
),
        val highlights: List<FeatureListItem> = listOf(
    FeatureListItem(title = "地址格式正确", subtitle = "与 TRON 主网地址规则匹配", trailing = "通过"),
    FeatureListItem(title = "历史交互检测", subtitle = "此前已转账 2 次，无异常标签", trailing = "通过"),
    FeatureListItem(title = "VPN 隐私路由可用", subtitle = "发送请求将通过已连接的安全通道广播", trailing = "通过"),
),
        val checklist: List<FeatureBullet> = listOf(
    FeatureBullet(title = "网络费", detail = "1.24 USDT"),
    FeatureBullet(title = "预计到账", detail = "~ 38 秒"),
    FeatureBullet(title = "安全检查", detail = "通过 3/4"),
    FeatureBullet(title = "网络选择", detail = "TRON / Solana"),
),
        val note: String = "确认后将发起链上广播。",
        val isLoading: Boolean = false,
        val errorMessage: String? = null,
        val emptyMessage: String? = null,
        val blockerTitle: String? = null,
        val blockerMessage: String? = null,
    )

    sealed interface SendEvent {
        data object Refresh : SendEvent
        data object PrimaryActionClicked : SendEvent
        data object SecondaryActionClicked : SendEvent
        data class FieldChanged(
            val key: String,
            val value: String,
        ) : SendEvent
    }

    val sendNavigation: RouteDefinition = CryptoVpnRouteSpec.send

    fun sendPreviewState(): SendUiState = SendUiState()
