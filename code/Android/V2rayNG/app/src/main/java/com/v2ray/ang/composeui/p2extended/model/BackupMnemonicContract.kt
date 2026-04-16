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
        val badge: String = "待备份",
        val summary: String = "等待钱包返回可备份的助记词数据。",
        val primaryActionLabel: String = "进入确认助记词",
        val secondaryActionLabel: String? = "返回创建钱包",
        val heroAccent: String = "backup_mnemonic",
        val metrics: List<FeatureMetric> = listOf(
    FeatureMetric(label = "词数", value = "待返回"),
    FeatureMetric(label = "备份状态", value = "未开始"),
    FeatureMetric(label = "风险提示", value = "待返回"),
),
        val fields: List<FeatureField> = emptyList(),
        val highlights: List<FeatureListItem> = listOf(
    FeatureListItem(title = "备份状态", subtitle = "等待钱包返回助记词与风险提示。", trailing = "未开始", badge = "State"),
    FeatureListItem(title = "导航参数", subtitle = "walletId", trailing = "1 个", badge = "Nav"),
    FeatureListItem(title = "表单状态", subtitle = "当前页面仅展示备份内容", trailing = "0 项", badge = "Info"),
    FeatureListItem(title = "数据来源", subtitle = "由钱包备份流程实时返回。", trailing = "Runtime", badge = "Source"),
),
        val checklist: List<FeatureBullet> = listOf(
    FeatureBullet(title = "助记词返回", detail = "未返回真实助记词前，不展示伪造词序。"),
    FeatureBullet(title = "备份确认", detail = "确认备份后再进入下一步校验。"),
    FeatureBullet(title = "导航参数", detail = "根据 walletId 读取当前钱包。"),
),
        val note: String = "当前未返回助记词备份数据。",
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
        note = "正在获取助记词备份数据。",
    )

    fun backupMnemonicPreviewState(): BackupMnemonicUiState = BackupMnemonicUiState(
        note = "当前未返回助记词备份数据。",
    )
