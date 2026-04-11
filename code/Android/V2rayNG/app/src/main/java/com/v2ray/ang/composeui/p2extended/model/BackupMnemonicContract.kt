package com.v2ray.ang.composeui.p2extended.model

import com.v2ray.ang.composeui.navigation.CryptoVpnRouteSpec
import com.v2ray.ang.composeui.navigation.RouteDefinition
import com.v2ray.ang.composeui.common.model.FeatureBullet
import com.v2ray.ang.composeui.common.model.FeatureField
import com.v2ray.ang.composeui.common.model.FeatureListItem
import com.v2ray.ang.composeui.common.model.FeatureMetric

data class BackupMnemonicRouteArgs(val walletId: String = "primary_wallet")

data class BackupMnemonicUiState(
        val title: String = "备份助记词",
        val subtitle: String = "BACKUP MNEMONIC",
        val badge: String = "P2 · EXTENDED",
        val summary: String = "备份助记词页展示助记词分组、风险说明与下一步确认入口。",
        val primaryActionLabel: String? = "进入确认助记词",
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
    FeatureListItem(title = "交付内容", subtitle = "Composable + UiState + Event + ViewModel + Mock Repository 已补齐", trailing = "Ready", badge = "Drop-in"),
),
        val checklist: List<FeatureBullet> = listOf(
    FeatureBullet(title = "ViewModel Stub", detail = "备份助记词 已预留事件分发与 refresh 占位。"),
    FeatureBullet(title = "Mock Repository", detail = "可通过 BackupMnemonicPreviewState / Repository 种子替换真实接口。"),
    FeatureBullet(title = "Preview", detail = "页面已内置 @Preview，可直接在 Android Studio 查看。"),
    FeatureBullet(title = "Navigation Args", detail = "createRoute builder 与 NavGraph 参数解析已补齐。"),
),
        val note: String = "备份助记词 已按 P2 扩展页 页面补齐，可继续替换为真实业务逻辑与接口数据。",
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
