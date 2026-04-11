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

describe('VPN Postgres runtime state (e2e)', () => {
  let app: INestApplication;
  let accessToken: string;
  let orderNo: string;

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
      .send({ email: 'vpn-pg@example.com' })
      .expect(200);

    const registerResponse = await request(app.getHttpServer())
      .post('/api/client/v1/auth/register/email')
      .set('x-idempotency-key', 'vpn-pg-register')
      .send({
        email: 'vpn-pg@example.com',
        code: '123456',
        password: 'Passw0rd!',
      })
      .expect(200);

    accessToken = registerResponse.body.data.accessToken;

    const createOrder = await request(app.getHttpServer())
      .post('/api/client/v1/orders')
      .set('authorization', `Bearer ${accessToken}`)
      .set('x-idempotency-key', 'vpn-pg-order-1')
      .send({
        planCode: 'BASIC_1M',
        orderType: 'NEW',
        quoteAssetCode: 'USDT',
        quoteNetworkCode: 'SOLANA',
      })
      .expect(201);

    orderNo = createOrder.body.data.orderNo;

    await request(app.getHttpServer())
      .post(`/api/client/v1/orders/${orderNo}/submit-client-tx`)
      .set('authorization', `Bearer ${accessToken}`)
      .send({
        txHash: 'sol-pg-tx-1',
        networkCode: 'SOLANA',
      })
      .expect(201);

    await request(app.getHttpServer())
      .post(`/api/client/v1/orders/${orderNo}/refresh-status`)
      .set('authorization', `Bearer ${accessToken}`)
      .send({})
      .expect(201);
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

  it('keeps fulfillment, subscription reads, and vpn access restart-safe in Postgres', async () => {
    const plans = await request(app.getHttpServer())
      .get('/api/client/v1/plans')
      .set('authorization', `Bearer ${accessToken}`)
      .expect(200);

    expect(plans.body.data.items).toEqual([
      expect.objectContaining({
        planCode: 'BASIC_1M',
        name: '数据库基础版-1个月',
        priceUsd: '11.50',
      }),
      expect.objectContaining({
        planCode: 'PRO_12M_DB',
        name: '数据库专业版-12个月',
      }),
    ]);

    const order = await request(app.getHttpServer())
      .get(`/api/client/v1/orders/${orderNo}`)
      .set('authorization', `Bearer ${accessToken}`)
      .expect(200);

    expect(order.body.data.status).toBe('COMPLETED');

    const subscription = await request(app.getHttpServer())
      .get('/api/client/v1/subscriptions/current')
      .set('authorization', `Bearer ${accessToken}`)
      .expect(200);

    expect(subscription.body.data.status).toBe('ACTIVE');
    expect(subscription.body.data.maxActiveSessions).toBe(2);

    await app.close();
    app = await bootstrapApp();

    const subscriptionAfterRestart = await request(app.getHttpServer())
      .get('/api/client/v1/subscriptions/current')
      .set('authorization', `Bearer ${accessToken}`)
      .expect(200);

    expect(subscriptionAfterRestart.body.data.status).toBe('ACTIVE');

    const regions = await request(app.getHttpServer())
      .get('/api/client/v1/vpn/regions')
      .set('authorization', `Bearer ${accessToken}`)
      .expect(200);

    expect(regions.body.data.items).toEqual([
      expect.objectContaining({
        regionCode: 'JP_DB_BASIC',
        displayName: '日本-数据库基础线路',
        isAllowed: true,
      }),
      expect.objectContaining({
        regionCode: 'US_DB_ADV',
        displayName: '美国-数据库高速线路',
        isAllowed: false,
      }),
    ]);

    const issue = await request(app.getHttpServer())
      .post('/api/client/v1/vpn/config/issue')
      .set('authorization', `Bearer ${accessToken}`)
      .send({
        regionCode: 'JP_DB_BASIC',
        connectionMode: 'global',
      })
      .expect(201);

    expect(issue.body.data.regionCode).toBe('JP_DB_BASIC');
    expect(issue.body.data.configPayload).toContain('jp-db-01.example.com');
    expect(issue.body.data.configPayload).toContain('security=reality');
    expect(issue.body.data.configPayload).toContain('pbk=public-key-jp-db');
    expect(issue.body.data.configPayload).toContain('sid=jpdb01');

    const forbiddenIssue = await request(app.getHttpServer())
      .post('/api/client/v1/vpn/config/issue')
      .set('authorization', `Bearer ${accessToken}`)
      .send({
        regionCode: 'US_DB_ADV',
        connectionMode: 'global',
      })
      .expect(403);

    expect(forbiddenIssue.body.code).toBe('VPN_REGION_FORBIDDEN');

    const status = await request(app.getHttpServer())
      .get('/api/client/v1/vpn/status')
      .set('authorization', `Bearer ${accessToken}`)
      .expect(200);

    expect(status.body.data.canIssueConfig).toBe(true);
    expect(status.body.data.currentRegionCode).toBe('JP_DB_BASIC');
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
