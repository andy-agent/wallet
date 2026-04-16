package com.app.vpncore.model

data class VpnConfig(
    val selectedNodeId: String? = null,
    val subscriptionUrl: String = "mock://subscription/vpn01-main",
    val lastUpdatedAt: Long = System.currentTimeMillis(),
)
