import { INestApplication } from '@nestjs/common';
import { Test } from '@nestjs/testing';
import * as request from 'supertest';
import { AppModule } from '../src/app.module';

describe('Chain-USDT E2E', () => {
  let app: INestApplication;

  beforeAll(async () => {
    const moduleRef = await Test.createTestingModule({
      imports: [AppModule],
    }).compile();

    app = moduleRef.createNestApplication();
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
          expect(res.body.code).toBe('INTERNAL_AUTH_NOT_CONFIGURED');
        });
    });

    it('/v1/chain/capabilities (GET) - should reject without API key', () => {
      return request(app.getHttpServer())
        .get('/api/v1/chain/capabilities')
        .expect(401);
    });
  });
});
