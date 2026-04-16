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
import com.v2ray.ang.payment.data.api.UserInfo
import com.v2ray.ang.payment.data.model.AuthData
import com.v2ray.ang.payment.data.model.LoginRequest
import com.v2ray.ang.payment.data.model.RegisterRequest
import com.v2ray.ang.payment.data.repository.PaymentRepository
import kotlinx.coroutines.launch
import java.util.UUID

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var repository: PaymentRepository
    private var isRegisterMode = false

    private val passwordRegex = Regex("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$")
    private val emailRegex = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")

    companion object {
        const val RESULT_CODE_LOGIN_SUCCESS = RESULT_OK
        private const val EXTRA_REGISTER_MODE = "extra_register_mode"
        private const val EXTRA_RETURN_RESULT_ONLY = "extra_return_result_only"

        fun createRegisterIntent(context: android.content.Context): Intent {
            return Intent(context, LoginActivity::class.java).apply {
                putExtra(EXTRA_REGISTER_MODE, true)
                putExtra(EXTRA_RETURN_RESULT_ONLY, true)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        repository = PaymentRepository(this)
        isRegisterMode = intent.getBooleanExtra(EXTRA_REGISTER_MODE, false)

        if (checkAutoLogin()) {
            return
        }

        setupUI()
        setupListeners()
    }

    private fun checkAutoLogin(): Boolean {
        return if (repository.isTokenValid()) {
            finishWithAuthResultOrNavigate()
            true
        } else if (repository.getRefreshToken() != null) {
            lifecycleScope.launch {
                showLoading(true)
                val success = repository.refreshTokenIfNeeded()
                showLoading(false)
                if (success) {
                    finishWithAuthResultOrNavigate()
                }
            }
            true
        } else {
            false
        }
    }

    private fun navigateToUserProfile() {
        startActivity(Intent(this, UserProfileActivity::class.java))
        finish()
    }

    private fun finishWithAuthResultOrNavigate() {
        if (intent.getBooleanExtra(EXTRA_RETURN_RESULT_ONLY, false)) {
            setResult(RESULT_CODE_LOGIN_SUCCESS)
            finish()
        } else {
            navigateToUserProfile()
        }
    }

    private fun setupUI() {
        updateUIForCurrentMode()
    }

    private fun setupListeners() {
        binding.btnAction.setOnClickListener {
            if (isRegisterMode) attemptRegister() else attemptLogin()
        }
        binding.tvToggleAction.setOnClickListener { toggleMode() }

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

    private fun toggleMode() {
        isRegisterMode = !isRegisterMode
        binding.etUsername.text?.clear()
        binding.etPassword.text?.clear()
        binding.etConfirmPassword.text?.clear()
        binding.etEmail.text?.clear()
        binding.tilUsername.error = null
        binding.tilPassword.error = null
        binding.tilConfirmPassword.error = null
        binding.tilEmail.error = null
        updateUIForCurrentMode()
    }

    private fun updateUIForCurrentMode() {
        binding.tilUsername.hint = getString(R.string.email)
        if (isRegisterMode) {
            binding.tvTitle.text = getString(R.string.register)
            binding.tvSubtitle.text = getString(R.string.register_subtitle)
            binding.btnAction.text = getString(R.string.register)
            binding.tvToggleHint.text = getString(R.string.has_account_hint)
            binding.tvToggleAction.text = getString(R.string.login_now)
            binding.tilConfirmPassword.visibility = View.VISIBLE
            binding.tilEmail.visibility = View.GONE
        } else {
            binding.tvTitle.text = getString(R.string.login)
            binding.tvSubtitle.text = getString(R.string.login_subtitle)
            binding.btnAction.text = getString(R.string.login)
            binding.tvToggleHint.text = getString(R.string.no_account_hint)
            binding.tvToggleAction.text = getString(R.string.register_now)
            binding.tilConfirmPassword.visibility = View.GONE
            binding.tilEmail.visibility = View.GONE
        }
    }

    private fun attemptLogin() {
        val email = binding.etUsername.text.toString().trim()
        val password = binding.etPassword.text.toString()
        if (!validateLoginInput(email, password)) return

        showLoading(true)
        lifecycleScope.launch {
            try {
                val response = repository.api.login(
                    LoginRequest(
                        email = email,
                        password = password,
                        installationId = repository.getDeviceId(),
                    ),
                )
                if (response.isSuccessful && response.body()?.code == "OK") {
                    response.body()?.data?.let { handleAuthSuccess(it, email, false) }
                        ?: showError(getString(R.string.error_operation_failed, "Empty response data"))
                } else {
                    showError(response.body()?.message ?: getString(R.string.error_operation_failed, "HTTP ${response.code()}"))
                }
            } catch (e: Exception) {
                showError(getString(R.string.error_network))
            } finally {
                showLoading(false)
            }
        }
    }

    private fun attemptRegister() {
        val email = binding.etUsername.text.toString().trim()
        val password = binding.etPassword.text.toString()
        val confirmPassword = binding.etConfirmPassword.text.toString()
        if (!validateRegisterInput(email, password, confirmPassword)) return

        showLoading(true)
        lifecycleScope.launch {
            try {
                val response = repository.api.register(
                    UUID.randomUUID().toString(),
                    RegisterRequest(
                        email = email,
                        password = password,
                        installationId = repository.getDeviceId()
                    )
                )

                if (response.isSuccessful && response.body()?.code == "OK") {
                    response.body()?.data?.let { handleAuthSuccess(it, email, true) }
                        ?: showError(getString(R.string.error_operation_failed, "Empty response data"))
                } else {
                    showError(response.body()?.message ?: getString(R.string.error_operation_failed, "HTTP ${response.code()}"))
                }
            } catch (e: Exception) {
                showError(getString(R.string.error_network))
            } finally {
                showLoading(false)
            }
        }
    }

    private fun validateLoginInput(email: String, password: String): Boolean {
        var isValid = true
        if (email.isEmpty() || !emailRegex.matches(email)) {
            binding.tilUsername.error = getString(R.string.error_email_invalid)
            isValid = false
        }
        if (password.isEmpty()) {
            binding.tilPassword.error = getString(R.string.error_password_too_short)
            isValid = false
        }
        return isValid
    }

    private fun validateRegisterInput(
        email: String,
        password: String,
        confirmPassword: String
    ): Boolean {
        var isValid = true
        if (!emailRegex.matches(email)) {
            binding.tilUsername.error = getString(R.string.error_email_invalid)
            isValid = false
        }
        if (password.length < 8) {
            binding.tilPassword.error = getString(R.string.error_password_too_short)
            isValid = false
        } else if (!passwordRegex.matches(password)) {
            binding.tilPassword.error = getString(R.string.error_password_requirements)
            isValid = false
        }
        if (password != confirmPassword) {
            binding.tilConfirmPassword.error = getString(R.string.error_passwords_not_match)
            isValid = false
        }
        return isValid
    }

    private fun handleAuthSuccess(authData: AuthData, email: String, isRegistration: Boolean) {
        repository.saveAuthResponse(authData)

        lifecycleScope.launch {
            try {
                val userInfo = UserInfo(
                    username = email,
                    status = authData.accountStatus,
                    expiredAt = authData.expiresAt,
                    trafficTotal = 0L,
                    trafficUsed = 0L,
                    trafficRemaining = 0L
                )
                val userEntity = com.v2ray.ang.payment.data.local.entity.UserEntity(
                    userId = authData.userId,
                    username = userInfo.username,
                    email = email,
                    accessToken = authData.accessToken,
                    refreshToken = authData.refreshToken,
                    loginAt = System.currentTimeMillis()
                )
                repository.getLocalRepository().saveUser(userEntity)
                repository.saveCurrentUserId(authData.userId)

                Toast.makeText(
                    this@LoginActivity,
                    if (isRegistration) getString(R.string.register_success) else getString(R.string.login_success),
                    Toast.LENGTH_SHORT
                ).show()

                setResult(RESULT_CODE_LOGIN_SUCCESS, Intent().apply {
                    putExtra("username", email)
                    putExtra("access_token", authData.accessToken)
                })
                if (intent.getBooleanExtra(EXTRA_RETURN_RESULT_ONLY, false)) {
                    finish()
                } else {
                    navigateToUserProfile()
                }
            } catch (e: Exception) {
                showError(getString(R.string.error_operation_failed, e.message ?: "Cache failed"))
            }
        }
    }

    private fun showLoading(show: Boolean) {
        binding.progressBar.visibility = if (show) View.VISIBLE else View.GONE
        binding.btnAction.isEnabled = !show
        binding.btnAction.alpha = if (show) 0.7f else 1.0f
    }

    private fun showError(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
    }
}
