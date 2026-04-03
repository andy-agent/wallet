import { Test, TestingModule } from '@nestjs/testing';
import { INestApplication, ValidationPipe } from '@nestjs/common';
import * as request from 'supertest';
import { AppModule } from '../src/app.module';
import { ResponseEnvelopeInterceptor } from '../src/common/interceptors/response-envelope.interceptor';
import { AllExceptionsFilter } from '../src/common/filters/all-exceptions.filter';

describe('AddressController (e2e)', () => {
  let app: INestApplication;
  const token = 'test-token';

  beforeAll(async () => {
    process.env.INTERNAL_AUTH_TOKEN = token;
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

  it('POST /api/internal/v1/address - 无鉴权应返回 401', () => {
    return request(app.getHttpServer())
      .post('/api/internal/v1/address')
      .send({ accountId: 'user_1' })
      .expect(401);
  });

  it('POST /api/internal/v1/address - 有鉴权应返回真实 Solana 地址', () => {
    return request(app.getHttpServer())
      .post('/api/internal/v1/address')
      .set('X-Internal-Auth', `Bearer ${token}`)
      .send({ accountId: 'user_1' })
      .expect(200)
      .expect((res) => {
        expect(res.body.code).toBe('OK');
        expect(res.body.data.accountId).toBe('user_1');
        // 验证返回的是真实的 base58 编码的 Solana 地址（32-44 字符）
        expect(res.body.data.address).toMatch(/^[1-9A-HJ-NP-Za-km-z]{32,44}$/);
        expect(res.body.data.publicKey).toMatch(/^[1-9A-HJ-NP-Za-km-z]{32,44}$/);
        expect(res.body.data.networkCode).toBe('solana-mainnet');
        expect(res.body.data.createdAt).toBeDefined();
      });
  });

  it('GET /api/internal/v1/address/user_1 - 有鉴权应返回已生成的地址', () => {
    return request(app.getHttpServer())
      .get('/api/internal/v1/address/user_1')
      .set('X-Internal-Auth', `Bearer ${token}`)
      .expect(200)
      .expect((res) => {
        expect(res.body.code).toBe('OK');
        expect(res.body.data.accountId).toBe('user_1');
        expect(res.body.data.address).toMatch(/^[1-9A-HJ-NP-Za-km-z]{32,44}$/);
      });
  });

  it('GET /api/internal/v1/address/nonexistent - 有鉴权应返回未找到', () => {
    return request(app.getHttpServer())
      .get('/api/internal/v1/address/nonexistent')
      .set('X-Internal-Auth', `Bearer ${token}`)
      .expect(200)
      .expect((res) => {
        expect(res.body.code).toBe('OK');
        expect(res.body.data.accountId).toBe('nonexistent');
        expect(res.body.data.address).toBeNull();
        expect(res.body.data.note).toContain('not found');
      });
  });
});
