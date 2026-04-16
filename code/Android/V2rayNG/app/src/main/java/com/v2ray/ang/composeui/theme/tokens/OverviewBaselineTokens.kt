package com.v2ray.ang.composeui.theme.tokens

import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Immutable
data class AppOverviewVisualBaseline(
    val pageHorizontal: Dp,
    val pageTopSpacing: Dp,
    val sectionGap: Dp,
    val heroRadius: Dp,
    val heroCardHeight: Dp,
    val heroSummaryMinHeight: Dp,
    val heroPadding: Dp,
    val heroShadow: Dp,
    val metricSurfaceRadius: Dp,
    val metricSurfaceMinHeight: Dp,
    val infoCardRadius: Dp,
    val infoCardPadding: Dp,
    val infoCardShadow: Dp,
    val actionButtonHeight: Dp,
    val actionButtonRadius: Dp,
    val actionButtonGap: Dp,
    val contentTightGap: Dp,
)

object OverviewBaselineTokens {
    val primary = AppOverviewVisualBaseline(
        pageHorizontal = 20.dp,
        pageTopSpacing = 10.dp,
        sectionGap = 14.dp,
        heroRadius = 28.dp,
        heroCardHeight = 322.dp,
        heroSummaryMinHeight = 252.dp,
        heroPadding = 16.dp,
        heroShadow = 12.dp,
        metricSurfaceRadius = 20.dp,
        metricSurfaceMinHeight = 58.dp,
        infoCardRadius = 22.dp,
        infoCardPadding = 14.dp,
        infoCardShadow = 4.dp,
        actionButtonHeight = 54.dp,
        actionButtonRadius = 18.dp,
        actionButtonGap = 10.dp,
        contentTightGap = 6.dp,
    )
}
