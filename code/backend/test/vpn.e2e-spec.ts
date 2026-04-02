import { INestApplication, ValidationPipe } from '@nestjs/common';
import { Test, TestingModule } from '@nestjs/testing';
import * as request from 'supertest';
import { AppModule } from '../src/app.module';
import { AllExceptionsFilter } from '../src/common/filters/all-exceptions.filter';
import { ResponseEnvelopeInterceptor } from '../src/common/interceptors/response-envelope.interceptor';

describe('PlansSubscriptionVpn (e2e)', () => {
  let app: INestApplication;
  let accessToken: string;
  let orderNo: string;

  beforeEach(async () => {
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

    await request(app.getHttpServer())
      .post('/api/client/v1/auth/register/email/request-code')
      .send({ email: 'vpn@example.com' })
      .expect(200);

    const registerResponse = await request(app.getHttpServer())
      .post('/api/client/v1/auth/register/email')
      .set('x-idempotency-key', 'vpn-register-1')
      .send({
        email: 'vpn@example.com',
        code: '123456',
        password: 'Passw0rd!',
      })
      .expect(200);

    accessToken = registerResponse.body.data.accessToken;

    const createOrder = await request(app.getHttpServer())
      .post('/api/client/v1/orders')
      .set('authorization', `Bearer ${accessToken}`)
      .set('x-idempotency-key', 'vpn-order-1')
      .send({
        planCode: 'BASIC_1M',
        orderType: 'NEW',
        quoteAssetCode: 'USDT',
        quoteNetworkCode: 'SOLANA',
      })
      .expect(201);

    orderNo = createOrder.body.data.orderNo;

    await request(app.getHttpServer())
      .post(`/api/client/v1/orders/${orderNo}/submit-client-tx`)
      .set('authorization', `Bearer ${accessToken}`)
      .send({
        txHash: 'tx-vpn-1',
        networkCode: 'SOLANA',
      })
      .expect(201);

    await request(app.getHttpServer())
      .post(`/api/client/v1/orders/${orderNo}/refresh-status`)
      .set('authorization', `Bearer ${accessToken}`)
      .send({})
      .expect(201);

    await request(app.getHttpServer())
      .post(`/api/client/v1/orders/${orderNo}/refresh-status`)
      .set('authorization', `Bearer ${accessToken}`)
      .send({})
      .expect(201);

    await request(app.getHttpServer())
      .post(`/api/client/v1/orders/${orderNo}/refresh-status`)
      .set('authorization', `Bearer ${accessToken}`)
      .send({})
      .expect(201);
  });

  afterEach(async () => {
    await app.close();
  });

  it('plans / subscription / vpn flow', async () => {
    const plans = await request(app.getHttpServer())
      .get('/api/client/v1/plans')
      .set('authorization', `Bearer ${accessToken}`)
      .expect(200);

    expect(plans.body.data.items).toHaveLength(1);

    const subscription = await request(app.getHttpServer())
      .get('/api/client/v1/subscriptions/current')
      .set('authorization', `Bearer ${accessToken}`)
      .expect(200);

    expect(subscription.body.data.status).toBe('ACTIVE');

    const regions = await request(app.getHttpServer())
      .get('/api/client/v1/vpn/regions')
      .set('authorization', `Bearer ${accessToken}`)
      .expect(200);

    expect(regions.body.data.items[0].regionCode).toBe('JP_BASIC');

    const issue = await request(app.getHttpServer())
      .post('/api/client/v1/vpn/config/issue')
      .set('authorization', `Bearer ${accessToken}`)
      .send({
        regionCode: 'JP_BASIC',
        connectionMode: 'global',
      })
      .expect(201);

    expect(issue.body.data.regionCode).toBe('JP_BASIC');

    const status = await request(app.getHttpServer())
      .get('/api/client/v1/vpn/status')
      .set('authorization', `Bearer ${accessToken}`)
      .expect(200);

    expect(status.body.data.canIssueConfig).toBe(true);
  });
});
