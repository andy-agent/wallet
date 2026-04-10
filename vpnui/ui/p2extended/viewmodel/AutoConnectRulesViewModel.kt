package com.cryptovpn.ui.p2extended.viewmodel

import com.cryptovpn.ui.common.repository.CryptoVpnRepository
import com.cryptovpn.ui.common.viewmodel.BaseFeatureViewModel
import com.cryptovpn.ui.p2extended.model.AutoConnectRulesEvent
import com.cryptovpn.ui.p2extended.model.AutoConnectRulesUiState

class AutoConnectRulesViewModel(
    private val repository: CryptoVpnRepository,
) : BaseFeatureViewModel<AutoConnectRulesUiState>(AutoConnectRulesUiState()) {

    init {
        refresh()
    }

    fun onEvent(event: AutoConnectRulesEvent) {
        when (event) {
            is AutoConnectRulesEvent.FieldChanged -> {
                _uiState.value = _uiState.value.copy(
                    fields = _uiState.value.fields.map { field ->
                        if (field.key == event.key) field.copy(value = event.value) else field
                    },
                )
            }

            AutoConnectRulesEvent.PrimaryActionClicked -> Unit
            AutoConnectRulesEvent.SecondaryActionClicked -> Unit
            AutoConnectRulesEvent.Refresh -> refresh()
        }
    }

    private fun refresh() {
        launchLoad {
            repository.getAutoConnectRulesState()
        }
    }
}
