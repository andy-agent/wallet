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
        val badge: String = "",
        val summary: String = "",
        val primaryActionLabel: String = "开始备份助记词",
        val secondaryActionLabel: String? = "改用导入方式",
        val heroAccent: String = "create_wallet",
        val metrics: List<FeatureMetric> = emptyList(),
        val fields: List<FeatureField> = listOf(
    FeatureField(key = "name", label = "", value = "", supportingText = "请输入本地展示名称，支持中文。", placeholder = "钱包代号"),
),
        val highlights: List<FeatureListItem> = emptyList(),
        val checklist: List<FeatureBullet> = emptyList(),
        val note: String = "",
        val progressVisible: Boolean = false,
        val progressValue: Float = 0f,
        val progressStageLabel: String = "",
        val progressEtaLabel: String = "预计约 10 秒",
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
        note = "正在获取钱包创建状态。",
    )

    fun createWalletPreviewState(): CreateWalletUiState = CreateWalletUiState(
        note = "当前未返回钱包创建结果。",
    )
