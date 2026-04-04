package com.v2ray.ang.composeui.effects

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import com.v2ray.ang.composeui.theme.TextPrimary

@Composable
fun StarfieldParticles(
    modifier: Modifier = Modifier,
    color: Color = TextPrimary,
) {
    Canvas(modifier = modifier) {
        drawCircle(color = color, radius = size.minDimension * 0.02f, center = Offset(size.width * 0.3f, size.height * 0.4f))
        drawCircle(color = color, radius = size.minDimension * 0.015f, center = Offset(size.width * 0.7f, size.height * 0.6f))
    }
}
