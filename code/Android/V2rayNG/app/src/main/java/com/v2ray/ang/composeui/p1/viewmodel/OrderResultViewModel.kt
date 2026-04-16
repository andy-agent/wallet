package com.v2ray.ang.composeui.p1.viewmodel

import com.v2ray.ang.composeui.common.repository.CryptoVpnRepository
import com.v2ray.ang.composeui.common.viewmodel.BaseFeatureViewModel
import com.v2ray.ang.composeui.p1.model.OrderResultEvent
import com.v2ray.ang.composeui.p1.model.OrderResultUiState
import com.v2ray.ang.composeui.p1.model.OrderResultRouteArgs

class OrderResultViewModel(
    private val repository: CryptoVpnRepository,
    private val routeArgs: OrderResultRouteArgs = OrderResultRouteArgs(),
) : BaseFeatureViewModel<OrderResultUiState>(OrderResultUiState()) {

    init {
        refresh()
    }

    fun onEvent(event: OrderResultEvent) {
        when (event) {
            OrderResultEvent.PrimaryActionClicked -> refresh()
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
