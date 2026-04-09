package com.v2ray.ang.composeui.pages.profile

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
import com.v2ray.ang.composeui.theme.BackgroundOverlay
import com.v2ray.ang.composeui.theme.BackgroundPrimary
import com.v2ray.ang.composeui.theme.BackgroundSecondary
import com.v2ray.ang.composeui.theme.BackgroundTertiary
import com.v2ray.ang.composeui.theme.BorderDefault
import com.v2ray.ang.composeui.theme.Error
import com.v2ray.ang.composeui.theme.Primary
import com.v2ray.ang.composeui.theme.PrimaryHover
import com.v2ray.ang.composeui.theme.TextPrimary
import com.v2ray.ang.composeui.theme.TextSecondary
import com.v2ray.ang.composeui.theme.TextTertiary
import com.v2ray.ang.composeui.theme.Warning

internal val ProfilePageBackground = BackgroundPrimary
internal val ProfileSurface = BackgroundSecondary
internal val ProfileSurfaceRaised = BackgroundTertiary
internal val ProfileAccent = Primary
internal val ProfileAccentDeep = PrimaryHover
internal val ProfileTextPrimary = TextPrimary
internal val ProfileTextSecondary = TextSecondary
internal val ProfileTextTertiary = TextTertiary
internal val ProfileDanger = Error
internal val ProfileWarningSurface = Warning.copy(alpha = 0.14f)
internal val ProfileWarningText = Warning
internal val ProfileDivider = BorderDefault

private val ProfileCardShape = RoundedCornerShape(28.dp)

@Composable
internal fun ProfileBitgetBackground(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(BackgroundOverlay, BackgroundSecondary, ProfilePageBackground),
                ),
            ),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(ProfileAccent.copy(alpha = 0.05f), Color.Transparent),
                        radius = 980f,
                    ),
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
                modifier = Modifier.size(40.dp),
                shape = CircleShape,
                color = ProfileSurface,
                border = BorderStroke(1.dp, ProfileDivider),
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

        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
            color = ProfileTextPrimary,
            modifier = Modifier.weight(1f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )

        SpacerSlot()
    }
}

@Composable
private fun SpacerSlot() {
    Box(modifier = Modifier.size(40.dp))
}

@Composable
internal fun ProfileCard(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(20.dp),
    content: @Composable ColumnScope.() -> Unit,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = ProfileCardShape,
        colors = CardDefaults.cardColors(containerColor = ProfileSurface),
    ) {
        Column(
            modifier = Modifier.padding(contentPadding),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            content = content,
        )
    }
}

@Composable
internal fun ProfileBadge(
    text: String,
    modifier: Modifier = Modifier,
    containerColor: Color = ProfileAccent.copy(alpha = 0.15f),
    contentColor: Color = ProfileAccent,
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
internal fun ProfileListDivider() {
    HorizontalDivider(color = ProfileDivider)
}
