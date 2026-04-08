import { INestApplication, ServiceUnavailableException, ValidationPipe } from '@nestjs/common';
import { Test, TestingModule } from '@nestjs/testing';
import * as request from 'supertest';
import { AllExceptionsFilter } from '../src/common/filters/all-exceptions.filter';
import { ResponseEnvelopeInterceptor } from '../src/common/interceptors/response-envelope.interceptor';
import { MarketController } from '../src/modules/market/market.controller';
import { MarketService } from '../src/modules/market/market.service';
import {
  MARKET_DATA_PROVIDER,
  type MarketDataProvider,
  type ProviderCoinDetail,
  type ProviderMarketCoin,
  type ProviderOhlcPoint,
  type ProviderSearchCoin,
  type ProviderTrendingCoin,
} from '../src/modules/market/market.types';

describe('MarketController (e2e)', () => {
  let app: INestApplication;

  const topMarkets: ProviderMarketCoin[] = [
    {
      id: 'bitcoin',
      symbol: 'btc',
      name: 'Bitcoin',
      currentPrice: 71535,
      priceChange24h: 3245.33,
      priceChangePct24h: 4.752265294634004,
      high24h: 72379,
      low24h: 67805,
      totalVolume: 51900625548,
      marketCap: 1431424238213,
      marketCapRank: 1,
      lastUpdatedAt: 1775646549888,
    },
    {
      id: 'solana',
      symbol: 'sol',
      name: 'Solana',
      currentPrice: 84.59,
      priceChange24h: 5.225602,
      priceChangePct24h: 6.58426,
      high24h: 86.37,
      low24h: 78.48,
      totalVolume: 5022173872,
      marketCap: 48534787520,
      marketCapRank: 7,
      lastUpdatedAt: 1775646590732,
    },
  ];

  const searchResults: ProviderSearchCoin[] = [
    {
      id: 'solana',
      symbol: 'sol',
      name: 'Solana',
      marketCapRank: 7,
    },
  ];

  const trendingCoins: ProviderTrendingCoin[] = [
    {
      id: 'solana',
      symbol: 'sol',
      name: 'Solana',
      marketCapRank: 7,
      score: 0,
      currentPrice: 84.59,
      priceChangePct24h: 6.58426,
      totalVolume: 5022173872,
      marketCap: 48534787520,
    },
    {
      id: 'pepe',
      symbol: 'pepe',
      name: 'Pepe',
      marketCapRank: 35,
      score: 1,
      currentPrice: 0.0000128,
      priceChangePct24h: 14.29,
      totalVolume: 1120000000,
      marketCap: 5380000000,
    },
  ];

  const additionalMarkets: ProviderMarketCoin[] = [
    {
      id: 'pepe',
      symbol: 'pepe',
      name: 'Pepe',
      currentPrice: 0.0000128,
      priceChange24h: 0.0000016,
      priceChangePct24h: 14.29,
      high24h: 0.0000132,
      low24h: 0.0000109,
      totalVolume: 1120000000,
      marketCap: 5380000000,
      marketCapRank: 35,
      lastUpdatedAt: 1775646591732,
    },
  ];

  const detail: ProviderCoinDetail = {
    id: 'solana',
    symbol: 'sol',
    name: 'Solana',
    description: 'Solana detail',
    categories: ['Smart Contract Platform', 'Layer 1 (L1)', 'Solana Ecosystem'],
    homepageUrl: 'https://solana.com/',
    marketCapRank: 7,
    currentPrice: 84.59,
    priceChange24h: 5.225602,
    priceChangePct24h: 6.58426,
    high24h: 86.37,
    low24h: 78.48,
    totalVolume: 5022173872,
    marketCap: 48534787520,
    lastUpdatedAt: 1775646590732,
  };

  const ohlc: ProviderOhlcPoint[] = [
    {
      timestamp: 1775561400000,
      open: 80,
      high: 81,
      low: 79.5,
      close: 80.5,
    },
    {
      timestamp: 1775563200000,
      open: 80.5,
      high: 82,
      low: 80.25,
      close: 81.75,
    },
    {
      timestamp: 1775565000000,
      open: 81.75,
      high: 83,
      low: 81.5,
      close: 82.5,
    },
    {
      timestamp: 1775566800000,
      open: 82.5,
      high: 84,
      low: 82.25,
      close: 83.75,
    },
  ];

  const marketProvider: MarketDataProvider = {
    getTopMarkets: jest.fn().mockResolvedValue(topMarkets),
    getMarketsByIds: jest.fn().mockImplementation(async (ids: string[]) => {
      const pool = [...topMarkets, ...additionalMarkets];
      return pool.filter((coin) => ids.includes(coin.id));
    }),
    searchCoins: jest.fn().mockResolvedValue(searchResults),
    getTrendingCoins: jest.fn().mockResolvedValue(trendingCoins),
    getCoinDetail: jest.fn().mockResolvedValue(detail),
    getCoinOhlc: jest.fn().mockResolvedValue(ohlc),
  };

  beforeEach(async () => {
    const moduleBuilder = Test.createTestingModule({
      controllers: [MarketController],
      providers: [
        MarketService,
        {
          provide: MARKET_DATA_PROVIDER,
          useValue: marketProvider,
        },
      ],
    });

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
  });

  afterEach(async () => {
    jest.clearAllMocks();
    await app.close();
  });

  it('/api/client/v1/market/overview (GET) exposes real-market DTO shape', async () => {
    const response = await request(app.getHttpServer())
      .get('/api/client/v1/market/overview')
      .expect(200);

    expect(response.body).toMatchObject({
      code: 'OK',
      message: 'ok',
      data: {
        boards: expect.arrayContaining([
          expect.objectContaining({
            key: 'hot',
            label: '热门',
          }),
        ]),
        categories: expect.arrayContaining([
          expect.objectContaining({
            key: 'hot',
          }),
        ]),
        rows: expect.arrayContaining([
          expect.objectContaining({
            instrument: expect.objectContaining({
              instrumentId: 'crypto:solana',
              symbol: 'SOL',
            }),
            ticker24h: expect.objectContaining({
              lastPrice: '84.59',
              pctChange24h: '6.58426',
            }),
          }),
        ]),
      },
    });
    expect(response.body.requestId).toBeDefined();
  });

  it('/api/client/v1/market/instruments/:instrumentId (GET) returns detail facts', async () => {
    const response = await request(app.getHttpServer())
      .get('/api/client/v1/market/instruments/crypto:solana')
      .expect(200);

    expect(response.body.data).toMatchObject({
      instrument: {
        instrumentId: 'crypto:solana',
        symbol: 'SOL',
        displayName: 'Solana',
      },
      ticker24h: {
        lastPrice: '84.59',
        turnover24h: '5022173872',
      },
      supportedTimeframes: expect.arrayContaining([
        expect.objectContaining({ key: '1h' }),
        expect.objectContaining({ key: '1d' }),
      ]),
      overviewFacts: expect.arrayContaining([
        expect.objectContaining({ key: 'range24h' }),
      ]),
      detailFacts: expect.arrayContaining([
        expect.objectContaining({ key: 'market', value: 'CRYPTO' }),
      ]),
    });
  });

  it('/api/client/v1/market/instruments/:instrumentId (GET) falls back to market summary when provider detail is unavailable', async () => {
    (marketProvider.getCoinDetail as jest.Mock).mockRejectedValueOnce(
      new ServiceUnavailableException({
        code: 'MARKET_PROVIDER_UNAVAILABLE',
        message: 'Market data provider is unavailable',
      }),
    );
    (marketProvider.getMarketsByIds as jest.Mock).mockResolvedValueOnce([
      {
        id: 'zcash',
        symbol: 'zec',
        name: 'Zcash',
        currentPrice: 329.75,
        priceChange24h: 63.54,
        priceChangePct24h: 23.87,
        high24h: 341.0,
        low24h: 260.0,
        totalVolume: 125000000,
        marketCap: 5100000000,
        marketCapRank: 45,
        lastUpdatedAt: 1775646590732,
      },
    ]);

    const response = await request(app.getHttpServer())
      .get('/api/client/v1/market/instruments/crypto:zcash')
      .expect(200);

    expect(response.body.data).toMatchObject({
      instrument: {
        instrumentId: 'crypto:zcash',
        symbol: 'ZEC',
        displayName: 'Zcash',
      },
      tradeAction: {
        enabled: true,
      },
    });
    expect(marketProvider.getCoinDetail).toHaveBeenCalledWith('zcash');
    expect(marketProvider.getMarketsByIds).toHaveBeenCalledWith(['zcash']);
  });

  it('/api/client/v1/market/instruments/:instrumentId/candles (GET) returns candles', async () => {
    const response = await request(app.getHttpServer())
      .get('/api/client/v1/market/instruments/crypto:solana/candles')
      .query({ timeframe: '1h', limit: 2 })
      .expect(200);

    expect(response.body.data).toMatchObject({
      instrumentId: 'crypto:solana',
      timeframe: '1h',
      candles: [
        expect.objectContaining({
          open: '80',
          high: '82',
          low: '79.5',
          close: '81.75',
        }),
        expect.objectContaining({
          open: '81.75',
          high: '84',
          low: '81.5',
          close: '83.75',
        }),
      ],
    });
  });
});
