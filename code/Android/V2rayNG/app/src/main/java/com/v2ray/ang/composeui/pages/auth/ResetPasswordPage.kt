package com.v2ray.ang.composeui.pages.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
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
 * 重置密码页步骤
 */
enum class ResetPasswordStep {
    EMAIL,      // 输入邮箱
    VERIFY,     // 输入验证码
    NEW_PASSWORD // 设置新密码
}

/**
 * 重置密码页状态
 */
sealed class ResetPasswordState {
    object Idle : ResetPasswordState()
    object Loading : ResetPasswordState()
    data class Success(val message: String) : ResetPasswordState()
    data class Error(val message: String) : ResetPasswordState()
}

/**
 * 重置密码页ViewModel
 */
class ResetPasswordViewModel : ViewModel() {
    private val _state = MutableStateFlow<ResetPasswordState>(ResetPasswordState.Idle)
    val state: StateFlow<ResetPasswordState> = _state

    private val _currentStep = MutableStateFlow(ResetPasswordStep.EMAIL)
    val currentStep: StateFlow<ResetPasswordStep> = _currentStep

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email

    private val _verificationCode = MutableStateFlow("")
    val verificationCode: StateFlow<String> = _verificationCode

    private val _newPassword = MutableStateFlow("")
    val newPassword: StateFlow<String> = _newPassword

    private val _confirmPassword = MutableStateFlow("")
    val confirmPassword: StateFlow<String> = _confirmPassword

    private val _passwordVisible = MutableStateFlow(false)
    val passwordVisible: StateFlow<Boolean> = _passwordVisible

    private val _countdown = MutableStateFlow(0)
    val countdown: StateFlow<Int> = _countdown

    fun onEmailChange(value: String) {
        _email.value = value
    }

    fun onVerificationCodeChange(value: String) {
        _verificationCode.value = value.take(6)
    }

    fun onNewPasswordChange(value: String) {
        _newPassword.value = value
    }

    fun onConfirmPasswordChange(value: String) {
        _confirmPassword.value = value
    }

    fun togglePasswordVisibility() {
        _passwordVisible.value = !_passwordVisible.value
    }

    fun sendVerificationCode() {
        if (_email.value.isBlank() || !_email.value.contains("@")) {
            _state.value = ResetPasswordState.Error("请输入有效的邮箱地址")
            return
        }

        viewModelScope.launch {
            _state.value = ResetPasswordState.Loading
            delay(1000)
            _state.value = ResetPasswordState.Idle
            _currentStep.value = ResetPasswordStep.VERIFY
            startCountdown()
        }
    }

    private fun startCountdown() {
        viewModelScope.launch {
            _countdown.value = 60
            while (_countdown.value > 0) {
                delay(1000)
                _countdown.value--
            }
        }
    }

    fun resendCode() {
        if (_countdown.value == 0) {
            startCountdown()
        }
    }

    fun verifyCode() {
        if (_verificationCode.value.length != 6) {
            _state.value = ResetPasswordState.Error("请输入6位验证码")
            return
        }

        viewModelScope.launch {
            _state.value = ResetPasswordState.Loading
            delay(1000)
            _state.value = ResetPasswordState.Idle
            _currentStep.value = ResetPasswordStep.NEW_PASSWORD
        }
    }

    fun resetPassword() {
        when {
            _newPassword.value.length < 6 -> {
                _state.value = ResetPasswordState.Error("密码长度至少6位")
                return
            }
            _newPassword.value != _confirmPassword.value -> {
                _state.value = ResetPasswordState.Error("两次输入的密码不一致")
                return
            }
        }

        viewModelScope.launch {
            _state.value = ResetPasswordState.Loading
            delay(1500)
            _state.value = ResetPasswordState.Success("密码重置成功")
        }
    }

    fun goBack() {
        when (_currentStep.value) {
            ResetPasswordStep.VERIFY -> _currentStep.value = ResetPasswordStep.EMAIL
            ResetPasswordStep.NEW_PASSWORD -> _currentStep.value = ResetPasswordStep.VERIFY
            else -> {}
        }
    }

    fun clearError() {
        if (_state.value is ResetPasswordState.Error) {
            _state.value = ResetPasswordState.Idle
        }
    }
}

