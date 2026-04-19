package com.v2ray.ang.payment.wallet

import android.util.Base64
import org.bitcoinj.core.Base58
import org.bitcoinj.core.ECKey
import org.bitcoinj.core.Sha256Hash
import org.bitcoinj.crypto.ChildNumber
import org.bitcoinj.crypto.DeterministicKey
import org.bitcoinj.crypto.HDKeyDerivation
import org.bitcoinj.crypto.MnemonicCode
import org.bouncycastle.crypto.params.Ed25519PrivateKeyParameters
import org.bouncycastle.crypto.signers.Ed25519Signer
import org.bouncycastle.jcajce.provider.digest.Keccak
import java.nio.ByteBuffer
import java.util.Locale
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

data class DerivedWalletAddresses(
    val evmAddress: String,
    val solanaAddress: String,
    val tronAddress: String,
)

class WalletKeyManager(
    private val walletSecretStore: WalletSecretStore,
) {
    fun deriveAddressesFromMnemonic(mnemonic: String): DerivedWalletAddresses {
        return DerivedWalletAddresses(
            evmAddress = deriveEvmAddress(mnemonic),
            solanaAddress = deriveSolanaAddress(mnemonic),
            tronAddress = deriveTronAddress(mnemonic),
        )
    }

    fun deriveAddresses(accountId: String): DerivedWalletAddresses {
        val record = walletSecretStore.getMnemonicRecord(accountId)
            ?: throw IllegalStateException("本地未找到钱包助记词")
        return deriveAddressesFromMnemonic(record.mnemonic)
    }

    fun deriveAddresses(walletId: String, keySlotId: String? = null): DerivedWalletAddresses {
        val record = walletSecretStore.getMnemonicRecord(walletId, keySlotId)
            ?: throw IllegalStateException("本地未找到钱包助记词")
        return deriveAddressesFromMnemonic(record.mnemonic)
    }

    fun signSolanaMessage(accountId: String, signingPayloadBase64: String): String {
        val record = walletSecretStore.getMnemonicRecord(accountId)
            ?: throw IllegalStateException("本地未找到钱包助记词")
        val privateSeed = deriveSolanaPrivateSeed(record.mnemonic)
        val signer = Ed25519Signer()
        signer.init(true, Ed25519PrivateKeyParameters(privateSeed, 0))
        val message = Base64.decode(signingPayloadBase64, Base64.DEFAULT)
        signer.update(message, 0, message.size)
        val signature = signer.generateSignature()
        return Base64.encodeToString(signature, Base64.NO_WRAP)
    }

    fun signSolanaMessage(walletId: String, keySlotId: String?, signingPayloadBase64: String): String {
        val record = walletSecretStore.getMnemonicRecord(walletId, keySlotId)
            ?: throw IllegalStateException("本地未找到钱包助记词")
        val privateSeed = deriveSolanaPrivateSeed(record.mnemonic)
        val signer = Ed25519Signer()
        signer.init(true, Ed25519PrivateKeyParameters(privateSeed, 0))
        val message = Base64.decode(signingPayloadBase64, Base64.DEFAULT)
        signer.update(message, 0, message.size)
        val signature = signer.generateSignature()
        return Base64.encodeToString(signature, Base64.NO_WRAP)
    }

    fun signTronTransactionId(accountId: String, txIdHex: String): String {
        val record = walletSecretStore.getMnemonicRecord(accountId)
            ?: throw IllegalStateException("本地未找到钱包助记词")
        val privateKey = deriveTronPrivateKey(record.mnemonic)
        val ecKey = ECKey.fromPrivate(privateKey, false)
        val txHashBytes = txIdHex.hexToByteArray()
        val txHash = Sha256Hash.wrap(txHashBytes)
        val signature = ecKey.sign(txHash).toCanonicalised()
        val recId = (0..3).firstOrNull { candidate ->
            ECKey.recoverFromSignature(candidate, signature, txHash, false)?.pubKey?.contentEquals(ecKey.pubKey) == true
        } ?: throw IllegalStateException("无法恢复 TRON 签名恢复位")
        val payload = signature.r.toFixed32Bytes() + signature.s.toFixed32Bytes() + byteArrayOf(recId.toByte())
        return payload.toHex()
    }

    fun signTronTransactionId(walletId: String, keySlotId: String?, txIdHex: String): String {
        val record = walletSecretStore.getMnemonicRecord(walletId, keySlotId)
            ?: throw IllegalStateException("本地未找到钱包助记词")
        val privateKey = deriveTronPrivateKey(record.mnemonic)
        val ecKey = ECKey.fromPrivate(privateKey, false)
        val txHashBytes = txIdHex.hexToByteArray()
        val txHash = Sha256Hash.wrap(txHashBytes)
        val signature = ecKey.sign(txHash).toCanonicalised()
        val recId = (0..3).firstOrNull { candidate ->
            ECKey.recoverFromSignature(candidate, signature, txHash, false)?.pubKey?.contentEquals(ecKey.pubKey) == true
        } ?: throw IllegalStateException("无法恢复 TRON 签名恢复位")
        val payload = signature.r.toFixed32Bytes() + signature.s.toFixed32Bytes() + byteArrayOf(recId.toByte())
        return payload.toHex()
    }

    private fun deriveSolanaAddress(mnemonic: String): String {
        val privateSeed = deriveSolanaPrivateSeed(mnemonic)
        val publicKey = Ed25519PrivateKeyParameters(privateSeed, 0).generatePublicKey().encoded
        return Base58.encode(publicKey)
    }

    private fun deriveSolanaPrivateSeed(mnemonic: String): ByteArray {
        val seed = MnemonicCode.toSeed(mnemonic.trim().split(Regex("\\s+")), "")
        val path = intArrayOf(
            hardened(44),
            hardened(501),
            hardened(0),
            hardened(0),
        )
        return deriveEd25519Slip10(seed, path)
    }

    private fun deriveTronAddress(mnemonic: String): String {
        val privateKey = deriveTronPrivateKey(mnemonic)
        val ecKey = ECKey.fromPrivate(privateKey, false)
        val uncompressed = ecKey.pubKeyPoint.getEncoded(false).copyOfRange(1, 65)
        val digest = Keccak.Digest256()
        digest.update(uncompressed, 0, uncompressed.size)
        val hash = digest.digest()
        val addressBytes = byteArrayOf(0x41.toByte()) + hash.copyOfRange(hash.size - 20, hash.size)
        val checksum = Sha256Hash.hashTwice(addressBytes).copyOfRange(0, 4)
        return Base58.encode(addressBytes + checksum)
    }

    private fun deriveTronPrivateKey(mnemonic: String): ByteArray {
        val seed = MnemonicCode.toSeed(mnemonic.trim().split(Regex("\\s+")), "")
        var key: DeterministicKey = HDKeyDerivation.createMasterPrivateKey(seed)
        val path = listOf(
            ChildNumber(44, true),
            ChildNumber(195, true),
            ChildNumber.ZERO_HARDENED,
            ChildNumber.ZERO,
            ChildNumber.ZERO,
        )
        path.forEach { child ->
            key = HDKeyDerivation.deriveChildKey(key, child)
        }
        return key.privKeyBytes
    }

    private fun deriveEvmAddress(mnemonic: String): String {
        val privateKey = deriveEvmPrivateKey(mnemonic)
        val ecKey = ECKey.fromPrivate(privateKey, false)
        val uncompressed = ecKey.pubKeyPoint.getEncoded(false).copyOfRange(1, 65)
        val digest = Keccak.Digest256()
        digest.update(uncompressed, 0, uncompressed.size)
        val hash = digest.digest()
        return "0x" + hash.copyOfRange(hash.size - 20, hash.size).toHex()
    }

    private fun deriveEvmPrivateKey(mnemonic: String): ByteArray {
        val seed = MnemonicCode.toSeed(mnemonic.trim().split(Regex("\\s+")), "")
        var key: DeterministicKey = HDKeyDerivation.createMasterPrivateKey(seed)
        val path = listOf(
            ChildNumber(44, true),
            ChildNumber(60, true),
            ChildNumber.ZERO_HARDENED,
            ChildNumber.ZERO,
            ChildNumber.ZERO,
        )
        path.forEach { child ->
            key = HDKeyDerivation.deriveChildKey(key, child)
        }
        return key.privKeyBytes
    }

    private fun deriveEd25519Slip10(seed: ByteArray, path: IntArray): ByteArray {
        var keyMaterial = hmacSha512("ed25519 seed".toByteArray(), seed)
        var key = keyMaterial.copyOfRange(0, 32)
        var chainCode = keyMaterial.copyOfRange(32, 64)
        path.forEach { index ->
            val data = ByteArray(1 + 32 + 4)
            data[0] = 0
            System.arraycopy(key, 0, data, 1, 32)
            ByteBuffer.wrap(data, 33, 4).putInt(index)
            keyMaterial = hmacSha512(chainCode, data)
            key = keyMaterial.copyOfRange(0, 32)
            chainCode = keyMaterial.copyOfRange(32, 64)
        }
        return key
    }

    private fun hmacSha512(key: ByteArray, data: ByteArray): ByteArray {
        val mac = Mac.getInstance("HmacSHA512")
        mac.init(SecretKeySpec(key, "HmacSHA512"))
        return mac.doFinal(data)
    }

    private fun hardened(index: Int): Int = index or 0x80000000.toInt()

    private fun ByteArray.toHex(): String = joinToString("") { byte ->
        "%02x".format(Locale.US, byte)
    }

    private fun String.hexToByteArray(): ByteArray {
        require(length % 2 == 0) { "hex length must be even" }
        return chunked(2).map { it.toInt(16).toByte() }.toByteArray()
    }

    private fun java.math.BigInteger.toFixed32Bytes(): ByteArray {
        val raw = toByteArray().let { bytes ->
            if (bytes.size == 33 && bytes[0] == 0.toByte()) bytes.copyOfRange(1, 33) else bytes
        }
        return ByteArray(32 - raw.size) + raw
    }
}
