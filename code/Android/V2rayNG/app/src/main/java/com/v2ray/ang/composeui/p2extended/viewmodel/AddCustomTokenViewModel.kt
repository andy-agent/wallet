package com.v2ray.ang.composeui.p2extended.viewmodel

import com.v2ray.ang.composeui.common.repository.CryptoVpnRepository
import com.v2ray.ang.composeui.common.viewmodel.BaseFeatureViewModel
import com.v2ray.ang.composeui.p2extended.model.AddCustomTokenCandidateUi
import com.v2ray.ang.composeui.p2extended.model.AddCustomTokenEvent
import com.v2ray.ang.composeui.p2extended.model.AddCustomTokenUiState
import com.v2ray.ang.composeui.p2extended.model.AddCustomTokenRouteArgs
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class AddCustomTokenViewModel(
    private val repository: CryptoVpnRepository,
    private val routeArgs: AddCustomTokenRouteArgs = AddCustomTokenRouteArgs(),
) : BaseFeatureViewModel<AddCustomTokenUiState>(AddCustomTokenUiState()) {

    init {
        refresh()
    }

    fun onEvent(event: AddCustomTokenEvent) {
        when (event) {
            is AddCustomTokenEvent.QueryChanged -> _uiState.value = _uiState.value.copy(query = event.value)
            is AddCustomTokenEvent.AddressChanged -> _uiState.value = _uiState.value.copy(tokenAddress = event.value)
            is AddCustomTokenEvent.NameChanged -> _uiState.value = _uiState.value.copy(name = event.value)
            is AddCustomTokenEvent.SymbolChanged -> _uiState.value = _uiState.value.copy(symbol = event.value)
            is AddCustomTokenEvent.DecimalsChanged -> _uiState.value = _uiState.value.copy(decimals = event.value)
            AddCustomTokenEvent.Refresh -> refresh()
        }
    }

    fun refresh() {
        launchLoad {
            repository.getAddCustomTokenState(routeArgs)
        }
    }

    fun search() {
        val query = _uiState.value.query.trim().ifBlank { _uiState.value.tokenAddress.trim() }
        if (query.isBlank()) {
            _uiState.value = _uiState.value.copy(errorMessage = "请输入代币名称或精确地址")
            return
        }
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSearching = true, errorMessage = null, statusMessage = "正在搜索代币")
            val result = repository.searchCustomTokens(routeArgs.chainId, query)
            if (result.isSuccess) {
                val candidates = result.getOrNull().orEmpty()
                _uiState.value = _uiState.value.copy(
                    isSearching = false,
                    searchResults = candidates,
                    tokenAddress = _uiState.value.tokenAddress.ifBlank { query.takeIf { it.startsWith("0x") || it.length > 24 }.orEmpty() },
                    statusMessage = if (candidates.isEmpty()) "未命中候选，可继续手动填写元数据" else "已返回 ${candidates.size} 个候选",
                )
            } else {
                _uiState.value = _uiState.value.copy(
                    isSearching = false,
                    errorMessage = result.exceptionOrNull()?.message ?: "搜索失败",
                    statusMessage = null,
                )
            }
        }
    }

    fun selectCandidate(candidate: AddCustomTokenCandidateUi) {
        _uiState.value = _uiState.value.copy(
            selectedTokenAddress = candidate.tokenAddress,
            tokenAddress = candidate.tokenAddress,
            name = candidate.name,
            symbol = candidate.symbol,
            decimals = candidate.decimals.toString(),
            iconUrl = candidate.iconUrl,
            statusMessage = "已选择 ${candidate.symbol}",
            errorMessage = null,
        )
    }

    fun save(onSuccess: () -> Unit) {
        val state = _uiState.value
        val tokenAddress = state.tokenAddress.trim()
        val name = state.name.trim()
        val symbol = state.symbol.trim()
        val decimals = state.decimals.trim().toIntOrNull()
        if (tokenAddress.isBlank()) {
            _uiState.value = state.copy(errorMessage = "请输入精确地址")
            return
        }
        if (name.isBlank() || symbol.isBlank() || decimals == null) {
            _uiState.value = state.copy(errorMessage = "请补全名称、符号和精度")
            return
        }
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSaving = true, errorMessage = null, statusMessage = "正在保存自定义代币")
            val result = repository.submitCustomToken(
                walletId = routeArgs.walletId,
                chainId = routeArgs.chainId,
                tokenAddress = tokenAddress,
                name = name,
                symbol = symbol,
                decimals = decimals,
                iconUrl = state.iconUrl,
            )
            if (result.success) {
                _uiState.value = _uiState.value.copy(isSaving = false, statusMessage = result.message ?: "添加成功")
                onSuccess()
            } else {
                _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    errorMessage = result.errorMessage ?: "添加失败",
                    statusMessage = null,
                )
            }
        }
    }
}
