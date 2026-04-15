package com.v2ray.ang.composeui.p0.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.v2ray.ang.composeui.p0.model.LoginEvent
import com.v2ray.ang.composeui.p0.model.LoginUiState
import com.v2ray.ang.composeui.p0.repository.P0Repository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LoginViewModel(
    private val repository: P0Repository,
) : ViewModel() {
    var nextRoute: String? = null
        private set

    private val _uiState = MutableStateFlow(LoginUiState(isLoading = true))
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    init {
        refresh()
    }

    fun onEvent(event: LoginEvent, onLoginSuccess: (() -> Unit)? = null) {
        when (event) {
            is LoginEvent.EmailChanged -> {
                _uiState.value = _uiState.value.copy(
                    email = event.value,
                    successMessage = null,
                    errorMessage = null,
                    unavailableMessage = null,
                    dialogTitle = null,
                    dialogMessage = null,
                )
            }

            is LoginEvent.PasswordChanged -> {
                _uiState.value = _uiState.value.copy(
                    password = event.value,
                    successMessage = null,
                    errorMessage = null,
                    unavailableMessage = null,
                    dialogTitle = null,
                    dialogMessage = null,
                )
            }

            is LoginEvent.RememberMeChanged -> {
                _uiState.value = _uiState.value.copy(rememberMe = event.value)
            }

            LoginEvent.DialogDismissed -> {
                _uiState.value = _uiState.value.copy(
                    dialogTitle = null,
                    dialogMessage = null,
                )
            }

            LoginEvent.LoginClicked -> {
                viewModelScope.launch {
                    _uiState.value = _uiState.value.copy(
                        isLoading = true,
                        errorMessage = null,
                        unavailableMessage = null,
                        successMessage = null,
                        dialogTitle = null,
                        dialogMessage = null,
                    )
                    val result = repository.login(_uiState.value.email, _uiState.value.password)
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = null,
                        unavailableMessage = null,
                        successMessage = if (result.success) "登录成功，正在进入主界面。" else null,
                        dialogTitle = when {
                            result.success -> null
                            result.unavailable -> "服务不可用"
                            else -> "登录失败"
                        },
                        dialogMessage = when {
                            result.success -> null
                            result.unavailable -> result.errorMessage ?: "账户服务当前不可用"
                            else -> result.errorMessage ?: "登录失败"
                        },
                    )
                    if (result.success) {
                        nextRoute = result.nextRoute
                        onLoginSuccess?.invoke()
                    }
                }
            }
        }
    }

    fun refresh() {
        viewModelScope.launch {
            val seed = repository.getLoginSeed()
            _uiState.value = seed.copy(isLoading = false)
        }
    }
}
