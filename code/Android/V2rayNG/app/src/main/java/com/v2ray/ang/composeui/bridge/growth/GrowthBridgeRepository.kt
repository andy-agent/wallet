package com.v2ray.ang.composeui.bridge.growth

import android.content.Context
import com.v2ray.ang.payment.data.api.CommissionLedgerItem
import com.v2ray.ang.payment.data.api.CommissionSummaryData
import com.v2ray.ang.payment.data.api.ReferralOverviewData
import com.v2ray.ang.payment.data.api.WithdrawalItem
import com.v2ray.ang.payment.data.repository.PaymentRepository

class GrowthBridgeRepository(context: Context) {
    private val paymentRepository = PaymentRepository(context)

    suspend fun getReferralOverview(): Result<ReferralOverviewData> {
        return paymentRepository.getReferralOverview()
    }

    suspend fun bindReferralCode(code: String): Result<Unit> {
        return paymentRepository.bindReferralCode(code)
    }

    suspend fun getCommissionSummary(): Result<CommissionSummaryData> {
        return paymentRepository.getCommissionSummary()
    }

    suspend fun getCommissionLedger(): Result<List<CommissionLedgerItem>> {
        return paymentRepository.getCommissionLedger().map { it.items }
    }

    suspend fun createWithdrawal(amount: String, payoutAddress: String): Result<WithdrawalItem> {
        return paymentRepository.createWithdrawal(amount, payoutAddress)
    }

    suspend fun getWithdrawals(): Result<List<WithdrawalItem>> {
        return paymentRepository.getWithdrawals().map { it.items }
    }
}
