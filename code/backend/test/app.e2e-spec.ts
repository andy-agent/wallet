import { INestApplication, ValidationPipe } from '@nestjs/common';
import { Test, TestingModule } from '@nestjs/testing';
import * as request from 'supertest';
import { AppModule } from '../src/app.module';
import { AllExceptionsFilter } from '../src/common/filters/all-exceptions.filter';
import { ResponseEnvelopeInterceptor } from '../src/common/interceptors/response-envelope.interceptor';
import { SolanaClientService } from '../src/modules/solana-client/solana-client.service';
import { TronClientService } from '../src/modules/tron-client/tron-client.service';

describe('HealthController (e2e)', () => {
  let app: INestApplication;
  const originalSolanaServiceEnabled = process.env.SOLANA_SERVICE_ENABLED;
  const originalTronServiceEnabled = process.env.TRON_SERVICE_ENABLED;

  async function bootstrapApp(
    overrides?: {
      solanaClient?: Partial<Pick<SolanaClientService, 'isEnabled' | 'health'>>;
      tronClient?: Partial<Pick<TronClientService, 'isEnabled' | 'health'>>;
    },
  ) {
    const moduleBuilder = Test.createTestingModule({
      imports: [AppModule],
    });

    if (overrides?.solanaClient) {
      moduleBuilder
        .overrideProvider(SolanaClientService)
        .useValue(overrides.solanaClient);
    }

    if (overrides?.tronClient) {
      moduleBuilder
        .overrideProvider(TronClientService)
        .useValue(overrides.tronClient);
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
    delete process.env.TRON_SERVICE_ENABLED;
    await bootstrapApp();
  });

  afterEach(async () => {
    await app.close();
    if (originalSolanaServiceEnabled === undefined) {
      delete process.env.SOLANA_SERVICE_ENABLED;
      return;
    }

    process.env.SOLANA_SERVICE_ENABLED = originalSolanaServiceEnabled;

    if (originalTronServiceEnabled === undefined) {
      delete process.env.TRON_SERVICE_ENABLED;
      return;
    }

    process.env.TRON_SERVICE_ENABLED = originalTronServiceEnabled;
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
          services: {
            solana: {
              enabled: false,
              status: 'disabled',
            },
            tron: {
              enabled: false,
              status: 'disabled',
            },
          },
        },
      },
    });
    expect(response.body.requestId).toBeDefined();
  });

  it('/api/healthz (GET) exposes upstream chain-side state when enabled', async () => {
    await app.close();
    process.env.SOLANA_SERVICE_ENABLED = 'true';
    process.env.TRON_SERVICE_ENABLED = 'true';

    await bootstrapApp({
      solanaClient: {
        isEnabled: () => true,
        health: jest.fn().mockResolvedValue({
          status: 'healthy',
          version: 'sol-test-1.0.0',
          rpcLatencyMs: 12,
        }),
      },
      tronClient: {
        isEnabled: () => true,
        health: jest.fn().mockResolvedValue({
          status: 'healthy',
          service: 'chain-usdt',
          version: 'tron-test-1.0.0',
          checks: {
            chain: {
              status: 'connected',
              network: 'tron-mainnet',
              blockHeight: 123,
            },
          },
        }),
      },
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
          services: {
            solana: {
              enabled: true,
              status: 'healthy',
              upstream: {
                status: 'healthy',
                version: 'sol-test-1.0.0',
                rpcLatencyMs: 12,
              },
            },
            tron: {
              enabled: true,
              status: 'healthy',
              upstream: {
                status: 'healthy',
                version: 'tron-test-1.0.0',
                blockHeight: 123,
              },
            },
          },
        },
      },
    });
  });

  it('/api/healthz (GET) degrades when an enabled upstream fails', async () => {
    await app.close();
    process.env.SOLANA_SERVICE_ENABLED = 'true';
    process.env.TRON_SERVICE_ENABLED = 'true';

    await bootstrapApp({
      solanaClient: {
        isEnabled: () => true,
        health: jest.fn().mockResolvedValue({
          status: 'healthy',
          version: 'sol-test-1.0.0',
          rpcLatencyMs: 12,
        }),
      },
      tronClient: {
        isEnabled: () => true,
        health: jest.fn().mockRejectedValue(new Error('tron upstream unreachable')),
      },
    });

    const response = await request(app.getHttpServer())
      .get('/api/healthz')
      .expect(200);

    expect(response.body).toMatchObject({
      code: 'OK',
      message: 'ok',
      data: {
        status: 'degraded',
        service: 'cryptovpn-backend',
        chainSide: {
          enabled: true,
          status: 'degraded',
          services: {
            solana: {
              enabled: true,
              status: 'healthy',
            },
            tron: {
              enabled: true,
              status: 'degraded',
              message: 'Remote chain-side health check failed',
            },
          },
        },
      },
    });
  });
});
