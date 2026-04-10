package com.cryptovpn.ui.p2extended.model

import com.cryptovpn.navigation.CryptoVpnRouteSpec
import com.cryptovpn.navigation.RouteDefinition
import com.cryptovpn.ui.common.model.FeatureBullet
import com.cryptovpn.ui.common.model.FeatureField
import com.cryptovpn.ui.common.model.FeatureListItem
import com.cryptovpn.ui.common.model.FeatureMetric

data class ConfirmMnemonicRouteArgs(val walletId: String = "primary_wallet")

data class ConfirmMnemonicUiState(
        val title: String = "确认助记词",
        val subtitle: String = "CONFIRM MNEMONIC",
        val badge: String = "P2 · EXTENDED",
        val summary: String = "确认助记词页通过抽查顺序验证用户是否完成备份。",
        val primaryActionLabel: String = "完成验证并进入安全中心",
        val secondaryActionLabel: String? = "返回重新查看",
        val heroAccent: String = "confirm_mnemonic",
        val metrics: List<FeatureMetric> = listOf(
    FeatureMetric(label = "已选择", value = "3 / 12"),
    FeatureMetric(label = "错误次数", value = "0"),
    FeatureMetric(label = "当前阶段", value = "Final Gate"),
),
        val fields: List<FeatureField> = emptyList(),
        val highlights: List<FeatureListItem> = listOf(
    FeatureListItem(title = "路由标识", subtitle = "确认助记词页通过抽查顺序验证用户是否完成备份。", trailing = "confirm_mnemonic", badge = "P2 扩展页"),
    FeatureListItem(title = "导航参数", subtitle = "walletId", trailing = "1 个", badge = "Nav"),
    FeatureListItem(title = "表单占位", subtitle = "当前页面以信息展示与确认动作为主", trailing = "0 项", badge = "Info"),
    FeatureListItem(title = "交付内容", subtitle = "Composable + UiState + Event + ViewModel + Mock Repository 已补齐", trailing = "Ready", badge = "Drop-in"),
),
        val checklist: List<FeatureBullet> = listOf(
    FeatureBullet(title = "ViewModel Stub", detail = "确认助记词 已预留事件分发与 refresh 占位。"),
    FeatureBullet(title = "Mock Repository", detail = "可通过 ConfirmMnemonicPreviewState / Repository 种子替换真实接口。"),
    FeatureBullet(title = "Preview", detail = "页面已内置 @Preview，可直接在 Android Studio 查看。"),
    FeatureBullet(title = "Navigation Args", detail = "createRoute builder 与 NavGraph 参数解析已补齐。"),
),
        val note: String = "确认助记词 已按 P2 扩展页 页面补齐，可继续替换为真实业务逻辑与接口数据。",
    )

    sealed interface ConfirmMnemonicEvent {
        data object Refresh : ConfirmMnemonicEvent
        data object PrimaryActionClicked : ConfirmMnemonicEvent
        data object SecondaryActionClicked : ConfirmMnemonicEvent
        data class FieldChanged(
            val key: String,
            val value: String,
        ) : ConfirmMnemonicEvent
    }

    val confirmMnemonicNavigation: RouteDefinition = CryptoVpnRouteSpec.confirmMnemonic

    fun confirmMnemonicPreviewState(): ConfirmMnemonicUiState = ConfirmMnemonicUiState()
