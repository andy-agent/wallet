package com.cryptovpn.ui.p1.viewmodel

import com.cryptovpn.ui.common.repository.CryptoVpnRepository
import com.cryptovpn.ui.common.viewmodel.BaseFeatureViewModel
import com.cryptovpn.ui.p1.model.OrderResultEvent
import com.cryptovpn.ui.p1.model.OrderResultUiState
import com.cryptovpn.ui.p1.model.OrderResultRouteArgs

class OrderResultViewModel(
    private val repository: CryptoVpnRepository,
    private val routeArgs: OrderResultRouteArgs = OrderResultRouteArgs(),
) : BaseFeatureViewModel<OrderResultUiState>(OrderResultUiState()) {

    init {
        refresh()
    }

    fun onEvent(event: OrderResultEvent) {
        when (event) {
            is OrderResultEvent.FieldChanged -> {
                _uiState.value = _uiState.value.copy(
                    fields = _uiState.value.fields.map { field ->
                        if (field.key == event.key) field.copy(value = event.value) else field
                    },
                )
            }

            OrderResultEvent.PrimaryActionClicked -> Unit
            OrderResultEvent.SecondaryActionClicked -> Unit
            OrderResultEvent.Refresh -> refresh()
        }
    }

    private fun refresh() {
        launchLoad {
            repository.getOrderResultState(routeArgs)
        }
    }
}
