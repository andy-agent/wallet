package com.app.common.widgets

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import com.app.common.components.GradientCard
import com.app.data.model.UserProfile
import com.app.core.utils.Formatters

@Composable
fun BalanceHeader(profile: UserProfile) {
    GradientCard(title = profile.displayName, subtitle = profile.email) {
        Row(modifier = androidx.compose.ui.Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            MetricPill("总资产", Formatters.money(profile.totalAssetsUsd))
            MetricPill("钱包数", profile.walletCount.toString())
            MetricPill("邀请码", profile.inviteCode)
        }
    }
}
