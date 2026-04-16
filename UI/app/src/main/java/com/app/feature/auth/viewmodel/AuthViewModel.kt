package com.app.feature.auth.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.AppGraph
import com.app.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AuthUiState(
    val email: String = "",
    val password: String = "",
    val inviteCode: String = "",
    val loading: Boolean = false,
    val isLoggedIn: Boolean = false,
    val forceUpdate: Boolean = false,
    val versionUpdate: Boolean = true,
    val lastMessage: String = "",
)

class AuthViewModel(
    private val repository: AuthRepository = AppGraph.authRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoggedIn = repository.loggedIn.value,
                    forceUpdate = repository.checkForceUpdate(),
                    versionUpdate = repository.checkVersionUpdate(),
                )
            }
        }
    }

    fun updateEmail(value: String) { _uiState.update { it.copy(email = value) } }
    fun updatePassword(value: String) { _uiState.update { it.copy(password = value) } }
    fun updateInviteCode(value: String) { _uiState.update { it.copy(inviteCode = value) } }

    fun login() {
        viewModelScope.launch {
            _uiState.update { it.copy(loading = true, lastMessage = "") }
            val success = repository.login(uiState.value.email, uiState.value.password)
            _uiState.update { it.copy(loading = false, isLoggedIn = success, lastMessage = if (success) "登录成功" else "邮箱或密码格式不正确") }
        }
    }

    fun register() {
        viewModelScope.launch {
            _uiState.update { it.copy(loading = true, lastMessage = "") }
            val success = repository.register(uiState.value.email, uiState.value.password, uiState.value.inviteCode)
            _uiState.update { it.copy(loading = false, isLoggedIn = success, lastMessage = if (success) "创建成功" else "请检查输入") }
        }
    }

    fun resetPassword() {
        viewModelScope.launch {
            val success = repository.resetPassword(uiState.value.email)
            _uiState.update { it.copy(lastMessage = if (success) "重置邮件已发送" else "请输入有效邮箱") }
        }
    }
}
