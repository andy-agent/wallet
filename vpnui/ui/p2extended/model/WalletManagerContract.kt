package com.cryptovpn.ui.p2extended.model

import com.cryptovpn.navigation.CryptoVpnRouteSpec
import com.cryptovpn.navigation.RouteDefinition
import com.cryptovpn.ui.common.model.FeatureBullet
import com.cryptovpn.ui.common.model.FeatureField
import com.cryptovpn.ui.common.model.FeatureListItem
import com.cryptovpn.ui.common.model.FeatureMetric

data class WalletManagerRouteArgs(val walletId: String = "primary_wallet")

data class WalletManagerUiState(
        val title: String = "钱包管理",
        val subtitle: String = "WALLET MANAGER",
        val badge: String = "P2 · EXTENDED",
        val summary: String = "钱包管理页用于切换默认钱包、重命名、归档与导出安全操作。",
        val primaryActionLabel: String = "打开地址簿",
        val secondaryActionLabel: String? = "返回个人中心",
        val heroAccent: String = "wallet_manager",
        val metrics: List<FeatureMetric> = listOf(
    FeatureMetric(label = "钱包数量", value = "3"),
    FeatureMetric(label = "默认钱包", value = "1"),
    FeatureMetric(label = "已归档", value = "0"),
),
        val fields: List<FeatureField> = emptyList(),
        val highlights: List<FeatureListItem> = listOf(
    FeatureListItem(title = "路由标识", subtitle = "钱包管理页用于切换默认钱包、重命名、归档与导出安全操作。", trailing = "wallet_manager", badge = "P2 扩展页"),
    FeatureListItem(title = "导航参数", subtitle = "walletId", trailing = "1 个", badge = "Nav"),
    FeatureListItem(title = "表单占位", subtitle = "当前页面以信息展示与确认动作为主", trailing = "0 项", badge = "Info"),
    FeatureListItem(title = "交付内容", subtitle = "Composable + UiState + Event + ViewModel + Mock Repository 已补齐", trailing = "Ready", badge = "Drop-in"),
),
        val checklist: List<FeatureBullet> = listOf(
    FeatureBullet(title = "ViewModel Stub", detail = "钱包管理 已预留事件分发与 refresh 占位。"),
    FeatureBullet(title = "Mock Repository", detail = "可通过 WalletManagerPreviewState / Repository 种子替换真实接口。"),
    FeatureBullet(title = "Preview", detail = "页面已内置 @Preview，可直接在 Android Studio 查看。"),
    FeatureBullet(title = "Navigation Args", detail = "createRoute builder 与 NavGraph 参数解析已补齐。"),
),
        val note: String = "钱包管理 已按 P2 扩展页 页面补齐，可继续替换为真实业务逻辑与接口数据。",
    )

    sealed interface WalletManagerEvent {
        data object Refresh : WalletManagerEvent
        data object PrimaryActionClicked : WalletManagerEvent
        data object SecondaryActionClicked : WalletManagerEvent
        data class FieldChanged(
            val key: String,
            val value: String,
        ) : WalletManagerEvent
    }

    val walletManagerNavigation: RouteDefinition = CryptoVpnRouteSpec.walletManager

    fun walletManagerPreviewState(): WalletManagerUiState = WalletManagerUiState()
