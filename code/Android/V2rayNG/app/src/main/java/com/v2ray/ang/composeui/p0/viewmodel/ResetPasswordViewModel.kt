package com.v2ray.ang.composeui.p0.viewmodel

import androidx.lifecycle.viewModelScope
import com.v2ray.ang.composeui.common.repository.CryptoVpnRepository
import com.v2ray.ang.composeui.common.viewmodel.BaseFeatureViewModel
import com.v2ray.ang.composeui.p0.model.ResetPasswordEvent
import com.v2ray.ang.composeui.p0.model.ResetPasswordUiState
import kotlinx.coroutines.launch

class ResetPasswordViewModel(
    private val repository: CryptoVpnRepository,
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
                    successMessage = null,
                    unavailableMessage = null,
                    completed = false,
                )
            }

            ResetPasswordEvent.RequestCodeClicked -> requestCode()
            ResetPasswordEvent.PrimaryActionClicked -> submit()
            ResetPasswordEvent.SecondaryActionClicked -> Unit
            ResetPasswordEvent.Refresh -> refresh()
        }
    }

    private fun refresh() {
        launchLoad {
            repository.getResetPasswordState()
        }
    }

    private fun requestCode() {
        val email = fieldValue("email")
        _uiState.value = _uiState.value.copy(
            isRequestingCode = true,
            errorMessage = null,
            successMessage = null,
            unavailableMessage = null,
            completed = false,
        )
        viewModelScope.launch {
            val result = repository.requestResetPasswordCode(email)
            _uiState.value = _uiState.value.copy(
                isRequestingCode = false,
                errorMessage = result.errorMessage,
                successMessage = result.successMessage,
                unavailableMessage = if (result.unavailable) result.errorMessage else null,
                completed = false,
            )
        }
    }

    private fun submit() {
        val email = fieldValue("email")
        val code = fieldValue("code")
        val password = fieldValue("password")
        val confirm = fieldValue("confirm")
        if (password != confirm) {
            _uiState.value = _uiState.value.copy(
                errorMessage = "两次输入的新密码不一致",
                successMessage = null,
                unavailableMessage = null,
                completed = false,
            )
            return
        }

        _uiState.value = _uiState.value.copy(
            isSubmitting = true,
            errorMessage = null,
            successMessage = null,
            unavailableMessage = null,
            completed = false,
        )
        viewModelScope.launch {
            val result = repository.resetPassword(
                email = email,
                code = code,
                password = password,
            )
            _uiState.value = _uiState.value.copy(
                isSubmitting = false,
                errorMessage = result.errorMessage,
                successMessage = result.successMessage,
                unavailableMessage = if (result.unavailable) result.errorMessage else null,
                completed = result.completed,
            )
        }
    }

    private fun fieldValue(key: String): String =
        _uiState.value.fields.firstOrNull { it.key == key }?.value.orEmpty()
}
