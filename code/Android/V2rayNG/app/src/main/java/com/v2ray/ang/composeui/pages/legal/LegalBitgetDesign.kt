package com.v2ray.ang.composeui.pages.legal

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.v2ray.ang.composeui.theme.ControlPlaneIntent
import com.v2ray.ang.composeui.theme.ControlPlaneLayer
import com.v2ray.ang.composeui.theme.ControlPlaneTokens

private val LegalLayer0 = ControlPlaneTokens.layer(ControlPlaneLayer.Level0)
private val LegalLayer1 = ControlPlaneTokens.layer(ControlPlaneLayer.Level1)
private val LegalLayer2 = ControlPlaneTokens.layer(ControlPlaneLayer.Level2)
private val LegalLayer3 = ControlPlaneTokens.layer(ControlPlaneLayer.Level3)
private val LegalInfra = ControlPlaneTokens.Infra
private val LegalSettlement = ControlPlaneTokens.Settlement
private val LegalFinance = ControlPlaneTokens.Finance

internal val LegalPageBackground = LegalLayer0.container
internal val LegalCardBackground = LegalLayer1.container
internal val LegalCardRaised = LegalLayer2.container
internal val LegalAccent = LegalInfra.accent
internal val LegalAccentDeep = LegalInfra.onContainer
internal val LegalTextPrimary = ControlPlaneTokens.Ink
internal val LegalTextSecondary = ControlPlaneTokens.InkSecondary
internal val LegalTextTertiary = ControlPlaneTokens.InkTertiary
internal val LegalBorder = LegalLayer2.outline

private val LegalCardShape = RoundedCornerShape(24.dp)

@Composable
internal fun LegalBitgetBackground(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(LegalPageBackground),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(232.dp)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            LegalInfra.container.copy(alpha = 0.68f),
                            LegalPageBackground,
                        ),
                    ),
                ),
        )
        Box(
            modifier = Modifier
                .size(228.dp)
                .padding(start = 170.dp)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(LegalInfra.accent.copy(alpha = 0.06f), Color.Transparent),
                    ),
                    shape = CircleShape,
                ),
        )
        Box(
            modifier = Modifier
                .size(170.dp)
                .padding(top = 206.dp, start = 8.dp)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(LegalSettlement.accent.copy(alpha = 0.04f), Color.Transparent),
                    ),
                    shape = CircleShape,
                ),
        )
        Box(
            modifier = Modifier
                .size(146.dp)
                .padding(top = 334.dp, start = 252.dp)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(LegalFinance.accent.copy(alpha = 0.04f), Color.Transparent),
                    ),
                    shape = CircleShape,
                ),
        )
        content()
    }
}

@Composable
internal fun LegalPageScaffold(
    topBar: @Composable () -> Unit,
    content: @Composable (PaddingValues) -> Unit,
) {
    LegalBitgetBackground {
        Scaffold(
            containerColor = Color.Transparent,
            contentColor = LegalTextPrimary,
            contentWindowInsets = WindowInsets.safeDrawing,
            topBar = topBar,
        ) { paddingValues ->
            content(paddingValues)
        }
    }
}

@Composable
internal fun LegalTopBar(
    title: String? = null,
    subtitle: String? = null,
    onNavigateBack: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.statusBars)
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Surface(
            modifier = Modifier.size(42.dp),
            shape = RoundedCornerShape(16.dp),
            color = LegalLayer3.container,
            border = BorderStroke(1.dp, LegalLayer3.outline),
            shadowElevation = 4.dp,
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable(onClick = onNavigateBack),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = LegalTextPrimary,
                )
            }
        }

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            Text(
                text = "EVIDENCE CONTROL",
                style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.1.sp),
                color = LegalAccent,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
            )
            if (title != null) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = LegalTextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    color = LegalTextSecondary,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }

        Surface(
            shape = RoundedCornerShape(12.dp),
            color = LegalInfra.container,
            border = BorderStroke(1.dp, LegalInfra.border),
        ) {
            Text(
                text = "AUDIT",
                color = LegalAccent,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
            )
        }
    }
}

@Composable
internal fun LegalCard(
    modifier: Modifier = Modifier,
    layer: ControlPlaneLayer = ControlPlaneLayer.Level1,
    accentWash: Color? = null,
    contentPadding: PaddingValues = PaddingValues(20.dp),
    content: @Composable ColumnScope.() -> Unit,
) {
    val palette = ControlPlaneTokens.layer(layer)

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .animateContentSize(
                animationSpec = tween(
                    durationMillis = ControlPlaneTokens.Motion.stateChange.durationMillis,
                    easing = ControlPlaneTokens.Motion.stateChange.easing,
                ),
            ),
        shape = LegalCardShape,
        color = palette.container,
        border = BorderStroke(1.dp, palette.outline),
        shadowElevation = palette.shadowElevation,
        tonalElevation = palette.tonalElevation,
    ) {
        Column(
            modifier = Modifier
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(accentWash ?: Color.Transparent, palette.container),
                    ),
                )
                .padding(contentPadding),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            content = content,
        )
    }
}

@Composable
internal fun LegalHighlightCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    LegalCard(
        modifier = modifier,
        layer = ControlPlaneLayer.Level2,
        accentWash = LegalInfra.container.copy(alpha = 0.72f),
        contentPadding = PaddingValues(22.dp),
        content = content,
    )
}

@Composable
internal fun LegalBadge(
    text: String,
    modifier: Modifier = Modifier,
    intent: ControlPlaneIntent = ControlPlaneIntent.Neutral,
    compact: Boolean = false,
) {
    val palette = ControlPlaneTokens.intent(intent)

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(if (compact) 10.dp else 14.dp),
        color = palette.container,
        border = BorderStroke(1.dp, palette.border),
    ) {
        Text(
            text = text,
            color = if (intent == ControlPlaneIntent.Neutral) LegalTextSecondary else palette.accent,
            fontSize = if (compact) 11.sp else 12.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(
                horizontal = if (compact) 8.dp else 10.dp,
                vertical = if (compact) 5.dp else 7.dp,
            ),
        )
    }
}

@Composable
internal fun LegalSectionTitle(
    title: String,
    subtitle: String? = null,
    trailing: @Composable (() -> Unit)? = null,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top,
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = "EVIDENCE REGISTER",
                color = LegalTextTertiary,
                style = MaterialTheme.typography.labelMedium.copy(letterSpacing = 1.sp),
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = title,
                color = LegalTextPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    color = LegalTextSecondary,
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        }
        trailing?.invoke()
    }
}

@Composable
internal fun LegalListDivider() {
    HorizontalDivider(color = LegalBorder, thickness = 0.8.dp)
}

@Composable
internal fun LegalStatusView(
    title: String,
    message: String,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        LegalCard(
            modifier = Modifier.padding(horizontal = 20.dp),
            layer = ControlPlaneLayer.Level2,
            accentWash = ControlPlaneTokens.Critical.container.copy(alpha = 0.72f),
        ) {
            Text(
                text = title,
                color = LegalTextPrimary,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = message,
                color = LegalTextSecondary,
                fontSize = 14.sp,
                lineHeight = 20.sp,
            )
        }
    }
}
