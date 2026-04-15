package com.v2ray.ang.composeui.pages.p2

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.animation.animateColorAsState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.ArrowForwardIos
import androidx.compose.material.icons.outlined.AutoGraph
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.PersonOutline
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.v2ray.ang.composeui.effects.MotionProfile
import com.v2ray.ang.composeui.effects.TechParticleBackground
import com.v2ray.ang.composeui.navigation.CryptoVpnRouteSpec
import com.v2ray.ang.composeui.p0.ui.P01BottomNav
import com.v2ray.ang.composeui.p0.ui.P01HeaderHeroRing
import com.v2ray.ang.composeui.p0.ui.defaultP01Destinations
import com.v2ray.ang.composeui.p0.ui.P01BottomNav
import com.v2ray.ang.composeui.p0.ui.P01HeaderHeroRing
import com.v2ray.ang.composeui.p0.ui.defaultP01Destinations
import com.v2ray.ang.util.QRCodeDecoder
import kotlinx.coroutines.delay

private val CorePageBackground = Brush.verticalGradient(
    colors = listOf(
        Color(0xFFF8FBFF),
        Color(0xFFF3F6FD),
        Color(0xFFEAF6FB),
    ),
)

private val CoreCardBorder = Color(0xFFE7ECF7)
private val CoreText = Color(0xFF182345)
private val CoreSubtleText = Color(0xFF6D789E)
private val CorePrimary = Color(0xFF2F5BFF)
private val CoreMint = Color(0xFF23C8A8)
private val CoreHeroGradient = Brush.linearGradient(
    colors = listOf(
        Color(0xFF1F2E66),
        Color(0xFF2F5BFF),
        Color(0xFF3F7CFF),
    ),
)

internal enum class CoreNavSection {
    Overview,
    Vpn,
    Wallet,
    Growth,
    Profile,
}

internal enum class P2CoreRowEmphasis {
    Neutral,
    Brand,
    Success,
    Warning,
}

@Composable
internal fun rememberCoreLoopingIndex(
    itemCount: Int,
    durationMillis: Int = 4200,
): Int {
    if (itemCount <= 1) return 0
    var index by remember(itemCount, durationMillis) { mutableIntStateOf(0) }
    LaunchedEffect(itemCount, durationMillis) {
        index = 0
        while (true) {
            delay(durationMillis.toLong())
            index = (index + 1) % itemCount
        }
    }
    return index
}

@Composable
internal fun P2CorePageScaffold(
    kicker: String,
    title: String,
    subtitle: String,
    badge: String? = null,
    activeSection: CoreNavSection,
    onBottomNav: (String) -> Unit,
    showSecureHub: Boolean = true,
    secureHubLabel: String = "SECURE",
    primaryActionLabel: String? = null,
    onPrimaryAction: (() -> Unit)? = null,
    secondaryActionLabel: String? = null,
    onSecondaryAction: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.Transparent,
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(CorePageBackground),
        ) {
            TechParticleBackground(
                motionProfile = MotionProfile.L1,
                modifier = Modifier.fillMaxSize(),
                showNetwork = true,
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .padding(horizontal = 18.dp, vertical = 8.dp),
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.Top,
                    ) {
                        Column(
                            modifier = Modifier.weight(1f),
                        ) {
                            Text(kicker, style = MaterialTheme.typography.labelLarge, color = Color(0xFF7381AD))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(title, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = CoreText)
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(subtitle, style = MaterialTheme.typography.bodyMedium, color = CoreSubtleText)
                            if (!badge.isNullOrBlank()) {
                                Spacer(modifier = Modifier.height(12.dp))
                                StatusChip(text = badge)
                            }
                        }
                        if (showSecureHub) {
                            P2CoreSecureHub(label = secureHubLabel)
                        }
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                    Column(
                        verticalArrangement = Arrangement.spacedBy(14.dp),
                        content = content,
                    )
                    if (primaryActionLabel != null && onPrimaryAction != null) {
                        Spacer(modifier = Modifier.height(14.dp))
                        if (secondaryActionLabel != null && onSecondaryAction != null) {
                            CoreActionRow(
                                primaryActionLabel = primaryActionLabel,
                                onPrimaryAction = onPrimaryAction,
                                secondaryActionLabel = secondaryActionLabel,
                                onSecondaryAction = onSecondaryAction,
                            )
                        } else {
                            CorePrimaryButton(
                                label = primaryActionLabel,
                                onClick = onPrimaryAction,
                                modifier = Modifier.fillMaxWidth(),
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }
                P01BottomNav(
                    currentRoute = when (activeSection) {
                        CoreNavSection.Overview -> "vpn_home"
                        CoreNavSection.Vpn -> "plans"
                        CoreNavSection.Wallet -> "wallet_home"
                        CoreNavSection.Growth -> "invite_center"
                        CoreNavSection.Profile -> "profile"
                    },
                    destinations = defaultP01Destinations(),
                    onNavigate = onBottomNav,
                )
            }
        }
    }
}

