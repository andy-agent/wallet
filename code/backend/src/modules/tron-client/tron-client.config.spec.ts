import { TronClientConfig } from './tron-client.config';

describe('TronClientConfig', () => {
  it('orders private full nodes before public fallbacks', () => {
    const values: Record<string, string> = {
      TRON_FULL_NODES:
        'https://tron-private-1.local,https://tron-private-2.local',
      TRON_PUBLIC_FULL_NODE: 'https://tron-public-fallback.local',
    };

    const config = new TronClientConfig({
      get: (key: string) => values[key],
    } as never);

    expect(config.getOrderedFullNodeUrls()).toEqual([
      'https://tron-private-1.local',
      'https://tron-private-2.local',
      'https://tron-public-fallback.local',
    ]);
    expect(config.getPreferredFullNodeUrl()).toBe(
      'https://tron-private-1.local',
    );
  });

  it('falls back to public trongrid when no custom full node is configured', () => {
    const config = new TronClientConfig({
      get: () => undefined,
    } as never);

    expect(config.getOrderedFullNodeUrls()).toEqual([
      'https://api.trongrid.io',
    ]);
  });
});
