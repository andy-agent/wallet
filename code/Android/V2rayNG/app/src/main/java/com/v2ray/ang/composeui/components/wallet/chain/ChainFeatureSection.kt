package com.v2ray.ang.composeui.components.wallet.chain

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.v2ray.ang.composeui.common.model.FeatureListItem
import com.v2ray.ang.composeui.components.chips.AppChip
import com.v2ray.ang.composeui.components.chips.AppChipTone
import com.v2ray.ang.composeui.components.feedback.EmptyStateCard
import com.v2ray.ang.composeui.components.listitems.AppListCardItem
import com.v2ray.ang.composeui.theme.AppTheme

@Composable
fun ChainFeatureSection(
    items: List<FeatureListItem>,
    modifier: Modifier = Modifier,
    emptyTitle: String,
    emptyMessage: String,
) {
    if (items.isEmpty()) {
        EmptyStateCard(
            title = emptyTitle,
            message = emptyMessage,
            modifier = modifier,
        )
        return
    }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.space12),
    ) {
        items.forEachIndexed { index, item ->
            AppListCardItem(
                title = item.title,
                subtitle = item.subtitle,
                value = item.trailing,
                emphasized = index == 0,
                trailing = {
                    if (item.badge.isNotBlank()) {
                        AppChip(
                            text = item.badge,
                            tone = featureBadgeTone(item),
                        )
                    }
                },
            )
        }
    }
}

private fun featureBadgeTone(item: FeatureListItem): AppChipTone = when {
    item.trailing.contains("已配置", ignoreCase = true) -> AppChipTone.Success
    item.trailing.contains("Healthy", ignoreCase = true) -> AppChipTone.Success
    item.trailing.contains("待配置", ignoreCase = true) -> AppChipTone.Warning
    item.trailing.contains("阻塞", ignoreCase = true) -> AppChipTone.Warning
    item.trailing.contains("待接入", ignoreCase = true) -> AppChipTone.Warning
    item.badge.equals("REAL", ignoreCase = true) -> AppChipTone.Info
    else -> AppChipTone.Neutral
}
