package com.v2ray.ang.composeui.pages.p0

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.v2ray.ang.composeui.navigation.CryptoVpnRouteSpec
import com.v2ray.ang.composeui.p0.model.ResetPasswordEvent
import com.v2ray.ang.composeui.p0.model.ResetPasswordUiState
import com.v2ray.ang.composeui.p0.model.resetPasswordPreviewState
import com.v2ray.ang.composeui.p0.ui.P01Card
import com.v2ray.ang.composeui.p0.ui.P01Header
import com.v2ray.ang.composeui.p0.ui.P01InputField
import com.v2ray.ang.composeui.p0.ui.P01PhoneScaffold
import com.v2ray.ang.composeui.p0.ui.P01PrimaryButton
import com.v2ray.ang.composeui.p0.viewmodel.ResetPasswordViewModel
import com.v2ray.ang.composeui.theme.CryptoVpnTheme

@Composable
fun ResetPasswordRoute(
    viewModel: ResetPasswordViewModel,
    onPrimaryAction: () -> Unit = {},
    onSecondaryAction: (() -> Unit)? = null,
    onBottomNav: (String) -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsState()
    ResetPasswordScreen(
        uiState = uiState,
        onFieldChanged = { key, value -> viewModel.onEvent(ResetPasswordEvent.FieldChanged(key, value)) },
        onPrimaryAction = {
            viewModel.onEvent(ResetPasswordEvent.PrimaryActionClicked)
            onPrimaryAction()
        },
        onBack = {
            viewModel.onEvent(ResetPasswordEvent.SecondaryActionClicked)
            onSecondaryAction?.invoke()
        },
        onBottomNav = onBottomNav,
    )
}

@Composable
fun ResetPasswordScreen(
    uiState: ResetPasswordUiState,
    onFieldChanged: (String, String) -> Unit,
    onPrimaryAction: () -> Unit,
    onBack: () -> Unit,
    onBottomNav: (String) -> Unit = {},
) {
    P01PhoneScaffold(
        statusTime = "18:18",
        currentRoute = CryptoVpnRouteSpec.vpnHome.name,
        onBottomNav = onBottomNav,
    ) {
        P01Header(
            eyebrow = "RESET PASSWORD",
            title = "重置密码",
            backLabel = "<",
            onBack = onBack,
        )

        P01Card {
            resetField(uiState, "email")?.let { field ->
                P01InputField(field.label, field.value, { onFieldChanged(field.key, it) })
            }
            resetField(uiState, "code")?.let { field ->
                P01InputField(field.label, field.value, { onFieldChanged(field.key, it) })
            }
            (resetField(uiState, "confirm") ?: resetField(uiState, "password"))?.let { field ->
                P01InputField(field.label, field.value, { onFieldChanged(field.key, it) }, password = true)
            }
            P01PrimaryButton(
                text = "更确认密码",
                onClick = onPrimaryAction,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

private fun resetField(uiState: ResetPasswordUiState, key: String) =
    uiState.fields.firstOrNull { it.key == key }

@Preview(showBackground = true, widthDp = 393, heightDp = 852)
@Composable
private fun ResetPasswordPreview() {
    CryptoVpnTheme {
        ResetPasswordScreen(
            uiState = resetPasswordPreviewState(),
            onFieldChanged = { _, _ -> },
            onPrimaryAction = {},
            onBack = {},
        )
    }
}
