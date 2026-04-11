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
});
