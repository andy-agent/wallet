package com.cryptovpn.ui.p2extended.viewmodel

import com.cryptovpn.ui.common.repository.CryptoVpnRepository
import com.cryptovpn.ui.common.viewmodel.BaseFeatureViewModel
import com.cryptovpn.ui.p2extended.model.AddCustomTokenEvent
import com.cryptovpn.ui.p2extended.model.AddCustomTokenUiState
import com.cryptovpn.ui.p2extended.model.AddCustomTokenRouteArgs

class AddCustomTokenViewModel(
    private val repository: CryptoVpnRepository,
    private val routeArgs: AddCustomTokenRouteArgs = AddCustomTokenRouteArgs(),
) : BaseFeatureViewModel<AddCustomTokenUiState>(AddCustomTokenUiState()) {

    init {
        refresh()
    }

    fun onEvent(event: AddCustomTokenEvent) {
        when (event) {
            is AddCustomTokenEvent.FieldChanged -> {
                _uiState.value = _uiState.value.copy(
                    fields = _uiState.value.fields.map { field ->
                        if (field.key == event.key) field.copy(value = event.value) else field
                    },
                )
            }

            AddCustomTokenEvent.PrimaryActionClicked -> Unit
            AddCustomTokenEvent.SecondaryActionClicked -> Unit
            AddCustomTokenEvent.Refresh -> refresh()
        }
    }

    private fun refresh() {
        launchLoad {
            repository.getAddCustomTokenState(routeArgs)
        }
    }
}
