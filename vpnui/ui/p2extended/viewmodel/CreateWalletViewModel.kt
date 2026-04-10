package com.cryptovpn.ui.p2extended.viewmodel

import com.cryptovpn.ui.common.repository.CryptoVpnRepository
import com.cryptovpn.ui.common.viewmodel.BaseFeatureViewModel
import com.cryptovpn.ui.p2extended.model.CreateWalletEvent
import com.cryptovpn.ui.p2extended.model.CreateWalletUiState
import com.cryptovpn.ui.p2extended.model.CreateWalletRouteArgs

class CreateWalletViewModel(
    private val repository: CryptoVpnRepository,
    private val routeArgs: CreateWalletRouteArgs = CreateWalletRouteArgs(),
) : BaseFeatureViewModel<CreateWalletUiState>(CreateWalletUiState()) {

    init {
        refresh()
    }

    fun onEvent(event: CreateWalletEvent) {
        when (event) {
            is CreateWalletEvent.FieldChanged -> {
                _uiState.value = _uiState.value.copy(
                    fields = _uiState.value.fields.map { field ->
                        if (field.key == event.key) field.copy(value = event.value) else field
                    },
                )
            }

            CreateWalletEvent.PrimaryActionClicked -> Unit
            CreateWalletEvent.SecondaryActionClicked -> Unit
            CreateWalletEvent.Refresh -> refresh()
        }
    }

    private fun refresh() {
        launchLoad {
            repository.getCreateWalletState(routeArgs)
        }
    }
}
