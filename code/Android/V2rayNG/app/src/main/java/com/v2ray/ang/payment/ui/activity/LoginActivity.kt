package com.v2ray.ang.payment.ui.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import com.v2ray.ang.R
import com.v2ray.ang.databinding.ActivityLoginBinding
import com.v2ray.ang.payment.data.model.AuthData
import com.v2ray.ang.payment.data.api.UserInfo
import com.v2ray.ang.payment.data.model.LoginRequest
import com.v2ray.ang.payment.data.model.RegisterRequest
import com.v2ray.ang.payment.data.repository.PaymentRepository
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * 登录/注册页面
 * 
 * 支持两种模式：
 * 1. 登录模式 - 输入用户名和密码进行登录
 * 2. 注册模式 - 输入用户名、密码、确认密码和可选邮箱进行注册
 */
class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var repository: PaymentRepository

    /**
     * 当前模式：true为注册模式，false为登录模式
     */
    private var isRegisterMode = false

    /**
     * 用户名验证正则：3-64字符，字母数字下划线
     */
    private val usernameRegex = Regex("^[a-zA-Z0-9_]{3,64}$")

    /**
     * 密码验证正则：至少8字符，包含大小写字母和数字
     */
    private val passwordRegex = Regex("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$")

    /**
     * 邮箱验证正则
     */
    private val emailRegex = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")

    companion object {
        const val RESULT_CODE_LOGIN_SUCCESS = RESULT_OK
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        repository = PaymentRepository(this)

        // 检查自动登录状态
        if (checkAutoLogin()) {
            return
        }

        setupUI()
        setupListeners()
    }

    /**
     * 检查自动登录
     * @return true 表示已自动登录并跳转，false 表示需要显示登录界面
     */
    private fun checkAutoLogin(): Boolean {
        // 检查是否有有效 Token
        if (repository.isTokenValid()) {
            // Token 有效，直接跳转到用户中心
            navigateToUserProfile()
            return true
        } else if (repository.getRefreshToken() != null) {
            // Token 过期但 refresh_token 存在，尝试刷新
            lifecycleScope.launch {
                showLoading(true)
                val success = repository.refreshTokenIfNeeded()
                showLoading(false)
                if (success) {
                    navigateToUserProfile()
                }
                // 刷新失败则留在登录页面
            }
            // 显示加载状态，等待刷新结果
            return true
        }
        return false
    }

    /**
     * 跳转到用户中心
     */
    private fun navigateToUserProfile() {
        startActivity(Intent(this, UserProfileActivity::class.java))
        finish()
    }

    /**
     * 设置UI初始状态
     */
    private fun setupUI() {
        updateUIForCurrentMode()
    }

    /**
     * 设置事件监听器
     */
    private fun setupListeners() {
        // 登录/注册按钮点击
        binding.btnAction.setOnClickListener {
            if (isRegisterMode) {
                attemptRegister()
            } else {
                attemptLogin()
            }
        }

        // 切换模式链接点击
        binding.tvToggleAction.setOnClickListener {
            toggleMode()
        }

        // 输入框焦点变化时清除错误
        binding.etUsername.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) binding.tilUsername.error = null
        }
        binding.etPassword.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) binding.tilPassword.error = null
        }
        binding.etConfirmPassword.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) binding.tilConfirmPassword.error = null
        }
        binding.etEmail.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) binding.tilEmail.error = null
        }
    }

    /**
     * 切换登录/注册模式
     */
    private fun toggleMode() {
        isRegisterMode = !isRegisterMode
        
        // 清除所有输入和错误
        clearAllInputs()
        clearAllErrors()
        
        updateUIForCurrentMode()
    }

    /**
     * 根据当前模式更新UI
     */
    private fun updateUIForCurrentMode() {
        if (isRegisterMode) {
            // 注册模式
            binding.tvTitle.text = getString(R.string.register)
            binding.tvSubtitle.text = getString(R.string.register_subtitle)
            binding.btnAction.text = getString(R.string.register)
            binding.tvToggleHint.text = getString(R.string.has_account_hint)
            binding.tvToggleAction.text = getString(R.string.login_now)
            
            // 显示注册专属字段
            binding.tilConfirmPassword.visibility = View.VISIBLE
            binding.tilEmail.visibility = View.VISIBLE
        } else {
            // 登录模式
            binding.tvTitle.text = getString(R.string.login)
            binding.tvSubtitle.text = getString(R.string.login_subtitle)
            binding.btnAction.text = getString(R.string.login)
            binding.tvToggleHint.text = getString(R.string.no_account_hint)
            binding.tvToggleAction.text = getString(R.string.register_now)
            
            // 隐藏注册专属字段
            binding.tilConfirmPassword.visibility = View.GONE
            binding.tilEmail.visibility = View.GONE
        }
    }

    /**
     * 清除所有输入
     */
    private fun clearAllInputs() {
        binding.etUsername.text?.clear()
        binding.etPassword.text?.clear()
        binding.etConfirmPassword.text?.clear()
        binding.etEmail.text?.clear()
    }

    /**
     * 清除所有错误提示
     */
    private fun clearAllErrors() {
        binding.tilUsername.error = null
        binding.tilPassword.error = null
        binding.tilConfirmPassword.error = null
        binding.tilEmail.error = null
    }

    /**
     * 尝试登录
     */
    private fun attemptLogin() {
        val username = binding.etUsername.text.toString().trim()
        val password = binding.etPassword.text.toString()

        // 验证输入
        if (!validateLoginInput(username, password)) {
            return
        }

        // 显示加载状态
        showLoading(true)

        // 执行登录
        lifecycleScope.launch {
            try {
                val response = repository.api.login(LoginRequest(username, password))
                
                if (response.isSuccessful) {
                    val body = response.body()
                    when (body?.code) {
                        "SUCCESS" -> {
                            body.data?.let { authData ->
                                handleAuthSuccess(authData)
                            } ?: run {
                                showError(getString(R.string.error_operation_failed, "Empty response data"))
                            }
                        }
                        else -> {
                            showError(body?.message ?: getString(R.string.error_operation_failed, body?.code ?: "Unknown"))
                        }
                    }
                } else {
                    handleHttpError(response.code())
                }
            } catch (e: Exception) {
                showError(getString(R.string.error_network))
            } finally {
                showLoading(false)
            }
        }
    }

    /**
     * 尝试注册
     */
    private fun attemptRegister() {
        val username = binding.etUsername.text.toString().trim()
        val password = binding.etPassword.text.toString()
        val confirmPassword = binding.etConfirmPassword.text.toString()
        val email = binding.etEmail.text.toString().trim()

        // 验证输入
        if (!validateRegisterInput(username, password, confirmPassword, email)) {
            return
        }

        // 显示加载状态
        showLoading(true)

        // 执行注册
        lifecycleScope.launch {
            try {
                val request = RegisterRequest(
                    username = username,
                    password = password,
                    email = email.takeIf { it.isNotEmpty() }
                )
                val response = repository.api.register(request)
                
                if (response.isSuccessful) {
                    val body = response.body()
                    when (body?.code) {
                        "SUCCESS" -> {
                            body.data?.let { authData ->
                                handleAuthSuccess(authData, isRegistration = true)
                            } ?: run {
                                showError(getString(R.string.error_operation_failed, "Empty response data"))
                            }
                        }
                        else -> {
                            showError(body?.message ?: getString(R.string.error_operation_failed, body?.code ?: "Unknown"))
                        }
                    }
                } else {
                    handleHttpError(response.code())
                }
            } catch (e: Exception) {
                showError(getString(R.string.error_network))
            } finally {
                showLoading(false)
            }
        }
    }

    /**
     * 验证登录输入
     */
    private fun validateLoginInput(username: String, password: String): Boolean {
        var isValid = true

        // 验证用户名
        when {
            username.isEmpty() -> {
                binding.tilUsername.error = getString(R.string.error_username_too_short)
                isValid = false
            }
            username.length < 3 -> {
                binding.tilUsername.error = getString(R.string.error_username_too_short)
                isValid = false
            }
            !usernameRegex.matches(username) -> {
                binding.tilUsername.error = getString(R.string.error_username_invalid)
                isValid = false
            }
        }

        // 验证密码
        if (password.isEmpty()) {
            binding.tilPassword.error = getString(R.string.error_password_too_short)
            isValid = false
        }

        return isValid
    }

    /**
     * 验证注册输入
     */
    private fun validateRegisterInput(
        username: String,
        password: String,
        confirmPassword: String,
        email: String
    ): Boolean {
        var isValid = true

        // 验证用户名
        when {
            username.length < 3 -> {
                binding.tilUsername.error = getString(R.string.error_username_too_short)
                isValid = false
            }
            username.length > 64 -> {
                binding.tilUsername.error = getString(R.string.error_username_too_long)
                isValid = false
            }
            !usernameRegex.matches(username) -> {
                binding.tilUsername.error = getString(R.string.error_username_invalid)
                isValid = false
            }
        }

        // 验证密码
        when {
            password.length < 8 -> {
                binding.tilPassword.error = getString(R.string.error_password_too_short)
                isValid = false
            }
            !passwordRegex.matches(password) -> {
                binding.tilPassword.error = getString(R.string.error_password_requirements)
                isValid = false
            }
        }

        // 验证确认密码
        if (password != confirmPassword) {
            binding.tilConfirmPassword.error = getString(R.string.error_passwords_not_match)
            isValid = false
        }

        // 验证邮箱（如果填写了）
        if (email.isNotEmpty() && !emailRegex.matches(email)) {
            binding.tilEmail.error = getString(R.string.error_email_invalid)
            isValid = false
        }

        return isValid
    }

    /**
     * 处理HTTP错误
     */
    private fun handleHttpError(code: Int) {
        val message = when (code) {
            401 -> getString(R.string.error_unauthorized)
            409 -> getString(R.string.error_user_exists)
            422 -> getString(R.string.error_invalid_input, "Validation failed")
            else -> getString(R.string.error_operation_failed, "HTTP $code")
        }
        showError(message)
    }

    /**
     * 处理认证成功
     */
    private fun handleAuthSuccess(authData: AuthData, isRegistration: Boolean = false) {
        // 1. 保存完整的认证信息到 SharedPreferences（包含 access_token, refresh_token, expires_at）
        repository.saveAuthResponse(authData)
        
        // 2. 缓存用户信息到 Room 数据库（同时保存 refresh_token）
        lifecycleScope.launch {
            try {
                val userInfo = UserInfo(
                    username = authData.username,
                    status = "active",
                    expiredAt = authData.expiresAt,
                    trafficTotal = 0L,
                    trafficUsed = 0L,
                    trafficRemaining = 0L
                )
                // 创建包含 refresh_token 的 UserEntity
                val userEntity = com.v2ray.ang.payment.data.local.entity.UserEntity(
                    userId = authData.userId,
                    username = authData.username,
                    email = null,
                    accessToken = authData.accessToken,
                    refreshToken = authData.refreshToken,
                    loginAt = System.currentTimeMillis()
                )
                repository.getLocalRepository().saveUser(userEntity)
                repository.saveCurrentUserId(authData.userId)
                
                // 显示成功消息
                val successMessage = if (isRegistration) {
                    getString(R.string.register_success)
                } else {
                    getString(R.string.login_success)
                }
                Toast.makeText(this@LoginActivity, successMessage, Toast.LENGTH_SHORT).show()
                
                // 3. 返回结果给调用者
                setResult(RESULT_CODE_LOGIN_SUCCESS, Intent().apply {
                    putExtra("username", authData.username)
                    putExtra("access_token", authData.accessToken)
                })
                
                // 4. 跳转到用户中心
                navigateToUserProfile()
                
            } catch (e: Exception) {
                showError(getString(R.string.error_operation_failed, e.message ?: "Cache failed"))
            }
        }
    }

    /**
     * 显示/隐藏加载状态
     */
    private fun showLoading(show: Boolean) {
        binding.progressBar.visibility = if (show) View.VISIBLE else View.GONE
        binding.btnAction.isEnabled = !show
        binding.btnAction.alpha = if (show) 0.7f else 1.0f
    }

    /**
     * 显示错误信息
     */
    private fun showError(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
    }

    /**
     * 解析日期字符串为时间戳
     */
    private fun parseDate(dateString: String): Long? {
        return try {
            val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
            format.parse(dateString)?.time
        } catch (e: Exception) {
            null
        }
    }
}
