package com.v2ray.ang.composeui.pages.p1

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.animateFloat
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.v2ray.ang.composeui.common.model.FeatureListItem
import com.v2ray.ang.composeui.p0.ui.P01Card
import com.v2ray.ang.composeui.p0.ui.P01BottomIcon
import com.v2ray.ang.composeui.p0.ui.P01BottomIconKind
import com.v2ray.ang.composeui.p0.ui.P01PrimaryButton

private val P1AccentBlue = Color(0xFF4276FF)
private val P1AccentCyan = Color(0xFF20D3EE)
private val P1AccentViolet = Color(0xFF8B5CF6)
private val P1HubGlyphBlue = Color(0xFF244FD6)
private val P1TextStrong = Color(0xFF132748)
private val P1TextBody = Color(0xFF4D6287)
private val P1CardShape = RoundedCornerShape(28.dp)
private val P1RowShape = RoundedCornerShape(18.dp)
private val P1ContractPlaceholderTitles = setOf("路由标识", "导航参数", "表单占位", "交付内容")

internal fun List<FeatureListItem>.p1ContentItems(): List<FeatureListItem> =
    filterNot { it.title in P1ContractPlaceholderTitles }

@Composable
internal fun P1SelectableCard(
    selected: Boolean,
    modifier: Modifier = Modifier,
    centered: Boolean = false,
    accentColor: Color = P1AccentBlue,
    content: @Composable ColumnScope.() -> Unit,
) {
    val tint by animateColorAsState(
        targetValue = if (selected) accentColor.copy(alpha = 0.1f) else Color.Transparent,
        animationSpec = tween(durationMillis = 220),
        label = "p1_card_tint",
    )
    val borderColor by animateColorAsState(
        targetValue = if (selected) accentColor.copy(alpha = 0.22f) else Color.Transparent,
        animationSpec = tween(durationMillis = 220),
        label = "p1_card_border",
    )
    val scale by animateFloatAsState(
        targetValue = if (selected) 1.016f else 1f,
        animationSpec = spring(dampingRatio = 0.82f, stiffness = 620f),
        label = "p1_card_scale",
    )
    val lift by animateFloatAsState(
        targetValue = if (selected) -4f else 0f,
        animationSpec = spring(dampingRatio = 0.84f, stiffness = 700f),
        label = "p1_card_lift",
    )

    P01Card(
        modifier = modifier
            .offset(y = lift.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clip(P1CardShape)
            .background(tint)
            .border(1.dp, borderColor, P1CardShape),
        centered = centered,
        content = content,
    )
}

@Composable
internal fun P1FeedbackRow(
    title: String,
    value: String? = null,
    copy: String? = null,
    selected: Boolean = false,
    accentColor: Color = P1AccentBlue,
    valueColor: Color = accentColor,
    onClick: (() -> Unit)? = null,
) {
    val interaction = remember { MutableInteractionSource() }
    val isPressed by interaction.collectIsPressedAsState()
    val active = selected || isPressed
    val backgroundColor by animateColorAsState(
        targetValue = when {
            selected -> accentColor.copy(alpha = 0.1f)
            isPressed -> accentColor.copy(alpha = 0.05f)
            else -> Color.Transparent
        },
        animationSpec = tween(durationMillis = 160),
        label = "p1_row_bg",
    )
    val borderColor by animateColorAsState(
        targetValue = if (selected) accentColor.copy(alpha = 0.18f) else Color.Transparent,
        animationSpec = tween(durationMillis = 200),
        label = "p1_row_border",
    )
    val titleColor by animateColorAsState(
        targetValue = if (selected) accentColor else P1TextStrong,
        animationSpec = tween(durationMillis = 200),
        label = "p1_row_title",
    )
    val bodyColor by animateColorAsState(
        targetValue = if (selected) P1TextStrong.copy(alpha = 0.74f) else P1TextBody,
        animationSpec = tween(durationMillis = 200),
        label = "p1_row_copy",
    )
    val trailingColor by animateColorAsState(
        targetValue = if (selected) valueColor else P1TextStrong,
        animationSpec = tween(durationMillis = 200),
        label = "p1_row_value",
    )
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.992f else 1f,
        animationSpec = tween(durationMillis = 120),
        label = "p1_row_scale",
    )
    val indicatorAlpha by animateFloatAsState(
        targetValue = if (active) 1f else 0f,
        animationSpec = tween(durationMillis = 180),
        label = "p1_row_indicator",
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(P1RowShape)
            .background(backgroundColor)
            .border(1.dp, borderColor, P1RowShape)
            .drawBehind {
                if (indicatorAlpha > 0f) {
                    val stripeWidth = 3.dp.toPx()
                    drawRoundRect(
                        color = accentColor.copy(alpha = 0.72f * indicatorAlpha),
                        topLeft = Offset(0f, size.height * 0.22f),
                        size = Size(stripeWidth, size.height * 0.56f),
                        cornerRadius = CornerRadius(stripeWidth, stripeWidth),
                    )
                }
            }
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clickable(
                enabled = onClick != null,
                interactionSource = interaction,
                indication = null,
            ) { onClick?.invoke() }
            .padding(start = 14.dp, end = 12.dp, top = 14.dp, bottom = 14.dp),
        horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top,
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(if (copy.isNullOrBlank()) 0.dp else 4.dp),
        ) {
            Text(
                text = title,
                color = titleColor,
                fontSize = 15.sp,
                lineHeight = 19.sp,
                fontWeight = FontWeight.Bold,
            )
            if (!copy.isNullOrBlank()) {
                Text(
                    text = copy,
                    color = bodyColor,
                    fontSize = 12.sp,
                    lineHeight = 19.sp,
                )
            }
        }
        if (!value.isNullOrBlank()) {
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = value,
                color = trailingColor,
                fontSize = 16.sp,
                lineHeight = 20.sp,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

@Composable
internal fun P1PrimaryCta(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    active: Boolean = true,
) {
    val transition = rememberInfiniteTransition(label = "p1_primary_cta")
    val pulse by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1700, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "p1_primary_cta_pulse",
    )
    val emphasis = if (active) pulse else 0f

    P01PrimaryButton(
        text = text,
        onClick = onClick,
        modifier = modifier.graphicsLayer {
            val scale = 1f + (0.014f * emphasis)
            scaleX = scale
            scaleY = scale
        },
    )
}

@Composable
internal fun P1SecureHub(
    label: String = "SECURE",
) {
    val transition = rememberInfiniteTransition(label = "p1_secure_hub")
    val outerRingRotation by transition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 12000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "p1_secure_hub_outer_ring_rotation",
    )
    val innerRingRotation by transition.animateFloat(
        initialValue = 360f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 18000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "p1_secure_hub_inner_ring_rotation",
    )
    val glowScale by transition.animateFloat(
        initialValue = 0.96f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "p1_secure_hub_glow_scale",
    )

    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .background(Color.White.copy(alpha = 0.82f), RoundedCornerShape(999.dp))
                .border(1.dp, P1AccentBlue.copy(alpha = 0.16f), RoundedCornerShape(999.dp))
                .padding(horizontal = 10.dp, vertical = 6.dp),
        ) {
            Text(
                text = label,
                color = P1HubGlyphBlue,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
            )
        }
        P1ConfirmedOrb(
            outerRingRotation = outerRingRotation,
            innerRingRotation = innerRingRotation,
            glowScale = glowScale,
        )
    }
}