/**
 * 重置密码页
 * 三步流程：输入邮箱 -> 输入验证码 -> 设置新密码
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResetPasswordPage(
    viewModel: ResetPasswordViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    onResetSuccess: () -> Unit = {},
    onNavigateBack: () -> Unit = {}
) {
    val state by viewModel.state.collectAsState()
    val currentStep by viewModel.currentStep.collectAsState()
    val email by viewModel.email.collectAsState()
    val verificationCode by viewModel.verificationCode.collectAsState()
    val newPassword by viewModel.newPassword.collectAsState()
    val confirmPassword by viewModel.confirmPassword.collectAsState()
    val passwordVisible by viewModel.passwordVisible.collectAsState()
    val countdown by viewModel.countdown.collectAsState()
    val focusManager = LocalFocusManager.current

    // 监听状态
    LaunchedEffect(state) {
        when (state) {
            is ResetPasswordState.Success -> onResetSuccess()
            else -> {}
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("重置密码") },
                navigationIcon = {
                    IconButton(onClick = { 
                        if (currentStep == ResetPasswordStep.EMAIL) {
                            onNavigateBack()
                        } else {
                            viewModel.goBack()
                        }
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 步骤指示器
            StepIndicator(currentStep)

            Spacer(modifier = Modifier.height(32.dp))

            // 根据步骤显示不同内容
            when (currentStep) {
                ResetPasswordStep.EMAIL -> EmailStep(
                    email = email,
                    onEmailChange = { 
                        viewModel.onEmailChange(it)
                        viewModel.clearError()
                    },
                    onSendCode = { viewModel.sendVerificationCode() },
                    isLoading = state is ResetPasswordState.Loading,
                    focusManager = focusManager
                )
                ResetPasswordStep.VERIFY -> VerifyStep(
                    email = email,
                    code = verificationCode,
                    onCodeChange = { 
                        viewModel.onVerificationCodeChange(it)
                        viewModel.clearError()
                    },
                    countdown = countdown,
                    onResend = { viewModel.resendCode() },
                    onVerify = { viewModel.verifyCode() },
                    isLoading = state is ResetPasswordState.Loading,
                    focusManager = focusManager
                )
                ResetPasswordStep.NEW_PASSWORD -> NewPasswordStep(
                    newPassword = newPassword,
                    confirmPassword = confirmPassword,
                    passwordVisible = passwordVisible,
                    onNewPasswordChange = { 
                        viewModel.onNewPasswordChange(it)
                        viewModel.clearError()
                    },
                    onConfirmPasswordChange = { 
                        viewModel.onConfirmPasswordChange(it)
                        viewModel.clearError()
                    },
                    onToggleVisibility = { viewModel.togglePasswordVisibility() },
                    onReset = { viewModel.resetPassword() },
                    isLoading = state is ResetPasswordState.Loading,
                    focusManager = focusManager
                )
            }

            // 错误提示
            if (state is ResetPasswordState.Error) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = (state as ResetPasswordState.Error).message,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun StepIndicator(currentStep: ResetPasswordStep) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        StepItem(
            number = 1,
            label = "邮箱",
            isActive = currentStep == ResetPasswordStep.EMAIL,
            isCompleted = currentStep.ordinal > 0
        )
        
        Divider(
            modifier = Modifier.width(40.dp),
            color = if (currentStep.ordinal > 0) MaterialTheme.colorScheme.primary 
                    else MaterialTheme.colorScheme.outline
        )
        
        StepItem(
            number = 2,
            label = "验证",
            isActive = currentStep == ResetPasswordStep.VERIFY,
            isCompleted = currentStep.ordinal > 1
        )
        
        Divider(
            modifier = Modifier.width(40.dp),
            color = if (currentStep.ordinal > 1) MaterialTheme.colorScheme.primary 
                    else MaterialTheme.colorScheme.outline
        )
        
        StepItem(
            number = 3,
            label = "新密码",
            isActive = currentStep == ResetPasswordStep.NEW_PASSWORD,
            isCompleted = false
        )
    }
}

@Composable
private fun StepItem(
    number: Int,
    label: String,
    isActive: Boolean,
    isCompleted: Boolean
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Surface(
            shape = MaterialTheme.shapes.small,
            color = when {
                isCompleted -> MaterialTheme.colorScheme.primary
                isActive -> MaterialTheme.colorScheme.primaryContainer
                else -> MaterialTheme.colorScheme.surfaceVariant
            },
            modifier = Modifier.size(32.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                if (isCompleted) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                } else {
                    Text(
                        text = number.toString(),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = when {
                            isActive -> MaterialTheme.colorScheme.onPrimaryContainer
                            else -> MaterialTheme.colorScheme.onSurfaceVariant
                        }
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            fontSize = 12.sp,
            color = when {
                isActive || isCompleted -> MaterialTheme.colorScheme.primary
                else -> MaterialTheme.colorScheme.onSurfaceVariant
            }
        )
    }
}

@Composable
private fun EmailStep(
    email: String,
    onEmailChange: (String) -> Unit,
    onSendCode: () -> Unit,
    isLoading: Boolean,
    focusManager: androidx.compose.ui.focus.FocusManager
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "请输入您的注册邮箱",
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        OutlinedTextField(
            value = email,
            onValueChange = onEmailChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text("邮箱") },
            placeholder = { Text("请输入邮箱地址") },
            leadingIcon = {
                Icon(imageVector = Icons.Default.Email, contentDescription = null)
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    focusManager.clearFocus()
                    onSendCode()
                }
            ),
            singleLine = true,
            shape = MaterialTheme.shapes.medium
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Button(
            onClick = {
                focusManager.clearFocus()
                onSendCode()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = MaterialTheme.shapes.medium,
            enabled = !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary,
                    strokeWidth = 2.dp
                )
            } else {
                Text(
                    text = "发送验证码",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun VerifyStep(
    email: String,
    code: String,
    onCodeChange: (String) -> Unit,
    countdown: Int,
    onResend: () -> Unit,
    onVerify: () -> Unit,
    isLoading: Boolean,
    focusManager: androidx.compose.ui.focus.FocusManager
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "验证码已发送至",
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Text(
            text = email,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onBackground
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        OutlinedTextField(
            value = code,
            onValueChange = onCodeChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text("验证码") },
            placeholder = { Text("请输入6位验证码") },
            leadingIcon = {
                Icon(imageVector = Icons.Default.Verified, contentDescription = null)
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    focusManager.clearFocus()
                    onVerify()
                }
            ),
            singleLine = true,
            shape = MaterialTheme.shapes.medium
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // 重新发送
        Row {
            Text(
                text = "没有收到？",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            if (countdown > 0) {
                Text(
                    text = "${countdown}秒后重试",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                Text(
                    text = "重新发送",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.clickable { onResend() }
                )
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Button(
            onClick = {
                focusManager.clearFocus()
                onVerify()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = MaterialTheme.shapes.medium,
            enabled = !isLoading && code.length == 6
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary,
                    strokeWidth = 2.dp
                )
            } else {
                Text(
                    text = "验证",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun NewPasswordStep(
    newPassword: String,
    confirmPassword: String,
    passwordVisible: Boolean,
    onNewPasswordChange: (String) -> Unit,
    onConfirmPasswordChange: (String) -> Unit,
    onToggleVisibility: () -> Unit,
    onReset: () -> Unit,
    isLoading: Boolean,
    focusManager: androidx.compose.ui.focus.FocusManager
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "请设置新密码",
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        OutlinedTextField(
            value = newPassword,
            onValueChange = onNewPasswordChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text("新密码") },
            placeholder = { Text("至少6位字符") },
            leadingIcon = {
                Icon(imageVector = Icons.Default.Lock, contentDescription = null)
            },
            trailingIcon = {
                IconButton(onClick = onToggleVisibility) {
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
            shape = MaterialTheme.shapes.medium
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        OutlinedTextField(
            value = confirmPassword,
            onValueChange = onConfirmPasswordChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text("确认密码") },
            placeholder = { Text("再次输入新密码") },
            leadingIcon = {
                Icon(imageVector = Icons.Default.Lock, contentDescription = null)
            },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    focusManager.clearFocus()
                    onReset()
                }
            ),
            singleLine = true,
            shape = MaterialTheme.shapes.medium
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Button(
            onClick = {
                focusManager.clearFocus()
                onReset()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = MaterialTheme.shapes.medium,
            enabled = !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary,
                    strokeWidth = 2.dp
                )
            } else {
                Text(
                    text = "重置密码",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ResetPasswordPagePreview() {
    MaterialTheme {
        ResetPasswordPage()
    }
}
