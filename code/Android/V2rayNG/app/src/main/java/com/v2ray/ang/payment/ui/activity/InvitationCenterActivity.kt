package com.v2ray.ang.payment.ui.activity

import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.v2ray.ang.R
import com.v2ray.ang.databinding.ActivityInvitationCenterBinding
import com.v2ray.ang.payment.data.repository.PaymentRepository
import com.v2ray.ang.ui.BaseActivity
import kotlinx.coroutines.launch

class InvitationCenterActivity : BaseActivity() {

    private lateinit var binding: ActivityInvitationCenterBinding
    private lateinit var paymentRepository: PaymentRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInvitationCenterBinding.inflate(layoutInflater)
        setContentViewWithToolbar(binding.root, showHomeAsUp = true, title = getString(R.string.title_invitation_center))

        paymentRepository = PaymentRepository(this)

        setupClickListeners()
        loadData()
    }

    override fun onResume() {
        super.onResume()
        loadData()
    }

    private fun setupClickListeners() {
        binding.btnBindReferral.setOnClickListener {
            val code = binding.etReferralCode.text.toString().trim()
            if (code.isNotEmpty()) {
                bindReferralCode(code)
            } else {
                showError(getString(R.string.error_referral_code_empty))
            }
        }
    }

    private fun loadData() {
        lifecycleScope.launch {
            showLoading()

            val result = paymentRepository.getReferralOverview()
            result.onSuccess { data ->
                displayData(data)
            }.onFailure { error ->
                showError(error.message ?: getString(R.string.refresh_failed))
            }

            hideLoading()
        }
    }

    private fun displayData(data: com.v2ray.ang.payment.data.api.ReferralOverviewData) {
        binding.textReferralCode.text = data.referralCode

        if (data.hasBinding) {
            binding.textBindingStatus.text = getString(R.string.referral_bound)
            binding.textBindingStatus.setTextColor(getColor(R.color.md_theme_primary))
            binding.cardBindReferral.visibility = android.view.View.GONE
        } else {
            binding.textBindingStatus.text = getString(R.string.referral_not_bound)
            binding.textBindingStatus.setTextColor(getColor(R.color.md_theme_outline))
            binding.cardBindReferral.visibility = android.view.View.VISIBLE
        }

        binding.textLevel1Count.text = data.level1InviteCount.toString()
        binding.textLevel2Count.text = data.level2InviteCount.toString()

        binding.textLevel1Income.text = "${data.level1IncomeUsdt} USDT"
        binding.textLevel2Income.text = "${data.level2IncomeUsdt} USDT"

        binding.textAvailableAmount.text = "${data.availableAmountUsdt} USDT"
        binding.textFrozenAmount.text = "${data.frozenAmountUsdt} USDT"
        binding.textMinWithdraw.text = getString(R.string.min_withdraw_amount) + ": ${data.minWithdrawAmountUsdt} USDT"
    }

    private fun bindReferralCode(code: String) {
        lifecycleScope.launch {
            showLoading()

            val result = paymentRepository.bindReferralCode(code)
            result.onSuccess {
                Toast.makeText(this@InvitationCenterActivity, R.string.bind_referral_success, Toast.LENGTH_SHORT).show()
                binding.etReferralCode.text?.clear()
                loadData()
            }.onFailure { error ->
                showError(error.message ?: getString(R.string.bind_referral_failed))
            }

            hideLoading()
        }
    }

    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
