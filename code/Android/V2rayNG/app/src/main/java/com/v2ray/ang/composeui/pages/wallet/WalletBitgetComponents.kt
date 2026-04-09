package com.v2ray.ang.composeui.pages.wallet

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.v2ray.ang.composeui.theme.BackgroundDeepest
import com.v2ray.ang.composeui.theme.ControlPlaneIntent
import com.v2ray.ang.composeui.theme.ControlPlaneLayer
import com.v2ray.ang.composeui.theme.ControlPlaneTokens
import com.v2ray.ang.composeui.theme.Error
import com.v2ray.ang.composeui.theme.Info
import com.v2ray.ang.composeui.theme.SolanaPurple
import com.v2ray.ang.composeui.theme.TronRed
import com.v2ray.ang.composeui.theme.USDTGreen
import com.v2ray.ang.composeui.theme.Warning

private val WalletLayer0 = ControlPlaneTokens.layer(ControlPlaneLayer.Level0)
private val WalletLayer1 = ControlPlaneTokens.layer(ControlPlaneLayer.Level1)
private val WalletLayer2 = ControlPlaneTokens.layer(ControlPlaneLayer.Level2)
private val WalletLayer3 = ControlPlaneTokens.layer(ControlPlaneLayer.Level3)
private val WalletInfra = ControlPlaneTokens.Infra
private val WalletSettlement = ControlPlaneTokens.Settlement
private val WalletFinance = ControlPlaneTokens.Finance
private val WalletCritical = ControlPlaneTokens.Critical

internal val WalletAccent = WalletInfra.accent
internal val WalletBackgroundTop = WalletLayer0.container
internal val WalletBackgroundMiddle = WalletLayer1.container
internal val WalletBackgroundBottom = WalletLayer0.container
internal val WalletSurface = WalletLayer1.container
internal val WalletSurfaceStrong = WalletLayer2.container
internal val WalletSurfaceMuted = WalletLayer3.container
internal val WalletOutline = WalletLayer2.outline
internal val WalletTextPrimary = ControlPlaneTokens.Ink
internal val WalletTextSecondary = ControlPlaneTokens.InkSecondary
internal val WalletTextTertiary = ControlPlaneTokens.InkTertiary
internal val WalletDanger = WalletCritical.accent
internal val WalletWarningSurface = ControlPlaneTokens.Warning.container
internal val WalletPagePadding = 20.dp

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
            .background(WalletLayer0.container),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(228.dp)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            WalletInfra.container.copy(alpha = 0.72f),
                            WalletLayer0.container,
                        ),
                    ),
                ),
        )
        Box(
            modifier = Modifier
                .size(232.dp)
                .padding(start = 170.dp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(WalletInfra.accent.copy(alpha = 0.06f), Color.Transparent),
                    ),
                    shape = CircleShape,
                ),
        )
        Box(
            modifier = Modifier
                .size(176.dp)
                .padding(top = 208.dp, start = 8.dp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(WalletSettlement.accent.copy(alpha = 0.05f), Color.Transparent),
                    ),
                    shape = CircleShape,
                ),
        )
        Box(
            modifier = Modifier
                .size(154.dp)
                .padding(top = 336.dp, start = 250.dp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(WalletFinance.accent.copy(alpha = 0.04f), Color.Transparent),
                    ),
                    shape = CircleShape,
                ),
        )
        content()
    }
}

