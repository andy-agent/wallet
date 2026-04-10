package com.cryptovpn.ui.p2.model

import com.cryptovpn.navigation.CryptoVpnRouteSpec
import com.cryptovpn.navigation.RouteDefinition
import com.cryptovpn.ui.common.model.FeatureBullet
import com.cryptovpn.ui.common.model.FeatureField
import com.cryptovpn.ui.common.model.FeatureListItem
import com.cryptovpn.ui.common.model.FeatureMetric

data class SendRouteArgs(val assetId: String = "USDT", val chainId: String = "tron")

data class SendUiState(
        val title: String = "发送资产",
        val subtitle: String = "SEND",
        val badge: String = "P2 · BASE",
        val summary: String = "发送页校验目标地址、金额、手续费与安全风险后发起链上转账。",
        val primaryActionLabel: String = "确认并发送",
        val secondaryActionLabel: String? = "Gas 设置",
        val heroAccent: String = "send",
        val metrics: List<FeatureMetric> = listOf(
    FeatureMetric(label = "发送资产", value = "USDT"),
    FeatureMetric(label = "预估手续费", value = "1.24 USDT"),
    FeatureMetric(label = "风控分", value = "38 / 100"),
),
        val fields: List<FeatureField> = listOf(
    FeatureField(key = "to", label = "收款地址", value = "TQx...uJsv", supportingText = "目标地址或联系人地址"),
    FeatureField(key = "amount", label = "发送数量", value = "580", supportingText = "默认演示发送数量"),
    FeatureField(key = "memo", label = "备注", value = "", supportingText = "用于链下对账，链上不一定保留"),
),
        val highlights: List<FeatureListItem> = listOf(
    FeatureListItem(title = "路由标识", subtitle = "发送页校验目标地址、金额、手续费与安全风险后发起链上转账。", trailing = "send", badge = "P2 基础文档页"),
    FeatureListItem(title = "导航参数", subtitle = "assetId / chainId", trailing = "2 个", badge = "Nav"),
    FeatureListItem(title = "表单占位", subtitle = "收款地址、发送数量、备注", trailing = "3 项", badge = "Form"),
    FeatureListItem(title = "交付内容", subtitle = "Composable + UiState + Event + ViewModel + Mock Repository 已补齐", trailing = "Ready", badge = "Drop-in"),
),
        val checklist: List<FeatureBullet> = listOf(
    FeatureBullet(title = "ViewModel Stub", detail = "发送资产 已预留事件分发与 refresh 占位。"),
    FeatureBullet(title = "Mock Repository", detail = "可通过 SendPreviewState / Repository 种子替换真实接口。"),
    FeatureBullet(title = "Preview", detail = "页面已内置 @Preview，可直接在 Android Studio 查看。"),
    FeatureBullet(title = "Navigation Args", detail = "createRoute builder 与 NavGraph 参数解析已补齐。"),
),
        val note: String = "发送资产 已按 P2 基础文档页 页面补齐，可继续替换为真实业务逻辑与接口数据。",
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
