package com.v2ray.ang.composeui.p1.viewmodel

import androidx.lifecycle.viewModelScope
import com.v2ray.ang.composeui.common.repository.CryptoVpnRepository
import com.v2ray.ang.composeui.common.viewmodel.BaseFeatureViewModel
import com.v2ray.ang.composeui.p1.model.OrderCheckoutEvent
import com.v2ray.ang.composeui.p1.model.OrderCheckoutUiState
import com.v2ray.ang.composeui.p1.model.OrderCheckoutRouteArgs
import kotlinx.coroutines.launch

class OrderCheckoutViewModel(
    private val repository: CryptoVpnRepository,
    private val routeArgs: OrderCheckoutRouteArgs = OrderCheckoutRouteArgs(),
) : BaseFeatureViewModel<OrderCheckoutUiState>(OrderCheckoutUiState()) {

    init {
        refresh()
    }

    fun onEvent(event: OrderCheckoutEvent) {
        when (event) {
            OrderCheckoutEvent.CreateOrderClicked -> createOrder()
            is OrderCheckoutEvent.PayerWalletSelected -> {
                _uiState.value = _uiState.value.copy(
                    selectedPayerWalletId = event.walletId,
                    selectedPayerChainAccountId = event.chainAccountId,
                    payerWalletOptions = _uiState.value.payerWalletOptions.map { option ->
                        option.copy(
                            selected = option.walletId == event.walletId &&
                                option.chainAccountId == event.chainAccountId,
                        )
                    },
                )
            }
            OrderCheckoutEvent.PrimaryActionClicked -> Unit
            OrderCheckoutEvent.SecondaryActionClicked -> Unit
            OrderCheckoutEvent.Refresh -> refresh()
        }
    }

    private fun refresh() {
        viewModelScope.launch {
            repository.getCachedOrderCheckoutState(currentRouteArgs())?.let { cached ->
                _uiState.value = cached
            }
            _uiState.value = repository.refreshOrderCheckoutState(currentRouteArgs())
        }
    }

    private fun createOrder() {
        val current = _uiState.value
        _uiState.value = current.copy(
            summary = "正在创建订单",
            screenState = current.screenState.copy(
                isLoading = true,
                errorMessage = null,
                emptyMessage = null,
                unavailableMessage = null,
            ),
        )
        launchLoad {
            repository.getOrderCheckoutState(currentRouteArgs())
        }
    }

    private fun currentRouteArgs(): OrderCheckoutRouteArgs {
        val current = _uiState.value
        return routeArgs.copy(
            assetCode = current.assetCode.ifBlank { routeArgs.assetCode },
            networkCode = current.networkCode.ifBlank { routeArgs.networkCode },
            payerWalletId = current.selectedPayerWalletId ?: routeArgs.payerWalletId,
            payerChainAccountId = current.selectedPayerChainAccountId ?: routeArgs.payerChainAccountId,
        )
    }
}
