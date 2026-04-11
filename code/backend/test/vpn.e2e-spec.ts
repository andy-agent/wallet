import { INestApplication, ValidationPipe } from '@nestjs/common';
import { Test, TestingModule } from '@nestjs/testing';
import { mkdtempSync, rmSync } from 'fs';
import { tmpdir } from 'os';
import { join } from 'path';
import * as request from 'supertest';
import { AppModule } from '../src/app.module';
import { AllExceptionsFilter } from '../src/common/filters/all-exceptions.filter';
import { ResponseEnvelopeInterceptor } from '../src/common/interceptors/response-envelope.interceptor';

describe('PlansSubscriptionVpn (e2e)', () => {
  let app: INestApplication;
  let accessToken: string;
  let orderNo: string;
  let runtimeDir: string;
  const solanaCollectionAddress =
    '7YttLkHDo1B4ezgm6KPDLJrVN6a8GN28AL5soMgqd7qV';

  beforeEach(async () => {
    runtimeDir = mkdtempSync(join(tmpdir(), 'backend-vpn-'));
    process.env.NODE_ENV = 'test';
    process.env.RUNTIME_STATE_FILE = join(runtimeDir, 'runtime-state.json');
    process.env.SOLANA_ORDER_COLLECTION_ADDRESS = solanaCollectionAddress;
    delete process.env.RUNTIME_STATE_BACKEND;
    app = await bootstrapApp();

    await request(app.getHttpServer())
      .post('/api/client/v1/auth/register/email/request-code')
      .send({ email: 'vpn@example.com' })
      .expect(200);

    const registerResponse = await request(app.getHttpServer())
      .post('/api/client/v1/auth/register/email')
      .set('x-idempotency-key', 'vpn-register-1')
      .send({
        email: 'vpn@example.com',
        code: '123456',
        password: 'Passw0rd!',
      })
      .expect(200);

    accessToken = registerResponse.body.data.accessToken;

    const createOrder = await request(app.getHttpServer())
      .post('/api/client/v1/orders')
      .set('authorization', `Bearer ${accessToken}`)
      .set('x-idempotency-key', 'vpn-order-1')
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
        txHash: 'tx-vpn-1',
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
    delete process.env.NODE_ENV;
    delete process.env.RUNTIME_STATE_FILE;
    delete process.env.RUNTIME_STATE_BACKEND;
    delete process.env.SOLANA_ORDER_COLLECTION_ADDRESS;
    rmSync(runtimeDir, { recursive: true, force: true });
  });

  it('reads subscription and vpn access from persisted fulfillment state after restart', async () => {
    const plans = await request(app.getHttpServer())
      .get('/api/client/v1/plans')
      .set('authorization', `Bearer ${accessToken}`)
      .expect(200);

    expect(plans.body.data.items).toHaveLength(1);
    expect(plans.body.data.items[0]).toEqual(
      expect.objectContaining({
        planCode: 'BASIC_1M',
        name: '基础版-1个月',
        priceUsd: '9.99',
      }),
    );

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

    expect(regions.body.data.items[0].regionCode).toBe('JP_BASIC');

    const issue = await request(app.getHttpServer())
      .post('/api/client/v1/vpn/config/issue')
      .set('authorization', `Bearer ${accessToken}`)
      .send({
        regionCode: 'JP_BASIC',
        connectionMode: 'global',
      })
      .expect(201);

    expect(issue.body.data.regionCode).toBe('JP_BASIC');
    expect(issue.body.data.configPayload).toContain('bootstrap.jp.example.com');
    expect(issue.body.data.configPayload).toContain('security=reality');
    expect(issue.body.data.configPayload).toContain('pbk=bootstrap-jp-public-key');
    expect(issue.body.data.configPayload).toContain('sid=jpbasic');

    const status = await request(app.getHttpServer())
      .get('/api/client/v1/vpn/status')
      .set('authorization', `Bearer ${accessToken}`)
      .expect(200);

    expect(status.body.data.canIssueConfig).toBe(true);
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