@Composable
internal fun WalletGlassCard(
    modifier: Modifier = Modifier,
    accent: Color = WalletAccent,
    layer: ControlPlaneLayer = ControlPlaneLayer.Level1,
    selected: Boolean = false,
    contentPadding: PaddingValues = PaddingValues(horizontal = 20.dp, vertical = 18.dp),
    content: @Composable ColumnScope.() -> Unit,
) {
    val palette = ControlPlaneTokens.layer(
        when {
            selected && layer == ControlPlaneLayer.Level1 -> ControlPlaneLayer.Level2
            selected && layer == ControlPlaneLayer.Level2 -> ControlPlaneLayer.Level3
            else -> layer
        },
    )
    val borderColor = if (selected) accent.copy(alpha = 0.34f) else palette.outline

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(24.dp),
        color = palette.container,
        border = BorderStroke(if (selected) 1.6.dp else 1.dp, borderColor),
        shadowElevation = if (selected) palette.shadowElevation + 3.dp else palette.shadowElevation,
        tonalElevation = palette.tonalElevation,
    ) {
        Column(
            modifier = Modifier
                .animateContentSize(
                    animationSpec = tween(
                        durationMillis = ControlPlaneTokens.Motion.stateChange.durationMillis,
                        easing = ControlPlaneTokens.Motion.stateChange.easing,
                    ),
                )
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            accent.copy(alpha = if (selected) 0.1f else 0.05f),
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
internal fun WalletBottomSheetCard(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(horizontal = 24.dp, vertical = 20.dp),
    content: @Composable ColumnScope.() -> Unit,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        color = WalletLayer3.container,
        border = BorderStroke(1.dp, WalletLayer3.outline),
        shadowElevation = WalletLayer3.shadowElevation + 4.dp,
        tonalElevation = WalletLayer3.tonalElevation,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            WalletInfra.container.copy(alpha = 0.54f),
                            WalletLayer3.container,
                        ),
                    ),
                )
                .padding(contentPadding),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Box(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .width(42.dp)
                    .height(5.dp)
                    .background(WalletLayer3.outline.copy(alpha = 0.9f), RoundedCornerShape(999.dp)),
            )
            content()
        }
    }
}

@Composable
internal fun WalletSectionHeading(
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Text(
            text = "ASSET CONSOLE",
            style = MaterialTheme.typography.labelMedium.copy(letterSpacing = 1.sp),
            color = WalletTextTertiary,
            fontWeight = FontWeight.SemiBold,
        )
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            color = WalletTextPrimary,
            fontWeight = FontWeight.Bold,
        )
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodySmall,
            color = WalletTextSecondary,
        )
    }
}

@Composable
internal fun WalletConsoleHeader(
    eyebrow: String,
    title: String,
    detail: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top,
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = eyebrow,
                style = MaterialTheme.typography.labelMedium.copy(letterSpacing = 1.sp),
                color = WalletTextTertiary,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge.copy(letterSpacing = (-0.3).sp),
                color = WalletTextPrimary,
                fontWeight = FontWeight.Bold,
            )
        }
        Text(
            text = detail,
            style = MaterialTheme.typography.labelLarge,
            color = WalletTextTertiary,
            fontWeight = FontWeight.Medium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
internal fun WalletSummaryMetric(
    title: String,
    value: String,
    supporting: String,
    intent: ControlPlaneIntent,
    modifier: Modifier = Modifier,
) {
    val palette = ControlPlaneTokens.intent(intent)

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(18.dp),
        color = palette.container,
        border = BorderStroke(1.dp, palette.border),
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                color = if (intent == ControlPlaneIntent.Neutral) WalletTextSecondary else palette.accent,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                color = WalletTextPrimary,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = supporting,
                style = MaterialTheme.typography.bodySmall,
                color = WalletTextSecondary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
internal fun WalletIntentBadge(
    text: String,
    intent: ControlPlaneIntent,
    modifier: Modifier = Modifier,
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
            modifier = Modifier.padding(
                horizontal = if (compact) 8.dp else 10.dp,
                vertical = if (compact) 5.dp else 7.dp,
            ),
            style = if (compact) MaterialTheme.typography.labelSmall else MaterialTheme.typography.labelLarge,
            color = if (intent == ControlPlaneIntent.Neutral) WalletTextSecondary else palette.accent,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
        )
    }
}