@Composable
private fun P2CoreSecureHub(
    label: String,
) {
    P01HeaderHeroRing()
}

@Composable
private fun P2CoreSecureHubWalletGlyph(
    modifier: Modifier = Modifier,
) {
    Canvas(modifier = modifier) {
        val unit = size.minDimension / 24f
        val stroke = unit * 1.9f
        val walletBlue = Color(0xFF244FD6)

        drawRoundRect(
            color = walletBlue,
            topLeft = Offset(3f * unit, 6f * unit),
            size = Size(18f * unit, 13f * unit),
            cornerRadius = CornerRadius(3f * unit, 3f * unit),
            style = Stroke(width = stroke),
        )

        val pocketPath = Path().apply {
            moveTo(16f * unit, 10f * unit)
            lineTo(21f * unit, 10f * unit)
            lineTo(21f * unit, 15f * unit)
            lineTo(16f * unit, 15f * unit)
            quadraticTo(13.5f * unit, 15f * unit, 13.5f * unit, 12.5f * unit)
            quadraticTo(13.5f * unit, 10f * unit, 16f * unit, 10f * unit)
            close()
        }
        drawPath(
            path = pocketPath,
            color = walletBlue,
            style = Stroke(
                width = stroke,
                cap = StrokeCap.Round,
                join = StrokeJoin.Round,
            ),
        )
        drawCircle(
            color = walletBlue,
            radius = unit * 0.95f,
            center = Offset(16.5f * unit, 12.5f * unit),
        )
    }
}

@Composable
internal fun P2CoreCard(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(16.dp),
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(22.dp))
            .border(1.dp, CoreCardBorder, RoundedCornerShape(22.dp))
            .padding(contentPadding),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        content = content,
    )
}

@Composable
internal fun P2CoreCardHeader(
    title: String,
    subtitle: String? = null,
    trailing: String? = null,
    trailingColor: Color = Color(0xFF67B6FF),
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.titleMedium, color = CoreText, fontWeight = FontWeight.SemiBold)
            if (!subtitle.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(subtitle, style = MaterialTheme.typography.bodySmall, color = CoreSubtleText)
            }
        }
        if (!trailing.isNullOrBlank()) {
            StatusChip(text = trailing, color = trailingColor)
        }
    }
}

@Composable
internal fun P2CoreHeroValue(
    label: String,
    value: String,
    caption: String,
    accent: Color = CoreMint,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFF7FAFF), RoundedCornerShape(16.dp))
            .border(1.dp, Color(0xFFE2EAFA), RoundedCornerShape(16.dp))
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Text(label, style = MaterialTheme.typography.labelMedium, color = CoreSubtleText)
        Text(
            value,
            style = MaterialTheme.typography.headlineSmall,
            color = CoreText,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Text(caption, style = MaterialTheme.typography.bodySmall, color = accent)
    }
}

