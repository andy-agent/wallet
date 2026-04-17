package com.app.core.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.app.core.theme.AppDimens
import com.app.core.theme.AppWhite
import com.app.core.theme.CloudBackground
import com.app.core.theme.GlowBlue
import com.app.core.theme.GlowCyan
import com.app.core.theme.GlowMint
import com.app.core.theme.GridLine
import com.app.core.theme.SkyBackground
import com.app.core.ui.effects.EffectToggle
import com.app.core.ui.effects.ProductionMotionProfile
import com.app.core.ui.effects.TechMotionBackground

@Composable
fun AppScaffold(
    title: String,
    onBack: (() -> Unit)? = null,
    showTopBar: Boolean = true,
    useProductionMotion: Boolean = true,
    actions: @Composable RowScope.() -> Unit = {},
    content: @Composable (PaddingValues) -> Unit,
) {
    val density = LocalDensity.current
    val topInset = with(density) { WindowInsets.safeDrawing.getTop(this).toDp() }
    val topPadding = if (showTopBar) {
        topInset + AppDimens.topBarHeight + 18.dp
    } else {
        topInset + 12.dp
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(listOf(SkyBackground, CloudBackground)),
                shape = RoundedCornerShape(0.dp),
            ),
    ) {
        if (useProductionMotion) {
            ProductionMotionBackdrop()
        } else {
            StaticBackdrop()
        }

        Box(modifier = Modifier.fillMaxSize()) {
            content(PaddingValues(top = topPadding))
        }

        if (showTopBar) {
            AppTopBar(
                title = title,
                onBack = onBack,
                actions = actions,
            )
        } else if (useProductionMotion) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .align(Alignment.TopEnd),
            ) {
                FloatingHeaderRing(modifier = Modifier.align(Alignment.TopEnd))
            }
        }
    }
}

@Composable
private fun ProductionMotionBackdrop() {
    TechMotionBackground(
        particleCount = ProductionMotionProfile.particleCount,
        orbitDurationMs = ProductionMotionProfile.orbitDurationMs,
        showParticles = ProductionMotionProfile.isEnabled(EffectToggle.ParticleDrift),
        showNetwork = ProductionMotionProfile.isEnabled(EffectToggle.ParticleLinks),
        showGridScan = ProductionMotionProfile.isEnabled(EffectToggle.GridScan),
        showOrb = ProductionMotionProfile.isEnabled(EffectToggle.EnergyOrb),
        showOrbitalRings = ProductionMotionProfile.isEnabled(EffectToggle.OrbitalRings),
        showScanBeam = ProductionMotionProfile.isEnabled(EffectToggle.ScanBeam),
        showDataRain = ProductionMotionProfile.isEnabled(EffectToggle.DataRain),
        showCornerBeacons = ProductionMotionProfile.isEnabled(EffectToggle.CornerBeacons),
    )
}

@Composable
private fun StaticBackdrop() {
    Box(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .blur(58.dp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(GlowCyan, Color.Transparent),
                        center = Offset(280f, 180f),
                        radius = 580f,
                    ),
                ),
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .blur(62.dp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(GlowBlue, Color.Transparent),
                        center = Offset(880f, 280f),
                        radius = 620f,
                    ),
                ),
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .blur(68.dp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(GlowMint, Color.Transparent),
                        center = Offset(520f, 1180f),
                        radius = 680f,
                    ),
                ),
        )
        Canvas(modifier = Modifier.fillMaxSize()) {
            val step = 36.dp.toPx()
            var x = 0f
            while (x <= size.width) {
                drawLine(
                    color = GridLine,
                    start = Offset(x, 0f),
                    end = Offset(x, size.height),
                    strokeWidth = 1f,
                )
                x += step
            }
            var y = 0f
            while (y <= size.height) {
                drawLine(
                    color = GridLine,
                    start = Offset(0f, y),
                    end = Offset(size.width, y),
                    strokeWidth = 1f,
                )
                y += step
            }
        }
        Box(
            modifier = Modifier
                .padding(top = 42.dp, end = 22.dp)
                .size(74.dp)
                .blur(2.dp)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(AppWhite, GlowBlue, Color.Transparent),
                    ),
                    shape = CircleShape,
                )
                .align(Alignment.TopEnd),
        )
    }
}
