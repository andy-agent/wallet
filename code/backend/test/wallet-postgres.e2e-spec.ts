import { INestApplication, ValidationPipe } from '@nestjs/common';
import { Test, TestingModule } from '@nestjs/testing';
import { newDb } from 'pg-mem';
import * as request from 'supertest';

const runtimeDb = newDb({ noAstCoverageCheck: true });

jest.mock('pg', () => runtimeDb.adapters.createPg());

import { AppModule } from '../src/app.module';
import { AllExceptionsFilter } from '../src/common/filters/all-exceptions.filter';
import { ResponseEnvelopeInterceptor } from '../src/common/interceptors/response-envelope.interceptor';

describe('Wallet Postgres persistence (e2e)', () => {
  let app: INestApplication;
  let accessToken: string;
  let email: string;

  beforeEach(async () => {
    process.env.NODE_ENV = 'production';
    process.env.DATABASE_URL =
      'postgres://runtime:runtime@server2:5432/cryptovpn';
    delete process.env.RUNTIME_STATE_BACKEND;
    delete process.env.RUNTIME_STATE_FILE;
    process.env.WALLET_BACKUP_RECIPIENTS =
      'age1wp4zfn93luhad3qmzrvw60p5knc7lg736pxuhs4kkkzty0c5m58sn8p004';

    app = await bootstrapApp();
    email = `wallet-pg-${Date.now()}-${Math.random().toString(16).slice(2, 8)}@example.com`;

    await request(app.getHttpServer())
      .post('/api/client/v1/auth/register/email/request-code')
      .send({ email })
      .expect(200);

    const registerResponse = await request(app.getHttpServer())
      .post('/api/client/v1/auth/register/email')
      .set('x-idempotency-key', `wallet-pg-register-${Date.now()}`)
      .send({
        email,
        code: '123456',
        password: 'Passw0rd!',
      })
      .expect(200);

    accessToken = registerResponse.body.data.accessToken;
  });

  afterEach(async () => {
    await app.close();
    cleanupRuntimeTables();
    delete process.env.NODE_ENV;
    delete process.env.DATABASE_URL;
    delete process.env.RUNTIME_STATE_BACKEND;
    delete process.env.RUNTIME_STATE_FILE;
    delete process.env.WALLET_BACKUP_RECIPIENTS;
  });

  it('persists wallet public addresses across restart and keeps receive-context READY', async () => {
    await request(app.getHttpServer())
      .post('/api/client/v1/wallet/public-addresses')
      .set('authorization', `Bearer ${accessToken}`)
      .send({
        networkCode: 'TRON',
        assetCode: 'USDT',
        address: 'TLa2f6VPqDgRE67v1736s7bJ8Ray5wYjU7',
        isDefault: true,
      })
      .expect(201);

    const beforeRestart = await request(app.getHttpServer())
      .get('/api/client/v1/wallet/receive-context?networkCode=TRON&assetCode=USDT')
      .set('authorization', `Bearer ${accessToken}`)
      .expect(200);

    expect(beforeRestart.body.data.receiveState).toBe('READY');
    expect(beforeRestart.body.data.defaultAddress).toBe(
      'TLa2f6VPqDgRE67v1736s7bJ8Ray5wYjU7',
    );

    await app.close();
    app = await bootstrapApp();

    const afterRestart = await request(app.getHttpServer())
      .get('/api/client/v1/wallet/receive-context?networkCode=TRON&assetCode=USDT')
      .set('authorization', `Bearer ${accessToken}`)
      .expect(200);

    expect(afterRestart.body.data.receiveState).toBe('READY');
    expect(afterRestart.body.data.defaultAddress).toBe(
      'TLa2f6VPqDgRE67v1736s7bJ8Ray5wYjU7',
    );
    expect(afterRestart.body.data.addresses).toEqual([
      expect.objectContaining({
        networkCode: 'TRON',
        assetCode: 'USDT',
        address: 'TLa2f6VPqDgRE67v1736s7bJ8Ray5wYjU7',
        isDefault: true,
      }),
    ]);

    const lifecycle = await request(app.getHttpServer())
      .get('/api/client/v1/wallet/lifecycle')
      .set('authorization', `Bearer ${accessToken}`)
      .expect(200);

    expect(lifecycle.body.data.walletExists).toBe(true);
    expect(lifecycle.body.data.receiveState).toBe('READY');
    expect(lifecycle.body.data.configuredAddressCount).toBe(1);
  });

  it('wallet balances return no-address state when account has no configured addresses', async () => {
    const balances = await request(app.getHttpServer())
      .get('/api/client/v1/wallet/balances')
      .set('authorization', `Bearer ${accessToken}`)
      .expect(200);

    expect(balances.body.data.accountEmail).toBe(email);
    expect(balances.body.data.items).toEqual(
      expect.arrayContaining([
        expect.objectContaining({
          networkCode: 'SOLANA',
          assetCode: 'SOL',
          availableBalanceStatus: 'NO_ADDRESS',
        }),
        expect.objectContaining({
          networkCode: 'TRON',
          assetCode: 'USDT',
          availableBalanceStatus: 'NO_ADDRESS',
        }),
      ]),
    );
  });

  it('stores encrypted wallet secret backup metadata without returning plaintext mnemonic', async () => {
    const lifecycle = await request(app.getHttpServer())
      .post('/api/client/v1/wallet/lifecycle')
      .set('authorization', `Bearer ${accessToken}`)
      .send({
        action: 'IMPORT',
        displayName: 'Primary Wallet',
        mnemonicHash: 'c557eec878dfd852ba3f88087c4f350f09c55537ab5e549c3cd14320ec3cef38',
        mnemonicWordCount: 12,
      })
      .expect(201);

    const backupResponse = await request(app.getHttpServer())
      .post('/api/client/v1/wallet/secret-backups')
      .set('authorization', `Bearer ${accessToken}`)
      .send({
        walletId: lifecycle.body.data.walletId,
        secretType: 'MNEMONIC',
        mnemonic:
          'abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon about',
        mnemonicHash:
          'c557eec878dfd852ba3f88087c4f350f09c55537ab5e549c3cd14320ec3cef38',
        mnemonicWordCount: 12,
      })
      .expect(201);

    expect(backupResponse.body.data.encryptionScheme).toBe('AGE');
    expect(backupResponse.body.data.backupId).toBeTruthy();
    expect(JSON.stringify(backupResponse.body.data)).not.toContain('abandon abandon');

    const metadata = await request(app.getHttpServer())
      .get('/api/client/v1/wallet/secret-backups')
      .set('authorization', `Bearer ${accessToken}`)
      .expect(200);

    expect(metadata.body.data.exists).toBe(true);
    expect(metadata.body.data.backupId).toBe(backupResponse.body.data.backupId);
    expect(metadata.body.data.encryptionScheme).toBe('AGE');
    expect(metadata.body.data).not.toHaveProperty('ciphertext');

    const exportResponse = await request(app.getHttpServer())
      .get('/api/client/v1/wallet/secret-backups/export')
      .set('authorization', `Bearer ${accessToken}`)
      .expect(200);

    expect(exportResponse.body.data.exists).toBe(true);
    expect(exportResponse.body.data.fileName).toContain(lifecycle.body.data.walletId);
    expect(exportResponse.body.data.payload.encryptionScheme).toBe('AGE');
    expect(exportResponse.body.data.payload.ciphertext).toBeTruthy();
    expect(JSON.stringify(exportResponse.body.data)).not.toContain('abandon abandon');
  });
});

async function bootstrapApp() {
  const moduleFixture: TestingModule = await Test.createTestingModule({
    imports: [AppModule],
  }).compile();

  const nextApp = moduleFixture.createNestApplication();
  nextApp.setGlobalPrefix('api');
  nextApp.useGlobalPipes(
    new ValidationPipe({
      whitelist: true,
      transform: true,
      forbidUnknownValues: false,
    }),
  );
  nextApp.useGlobalInterceptors(new ResponseEnvelopeInterceptor());
  nextApp.useGlobalFilters(new AllExceptionsFilter());
  await nextApp.init();
  return nextApp;
}

function cleanupRuntimeTables() {
  const tables = [
    'runtime_state_wallet_public_addresses',
    'runtime_state_wallet_secret_backups',
    'runtime_state_wallet_lifecycles',
    'runtime_state_subscriptions',
    'runtime_state_orders',
    'client_sessions',
    'verification_codes',
    'accounts',
  ];

  for (const table of tables) {
    try {
      runtimeDb.public.none(`DELETE FROM ${table}`);
    } catch {}
  }
}
