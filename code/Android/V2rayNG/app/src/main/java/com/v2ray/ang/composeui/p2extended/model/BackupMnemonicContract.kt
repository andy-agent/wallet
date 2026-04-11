package com.v2ray.ang.composeui.p2extended.model

import com.v2ray.ang.composeui.common.model.FeatureBullet
import com.v2ray.ang.composeui.common.model.FeatureField
import com.v2ray.ang.composeui.common.model.FeatureListItem
import com.v2ray.ang.composeui.common.model.FeatureMetric
import com.v2ray.ang.composeui.navigation.CryptoVpnRouteSpec
import com.v2ray.ang.composeui.navigation.RouteDefinition

data class BackupMnemonicRouteArgs(val walletId: String = "primary_wallet")

data class BackupMnemonicUiState(
    val title: String = "备份助记词",
    val subtitle: String = "BACKUP MNEMONIC",
    val badge: String = "同步中",
    val summary: String = "正在检查助记词备份材料；若未接通真实钱包引擎，将显示阻塞说明。",
    val primaryActionLabel: String? = null,
    val secondaryActionLabel: String? = null,
    val heroAccent: String = "backup_mnemonic",
    val metrics: List<FeatureMetric> = listOf(
        FeatureMetric(label = "钱包标识", value = "读取中"),
        FeatureMetric(label = "词数", value = "读取中"),
        FeatureMetric(label = "备份状态", value = "读取中"),
    ),
    val fields: List<FeatureField> = emptyList(),
    val highlights: List<FeatureListItem> = listOf(
        FeatureListItem(title = "当前状态", subtitle = "正在检查备份材料", trailing = "", badge = "LOADING"),
    ),
    val checklist: List<FeatureBullet> = listOf(
        FeatureBullet(title = "说明", detail = "默认态不再展示演示助记词计数和假确认入口。"),
    ),
    val note: String = "刷新后会替换为真实状态或明确阻塞说明。",
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

fun backupMnemonicPreviewState(): BackupMnemonicUiState = BackupMnemonicUiState()
