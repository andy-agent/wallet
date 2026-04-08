package com.v2ray.ang.composeui.pages.vpn

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.v2ray.ang.composeui.theme.BackgroundPrimary
import com.v2ray.ang.composeui.theme.BackgroundDeepest
import com.v2ray.ang.composeui.theme.Error
import com.v2ray.ang.composeui.theme.TextPrimary
import com.v2ray.ang.composeui.theme.TextSecondary
import com.v2ray.ang.composeui.theme.TextTertiary

internal val VpnPageHorizontalPadding = 20.dp
internal val VpnPageTopPadding = 16.dp
internal val VpnPageBottomPadding = 132.dp

internal val VpnAccent = Color(0xFF35D4E6)
internal val VpnAccentSoft = VpnAccent.copy(alpha = 0.16f)
internal val VpnSurface = Color(0xFFFFFFFF)
internal val VpnSurfaceStrong = Color(0xFFF6FAFD)
internal val VpnSurfaceMuted = Color(0xFFEFF4F8)
internal val VpnOutline = Color(0xFFD8E2EC)
internal val VpnWarningSurface = Color(0xFFFFF6EA)
internal val VpnSheetScrim = Color(0x33102033)
internal val VpnPositive = VpnAccent
internal val VpnNegative = Color(0xFFE9687F)
internal val VpnDisabledButton = Color(0xFFC6D5E1)

private val VpnCardShape = RoundedCornerShape(28.dp)
private val VpnRowShape = RoundedCornerShape(20.dp)
private val VpnSheetShape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
private val VpnPillShape = RoundedCornerShape(999.dp)

data class VpnHeroMetric(
    val label: String,
    val value: String,
)

data class VpnDockItem(
    val label: String,
    val icon: ImageVector,
)

data class VpnChartCandle(
    val open: Float,
    val close: Float,
    val high: Float,
    val low: Float,
)

@Composable
fun VpnBitgetBackground(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFFBFDFF),
                        Color(0xFFF6FAFD),
                        BackgroundPrimary,
                    ),
                ),
            ),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(VpnAccent.copy(alpha = 0.08f), Color.Transparent),
                        center = Offset(600f, 260f),
                        radius = 860f,
                    ),
                ),
        )
        content()
    }
}

@Composable
fun VpnTopChrome(
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
    onBack: (() -> Unit)? = null,
    actionIcon: ImageVector? = null,
    onActionClick: (() -> Unit)? = null,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        if (onBack != null) {
            VpnTopBarIcon(
                icon = Icons.AutoMirrored.Filled.ArrowBack,
                onClick = onBack,
            )
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
        }

        if (actionIcon != null && onActionClick != null) {
            VpnTopBarIcon(
                icon = actionIcon,
                onClick = onActionClick,
            )
        }
    }
}

@Composable
fun VpnCenterTopBar(
    title: String,
    modifier: Modifier = Modifier,
    onBack: (() -> Unit)? = null,
    backIcon: ImageVector = Icons.AutoMirrored.Filled.ArrowBack,
    rightText: String? = null,
    onRightTextClick: (() -> Unit)? = null,
    rightIcon: ImageVector? = null,
    onRightIconClick: (() -> Unit)? = null,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(modifier = Modifier.width(40.dp), contentAlignment = Alignment.CenterStart) {
            if (onBack != null) {
                VpnTopBarIcon(
                    icon = backIcon,
                    onClick = onBack,
                )
            }
        }
        Text(
            text = title,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = TextPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Box(modifier = Modifier.width(40.dp), contentAlignment = Alignment.CenterEnd) {
            when {
                rightText != null && onRightTextClick != null -> {
                    Text(
                        text = rightText,
                        modifier = Modifier.clickable(onClick = onRightTextClick),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = TextPrimary,
                    )
                }

                rightIcon != null && onRightIconClick != null -> {
                    VpnTopBarIcon(
                        icon = rightIcon,
                        onClick = onRightIconClick,
                    )
                }
            }
        }
    }
}