@Composable
internal fun P2CoreTrendCard(
    title: String,
    value: String,
    caption: String,
    accent: Color = CorePrimary,
) {
    P2CoreCard {
        P2CoreCardHeader(title = title)
        Text(
            value,
            style = MaterialTheme.typography.headlineMedium,
            color = CoreText,
            fontWeight = FontWeight.Bold,
        )
        Text(caption, style = MaterialTheme.typography.bodySmall, color = CoreSubtleText)
        P2CoreChartPlaceholder(accent = accent)
    }
}

@Composable
internal fun P2CoreQrInfoCard(
    title: String,
    subtitle: String,
    address: String? = null,
) {
    P2CoreCard {
        P2CoreCardHeader(title = title, subtitle = subtitle)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
        ) {
            P2CoreQrPlaceholder()
        }
        if (!address.isNullOrBlank()) {
            P2CoreField(
                label = "标识",
                value = address,
            )
        }
    }
}

@Composable
internal fun P2CoreAddressModule(
    title: String,
    value: String,
    supportingText: String,
    status: String? = null,
    primaryActionLabel: String? = null,
    onPrimaryAction: (() -> Unit)? = null,
    secondaryActionLabel: String? = null,
    onSecondaryAction: (() -> Unit)? = null,
) {
    P2CoreCard {
        P2CoreCardHeader(
            title = title,
            trailing = status,
            trailingColor = Color(0xFFE6FFF6),
        )
        P2CoreField(
            label = "地址 / 标识",
            value = value,
            supportingText = supportingText,
        )
        if (
            !primaryActionLabel.isNullOrBlank() &&
            onPrimaryAction != null &&
            !secondaryActionLabel.isNullOrBlank() &&
            onSecondaryAction != null
        ) {
            CoreActionRow(
                primaryActionLabel = primaryActionLabel,
                onPrimaryAction = onPrimaryAction,
                secondaryActionLabel = secondaryActionLabel,
                onSecondaryAction = onSecondaryAction,
            )
        }
    }
}

@Composable
internal fun P2CoreActionValueRow(
    label: String,
    value: String,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null,
    valueColor: Color = CoreText,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        Color(0xFFF7F9FF),
                        Color(0xFFF2F6FF),
                    ),
                ),
                shape = RoundedCornerShape(18.dp),
            )
            .border(1.dp, Color(0xFFDCE5FB), RoundedCornerShape(18.dp))
            .padding(horizontal = 14.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(label, style = MaterialTheme.typography.labelMedium, color = CoreSubtleText)
            Text(
                value,
                style = MaterialTheme.typography.bodyLarge,
                color = valueColor,
                fontWeight = FontWeight.SemiBold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
        }
        if (!actionLabel.isNullOrBlank() && onAction != null) {
            OutlinedButton(
                onClick = onAction,
                shape = RoundedCornerShape(999.dp),
                border = BorderStroke(1.dp, Color(0xFFD7E3FF)),
                colors = ButtonDefaults.outlinedButtonColors(containerColor = Color(0xFFFFFFFF)),
            ) {
                Text(
                    actionLabel,
                    style = MaterialTheme.typography.labelLarge,
                    color = CorePrimary,
                )
            }
        }
    }
}

