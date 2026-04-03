package com.cryptovpn.ui.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.cryptovpn.ui.theme.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * 登录状态
 */
sealed class LoginState {
    object Idle : LoginState()
    object Loading : LoginState()
    data class Success(val token: String) : LoginState()
    data class Error(val message: String) : LoginState()
}

/**
 * 登录页面 UI State
 */
data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isPasswordVisible: Boolean = false,
    val loginState: LoginState = LoginState.Idle,
    val emailError: String? = null,
    val passwordError: String? = null
) {
    val isLoading: Boolean get() = loginState is LoginState.Loading
    val canSubmit: Boolean get() = email.isNotBlank() && password.isNotBlank() && 
                                   emailError == null && passwordError == null
}

/**
 * 登录 ViewModel
 */
class LoginViewModel {
    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onEmailChange(email: String) {
        _uiState.value = _uiState.value.copy(
            email = email,
            emailError = validateEmail(email)
        )
    }

    fun onPasswordChange(password: String) {
        _uiState.value = _uiState.value.copy(
            password = password,
            passwordError = validatePassword(password)
        )
    }

    fun togglePasswordVisibility() {
        _uiState.value = _uiState.value.copy(
            isPasswordVisible = !_uiState.value.isPasswordVisible
        )
    }

    fun login() {
        val currentState = _uiState.value
        
        // 验证输入
        val emailError = validateEmail(currentState.email)
        val passwordError = validatePassword(currentState.password)
        
        if (emailError != null || passwordError != null) {
            _uiState.value = currentState.copy(
                emailError = emailError,
                passwordError = passwordError
            )
            return
        }
        
        // 开始登录
        _uiState.value = currentState.copy(loginState = LoginState.Loading)
        
        // 模拟登录请求
        // 实际项目中这里调用API
    }

    fun onLoginSuccess(token: String) {
        _uiState.value = _uiState.value.copy(
            loginState = LoginState.Success(token)
        )
    }

    fun onLoginError(message: String) {
        _uiState.value = _uiState.value.copy(
            loginState = LoginState.Error(message)
        )
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(
            loginState = LoginState.Idle,
            emailError = null,
            passwordError = null
        )
    }

    private fun validateEmail(email: String): String? {
        return when {
            email.isBlank() -> null // 不显示错误，只是不能提交
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
}

/**
 * 邮箱登录页
 * 
 * @param uiState 页面状态
 * @param onEmailChange 邮箱变更
 * @param onPasswordChange 密码变更
 * @param onPasswordVisibilityToggle 密码可见性切换
 * @param onLoginClick 登录按钮点击
 * @param onForgotPasswordClick 忘记密码点击
 * @param onRegisterClick 注册点击
 */
@Composable
fun EmailLoginPage(
    uiState: LoginUiState = LoginUiState(),
    onEmailChange: (String) -> Unit = {},
    onPasswordChange: (String) -> Unit = {},
    onPasswordVisibilityToggle: () -> Unit = {},
    onLoginClick: () -> Unit = {},
    onForgotPasswordClick: () -> Unit = {},
    onRegisterClick: () -> Unit = {}
) {
    val focusManager = LocalFocusManager.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkNavyBackground)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 标题区域
            Text(
                text = "欢迎回来",
                color = TextPrimaryWhite,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "登录您的CryptoVPN账户",
                color = TextSecondaryGray,
                fontSize = 14.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(40.dp))

            // 错误提示
            if (uiState.loginState is LoginState.Error) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    color = ErrorRed.copy(alpha = 0.1f)
                ) {
                    Text(
                        text = uiState.loginState.message,
                        color = ErrorRed,
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

            Spacer(modifier = Modifier.height(16.dp))

            // 密码输入框
            OutlinedTextField(
                value = uiState.password,
                onValueChange = onPasswordChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("密码") },
                placeholder = { Text("请输入密码") },
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
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = { 
                        focusManager.clearFocus()
                        onLoginClick()
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

            Spacer(modifier = Modifier.height(8.dp))

            // 忘记密码链接
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.CenterEnd
            ) {
                Text(
                    text = "忘记密码？",
                    color = PrimaryBlue,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.clickable(onClick = onForgotPasswordClick)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // 登录按钮
            Button(
                onClick = onLoginClick,
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
                    text = "还没有账户？",
                    color = TextSecondaryGray,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "立即注册",
                    color = PrimaryBlue,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.clickable(onClick = onRegisterClick)
                )
            }
        }
    }
}

/**
 * 邮箱登录页预览 - 默认状态
 */
@Preview(name = "Login - Default", showBackground = true)
@Composable
fun EmailLoginPagePreview() {
    CryptoVPNTheme {
        EmailLoginPage(
            uiState = LoginUiState()
        )
    }
}

/**
 * 邮箱登录页预览 - 加载中
 */
@Preview(name = "Login - Loading", showBackground = true)
@Composable
fun EmailLoginPageLoadingPreview() {
    CryptoVPNTheme {
        EmailLoginPage(
            uiState = LoginUiState(
                email = "user@example.com",
                password = "password123",
                loginState = LoginState.Loading
            )
        )
    }
}

/**
 * 邮箱登录页预览 - 错误状态
 */
@Preview(name = "Login - Error", showBackground = true)
@Composable
fun EmailLoginPageErrorPreview() {
    CryptoVPNTheme {
        EmailLoginPage(
            uiState = LoginUiState(
                email = "invalid-email",
                password = "123",
                emailError = "请输入有效的邮箱地址",
                passwordError = "密码长度至少6位",
                loginState = LoginState.Error("邮箱或密码错误")
            )
        )
    }
}

/**
 * 集成ViewModel的登录页面
 */
@Composable
fun EmailLoginScreen(
    viewModel: LoginViewModel = remember { LoginViewModel() },
    onNavigateToHome: () -> Unit = {},
    onNavigateToForgotPassword: () -> Unit = {},
    onNavigateToRegister: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    // 监听登录成功
    LaunchedEffect(uiState.loginState) {
        if (uiState.loginState is LoginState.Success) {
            onNavigateToHome()
        }
    }

    EmailLoginPage(
        uiState = uiState,
        onEmailChange = viewModel::onEmailChange,
        onPasswordChange = viewModel::onPasswordChange,
        onPasswordVisibilityToggle = viewModel::togglePasswordVisibility,
        onLoginClick = viewModel::login,
        onForgotPasswordClick = onNavigateToForgotPassword,
        onRegisterClick = onNavigateToRegister
    )
}
