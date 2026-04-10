package com.cryptovpn.ui.p2extended.viewmodel

import com.cryptovpn.ui.common.repository.CryptoVpnRepository
import com.cryptovpn.ui.common.viewmodel.BaseFeatureViewModel
import com.cryptovpn.ui.p2extended.model.ChainManagerEvent
import com.cryptovpn.ui.p2extended.model.ChainManagerUiState
import com.cryptovpn.ui.p2extended.model.ChainManagerRouteArgs

class ChainManagerViewModel(
    private val repository: CryptoVpnRepository,
    private val routeArgs: ChainManagerRouteArgs = ChainManagerRouteArgs(),
) : BaseFeatureViewModel<ChainManagerUiState>(ChainManagerUiState()) {

    init {
        refresh()
    }

    fun onEvent(event: ChainManagerEvent) {
        when (event) {
            is ChainManagerEvent.FieldChanged -> {
                _uiState.value = _uiState.value.copy(
                    fields = _uiState.value.fields.map { field ->
                        if (field.key == event.key) field.copy(value = event.value) else field
                    },
                )
            }

            ChainManagerEvent.PrimaryActionClicked -> Unit
            ChainManagerEvent.SecondaryActionClicked -> Unit
            ChainManagerEvent.Refresh -> refresh()
        }
    }

    private fun refresh() {
        launchLoad {
            repository.getChainManagerState(routeArgs)
        }
    }
}