@Composable
internal fun WalletSelectionChip(
    label: String,
    selected: Boolean,
    intent: ControlPlaneIntent,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val intentPalette = ControlPlaneTokens.intent(intent)
    val containerColor by animateColorAsState(
        targetValue = if (selected) intentPalette.container else WalletLayer1.container,
        animationSpec = tween(
            durationMillis = ControlPlaneTokens.Motion.stateChange.durationMillis,
            easing = ControlPlaneTokens.Motion.stateChange.easing,
        ),
        label = "walletSelectionContainer",
    )
    val borderColor by animateColorAsState(
        targetValue = if (selected) intentPalette.border else WalletLayer2.outline,
        animationSpec = tween(
            durationMillis = ControlPlaneTokens.Motion.stateChange.durationMillis,
            easing = ControlPlaneTokens.Motion.stateChange.easing,
        ),
        label = "walletSelectionBorder",
    )
    val textColor by animateColorAsState(
        targetValue = if (selected) WalletTextPrimary else WalletTextSecondary,
        animationSpec = tween(
            durationMillis = ControlPlaneTokens.Motion.stateChange.durationMillis,
            easing = ControlPlaneTokens.Motion.stateChange.easing,
        ),
        label = "walletSelectionText",
    )

    Surface(
        modifier = modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        color = containerColor,
        border = BorderStroke(1.dp, borderColor),
        shadowElevation = if (selected) 4.dp else 0.dp,
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 9.dp),
            style = MaterialTheme.typography.titleSmall,
            color = textColor,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
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
                shape = RoundedCornerShape(18.dp),
                color = WalletLayer2.container,
                border = BorderStroke(1.dp, WalletLayer2.outline),
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Text(
                        text = metric.label,
                        style = MaterialTheme.typography.labelSmall,
                        color = WalletTextTertiary,
                    )
                    Text(
                        text = metric.value,
                        style = MaterialTheme.typography.titleMedium,
                        color = WalletTextPrimary,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
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
            WalletGlassCard(
                modifier = Modifier
                    .width(162.dp)
                    .clickable(onClick = action.onClick),
                accent = action.accent,
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Surface(
                        modifier = Modifier.size(40.dp),
                        shape = CircleShape,
                        color = action.accent.copy(alpha = 0.16f),
                        border = BorderStroke(1.dp, action.accent.copy(alpha = 0.28f)),
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = action.icon,
                                contentDescription = action.label,
                                tint = action.accent,
                            )
                        }
                    }
                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text(
                            text = action.label,
                            style = MaterialTheme.typography.titleSmall,
                            color = WalletTextPrimary,
                            fontWeight = FontWeight.SemiBold,
                        )
                        Text(
                            text = action.hint,
                            style = MaterialTheme.typography.bodySmall,
                            color = WalletTextSecondary,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
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
    Surface(
        modifier = modifier.size(56.dp),
        shape = RoundedCornerShape(18.dp),
        color = accent.copy(alpha = 0.14f),
        border = BorderStroke(1.dp, accent.copy(alpha = 0.26f)),
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = symbol.take(2).uppercase(),
                style = MaterialTheme.typography.titleMedium,
                color = accent,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

@Composable
internal fun WalletTag(
    text: String,
    modifier: Modifier = Modifier,
    accent: Color = WalletAccent,
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        color = accent.copy(alpha = 0.12f),
        border = BorderStroke(1.dp, accent.copy(alpha = 0.22f)),
    ) {
        Text(
            text = text.uppercase(),
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            style = MaterialTheme.typography.labelMedium,
            color = accent,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
internal fun WalletPrimaryButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    icon: ImageVector? = null,
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(54.dp),
        enabled = enabled,
        shape = RoundedCornerShape(20.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = WalletAccent,
            contentColor = WalletInfra.onAccent,
            disabledContainerColor = WalletAccent.copy(alpha = 0.4f),
            disabledContentColor = WalletInfra.onAccent.copy(alpha = 0.6f),
        ),
    ) {
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(18.dp),
            )
        }
        if (icon != null) {
            Box(modifier = Modifier.width(8.dp))
        }
        Text(
            text = label,
            fontWeight = FontWeight.SemiBold,
            style = MaterialTheme.typography.titleSmall,
        )
    }
}

@Composable
internal fun WalletSecondaryButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    icon: ImageVector? = null,
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.height(54.dp),
        enabled = enabled,
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, WalletLayer2.outline),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = WalletLayer1.container,
            contentColor = WalletTextPrimary,
        ),
    ) {
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(18.dp),
            )
        }
        if (icon != null) {
            Box(modifier = Modifier.width(8.dp))
        }
        Text(
            text = label,
            fontWeight = FontWeight.Medium,
            style = MaterialTheme.typography.titleSmall,
        )
    }
}

