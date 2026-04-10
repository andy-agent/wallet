package com.v2ray.ang.composeui.pages.p0

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.v2ray.ang.composeui.components.app.TechScaffold
import com.v2ray.ang.composeui.components.cards.GradientHeroCard
import com.v2ray.ang.composeui.effects.ConnectionHero
import com.v2ray.ang.composeui.effects.MotionProfile
import com.v2ray.ang.composeui.p0.viewmodel.SplashViewModel
import kotlinx.coroutines.delay

@Composable
fun SplashRoute(
    viewModel: SplashViewModel,
    onFinished: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.authResolved) {
        if (uiState.authResolved) {
            delay(900)
            onFinished()
        }
    }

    TechScaffold(
        motionProfile = MotionProfile.L3,
        showNetwork = true,
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.Center,
        ) {
            ConnectionHero(
                status = com.v2ray.ang.composeui.p0.model.VpnConnectionStatus.CONNECTING,
                motionProfile = MotionProfile.L3,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "CryptoVPN",
                style = MaterialTheme.typography.headlineLarge,
            )
            Text(
                text = "Multichain wallet + secure VPN shell",
                style = MaterialTheme.typography.bodyLarge,
                color = com.v2ray.ang.composeui.theme.TextMuted,
            )
            Spacer(modifier = Modifier.height(18.dp))
            GradientHeroCard(
                title = "Secure bootstrap",
                value = uiState.versionLabel,
                subtitle = uiState.buildStatus,
            )
            Spacer(modifier = Modifier.height(18.dp))
            LinearProgressIndicator(
                progress = if (uiState.checkingSecureBoot) 0.48f else 1f,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}
