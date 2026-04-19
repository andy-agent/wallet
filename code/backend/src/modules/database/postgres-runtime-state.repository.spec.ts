import { newDb } from 'pg-mem';
import { PostgresRuntimeStateRepository } from './postgres-runtime-state.repository';

describe('PostgresRuntimeStateRepository matcher persistence', () => {
  it('persists payment contexts, scan cursors, and onchain receipts', async () => {
    const db = newDb({
      noAstCoverageCheck: true,
    });
    const { Pool } = db.adapters.createPg();
    const repository = new PostgresRuntimeStateRepository(
      {
        connectionString: 'postgres://runtime:runtime@server2:5432/cryptovpn',
      },
      new Pool(),
    );

    await repository.initialize();

    const now = Date.now();
    await repository.createOrder(
      {
        orderId: 'order-1',
        orderNo: 'ORD-1',
        accountId: 'acc-1',
        payerWalletId: null,
        payerChainAccountId: null,
        submittedFromAddress: null,
        planCode: 'BASIC_1M',
        planName: 'Basic 1M',
        orderType: 'NEW',
        quoteAssetCode: 'USDT',
        quoteNetworkCode: 'SOLANA',
        quoteUsdAmount: '58.000000',
        baseAmount: '58.000000',
        uniqueAmountDelta: '0.000123',
        payableAmount: '58.000123',
        status: 'AWAITING_PAYMENT',
        expiresAt: new Date(now + 15 * 60 * 1000).toISOString(),
        confirmedAt: null,
        completedAt: null,
        failureReason: null,
        submittedClientTxHash: null,
        matchedOnchainTxHash: null,
        paymentMatchedAt: null,
        matcherRemark: null,
        createdAt: new Date(now).toISOString(),
        idempotencyKey: 'acc-1:order-1',
        collectionAddress: 'SharedAddress111111111111111111111111111111',
      },
      'acc-1:order-1',
    );

    const context = {
      collectionAddress: 'SharedAddress111111111111111111111111111111',
      quoteAssetCode: 'USDT' as const,
      quoteNetworkCode: 'SOLANA' as const,
    };

    await repository.savePaymentScanCursor({
      cursorKey: 'SOLANA:USDT:SharedAddress111111111111111111111111111111',
      ...context,
      beforeSignature: 'sig-oldest',
      lastSignature: 'sig-newest',
      lastSlot: 321,
      updatedAt: new Date(now).toISOString(),
    });

    expect(
      await repository.findPaymentScanCursor(context),
    ).toMatchObject({
      beforeSignature: 'sig-oldest',
      lastSignature: 'sig-newest',
      lastSlot: 321,
    });

    const receipt = await repository.upsertOnchainReceipt({
      receiptId: 'SOLANA:sig-1:0',
      quoteNetworkCode: 'SOLANA',
      quoteAssetCode: 'USDT',
      collectionAddress: 'SharedAddress111111111111111111111111111111',
      txHash: 'sig-1',
      eventIndex: 0,
      recipientTokenAccount: 'TokenAccount111',
      fromAddress: 'FromAddress111',
      mint: 'Es9vMFrzaCERmJfrF4H2FYD4KCoNkY11McCe8BenwNYB',
      amount: '58.000123',
      amountMinor: '58000123',
      confirmationStatus: 'confirmed',
      slot: 321,
      blockTime: new Date(now).toISOString(),
      observedAt: new Date(now).toISOString(),
      matchedOrderNo: null,
      matchStatus: 'UNMATCHED',
      matcherRemark: null,
      rawPayload: {
        source: 'spec',
      },
    });

    const updatedReceipt = await repository.upsertOnchainReceipt({
      ...receipt,
      confirmationStatus: 'finalized',
      matchedOrderNo: 'ORD-1',
      matchStatus: 'MATCHED',
      matcherRemark: 'auto-match',
    });

    expect(updatedReceipt).toMatchObject({
      receiptId: 'SOLANA:sig-1:0',
      confirmationStatus: 'finalized',
      matchedOrderNo: 'ORD-1',
      matchStatus: 'MATCHED',
      matcherRemark: 'auto-match',
    });

    await repository.onModuleDestroy();
  });

  it('supports multiple wallets per account with chain accounts and wallet-scoped backups', async () => {
    const db = newDb({
      noAstCoverageCheck: true,
    });
    const { Pool } = db.adapters.createPg();
    const repository = new PostgresRuntimeStateRepository(
      {
        connectionString: 'postgres://runtime:runtime@server2:5432/cryptovpn',
      },
      new Pool(),
    );

    await repository.initialize();

    const primary = await repository.insertWallet({
      walletId: 'wallet-1',
      accountId: 'acc-1',
      walletName: 'Primary',
      walletKind: 'SELF_CUSTODY',
      sourceType: 'CREATED',
      isDefault: true,
      isArchived: false,
      createdAt: '2026-04-19T00:00:00.000Z',
      updatedAt: '2026-04-19T00:00:00.000Z',
    });
    const watch = await repository.insertWallet({
      walletId: 'wallet-2',
      accountId: 'acc-1',
      walletName: 'Watch',
      walletKind: 'WATCH_ONLY',
      sourceType: 'WATCH_IMPORTED',
      isDefault: false,
      isArchived: false,
      createdAt: '2026-04-19T00:01:00.000Z',
      updatedAt: '2026-04-19T00:01:00.000Z',
    });

    await repository.insertWalletKeySlot({
      keySlotId: 'slot-evm',
      walletId: primary.walletId,
      slotCode: 'EVM_0',
      chainFamily: 'EVM',
      derivationType: 'MNEMONIC',
      derivationPath: "m/44'/60'/0'/0/0",
      createdAt: primary.createdAt,
      updatedAt: primary.updatedAt,
    });
    const chainAccount = await repository.insertWalletChainAccount({
      chainAccountId: 'chain-base',
      walletId: primary.walletId,
      keySlotId: 'slot-evm',
      chainFamily: 'EVM',
      networkCode: 'BASE',
      address: '0x1111111111111111111111111111111111111111',
      capability: 'SIGN_AND_PAY',
      isEnabled: true,
      isDefaultReceive: true,
      createdAt: primary.createdAt,
      updatedAt: primary.updatedAt,
    });

    await repository.insertWalletChainAccount({
      chainAccountId: 'chain-sol-watch',
      walletId: watch.walletId,
      keySlotId: null,
      chainFamily: 'SOLANA',
      networkCode: 'SOLANA',
      address: '7YttLkHDo1B4ezgm6KPDLJrVN6a8GN28AL5soMgqd7qV',
      capability: 'WATCH_ONLY',
      isEnabled: true,
      isDefaultReceive: true,
      createdAt: watch.createdAt,
      updatedAt: watch.updatedAt,
    });

    await repository.upsertWalletSecretBackupV2({
      backupId: 'backup-1',
      accountId: 'acc-1',
      walletId: primary.walletId,
      secretType: 'MNEMONIC',
      encryptionScheme: 'AGE',
      recoveryKeyVersion: 'v1',
      recipientFingerprint: 'fingerprint',
      ciphertext: 'ciphertext',
      replicatedToBackupServer: false,
      backupServerReference: null,
      lastReplicationError: null,
      createdAt: primary.createdAt,
      updatedAt: primary.updatedAt,
    });

    expect(await repository.listWalletsByAccountId('acc-1')).toEqual([
      expect.objectContaining({ walletId: 'wallet-1', isDefault: true }),
      expect.objectContaining({ walletId: 'wallet-2', isDefault: false }),
    ]);

    expect(await repository.listWalletKeySlotsByWalletId('wallet-1')).toEqual([
      expect.objectContaining({ keySlotId: 'slot-evm', slotCode: 'EVM_0' }),
    ]);

    expect(await repository.findWalletChainAccountById(chainAccount.chainAccountId)).toEqual(
      expect.objectContaining({
        walletId: 'wallet-1',
        networkCode: 'BASE',
        capability: 'SIGN_AND_PAY',
      }),
    );

    expect(await repository.findWalletSecretBackupByWalletId('wallet-1')).toEqual(
      expect.objectContaining({
        walletId: 'wallet-1',
        encryptionScheme: 'AGE',
      }),
    );

    const nextDefault = await repository.setDefaultWallet('acc-1', 'wallet-2');
    expect(nextDefault.walletId).toBe('wallet-2');
    expect(nextDefault.isDefault).toBe(true);
    expect(
      (await repository.listWalletsByAccountId('acc-1')).filter((item) => item.isDefault),
    ).toHaveLength(1);

    await repository.onModuleDestroy();
  });

  it('clears wallet domain records while preserving encrypted backups', async () => {
    const db = newDb({
      noAstCoverageCheck: true,
    });
    const { Pool } = db.adapters.createPg();
    const repository = new PostgresRuntimeStateRepository(
      {
        connectionString: 'postgres://runtime:runtime@server2:5432/cryptovpn',
      },
      new Pool(),
    );

    await repository.initialize();

    await repository.upsertWalletLifecycle({
      accountId: 'acc-1',
      walletId: 'legacy-wallet',
      walletName: 'Legacy Wallet',
      status: 'ACTIVE',
      origin: 'CREATED',
      mnemonicHash: 'hash',
      mnemonicWordCount: 12,
      backupAcknowledgedAt: '2026-04-19T00:00:00.000Z',
      activatedAt: '2026-04-19T00:01:00.000Z',
      createdAt: '2026-04-19T00:00:00.000Z',
      updatedAt: '2026-04-19T00:01:00.000Z',
    });
    await repository.upsertWalletPublicAddress({
      addressId: 'addr-1',
      accountId: 'acc-1',
      walletId: 'legacy-wallet',
      networkCode: 'SOLANA',
      assetCode: 'SOL',
      address: '7YttLkHDo1B4ezgm6KPDLJrVN6a8GN28AL5soMgqd7qV',
      isDefault: true,
      createdAt: '2026-04-19T00:00:00.000Z',
      updatedAt: '2026-04-19T00:00:00.000Z',
    });
    await repository.insertWallet({
      walletId: 'wallet-1',
      accountId: 'acc-1',
      walletName: 'Primary',
      walletKind: 'SELF_CUSTODY',
      sourceType: 'CREATED',
      isDefault: true,
      isArchived: false,
      createdAt: '2026-04-19T00:00:00.000Z',
      updatedAt: '2026-04-19T00:00:00.000Z',
    });
    await repository.insertWalletKeySlot({
      keySlotId: 'slot-1',
      walletId: 'wallet-1',
      slotCode: 'SOLANA_0',
      chainFamily: 'SOLANA',
      derivationType: 'MNEMONIC',
      derivationPath: "m/44'/501'/0'/0'",
      createdAt: '2026-04-19T00:00:00.000Z',
      updatedAt: '2026-04-19T00:00:00.000Z',
    });
    await repository.insertWalletChainAccount({
      chainAccountId: 'chain-1',
      walletId: 'wallet-1',
      keySlotId: 'slot-1',
      chainFamily: 'SOLANA',
      networkCode: 'SOLANA',
      address: '7YttLkHDo1B4ezgm6KPDLJrVN6a8GN28AL5soMgqd7qV',
      capability: 'SIGN_AND_PAY',
      isEnabled: true,
      isDefaultReceive: true,
      createdAt: '2026-04-19T00:00:00.000Z',
      updatedAt: '2026-04-19T00:00:00.000Z',
    });
    await repository.upsertWalletSecretBackup({
      backupId: 'backup-legacy',
      accountId: 'acc-1',
      walletId: 'legacy-wallet',
      secretType: 'MNEMONIC',
      encryptionScheme: 'AGE',
      recoveryKeyVersion: 'v1',
      recipientFingerprint: 'fingerprint',
      ciphertext: 'ciphertext-legacy',
      replicatedToBackupServer: false,
      backupServerReference: null,
      lastReplicationError: null,
      createdAt: '2026-04-19T00:00:00.000Z',
      updatedAt: '2026-04-19T00:00:00.000Z',
    });
    await repository.upsertWalletSecretBackupV2({
      backupId: 'backup-v2',
      accountId: 'acc-1',
      walletId: 'wallet-1',
      secretType: 'MNEMONIC',
      encryptionScheme: 'AGE',
      recoveryKeyVersion: 'v2',
      recipientFingerprint: 'fingerprint-v2',
      ciphertext: 'ciphertext-v2',
      replicatedToBackupServer: false,
      backupServerReference: null,
      lastReplicationError: null,
      createdAt: '2026-04-19T00:00:00.000Z',
      updatedAt: '2026-04-19T00:00:00.000Z',
    });

    await repository.clearWalletDomainByAccountId('acc-1');

    expect(await repository.findWalletLifecycleByAccountId('acc-1')).toBeNull();
    expect(await repository.listWalletPublicAddressesByAccountId({ accountId: 'acc-1' })).toEqual(
      [],
    );
    expect(await repository.listWalletsByAccountId('acc-1')).toEqual([]);
    expect(await repository.listWalletKeySlotsByWalletId('wallet-1')).toEqual([]);
    expect(await repository.listWalletChainAccountsByWalletId('wallet-1')).toEqual([]);
    expect(await repository.findWalletSecretBackupByAccountId('acc-1')).toEqual(
      expect.objectContaining({
        backupId: 'backup-legacy',
        walletId: 'legacy-wallet',
      }),
    );
    expect(await repository.findWalletSecretBackupByWalletId('wallet-1')).toEqual(
      expect.objectContaining({
        backupId: 'backup-v2',
        walletId: 'wallet-1',
      }),
    );

    await repository.onModuleDestroy();
  });
});
