package com.cryptovpn.ui.p2.viewmodel

import com.cryptovpn.ui.common.repository.CryptoVpnRepository
import com.cryptovpn.ui.common.viewmodel.BaseFeatureViewModel
import com.cryptovpn.ui.p2.model.SendResultEvent
import com.cryptovpn.ui.p2.model.SendResultUiState
import com.cryptovpn.ui.p2.model.SendResultRouteArgs

class SendResultViewModel(
    private val repository: CryptoVpnRepository,
    private val routeArgs: SendResultRouteArgs = SendResultRouteArgs(),
) : BaseFeatureViewModel<SendResultUiState>(SendResultUiState()) {

    init {
        refresh()
    }

    fun onEvent(event: SendResultEvent) {
        when (event) {
            is SendResultEvent.FieldChanged -> {
                _uiState.value = _uiState.value.copy(
                    fields = _uiState.value.fields.map { field ->
                        if (field.key == event.key) field.copy(value = event.value) else field
                    },
                )
            }

            SendResultEvent.PrimaryActionClicked -> Unit
            SendResultEvent.SecondaryActionClicked -> Unit
            SendResultEvent.Refresh -> refresh()
        }
    }

    private fun refresh() {
        launchLoad {
            repository.getSendResultState(routeArgs)
        }
    }
}
