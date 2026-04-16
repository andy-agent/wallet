package com.v2ray.ang.composeui.p1.viewmodel

import com.v2ray.ang.composeui.common.repository.CryptoVpnRepository
import com.v2ray.ang.composeui.common.viewmodel.BaseFeatureViewModel
import com.v2ray.ang.composeui.p1.model.OrderDetailEvent
import com.v2ray.ang.composeui.p1.model.OrderDetailUiState
import com.v2ray.ang.composeui.p1.model.OrderDetailRouteArgs

class OrderDetailViewModel(
    private val repository: CryptoVpnRepository,
    private val routeArgs: OrderDetailRouteArgs = OrderDetailRouteArgs(),
) : BaseFeatureViewModel<OrderDetailUiState>(OrderDetailUiState()) {

    init {
        refresh()
    }

    fun onEvent(event: OrderDetailEvent) {
        when (event) {
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
