package com.v2ray.ang.composeui.p1.viewmodel

import com.v2ray.ang.composeui.common.repository.CryptoVpnRepository
import com.v2ray.ang.composeui.common.viewmodel.BaseFeatureViewModel
import com.v2ray.ang.composeui.p1.model.OrderCheckoutEvent
import com.v2ray.ang.composeui.p1.model.OrderCheckoutUiState
import com.v2ray.ang.composeui.p1.model.OrderCheckoutRouteArgs

class OrderCheckoutViewModel(
    private val repository: CryptoVpnRepository,
    private val routeArgs: OrderCheckoutRouteArgs = OrderCheckoutRouteArgs(),
) : BaseFeatureViewModel<OrderCheckoutUiState>(OrderCheckoutUiState()) {

    init {
        refresh()
    }

    fun onEvent(event: OrderCheckoutEvent) {
        when (event) {
            is OrderCheckoutEvent.FieldChanged -> {
                _uiState.value = _uiState.value.copy(
                    fields = _uiState.value.fields.map { field ->
                        if (field.key == event.key) field.copy(value = event.value) else field
                    },
                )
            }

            OrderCheckoutEvent.PrimaryActionClicked -> Unit
            OrderCheckoutEvent.SecondaryActionClicked -> Unit
            OrderCheckoutEvent.Refresh -> refresh()
        }
    }

    private fun refresh() {
        launchLoad {
            repository.getOrderCheckoutState(routeArgs)
        }
    }
}
