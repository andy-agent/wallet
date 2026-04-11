import { OrderPaymentMatcherService } from './order-payment-matcher.service';

describe('OrderPaymentMatcherService', () => {
  it('no-ops when matcher is disabled', async () => {
    const runtimeStateRepository = {
      listActivePaymentContexts: jest.fn(),
    };
    const solanaClient = {
      scanIncomingTransfers: jest.fn(),
    };
    const configService = {
      get: jest.fn().mockReturnValue('false'),
    };

    const service = new OrderPaymentMatcherService(
      configService as never,
      runtimeStateRepository as never,
      solanaClient as never,
    );

    const result = await service.scanActiveContextsOnce();

    expect(result).toEqual({
      enabled: false,
      scannedContexts: 0,
      storedEvents: 0,
    });
    expect(runtimeStateRepository.listActivePaymentContexts).not.toHaveBeenCalled();
    expect(solanaClient.scanIncomingTransfers).not.toHaveBeenCalled();
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
      findPaymentScanCursor: jest.fn().mockResolvedValue(null),
      upsertOnchainReceipt: jest.fn().mockImplementation(async (receipt) => receipt),
      savePaymentScanCursor: jest
        .fn()
        .mockImplementation(async (cursor) => cursor),
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
    const configService = {
      get: jest.fn().mockReturnValue('true'),
    };

    const service = new OrderPaymentMatcherService(
      configService as never,
      runtimeStateRepository as never,
      solanaClient as never,
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
  });
});
