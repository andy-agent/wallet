package com.cryptovpn.ui.components.app

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.cryptovpn.ui.effects.MotionProfile
import com.cryptovpn.ui.effects.TechParticleBackground

@Composable
fun TechScaffold(
    modifier: Modifier = Modifier,
    motionProfile: MotionProfile = MotionProfile.L1,
    showNetwork: Boolean = true,
    topBar: @Composable () -> Unit = {},
    bottomBar: @Composable () -> Unit = {},
    content: @Composable (PaddingValues) -> Unit,
) {
    Box(modifier = modifier.fillMaxSize()) {
        TechParticleBackground(
            motionProfile = motionProfile,
            modifier = Modifier.fillMaxSize(),
            showNetwork = showNetwork,
        )
        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding(),
            topBar = topBar,
            bottomBar = bottomBar,
            containerColor = androidx.compose.ui.graphics.Color.Transparent,
            content = content,
        )
    }
}
