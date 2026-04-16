package com.v2ray.ang.composeui.components.wallet.chain

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.v2ray.ang.composeui.components.app.AppPageBackgroundStyle
import com.v2ray.ang.composeui.components.app.AppPageScaffold
import com.v2ray.ang.composeui.components.chips.AppChip
import com.v2ray.ang.composeui.components.chips.AppChipTone
import com.v2ray.ang.composeui.components.navigation.AppTopBar
import com.v2ray.ang.composeui.components.navigation.AppTopBarMode
import com.v2ray.ang.composeui.components.navigation.CryptoVpnBottomBar
import com.v2ray.ang.composeui.theme.AppTheme
import com.v2ray.ang.composeui.theme.tokens.OverviewBaselineTokens
import androidx.compose.material3.Text

private val ChainGlowBlue = Color(0x224F7CFF)
private val ChainGlowMint = Color(0x162ED8A3)

@Composable
fun ChainManagementScaffold(
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
    badge: String = "",
    summary: String = "",
    currentRoute: String = "wallet_home",
    onBottomNav: (String) -> Unit = {},
    content: @Composable ColumnScope.() -> Unit,
) {
    val baseline = OverviewBaselineTokens.primary
    AppPageScaffold(
        modifier = modifier,
        backgroundStyle = AppPageBackgroundStyle.Hero,
        background = { ChainManagementBackgroundGlow() },
        bottomBar = {
            CryptoVpnBottomBar(
                currentRoute = currentRoute,
                onRouteSelected = onBottomNav,
            )
        },
        contentPadding = PaddingValues(
            horizontal = baseline.pageHorizontal,
            vertical = baseline.pageTopSpacing,
        ),
    ) { _ ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 680.dp),
            verticalArrangement = Arrangement.spacedBy(baseline.sectionGap),
        ) {
            AppTopBar(
                title = title,
                subtitle = subtitle,
                mode = AppTopBarMode.Hero,
                actions = {
                    if (badge.isNotBlank()) {
                        AppChip(
                            text = badge,
                            tone = AppChipTone.Info,
                        )
                    }
                },
            )

            if (summary.isNotBlank()) {
                Text(
                    text = summary,
                    style = androidx.compose.material3.MaterialTheme.typography.bodyMedium,
                    color = AppTheme.colors.textSecondary,
                )
            }

            content()
        }
    }
}

@Composable
private fun ChainManagementBackgroundGlow() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 36.dp),
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .size(220.dp)
                .background(ChainGlowBlue, RoundedCornerShape(999.dp))
                .blur(48.dp),
        )
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(top = 320.dp)
                .size(260.dp)
                .background(ChainGlowMint, RoundedCornerShape(999.dp))
                .blur(60.dp),
        )
    }
}
