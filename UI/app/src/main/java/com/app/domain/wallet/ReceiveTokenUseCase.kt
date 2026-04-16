package com.app.domain.wallet

import com.app.AppGraph
import com.app.data.repository.WalletRepository

class ReceiveTokenUseCase(
    private val repository: WalletRepository = AppGraph.walletRepository,
) {
    suspend operator fun invoke(symbol: String) = repository.getReceiveAddress(symbol)
}
