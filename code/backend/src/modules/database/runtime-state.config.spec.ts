import { resolveRuntimeStateConfig } from './runtime-state.config';

describe('resolveRuntimeStateConfig', () => {
  it('defaults to Postgres in production', () => {
    const config = resolveRuntimeStateConfig(
      createConfigService({
        NODE_ENV: 'production',
        DATABASE_URL: 'postgres://runtime:runtime@server2:5432/cryptovpn',
      }),
    );

    expect(config.backend).toBe('postgres');
    expect(config.databaseUrl).toContain('server2:5432');
  });

  it('defaults to file storage in development and test', () => {
    expect(
      resolveRuntimeStateConfig(
        createConfigService({
          NODE_ENV: 'development',
          DATABASE_URL: 'postgres://runtime:runtime@server2:5432/cryptovpn',
        }),
      ).backend,
    ).toBe('file');

    expect(
      resolveRuntimeStateConfig(
        createConfigService({
          NODE_ENV: 'test',
          DATABASE_URL: 'postgres://runtime:runtime@server2:5432/cryptovpn',
        }),
      ).backend,
    ).toBe('file');
  });

  it('allows explicitly opting into Postgres outside production', () => {
    const config = resolveRuntimeStateConfig(
      createConfigService({
        NODE_ENV: 'test',
        RUNTIME_STATE_BACKEND: 'postgres',
        DATABASE_URL: 'postgres://runtime:runtime@server2:5432/cryptovpn',
      }),
    );

    expect(config.backend).toBe('postgres');
  });

  it('rejects production file fallback', () => {
    expect(() =>
      resolveRuntimeStateConfig(
        createConfigService({
          NODE_ENV: 'production',
          RUNTIME_STATE_BACKEND: 'file',
        }),
      ),
    ).toThrow('RUNTIME_STATE_BACKEND=file is not allowed');
  });

  it('does not silently fall back to file when production Postgres is misconfigured', () => {
    expect(() =>
      resolveRuntimeStateConfig(
        createConfigService({
          NODE_ENV: 'production',
        }),
      ),
    ).toThrow('DATABASE_URL is required');
  });
});

function createConfigService(values: Record<string, string | undefined>) {
  return {
    get: (key: string) => values[key],
  };
}
