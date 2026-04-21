package com.v2ray.ang.composeui.p2.viewmodel

import com.v2ray.ang.composeui.common.repository.CryptoVpnRepository
import com.v2ray.ang.composeui.common.viewmodel.BaseFeatureViewModel
import com.v2ray.ang.composeui.p2.model.AssetDetailEvent
import com.v2ray.ang.composeui.p2.model.AssetDetailUiState
import com.v2ray.ang.composeui.p2.model.AssetDetailRouteArgs
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class AssetDetailViewModel(
    private val repository: CryptoVpnRepository,
    private val routeArgs: AssetDetailRouteArgs = AssetDetailRouteArgs(),
) : BaseFeatureViewModel<AssetDetailUiState>(AssetDetailUiState()) {
    private var periodicRefreshJob: Job? = null

    init {
        refresh()
        startPeriodicRefresh()
    }

    fun onEvent(event: AssetDetailEvent) {
        when (event) {
            is AssetDetailEvent.FieldChanged -> {
                _uiState.value = _uiState.value.copy(
                    fields = _uiState.value.fields.map { field ->
                        if (field.key == event.key) field.copy(value = event.value) else field
                    },
                )
            }

            AssetDetailEvent.PrimaryActionClicked -> Unit
            AssetDetailEvent.SecondaryActionClicked -> Unit
            AssetDetailEvent.Refresh -> refresh(forceRefresh = true)
        }
    }

    private fun refresh(forceRefresh: Boolean = false) {
        viewModelScope.launch {
            if (!forceRefresh) {
                repository.getCachedAssetDetailState(routeArgs)?.let { cached ->
                    _uiState.value = cached
                }
            }
            _uiState.value = repository.getAssetDetailState(routeArgs)
        }
    }

    private fun startPeriodicRefresh() {
        periodicRefreshJob?.cancel()
        periodicRefreshJob = viewModelScope.launch {
            while (isActive) {
                delay(60_000)
                refresh(forceRefresh = true)
            }
        }
    }

    override fun onCleared() {
        periodicRefreshJob?.cancel()
        super.onCleared()
    }
}
