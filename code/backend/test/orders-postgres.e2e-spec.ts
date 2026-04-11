import { INestApplication, ValidationPipe } from '@nestjs/common';
import { Test, TestingModule } from '@nestjs/testing';
import { newDb } from 'pg-mem';
import * as request from 'supertest';

const runtimeDb = newDb({ noAstCoverageCheck: true });

jest.mock('pg', () => runtimeDb.adapters.createPg());

import { AppModule } from '../src/app.module';
import { AllExceptionsFilter } from '../src/common/filters/all-exceptions.filter';
import { ResponseEnvelopeInterceptor } from '../src/common/interceptors/response-envelope.interceptor';
import {
  cleanupClientCatalogTables,
  ensureClientCatalogSchema,
  seedClientCatalogData,
} from './support/client-catalog.fixture';

describe('Orders Postgres runtime state (e2e)', () => {
  let app: INestApplication;
  let accessToken: string;

  beforeEach(async () => {
    process.env.NODE_ENV = 'production';
    process.env.DATABASE_URL =
      'postgres://runtime:runtime@server2:5432/cryptovpn';
    delete process.env.RUNTIME_STATE_BACKEND;
    delete process.env.RUNTIME_STATE_FILE;

    ensureClientCatalogSchema(runtimeDb);
    seedClientCatalogData(runtimeDb);
    app = await bootstrapApp();

    await request(app.getHttpServer())
      .post('/api/client/v1/auth/register/email/request-code')
      .send({ email: 'orders-pg@example.com' })
      .expect(200);

    const registerResponse = await request(app.getHttpServer())
      .post('/api/client/v1/auth/register/email')
      .set('x-idempotency-key', 'orders-pg-register')
      .send({
        email: 'orders-pg@example.com',
        code: '123456',
        password: 'Passw0rd!',
      })
      .expect(200);

    accessToken = registerResponse.body.data.accessToken;
  });

  afterEach(async () => {
    await app.close();
    cleanupRuntimeTables();
    cleanupClientCatalogTables(runtimeDb);
    delete process.env.NODE_ENV;
    delete process.env.DATABASE_URL;
    delete process.env.RUNTIME_STATE_BACKEND;
    delete process.env.RUNTIME_STATE_FILE;
  });

  it('keeps order retrieval, payment target, idempotency, and non-SOLANA status restart-safe in Postgres', async () => {
    const createResponse = await request(app.getHttpServer())
      .post('/api/client/v1/orders')
      .set('authorization', `Bearer ${accessToken}`)
      .set('x-idempotency-key', 'orders-pg-1')
      .send({
        planCode: 'BASIC_1M',
        orderType: 'NEW',
        quoteAssetCode: 'USDT',
        quoteNetworkCode: 'TRON',
      })
      .expect(201);

    expect(createResponse.body.data.planName).toBe('数据库基础版-1个月');
    expect(createResponse.body.data.quoteUsdAmount).toBe('11.50');

    const orderNo = createResponse.body.data.orderNo;

    const paymentTarget = await request(app.getHttpServer())
      .get(`/api/client/v1/orders/${orderNo}/payment-target`)
      .set('authorization', `Bearer ${accessToken}`)
      .expect(200);

    await request(app.getHttpServer())
      .post(`/api/client/v1/orders/${orderNo}/submit-client-tx`)
      .set('authorization', `Bearer ${accessToken}`)
      .send({
        txHash: 'tron-pg-tx-123',
        networkCode: 'TRON',
      })
      .expect(201);

    const refreshedBeforeRestart = await request(app.getHttpServer())
      .post(`/api/client/v1/orders/${orderNo}/refresh-status`)
      .set('authorization', `Bearer ${accessToken}`)
      .send({})
      .expect(201);

    expect(refreshedBeforeRestart.body.data.status).toBe('PAYMENT_DETECTED');
    expect(refreshedBeforeRestart.body.data.submittedClientTxHash).toBe(
      'tron-pg-tx-123',
    );

    await app.close();
    app = await bootstrapApp();

    const getAfterRestart = await request(app.getHttpServer())
      .get(`/api/client/v1/orders/${orderNo}`)
      .set('authorization', `Bearer ${accessToken}`)
      .expect(200);

    expect(getAfterRestart.body.data.orderNo).toBe(orderNo);
    expect(getAfterRestart.body.data.status).toBe('PAYMENT_DETECTED');
    expect(getAfterRestart.body.data.submittedClientTxHash).toBe(
      'tron-pg-tx-123',
    );

    const targetAfterRestart = await request(app.getHttpServer())
      .get(`/api/client/v1/orders/${orderNo}/payment-target`)
      .set('authorization', `Bearer ${accessToken}`)
      .expect(200);

    expect(targetAfterRestart.body.data).toEqual(paymentTarget.body.data);

    const recreated = await request(app.getHttpServer())
      .post('/api/client/v1/orders')
      .set('authorization', `Bearer ${accessToken}`)
      .set('x-idempotency-key', 'orders-pg-1')
      .send({
        planCode: 'BASIC_1M',
        orderType: 'NEW',
        quoteAssetCode: 'USDT',
        quoteNetworkCode: 'TRON',
      })
      .expect(201);

    expect(recreated.body.data.orderNo).toBe(orderNo);
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
  try {
    runtimeDb.public.none('DELETE FROM runtime_state_subscriptions');
  } catch {}

  try {
    runtimeDb.public.none('DELETE FROM runtime_state_orders');
  } catch {}
}
