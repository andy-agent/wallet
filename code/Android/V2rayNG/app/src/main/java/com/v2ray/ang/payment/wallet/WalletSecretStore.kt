package com.v2ray.ang.payment.wallet

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import com.google.gson.Gson
import com.tencent.mmkv.MMKV
import java.nio.charset.StandardCharsets
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

data class LocalWalletSecretRecord(
    val accountId: String,
    val walletId: String,
    val secretType: String,
    val mnemonic: String,
    val mnemonicHash: String,
    val mnemonicWordCount: Int,
    val sourceType: String,
    val createdAt: String,
    val updatedAt: String,
)

private data class StoredWalletSecretRecord(
    val accountId: String,
    val walletId: String,
    val secretType: String,
    val mnemonicCiphertext: String,
    val mnemonicIv: String,
    val mnemonicHash: String,
    val mnemonicWordCount: Int,
    val sourceType: String,
    val createdAt: String,
    val updatedAt: String,
)

class WalletSecretStore(context: Context) {
    private val appContext = context.applicationContext
    private val gson = Gson()
    private val storage by lazy {
        MMKV.mmkvWithID(STORAGE_ID, MMKV.MULTI_PROCESS_MODE)
    }

    fun upsertMnemonic(
        accountId: String,
        walletId: String,
        mnemonic: String,
        mnemonicHash: String,
        mnemonicWordCount: Int,
        sourceType: String,
        timestampIso: String,
    ): LocalWalletSecretRecord {
        val encrypted = encrypt(mnemonic)
        val existing = loadRecord(accountId)
        val stored = StoredWalletSecretRecord(
            accountId = accountId,
            walletId = walletId,
            secretType = SECRET_TYPE_MNEMONIC,
            mnemonicCiphertext = encrypted.ciphertext,
            mnemonicIv = encrypted.iv,
            mnemonicHash = mnemonicHash,
            mnemonicWordCount = mnemonicWordCount,
            sourceType = sourceType,
            createdAt = existing?.createdAt ?: timestampIso,
            updatedAt = timestampIso,
        )
        storage.encode(accountId, gson.toJson(stored))
        return stored.toLocalRecord()
    }

    fun getMnemonicRecord(accountId: String): LocalWalletSecretRecord? {
        return loadRecord(accountId)?.toLocalRecord()
    }

    fun getAnyMnemonicRecord(): LocalWalletSecretRecord? {
        return storage.allKeys()
            ?.firstOrNull()
            ?.let { accountId -> loadRecord(accountId)?.toLocalRecord() }
    }

    fun getMnemonicRecordByWalletId(walletId: String): LocalWalletSecretRecord? {
        return storage.allKeys()
            ?.firstNotNullOfOrNull { accountId ->
                loadRecord(accountId)
                    ?.takeIf { it.walletId == walletId }
                    ?.toLocalRecord()
            }
    }

    fun getConflictingMnemonicRecord(accountId: String): LocalWalletSecretRecord? {
        return storage.allKeys()
            ?.firstOrNull { storedAccountId ->
                storedAccountId != accountId && loadRecord(storedAccountId) != null
            }
            ?.let { storedAccountId -> loadRecord(storedAccountId)?.toLocalRecord() }
    }

    fun clear(accountId: String) {
        storage.remove(accountId)
    }

    private fun loadRecord(accountId: String): StoredWalletSecretRecord? {
        val json = storage.decodeString(accountId) ?: return null
        return gson.fromJson(json, StoredWalletSecretRecord::class.java)
    }

    private fun StoredWalletSecretRecord.toLocalRecord(): LocalWalletSecretRecord {
        return LocalWalletSecretRecord(
            accountId = accountId,
            walletId = walletId,
            secretType = secretType,
            mnemonic = decrypt(mnemonicCiphertext, mnemonicIv),
            mnemonicHash = mnemonicHash,
            mnemonicWordCount = mnemonicWordCount,
            sourceType = sourceType,
            createdAt = createdAt,
            updatedAt = updatedAt,
        )
    }

    private fun encrypt(plaintext: String): EncryptedValue {
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.ENCRYPT_MODE, getOrCreateSecretKey())
        val ciphertext = cipher.doFinal(plaintext.toByteArray(StandardCharsets.UTF_8))
        return EncryptedValue(
            ciphertext = Base64.encodeToString(ciphertext, Base64.NO_WRAP),
            iv = Base64.encodeToString(cipher.iv, Base64.NO_WRAP),
        )
    }

    private fun decrypt(ciphertext: String, iv: String): String {
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(
            Cipher.DECRYPT_MODE,
            getOrCreateSecretKey(),
            GCMParameterSpec(128, Base64.decode(iv, Base64.NO_WRAP)),
        )
        val plaintext = cipher.doFinal(Base64.decode(ciphertext, Base64.NO_WRAP))
        return String(plaintext, StandardCharsets.UTF_8)
    }

    private fun getOrCreateSecretKey(): SecretKey {
        val keyStore = KeyStore.getInstance(KEYSTORE_PROVIDER).apply { load(null) }
        val existing = keyStore.getKey(KEY_ALIAS, null) as? SecretKey
        if (existing != null) {
            return existing
        }
        val keyGenerator = KeyGenerator.getInstance(
            KeyProperties.KEY_ALGORITHM_AES,
            KEYSTORE_PROVIDER,
        )
        keyGenerator.init(
            KeyGenParameterSpec.Builder(
                KEY_ALIAS,
                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT,
            )
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .setRandomizedEncryptionRequired(true)
                .build(),
        )
        return keyGenerator.generateKey()
    }

    private data class EncryptedValue(
        val ciphertext: String,
        val iv: String,
    )

    companion object {
        private const val STORAGE_ID = "WALLET_SECRET"
        private const val KEYSTORE_PROVIDER = "AndroidKeyStore"
        private const val KEY_ALIAS = "cryptovpn_wallet_secret_v1"
        private const val TRANSFORMATION = "AES/GCM/NoPadding"
        private const val SECRET_TYPE_MNEMONIC = "MNEMONIC"
    }
}