@Composable
internal fun WalletToolbarIconButton(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    tint: Color = WalletTextPrimary,
) {
    Surface(
        modifier = modifier
            .size(44.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        color = WalletLayer3.container,
        border = BorderStroke(1.dp, WalletLayer3.outline),
        shadowElevation = 2.dp,
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                tint = tint,
            )
        }
    }
}

@Composable
internal fun WalletSearchChrome(
    avatarLabel: String,
    onAvatarClick: () -> Unit,
    onSearchClick: () -> Unit,
    onTrailingClick: () -> Unit,
    trailingIcon: ImageVector,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Surface(
            modifier = Modifier
                .size(42.dp)
                .clickable(onClick = onAvatarClick),
            shape = RoundedCornerShape(16.dp),
            color = WalletInfra.container,
            border = BorderStroke(1.dp, WalletInfra.border),
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = avatarLabel,
                    color = WalletAccent,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium,
                )
            }
        }
        Surface(
            modifier = Modifier
                .weight(1f)
                .height(40.dp)
                .clickable(onClick = onSearchClick),
            shape = RoundedCornerShape(22.dp),
            color = WalletLayer3.container,
            border = BorderStroke(1.dp, WalletLayer3.outline),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    tint = WalletTextTertiary,
                )
                Text(
                    text = "全局搜索",
                    style = MaterialTheme.typography.bodyMedium,
                    color = WalletTextTertiary,
                )
            }
        }
        WalletToolbarIconButton(
            icon = trailingIcon,
            contentDescription = "wallet toolbar action",
            onClick = onTrailingClick,
        )
    }
}

@Composable
internal fun WalletTopBar(
    title: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    trailingIcon: ImageVector? = null,
    trailingDescription: String = "",
    onTrailingClick: () -> Unit = {},
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        WalletToolbarIconButton(
            icon = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "back",
            onClick = onBack,
        )
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(1.dp),
        ) {
            Text(
                text = "WALLET CONTROL",
                style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.sp),
                color = WalletTextTertiary,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                color = WalletTextPrimary,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        if (trailingIcon != null) {
            WalletToolbarIconButton(
                icon = trailingIcon,
                contentDescription = trailingDescription,
                onClick = onTrailingClick,
            )
        } else {
            Box(modifier = Modifier.size(44.dp))
        }
    }
}

@Composable
internal fun WalletCloseBar(
    title: String? = null,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
    trailingIcon: ImageVector? = null,
    trailingDescription: String = "",
    onTrailingClick: () -> Unit = {},
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        WalletToolbarIconButton(
            icon = Icons.Default.Close,
            contentDescription = "close",
            onClick = onClose,
        )
        if (title != null) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(1.dp),
            ) {
                Text(
                    text = "SETTLEMENT VIEW",
                    style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.sp),
                    color = WalletTextTertiary,
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    color = WalletTextPrimary,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        } else {
            Box(modifier = Modifier.weight(1f))
        }
        if (trailingIcon != null) {
            WalletToolbarIconButton(
                icon = trailingIcon,
                contentDescription = trailingDescription,
                onClick = onTrailingClick,
            )
        } else {
            Box(modifier = Modifier.size(44.dp))
        }
    }
}

@Composable
internal fun WalletDivider(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(WalletLayer3.outline.copy(alpha = 0.8f)),
    )
}

