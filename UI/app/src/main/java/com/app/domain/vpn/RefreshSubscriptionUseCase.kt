package com.app.domain.vpn

import com.app.AppGraph
import com.app.data.repository.VpnRepository

class RefreshSubscriptionUseCase(
    private val repository: VpnRepository = AppGraph.vpnRepository,
) {
    suspend operator fun invoke() = repository.refreshSubscription()
}
