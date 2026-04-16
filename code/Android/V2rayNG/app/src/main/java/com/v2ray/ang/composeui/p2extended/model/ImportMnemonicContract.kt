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
        val badge: String = "待校验",
        val summary: String = "等待助记词校验服务返回结果。",
        val primaryActionLabel: String = "解析并导入钱包",
        val secondaryActionLabel: String? = "回到导入方式",
        val heroAccent: String = "import_mnemonic",
        val metrics: List<FeatureMetric> = listOf(
    FeatureMetric(label = "支持词数", value = "待返回"),
    FeatureMetric(label = "恢复链数", value = "待返回"),
    FeatureMetric(label = "导入状态", value = "未校验"),
),
        val fields: List<FeatureField> = listOf(
    FeatureField(key = "mnemonic", label = "助记词", value = "", supportingText = "使用空格分隔的标准助记词"),
    FeatureField(key = "walletName", label = "恢复后钱包名", value = "", supportingText = "导入后展示名称"),
),
        val highlights: List<FeatureListItem> = listOf(
    FeatureListItem(title = "导入状态", subtitle = "等待助记词校验与链信息返回。", trailing = "未校验", badge = "State"),
    FeatureListItem(title = "导航参数", subtitle = "source", trailing = "1 个", badge = "Nav"),
    FeatureListItem(title = "表单状态", subtitle = "助记词与钱包名称待输入", trailing = "2 项", badge = "Form"),
    FeatureListItem(title = "数据来源", subtitle = "由助记词校验与导入接口返回。", trailing = "Runtime", badge = "Source"),
),
        val checklist: List<FeatureBullet> = listOf(
    FeatureBullet(title = "助记词校验", detail = "未通过校验前，不展示伪造恢复结果。"),
    FeatureBullet(title = "钱包导入", detail = "导入成功后再返回真实链与账户信息。"),
    FeatureBullet(title = "导航参数", detail = "根据 source 区分来源入口。"),
),
        val note: String = "当前未返回助记词校验结果。",
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
        note = "正在获取导入上下文。",
    )

    fun importMnemonicPreviewState(): ImportMnemonicUiState = ImportMnemonicUiState(
        note = "当前未返回助记词校验结果。",
    )
