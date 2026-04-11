package com.v2ray.ang.composeui.p0.model

import com.v2ray.ang.composeui.navigation.CryptoVpnRouteSpec
import com.v2ray.ang.composeui.navigation.RouteDefinition
import com.v2ray.ang.composeui.common.model.FeatureBullet
import com.v2ray.ang.composeui.common.model.FeatureField
import com.v2ray.ang.composeui.common.model.FeatureListItem
import com.v2ray.ang.composeui.common.model.FeatureMetric

data class OptionalUpdateUiState(
        val title: String = "发现新版本",
        val subtitle: String = "OPTIONAL UPDATE",
        val badge: String = "P0 · CORE",
        val summary: String = "当前只展示真实版本信息，尚未接入应用内更新分发或版本检查接口。",
        val primaryActionLabel: String? = null,
        val secondaryActionLabel: String? = null,
        val heroAccent: String = "optional_update",
        val metrics: List<FeatureMetric> = listOf(
    FeatureMetric(label = "当前状态", value = "仅信息展示"),
    FeatureMetric(label = "更新检查", value = "未接入"),
    FeatureMetric(label = "分发能力", value = "未接入"),
),
        val fields: List<FeatureField> = emptyList(),
        val highlights: List<FeatureListItem> = listOf(
    FeatureListItem(title = "当前能力", subtitle = "展示真实版本与渠道信息，不提供应用内升级动作。", trailing = "信息页", badge = "P0"),
    FeatureListItem(title = "数据来源", subtitle = "当前由真实构建信息驱动，而不是营销文案或预览样本。", trailing = "BuildConfig", badge = "Real"),
    FeatureListItem(title = "阻塞项", subtitle = "缺版本检查接口、缺下载地址、缺安装调度。", trailing = "3 项", badge = "Block"),
),
        val checklist: List<FeatureBullet> = listOf(
    FeatureBullet(title = "真实状态", detail = "当前页只反映真实版本信息与阻塞项，不再假装支持立即升级。"),
    FeatureBullet(title = "动作收敛", detail = "未接独立更新能力前，不再展示误导性的更新按钮。"),
    FeatureBullet(title = "后续方向", detail = "需要补版本检查接口、下载来源和安装拉起能力。"),
),
        val note: String = "该页面当前是版本状态说明页，不代表应用内更新流程已实现。",
    )

    sealed interface OptionalUpdateEvent {
        data object Refresh : OptionalUpdateEvent
        data object PrimaryActionClicked : OptionalUpdateEvent
        data object SecondaryActionClicked : OptionalUpdateEvent
        data class FieldChanged(
            val key: String,
            val value: String,
        ) : OptionalUpdateEvent
    }

    val optionalUpdateNavigation: RouteDefinition = CryptoVpnRouteSpec.optionalUpdate

    fun optionalUpdatePreviewState(): OptionalUpdateUiState = OptionalUpdateUiState()
