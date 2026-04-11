import { INestApplication, ValidationPipe } from '@nestjs/common';
import { Test, TestingModule } from '@nestjs/testing';
import { mkdtempSync, rmSync } from 'fs';
import { tmpdir } from 'os';
import { join } from 'path';
import * as request from 'supertest';
import { AppModule } from '../src/app.module';
import { AllExceptionsFilter } from '../src/common/filters/all-exceptions.filter';
import { ResponseEnvelopeInterceptor } from '../src/common/interceptors/response-envelope.interceptor';
import { SolanaClientService } from '../src/modules/solana-client/solana-client.service';

describe('Orders (e2e)', () => {
  let app: INestApplication;
  let accessToken: string;
  let runtimeDir: string;
  let runtimeStateFile: string;
  const solanaCollectionAddress =
    '7YttLkHDo1B4ezgm6KPDLJrVN6a8GN28AL5soMgqd7qV';

  beforeEach(async () => {
    runtimeDir = mkdtempSync(join(tmpdir(), 'backend-orders-'));
    runtimeStateFile = join(runtimeDir, 'runtime-state.json');
    process.env.NODE_ENV = 'test';
    process.env.RUNTIME_STATE_FILE = runtimeStateFile;
    process.env.SOLANA_ORDER_COLLECTION_ADDRESS = solanaCollectionAddress;
    delete process.env.RUNTIME_STATE_BACKEND;

    app = await bootstrapApp();

    await request(app.getHttpServer())
      .post('/api/client/v1/auth/register/email/request-code')
      .send({ email: 'order@example.com' })
      .expect(200);

    const registerResponse = await request(app.getHttpServer())
      .post('/api/client/v1/auth/register/email')
      .set('x-idempotency-key', 'order-register-1')
      .send({
        email: 'order@example.com',
        code: '123456',
        password: 'Passw0rd!',
      })
      .expect(200);

    accessToken = registerResponse.body.data.accessToken;
  });

  afterEach(async () => {
    await app.close();
    delete process.env.NODE_ENV;
    delete process.env.RUNTIME_STATE_FILE;
    delete process.env.RUNTIME_STATE_BACKEND;
    delete process.env.SOLANA_ORDER_COLLECTION_ADDRESS;
    rmSync(runtimeDir, { recursive: true, force: true });
  });

  it('persists configured SOLANA payment target and idempotency across restart', async () => {
    const createResponse = await request(app.getHttpServer())
      .post('/api/client/v1/orders')
      .set('authorization', `Bearer ${accessToken}`)
      .set('x-idempotency-key', 'order-1')
      .send({
        planCode: 'BASIC_1M',
        orderType: 'NEW',
        quoteAssetCode: 'USDT',
        quoteNetworkCode: 'SOLANA',
      })
      .expect(201);

    const orderNo = createResponse.body.data.orderNo;
    const firstPaymentTarget = await request(app.getHttpServer())
      .get(`/api/client/v1/orders/${orderNo}/payment-target`)
      .set('authorization', `Bearer ${accessToken}`)
      .expect(200);

    const secondOrder = await request(app.getHttpServer())
      .post('/api/client/v1/orders')
      .set('authorization', `Bearer ${accessToken}`)
      .set('x-idempotency-key', 'order-2')
      .send({
        planCode: 'BASIC_1M',
        orderType: 'NEW',
        quoteAssetCode: 'USDT',
        quoteNetworkCode: 'SOLANA',
      })
      .expect(201);

    const secondPaymentTarget = await request(app.getHttpServer())
      .get(`/api/client/v1/orders/${secondOrder.body.data.orderNo}/payment-target`)
      .set('authorization', `Bearer ${accessToken}`)
      .expect(200);

    expect(firstPaymentTarget.body.data.collectionAddress).toBe(
      solanaCollectionAddress,
    );
    expect(secondPaymentTarget.body.data.collectionAddress).toBe(
      solanaCollectionAddress,
    );
    expect(firstPaymentTarget.body.data.uniqueAmountDelta).not.toBe(
      secondPaymentTarget.body.data.uniqueAmountDelta,
    );
    expect(firstPaymentTarget.body.data.qrText).toBe(
      `solana:${solanaCollectionAddress}?amount=${firstPaymentTarget.body.data.payableAmount}`,
    );

    await app.close();
    app = await bootstrapApp();

    const getAfterRestart = await request(app.getHttpServer())
      .get(`/api/client/v1/orders/${orderNo}`)
      .set('authorization', `Bearer ${accessToken}`)
      .expect(200);

    expect(getAfterRestart.body.data.orderNo).toBe(orderNo);
    expect(getAfterRestart.body.data.status).toBe('AWAITING_PAYMENT');

    const recreated = await request(app.getHttpServer())
      .post('/api/client/v1/orders')
      .set('authorization', `Bearer ${accessToken}`)
      .set('x-idempotency-key', 'order-1')
      .send({
        planCode: 'BASIC_1M',
        orderType: 'NEW',
        quoteAssetCode: 'USDT',
        quoteNetworkCode: 'SOLANA',
      })
      .expect(201);

    expect(recreated.body.data.orderNo).toBe(orderNo);

    const targetAfterRestart = await request(app.getHttpServer())
      .get(`/api/client/v1/orders/${orderNo}/payment-target`)
      .set('authorization', `Bearer ${accessToken}`)
      .expect(200);

    expect(targetAfterRestart.body.data).toEqual(firstPaymentTarget.body.data);
  });

  it('requires SOLANA transfer verification before provisioning and supports replacement tx submission', async () => {
    await app.close();

    const verifyIncomingTransfer = jest
      .fn()
      .mockImplementation(
        async ({ signature }: { signature: string }) => {
          if (signature === 'bad-sol-tx') {
            return {
              signature,
              status: 'confirmed' as const,
              confirmations: 1,
              verified: false,
              mismatchCode: 'RECIPIENT_MISMATCH' as const,
              failureReason: 'Recipient does not match the configured collection address',
            };
          }

          return {
            signature,
            status: 'confirmed' as const,
            confirmations: 1,
            verified: true,
            recipientAddress: solanaCollectionAddress,
            assetCode: 'USDT' as const,
            mint: 'Es9vMFrzaCERmJfrF4H2FYD4KCoNkY11McCe8BenwNYB',
            amount: '9.990000',
          };
        },
      );

    app = await bootstrapApp({
      solanaClient: {
        isEnabled: () => true,
        validateAddress: () => true,
        getUsdtMint: () => 'Es9vMFrzaCERmJfrF4H2FYD4KCoNkY11McCe8BenwNYB',
        verifyIncomingTransfer,
      },
    });

    await request(app.getHttpServer())
      .post('/api/client/v1/auth/register/email/request-code')
      .send({ email: 'order-verified@example.com' })
      .expect(200);

    const registerResponse = await request(app.getHttpServer())
      .post('/api/client/v1/auth/register/email')
      .set('x-idempotency-key', 'order-register-verified')
      .send({
        email: 'order-verified@example.com',
        code: '123456',
        password: 'Passw0rd!',
      })
      .expect(200);

    const verifiedAccessToken = registerResponse.body.data.accessToken;

    const createResponse = await request(app.getHttpServer())
      .post('/api/client/v1/orders')
      .set('authorization', `Bearer ${verifiedAccessToken}`)
      .set('x-idempotency-key', 'sol-verify-order')
      .send({
        planCode: 'BASIC_1M',
        orderType: 'NEW',
        quoteAssetCode: 'USDT',
        quoteNetworkCode: 'SOLANA',
      })
      .expect(201);

    const orderNo = createResponse.body.data.orderNo;

    await request(app.getHttpServer())
      .post(`/api/client/v1/orders/${orderNo}/submit-client-tx`)
      .set('authorization', `Bearer ${verifiedAccessToken}`)
      .send({
        txHash: 'bad-sol-tx',
        networkCode: 'SOLANA',
      })
      .expect(201);

    const invalidRefresh = await request(app.getHttpServer())
      .post(`/api/client/v1/orders/${orderNo}/refresh-status`)
      .set('authorization', `Bearer ${verifiedAccessToken}`)
      .send({})
      .expect(201);

    expect(invalidRefresh.body.data.status).toBe('UNDERPAID_REVIEW');
    expect(invalidRefresh.body.data.failureReason).toBe(
      'Recipient does not match the configured collection address',
    );

    await request(app.getHttpServer())
      .post(`/api/client/v1/orders/${orderNo}/submit-client-tx`)
      .set('authorization', `Bearer ${verifiedAccessToken}`)
      .send({
        txHash: 'good-sol-tx',
        networkCode: 'SOLANA',
      })
      .expect(201);

    const validRefresh = await request(app.getHttpServer())
      .post(`/api/client/v1/orders/${orderNo}/refresh-status`)
      .set('authorization', `Bearer ${verifiedAccessToken}`)
      .send({})
      .expect(201);

    expect(validRefresh.body.data.status).toBe('COMPLETED');
    expect(verifyIncomingTransfer).toHaveBeenNthCalledWith(1, {
      signature: 'bad-sol-tx',
      recipientAddress: solanaCollectionAddress,
      assetCode: 'USDT',
      mint: 'Es9vMFrzaCERmJfrF4H2FYD4KCoNkY11McCe8BenwNYB',
      expectedAmount: '9.990000',
    });
    expect(verifyIncomingTransfer).toHaveBeenNthCalledWith(2, {
      signature: 'good-sol-tx',
      recipientAddress: solanaCollectionAddress,
      assetCode: 'USDT',
      mint: 'Es9vMFrzaCERmJfrF4H2FYD4KCoNkY11McCe8BenwNYB',
      expectedAmount: '9.990000',
    });
  });

  it('keeps non-SOLANA tx state restart-safe without synthetic refresh progression', async () => {
    const createResponse = await request(app.getHttpServer())
      .post('/api/client/v1/orders')
      .set('authorization', `Bearer ${accessToken}`)
      .set('x-idempotency-key', 'tron-order-1')
      .send({
        planCode: 'BASIC_1M',
        orderType: 'NEW',
        quoteAssetCode: 'USDT',
        quoteNetworkCode: 'TRON',
      })
      .expect(201);

    const orderNo = createResponse.body.data.orderNo;

    await request(app.getHttpServer())
      .post(`/api/client/v1/orders/${orderNo}/submit-client-tx`)
      .set('authorization', `Bearer ${accessToken}`)
      .send({
        txHash: 'tron-tx-123',
        networkCode: 'TRON',
      })
      .expect(201);

    const refreshedBeforeRestart = await request(app.getHttpServer())
      .post(`/api/client/v1/orders/${orderNo}/refresh-status`)
      .set('authorization', `Bearer ${accessToken}`)
      .send({})
      .expect(201);

    expect(refreshedBeforeRestart.body.data.status).toBe('PAYMENT_DETECTED');
    expect(refreshedBeforeRestart.body.data.submittedClientTxHash).toBe('tron-tx-123');

    await app.close();
    app = await bootstrapApp();

    const orderAfterRestart = await request(app.getHttpServer())
      .get(`/api/client/v1/orders/${orderNo}`)
      .set('authorization', `Bearer ${accessToken}`)
      .expect(200);

    expect(orderAfterRestart.body.data.status).toBe('PAYMENT_DETECTED');
    expect(orderAfterRestart.body.data.submittedClientTxHash).toBe('tron-tx-123');

    const refreshedAfterRestart = await request(app.getHttpServer())
      .post(`/api/client/v1/orders/${orderNo}/refresh-status`)
      .set('authorization', `Bearer ${accessToken}`)
      .send({})
      .expect(201);

    expect(refreshedAfterRestart.body.data.status).toBe('PAYMENT_DETECTED');
  });
});

async function bootstrapApp(
  overrides?: {
    solanaClient?: Partial<
      Pick<
        SolanaClientService,
        'isEnabled' | 'validateAddress' | 'getUsdtMint' | 'verifyIncomingTransfer'
      >
    >;
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

  const moduleFixture: TestingModule = await moduleBuilder.compile();

  const nextApp = moduleFixture.createNestApplication();
  nextApp.setGlobalPrefix('api');
  nextApp.useGlobalPipes(
    new ValidationPipe({
      whitelist: true,
      transform: true,
      forbidUnknownValues: false,
    }),
  );
  nextApp.useGlobalInterceptors(new ResponseEnvelopeInterceptor());
  nextApp.useGlobalFilters(new AllExceptionsFilter());
  await nextApp.init();
  return nextApp;
}
