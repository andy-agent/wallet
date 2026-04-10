package com.v2ray.ang.composeui.pages.p0

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.v2ray.ang.composeui.components.app.TechScaffold
import com.v2ray.ang.composeui.components.buttons.GradientCTAButton
import com.v2ray.ang.composeui.components.buttons.SecondaryOutlineButton
import com.v2ray.ang.composeui.components.cards.TechCard
import com.v2ray.ang.composeui.components.navigation.CryptoVpnTopBar
import com.v2ray.ang.composeui.effects.MotionProfile
import com.v2ray.ang.composeui.p0.model.WalletCreationMode
import com.v2ray.ang.composeui.p0.model.WalletOnboardingEvent
import com.v2ray.ang.composeui.p0.viewmodel.WalletOnboardingViewModel
import com.v2ray.ang.composeui.theme.TextMuted

@Composable
fun WalletOnboardingRoute(
    viewModel: WalletOnboardingViewModel,
    onContinue: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()

    TechScaffold(
        motionProfile = MotionProfile.L2,
        topBar = {
            CryptoVpnTopBar(
                title = "Wallet setup",
                subtitle = "Self-custody core",
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp),
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                SecondaryOutlineButton(
                    selected = uiState.selectedMode == WalletCreationMode.CREATE,
                    onClick = {
                        viewModel.onEvent(WalletOnboardingEvent.SelectMode(WalletCreationMode.CREATE))
                    },
                    label = { Text("Create new wallet") },
                )
                SecondaryOutlineButton(
                    selected = uiState.selectedMode == WalletCreationMode.IMPORT,
                    onClick = {
                        viewModel.onEvent(WalletOnboardingEvent.SelectMode(WalletCreationMode.IMPORT))
                    },
                    label = { Text("Import existing") },
                )
            }

            TechCard {
                Text(
                    text = if (uiState.selectedMode == WalletCreationMode.CREATE) {
                        "Generate a new seed, secure recovery flow, then land on the multichain asset shell."
                    } else {
                        "Import seed phrase / private key in P2, then restore supported chains and wallet sessions."
                    },
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextMuted,
                )
            }

            TechCard {
                Text("P0 supported surfaces", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(12.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(uiState.focusedChains) { label ->
                        SecondaryOutlineButton(onClick = {}, selected = false, label = { Text(label) })
                    }
                }
            }

            TechCard {
                Text("White-tech direction", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "White background, blue-cyan-purple glow, secure metrics, and restrained card language.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMuted,
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
            GradientCTAButton(
                text = if (uiState.selectedMode == WalletCreationMode.CREATE) "Create wallet" else "Go to import flow",
                modifier = Modifier.fillMaxWidth(),
                onClick = onContinue,
            )
        }
    }
}
