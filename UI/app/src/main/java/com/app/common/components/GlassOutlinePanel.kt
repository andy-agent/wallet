package com.app.common.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.app.core.theme.AppDimens
import com.app.core.theme.BorderSubtle
import com.app.core.theme.CardGlass
import com.app.core.theme.CardGlassStrong
import com.app.core.theme.GlowBlue
import com.app.core.theme.GlowCyan

@Composable
fun GlassOutlinePanel(
    modifier: Modifier = Modifier,
    radius: Dp = AppDimens.cardRadius,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    content: @Composable BoxScope.() -> Unit,
) {
    val shape = RoundedCornerShape(radius)
    Surface(
        modifier = modifier,
        shape = shape,
        color = Color.Transparent,
        shadowElevation = 14.dp,
    ) {
        Box(
            modifier = Modifier
                .clip(shape)
                .background(
                    brush = Brush.verticalGradient(
                        listOf(
                            CardGlassStrong.copy(alpha = 0.98f),
                            CardGlass.copy(alpha = 0.95f),
                            Color.White.copy(alpha = 0.9f),
                        ),
                    ),
                )
                .border(1.dp, BorderSubtle.copy(alpha = 0.9f), shape)
                .drawWithCache {
                    val cyanGlow = Brush.radialGradient(
                        colors = listOf(GlowCyan.copy(alpha = 0.42f), Color.Transparent),
                        center = Offset(size.width * 0.82f, size.height * 0.16f),
                        radius = size.maxDimension * 0.92f,
                    )
                    val blueGlow = Brush.radialGradient(
                        colors = listOf(GlowBlue.copy(alpha = 0.34f), Color.Transparent),
                        center = Offset(size.width * 0.08f, size.height * 0.08f),
                        radius = size.maxDimension * 0.76f,
                    )
                    onDrawWithContent {
                        drawRect(brush = cyanGlow)
                        drawRect(brush = blueGlow)
                        drawContent()
                    }
                }
                .padding(contentPadding),
        ) {
            content()
        }
    }
}
