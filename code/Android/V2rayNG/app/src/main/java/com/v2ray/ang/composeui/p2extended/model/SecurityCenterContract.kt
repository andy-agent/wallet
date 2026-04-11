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
        val summary: String = "安全中心聚合备份状态、设备保护、多签与风险授权入口。",
        val primaryActionLabel: String? = "查看风险授权",
        val secondaryActionLabel: String? = "返回个人中心",
        val heroAccent: String = "security_center",
        val metrics: List<FeatureMetric> = listOf(
    FeatureMetric(label = "助记词", value = "已备份"),
    FeatureMetric(label = "2FA", value = "已开启"),
    FeatureMetric(label = "AES 密钥", value = "2 份"),
),
        val fields: List<FeatureField> = emptyList(),
        val highlights: List<FeatureListItem> = listOf(
    FeatureListItem(title = "路由标识", subtitle = "安全中心聚合备份状态、设备保护、多签与风险授权入口。", trailing = "security_center", badge = "P2 扩展页"),
    FeatureListItem(title = "导航参数", subtitle = "当前页面无必填导航参数", trailing = "0 个", badge = "Nav"),
    FeatureListItem(title = "表单占位", subtitle = "当前页面以信息展示与确认动作为主", trailing = "0 项", badge = "Info"),
    FeatureListItem(title = "交付内容", subtitle = "Composable + UiState + Event + ViewModel + Mock Repository 已补齐", trailing = "Ready", badge = "Drop-in"),
),
        val checklist: List<FeatureBullet> = listOf(
    FeatureBullet(title = "ViewModel Stub", detail = "安全中心 已预留事件分发与 refresh 占位。"),
    FeatureBullet(title = "Mock Repository", detail = "可通过 SecurityCenterPreviewState / Repository 种子替换真实接口。"),
    FeatureBullet(title = "Preview", detail = "页面已内置 @Preview，可直接在 Android Studio 查看。"),
    FeatureBullet(title = "Navigation Args", detail = "当前页面无必填参数，但已纳入统一 RouteSpec 台账。"),
),
        val note: String = "安全中心 已按 P2 扩展页 页面补齐，可继续替换为真实业务逻辑与接口数据。",
    )

    sealed interface SecurityCenterEvent {
        data object Refresh : SecurityCenterEvent
        data object PrimaryActionClicked : SecurityCenterEvent
        data object SecondaryActionClicked : SecurityCenterEvent
        data class FieldChanged(
            val key: String,
            val value: String,
        ) : SecurityCenterEvent
    }

    val securityCenterNavigation: RouteDefinition = CryptoVpnRouteSpec.securityCenter

    fun securityCenterPreviewState(): SecurityCenterUiState = SecurityCenterUiState()
