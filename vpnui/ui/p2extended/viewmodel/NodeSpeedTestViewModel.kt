package com.cryptovpn.ui.p2extended.viewmodel

import com.cryptovpn.ui.common.repository.CryptoVpnRepository
import com.cryptovpn.ui.common.viewmodel.BaseFeatureViewModel
import com.cryptovpn.ui.p2extended.model.NodeSpeedTestEvent
import com.cryptovpn.ui.p2extended.model.NodeSpeedTestUiState
import com.cryptovpn.ui.p2extended.model.NodeSpeedTestRouteArgs

class NodeSpeedTestViewModel(
    private val repository: CryptoVpnRepository,
    private val routeArgs: NodeSpeedTestRouteArgs = NodeSpeedTestRouteArgs(),
) : BaseFeatureViewModel<NodeSpeedTestUiState>(NodeSpeedTestUiState()) {

    init {
        refresh()
    }

    fun onEvent(event: NodeSpeedTestEvent) {
        when (event) {
            is NodeSpeedTestEvent.FieldChanged -> {
                _uiState.value = _uiState.value.copy(
                    fields = _uiState.value.fields.map { field ->
                        if (field.key == event.key) field.copy(value = event.value) else field
                    },
                )
            }

            NodeSpeedTestEvent.PrimaryActionClicked -> Unit
            NodeSpeedTestEvent.SecondaryActionClicked -> Unit
            NodeSpeedTestEvent.Refresh -> refresh()
        }
    }

    private fun refresh() {
        launchLoad {
            repository.getNodeSpeedTestState(routeArgs)
        }
    }
}
