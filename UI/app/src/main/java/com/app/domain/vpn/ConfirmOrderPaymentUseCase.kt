package com.app.domain.vpn

import com.app.AppGraph
import com.app.data.repository.VpnRepository

class ConfirmOrderPaymentUseCase(
    private val repository: VpnRepository = AppGraph.vpnRepository,
) {
    suspend operator fun invoke(orderId: String, paySymbol: String = "USDT") = repository.confirmOrderPayment(orderId, paySymbol)
}
