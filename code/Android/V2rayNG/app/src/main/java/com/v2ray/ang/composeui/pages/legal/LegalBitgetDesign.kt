package com.v2ray.ang.composeui.pages.legal

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

internal val LegalPageBackground = Color(0xFF0D1417)
internal val LegalCardBackground = Color(0xFF162027)
internal val LegalCardRaised = Color(0xFF1B2830)
internal val LegalAccent = Color(0xFF16D7E8)
internal val LegalAccentDeep = Color(0xFF08B5C6)
internal val LegalTextPrimary = Color(0xFFF2F7FA)
internal val LegalTextSecondary = Color(0xFF95A6B3)
internal val LegalTextTertiary = Color(0xFF62737F)
internal val LegalBorder = Color(0xFF2D3C46)

private val LegalCardShape = RoundedCornerShape(28.dp)

@Composable
internal fun LegalBitgetBackground(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF091014), LegalPageBackground, LegalPageBackground),
                ),
            ),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(LegalAccent.copy(alpha = 0.12f), Color.Transparent),
                        radius = 980f,
                    ),
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
            modifier = Modifier.size(40.dp),
            shape = CircleShape,
            color = LegalCardBackground,
            border = BorderStroke(1.dp, LegalBorder),
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

        Column(modifier = Modifier.weight(1f)) {
            if (title != null) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = LegalTextPrimary,
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
    }
}

@Composable
internal fun LegalCard(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(20.dp),
    content: @Composable ColumnScope.() -> Unit,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = LegalCardShape,
        colors = CardDefaults.cardColors(containerColor = LegalCardBackground),
    ) {
        Column(
            modifier = Modifier.padding(contentPadding),
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
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(LegalCardShape)
            .background(
                Brush.linearGradient(
                    colors = listOf(Color(0xFF0F2430), Color(0xFF112A34), Color(0xFF111A20)),
                ),
            )
            .background(Color.Transparent)
            .padding(22.dp),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(14.dp), content = content)
    }
}

@Composable
internal fun LegalBadge(
    text: String,
    modifier: Modifier = Modifier,
    containerColor: Color = LegalAccent.copy(alpha = 0.15f),
    contentColor: Color = LegalAccent,
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(999.dp),
        color = containerColor,
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
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                color = LegalTextPrimary,
                fontWeight = FontWeight.SemiBold,
                fontSize = 18.sp,
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    color = LegalTextSecondary,
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
internal fun LegalListDivider() {
    HorizontalDivider(color = LegalBorder)
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
        LegalCard(modifier = Modifier.padding(horizontal = 20.dp)) {
            Box(
                modifier = Modifier
                    .size(68.dp)
                    .clip(CircleShape)
                    .background(LegalCardRaised),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = title.take(1),
                    color = LegalAccent,
                    fontWeight = FontWeight.Bold,
                    fontSize = 28.sp,
                )
            }
            Text(
                text = title,
                color = LegalTextPrimary,
                fontWeight = FontWeight.SemiBold,
                fontSize = 20.sp,
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
