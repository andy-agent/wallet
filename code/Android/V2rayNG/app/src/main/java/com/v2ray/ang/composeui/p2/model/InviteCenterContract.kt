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
    FeatureMetric(label = "累计佣金", value = "$0.00"),
    FeatureMetric(label = "邀请人数", value = "0"),
    FeatureMetric(label = "转化率", value = "0%"),
),
        val fields: List<FeatureField> = emptyList(),
        val highlights: List<FeatureListItem> = emptyList(),
        val checklist: List<FeatureBullet> = emptyList(),
        val note: String = "正在从真实邀请服务同步数据。",
        val banner: P2SurfaceBanner = p2LoadingBanner(),
        val feedbackMessage: String? = null,
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
