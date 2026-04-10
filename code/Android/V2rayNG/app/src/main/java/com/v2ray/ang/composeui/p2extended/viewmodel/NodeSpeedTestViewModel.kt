package com.v2ray.ang.composeui.p2extended.viewmodel

import com.v2ray.ang.composeui.common.repository.CryptoVpnRepository
import com.v2ray.ang.composeui.common.viewmodel.BaseFeatureViewModel
import com.v2ray.ang.composeui.p2extended.model.NodeSpeedTestEvent
import com.v2ray.ang.composeui.p2extended.model.NodeSpeedTestUiState
import com.v2ray.ang.composeui.p2extended.model.NodeSpeedTestRouteArgs

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
