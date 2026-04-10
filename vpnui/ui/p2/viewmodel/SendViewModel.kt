package com.cryptovpn.ui.p2.viewmodel

import com.cryptovpn.ui.common.repository.CryptoVpnRepository
import com.cryptovpn.ui.common.viewmodel.BaseFeatureViewModel
import com.cryptovpn.ui.p2.model.SendEvent
import com.cryptovpn.ui.p2.model.SendUiState
import com.cryptovpn.ui.p2.model.SendRouteArgs

class SendViewModel(
    private val repository: CryptoVpnRepository,
    private val routeArgs: SendRouteArgs = SendRouteArgs(),
) : BaseFeatureViewModel<SendUiState>(SendUiState()) {

    init {
        refresh()
    }

    fun onEvent(event: SendEvent) {
        when (event) {
            is SendEvent.FieldChanged -> {
                _uiState.value = _uiState.value.copy(
                    fields = _uiState.value.fields.map { field ->
                        if (field.key == event.key) field.copy(value = event.value) else field
                    },
                )
            }

            SendEvent.PrimaryActionClicked -> Unit
            SendEvent.SecondaryActionClicked -> Unit
            SendEvent.Refresh -> refresh()
        }
    }

    private fun refresh() {
        launchLoad {
            repository.getSendState(routeArgs)
        }
    }
}
