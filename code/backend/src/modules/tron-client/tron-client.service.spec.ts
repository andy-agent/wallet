import { of } from 'rxjs';
import { TronClientService } from './tron-client.service';

describe('TronClientService', () => {
  it('verifies a confirmed TRON USDT payment from chain-side query response', async () => {
    const httpService = {
      get: jest.fn().mockReturnValue(
        of({
          data: {
            found: true,
            transaction: {
              txHash: 'tron-good-tx',
              status: 'confirmed',
              from: 'TFrom11111111111111111111111111111111',
              to: 'TLa2f6VPqDgRE67v1736s7bJ8Ray5wYjU7',
              amount: '3.000000',
              token: 'USDT',
              confirmations: 12,
            },
          },
        }),
      ),
      post: jest.fn(),
    };

    const config = {
      isEnabled: () => true,
      getTimeoutMs: () => 1000,
      getApiKey: () => 'tron-key',
      getBaseUrl: () => 'https://usdt.residential-agent.com/api',
      getOrderedFullNodeUrls: () => ['https://api.trongrid.io'],
      getPreferredFullNodeUrl: () => 'https://api.trongrid.io',
    };

    const service = new TronClientService(
      httpService as never,
      config as never,
    );

    const result = await service.verifyIncomingTransfer({
      txHash: 'tron-good-tx',
      recipientAddress: 'TLa2f6VPqDgRE67v1736s7bJ8Ray5wYjU7',
      assetCode: 'USDT',
      expectedAmount: '3.000000',
      assetDecimals: 6,
    });

    expect(result).toEqual(
      expect.objectContaining({
        signature: 'tron-good-tx',
        status: 'confirmed',
        confirmations: 12,
        verified: true,
        recipientAddress: 'TLa2f6VPqDgRE67v1736s7bJ8Ray5wYjU7',
        assetCode: 'USDT',
        amount: '3.000000',
      }),
    );
  });

  it('maps underpaid TRON payment to AMOUNT_UNDER mismatch', async () => {
    const httpService = {
      get: jest.fn().mockReturnValue(
        of({
          data: {
            found: true,
            transaction: {
              txHash: 'tron-underpaid-tx',
              status: 'confirmed',
              from: 'TFrom11111111111111111111111111111111',
              to: 'TLa2f6VPqDgRE67v1736s7bJ8Ray5wYjU7',
              amount: '2.500000',
              token: 'USDT',
              confirmations: 4,
            },
          },
        }),
      ),
      post: jest.fn(),
    };

    const config = {
      isEnabled: () => true,
      getTimeoutMs: () => 1000,
      getApiKey: () => 'tron-key',
      getBaseUrl: () => 'https://usdt.residential-agent.com/api',
      getOrderedFullNodeUrls: () => ['https://api.trongrid.io'],
      getPreferredFullNodeUrl: () => 'https://api.trongrid.io',
    };

    const service = new TronClientService(
      httpService as never,
      config as never,
    );

    const result = await service.verifyIncomingTransfer({
      txHash: 'tron-underpaid-tx',
      recipientAddress: 'TLa2f6VPqDgRE67v1736s7bJ8Ray5wYjU7',
      assetCode: 'USDT',
      expectedAmount: '3.000000',
      assetDecimals: 6,
    });

    expect(result.verified).toBe(false);
    expect(result.mismatchCode).toBe('AMOUNT_UNDER');
    expect(result.failureReason).toContain('below the expected payment target');
  });
});
