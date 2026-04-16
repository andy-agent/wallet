package com.v2ray.ang.composeui.p2extended.model

import com.v2ray.ang.composeui.navigation.CryptoVpnRouteSpec
import com.v2ray.ang.composeui.navigation.RouteDefinition
import com.v2ray.ang.composeui.common.model.FeatureBullet
import com.v2ray.ang.composeui.common.model.FeatureField
import com.v2ray.ang.composeui.common.model.FeatureListItem
import com.v2ray.ang.composeui.common.model.FeatureMetric

data class SecurityCenterUiState(
        val title: String = "安全中心",
        val subtitle: String = "SECURITY CENTER",
        val badge: String = "P2 · EXTENDED",
        val summary: String = "安全中心汇总备份状态、设备保护与风险授权入口；未接入项需显式标记。",
        val primaryActionLabel: String? = "导出加密备份",
        val secondaryActionLabel: String? = "退出登录",
        val destructiveActionLabel: String? = "清除本地钱包",
        val heroAccent: String = "security_center",
        val localWalletId: String? = null,
        val localWalletPresent: Boolean = false,
        val metrics: List<FeatureMetric> = listOf(
    FeatureMetric(label = "助记词", value = "状态待同步"),
    FeatureMetric(label = "2FA", value = "状态待同步"),
    FeatureMetric(label = "风险授权", value = "待接入"),
),
        val fields: List<FeatureField> = emptyList(),
        val highlights: List<FeatureListItem> = listOf(
    FeatureListItem(title = "备份状态", subtitle = "助记词、密码与恢复方式以后端同步状态为准。", trailing = "待同步", badge = "状态"),
    FeatureListItem(title = "设备保护", subtitle = "设备风控与多端登录记录待接入。", trailing = "待接入", badge = "风控"),
    FeatureListItem(title = "风险授权", subtitle = "高风险授权需在真实链上会话接入后展示。", trailing = "阻塞", badge = "授权"),
),
        val checklist: List<FeatureBullet> = listOf(
    FeatureBullet(title = "备份校验", detail = "仅展示真实恢复状态，未接入时保持空态。"),
    FeatureBullet(title = "设备审计", detail = "设备列表与最近登录来源待后端返回。"),
    FeatureBullet(title = "授权撤销", detail = "链上授权撤销能力未接入前不展示默认数字。"),
),
        val note: String = "当前仅展示真实状态或明确阻塞态，不再注入默认填充值。",
    )

    sealed interface SecurityCenterEvent {
        data object Refresh : SecurityCenterEvent
        data object PrimaryActionClicked : SecurityCenterEvent
        data object SecondaryActionClicked : SecurityCenterEvent
        data object DestructiveActionClicked : SecurityCenterEvent
        data class FieldChanged(
            val key: String,
            val value: String,
        ) : SecurityCenterEvent
    }

    val securityCenterNavigation: RouteDefinition = CryptoVpnRouteSpec.securityCenter

    fun securityCenterPreviewState(): SecurityCenterUiState = SecurityCenterUiState()
