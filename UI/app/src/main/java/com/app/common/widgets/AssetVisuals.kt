package com.app.common.widgets

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.core.theme.BorderSubtle
import com.app.core.theme.CardGlassStrong
import com.app.core.theme.TextPrimary
import com.app.core.theme.TextSecondary

data class AssetPalette(
    val start: Color,
    val end: Color,
    val accent: Color,
    val text: Color = Color.White,
)

@Composable
fun TokenIcon(
    symbol: String,
    modifier: Modifier = Modifier,
    size: Dp = 40.dp,
    chainId: String? = null,
) {
    val palette = paletteFor(symbol)
    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(CircleShape)
                .background(Brush.linearGradient(listOf(palette.start, palette.end)))
                .border(1.dp, Color.White.copy(alpha = 0.24f), CircleShape),
        )
        Box(
            modifier = Modifier
                .size(size * 0.78f)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(Color.White.copy(alpha = 0.24f), Color.Transparent),
                    ),
                ),
        )
        Text(
            text = iconGlyph(symbol),
            color = palette.text,
            fontWeight = FontWeight.Bold,
            fontSize = (size.value * 0.28f).sp,
            letterSpacing = 0.2.sp,
        )
        if (chainId != null) {
            MiniChainBadge(
                chainId = chainId,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(size * 0.38f),
            )
        }
    }
}

@Composable
fun ChainPill(
    chainId: String,
    modifier: Modifier = Modifier,
) {
    val palette = paletteFor(chainId)
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(999.dp))
            .background(CardGlassStrong.copy(alpha = 0.88f))
            .border(1.dp, BorderSubtle, RoundedCornerShape(999.dp))
            .padding(horizontal = 8.dp, vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        MiniChainBadge(chainId = chainId, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.size(6.dp))
        Text(
            text = chainLabel(chainId),
            style = MaterialTheme.typography.labelSmall,
            color = TextSecondary,
            fontWeight = FontWeight.Medium,
        )
    }
}

fun assetPalette(value: String): AssetPalette = paletteFor(value)

fun chainIdForSymbol(symbol: String): String = when (symbol.trim().uppercase()) {
    "BTC" -> "bitcoin"
    "ETH", "ENJ" -> "ethereum"
    "USDT" -> "tron"
    "SOL" -> "solana"
    "ARB" -> "arbitrum"
    "OP" -> "optimism"
    "AVAX" -> "avalanche"
    "TRX" -> "tron"
    "BASE" -> "base"
    else -> symbol.lowercase()
}

fun chainDisplayName(chainId: String): String = chainLabel(chainId)

@Composable
private fun MiniChainBadge(
    chainId: String,
    modifier: Modifier = Modifier,
) {
    val palette = paletteFor(chainId)
    Box(
        modifier = modifier
            .clip(CircleShape)
            .background(Brush.linearGradient(listOf(palette.start, palette.end)))
            .border(1.dp, Color.White.copy(alpha = 0.22f), CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = iconGlyph(chainId).take(2),
            color = palette.text,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
        )
    }
}

private fun iconGlyph(value: String): String {
    val key = value.trim().uppercase()
    return when {
        key == "BTC" || key == "BITCOIN" -> "B"
        key == "ETH" || key == "ETHEREUM" -> "E"
        key == "USDT" || key == "TETHER" -> "T"
        key == "SOL" || key == "SOLANA" -> "S"
        key == "ARB" || key == "ARBITRUM" -> "A"
        key == "AVAX" || key == "AVALANCHE" -> "AV"
        key == "OP" || key == "OPTIMISM" -> "OP"
        key == "ENJ" -> "EN"
        key == "TRON" || key == "TRX" -> "TR"
        key == "BASE" -> "BA"
        else -> key.take(2)
    }
}

private fun chainLabel(chainId: String): String = when (chainId.lowercase()) {
    "ethereum" -> "Ethereum"
    "bitcoin" -> "Bitcoin"
    "tron" -> "TRON"
    "solana" -> "Solana"
    "arbitrum" -> "Arbitrum"
    "optimism" -> "Optimism"
    "avalanche" -> "Avalanche"
    "base" -> "Base"
    else -> chainId.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
}

private fun paletteFor(value: String): AssetPalette = when (value.trim().uppercase()) {
    "BTC", "BITCOIN" -> AssetPalette(Color(0xFFFFB34D), Color(0xFFF7931A), Color(0xFFFFE2AF), text = TextPrimary)
    "ETH", "ETHEREUM" -> AssetPalette(Color(0xFF5A6BFF), Color(0xFF9AA9FF), Color(0xFFDCE4FF))
    "USDT", "TETHER" -> AssetPalette(Color(0xFF11C389), Color(0xFF26E7A2), Color(0xFFC9FBE7), text = TextPrimary)
    "SOL", "SOLANA" -> AssetPalette(Color(0xFF5E44FF), Color(0xFF32F0C8), Color(0xFFD7FFF6), text = TextPrimary)
    "ARB", "ARBITRUM" -> AssetPalette(Color(0xFF2F78FF), Color(0xFF7EB5FF), Color(0xFFDCEBFF), text = TextPrimary)
    "ENJ" -> AssetPalette(Color(0xFF586DFF), Color(0xFFB455FF), Color(0xFFEEE2FF))
    "AVAX", "AVALANCHE" -> AssetPalette(Color(0xFFFF6C7D), Color(0xFFE84057), Color(0xFFFFD6DD))
    "OP", "OPTIMISM" -> AssetPalette(Color(0xFFFF6A6A), Color(0xFFFF4141), Color(0xFFFFD9D9))
    "TRON", "TRX" -> AssetPalette(Color(0xFFFF6E5C), Color(0xFFFF9279), Color(0xFFFFDED7), text = TextPrimary)
    "BASE" -> AssetPalette(Color(0xFF3B7CFF), Color(0xFF75ADFF), Color(0xFFD8E7FF), text = TextPrimary)
    else -> AssetPalette(Color(0xFF5A87FF), Color(0xFF5AD7FF), Color(0xFFD8F8FF), text = TextPrimary)
}
