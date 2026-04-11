package com.v2ray.ang.composeui.pages.p2extended

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.v2ray.ang.composeui.effects.MotionProfile
import com.v2ray.ang.composeui.effects.TechParticleBackground

@Composable
internal fun P2ExtendedPageScaffold(
    kicker: String,
    title: String,
    subtitle: String,
    hubLabel: String,
    onHubClick: () -> Unit,
    primaryActionLabel: String? = null,
    onPrimaryAction: (() -> Unit)? = null,
    secondaryActionLabel: String? = null,
    onSecondaryAction: (() -> Unit)? = null,
    content: @Composable ColumnScopeWrapper.() -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.Transparent,
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            TechParticleBackground(
                motionProfile = MotionProfile.L1,
                modifier = Modifier.fillMaxSize(),
                showNetwork = true,
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 8.dp),
            ) {
                HeroSection(
                    kicker = kicker,
                    title = title,
                    subtitle = subtitle,
                    hubLabel = hubLabel,
                    onHubClick = onHubClick,
                )
                Spacer(modifier = Modifier.height(12.dp))
                ColumnScopeWrapper.content()
                if (primaryActionLabel != null && onPrimaryAction != null) {
                    Spacer(modifier = Modifier.height(14.dp))
                    ExtendedPrimaryButton(
                        label = primaryActionLabel,
                        onClick = onPrimaryAction,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
                if (secondaryActionLabel != null && onSecondaryAction != null) {
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedButton(
                        onClick = onSecondaryAction,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(14.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFD6DDFE)),
                    ) {
                        Text(secondaryActionLabel, color = Color(0xFF3C4D8A), style = MaterialTheme.typography.titleMedium)
                    }
                }
                Spacer(modifier = Modifier.height(14.dp))
            }
        }
    }
}

@Composable
private fun ExtendedPrimaryButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val transition = rememberInfiniteTransition(label = "p2e_primary_button")
    val scanOffset by transition.animateFloat(
        initialValue = -0.45f,
        targetValue = 1.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2200, easing = LinearEasing),
        ),
        label = "p2e_primary_button_scan",
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
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2F5BFF)),
    ) {
        Text(label, color = Color.White, style = MaterialTheme.typography.titleMedium)
    }
}

@Composable
private fun HeroSection(
    kicker: String,
    title: String,
    subtitle: String,
    hubLabel: String,
    onHubClick: () -> Unit,
) {
    val shape = RoundedCornerShape(22.dp)
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(Color(0xFF1F2E66), Color(0xFF385DE6)),
                ),
                shape = shape,
            )
            .padding(18.dp),
    ) {
        Column(modifier = Modifier.padding(end = 96.dp)) {
            Text(kicker, color = Color(0xFFAEC4FF), style = MaterialTheme.typography.labelLarge)
            Spacer(modifier = Modifier.height(6.dp))
            Text(title, color = Color.White, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Text(subtitle, color = Color(0xFFE3EAFF), style = MaterialTheme.typography.bodyMedium)
        }
        Row(
            modifier = Modifier.align(Alignment.TopEnd),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .background(Color(0x26FFFFFF), RoundedCornerShape(999.dp))
                    .border(1.dp, Color(0x33FFFFFF), RoundedCornerShape(999.dp))
                    .padding(horizontal = 12.dp, vertical = 8.dp),
            ) {
                Text(hubLabel, color = Color.White, style = MaterialTheme.typography.labelMedium)
            }
            P2HubOrb(onClick = onHubClick)
        }
    }
}

