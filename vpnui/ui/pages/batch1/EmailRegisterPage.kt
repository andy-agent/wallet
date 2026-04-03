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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * 密码强度等级
 */
enum class PasswordStrength {
    NONE, WEAK, MEDIUM, STRONG
}

/**
 * 注册状态
 */
sealed class RegisterState {
    object Idle : RegisterState()
    object Loading : RegisterState()
    data class Success(val message: String) : RegisterState()
    data class Error(val message: String) : RegisterState()
}

/**
 * 注册页面 UI State
 */
data class RegisterUiState(
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val isPasswordVisible: Boolean = false,
    val isConfirmPasswordVisible: Boolean = false,
    val registerState: RegisterState = RegisterState.Idle,
    val emailError: String? = null,
    val passwordError: String? = null,
    val confirmPasswordError: String? = null,
    val agreedToTerms: Boolean = false
) {
    val isLoading: Boolean get() = registerState is RegisterState.Loading
    val canSubmit: Boolean get() = email.isNotBlank() && password.isNotBlank() && 
                                   confirmPassword.isNotBlank() && agreedToTerms &&
                                   emailError == null && passwordError == null && 
                                   confirmPasswordError == null
    
    val passwordStrength: PasswordStrength
        get() = when {
            password.isEmpty() -> PasswordStrength.NONE
            password.length < 6 -> PasswordStrength.WEAK
            password.length < 10 && password.matches(Regex(".*[A-Z].*")) && 
                password.matches(Regex(".*[0-9].*")) -> PasswordStrength.MEDIUM
            password.length >= 10 && password.matches(Regex(".*[A-Z].*")) && 
                password.matches(Regex(".*[0-9].*")) && 
                password.matches(Regex(".*[!@#$%^&*].*")) -> PasswordStrength.STRONG
            else -> PasswordStrength.WEAK
        }
}

/**
 * 注册 ViewModel
 */
class RegisterViewModel {
    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    fun onEmailChange(email: String) {
        _uiState.value = _uiState.value.copy(
            email = email,
            emailError = validateEmail(email)
        )
    }

    fun onPasswordChange(password: String) {
        val currentState = _uiState.value
        _uiState.value = currentState.copy(
            password = password,
            passwordError = validatePassword(password),
            confirmPasswordError = if (currentState.confirmPassword.isNotEmpty()) {
                validateConfirmPassword(currentState.confirmPassword, password)
            } else null
        )
    }

    fun onConfirmPasswordChange(confirmPassword: String) {
        _uiState.value = _uiState.value.copy(
            confirmPassword = confirmPassword,
            confirmPasswordError = validateConfirmPassword(confirmPassword, _uiState.value.password)
        )
    }

    fun togglePasswordVisibility() {
        _uiState.value = _uiState.value.copy(
            isPasswordVisible = !_uiState.value.isPasswordVisible
        )
    }

    fun toggleConfirmPasswordVisibility() {
        _uiState.value = _uiState.value.copy(
            isConfirmPasswordVisible = !_uiState.value.isConfirmPasswordVisible
        )
    }

    fun toggleAgreedToTerms() {
        _uiState.value = _uiState.value.copy(
            agreedToTerms = !_uiState.value.agreedToTerms
        )
    }

    fun register() {
        val currentState = _uiState.value
        
        // 验证输入
        val emailError = validateEmail(currentState.email)
        val passwordError = validatePassword(currentState.password)
        val confirmPasswordError = validateConfirmPassword(
            currentState.confirmPassword, 
            currentState.password
        )
        
        if (emailError != null || passwordError != null || confirmPasswordError != null) {
            _uiState.value = currentState.copy(
                emailError = emailError,
                passwordError = passwordError,
                confirmPasswordError = confirmPasswordError
            )
            return
        }
        
        if (!currentState.agreedToTerms) {
            _uiState.value = currentState.copy(
                registerState = RegisterState.Error("请同意服务条款和隐私政策")
            )
            return
        }
        
        // 开始注册
        _uiState.value = currentState.copy(registerState = RegisterState.Loading)
        
        // 模拟注册请求
        // 实际项目中这里调用API
    }

