package com.v2ray.ang.composeui.p2extended.model

import com.v2ray.ang.composeui.common.model.FeatureBullet
import com.v2ray.ang.composeui.common.model.FeatureField
import com.v2ray.ang.composeui.common.model.FeatureListItem
import com.v2ray.ang.composeui.common.model.FeatureMetric
import com.v2ray.ang.composeui.navigation.CryptoVpnRouteSpec
import com.v2ray.ang.composeui.navigation.RouteDefinition

data class ImportWalletMethodUiState(
    val title: String = "导入钱包方式",
    val subtitle: String = "IMPORT WALLET",
    val badge: String = "同步中",
    val summary: String = "正在检查可用的钱包导入能力；若导入引擎未接通，将显示阻塞说明。",
    val primaryActionLabel: String? = null,
    val secondaryActionLabel: String? = null,
    val heroAccent: String = "import_wallet_method",
    val metrics: List<FeatureMetric> = listOf(
        FeatureMetric(label = "账户状态", value = "读取中"),
        FeatureMetric(label = "恢复入口", value = "读取中"),
        FeatureMetric(label = "本地密钥", value = "读取中"),
    ),
    val fields: List<FeatureField> = emptyList(),
    val highlights: List<FeatureListItem> = listOf(
        FeatureListItem(title = "当前状态", subtitle = "正在检查导入能力", trailing = "", badge = "LOADING"),
    ),
    val checklist: List<FeatureBullet> = listOf(
        FeatureBullet(title = "说明", detail = "默认态不再展示演示导入方式和假入口。"),
    ),
    val note: String = "刷新后会替换为真实状态或明确阻塞说明。",
)

sealed interface ImportWalletMethodEvent {
    data object Refresh : ImportWalletMethodEvent
    data object PrimaryActionClicked : ImportWalletMethodEvent
    data object SecondaryActionClicked : ImportWalletMethodEvent
    data class FieldChanged(
        val key: String,
        val value: String,
    ) : ImportWalletMethodEvent
}

val importWalletMethodNavigation: RouteDefinition = CryptoVpnRouteSpec.importWalletMethod

fun importWalletMethodPreviewState(): ImportWalletMethodUiState = ImportWalletMethodUiState()
