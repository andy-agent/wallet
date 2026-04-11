package com.v2ray.ang.composeui.p2.viewmodel

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.v2ray.ang.composeui.common.repository.CryptoVpnRepository
import com.v2ray.ang.composeui.common.viewmodel.BaseFeatureViewModel
import com.v2ray.ang.composeui.p2.model.WithdrawEvent
import com.v2ray.ang.composeui.p2.model.WithdrawUiState
import com.v2ray.ang.payment.data.repository.PaymentRepository
import kotlinx.coroutines.launch

class WithdrawViewModel(
    private val repository: CryptoVpnRepository,
    appContext: Context,
) : BaseFeatureViewModel<WithdrawUiState>(initialWithdrawState()) {
    private val paymentRepository = PaymentRepository(appContext.applicationContext)

    init {
        refresh()
    }

    fun onEvent(event: WithdrawEvent) {
        when (event) {
            is WithdrawEvent.FieldChanged -> {
                _uiState.value = _uiState.value.copy(
                    fields = _uiState.value.fields.map { field ->
                        if (field.key == event.key) field.copy(value = event.value) else field
                    },
                )
            }

            WithdrawEvent.PrimaryActionClicked -> submitWithdrawal()
            WithdrawEvent.SecondaryActionClicked -> Unit
            WithdrawEvent.Refresh -> refresh()
        }
    }

    private fun refresh() {
        _uiState.value = _uiState.value.copy(isLoading = true, isSubmitting = false, errorMessage = null, emptyMessage = null)
        viewModelScope.launch {
            runCatching { repository.getWithdrawState() }
                .onSuccess { state ->
                    _uiState.value = state.copy(
                        isLoading = false,
                        feedbackMessage = _uiState.value.feedbackMessage,
                    )
                }
                .onFailure {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = it.message ?: "加载提现页失败",
                    )
                }
        }
    }

    private fun submitWithdrawal() {
        val amount = _uiState.value.fields.firstOrNull { it.key == "amount" }?.value?.trim().orEmpty()
        val address = _uiState.value.fields.firstOrNull { it.key == "address" }?.value?.trim().orEmpty()
        if (amount.isBlank() || address.isBlank()) {
            _uiState.value = _uiState.value.copy(errorMessage = "请填写提现金额和到账地址")
            return
        }

        _uiState.value = _uiState.value.copy(isSubmitting = true, errorMessage = null, feedbackMessage = null)
        viewModelScope.launch {
            paymentRepository.createWithdrawal(amount = amount, payoutAddress = address)
                .onSuccess { withdrawal ->
                    val refreshed = runCatching { repository.getWithdrawState() }.getOrElse { _uiState.value }
                    _uiState.value = refreshed.copy(
                        isLoading = false,
                        isSubmitting = false,
                        feedbackMessage = "提现申请已提交：${withdrawal.requestNo}",
                    )
                }
                .onFailure {
                    _uiState.value = _uiState.value.copy(
                        isSubmitting = false,
                        errorMessage = it.message ?: "提交提现失败",
                    )
                }
        }
    }
}

private fun initialWithdrawState() = WithdrawUiState(
    badge = "",
    summary = "",
    primaryActionLabel = null,
    secondaryActionLabel = null,
    metrics = emptyList(),
    fields = emptyList(),
    highlights = emptyList(),
    checklist = emptyList(),
    note = "",
    isLoading = true,
)
