import { of } from 'rxjs';
import { SolanaClientService } from './solana-client.service';

describe('SolanaClientService', () => {
  it('treats verify mismatch as confirmed verification evidence even when tx-status is pending', async () => {
    const httpService = {
      post: jest.fn().mockReturnValue(
        of({
          data: {
            status: 'mismatch',
            recipientAddress: 'ChbtUwZqyi2wePLDTs57UyPPbn8jSpHTbn49FQWuX6wG',
            receivedAmount: '0',
            recipientMatched: false,
            amountSatisfied: false,
            blockTime: 1775888281,
            slot: 412450831,
          },
        }),
      ),
      get: jest.fn(),
    };

    const config = {
      isEnabled: () => true,
      getTimeoutMs: () => 1000,
      getApiKey: () => 'test-token',
      useDevnet: () => false,
      getBaseUrl: () => 'https://sol.residential-agent.com',
      getMaxRetries: () => 3,
    };

    const service = new SolanaClientService(
      httpService as never,
      config as never,
    );

    jest
      .spyOn(service, 'getTransactionStatus')
      .mockResolvedValue({
        signature: 'sig-mismatch',
        status: 'pending',
        confirmations: 0,
      });

    const result = await service.verifyIncomingTransfer({
      signature: 'sig-mismatch',
      recipientAddress: 'ChbtUwZqyi2wePLDTs57UyPPbn8jSpHTbn49FQWuX6wG',
      assetCode: 'USDT',
      mint: 'Es9vMFrzaCERmJfrF4H2FYD4KCoNkY11McCe8BenwNYB',
      expectedAmount: '9.990000',
    });

    expect(result.status).toBe('confirmed');
    expect(result.verified).toBe(false);
    expect(result.mismatchCode).toBe('RECIPIENT_MISMATCH');
    expect(result.failureReason).toContain('collection address');
    expect(httpService.post).toHaveBeenCalledWith(
      'https://sol.residential-agent.com/api/internal/v1/payment/verify',
      expect.objectContaining({
        mintAddress: 'Es9vMFrzaCERmJfrF4H2FYD4KCoNkY11McCe8BenwNYB',
      }),
      expect.any(Object),
    );
  });

  it('returns an empty scan response when chain-side service is disabled', async () => {
    const httpService = {
      post: jest.fn(),
      get: jest.fn(),
    };

    const config = {
      isEnabled: () => false,
      getTimeoutMs: () => 1000,
      getApiKey: () => 'test-token',
      useDevnet: () => false,
      getBaseUrl: () => 'https://sol.residential-agent.com',
      getMaxRetries: () => 3,
    };

    const service = new SolanaClientService(
      httpService as never,
      config as never,
    );

    const result = await service.scanIncomingTransfers({
      collectionAddress: 'SharedAddress111111111111111111111111111111',
      assetCode: 'USDT',
      mint: 'Es9vMFrzaCERmJfrF4H2FYD4KCoNkY11McCe8BenwNYB',
    });

    expect(result).toEqual({
      networkCode: 'solana-mainnet',
      collectionAddress: 'SharedAddress111111111111111111111111111111',
      assetCode: 'USDT',
      mint: 'Es9vMFrzaCERmJfrF4H2FYD4KCoNkY11McCe8BenwNYB',
      events: [],
      nextCursor: null,
      scannedAt: expect.any(String),
    });
    expect(httpService.post).not.toHaveBeenCalled();
  });

  it('normalizes the shared-address scan response from sol-agent', async () => {
    const httpService = {
      post: jest.fn().mockReturnValue(
        of({
          data: {
            networkCode: 'solana-mainnet',
            collectionAddress:
              'SharedAddress111111111111111111111111111111',
            assetCode: 'USDT',
            mintAddress: 'Es9vMFrzaCERmJfrF4H2FYD4KCoNkY11McCe8BenwNYB',
            nextBeforeSignature: 'sig-oldest',
            items: [
              {
                signature: 'sig-newest',
                slot: 987,
                blockTime: 1775897000,
                confirmationStatus: 'confirmed',
                collectionAddress:
                  'SharedAddress111111111111111111111111111111',
                assetCode: 'USDT',
                mintAddress: 'Es9vMFrzaCERmJfrF4H2FYD4KCoNkY11McCe8BenwNYB',
                decimals: 6,
                amount: '58.000123',
                amountRaw: '58000123',
                matchedAccounts: ['TokenAccount111'],
              },
            ],
          },
        }),
      ),
      get: jest.fn(),
    };

    const config = {
      isEnabled: () => true,
      getTimeoutMs: () => 1000,
      getApiKey: () => 'test-token',
      useDevnet: () => false,
      getBaseUrl: () => 'https://sol.residential-agent.com',
      getMaxRetries: () => 3,
    };

    const service = new SolanaClientService(
      httpService as never,
      config as never,
    );

    const result = await service.scanIncomingTransfers({
      collectionAddress: 'SharedAddress111111111111111111111111111111',
      assetCode: 'USDT',
      mint: 'Es9vMFrzaCERmJfrF4H2FYD4KCoNkY11McCe8BenwNYB',
      cursor: {
        beforeSignature: 'sig-previous',
        minSlotExclusive: 123,
      },
      limit: 25,
    });

    expect(httpService.post).toHaveBeenCalledWith(
      'https://sol.residential-agent.com/api/internal/v1/payment/scan-incoming',
      expect.objectContaining({
        networkCode: 'solana-mainnet',
        collectionAddress: 'SharedAddress111111111111111111111111111111',
        assetCode: 'USDT',
        mintAddress: 'Es9vMFrzaCERmJfrF4H2FYD4KCoNkY11McCe8BenwNYB',
        beforeSignature: 'sig-previous',
        limit: 25,
      }),
      expect.any(Object),
    );

    expect(result).toMatchObject({
      networkCode: 'solana-mainnet',
      collectionAddress: 'SharedAddress111111111111111111111111111111',
      assetCode: 'USDT',
      mint: 'Es9vMFrzaCERmJfrF4H2FYD4KCoNkY11McCe8BenwNYB',
      nextCursor: {
        beforeSignature: 'sig-oldest',
        minSlotExclusive: 987,
      },
      events: [
        {
          signature: 'sig-newest',
          eventIndex: 0,
          recipientOwnerAddress:
            'SharedAddress111111111111111111111111111111',
          recipientTokenAccount: 'TokenAccount111',
          assetCode: 'USDT',
          amount: '58.000123',
          amountRaw: '58000123',
          confirmationStatus: 'confirmed',
        },
      ],
    });
  });
});
