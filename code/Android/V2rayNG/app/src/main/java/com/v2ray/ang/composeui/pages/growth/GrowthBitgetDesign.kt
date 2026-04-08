package com.v2ray.ang.composeui.pages.growth

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

internal val GrowthPageBackground = Color(0xFF0D1417)
internal val GrowthPageBackgroundDeep = Color(0xFF091014)
internal val GrowthSurface = Color(0xFF151F24)
internal val GrowthSurfaceRaised = Color(0xFF1B2830)
internal val GrowthSurfaceStrong = Color(0xFF223540)
internal val GrowthAccent = Color(0xFF15D7E7)
internal val GrowthAccentSoft = GrowthAccent.copy(alpha = 0.16f)
internal val GrowthTextPrimary = Color(0xFFF2F7FA)
internal val GrowthTextSecondary = Color(0xFF96A7B4)
internal val GrowthTextTertiary = Color(0xFF61727E)
internal val GrowthPositive = Color(0xFF53E4B3)
internal val GrowthNegative = Color(0xFFFF6B87)
internal val GrowthWarningSurface = Color(0xFF3A2B17)
internal val GrowthWarningText = Color(0xFFFFC978)
internal val GrowthBorder = Color(0xFF2C3C46)
internal val GrowthHeroGradient = Brush.linearGradient(
    colors = listOf(Color(0xFF10262E), Color(0xFF123746), Color(0xFF0F1C22)),
)

private val GrowthCardShape = RoundedCornerShape(28.dp)

@Composable
internal fun GrowthBitgetBackground(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        GrowthPageBackgroundDeep,
                        GrowthPageBackground,
                        GrowthPageBackground,
                    ),
                ),
            ),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(GrowthAccent.copy(alpha = 0.12f), Color.Transparent),
                        radius = 920f,
                    ),
                ),
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(Color(0xFF5EE9FF).copy(alpha = 0.08f), Color.Transparent),
                        radius = 1280f,
                    ),
                ),
        )
        content()
    }
}

@Composable
internal fun GrowthPageScaffold(
    modifier: Modifier = Modifier,
    topBar: @Composable () -> Unit,
    bottomBar: @Composable () -> Unit = {},
    content: @Composable (PaddingValues) -> Unit,
) {
    GrowthBitgetBackground(modifier = modifier) {
        Scaffold(
            containerColor = Color.Transparent,
            contentColor = GrowthTextPrimary,
            topBar = topBar,
            bottomBar = bottomBar,
        ) { paddingValues ->
            content(paddingValues)
        }
    }
}

@Composable
internal fun GrowthTopBar(
    title: String? = null,
    subtitle: String? = null,
    onNavigateBack: () -> Unit,
    trailingContent: @Composable (() -> Unit)? = null,
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
            modifier = Modifier.size(40.dp),
            shape = CircleShape,
            color = GrowthSurface.copy(alpha = 0.94f),
            border = BorderStroke(1.dp, GrowthBorder),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable(
                        interactionSource = MutableInteractionSource(),
                        indication = null,
                        onClick = onNavigateBack,
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = GrowthTextPrimary,
                )
            }
        }

        Column(modifier = Modifier.weight(1f)) {
            if (title != null) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = GrowthTextPrimary,
                )
            }
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    color = GrowthTextSecondary,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }

        trailingContent?.invoke()
    }
}

@Composable
internal fun GrowthTopActionPill(
    label: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(999.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(999.dp),
        color = GrowthSurface,
        border = BorderStroke(1.dp, GrowthBorder),
    ) {
        Text(
            text = label,
            color = GrowthTextPrimary,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
        )
    }
}

@Composable
internal fun GrowthSectionCard(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(20.dp),
    content: @Composable ColumnScope.() -> Unit,
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, GrowthBorder, GrowthCardShape),
        shape = GrowthCardShape,
        colors = CardDefaults.cardColors(containerColor = GrowthSurface),
    ) {
        Column(
            modifier = Modifier.padding(contentPadding),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            content = content,
        )
    }
}

@Composable
internal fun GrowthHighlightCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(GrowthCardShape)
            .background(GrowthHeroGradient)
            .border(1.dp, GrowthBorder, GrowthCardShape),
    ) {
        Column(
            modifier = Modifier.padding(22.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            content = content,
        )
    }
}

