package com.v2ray.ang.composeui.effects

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.v2ray.ang.composeui.theme.GlowBlue

@Composable
fun SpecialGlowOverlay(modifier: Modifier = Modifier) {
    Box(modifier = modifier.background(GlowBlue))
}
