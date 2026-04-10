package com.cryptovpn.ui.p2extended.viewmodel

import com.cryptovpn.ui.common.repository.CryptoVpnRepository
import com.cryptovpn.ui.common.viewmodel.BaseFeatureViewModel
import com.cryptovpn.ui.p2extended.model.WalletConnectSessionEvent
import com.cryptovpn.ui.p2extended.model.WalletConnectSessionUiState
import com.cryptovpn.ui.p2extended.model.WalletConnectSessionRouteArgs

class WalletConnectSessionViewModel(
    private val repository: CryptoVpnRepository,
    private val routeArgs: WalletConnectSessionRouteArgs = WalletConnectSessionRouteArgs(),
) : BaseFeatureViewModel<WalletConnectSessionUiState>(WalletConnectSessionUiState()) {

    init {
        refresh()
    }

    fun onEvent(event: WalletConnectSessionEvent) {
        when (event) {
            is WalletConnectSessionEvent.FieldChanged -> {
                _uiState.value = _uiState.value.copy(
                    fields = _uiState.value.fields.map { field ->
                        if (field.key == event.key) field.copy(value = event.value) else field
                    },
                )
            }

            WalletConnectSessionEvent.PrimaryActionClicked -> Unit
            WalletConnectSessionEvent.SecondaryActionClicked -> Unit
            WalletConnectSessionEvent.Refresh -> refresh()
        }
    }

    private fun refresh() {
        launchLoad {
            repository.getWalletConnectSessionState(routeArgs)
        }
    }
}
