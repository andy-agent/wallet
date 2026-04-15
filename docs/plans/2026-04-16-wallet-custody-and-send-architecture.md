# Wallet Custody And Multi-Chain Send Architecture

**Goal:** add Android local signable wallet storage, backend recoverable encrypted backup, and real multi-chain send for Solana/TRON.

## Current Blocking Facts

- Android create/import flows do not persist signable mnemonic/private-key material locally.
- Backend wallet lifecycle stores only `mnemonicHash`, not recoverable secret material.
- Backend exposes `transfer/precheck` and `transfer/proxy-broadcast`, but does not expose unsigned transaction builders for Android signing.

## Recommended Default

Use **client-side custody + admin-local recoverable backup**.

Meaning:

- Android stores the wallet mnemonic/private key locally in an encrypted device store.
- Android derives addresses and signs transactions locally.
- Backend stores only an encrypted backup blob that the server cannot decrypt by itself.
- The admin private decryption material stays on the MacBook, not on the server.

This matches the user requirement that decryption capability should live locally on the admin machine.

## Encryption Options For Recoverable Backup

### Option A: `age` public-key envelope encryption

Recommended.

- Generate a random DEK per wallet backup.
- Encrypt mnemonic/private key with `AES-256-GCM`.
- Encrypt the DEK to an admin `age` public key.
- Store on backend:
  - ciphertext
  - nonce
  - wrapped DEK
  - version / metadata
- Decrypt only on the admin Mac with the `age` private key.

Why this is the best fit:

- Server never holds the private decryption key.
- Key rotation and multi-admin recipients are straightforward.
- Operational model is simpler and safer than PGP.
- Cleanly supports future multiple recipients.

### Option B: passphrase-based encryption with `Argon2id` + `AES-256-GCM`

Acceptable fallback.

- Admin stores a strong passphrase locally, preferably in macOS Keychain / password manager.
- Backend stores salt + Argon2id parameters + ciphertext.

Tradeoff:

- Simpler than managing keypairs.
- Weaker operationally than asymmetric encryption because the passphrase becomes the whole trust root.

### Option C: backend-held symmetric key or server KMS

Not recommended for this requirement.

- Violates the requirement that decryption capability should remain on the admin Mac rather than the server.

## Android Local Secret Storage Recommendation

Use **Android Keystore-wrapped AES-GCM local encryption** for mnemonic/private-key material.

- Generate or import wallet material on device.
- Store encrypted secret blob in app storage / MMKV / Room.
- Keep the wrapping key in Android Keystore.
- Decrypt only in memory for signing.

Optional hardening:

- require user authentication for key use
- biometric gate for export / reveal
- explicit wipe on logout / wallet reset

## Send Architecture

### Phase 1

- Android local secure wallet store
- Android create/import writes signable material locally
- Backend encrypted recoverable backup using admin-local decryptability

### Phase 2

- Add backend unsigned-transaction builder APIs:
  - Solana native SOL transfer
  - Solana SPL USDT transfer
  - TRON TRX transfer
  - TRON TRC20 USDT transfer

These endpoints should return chain-specific unsigned payloads for local signing.

### Phase 3

- Android:
  - call `transfer/precheck`
  - request unsigned transaction
  - sign locally
  - call `transfer/proxy-broadcast`
  - show real send result / tx hash / failure

## Why unsigned-transaction builder APIs are needed

Without them, Android must implement low-level Solana and TRON transaction construction itself, including:

- recent blockhash / fee payer / ATA handling on Solana
- TRON contract call assembly for TRC20
- chain-specific serialization rules

That is possible, but it is a larger and riskier client implementation than reusing backend chain-side builders and keeping Android focused on custody + signing.

## Recommendation To Approve

Approve this stack:

1. Android local custody with Android Keystore-wrapped AES-GCM
2. Backend recoverable backup with `age` public-key envelope encryption
3. Admin private decryption key stored only on the MacBook
4. Backend unsigned-transaction builder APIs for Solana/TRON
5. Android local signing + backend proxy broadcast

## Implementation Notes

- Main backend runtime configuration:
  - `WALLET_BACKUP_RECIPIENTS`
  - `WALLET_BACKUP_RECOVERY_KEY_VERSION`
  - optional `WALLET_BACKUP_SERVER_URL`
  - optional `WALLET_BACKUP_SERVER_API_KEY`
- Current helper script to derive the public recipient from a local identity:
  - [age-derive-recipient.mjs](/Users/cnyirui/git/projects/liaojiang/code/backend/scripts/age-derive-recipient.mjs)
- Default local identity path configured in the helper script:
  - `/Users/cnyirui/server/区块恢复私钥`
- Current code supports:
  - Android local mnemonic custody
  - main-backend encrypted backup storage
  - optional relay to a dedicated backup server
- Current code does not yet support:
  - Solana/TRON unsigned transaction builder APIs
  - Android local multi-chain signing and send
