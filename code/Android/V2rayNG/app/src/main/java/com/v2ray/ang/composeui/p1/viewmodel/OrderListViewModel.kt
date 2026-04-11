package com.v2ray.ang.composeui.p1.viewmodel

import com.v2ray.ang.composeui.common.repository.CryptoVpnRepository
import com.v2ray.ang.composeui.common.viewmodel.BaseFeatureViewModel
import com.v2ray.ang.composeui.common.model.FeatureField
import com.v2ray.ang.composeui.p1.model.P1ScreenState
import com.v2ray.ang.composeui.p1.model.P1StateInfo
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
            is OrderListEvent.FieldChanged -> {
                _uiState.value = _uiState.value.copy(
                    searchField = FeatureField(
                        key = _uiState.value.searchField.key,
                        label = _uiState.value.searchField.label,
                        value = event.value,
                        supportingText = _uiState.value.searchField.supportingText,
                    ),
                )
            }

            OrderListEvent.PrimaryActionClicked -> Unit
            OrderListEvent.SecondaryActionClicked -> Unit
            OrderListEvent.Refresh -> refresh()
        }
    }

    private fun refresh() {
        _uiState.value = _uiState.value.copy(
            stateInfo = P1StateInfo(P1ScreenState.Loading, message = "正在读取真实订单列表..."),
        )
        launchLoad {
            repository.getOrderListState()
        }
    }
}
