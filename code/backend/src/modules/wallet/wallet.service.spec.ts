import { WalletService } from './wallet.service';

describe('WalletService', () => {
  const authService = {
    getMe: () => ({
      accountId: 'acct-1',
      email: 'acct-1@example.com',
    }),
  };

  const noop = {} as never;

  it('advertises configured private rpc endpoints before public fallbacks', () => {
    const values: Record<string, string> = {
      SOLANA_RPC_URL_MAINNETS:
        'https://sol-private-1.local,https://sol-private-2.local',
      TRON_FULL_NODES: 'https://tron-private-1.local,https://tron-private-2.local',
      SOLANA_PUBLIC_RPC_URL_MAINNET: 'https://sol-public-fallback.local',
      TRON_PUBLIC_FULL_NODE: 'https://tron-public-fallback.local',
    };

    const service = new WalletService(
      {
        get: (key: string) => values[key],
      } as never,
      authService as never,
      noop,
      noop,
      noop,
      {} as never,
      {} as never,
      noop,
      noop,
      noop,
    );

    expect(service.getChains('token').items).toEqual([
      expect.objectContaining({
        networkCode: 'SOLANA',
        publicRpcUrl: 'https://sol-private-1.local',
      }),
      expect.objectContaining({
        networkCode: 'TRON',
        publicRpcUrl: 'https://tron-private-1.local',
      }),
    ]);
  });

  it('prefers client-provided rpc ordering when helper methods are available', () => {
    const service = new WalletService(
      {
        get: () => undefined,
      } as never,
      authService as never,
      noop,
      noop,
      noop,
      {
        getPreferredRpcUrl: () => 'https://sol-client-priority.local',
      } as never,
      {
        getPreferredFullNodeUrl: () => 'https://tron-client-priority.local',
      } as never,
      noop,
      noop,
      noop,
    );

    expect(service.getChains('token').items).toEqual([
      expect.objectContaining({
        networkCode: 'SOLANA',
        publicRpcUrl: 'https://sol-client-priority.local',
      }),
      expect.objectContaining({
        networkCode: 'TRON',
        publicRpcUrl: 'https://tron-client-priority.local',
      }),
    ]);
  });
});
