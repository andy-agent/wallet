package com.v2ray.ang.composeui.effects

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import com.v2ray.ang.composeui.theme.Info

@Composable
fun NetworkParticles(
    modifier: Modifier = Modifier,
    color: Color = Info,
) {
    Canvas(modifier = modifier) {
        val center = Offset(size.width / 2, size.height / 2)
        drawCircle(color = color, radius = size.minDimension * 0.04f, center = center)
        drawLine(color = color, start = Offset.Zero, end = center)
    }
}
