package com.cryptovpn.ui.p2.model

import com.cryptovpn.navigation.CryptoVpnRouteSpec
import com.cryptovpn.navigation.RouteDefinition
import com.cryptovpn.ui.common.model.FeatureBullet
import com.cryptovpn.ui.common.model.FeatureField
import com.cryptovpn.ui.common.model.FeatureListItem
import com.cryptovpn.ui.common.model.FeatureMetric

data class ReceiveRouteArgs(val assetId: String = "USDT", val chainId: String = "tron")

data class ReceiveUiState(
        val title: String = "收款资产",
        val subtitle: String = "RECEIVE",
        val badge: String = "P2 · BASE",
        val summary: String = "收款页展示当前地址、二维码与可切换网络，方便分享给转账方。",
        val primaryActionLabel: String = "复制地址并返回",
        val secondaryActionLabel: String? = "回到钱包首页",
        val heroAccent: String = "receive",
        val metrics: List<FeatureMetric> = listOf(
    FeatureMetric(label = "默认链", value = "TRON"),
    FeatureMetric(label = "支持网络", value = "4"),
    FeatureMetric(label = "校验状态", value = "已验证"),
),
        val fields: List<FeatureField> = listOf(
    FeatureField(key = "label", label = "地址标签", value = "我的常用收款地址", supportingText = "仅用于本地标记与识别"),
),
        val highlights: List<FeatureListItem> = listOf(
    FeatureListItem(title = "路由标识", subtitle = "收款页展示当前地址、二维码与可切换网络，方便分享给转账方。", trailing = "receive", badge = "P2 基础文档页"),
    FeatureListItem(title = "导航参数", subtitle = "assetId / chainId", trailing = "2 个", badge = "Nav"),
    FeatureListItem(title = "表单占位", subtitle = "地址标签", trailing = "1 项", badge = "Form"),
    FeatureListItem(title = "交付内容", subtitle = "Composable + UiState + Event + ViewModel + Mock Repository 已补齐", trailing = "Ready", badge = "Drop-in"),
),
        val checklist: List<FeatureBullet> = listOf(
    FeatureBullet(title = "ViewModel Stub", detail = "收款资产 已预留事件分发与 refresh 占位。"),
    FeatureBullet(title = "Mock Repository", detail = "可通过 ReceivePreviewState / Repository 种子替换真实接口。"),
    FeatureBullet(title = "Preview", detail = "页面已内置 @Preview，可直接在 Android Studio 查看。"),
    FeatureBullet(title = "Navigation Args", detail = "createRoute builder 与 NavGraph 参数解析已补齐。"),
),
        val note: String = "收款资产 已按 P2 基础文档页 页面补齐，可继续替换为真实业务逻辑与接口数据。",
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
