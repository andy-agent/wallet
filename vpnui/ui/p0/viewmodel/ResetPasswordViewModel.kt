package com.cryptovpn.ui.p0.viewmodel

import com.cryptovpn.ui.common.repository.CryptoVpnRepository
import com.cryptovpn.ui.common.viewmodel.BaseFeatureViewModel
import com.cryptovpn.ui.p0.model.ResetPasswordEvent
import com.cryptovpn.ui.p0.model.ResetPasswordUiState

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
                )
            }

            ResetPasswordEvent.PrimaryActionClicked -> Unit
            ResetPasswordEvent.SecondaryActionClicked -> Unit
            ResetPasswordEvent.Refresh -> refresh()
        }
    }

    private fun refresh() {
        launchLoad {
            repository.getResetPasswordState()
        }
    }
}
