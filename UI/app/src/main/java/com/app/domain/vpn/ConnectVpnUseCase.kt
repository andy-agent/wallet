package com.app.domain.vpn

import com.app.AppGraph
import com.app.data.repository.VpnRepository

class ConnectVpnUseCase(
    private val repository: VpnRepository = AppGraph.vpnRepository,
) {
    operator fun invoke() = repository.connect()
}
