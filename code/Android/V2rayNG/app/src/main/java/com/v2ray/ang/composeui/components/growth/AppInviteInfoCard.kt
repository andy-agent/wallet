package com.v2ray.ang.composeui.components.growth

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.v2ray.ang.composeui.components.cards.AppCard
import com.v2ray.ang.composeui.components.cards.AppCardVariant
import com.v2ray.ang.composeui.components.chips.AppChip
import com.v2ray.ang.composeui.components.chips.AppChipTone
import com.v2ray.ang.composeui.theme.AppTheme

@Composable
fun AppInviteInfoCard(
    inviteCode: String,
    shareLink: String,
    modifier: Modifier = Modifier,
    title: String = "邀请信息",
    subtitle: String = "邀请码、推广链接与实时状态",
    statusLabel: String? = "实时同步",
    note: String = "",
) {
    AppCard(
        modifier = modifier.fillMaxWidth(),
        variant = AppCardVariant.Elevated,
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.sectionGap),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.space4),
                ) {
                    Text(
                        text = title,
                        style = androidx.compose.material3.MaterialTheme.typography.titleLarge,
                        color = AppTheme.colors.textPrimary,
                    )
                    Text(
                        text = subtitle,
                        style = androidx.compose.material3.MaterialTheme.typography.bodySmall,
                        color = AppTheme.colors.textSecondary,
                    )
                }
                if (!statusLabel.isNullOrBlank()) {
                    AppChip(
                        text = statusLabel,
                        tone = AppChipTone.Info,
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = AppTheme.spacing.space4),
                verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.space12),
            ) {
                InvitePrimaryValueBlock(
                    label = "邀请码",
                    value = inviteCode.ifBlank { "--" },
                )
                InviteSupportingValueBlock(
                    label = "推广链接",
                    value = shareLink.ifBlank { "--" },
                    supportingText = "长按可复制，适合通过系统分享发送给新用户",
                )
            }

            if (note.isNotBlank()) {
                Text(
                    text = note,
                    style = androidx.compose.material3.MaterialTheme.typography.bodySmall,
                    color = AppTheme.colors.textTertiary,
                )
            }
        }
    }
}

@Composable
private fun InvitePrimaryValueBlock(
    label: String,
    value: String,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = AppTheme.spacing.space4),
        verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.space4),
    ) {
        Text(
            text = label,
            style = androidx.compose.material3.MaterialTheme.typography.labelMedium,
            color = AppTheme.colors.textSecondary,
        )
        Text(
            text = value,
            style = androidx.compose.material3.MaterialTheme.typography.displaySmall,
            color = AppTheme.colors.textPrimary,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun InviteSupportingValueBlock(
    label: String,
    value: String,
    supportingText: String,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.space8),
    ) {
        Text(
            text = label,
            style = androidx.compose.material3.MaterialTheme.typography.labelMedium,
            color = AppTheme.colors.textSecondary,
        )
        androidx.compose.material3.Surface(
            color = AppTheme.colors.bgSubtle,
            shape = RoundedCornerShape(AppTheme.shapes.radiusMd),
            border = BorderStroke(1.dp, AppTheme.colors.dividerSubtle),
        ) {
            Column(
                modifier = Modifier.padding(
                    horizontal = AppTheme.spacing.space12,
                    vertical = AppTheme.spacing.space12,
                ),
                verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.space8),
            ) {
                Text(
                    text = value,
                    style = androidx.compose.material3.MaterialTheme.typography.bodyMedium,
                    color = AppTheme.colors.textPrimary,
                )
                Text(
                    text = supportingText,
                    style = androidx.compose.material3.MaterialTheme.typography.labelSmall,
                    color = AppTheme.colors.textTertiary,
                )
            }
        }
    }
}
