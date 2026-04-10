package com.cryptovpn.ui.p1.viewmodel

import com.cryptovpn.ui.common.repository.CryptoVpnRepository
import com.cryptovpn.ui.common.viewmodel.BaseFeatureViewModel
import com.cryptovpn.ui.p1.model.OrderListEvent
import com.cryptovpn.ui.p1.model.OrderListUiState

class OrderListViewModel(
    private val repository: CryptoVpnRepository,
) : BaseFeatureViewModel<OrderListUiState>(OrderListUiState()) {

    init {
        refresh()
    }

    fun onEvent(event: OrderListEvent) {
        when (event) {
            is OrderListEvent.FieldChanged -> {
                _uiState.value = _uiState.value.copy(
                    fields = _uiState.value.fields.map { field ->
                        if (field.key == event.key) field.copy(value = event.value) else field
                    },
                )
            }

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
