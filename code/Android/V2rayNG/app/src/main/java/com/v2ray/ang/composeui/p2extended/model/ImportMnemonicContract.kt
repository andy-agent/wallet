package com.v2ray.ang.composeui.p2extended.model

import com.v2ray.ang.composeui.navigation.CryptoVpnRouteSpec
import com.v2ray.ang.composeui.navigation.RouteDefinition
import com.v2ray.ang.composeui.common.model.FeatureBullet
import com.v2ray.ang.composeui.common.model.FeatureField
import com.v2ray.ang.composeui.common.model.FeatureListItem
import com.v2ray.ang.composeui.common.model.FeatureMetric

data class ImportMnemonicRouteArgs(val source: String = "onboarding")

data class ImportMnemonicUiState(
        val isLoading: Boolean = false,
        val title: String = "输入助记词",
        val subtitle: String = "IMPORT MNEMONIC",
        val badge: String = "MNEMONIC IMPORT",
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
    FeatureField(key = "mnemonic", label = "助记词", value = "", supportingText = "使用空格分隔的标准助记词"),
    FeatureField(key = "walletName", label = "恢复后钱包名", value = "", supportingText = "导入后展示名称"),
),
        val highlights: List<FeatureListItem> = listOf(
    FeatureListItem(title = "路由标识", subtitle = "助记词导入页提供文本输入、词数校验与恢复后的链列表预估。", trailing = "import_mnemonic", badge = "P2 扩展页"),
    FeatureListItem(title = "导航参数", subtitle = "source", trailing = "1 个", badge = "Nav"),
    FeatureListItem(title = "表单占位", subtitle = "助记词、恢复后钱包名", trailing = "2 项", badge = "Form"),
    FeatureListItem(title = "数据源", subtitle = "运行时由真实仓储覆盖，预览仅保留结构默认值。", trailing = "Runtime", badge = "Source"),
),
        val checklist: List<FeatureBullet> = listOf(
    FeatureBullet(title = "运行时导入", detail = "主按钮会提交真实钱包导入状态。"),
    FeatureBullet(title = "预览默认值", detail = "仅用于 Android Studio 预览，不参与运行时分支。"),
    FeatureBullet(title = "导航参数", detail = "createRoute builder 与 NavGraph 参数解析已补齐。"),
),
        val note: String = "输入助记词运行时走真实仓储，默认值只保留预览结构。",
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

    fun importMnemonicLoadingState(): ImportMnemonicUiState = ImportMnemonicUiState(
        isLoading = true,
        summary = "正在同步导入上下文。",
        primaryActionLabel = "正在同步...",
        secondaryActionLabel = null,
        metrics = emptyList(),
        fields = emptyList(),
        highlights = emptyList(),
        checklist = emptyList(),
        note = "当前页面会在拿到真实账户与导入来源后再展示输入表单。",
    )

    fun importMnemonicPreviewState(): ImportMnemonicUiState = ImportMnemonicUiState(
        metrics = listOf(
            FeatureMetric(label = "支持词数", value = "12 / 24"),
            FeatureMetric(label = "恢复链数", value = "6"),
            FeatureMetric(label = "预计耗时", value = "4 分钟"),
        ),
        fields = listOf(
            FeatureField(key = "mnemonic", label = "助记词", value = "", supportingText = "使用空格分隔的标准助记词"),
            FeatureField(key = "walletName", label = "恢复后钱包名", value = "", supportingText = "导入后展示名称"),
        ),
        highlights = listOf(
            FeatureListItem(title = "路由标识", subtitle = "助记词导入页提供文本输入、词数校验与恢复后的链列表预估。", trailing = "import_mnemonic", badge = "P2 扩展页"),
            FeatureListItem(title = "导航参数", subtitle = "source", trailing = "1 个", badge = "Nav"),
            FeatureListItem(title = "表单占位", subtitle = "助记词、恢复后钱包名", trailing = "2 项", badge = "Form"),
            FeatureListItem(title = "数据源", subtitle = "运行时由真实仓储覆盖，预览仅保留结构默认值。", trailing = "Runtime", badge = "Source"),
        ),
        checklist = listOf(
            FeatureBullet(title = "运行时导入", detail = "主按钮会提交真实钱包导入状态。"),
            FeatureBullet(title = "预览默认值", detail = "仅用于 Android Studio 预览，不参与运行时分支。"),
            FeatureBullet(title = "导航参数", detail = "createRoute builder 与 NavGraph 参数解析已补齐。"),
        ),
        note = "输入助记词运行时走真实仓储，默认值只保留预览结构。",
    )