@Composable
private fun P2HubOrb(
    onClick: () -> Unit,
) {
    val transition = rememberInfiniteTransition(label = "p2e_hub")
    val rotation by transition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 8200, easing = LinearEasing),
        ),
        label = "p2e_hub_rotation",
    )
    val pulse by transition.animateFloat(
        initialValue = 0.94f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1800, easing = LinearEasing),
        ),
        label = "p2e_hub_pulse",
    )
    val glow by transition.animateFloat(
        initialValue = 0.18f,
        targetValue = 0.36f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2200, easing = LinearEasing),
        ),
        label = "p2e_hub_glow",
    )

    Box(
        modifier = Modifier
            .size(56.dp)
            .graphicsLayer {
                rotationZ = rotation
                scaleX = pulse
                scaleY = pulse
            }
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.92f),
                        Color(0x338D7DFF).copy(alpha = 0.35f + glow),
                        Color.Transparent,
                    ),
                ),
                shape = CircleShape,
            )
            .border(2.dp, Color(0x99B7C6FF), CircleShape)
            .padding(10.dp),
    ) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .border(1.dp, Color(0x66C7D7FF), CircleShape),
        )
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .size(24.dp)
                .background(Color.White.copy(alpha = 0.96f), CircleShape)
                .border(1.dp, Color(0x334F7CFF), CircleShape),
        ) {
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(10.dp)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(Color(0xFF4F7CFF), Color(0xFF20D3EE)),
                        ),
                        shape = CircleShape,
                    ),
            )
        }
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(Color.Transparent, CircleShape),
        )
    }
}

@Composable
internal fun P2Card(
    title: String,
    subtitle: String? = null,
    content: @Composable () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(18.dp))
            .border(1.dp, Color(0xFFE9ECF8), RoundedCornerShape(18.dp))
            .padding(16.dp),
    ) {
        Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, color = Color(0xFF192140))
        if (subtitle != null) {
            Spacer(modifier = Modifier.height(6.dp))
            Text(subtitle, style = MaterialTheme.typography.bodySmall, color = Color(0xFF6671A1))
        }
        Spacer(modifier = Modifier.height(14.dp))
        content()
    }
}

@Composable
internal fun FieldRow(label: String, value: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFF7F9FF), RoundedCornerShape(12.dp))
            .padding(12.dp),
    ) {
        Text(label, style = MaterialTheme.typography.labelSmall, color = Color(0xFF6E78A4))
        Spacer(modifier = Modifier.height(4.dp))
        Text(value, style = MaterialTheme.typography.bodyLarge, color = Color(0xFF1F2A52))
    }
}

@Composable
internal fun ChipRow(items: List<String>, activeIndex: Int = 0) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
        items.forEachIndexed { index, item ->
            Box(
                modifier = Modifier
                    .background(
                        if (index == activeIndex) Color(0xFFE7EDFF) else Color(0xFFF2F4FA),
                        RoundedCornerShape(999.dp),
                    )
                    .padding(horizontal = 12.dp, vertical = 7.dp),
            ) {
                Text(item, style = MaterialTheme.typography.labelMedium, color = if (index == activeIndex) Color(0xFF2D4ED7) else Color(0xFF5D688E))
            }
        }
    }
}

@Composable
internal fun KpiRow(items: List<Pair<String, String>>) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        items.forEach { (label, value) ->
            Column(
                modifier = Modifier
                    .weight(1f)
                    .background(Color(0xFFF7F9FF), RoundedCornerShape(12.dp))
                    .padding(10.dp),
            ) {
                Text(label, style = MaterialTheme.typography.labelSmall, color = Color(0xFF6F79A8))
                Spacer(modifier = Modifier.height(6.dp))
                Text(value, style = MaterialTheme.typography.titleMedium, color = Color(0xFF1E2A56), fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
internal fun ListRow(title: String, subtitle: String, trailing: String? = null) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(14.dp))
            .border(1.dp, Color(0xFFE9ECF8), RoundedCornerShape(14.dp))
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .background(Color(0xFFEBF0FF), RoundedCornerShape(8.dp)),
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.bodyLarge, color = Color(0xFF1E274D), fontWeight = FontWeight.SemiBold)
            Text(subtitle, style = MaterialTheme.typography.bodySmall, color = Color(0xFF68739E))
        }
        if (trailing != null) {
            Text(trailing, style = MaterialTheme.typography.labelMedium, color = Color(0xFF7A84AF))
        }
    }
}

