import { SolanaClientConfig } from './solana-client.config';

describe('SolanaClientConfig', () => {
  it('orders private rpc urls before public fallbacks', () => {
    const values: Record<string, string> = {
      SOLANA_RPC_URL_MAINNETS:
        'https://sol-private-1.local, https://sol-private-2.local',
      SOLANA_PUBLIC_RPC_URL_MAINNET: 'https://sol-public-fallback.local',
    };

    const config = new SolanaClientConfig({
      get: (key: string) => values[key],
    } as never);

    expect(config.getOrderedRpcUrls('mainnet')).toEqual([
      'https://sol-private-1.local',
      'https://sol-private-2.local',
      'https://sol-public-fallback.local',
    ]);
    expect(config.getPreferredRpcUrl('mainnet')).toBe(
      'https://sol-private-1.local',
    );
  });

  it('prefers explicit websocket urls and otherwise derives them from rpc urls', () => {
    const values: Record<string, string> = {
      SOLANA_RPC_URL_MAINNET: 'https://sol-private-1.local',
      SOLANA_WS_URL_MAINNET: 'wss://sol-private-ws.local',
    };

    const config = new SolanaClientConfig({
      get: (key: string) => values[key],
    } as never);

    expect(config.getPreferredWsUrl('mainnet')).toBe(
      'wss://sol-private-ws.local',
    );

    const derivedConfig = new SolanaClientConfig({
      get: (key: string) =>
        key === 'SOLANA_RPC_URL_MAINNET'
          ? 'https://sol-private-2.local/path'
          : undefined,
    } as never);

    expect(derivedConfig.getPreferredWsUrl('mainnet')).toBe(
      'wss://sol-private-2.local/path',
    );
  });

  it('falls back to the default public rpc when no custom urls are configured', () => {
    const config = new SolanaClientConfig({
      get: () => undefined,
    } as never);

    expect(config.getOrderedRpcUrls('mainnet')).toEqual([
      'https://api.mainnet-beta.solana.com',
    ]);
    expect(config.getOrderedRpcUrls('devnet')).toEqual([
      'https://api.devnet.solana.com',
    ]);
  });
});
