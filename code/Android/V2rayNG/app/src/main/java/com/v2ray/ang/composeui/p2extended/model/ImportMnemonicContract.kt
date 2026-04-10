package com.v2ray.ang.composeui.p2extended.model

import com.v2ray.ang.composeui.navigation.CryptoVpnRouteSpec
import com.v2ray.ang.composeui.navigation.RouteDefinition
import com.v2ray.ang.composeui.common.model.FeatureBullet
import com.v2ray.ang.composeui.common.model.FeatureField
import com.v2ray.ang.composeui.common.model.FeatureListItem
import com.v2ray.ang.composeui.common.model.FeatureMetric

data class ImportMnemonicRouteArgs(val source: String = "onboarding")

data class ImportMnemonicUiState(
        val title: String = "输入助记词",
        val subtitle: String = "IMPORT MNEMONIC",
        val badge: String = "P2 · EXTENDED",
        val summary: String = "助记词导入页提供文本输入、词数校验与恢复后的链列表预估。",
        val primaryActionLabel: String = "解析并导入钱包",
        val secondaryActionLabel: String? = "回到导入方式",
        val heroAccent: String = "import_mnemonic",
        val metrics: List<FeatureMetric> = listOf(
    FeatureMetric(label = "支持词数", value = "12 / 24"),
    FeatureMetric(label = "恢复链数", value = "6"),
    FeatureMetric(label = "预计耗时", value = "4 分钟"),
),
        val fields: List<FeatureField> = listOf(
    FeatureField(key = "mnemonic", label = "助记词", value = "ocean brick velvet lamp maple vivid orbit coral charge laptop anchor glow", supportingText = "使用空格分隔的标准助记词"),
    FeatureField(key = "walletName", label = "恢复后钱包名", value = "Main Wallet", supportingText = "导入后展示名称"),
),
        val highlights: List<FeatureListItem> = listOf(
    FeatureListItem(title = "路由标识", subtitle = "助记词导入页提供文本输入、词数校验与恢复后的链列表预估。", trailing = "import_mnemonic", badge = "P2 扩展页"),
    FeatureListItem(title = "导航参数", subtitle = "source", trailing = "1 个", badge = "Nav"),
    FeatureListItem(title = "表单占位", subtitle = "助记词、恢复后钱包名", trailing = "2 项", badge = "Form"),
    FeatureListItem(title = "交付内容", subtitle = "Composable + UiState + Event + ViewModel + Mock Repository 已补齐", trailing = "Ready", badge = "Drop-in"),
),
        val checklist: List<FeatureBullet> = listOf(
    FeatureBullet(title = "ViewModel Stub", detail = "输入助记词 已预留事件分发与 refresh 占位。"),
    FeatureBullet(title = "Mock Repository", detail = "可通过 ImportMnemonicPreviewState / Repository 种子替换真实接口。"),
    FeatureBullet(title = "Preview", detail = "页面已内置 @Preview，可直接在 Android Studio 查看。"),
    FeatureBullet(title = "Navigation Args", detail = "createRoute builder 与 NavGraph 参数解析已补齐。"),
),
        val note: String = "输入助记词 已按 P2 扩展页 页面补齐，可继续替换为真实业务逻辑与接口数据。",
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
