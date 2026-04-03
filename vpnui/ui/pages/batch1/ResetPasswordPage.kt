package com.cryptovpn.ui.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.cryptovpn.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * 验证码状态
 */
sealed class VerificationCodeState {
    object Idle : VerificationCodeState()
    data class CountingDown(val secondsRemaining: Int) : VerificationCodeState()
    object CanResend : VerificationCodeState()
}

/**
 * 重置密码状态
 */
sealed class ResetPasswordState {
    object Idle : ResetPasswordState()
    object SendingCode : ResetPasswordState()
    object CodeSent : ResetPasswordState()
    object Verifying : ResetPasswordState()
    object Resetting : ResetPasswordState()
    data class Success(val message: String) : ResetPasswordState()
    data class Error(val message: String) : ResetPasswordState()
}

/**
 * 重置密码页面 UI State
 */
data class ResetPasswordUiState(
    val email: String = "",
    val verificationCode: String = "",
    val newPassword: String = "",
    val confirmPassword: String = "",
    val isNewPasswordVisible: Boolean = false,
    val isConfirmPasswordVisible: Boolean = false,
    val resetState: ResetPasswordState = ResetPasswordState.Idle,
    val codeState: VerificationCodeState = VerificationCodeState.Idle,
    val emailError: String? = null,
    val codeError: String? = null,
    val newPasswordError: String? = null,
    val confirmPasswordError: String? = null
) {
    val isLoading: Boolean get() = resetState is ResetPasswordState.Resetting || 
                                   resetState is ResetPasswordState.SendingCode ||
                                   resetState is ResetPasswordState.Verifying
    val canSendCode: Boolean get() = email.isNotBlank() && emailError == null && 
                                     codeState !is VerificationCodeState.CountingDown &&
                                     resetState !is ResetPasswordState.SendingCode
    val canSubmit: Boolean get() = email.isNotBlank() && verificationCode.isNotBlank() && 
                                   newPassword.isNotBlank() && confirmPassword.isNotBlank() &&
                                   emailError == null && codeError == null && 
                                   newPasswordError == null && confirmPasswordError == null
}

/**
 * 重置密码 ViewModel
 */
class ResetPasswordViewModel {
    private val _uiState = MutableStateFlow(ResetPasswordUiState())
    val uiState: StateFlow<ResetPasswordUiState> = _uiState.asStateFlow()

    private val countdownSeconds = 60

    fun onEmailChange(email: String) {
        _uiState.value = _uiState.value.copy(
            email = email,
            emailError = validateEmail(email)
        )
    }

    fun onVerificationCodeChange(code: String) {
        // 限制验证码长度为6位数字
        if (code.length <= 6 && code.all { it.isDigit() }) {
            _uiState.value = _uiState.value.copy(
                verificationCode = code,
                codeError = validateVerificationCode(code)
            )
        }
    }

    fun onNewPasswordChange(password: String) {
        val currentState = _uiState.value
        _uiState.value = currentState.copy(
            newPassword = password,
            newPasswordError = validatePassword(password),
            confirmPasswordError = if (currentState.confirmPassword.isNotEmpty()) {
                validateConfirmPassword(currentState.confirmPassword, password)
            } else null
        )
    }

    fun onConfirmPasswordChange(password: String) {
        _uiState.value = _uiState.value.copy(
            confirmPassword = password,
            confirmPasswordError = validateConfirmPassword(password, _uiState.value.newPassword)
        )
    }

    fun toggleNewPasswordVisibility() {
        _uiState.value = _uiState.value.copy(
            isNewPasswordVisible = !_uiState.value.isNewPasswordVisible
        )
    }

    fun toggleConfirmPasswordVisibility() {
        _uiState.value = _uiState.value.copy(
            isConfirmPasswordVisible = !_uiState.value.isConfirmPasswordVisible
        )
    }

    suspend fun sendVerificationCode() {
        val currentState = _uiState.value
        
        val emailError = validateEmail(currentState.email)
        if (emailError != null) {
            _uiState.value = currentState.copy(emailError = emailError)
            return
        }
        
        _uiState.value = currentState.copy(resetState = ResetPasswordState.SendingCode)
        
        // 模拟发送验证码
        delay(1000)
        
        _uiState.value = _uiState.value.copy(
            resetState = ResetPasswordState.CodeSent
        )
        
        // 开始倒计时
        startCountdown()
    }

