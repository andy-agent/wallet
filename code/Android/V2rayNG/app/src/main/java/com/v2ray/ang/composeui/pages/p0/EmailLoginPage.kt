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
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.v2ray.ang.composeui.components.app.TechScaffold
import com.v2ray.ang.composeui.components.buttons.GradientCTAButton
import com.v2ray.ang.composeui.components.buttons.SecondaryOutlineButton
import com.v2ray.ang.composeui.components.cards.TechCard
import com.v2ray.ang.composeui.components.inputs.GlassTextField
import com.v2ray.ang.composeui.components.navigation.CryptoVpnTopBar
import com.v2ray.ang.composeui.effects.MotionProfile
import com.v2ray.ang.composeui.p0.model.LoginEvent
import com.v2ray.ang.composeui.p0.viewmodel.LoginViewModel
import com.v2ray.ang.composeui.theme.TextMuted

@Composable
fun EmailLoginRoute(
    viewModel: LoginViewModel,
    onLoginSuccess: () -> Unit,
    onForgotPassword: () -> Unit,
    onRegister: () -> Unit,
    onWalletOnboarding: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()

    TechScaffold(
        motionProfile = MotionProfile.L2,
        showNetwork = true,
        topBar = {
            CryptoVpnTopBar(
                title = "Secure access",
                subtitle = "Android shell",
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
            TechCard {
                Text(
                    text = "Log in to manage VPN subscriptions, wallet balance, and multichain activity in one place.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextMuted,
                )
            }

            GlassTextField(
                value = uiState.email,
                label = "Email",
                onValueChange = { viewModel.onEvent(LoginEvent.EmailChanged(it)) },
            )
            GlassTextField(
                value = uiState.password,
                label = "Password",
                onValueChange = { viewModel.onEvent(LoginEvent.PasswordChanged(it)) },
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .toggleable(
                        value = uiState.rememberMe,
                        onValueChange = { viewModel.onEvent(LoginEvent.RememberMeChanged(it)) },
                    ),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Checkbox(
                    checked = uiState.rememberMe,
                    onCheckedChange = { viewModel.onEvent(LoginEvent.RememberMeChanged(it)) },
                )
                Text("Remember this device", style = MaterialTheme.typography.bodyMedium)
            }

            GradientCTAButton(
                text = if (uiState.isLoading) "Signing in..." else "Continue",
                modifier = Modifier.fillMaxWidth(),
            ) {
                viewModel.onEvent(LoginEvent.LoginClicked, onLoginSuccess)
            }

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                SecondaryOutlineButton(
                    onClick = onForgotPassword,
                    label = { Text("Forgot password") },
                )
                SecondaryOutlineButton(
                    onClick = onRegister,
                    label = { Text("Create account") },
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            TechCard {
                Text("Wallet first", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = uiState.helperText,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMuted,
                )
                Spacer(modifier = Modifier.height(12.dp))
                GradientCTAButton(
                    text = "Create or import wallet",
                    modifier = Modifier.fillMaxWidth(),
                    onClick = onWalletOnboarding,
                )
            }
        }
    }
}
