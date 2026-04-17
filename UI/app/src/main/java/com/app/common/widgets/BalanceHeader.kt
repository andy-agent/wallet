package com.app.common.widgets

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.Spacer
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.app.common.components.GradientCard
import com.app.common.components.InfoRow
import com.app.data.model.UserProfile
import com.app.core.utils.Formatters

@Composable
fun BalanceHeader(profile: UserProfile) {
    GradientCard(title = "总资产", subtitle = profile.displayName) {
        Column {
            androidx.compose.material3.Text(
                text = Formatters.money(profile.totalAssetsUsd),
                style = androidx.compose.material3.MaterialTheme.typography.headlineLarge,
            )
            Spacer(modifier = androidx.compose.ui.Modifier.height(8.dp))
            InfoRow("账户等级", profile.levelLabel)
            InfoRow("联系邮箱", profile.email)
            Spacer(modifier = androidx.compose.ui.Modifier.height(14.dp))
            Row(
                modifier = androidx.compose.ui.Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                MetricPill("钱包数", profile.walletCount.toString())
                MetricPill("邀请码", profile.inviteCode)
            }
        }
    }
}
