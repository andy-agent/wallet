package com.v2ray.ang.composeui.p0.viewmodel

import androidx.lifecycle.viewModelScope
import com.v2ray.ang.composeui.common.repository.CryptoVpnRepository
import com.v2ray.ang.composeui.common.viewmodel.BaseFeatureViewModel
import com.v2ray.ang.composeui.p0.model.EmailRegisterEvent
import com.v2ray.ang.composeui.p0.model.EmailRegisterUiState
import kotlinx.coroutines.launch

class EmailRegisterViewModel(
    private val repository: CryptoVpnRepository,
) : BaseFeatureViewModel<EmailRegisterUiState>(EmailRegisterUiState()) {
    var nextRoute: String? = null
        private set

    init {
        refresh()
    }

    fun onEvent(event: EmailRegisterEvent) {
        when (event) {
            is EmailRegisterEvent.FieldChanged -> {
                _uiState.value = _uiState.value.copy(
                    fields = _uiState.value.fields.map { field ->
                        if (field.key == event.key) {
                            field.copy(value = event.value)
                        } else {
                            field
                        }
                    },
                    errorMessage = null,
                    successMessage = null,
                    unavailableMessage = null,
                    completed = false,
                )
            }

            EmailRegisterEvent.RequestCodeClicked -> requestCode()
            EmailRegisterEvent.PrimaryActionClicked -> submit()
            EmailRegisterEvent.SecondaryActionClicked -> Unit
            EmailRegisterEvent.Refresh -> refresh()
        }
    }

    private fun refresh() {
        launchLoad {
            repository.getEmailRegisterState()
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
            val result = repository.requestEmailRegisterCode(email)
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
        val invite = fieldValue("invite")

        _uiState.value = _uiState.value.copy(
            isSubmitting = true,
            errorMessage = null,
            successMessage = null,
            unavailableMessage = null,
            completed = false,
        )
        viewModelScope.launch {
            val result = repository.registerEmail(
                email = email,
                password = password,
                code = code,
                inviteCode = invite,
            )
            nextRoute = result.nextRoute
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
