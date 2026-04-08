import { INestApplication, ValidationPipe } from '@nestjs/common';
import { Test, TestingModule } from '@nestjs/testing';
import { mkdtempSync, rmSync } from 'fs';
import { tmpdir } from 'os';
import { join } from 'path';
import * as request from 'supertest';
import { AppModule } from '../src/app.module';
import { AllExceptionsFilter } from '../src/common/filters/all-exceptions.filter';
import { ResponseEnvelopeInterceptor } from '../src/common/interceptors/response-envelope.interceptor';

describe('AuthAndAccount (e2e)', () => {
  let app: INestApplication;
  let runtimeDir: string;

  beforeEach(async () => {
    runtimeDir = mkdtempSync(join(tmpdir(), 'backend-auth-'));
    process.env.NODE_ENV = 'test';
    process.env.RUNTIME_STATE_FILE = join(runtimeDir, 'runtime-state.json');
    delete process.env.RUNTIME_STATE_BACKEND;
    delete process.env.DATABASE_URL;

    const moduleFixture: TestingModule = await Test.createTestingModule({
      imports: [AppModule],
    }).compile();

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
  });

  afterEach(async () => {
    await app.close();
    delete process.env.NODE_ENV;
    delete process.env.RUNTIME_STATE_FILE;
    delete process.env.RUNTIME_STATE_BACKEND;
    delete process.env.DATABASE_URL;
    rmSync(runtimeDir, { recursive: true, force: true });
  });

  it('register -> me -> session -> logout flow', async () => {
    await request(app.getHttpServer())
      .post('/api/client/v1/auth/register/email/request-code')
      .send({ email: 'user@example.com' })
      .expect(200);

    const registerResponse = await request(app.getHttpServer())
      .post('/api/client/v1/auth/register/email')
      .set('x-idempotency-key', 'register-1')
      .send({
        email: 'user@example.com',
        code: '123456',
        password: 'Passw0rd!',
      })
      .expect(200);

    const accessToken = registerResponse.body.data.accessToken;

    const meResponse = await request(app.getHttpServer())
      .get('/api/client/v1/me')
      .set('authorization', `Bearer ${accessToken}`)
      .expect(200);

    expect(meResponse.body.data.email).toBe('user@example.com');

    const sessionResponse = await request(app.getHttpServer())
      .get('/api/client/v1/me/session')
      .set('authorization', `Bearer ${accessToken}`)
      .expect(200);

    expect(sessionResponse.body.data.status).toBe('ACTIVE');

    await request(app.getHttpServer())
      .post('/api/client/v1/auth/logout')
      .set('authorization', `Bearer ${accessToken}`)
      .send({})
      .expect(200);
  });
});
