package com.v2ray.ang.composeui.p2extended.model

import com.v2ray.ang.composeui.common.model.FeatureBullet
import com.v2ray.ang.composeui.common.model.FeatureField
import com.v2ray.ang.composeui.common.model.FeatureListItem
import com.v2ray.ang.composeui.common.model.FeatureMetric
import com.v2ray.ang.composeui.navigation.CryptoVpnRouteSpec
import com.v2ray.ang.composeui.navigation.RouteDefinition

data class ExpiryReminderRouteArgs(val daysLeft: String = "5")

data class ExpiryReminderUiState(
    val title: String = "到期提醒",
    val subtitle: String = "EXPIRY REMINDER",
    val badge: String = "同步中",
    val summary: String = "正在读取订阅剩余时长和续费上下文。",
    val primaryActionLabel: String? = null,
    val secondaryActionLabel: String? = null,
    val heroAccent: String = "expiry_reminder",
    val metrics: List<FeatureMetric> = listOf(
        FeatureMetric(label = "剩余天数", value = "读取中"),
        FeatureMetric(label = "续费金额", value = "读取中"),
        FeatureMetric(label = "自动续费", value = "读取中"),
    ),
    val fields: List<FeatureField> = emptyList(),
    val highlights: List<FeatureListItem> = listOf(
        FeatureListItem(title = "当前状态", subtitle = "正在读取真实订阅信息", trailing = "", badge = "LOADING"),
    ),
    val checklist: List<FeatureBullet> = listOf(
        FeatureBullet(title = "说明", detail = "默认态仅展示同步状态，不再展示演示价格和模板交付信息。"),
    ),
    val note: String = "刷新后会替换为真实订阅状态或明确空态说明。",
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
