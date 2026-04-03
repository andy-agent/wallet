import { INestApplication, ValidationPipe } from '@nestjs/common';
import { Test } from '@nestjs/testing';
import * as request from 'supertest';
import { AppModule } from '../src/app.module';
import { AllExceptionsFilter } from '../src/common/filters/all-exceptions.filter';
import { ResponseEnvelopeInterceptor } from '../src/common/interceptors/response-envelope.interceptor';

describe('Chain-USDT E2E', () => {
  let app: INestApplication;

  beforeAll(async () => {
    process.env.INTERNAL_API_KEY = 'test-internal-key';
    process.env.MOCK_CHAIN = 'true';
    const moduleRef = await Test.createTestingModule({
      imports: [AppModule],
    }).compile();

    app = moduleRef.createNestApplication();
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

  afterAll(async () => {
    await app.close();
  });

  describe('Health', () => {
    it('/healthz (GET) - should return healthy status', () => {
      return request(app.getHttpServer())
        .get('/api/healthz')
        .expect(200)
        .expect((res) => {
          expect(res.body.code).toBe('OK');
          expect(res.body.data.status).toBe('healthy');
          expect(res.body.data.checks.chain.network).toBe('tron-mainnet');
        });
    });

    it('/healthz/ready (GET) - should return ready status', () => {
      return request(app.getHttpServer())
        .get('/api/healthz/ready')
        .expect(200)
        .expect((res) => {
          expect(res.body.code).toBe('OK');
          expect(res.body.data.ready).toBe(true);
        });
    });
  });

  describe('Internal', () => {
    it('/internal/discovery (GET) - should return service info', () => {
      return request(app.getHttpServer())
        .get('/api/internal/discovery')
        .expect(200)
        .expect((res) => {
          expect(res.body.code).toBe('OK');
          expect(res.body.data.service).toBe('chain-usdt');
          expect(res.body.data.network.name).toBe('tron-mainnet');
        });
    });
  });

  describe('Chain (Protected)', () => {
    it('/v1/chain/tx/:hash (GET) - should reject without API key', () => {
      return request(app.getHttpServer())
        .get('/api/v1/chain/tx/test123')
        .expect(401)
        .expect((res) => {
          expect(res.body.code).toBe('INTERNAL_AUTH_MISSING_KEY');
        });
    });

    it('/v1/chain/capabilities (GET) - should reject without API key', () => {
      return request(app.getHttpServer())
        .get('/api/v1/chain/capabilities')
        .expect(401);
    });

    it('/v1/chain/capabilities (GET) - should return mockMode=true when MOCK_CHAIN=true', () => {
      return request(app.getHttpServer())
        .get('/api/v1/chain/capabilities')
        .set('x-api-key', 'test-internal-key')
        .expect(200)
        .expect((res) => {
          expect(res.body.code).toBe('OK');
          expect(res.body.data.network).toBe('tron');
          expect(res.body.data.mockMode).toBe(true);
          expect(res.body.data.capabilities.query).toBe(true);
          expect(res.body.data.capabilities.broadcast).toBe(true);
        });
    });

    it('/v1/chain/block/current (GET) - should return mock block data in mock mode', () => {
      return request(app.getHttpServer())
        .get('/api/v1/chain/block/current')
        .set('x-api-key', 'test-internal-key')
        .expect(200)
        .expect((res) => {
          expect(res.body.code).toBe('OK');
          expect(res.body.data.height).toBeDefined();
          expect(res.body.data.hash).toBeDefined();
          expect(res.body.data.timestamp).toBeDefined();
          expect(res.body.data.txCount).toBeDefined();
        });
    });

    it('/v1/chain/tx/:hash (GET) - should return mock transaction in mock mode', () => {
      return request(app.getHttpServer())
        .get('/api/v1/chain/tx/test123')
        .set('x-api-key', 'test-internal-key')
        .expect(200)
        .expect((res) => {
          expect(res.body.code).toBe('OK');
          expect(res.body.data.found).toBe(true);
          expect(res.body.data.transaction).toBeDefined();
          expect(res.body.data.transaction.txHash).toBe('test123');
          expect(res.body.data.transaction.status).toBe('confirmed');
        });
    });
  });
});
