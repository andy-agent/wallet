package com.v2ray.ang.composeui.pages.vpn

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.v2ray.ang.composeui.theme.BackgroundDeepest
import com.v2ray.ang.composeui.theme.BackgroundOverlay
import com.v2ray.ang.composeui.theme.BackgroundPrimary
import com.v2ray.ang.composeui.theme.BackgroundSecondary
import com.v2ray.ang.composeui.theme.BorderDefault
import com.v2ray.ang.composeui.theme.GlowBlue
import com.v2ray.ang.composeui.theme.GlowGreen
import com.v2ray.ang.composeui.theme.Info
import com.v2ray.ang.composeui.theme.Primary
import com.v2ray.ang.composeui.theme.TextPrimary
import com.v2ray.ang.composeui.theme.TextSecondary
import com.v2ray.ang.composeui.theme.TextTertiary

internal val VpnPageHorizontalPadding = 20.dp
internal val VpnPageTopPadding = 16.dp
internal val VpnPageBottomPadding = 120.dp
private val VpnCardShape = RoundedCornerShape(28.dp)
private val VpnPillShape = RoundedCornerShape(999.dp)

data class VpnHeroMetric(
    val label: String,
    val value: String,
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
                        BackgroundDeepest,
                        BackgroundPrimary,
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
                        colors = listOf(GlowBlue.copy(alpha = 0.15f), Color.Transparent),
                        radius = 1200f,
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
            Surface(
                modifier = Modifier
                    .size(42.dp)
                    .clickable(onClick = onBack),
                shape = CircleShape,
                color = BackgroundSecondary.copy(alpha = 0.96f),
                border = BorderStroke(1.dp, BorderDefault.copy(alpha = 0.85f)),
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = TextPrimary,
                    )
                }
            }
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
        }

        if (actionIcon != null && onActionClick != null) {
            Surface(
                modifier = Modifier
                    .size(42.dp)
                    .clickable(onClick = onActionClick),
                shape = CircleShape,
                color = BackgroundSecondary.copy(alpha = 0.96f),
                border = BorderStroke(1.dp, BorderDefault.copy(alpha = 0.85f)),
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = actionIcon,
                        contentDescription = null,
                        tint = TextPrimary,
                    )
                }
            }
        }
    }
}

@Composable
fun VpnGlassCard(
    modifier: Modifier = Modifier,
    accent: Color = GlowBlue,
    shape: Shape = VpnCardShape,
    content: @Composable ColumnScope.() -> Unit,
) {
    Box(
        modifier = modifier
            .clip(shape)
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        BackgroundSecondary.copy(alpha = 0.98f),
                        BackgroundOverlay.copy(alpha = 0.98f),
                    ),
                ),
            )
            .border(1.dp, BorderDefault.copy(alpha = 0.95f), shape),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(accent.copy(alpha = 0.18f), Color.Transparent),
                        radius = 760f,
                    ),
                ),
        )
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            content = content,
        )
    }
}

@Composable
fun VpnHeroCard(
    eyebrow: String,
    title: String,
    subtitle: String,
    metrics: List<VpnHeroMetric>,
    modifier: Modifier = Modifier,
    accent: Color = Primary,
) {
    VpnGlassCard(
        modifier = modifier,
        accent = accent,
    ) {
        VpnStatusChip(
            text = eyebrow,
            containerColor = accent.copy(alpha = 0.16f),
            contentColor = accent,
        )
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
        )
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary,
        )
        if (metrics.isNotEmpty()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                metrics.take(3).forEach { metric ->
                    VpnMetricPill(
                        modifier = Modifier.weight(1f),
                        label = metric.label,
                        value = metric.value,
                    )
                }
            }
        }
    }
}

@Composable
fun VpnMetricPill(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.heightIn(min = 68.dp),
        shape = RoundedCornerShape(22.dp),
        color = BackgroundPrimary.copy(alpha = 0.82f),
        border = BorderStroke(1.dp, BorderDefault.copy(alpha = 0.72f)),
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = label.uppercase(),
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextTertiary,
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
fun VpnSectionHeading(
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
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
fun VpnStatusChip(
    text: String,
    modifier: Modifier = Modifier,
    containerColor: Color = Primary.copy(alpha = 0.14f),
    contentColor: Color = Primary,
) {
    Surface(
        modifier = modifier,
        shape = VpnPillShape,
        color = containerColor,
        border = BorderStroke(1.dp, contentColor.copy(alpha = 0.28f)),
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
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
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary,
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = valueColor,
            maxLines = 1,
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
        shape = RoundedCornerShape(24.dp),
        color = BackgroundSecondary.copy(alpha = 0.96f),
        border = BorderStroke(1.dp, BorderDefault.copy(alpha = 0.85f)),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
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
                    tint = Info,
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
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.fillMaxWidth(),
        singleLine = true,
        shape = RoundedCornerShape(24.dp),
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
                Surface(
                    modifier = Modifier
                        .size(32.dp)
                        .clickable(onClick = onTrailingClick),
                    shape = CircleShape,
                    color = BackgroundPrimary,
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = trailingIcon,
                            contentDescription = null,
                            tint = TextSecondary,
                        )
                    }
                }
            }
        } else {
            null
        },
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = TextPrimary,
            unfocusedTextColor = TextPrimary,
            focusedContainerColor = BackgroundSecondary.copy(alpha = 0.95f),
            unfocusedContainerColor = BackgroundSecondary.copy(alpha = 0.95f),
            focusedBorderColor = Primary.copy(alpha = 0.6f),
            unfocusedBorderColor = BorderDefault.copy(alpha = 0.82f),
            cursorColor = Primary,
        ),
    )
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
        shape = RoundedCornerShape(22.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Primary,
            contentColor = BackgroundDeepest,
            disabledContainerColor = Primary.copy(alpha = 0.32f),
            disabledContentColor = BackgroundDeepest.copy(alpha = 0.6f),
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
        shape = RoundedCornerShape(22.dp),
        border = BorderStroke(1.dp, BorderDefault.copy(alpha = 0.9f)),
        colors = ButtonDefaults.outlinedButtonColors(
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
fun VpnLoadingPanel(
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
) {
    VpnGlassCard(
        modifier = modifier.fillMaxWidth(),
        accent = GlowBlue,
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
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
        modifier = modifier.fillMaxWidth(),
        accent = GlowBlue,
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
            color = TextPrimary,
        )
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary,
        )
        if (actionText != null && onAction != null) {
            VpnPrimaryButton(
                text = actionText,
                onClick = onAction,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}
