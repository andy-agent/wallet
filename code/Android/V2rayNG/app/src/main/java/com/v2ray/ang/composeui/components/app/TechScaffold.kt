package com.v2ray.ang.composeui.components.app

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import com.v2ray.ang.composeui.effects.MotionProfile
import com.v2ray.ang.composeui.theme.AppWhite
import com.v2ray.ang.composeui.theme.ElectricCyan
import com.v2ray.ang.composeui.theme.LayerWhite
import com.v2ray.ang.composeui.theme.ScreenStroke

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
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .padding(horizontal = 14.dp, vertical = 12.dp),
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 430.dp)
                    .heightIn(min = maxHeight - 6.dp),
                shape = RoundedCornerShape(34.dp),
                color = LayerWhite.copy(alpha = 0.96f),
                border = BorderStroke(1.dp, ScreenStroke),
                shadowElevation = 24.dp,
                tonalElevation = 0.dp,
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
}
