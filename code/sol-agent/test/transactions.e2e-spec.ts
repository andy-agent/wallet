import { Test, TestingModule } from '@nestjs/testing';
import { INestApplication, ValidationPipe } from '@nestjs/common';
import * as request from 'supertest';
import { AppModule } from '../src/app.module';
import { ResponseEnvelopeInterceptor } from '../src/common/interceptors/response-envelope.interceptor';
import { AllExceptionsFilter } from '../src/common/filters/all-exceptions.filter';

describe('TransactionsController (e2e)', () => {
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

  it('GET /api/v1/transactions/:signature - 无鉴权应返回 401', () => {
    return request(app.getHttpServer())
      .get('/api/v1/transactions/test-signature')
      .expect(401);
  });

  it('GET /api/v1/transactions/:signature - 有鉴权应返回交易状态 (mock 模式下为 pending)', async () => {
    const testSignature = '5f6aKj7x8y9z...test-signature';
    const response = await request(app.getHttpServer())
      .get(`/api/v1/transactions/${testSignature}`)
      .set('X-Internal-Auth', `Bearer ${token}`)
      .expect(200);

    expect(response.body.code).toBe('OK');
    expect(response.body.data.signature).toBe(testSignature);
    // Mock 模式下 transaction 为 null，返回 pending
    expect(response.body.data.status).toBe('pending');
    expect(response.body.data.confirmations).toBe(0);
  }, 30000);

  it('GET /api/v1/transactions/:signature - 支持 network 查询参数', async () => {
    const testSignature = 'test-sig-with-network';
    const response = await request(app.getHttpServer())
      .get(`/api/v1/transactions/${testSignature}?network=devnet`)
      .set('X-Internal-Auth', `Bearer ${token}`)
      .expect(200);

    expect(response.body.code).toBe('OK');
    expect(response.body.data.signature).toBe(testSignature);
    expect(response.body.data.status).toBeDefined();
    expect(response.body.data.confirmations).toBeDefined();
  }, 30000);
});
