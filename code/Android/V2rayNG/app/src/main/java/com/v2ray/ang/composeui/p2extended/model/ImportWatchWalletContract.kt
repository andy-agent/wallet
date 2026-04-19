package com.v2ray.ang.composeui.p2extended.model

import com.v2ray.ang.composeui.common.model.FeatureBullet
import com.v2ray.ang.composeui.common.model.FeatureField
import com.v2ray.ang.composeui.common.model.FeatureListItem
import com.v2ray.ang.composeui.common.model.FeatureMetric
import com.v2ray.ang.composeui.navigation.CryptoVpnRouteSpec
import com.v2ray.ang.composeui.navigation.RouteDefinition

data class ImportWatchWalletUiState(
    val isLoading: Boolean = false,
    val title: String = "导入观察钱包",
    val subtitle: String = "IMPORT WATCH WALLET",
    val badge: String = "",
    val summary: String = "导入仅查看的钱包地址，不提供签名与支付能力。",
    val primaryActionLabel: String = "导入观察钱包",
    val secondaryActionLabel: String? = "返回导入方式",
    val heroAccent: String = "import_watch_wallet",
    val metrics: List<FeatureMetric> = listOf(
        FeatureMetric("导入模式", "WATCH_ONLY"),
        FeatureMetric("支持链", "EVM / SOLANA / TRON"),
        FeatureMetric("支付能力", "只读"),
    ),
    val fields: List<FeatureField> = listOf(
        FeatureField("walletName", "钱包名称", "", "例如 Watch Wallet"),
        FeatureField("networkCode", "网络", "SOLANA", "ETHEREUM / BSC / POLYGON / ARBITRUM / BASE / OPTIMISM / AVALANCHE_C / SOLANA / TRON"),
        FeatureField("address", "钱包地址", "", "输入对应链地址"),
    ),
    val highlights: List<FeatureListItem> = listOf(
        FeatureListItem("钱包类型", "观察钱包", "WATCH_ONLY", "LIVE"),
        FeatureListItem("支付能力", "导入后仅可查看，不可支付", "DISABLED", "SAFE"),
    ),
    val checklist: List<FeatureBullet> = listOf(
        FeatureBullet("只读钱包", "不会在本机保存签名材料。"),
        FeatureBullet("链校验", "地址格式必须与网络一致。"),
    ),
    val note: String = "",
)

sealed interface ImportWatchWalletEvent {
    data object Refresh : ImportWatchWalletEvent
    data object PrimaryActionClicked : ImportWatchWalletEvent
    data object SecondaryActionClicked : ImportWatchWalletEvent
    data class FieldChanged(
        val key: String,
        val value: String,
    ) : ImportWatchWalletEvent
}

val importWatchWalletNavigation: RouteDefinition = CryptoVpnRouteSpec.importWatchWallet

fun importWatchWalletPreviewState(): ImportWatchWalletUiState = ImportWatchWalletUiState()