@Composable
internal fun GrowthSectionTitle(
    title: String,
    subtitle: String? = null,
    trailing: @Composable (() -> Unit)? = null,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                color = GrowthTextPrimary,
                fontWeight = FontWeight.SemiBold,
                fontSize = 18.sp,
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    color = GrowthTextSecondary,
                    fontSize = 13.sp,
                    lineHeight = 19.sp,
                    modifier = Modifier.padding(top = 6.dp),
                )
            }
        }
        trailing?.invoke()
    }
}

@Composable
internal fun GrowthBadge(
    text: String,
    modifier: Modifier = Modifier,
    containerColor: Color = GrowthAccentSoft,
    contentColor: Color = GrowthAccent,
) {
    Surface(
        modifier = modifier,
        color = containerColor,
        shape = RoundedCornerShape(999.dp),
    ) {
        Text(
            text = text,
            color = contentColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
        )
    }
}

@Composable
internal fun GrowthMetricBlock(
    value: String,
    label: String,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Text(
            text = value,
            color = GrowthTextPrimary,
            fontWeight = FontWeight.Bold,
            fontSize = 26.sp,
        )
        Text(
            text = label,
            color = GrowthTextSecondary,
            fontSize = 12.sp,
            modifier = Modifier.padding(top = 4.dp),
        )
    }
}

@Composable
internal fun GrowthStatChip(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    accentColor: Color = GrowthAccent,
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .background(GrowthSurfaceStrong)
            .padding(horizontal = 14.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(5.dp),
    ) {
        Text(
            text = label,
            color = GrowthTextSecondary,
            fontSize = 12.sp,
        )
        Text(
            text = value,
            color = GrowthTextPrimary,
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
        )
        Box(
            modifier = Modifier
                .size(width = 22.dp, height = 2.dp)
                .clip(RoundedCornerShape(999.dp))
                .background(accentColor.copy(alpha = 0.9f)),
        )
    }
}

@Composable
internal fun GrowthInfoRow(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    emphasize: Boolean = false,
    valueColor: Color = if (emphasize) GrowthTextPrimary else GrowthTextSecondary,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            color = GrowthTextSecondary,
            fontSize = 13.sp,
        )
        Text(
            text = value,
            color = valueColor,
            fontSize = 13.sp,
            fontWeight = if (emphasize) FontWeight.SemiBold else FontWeight.Normal,
            textAlign = TextAlign.End,
        )
    }
}

@Composable
internal fun GrowthListDivider() {
    HorizontalDivider(color = GrowthBorder.copy(alpha = 0.9f))
}

@Composable
internal fun GrowthBulletItem(
    text: String,
    modifier: Modifier = Modifier,
    bulletColor: Color = GrowthAccent,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Box(
            modifier = Modifier
                .padding(top = 7.dp)
                .size(6.dp)
                .clip(CircleShape)
                .background(bulletColor),
        )
        Text(
            text = text,
            color = GrowthTextSecondary,
            fontSize = 13.sp,
            lineHeight = 19.sp,
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
internal fun GrowthPrimaryButton(
    text: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onClick: () -> Unit,
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(54.dp),
        enabled = enabled,
        shape = RoundedCornerShape(24.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = GrowthAccent,
            contentColor = GrowthPageBackgroundDeep,
            disabledContainerColor = GrowthAccent.copy(alpha = 0.36f),
            disabledContentColor = GrowthPageBackgroundDeep.copy(alpha = 0.7f),
        ),
    ) {
        Text(text = text, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
    }
}

@Composable
internal fun GrowthSecondaryButton(
    text: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(54.dp),
        shape = RoundedCornerShape(24.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = GrowthSurfaceStrong,
            contentColor = GrowthTextPrimary,
        ),
    ) {
        Text(text = text, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
    }
}

@Composable
internal fun GrowthStatusView(
    title: String,
    message: String,
    modifier: Modifier = Modifier,
    action: @Composable (() -> Unit)? = null,
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        GrowthSectionCard(
            modifier = Modifier.padding(horizontal = 20.dp),
            contentPadding = PaddingValues(horizontal = 22.dp, vertical = 24.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(68.dp)
                    .clip(CircleShape)
                    .background(GrowthSurfaceStrong),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = title.take(1),
                    color = GrowthAccent,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
            Text(
                text = title,
                color = GrowthTextPrimary,
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = message,
                color = GrowthTextSecondary,
                fontSize = 14.sp,
                lineHeight = 20.sp,
            )
            action?.invoke()
        }
    }
}
