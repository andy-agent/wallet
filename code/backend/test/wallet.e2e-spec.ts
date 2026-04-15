import { INestApplication, ValidationPipe } from '@nestjs/common';
import { Test, TestingModule } from '@nestjs/testing';
import * as request from 'supertest';
import { AppModule } from '../src/app.module';
import { AllExceptionsFilter } from '../src/common/filters/all-exceptions.filter';
import { ResponseEnvelopeInterceptor } from '../src/common/interceptors/response-envelope.interceptor';
import { TronClientService } from '../src/modules/tron-client/tron-client.service';

describe('Wallet (e2e)', () => {
  let app: INestApplication;
  let accessToken: string;
  let email: string;
  const originalTronServiceEnabled = process.env.TRON_SERVICE_ENABLED;

  async function bootstrapApp(
    tronClientOverride?: Partial<
      Pick<
        TronClientService,
        'isEnabled' | 'validateAddress' | 'broadcastTransaction'
      >
    >,
  ) {
    const moduleBuilder = Test.createTestingModule({
      imports: [AppModule],
    });

    if (tronClientOverride) {
      moduleBuilder.overrideProvider(TronClientService).useValue(tronClientOverride);
    }

    const moduleFixture: TestingModule = await moduleBuilder.compile();

    app = moduleFixture.createNestApplication();
    app.setGlobalPrefix('api');
    app.useGlobalPipes(
      new ValidationPipe({
        whitelist: true,
        transform: true,
        forbidUnknownValues: false,
      }),
    );
    app.useGlobalInterceptors(new ResponseEnvelopeInterceptor());
    app.useGlobalFilters(new AllExceptionsFilter());
    await app.init();
  }

  beforeEach(async () => {
    delete process.env.TRON_SERVICE_ENABLED;
    await bootstrapApp();
    email = `wallet-${Date.now()}-${Math.random().toString(16).slice(2, 8)}@example.com`;

    await request(app.getHttpServer())
      .post('/api/client/v1/auth/register/email/request-code')
      .send({ email })
      .expect(200);

    const registerResponse = await request(app.getHttpServer())
      .post('/api/client/v1/auth/register/email')
      .set('x-idempotency-key', `wallet-register-${Date.now()}`)
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

    if (originalTronServiceEnabled === undefined) {
      delete process.env.TRON_SERVICE_ENABLED;
      return;
    }

    process.env.TRON_SERVICE_ENABLED = originalTronServiceEnabled;
  });

  it('wallet lifecycle exposes no-wallet vs created-wallet receive gating state', async () => {
    await request(app.getHttpServer())
      .get('/api/client/v1/wallet/lifecycle')
      .set('authorization', `Bearer ${accessToken}`)
      .expect(200)
      .expect((res) => {
        expect(res.body.data.walletExists).toBe(false);
        expect(res.body.data.lifecycleStatus).toBe('NOT_CREATED');
        expect(res.body.data.receiveState).toBe('NO_WALLET');
        expect(res.body.data.configuredAddressCount).toBe(0);
        expect(res.body.data.nextAction).toBe('CREATE_OR_IMPORT');
      });

    await request(app.getHttpServer())
      .post('/api/client/v1/wallet/lifecycle')
      .set('authorization', `Bearer ${accessToken}`)
      .send({
        action: 'CREATE',
        displayName: 'Primary Wallet',
      })
      .expect(201)
      .expect((res) => {
        expect(res.body.data.walletExists).toBe(true);
        expect(res.body.data.lifecycleStatus).toBe('CREATED');
        expect(res.body.data.status).toBe('CREATED_PENDING_BACKUP');
        expect(res.body.data.sourceType).toBe('CREATE');
        expect(res.body.data.receiveState).toBe('NO_ADDRESS');
        expect(res.body.data.displayName).toBe('Primary Wallet');
        expect(res.body.data.nextAction).toBe('BACKUP_MNEMONIC');
      });

    await request(app.getHttpServer())
      .post('/api/client/v1/wallet/lifecycle')
      .set('authorization', `Bearer ${accessToken}`)
      .send({
        action: 'ACKNOWLEDGE_BACKUP',
      })
      .expect(201)
      .expect((res) => {
        expect(res.body.data.lifecycleStatus).toBe('CREATED');
        expect(res.body.data.status).toBe('BACKUP_PENDING_CONFIRMATION');
        expect(res.body.data.nextAction).toBe('CONFIRM_MNEMONIC');
      });

    await request(app.getHttpServer())
      .post('/api/client/v1/wallet/lifecycle')
      .set('authorization', `Bearer ${accessToken}`)
      .send({
        action: 'CONFIRM_BACKUP',
      })
      .expect(201)
      .expect((res) => {
        expect(res.body.data.lifecycleStatus).toBe('ACTIVE');
        expect(res.body.data.nextAction).toBe('READY');
      });

    await request(app.getHttpServer())
      .post('/api/client/v1/wallet/public-addresses')
      .set('authorization', `Bearer ${accessToken}`)
      .send({
        networkCode: 'SOLANA',
        assetCode: 'USDT',
        address: 'So11111111111111111111111111111111111111112',
        isDefault: true,
      })
      .expect(201);

    await request(app.getHttpServer())
      .get('/api/client/v1/wallet/lifecycle')
      .set('authorization', `Bearer ${accessToken}`)
      .expect(200)
      .expect((res) => {
        expect(res.body.data.walletExists).toBe(true);
        expect(res.body.data.receiveState).toBe('READY');
        expect(res.body.data.configuredAddressCount).toBe(1);
      });
  });

  it('wallet metadata and fallback flow', async () => {
    await request(app.getHttpServer())
      .get('/api/client/v1/wallet/lifecycle')
      .set('authorization', `Bearer ${accessToken}`)
      .expect(200)
      .expect((res) => {
        expect(res.body.data.walletExists).toBe(false);
        expect(res.body.data.nextAction).toBe('CREATE_OR_IMPORT');
      });

    await request(app.getHttpServer())
      .post('/api/client/v1/wallet/lifecycle')
      .set('authorization', `Bearer ${accessToken}`)
      .send({
        action: 'CREATE',
        displayName: 'Primary Wallet',
      })
      .expect(201)
      .expect((res) => {
        expect(res.body.data.walletExists).toBe(true);
        expect(res.body.data.status).toBe('CREATED_PENDING_BACKUP');
      });

    await request(app.getHttpServer())
      .get('/api/client/v1/wallet/chains')
      .set('authorization', `Bearer ${accessToken}`)
      .expect(200);

    await request(app.getHttpServer())
      .get('/api/client/v1/wallet/assets/catalog')
      .set('authorization', `Bearer ${accessToken}`)
      .expect(200);

    await request(app.getHttpServer())
      .post('/api/client/v1/wallet/public-addresses')
      .set('authorization', `Bearer ${accessToken}`)
      .send({
        networkCode: 'SOLANA',
        assetCode: 'USDT',
        address: 'So11111111111111111111111111111111111111112',
        isDefault: true,
      })
      .expect(201);

    await request(app.getHttpServer())
      .get('/api/client/v1/wallet/public-addresses')
      .set('authorization', `Bearer ${accessToken}`)
      .expect(200);

    await request(app.getHttpServer())
      .get('/api/client/v1/wallet/overview')
      .set('authorization', `Bearer ${accessToken}`)
      .expect(200)
      .expect((res) => {
        expect(res.body.data.chainItems).toEqual(
          expect.arrayContaining([
            expect.objectContaining({ networkCode: 'SOLANA' }),
            expect.objectContaining({ networkCode: 'TRON' }),
          ]),
        );
        expect(res.body.data.assetItems).toEqual(
          expect.arrayContaining([
            expect.objectContaining({ networkCode: 'SOLANA', assetCode: 'SOL' }),
            expect.objectContaining({ networkCode: 'TRON', assetCode: 'USDT' }),
          ]),
        );
      });

    await request(app.getHttpServer())
      .get('/api/client/v1/wallet/receive-context?networkCode=SOLANA&assetCode=USDT')
      .set('authorization', `Bearer ${accessToken}`)
      .expect(200)
      .expect((res) => {
        expect(res.body.data.selectedNetworkCode).toBe('SOLANA');
        expect(res.body.data.selectedAssetCode).toBe('USDT');
        expect(res.body.data.walletExists).toBe(true);
        expect(res.body.data.receiveState).toBe('READY');
        expect(res.body.data.status).toBe('已配置收款地址');
        expect(res.body.data.defaultAddress).toBe(
          'So11111111111111111111111111111111111111112',
        );
      });

    await request(app.getHttpServer())
      .get('/api/client/v1/referral/share-context')
      .set('authorization', `Bearer ${accessToken}`)
      .expect(200)
      .expect((res) => {
        expect(res.body.data.referralCode).toBeDefined();
        expect(res.body.data.shareLink).toContain(res.body.data.referralCode);
      });

    await request(app.getHttpServer())
      .post('/api/client/v1/wallet/transfer/precheck')
      .set('authorization', `Bearer ${accessToken}`)
      .send({
        networkCode: 'SOLANA',
        assetCode: 'USDT',
        toAddress: 'So11111111111111111111111111111111111111112',
        amount: '10.5',
      })
      .expect(201);

    await request(app.getHttpServer())
      .post('/api/client/v1/wallet/transfer/proxy-broadcast')
      .set('authorization', `Bearer ${accessToken}`)
      .send({
        networkCode: 'SOLANA',
        assetCode: 'USDT',
        signedPayload: 'signed-payload',
        clientTxHash: 'tx-wallet-1',
      })
      .expect(201);
  });

  it('wallet TRON flow uses the remote client when enabled', async () => {
    await app.close();
    process.env.TRON_SERVICE_ENABLED = 'true';

    await bootstrapApp({
      isEnabled: () => true,
      validateAddress: jest.fn().mockResolvedValue({
        address: 'TLa2f6VPqDgRE67v1736s7bJ8Ray5wYjU7',
        valid: true,
        type: 'tron',
      }),
      broadcastTransaction: jest.fn().mockResolvedValue({
        success: true,
        txHash: 'tron-remote-1',
        acceptedAt: '2026-04-07T00:00:00.000Z',
      }),
    });

    const tronEmail = `wallet-tron-${Date.now()}-${Math.random().toString(16).slice(2, 8)}@example.com`;
    await request(app.getHttpServer())
      .post('/api/client/v1/auth/register/email/request-code')
      .send({ email: tronEmail })
      .expect(200);

    const registerResponse = await request(app.getHttpServer())
      .post('/api/client/v1/auth/register/email')
      .set('x-idempotency-key', `wallet-tron-register-${Date.now()}`)
      .send({
        email: tronEmail,
        code: '123456',
        password: 'Passw0rd!',
      })
      .expect(200);

    accessToken = registerResponse.body.data.accessToken;

    await request(app.getHttpServer())
      .post('/api/client/v1/wallet/transfer/precheck')
      .set('authorization', `Bearer ${accessToken}`)
      .send({
        networkCode: 'TRON',
        assetCode: 'USDT',
        toAddress: 'TLa2f6VPqDgRE67v1736s7bJ8Ray5wYjU7',
        amount: '10.5',
      })
      .expect(201)
      .expect((res) => {
        expect(res.body.data.networkCode).toBe('TRON');
        expect(res.body.data.serviceEnabled).toBe(true);
      });

    await request(app.getHttpServer())
      .post('/api/client/v1/wallet/transfer/proxy-broadcast')
      .set('authorization', `Bearer ${accessToken}`)
      .send({
        networkCode: 'TRON',
        assetCode: 'USDT',
        signedPayload: 'deadbeef',
        toAddress: 'TLa2f6VPqDgRE67v1736s7bJ8Ray5wYjU7',
      })
      .expect(201)
      .expect((res) => {
        expect(res.body.data.networkCode).toBe('TRON');
        expect(res.body.data.serviceEnabled).toBe(true);
        expect(res.body.data.txHash).toBe('tron-remote-1');
      });
  });

  it('wallet TRON broadcast falls back when the remote client errors', async () => {
    await app.close();
    process.env.TRON_SERVICE_ENABLED = 'true';

    await bootstrapApp({
      isEnabled: () => true,
      validateAddress: jest.fn().mockResolvedValue({
        address: 'TLa2f6VPqDgRE67v1736s7bJ8Ray5wYjU7',
        valid: true,
        type: 'tron',
      }),
      broadcastTransaction: jest
        .fn()
        .mockRejectedValue(new Error('tron remote unavailable')),
    });

    const tronFallbackEmail = `wallet-tron-fallback-${Date.now()}-${Math.random().toString(16).slice(2, 8)}@example.com`;
    await request(app.getHttpServer())
      .post('/api/client/v1/auth/register/email/request-code')
      .send({ email: tronFallbackEmail })
      .expect(200);

    const registerResponse = await request(app.getHttpServer())
      .post('/api/client/v1/auth/register/email')
      .set('x-idempotency-key', `wallet-tron-register-${Date.now()}`)
      .send({
        email: tronFallbackEmail,
        code: '123456',
        password: 'Passw0rd!',
      })
      .expect(200);

    accessToken = registerResponse.body.data.accessToken;

    await request(app.getHttpServer())
      .post('/api/client/v1/wallet/transfer/proxy-broadcast')
      .set('authorization', `Bearer ${accessToken}`)
      .send({
        networkCode: 'TRON',
        assetCode: 'USDT',
        signedPayload: 'deadbeef',
        clientTxHash: 'tron-fallback-client-hash',
        toAddress: 'TLa2f6VPqDgRE67v1736s7bJ8Ray5wYjU7',
      })
      .expect(201)
      .expect((res) => {
        expect(res.body.data.networkCode).toBe('TRON');
        expect(res.body.data.serviceEnabled).toBe(false);
        expect(res.body.data.txHash).toBe('tron-fallback-client-hash');
      });
  });
});
