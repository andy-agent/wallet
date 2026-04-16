package com.v2ray.ang.composeui.p1.viewmodel

import com.v2ray.ang.composeui.common.repository.CryptoVpnRepository
import com.v2ray.ang.composeui.common.viewmodel.BaseFeatureViewModel
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
            WalletPaymentConfirmEvent.PrimaryActionClicked -> refresh()
            WalletPaymentConfirmEvent.SecondaryActionClicked -> Unit
            WalletPaymentConfirmEvent.Refresh -> refresh()
        }
    }

    private fun refresh() {
        launchLoad {
            repository.getWalletPaymentConfirmState(routeArgs)
        }
    }
}
