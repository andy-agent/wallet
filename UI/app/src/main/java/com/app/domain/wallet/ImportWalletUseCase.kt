package com.app.domain.wallet

import com.app.AppGraph
import com.app.data.repository.WalletRepository

class ImportWalletUseCase(
    private val repository: WalletRepository = AppGraph.walletRepository,
) {
    suspend operator fun invoke(mnemonic: String) = repository.importWallet(mnemonic)
}