    fun onRegisterSuccess(message: String) {
        _uiState.value = _uiState.value.copy(
            registerState = RegisterState.Success(message)
        )
    }

    fun onRegisterError(message: String) {
        _uiState.value = _uiState.value.copy(
            registerState = RegisterState.Error(message)
        )
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(
            registerState = RegisterState.Idle
        )
    }

    private fun validateEmail(email: String): String? {
        return when {
            email.isBlank() -> null
            !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> "请输入有效的邮箱地址"
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
 * 邮箱注册页
 */
@Composable
fun EmailRegisterPage(
    uiState: RegisterUiState = RegisterUiState(),
    onEmailChange: (String) -> Unit = {},
    onPasswordChange: (String) -> Unit = {},
    onConfirmPasswordChange: (String) -> Unit = {},
    onPasswordVisibilityToggle: () -> Unit = {},
    onConfirmPasswordVisibilityToggle: () -> Unit = {},
    onAgreedToTermsToggle: () -> Unit = {},
    onRegisterClick: () -> Unit = {},
    onBackClick: () -> Unit = {},
    onLoginClick: () -> Unit = {},
    onTermsClick: () -> Unit = {},
    onPrivacyClick: () -> Unit = {}
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
                text = "注册账户",
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
                text = "创建新账户",
                color = TextPrimaryWhite,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "填写以下信息完成注册",
                color = TextSecondaryGray,
                fontSize = 14.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            // 错误提示
            if (uiState.registerState is RegisterState.Error) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    color = ErrorRed.copy(alpha = 0.1f)
                ) {
                    Text(
                        text = uiState.registerState.message,
                        color = ErrorRed,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(12.dp),
                        textAlign = TextAlign.Center
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // 成功提示
            if (uiState.registerState is RegisterState.Success) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    color = SuccessGreen.copy(alpha = 0.1f)
                ) {
                    Text(
                        text = uiState.registerState.message,
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
                placeholder = { Text("请输入邮箱地址") },
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

            // 密码输入框
            OutlinedTextField(
                value = uiState.password,
                onValueChange = onPasswordChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("设置密码") },
                placeholder = { Text("请设置密码（至少6位）") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        tint = TextSecondaryGray
                    )
                },
                trailingIcon = {
                    IconButton(onClick = onPasswordVisibilityToggle) {
                        Icon(
                            imageVector = if (uiState.isPasswordVisible) 
                                Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = if (uiState.isPasswordVisible) "隐藏密码" else "显示密码",
                            tint = TextSecondaryGray
                        )
                    }
                },
                isError = uiState.passwordError != null,
                supportingText = uiState.passwordError?.let { { Text(it) } },
                visualTransformation = if (uiState.isPasswordVisible) 
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

            // 密码强度指示器
            if (uiState.password.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                PasswordStrengthIndicator(strength = uiState.passwordStrength)
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 确认密码输入框
            OutlinedTextField(
                value = uiState.confirmPassword,
                onValueChange = onConfirmPasswordChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("确认密码") },
                placeholder = { Text("请再次输入密码") },
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
                        onRegisterClick()
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

            Spacer(modifier = Modifier.height(16.dp))

            // 服务条款勾选
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = uiState.agreedToTerms,
                    onCheckedChange = { onAgreedToTermsToggle() },
                    colors = CheckboxDefaults.colors(
                        checkedColor = PrimaryBlue,
                        uncheckedColor = TextSecondaryGray
                    )
                )
                
                Spacer(modifier = Modifier.width(4.dp))
                
                Row {
                    Text(
                        text = "我已阅读并同意",
                        color = TextSecondaryGray,
                        fontSize = 12.sp
                    )
                    Text(
                        text = "服务条款",
                        color = PrimaryBlue,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.clickable(onClick = onTermsClick)
                    )
                    Text(
                        text = "和",
                        color = TextSecondaryGray,
                        fontSize = 12.sp
                    )
                    Text(
                        text = "隐私政策",
                        color = PrimaryBlue,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.clickable(onClick = onPrivacyClick)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 注册按钮
            Button(
                onClick = onRegisterClick,
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
                        text = "注册",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 登录链接
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "已有账户？",
                    color = TextSecondaryGray,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "立即登录",
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
 * 密码强度指示器
 */
@Composable
private fun PasswordStrengthIndicator(strength: PasswordStrength) {
    val (color, text) = when (strength) {
        PasswordStrength.NONE -> TextSecondaryGray to ""
        PasswordStrength.WEAK -> ErrorRed to "密码强度：弱"
        PasswordStrength.MEDIUM -> WarningYellow to "密码强度：中"
        PasswordStrength.STRONG -> SuccessGreen to "密码强度：强"
    }

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            repeat(4) { index ->
                val barColor = when {
                    strength == PasswordStrength.NONE -> DarkNavyBackground
                    strength == PasswordStrength.WEAK && index < 1 -> ErrorRed
                    strength == PasswordStrength.MEDIUM && index < 2 -> WarningYellow
                    strength == PasswordStrength.STRONG -> SuccessGreen
                    else -> DarkNavyBackground
                }
                
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(4.dp)
                        .background(barColor, RoundedCornerShape(2.dp))
                )
            }
        }
        
        if (text.isNotEmpty()) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = text,
                color = color,
                fontSize = 11.sp
            )
        }
    }
}

/**
 * 邮箱注册页预览 - 默认状态
 */
@Preview(name = "Register - Default", showBackground = true)
@Composable
fun EmailRegisterPagePreview() {
    CryptoVPNTheme {
        EmailRegisterPage()
    }
}

/**
 * 邮箱注册页预览 - 填写中
 */
@Preview(name = "Register - Filling", showBackground = true)
@Composable
fun EmailRegisterPageFillingPreview() {
    CryptoVPNTheme {
        EmailRegisterPage(
            uiState = RegisterUiState(
                email = "user@example.com",
                password = "Pass123!",
                confirmPassword = "Pass123!",
                agreedToTerms = true
            )
        )
    }
}

/**
 * 邮箱注册页预览 - 错误状态
 */
@Preview(name = "Register - Error", showBackground = true)
@Composable
fun EmailRegisterPageErrorPreview() {
    CryptoVPNTheme {
        EmailRegisterPage(
            uiState = RegisterUiState(
                email = "invalid-email",
                password = "123",
                confirmPassword = "456",
                emailError = "请输入有效的邮箱地址",
                passwordError = "密码长度至少6位",
                confirmPasswordError = "两次输入的密码不一致",
                registerState = RegisterState.Error("注册失败，请稍后重试")
            )
        )
    }
}

/**
 * 集成ViewModel的注册页面
 */
@Composable
fun EmailRegisterScreen(
    viewModel: RegisterViewModel = remember { RegisterViewModel() },
    onNavigateBack: () -> Unit = {},
    onNavigateToLogin: () -> Unit = {},
    onNavigateToHome: () -> Unit = {},
    onNavigateToTerms: () -> Unit = {},
    onNavigateToPrivacy: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    // 监听注册成功
    LaunchedEffect(uiState.registerState) {
        if (uiState.registerState is RegisterState.Success) {
            // 可以延迟后自动跳转到登录页
        }
    }

    EmailRegisterPage(
        uiState = uiState,
        onEmailChange = viewModel::onEmailChange,
        onPasswordChange = viewModel::onPasswordChange,
        onConfirmPasswordChange = viewModel::onConfirmPasswordChange,
        onPasswordVisibilityToggle = viewModel::togglePasswordVisibility,
        onConfirmPasswordVisibilityToggle = viewModel::toggleConfirmPasswordVisibility,
        onAgreedToTermsToggle = viewModel::toggleAgreedToTerms,
        onRegisterClick = viewModel::register,
        onBackClick = onNavigateBack,
        onLoginClick = onNavigateToLogin,
        onTermsClick = onNavigateToTerms,
        onPrivacyClick = onNavigateToPrivacy
    )
}
