package com.v2ray.ang.payment.ui.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.v2ray.ang.R
import com.v2ray.ang.databinding.ActivityCommissionLedgerBinding
import com.v2ray.ang.payment.data.api.CommissionLedgerItem
import com.v2ray.ang.payment.data.api.CommissionSummaryData
import com.v2ray.ang.payment.data.repository.PaymentRepository
import com.v2ray.ang.ui.BaseActivity
import kotlinx.coroutines.launch

class CommissionLedgerActivity : BaseActivity() {

    private lateinit var binding: ActivityCommissionLedgerBinding
    private lateinit var paymentRepository: PaymentRepository
    private lateinit var ledgerAdapter: CommissionLedgerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCommissionLedgerBinding.inflate(layoutInflater)
        setContentViewWithToolbar(binding.root, showHomeAsUp = true, title = getString(R.string.title_commission_ledger))

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
        ledgerAdapter = CommissionLedgerAdapter()
        binding.recyclerViewLedger.apply {
            layoutManager = LinearLayoutManager(this@CommissionLedgerActivity)
            adapter = ledgerAdapter
        }
    }

    private fun setupClickListeners() {
        binding.buttonRefresh.setOnClickListener {
            loadData()
        }
    }

    private fun loadData() {
        lifecycleScope.launch {
            showLoading()

            val summaryResult = paymentRepository.getCommissionSummary()
            summaryResult.onSuccess { data ->
                displaySummary(data)
            }.onFailure { error ->
                showError(error.message ?: getString(R.string.refresh_failed))
            }

            val ledgerResult = paymentRepository.getCommissionLedger()
            ledgerResult.onSuccess { data ->
                displayLedger(data.items)
            }.onFailure { error ->
                showError(error.message ?: getString(R.string.refresh_failed))
            }

            hideLoading()
        }
    }

    private fun displaySummary(data: CommissionSummaryData) {
        binding.textSettlementAsset.text = "${data.settlementAssetCode} (${data.settlementNetworkCode})"
        binding.textAvailableAmount.text = "${data.availableAmount} ${data.settlementAssetCode}"
        binding.textFrozenAmount.text = "${data.frozenAmount} ${data.settlementAssetCode}"
        binding.textWithdrawingAmount.text = "${data.withdrawingAmount} ${data.settlementAssetCode}"
        binding.textWithdrawnTotal.text = "${data.withdrawnTotal} ${data.settlementAssetCode}"
    }

    private fun displayLedger(items: List<CommissionLedgerItem>) {
        ledgerAdapter.submitList(items)

        if (items.isEmpty()) {
            binding.recyclerViewLedger.visibility = View.GONE
            binding.textEmptyLedger.visibility = View.VISIBLE
        } else {
            binding.recyclerViewLedger.visibility = View.VISIBLE
            binding.textEmptyLedger.visibility = View.GONE
        }
    }

    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    inner class CommissionLedgerAdapter : RecyclerView.Adapter<CommissionLedgerAdapter.ViewHolder>() {

        private var items: List<CommissionLedgerItem> = emptyList()

        fun submitList(newItems: List<CommissionLedgerItem>) {
            items = newItems
            notifyDataSetChanged()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_commission_ledger, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.bind(items[position])
        }

        override fun getItemCount(): Int = items.size

        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            private val textEntryNo: TextView = itemView.findViewById(R.id.textEntryNo)
            private val textSourceAccount: TextView = itemView.findViewById(R.id.textSourceAccount)
            private val textCommissionLevel: TextView = itemView.findViewById(R.id.textCommissionLevel)
            private val textAmount: TextView = itemView.findViewById(R.id.textAmount)
            private val textStatus: TextView = itemView.findViewById(R.id.textStatus)
            private val textDate: TextView = itemView.findViewById(R.id.textDate)

            fun bind(item: CommissionLedgerItem) {
                textEntryNo.text = item.entryNo
                textSourceAccount.text = item.sourceAccountMasked
                textCommissionLevel.text = when (item.commissionLevel) {
                    "LEVEL1" -> getString(R.string.level1)
                    "LEVEL2" -> getString(R.string.level2)
                    else -> item.commissionLevel
                }
                textAmount.text = "${item.settlementAmountUsdt} USDT"
                textStatus.text = getStatusText(item.status)
                textDate.text = item.createdAt.substring(0, 10)

                when (item.status) {
                    "AVAILABLE" -> textStatus.setTextColor(getColor(R.color.md_theme_primary))
                    "FROZEN" -> textStatus.setTextColor(getColor(R.color.md_theme_tertiary))
                    else -> textStatus.setTextColor(getColor(R.color.md_theme_onSurfaceVariant))
                }
            }

            private fun getStatusText(status: String): String {
                return when (status) {
                    "AVAILABLE" -> getString(R.string.status_available)
                    "FROZEN" -> getString(R.string.status_frozen)
                    "SETTLED" -> getString(R.string.status_settled)
                    else -> status
                }
            }
        }
    }
}
