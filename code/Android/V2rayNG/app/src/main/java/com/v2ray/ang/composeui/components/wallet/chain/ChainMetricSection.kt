package com.v2ray.ang.composeui.components.wallet.chain

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.v2ray.ang.composeui.common.model.FeatureMetric
import com.v2ray.ang.composeui.components.cards.MetricCard
import com.v2ray.ang.composeui.theme.AppTheme

@Composable
fun ChainMetricSection(
    metrics: List<FeatureMetric>,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.space12),
    ) {
        metrics.chunked(2).forEachIndexed { rowIndex, row ->
            Row(horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.space12)) {
                row.forEachIndexed { itemIndex, metric ->
                    MetricCard(
                        title = metric.label,
                        value = metric.value,
                        modifier = Modifier.weight(1f),
                        emphasized = rowIndex == 0 && itemIndex == 0,
                    )
                }
                if (row.size == 1) {
                    Box(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}
