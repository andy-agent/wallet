package com.v2ray.ang.composeui.p1.viewmodel

import com.v2ray.ang.composeui.common.repository.CryptoVpnRepository
import com.v2ray.ang.composeui.common.viewmodel.BaseFeatureViewModel
import com.v2ray.ang.composeui.p1.model.P1ScreenState
import com.v2ray.ang.composeui.p1.model.P1StateInfo
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
            is OrderResultEvent.FieldChanged -> Unit

            OrderResultEvent.PrimaryActionClicked -> Unit
            OrderResultEvent.SecondaryActionClicked -> Unit
            OrderResultEvent.Refresh -> refresh()
        }
    }

    private fun refresh() {
        _uiState.value = _uiState.value.copy(
            stateInfo = P1StateInfo(P1ScreenState.Loading, message = "正在刷新真实订单状态..."),
        )
        launchLoad {
            repository.getOrderResultState(routeArgs)
        }
    }
}