@Composable
internal fun P2CoreMetricGrid(
    items: List<Pair<String, String>>,
    accentIndexes: Set<Int> = emptySet(),
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        items.chunked(2).forEachIndexed { rowIndex, row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                row.forEachIndexed { itemIndex, item ->
                    val absoluteIndex = rowIndex * 2 + itemIndex
                    MetricTile(
                        label = item.first,
                        value = item.second,
                        modifier = Modifier.weight(1f),
                        accent = accentIndexes.contains(absoluteIndex),
                    )
                }
                if (row.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
internal fun P2CoreHeroValueCard(
    label: String,
    value: String,
    supportingText: String,
    modifier: Modifier = Modifier,
    highlight: String? = null,
    highlightColor: Color = Color(0x26FFFFFF),
    stats: List<Pair<String, String>> = emptyList(),
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(CoreHeroGradient, RoundedCornerShape(24.dp))
            .border(1.dp, Color(0x1FFFFFFF), RoundedCornerShape(24.dp))
            .padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top,
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Text(label, style = MaterialTheme.typography.labelLarge, color = Color(0xFFAEC4FF))
                Text(value, style = MaterialTheme.typography.headlineMedium, color = Color.White, fontWeight = FontWeight.Bold)
                Text(supportingText, style = MaterialTheme.typography.bodyMedium, color = Color(0xFFE3EAFF))
            }
            if (!highlight.isNullOrBlank()) {
                StatusChip(text = highlight, color = highlightColor)
            }
        }
        if (stats.isNotEmpty()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                stats.take(2).forEach { (title, statValue) ->
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .background(Color(0x1FFFFFFF), RoundedCornerShape(16.dp))
                            .border(1.dp, Color(0x14FFFFFF), RoundedCornerShape(16.dp))
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        Text(title, style = MaterialTheme.typography.labelSmall, color = Color(0xFFAEC4FF))
                        Text(statValue, style = MaterialTheme.typography.titleSmall, color = Color.White, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}

@Composable
private fun MetricTile(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    accent: Boolean = false,
) {
    Column(
        modifier = modifier
            .background(Color(0xFFF7F9FF), RoundedCornerShape(14.dp))
            .padding(12.dp),
    ) {
        Text(label, style = MaterialTheme.typography.labelSmall, color = CoreSubtleText)
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            value,
            style = MaterialTheme.typography.titleMedium,
            color = if (accent) CoreMint else CoreText,
            fontWeight = FontWeight.SemiBold,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
internal fun P2CoreField(
    label: String,
    value: String,
    supportingText: String? = null,
) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(label, style = MaterialTheme.typography.labelMedium, color = CoreSubtleText)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFF7F9FF), RoundedCornerShape(14.dp))
                .padding(14.dp),
        ) {
            Text(value, style = MaterialTheme.typography.bodyLarge, color = CoreText)
            if (!supportingText.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(supportingText, style = MaterialTheme.typography.bodySmall, color = CoreSubtleText)
            }
        }
    }
}

@Composable
internal fun P2CoreChipRow(
    items: List<String>,
    activeIndex: Int = 0,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items.forEachIndexed { index, item ->
            val background by animateColorAsState(
                targetValue = if (index == activeIndex) Color(0xFFE7EEFF) else Color(0xFFF1F4FA),
                label = "p2_core_chip_bg_$index",
            )
            val textColor by animateColorAsState(
                targetValue = if (index == activeIndex) CorePrimary else Color(0xFF63709C),
                label = "p2_core_chip_text_$index",
            )
            Box(
                modifier = Modifier
                    .background(
                        color = background,
                        shape = RoundedCornerShape(999.dp),
                    )
                    .border(
                        width = 1.dp,
                        color = if (index == activeIndex) Color(0xFFC8D7FF) else Color.Transparent,
                        shape = RoundedCornerShape(999.dp),
                    )
                    .padding(horizontal = 12.dp, vertical = 7.dp),
            ) {
                Text(
                    item,
                    style = MaterialTheme.typography.labelMedium,
                    color = textColor,
                )
            }
        }
    }
}

