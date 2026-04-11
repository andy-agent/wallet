package com.v2ray.ang.composeui.p2extended.model

import com.v2ray.ang.composeui.common.model.FeatureBullet
import com.v2ray.ang.composeui.common.model.FeatureField
import com.v2ray.ang.composeui.common.model.FeatureListItem
import com.v2ray.ang.composeui.common.model.FeatureMetric
import com.v2ray.ang.composeui.navigation.CryptoVpnRouteSpec
import com.v2ray.ang.composeui.navigation.RouteDefinition

data class ImportMnemonicRouteArgs(val source: String = "onboarding")

data class ImportMnemonicUiState(
    val title: String = "输入助记词",
    val subtitle: String = "IMPORT MNEMONIC",
    val badge: String = "同步中",
    val summary: String = "正在检查助记词解析与恢复能力；若未接通，将显示阻塞说明。",
    val primaryActionLabel: String? = null,
    val secondaryActionLabel: String? = null,
    val heroAccent: String = "import_mnemonic",
    val metrics: List<FeatureMetric> = listOf(
        FeatureMetric(label = "导入来源", value = "读取中"),
        FeatureMetric(label = "恢复链数", value = "读取中"),
        FeatureMetric(label = "解析状态", value = "读取中"),
    ),
    val fields: List<FeatureField> = listOf(
        FeatureField(key = "mnemonic", label = "助记词", value = "", supportingText = "等待真实解析能力或阻塞说明。"),
        FeatureField(key = "walletName", label = "恢复后钱包名", value = "", supportingText = "仅在真实导入流程接通后生效。"),
    ),
    val highlights: List<FeatureListItem> = listOf(
        FeatureListItem(title = "当前状态", subtitle = "正在检查助记词导入能力", trailing = "", badge = "LOADING"),
    ),
    val checklist: List<FeatureBullet> = listOf(
        FeatureBullet(title = "说明", detail = "默认态不再展示演示助记词、假钱包名和假导入按钮。"),
    ),
    val note: String = "刷新后会替换为真实状态或明确阻塞说明。",
)

sealed interface ImportMnemonicEvent {
    data object Refresh : ImportMnemonicEvent
    data object PrimaryActionClicked : ImportMnemonicEvent
    data object SecondaryActionClicked : ImportMnemonicEvent
    data class FieldChanged(
        val key: String,
        val value: String,
    ) : ImportMnemonicEvent
}

val importMnemonicNavigation: RouteDefinition = CryptoVpnRouteSpec.importMnemonic

fun importMnemonicPreviewState(): ImportMnemonicUiState = ImportMnemonicUiState()
