package com.v2ray.ang.payment.wallet

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Log
import android.util.Base64
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import java.io.File
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
    val walletName: String? = null,
    val keySlotId: String? = null,
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
    val walletName: String? = null,
    val keySlotId: String? = null,
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
        walletName: String? = null,
        mnemonic: String,
        mnemonicHash: String,
        mnemonicWordCount: Int,
        sourceType: String,
        timestampIso: String,
    ): LocalWalletSecretRecord {
        return upsertMnemonicForWallet(
            accountId = accountId,
            walletId = walletId,
            walletName = walletName,
            keySlotId = null,
            mnemonic = mnemonic,
            mnemonicHash = mnemonicHash,
            mnemonicWordCount = mnemonicWordCount,
            sourceType = sourceType,
            timestampIso = timestampIso,
        )
    }

    fun upsertMnemonicForWallet(
        accountId: String,
        walletId: String,
        walletName: String? = null,
        keySlotId: String?,
        mnemonic: String,
        mnemonicHash: String,
        mnemonicWordCount: Int,
        sourceType: String,
        timestampIso: String,
    ): LocalWalletSecretRecord {
        val encrypted = encrypt(mnemonic)
        val storageKey = walletStorageKey(walletId, keySlotId)
        val existing = loadRecordByStorageKey(storageKey)
        val stored = StoredWalletSecretRecord(
            accountId = accountId,
            walletId = walletId,
            walletName = walletName,
            keySlotId = keySlotId,
            secretType = SECRET_TYPE_MNEMONIC,
            mnemonicCiphertext = encrypted.ciphertext,
            mnemonicIv = encrypted.iv,
            mnemonicHash = mnemonicHash,
            mnemonicWordCount = mnemonicWordCount,
            sourceType = sourceType,
            createdAt = existing?.createdAt ?: timestampIso,
            updatedAt = timestampIso,
        )
        storage.encode(storageKey, gson.toJson(stored))
        return stored.toLocalRecord()
    }

    fun getMnemonicRecord(accountId: String): LocalWalletSecretRecord? {
        loadRecordByStorageKey(accountId)?.toLocalRecord()?.let { return it }
        return allStoredRecords()
            .firstOrNull { it.accountId == accountId }
            ?.toLocalRecord()
    }

    fun getMnemonicRecord(walletId: String, keySlotId: String?): LocalWalletSecretRecord? {
        return loadRecordByStorageKey(walletStorageKey(walletId, keySlotId))?.toLocalRecord()
    }

    fun getAnyMnemonicRecord(): LocalWalletSecretRecord? {
        val localRecords = allStoredRecords()
            .mapNotNull(::toLocalRecordSafely)
            .sortedByDescending { it.updatedAt }
        if (localRecords.isEmpty()) {
            Log.d(
                "WalletSecretDebug",
                "getAnyMnemonicRecord empty keys=${storage.allKeys()?.joinToString()} rawExists=${rawStorageFile().exists()}",
            )
        } else {
            Log.d(
                "WalletSecretDebug",
                "getAnyMnemonicRecord count=${localRecords.size} latestWalletId=${localRecords.firstOrNull()?.walletId}",
            )
        }
        return localRecords.firstOrNull()
    }

    fun getMnemonicRecordByWalletId(walletId: String): LocalWalletSecretRecord? {
        return allStoredRecords()
            .mapNotNull(::toLocalRecordSafely)
            .firstOrNull { it.walletId == walletId }
    }

    fun getConflictingMnemonicRecord(accountId: String): LocalWalletSecretRecord? {
        return allStoredRecords()
            .mapNotNull(::toLocalRecordSafely)
            .firstOrNull { it.accountId != accountId }
    }

    fun getAllMnemonicRecords(): List<LocalWalletSecretRecord> {
        return allStoredRecords()
            .mapNotNull(::toLocalRecordSafely)
            .sortedByDescending { it.updatedAt }
    }

    fun clear(accountId: String) {
        val keysToRemove = mutableListOf<String>()
        if (storage.containsKey(accountId)) {
            keysToRemove += accountId
        }
        storage.allKeys()
            ?.forEach { storageKey ->
                val record = loadRecordByStorageKey(storageKey) ?: return@forEach
                if (record.accountId == accountId) {
                    keysToRemove += storageKey
                }
            }
        keysToRemove.distinct().forEach { storage.remove(it) }
    }

    fun clearWallet(walletId: String, keySlotId: String?) {
        storage.remove(walletStorageKey(walletId, keySlotId))
    }

    private fun loadRecord(accountId: String): StoredWalletSecretRecord? {
        val json = storage.decodeString(accountId) ?: return null
        return gson.fromJson(json, StoredWalletSecretRecord::class.java)
    }

    private fun loadRecordByStorageKey(storageKey: String): StoredWalletSecretRecord? {
        val json = storage.decodeString(storageKey) ?: return null
        return gson.fromJson(json, StoredWalletSecretRecord::class.java)
    }

    private fun allStoredRecords(): List<StoredWalletSecretRecord> {
        val fromMmkv = storage.allKeys()
            ?.mapNotNull { storageKey -> loadRecordByStorageKey(storageKey) }
            .orEmpty()
        if (fromMmkv.isNotEmpty()) {
            return fromMmkv
        }
        return loadRecordsFromRawStorageFile()
    }

    private fun StoredWalletSecretRecord.toLocalRecord(): LocalWalletSecretRecord {
        return LocalWalletSecretRecord(
            accountId = accountId,
            walletId = walletId,
            walletName = walletName,
            keySlotId = keySlotId,
            secretType = secretType,
            mnemonic = decrypt(mnemonicCiphertext, mnemonicIv),
            mnemonicHash = mnemonicHash,
            mnemonicWordCount = mnemonicWordCount,
            sourceType = sourceType,
            createdAt = createdAt,
            updatedAt = updatedAt,
        )
    }

    private fun toLocalRecordSafely(record: StoredWalletSecretRecord): LocalWalletSecretRecord? {
        return runCatching { record.toLocalRecord() }
            .onFailure { error ->
                Log.w(
                    "WalletSecretDebug",
                    "failed to decrypt wallet secret walletId=${record.walletId} keySlotId=${record.keySlotId}",
                    error,
                )
            }
            .getOrNull()
    }

    private fun loadRecordsFromRawStorageFile(): List<StoredWalletSecretRecord> {
        val rawFile = rawStorageFile()
        if (!rawFile.exists()) {
            return emptyList()
        }
        val rawText = runCatching {
            rawFile.readBytes().toString(StandardCharsets.ISO_8859_1)
        }.getOrElse { error ->
            Log.w("WalletSecretDebug", "failed to read raw wallet secret file", error)
            return emptyList()
        }
        val payloads = mutableListOf<String>()
        var searchStart = 0
        while (true) {
            val jsonStart = rawText.indexOf('{', searchStart)
            if (jsonStart < 0) break
            val jsonEnd = rawText.indexOf('}', jsonStart)
            if (jsonEnd < 0) break
            val candidate = rawText.substring(jsonStart, jsonEnd + 1)
            if (
                candidate.contains("\"accountId\"") &&
                candidate.contains("\"walletId\"") &&
                candidate.contains("\"mnemonicCiphertext\"") &&
                candidate.contains("\"updatedAt\"")
            ) {
                payloads += candidate
            }
            searchStart = jsonEnd + 1
        }
        val records = payloads.mapNotNull { payload ->
                runCatching { gson.fromJson(payload, StoredWalletSecretRecord::class.java) }
                    .recoverCatching {
                        gson.fromJson(payload.replace("\\u003d", "="), StoredWalletSecretRecord::class.java)
                    }
                    .onFailure { error ->
                        if (error !is JsonSyntaxException) {
                            Log.w("WalletSecretDebug", "failed to parse raw wallet secret payload", error)
                        }
                    }
                    .getOrNull()
            }
            .distinctBy { "${it.walletId}:${it.keySlotId}:${it.updatedAt}" }
            .toList()
        if (records.isNotEmpty()) {
            Log.d("WalletSecretDebug", "loaded ${records.size} wallet secrets from raw mmkv file fallback")
        }
        return records
    }

    private fun rawStorageFile(): File = File(appContext.filesDir, "mmkv/$STORAGE_ID")

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

    private fun walletStorageKey(walletId: String, keySlotId: String?) =
        "wallet:$walletId:${keySlotId ?: "default"}"

    companion object {
        private const val STORAGE_ID = "WALLET_SECRET"
        private const val KEYSTORE_PROVIDER = "AndroidKeyStore"
        private const val KEY_ALIAS = "cryptovpn_wallet_secret_v1"
        private const val TRANSFORMATION = "AES/GCM/NoPadding"
        private const val SECRET_TYPE_MNEMONIC = "MNEMONIC"
    }
}
