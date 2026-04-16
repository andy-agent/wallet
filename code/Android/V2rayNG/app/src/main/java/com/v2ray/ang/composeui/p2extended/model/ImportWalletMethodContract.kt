package com.v2ray.ang.composeui.p2extended.model

import com.v2ray.ang.composeui.navigation.CryptoVpnRouteSpec
import com.v2ray.ang.composeui.navigation.RouteDefinition
import com.v2ray.ang.composeui.common.model.FeatureBullet
import com.v2ray.ang.composeui.common.model.FeatureField
import com.v2ray.ang.composeui.common.model.FeatureListItem
import com.v2ray.ang.composeui.common.model.FeatureMetric

data class ImportWalletMethodUiState(
        val isLoading: Boolean = false,
        val title: String = "导入钱包方式",
        val subtitle: String = "IMPORT WALLET METHOD",
        val badge: String = "待选择",
        val summary: String = "等待账户状态返回可用的导入方式。",
        val primaryActionLabel: String = "使用助记词导入",
        val secondaryActionLabel: String? = "返回钱包引导",
        val heroAccent: String = "import_wallet_method",
        val metrics: List<FeatureMetric> = listOf(
    FeatureMetric(label = "导入方式", value = "待返回"),
    FeatureMetric(label = "支持链", value = "待返回"),
    FeatureMetric(label = "能力状态", value = "未选择"),
),
        val fields: List<FeatureField> = emptyList(),
        val highlights: List<FeatureListItem> = listOf(
    FeatureListItem(title = "导入状态", subtitle = "等待后端返回当前账户支持的导入方式。", trailing = "未选择", badge = "State"),
    FeatureListItem(title = "导航参数", subtitle = "当前页面无必填导航参数", trailing = "0 个", badge = "Nav"),
    FeatureListItem(title = "表单状态", subtitle = "当前页面仅展示导入入口", trailing = "0 项", badge = "Info"),
    FeatureListItem(title = "数据来源", subtitle = "由钱包导入能力实时返回。", trailing = "Runtime", badge = "Source"),
),
        val checklist: List<FeatureBullet> = listOf(
    FeatureBullet(title = "导入能力", detail = "未返回导入能力前，不展示伪造入口数量。"),
    FeatureBullet(title = "助记词流程", detail = "选择助记词导入后再进入输入页。"),
    FeatureBullet(title = "导航参数", detail = "当前页面无需额外参数。"),
),
        val note: String = "当前未返回可用导入方式。",
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

    fun importWalletMethodLoadingState(): ImportWalletMethodUiState = ImportWalletMethodUiState(
        isLoading = true,
        summary = "正在同步钱包导入方式与当前钱包状态。",
        primaryActionLabel = "正在同步...",
        secondaryActionLabel = null,
        metrics = emptyList(),
        highlights = emptyList(),
        checklist = emptyList(),
        note = "正在获取导入方式。",
    )

    fun importWalletMethodPreviewState(): ImportWalletMethodUiState = ImportWalletMethodUiState(
        note = "当前未返回可用导入方式。",
    )
