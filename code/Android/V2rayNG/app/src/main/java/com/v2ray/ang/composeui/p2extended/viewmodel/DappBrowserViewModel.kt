package com.v2ray.ang.composeui.p2extended.viewmodel

import com.v2ray.ang.composeui.common.repository.CryptoVpnRepository
import com.v2ray.ang.composeui.common.viewmodel.BaseFeatureViewModel
import com.v2ray.ang.composeui.p2extended.model.DappBrowserEvent
import com.v2ray.ang.composeui.p2extended.model.DappBrowserUiState
import com.v2ray.ang.composeui.p2extended.model.DappBrowserRouteArgs

class DappBrowserViewModel(
    private val repository: CryptoVpnRepository,
    private val routeArgs: DappBrowserRouteArgs = DappBrowserRouteArgs(),
) : BaseFeatureViewModel<DappBrowserUiState>(DappBrowserUiState()) {

    init {
        refresh()
    }

    fun onEvent(event: DappBrowserEvent) {
        when (event) {
            is DappBrowserEvent.FieldChanged -> {
                _uiState.value = _uiState.value.copy(
                    fields = _uiState.value.fields.map { field ->
                        if (field.key == event.key) field.copy(value = event.value) else field
                    },
                )
            }

            DappBrowserEvent.PrimaryActionClicked -> Unit
            DappBrowserEvent.SecondaryActionClicked -> Unit
            DappBrowserEvent.Refresh -> refresh()
        }
    }

    private fun refresh() {
        launchLoad {
            repository.getDappBrowserState(routeArgs)
        }
    }
}