@Composable
internal fun P2CoreListRow(
    title: String,
    subtitle: String,
    trailing: String? = null,
    onClick: (() -> Unit)? = null,
    trailingColor: Color = CoreSubtleText,
    emphasis: P2CoreRowEmphasis = P2CoreRowEmphasis.Neutral,
) {
    val borderColor = when (emphasis) {
        P2CoreRowEmphasis.Success -> Color(0xFFD9F4EB)
        P2CoreRowEmphasis.Warning -> Color(0xFFFBE5C4)
        P2CoreRowEmphasis.Brand -> Color(0xFFD9E4FF)
        P2CoreRowEmphasis.Neutral -> Color(0xFFE7ECF7)
    }
    val backgroundBrush = when (emphasis) {
        P2CoreRowEmphasis.Success -> Brush.horizontalGradient(listOf(Color(0xFFF7FFFB), Color(0xFFEEFFF7)))
        P2CoreRowEmphasis.Warning -> Brush.horizontalGradient(listOf(Color(0xFFFFFBF4), Color(0xFFFFF6E5)))
        P2CoreRowEmphasis.Brand -> Brush.horizontalGradient(listOf(Color(0xFFF8FAFF), Color(0xFFF1F5FF)))
        P2CoreRowEmphasis.Neutral -> Brush.horizontalGradient(listOf(Color.White, Color(0xFFF9FBFF)))
    }
    val trailingChipColor = when (emphasis) {
        P2CoreRowEmphasis.Success -> Color(0xFFE6FFF5)
        P2CoreRowEmphasis.Warning -> Color(0xFFFFF1D8)
        P2CoreRowEmphasis.Brand -> Color(0xFFE9F0FF)
        P2CoreRowEmphasis.Neutral -> Color(0xFFF1F4FA)
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(backgroundBrush, RoundedCornerShape(18.dp))
            .border(1.dp, borderColor, RoundedCornerShape(18.dp))
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.bodyLarge, color = CoreText, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(4.dp))
            Text(subtitle, style = MaterialTheme.typography.bodySmall, color = CoreSubtleText)
        }
        if (!trailing.isNullOrBlank()) {
            Box(
                modifier = Modifier
                    .background(trailingChipColor, RoundedCornerShape(999.dp))
                    .padding(horizontal = 10.dp, vertical = 6.dp),
            ) {
                Text(trailing, style = MaterialTheme.typography.labelMedium, color = trailingColor)
            }
        }
        if (onClick != null) {
            Icon(
                imageVector = Icons.Outlined.ArrowForwardIos,
                contentDescription = null,
                tint = Color(0xFF9AA8CC),
                modifier = Modifier.size(14.dp),
            )
        }
    }
}

@Composable
internal fun P2CoreNoteCard(
    title: String,
    text: String,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFF7FBFF), RoundedCornerShape(16.dp))
            .border(1.dp, Color(0xFFDDF1FF), RoundedCornerShape(16.dp))
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(26.dp)
                .background(Color(0xFFEAF3FF), RoundedCornerShape(8.dp)),
        )
        Spacer(modifier = Modifier.width(10.dp))
        Column {
            Text(title, style = MaterialTheme.typography.labelLarge, color = CoreText, fontWeight = FontWeight.SemiBold)
            Text(text, style = MaterialTheme.typography.bodySmall, color = CoreSubtleText)
        }
    }
}

@Composable
internal fun P2CoreQrPlaceholder() {
    val transition = rememberInfiniteTransition(label = "p2_core_qr")
    val sweep by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "p2_core_qr_sweep",
    )
    val pulse by transition.animateFloat(
        initialValue = 0.92f,
        targetValue = 1.04f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "p2_core_qr_pulse",
    )
    Box(
        modifier = Modifier
            .size(188.dp)
            .background(Color(0xFFFCFDFF), RoundedCornerShape(18.dp))
            .border(1.dp, CoreCardBorder, RoundedCornerShape(18.dp))
            .padding(18.dp),
        contentAlignment = Alignment.Center,
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val unit = size.minDimension / 7f
            val inset = (size.minDimension * (1f - pulse)) / 2f
            fun square(x: Int, y: Int, filled: Boolean = true) {
                if (filled) {
                    drawRoundRect(
                        color = CorePrimary,
                        topLeft = Offset(x * unit + inset, y * unit + inset),
                        size = androidx.compose.ui.geometry.Size(unit * 0.82f * pulse, unit * 0.82f * pulse),
                        cornerRadius = CornerRadius(unit * 0.12f, unit * 0.12f),
                    )
                }
            }
            square(0, 0); square(1, 0); square(2, 0)
            square(0, 1); square(2, 1); square(0, 2); square(1, 2); square(2, 2)
            square(4, 0); square(5, 0); square(6, 0)
            square(4, 1); square(6, 1); square(4, 2); square(5, 2); square(6, 2)
            square(0, 4); square(1, 4); square(2, 4)
            square(0, 5); square(2, 5); square(0, 6); square(1, 6); square(2, 6)
            listOf(Offset(4f, 4f), Offset(5f, 4f), Offset(4f, 5f), Offset(6f, 5f), Offset(5f, 6f)).forEach {
                square(it.x.toInt(), it.y.toInt())
            }
            val scanTop = size.height * sweep - size.height * 0.24f
            drawRoundRect(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color.Transparent,
                        Color(0x552F5BFF),
                        Color.Transparent,
                    ),
                ),
                topLeft = Offset(0f, scanTop.coerceIn(0f, size.height)),
                size = Size(size.width, size.height * 0.2f),
                cornerRadius = CornerRadius(24f, 24f),
            )
        }
    }
}

