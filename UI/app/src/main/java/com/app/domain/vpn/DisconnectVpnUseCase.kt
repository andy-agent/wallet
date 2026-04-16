package com.app.domain.vpn

import com.app.AppGraph
import com.app.data.repository.VpnRepository

class DisconnectVpnUseCase(
    private val repository: VpnRepository = AppGraph.vpnRepository,
) {
    operator fun invoke() = repository.disconnect()
}
