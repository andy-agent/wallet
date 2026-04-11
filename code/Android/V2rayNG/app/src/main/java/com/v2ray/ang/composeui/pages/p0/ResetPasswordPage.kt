package com.v2ray.ang.composeui.pages.p0

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.v2ray.ang.composeui.navigation.CryptoVpnRouteSpec
import com.v2ray.ang.composeui.p0.model.ResetPasswordEvent
import com.v2ray.ang.composeui.p0.model.ResetPasswordUiState
import com.v2ray.ang.composeui.p0.model.resetPasswordPreviewState
import com.v2ray.ang.composeui.p0.ui.P01Card
import com.v2ray.ang.composeui.p0.ui.P01CardCopy
import com.v2ray.ang.composeui.p0.ui.P01Header
import com.v2ray.ang.composeui.p0.ui.P01InputField
import com.v2ray.ang.composeui.p0.ui.P01PhoneScaffold
import com.v2ray.ang.composeui.p0.ui.P01PrimaryButton
import com.v2ray.ang.composeui.p0.ui.P01SecondaryButton
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
    LaunchedEffect(uiState.statusMessage) {
        if (uiState.statusMessage?.contains("密码已重置") == true) {
            onPrimaryAction()
        }
    }
    ResetPasswordScreen(
        uiState = uiState,
        onFieldChanged = { key, value -> viewModel.onEvent(ResetPasswordEvent.FieldChanged(key, value)) },
        onPrimaryAction = { viewModel.onEvent(ResetPasswordEvent.PrimaryActionClicked) },
        onRequestCode = { viewModel.onEvent(ResetPasswordEvent.SendCodeClicked) },
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
    onRequestCode: () -> Unit,
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
            resetField(uiState, "password")?.let { field ->
                P01InputField(field.label, field.value, { onFieldChanged(field.key, it) }, password = true)
            }
            resetField(uiState, "confirm")?.let { field ->
                P01InputField(field.label, field.value, { onFieldChanged(field.key, it) }, password = true)
            }
            uiState.statusMessage?.let {
                P01CardCopy(it)
            }
            uiState.errorMessage?.let {
                P01CardCopy(it)
            }
            P01SecondaryButton(
                text = if (uiState.isRequestingCode) "发送中..." else "发送验证码",
                onClick = onRequestCode,
                modifier = Modifier.fillMaxWidth(),
            )
            P01PrimaryButton(
                text = if (uiState.isLoading) "重置中..." else "提交并重置密码",
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
            onRequestCode = {},
            onBack = {},
        )
    }
}