@Composable
internal fun OptionCard(
    title: String,
    subtitle: String,
    selected: Boolean = false,
    badge: String? = null,
) {
    val borderColor = if (selected) Color(0xFF9AB1FF) else Color(0xFFE9ECF8)
    val backgroundColor = if (selected) Color(0xFFF1F5FF) else Color.White
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(backgroundColor, RoundedCornerShape(14.dp))
            .border(1.dp, borderColor, RoundedCornerShape(14.dp))
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(30.dp)
                .background(
                    if (selected) Color(0xFFDCE6FF) else Color(0xFFEBF0FF),
                    RoundedCornerShape(9.dp),
                ),
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.bodyLarge, color = Color(0xFF1E274D), fontWeight = FontWeight.SemiBold)
            Text(subtitle, style = MaterialTheme.typography.bodySmall, color = Color(0xFF68739E))
        }
        if (badge != null) {
            Spacer(modifier = Modifier.width(8.dp))
            Box(
                modifier = Modifier
                    .background(Color(0xFFEAF0FF), RoundedCornerShape(999.dp))
                    .border(1.dp, Color(0xFFD4DEFF), RoundedCornerShape(999.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp),
            ) {
                Text(
                    badge,
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFF3150D0),
                )
            }
        }
        Spacer(modifier = Modifier.width(8.dp))
        Box(
            modifier = Modifier
                .size(18.dp)
                .border(1.5.dp, if (selected) Color(0xFF2D4ED7) else Color(0xFFB8C1E5), CircleShape)
                .padding(3.dp),
        ) {
            if (selected) {
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .background(Color(0xFF2D4ED7), CircleShape),
                )
            }
        }
    }
}

@Composable
internal fun MnemonicGrid(words: List<String>) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        words.chunked(3).forEachIndexed { rowIndex, rowWords ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                rowWords.forEachIndexed { columnIndex, word ->
                    val absoluteIndex = rowIndex * 3 + columnIndex + 1
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .background(Color(0xFFF7F9FF), RoundedCornerShape(12.dp))
                            .border(1.dp, Color(0xFFE9ECF8), RoundedCornerShape(12.dp))
                            .padding(horizontal = 10.dp, vertical = 9.dp),
                    ) {
                        Text(
                            absoluteIndex.toString().padStart(2, '0'),
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFF6E78A4),
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            word,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFF1F2A52),
                            fontWeight = FontWeight.SemiBold,
                        )
                    }
                }
                repeat(3 - rowWords.size) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
internal fun DetectedChainList(chains: List<String>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(14.dp))
            .border(1.dp, Color(0xFFE9ECF8), RoundedCornerShape(14.dp))
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        chains.forEach { chain ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF7F9FF), RoundedCornerShape(10.dp))
                    .padding(horizontal = 10.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(Color(0xFF2D4ED7), CircleShape),
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    chain,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF25315E),
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.weight(1f),
                )
                Text(
                    "已识别",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFF6270A4),
                )
            }
        }
    }
}

@Composable
internal fun NoteCard(title: String, text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(14.dp))
            .border(1.dp, Color(0xFFE9ECF8), RoundedCornerShape(14.dp))
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(26.dp)
                .background(Color(0x1F25CB88), RoundedCornerShape(8.dp)),
        )
        Spacer(modifier = Modifier.width(10.dp))
        Column {
            Text(title, style = MaterialTheme.typography.labelLarge, color = Color(0xFF1D274D), fontWeight = FontWeight.SemiBold)
            Text(text, style = MaterialTheme.typography.bodySmall, color = Color(0xFF6673A0))
        }
    }
}

