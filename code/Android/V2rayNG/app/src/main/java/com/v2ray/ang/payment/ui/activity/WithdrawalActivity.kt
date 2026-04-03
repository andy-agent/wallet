package com.v2ray.ang.payment.ui.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.v2ray.ang.R
import com.v2ray.ang.databinding.ActivityWithdrawalBinding
import com.v2ray.ang.payment.data.api.WithdrawalItem
import com.v2ray.ang.payment.data.repository.PaymentRepository
import com.v2ray.ang.ui.BaseActivity
import kotlinx.coroutines.launch

class WithdrawalActivity : BaseActivity() {

    private lateinit var binding: ActivityWithdrawalBinding
    private lateinit var paymentRepository: PaymentRepository
    private lateinit var withdrawalAdapter: WithdrawalAdapter

    private var availableAmount: String = "0"
    private var minWithdrawAmount: String = "0"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWithdrawalBinding.inflate(layoutInflater)
        setContentViewWithToolbar(binding.root, showHomeAsUp = true, title = getString(R.string.title_withdrawal))

        paymentRepository = PaymentRepository(this)

        setupRecyclerView()
        setupClickListeners()
        loadData()
    }

    override fun onResume() {
        super.onResume()
        loadData()
    }

    private fun setupRecyclerView() {
        withdrawalAdapter = WithdrawalAdapter()
        binding.recyclerViewWithdrawals.apply {
            layoutManager = LinearLayoutManager(this@WithdrawalActivity)
            adapter = withdrawalAdapter
        }
    }

    private fun setupClickListeners() {
        binding.buttonSubmitWithdrawal.setOnClickListener {
            submitWithdrawal()
        }

        binding.buttonRefresh.setOnClickListener {
            loadData()
        }
    }

    private fun loadData() {
        lifecycleScope.launch {
            showLoading()

            val summaryResult = paymentRepository.getCommissionSummary()
            summaryResult.onSuccess { data ->
                availableAmount = data.availableAmount
                binding.textAvailableAmount.text = "${data.availableAmount} ${data.settlementAssetCode}"
            }.onFailure { error ->
                showError(error.message ?: getString(R.string.refresh_failed))
            }

            val referralResult = paymentRepository.getReferralOverview()
            referralResult.onSuccess { data ->
                minWithdrawAmount = data.minWithdrawAmountUsdt
                binding.textMinWithdrawAmount.text = getString(R.string.minimum) + ": ${data.minWithdrawAmountUsdt} USDT"
            }.onFailure { error ->
                showError(error.message ?: getString(R.string.refresh_failed))
            }

            loadWithdrawals()

            hideLoading()
        }
    }

    private suspend fun loadWithdrawals() {
        val result = paymentRepository.getWithdrawals()
        result.onSuccess { data ->
            displayWithdrawals(data.items)
        }.onFailure { error ->
            showError(error.message ?: getString(R.string.refresh_failed))
        }
    }

    private fun displayWithdrawals(items: List<WithdrawalItem>) {
        withdrawalAdapter.submitList(items)

        if (items.isEmpty()) {
            binding.recyclerViewWithdrawals.visibility = View.GONE
            binding.textEmptyWithdrawals.visibility = View.VISIBLE
        } else {
            binding.recyclerViewWithdrawals.visibility = View.VISIBLE
            binding.textEmptyWithdrawals.visibility = View.GONE
        }
    }

    private fun submitWithdrawal() {
        val amount = binding.editAmount.text.toString().trim()
        val address = binding.editPayoutAddress.text.toString().trim()

        if (amount.isEmpty()) {
            binding.editAmount.error = getString(R.string.error_amount_required)
            return
        }

        if (address.isEmpty()) {
            binding.editPayoutAddress.error = getString(R.string.error_address_required)
            return
        }

        try {
            val amountValue = amount.toDouble()
            val minValue = minWithdrawAmount.toDouble()
            val availableValue = availableAmount.toDouble()

            if (amountValue < minValue) {
                binding.editAmount.error = getString(R.string.error_amount_too_small, minWithdrawAmount)
                return
            }

            if (amountValue > availableValue) {
                binding.editAmount.error = getString(R.string.error_amount_too_large)
                return
            }
        } catch (e: NumberFormatException) {
            binding.editAmount.error = getString(R.string.error_invalid_amount)
            return
        }

        AlertDialog.Builder(this)
            .setTitle(R.string.confirm_withdrawal)
            .setMessage(getString(R.string.withdrawal_confirm_message, amount, address))
            .setPositiveButton(R.string.confirm) { _, _ ->
                performWithdrawal(amount, address)
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    private fun performWithdrawal(amount: String, address: String) {
        lifecycleScope.launch {
            showLoading()

            val result = paymentRepository.createWithdrawal(amount, address)
            result.onSuccess {
                Toast.makeText(this@WithdrawalActivity, R.string.withdrawal_submitted, Toast.LENGTH_SHORT).show()
                binding.editAmount.text?.clear()
                binding.editPayoutAddress.text?.clear()
                loadWithdrawals()
            }.onFailure { error ->
                showError(error.message ?: getString(R.string.withdrawal_failed))
            }

            hideLoading()
        }
    }

    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    inner class WithdrawalAdapter : RecyclerView.Adapter<WithdrawalAdapter.ViewHolder>() {

        private var items: List<WithdrawalItem> = emptyList()

        fun submitList(newItems: List<WithdrawalItem>) {
            items = newItems
            notifyDataSetChanged()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_withdrawal, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.bind(items[position])
        }

        override fun getItemCount(): Int = items.size

        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            private val cardView: MaterialCardView = itemView.findViewById(R.id.cardWithdrawal)
            private val textRequestNo: TextView = itemView.findViewById(R.id.textRequestNo)
            private val textAmount: TextView = itemView.findViewById(R.id.textAmount)
            private val textStatus: TextView = itemView.findViewById(R.id.textStatus)
            private val textPayoutAddress: TextView = itemView.findViewById(R.id.textPayoutAddress)
            private val textDate: TextView = itemView.findViewById(R.id.textDate)

            fun bind(item: WithdrawalItem) {
                textRequestNo.text = item.requestNo
                textAmount.text = "${item.amount} ${item.assetCode}"
                textStatus.text = getStatusText(item.status)
                textPayoutAddress.text = item.payoutAddress
                textDate.text = item.createdAt.substring(0, 10)

                when (item.status) {
                    "COMPLETED" -> textStatus.setTextColor(getColor(R.color.md_theme_primary))
                    "PENDING", "REVIEWING" -> textStatus.setTextColor(getColor(R.color.md_theme_tertiary))
                    "FAILED", "REJECTED" -> textStatus.setTextColor(getColor(R.color.md_theme_error))
                    else -> textStatus.setTextColor(getColor(R.color.md_theme_onSurfaceVariant))
                }

                cardView.setOnClickListener {
                    showWithdrawalDetail(item)
                }
            }

            private fun getStatusText(status: String): String {
                return when (status) {
                    "PENDING" -> getString(R.string.status_pending)
                    "REVIEWING" -> getString(R.string.status_reviewing)
                    "COMPLETED" -> getString(R.string.status_fulfilled)
                    "FAILED" -> getString(R.string.status_failed)
                    "REJECTED" -> getString(R.string.status_rejected)
                    else -> status
                }
            }

            private fun showWithdrawalDetail(item: WithdrawalItem) {
                val message = StringBuilder().apply {
                    appendLine("${getString(R.string.request_no)}: ${item.requestNo}")
                    appendLine("${getString(R.string.amount)}: ${item.amount} ${item.assetCode}")
                    appendLine("${getString(R.string.payout_address)}: ${item.payoutAddress}")
                    appendLine("${getString(R.string.status)}: ${getStatusText(item.status)}")
                    appendLine("${getString(R.string.created_at)}: ${item.createdAt}")
                    item.txHash?.let {
                        appendLine("${getString(R.string.tx_hash)}: $it")
                    }
                    item.failReason?.let {
                        appendLine("${getString(R.string.fail_reason)}: $it")
                    }
                }

                AlertDialog.Builder(itemView.context)
                    .setTitle(R.string.withdrawal_detail)
                    .setMessage(message.toString())
                    .setPositiveButton(R.string.ok, null)
                    .show()
            }
        }
    }
}
