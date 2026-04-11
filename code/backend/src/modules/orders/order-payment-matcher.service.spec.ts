import { OrderPaymentMatcherService } from './order-payment-matcher.service';

describe('OrderPaymentMatcherService', () => {
  it('no-ops when matcher is disabled', async () => {
    const runtimeStateRepository = {
      listActivePaymentContexts: jest.fn(),
    };
    const solanaClient = {
      scanIncomingTransfers: jest.fn(),
    };
    const provisioningService = {
      provisionPaidOrder: jest.fn(),
    };
    const configService = {
      get: jest.fn().mockReturnValue('false'),
    };

    const service = new OrderPaymentMatcherService(
      configService as never,
      runtimeStateRepository as never,
      solanaClient as never,
      provisioningService as never,
    );

    const result = await service.scanActiveContextsOnce();

    expect(result).toEqual({
      enabled: false,
      scannedContexts: 0,
      storedEvents: 0,
    });
    expect(runtimeStateRepository.listActivePaymentContexts).not.toHaveBeenCalled();
    expect(solanaClient.scanIncomingTransfers).not.toHaveBeenCalled();
    expect(provisioningService.provisionPaidOrder).not.toHaveBeenCalled();
  });

  it('stores scanned receipts and advances the cursor when enabled', async () => {
    const runtimeStateRepository = {
      listActivePaymentContexts: jest.fn().mockResolvedValue([
        {
          collectionAddress: 'SharedAddress111111111111111111111111111111',
          quoteAssetCode: 'USDT',
          quoteNetworkCode: 'SOLANA',
        },
      ]),
      listActiveOrdersForPaymentContext: jest.fn().mockResolvedValue([]),
      findPaymentScanCursor: jest.fn().mockResolvedValue(null),
      upsertOnchainReceipt: jest.fn().mockImplementation(async (receipt) => receipt),
      savePaymentScanCursor: jest
        .fn()
        .mockImplementation(async (cursor) => cursor),
      saveOrder: jest.fn().mockImplementation(async (order) => order),
    };
    const solanaClient = {
      getUsdtMint: jest
        .fn()
        .mockReturnValue('Es9vMFrzaCERmJfrF4H2FYD4KCoNkY11McCe8BenwNYB'),
      scanIncomingTransfers: jest.fn().mockResolvedValue({
        networkCode: 'solana-mainnet',
        collectionAddress: 'SharedAddress111111111111111111111111111111',
        assetCode: 'USDT',
        mint: 'Es9vMFrzaCERmJfrF4H2FYD4KCoNkY11McCe8BenwNYB',
        events: [
          {
            signature: 'sig_123',
            eventIndex: 0,
            slot: 123,
            blockTime: 1775890039,
            confirmationStatus: 'confirmed',
            recipientOwnerAddress:
              'SharedAddress111111111111111111111111111111',
            recipientTokenAccount: 'TokenAccount111',
            fromAddress: 'FromAddress111',
            assetCode: 'USDT',
            mint: 'Es9vMFrzaCERmJfrF4H2FYD4KCoNkY11McCe8BenwNYB',
            decimals: 6,
            amount: '58.000123',
            amountRaw: '58000123',
            rawPayload: { source: 'test' },
          },
        ],
        nextCursor: {
          beforeSignature: 'sig_123',
          minSlotExclusive: 123,
        },
        scannedAt: '2026-04-11T18:00:00.000Z',
      }),
    };
    const provisioningService = {
      provisionPaidOrder: jest.fn(),
    };
    const configService = {
      get: jest.fn().mockReturnValue('true'),
    };

    const service = new OrderPaymentMatcherService(
      configService as never,
      runtimeStateRepository as never,
      solanaClient as never,
      provisioningService as never,
    );

    const result = await service.scanActiveContextsOnce();

    expect(result.enabled).toBe(true);
    expect(result.scannedContexts).toBe(1);
    expect(result.storedEvents).toBe(1);
    expect(solanaClient.scanIncomingTransfers).toHaveBeenCalledWith(
      expect.objectContaining({
        collectionAddress: 'SharedAddress111111111111111111111111111111',
        assetCode: 'USDT',
        mint: 'Es9vMFrzaCERmJfrF4H2FYD4KCoNkY11McCe8BenwNYB',
      }),
    );
    expect(runtimeStateRepository.upsertOnchainReceipt).toHaveBeenCalledWith(
      expect.objectContaining({
        receiptId: 'SOLANA:sig_123:0',
        txHash: 'sig_123',
        amount: '58.000123',
        amountMinor: '58000123',
        matchStatus: 'UNMATCHED',
      }),
    );
    expect(runtimeStateRepository.savePaymentScanCursor).toHaveBeenCalledWith(
      expect.objectContaining({
        cursorKey: 'SOLANA:USDT:SharedAddress111111111111111111111111111111',
        beforeSignature: 'sig_123',
        lastSignature: 'sig_123',
        lastSlot: 123,
      }),
    );
    expect(runtimeStateRepository.saveOrder).not.toHaveBeenCalled();
    expect(provisioningService.provisionPaidOrder).not.toHaveBeenCalled();
  });

  it('matches a single pending order and provisions it on confirmed scan results', async () => {
    const savedOrderStatuses: string[] = [];
    const runtimeStateRepository = {
      listActivePaymentContexts: jest.fn().mockResolvedValue([
        {
          collectionAddress: 'SharedAddress111111111111111111111111111111',
          quoteAssetCode: 'USDT',
          quoteNetworkCode: 'SOLANA',
        },
      ]),
      listActiveOrdersForPaymentContext: jest.fn().mockResolvedValue([
        {
          orderId: 'order-1',
          orderNo: 'ORD-1',
          accountId: 'acct-1',
          planCode: 'BASIC_1M',
          planName: 'Basic',
          orderType: 'NEW',
          quoteAssetCode: 'USDT',
          quoteNetworkCode: 'SOLANA',
          quoteUsdAmount: '9.99000000',
          baseAmount: '9.990000',
          uniqueAmountDelta: '0.000123',
          payableAmount: '9.990123',
          status: 'AWAITING_PAYMENT',
          expiresAt: new Date(Date.now() + 60_000).toISOString(),
          confirmedAt: null,
          completedAt: null,
          failureReason: null,
          submittedClientTxHash: null,
          matchedOnchainTxHash: null,
          paymentMatchedAt: null,
          matcherRemark: null,
          createdAt: new Date(Date.now() - 60_000).toISOString(),
          idempotencyKey: 'acct-1:key',
          collectionAddress: 'SharedAddress111111111111111111111111111111',
        },
      ]),
      findPaymentScanCursor: jest.fn().mockResolvedValue(null),
      upsertOnchainReceipt: jest.fn().mockImplementation(async (receipt) => receipt),
      savePaymentScanCursor: jest
        .fn()
        .mockImplementation(async (cursor) => cursor),
      saveOrder: jest.fn().mockImplementation(async (order) => {
        savedOrderStatuses.push(order.status);
        return order;
      }),
    };
    const solanaClient = {
      getUsdtMint: jest
        .fn()
        .mockReturnValue('Es9vMFrzaCERmJfrF4H2FYD4KCoNkY11McCe8BenwNYB'),
      scanIncomingTransfers: jest.fn().mockResolvedValue({
        networkCode: 'solana-mainnet',
        collectionAddress: 'SharedAddress111111111111111111111111111111',
        assetCode: 'USDT',
        mint: 'Es9vMFrzaCERmJfrF4H2FYD4KCoNkY11McCe8BenwNYB',
        events: [
          {
            signature: 'sig_456',
            eventIndex: 0,
            slot: 456,
            blockTime: Math.floor(Date.now() / 1000),
            confirmationStatus: 'confirmed',
            recipientOwnerAddress:
              'SharedAddress111111111111111111111111111111',
            recipientTokenAccount: 'TokenAccount222',
            fromAddress: 'FromAddress222',
            assetCode: 'USDT',
            mint: 'Es9vMFrzaCERmJfrF4H2FYD4KCoNkY11McCe8BenwNYB',
            decimals: 6,
            amount: '9.990123',
            amountRaw: '9990123',
            rawPayload: { source: 'test' },
          },
        ],
        nextCursor: {
          beforeSignature: 'sig_456',
          minSlotExclusive: 456,
        },
        scannedAt: '2026-04-11T18:00:00.000Z',
      }),
    };
    const provisioningService = {
      provisionPaidOrder: jest.fn(),
    };
    const configService = {
      get: jest.fn().mockReturnValue('true'),
    };

    const service = new OrderPaymentMatcherService(
      configService as never,
      runtimeStateRepository as never,
      solanaClient as never,
      provisioningService as never,
    );

    await service.scanActiveContextsOnce();

    expect(runtimeStateRepository.upsertOnchainReceipt).toHaveBeenCalledWith(
      expect.objectContaining({
        txHash: 'sig_456',
        matchedOrderNo: 'ORD-1',
        matchStatus: 'MATCHED',
      }),
    );
    expect(savedOrderStatuses).toEqual(['PAID', 'PROVISIONING', 'COMPLETED']);
    expect(runtimeStateRepository.saveOrder.mock.calls[0][0]).toEqual(
      expect.objectContaining({
        orderNo: 'ORD-1',
        matchedOnchainTxHash: 'sig_456',
      }),
    );
    expect(provisioningService.provisionPaidOrder).toHaveBeenCalledWith(
      expect.objectContaining({
        orderNo: 'ORD-1',
      }),
    );
  });

  it('keeps ambiguous same-amount events as receipts only', async () => {
    const now = Date.now();
    const runtimeStateRepository = {
      listActivePaymentContexts: jest.fn().mockResolvedValue([
        {
          collectionAddress: 'SharedAddress111111111111111111111111111111',
          quoteAssetCode: 'USDT',
          quoteNetworkCode: 'SOLANA',
        },
      ]),
      listActiveOrdersForPaymentContext: jest.fn().mockResolvedValue([
        {
          orderNo: 'ORD-1',
          orderId: 'order-1',
          accountId: 'acct-1',
          planCode: 'BASIC_1M',
          planName: 'Basic',
          orderType: 'NEW',
          quoteAssetCode: 'USDT',
          quoteNetworkCode: 'SOLANA',
          quoteUsdAmount: '9.99000000',
          baseAmount: '9.990000',
          uniqueAmountDelta: '0.000123',
          payableAmount: '9.990123',
          status: 'AWAITING_PAYMENT',
          expiresAt: new Date(now + 60_000).toISOString(),
          confirmedAt: null,
          completedAt: null,
          failureReason: null,
          submittedClientTxHash: null,
          matchedOnchainTxHash: null,
          paymentMatchedAt: null,
          matcherRemark: null,
          createdAt: new Date(now - 60_000).toISOString(),
          idempotencyKey: 'acct-1:key-1',
          collectionAddress: 'SharedAddress111111111111111111111111111111',
        },
        {
          orderNo: 'ORD-2',
          orderId: 'order-2',
          accountId: 'acct-2',
          planCode: 'BASIC_1M',
          planName: 'Basic',
          orderType: 'NEW',
          quoteAssetCode: 'USDT',
          quoteNetworkCode: 'SOLANA',
          quoteUsdAmount: '9.99000000',
          baseAmount: '9.990000',
          uniqueAmountDelta: '0.000123',
          payableAmount: '9.990123',
          status: 'AWAITING_PAYMENT',
          expiresAt: new Date(now + 60_000).toISOString(),
          confirmedAt: null,
          completedAt: null,
          failureReason: null,
          submittedClientTxHash: null,
          matchedOnchainTxHash: null,
          paymentMatchedAt: null,
          matcherRemark: null,
          createdAt: new Date(now - 30_000).toISOString(),
          idempotencyKey: 'acct-2:key-2',
          collectionAddress: 'SharedAddress111111111111111111111111111111',
        },
      ]),
      findPaymentScanCursor: jest.fn().mockResolvedValue(null),
      upsertOnchainReceipt: jest.fn().mockImplementation(async (receipt) => receipt),
      savePaymentScanCursor: jest.fn().mockImplementation(async (cursor) => cursor),
      saveOrder: jest.fn().mockImplementation(async (order) => order),
    };
    const solanaClient = {
      getUsdtMint: jest
        .fn()
        .mockReturnValue('Es9vMFrzaCERmJfrF4H2FYD4KCoNkY11McCe8BenwNYB'),
      scanIncomingTransfers: jest.fn().mockResolvedValue({
        networkCode: 'solana-mainnet',
        collectionAddress: 'SharedAddress111111111111111111111111111111',
        assetCode: 'USDT',
        mint: 'Es9vMFrzaCERmJfrF4H2FYD4KCoNkY11McCe8BenwNYB',
        events: [
          {
            signature: 'sig_ambiguous',
            eventIndex: 0,
            slot: 500,
            blockTime: Math.floor(now / 1000),
            confirmationStatus: 'finalized',
            recipientOwnerAddress:
              'SharedAddress111111111111111111111111111111',
            recipientTokenAccount: 'TokenAccount333',
            fromAddress: 'FromAddress333',
            assetCode: 'USDT',
            mint: 'Es9vMFrzaCERmJfrF4H2FYD4KCoNkY11McCe8BenwNYB',
            decimals: 6,
            amount: '9.990123',
            amountRaw: '9990123',
            rawPayload: { source: 'test' },
          },
        ],
        nextCursor: null,
        scannedAt: '2026-04-11T18:00:00.000Z',
      }),
    };
    const provisioningService = {
      provisionPaidOrder: jest.fn(),
    };
    const configService = {
      get: jest.fn().mockReturnValue('true'),
    };

    const service = new OrderPaymentMatcherService(
      configService as never,
      runtimeStateRepository as never,
      solanaClient as never,
      provisioningService as never,
    );

    await service.scanActiveContextsOnce();

    expect(runtimeStateRepository.upsertOnchainReceipt).toHaveBeenCalledWith(
      expect.objectContaining({
        txHash: 'sig_ambiguous',
        matchedOrderNo: null,
        matchStatus: 'AMBIGUOUS',
        matcherRemark: 'MULTIPLE_PENDING_ORDERS_MATCHED',
      }),
    );
    expect(runtimeStateRepository.saveOrder).not.toHaveBeenCalled();
    expect(provisioningService.provisionPaidOrder).not.toHaveBeenCalled();
  });

  it('keeps non-confirmed events as unmatched receipts only', async () => {
    const now = Date.now();
    const runtimeStateRepository = {
      listActivePaymentContexts: jest.fn().mockResolvedValue([
        {
          collectionAddress: 'SharedAddress111111111111111111111111111111',
          quoteAssetCode: 'USDT',
          quoteNetworkCode: 'SOLANA',
        },
      ]),
      listActiveOrdersForPaymentContext: jest.fn().mockResolvedValue([
        {
          orderNo: 'ORD-1',
          orderId: 'order-1',
          accountId: 'acct-1',
          planCode: 'BASIC_1M',
          planName: 'Basic',
          orderType: 'NEW',
          quoteAssetCode: 'USDT',
          quoteNetworkCode: 'SOLANA',
          quoteUsdAmount: '9.99000000',
          baseAmount: '9.990000',
          uniqueAmountDelta: '0.000123',
          payableAmount: '9.990123',
          status: 'AWAITING_PAYMENT',
          expiresAt: new Date(now + 60_000).toISOString(),
          confirmedAt: null,
          completedAt: null,
          failureReason: null,
          submittedClientTxHash: null,
          matchedOnchainTxHash: null,
          paymentMatchedAt: null,
          matcherRemark: null,
          createdAt: new Date(now - 60_000).toISOString(),
          idempotencyKey: 'acct-1:key-1',
          collectionAddress: 'SharedAddress111111111111111111111111111111',
        },
      ]),
      findPaymentScanCursor: jest.fn().mockResolvedValue(null),
      upsertOnchainReceipt: jest.fn().mockImplementation(async (receipt) => receipt),
      savePaymentScanCursor: jest.fn().mockImplementation(async (cursor) => cursor),
      saveOrder: jest.fn().mockImplementation(async (order) => order),
    };
    const solanaClient = {
      getUsdtMint: jest
        .fn()
        .mockReturnValue('Es9vMFrzaCERmJfrF4H2FYD4KCoNkY11McCe8BenwNYB'),
      scanIncomingTransfers: jest.fn().mockResolvedValue({
        networkCode: 'solana-mainnet',
        collectionAddress: 'SharedAddress111111111111111111111111111111',
        assetCode: 'USDT',
        mint: 'Es9vMFrzaCERmJfrF4H2FYD4KCoNkY11McCe8BenwNYB',
        events: [
          {
            signature: 'sig_processed',
            eventIndex: 0,
            slot: 600,
            blockTime: Math.floor(now / 1000),
            confirmationStatus: 'processed',
            recipientOwnerAddress:
              'SharedAddress111111111111111111111111111111',
            recipientTokenAccount: 'TokenAccount444',
            fromAddress: 'FromAddress444',
            assetCode: 'USDT',
            mint: 'Es9vMFrzaCERmJfrF4H2FYD4KCoNkY11McCe8BenwNYB',
            decimals: 6,
            amount: '9.990123',
            amountRaw: '9990123',
            rawPayload: { source: 'test' },
          },
        ],
        nextCursor: null,
        scannedAt: '2026-04-11T18:00:00.000Z',
      }),
    };
    const provisioningService = {
      provisionPaidOrder: jest.fn(),
    };
    const configService = {
      get: jest.fn().mockReturnValue('true'),
    };

    const service = new OrderPaymentMatcherService(
      configService as never,
      runtimeStateRepository as never,
      solanaClient as never,
      provisioningService as never,
    );

    await service.scanActiveContextsOnce();

    expect(runtimeStateRepository.upsertOnchainReceipt).toHaveBeenCalledWith(
      expect.objectContaining({
        txHash: 'sig_processed',
        matchedOrderNo: null,
        matchStatus: 'UNMATCHED',
        matcherRemark: 'NO_PENDING_ORDER_MATCHED',
      }),
    );
    expect(runtimeStateRepository.saveOrder).not.toHaveBeenCalled();
    expect(provisioningService.provisionPaidOrder).not.toHaveBeenCalled();
  });

  it('provisions only once when duplicate matching events appear in the same scan batch', async () => {
    const now = Date.now();
    const savedOrderStatuses: string[] = [];
    const runtimeStateRepository = {
      listActivePaymentContexts: jest.fn().mockResolvedValue([
        {
          collectionAddress: 'SharedAddress111111111111111111111111111111',
          quoteAssetCode: 'USDT',
          quoteNetworkCode: 'SOLANA',
        },
      ]),
      listActiveOrdersForPaymentContext: jest.fn().mockResolvedValue([
        {
          orderNo: 'ORD-1',
          orderId: 'order-1',
          accountId: 'acct-1',
          planCode: 'BASIC_1M',
          planName: 'Basic',
          orderType: 'NEW',
          quoteAssetCode: 'USDT',
          quoteNetworkCode: 'SOLANA',
          quoteUsdAmount: '9.99000000',
          baseAmount: '9.990000',
          uniqueAmountDelta: '0.000123',
          payableAmount: '9.990123',
          status: 'AWAITING_PAYMENT',
          expiresAt: new Date(now + 60_000).toISOString(),
          confirmedAt: null,
          completedAt: null,
          failureReason: null,
          submittedClientTxHash: null,
          matchedOnchainTxHash: null,
          paymentMatchedAt: null,
          matcherRemark: null,
          createdAt: new Date(now - 60_000).toISOString(),
          idempotencyKey: 'acct-1:key-1',
          collectionAddress: 'SharedAddress111111111111111111111111111111',
        },
      ]),
      findPaymentScanCursor: jest.fn().mockResolvedValue(null),
      upsertOnchainReceipt: jest.fn().mockImplementation(async (receipt) => receipt),
      savePaymentScanCursor: jest.fn().mockImplementation(async (cursor) => cursor),
      saveOrder: jest.fn().mockImplementation(async (order) => {
        savedOrderStatuses.push(order.status);
        return order;
      }),
    };
    const solanaClient = {
      getUsdtMint: jest
        .fn()
        .mockReturnValue('Es9vMFrzaCERmJfrF4H2FYD4KCoNkY11McCe8BenwNYB'),
      scanIncomingTransfers: jest.fn().mockResolvedValue({
        networkCode: 'solana-mainnet',
        collectionAddress: 'SharedAddress111111111111111111111111111111',
        assetCode: 'USDT',
        mint: 'Es9vMFrzaCERmJfrF4H2FYD4KCoNkY11McCe8BenwNYB',
        events: [
          {
            signature: 'sig_dup',
            eventIndex: 0,
            slot: 700,
            blockTime: Math.floor(now / 1000),
            confirmationStatus: 'confirmed',
            recipientOwnerAddress:
              'SharedAddress111111111111111111111111111111',
            recipientTokenAccount: 'TokenAccount555',
            fromAddress: 'FromAddress555',
            assetCode: 'USDT',
            mint: 'Es9vMFrzaCERmJfrF4H2FYD4KCoNkY11McCe8BenwNYB',
            decimals: 6,
            amount: '9.990123',
            amountRaw: '9990123',
            rawPayload: { source: 'test' },
          },
          {
            signature: 'sig_dup_retry',
            eventIndex: 1,
            slot: 701,
            blockTime: Math.floor(now / 1000),
            confirmationStatus: 'confirmed',
            recipientOwnerAddress:
              'SharedAddress111111111111111111111111111111',
            recipientTokenAccount: 'TokenAccount555',
            fromAddress: 'FromAddress555',
            assetCode: 'USDT',
            mint: 'Es9vMFrzaCERmJfrF4H2FYD4KCoNkY11McCe8BenwNYB',
            decimals: 6,
            amount: '9.990123',
            amountRaw: '9990123',
            rawPayload: { source: 'test' },
          },
        ],
        nextCursor: null,
        scannedAt: '2026-04-11T18:00:00.000Z',
      }),
    };
    const provisioningService = {
      provisionPaidOrder: jest.fn(),
    };
    const configService = {
      get: jest.fn().mockReturnValue('true'),
    };

    const service = new OrderPaymentMatcherService(
      configService as never,
      runtimeStateRepository as never,
      solanaClient as never,
      provisioningService as never,
    );

    await service.scanActiveContextsOnce();

    expect(savedOrderStatuses).toEqual(['PAID', 'PROVISIONING', 'COMPLETED']);
    expect(provisioningService.provisionPaidOrder).toHaveBeenCalledTimes(1);
    const receiptCalls = runtimeStateRepository.upsertOnchainReceipt.mock.calls;
    expect(receiptCalls).toHaveLength(2);
    expect(receiptCalls[0][0]).toEqual(
      expect.objectContaining({
        txHash: 'sig_dup',
        matchedOrderNo: 'ORD-1',
        matchStatus: 'MATCHED',
      }),
    );
    expect(receiptCalls[1][0]).toEqual(
      expect.objectContaining({
        txHash: 'sig_dup_retry',
        matchedOrderNo: null,
        matchStatus: 'UNMATCHED',
      }),
    );
  });
});