@Composable
internal fun ImportMethodOptionCard(
    title: String,
    subtitle: String,
    tag: String? = null,
    highlighted: Boolean = false,
) {
    val borderColor = if (highlighted) Color(0xFFB6C5FF) else Color(0xFFE9ECF8)
    val backgroundColor = if (highlighted) Color(0xFFF2F5FF) else Color.White
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(backgroundColor, RoundedCornerShape(14.dp))
            .border(1.dp, borderColor, RoundedCornerShape(14.dp))
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(34.dp)
                .background(Color(0xFFE7EEFF), RoundedCornerShape(10.dp))
                .border(1.dp, Color(0xFFD7E2FF), RoundedCornerShape(10.dp)),
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = Color(0xFF1E274D),
                fontWeight = FontWeight.SemiBold,
            )
            Spacer(modifier = Modifier.height(3.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF6673A0),
            )
        }
        if (tag != null) {
            Box(
                modifier = Modifier
                    .background(Color(0xFFEAF0FF), RoundedCornerShape(999.dp))
                    .border(1.dp, Color(0xFFD4DEFF), RoundedCornerShape(999.dp))
                    .padding(horizontal = 10.dp, vertical = 6.dp),
            ) {
                Text(tag, style = MaterialTheme.typography.labelSmall, color = Color(0xFF3150D0))
            }
        }
    }
}

@Composable
internal fun MnemonicChunkCard(
    chunkLabel: String,
    words: List<String>,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFF7F9FF), RoundedCornerShape(12.dp))
            .border(1.dp, Color(0xFFE9ECF8), RoundedCornerShape(12.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .background(Color.White, RoundedCornerShape(999.dp))
                .border(1.dp, Color(0xFFDCE3FF), RoundedCornerShape(999.dp))
                .padding(horizontal = 10.dp, vertical = 5.dp),
        ) {
            Text(chunkLabel, style = MaterialTheme.typography.labelSmall, color = Color(0xFF3E5197))
        }
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = words.joinToString("  ·  "),
            style = MaterialTheme.typography.bodyMedium,
            color = Color(0xFF1F2A52),
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
internal fun MnemonicCheckpointRow(
    label: String,
    answer: String,
    verified: Boolean,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(12.dp))
            .border(1.dp, Color(0xFFE8ECF9), RoundedCornerShape(12.dp))
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(label, style = MaterialTheme.typography.labelMedium, color = Color(0xFF6671A1))
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                answer,
                style = MaterialTheme.typography.bodyLarge,
                color = Color(0xFF1E274D),
                fontWeight = FontWeight.SemiBold,
            )
        }
        SecurityStatusPill(
            label = if (verified) "校验通过" else "待确认",
            healthy = verified,
        )
    }
}

@Composable
internal fun SecurityStatusPill(
    label: String,
    healthy: Boolean,
    modifier: Modifier = Modifier,
) {
    val bg = if (healthy) Color(0xFFEAFBF1) else Color(0xFFFFF4E8)
    val border = if (healthy) Color(0xFFC7F0D8) else Color(0xFFFFE2BF)
    val text = if (healthy) Color(0xFF177245) else Color(0xFF995315)
    Row(
        modifier = modifier
            .defaultMinSize(minHeight = 30.dp)
            .background(bg, RoundedCornerShape(999.dp))
            .border(1.dp, border, RoundedCornerShape(999.dp))
            .padding(horizontal = 10.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(7.dp)
                .background(text, CircleShape),
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(label, style = MaterialTheme.typography.labelMedium, color = text)
    }
}

@Composable
internal fun P2FlowStepCard(
    step: String,
    title: String,
    detail: String,
    emphasized: Boolean = false,
) {
    val background = if (emphasized) Color(0xFFF0F4FF) else Color(0xFFF7F9FF)
    val border = if (emphasized) Color(0xFFCBD8FF) else Color(0xFFE5EAF8)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(background, RoundedCornerShape(14.dp))
            .border(1.dp, border, RoundedCornerShape(14.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .background(Color.White, RoundedCornerShape(999.dp))
                .border(1.dp, Color(0xFFD7DFFB), RoundedCornerShape(999.dp))
                .padding(horizontal = 10.dp, vertical = 5.dp),
        ) {
            Text(step, style = MaterialTheme.typography.labelSmall, color = Color(0xFF40539C))
        }
        Spacer(modifier = Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                title,
                style = MaterialTheme.typography.bodyLarge,
                color = Color(0xFF1E274D),
                fontWeight = FontWeight.SemiBold,
            )
            Spacer(modifier = Modifier.height(3.dp))
            Text(
                detail,
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF6673A0),
            )
        }
    }
}

