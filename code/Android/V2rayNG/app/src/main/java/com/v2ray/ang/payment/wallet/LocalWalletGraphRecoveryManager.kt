package com.v2ray.ang.payment.wallet

import android.util.Log
import com.v2ray.ang.payment.data.api.CreateMnemonicWalletChainAccountRequest
import com.v2ray.ang.payment.data.api.CreateMnemonicWalletKeySlotRequest
import com.v2ray.ang.payment.data.api.WalletDetailData
import com.v2ray.ang.payment.data.api.WalletSecretBackupPublicAddressRequest
import com.v2ray.ang.payment.data.repository.PaymentRepository
import java.time.Instant
import java.util.Locale

class LocalWalletGraphRecoveryManager(
    private val paymentRepository: PaymentRepository,
    private val walletSecretStore: WalletSecretStore,
    private val walletKeyManager: WalletKeyManager,
) {

    suspend fun ensureServerWalletGraphRecoveredIfMissing(): Boolean {
        val currentUserId = paymentRepository.getCurrentUserId() ?: return false
        val remoteWalletsResult = paymentRepository.listWallets()
        val remoteWallets = remoteWalletsResult.getOrNull() ?: return false
        if (remoteWallets.isNotEmpty()) {
            return false
        }
        val localRecords = walletSecretStore.getAllMnemonicRecords()
            .filter { it.accountId == currentUserId }
            .distinctBy { it.walletId }
        if (localRecords.isEmpty()) {
            Log.d("WalletRecoveryDebug", "skip recovery: no local mnemonic records for user=$currentUserId")
            return false
        }

        Log.d("WalletRecoveryDebug", "recovering ${localRecords.size} local wallet records into empty server graph")
        var recoveredAny = false
        localRecords.forEachIndexed { index, record ->
            val addresses = walletKeyManager.deriveAddressesFromMnemonic(record.mnemonic)
            val walletName = record.walletName
                ?.takeIf { it.isNotBlank() }
                ?: inferRecoveredWalletName(record, index)
            val detail = when {
                record.sourceType.equals("CREATE", ignoreCase = true) ->
                    paymentRepository.createMnemonicWallet(
                        walletName = walletName,
                        keySlots = buildMnemonicWalletKeySlots(),
                        chainAccounts = buildMnemonicWalletChainAccounts(addresses),
                    ).getOrNull()

                else ->
                    paymentRepository.importMnemonicWallet(
                        walletName = walletName,
                        keySlots = buildMnemonicWalletKeySlots(),
                        chainAccounts = buildMnemonicWalletChainAccounts(addresses),
                    ).getOrNull()
            } ?: run {
                Log.w(
                    "WalletRecoveryDebug",
                    "failed to recreate wallet graph for oldWalletId=${record.walletId} sourceType=${record.sourceType}",
                )
                return@forEachIndexed
            }

            val timestamp = Instant.now().toString()
            persistMnemonicSecret(
                detail = detail,
                accountId = currentUserId,
                record = record,
                timestampIso = timestamp,
            )
            val publicAddresses = syncDerivedPublicAddresses(detail)
            paymentRepository.upsertWalletSecretBackupForWallet(
                walletId = detail.wallet.walletId,
                request = com.v2ray.ang.payment.data.api.WalletSecretBackupUpsertRequest(
                    secretType = "MNEMONIC",
                    mnemonic = record.mnemonic,
                    mnemonicHash = record.mnemonicHash,
                    mnemonicWordCount = record.mnemonicWordCount,
                    walletName = walletName,
                    sourceType = record.sourceType.ifBlank { "IMPORT" },
                    publicAddresses = publicAddresses,
                ),
            )
            paymentRepository.upsertWalletLifecycle(
                action = if (record.sourceType.equals("CREATE", ignoreCase = true)) "CREATE" else "IMPORT",
                walletId = detail.wallet.walletId,
                displayName = walletName,
                mnemonicHash = record.mnemonicHash,
                mnemonicWordCount = record.mnemonicWordCount,
            )
            recoveredAny = true
        }

        if (recoveredAny) {
            paymentRepository.listWallets()
            paymentRepository.syncWalletLifecycleFromServer(force = true, userId = currentUserId)
            paymentRepository.refreshCoreSnapshotsOnForeground(force = true)
        }
        return recoveredAny
    }

    private fun inferRecoveredWalletName(
        record: LocalWalletSecretRecord,
        index: Int,
    ): String {
        val suffix = record.walletId.takeLast(4)
        return when {
            record.sourceType.equals("CREATE", ignoreCase = true) -> "Recovered XWallet $suffix"
            index == 0 -> "Recovered002Wallet"
            else -> "Recovered Wallet $suffix"
        }
    }

    private suspend fun syncDerivedPublicAddresses(
        detail: WalletDetailData,
    ): List<WalletSecretBackupPublicAddressRequest> {
        val solanaAddress = detail.chainAccounts.firstOrNull { it.networkCode == "SOLANA" }?.address.orEmpty()
        val tronAddress = detail.chainAccounts.firstOrNull { it.networkCode == "TRON" }?.address.orEmpty()
        val payload = buildList {
            if (solanaAddress.isNotBlank()) {
                add(
                    WalletSecretBackupPublicAddressRequest(
                        networkCode = "SOLANA",
                        assetCode = "SOL",
                        address = solanaAddress,
                        isDefault = true,
                    ),
                )
                add(
                    WalletSecretBackupPublicAddressRequest(
                        networkCode = "SOLANA",
                        assetCode = "USDT",
                        address = solanaAddress,
                        isDefault = true,
                    ),
                )
            }
            if (tronAddress.isNotBlank()) {
                add(
                    WalletSecretBackupPublicAddressRequest(
                        networkCode = "TRON",
                        assetCode = "TRX",
                        address = tronAddress,
                        isDefault = true,
                    ),
                )
                add(
                    WalletSecretBackupPublicAddressRequest(
                        networkCode = "TRON",
                        assetCode = "USDT",
                        address = tronAddress,
                        isDefault = true,
                    ),
                )
            }
        }
        payload.forEach { item ->
            paymentRepository.upsertWalletPublicAddress(
                networkCode = item.networkCode,
                assetCode = item.assetCode,
                address = item.address,
                isDefault = item.isDefault,
            )
        }
        return payload
    }

    private fun buildMnemonicWalletKeySlots() = listOf(
        CreateMnemonicWalletKeySlotRequest(
            slotCode = "EVM_0",
            chainFamily = "EVM",
            derivationType = "MNEMONIC",
            derivationPath = "m/44'/60'/0'/0/0",
        ),
        CreateMnemonicWalletKeySlotRequest(
            slotCode = "SOLANA_0",
            chainFamily = "SOLANA",
            derivationType = "MNEMONIC",
            derivationPath = "m/44'/501'/0'/0'",
        ),
        CreateMnemonicWalletKeySlotRequest(
            slotCode = "TRON_0",
            chainFamily = "TRON",
            derivationType = "MNEMONIC",
            derivationPath = "m/44'/195'/0'/0/0",
        ),
    )

    private fun buildMnemonicWalletChainAccounts(
        addresses: DerivedWalletAddresses,
    ) = listOf(
        CreateMnemonicWalletChainAccountRequest("EVM_0", "EVM", "ETHEREUM", addresses.evmAddress, true, true),
        CreateMnemonicWalletChainAccountRequest("EVM_0", "EVM", "BSC", addresses.evmAddress, true, false),
        CreateMnemonicWalletChainAccountRequest("EVM_0", "EVM", "POLYGON", addresses.evmAddress, true, false),
        CreateMnemonicWalletChainAccountRequest("EVM_0", "EVM", "ARBITRUM", addresses.evmAddress, true, false),
        CreateMnemonicWalletChainAccountRequest("EVM_0", "EVM", "BASE", addresses.evmAddress, true, false),
        CreateMnemonicWalletChainAccountRequest("EVM_0", "EVM", "OPTIMISM", addresses.evmAddress, true, false),
        CreateMnemonicWalletChainAccountRequest("EVM_0", "EVM", "AVALANCHE_C", addresses.evmAddress, true, false),
        CreateMnemonicWalletChainAccountRequest("SOLANA_0", "SOLANA", "SOLANA", addresses.solanaAddress, true, true),
        CreateMnemonicWalletChainAccountRequest("TRON_0", "TRON", "TRON", addresses.tronAddress, true, true),
    )

    private fun persistMnemonicSecret(
        detail: WalletDetailData,
        accountId: String,
        record: LocalWalletSecretRecord,
        timestampIso: String,
    ) {
        detail.keySlots.forEach { keySlot ->
            walletSecretStore.upsertMnemonicForWallet(
                accountId = accountId,
                walletId = detail.wallet.walletId,
                walletName = detail.wallet.walletName,
                keySlotId = keySlot.keySlotId,
                mnemonic = record.mnemonic,
                mnemonicHash = record.mnemonicHash,
                mnemonicWordCount = record.mnemonicWordCount,
                sourceType = record.sourceType,
                timestampIso = timestampIso,
            )
        }
    }
}
