package com.v2ray.ang.composeui.p0.model

import com.v2ray.ang.composeui.navigation.CryptoVpnRouteSpec
import com.v2ray.ang.composeui.navigation.RouteDefinition
import com.v2ray.ang.composeui.common.model.FeatureBullet
import com.v2ray.ang.composeui.common.model.FeatureField
import com.v2ray.ang.composeui.common.model.FeatureListItem
import com.v2ray.ang.composeui.common.model.FeatureMetric

data class ForceUpdateUiState(
        val title: String = "强制更新",
        val subtitle: String = "FORCE UPDATE",
        val badge: String = "P0 · CORE",
        val summary: String = "当前只展示真实版本与渠道信息，尚未接入强制升级分发或拦截策略。",
        val primaryActionLabel: String? = null,
        val secondaryActionLabel: String? = null,
        val heroAccent: String = "force_update",
        val metrics: List<FeatureMetric> = listOf(
    FeatureMetric(label = "当前状态", value = "仅信息展示"),
    FeatureMetric(label = "拦截能力", value = "未接入"),
    FeatureMetric(label = "分发能力", value = "未接入"),
),
        val fields: List<FeatureField> = emptyList(),
        val highlights: List<FeatureListItem> = listOf(
    FeatureListItem(title = "当前能力", subtitle = "展示真实版本状态，不代表服务端已经启用强制升级封禁。", trailing = "说明页", badge = "P0"),
    FeatureListItem(title = "数据来源", subtitle = "当前由真实构建信息驱动，而不是固定目标版本样本。", trailing = "BuildConfig", badge = "Real"),
    FeatureListItem(title = "阻塞项", subtitle = "缺后端最小版本接口、缺下载地址、缺升级安装流程。", trailing = "3 项", badge = "Block"),
),
        val checklist: List<FeatureBullet> = listOf(
    FeatureBullet(title = "真实状态", detail = "当前页只反映版本状态与阻塞项，不再伪装成已经可升级。"),
    FeatureBullet(title = "动作收敛", detail = "在缺下载与安装能力前，不再展示假的升级按钮。"),
    FeatureBullet(title = "后续方向", detail = "需要最小版本接口、安装来源与升级调度。"),
),
        val note: String = "该页面当前不是可执行升级流程，只是阻塞说明页。",
    )

    sealed interface ForceUpdateEvent {
        data object Refresh : ForceUpdateEvent
        data object PrimaryActionClicked : ForceUpdateEvent
        data object SecondaryActionClicked : ForceUpdateEvent
        data class FieldChanged(
            val key: String,
            val value: String,
        ) : ForceUpdateEvent
    }

    val forceUpdateNavigation: RouteDefinition = CryptoVpnRouteSpec.forceUpdate

    fun forceUpdatePreviewState(): ForceUpdateUiState = ForceUpdateUiState()
