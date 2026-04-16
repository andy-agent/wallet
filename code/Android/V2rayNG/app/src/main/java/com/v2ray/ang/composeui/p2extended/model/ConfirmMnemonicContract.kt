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
        val badge: String = "待验证",
        val summary: String = "等待助记词确认题目与验证结果返回。",
        val primaryActionLabel: String = "完成验证并进入安全中心",
        val secondaryActionLabel: String? = "返回重新查看",
        val heroAccent: String = "confirm_mnemonic",
        val metrics: List<FeatureMetric> = listOf(
    FeatureMetric(label = "已选择", value = "0"),
    FeatureMetric(label = "错误次数", value = "0"),
    FeatureMetric(label = "验证状态", value = "未开始"),
),
        val fields: List<FeatureField> = emptyList(),
        val highlights: List<FeatureListItem> = listOf(
    FeatureListItem(title = "验证状态", subtitle = "等待确认题目与当前进度返回。", trailing = "未开始", badge = "State"),
    FeatureListItem(title = "导航参数", subtitle = "walletId", trailing = "1 个", badge = "Nav"),
    FeatureListItem(title = "表单状态", subtitle = "当前页面展示确认流程", trailing = "0 项", badge = "Info"),
    FeatureListItem(title = "数据来源", subtitle = "由助记词确认流程实时返回。", trailing = "Runtime", badge = "Source"),
),
        val checklist: List<FeatureBullet> = listOf(
    FeatureBullet(title = "确认题目", detail = "未返回真实抽查题目前，不展示伪造顺序。"),
    FeatureBullet(title = "钱包激活", detail = "验证通过后再更新钱包状态。"),
    FeatureBullet(title = "导航参数", detail = "根据 walletId 读取当前确认流程。"),
),
        val note: String = "当前未返回助记词确认数据。",
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
        note = "正在获取助记词确认数据。",
    )

    fun confirmMnemonicPreviewState(): ConfirmMnemonicUiState = ConfirmMnemonicUiState(
        note = "当前未返回助记词确认数据。",
    )
