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
        val badge: String = "•4台设备在线。",
        val summary: String = "个人中心聚合账户信息、设备、安全、法务与钱包管理入口。",
        val primaryActionLabel: String? = "进入安全中心",
        val secondaryActionLabel: String? = "进入法务文档",
        val heroAccent: String = "profile",
        val metrics: List<FeatureMetric> = listOf(
    FeatureMetric(label = "当前套餐", value = "年费 Pro"),
    FeatureMetric(label = "剩余天数", value = "23天"),
    FeatureMetric(label = "钱包数量", value = "4个"),
    FeatureMetric(label = "安全级别", value = "A级"),
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
    FeatureBullet(title = "账户信息", detail = "hello@cryptovpn.app · GLOW OPS"),
    FeatureBullet(title = "在线设备", detail = "4台设备在线。"),
    FeatureBullet(title = "套餐", detail = "年费 Pro"),
    FeatureBullet(title = "安全级别", detail = "A级"),
),
        val note: String = "账户、设备安全、订阅与法务入口在这里统一管理。",
        val isLoading: Boolean = false,
        val errorMessage: String? = null,
        val emptyMessage: String? = null,
        val blockerTitle: String? = null,
        val blockerMessage: String? = null,
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
