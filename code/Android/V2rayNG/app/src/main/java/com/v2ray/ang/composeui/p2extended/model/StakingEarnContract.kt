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
        val summary: String = "质押收益数据待接口返回。",
        val primaryActionLabel: String = "返回钱包首页",
        val secondaryActionLabel: String? = "查看资产详情",
        val heroAccent: String = "staking_earn",
        val metrics: List<FeatureMetric> = listOf(
    FeatureMetric(label = "APR", value = "待接口返回"),
    FeatureMetric(label = "已质押", value = "待接口返回"),
    FeatureMetric(label = "待领取", value = "待接口返回"),
),
        val fields: List<FeatureField> = emptyList(),
        val highlights: List<FeatureListItem> = listOf(
    FeatureListItem(title = "质押状态", subtitle = "质押仓位与收益待接口返回", trailing = "待接口返回", badge = "Runtime"),
    FeatureListItem(title = "收益信息", subtitle = "APR 和待领取收益待接口返回", trailing = "待同步", badge = "Yield"),
    FeatureListItem(title = "能力状态", subtitle = "未接入质押接口时保持阻塞态", trailing = "能力暂未接入", badge = "Blocked"),
),
        val checklist: List<FeatureBullet> = listOf(
    FeatureBullet(title = "数据来源", detail = "APR、持仓和收益必须来自真实质押接口。"),
    FeatureBullet(title = "阻塞策略", detail = "能力未接入时显示能力暂未接入，不展示演示收益。"),
    FeatureBullet(title = "异常处理", detail = "接口失败时应展示真实错误。"),
    FeatureBullet(title = "空态策略", detail = "无质押仓位时显示当前无数据。"),
),
        val note: String = "质押赚币能力尚未返回真实收益数据。",
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
