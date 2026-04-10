package com.cryptovpn.ui.p2extended.viewmodel

import com.cryptovpn.ui.common.repository.CryptoVpnRepository
import com.cryptovpn.ui.common.viewmodel.BaseFeatureViewModel
import com.cryptovpn.ui.p2extended.model.BridgeEvent
import com.cryptovpn.ui.p2extended.model.BridgeUiState
import com.cryptovpn.ui.p2extended.model.BridgeRouteArgs

class BridgeViewModel(
    private val repository: CryptoVpnRepository,
    private val routeArgs: BridgeRouteArgs = BridgeRouteArgs(),
) : BaseFeatureViewModel<BridgeUiState>(BridgeUiState()) {

    init {
        refresh()
    }

    fun onEvent(event: BridgeEvent) {
        when (event) {
            is BridgeEvent.FieldChanged -> {
                _uiState.value = _uiState.value.copy(
                    fields = _uiState.value.fields.map { field ->
                        if (field.key == event.key) field.copy(value = event.value) else field
                    },
                )
            }

            BridgeEvent.PrimaryActionClicked -> Unit
            BridgeEvent.SecondaryActionClicked -> Unit
            BridgeEvent.Refresh -> refresh()
        }
    }

    private fun refresh() {
        launchLoad {
            repository.getBridgeState(routeArgs)
        }
    }
}
