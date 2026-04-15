import { INestApplication, ValidationPipe } from '@nestjs/common';
import { Test, TestingModule } from '@nestjs/testing';
import { newDb } from 'pg-mem';
import * as request from 'supertest';

const runtimeDb = newDb({ noAstCoverageCheck: true });

jest.mock('pg', () => runtimeDb.adapters.createPg());

import { AppModule } from '../src/app.module';
import { AllExceptionsFilter } from '../src/common/filters/all-exceptions.filter';
import { ResponseEnvelopeInterceptor } from '../src/common/interceptors/response-envelope.interceptor';
import { ReferralService } from '../src/modules/referral/referral.service';

describe('Admin Postgres real data (e2e)', () => {
  let app: INestApplication;
  let adminToken: string;

  beforeEach(async () => {
    process.env.NODE_ENV = 'production';
    process.env.DATABASE_URL =
      'postgres://runtime:runtime@server2:5432/cryptovpn';
    process.env.MARZBAN_MOCK_MODE = 'true';
    process.env.MARZBAN_BASE_URL = 'https://vpn.residential-agent.com';
    process.env.SOLANA_ORDER_COLLECTION_ADDRESS =
      '7YttLkHDo1B4ezgm6KPDLJrVN6a8GN28AL5soMgqd7qV';
    delete process.env.RUNTIME_STATE_BACKEND;
    delete process.env.RUNTIME_STATE_FILE;

    ensureAdminReadSchema();
    seedAdminReadData();

    app = await bootstrapApp();
    adminToken = await loginAdmin(app);
  });

  afterEach(async () => {
    if (app) {
      await app.close();
    }
    cleanupTables();
    delete process.env.NODE_ENV;
    delete process.env.DATABASE_URL;
    delete process.env.RUNTIME_STATE_BACKEND;
    delete process.env.RUNTIME_STATE_FILE;
    delete process.env.MARZBAN_MOCK_MODE;
    delete process.env.MARZBAN_BASE_URL;
    delete process.env.SOLANA_ORDER_COLLECTION_ADDRESS;
  });

  it('reads seeded PostgreSQL data for admin plans, audit, legal, configs, versions, and vpn with flat pagination', async () => {
    const plansResponse = await request(app.getHttpServer())
      .get('/api/admin/v1/plans')
      .set('authorization', `Bearer ${adminToken}`)
      .expect(200);
    const plansPayload = plansResponse.body.data;
    expectFlatPagination(plansPayload, {
      expectedPage: 1,
      expectedTotal: 2,
      expectedItemsLength: 2,
    });
    expect(plansPayload.items).toEqual(
      expect.arrayContaining([
        expect.objectContaining({
          planCode: 'PRO_12M_DB',
          name: '数据库专业版-12个月',
          status: 'DISABLED',
        }),
      ]),
    );

    const auditResponse = await request(app.getHttpServer())
      .get('/api/admin/v1/audit-logs?page=1&pageSize=10')
      .set('authorization', `Bearer ${adminToken}`)
      .expect(200);
    const auditPayload = auditResponse.body.data;
    expectFlatPagination(auditPayload, {
      expectedPage: 1,
      expectedPageSize: 10,
      expectedTotal: 2,
      expectedItemsLength: 2,
    });
    expect(auditPayload.items[0]).toEqual(
      expect.objectContaining({
        requestId: 'req-db-audit-2',
        module: 'SYSTEM_CONFIGS',
        action: 'UPDATE',
      }),
    );

    const legalResponse = await request(app.getHttpServer())
      .get('/api/admin/v1/legal-documents?page=1&pageSize=10')
      .set('authorization', `Bearer ${adminToken}`)
      .expect(200);
    const legalPayload = legalResponse.body.data;
    expectFlatPagination(legalPayload, {
      expectedPage: 1,
      expectedPageSize: 10,
      expectedTotal: 4,
      expectedItemsLength: 4,
    });
    const legalDocTypeByTitle = new Map<string, string>(
      legalPayload.items.map((item: any) => [item.title, item.docType]),
    );
    expect(legalDocTypeByTitle.get('数据库用户协议')).toBe(
      'TERMS_OF_SERVICE',
    );
    expect(legalDocTypeByTitle.get('数据库隐私政策')).toBe('PRIVACY_POLICY');
    expect(legalDocTypeByTitle.get('数据库退款政策')).toBe('REFUND_POLICY');
    expect(legalDocTypeByTitle.get('数据库风险披露')).toBe('RISK_DISCLOSURE');

    const configsResponse = await request(app.getHttpServer())
      .get('/api/admin/v1/system-configs')
      .set('authorization', `Bearer ${adminToken}`)
      .expect(200);
    const configsPayload = configsResponse.body.data;
    expectFlatPagination(configsPayload, {
      expectedPage: 1,
      expectedTotal: 4,
      expectedItemsLength: 4,
    });
    const configScopeByKey = new Map<string, string>(
      configsPayload.items.map((item: any) => [item.configKey, item.scope]),
    );
    expect(configScopeByKey.get('SITE_NOTICE')).toBe('GLOBAL');
    expect(configScopeByKey.get('CONFIG_ISSUE_MINUTES')).toBe('VPN');
    expect(configScopeByKey.get('ORDER_EXPIRE_SECONDS')).toBe('PAYMENT');
    expect(configScopeByKey.get('WITHDRAW_MIN_AMOUNT')).toBe('REFERRAL');

    const versionsResponse = await request(app.getHttpServer())
      .get('/api/admin/v1/app-versions?page=1&pageSize=10')
      .set('authorization', `Bearer ${adminToken}`)
      .expect(200);
    const versionsPayload = versionsResponse.body.data;
    expectFlatPagination(versionsPayload, {
      expectedPage: 1,
      expectedPageSize: 10,
      expectedTotal: 2,
      expectedItemsLength: 2,
    });
    expect(versionsPayload.items).toEqual(
      expect.arrayContaining([
        expect.objectContaining({
          platform: 'ANDROID',
          channel: 'GOOGLE_PLAY',
          versionName: '9.9.9-db',
        }),
        expect.objectContaining({
          platform: 'IOS',
          channel: 'APP_STORE',
          versionName: '8.8.8-db',
        }),
      ]),
    );

    const regionsResponse = await request(app.getHttpServer())
      .get('/api/admin/v1/vpn/regions?page=1&pageSize=10')
      .set('authorization', `Bearer ${adminToken}`)
      .expect(200);
    const regionsPayload = regionsResponse.body.data;
    expectFlatPagination(regionsPayload, {
      expectedPage: 1,
      expectedPageSize: 10,
      expectedTotal: 2,
      expectedItemsLength: 2,
    });
    expect(regionsPayload.items).toEqual(
      expect.arrayContaining([
        expect.objectContaining({
          regionCode: 'EU_DB_FAST',
          status: 'INACTIVE',
        }),
        expect.objectContaining({
          regionCode: 'SG_DB_MAINT',
          status: 'MAINTENANCE',
        }),
      ]),
    );

    const nodesResponse = await request(app.getHttpServer())
      .get('/api/admin/v1/vpn/nodes?page=1&pageSize=10')
      .set('authorization', `Bearer ${adminToken}`)
      .expect(200);
    const nodesPayload = nodesResponse.body.data;
    expectFlatPagination(nodesPayload, {
      expectedPage: 1,
      expectedPageSize: 10,
      expectedTotal: 2,
      expectedItemsLength: 2,
    });
    expect(nodesPayload.items).toEqual(
      expect.arrayContaining([
        expect.objectContaining({
          nodeCode: 'EU-DB-01',
          status: 'INACTIVE',
          healthStatus: 'UNHEALTHY',
        }),
        expect.objectContaining({
          nodeCode: 'SG-DB-01',
          status: 'MAINTENANCE',
          healthStatus: 'DEGRADED',
        }),
      ]),
    );
  });

  it('allows admin to manage plans and exposes active plans to the client catalog', async () => {
    const createResponse = await request(app.getHttpServer())
      .post('/api/admin/v1/plans')
      .set('authorization', `Bearer ${adminToken}`)
      .send({
        planCode: 'ADMIN_DYNAMIC_3M',
        name: '后台动态套餐-3个月',
        description: '由后台创建并直接供客户端购买',
        billingCycleMonths: 3,
        priceUsd: '29.99',
        isUnlimitedTraffic: true,
        maxActiveSessions: 3,
        regionAccessPolicy: 'CUSTOM',
        includesAdvancedRegions: false,
        allowedRegionIds: ['region-sg-db'],
        displayOrder: 3,
        status: 'ACTIVE',
      })
      .expect(201);

    expect(createResponse.body.data).toEqual(
      expect.objectContaining({
        planCode: 'ADMIN_DYNAMIC_3M',
        name: '后台动态套餐-3个月',
        billingCycleMonths: 3,
        priceUsd: '29.99',
        regionAccessPolicy: 'CUSTOM',
        allowedRegionIds: ['region-sg-db'],
        status: 'ACTIVE',
      }),
    );

    const clientPlansAfterCreate = await request(app.getHttpServer())
      .get('/api/client/v1/plans')
      .expect(200);

    expect(clientPlansAfterCreate.body.data.items).toEqual(
      expect.arrayContaining([
        expect.objectContaining({
          planCode: 'ADMIN_DYNAMIC_3M',
          name: '后台动态套餐-3个月',
          status: 'ACTIVE',
        }),
      ]),
    );

    const planId = createResponse.body.data.planId;
    await request(app.getHttpServer())
      .put(`/api/admin/v1/plans/${planId}`)
      .set('authorization', `Bearer ${adminToken}`)
      .send({
        planCode: 'ADMIN_DYNAMIC_3M',
        name: '后台动态套餐-已停用',
        description: '更新后停止对客户端售卖',
        billingCycleMonths: 6,
        priceUsd: '49.99',
        isUnlimitedTraffic: false,
        maxActiveSessions: 6,
        regionAccessPolicy: 'CUSTOM',
        includesAdvancedRegions: true,
        allowedRegionIds: ['region-eu-db'],
        displayOrder: 9,
        status: 'DISABLED',
      })
      .expect(200)
      .expect((res) => {
        expect(res.body.data).toEqual(
          expect.objectContaining({
            planId,
            name: '后台动态套餐-已停用',
            billingCycleMonths: 6,
            priceUsd: '49.99',
            isUnlimitedTraffic: false,
            maxActiveSessions: 6,
            includesAdvancedRegions: true,
            allowedRegionIds: ['region-eu-db'],
            status: 'DISABLED',
          }),
        );
      });

    const clientPlansAfterDisable = await request(app.getHttpServer())
      .get('/api/client/v1/plans')
      .expect(200);

    expect(
      clientPlansAfterDisable.body.data.items.some(
        (item: { planCode: string }) => item.planCode === 'ADMIN_DYNAMIC_3M',
      ),
    ).toBe(false);
  });

  it('runs the full admin-managed plan flow from plan config to client subscription', async () => {
    const createPlanResponse = await request(app.getHttpServer())
      .post('/api/admin/v1/plans')
      .set('authorization', `Bearer ${adminToken}`)
      .send({
        planCode: 'ADMIN_FLOW_1M',
        name: '后台链路套餐-1个月',
        description: '用于验证后台配置到用户订阅全链路',
        billingCycleMonths: 1,
        priceUsd: '19.99',
        isUnlimitedTraffic: true,
        maxActiveSessions: 4,
        regionAccessPolicy: 'BASIC_ONLY',
        includesAdvancedRegions: false,
        allowedRegionIds: [],
        displayOrder: 4,
        status: 'ACTIVE',
      })
      .expect(201);

    expect(createPlanResponse.body.data).toEqual(
      expect.objectContaining({
        planCode: 'ADMIN_FLOW_1M',
        name: '后台链路套餐-1个月',
        status: 'ACTIVE',
      }),
    );

    const clientPlansResponse = await request(app.getHttpServer())
      .get('/api/client/v1/plans')
      .expect(200);

    expect(clientPlansResponse.body.data.items).toEqual(
      expect.arrayContaining([
        expect.objectContaining({
          planCode: 'ADMIN_FLOW_1M',
          name: '后台链路套餐-1个月',
          priceUsd: '19.99',
        }),
      ]),
    );

    const client = await registerClient(app, 'plan-flow@example.com');

    const createOrderResponse = await request(app.getHttpServer())
      .post('/api/client/v1/orders')
      .set('authorization', `Bearer ${client.accessToken}`)
      .set('x-idempotency-key', 'admin-flow-order')
      .send({
        planCode: 'ADMIN_FLOW_1M',
        orderType: 'NEW',
        quoteAssetCode: 'USDT',
        quoteNetworkCode: 'SOLANA',
      })
      .expect(201);

    const orderNo = createOrderResponse.body.data.orderNo as string;
    expect(createOrderResponse.body.data).toEqual(
      expect.objectContaining({
        planCode: 'ADMIN_FLOW_1M',
        planName: '后台链路套餐-1个月',
        quoteUsdAmount: '19.99',
      }),
    );

    await request(app.getHttpServer())
      .post(`/api/client/v1/orders/${orderNo}/submit-client-tx`)
      .set('authorization', `Bearer ${client.accessToken}`)
      .send({
        txHash: 'admin-flow-solana-tx',
        networkCode: 'SOLANA',
      })
      .expect(201);

    await request(app.getHttpServer())
      .post(`/api/client/v1/orders/${orderNo}/refresh-status`)
      .set('authorization', `Bearer ${client.accessToken}`)
      .send({})
      .expect(201)
      .expect((res) => {
        expect(res.body.data).toEqual(
          expect.objectContaining({
            status: 'COMPLETED',
            planCode: 'ADMIN_FLOW_1M',
            planName: '后台链路套餐-1个月',
          }),
        );
      });

    const currentSubscriptionResponse = await request(app.getHttpServer())
      .get('/api/client/v1/subscriptions/current')
      .set('authorization', `Bearer ${client.accessToken}`)
      .expect(200);

    expect(currentSubscriptionResponse.body.data).toEqual(
      expect.objectContaining({
        planCode: 'ADMIN_FLOW_1M',
        planName: '后台链路套餐-1个月',
        status: 'ACTIVE',
        maxActiveSessions: 4,
      }),
    );
    expect(currentSubscriptionResponse.body.data.subscriptionUrl).toMatch(
      /^https:\/\/vpn\.residential-agent\.com\/sub\//,
    );
    expect(currentSubscriptionResponse.body.data.marzbanUsername).toMatch(/^cvpn_/);

    const meResponse = await request(app.getHttpServer())
      .get('/api/client/v1/me')
      .set('authorization', `Bearer ${client.accessToken}`)
      .expect(200);

    expect(meResponse.body.data.subscription).toEqual(
      expect.objectContaining({
        planCode: 'ADMIN_FLOW_1M',
        planName: '后台链路套餐-1个月',
        status: 'ACTIVE',
        maxActiveSessions: 4,
      }),
    );
  });

  it('reads persisted commission withdraw requests from PostgreSQL for admin and never falls back to mock rows', async () => {
    const client = await registerClient(app, 'withdraw-admin-db@example.com');
    seedPersistedWithdrawal({
      accountId: client.accountId,
      requestNo: 'WDR-REAL-001',
      amount: '88.50',
      status: 'UNDER_REVIEW',
      payoutAddress: 'DbAdminWithdrawalAddress11111111111111111111',
      txHash: null,
      failReason: null,
      reviewedAt: '2026-04-10T09:30:00.000Z',
      completedAt: null,
      createdAt: '2026-04-10T09:00:00.000Z',
      updatedAt: '2026-04-10T09:30:00.000Z',
      reviewerAdminId: 'admin-seed-1',
    });

    const response = await request(app.getHttpServer())
      .get(
        '/api/admin/v1/withdrawals?page=1&pageSize=10&accountEmail=withdraw-admin-db@example.com',
      )
      .set('authorization', `Bearer ${adminToken}`)
      .expect(200);

    const payload = response.body.data;
    expectFlatPagination(payload, {
      expectedPage: 1,
      expectedPageSize: 10,
      expectedTotal: 1,
      expectedItemsLength: 1,
    });
    expect(payload.items[0]).toEqual(
      expect.objectContaining({
        requestNo: 'WDR-REAL-001',
        accountEmail: 'withdraw-admin-db@example.com',
        status: 'UNDER_REVIEW',
        payoutAddress: 'DbAdminWithdrawalAddress11111111111111111111',
      }),
    );
    expect(Number(payload.items[0].amount)).toBeCloseTo(88.5, 8);

    const requestNos = payload.items.map((item: any) => item.requestNo);
    expect(requestNos).not.toEqual(
      expect.arrayContaining(['WDR-001', 'WDR-002']),
    );
  });

  it('persists client withdrawals in postgres mode so admin can read them after restart', async () => {
    const client = await registerClient(app, 'withdraw-persist-db@example.com');
    seedAvailableWithdrawalBalance(app, client.accountId, 25);

    const createResponse = await request(app.getHttpServer())
      .post('/api/client/v1/withdrawals')
      .set('authorization', `Bearer ${client.accessToken}`)
      .set('x-idempotency-key', 'withdraw-postgres-persist')
      .send({
        amount: '12.00',
        payoutAddress: 'PersistDbAddress111111111111111111111111111111',
        assetCode: 'USDT',
        networkCode: 'SOLANA',
      })
      .expect(201);

    const requestNo = createResponse.body.data.requestNo as string;
    expectPersistedWithdrawalRow(requestNo, {
      accountId: client.accountId,
      amount: 12,
      status: 'SUBMITTED',
    });

    await app.close();
    app = await bootstrapApp();
    adminToken = await loginAdmin(app);

    const adminResponse = await request(app.getHttpServer())
      .get(
        '/api/admin/v1/withdrawals?page=1&pageSize=10&accountEmail=withdraw-persist-db@example.com',
      )
      .set('authorization', `Bearer ${adminToken}`)
      .expect(200);

    const adminPayload = adminResponse.body.data;
    expectFlatPagination(adminPayload, {
      expectedPage: 1,
      expectedPageSize: 10,
      expectedTotal: 1,
      expectedItemsLength: 1,
    });
    expect(adminPayload.items[0]).toEqual(
      expect.objectContaining({
        requestNo,
        accountEmail: 'withdraw-persist-db@example.com',
        status: 'SUBMITTED',
        assetCode: 'USDT',
        networkCode: 'SOLANA',
      }),
    );
    expect(Number(adminPayload.items[0].amount)).toBeCloseTo(12, 8);

    const clientHistory = await request(app.getHttpServer())
      .get('/api/client/v1/withdrawals')
      .set('authorization', `Bearer ${client.accessToken}`)
      .expect(200);

    expect(clientHistory.body.data.items).toEqual(
      expect.arrayContaining([
        expect.objectContaining({
          requestNo,
          status: 'SUBMITTED',
        }),
      ]),
    );
    expect(Number(clientHistory.body.data.items[0].amount)).toBeCloseTo(12, 8);
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

async function loginAdmin(app: INestApplication) {
  const response = await request(app.getHttpServer())
    .post('/api/admin/v1/auth/login')
    .send({
      username: 'admin',
      password: 'admin123',
    })
    .expect(200);

  return response.body.data.accessToken as string;
}

async function registerClient(app: INestApplication, email: string) {
  await request(app.getHttpServer())
    .post('/api/client/v1/auth/register/email/request-code')
    .send({ email })
    .expect(200);

  const registerResponse = await request(app.getHttpServer())
    .post('/api/client/v1/auth/register/email')
    .set('x-idempotency-key', `register-${email}`)
    .send({
      email,
      code: '123456',
      password: 'Passw0rd!',
    })
    .expect(200);

  return {
    email,
    accountId: registerResponse.body.data.accountId as string,
    accessToken: registerResponse.body.data.accessToken as string,
  };
}

function expectFlatPagination(
  payload: any,
  input: {
    expectedPage: number;
    expectedTotal: number;
    expectedItemsLength: number;
    expectedPageSize?: number;
  },
) {
  expect(payload).toEqual(
    expect.objectContaining({
      items: expect.any(Array),
      page: expect.any(Number),
      pageSize: expect.any(Number),
      total: input.expectedTotal,
    }),
  );
  expect(payload.page).toBe(input.expectedPage);
  if (input.expectedPageSize !== undefined) {
    expect(payload.pageSize).toBe(input.expectedPageSize);
  }
  expect(payload.items).toHaveLength(input.expectedItemsLength);
}

function ensureAdminReadSchema() {
  runtimeDb.public.none(`
    CREATE TABLE IF NOT EXISTS accounts (
      id text PRIMARY KEY,
      account_no text NOT NULL UNIQUE,
      email text NOT NULL UNIQUE,
      password_hash text NOT NULL,
      status text NOT NULL DEFAULT 'PENDING_VERIFY',
      referral_code text NOT NULL UNIQUE,
      inviter_account_id text NULL,
      email_verified_at timestamptz NULL,
      risk_level integer NOT NULL DEFAULT 0,
      last_login_at timestamptz NULL,
      created_at timestamptz NOT NULL,
      updated_at timestamptz NOT NULL
    );

    CREATE TABLE IF NOT EXISTS admin_users (
      id text PRIMARY KEY,
      username text NOT NULL UNIQUE,
      password_hash text NOT NULL,
      role text NOT NULL,
      status text NOT NULL,
      display_name text NULL,
      email text NULL,
      created_at timestamptz NOT NULL,
      updated_at timestamptz NOT NULL
    );

    CREATE TABLE IF NOT EXISTS audit_logs (
      id text PRIMARY KEY,
      request_id text NULL,
      actor_type text NOT NULL,
      actor_id text NULL,
      module text NOT NULL,
      action text NOT NULL,
      target_type text NULL,
      target_id text NULL,
      before_json jsonb NOT NULL DEFAULT '{}'::jsonb,
      after_json jsonb NOT NULL DEFAULT '{}'::jsonb,
      metadata_json jsonb NOT NULL DEFAULT '{}'::jsonb,
      ip text NULL,
      user_agent text NULL,
      created_at timestamptz NOT NULL
    );

    CREATE TABLE IF NOT EXISTS plans (
      id text PRIMARY KEY,
      plan_code text NOT NULL UNIQUE,
      name text NOT NULL,
      description text NULL,
      billing_cycle_months integer NOT NULL,
      price_usd numeric(20,8) NOT NULL,
      is_unlimited_traffic boolean NOT NULL DEFAULT true,
      max_active_sessions integer NOT NULL DEFAULT 1,
      region_access_policy text NOT NULL,
      includes_advanced_regions boolean NOT NULL DEFAULT false,
      status text NOT NULL,
      display_order integer NOT NULL DEFAULT 0,
      created_at timestamptz NOT NULL,
      updated_at timestamptz NOT NULL
    );

    CREATE TABLE IF NOT EXISTS vpn_regions (
      id text PRIMARY KEY,
      region_code text NOT NULL UNIQUE,
      display_name text NOT NULL,
      tier text NOT NULL,
      status text NOT NULL,
      sort_order integer NOT NULL,
      icon_url text NULL,
      remark text NULL,
      created_at timestamptz NOT NULL,
      updated_at timestamptz NOT NULL
    );

    CREATE TABLE IF NOT EXISTS plan_region_permissions (
      id text PRIMARY KEY,
      plan_id text NOT NULL REFERENCES plans(id) ON DELETE CASCADE,
      region_id text NOT NULL REFERENCES vpn_regions(id) ON DELETE CASCADE,
      created_at timestamptz NOT NULL
    );

    CREATE TABLE IF NOT EXISTS vpn_nodes (
      id text PRIMARY KEY,
      region_id text NOT NULL REFERENCES vpn_regions(id) ON DELETE CASCADE,
      node_code text NOT NULL UNIQUE,
      host text NOT NULL,
      port integer NOT NULL,
      protocol text NOT NULL,
      transport_protocol text NOT NULL,
      security_type text NOT NULL,
      reality_public_key text NULL,
      server_name text NULL,
      short_id text NULL,
      flow text NOT NULL,
      weight integer NOT NULL,
      status text NOT NULL,
      health_status text NOT NULL,
      last_heartbeat_at timestamptz NULL,
      created_at timestamptz NOT NULL,
      updated_at timestamptz NOT NULL
    );

    CREATE TABLE IF NOT EXISTS legal_documents (
      id text PRIMARY KEY,
      doc_type text NOT NULL,
      title text NOT NULL,
      version_no integer NOT NULL,
      markdown_content text NOT NULL,
      status text NOT NULL,
      published_at timestamptz NULL,
      created_at timestamptz NOT NULL,
      updated_at timestamptz NOT NULL
    );

    CREATE TABLE IF NOT EXISTS system_configs (
      id text PRIMARY KEY,
      scope text NOT NULL,
      config_key text NOT NULL,
      config_value text NOT NULL,
      description text NULL,
      mutable_in_prod boolean NOT NULL DEFAULT true,
      created_at timestamptz NOT NULL,
      updated_at timestamptz NOT NULL
    );

    CREATE TABLE IF NOT EXISTS app_versions (
      id text PRIMARY KEY,
      platform text NOT NULL,
      channel text NOT NULL,
      version_name text NOT NULL,
      version_code integer NOT NULL,
      min_supported_code integer NOT NULL,
      force_update boolean NOT NULL DEFAULT false,
      download_url text NOT NULL,
      sha256 text NOT NULL,
      release_notes text NOT NULL,
      status text NOT NULL,
      published_at timestamptz NULL,
      created_at timestamptz NOT NULL,
      updated_at timestamptz NOT NULL
    );

    CREATE TABLE IF NOT EXISTS commission_balances (
      id text PRIMARY KEY,
      account_id text NOT NULL REFERENCES accounts(id) ON DELETE CASCADE,
      settlement_asset_code text NOT NULL,
      settlement_network_code text NOT NULL,
      frozen_amount numeric(20,8) NOT NULL DEFAULT 0,
      available_amount numeric(20,8) NOT NULL DEFAULT 0,
      withdrawing_amount numeric(20,8) NOT NULL DEFAULT 0,
      withdrawn_total numeric(20,8) NOT NULL DEFAULT 0,
      created_at timestamptz NOT NULL,
      updated_at timestamptz NOT NULL
    );

    CREATE TABLE IF NOT EXISTS commission_withdraw_requests (
      id text PRIMARY KEY,
      request_no text NOT NULL UNIQUE,
      account_id text NOT NULL REFERENCES accounts(id) ON DELETE CASCADE,
      amount numeric(20,8) NOT NULL,
      asset_code text NOT NULL,
      network_code text NOT NULL,
      payout_address text NOT NULL,
      fee_amount numeric(20,8) NOT NULL DEFAULT 0,
      status text NOT NULL,
      reviewer_admin_id text NULL REFERENCES admin_users(id) ON DELETE SET NULL,
      reviewed_at timestamptz NULL,
      tx_hash text NULL,
      fail_reason text NULL,
      completed_at timestamptz NULL,
      created_at timestamptz NOT NULL,
      updated_at timestamptz NOT NULL
    );
  `);
}

function seedAdminReadData() {
  runtimeDb.public.none(`
    INSERT INTO admin_users (
      id,
      username,
      password_hash,
      role,
      status,
      display_name,
      email,
      created_at,
      updated_at
    )
    VALUES (
      'admin-seed-1',
      'seeded-admin',
      'hashed',
      'FINANCE_ADMIN',
      'ACTIVE',
      'Seeded Admin',
      'seeded-admin@example.com',
      '2026-04-10T08:00:00.000Z',
      '2026-04-10T08:00:00.000Z'
    )
    ON CONFLICT (id) DO NOTHING;

    INSERT INTO audit_logs (
      id,
      request_id,
      actor_type,
      actor_id,
      module,
      action,
      target_type,
      target_id,
      before_json,
      after_json,
      metadata_json,
      ip,
      user_agent,
      created_at
    )
    VALUES
      (
        'audit-db-1',
        'req-db-audit-1',
        'ADMIN',
        'admin-seed-1',
        'WITHDRAWALS',
        'APPROVE',
        'WITHDRAWAL',
        'WDR-REAL-001',
        '{"status":"SUBMITTED"}',
        '{"status":"APPROVED"}',
        '{"source":"postgres"}',
        '10.0.0.10',
        'db-agent',
        '2026-04-10T08:00:00.000Z'
      ),
      (
        'audit-db-2',
        'req-db-audit-2',
        'SYSTEM',
        NULL,
        'SYSTEM_CONFIGS',
        'UPDATE',
        'CONFIG',
        'WITHDRAW_MIN_AMOUNT',
        '{"configValue":"10"}',
        '{"configValue":"12"}',
        '{"source":"postgres"}',
        '10.0.0.11',
        'db-agent',
        '2026-04-10T09:00:00.000Z'
      )
    ON CONFLICT (id) DO NOTHING;

    INSERT INTO plans (
      id,
      plan_code,
      name,
      description,
      billing_cycle_months,
      price_usd,
      is_unlimited_traffic,
      max_active_sessions,
      region_access_policy,
      includes_advanced_regions,
      status,
      display_order,
      created_at,
      updated_at
    )
    VALUES
      (
        'plan-basic-db',
        'BASIC_1M',
        '数据库基础版-1个月',
        '来自 PostgreSQL 的基础套餐',
        1,
        9.99,
        true,
        1,
        'BASIC_ONLY',
        false,
        'ACTIVE',
        1,
        '2026-04-01T00:00:00.000Z',
        '2026-04-09T00:00:00.000Z'
      ),
      (
        'plan-pro-db',
        'PRO_12M_DB',
        '数据库专业版-12个月',
        '来自 PostgreSQL 的高级套餐',
        12,
        99.99,
        true,
        5,
        'CUSTOM',
        true,
        'DISABLED',
        2,
        '2026-04-02T00:00:00.000Z',
        '2026-04-10T00:00:00.000Z'
      )
    ON CONFLICT (id) DO NOTHING;

    INSERT INTO vpn_regions (
      id,
      region_code,
      display_name,
      tier,
      status,
      sort_order,
      icon_url,
      remark,
      created_at,
      updated_at
    )
    VALUES
      (
        'region-eu-db',
        'EU_DB_FAST',
        '欧洲-数据库快线',
        'ADVANCED',
        'DISABLED',
        1,
        NULL,
        '来自 PostgreSQL 的禁用区域',
        '2026-04-01T00:00:00.000Z',
        '2026-04-10T01:00:00.000Z'
      ),
      (
        'region-sg-db',
        'SG_DB_MAINT',
        '新加坡-数据库维护区',
        'BASIC',
        'MAINTENANCE',
        2,
        NULL,
        '来自 PostgreSQL 的维护区域',
        '2026-04-02T00:00:00.000Z',
        '2026-04-10T02:00:00.000Z'
      )
    ON CONFLICT (id) DO NOTHING;

    INSERT INTO plan_region_permissions (id, plan_id, region_id, created_at)
    VALUES
      (
        'perm-basic-sg-db',
        'plan-basic-db',
        'region-sg-db',
        '2026-04-10T02:30:00.000Z'
      ),
      (
        'perm-pro-eu-db',
        'plan-pro-db',
        'region-eu-db',
        '2026-04-10T02:31:00.000Z'
      )
    ON CONFLICT (id) DO NOTHING;

    INSERT INTO vpn_nodes (
      id,
      region_id,
      node_code,
      host,
      port,
      protocol,
      transport_protocol,
      security_type,
      reality_public_key,
      server_name,
      short_id,
      flow,
      weight,
      status,
      health_status,
      last_heartbeat_at,
      created_at,
      updated_at
    )
    VALUES
      (
        'node-eu-db',
        'region-eu-db',
        'EU-DB-01',
        'eu-db-01.example.com',
        443,
        'VLESS',
        'tcp',
        'REALITY',
        'pubkey-eu',
        'eu-db.example.com',
        'abc123',
        'XTLS_VISION',
        80,
        'DISABLED',
        'OFFLINE',
        '2026-04-10T02:45:00.000Z',
        '2026-04-01T00:00:00.000Z',
        '2026-04-10T02:45:00.000Z'
      ),
      (
        'node-sg-db',
        'region-sg-db',
        'SG-DB-01',
        'sg-db-01.example.com',
        8443,
        'TROJAN',
        'ws',
        'TLS',
        NULL,
        'sg-db.example.com',
        NULL,
        'XTLS_VISION',
        90,
        'MAINTENANCE',
        'DEGRADED',
        '2026-04-10T02:50:00.000Z',
        '2026-04-02T00:00:00.000Z',
        '2026-04-10T02:50:00.000Z'
      )
    ON CONFLICT (id) DO NOTHING;

    INSERT INTO legal_documents (
      id,
      doc_type,
      title,
      version_no,
      markdown_content,
      status,
      published_at,
      created_at,
      updated_at
    )
    VALUES
      (
        'legal-db-terms',
        'USER_AGREEMENT',
        '数据库用户协议',
        1,
        '# terms from pg',
        'PUBLISHED',
        '2026-04-01T00:00:00.000Z',
        '2026-04-01T00:00:00.000Z',
        '2026-04-10T03:00:00.000Z'
      ),
      (
        'legal-db-privacy',
        'PRIVACY_POLICY',
        '数据库隐私政策',
        2,
        '# privacy from pg',
        'PUBLISHED',
        '2026-04-02T00:00:00.000Z',
        '2026-04-02T00:00:00.000Z',
        '2026-04-10T03:01:00.000Z'
      ),
      (
        'legal-db-refund',
        'PAYMENT_POLICY',
        '数据库退款政策',
        3,
        '# refund from pg',
        'PUBLISHED',
        '2026-04-03T00:00:00.000Z',
        '2026-04-03T00:00:00.000Z',
        '2026-04-10T03:02:00.000Z'
      ),
      (
        'legal-db-risk',
        'RISK_DISCLOSURE',
        '数据库风险披露',
        4,
        '# risk from pg',
        'PUBLISHED',
        '2026-04-04T00:00:00.000Z',
        '2026-04-04T00:00:00.000Z',
        '2026-04-10T03:03:00.000Z'
      )
    ON CONFLICT (id) DO NOTHING;

    INSERT INTO system_configs (
      id,
      scope,
      config_key,
      config_value,
      description,
      mutable_in_prod,
      created_at,
      updated_at
    )
    VALUES
      (
        'cfg-global-db',
        'GENERAL',
        'SITE_NOTICE',
        'database notice',
        '全局站点公告',
        true,
        '2026-04-10T04:00:00.000Z',
        '2026-04-10T04:00:00.000Z'
      ),
      (
        'cfg-vpn-db',
        'VPN',
        'CONFIG_ISSUE_MINUTES',
        '15',
        'VPN 配置签发分钟数',
        true,
        '2026-04-10T04:01:00.000Z',
        '2026-04-10T04:01:00.000Z'
      ),
      (
        'cfg-payment-db',
        'PAYMENT',
        'ORDER_EXPIRE_SECONDS',
        '900',
        '订单过期秒数',
        true,
        '2026-04-10T04:02:00.000Z',
        '2026-04-10T04:02:00.000Z'
      ),
      (
        'cfg-referral-db',
        'COMMISSION',
        'WITHDRAW_MIN_AMOUNT',
        '10',
        '提现最小金额',
        true,
        '2026-04-10T04:03:00.000Z',
        '2026-04-10T04:03:00.000Z'
      )
    ON CONFLICT (id) DO NOTHING;

    INSERT INTO app_versions (
      id,
      platform,
      channel,
      version_name,
      version_code,
      min_supported_code,
      force_update,
      download_url,
      sha256,
      release_notes,
      status,
      published_at,
      created_at,
      updated_at
    )
    VALUES
      (
        'version-android-db',
        'android',
        'google_play',
        '9.9.9-db',
        999,
        900,
        true,
        'https://example.com/android-db.apk',
        'sha-android-db',
        'android release from postgres',
        'PUBLISHED',
        '2026-04-10T05:00:00.000Z',
        '2026-04-10T05:00:00.000Z',
        '2026-04-10T05:00:00.000Z'
      ),
      (
        'version-ios-db',
        'ios',
        'app_store',
        '8.8.8-db',
        888,
        800,
        false,
        'https://example.com/ios-db.ipa',
        'sha-ios-db',
        'ios release from postgres',
        'PUBLISHED',
        '2026-04-10T05:01:00.000Z',
        '2026-04-10T05:01:00.000Z',
        '2026-04-10T05:01:00.000Z'
      )
    ON CONFLICT (id) DO NOTHING;
  `);
}

function seedPersistedWithdrawal(input: {
  accountId: string;
  requestNo: string;
  amount: string;
  status: string;
  payoutAddress: string;
  txHash: string | null;
  failReason: string | null;
  reviewedAt: string | null;
  completedAt: string | null;
  createdAt: string;
  updatedAt: string;
  reviewerAdminId: string | null;
}) {
  runtimeDb.public.none(`
    INSERT INTO commission_withdraw_requests (
      id,
      request_no,
      account_id,
      amount,
      asset_code,
      network_code,
      payout_address,
      fee_amount,
      status,
      reviewer_admin_id,
      reviewed_at,
      tx_hash,
      fail_reason,
      completed_at,
      created_at,
      updated_at
    )
    VALUES (
      'withdraw-${input.requestNo}',
      '${input.requestNo}',
      '${input.accountId}',
      ${input.amount},
      'USDT',
      'SOLANA',
      '${input.payoutAddress}',
      0,
      '${input.status}',
      ${toSqlStringOrNull(input.reviewerAdminId)},
      ${toSqlTimestampOrNull(input.reviewedAt)},
      ${toSqlStringOrNull(input.txHash)},
      ${toSqlStringOrNull(input.failReason)},
      ${toSqlTimestampOrNull(input.completedAt)},
      '${input.createdAt}',
      '${input.updatedAt}'
    )
    ON CONFLICT (request_no) DO NOTHING;
  `);
}

function seedAvailableWithdrawalBalance(
  app: INestApplication,
  accountId: string,
  amount: number,
) {
  runtimeDb.public.none(`
    INSERT INTO commission_balances (
      id,
      account_id,
      settlement_asset_code,
      settlement_network_code,
      frozen_amount,
      available_amount,
      withdrawing_amount,
      withdrawn_total,
      created_at,
      updated_at
    )
    VALUES (
      'balance-${accountId}',
      '${accountId}',
      'USDT',
      'SOLANA',
      0,
      ${amount.toFixed(8)},
      0,
      0,
      '2026-04-10T06:00:00.000Z',
      '2026-04-10T06:00:00.000Z'
    )
    ON CONFLICT (id) DO NOTHING;
  `);

  const referralService = app.get(ReferralService, { strict: false });
  const ledgerByBeneficiary = (referralService as any)?.ledgerByBeneficiary;
  if (ledgerByBeneficiary instanceof Map) {
    ledgerByBeneficiary.set(accountId, [
      {
        entryNo: `LEDGER-${accountId}`,
        beneficiaryAccountId: accountId,
        sourceOrderNo: 'ORD-DB-SEED',
        sourceAccountId: accountId,
        commissionLevel: 'LEVEL1',
        sourceAssetCode: 'USDT',
        sourceAmount: amount.toFixed(2),
        fxRateSnapshot: '1.00',
        settlementAmountUsdt: amount.toFixed(2),
        status: 'AVAILABLE',
        createdAt: '2026-04-10T06:00:00.000Z',
        availableAt: '2026-04-10T06:00:00.000Z',
      },
    ]);
  }
}

function expectPersistedWithdrawalRow(
  requestNo: string,
  expected: { accountId: string; amount: number; status: string },
) {
  const rows = runtimeDb.public.many(`
    SELECT
      request_no AS "requestNo",
      account_id AS "accountId",
      amount::text AS "amount",
      status
    FROM commission_withdraw_requests
    WHERE request_no = '${requestNo}'
  `) as Array<{
    requestNo: string;
    accountId: string;
    amount: string;
    status: string;
  }>;

  expect(rows).toHaveLength(1);
  expect(rows[0]).toEqual(
    expect.objectContaining({
      requestNo,
      accountId: expected.accountId,
      status: expected.status,
    }),
  );
  expect(Number(rows[0].amount)).toBeCloseTo(expected.amount, 8);
}

function cleanupTables() {
  const tables = [
    'commission_withdraw_requests',
    'commission_balances',
    'plan_region_permissions',
    'vpn_nodes',
    'vpn_regions',
    'app_versions',
    'legal_documents',
    'system_configs',
    'audit_logs',
    'plans',
    'admin_users',
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

function toSqlStringOrNull(value: string | null) {
  return value === null ? 'NULL' : `'${value}'`;
}

function toSqlTimestampOrNull(value: string | null) {
  return value === null ? 'NULL' : `'${value}'`;
}
