package com.app.common.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.dp
import com.app.core.theme.AppDimens
import com.app.core.theme.BorderSubtle
import com.app.core.theme.CardGlass
import com.app.core.theme.CardGlassStrong
import com.app.core.theme.GlowBlue
import com.app.core.theme.GlowCyan
import com.app.core.theme.TextSecondary

@Composable
fun GradientCard(
    modifier: Modifier = Modifier,
    title: String? = null,
    subtitle: String? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, BorderSubtle, RoundedCornerShape(AppDimens.cardRadius))
            .clip(RoundedCornerShape(AppDimens.cardRadius)),
        color = CardGlassStrong,
        shadowElevation = 14.dp,
        shape = RoundedCornerShape(AppDimens.cardRadius),
    ) {
        Box(
            modifier = Modifier.background(
                Brush.verticalGradient(
                    listOf(CardGlassStrong, CardGlass, Color.White),
                ),
            ),
        ) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(
                        Brush.radialGradient(
                            colors = listOf(GlowCyan, Color.Transparent),
                            center = Offset(920f, 80f),
                            radius = 360f,
                        ),
                    ),
            )
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(
                        Brush.radialGradient(
                            colors = listOf(GlowBlue, Color.Transparent),
                            center = Offset(80f, 40f),
                            radius = 320f,
                        ),
                    ),
            )
            Column(
                modifier = Modifier.padding(AppDimens.cardPadding),
            ) {
                if (title != null) {
                    Text(title, style = MaterialTheme.typography.titleMedium)
                }
                if (subtitle != null) {
                    Spacer(Modifier.height(4.dp))
                    Text(subtitle, style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                    Spacer(Modifier.height(12.dp))
                }
                content()
            }
        }
    }
}
