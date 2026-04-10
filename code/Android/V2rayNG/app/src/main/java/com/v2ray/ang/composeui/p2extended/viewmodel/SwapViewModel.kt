package com.v2ray.ang.composeui.p2extended.viewmodel

import com.v2ray.ang.composeui.common.repository.CryptoVpnRepository
import com.v2ray.ang.composeui.common.viewmodel.BaseFeatureViewModel
import com.v2ray.ang.composeui.p2extended.model.SwapEvent
import com.v2ray.ang.composeui.p2extended.model.SwapUiState
import com.v2ray.ang.composeui.p2extended.model.SwapRouteArgs

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
