package com.v2ray.ang.composeui.p2extended.viewmodel

import com.v2ray.ang.composeui.common.repository.CryptoVpnRepository
import com.v2ray.ang.composeui.common.viewmodel.BaseFeatureViewModel
import com.v2ray.ang.composeui.p2extended.model.WalletConnectSessionEvent
import com.v2ray.ang.composeui.p2extended.model.WalletConnectSessionUiState
import com.v2ray.ang.composeui.p2extended.model.WalletConnectSessionRouteArgs

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
