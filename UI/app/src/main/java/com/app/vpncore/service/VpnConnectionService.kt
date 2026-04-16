package com.app.vpncore.service

import android.net.VpnService
import com.app.vpncore.model.VpnNode
import kotlinx.coroutines.delay

class VpnConnectionService : VpnService() {
    companion object {
        suspend fun connect(node: VpnNode): Result<Unit> {
            delay(600)
            return if (node.host.isNotBlank()) Result.success(Unit) else Result.failure(IllegalStateException("invalid node"))
        }

        suspend fun disconnect(): Result<Unit> {
            delay(240)
            return Result.success(Unit)
        }
    }
}
