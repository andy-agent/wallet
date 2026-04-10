package com.v2ray.ang.composeui.p2.viewmodel

import com.v2ray.ang.composeui.common.repository.CryptoVpnRepository
import com.v2ray.ang.composeui.common.viewmodel.BaseFeatureViewModel
import com.v2ray.ang.composeui.p2.model.SendResultEvent
import com.v2ray.ang.composeui.p2.model.SendResultUiState
import com.v2ray.ang.composeui.p2.model.SendResultRouteArgs

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
