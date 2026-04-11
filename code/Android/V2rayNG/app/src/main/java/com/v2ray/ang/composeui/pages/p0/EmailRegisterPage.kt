package com.v2ray.ang.composeui.pages.p0

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.v2ray.ang.composeui.navigation.CryptoVpnRouteSpec
import com.v2ray.ang.composeui.p0.model.EmailRegisterEvent
import com.v2ray.ang.composeui.p0.model.EmailRegisterUiState
import com.v2ray.ang.composeui.p0.model.emailRegisterPreviewState
import com.v2ray.ang.composeui.p0.ui.P01Card
import com.v2ray.ang.composeui.p0.ui.P01CardCopy
import com.v2ray.ang.composeui.p0.ui.P01Header
import com.v2ray.ang.composeui.p0.ui.P01InputField
import com.v2ray.ang.composeui.p0.ui.P01PhoneScaffold
import com.v2ray.ang.composeui.p0.ui.P01PrimaryButton
import com.v2ray.ang.composeui.p0.ui.P01SecondaryButton
import com.v2ray.ang.composeui.p0.viewmodel.EmailRegisterViewModel
import com.v2ray.ang.composeui.theme.CryptoVpnTheme

@Composable
fun EmailRegisterRoute(
    viewModel: EmailRegisterViewModel,
    onPrimaryAction: () -> Unit = {},
    onSecondaryAction: (() -> Unit)? = null,
    onBottomNav: (String) -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsState()
    LaunchedEffect(uiState.statusMessage) {
        if (uiState.statusMessage?.contains("注册成功") == true) {
            onPrimaryAction()
        }
    }
    EmailRegisterScreen(
        uiState = uiState,
        onFieldChanged = { key, value -> viewModel.onEvent(EmailRegisterEvent.FieldChanged(key, value)) },
        onPrimaryAction = { viewModel.onEvent(EmailRegisterEvent.PrimaryActionClicked) },
        onRequestCode = { viewModel.onEvent(EmailRegisterEvent.SendCodeClicked) },
        onBack = {
            viewModel.onEvent(EmailRegisterEvent.SecondaryActionClicked)
            onSecondaryAction?.invoke()
        },
        onBottomNav = onBottomNav,
    )
}

@Composable
fun EmailRegisterScreen(
    uiState: EmailRegisterUiState,
    onFieldChanged: (String, String) -> Unit,
    onPrimaryAction: () -> Unit,
    onRequestCode: () -> Unit,
    onBack: () -> Unit,
    onBottomNav: (String) -> Unit = {},
) {
    P01PhoneScaffold(
        statusTime = "18:17",
        currentRoute = CryptoVpnRouteSpec.vpnHome.name,
        onBottomNav = onBottomNav,
    ) {
        P01Header(
            eyebrow = "CREATE ACCOUNT",
            title = "创建你的账户",
            backLabel = "<",
            onBack = onBack,
        )

        P01Card {
            registerField(uiState, "email")?.let { field ->
                P01InputField(field.label, field.value, { onFieldChanged(field.key, it) })
            }
            registerField(uiState, "code")?.let { field ->
                P01InputField(field.label, field.value, { onFieldChanged(field.key, it) })
            }
            registerField(uiState, "password")?.let { field ->
                P01InputField(field.label, field.value, { onFieldChanged(field.key, it) }, password = true)
            }
            registerField(uiState, "invite")?.let { field ->
                P01InputField(field.label, field.value, { onFieldChanged(field.key, it) })
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
                text = if (uiState.isLoading) "注册中..." else "创建账户并进入",
                onClick = onPrimaryAction,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

private fun registerField(uiState: EmailRegisterUiState, key: String) =
    uiState.fields.firstOrNull { it.key == key }

@Preview(showBackground = true, widthDp = 393, heightDp = 852)
@Composable
private fun EmailRegisterPreview() {
    CryptoVpnTheme {
        EmailRegisterScreen(
            uiState = emailRegisterPreviewState(),
            onFieldChanged = { _, _ -> },
            onPrimaryAction = {},
            onRequestCode = {},
            onBack = {},
        )
    }
}