    private suspend fun startCountdown() {
        for (i in countdownSeconds downTo 1) {
            _uiState.value = _uiState.value.copy(
                codeState = VerificationCodeState.CountingDown(i)
            )
            delay(1000)
        }
        _uiState.value = _uiState.value.copy(
            codeState = VerificationCodeState.CanResend
        )
    }

    fun resetPassword() {
        val currentState = _uiState.value
        
        // 验证输入
        val emailError = validateEmail(currentState.email)
        val codeError = validateVerificationCode(currentState.verificationCode)
        val newPasswordError = validatePassword(currentState.newPassword)
        val confirmPasswordError = validateConfirmPassword(
            currentState.confirmPassword, 
            currentState.newPassword
        )
        
        if (emailError != null || codeError != null || 
            newPasswordError != null || confirmPasswordError != null) {
            _uiState.value = currentState.copy(
                emailError = emailError,
                codeError = codeError,
                newPasswordError = newPasswordError,
                confirmPasswordError = confirmPasswordError
            )
            return
        }
        
        _uiState.value = currentState.copy(resetState = ResetPasswordState.Resetting)
        
        // 模拟重置密码请求
        // 实际项目中这里调用API
    }

    fun onResetSuccess(message: String) {
        _uiState.value = _uiState.value.copy(
            resetState = ResetPasswordState.Success(message)
        )
    }

    fun onResetError(message: String) {
        _uiState.value = _uiState.value.copy(
            resetState = ResetPasswordState.Error(message)
        )
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(
            resetState = ResetPasswordState.Idle
        )
    }

    private fun validateEmail(email: String): String? {
        return when {
            email.isBlank() -> null
            !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> "请输入有效的邮箱地址"
            else -> null
        }
    }

    private fun validateVerificationCode(code: String): String? {
        return when {
            code.isBlank() -> null
            code.length != 6 -> "验证码应为6位数字"
            else -> null
        }
    }

    private fun validatePassword(password: String): String? {
        return when {
            password.isBlank() -> null
            password.length < 6 -> "密码长度至少6位"
            else -> null
        }
    }

    private fun validateConfirmPassword(confirmPassword: String, password: String): String? {
        return when {
            confirmPassword.isBlank() -> null
            confirmPassword != password -> "两次输入的密码不一致"
            else -> null
        }
    }
}

/**
 * 重置密码页
 */
