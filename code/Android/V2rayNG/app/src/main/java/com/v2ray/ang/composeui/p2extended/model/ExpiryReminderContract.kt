package com.v2ray.ang.composeui.p2extended.model

import com.v2ray.ang.composeui.navigation.CryptoVpnRouteSpec
import com.v2ray.ang.composeui.navigation.RouteDefinition
import com.v2ray.ang.composeui.common.model.FeatureBullet
import com.v2ray.ang.composeui.common.model.FeatureField
import com.v2ray.ang.composeui.common.model.FeatureListItem
import com.v2ray.ang.composeui.common.model.FeatureMetric

data class ExpiryReminderRouteArgs(val daysLeft: String = "5")

data class ExpiryReminderUiState(
        val title: String = "到期提醒",
        val subtitle: String = "EXPIRY REMINDER",
        val badge: String = "P2 · EXTENDED",
        val summary: String = "到期提醒页用于在续费前提示用户剩余时长、价格与自动续费状态。",
        val primaryActionLabel: String = "查看当前订阅",
        val secondaryActionLabel: String? = "返回套餐页",
        val heroAccent: String = "expiry_reminder",
        val metrics: List<FeatureMetric> = listOf(
    FeatureMetric(label = "剩余天数", value = "5"),
    FeatureMetric(label = "续费金额", value = "US$8.90"),
    FeatureMetric(label = "自动续费", value = "关闭"),
),
        val fields: List<FeatureField> = emptyList(),
        val highlights: List<FeatureListItem> = listOf(
    FeatureListItem(title = "路由标识", subtitle = "到期提醒页用于在续费前提示用户剩余时长、价格与自动续费状态。", trailing = "expiry_reminder", badge = "P2 扩展页"),
    FeatureListItem(title = "导航参数", subtitle = "daysLeft", trailing = "1 个", badge = "Nav"),
    FeatureListItem(title = "表单占位", subtitle = "当前页面以信息展示与确认动作为主", trailing = "0 项", badge = "Info"),
    FeatureListItem(title = "交付内容", subtitle = "Composable + UiState + Event + ViewModel + Mock Repository 已补齐", trailing = "Ready", badge = "Drop-in"),
),
        val checklist: List<FeatureBullet> = listOf(
    FeatureBullet(title = "ViewModel Stub", detail = "到期提醒 已预留事件分发与 refresh 占位。"),
    FeatureBullet(title = "Mock Repository", detail = "可通过 ExpiryReminderPreviewState / Repository 种子替换真实接口。"),
    FeatureBullet(title = "Preview", detail = "页面已内置 @Preview，可直接在 Android Studio 查看。"),
    FeatureBullet(title = "Navigation Args", detail = "createRoute builder 与 NavGraph 参数解析已补齐。"),
),
        val note: String = "到期提醒 已按 P2 扩展页 页面补齐，可继续替换为真实业务逻辑与接口数据。",
    )

    sealed interface ExpiryReminderEvent {
        data object Refresh : ExpiryReminderEvent
        data object PrimaryActionClicked : ExpiryReminderEvent
        data object SecondaryActionClicked : ExpiryReminderEvent
        data class FieldChanged(
            val key: String,
            val value: String,
        ) : ExpiryReminderEvent
    }

    val expiryReminderNavigation: RouteDefinition = CryptoVpnRouteSpec.expiryReminder

    fun expiryReminderPreviewState(): ExpiryReminderUiState = ExpiryReminderUiState()
