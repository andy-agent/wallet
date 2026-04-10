package com.v2ray.ang.composeui.p2.model

import com.v2ray.ang.composeui.navigation.CryptoVpnRouteSpec
import com.v2ray.ang.composeui.navigation.RouteDefinition
import com.v2ray.ang.composeui.common.model.FeatureBullet
import com.v2ray.ang.composeui.common.model.FeatureField
import com.v2ray.ang.composeui.common.model.FeatureListItem
import com.v2ray.ang.composeui.common.model.FeatureMetric

data class ProfileUiState(
        val title: String = "我的",
        val subtitle: String = "PROFILE",
        val badge: String = "P2 · BASE",
        val summary: String = "个人中心聚合账户信息、设备、安全、法务与钱包管理入口。",
        val primaryActionLabel: String = "进入安全中心",
        val secondaryActionLabel: String? = "查看法务文档",
        val heroAccent: String = "profile",
        val metrics: List<FeatureMetric> = listOf(
    FeatureMetric(label = "当前套餐", value = "年费 Pro"),
    FeatureMetric(label = "设备数量", value = "23"),
    FeatureMetric(label = "安全评分", value = "A+"),
),
        val fields: List<FeatureField> = emptyList(),
        val highlights: List<FeatureListItem> = listOf(
    FeatureListItem(title = "路由标识", subtitle = "个人中心聚合账户信息、设备、安全、法务与钱包管理入口。", trailing = "profile", badge = "P2 基础文档页"),
    FeatureListItem(title = "导航参数", subtitle = "当前页面无必填导航参数", trailing = "0 个", badge = "Nav"),
    FeatureListItem(title = "表单占位", subtitle = "当前页面以信息展示与确认动作为主", trailing = "0 项", badge = "Info"),
    FeatureListItem(title = "交付内容", subtitle = "Composable + UiState + Event + ViewModel + Mock Repository 已补齐", trailing = "Ready", badge = "Drop-in"),
),
        val checklist: List<FeatureBullet> = listOf(
    FeatureBullet(title = "ViewModel Stub", detail = "我的 已预留事件分发与 refresh 占位。"),
    FeatureBullet(title = "Mock Repository", detail = "可通过 ProfilePreviewState / Repository 种子替换真实接口。"),
    FeatureBullet(title = "Preview", detail = "页面已内置 @Preview，可直接在 Android Studio 查看。"),
    FeatureBullet(title = "Navigation Args", detail = "当前页面无必填参数，但已纳入统一 RouteSpec 台账。"),
),
        val note: String = "我的 已按 P2 基础文档页 页面补齐，可继续替换为真实业务逻辑与接口数据。",
    )

    sealed interface ProfileEvent {
        data object Refresh : ProfileEvent
        data object PrimaryActionClicked : ProfileEvent
        data object SecondaryActionClicked : ProfileEvent
        data class FieldChanged(
            val key: String,
            val value: String,
        ) : ProfileEvent
    }

    val profileNavigation: RouteDefinition = CryptoVpnRouteSpec.profile

    fun profilePreviewState(): ProfileUiState = ProfileUiState()