@Composable
fun ResetPasswordPage(
    uiState: ResetPasswordUiState = ResetPasswordUiState(),
    onEmailChange: (String) -> Unit = {},
    onVerificationCodeChange: (String) -> Unit = {},
    onNewPasswordChange: (String) -> Unit = {},
    onConfirmPasswordChange: (String) -> Unit = {},
    onNewPasswordVisibilityToggle: () -> Unit = {},
    onConfirmPasswordVisibilityToggle: () -> Unit = {},
    onSendCodeClick: () -> Unit = {},
    onResetClick: () -> Unit = {},
    onBackClick: () -> Unit = {},
    onLoginClick: () -> Unit = {}
) {
    val focusManager = LocalFocusManager.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkNavyBackground)
    ) {
        // 顶部导航栏
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier.align(Alignment.CenterStart)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "返回",
                    tint = TextPrimaryWhite
                )
            }
            
            Text(
                text = "重置密码",
                color = TextPrimaryWhite,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.align(Alignment.Center)
            )
        }

        // 内容区域
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 标题
            Text(
                text = "忘记密码？",
                color = TextPrimaryWhite,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "输入您的邮箱，我们将发送验证码帮您重置密码",
                color = TextSecondaryGray,
                fontSize = 14.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            // 错误提示
            if (uiState.resetState is ResetPasswordState.Error) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    color = ErrorRed.copy(alpha = 0.1f)
                ) {
                    Text(
                        text = uiState.resetState.message,
                        color = ErrorRed,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(12.dp),
                        textAlign = TextAlign.Center
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // 成功提示
            if (uiState.resetState is ResetPasswordState.Success) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    color = SuccessGreen.copy(alpha = 0.1f)
                ) {
                    Text(
                        text = uiState.resetState.message,
                        color = SuccessGreen,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(12.dp),
                        textAlign = TextAlign.Center
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // 邮箱输入框
            OutlinedTextField(
                value = uiState.email,
                onValueChange = onEmailChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("邮箱地址") },
                placeholder = { Text("请输入注册邮箱") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Email,
                        contentDescription = null,
                        tint = TextSecondaryGray
                    )
                },
                isError = uiState.emailError != null,
                supportingText = uiState.emailError?.let { { Text(it) } },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                ),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PrimaryBlue,
                    unfocusedBorderColor = DarkNavyCard,
                    focusedLabelColor = PrimaryBlue,
                    unfocusedLabelColor = TextSecondaryGray,
                    focusedTextColor = TextPrimaryWhite,
                    unfocusedTextColor = TextPrimaryWhite,
                    errorBorderColor = ErrorRed,
                    errorSupportingTextColor = ErrorRed,
                    focusedContainerColor = DarkNavyCard,
                    unfocusedContainerColor = DarkNavyCard
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            // 验证码输入框
            OutlinedTextField(
                value = uiState.verificationCode,
                onValueChange = onVerificationCodeChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("验证码") },
                placeholder = { Text("请输入6位验证码") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Verified,
                        contentDescription = null,
                        tint = TextSecondaryGray
                    )
                },
                trailingIcon = {
                    // 发送验证码按钮
                    val buttonText = when (uiState.codeState) {
                        is VerificationCodeState.Idle -> "获取验证码"
                        is VerificationCodeState.CountingDown -> 
                            "${uiState.codeState.secondsRemaining}s"
                        is VerificationCodeState.CanResend -> "重新获取"
                    }
                    
                    val isEnabled = uiState.canSendCode
                    
                    TextButton(
                        onClick = onSendCodeClick,
                        enabled = isEnabled,
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = PrimaryBlue,
                            disabledContentColor = TextSecondaryGray.copy(alpha = 0.5f)
                        )
                    ) {
                        if (uiState.resetState is ResetPasswordState.SendingCode) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                color = PrimaryBlue,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(
                                text = buttonText,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                },
                isError = uiState.codeError != null,
                supportingText = uiState.codeError?.let { { Text(it) } },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                ),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PrimaryBlue,
                    unfocusedBorderColor = DarkNavyCard,
                    focusedLabelColor = PrimaryBlue,
                    unfocusedLabelColor = TextSecondaryGray,
                    focusedTextColor = TextPrimaryWhite,
                    unfocusedTextColor = TextPrimaryWhite,
                    errorBorderColor = ErrorRed,
                    errorSupportingTextColor = ErrorRed,
                    focusedContainerColor = DarkNavyCard,
                    unfocusedContainerColor = DarkNavyCard
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            // 新密码输入框
            OutlinedTextField(
                value = uiState.newPassword,
                onValueChange = onNewPasswordChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("新密码") },
                placeholder = { Text("请设置新密码（至少6位）") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        tint = TextSecondaryGray
                    )
                },
                trailingIcon = {
                    IconButton(onClick = onNewPasswordVisibilityToggle) {
                        Icon(
                            imageVector = if (uiState.isNewPasswordVisible) 
                                Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = if (uiState.isNewPasswordVisible) "隐藏密码" else "显示密码",
                            tint = TextSecondaryGray
                        )
                    }
                },
                isError = uiState.newPasswordError != null,
                supportingText = uiState.newPasswordError?.let { { Text(it) } },
                visualTransformation = if (uiState.isNewPasswordVisible) 
                    VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                ),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PrimaryBlue,
                    unfocusedBorderColor = DarkNavyCard,
                    focusedLabelColor = PrimaryBlue,
                    unfocusedLabelColor = TextSecondaryGray,
                    focusedTextColor = TextPrimaryWhite,
                    unfocusedTextColor = TextPrimaryWhite,
                    errorBorderColor = ErrorRed,
                    errorSupportingTextColor = ErrorRed,
                    focusedContainerColor = DarkNavyCard,
                    unfocusedContainerColor = DarkNavyCard
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            // 确认密码输入框
            OutlinedTextField(
                value = uiState.confirmPassword,
                onValueChange = onConfirmPasswordChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("确认密码") },
                placeholder = { Text("请再次输入新密码") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        tint = TextSecondaryGray
                    )
                },
                trailingIcon = {
                    IconButton(onClick = onConfirmPasswordVisibilityToggle) {
                        Icon(
                            imageVector = if (uiState.isConfirmPasswordVisible) 
                                Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = if (uiState.isConfirmPasswordVisible) "隐藏密码" else "显示密码",
                            tint = TextSecondaryGray
                        )
                    }
                },
                isError = uiState.confirmPasswordError != null,
                supportingText = uiState.confirmPasswordError?.let { { Text(it) } },
                visualTransformation = if (uiState.isConfirmPasswordVisible) 
                    VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = { 
                        focusManager.clearFocus()
                        onResetClick()
                    }
                ),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PrimaryBlue,
                    unfocusedBorderColor = DarkNavyCard,
                    focusedLabelColor = PrimaryBlue,
                    unfocusedLabelColor = TextSecondaryGray,
                    focusedTextColor = TextPrimaryWhite,
                    unfocusedTextColor = TextPrimaryWhite,
                    errorBorderColor = ErrorRed,
                    errorSupportingTextColor = ErrorRed,
                    focusedContainerColor = DarkNavyCard,
                    unfocusedContainerColor = DarkNavyCard
                )
            )

            Spacer(modifier = Modifier.height(32.dp))

            // 重置密码按钮
            Button(
                onClick = onResetClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryBlue,
                    disabledContainerColor = PrimaryBlue.copy(alpha = 0.5f)
                ),
                enabled = uiState.canSubmit && !uiState.isLoading
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White,
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

            Spacer(modifier = Modifier.height(16.dp))

            // 返回登录链接
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "想起密码了？",
                    color = TextSecondaryGray,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "返回登录",
                    color = PrimaryBlue,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.clickable(onClick = onLoginClick)
                )
            }
        }
    }
}

