package com.cryptovpn.ui.p0.viewmodel

import com.cryptovpn.ui.common.repository.CryptoVpnRepository
import com.cryptovpn.ui.common.viewmodel.BaseFeatureViewModel
import com.cryptovpn.ui.p0.model.EmailRegisterEvent
import com.cryptovpn.ui.p0.model.EmailRegisterUiState

class EmailRegisterViewModel(
    private val repository: CryptoVpnRepository,
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
                )
            }

            EmailRegisterEvent.PrimaryActionClicked -> Unit
            EmailRegisterEvent.SecondaryActionClicked -> Unit
            EmailRegisterEvent.Refresh -> refresh()
        }
    }

    private fun refresh() {
        launchLoad {
            repository.getEmailRegisterState()
        }
    }
}
