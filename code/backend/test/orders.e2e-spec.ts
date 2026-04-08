import { INestApplication, ValidationPipe } from '@nestjs/common';
import { Test, TestingModule } from '@nestjs/testing';
import { mkdtempSync, rmSync } from 'fs';
import { tmpdir } from 'os';
import { join } from 'path';
import * as request from 'supertest';
import { AppModule } from '../src/app.module';
import { AllExceptionsFilter } from '../src/common/filters/all-exceptions.filter';
import { ResponseEnvelopeInterceptor } from '../src/common/interceptors/response-envelope.interceptor';

describe('Orders (e2e)', () => {
  let app: INestApplication;
  let accessToken: string;
  let runtimeDir: string;
  let runtimeStateFile: string;

  beforeEach(async () => {
    runtimeDir = mkdtempSync(join(tmpdir(), 'backend-orders-'));
    runtimeStateFile = join(runtimeDir, 'runtime-state.json');
    process.env.RUNTIME_STATE_FILE = runtimeStateFile;

    app = await bootstrapApp();

    await request(app.getHttpServer())
      .post('/api/client/v1/auth/register/email/request-code')
      .send({ email: 'order@example.com' })
      .expect(200);

    const registerResponse = await request(app.getHttpServer())
      .post('/api/client/v1/auth/register/email')
      .set('x-idempotency-key', 'order-register-1')
      .send({
        email: 'order@example.com',
        code: '123456',
        password: 'Passw0rd!',
      })
      .expect(200);

    accessToken = registerResponse.body.data.accessToken;
  });

  afterEach(async () => {
    await app.close();
    delete process.env.RUNTIME_STATE_FILE;
    rmSync(runtimeDir, { recursive: true, force: true });
  });

  it('persists order retrieval, idempotency, and payment target across restart', async () => {
    const createResponse = await request(app.getHttpServer())
      .post('/api/client/v1/orders')
      .set('authorization', `Bearer ${accessToken}`)
      .set('x-idempotency-key', 'order-1')
      .send({
        planCode: 'BASIC_1M',
        orderType: 'NEW',
        quoteAssetCode: 'USDT',
        quoteNetworkCode: 'SOLANA',
      })
      .expect(201);

    const orderNo = createResponse.body.data.orderNo;
    const firstPaymentTarget = await request(app.getHttpServer())
      .get(`/api/client/v1/orders/${orderNo}/payment-target`)
      .set('authorization', `Bearer ${accessToken}`)
      .expect(200);

    const secondOrder = await request(app.getHttpServer())
      .post('/api/client/v1/orders')
      .set('authorization', `Bearer ${accessToken}`)
      .set('x-idempotency-key', 'order-2')
      .send({
        planCode: 'BASIC_1M',
        orderType: 'NEW',
        quoteAssetCode: 'USDT',
        quoteNetworkCode: 'SOLANA',
      })
      .expect(201);

    const secondPaymentTarget = await request(app.getHttpServer())
      .get(`/api/client/v1/orders/${secondOrder.body.data.orderNo}/payment-target`)
      .set('authorization', `Bearer ${accessToken}`)
      .expect(200);

    expect(firstPaymentTarget.body.data.collectionAddress).not.toBe(
      secondPaymentTarget.body.data.collectionAddress,
    );
    expect(firstPaymentTarget.body.data.uniqueAmountDelta).not.toBe(
      secondPaymentTarget.body.data.uniqueAmountDelta,
    );

    await app.close();
    app = await bootstrapApp();

    const getAfterRestart = await request(app.getHttpServer())
      .get(`/api/client/v1/orders/${orderNo}`)
      .set('authorization', `Bearer ${accessToken}`)
      .expect(200);

    expect(getAfterRestart.body.data.orderNo).toBe(orderNo);
    expect(getAfterRestart.body.data.status).toBe('AWAITING_PAYMENT');

    const recreated = await request(app.getHttpServer())
      .post('/api/client/v1/orders')
      .set('authorization', `Bearer ${accessToken}`)
      .set('x-idempotency-key', 'order-1')
      .send({
        planCode: 'BASIC_1M',
        orderType: 'NEW',
        quoteAssetCode: 'USDT',
        quoteNetworkCode: 'SOLANA',
      })
      .expect(201);

    expect(recreated.body.data.orderNo).toBe(orderNo);

    const targetAfterRestart = await request(app.getHttpServer())
      .get(`/api/client/v1/orders/${orderNo}/payment-target`)
      .set('authorization', `Bearer ${accessToken}`)
      .expect(200);

    expect(targetAfterRestart.body.data).toEqual(firstPaymentTarget.body.data);
  });

  it('keeps non-SOLANA tx state restart-safe without synthetic refresh progression', async () => {
    const createResponse = await request(app.getHttpServer())
      .post('/api/client/v1/orders')
      .set('authorization', `Bearer ${accessToken}`)
      .set('x-idempotency-key', 'tron-order-1')
      .send({
        planCode: 'BASIC_1M',
        orderType: 'NEW',
        quoteAssetCode: 'USDT',
        quoteNetworkCode: 'TRON',
      })
      .expect(201);

    const orderNo = createResponse.body.data.orderNo;

    await request(app.getHttpServer())
      .post(`/api/client/v1/orders/${orderNo}/submit-client-tx`)
      .set('authorization', `Bearer ${accessToken}`)
      .send({
        txHash: 'tron-tx-123',
        networkCode: 'TRON',
      })
      .expect(201);

    const refreshedBeforeRestart = await request(app.getHttpServer())
      .post(`/api/client/v1/orders/${orderNo}/refresh-status`)
      .set('authorization', `Bearer ${accessToken}`)
      .send({})
      .expect(201);

    expect(refreshedBeforeRestart.body.data.status).toBe('PAYMENT_DETECTED');
    expect(refreshedBeforeRestart.body.data.submittedClientTxHash).toBe('tron-tx-123');

    await app.close();
    app = await bootstrapApp();

    const orderAfterRestart = await request(app.getHttpServer())
      .get(`/api/client/v1/orders/${orderNo}`)
      .set('authorization', `Bearer ${accessToken}`)
      .expect(200);

    expect(orderAfterRestart.body.data.status).toBe('PAYMENT_DETECTED');
    expect(orderAfterRestart.body.data.submittedClientTxHash).toBe('tron-tx-123');

    const refreshedAfterRestart = await request(app.getHttpServer())
      .post(`/api/client/v1/orders/${orderNo}/refresh-status`)
      .set('authorization', `Bearer ${accessToken}`)
      .send({})
      .expect(201);

    expect(refreshedAfterRestart.body.data.status).toBe('PAYMENT_DETECTED');
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
