import { INestApplication, ValidationPipe } from '@nestjs/common';
import { Test, TestingModule } from '@nestjs/testing';
import * as request from 'supertest';
import { AppModule } from '../src/app.module';
import { AllExceptionsFilter } from '../src/common/filters/all-exceptions.filter';
import { ResponseEnvelopeInterceptor } from '../src/common/interceptors/response-envelope.interceptor';

describe('Wallet (e2e)', () => {
  let app: INestApplication;
  let accessToken: string;

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
      .send({ email: 'wallet@example.com' })
      .expect(200);

    const registerResponse = await request(app.getHttpServer())
      .post('/api/client/v1/auth/register/email')
      .set('x-idempotency-key', 'wallet-register-1')
      .send({
        email: 'wallet@example.com',
        code: '123456',
        password: 'Passw0rd!',
      })
      .expect(200);

    accessToken = registerResponse.body.data.accessToken;
  });

  afterEach(async () => {
    await app.close();
  });

  it('wallet metadata and fallback flow', async () => {
    await request(app.getHttpServer())
      .get('/api/client/v1/wallet/chains')
      .set('authorization', `Bearer ${accessToken}`)
      .expect(200);

    await request(app.getHttpServer())
      .get('/api/client/v1/wallet/assets/catalog')
      .set('authorization', `Bearer ${accessToken}`)
      .expect(200);

    await request(app.getHttpServer())
      .post('/api/client/v1/wallet/public-addresses')
      .set('authorization', `Bearer ${accessToken}`)
      .send({
        networkCode: 'SOLANA',
        assetCode: 'USDT',
        address: 'So11111111111111111111111111111111111111112',
        isDefault: true,
      })
      .expect(201);

    await request(app.getHttpServer())
      .get('/api/client/v1/wallet/public-addresses')
      .set('authorization', `Bearer ${accessToken}`)
      .expect(200);

    await request(app.getHttpServer())
      .post('/api/client/v1/wallet/transfer/precheck')
      .set('authorization', `Bearer ${accessToken}`)
      .send({
        networkCode: 'SOLANA',
        assetCode: 'USDT',
        toAddress: 'So11111111111111111111111111111111111111112',
        amount: '10.5',
      })
      .expect(201);

    await request(app.getHttpServer())
      .post('/api/client/v1/wallet/transfer/proxy-broadcast')
      .set('authorization', `Bearer ${accessToken}`)
      .send({
        networkCode: 'SOLANA',
        assetCode: 'USDT',
        signedPayload: 'signed-payload',
        clientTxHash: 'tx-wallet-1',
      })
      .expect(201);
  });
});
