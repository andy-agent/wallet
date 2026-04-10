package com.v2ray.ang.composeui.p2extended.viewmodel

import com.v2ray.ang.composeui.common.repository.CryptoVpnRepository
import com.v2ray.ang.composeui.common.viewmodel.BaseFeatureViewModel
import com.v2ray.ang.composeui.p2extended.model.WalletManagerEvent
import com.v2ray.ang.composeui.p2extended.model.WalletManagerUiState
import com.v2ray.ang.composeui.p2extended.model.WalletManagerRouteArgs

class WalletManagerViewModel(
    private val repository: CryptoVpnRepository,
    private val routeArgs: WalletManagerRouteArgs = WalletManagerRouteArgs(),
) : BaseFeatureViewModel<WalletManagerUiState>(WalletManagerUiState()) {

    init {
        refresh()
    }

    fun onEvent(event: WalletManagerEvent) {
        when (event) {
            is WalletManagerEvent.FieldChanged -> {
                _uiState.value = _uiState.value.copy(
                    fields = _uiState.value.fields.map { field ->
                        if (field.key == event.key) field.copy(value = event.value) else field
                    },
                )
            }

            WalletManagerEvent.PrimaryActionClicked -> Unit
            WalletManagerEvent.SecondaryActionClicked -> Unit
            WalletManagerEvent.Refresh -> refresh()
        }
    }

    private fun refresh() {
        launchLoad {
            repository.getWalletManagerState(routeArgs)
        }
    }
}