/**
 * 重置密码页预览 - 默认状态
 */
@Preview(name = "Reset Password - Default", showBackground = true)
@Composable
fun ResetPasswordPagePreview() {
    CryptoVPNTheme {
        ResetPasswordPage()
    }
}

/**
 * 重置密码页预览 - 填写中
 */
@Preview(name = "Reset Password - Filling", showBackground = true)
@Composable
fun ResetPasswordPageFillingPreview() {
    CryptoVPNTheme {
        ResetPasswordPage(
            uiState = ResetPasswordUiState(
                email = "user@example.com",
                verificationCode = "123456",
                newPassword = "NewPass123!",
                confirmPassword = "NewPass123!",
                codeState = VerificationCodeState.CountingDown(45)
            )
        )
    }
}

/**
 * 重置密码页预览 - 成功状态
 */
@Preview(name = "Reset Password - Success", showBackground = true)
@Composable
fun ResetPasswordPageSuccessPreview() {
    CryptoVPNTheme {
        ResetPasswordPage(
            uiState = ResetPasswordUiState(
                email = "user@example.com",
                verificationCode = "123456",
                newPassword = "NewPass123!",
                confirmPassword = "NewPass123!",
                resetState = ResetPasswordState.Success("密码重置成功，请使用新密码登录")
            )
        )
    }
}

/**
 * 集成ViewModel的重置密码页面
 */
@Composable
fun ResetPasswordScreen(
    viewModel: ResetPasswordViewModel = remember { ResetPasswordViewModel() },
    onNavigateBack: () -> Unit = {},
    onNavigateToLogin: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val scope = rememberCoroutineScope()

    // 监听重置成功
    LaunchedEffect(uiState.resetState) {
        if (uiState.resetState is ResetPasswordState.Success) {
            // 可以延迟后自动跳转到登录页
        }
    }

    ResetPasswordPage(
        uiState = uiState,
        onEmailChange = viewModel::onEmailChange,
        onVerificationCodeChange = viewModel::onVerificationCodeChange,
        onNewPasswordChange = viewModel::onNewPasswordChange,
        onConfirmPasswordChange = viewModel::onConfirmPasswordChange,
        onNewPasswordVisibilityToggle = viewModel::toggleNewPasswordVisibility,
        onConfirmPasswordVisibilityToggle = viewModel::toggleConfirmPasswordVisibility,
        onSendCodeClick = { scope.launch { viewModel.sendVerificationCode() } },
        onResetClick = viewModel::resetPassword,
        onBackClick = onNavigateBack,
        onLoginClick = onNavigateToLogin
    )
}
