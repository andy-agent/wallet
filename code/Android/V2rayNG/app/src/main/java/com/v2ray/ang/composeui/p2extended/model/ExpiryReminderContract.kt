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
        val badge: String = "",
        val summary: String = "",
        val primaryActionLabel: String = "查看订阅",
        val secondaryActionLabel: String? = "查看套餐",
        val heroAccent: String = "expiry_reminder",
        val metrics: List<FeatureMetric> = listOf(
    FeatureMetric(label = "剩余天数", value = "待接口返回"),
    FeatureMetric(label = "续费金额", value = "待接口返回"),
    FeatureMetric(label = "自动续费", value = "待接口返回"),
),
        val fields: List<FeatureField> = emptyList(),
        val highlights: List<FeatureListItem> = emptyList(),
        val checklist: List<FeatureBullet> = emptyList(),
        val note: String = "",
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
