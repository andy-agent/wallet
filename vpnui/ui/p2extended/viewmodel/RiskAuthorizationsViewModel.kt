package com.cryptovpn.ui.p2extended.viewmodel

import com.cryptovpn.ui.common.repository.CryptoVpnRepository
import com.cryptovpn.ui.common.viewmodel.BaseFeatureViewModel
import com.cryptovpn.ui.p2extended.model.RiskAuthorizationsEvent
import com.cryptovpn.ui.p2extended.model.RiskAuthorizationsUiState

class RiskAuthorizationsViewModel(
    private val repository: CryptoVpnRepository,
) : BaseFeatureViewModel<RiskAuthorizationsUiState>(RiskAuthorizationsUiState()) {

    init {
        refresh()
    }

    fun onEvent(event: RiskAuthorizationsEvent) {
        when (event) {
            is RiskAuthorizationsEvent.FieldChanged -> {
                _uiState.value = _uiState.value.copy(
                    fields = _uiState.value.fields.map { field ->
                        if (field.key == event.key) field.copy(value = event.value) else field
                    },
                )
            }

            RiskAuthorizationsEvent.PrimaryActionClicked -> Unit
            RiskAuthorizationsEvent.SecondaryActionClicked -> Unit
            RiskAuthorizationsEvent.Refresh -> refresh()
        }
    }

    private fun refresh() {
        launchLoad {
            repository.getRiskAuthorizationsState()
        }
    }
}
