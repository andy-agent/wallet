package com.v2ray.ang.composeui.pages.p0

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
    LaunchedEffect(uiState.completed) {
        if (uiState.completed) {
            onPrimaryAction()
        }
    }
    ResetPasswordScreen(
        uiState = uiState,
        onFieldChanged = { key, value -> viewModel.onEvent(ResetPasswordEvent.FieldChanged(key, value)) },
        onRequestCode = { viewModel.onEvent(ResetPasswordEvent.RequestCodeClicked) },
        onPrimaryAction = { viewModel.onEvent(ResetPasswordEvent.PrimaryActionClicked) },
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
    onRequestCode: () -> Unit,
    onPrimaryAction: () -> Unit,
    onBack: () -> Unit,
    onBottomNav: (String) -> Unit = {},
) {
    P01PhoneScaffold(
        currentRoute = CryptoVpnRouteSpec.resetPassword.name,
        onBottomNav = onBottomNav,
    ) {
        P01Header(
            eyebrow = "RESET PASSWORD",
            title = uiState.title,
            backLabel = "<",
            onBack = onBack,
        )

        P01Card {
            uiState.successMessage?.let { P01CardCopy("成功：$it") }
            uiState.errorMessage?.let { P01CardCopy("失败：$it") }
            uiState.unavailableMessage?.let { P01CardCopy("不可用：$it") }

            resetField(uiState, "email")?.let { field ->
                P01InputField(field.label, field.value, { onFieldChanged(field.key, it) })
            }
            resetField(uiState, "code")?.let { field ->
                P01InputField(
                    label = field.label,
                    value = field.value,
                    onValueChange = { onFieldChanged(field.key, it) },
                    trailingText = if (uiState.isRequestingCode) "发送中" else "发送验证码",
                    onTrailingClick = if (uiState.isRequestingCode) null else onRequestCode,
                )
            }
            resetField(uiState, "password")?.let { field ->
                P01InputField(
                    label = field.label,
                    value = field.value,
                    onValueChange = { onFieldChanged(field.key, it) },
                    password = true,
                )
            }
            resetField(uiState, "confirm")?.let { field ->
                P01InputField(
                    label = field.label,
                    value = field.value,
                    onValueChange = { onFieldChanged(field.key, it) },
                    password = true,
                )
            }
            P01PrimaryButton(
                text = if (uiState.isSubmitting) "提交中..." else uiState.primaryActionLabel,
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
            onRequestCode = {},
            onPrimaryAction = {},
            onBack = {},
        )
    }
}
