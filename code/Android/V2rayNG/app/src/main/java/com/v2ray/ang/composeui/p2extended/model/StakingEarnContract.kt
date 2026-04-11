package com.v2ray.ang.composeui.p2extended.model

import com.v2ray.ang.composeui.common.model.FeatureBullet
import com.v2ray.ang.composeui.common.model.FeatureField
import com.v2ray.ang.composeui.common.model.FeatureListItem
import com.v2ray.ang.composeui.common.model.FeatureMetric
import com.v2ray.ang.composeui.navigation.CryptoVpnRouteSpec
import com.v2ray.ang.composeui.navigation.RouteDefinition

data class StakingEarnUiState(
    val title: String = "质押赚币",
    val subtitle: String = "STAKING EARN",
    val badge: String = "同步中",
    val summary: String = "正在读取质押数据源；若未接 Earn/Staking 能力，将显示空态或阻塞说明。",
    val primaryActionLabel: String? = null,
    val secondaryActionLabel: String? = null,
    val heroAccent: String = "staking_earn",
    val metrics: List<FeatureMetric> = listOf(
        FeatureMetric(label = "APR", value = "读取中"),
        FeatureMetric(label = "已质押", value = "读取中"),
        FeatureMetric(label = "待领取", value = "读取中"),
    ),
    val fields: List<FeatureField> = emptyList(),
    val highlights: List<FeatureListItem> = listOf(
        FeatureListItem(title = "当前状态", subtitle = "正在读取质押数据源", trailing = "", badge = "LOADING"),
    ),
    val checklist: List<FeatureBullet> = listOf(
        FeatureBullet(title = "说明", detail = "默认态不再展示演示 APR、收益和假入口。"),
    ),
    val note: String = "刷新后会替换为真实状态、空态或明确阻塞说明。",
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
