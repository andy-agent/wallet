package com.cryptovpn.ui.pages.p0

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.cryptovpn.ui.components.app.TechScaffold
import com.cryptovpn.ui.components.buttons.GradientCTAButton
import com.cryptovpn.ui.components.cards.GradientHeroCard
import com.cryptovpn.ui.components.cards.MiniMetricPill
import com.cryptovpn.ui.components.cards.SettingTileCard
import com.cryptovpn.ui.components.cards.TechCard
import com.cryptovpn.ui.components.listitems.RegionSpeedRow
import com.cryptovpn.ui.components.listitems.WatchSignalRow
import com.cryptovpn.ui.components.navigation.CryptoVpnBottomBar
import com.cryptovpn.ui.components.navigation.CryptoVpnTopBar
import com.cryptovpn.ui.effects.ConnectionHero
import com.cryptovpn.ui.effects.MotionProfile
import com.cryptovpn.ui.p0.model.VpnConnectionStatus
import com.cryptovpn.ui.p0.model.VpnHomeEvent
import com.cryptovpn.ui.p0.viewmodel.VpnHomeViewModel
import com.cryptovpn.ui.theme.TextMuted

@Composable
fun VpnHomeRoute(
    currentRoute: String,
    viewModel: VpnHomeViewModel,
    onBottomNav: (String) -> Unit,
    onWalletHome: () -> Unit,
    onPlans: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()

    TechScaffold(
        motionProfile = MotionProfile.L2,
        showNetwork = true,
        topBar = {
            CryptoVpnTopBar(
                title = "Unified secure home",
                subtitle = "VPN + wallet shell",
            )
        },
        bottomBar = {
            CryptoVpnBottomBar(
                currentRoute = currentRoute,
                onRouteSelected = onBottomNav,
            )
        },
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            item {
                ConnectionHero(
                    status = uiState.connectionStatus,
                    motionProfile = MotionProfile.L3,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp),
                )
                GradientHeroCard(
                    title = "VPN state",
                    value = when (uiState.connectionStatus) {
                        VpnConnectionStatus.DISCONNECTED -> "Offline"
                        VpnConnectionStatus.CONNECTING -> "Connecting"
                        VpnConnectionStatus.CONNECTED -> "Protected"
                    },
                    subtitle = "${uiState.selectedRegion.regionName} · ${uiState.selectedRegion.latencyMs} ms",
                    accent = "${uiState.subscription.expiresInDays} days left",
                )
            }

            item {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    MiniMetricPill("Wallet", uiState.walletTotalLabel)
                    MiniMetricPill("Plan", uiState.subscription.planName)
                    MiniMetricPill("Renewal", uiState.subscription.nextBillingLabel)
                }
            }

            item {
                GradientCTAButton(
                    text = uiState.oneTapLabel,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    viewModel.onEvent(VpnHomeEvent.ToggleConnection)
                }
            }

            item {
                SettingTileCard(
                    title = "Auto-connect rules",
                    summary = "Connect on insecure Wi-Fi and on app relaunch.",
                    checked = uiState.autoConnectEnabled,
                    onCheckedChange = { viewModel.onEvent(VpnHomeEvent.AutoConnectChanged(it)) },
                )
            }

            item {
                TechCard {
                    Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("Subscription", style = MaterialTheme.typography.titleMedium)
                            Text(
                                "Track expiry and quick upgrade access from the same shell.",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextMuted,
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    GradientCTAButton(
                        text = "Open plans",
                        modifier = Modifier.fillMaxWidth(),
                        onClick = onPlans,
                    )
                }
            }

            item {
                Text("Fast nodes", style = MaterialTheme.typography.titleLarge)
            }

            items(uiState.speedNodes) { node ->
                RegionSpeedRow(
                    item = node,
                    modifier = Modifier,
                )
            }

            item {
                Text("Market watch", style = MaterialTheme.typography.titleLarge)
            }

            items(uiState.watchSignals) { signal ->
                WatchSignalRow(signal = signal)
            }

            item {
                TechCard {
                    Text("Wallet snapshot", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        "Enter the multichain asset shell, review balances, and prepare send / receive actions.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted,
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    GradientCTAButton(
                        text = "Open wallet",
                        modifier = Modifier.fillMaxWidth(),
                        onClick = onWalletHome,
                    )
                }
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}
