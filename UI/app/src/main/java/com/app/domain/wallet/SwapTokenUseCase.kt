package com.app.domain.wallet

import com.app.AppGraph
import com.app.data.repository.WalletRepository

class SwapTokenUseCase(
    private val repository: WalletRepository = AppGraph.walletRepository,
) {
    suspend operator fun invoke(fromSymbol: String, toSymbol: String, amount: Double) = repository.swapToken(fromSymbol, toSymbol, amount)
}
