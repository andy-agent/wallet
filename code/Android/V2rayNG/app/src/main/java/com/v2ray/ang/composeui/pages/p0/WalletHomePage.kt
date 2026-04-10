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
import androidx.compose.foundation.lazy.LazyColumn
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
import com.v2ray.ang.composeui.components.cards.GradientHeroCard
import com.v2ray.ang.composeui.components.cards.TechCard
import com.v2ray.ang.composeui.components.listitems.AssetRow
import com.v2ray.ang.composeui.components.navigation.CryptoVpnBottomBar
import com.v2ray.ang.composeui.components.navigation.CryptoVpnTopBar
import com.v2ray.ang.composeui.effects.MotionProfile
import com.v2ray.ang.composeui.p0.model.WalletHomeEvent
import com.v2ray.ang.composeui.p0.viewmodel.WalletHomeViewModel
import com.v2ray.ang.composeui.theme.TextMuted

@Composable
fun WalletHomeRoute(
    currentRoute: String,
    viewModel: WalletHomeViewModel,
    onBottomNav: (String) -> Unit,
    onReceive: (() -> Unit)? = null,
    onSend: (() -> Unit)? = null,
) {
    val uiState by viewModel.uiState.collectAsState()

    TechScaffold(
        motionProfile = MotionProfile.L2,
        showNetwork = true,
        topBar = {
            CryptoVpnTopBar(
                title = "Multichain wallet",
                subtitle = "ETH / BSC / Polygon / Arbitrum / Base / Solana / TRON",
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
                GradientHeroCard(
                    title = "Total balance",
                    value = uiState.totalBalanceText,
                    subtitle = uiState.alertBanner,
                    accent = "Hot / cold shell",
                )
            }

            item {
                Text("Chains", style = MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.height(10.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    uiState.chains.take(3).forEach { chain ->
                        SecondaryOutlineButton(
                            selected = chain.chainId == uiState.selectedChainId,
                            onClick = { viewModel.onEvent(WalletHomeEvent.ChainSelected(chain.chainId)) },
                            label = { Text(chain.label) },
                        )
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    uiState.chains.drop(3).forEach { chain ->
                        SecondaryOutlineButton(
                            selected = chain.chainId == uiState.selectedChainId,
                            onClick = { viewModel.onEvent(WalletHomeEvent.ChainSelected(chain.chainId)) },
                            label = { Text(chain.label) },
                        )
                    }
                }
            }

            item {
                TechCard {
                    Text("Quick actions", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(12.dp))
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        GradientCTAButton(
                            text = "Receive",
                            modifier = Modifier.fillMaxWidth(),
                            onClick = { onReceive?.invoke() },
                        )
                        GradientCTAButton(
                            text = "Send",
                            modifier = Modifier.fillMaxWidth(),
                            onClick = { onSend?.invoke() },
                        )
                    }
                }
            }

            item {
                Text("Assets", style = MaterialTheme.typography.titleLarge)
            }

            items(uiState.assets) { asset ->
                AssetRow(asset = asset)
            }

            item {
                TechCard {
                    Text("Security note", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        "P2 extends this shell with seed backup, custom tokens, bridge, swap, DApp sessions, NFT and approval management.",
                        color = TextMuted,
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
                Spacer(modifier = Modifier.height(22.dp))
            }
        }
    }
}
