import { INestApplication, ValidationPipe } from '@nestjs/common';
import { Test, TestingModule } from '@nestjs/testing';
import { newDb } from 'pg-mem';
import * as request from 'supertest';

const runtimeDb = newDb({ noAstCoverageCheck: true });

jest.mock('pg', () => runtimeDb.adapters.createPg());

import { AppModule } from '../src/app.module';
import { AllExceptionsFilter } from '../src/common/filters/all-exceptions.filter';
import { ResponseEnvelopeInterceptor } from '../src/common/interceptors/response-envelope.interceptor';

describe('Auth Postgres persistence (e2e)', () => {
  let app: INestApplication;

  beforeEach(async () => {
    process.env.NODE_ENV = 'production';
    process.env.DATABASE_URL =
      'postgres://runtime:runtime@server2:5432/cryptovpn';
    process.env.AUTH_BOOTSTRAP_SYSTEM_PASSWORD = 'SystemPassw0rd!';
    delete process.env.RUNTIME_STATE_BACKEND;
    delete process.env.RUNTIME_STATE_FILE;

    app = await bootstrapApp();
  });

  afterEach(async () => {
    await app.close();
    cleanupRuntimeTables();
    delete process.env.NODE_ENV;
    delete process.env.DATABASE_URL;
    delete process.env.RUNTIME_STATE_BACKEND;
    delete process.env.RUNTIME_STATE_FILE;
    delete process.env.AUTH_BOOTSTRAP_SYSTEM_PASSWORD;
    delete process.env.AUTH_BOOTSTRAP_SYSTEM_PASSWORD_HASH;
    delete process.env.AUTH_BOOTSTRAP_SYSTEM_EMAIL;
  });

  it('persists registered accounts, refresh sessions, and logout state across restart', async () => {
    await request(app.getHttpServer())
      .post('/api/client/v1/auth/register/email/request-code')
      .send({ email: 'auth-pg@example.com' })
      .expect(200);

    const registerResponse = await request(app.getHttpServer())
      .post('/api/client/v1/auth/register/email')
      .set('x-idempotency-key', 'auth-pg-register')
      .send({
        email: 'auth-pg@example.com',
        code: '123456',
        password: 'Passw0rd!',
      })
      .expect(200);

    const refreshBeforeRestart = await request(app.getHttpServer())
      .post('/api/client/v1/auth/refresh')
      .send({
        refreshToken: registerResponse.body.data.refreshToken,
      })
      .expect(200);

    const preRestartRefreshToken = refreshBeforeRestart.body.data.refreshToken;

    await app.close();
    app = await bootstrapApp();

    const refreshedAfterRestart = await request(app.getHttpServer())
      .post('/api/client/v1/auth/refresh')
      .send({
        refreshToken: preRestartRefreshToken,
      })
      .expect(200);

    const refreshedAccessToken = refreshedAfterRestart.body.data.accessToken;

    const refreshedSession = await request(app.getHttpServer())
      .get('/api/client/v1/me/session')
      .set('authorization', `Bearer ${refreshedAccessToken}`)
      .expect(200);

    expect(refreshedSession.body.data.status).toBe('ACTIVE');

    const loginResponse = await request(app.getHttpServer())
      .post('/api/client/v1/auth/login/password')
      .send({
        email: 'auth-pg@example.com',
        password: 'Passw0rd!',
      })
      .expect(200);

    expect(loginResponse.body.data.accessToken).toBeDefined();
    expect(loginResponse.body.data.refreshToken).toBeDefined();

    const restartedAccessToken = loginResponse.body.data.accessToken;

    await request(app.getHttpServer())
      .get('/api/client/v1/me')
      .set('authorization', `Bearer ${restartedAccessToken}`)
      .expect(200);

    await request(app.getHttpServer())
      .post('/api/client/v1/auth/logout')
      .set('authorization', `Bearer ${restartedAccessToken}`)
      .send({})
      .expect(200);

    await app.close();
    app = await bootstrapApp();

    await request(app.getHttpServer())
      .get('/api/client/v1/me')
      .set('authorization', `Bearer ${restartedAccessToken}`)
      .expect(401);
  });

  it('bootstraps and restores login for system@cnyirui.cn', async () => {
    const firstLogin = await request(app.getHttpServer())
      .post('/api/client/v1/auth/login/password')
      .send({
        email: 'system@cnyirui.cn',
        password: 'SystemPassw0rd!',
      })
      .expect(200);

    await app.close();
    app = await bootstrapApp();

    const secondLogin = await request(app.getHttpServer())
      .post('/api/client/v1/auth/login/password')
      .send({
        email: 'system@cnyirui.cn',
        password: 'SystemPassw0rd!',
      })
      .expect(200);

    const meResponse = await request(app.getHttpServer())
      .get('/api/client/v1/me')
      .set('authorization', `Bearer ${secondLogin.body.data.accessToken}`)
      .expect(200);

    expect(secondLogin.body.data.accountId).toBe(firstLogin.body.data.accountId);
    expect(meResponse.body.data.email).toBe('system@cnyirui.cn');
    expect(meResponse.body.data.status).toBe('ACTIVE');
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
    'client_sessions',
    'verification_codes',
    'accounts',
    'runtime_state_subscriptions',
    'runtime_state_orders',
  ];

  for (const table of tables) {
    try {
      runtimeDb.public.none(`DELETE FROM ${table}`);
    } catch {}
  }
}