@Composable
internal fun P2CoreQrAddressCard(
    title: String,
    subtitle: String,
    address: String,
    modifier: Modifier = Modifier,
    qrContent: String = address,
    addressLabel: String = "二维码内容",
    supportingText: String? = null,
    status: String? = null,
    statusColor: Color = Color(0xFFE9F2FF),
    footer: @Composable ColumnScope.() -> Unit = {},
) {
    P2CoreCard(modifier = modifier, contentPadding = PaddingValues(18.dp)) {
        P2CoreCardHeader(
            title = title,
            subtitle = subtitle,
            trailing = status,
            trailingColor = statusColor,
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFFF8FBFF),
                            Color(0xFFF2F6FF),
                        ),
                    ),
                    shape = RoundedCornerShape(24.dp),
                )
                .border(1.dp, Color(0xFFE4ECFF), RoundedCornerShape(24.dp))
                .padding(vertical = 22.dp),
            contentAlignment = Alignment.Center,
        ) {
            P2CoreQrContent(
                content = qrContent,
                emptyLabel = "暂无可生成二维码",
            )
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFF7F9FF), RoundedCornerShape(18.dp))
                .border(1.dp, Color(0xFFE7ECF7), RoundedCornerShape(18.dp))
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(addressLabel, style = MaterialTheme.typography.labelMedium, color = CoreSubtleText)
            Text(address, style = MaterialTheme.typography.bodyLarge, color = CoreText, fontWeight = FontWeight.SemiBold)
            if (!supportingText.isNullOrBlank()) {
                Text(supportingText, style = MaterialTheme.typography.bodySmall, color = CoreSubtleText)
            }
        }
        footer()
    }
}

@Composable
private fun P2CoreQrContent(
    content: String,
    emptyLabel: String,
) {
    val normalized = content.trim().takeUnless { it.isBlank() || it == "--" }
    val qrBitmap = remember(normalized) {
        normalized?.let { QRCodeDecoder.createQRCode(it, size = 720) }
    }
    if (qrBitmap != null) {
        Image(
            bitmap = qrBitmap.asImageBitmap(),
            contentDescription = "二维码",
            modifier = Modifier.size(188.dp),
            contentScale = ContentScale.Fit,
        )
        return
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Text(
            text = emptyLabel,
            style = MaterialTheme.typography.bodySmall,
            color = CoreSubtleText,
        )
    }
}

