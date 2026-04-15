package com.v2ray.ang.composeui.p2.viewmodel

import androidx.lifecycle.viewModelScope
import com.v2ray.ang.composeui.common.repository.CryptoVpnRepository
import com.v2ray.ang.composeui.common.viewmodel.BaseFeatureViewModel
import com.v2ray.ang.composeui.navigation.CryptoVpnRouteSpec
import com.v2ray.ang.composeui.p2.model.SendEvent
import com.v2ray.ang.composeui.p2.model.SendUiState
import com.v2ray.ang.composeui.p2.model.SendRouteArgs
import kotlinx.coroutines.launch

class SendViewModel(
    private val repository: CryptoVpnRepository,
    private val routeArgs: SendRouteArgs = SendRouteArgs(),
) : BaseFeatureViewModel<SendUiState>(SendUiState()) {
    private var currentRouteArgs: SendRouteArgs = routeArgs

    init {
        refresh()
    }

    fun onEvent(event: SendEvent) {
        when (event) {
            is SendEvent.FieldChanged -> {
                _uiState.value = _uiState.value.copy(
                    fields = _uiState.value.fields.map { field ->
                        if (field.key == event.key) field.copy(value = event.value) else field
                    },
                )
            }

            is SendEvent.NetworkSelected -> switchNetwork(event.chainId)
            is SendEvent.AssetSelected -> switchAsset(event.assetId)
            SendEvent.PrimaryActionClicked -> submit()
            SendEvent.SecondaryActionClicked -> Unit
            SendEvent.Refresh -> refresh()
        }
    }

    private fun switchNetwork(chainId: String) {
        if (chainId == currentRouteArgs.chainId) {
            return
        }
        currentRouteArgs = currentRouteArgs.copy(chainId = chainId)
        refresh()
    }

    private fun switchAsset(assetId: String) {
        if (assetId.equals(currentRouteArgs.assetId, ignoreCase = true)) {
            return
        }
        currentRouteArgs = currentRouteArgs.copy(assetId = assetId)
        refresh()
    }

    private fun refresh() {
        launchLoad {
            repository.getSendState(currentRouteArgs).also { state ->
                currentRouteArgs = state.currentRoute
            }
        }
    }

    private fun submit() {
        val toAddress = _uiState.value.fields.firstOrNull { it.key == "to" }?.value.orEmpty().trim()
        val amount = _uiState.value.fields.firstOrNull { it.key == "amount" }?.value.orEmpty().trim()
        val memo = _uiState.value.fields.firstOrNull { it.key == "memo" }?.value.orEmpty().trim()
        if (toAddress.isBlank() || amount.isBlank()) {
            _uiState.value = _uiState.value.copy(
                feedbackMessage = "请填写收款地址和发送数量",
            )
            return
        }
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSubmitting = true, feedbackMessage = "正在发送...")
            val result = repository.submitSend(currentRouteArgs, toAddress, amount, memo)
            _uiState.value = _uiState.value.copy(
                isSubmitting = false,
                feedbackMessage = result.errorMessage ?: "发送成功",
                redirectRoute = result.txHash?.let { CryptoVpnRouteSpec.sendResultRoute(it) },
            )
        }
    }
}
