package com.app.vpncore.manager

import com.app.vpncore.model.VpnConfig
import com.app.vpncore.model.VpnNode
import com.app.vpncore.model.VpnState
import com.app.vpncore.parser.VpnParser
import com.app.vpncore.service.VpnConnectionService
import com.app.vpncore.storage.VpnStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class VpnManager(
    private val storage: VpnStorage,
    private val parser: VpnParser,
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private val _state = MutableStateFlow<VpnState>(VpnState.Disconnected)
    val state: StateFlow<VpnState> = _state.asStateFlow()
    val nodes: StateFlow<List<VpnNode>> = storage.nodes
    val config: StateFlow<VpnConfig> = storage.config
    val selectedNode: StateFlow<VpnNode?> = storage.nodes
        .map { items -> items.firstOrNull { it.id == storage.config.value.selectedNodeId } ?: items.firstOrNull() }
        .stateIn(scope, kotlinx.coroutines.flow.SharingStarted.Eagerly, null)

    fun replaceNodes(items: List<VpnNode>) {
        storage.replaceNodes(items)
        if (storage.config.value.selectedNodeId == null && items.isNotEmpty()) {
            storage.selectNode(items.first().id)
        }
    }

    fun refreshFromSubscription(payload: String, url: String) {
        val parsed = parser.parseSubscription(payload)
        replaceNodes(parsed)
        storage.updateSubscription(url, System.currentTimeMillis())
    }

    fun selectNode(nodeId: String) {
        storage.selectNode(nodeId)
    }

    fun connect() {
        val node = selectedNode.value ?: return run { _state.value = VpnState.Error("暂无可连接节点") }
        scope.launch {
            _state.value = VpnState.Connecting(node.name)
            VpnConnectionService.connect(node)
                .onSuccess {
                    _state.value = VpnState.Connected(node.name, System.currentTimeMillis(), 842.0, 163.0)
                }
                .onFailure {
                    _state.value = VpnState.Error(it.message ?: "连接失败")
                }
        }
    }

    fun disconnect() {
        scope.launch {
            _state.value = VpnState.Disconnecting
            VpnConnectionService.disconnect()
            _state.value = VpnState.Disconnected
        }
    }
}
