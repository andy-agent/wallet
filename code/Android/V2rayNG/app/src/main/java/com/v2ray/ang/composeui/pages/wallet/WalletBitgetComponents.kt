package com.v2ray.ang.composeui.pages.wallet

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.v2ray.ang.composeui.theme.AccentSurface
import com.v2ray.ang.composeui.theme.AppShape
import com.v2ray.ang.composeui.theme.BackgroundDeepest
import com.v2ray.ang.composeui.theme.BackgroundOverlay
import com.v2ray.ang.composeui.theme.BackgroundPrimary
import com.v2ray.ang.composeui.theme.BackgroundSecondary
import com.v2ray.ang.composeui.theme.BorderDefault
import com.v2ray.ang.composeui.theme.GlowBlue
import com.v2ray.ang.composeui.theme.GlowGreen
import com.v2ray.ang.composeui.theme.Info
import com.v2ray.ang.composeui.theme.Primary
import com.v2ray.ang.composeui.theme.SolanaPurple
import com.v2ray.ang.composeui.theme.TextSecondary
import com.v2ray.ang.composeui.theme.TronRed
import com.v2ray.ang.composeui.theme.USDTGreen
import com.v2ray.ang.composeui.theme.Warning

internal data class WalletQuickAction(
    val label: String,
    val hint: String,
    val icon: ImageVector,
    val accent: Color,
    val onClick: () -> Unit,
)

internal data class WalletOverviewMetric(
    val label: String,
    val value: String,
)

@Composable
internal fun WalletPageBackdrop(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        BackgroundDeepest,
                        BackgroundPrimary,
                        BackgroundSecondary,
                    ),
                ),
            ),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(GlowGreen.copy(alpha = 0.18f), Color.Transparent),
                        radius = 920f,
                    ),
                ),
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(GlowBlue.copy(alpha = 0.14f), Color.Transparent),
                        radius = 1240f,
                    ),
                ),
        )
        content()
    }
}

@Composable
internal fun WalletGlassCard(
    modifier: Modifier = Modifier,
    accent: Color = Primary,
    contentPadding: PaddingValues = PaddingValues(20.dp),
    content: @Composable ColumnScope.() -> Unit,
) {
    Surface(
        modifier = modifier,
        shape = AppShape.CardLarge,
        color = BackgroundOverlay.copy(alpha = 0.95f),
        border = BorderStroke(1.dp, BorderDefault.copy(alpha = 0.95f)),
        shadowElevation = 12.dp,
    ) {
        Column(
            modifier = Modifier
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            accent.copy(alpha = 0.16f),
                            BackgroundOverlay.copy(alpha = 0.98f),
                            BackgroundSecondary.copy(alpha = 0.98f),
                        ),
                    ),
                )
                .padding(contentPadding),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            content = content,
        )
    }
}

@Composable
internal fun WalletSectionHeading(
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground,
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
internal fun WalletMetricStrip(
    metrics: List<WalletOverviewMetric>,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        metrics.forEach { metric ->
            Surface(
                modifier = Modifier.weight(1f),
                shape = AppShape.Card,
                color = BackgroundPrimary.copy(alpha = 0.72f),
                border = BorderStroke(1.dp, BorderDefault.copy(alpha = 0.72f)),
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Text(
                        text = metric.label,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Text(
                        text = metric.value,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }
        }
    }
}

@Composable
internal fun WalletActionRow(
    actions: List<WalletQuickAction>,
    modifier: Modifier = Modifier,
) {
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        items(actions) { action ->
            Surface(
                modifier = Modifier
                    .width(148.dp)
                    .clickable(onClick = action.onClick),
                shape = AppShape.Card,
                color = BackgroundPrimary.copy(alpha = 0.82f),
                border = BorderStroke(1.dp, BorderDefault.copy(alpha = 0.82f)),
            ) {
                Column(
                    modifier = Modifier
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    action.accent.copy(alpha = 0.16f),
                                    BackgroundOverlay.copy(alpha = 0.96f),
                                ),
                            ),
                        )
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(action.accent.copy(alpha = 0.18f))
                            .border(
                                width = 1.dp,
                                color = action.accent.copy(alpha = 0.22f),
                                shape = CircleShape,
                            ),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = action.icon,
                            contentDescription = action.label,
                            tint = action.accent,
                        )
                    }
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = action.label,
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                        Text(
                            text = action.hint,
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary,
                        )
                    }
                }
            }
        }
    }
}

@Composable
internal fun WalletTokenBadge(symbol: String, modifier: Modifier = Modifier) {
    val accent = walletAssetAccent(symbol)
    Box(
        modifier = modifier
            .size(48.dp)
            .clip(CircleShape)
            .background(accent.copy(alpha = 0.16f))
            .border(1.dp, accent.copy(alpha = 0.24f), CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = symbol.take(1).uppercase(),
            style = MaterialTheme.typography.titleMedium,
            color = accent,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
internal fun WalletTag(
    text: String,
    modifier: Modifier = Modifier,
    accent: Color = Primary,
) {
    Surface(
        modifier = modifier,
        shape = AppShape.TagPill,
        color = AccentSurface,
        border = BorderStroke(1.dp, accent.copy(alpha = 0.24f)),
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
            style = MaterialTheme.typography.labelMedium,
            color = accent,
        )
    }
}

@Composable
internal fun WalletPrimaryButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        shape = AppShape.ButtonLarge,
        colors = ButtonDefaults.buttonColors(
            containerColor = Primary,
            contentColor = BackgroundDeepest,
        ),
    ) {
        Text(text = label, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
internal fun WalletSecondaryButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier,
        shape = AppShape.ButtonLarge,
        border = BorderStroke(1.dp, BorderDefault.copy(alpha = 0.9f)),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = MaterialTheme.colorScheme.onSurface,
        ),
    ) {
        Text(text = label, fontWeight = FontWeight.Medium)
    }
}

internal fun walletAssetAccent(symbol: String): Color {
    return when (symbol.uppercase()) {
        "USDT" -> USDTGreen
        "SOL" -> SolanaPurple
        "TRX", "TRON" -> TronRed
        "ETH" -> Info
        "BNB" -> Warning
        else -> Primary
    }
}

internal fun walletNetworkLabel(symbol: String): String {
    return when (symbol.uppercase()) {
        "USDT", "TRX", "TRON" -> "TRON"
        "SOL" -> "Solana"
        "ETH" -> "Ethereum"
        "BNB" -> "BNB Smart Chain"
        "MATIC" -> "Polygon"
        else -> "Mainnet"
    }
}

internal fun walletShortAddress(address: String): String {
    if (address.length <= 14) return address
    return "${address.take(8)}...${address.takeLast(6)}"
}
