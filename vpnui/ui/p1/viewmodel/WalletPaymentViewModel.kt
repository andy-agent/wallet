package com.cryptovpn.ui.p1.viewmodel

import com.cryptovpn.ui.common.repository.CryptoVpnRepository
import com.cryptovpn.ui.common.viewmodel.BaseFeatureViewModel
import com.cryptovpn.ui.p1.model.WalletPaymentEvent
import com.cryptovpn.ui.p1.model.WalletPaymentUiState

class WalletPaymentViewModel(
    private val repository: CryptoVpnRepository,
) : BaseFeatureViewModel<WalletPaymentUiState>(WalletPaymentUiState()) {

    init {
        refresh()
    }

    fun onEvent(event: WalletPaymentEvent) {
        when (event) {
            is WalletPaymentEvent.FieldChanged -> {
                _uiState.value = _uiState.value.copy(
                    fields = _uiState.value.fields.map { field ->
                        if (field.key == event.key) field.copy(value = event.value) else field
                    },
                )
            }

            WalletPaymentEvent.PrimaryActionClicked -> Unit
            WalletPaymentEvent.SecondaryActionClicked -> Unit
            WalletPaymentEvent.Refresh -> refresh()
        }
    }

    private fun refresh() {
        launchLoad {
            repository.getWalletPaymentState()
        }
    }
}
