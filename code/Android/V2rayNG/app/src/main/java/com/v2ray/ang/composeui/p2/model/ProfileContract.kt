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
        val badge: String = "",
        val summary: String = "个人中心聚合账户信息、设备、安全、法务与钱包管理入口。",
        val primaryActionLabel: String = "进入安全中心",
        val secondaryActionLabel: String? = "进入法务文档",
        val heroAccent: String = "profile",
        val metrics: List<FeatureMetric> = listOf(
    FeatureMetric(label = "当前套餐", value = "--"),
    FeatureMetric(label = "订单数", value = "0"),
    FeatureMetric(label = "账户状态", value = "--"),
),
        val fields: List<FeatureField> = emptyList(),
        val highlights: List<FeatureListItem> = listOf(
    FeatureListItem(title = "安全中心", subtitle = "助记词、设备、签名授权", trailing = "进入"),
    FeatureListItem(title = "订单与订阅", subtitle = "查看支付与续费记录", trailing = "进入"),
    FeatureListItem(title = "邀请中心", subtitle = "推广链接与佣金收入", trailing = "进入"),
    FeatureListItem(title = "法务文档", subtitle = "服务协议、隐私与免责声明", trailing = "进入"),
    FeatureListItem(title = "关于应用", subtitle = "版本、更新与帮助", trailing = "进入"),
),
        val checklist: List<FeatureBullet> = listOf(
    FeatureBullet(title = "账户信息", detail = ""),
),
        val note: String = "",
        val banner: P2SurfaceBanner = p2ReadyBanner(),
        val feedbackMessage: String? = null,
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
