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
        val badge: String = "WALLET IMPORT",
        val summary: String = "导入钱包方式页提供助记词、私钥等入口，并标记风险提示。",
        val primaryActionLabel: String = "使用助记词导入",
        val secondaryActionLabel: String? = "返回钱包引导",
        val heroAccent: String = "import_wallet_method",
        val metrics: List<FeatureMetric> = listOf(
    FeatureMetric(label = "导入方式", value = "3 种"),
    FeatureMetric(label = "支持链", value = "7 条"),
    FeatureMetric(label = "恢复速度", value = "1 分钟"),
),
        val fields: List<FeatureField> = emptyList(),
        val highlights: List<FeatureListItem> = listOf(
    FeatureListItem(title = "路由标识", subtitle = "导入钱包方式页提供助记词、私钥等入口，并标记风险提示。", trailing = "import_wallet_method", badge = "P2 扩展页"),
    FeatureListItem(title = "导航参数", subtitle = "当前页面无必填导航参数", trailing = "0 个", badge = "Nav"),
    FeatureListItem(title = "表单占位", subtitle = "当前页面以信息展示与确认动作为主", trailing = "0 项", badge = "Info"),
    FeatureListItem(title = "数据源", subtitle = "运行时由真实仓储覆盖，预览仅保留结构默认值。", trailing = "Runtime", badge = "Source"),
),
        val checklist: List<FeatureBullet> = listOf(
    FeatureBullet(title = "运行时导入", detail = "主按钮会继续进入真实助记词导入流程。"),
    FeatureBullet(title = "预览默认值", detail = "仅用于 Android Studio 预览，不参与运行时分支。"),
    FeatureBullet(title = "导航参数", detail = "当前页面无必填参数，但已纳入统一 RouteSpec 台账。"),
),
        val note: String = "导入钱包方式运行时走真实仓储，默认值只保留预览结构。",
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
        note = "当前页面会在拿到真实账户与钱包生命周期后再展示导入信息。",
    )

    fun importWalletMethodPreviewState(): ImportWalletMethodUiState = ImportWalletMethodUiState(
        metrics = listOf(
            FeatureMetric(label = "导入方式", value = "3 种"),
            FeatureMetric(label = "支持链", value = "7 条"),
            FeatureMetric(label = "恢复速度", value = "1 分钟"),
        ),
        highlights = listOf(
            FeatureListItem(title = "路由标识", subtitle = "导入钱包方式页提供助记词、私钥等入口，并标记风险提示。", trailing = "import_wallet_method", badge = "P2 扩展页"),
            FeatureListItem(title = "导航参数", subtitle = "当前页面无必填导航参数", trailing = "0 个", badge = "Nav"),
            FeatureListItem(title = "表单占位", subtitle = "当前页面以信息展示与确认动作为主", trailing = "0 项", badge = "Info"),
            FeatureListItem(title = "数据源", subtitle = "运行时由真实仓储覆盖，预览仅保留结构默认值。", trailing = "Runtime", badge = "Source"),
        ),
        checklist = listOf(
            FeatureBullet(title = "运行时导入", detail = "主按钮会继续进入真实助记词导入流程。"),
            FeatureBullet(title = "预览默认值", detail = "仅用于 Android Studio 预览，不参与运行时分支。"),
            FeatureBullet(title = "导航参数", detail = "当前页面无必填参数，但已纳入统一 RouteSpec 台账。"),
        ),
        note = "导入钱包方式运行时走真实仓储，默认值只保留预览结构。",
    )
