package com.v2ray.ang.composeui.p2extended.model

import com.v2ray.ang.composeui.navigation.CryptoVpnRouteSpec
import com.v2ray.ang.composeui.navigation.RouteDefinition
import com.v2ray.ang.composeui.common.model.FeatureBullet
import com.v2ray.ang.composeui.common.model.FeatureField
import com.v2ray.ang.composeui.common.model.FeatureListItem
import com.v2ray.ang.composeui.common.model.FeatureMetric

data class CreateWalletRouteArgs(val mode: String = "create")

data class CreateWalletUiState(
        val title: String = "创建钱包",
        val subtitle: String = "CREATE WALLET",
        val badge: String = "P2 · EXTENDED",
        val summary: String = "创建钱包页承接钱包命名、多链初始化与备份前校验。",
        val primaryActionLabel: String? = "开始备份助记词",
        val secondaryActionLabel: String? = "改用导入方式",
        val heroAccent: String = "create_wallet",
        val metrics: List<FeatureMetric> = listOf(
    FeatureMetric(label = "默认链", value = "7 条"),
    FeatureMetric(label = "备份要求", value = "必须"),
    FeatureMetric(label = "模式", value = "Create"),
),
        val fields: List<FeatureField> = listOf(
    FeatureField(key = "name", label = "钱包名称", value = "Primary Wallet", supportingText = "仅作为本地钱包展示名称"),
),
        val highlights: List<FeatureListItem> = listOf(
    FeatureListItem(title = "路由标识", subtitle = "创建钱包页承接钱包命名、多链初始化与备份前校验。", trailing = "create_wallet", badge = "P2 扩展页"),
    FeatureListItem(title = "导航参数", subtitle = "mode", trailing = "1 个", badge = "Nav"),
    FeatureListItem(title = "表单占位", subtitle = "钱包名称", trailing = "1 项", badge = "Form"),
    FeatureListItem(title = "交付内容", subtitle = "Composable + UiState + Event + ViewModel + Mock Repository 已补齐", trailing = "Ready", badge = "Drop-in"),
),
        val checklist: List<FeatureBullet> = listOf(
    FeatureBullet(title = "ViewModel Stub", detail = "创建钱包 已预留事件分发与 refresh 占位。"),
    FeatureBullet(title = "Mock Repository", detail = "可通过 CreateWalletPreviewState / Repository 种子替换真实接口。"),
    FeatureBullet(title = "Preview", detail = "页面已内置 @Preview，可直接在 Android Studio 查看。"),
    FeatureBullet(title = "Navigation Args", detail = "createRoute builder 与 NavGraph 参数解析已补齐。"),
),
        val note: String = "创建钱包 已按 P2 扩展页 页面补齐，可继续替换为真实业务逻辑与接口数据。",
    )

    sealed interface CreateWalletEvent {
        data object Refresh : CreateWalletEvent
        data object PrimaryActionClicked : CreateWalletEvent
        data object SecondaryActionClicked : CreateWalletEvent
        data class FieldChanged(
            val key: String,
            val value: String,
        ) : CreateWalletEvent
    }

    val createWalletNavigation: RouteDefinition = CryptoVpnRouteSpec.createWallet

    fun createWalletPreviewState(): CreateWalletUiState = CreateWalletUiState()
