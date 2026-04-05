import { INestApplication, ValidationPipe } from '@nestjs/common';
import { Test, TestingModule } from '@nestjs/testing';
import * as request from 'supertest';
import { AppModule } from '../src/app.module';
import { AllExceptionsFilter } from '../src/common/filters/all-exceptions.filter';
import { ResponseEnvelopeInterceptor } from '../src/common/interceptors/response-envelope.interceptor';
import { SolanaClientService } from '../src/modules/solana-client/solana-client.service';

describe('HealthController (e2e)', () => {
  let app: INestApplication;
  const originalSolanaServiceEnabled = process.env.SOLANA_SERVICE_ENABLED;

  async function bootstrapApp(
    solanaClientOverride?: Partial<Pick<SolanaClientService, 'isEnabled' | 'health'>>,
  ) {
    const moduleBuilder = Test.createTestingModule({
      imports: [AppModule],
    });

    if (solanaClientOverride) {
      moduleBuilder.overrideProvider(SolanaClientService).useValue(solanaClientOverride);
    }

    const moduleFixture: TestingModule = await moduleBuilder.compile();

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
  }

  beforeEach(async () => {
    delete process.env.SOLANA_SERVICE_ENABLED;
    await bootstrapApp();
  });

  afterEach(async () => {
    await app.close();
    if (originalSolanaServiceEnabled === undefined) {
      delete process.env.SOLANA_SERVICE_ENABLED;
      return;
    }

    process.env.SOLANA_SERVICE_ENABLED = originalSolanaServiceEnabled;
  });

  it('/api/healthz (GET)', async () => {
    const response = await request(app.getHttpServer())
      .get('/api/healthz')
      .expect(200);

    expect(response.body).toMatchObject({
      code: 'OK',
      message: 'ok',
      data: {
        status: 'healthy',
        service: 'cryptovpn-backend',
        chainSide: {
          enabled: false,
          status: 'disabled',
        },
      },
    });
    expect(response.body.requestId).toBeDefined();
  });

  it('/api/healthz (GET) exposes upstream chain-side state when enabled', async () => {
    await app.close();
    process.env.SOLANA_SERVICE_ENABLED = 'true';

    await bootstrapApp({
      isEnabled: () => true,
      health: jest.fn().mockResolvedValue({
        status: 'healthy',
        version: 'test-1.0.0',
        rpcLatencyMs: 12,
      }),
    });

    const response = await request(app.getHttpServer())
      .get('/api/healthz')
      .expect(200);

    expect(response.body).toMatchObject({
      code: 'OK',
      message: 'ok',
      data: {
        status: 'healthy',
        service: 'cryptovpn-backend',
        chainSide: {
          enabled: true,
          status: 'healthy',
          upstream: {
            status: 'healthy',
            version: 'test-1.0.0',
            rpcLatencyMs: 12,
          },
        },
      },
    });
  });
});
