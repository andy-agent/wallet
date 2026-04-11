package com.v2ray.ang.composeui.p0.viewmodel

import androidx.lifecycle.viewModelScope
import com.v2ray.ang.composeui.common.repository.CryptoVpnRepository
import com.v2ray.ang.composeui.common.viewmodel.BaseFeatureViewModel
import com.v2ray.ang.composeui.p0.model.ResetPasswordEvent
import com.v2ray.ang.composeui.p0.model.ResetPasswordUiState
import com.v2ray.ang.composeui.p0.repository.P0Repository
import kotlinx.coroutines.launch

class ResetPasswordViewModel(
    private val repository: CryptoVpnRepository,
    private val p0Repository: P0Repository,
) : BaseFeatureViewModel<ResetPasswordUiState>(ResetPasswordUiState()) {

    init {
        refresh()
    }

    fun onEvent(event: ResetPasswordEvent) {
        when (event) {
            is ResetPasswordEvent.FieldChanged -> {
                _uiState.value = _uiState.value.copy(
                    fields = _uiState.value.fields.map { field ->
                        if (field.key == event.key) field.copy(value = event.value) else field
                    },
                    errorMessage = null,
                    statusMessage = null,
                )
            }

            ResetPasswordEvent.SendCodeClicked -> {
                viewModelScope.launch {
                    val email = fieldValue("email")
                    _uiState.value = _uiState.value.copy(isRequestingCode = true, errorMessage = null, statusMessage = null)
                    val result = p0Repository.requestResetCode(email)
                    _uiState.value = _uiState.value.copy(
                        isRequestingCode = false,
                        statusMessage = result.message.takeIf { result.success },
                        errorMessage = result.message.takeIf { !result.success },
                    )
                }
            }

            ResetPasswordEvent.PrimaryActionClicked -> {
                viewModelScope.launch {
                    val email = fieldValue("email")
                    val code = fieldValue("code")
                    val password = fieldValue("password")
                    val confirm = fieldValue("confirm")
                    if (password != confirm) {
                        _uiState.value = _uiState.value.copy(errorMessage = "两次输入的密码不一致")
                        return@launch
                    }
                    _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null, statusMessage = null)
                    val result = p0Repository.resetPassword(email, code, password)
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        statusMessage = result.message.takeIf { result.success },
                        errorMessage = result.message.takeIf { !result.success },
                    )
                }
            }
            ResetPasswordEvent.SecondaryActionClicked -> Unit
            ResetPasswordEvent.Refresh -> refresh()
        }
    }

    private fun fieldValue(key: String): String =
        _uiState.value.fields.firstOrNull { it.key == key }?.value.orEmpty()

    private fun refresh() {
        launchLoad {
            repository.getResetPasswordState()
        }
    }
}
