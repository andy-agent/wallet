package com.cryptovpn.ui.p1.viewmodel

import com.cryptovpn.ui.common.repository.CryptoVpnRepository
import com.cryptovpn.ui.common.viewmodel.BaseFeatureViewModel
import com.cryptovpn.ui.p1.model.WalletPaymentConfirmEvent
import com.cryptovpn.ui.p1.model.WalletPaymentConfirmUiState
import com.cryptovpn.ui.p1.model.WalletPaymentConfirmRouteArgs

class WalletPaymentConfirmViewModel(
    private val repository: CryptoVpnRepository,
    private val routeArgs: WalletPaymentConfirmRouteArgs = WalletPaymentConfirmRouteArgs(),
) : BaseFeatureViewModel<WalletPaymentConfirmUiState>(WalletPaymentConfirmUiState()) {

    init {
        refresh()
    }

    fun onEvent(event: WalletPaymentConfirmEvent) {
        when (event) {
            is WalletPaymentConfirmEvent.FieldChanged -> {
                _uiState.value = _uiState.value.copy(
                    fields = _uiState.value.fields.map { field ->
                        if (field.key == event.key) field.copy(value = event.value) else field
                    },
                )
            }

            WalletPaymentConfirmEvent.PrimaryActionClicked -> Unit
            WalletPaymentConfirmEvent.SecondaryActionClicked -> Unit
            WalletPaymentConfirmEvent.Refresh -> refresh()
        }
    }

    private fun refresh() {
        launchLoad {
            repository.getWalletPaymentConfirmState(routeArgs)
        }
    }
}
