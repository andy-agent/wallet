import { SolanaRpcService } from './solana.rpc.service';

describe('SolanaRpcService', () => {
  function createService(values: Record<string, string | undefined> = {}) {
    const configService = {
      get: jest.fn((key: string) => values[key]),
    };

    return new SolanaRpcService(configService as never);
  }

  it('prefers configured private rpc and ws endpoints', () => {
    const service = createService({
      SOLANA_RPC_URL_MAINNET:
        'https://sly-boldest-field.solana-mainnet.quiknode.pro/token/',
      SOLANA_WS_URL_MAINNET:
        'wss://sly-boldest-field.solana-mainnet.quiknode.pro/token/',
    });

    expect(service.getPreferredRpcUrl('solana-mainnet')).toBe(
      'https://sly-boldest-field.solana-mainnet.quiknode.pro/token/',
    );
    expect(service.getPreferredWsUrl('solana-mainnet')).toBe(
      'wss://sly-boldest-field.solana-mainnet.quiknode.pro/token/',
    );
  });

  it('derives ws endpoint from rpc url when no explicit ws url is configured', () => {
    const service = createService({
      SOLANA_RPC_URL_MAINNET: 'https://rpc.example.com/path',
    });

    expect(service.getPreferredRpcUrl('solana-mainnet')).toBe(
      'https://rpc.example.com/path',
    );
    expect(service.getPreferredWsUrl('solana-mainnet')).toBe(
      'wss://rpc.example.com/path',
    );
  });

  it('aggregates SPL token balances across owner token accounts', async () => {
    const service = createService();
    const connection = {
      getParsedTokenAccountsByOwner: jest.fn().mockResolvedValue({
        value: [
          {
            account: {
              data: {
                parsed: {
                  info: {
                    mint: 'Es9vMFrzaCERmJfrF4H2FYD4KCoNkY11McCe8BenwNYB',
                    tokenAmount: {
                      amount: '1000000',
                      decimals: 6,
                    },
                  },
                },
              },
            },
          },
          {
            account: {
              data: {
                parsed: {
                  info: {
                    mint: 'Es9vMFrzaCERmJfrF4H2FYD4KCoNkY11McCe8BenwNYB',
                    tokenAmount: {
                      amount: '2500000',
                      decimals: 6,
                    },
                  },
                },
              },
            },
          },
          {
            account: {
              data: {
                parsed: {
                  info: {
                    mint: 'So11111111111111111111111111111111111111112',
                    tokenAmount: {
                      amount: '9900000',
                      decimals: 9,
                    },
                  },
                },
              },
            },
          },
        ],
      }),
    };

    jest.spyOn(service, 'getConnection').mockReturnValue(connection as never);

    const result = await service.getBalance(
      '11111111111111111111111111111111',
      'solana-mainnet',
      'Es9vMFrzaCERmJfrF4H2FYD4KCoNkY11McCe8BenwNYB',
    );

    expect(connection.getParsedTokenAccountsByOwner).toHaveBeenCalled();
    expect(result).toEqual({
      address: '11111111111111111111111111111111',
      balance: 3500000,
      balanceUiAmount: '3.5',
      balanceInSOL: '3.5',
      decimals: 6,
      networkCode: 'solana-mainnet',
      mintAddress: 'Es9vMFrzaCERmJfrF4H2FYD4KCoNkY11McCe8BenwNYB',
    });
  });

  it('returns slot information after broadcasting a transaction', async () => {
    const service = createService();
    const connection = {
      sendRawTransaction: jest.fn().mockResolvedValue('sig-123'),
      confirmTransaction: jest.fn().mockResolvedValue({
        value: { err: null },
      }),
      getSignatureStatuses: jest.fn().mockResolvedValue({
        value: [{ slot: 456 }],
      }),
    };

    jest.spyOn(service, 'getConnection').mockReturnValue(connection as never);

    const result = await service.broadcastTransaction(
      Buffer.from('signed-transaction').toString('base64'),
      'solana-mainnet',
      { maxRetries: 3 },
    );

    expect(connection.sendRawTransaction).toHaveBeenCalledWith(
      Buffer.from('signed-transaction'),
      expect.objectContaining({
        skipPreflight: false,
        maxRetries: 3,
      }),
    );
    expect(result).toEqual({
      signature: 'sig-123',
      confirmed: true,
      slot: 456,
    });
  });

  it('reports realtime health via slot subscription', async () => {
    const service = createService({
      SOLANA_WS_URL_MAINNET: 'wss://rpc.example.com/path',
    });
    const removeSlotChangeListener = jest.fn().mockResolvedValue(undefined);
    const connection = {
      onSlotChange: jest.fn((callback: (slotInfo: { slot: number }) => void) => {
        setImmediate(() => callback({ slot: 789 }));
        return 11;
      }),
      removeSlotChangeListener,
    };

    jest.spyOn(service, 'getConnection').mockReturnValue(connection as never);

    const result = await service.checkRealtimeHealth('solana-mainnet');

    expect(result).toEqual({
      healthy: true,
      network: 'solana-mainnet',
      slot: 789,
      wsUrl: 'wss://rpc.example.com/path',
    });
    expect(removeSlotChangeListener).toHaveBeenCalledWith(11);
  });
});
