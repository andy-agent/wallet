import { INestApplication, ValidationPipe } from '@nestjs/common';
import { Test, TestingModule } from '@nestjs/testing';
import * as request from 'supertest';
import { AppModule } from '../src/app.module';
import { AllExceptionsFilter } from '../src/common/filters/all-exceptions.filter';
import { ResponseEnvelopeInterceptor } from '../src/common/interceptors/response-envelope.interceptor';

describe('ReferralWithdrawal (e2e)', () => {
  let app: INestApplication;
  let inviterToken: string;
  let inviterCode: string;
  let inviteeToken: string;

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
      .send({ email: 'inviter@example.com' })
      .expect(200);
    const inviter = await request(app.getHttpServer())
      .post('/api/client/v1/auth/register/email')
      .set('x-idempotency-key', 'inviter-register')
      .send({
        email: 'inviter@example.com',
        code: '123456',
        password: 'Passw0rd!',
      })
      .expect(200);
    inviterToken = inviter.body.data.accessToken;
    const inviterOverview = await request(app.getHttpServer())
      .get('/api/client/v1/referral/overview')
      .set('authorization', `Bearer ${inviterToken}`)
      .expect(200);
    inviterCode = inviterOverview.body.data.referralCode;

    await request(app.getHttpServer())
      .post('/api/client/v1/auth/register/email/request-code')
      .send({ email: 'invitee@example.com' })
      .expect(200);
    const invitee = await request(app.getHttpServer())
      .post('/api/client/v1/auth/register/email')
      .set('x-idempotency-key', 'invitee-register')
      .send({
        email: 'invitee@example.com',
        code: '123456',
        password: 'Passw0rd!',
      })
      .expect(200);
    inviteeToken = invitee.body.data.accessToken;
  });

  afterEach(async () => {
    await app.close();
  });

  it('bind referral and create frozen commission after completed order', async () => {
    await request(app.getHttpServer())
      .post('/api/client/v1/referral/bind')
      .set('authorization', `Bearer ${inviteeToken}`)
      .send({ referralCode: inviterCode })
      .expect(201);

    const order = await request(app.getHttpServer())
      .post('/api/client/v1/orders')
      .set('authorization', `Bearer ${inviteeToken}`)
      .set('x-idempotency-key', 'invitee-order-1')
      .send({
        planCode: 'BASIC_1M',
        orderType: 'NEW',
        quoteAssetCode: 'USDT',
        quoteNetworkCode: 'SOLANA',
      })
      .expect(201);

    const orderNo = order.body.data.orderNo;
    await request(app.getHttpServer())
      .post(`/api/client/v1/orders/${orderNo}/submit-client-tx`)
      .set('authorization', `Bearer ${inviteeToken}`)
      .send({ txHash: 'tx-ref-1', networkCode: 'SOLANA' })
      .expect(201);
    await request(app.getHttpServer())
      .post(`/api/client/v1/orders/${orderNo}/refresh-status`)
      .set('authorization', `Bearer ${inviteeToken}`)
      .send({})
      .expect(201);
    await request(app.getHttpServer())
      .post(`/api/client/v1/orders/${orderNo}/refresh-status`)
      .set('authorization', `Bearer ${inviteeToken}`)
      .send({})
      .expect(201);
    await request(app.getHttpServer())
      .post(`/api/client/v1/orders/${orderNo}/refresh-status`)
      .set('authorization', `Bearer ${inviteeToken}`)
      .send({})
      .expect(201);

    const overview = await request(app.getHttpServer())
      .get('/api/client/v1/referral/overview')
      .set('authorization', `Bearer ${inviterToken}`)
      .expect(200);

    expect(overview.body.data.hasBinding).toBe(false);
    expect(Number(overview.body.data.frozenAmountUsdt)).toBeGreaterThan(0);

    const ledger = await request(app.getHttpServer())
      .get('/api/client/v1/commissions/ledger')
      .set('authorization', `Bearer ${inviterToken}`)
      .expect(200);

    expect(ledger.body.data.items.length).toBeGreaterThan(0);
    expect(ledger.body.data.items[0].status).toBe('FROZEN');
  });

  it('create withdrawal returns insufficient when no available balance', async () => {
    await request(app.getHttpServer())
      .post('/api/client/v1/withdrawals')
      .set('authorization', `Bearer ${inviteeToken}`)
      .set('x-idempotency-key', 'withdraw-1')
      .send({
        amount: '10.00',
        payoutAddress: 'So11111111111111111111111111111111111111112',
        assetCode: 'USDT',
        networkCode: 'SOLANA',
      })
      .expect(409);

    await request(app.getHttpServer())
      .get('/api/client/v1/withdrawals')
      .set('authorization', `Bearer ${inviteeToken}`)
      .expect(200);
  });
});