@Composable
internal fun P2SecurityActionCard(
    title: String,
    detail: String,
    badge: String? = null,
    risk: Boolean = false,
) {
    val border = if (risk) Color(0xFFFFD7C2) else Color(0xFFE8ECF8)
    val background = if (risk) Color(0xFFFFFAF6) else Color.White
    val badgeBg = if (risk) Color(0xFFFFEFE5) else Color(0xFFEAF0FF)
    val badgeText = if (risk) Color(0xFFB25A1C) else Color(0xFF3150D0)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(background, RoundedCornerShape(14.dp))
            .border(1.dp, border, RoundedCornerShape(14.dp))
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(34.dp)
                .background(
                    if (risk) Color(0xFFFFEFE5) else Color(0xFFE7EEFF),
                    RoundedCornerShape(10.dp),
                )
                .border(
                    1.dp,
                    if (risk) Color(0xFFFFDCC8) else Color(0xFFD7E2FF),
                    RoundedCornerShape(10.dp),
                ),
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = Color(0xFF1E274D),
                fontWeight = FontWeight.SemiBold,
            )
            Spacer(modifier = Modifier.height(3.dp))
            Text(
                text = detail,
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF6673A0),
            )
        }
        if (badge != null) {
            Box(
                modifier = Modifier
                    .background(badgeBg, RoundedCornerShape(999.dp))
                    .border(1.dp, border, RoundedCornerShape(999.dp))
                    .padding(horizontal = 10.dp, vertical = 6.dp),
            ) {
                Text(badge, style = MaterialTheme.typography.labelSmall, color = badgeText)
            }
        }
    }
}

@Composable
internal fun P2InlineWarningCard(
    title: String,
    text: String,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFFFF6EC), RoundedCornerShape(14.dp))
            .border(1.dp, Color(0xFFFFE0C5), RoundedCornerShape(14.dp))
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .background(Color(0xFFF18A3D), CircleShape),
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(
                title,
                style = MaterialTheme.typography.labelLarge,
                color = Color(0xFF9D4E1D),
                fontWeight = FontWeight.SemiBold,
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text,
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFFAF6736),
            )
        }
    }
}

@Composable
internal fun P2SearchShell(
    placeholder: String,
    quickHint: String,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(14.dp))
            .border(1.dp, Color(0xFFE9ECF8), RoundedCornerShape(14.dp))
            .padding(12.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFF7F9FF), RoundedCornerShape(10.dp))
                .border(1.dp, Color(0xFFE7EBFA), RoundedCornerShape(10.dp))
                .padding(horizontal = 10.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(Color(0xFF7A8AC3), CircleShape),
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                placeholder,
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF6A76A4),
            )
            Spacer(modifier = Modifier.weight(1f))
            SecurityStatusPill(label = "可访问", healthy = true)
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            quickHint,
            style = MaterialTheme.typography.bodySmall,
            color = Color(0xFF6974A0),
        )
    }
}

