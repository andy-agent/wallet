package com.app.domain.wallet

import com.app.AppGraph
import com.app.data.repository.WalletRepository

class SendTokenUseCase(
    private val repository: WalletRepository = AppGraph.walletRepository,
) {
    suspend operator fun invoke(symbol: String, address: String, amount: Double) = repository.sendToken(symbol, address, amount)
}