@Composable
internal fun P2CoreChartPlaceholder(
    accent: Color = CorePrimary,
) {
    val transition = rememberInfiniteTransition(label = "p2_core_chart")
    val sweep by transition.animateFloat(
        initialValue = -0.35f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2600, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "p2_core_chart_sweep",
    )
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(122.dp)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFF7F9FF),
                        Color(0xFFF0F5FF),
                    ),
                ),
                shape = RoundedCornerShape(18.dp),
            )
            .border(1.dp, Color(0xFFE3EAF9), RoundedCornerShape(18.dp))
            .padding(12.dp),
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val stepX = size.width / 6f
            val points = listOf(0.72f, 0.68f, 0.74f, 0.61f, 0.58f, 0.34f, 0.22f)
            for (grid in 1..3) {
                drawLine(
                    color = Color(0xFFE5ECFA),
                    start = Offset(0f, size.height * grid / 4f),
                    end = Offset(size.width, size.height * grid / 4f),
                    strokeWidth = 2f,
                )
            }
            val sweepWidth = size.width * 0.28f
            drawRoundRect(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        Color.Transparent,
                        accent.copy(alpha = 0.09f),
                        Color.Transparent,
                    ),
                ),
                topLeft = Offset(size.width * sweep - sweepWidth, 0f),
                size = Size(sweepWidth, size.height),
                cornerRadius = CornerRadius(18.dp.toPx(), 18.dp.toPx()),
            )
            for (i in 1 until points.size) {
                drawLine(
                    color = accent,
                    start = Offset(stepX * (i - 1), size.height * points[i - 1]),
                    end = Offset(stepX * i, size.height * points[i]),
                    strokeWidth = 7f,
                    cap = StrokeCap.Round,
                )
            }
            points.forEachIndexed { index, point ->
                drawCircle(
                    color = Color.White,
                    radius = 8f,
                    center = Offset(stepX * index, size.height * point),
                )
                drawCircle(
                    color = accent,
                    radius = 5f,
                    center = Offset(stepX * index, size.height * point),
                )
            }
        }
    }
}

@Composable
internal fun P2CoreChartInfoBlock(
    title: String,
    subtitle: String,
    chips: List<String>,
    infoItems: List<Pair<String, String>>,
    modifier: Modifier = Modifier,
    highlight: String? = null,
    highlightColor: Color = Color(0xFFEAF6FF),
    accent: Color = CorePrimary,
    activeChipIndex: Int = 0,
) {
    P2CoreCard(modifier = modifier, contentPadding = PaddingValues(18.dp)) {
        P2CoreCardHeader(
            title = title,
            subtitle = subtitle,
            trailing = highlight,
            trailingColor = highlightColor,
        )
        P2CoreChartPlaceholder(accent = accent)
        if (chips.isNotEmpty()) {
            P2CoreChipRow(items = chips, activeIndex = activeChipIndex % chips.size)
        }
        if (infoItems.isNotEmpty()) {
            P2CoreMetricGrid(items = infoItems)
        }
    }
}

@Composable
internal fun CorePrimaryButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val transition = rememberInfiniteTransition(label = "p2_core_primary_button")
    val scanOffset by transition.animateFloat(
        initialValue = -0.45f,
        targetValue = 1.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2200, easing = LinearEasing),
        ),
        label = "p2_core_primary_button_scan",
    )
    Button(
        onClick = onClick,
        modifier = modifier
            .height(52.dp)
            .drawWithContent {
                drawContent()
                val scanWidth = size.width * 0.34f
                drawRoundRect(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.White.copy(alpha = 0.36f),
                            Color.Transparent,
                        ),
                    ),
                    topLeft = Offset(
                        x = size.width * scanOffset - scanWidth,
                        y = 0f,
                    ),
                    size = Size(scanWidth, size.height),
                    cornerRadius = CornerRadius(size.height / 2f, size.height / 2f),
                )
            },
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(containerColor = CorePrimary),
    ) {
        Text(label, color = Color.White, style = MaterialTheme.typography.titleMedium)
    }
}

