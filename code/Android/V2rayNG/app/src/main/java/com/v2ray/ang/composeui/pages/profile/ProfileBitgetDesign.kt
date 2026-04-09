package com.v2ray.ang.composeui.pages.profile

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

private val ProfileLayer0 = ControlPlaneTokens.layer(ControlPlaneLayer.Level0)
private val ProfileLayer1 = ControlPlaneTokens.layer(ControlPlaneLayer.Level1)
private val ProfileLayer2 = ControlPlaneTokens.layer(ControlPlaneLayer.Level2)
private val ProfileLayer3 = ControlPlaneTokens.layer(ControlPlaneLayer.Level3)
private val ProfileInfra = ControlPlaneTokens.Infra
private val ProfileSettlement = ControlPlaneTokens.Settlement
private val ProfileFinance = ControlPlaneTokens.Finance

internal val ProfilePageBackground = ProfileLayer0.container
internal val ProfileSurface = ProfileLayer1.container
internal val ProfileSurfaceRaised = ProfileLayer2.container
internal val ProfileSurfaceStrong = ProfileLayer3.container
internal val ProfileAccent = ProfileInfra.accent
internal val ProfileAccentDeep = ProfileInfra.onContainer
internal val ProfileTextPrimary = ControlPlaneTokens.Ink
internal val ProfileTextSecondary = ControlPlaneTokens.InkSecondary
internal val ProfileTextTertiary = ControlPlaneTokens.InkTertiary
internal val ProfileDanger = ControlPlaneTokens.Critical.accent
internal val ProfileWarningSurface = ControlPlaneTokens.Warning.container
internal val ProfileWarningText = ControlPlaneTokens.Warning.accent
internal val ProfileDivider = ProfileLayer2.outline

private val ProfileCardShape = RoundedCornerShape(24.dp)

@Composable
internal fun ProfileBitgetBackground(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(ProfilePageBackground),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(228.dp)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            ProfileInfra.container.copy(alpha = 0.72f),
                            ProfilePageBackground,
                        ),
                    ),
                ),
        )
        Box(
            modifier = Modifier
                .size(220.dp)
                .padding(start = 152.dp)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(ProfileInfra.accent.copy(alpha = 0.06f), Color.Transparent),
                    ),
                    shape = CircleShape,
                ),
        )
        Box(
            modifier = Modifier
                .size(164.dp)
                .padding(top = 172.dp, start = 18.dp)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(ProfileSettlement.accent.copy(alpha = 0.05f), Color.Transparent),
                    ),
                    shape = CircleShape,
                ),
        )
        Box(
            modifier = Modifier
                .size(138.dp)
                .padding(top = 326.dp, start = 248.dp)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(ProfileFinance.accent.copy(alpha = 0.04f), Color.Transparent),
                    ),
                    shape = CircleShape,
                ),
        )
        content()
    }
}

@Composable
internal fun ProfilePageScaffold(
    topBar: @Composable () -> Unit,
    content: @Composable (PaddingValues) -> Unit,
) {
    ProfileBitgetBackground {
        Scaffold(
            containerColor = Color.Transparent,
            contentColor = ProfileTextPrimary,
            contentWindowInsets = WindowInsets.safeDrawing,
            topBar = topBar,
        ) { paddingValues ->
            content(paddingValues)
        }
    }
}

@Composable
internal fun ProfileTopBar(
    title: String,
    onNavigateBack: (() -> Unit)? = null,
    subtitle: String? = null,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.statusBars)
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        if (onNavigateBack != null) {
            Surface(
                modifier = Modifier.size(42.dp),
                shape = RoundedCornerShape(16.dp),
                color = ProfileSurfaceStrong,
                border = BorderStroke(1.dp, ProfileLayer3.outline),
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
                        tint = ProfileTextPrimary,
                    )
                }
            }
        } else {
            SpacerSlot()
        }

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            Text(
                text = "ACCOUNT CONTROL",
                style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.1.sp),
                color = ProfileAccent,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
            )
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = ProfileTextPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    color = ProfileTextSecondary,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }

        Surface(
            shape = RoundedCornerShape(12.dp),
            color = ProfileInfra.container,
            border = BorderStroke(1.dp, ProfileInfra.border),
        ) {
            Text(
                text = "INFRA",
                color = ProfileAccent,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
            )
        }
    }
}

@Composable
private fun SpacerSlot() {
    Box(modifier = Modifier.size(42.dp))
}

@Composable
internal fun ProfileCard(
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
        shape = ProfileCardShape,
        color = palette.container,
        border = BorderStroke(1.dp, palette.outline),
        shadowElevation = palette.shadowElevation,
        tonalElevation = palette.tonalElevation,
    ) {
        Column(
            modifier = Modifier
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            accentWash ?: Color.Transparent,
                            palette.container,
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
internal fun ProfileBadge(
    text: String,
    modifier: Modifier = Modifier,
    intent: ControlPlaneIntent = ControlPlaneIntent.Neutral,
    compact: Boolean = false,
) {
    val palette = ControlPlaneTokens.intent(intent)

    Surface(
        modifier = modifier,
        color = palette.container,
        shape = RoundedCornerShape(if (compact) 10.dp else 14.dp),
        border = BorderStroke(1.dp, palette.border),
    ) {
        Text(
            text = text,
            color = if (intent == ControlPlaneIntent.Neutral) ProfileTextSecondary else palette.accent,
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
internal fun ProfileSectionHeading(
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Text(
            text = "PROFILE MATRIX",
            color = ProfileTextTertiary,
            style = MaterialTheme.typography.labelMedium.copy(letterSpacing = 1.sp),
            fontWeight = FontWeight.SemiBold,
        )
        Text(
            text = title,
            color = ProfileTextPrimary,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
        )
        Text(
            text = subtitle,
            color = ProfileTextSecondary,
            style = MaterialTheme.typography.bodySmall,
        )
    }
}

@Composable
internal fun ProfileListDivider() {
    HorizontalDivider(color = ProfileDivider, thickness = 0.8.dp)
}
