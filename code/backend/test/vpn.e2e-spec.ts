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
    process.env.MARZBAN_MOCK_MODE = 'true';
    process.env.MARZBAN_BASE_URL = 'https://vpn.residential-agent.com';
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
    delete process.env.MARZBAN_MOCK_MODE;
    delete process.env.MARZBAN_BASE_URL;
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
    expect(subscription.body.data.subscriptionUrl).toMatch(
      /^https:\/\/vpn\.residential-agent\.com\/sub\//,
    );
    expect(subscription.body.data.marzbanUsername).toMatch(/^cvpn_/);

    await app.close();
    app = await bootstrapApp();

    const subscriptionAfterRestart = await request(app.getHttpServer())
      .get('/api/client/v1/subscriptions/current')
      .set('authorization', `Bearer ${accessToken}`)
      .expect(200);

    expect(subscriptionAfterRestart.body.data.status).toBe('ACTIVE');
    expect(subscriptionAfterRestart.body.data.subscriptionUrl).toMatch(
      /^https:\/\/vpn\.residential-agent\.com\/sub\//,
    );
    expect(subscriptionAfterRestart.body.data.marzbanUsername).toMatch(/^cvpn_/);

    const regions = await request(app.getHttpServer())
      .get('/api/client/v1/vpn/regions')
      .set('authorization', `Bearer ${accessToken}`)
      .expect(200);

    expect(regions.body.data.items[0].regionCode).toBe('JP_BASIC');

    const nodes = await request(app.getHttpServer())
      .get('/api/client/v1/vpn/nodes?lineCode=JP_BASIC')
      .set('authorization', `Bearer ${accessToken}`)
      .expect(200);

    expect(nodes.body.data.items).toEqual([
      expect.objectContaining({
        lineCode: 'JP_BASIC',
        lineName: '日本-基础线路',
        regionCode: 'JP',
        regionName: '日本',
        nodeName: 'NODE_JP_01',
        host: 'bootstrap.jp.example.com',
      }),
    ]);

    const selection = await request(app.getHttpServer())
      .post('/api/client/v1/vpn/selection')
      .set('authorization', `Bearer ${accessToken}`)
      .send({
        lineCode: 'JP_BASIC',
        nodeId: nodes.body.data.items[0].nodeId,
      })
      .expect(201);

    expect(selection.body.data.selectedLineCode).toBe('JP_BASIC');
    expect(selection.body.data.selectedLineName).toBe('日本-基础线路');
    expect(selection.body.data.selectedNodeId).toBe(nodes.body.data.items[0].nodeId);
    expect(selection.body.data.selectedNodeName).toBe('NODE_JP_01');

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
    expect(status.body.data.selectedLineCode).toBe('JP_BASIC');
    expect(status.body.data.selectedLineName).toBe('日本-基础线路');
    expect(status.body.data.selectedNodeName).toBe('NODE_JP_01');
  });

  it('returns active subscriptions even when legacy marzban fields are missing', async () => {
    const runtimePath = join(runtimeDir, 'runtime-state.json')
    const snapshot = JSON.parse(require('fs').readFileSync(runtimePath, 'utf8'))
    snapshot.subscriptions = snapshot.subscriptions.map((item: Record<string, unknown>) => ({
      ...item,
      marzbanUsername: null,
      subscriptionUrl: null,
    }))
    require('fs').writeFileSync(runtimePath, JSON.stringify(snapshot, null, 2))

    await app.close();
    delete process.env.MARZBAN_MOCK_MODE;
    delete process.env.MARZBAN_BASE_URL;
    app = await bootstrapApp();

    const subscription = await request(app.getHttpServer())
      .get('/api/client/v1/subscriptions/current')
      .set('authorization', `Bearer ${accessToken}`)
      .expect(200)

    expect(subscription.body.data.status).toBe('ACTIVE')
    expect(subscription.body.data.subscriptionUrl).toBeNull()
    expect(subscription.body.data.marzbanUsername).toBeNull()
  })

  it('normalizes a persisted relative subscription URL for active subscriptions', async () => {
    const runtimePath = join(runtimeDir, 'runtime-state.json')
    const snapshot = JSON.parse(require('fs').readFileSync(runtimePath, 'utf8'))
    snapshot.subscriptions = snapshot.subscriptions.map((item: Record<string, unknown>) => ({
      ...item,
      subscriptionUrl: '/sub/legacy-relative-token',
    }))
    require('fs').writeFileSync(runtimePath, JSON.stringify(snapshot, null, 2))

    const subscription = await request(app.getHttpServer())
      .get('/api/client/v1/subscriptions/current')
      .set('authorization', `Bearer ${accessToken}`)
      .expect(200)

    expect(subscription.body.data.subscriptionUrl).toBe(
      'https://vpn.residential-agent.com/sub/legacy-relative-token',
    )
  })
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
