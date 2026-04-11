package com.v2ray.ang.composeui.p0.viewmodel

import androidx.lifecycle.viewModelScope
import com.v2ray.ang.composeui.common.repository.CryptoVpnRepository
import com.v2ray.ang.composeui.common.viewmodel.BaseFeatureViewModel
import com.v2ray.ang.composeui.p0.model.EmailRegisterEvent
import com.v2ray.ang.composeui.p0.model.EmailRegisterUiState
import com.v2ray.ang.composeui.p0.repository.P0Repository
import kotlinx.coroutines.launch

class EmailRegisterViewModel(
    private val repository: CryptoVpnRepository,
    private val p0Repository: P0Repository,
) : BaseFeatureViewModel<EmailRegisterUiState>(EmailRegisterUiState()) {

    init {
        refresh()
    }

    fun onEvent(event: EmailRegisterEvent) {
        when (event) {
            is EmailRegisterEvent.FieldChanged -> {
                _uiState.value = _uiState.value.copy(
                    fields = _uiState.value.fields.map { field ->
                        if (field.key == event.key) field.copy(value = event.value) else field
                    },
                    errorMessage = null,
                    statusMessage = null,
                )
            }

            EmailRegisterEvent.SendCodeClicked -> {
                viewModelScope.launch {
                    val email = fieldValue("email")
                    _uiState.value = _uiState.value.copy(isRequestingCode = true, errorMessage = null, statusMessage = null)
                    val result = p0Repository.requestRegisterCode(email)
                    _uiState.value = _uiState.value.copy(
                        isRequestingCode = false,
                        statusMessage = result.message.takeIf { result.success },
                        errorMessage = result.message.takeIf { !result.success },
                    )
                }
            }

            EmailRegisterEvent.PrimaryActionClicked -> {
                viewModelScope.launch {
                    val email = fieldValue("email")
                    val code = fieldValue("code")
                    val password = fieldValue("password")
                    _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null, statusMessage = null)
                    val result = p0Repository.register(email, password, code)
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        statusMessage = result.message.takeIf { result.success },
                        errorMessage = result.message.takeIf { !result.success },
                    )
                }
            }
            EmailRegisterEvent.SecondaryActionClicked -> Unit
            EmailRegisterEvent.Refresh -> refresh()
        }
    }

    private fun fieldValue(key: String): String =
        _uiState.value.fields.firstOrNull { it.key == key }?.value.orEmpty()

    private fun refresh() {
        launchLoad {
            repository.getEmailRegisterState()
        }
    }
}
