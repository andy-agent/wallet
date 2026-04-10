package com.cryptovpn.ui.p2.viewmodel

import com.cryptovpn.ui.common.repository.CryptoVpnRepository
import com.cryptovpn.ui.common.viewmodel.BaseFeatureViewModel
import com.cryptovpn.ui.p2.model.ReceiveEvent
import com.cryptovpn.ui.p2.model.ReceiveUiState
import com.cryptovpn.ui.p2.model.ReceiveRouteArgs

class ReceiveViewModel(
    private val repository: CryptoVpnRepository,
    private val routeArgs: ReceiveRouteArgs = ReceiveRouteArgs(),
) : BaseFeatureViewModel<ReceiveUiState>(ReceiveUiState()) {

    init {
        refresh()
    }

    fun onEvent(event: ReceiveEvent) {
        when (event) {
            is ReceiveEvent.FieldChanged -> {
                _uiState.value = _uiState.value.copy(
                    fields = _uiState.value.fields.map { field ->
                        if (field.key == event.key) field.copy(value = event.value) else field
                    },
                )
            }

            ReceiveEvent.PrimaryActionClicked -> Unit
            ReceiveEvent.SecondaryActionClicked -> Unit
            ReceiveEvent.Refresh -> refresh()
        }
    }

    private fun refresh() {
        launchLoad {
            repository.getReceiveState(routeArgs)
        }
    }
}
