package com.v2ray.ang.composeui.p0.viewmodel

import com.v2ray.ang.composeui.common.repository.CryptoVpnRepository
import com.v2ray.ang.composeui.common.viewmodel.BaseFeatureViewModel
import com.v2ray.ang.composeui.p0.model.ResetPasswordEvent
import com.v2ray.ang.composeui.p0.model.ResetPasswordUiState

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