@Composable
private fun VpnTopBarIcon(
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .size(36.dp)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = TextPrimary,
        )
    }
}

@Composable
fun VpnTabStrip(
    tabs: List<String>,
    selectedIndex: Int,
    modifier: Modifier = Modifier,
    onSelect: (Int) -> Unit = {},
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        tabs.forEachIndexed { index, label ->
            Column(
                modifier = Modifier.clickable { onSelect(index) },
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = if (selectedIndex == index) FontWeight.SemiBold else FontWeight.Medium,
                    color = if (selectedIndex == index) TextPrimary else TextSecondary,
                )
                Box(
                    modifier = Modifier
                        .width(24.dp)
                        .height(3.dp)
                        .clip(VpnPillShape)
                        .background(if (selectedIndex == index) TextPrimary else Color.Transparent),
                )
            }
        }
    }
}

@Composable
fun VpnValueBlock(
    value: String,
    change: String,
    modifier: Modifier = Modifier,
    helper: String? = null,
    changeColor: Color = VpnAccent,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
        )
        Text(
            text = change,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Medium,
            color = changeColor,
        )
        if (!helper.isNullOrBlank()) {
            VpnStatusChip(
                text = helper,
                containerColor = VpnAccentSoft,
                contentColor = VpnAccent,
            )
        }
    }
}

@Composable
fun VpnMetricColumn(
    metrics: List<VpnHeroMetric>,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        metrics.forEach { metric ->
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = metric.label,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextTertiary,
                )
                Text(
                    text = metric.value,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary,
                )
            }
        }
    }
}

@Composable
fun VpnGlassCard(
    modifier: Modifier = Modifier,
    accent: Color = VpnAccent,
    shape: Shape = VpnCardShape,
    contentPadding: PaddingValues = PaddingValues(horizontal = 18.dp, vertical = 18.dp),
    content: @Composable ColumnScope.() -> Unit,
) {
    Surface(
        modifier = modifier,
        shape = shape,
        color = VpnSurface,
        border = androidx.compose.foundation.BorderStroke(
            width = 1.dp,
            color = accent.copy(alpha = 0.16f).takeIf { accent != VpnAccent } ?: VpnOutline,
        ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(contentPadding),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            content = content,
        )
    }
}

@Composable
fun VpnMetricPill(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    valueColor: Color = TextPrimary,
) {
    Surface(
        modifier = modifier.heightIn(min = 68.dp),
        shape = RoundedCornerShape(20.dp),
        color = VpnSurfaceStrong,
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = TextTertiary,
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = valueColor,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
fun VpnSectionHeading(
    title: String,
    subtitle: String = "",
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = TextPrimary,
        )
        if (subtitle.isNotBlank()) {
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
            )
        }
    }
}

@Composable
fun VpnStatusChip(
    text: String,
    modifier: Modifier = Modifier,
    containerColor: Color = VpnSurfaceStrong,
    contentColor: Color = TextPrimary,
) {
    Surface(
        modifier = modifier,
        shape = VpnPillShape,
        color = containerColor,
        border = androidx.compose.foundation.BorderStroke(1.dp, contentColor.copy(alpha = 0.16f)),
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = contentColor,
        )
    }
}

@Composable
fun VpnLabelValueRow(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    valueColor: Color = TextPrimary,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = label,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary,
        )
        Text(
            text = value,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = valueColor,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
fun VpnSearchStrip(
    placeholder: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    trailingIcon: ImageVector? = null,
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(22.dp),
        color = VpnSurface,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null,
                tint = TextTertiary,
            )
            Text(
                text = placeholder,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
            )
            if (trailingIcon != null) {
                Icon(
                    imageVector = trailingIcon,
                    contentDescription = null,
                    tint = TextSecondary,
                )
            }
        }
    }
}

