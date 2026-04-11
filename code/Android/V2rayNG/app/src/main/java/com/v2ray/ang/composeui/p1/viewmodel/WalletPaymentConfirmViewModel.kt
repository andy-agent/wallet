package com.v2ray.ang.composeui.p1.viewmodel

import com.v2ray.ang.composeui.common.repository.CryptoVpnRepository
import com.v2ray.ang.composeui.common.viewmodel.BaseFeatureViewModel
import com.v2ray.ang.composeui.p1.model.P1ScreenState
import com.v2ray.ang.composeui.p1.model.P1StateInfo
import com.v2ray.ang.composeui.p1.model.WalletPaymentConfirmEvent
import com.v2ray.ang.composeui.p1.model.WalletPaymentConfirmUiState
import com.v2ray.ang.composeui.p1.model.WalletPaymentConfirmRouteArgs

class WalletPaymentConfirmViewModel(
    private val repository: CryptoVpnRepository,
    private val routeArgs: WalletPaymentConfirmRouteArgs = WalletPaymentConfirmRouteArgs(),
) : BaseFeatureViewModel<WalletPaymentConfirmUiState>(WalletPaymentConfirmUiState()) {

    init {
        refresh()
    }

    fun onEvent(event: WalletPaymentConfirmEvent) {
        when (event) {
            is WalletPaymentConfirmEvent.FieldChanged -> Unit

            WalletPaymentConfirmEvent.PrimaryActionClicked -> Unit
            WalletPaymentConfirmEvent.SecondaryActionClicked -> Unit
            WalletPaymentConfirmEvent.Refresh -> refresh()
        }
    }

    private fun refresh() {
        _uiState.value = _uiState.value.copy(
            stateInfo = P1StateInfo(P1ScreenState.Loading, message = "正在读取真实支付确认..."),
        )
        launchLoad {
            repository.getWalletPaymentConfirmState(routeArgs)
        }
    }
}
