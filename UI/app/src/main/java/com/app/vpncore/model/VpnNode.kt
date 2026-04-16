package com.app.vpncore.model

data class VpnNode(
    val id: String,
    val name: String,
    val protocol: VpnProtocol,
    val host: String,
    val port: Int,
    val country: String,
    val latencyMs: Int,
    val isPremium: Boolean,
    val rawConfig: String,
)