@Composable
private fun P1ConfirmedOrb(
    outerRingRotation: Float,
    innerRingRotation: Float,
    glowScale: Float,
) {
    Box(
        modifier = Modifier.size(64.dp),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    scaleX = glowScale
                    scaleY = glowScale
                }
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            P1AccentBlue.copy(alpha = 0.16f),
                            P1AccentCyan.copy(alpha = 0.12f),
                            Color.Transparent,
                        ),
                    ),
                    shape = CircleShape,
                ),
        )
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeWidth = 1.2.dp.toPx()
            drawCircle(
                color = P1AccentBlue.copy(alpha = 0.15f),
                radius = size.minDimension * 0.40f,
                style = Stroke(width = strokeWidth),
            )
            drawCircle(
                color = P1AccentCyan.copy(alpha = 0.14f),
                radius = size.minDimension * 0.48f,
                style = Stroke(width = strokeWidth),
            )
        }
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer { rotationZ = outerRingRotation },
        ) {
            drawCircle(
                color = P1AccentBlue.copy(alpha = 0.34f),
                radius = size.minDimension * 0.42f,
                style = Stroke(
                    width = 1.5.dp.toPx(),
                    pathEffect = PathEffect.dashPathEffect(
                        intervals = floatArrayOf(6.dp.toPx(), 7.dp.toPx()),
                    ),
                ),
            )
        }
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer { rotationZ = innerRingRotation },
        ) {
            drawCircle(
                color = P1AccentCyan.copy(alpha = 0.28f),
                radius = size.minDimension * 0.48f,
                style = Stroke(
                    width = 1.3.dp.toPx(),
                    pathEffect = PathEffect.dashPathEffect(
                        intervals = floatArrayOf(5.dp.toPx(), 6.dp.toPx()),
                    ),
                ),
            )
        }
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .size(44.dp)
                .background(
                    brush = Brush.sweepGradient(
                        colors = listOf(
                            P1AccentBlue.copy(alpha = 0.94f),
                            P1AccentCyan.copy(alpha = 0.76f),
                            P1AccentViolet.copy(alpha = 0.72f),
                            P1AccentBlue.copy(alpha = 0.94f),
                        ),
                    ),
                    shape = CircleShape,
                )
                .border(1.dp, Color.White.copy(alpha = 0.36f), CircleShape),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color.White.copy(alpha = 0.96f),
                                Color.White.copy(alpha = 0.78f),
                                P1AccentBlue.copy(alpha = 0.28f),
                                Color.Transparent,
                            ),
                        ),
                        shape = CircleShape,
                    ),
            )
        }
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .size(26.dp)
                .clip(CircleShape)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.98f),
                            Color(0xFFF3F7FF),
                            P1AccentBlue.copy(alpha = 0.18f),
                        ),
                    ),
                    shape = CircleShape,
                )
                .border(1.dp, Color.White.copy(alpha = 0.78f), CircleShape),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(2.dp)
                    .clip(CircleShape)
                    .border(1.dp, P1AccentBlue.copy(alpha = 0.16f), CircleShape),
            )
            P01BottomIcon(
                kind = P01BottomIconKind.WALLET,
                tint = P1HubGlyphBlue,
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(15.dp),
            )
        }
    }
}
