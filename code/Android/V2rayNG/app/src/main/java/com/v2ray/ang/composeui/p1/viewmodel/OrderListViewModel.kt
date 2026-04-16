package com.v2ray.ang.composeui.p1.viewmodel

import com.v2ray.ang.composeui.common.repository.CryptoVpnRepository
import com.v2ray.ang.composeui.common.viewmodel.BaseFeatureViewModel
import com.v2ray.ang.composeui.p1.model.OrderListEvent
import com.v2ray.ang.composeui.p1.model.OrderListUiState

class OrderListViewModel(
    private val repository: CryptoVpnRepository,
) : BaseFeatureViewModel<OrderListUiState>(OrderListUiState()) {

    init {
        refresh()
    }

    fun onEvent(event: OrderListEvent) {
        when (event) {
            OrderListEvent.PrimaryActionClicked -> Unit
            OrderListEvent.SecondaryActionClicked -> Unit
            OrderListEvent.Refresh -> refresh()
        }
    }

    private fun refresh() {
        launchLoad {
            repository.getOrderListState()
        }
    }
}
