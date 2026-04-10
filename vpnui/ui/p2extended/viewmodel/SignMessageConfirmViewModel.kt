package com.cryptovpn.ui.p2extended.viewmodel

import com.cryptovpn.ui.common.repository.CryptoVpnRepository
import com.cryptovpn.ui.common.viewmodel.BaseFeatureViewModel
import com.cryptovpn.ui.p2extended.model.SignMessageConfirmEvent
import com.cryptovpn.ui.p2extended.model.SignMessageConfirmUiState
import com.cryptovpn.ui.p2extended.model.SignMessageConfirmRouteArgs

class SignMessageConfirmViewModel(
    private val repository: CryptoVpnRepository,
    private val routeArgs: SignMessageConfirmRouteArgs = SignMessageConfirmRouteArgs(),
) : BaseFeatureViewModel<SignMessageConfirmUiState>(SignMessageConfirmUiState()) {

    init {
        refresh()
    }

    fun onEvent(event: SignMessageConfirmEvent) {
        when (event) {
            is SignMessageConfirmEvent.FieldChanged -> {
                _uiState.value = _uiState.value.copy(
                    fields = _uiState.value.fields.map { field ->
                        if (field.key == event.key) field.copy(value = event.value) else field
                    },
                )
            }

            SignMessageConfirmEvent.PrimaryActionClicked -> Unit
            SignMessageConfirmEvent.SecondaryActionClicked -> Unit
            SignMessageConfirmEvent.Refresh -> refresh()
        }
    }

    private fun refresh() {
        launchLoad {
            repository.getSignMessageConfirmState(routeArgs)
        }
    }
}
