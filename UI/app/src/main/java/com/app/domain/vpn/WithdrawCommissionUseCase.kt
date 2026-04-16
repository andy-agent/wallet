package com.app.domain.vpn

import com.app.AppGraph
import com.app.data.repository.VpnRepository

class WithdrawCommissionUseCase(
    private val repository: VpnRepository = AppGraph.vpnRepository,
) {
    suspend operator fun invoke(amountUsd: Double, address: String) = repository.withdrawCommission(amountUsd, address)
}
