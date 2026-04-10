package com.cryptovpn.ui.p2extended.viewmodel

import com.cryptovpn.ui.common.repository.CryptoVpnRepository
import com.cryptovpn.ui.common.viewmodel.BaseFeatureViewModel
import com.cryptovpn.ui.p2extended.model.SwapEvent
import com.cryptovpn.ui.p2extended.model.SwapUiState
import com.cryptovpn.ui.p2extended.model.SwapRouteArgs

class SwapViewModel(
    private val repository: CryptoVpnRepository,
    private val routeArgs: SwapRouteArgs = SwapRouteArgs(),
) : BaseFeatureViewModel<SwapUiState>(SwapUiState()) {

    init {
        refresh()
    }

    fun onEvent(event: SwapEvent) {
        when (event) {
            is SwapEvent.FieldChanged -> {
                _uiState.value = _uiState.value.copy(
                    fields = _uiState.value.fields.map { field ->
                        if (field.key == event.key) field.copy(value = event.value) else field
                    },
                )
            }

            SwapEvent.PrimaryActionClicked -> Unit
            SwapEvent.SecondaryActionClicked -> Unit
            SwapEvent.Refresh -> refresh()
        }
    }

    private fun refresh() {
        launchLoad {
            repository.getSwapState(routeArgs)
        }
    }
}