@Composable
internal fun P2SwapPairCard(
    payToken: String,
    payChain: String,
    payAmount: String,
    receiveToken: String,
    receiveChain: String,
    receiveAmount: String,
    routeDetail: String,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(16.dp))
            .border(1.dp, Color(0xFFE9ECF8), RoundedCornerShape(16.dp))
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        FieldRow(label = "支付", value = "$payToken · $payChain · $payAmount")
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            Box(
                modifier = Modifier
                    .width(28.dp)
                    .height(2.dp)
                    .background(Color(0xFFD9E0FA), RoundedCornerShape(999.dp)),
            )
            Spacer(modifier = Modifier.width(8.dp))
            SecurityStatusPill(label = "路径计算中", healthy = true)
            Spacer(modifier = Modifier.width(8.dp))
            Box(
                modifier = Modifier
                    .width(28.dp)
                    .height(2.dp)
                    .background(Color(0xFFD9E0FA), RoundedCornerShape(999.dp)),
            )
        }
        FieldRow(label = "获得", value = "$receiveToken · $receiveChain · $receiveAmount")
        NoteCard(title = "路由详情", text = routeDetail)
    }
}

@Composable
internal fun P2BridgeFlowCard(
    sourceChain: String,
    targetChain: String,
    asset: String,
    amount: String,
    eta: String,
    fee: String,
) {
    P2Card(
        title = "桥接流程",
        subtitle = "从来源链冻结资产，跨链验证后在目标链释放。",
    ) {
        P2FlowStepCard(
            step = "01",
            title = "来源链：$sourceChain",
            detail = "$asset · $amount",
            emphasized = true,
        )
        Spacer(modifier = Modifier.height(8.dp))
        P2FlowStepCard(
            step = "02",
            title = "目标链：$targetChain",
            detail = "预计到账 $eta",
        )
        Spacer(modifier = Modifier.height(8.dp))
        KpiRow(
            items = listOf(
                "手续费" to fee,
                "链路状态" to "健康",
                "确认数" to "12/12",
            ),
        )
    }
}

@Composable
internal fun P2SessionAppCard(
    title: String,
    subtitle: String,
    network: String,
    riskFlag: Boolean = false,
    actionLabel: String = "断开",
) {
    val border = if (riskFlag) Color(0xFFFFD5C0) else Color(0xFFE9ECF8)
    val background = if (riskFlag) Color(0xFFFFFAF6) else Color.White
    val badgeLabel = if (riskFlag) "高风险" else "活跃"
    val badgeBg = if (riskFlag) Color(0xFFFFEFE5) else Color(0xFFEAFBF1)
    val badgeColor = if (riskFlag) Color(0xFFAA5621) else Color(0xFF177245)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(background, RoundedCornerShape(14.dp))
            .border(1.dp, border, RoundedCornerShape(14.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(34.dp)
                .background(Color(0xFFE8EEFF), RoundedCornerShape(10.dp))
                .border(1.dp, Color(0xFFD8E2FF), RoundedCornerShape(10.dp)),
        )
        Spacer(modifier = Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                title,
                style = MaterialTheme.typography.bodyLarge,
                color = Color(0xFF1E274D),
                fontWeight = FontWeight.SemiBold,
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                "$subtitle · $network",
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF6673A0),
            )
        }
        Column(horizontalAlignment = Alignment.End) {
            Box(
                modifier = Modifier
                    .background(badgeBg, RoundedCornerShape(999.dp))
                    .border(1.dp, border, RoundedCornerShape(999.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp),
            ) {
                Text(badgeLabel, style = MaterialTheme.typography.labelSmall, color = badgeColor)
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                actionLabel,
                style = MaterialTheme.typography.labelMedium,
                color = Color(0xFF3651C8),
                fontWeight = FontWeight.SemiBold,
            )
        }
    }
}

@Composable
internal fun P2SignRequestCard(
    dapp: String,
    domain: String,
    operation: String,
    network: String,
    payload: String,
    gasHint: String,
) {
    P2Card(title = "$dapp 请求签名", subtitle = domain) {
        FieldRow("操作类型", operation)
        Spacer(modifier = Modifier.height(8.dp))
        FieldRow("网络", network)
        Spacer(modifier = Modifier.height(8.dp))
        FieldRow("签名摘要", payload)
        Spacer(modifier = Modifier.height(8.dp))
        NoteCard(title = "预估网络费", text = gasHint)
    }
}

internal object ColumnScopeWrapper
