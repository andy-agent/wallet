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
 * 邮箱注册页状态
 */
sealed class EmailRegisterState {
    object Idle : EmailRegisterState()
    object Loading : EmailRegisterState()
    data class Success(val message: String) : EmailRegisterState()
    data class Error(val message: String) : EmailRegisterState()
}

/**
 * 邮箱注册页ViewModel
 */
class EmailRegisterViewModel : ViewModel() {
    private val _state = MutableStateFlow<EmailRegisterState>(EmailRegisterState.Idle)
    val state: StateFlow<EmailRegisterState> = _state

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password

    private val _confirmPassword = MutableStateFlow("")
    val confirmPassword: StateFlow<String> = _confirmPassword

    private val _passwordVisible = MutableStateFlow(false)
    val passwordVisible: StateFlow<Boolean> = _passwordVisible

    private val _confirmPasswordVisible = MutableStateFlow(false)
    val confirmPasswordVisible: StateFlow<Boolean> = _confirmPasswordVisible

    private val _agreeTerms = MutableStateFlow(false)
    val agreeTerms: StateFlow<Boolean> = _agreeTerms

    fun onEmailChange(value: String) {
        _email.value = value
    }

    fun onPasswordChange(value: String) {
        _password.value = value
    }

    fun onConfirmPasswordChange(value: String) {
        _confirmPassword.value = value
    }

    fun togglePasswordVisibility() {
        _passwordVisible.value = !_passwordVisible.value
    }

    fun toggleConfirmPasswordVisibility() {
        _confirmPasswordVisible.value = !_confirmPasswordVisible.value
    }

    fun onAgreeTermsChange(value: Boolean) {
        _agreeTerms.value = value
    }

    fun register() {
        when {
            _email.value.isBlank() -> {
                _state.value = EmailRegisterState.Error("请输入邮箱地址")
                return
            }
            !_email.value.contains("@") -> {
                _state.value = EmailRegisterState.Error("请输入有效的邮箱地址")
                return
            }
            _password.value.length < 6 -> {
                _state.value = EmailRegisterState.Error("密码长度至少6位")
                return
            }
            _password.value != _confirmPassword.value -> {
                _state.value = EmailRegisterState.Error("两次输入的密码不一致")
                return
            }
            !_agreeTerms.value -> {
                _state.value = EmailRegisterState.Error("请同意用户协议和隐私政策")
                return
            }
        }

        viewModelScope.launch {
            _state.value = EmailRegisterState.Loading
            delay(1500) // 模拟网络请求
            _state.value = EmailRegisterState.Success("注册成功，请登录")
        }
    }

    fun clearError() {
        if (_state.value is EmailRegisterState.Error) {
            _state.value = EmailRegisterState.Idle
        }
    }
}

/**
 * 邮箱注册页
 * 用户输入邮箱、密码和确认密码进行注册
 */
@Composable
fun EmailRegisterPage(
    viewModel: EmailRegisterViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    onRegisterSuccess: () -> Unit = {},
    onNavigateToLogin: () -> Unit = {},
    onNavigateToTerms: () -> Unit = {},
    onNavigateToPrivacy: () -> Unit = {}
) {
    val state by viewModel.state.collectAsState()
    val email by viewModel.email.collectAsState()
    val password by viewModel.password.collectAsState()
    val confirmPassword by viewModel.confirmPassword.collectAsState()
    val passwordVisible by viewModel.passwordVisible.collectAsState()
    val confirmPasswordVisible by viewModel.confirmPasswordVisible.collectAsState()
    val agreeTerms by viewModel.agreeTerms.collectAsState()
    val focusManager = LocalFocusManager.current

    // 监听注册状态
    LaunchedEffect(state) {
        when (state) {
            is EmailRegisterState.Success -> onRegisterSuccess()
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
            Spacer(modifier = Modifier.height(60.dp))

            // 标题
            Text(
                text = "创建账号",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "填写以下信息完成注册",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(32.dp))

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
                isError = state is EmailRegisterState.Error
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
                placeholder = { Text("至少6位字符") },
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
                            contentDescription = null
                        )
                    }
                },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                ),
                singleLine = true,
                shape = MaterialTheme.shapes.medium,
                isError = state is EmailRegisterState.Error
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 确认密码输入框
            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { 
                    viewModel.onConfirmPasswordChange(it)
                    viewModel.clearError()
                },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("确认密码") },
                placeholder = { Text("再次输入密码") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Confirm Password"
                    )
                },
                trailingIcon = {
                    IconButton(onClick = { viewModel.toggleConfirmPasswordVisibility() }) {
                        Icon(
                            imageVector = if (confirmPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = null
                        )
                    }
                },
                visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        focusManager.clearFocus()
                        viewModel.register()
                    }
                ),
                singleLine = true,
                shape = MaterialTheme.shapes.medium,
                isError = state is EmailRegisterState.Error
            )

            // 错误提示
            if (state is EmailRegisterState.Error) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = (state as EmailRegisterState.Error).message,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Start,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 用户协议
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = agreeTerms,
                    onCheckedChange = { viewModel.onAgreeTermsChange(it) }
                )
                Row {
                    Text(
                        text = "我已阅读并同意",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "用户协议",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.clickable { onNavigateToTerms() }
                    )
                    Text(
                        text = "和",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "隐私政策",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.clickable { onNavigateToPrivacy() }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 注册按钮
            Button(
                onClick = { 
                    focusManager.clearFocus()
                    viewModel.register() 
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = MaterialTheme.shapes.medium,
                enabled = state !is EmailRegisterState.Loading
            ) {
                if (state is EmailRegisterState.Loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = "注册",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 登录链接
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "已有账号？",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "立即登录",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.clickable { onNavigateToLogin() }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun EmailRegisterPagePreview() {
    MaterialTheme {
        EmailRegisterPage()
    }
}
