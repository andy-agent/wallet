package com.v2ray.ang.composeui.effects

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.v2ray.ang.composeui.theme.Warning

@Composable
fun EnergyParticles(
    modifier: Modifier = Modifier,
    color: Color = Warning,
) {
    Canvas(modifier = modifier) {
        drawCircle(color = color, radius = size.minDimension * 0.06f)
    }
}
