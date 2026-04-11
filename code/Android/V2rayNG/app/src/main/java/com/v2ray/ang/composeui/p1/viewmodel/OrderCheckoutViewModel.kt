package com.v2ray.ang.composeui.p1.viewmodel

import com.v2ray.ang.composeui.common.repository.CryptoVpnRepository
import com.v2ray.ang.composeui.common.viewmodel.BaseFeatureViewModel
import com.v2ray.ang.composeui.p1.model.OrderCheckoutEvent
import com.v2ray.ang.composeui.p1.model.OrderCheckoutUiState
import com.v2ray.ang.composeui.p1.model.OrderCheckoutRouteArgs
import com.v2ray.ang.composeui.p1.model.P1ScreenState
import com.v2ray.ang.composeui.p1.model.P1StateInfo

class OrderCheckoutViewModel(
    private val repository: CryptoVpnRepository,
    private val routeArgs: OrderCheckoutRouteArgs = OrderCheckoutRouteArgs(),
) : BaseFeatureViewModel<OrderCheckoutUiState>(OrderCheckoutUiState()) {

    init {
        refresh()
    }

    fun onEvent(event: OrderCheckoutEvent) {
        when (event) {
            is OrderCheckoutEvent.FieldChanged -> Unit

            OrderCheckoutEvent.PrimaryActionClicked -> Unit
            OrderCheckoutEvent.SecondaryActionClicked -> Unit
            OrderCheckoutEvent.Refresh -> refresh()
        }
    }

    private fun refresh() {
        _uiState.value = _uiState.value.copy(
            stateInfo = P1StateInfo(P1ScreenState.Loading, message = "正在生成真实订单..."),
        )
        launchLoad {
            repository.getOrderCheckoutState(routeArgs)
        }
    }
}
