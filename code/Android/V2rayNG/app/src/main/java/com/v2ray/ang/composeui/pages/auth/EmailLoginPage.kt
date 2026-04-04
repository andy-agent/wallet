package com.v2ray.ang.composeui.pages.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * 邮箱登录页状态
 */
sealed class EmailLoginState {
    object Idle : EmailLoginState()
    object Loading : EmailLoginState()
    data class Success(val token: String) : EmailLoginState()
    data class Error(val message: String) : EmailLoginState()
}

/**
 * 邮箱登录页ViewModel
 */
class EmailLoginViewModel : ViewModel() {
    private val _state = MutableStateFlow<EmailLoginState>(EmailLoginState.Idle)
    val state: StateFlow<EmailLoginState> = _state

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password

    private val _passwordVisible = MutableStateFlow(false)
    val passwordVisible: StateFlow<Boolean> = _passwordVisible

    fun onEmailChange(value: String) {
        _email.value = value
    }

    fun onPasswordChange(value: String) {
        _password.value = value
    }

    fun togglePasswordVisibility() {
        _passwordVisible.value = !_passwordVisible.value
    }

    fun login() {
        if (_email.value.isBlank() || _password.value.isBlank()) {
            _state.value = EmailLoginState.Error("请填写邮箱和密码")
            return
        }
        
        viewModelScope.launch {
            _state.value = EmailLoginState.Loading
            delay(1500) // 模拟网络请求
            
            // 模拟登录成功
            if (_email.value.contains("@") && _password.value.length >= 6) {
                _state.value = EmailLoginState.Success("mock_token_12345")
            } else {
                _state.value = EmailLoginState.Error("邮箱或密码错误")
            }
        }
    }

    fun clearError() {
        if (_state.value is EmailLoginState.Error) {
            _state.value = EmailLoginState.Idle
        }
    }
}

/**
 * 邮箱登录页
 * 用户输入邮箱和密码进行登录
 */
@Composable
fun EmailLoginPage(
    viewModel: EmailLoginViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    onLoginSuccess: () -> Unit = {},
    onNavigateToRegister: () -> Unit = {},
    onNavigateToResetPassword: () -> Unit = {}
) {
    val state by viewModel.state.collectAsState()
    val email by viewModel.email.collectAsState()
    val password by viewModel.password.collectAsState()
    val passwordVisible by viewModel.passwordVisible.collectAsState()
    val focusManager = LocalFocusManager.current

    // 监听登录状态
    LaunchedEffect(state) {
        when (state) {
            is EmailLoginState.Success -> onLoginSuccess()
            else -> {}
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(80.dp))

            // Logo
            Text(
                text = "CryptoVPN",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "欢迎回来",
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(48.dp))

            // 邮箱输入框
            OutlinedTextField(
                value = email,
                onValueChange = { 
                    viewModel.onEmailChange(it)
                    viewModel.clearError()
                },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("邮箱") },
                placeholder = { Text("请输入邮箱地址") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Email,
                        contentDescription = "Email"
                    )
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                ),
                singleLine = true,
                shape = MaterialTheme.shapes.medium,
                isError = state is EmailLoginState.Error
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 密码输入框
            OutlinedTextField(
                value = password,
                onValueChange = { 
                    viewModel.onPasswordChange(it)
                    viewModel.clearError()
                },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("密码") },
                placeholder = { Text("请输入密码") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Password"
                    )
                },
                trailingIcon = {
                    IconButton(onClick = { viewModel.togglePasswordVisibility() }) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = if (passwordVisible) "Hide password" else "Show password"
                        )
                    }
                },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        focusManager.clearFocus()
                        viewModel.login()
                    }
                ),
                singleLine = true,
                shape = MaterialTheme.shapes.medium,
                isError = state is EmailLoginState.Error
            )

            // 错误提示
            if (state is EmailLoginState.Error) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = (state as EmailLoginState.Error).message,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Start,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 忘记密码
            Text(
                text = "忘记密码？",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .align(Alignment.End)
                    .clickable { onNavigateToResetPassword() }
            )

            Spacer(modifier = Modifier.height(32.dp))

            // 登录按钮
            Button(
                onClick = { 
                    focusManager.clearFocus()
                    viewModel.login() 
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = MaterialTheme.shapes.medium,
                enabled = state !is EmailLoginState.Loading
            ) {
                if (state is EmailLoginState.Loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = "登录",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 注册链接
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "还没有账号？",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "立即注册",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.clickable { onNavigateToRegister() }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun EmailLoginPagePreview() {
    MaterialTheme {
        EmailLoginPage()
    }
}
