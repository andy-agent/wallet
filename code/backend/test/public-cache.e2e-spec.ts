import { INestApplication, ValidationPipe } from '@nestjs/common';
import { Test, TestingModule } from '@nestjs/testing';
import * as request from 'supertest';
import { AllExceptionsFilter } from '../src/common/filters/all-exceptions.filter';
import { ResponseEnvelopeInterceptor } from '../src/common/interceptors/response-envelope.interceptor';
import {
  PUBLIC_EDGE_MARKET_CACHE_CONTROL,
  PUBLIC_EDGE_REFERENCE_CACHE_CONTROL,
  PUBLIC_EDGE_VERSION_CACHE_CONTROL,
} from '../src/common/http/public-cache-control';
import { AppVersionsController } from '../src/modules/app-versions/app-versions.controller';
import { AppVersionsService } from '../src/modules/app-versions/app-versions.service';
import { MarketController } from '../src/modules/market/market.controller';
import { MarketService } from '../src/modules/market/market.service';
import { PlansController } from '../src/modules/plans/plans.controller';
import { PlansService } from '../src/modules/plans/plans.service';

describe('Public cache headers (e2e)', () => {
  let app: INestApplication;

  beforeEach(async () => {
    const moduleFixture: TestingModule = await Test.createTestingModule({
      controllers: [AppVersionsController, PlansController, MarketController],
      providers: [
        {
          provide: AppVersionsService,
          useValue: {
            getLatestVersion: jest.fn().mockResolvedValue({
              versionName: '2.0.17.09',
              versionCode: 5072600,
              status: 'PUBLISHED',
            }),
          },
        },
        {
          provide: PlansService,
          useValue: {
            listPlans: jest.fn().mockResolvedValue([]),
          },
        },
        {
          provide: MarketService,
          useValue: {
            getOverview: jest.fn().mockResolvedValue({
              serverTime: 1775646549888,
              categories: [],
              boards: [],
              rows: [],
            }),
          },
        },
      ],
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
    jest.clearAllMocks();
    await app.close();
  });

  it('/api/client/v1/app-versions/latest (GET) exposes Cloudflare-friendly cache headers', async () => {
    const response = await request(app.getHttpServer())
      .get('/api/client/v1/app-versions/latest?platform=android&channel=official')
      .expect(200);

    expect(response.headers['cache-control']).toBe(
      PUBLIC_EDGE_VERSION_CACHE_CONTROL,
    );
  });

  it('/api/client/v1/plans (GET) exposes reference-data cache headers', async () => {
    const response = await request(app.getHttpServer())
      .get('/api/client/v1/plans')
      .expect(200);

    expect(response.headers['cache-control']).toBe(
      PUBLIC_EDGE_REFERENCE_CACHE_CONTROL,
    );
  });

  it('/api/client/v1/market/overview (GET) exposes short edge cache headers', async () => {
    const response = await request(app.getHttpServer())
      .get('/api/client/v1/market/overview')
      .expect(200);

    expect(response.headers['cache-control']).toBe(
      PUBLIC_EDGE_MARKET_CACHE_CONTROL,
    );
  });
});
