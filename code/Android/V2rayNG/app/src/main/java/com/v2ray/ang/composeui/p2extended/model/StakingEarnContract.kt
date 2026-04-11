package com.v2ray.ang.composeui.p2extended.model

import com.v2ray.ang.composeui.navigation.CryptoVpnRouteSpec
import com.v2ray.ang.composeui.navigation.RouteDefinition
import com.v2ray.ang.composeui.common.model.FeatureBullet
import com.v2ray.ang.composeui.common.model.FeatureField
import com.v2ray.ang.composeui.common.model.FeatureListItem
import com.v2ray.ang.composeui.common.model.FeatureMetric

data class StakingEarnUiState(
        val title: String = "质押赚币",
        val subtitle: String = "STAKING EARN",
        val badge: String = "P2 · EXTENDED",
        val summary: String = "质押赚币页展示 APR、已质押金额与待领取收益。",
        val primaryActionLabel: String? = "返回钱包首页",
        val secondaryActionLabel: String? = "查看资产详情",
        val heroAccent: String = "staking_earn",
        val metrics: List<FeatureMetric> = listOf(
    FeatureMetric(label = "APR", value = "8.4%"),
    FeatureMetric(label = "已质押", value = "1,280 USDT"),
    FeatureMetric(label = "待领取", value = "24.6 USDT"),
),
        val fields: List<FeatureField> = emptyList(),
        val highlights: List<FeatureListItem> = listOf(
    FeatureListItem(title = "路由标识", subtitle = "质押赚币页展示 APR、已质押金额与待领取收益。", trailing = "staking_earn", badge = "P2 扩展页"),
    FeatureListItem(title = "导航参数", subtitle = "当前页面无必填导航参数", trailing = "0 个", badge = "Nav"),
    FeatureListItem(title = "表单占位", subtitle = "当前页面以信息展示与确认动作为主", trailing = "0 项", badge = "Info"),
    FeatureListItem(title = "交付内容", subtitle = "Composable + UiState + Event + ViewModel + Mock Repository 已补齐", trailing = "Ready", badge = "Drop-in"),
),
        val checklist: List<FeatureBullet> = listOf(
    FeatureBullet(title = "ViewModel Stub", detail = "质押赚币 已预留事件分发与 refresh 占位。"),
    FeatureBullet(title = "Mock Repository", detail = "可通过 StakingEarnPreviewState / Repository 种子替换真实接口。"),
    FeatureBullet(title = "Preview", detail = "页面已内置 @Preview，可直接在 Android Studio 查看。"),
    FeatureBullet(title = "Navigation Args", detail = "当前页面无必填参数，但已纳入统一 RouteSpec 台账。"),
),
        val note: String = "质押赚币 已按 P2 扩展页 页面补齐，可继续替换为真实业务逻辑与接口数据。",
    )

    sealed interface StakingEarnEvent {
        data object Refresh : StakingEarnEvent
        data object PrimaryActionClicked : StakingEarnEvent
        data object SecondaryActionClicked : StakingEarnEvent
        data class FieldChanged(
            val key: String,
            val value: String,
        ) : StakingEarnEvent
    }

    val stakingEarnNavigation: RouteDefinition = CryptoVpnRouteSpec.stakingEarn

    fun stakingEarnPreviewState(): StakingEarnUiState = StakingEarnUiState()