@Composable
fun VpnSearchField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "Search",
    trailingIcon: ImageVector? = null,
    onTrailingClick: (() -> Unit)? = null,
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp)),
        singleLine = true,
        shape = RoundedCornerShape(22.dp),
        placeholder = {
            Text(
                text = placeholder,
                color = TextTertiary,
            )
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null,
                tint = TextTertiary,
            )
        },
        trailingIcon = if (trailingIcon != null && onTrailingClick != null) {
            {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .clickable(onClick = onTrailingClick),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = trailingIcon,
                        contentDescription = null,
                        tint = TextSecondary,
                    )
                }
            }
        } else {
            null
        },
        colors = TextFieldDefaults.colors(
            focusedTextColor = TextPrimary,
            unfocusedTextColor = TextPrimary,
            focusedContainerColor = VpnSurface,
            unfocusedContainerColor = VpnSurface,
            disabledContainerColor = VpnSurface,
            cursorColor = VpnAccent,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            focusedLeadingIconColor = TextSecondary,
            unfocusedLeadingIconColor = TextSecondary,
        ),
    )
}

@Composable
fun VpnWarningStrip(
    text: String,
    modifier: Modifier = Modifier,
    accent: Color = VpnAccent,
    trailingIcon: ImageVector? = Icons.Default.Close,
    onTrailingClick: (() -> Unit)? = null,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        color = VpnSurfaceStrong,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Surface(
                modifier = Modifier.size(18.dp),
                shape = CircleShape,
                color = accent.copy(alpha = 0.18f),
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(accent),
                    )
                }
            }
            Text(
                text = text,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodySmall,
                color = TextPrimary,
            )
            if (trailingIcon != null) {
                Icon(
                    imageVector = trailingIcon,
                    contentDescription = null,
                    tint = TextSecondary,
                    modifier = Modifier.clickable(enabled = onTrailingClick != null) {
                        onTrailingClick?.invoke()
                    },
                )
            }
        }
    }
}

@Composable
fun VpnPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    Button(
        onClick = onClick,
        modifier = modifier.heightIn(min = 52.dp),
        enabled = enabled,
        shape = RoundedCornerShape(26.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (enabled) VpnAccent else VpnDisabledButton,
            contentColor = Color(0xFF041012),
            disabledContainerColor = VpnDisabledButton,
            disabledContentColor = Color(0xFF041012).copy(alpha = 0.6f),
        ),
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
        )
    }
}

@Composable
fun VpnSecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.heightIn(min = 50.dp),
        shape = RoundedCornerShape(26.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, VpnOutline),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = VpnSurfaceStrong,
            contentColor = TextPrimary,
        ),
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Medium,
        )
    }
}

@Composable
fun VpnLightPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    Button(
        onClick = onClick,
        modifier = modifier.heightIn(min = 52.dp),
        enabled = enabled,
        shape = RoundedCornerShape(26.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFFF4F4F2),
            contentColor = Color(0xFF111417),
            disabledContainerColor = Color(0xFFB7BCBF),
            disabledContentColor = Color(0xFF111417).copy(alpha = 0.6f),
        ),
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
        )
    }
}

@Composable
fun VpnGroupRow(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    selected: Boolean = false,
    onClick: (() -> Unit)? = null,
    leading: (@Composable () -> Unit)? = null,
    trailing: @Composable RowScope.() -> Unit = {},
) {
    val rowModifier = if (onClick != null) modifier.clickable(onClick = onClick) else modifier
    Row(
        modifier = rowModifier
            .fillMaxWidth()
            .clip(VpnRowShape)
            .background(if (selected) VpnSurfaceMuted else Color.Transparent)
            .padding(horizontal = 14.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        if (leading != null) {
            leading()
        }
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary,
            )
            if (!subtitle.isNullOrBlank()) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                )
            }
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            content = trailing,
        )
    }
}

@Composable
fun VpnListDivider(modifier: Modifier = Modifier) {
    Divider(
        modifier = modifier.padding(horizontal = 14.dp),
        color = VpnOutline.copy(alpha = 0.75f),
    )
}

