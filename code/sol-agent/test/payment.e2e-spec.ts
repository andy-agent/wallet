import { Test, TestingModule } from '@nestjs/testing';
import { INestApplication, ValidationPipe } from '@nestjs/common';
import * as request from 'supertest';
import { AppModule } from '../src/app.module';
import { ResponseEnvelopeInterceptor } from '../src/common/interceptors/response-envelope.interceptor';
import { AllExceptionsFilter } from '../src/common/filters/all-exceptions.filter';

// 使用一个已知的有效 Solana 地址进行测试（这是 System Program 地址）
const VALID_SOLANA_ADDRESS = '11111111111111111111111111111111';

// 检查是否在 mock 模式
const isMockMode = process.env.SOLANA_RPC_MODE === 'mock';

describe('PaymentController (e2e)', () => {
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

  it('GET /api/internal/v1/payment/:address/status - 无鉴权应返回 401', () => {
    return request(app.getHttpServer())
      .get(`/api/internal/v1/payment/${VALID_SOLANA_ADDRESS}/status`)
      .expect(401);
  });

  it('GET /api/internal/v1/payment/:address/status - 有鉴权应返回状态', async () => {
    const response = await request(app.getHttpServer())
      .get(`/api/internal/v1/payment/${VALID_SOLANA_ADDRESS}/status`)
      .set('X-Internal-Auth', `Bearer ${token}`)
      .expect(200);

    expect(response.body.code).toBe('OK');
    expect(response.body.data.address).toBe(VALID_SOLANA_ADDRESS);
    expect(response.body.data.status).toMatch(/^(pending|received|error)$/);
    expect(response.body.data.networkCode).toBe('solana-mainnet');
    expect(response.body.data.receivedAmount).toBeDefined();
    expect(response.body.data.updatedAt).toBeDefined();
  }, 30000);

  it('POST /api/internal/v1/payment/detect - 有鉴权应返回检测结果', async () => {
    const response = await request(app.getHttpServer())
      .post('/api/internal/v1/payment/detect')
      .set('X-Internal-Auth', `Bearer ${token}`)
      .send({ address: VALID_SOLANA_ADDRESS, expectedAmount: '1.0' })
      .expect(200);

    expect(response.body.code).toBe('OK');
    expect(response.body.data.address).toBe(VALID_SOLANA_ADDRESS);
    expect(response.body.data.status).toMatch(/^(pending|partial|confirmed|error)$/);
    expect(response.body.data.expectedAmount).toBe('1.0');
    expect(response.body.data.receivedAmount).toBeDefined();
    expect(response.body.data.updatedAt).toBeDefined();
  }, 30000);

  it('POST /api/internal/v1/payment/detect - 无效地址处理', async () => {
    const response = await request(app.getHttpServer())
      .post('/api/internal/v1/payment/detect')
      .set('X-Internal-Auth', `Bearer ${token}`)
      .send({ address: 'invalid_address', expectedAmount: '1.0' })
      .expect(200);

    expect(response.body.code).toBe('OK');
    expect(response.body.data.address).toBe('invalid_address');
    
    // 在 mock 模式下不会验证地址格式，返回 pending
    // 在真实 RPC 模式下会返回 error
    if (isMockMode) {
      expect(response.body.data.status).toBe('pending');
    } else {
      expect(response.body.data.status).toBe('error');
      expect(response.body.data.error).toBeDefined();
    }
  }, 30000);
});
