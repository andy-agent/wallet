package com.app.vpncore.model

sealed interface VpnState {
    data object Disconnected : VpnState
    data class Connecting(val nodeName: String) : VpnState
    data class Connected(val nodeName: String, val connectedAt: Long, val downloadKbps: Double, val uploadKbps: Double) : VpnState
    data object Disconnecting : VpnState
    data class Error(val message: String) : VpnState
}
