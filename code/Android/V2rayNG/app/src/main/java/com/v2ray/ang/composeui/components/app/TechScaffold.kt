package com.v2ray.ang.composeui.components.app

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import com.v2ray.ang.composeui.effects.MotionProfile
import com.v2ray.ang.composeui.theme.AppWhite
import com.v2ray.ang.composeui.theme.ElectricCyan
import com.v2ray.ang.composeui.theme.LayerWhite

@Composable
fun TechScaffold(
    modifier: Modifier = Modifier,
    motionProfile: MotionProfile = MotionProfile.L1,
    showNetwork: Boolean = true,
    topBar: @Composable () -> Unit = {},
    bottomBar: @Composable () -> Unit = {},
    content: @Composable (PaddingValues) -> Unit,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        AppWhite,
                        LayerWhite,
                    ),
                ),
            ),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding(),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                ElectricCyan.copy(alpha = 0.16f),
                                LayerWhite.copy(alpha = 0f),
                            ),
                            radius = 1100f,
                        ),
                    ),
            ) {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = topBar,
                    bottomBar = bottomBar,
                    containerColor = androidx.compose.ui.graphics.Color.Transparent,
                    content = content,
                )
            }
        }
    }
}
