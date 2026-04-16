package com.app.domain.wallet

import com.app.AppGraph
import com.app.data.repository.WalletRepository

class AddCustomTokenUseCase(
    private val repository: WalletRepository = AppGraph.walletRepository,
) {
    suspend operator fun invoke(symbol: String, name: String, chainId: String) = repository.addCustomToken(symbol, name, chainId)
}
