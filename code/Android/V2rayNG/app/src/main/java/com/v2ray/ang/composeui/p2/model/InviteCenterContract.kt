package com.v2ray.ang.composeui.p2.model

import com.v2ray.ang.composeui.navigation.CryptoVpnRouteSpec
import com.v2ray.ang.composeui.navigation.RouteDefinition
import com.v2ray.ang.composeui.common.model.FeatureBullet
import com.v2ray.ang.composeui.common.model.FeatureField
import com.v2ray.ang.composeui.common.model.FeatureListItem
import com.v2ray.ang.composeui.common.model.FeatureMetric

data class InviteCenterUiState(
        val title: String = "邀请中心",
        val subtitle: String = "GROWTH",
        val badge: String = "• 一级 25%/二级 5%",
        val summary: String = "邀请中心聚合邀请码、邀请人数、转化率与佣金入口。",
        val primaryActionLabel: String = "复制邀请码",
        val secondaryActionLabel: String? = "分享推广链接",
        val heroAccent: String = "invite_center",
        val metrics: List<FeatureMetric> = listOf(
    FeatureMetric(label = "累计佣金", value = "$3,480.20"),
    FeatureMetric(label = "待提现", value = "$580.00"),
    FeatureMetric(label = "本月新增", value = "42人"),
    FeatureMetric(label = "转化率", value = "18.6%"),
),
        val fields: List<FeatureField> = emptyList(),
        val highlights: List<FeatureListItem> = listOf(
    FeatureListItem(title = "CVPN-2025-GLOW", subtitle = "可复制链接、二维码或分享给好友。", trailing = "复制邀请码"),
    FeatureListItem(title = "一级分佣", subtitle = "25%", trailing = "二级分佣 5%"),
    FeatureListItem(title = "分享推广链接", subtitle = "今日点击 268", trailing = "进入"),
),
        val checklist: List<FeatureBullet> = listOf(
    FeatureBullet(title = "邀请收益总览", detail = "邀请新用户购买套餐后，收益将进入你的 Growth 账户。"),
    FeatureBullet(title = "累计佣金", detail = "$3,480.20"),
    FeatureBullet(title = "待提现", detail = "$580.00"),
    FeatureBullet(title = "转化率", detail = "18.6%"),
),
        val note: String = "推广加密网络服务，佣金可直接提到你的多链钱包。",
    )

    sealed interface InviteCenterEvent {
        data object Refresh : InviteCenterEvent
        data object PrimaryActionClicked : InviteCenterEvent
        data object SecondaryActionClicked : InviteCenterEvent
        data class FieldChanged(
            val key: String,
            val value: String,
        ) : InviteCenterEvent
    }

    val inviteCenterNavigation: RouteDefinition = CryptoVpnRouteSpec.inviteCenter

    fun inviteCenterPreviewState(): InviteCenterUiState = InviteCenterUiState()
