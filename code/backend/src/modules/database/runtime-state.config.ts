import { ConfigService } from '@nestjs/config';
import { resolve } from 'path';

export type RuntimeStateBackend = 'file' | 'postgres';

export interface RuntimeStateConfig {
  backend: RuntimeStateBackend;
  filePath: string;
  databaseUrl?: string;
}

export function resolveRuntimeStateConfig(
  configService: Pick<ConfigService, 'get'>,
): RuntimeStateConfig {
  const nodeEnv = (
    configService.get<string>('NODE_ENV') ??
    process.env.NODE_ENV ??
    'development'
  ).toLowerCase();
  const backend = resolveRuntimeStateBackend(configService, nodeEnv);
  const filePath = resolve(
    configService.get<string>('RUNTIME_STATE_FILE') ??
      `${process.cwd()}/.runtime/runtime-state.json`,
  );

  if (backend === 'file') {
    return {
      backend,
      filePath,
    };
  }

  const databaseUrl = configService.get<string>('DATABASE_URL');
  if (!databaseUrl) {
    throw new Error(
      'DATABASE_URL is required when runtime state backend resolves to Postgres',
    );
  }

  return {
    backend,
    filePath,
    databaseUrl,
  };
}

function resolveRuntimeStateBackend(
  configService: Pick<ConfigService, 'get'>,
  nodeEnv: string,
): RuntimeStateBackend {
  const rawBackend = configService.get<string>('RUNTIME_STATE_BACKEND');
  const normalizedBackend = rawBackend?.trim().toLowerCase();

  if (!normalizedBackend) {
    return nodeEnv === 'production' ? 'postgres' : 'file';
  }

  if (normalizedBackend === 'postgres') {
    return 'postgres';
  }

  if (normalizedBackend === 'file') {
    if (nodeEnv === 'production') {
      throw new Error(
        'RUNTIME_STATE_BACKEND=file is not allowed when NODE_ENV=production',
      );
    }
    return 'file';
  }

  throw new Error(
    `Unsupported RUNTIME_STATE_BACKEND value "${rawBackend}". Expected "postgres" or "file".`,
  );
}
