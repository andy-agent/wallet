package com.v2ray.ang.composeui.p2extended.viewmodel

import androidx.lifecycle.viewModelScope
import com.v2ray.ang.composeui.common.repository.CryptoVpnRepository
import com.v2ray.ang.composeui.common.repository.LocalWalletActionResult
import com.v2ray.ang.composeui.common.repository.LogoutResult
import com.v2ray.ang.composeui.common.viewmodel.BaseFeatureViewModel
import com.v2ray.ang.composeui.p2extended.model.SecurityCenterEvent
import com.v2ray.ang.composeui.p2extended.model.SecurityCenterUiState
import kotlinx.coroutines.launch

class SecurityCenterViewModel(
    private val repository: CryptoVpnRepository,
) : BaseFeatureViewModel<SecurityCenterUiState>(SecurityCenterUiState()) {
    var lastLogoutResult: LogoutResult? = null
        private set
    var lastLocalWalletActionResult: LocalWalletActionResult? = null
        private set

    init {
        refresh()
    }

    fun onEvent(event: SecurityCenterEvent) {
        when (event) {
            is SecurityCenterEvent.FieldChanged -> {
                _uiState.value = _uiState.value.copy(
                    fields = _uiState.value.fields.map { field ->
                        if (field.key == event.key) field.copy(value = event.value) else field
                    },
                )
            }

            SecurityCenterEvent.PrimaryActionClicked -> Unit
            SecurityCenterEvent.SecondaryActionClicked -> Unit
            SecurityCenterEvent.DestructiveActionClicked -> Unit
            SecurityCenterEvent.Refresh -> refresh()
        }
    }

    private fun refresh() {
        launchLoad {
            repository.getSecurityCenterState()
        }
    }

    fun logout(
        onSuccess: () -> Unit,
        onError: (String) -> Unit,
    ) {
        viewModelScope.launch {
            val result = repository.logoutSession()
            lastLogoutResult = result
            if (result.success) {
                onSuccess()
            } else {
                onError(result.errorMessage ?: "退出登录失败")
            }
        }
    }

    fun exportWallet(
        onSuccess: (LocalWalletActionResult) -> Unit,
        onError: (String) -> Unit,
    ) {
        viewModelScope.launch {
            val result = repository.exportLocalWallet()
            lastLocalWalletActionResult = result
            if (result.success && !result.exportContent.isNullOrBlank()) {
                onSuccess(result)
            } else {
                onError(result.errorMessage ?: "导出加密备份失败")
            }
        }
    }

    fun clearLocalWallet(
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit,
    ) {
        viewModelScope.launch {
            val result = repository.clearLocalWallet()
            lastLocalWalletActionResult = result
            if (result.success) {
                refresh()
                onSuccess("本地钱包已清除")
            } else {
                onError(result.errorMessage ?: "清除本地钱包失败")
            }
        }
    }
}
