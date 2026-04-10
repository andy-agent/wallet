package com.cryptovpn.ui.p1.viewmodel

import com.cryptovpn.ui.common.repository.CryptoVpnRepository
import com.cryptovpn.ui.common.viewmodel.BaseFeatureViewModel
import com.cryptovpn.ui.p1.model.PlansEvent
import com.cryptovpn.ui.p1.model.PlansUiState

class PlansViewModel(
    private val repository: CryptoVpnRepository,
) : BaseFeatureViewModel<PlansUiState>(PlansUiState()) {

    init {
        refresh()
    }

    fun onEvent(event: PlansEvent) {
        when (event) {
            is PlansEvent.FieldChanged -> {
                _uiState.value = _uiState.value.copy(
                    fields = _uiState.value.fields.map { field ->
                        if (field.key == event.key) field.copy(value = event.value) else field
                    },
                )
            }

            PlansEvent.PrimaryActionClicked -> Unit
            PlansEvent.SecondaryActionClicked -> Unit
            PlansEvent.Refresh -> refresh()
        }
    }

    private fun refresh() {
        launchLoad {
            repository.getPlansState()
        }
    }
}
