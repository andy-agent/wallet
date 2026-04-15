package com.v2ray.ang.composeui.p2extended.model

import com.v2ray.ang.composeui.navigation.CryptoVpnRouteSpec
import com.v2ray.ang.composeui.navigation.RouteDefinition
import com.v2ray.ang.composeui.common.model.FeatureBullet
import com.v2ray.ang.composeui.common.model.FeatureField
import com.v2ray.ang.composeui.common.model.FeatureListItem
import com.v2ray.ang.composeui.common.model.FeatureMetric

data class ConfirmMnemonicRouteArgs(val walletId: String = "primary_wallet")

data class ConfirmMnemonicUiState(
        val isLoading: Boolean = false,
        val title: String = "确认助记词",
        val subtitle: String = "CONFIRM MNEMONIC",
        val badge: String = "FINAL CONFIRM",
        val summary: String = "最终确认页用于把钱包生命周期切到可用状态。",
        val primaryActionLabel: String = "完成验证并进入安全中心",
        val secondaryActionLabel: String? = "返回重新查看",
        val heroAccent: String = "confirm_mnemonic",
        val metrics: List<FeatureMetric> = listOf(
    FeatureMetric(label = "已选择", value = "3 / 12"),
    FeatureMetric(label = "错误次数", value = "0"),
    FeatureMetric(label = "当前阶段", value = "Final Gate"),
),
        val fields: List<FeatureField> = emptyList(),
        val highlights: List<FeatureListItem> = listOf(
    FeatureListItem(title = "路由标识", subtitle = "确认助记词页通过抽查顺序验证用户是否完成备份。", trailing = "confirm_mnemonic", badge = "P2 扩展页"),
    FeatureListItem(title = "导航参数", subtitle = "walletId", trailing = "1 个", badge = "Nav"),
    FeatureListItem(title = "表单占位", subtitle = "当前页面以信息展示与确认动作为主", trailing = "0 项", badge = "Info"),
    FeatureListItem(title = "数据源", subtitle = "运行时由真实仓储覆盖，预览仅保留结构默认值。", trailing = "Runtime", badge = "Source"),
),
        val checklist: List<FeatureBullet> = listOf(
    FeatureBullet(title = "运行时激活", detail = "主按钮会把钱包生命周期更新为 ACTIVE。"),
    FeatureBullet(title = "预览默认值", detail = "仅用于 Android Studio 预览，不参与运行时分支。"),
    FeatureBullet(title = "导航参数", detail = "createRoute builder 与 NavGraph 参数解析已补齐。"),
),
        val note: String = "最终确认运行时走真实仓储，默认值只保留预览结构。",
    )

    sealed interface ConfirmMnemonicEvent {
        data object Refresh : ConfirmMnemonicEvent
        data object PrimaryActionClicked : ConfirmMnemonicEvent
        data object SecondaryActionClicked : ConfirmMnemonicEvent
        data class FieldChanged(
            val key: String,
            val value: String,
        ) : ConfirmMnemonicEvent
    }

    val confirmMnemonicNavigation: RouteDefinition = CryptoVpnRouteSpec.confirmMnemonic

    fun confirmMnemonicLoadingState(): ConfirmMnemonicUiState = ConfirmMnemonicUiState(
        isLoading = true,
        summary = "正在同步最终确认状态。",
        primaryActionLabel = "正在同步...",
        secondaryActionLabel = null,
        metrics = emptyList(),
        highlights = emptyList(),
        checklist = emptyList(),
        note = "当前页面会在拿到真实钱包生命周期后再展示最终确认内容。",
    )

    fun confirmMnemonicPreviewState(): ConfirmMnemonicUiState = ConfirmMnemonicUiState(
        metrics = listOf(
            FeatureMetric(label = "已选择", value = "3 / 12"),
            FeatureMetric(label = "错误次数", value = "0"),
            FeatureMetric(label = "当前阶段", value = "Final Gate"),
        ),
        highlights = listOf(
            FeatureListItem(title = "路由标识", subtitle = "确认助记词页通过抽查顺序验证用户是否完成备份。", trailing = "confirm_mnemonic", badge = "P2 扩展页"),
            FeatureListItem(title = "导航参数", subtitle = "walletId", trailing = "1 个", badge = "Nav"),
            FeatureListItem(title = "表单占位", subtitle = "当前页面以信息展示与确认动作为主", trailing = "0 项", badge = "Info"),
            FeatureListItem(title = "数据源", subtitle = "运行时由真实仓储覆盖，预览仅保留结构默认值。", trailing = "Runtime", badge = "Source"),
        ),
        checklist = listOf(
            FeatureBullet(title = "运行时激活", detail = "主按钮会把钱包生命周期更新为 ACTIVE。"),
            FeatureBullet(title = "预览默认值", detail = "仅用于 Android Studio 预览，不参与运行时分支。"),
            FeatureBullet(title = "导航参数", detail = "createRoute builder 与 NavGraph 参数解析已补齐。"),
        ),
        note = "最终确认运行时走真实仓储，默认值只保留预览结构。",
    )
