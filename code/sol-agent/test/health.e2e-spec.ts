import { Test, TestingModule } from '@nestjs/testing';
import { INestApplication, ValidationPipe } from '@nestjs/common';
import * as request from 'supertest';
import { AppModule } from '../src/app.module';
import { ResponseEnvelopeInterceptor } from '../src/common/interceptors/response-envelope.interceptor';
import { AllExceptionsFilter } from '../src/common/filters/all-exceptions.filter';

describe('HealthController (e2e)', () => {
  let app: INestApplication;

  beforeAll(async () => {
    process.env.INTERNAL_AUTH_TOKEN = 'test-token';
    process.env.SOLANA_RPC_MODE = 'mock';
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
  }, 30000);

  afterAll(async () => {
    await app.close();
  }, 10000);

  it('GET /api/healthz should return healthy', () => {
    return request(app.getHttpServer())
      .get('/api/healthz')
      .expect(200)
      .expect((res) => {
        expect(res.body.code).toBe('OK');
        expect(res.body.data.status).toMatch(/^(healthy|degraded)$/);
        expect(res.body.data.service).toBe('sol-agent');
        expect(res.body.data.rpc).toBeDefined();
        expect(res.body.data.rpc.network).toBeDefined();
      });
  }, 30000);
});
