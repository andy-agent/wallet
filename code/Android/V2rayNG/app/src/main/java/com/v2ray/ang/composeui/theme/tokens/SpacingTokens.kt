package com.v2ray.ang.composeui.theme.tokens

import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Immutable
data class AppSpacingTokens(
    val space4: Dp,
    val space8: Dp,
    val space12: Dp,
    val space16: Dp,
    val space20: Dp,
    val space24: Dp,
    val space32: Dp,
    val pageHorizontal: Dp,
    val cardPadding: Dp,
    val sectionGap: Dp,
    val itemGap: Dp,
)

object SpacingTokens {
    fun compact() = AppSpacingTokens(
        space4 = 4.dp,
        space8 = 8.dp,
        space12 = 12.dp,
        space16 = 16.dp,
        space20 = 20.dp,
        space24 = 24.dp,
        space32 = 32.dp,
        pageHorizontal = 16.dp,
        cardPadding = 16.dp,
        sectionGap = 12.dp,
        itemGap = 12.dp,
    )

    fun medium() = AppSpacingTokens(
        space4 = 4.dp,
        space8 = 8.dp,
        space12 = 12.dp,
        space16 = 16.dp,
        space20 = 20.dp,
        space24 = 24.dp,
        space32 = 32.dp,
        pageHorizontal = 20.dp,
        cardPadding = 16.dp,
        sectionGap = 16.dp,
        itemGap = 12.dp,
    )

    fun expanded() = AppSpacingTokens(
        space4 = 4.dp,
        space8 = 8.dp,
        space12 = 12.dp,
        space16 = 16.dp,
        space20 = 20.dp,
        space24 = 24.dp,
        space32 = 32.dp,
        pageHorizontal = 24.dp,
        cardPadding = 20.dp,
        sectionGap = 20.dp,
        itemGap = 16.dp,
    )

    val default = medium()
}
