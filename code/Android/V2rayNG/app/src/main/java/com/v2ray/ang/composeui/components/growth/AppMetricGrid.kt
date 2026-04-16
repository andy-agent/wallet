package com.v2ray.ang.composeui.components.growth

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.v2ray.ang.composeui.components.cards.AppMetricCard
import com.v2ray.ang.composeui.theme.AppTheme

data class AppMetricGridItem(
    val label: String,
    val value: String,
    val supportingText: String = "",
)

@Composable
fun AppMetricGrid(
    items: List<AppMetricGridItem>,
    modifier: Modifier = Modifier,
    emphasizedIndexes: Set<Int> = emptySet(),
) {
    val visibleItems = items.filter { it.label.isNotBlank() || it.value.isNotBlank() }
    if (visibleItems.isEmpty()) return
    androidx.compose.foundation.layout.Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.itemGap),
    ) {
        visibleItems.chunked(2).forEachIndexed { rowIndex, row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.itemGap),
            ) {
                row.forEachIndexed { itemIndex, item ->
                    val absoluteIndex = rowIndex * 2 + itemIndex
                    AppMetricCard(
                        title = item.label,
                        value = item.value,
                        supportingText = item.supportingText,
                        emphasized = emphasizedIndexes.contains(absoluteIndex),
                        modifier = Modifier.weight(1f),
                    )
                }
                if (row.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}
