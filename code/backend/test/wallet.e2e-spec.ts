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

    await request(app.getHttpServer())
      .post('/api/client/v1/auth/register/email/request-code')
      .send({ email: 'wallet@example.com' })
      .expect(200);

    const registerResponse = await request(app.getHttpServer())
      .post('/api/client/v1/auth/register/email')
      .set('x-idempotency-key', 'wallet-register-1')
      .send({
        email: 'wallet@example.com',
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

  it('wallet metadata and fallback flow', async () => {
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

    await request(app.getHttpServer())
      .post('/api/client/v1/auth/register/email/request-code')
      .send({ email: 'wallet-tron@example.com' })
      .expect(200);

    const registerResponse = await request(app.getHttpServer())
      .post('/api/client/v1/auth/register/email')
      .set('x-idempotency-key', 'wallet-tron-register-1')
      .send({
        email: 'wallet-tron@example.com',
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

    await request(app.getHttpServer())
      .post('/api/client/v1/auth/register/email/request-code')
      .send({ email: 'wallet-tron-fallback@example.com' })
      .expect(200);

    const registerResponse = await request(app.getHttpServer())
      .post('/api/client/v1/auth/register/email')
      .set('x-idempotency-key', 'wallet-tron-register-2')
      .send({
        email: 'wallet-tron-fallback@example.com',
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
