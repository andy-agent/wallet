package com.v2ray.ang.composeui.p2extended.model

import com.v2ray.ang.composeui.navigation.CryptoVpnRouteSpec
import com.v2ray.ang.composeui.navigation.RouteDefinition
import com.v2ray.ang.composeui.common.model.FeatureBullet
import com.v2ray.ang.composeui.common.model.FeatureField
import com.v2ray.ang.composeui.common.model.FeatureListItem
import com.v2ray.ang.composeui.common.model.FeatureMetric

data class BackupMnemonicRouteArgs(val walletId: String = "primary_wallet")

data class BackupMnemonicUiState(
        val isLoading: Boolean = false,
        val title: String = "备份助记词",
        val subtitle: String = "BACKUP MNEMONIC",
        val badge: String = "BACKUP CHECK",
        val summary: String = "备份确认页用于把钱包生命周期推进到下一步。",
        val primaryActionLabel: String = "进入确认助记词",
        val secondaryActionLabel: String? = "返回创建钱包",
        val heroAccent: String = "backup_mnemonic",
        val metrics: List<FeatureMetric> = listOf(
    FeatureMetric(label = "词数", value = "12"),
    FeatureMetric(label = "已确认", value = "0 / 12"),
    FeatureMetric(label = "风险", value = "高"),
),
        val fields: List<FeatureField> = emptyList(),
        val highlights: List<FeatureListItem> = listOf(
    FeatureListItem(title = "路由标识", subtitle = "备份助记词页展示助记词分组、风险说明与下一步确认入口。", trailing = "backup_mnemonic", badge = "P2 扩展页"),
    FeatureListItem(title = "导航参数", subtitle = "walletId", trailing = "1 个", badge = "Nav"),
    FeatureListItem(title = "表单占位", subtitle = "当前页面以信息展示与确认动作为主", trailing = "0 项", badge = "Info"),
    FeatureListItem(title = "数据源", subtitle = "运行时由真实仓储覆盖，预览仅保留结构默认值。", trailing = "Runtime", badge = "Source"),
),
        val checklist: List<FeatureBullet> = listOf(
    FeatureBullet(title = "运行时确认", detail = "主按钮会确认已完成备份并推进生命周期。"),
    FeatureBullet(title = "预览默认值", detail = "仅用于 Android Studio 预览，不参与运行时分支。"),
    FeatureBullet(title = "导航参数", detail = "createRoute builder 与 NavGraph 参数解析已补齐。"),
),
        val note: String = "备份确认运行时走真实仓储，默认值只保留预览结构。",
    )

    sealed interface BackupMnemonicEvent {
        data object Refresh : BackupMnemonicEvent
        data object PrimaryActionClicked : BackupMnemonicEvent
        data object SecondaryActionClicked : BackupMnemonicEvent
        data class FieldChanged(
            val key: String,
            val value: String,
        ) : BackupMnemonicEvent
    }

    val backupMnemonicNavigation: RouteDefinition = CryptoVpnRouteSpec.backupMnemonic

    fun backupMnemonicLoadingState(): BackupMnemonicUiState = BackupMnemonicUiState(
        isLoading = true,
        summary = "正在同步备份确认状态。",
        primaryActionLabel = "正在同步...",
        secondaryActionLabel = null,
        metrics = emptyList(),
        highlights = emptyList(),
        checklist = emptyList(),
        note = "当前页面会在拿到真实钱包生命周期后再展示备份确认内容。",
    )

    fun backupMnemonicPreviewState(): BackupMnemonicUiState = BackupMnemonicUiState(
        metrics = listOf(
            FeatureMetric(label = "词数", value = "12"),
            FeatureMetric(label = "已确认", value = "0 / 12"),
            FeatureMetric(label = "风险", value = "高"),
        ),
        highlights = listOf(
            FeatureListItem(title = "路由标识", subtitle = "备份助记词页展示助记词分组、风险说明与下一步确认入口。", trailing = "backup_mnemonic", badge = "P2 扩展页"),
            FeatureListItem(title = "导航参数", subtitle = "walletId", trailing = "1 个", badge = "Nav"),
            FeatureListItem(title = "表单占位", subtitle = "当前页面以信息展示与确认动作为主", trailing = "0 项", badge = "Info"),
            FeatureListItem(title = "数据源", subtitle = "运行时由真实仓储覆盖，预览仅保留结构默认值。", trailing = "Runtime", badge = "Source"),
        ),
        checklist = listOf(
            FeatureBullet(title = "运行时确认", detail = "主按钮会确认已完成备份并推进生命周期。"),
            FeatureBullet(title = "预览默认值", detail = "仅用于 Android Studio 预览，不参与运行时分支。"),
            FeatureBullet(title = "导航参数", detail = "createRoute builder 与 NavGraph 参数解析已补齐。"),
        ),
        note = "备份确认运行时走真实仓储，默认值只保留预览结构。",
    )
