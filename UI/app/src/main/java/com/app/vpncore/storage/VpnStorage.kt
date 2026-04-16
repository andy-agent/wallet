package com.app.vpncore.storage

import com.app.data.local.prefs.VpnPreferences
import com.app.vpncore.model.VpnConfig
import com.app.vpncore.model.VpnNode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class VpnStorage(
    private val preferences: VpnPreferences,
) {
    private val _nodes = MutableStateFlow<List<VpnNode>>(emptyList())
    val nodes: StateFlow<List<VpnNode>> = _nodes.asStateFlow()

    private val _config = MutableStateFlow(
        VpnConfig(
            selectedNodeId = preferences.selectedNodeId,
            subscriptionUrl = preferences.subscriptionUrl,
            lastUpdatedAt = preferences.lastUpdatedAt,
        ),
    )
    val config: StateFlow<VpnConfig> = _config.asStateFlow()

    fun replaceNodes(items: List<VpnNode>) {
        _nodes.value = items
    }

    fun selectNode(nodeId: String) {
        preferences.selectedNodeId = nodeId
        _config.value = _config.value.copy(selectedNodeId = nodeId)
    }

    fun updateSubscription(url: String, lastUpdatedAt: Long) {
        preferences.subscriptionUrl = url
        preferences.lastUpdatedAt = lastUpdatedAt
        _config.value = _config.value.copy(subscriptionUrl = url, lastUpdatedAt = lastUpdatedAt)
    }
}