@Composable
fun VpnCodeBadge(
    text: String,
    modifier: Modifier = Modifier,
    backgroundColor: Color = VpnAccentSoft,
    contentColor: Color = VpnAccent,
) {
    Surface(
        modifier = modifier.size(42.dp),
        shape = CircleShape,
        color = backgroundColor,
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = contentColor,
            )
        }
    }
}

@Composable
fun VpnRangeSelector(
    labels: List<String>,
    selectedIndex: Int,
    modifier: Modifier = Modifier,
    trailingIcon: ImageVector? = null,
    onTrailingClick: (() -> Unit)? = null,
    onSelect: (Int) -> Unit = {},
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Row(
            modifier = Modifier
                .weight(1f)
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            labels.forEachIndexed { index, label ->
                val selected = index == selectedIndex
                Surface(
                    modifier = Modifier.clickable { onSelect(index) },
                    shape = VpnPillShape,
                    color = if (selected) VpnSurfaceStrong else Color.Transparent,
                ) {
                    Text(
                        text = label,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium,
                        color = if (selected) TextPrimary else TextSecondary,
                    )
                }
            }
        }
        if (trailingIcon != null && onTrailingClick != null) {
            Surface(
                modifier = Modifier
                    .size(38.dp)
                    .clickable(onClick = onTrailingClick),
                shape = CircleShape,
                color = VpnSurfaceStrong,
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = trailingIcon,
                        contentDescription = null,
                        tint = TextPrimary,
                    )
                }
            }
        }
    }
}

@Composable
fun VpnSwapDeck(
    modifier: Modifier = Modifier,
    onSwap: () -> Unit,
    topCard: @Composable ColumnScope.() -> Unit,
    bottomCard: @Composable ColumnScope.() -> Unit,
) {
    Box(modifier = modifier.fillMaxWidth()) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            VpnGlassCard(
                accent = VpnOutline,
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
                content = topCard,
            )
            Spacer(modifier = Modifier.height(14.dp))
            VpnGlassCard(
                accent = VpnOutline,
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
                content = bottomCard,
            )
        }
        Surface(
            modifier = Modifier
                .align(Alignment.Center)
                .size(44.dp)
                .border(2.dp, BackgroundDeepest, CircleShape)
                .clickable(onClick = onSwap),
            shape = CircleShape,
            color = VpnSurfaceMuted,
        ) {
            Box(contentAlignment = Alignment.Center) {
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Box(
                        modifier = Modifier
                            .width(12.dp)
                            .height(2.dp)
                            .background(TextPrimary, RoundedCornerShape(999.dp)),
                    )
                    Box(
                        modifier = Modifier
                            .width(12.dp)
                            .height(2.dp)
                            .background(TextPrimary, RoundedCornerShape(999.dp)),
                    )
                }
            }
        }
    }
}

@Composable
fun VpnModeDock(
    items: List<VpnDockItem>,
    selectedIndex: Int,
    modifier: Modifier = Modifier,
    onSelect: (Int) -> Unit,
    onClose: (() -> Unit)? = null,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding(),
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Surface(
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(30.dp),
            color = VpnSurfaceStrong,
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                items.forEachIndexed { index, item ->
                    val selected = index == selectedIndex
                    Column(
                        modifier = Modifier.clickable { onSelect(index) },
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        Box {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = null,
                                tint = if (selected) TextPrimary else TextTertiary,
                            )
                            if (selected) {
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(VpnAccent),
                                )
                            }
                        }
                        Text(
                            text = item.label,
                            style = MaterialTheme.typography.bodySmall,
                            color = if (selected) TextPrimary else TextTertiary,
                        )
                    }
                }
            }
        }
        if (onClose != null) {
            Surface(
                modifier = Modifier
                    .size(56.dp)
                    .clickable(onClick = onClose),
                shape = CircleShape,
                color = VpnSurfaceStrong,
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = null,
                        tint = TextPrimary,
                    )
                }
            }
        }
    }
}