@Composable
internal fun CoreActionRow(
    primaryActionLabel: String,
    onPrimaryAction: () -> Unit,
    secondaryActionLabel: String,
    onSecondaryAction: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        OutlinedButton(
            onClick = onSecondaryAction,
            modifier = Modifier
                .weight(1f)
                .height(52.dp),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, Color(0xFFD7E3FF)),
            colors = ButtonDefaults.outlinedButtonColors(containerColor = Color(0xFFF7FAFF)),
        ) {
            Text(secondaryActionLabel, color = Color(0xFF506188), style = MaterialTheme.typography.titleSmall)
        }
        CorePrimaryButton(
            label = primaryActionLabel,
            onClick = onPrimaryAction,
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun StatusChip(
    text: String,
    color: Color = Color(0xFFE9F2FF),
) {
    Box(
        modifier = Modifier
            .background(color, RoundedCornerShape(999.dp))
            .padding(horizontal = 12.dp, vertical = 7.dp),
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            color = if (color.luminance() > 0.72f) Color(0xFF3752B8) else Color.White,
        )
    }
}

@Composable
private fun CoreBottomNav(
    activeSection: CoreNavSection,
    onBottomNav: (String) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .drawBehind {
                drawLine(
                    color = CoreCardBorder.copy(alpha = 0.75f),
                    start = Offset(0f, 0f),
                    end = Offset(size.width, 0f),
                    strokeWidth = 1.dp.toPx(),
                )
            }
            .background(Color.White.copy(alpha = 0.97f))
            .padding(horizontal = 4.dp, vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Box(modifier = Modifier.weight(1f)) {
            CoreBottomNavItem(
                label = "总览",
                icon = { active, tint -> Icon(Icons.Outlined.Home, contentDescription = null, tint = if (active) tint else Color(0xFF97A4C4)) },
                active = activeSection == CoreNavSection.Overview,
                onClick = { onBottomNav(CryptoVpnRouteSpec.vpnHome.pattern) },
            )
        }
        Box(modifier = Modifier.weight(1f)) {
            CoreBottomNavItem(
                label = "VPN",
                icon = { active, tint -> Icon(Icons.Outlined.Shield, contentDescription = null, tint = if (active) tint else Color(0xFF97A4C4)) },
                active = activeSection == CoreNavSection.Vpn,
                onClick = { onBottomNav(CryptoVpnRouteSpec.plans.pattern) },
            )
        }
        Box(modifier = Modifier.weight(1f)) {
            CoreBottomNavItem(
                label = "钱包",
                icon = { active, tint -> Icon(Icons.Outlined.AccountBalanceWallet, contentDescription = null, tint = if (active) tint else Color(0xFF97A4C4)) },
                active = activeSection == CoreNavSection.Wallet,
                onClick = { onBottomNav(CryptoVpnRouteSpec.walletHome.pattern) },
            )
        }
        Box(modifier = Modifier.weight(1f)) {
            CoreBottomNavItem(
                label = "增长",
                icon = { active, tint -> Icon(Icons.Outlined.AutoGraph, contentDescription = null, tint = if (active) tint else Color(0xFF97A4C4)) },
                active = activeSection == CoreNavSection.Growth,
                onClick = { onBottomNav(CryptoVpnRouteSpec.inviteCenter.pattern) },
            )
        }
        Box(modifier = Modifier.weight(1f)) {
            CoreBottomNavItem(
                label = "我的",
                icon = { active, tint -> Icon(Icons.Outlined.PersonOutline, contentDescription = null, tint = if (active) tint else Color(0xFF97A4C4)) },
                active = activeSection == CoreNavSection.Profile,
                onClick = { onBottomNav(CryptoVpnRouteSpec.profile.pattern) },
            )
        }
    }
}

@Composable
private fun CoreBottomNavItem(
    label: String,
    icon: @Composable (Boolean, Color) -> Unit,
    active: Boolean,
    onClick: () -> Unit,
) {
    val activeTint = CorePrimary
    Column(
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(vertical = 3.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        Box(
            modifier = Modifier
                .size(22.dp)
                .background(if (active) Color(0xFFEAF0FF) else Color.Transparent, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            icon(active, activeTint)
        }
        Text(
            label,
            style = MaterialTheme.typography.labelSmall,
            color = if (active) activeTint else Color(0xFF8B98BA),
        )
    }
}
