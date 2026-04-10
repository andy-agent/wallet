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

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            _uiState.value = repository.getLoginSeed()
        }
    }

    fun onEvent(event: LoginEvent, onLoginSuccess: (() -> Unit)? = null) {
        when (event) {
            is LoginEvent.EmailChanged -> _uiState.value = _uiState.value.copy(email = event.value)
            is LoginEvent.PasswordChanged -> _uiState.value = _uiState.value.copy(password = event.value)
            is LoginEvent.RememberMeChanged -> _uiState.value = _uiState.value.copy(rememberMe = event.value)
            LoginEvent.LoginClicked -> {
                viewModelScope.launch {
                    _uiState.value = _uiState.value.copy(isLoading = true)
                    val result = repository.login(_uiState.value.email, _uiState.value.password)
                    _uiState.value = _uiState.value.copy(isLoading = false)
                    if (result.success) {
                        onLoginSuccess?.invoke()
                    }
                }
            }
        }
    }
}