@Composable
fun VpnBottomSheet(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = VpnSheetShape,
        color = VpnSurface,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Box(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .width(44.dp)
                    .height(4.dp)
                    .clip(VpnPillShape)
                    .background(TextTertiary.copy(alpha = 0.65f)),
            )
            content()
        }
    }
}

@Composable
fun VpnCandleChart(
    entries: List<VpnChartCandle>,
    modifier: Modifier = Modifier,
    calloutLines: List<Pair<String, String>> = emptyList(),
    rightLabels: List<String> = emptyList(),
    bottomLabels: List<String> = emptyList(),
) {
    if (entries.isEmpty()) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .height(240.dp)
                .background(VpnSurfaceStrong, RoundedCornerShape(24.dp)),
        )
        return
    }

    val max = entries.maxOf { it.high }
    val min = entries.minOf { it.low }
    val range = (max - min).takeIf { it > 0f } ?: 1f
    val crosshairIndex = (entries.size * 0.62f).toInt().coerceIn(entries.indices)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(268.dp),
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val chartTop = size.height * 0.06f
            val chartBottom = size.height * 0.86f
            val chartHeight = chartBottom - chartTop
            val candleWidth = size.width / (entries.size * 2.3f)
            val spacing = size.width / (entries.size + 1)

            repeat(4) { index ->
                val y = chartTop + chartHeight * (index / 3f)
                drawLine(
                    color = VpnOutline.copy(alpha = 0.9f),
                    start = Offset(0f, y),
                    end = Offset(size.width, y),
                    strokeWidth = 1f,
                )
            }
            repeat(5) { index ->
                val x = size.width * (index / 4f)
                drawLine(
                    color = VpnOutline.copy(alpha = 0.55f),
                    start = Offset(x, chartTop),
                    end = Offset(x, chartBottom),
                    strokeWidth = 1f,
                )
            }

            fun toY(value: Float): Float = chartTop + ((max - value) / range) * chartHeight

            entries.forEachIndexed { index, candle ->
                val x = spacing * (index + 1)
                val openY = toY(candle.open)
                val closeY = toY(candle.close)
                val highY = toY(candle.high)
                val lowY = toY(candle.low)
                val positive = candle.close >= candle.open
                val color = if (positive) VpnPositive else VpnNegative

                drawLine(
                    color = color,
                    start = Offset(x, highY),
                    end = Offset(x, lowY),
                    strokeWidth = 3f,
                    cap = StrokeCap.Round,
                )

                val bodyTop = minOf(openY, closeY)
                val bodyHeight = (kotlin.math.abs(openY - closeY)).coerceAtLeast(10f)
                drawRoundRect(
                    color = color,
                    topLeft = Offset(x - candleWidth / 2f, bodyTop),
                    size = Size(candleWidth, bodyHeight),
                    cornerRadius = CornerRadius(4f, 4f),
                    style = Fill,
                )
            }

            val crosshairX = spacing * (crosshairIndex + 1)
            val crosshairY = toY(entries[crosshairIndex].close)
            drawLine(
                color = TextTertiary.copy(alpha = 0.8f),
                start = Offset(crosshairX, chartTop),
                end = Offset(crosshairX, chartBottom),
                strokeWidth = 1f,
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f),
            )
            drawLine(
                color = TextTertiary.copy(alpha = 0.8f),
                start = Offset(0f, crosshairY),
                end = Offset(size.width, crosshairY),
                strokeWidth = 1f,
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f),
            )
        }

        if (calloutLines.isNotEmpty()) {
            Surface(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 18.dp),
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFF3A4245),
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    calloutLines.forEach { (label, value) ->
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Text(
                                text = label,
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary,
                            )
                            Text(
                                text = value,
                                style = MaterialTheme.typography.bodySmall,
                                color = TextPrimary,
                            )
                        }
                    }
                }
            }
        }

        if (rightLabels.isNotEmpty()) {
            Column(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 18.dp),
                verticalArrangement = Arrangement.SpaceBetween,
            ) {
                rightLabels.forEach { label ->
                    Text(
                        text = label,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextTertiary,
                    )
                    Spacer(modifier = Modifier.height(44.dp))
                }
            }
        }

        if (bottomLabels.isNotEmpty()) {
            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(bottom = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                bottomLabels.forEach { label ->
                    Text(
                        text = label,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextTertiary,
                    )
                }
            }
        }
    }
}

