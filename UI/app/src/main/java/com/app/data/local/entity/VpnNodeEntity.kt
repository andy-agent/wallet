package com.app.data.local.entity

import com.app.vpncore.model.VpnNode
import com.app.vpncore.model.VpnProtocol

data class VpnNodeEntity(
    val id: String,
    val name: String,
    val protocol: String,
    val host: String,
    val port: Int,
    val country: String,
    val latencyMs: Int,
    val premium: Boolean,
    val rawConfig: String,
)

fun VpnNodeEntity.toModel() = VpnNode(id, name, VpnProtocol.valueOf(protocol), host, port, country, latencyMs, premium, rawConfig)
fun VpnNode.toEntity() = VpnNodeEntity(id, name, protocol.name, host, port, country, latencyMs, isPremium, rawConfig)
