package com.v2ray.ang.composeui.p2extended.model

import com.v2ray.ang.composeui.navigation.CryptoVpnRouteSpec
import com.v2ray.ang.composeui.navigation.RouteDefinition
import com.v2ray.ang.composeui.common.model.FeatureBullet
import com.v2ray.ang.composeui.common.model.FeatureField
import com.v2ray.ang.composeui.common.model.FeatureListItem
import com.v2ray.ang.composeui.common.model.FeatureMetric

data class WalletManagerRouteArgs(val walletId: String = "primary_wallet")

data class WalletManagerWalletItemUi(
    val walletId: String,
    val walletName: String,
    val walletKind: String,
    val isDefault: Boolean,
    val isArchived: Boolean,
    val subtitle: String,
)

data class WalletManagerUiState(
        val title: String = "钱包管理",
        val subtitle: String = "WALLET MANAGER",
        val badge: String = "",
        val summary: String = "",
        val primaryActionLabel: String = "新增钱包",
        val secondaryActionLabel: String? = "返回个人中心",
        val heroAccent: String = "wallet_manager",
        val metrics: List<FeatureMetric> = listOf(
    FeatureMetric(label = "钱包数量", value = "0"),
    FeatureMetric(label = "当前钱包", value = "未创建"),
    FeatureMetric(label = "关联订单", value = "0"),
),
        val fields: List<FeatureField> = emptyList(),
        val wallets: List<WalletManagerWalletItemUi> = emptyList(),
        val highlights: List<FeatureListItem> = listOf(
    FeatureListItem(title = "当前钱包", subtitle = "未创建", trailing = "NOT_CREATED", badge = "REAL"),
    FeatureListItem(title = "新增钱包", subtitle = "为当前账号新增一个独立钱包地址入口。", trailing = "创建", badge = "CREATE"),
    FeatureListItem(title = "账户标签", subtitle = "--", trailing = "", badge = "ACCOUNT"),
),
        val checklist: List<FeatureBullet> = emptyList(),
        val note: String = "",
        val isSwitchingWallet: Boolean = false,
        val switchingWalletId: String? = null,
        val switchingWalletName: String? = null,
    )

    sealed interface WalletManagerEvent {
        data object Refresh : WalletManagerEvent
        data object PrimaryActionClicked : WalletManagerEvent
        data object SecondaryActionClicked : WalletManagerEvent
        data class WalletSelected(val walletId: String) : WalletManagerEvent
        data class FieldChanged(
            val key: String,
            val value: String,
        ) : WalletManagerEvent
    }

    val walletManagerNavigation: RouteDefinition = CryptoVpnRouteSpec.walletManager

    fun walletManagerPreviewState(): WalletManagerUiState = WalletManagerUiState()
