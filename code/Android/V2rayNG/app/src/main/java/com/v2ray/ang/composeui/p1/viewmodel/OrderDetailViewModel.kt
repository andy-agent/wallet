package com.v2ray.ang.composeui.p1.viewmodel

import com.v2ray.ang.composeui.common.repository.CryptoVpnRepository
import com.v2ray.ang.composeui.common.viewmodel.BaseFeatureViewModel
import com.v2ray.ang.composeui.p1.model.P1ScreenState
import com.v2ray.ang.composeui.p1.model.P1StateInfo
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
            is OrderDetailEvent.FieldChanged -> Unit

            OrderDetailEvent.PrimaryActionClicked -> Unit
            OrderDetailEvent.SecondaryActionClicked -> Unit
            OrderDetailEvent.Refresh -> refresh()
        }
    }

    private fun refresh() {
        _uiState.value = _uiState.value.copy(
            stateInfo = P1StateInfo(P1ScreenState.Loading, message = "正在读取真实订单详情..."),
        )
        launchLoad {
            repository.getOrderDetailState(routeArgs)
        }
    }
}
