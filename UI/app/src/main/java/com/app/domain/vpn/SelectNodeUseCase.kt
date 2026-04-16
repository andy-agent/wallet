package com.app.domain.vpn

import com.app.AppGraph
import com.app.data.repository.VpnRepository

class SelectNodeUseCase(
    private val repository: VpnRepository = AppGraph.vpnRepository,
) {
    suspend operator fun invoke(nodeId: String) = repository.selectNode(nodeId)
}