@Composable
internal fun WalletInfoRow(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    valueColor: Color = WalletTextPrimary,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = WalletTextSecondary,
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = valueColor,
            fontWeight = FontWeight.Medium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
internal fun WalletAssistPillButton(
    label: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier
            .height(44.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        color = WalletLayer1.container,
        border = BorderStroke(1.dp, WalletLayer2.outline),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 18.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = WalletAccent,
                modifier = Modifier.size(18.dp),
            )
            Text(
                text = label,
                color = WalletTextPrimary,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Medium,
            )
        }
    }
}

@Composable
internal fun WalletInputField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    leadingIcon: ImageVector? = null,
    trailingContent: (@Composable () -> Unit)? = null,
    singleLine: Boolean = true,
    minLines: Int = 1,
    maxLines: Int = 1,
    readOnly: Boolean = false,
    isError: Boolean = false,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.fillMaxWidth(),
        singleLine = singleLine,
        minLines = minLines,
        maxLines = maxLines,
        readOnly = readOnly,
        isError = isError,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        visualTransformation = visualTransformation,
        textStyle = MaterialTheme.typography.bodyLarge.copy(color = WalletTextPrimary),
        placeholder = {
            Text(
                text = placeholder,
                style = MaterialTheme.typography.bodyLarge,
                color = WalletTextTertiary,
            )
        },
        leadingIcon = if (leadingIcon != null) {
            {
                Icon(
                    imageVector = leadingIcon,
                    contentDescription = null,
                    tint = WalletTextSecondary,
                )
            }
        } else {
            null
        },
        trailingIcon = trailingContent,
        shape = RoundedCornerShape(24.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = WalletLayer1.container,
            unfocusedContainerColor = WalletLayer1.container,
            disabledContainerColor = WalletLayer1.container,
            focusedBorderColor = WalletAccent.copy(alpha = 0.62f),
            unfocusedBorderColor = WalletLayer2.outline,
            disabledBorderColor = WalletLayer2.outline,
            errorBorderColor = WalletDanger,
            cursorColor = WalletAccent,
            focusedTextColor = WalletTextPrimary,
            unfocusedTextColor = WalletTextPrimary,
            focusedPlaceholderColor = WalletTextTertiary,
            unfocusedPlaceholderColor = WalletTextTertiary,
            focusedLeadingIconColor = WalletTextSecondary,
            unfocusedLeadingIconColor = WalletTextSecondary,
            focusedTrailingIconColor = WalletTextSecondary,
            unfocusedTrailingIconColor = WalletTextSecondary,
        ),
    )
}

internal fun walletAssetAccent(symbol: String): Color {
    return when (symbol.uppercase()) {
        "USDT", "USDC", "USD24" -> USDTGreen
        "SOL" -> SolanaPurple
        "TRX", "TRON" -> TronRed
        "ETH" -> Info
        "BNB" -> Warning
        else -> WalletAccent
    }
}

internal fun walletNetworkLabel(symbol: String): String {
    return when (symbol.uppercase()) {
        "USD24" -> "Arbitrum"
        "USDT", "TRX", "TRON" -> "TRON"
        "SOL" -> "Solana"
        "ETH" -> "Ethereum"
        "BNB" -> "BNB Smart Chain"
        "MATIC" -> "Polygon"
        else -> "Mainnet"
    }
}

internal fun walletReferencePrice(symbol: String): String {
    return when (symbol.uppercase()) {
        "ETH" -> "$2,233.56"
        "SOL" -> "$175.80"
        "BNB" -> "$602.30"
        "USD24", "USDT", "USDC" -> "$1.00"
        else -> "--"
    }
}

internal fun walletReferenceChange(symbol: String): String {
    return when (symbol.uppercase()) {
        "ETH" -> "+5.68%"
        "SOL" -> "+3.40%"
        "BNB" -> "+1.22%"
        "USD24" -> "-0.08%"
        "USDT", "USDC" -> "0.00%"
        else -> "--"
    }
}

internal fun walletShortAddress(address: String): String {
    if (address.length <= 14) return address
    return "${address.take(8)}...${address.takeLast(6)}"
}
