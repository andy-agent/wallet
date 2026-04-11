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
});
