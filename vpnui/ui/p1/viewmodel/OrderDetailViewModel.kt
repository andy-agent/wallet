package com.cryptovpn.ui.p1.viewmodel

import com.cryptovpn.ui.common.repository.CryptoVpnRepository
import com.cryptovpn.ui.common.viewmodel.BaseFeatureViewModel
import com.cryptovpn.ui.p1.model.OrderDetailEvent
import com.cryptovpn.ui.p1.model.OrderDetailUiState
import com.cryptovpn.ui.p1.model.OrderDetailRouteArgs

class OrderDetailViewModel(
    private val repository: CryptoVpnRepository,
    private val routeArgs: OrderDetailRouteArgs = OrderDetailRouteArgs(),
) : BaseFeatureViewModel<OrderDetailUiState>(OrderDetailUiState()) {

    init {
        refresh()
    }

    fun onEvent(event: OrderDetailEvent) {
        when (event) {
            is OrderDetailEvent.FieldChanged -> {
                _uiState.value = _uiState.value.copy(
                    fields = _uiState.value.fields.map { field ->
                        if (field.key == event.key) field.copy(value = event.value) else field
                    },
                )
            }

            OrderDetailEvent.PrimaryActionClicked -> Unit
            OrderDetailEvent.SecondaryActionClicked -> Unit
            OrderDetailEvent.Refresh -> refresh()
        }
    }

    private fun refresh() {
        launchLoad {
            repository.getOrderDetailState(routeArgs)
        }
    }
}
