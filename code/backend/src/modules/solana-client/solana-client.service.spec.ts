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
      getOrderedRpcUrls: () => ['https://private-solana-rpc.local'],
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
      assetDecimals: 6,
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
        assetDecimals: 6,
      }),
      expect.any(Object),
    );
  });

  it('passes mint and decimals for custom SPL token verification', async () => {
    const httpService = {
      post: jest.fn().mockReturnValue(
        of({
          data: {
            status: 'verified',
            signature: 'sig-andy',
            recipientAddress: 'EVYe1JoVU9m46o5QLgJdZM6CCG996jfCvYoKu5DTNEjj',
            assetCode: 'ANDY',
            mintAddress: '8zFP8GeszFz7FvuHesguekTxDjm4KLsJEYBZTKyMLEoE',
            expectedAmount: '29693.170570770',
            receivedAmount: '29693.170570770',
            recipientMatched: true,
            amountSatisfied: true,
            blockTime: 1776950131,
            slot: 412450900,
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
      getOrderedRpcUrls: () => ['https://private-solana-rpc.local'],
    };

    const service = new SolanaClientService(
      httpService as never,
      config as never,
    );

    jest
      .spyOn(service, 'getTransactionStatus')
      .mockResolvedValue({
        signature: 'sig-andy',
        status: 'confirmed',
        confirmations: 1,
      });

    const result = await service.verifyIncomingTransfer({
      signature: 'sig-andy',
      recipientAddress: 'EVYe1JoVU9m46o5QLgJdZM6CCG996jfCvYoKu5DTNEjj',
      assetCode: 'ANDY',
      mint: '8zFP8GeszFz7FvuHesguekTxDjm4KLsJEYBZTKyMLEoE',
      assetDecimals: 9,
      expectedAmount: '29693.170570770',
    });

    expect(result.verified).toBe(true);
    expect(httpService.post).toHaveBeenCalledWith(
      'https://sol.residential-agent.com/api/internal/v1/payment/verify',
      expect.objectContaining({
        assetCode: 'ANDY',
        mintAddress: '8zFP8GeszFz7FvuHesguekTxDjm4KLsJEYBZTKyMLEoE',
        assetDecimals: 9,
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
      getOrderedRpcUrls: () => ['https://private-solana-rpc.local'],
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
              {
                signature: 'sig-older',
                slot: 980,
                blockTime: 1775896900,
                confirmationStatus: 'finalized',
                collectionAddress:
                  'SharedAddress111111111111111111111111111111',
                assetCode: 'USDT',
                mintAddress: 'Es9vMFrzaCERmJfrF4H2FYD4KCoNkY11McCe8BenwNYB',
                decimals: 6,
                amount: '57.999999',
                amountRaw: '57999999',
                matchedAccounts: ['TokenAccount112'],
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
      getOrderedRpcUrls: () => ['https://private-solana-rpc.local'],
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
        minSlotExclusive: 123,
        limit: 25,
      }),
      expect.any(Object),
    );

    expect(result.networkCode).toBe('solana-mainnet');
    expect(result.collectionAddress).toBe(
      'SharedAddress111111111111111111111111111111',
    );
    expect(result.assetCode).toBe('USDT');
    expect(result.mint).toBe('Es9vMFrzaCERmJfrF4H2FYD4KCoNkY11McCe8BenwNYB');
    expect(result.nextCursor).toEqual({
      beforeSignature: 'sig-oldest',
      minSlotExclusive: 987,
    });
    expect(result.events).toEqual(
      expect.arrayContaining([
        expect.objectContaining({
          signature: 'sig-newest',
          eventIndex: 0,
          recipientOwnerAddress:
            'SharedAddress111111111111111111111111111111',
          recipientTokenAccount: 'TokenAccount111',
          assetCode: 'USDT',
          amount: '58.000123',
          amountRaw: '58000123',
          confirmationStatus: 'confirmed',
        }),
        expect.objectContaining({
          signature: 'sig-older',
          eventIndex: 1,
          recipientOwnerAddress:
            'SharedAddress111111111111111111111111111111',
          recipientTokenAccount: 'TokenAccount112',
          assetCode: 'USDT',
          amount: '57.999999',
          amountRaw: '57999999',
          confirmationStatus: 'finalized',
        }),
      ]),
    );
  });

  it('prefers nextMinSlotExclusive from chain-side payload when present', async () => {
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
            nextMinSlotExclusive: 2000,
            items: [
              {
                signature: 'sig-newest',
                slot: 1990,
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
      getOrderedRpcUrls: () => ['https://private-solana-rpc.local'],
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

    expect(result.nextCursor).toEqual({
      beforeSignature: 'sig-oldest',
      minSlotExclusive: 2000,
    });
  });

  it('falls back to direct RPC balance query when chain-side balance endpoint fails', async () => {
    const httpService = {
      post: jest.fn(),
      get: jest.fn(() => {
        throw new Error('404');
      }),
    };

    const config = {
      isEnabled: () => true,
      getTimeoutMs: () => 1000,
      getApiKey: () => undefined,
      useDevnet: () => false,
      getBaseUrl: () => 'https://sol.residential-agent.com',
      getMaxRetries: () => 3,
      getOrderedRpcUrls: () => [
        'https://private-solana-rpc.local',
        'https://api.mainnet-beta.solana.com',
      ],
    };

    const service = new SolanaClientService(
      httpService as never,
      config as never,
    );

    (service as any).getBalanceViaRpc = jest.fn().mockResolvedValue({
      address: 'Wallet111',
      mint: null,
      balance: '0',
      decimals: 9,
      uiAmount: '0',
    });

    const result = await service.getBalance({
      address: 'Wallet111',
    });

    expect(result).toEqual({
      address: 'Wallet111',
      mint: null,
      balance: '0',
      decimals: 9,
      uiAmount: '0',
    });
  });
});
