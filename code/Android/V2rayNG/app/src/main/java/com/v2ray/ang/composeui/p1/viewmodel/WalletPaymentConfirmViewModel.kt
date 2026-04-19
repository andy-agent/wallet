package com.v2ray.ang.composeui.p1.viewmodel

import androidx.lifecycle.viewModelScope
import com.v2ray.ang.composeui.common.repository.CryptoVpnRepository
import com.v2ray.ang.composeui.common.viewmodel.BaseFeatureViewModel
import com.v2ray.ang.composeui.p1.model.WalletPaymentConfirmEvent
import com.v2ray.ang.composeui.p1.model.WalletPaymentConfirmUiState
import com.v2ray.ang.composeui.p1.model.WalletPaymentConfirmRouteArgs
import kotlinx.coroutines.launch

class WalletPaymentConfirmViewModel(
    private val repository: CryptoVpnRepository,
    private val routeArgs: WalletPaymentConfirmRouteArgs = WalletPaymentConfirmRouteArgs(),
) : BaseFeatureViewModel<WalletPaymentConfirmUiState>(WalletPaymentConfirmUiState()) {

    init {
        refresh()
    }

    fun onEvent(event: WalletPaymentConfirmEvent) {
        when (event) {
            WalletPaymentConfirmEvent.PrimaryActionClicked -> refresh()
            WalletPaymentConfirmEvent.SecondaryActionClicked -> Unit
            is WalletPaymentConfirmEvent.PayerWalletSelected -> {
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
            WalletPaymentConfirmEvent.Refresh -> refresh()
        }
    }

    fun submitPayment(
        onSuccess: () -> Unit,
        onError: (String) -> Unit,
    ) {
        val walletId = _uiState.value.selectedPayerWalletId
        val chainAccountId = _uiState.value.selectedPayerChainAccountId
        val orderNo = routeArgs.orderId
        if (walletId.isNullOrBlank() || chainAccountId.isNullOrBlank()) {
            onError("请选择付款钱包")
            return
        }
        viewModelScope.launch {
            val result = repository.submitWalletOrderPayment(orderNo, walletId, chainAccountId)
            if (result.success) {
                refresh()
                onSuccess()
            } else {
                onError(result.errorMessage ?: "钱包支付失败")
            }
        }
    }

    private fun refresh() {
        launchLoad {
            repository.getWalletPaymentConfirmState(routeArgs)
        }
    }
}
