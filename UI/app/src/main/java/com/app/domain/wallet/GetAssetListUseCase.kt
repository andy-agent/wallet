package com.app.domain.wallet

import com.app.AppGraph
import com.app.data.repository.WalletRepository

class GetAssetListUseCase(
    private val repository: WalletRepository = AppGraph.walletRepository,
) {
    suspend operator fun invoke() = repository.getAssets()
}
