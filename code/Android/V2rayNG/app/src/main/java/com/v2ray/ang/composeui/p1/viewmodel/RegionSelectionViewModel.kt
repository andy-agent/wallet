package com.v2ray.ang.composeui.p1.viewmodel

import com.v2ray.ang.composeui.common.repository.CryptoVpnRepository
import com.v2ray.ang.composeui.common.viewmodel.BaseFeatureViewModel
import com.v2ray.ang.composeui.p1.model.RegionSelectionEvent
import com.v2ray.ang.composeui.p1.model.RegionSelectionUiState
import androidx.lifecycle.viewModelScope
import android.util.Log
import com.v2ray.ang.BuildConfig
import kotlinx.coroutines.launch

class RegionSelectionViewModel(
    private val repository: CryptoVpnRepository,
) : BaseFeatureViewModel<RegionSelectionUiState>(RegionSelectionUiState()) {

    init {
        refresh()
    }

    fun onEvent(event: RegionSelectionEvent) {
        when (event) {
            is RegionSelectionEvent.FieldChanged -> {
                _uiState.value = _uiState.value.copy(
                    searchQuery = event.value,
                )
            }

            is RegionSelectionEvent.NodeSelected -> selectNode(event.lineCode, event.nodeId)
            RegionSelectionEvent.PrimaryActionClicked -> Unit
            RegionSelectionEvent.SecondaryActionClicked -> Unit
            RegionSelectionEvent.SelectionNavigated -> {
                _uiState.value = _uiState.value.copy(selectionApplied = false)
            }
            RegionSelectionEvent.Refresh -> refresh()
        }
    }

    private fun refresh() {
        viewModelScope.launch {
            runCatching {
                repository.getCachedRegionSelectionState()?.let { cached ->
                    _uiState.value = cached
                }
                _uiState.value = repository.getRegionSelectionState()
            }.onFailure { error ->
                Log.e(BuildConfig.APPLICATION_ID, "RegionSelection refresh failed", error)
                _uiState.value = _uiState.value.copy(
                    screenState = _uiState.value.screenState.copy(
                        errorMessage = error.message ?: "节点页加载失败",
                    ),
                )
            }
        }
    }

    private fun selectNode(lineCode: String, nodeId: String) {
        launchLoad {
            repository.selectVpnNode(lineCode, nodeId)
        }
    }
}
