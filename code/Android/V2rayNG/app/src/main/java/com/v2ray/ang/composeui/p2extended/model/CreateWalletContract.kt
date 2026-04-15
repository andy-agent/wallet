package com.v2ray.ang.composeui.p2extended.model

import com.v2ray.ang.composeui.navigation.CryptoVpnRouteSpec
import com.v2ray.ang.composeui.navigation.RouteDefinition
import com.v2ray.ang.composeui.common.model.FeatureBullet
import com.v2ray.ang.composeui.common.model.FeatureField
import com.v2ray.ang.composeui.common.model.FeatureListItem
import com.v2ray.ang.composeui.common.model.FeatureMetric

data class CreateWalletRouteArgs(val mode: String = "create")

data class CreateWalletUiState(
        val isLoading: Boolean = false,
        val title: String = "创建钱包",
        val subtitle: String = "CREATE WALLET",
        val badge: String = "WALLET SETUP",
        val summary: String = "创建钱包页承接钱包命名与钱包生命周期建立。",
        val primaryActionLabel: String = "开始备份助记词",
        val secondaryActionLabel: String? = "改用导入方式",
        val heroAccent: String = "create_wallet",
        val metrics: List<FeatureMetric> = listOf(
    FeatureMetric(label = "默认链", value = "7 条"),
    FeatureMetric(label = "备份要求", value = "必须"),
    FeatureMetric(label = "模式", value = "Create"),
),
        val fields: List<FeatureField> = listOf(
    FeatureField(key = "name", label = "钱包名称", value = "Primary Wallet", supportingText = "仅作为本地钱包展示名称"),
),
        val highlights: List<FeatureListItem> = listOf(
    FeatureListItem(title = "路由标识", subtitle = "创建钱包页承接钱包命名、多链初始化与备份前校验。", trailing = "create_wallet", badge = "P2 扩展页"),
    FeatureListItem(title = "导航参数", subtitle = "mode", trailing = "1 个", badge = "Nav"),
    FeatureListItem(title = "表单占位", subtitle = "钱包名称", trailing = "1 项", badge = "Form"),
    FeatureListItem(title = "数据源", subtitle = "运行时由真实仓储覆盖，预览仅保留结构默认值。", trailing = "Runtime", badge = "Source"),
),
        val checklist: List<FeatureBullet> = listOf(
    FeatureBullet(title = "运行时创建", detail = "主按钮会触发真实钱包生命周期写入。"),
    FeatureBullet(title = "预览默认值", detail = "仅用于 Android Studio 预览，不参与运行时分支。"),
    FeatureBullet(title = "导航参数", detail = "createRoute builder 与 NavGraph 参数解析已补齐。"),
),
        val note: String = "创建钱包运行时走真实仓储，默认值只保留预览结构。",
    )

    sealed interface CreateWalletEvent {
        data object Refresh : CreateWalletEvent
        data object PrimaryActionClicked : CreateWalletEvent
        data object SecondaryActionClicked : CreateWalletEvent
        data class FieldChanged(
            val key: String,
            val value: String,
        ) : CreateWalletEvent
    }

    val createWalletNavigation: RouteDefinition = CryptoVpnRouteSpec.createWallet

    fun createWalletLoadingState(): CreateWalletUiState = CreateWalletUiState(
        isLoading = true,
        summary = "正在同步钱包创建状态。",
        primaryActionLabel = "正在同步...",
        secondaryActionLabel = null,
        metrics = emptyList(),
        fields = emptyList(),
        highlights = emptyList(),
        checklist = emptyList(),
        note = "当前页面会在拿到真实钱包生命周期后再展示表单与动作。",
    )

    fun createWalletPreviewState(): CreateWalletUiState = CreateWalletUiState(
        metrics = listOf(
            FeatureMetric(label = "默认链", value = "7 条"),
            FeatureMetric(label = "备份要求", value = "必须"),
            FeatureMetric(label = "模式", value = "Create"),
        ),
        fields = listOf(
            FeatureField(key = "name", label = "钱包名称", value = "Primary Wallet", supportingText = "仅作为本地钱包展示名称"),
        ),
        highlights = listOf(
            FeatureListItem(title = "路由标识", subtitle = "创建钱包页承接钱包命名、多链初始化与备份前校验。", trailing = "create_wallet", badge = "P2 扩展页"),
            FeatureListItem(title = "导航参数", subtitle = "mode", trailing = "1 个", badge = "Nav"),
            FeatureListItem(title = "表单占位", subtitle = "钱包名称", trailing = "1 项", badge = "Form"),
            FeatureListItem(title = "数据源", subtitle = "运行时由真实仓储覆盖，预览仅保留结构默认值。", trailing = "Runtime", badge = "Source"),
        ),
        checklist = listOf(
            FeatureBullet(title = "运行时创建", detail = "主按钮会触发真实钱包生命周期写入。"),
            FeatureBullet(title = "预览默认值", detail = "仅用于 Android Studio 预览，不参与运行时分支。"),
            FeatureBullet(title = "导航参数", detail = "createRoute builder 与 NavGraph 参数解析已补齐。"),
        ),
        note = "创建钱包运行时走真实仓储，默认值只保留预览结构。",
    )
