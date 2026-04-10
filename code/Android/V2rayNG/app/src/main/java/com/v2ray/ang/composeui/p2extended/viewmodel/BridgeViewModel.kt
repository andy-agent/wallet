package com.v2ray.ang.composeui.p2extended.viewmodel

import com.v2ray.ang.composeui.common.repository.CryptoVpnRepository
import com.v2ray.ang.composeui.common.viewmodel.BaseFeatureViewModel
import com.v2ray.ang.composeui.p2extended.model.BridgeEvent
import com.v2ray.ang.composeui.p2extended.model.BridgeUiState
import com.v2ray.ang.composeui.p2extended.model.BridgeRouteArgs

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