@Composable
fun VpnLineChart(
    values: List<Float>,
    modifier: Modifier = Modifier,
    accent: Color = VpnAccent,
) {
    if (values.isEmpty()) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .height(144.dp)
                .background(VpnSurfaceStrong, RoundedCornerShape(24.dp)),
        )
        return
    }

    val max = values.maxOrNull() ?: 1f
    val min = values.minOrNull() ?: 0f
    val range = (max - min).takeIf { it > 0f } ?: 1f

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(144.dp),
    ) {
        repeat(4) { index ->
            val y = size.height * (index / 3f)
            drawLine(
                color = VpnOutline.copy(alpha = 0.8f),
                start = Offset(0f, y),
                end = Offset(size.width, y),
                strokeWidth = 1f,
            )
        }

        val path = Path()
        values.forEachIndexed { index, value ->
            val x = if (values.size == 1) 0f else size.width * index / (values.lastIndex.toFloat())
            val y = ((max - value) / range) * size.height
            if (index == 0) {
                path.moveTo(x, y)
            } else {
                path.lineTo(x, y)
            }
        }

        drawPath(
            path = path,
            brush = Brush.horizontalGradient(
                colors = listOf(accent.copy(alpha = 0.72f), accent),
            ),
            style = Stroke(width = 6f, cap = StrokeCap.Round),
        )
    }
}

@Composable
fun VpnLoadingPanel(
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
) {
    VpnGlassCard(
        modifier = modifier,
        accent = VpnOutline,
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(28.dp),
            color = VpnAccent,
            strokeWidth = 2.5.dp,
        )
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = TextPrimary,
        )
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary,
        )
    }
}

@Composable
fun VpnEmptyPanel(
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
    actionText: String? = null,
    onAction: (() -> Unit)? = null,
) {
    VpnGlassCard(
        modifier = modifier,
        accent = VpnOutline,
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = TextPrimary,
        )
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary,
        )
        if (!actionText.isNullOrBlank() && onAction != null) {
            VpnPrimaryButton(
                text = actionText,
                onClick = onAction,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

internal fun vpnDemoCandles(seed: Float): List<VpnChartCandle> {
    val base = listOf(
        84f, 78f, 82f, 76f, 79f, 72f, 75f, 69f, 73f, 70f,
        77f, 74f, 79f, 71f, 81f, 78f, 83f, 76f, 86f, 82f,
    )
    return base.mapIndexed { index, value ->
        val drift = (seed * 0.03f) + (index % 4) * 1.2f
        val open = value + drift
        val close = open + if (index % 3 == 0) -5.5f else 4.5f
        val high = maxOf(open, close) + 3.2f
        val low = minOf(open, close) - 3.8f
        VpnChartCandle(
            open = open,
            close = close,
            high = high,
            low = low,
        )
    }
}

internal fun vpnDemoLine(seed: Float): List<Float> {
    return listOf(
        0.18f, 0.22f, 0.24f, 0.24f, 0.23f, 0.19f, 0.16f, 0.21f,
        0.25f, 0.28f, 0.31f, 0.33f, 0.36f,
    ).map { it + seed * 0.01f }
}

internal fun String.takeTrailing(count: Int): String {
    return if (length <= count) this else takeLast(count)
}

internal fun statusAccent(status: String): Color {
    return when (status.lowercase()) {
        "success",
        "paid",
        "completed",
        "connected",
        -> VpnAccent

        "pending",
        "confirming",
        "connecting",
        -> Color(0xFFFFB14A)

        else -> Error
    }
}
