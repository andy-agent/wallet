import { INestApplication, ValidationPipe } from '@nestjs/common';
import { Test, TestingModule } from '@nestjs/testing';
import { createHash } from 'crypto';
import * as request from 'supertest';
import { AppModule } from '../src/app.module';
import { AllExceptionsFilter } from '../src/common/filters/all-exceptions.filter';
import { ResponseEnvelopeInterceptor } from '../src/common/interceptors/response-envelope.interceptor';
import { TronClientService } from '../src/modules/tron-client/tron-client.service';

describe('Wallet (e2e)', () => {
  let app: INestApplication;
  let accessToken: string;
  let email: string;
  const validSolanaAddress = '7YttLkHDo1B4ezgm6KPDLJrVN6a8GN28AL5soMgqd7qV';
  const validTronAddress = 'TLa2f6VPqDgRE67v1736s7bJ8Ray5wYjU7';
  const placeholderSolanaAddress = 'So11111111111111111111111111111111111111112';
  const originalTronServiceEnabled = process.env.TRON_SERVICE_ENABLED;
  const originalSolanaOrderCollectionAddress =
    process.env.SOLANA_ORDER_COLLECTION_ADDRESS;
  const originalSolanaCustomOrderAssetsJson =
    process.env.SOLANA_CUSTOM_ORDER_ASSETS_JSON;
  const originalWalletBackupRecipients = process.env.WALLET_BACKUP_RECIPIENTS;

  async function bootstrapApp(
    tronClientOverride?: Partial<
      Pick<
        TronClientService,
        'isEnabled' | 'validateAddress' | 'broadcastTransaction'
      >
    >,
  ) {
    const moduleBuilder = Test.createTestingModule({
      imports: [AppModule],
    });

    if (tronClientOverride) {
      moduleBuilder.overrideProvider(TronClientService).useValue(tronClientOverride);
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
    delete process.env.TRON_SERVICE_ENABLED;
    delete process.env.SOLANA_ORDER_COLLECTION_ADDRESS;
    process.env.WALLET_BACKUP_RECIPIENTS =
      'age1wp4zfn93luhad3qmzrvw60p5knc7lg736pxuhs4kkkzty0c5m58sn8p004';
    await bootstrapApp();
    email = `wallet-${Date.now()}-${Math.random().toString(16).slice(2, 8)}@example.com`;

    await request(app.getHttpServer())
      .post('/api/client/v1/auth/register/email/request-code')
      .send({ email })
      .expect(200);

    const registerResponse = await request(app.getHttpServer())
      .post('/api/client/v1/auth/register/email')
      .set('x-idempotency-key', `wallet-register-${Date.now()}`)
      .send({
        email,
        code: '123456',
        password: 'Passw0rd!',
      })
      .expect(200);

    accessToken = registerResponse.body.data.accessToken;
  });

  afterEach(async () => {
    await app.close();

    if (originalTronServiceEnabled === undefined) {
      delete process.env.TRON_SERVICE_ENABLED;
    } else {
      process.env.TRON_SERVICE_ENABLED = originalTronServiceEnabled;
    }

    if (originalSolanaOrderCollectionAddress === undefined) {
      delete process.env.SOLANA_ORDER_COLLECTION_ADDRESS;
    } else {
      process.env.SOLANA_ORDER_COLLECTION_ADDRESS =
        originalSolanaOrderCollectionAddress;
    }

    if (originalSolanaCustomOrderAssetsJson === undefined) {
      delete process.env.SOLANA_CUSTOM_ORDER_ASSETS_JSON;
    } else {
      process.env.SOLANA_CUSTOM_ORDER_ASSETS_JSON =
        originalSolanaCustomOrderAssetsJson;
    }

    if (originalWalletBackupRecipients === undefined) {
      delete process.env.WALLET_BACKUP_RECIPIENTS;
    } else {
      process.env.WALLET_BACKUP_RECIPIENTS = originalWalletBackupRecipients;
    }
  });

  it('wallet lifecycle exposes no-wallet vs created-wallet receive gating state', async () => {
    await request(app.getHttpServer())
      .get('/api/client/v1/wallet/lifecycle')
      .set('authorization', `Bearer ${accessToken}`)
      .expect(200)
      .expect((res) => {
        expect(res.body.data.walletExists).toBe(false);
        expect(res.body.data.lifecycleStatus).toBe('NOT_CREATED');
        expect(res.body.data.receiveState).toBe('NO_WALLET');
        expect(res.body.data.configuredAddressCount).toBe(0);
        expect(res.body.data.nextAction).toBe('CREATE_OR_IMPORT');
      });

    await request(app.getHttpServer())
      .post('/api/client/v1/wallet/lifecycle')
      .set('authorization', `Bearer ${accessToken}`)
      .send({
        action: 'CREATE',
        displayName: 'Primary Wallet',
      })
      .expect(201)
      .expect((res) => {
        expect(res.body.data.walletExists).toBe(true);
        expect(res.body.data.lifecycleStatus).toBe('CREATED');
        expect(res.body.data.status).toBe('CREATED_PENDING_BACKUP');
        expect(res.body.data.sourceType).toBe('CREATE');
        expect(res.body.data.receiveState).toBe('NO_ADDRESS');
        expect(res.body.data.displayName).toBe('Primary Wallet');
        expect(res.body.data.nextAction).toBe('BACKUP_MNEMONIC');
      });

    await request(app.getHttpServer())
      .post('/api/client/v1/wallet/lifecycle')
      .set('authorization', `Bearer ${accessToken}`)
      .send({
        action: 'ACKNOWLEDGE_BACKUP',
      })
      .expect(201)
      .expect((res) => {
        expect(res.body.data.lifecycleStatus).toBe('CREATED');
        expect(res.body.data.status).toBe('BACKUP_PENDING_CONFIRMATION');
        expect(res.body.data.nextAction).toBe('CONFIRM_MNEMONIC');
      });

    await request(app.getHttpServer())
      .post('/api/client/v1/wallet/lifecycle')
      .set('authorization', `Bearer ${accessToken}`)
      .send({
        action: 'CONFIRM_BACKUP',
      })
      .expect(201)
      .expect((res) => {
        expect(res.body.data.lifecycleStatus).toBe('ACTIVE');
        expect(res.body.data.nextAction).toBe('READY');
      });

    await request(app.getHttpServer())
      .post('/api/client/v1/wallet/public-addresses')
      .set('authorization', `Bearer ${accessToken}`)
      .send({
        networkCode: 'SOLANA',
        assetCode: 'USDT',
        address: validSolanaAddress,
        isDefault: true,
      })
      .expect(201);

    await request(app.getHttpServer())
      .get('/api/client/v1/wallet/lifecycle')
      .set('authorization', `Bearer ${accessToken}`)
      .expect(200)
      .expect((res) => {
        expect(res.body.data.walletExists).toBe(true);
        expect(res.body.data.receiveState).toBe('READY');
        expect(res.body.data.configuredAddressCount).toBe(1);
      });
  });

  it('wallet lifecycle keeps NO_ADDRESS across app restart', async () => {
    await request(app.getHttpServer())
      .post('/api/client/v1/wallet/lifecycle')
      .set('authorization', `Bearer ${accessToken}`)
      .send({
        action: 'CREATE',
        displayName: 'Restart Wallet',
      })
      .expect(201);

    await app.close();
    await bootstrapApp();

    await request(app.getHttpServer())
      .get('/api/client/v1/wallet/lifecycle')
      .set('authorization', `Bearer ${accessToken}`)
      .expect(200)
      .expect((res) => {
        expect(res.body.data.walletExists).toBe(true);
        expect(res.body.data.receiveState).toBe('NO_ADDRESS');
        expect(res.body.data.configuredAddressCount).toBe(0);
        expect(res.body.data.nextAction).toBe('BACKUP_MNEMONIC');
      });

    await request(app.getHttpServer())
      .get('/api/client/v1/wallet/receive-context?networkCode=SOLANA&assetCode=USDT')
      .set('authorization', `Bearer ${accessToken}`)
      .expect(200)
      .expect((res) => {
        expect(res.body.data.walletExists).toBe(true);
        expect(res.body.data.receiveState).toBe('NO_ADDRESS');
        expect(res.body.data.defaultAddress).toBeNull();
        expect(res.body.data.canShare).toBe(false);
        expect(res.body.data.status).toBe('未配置收款地址');
      });
  });

  it(
    'supports wallet custom token CRUD by wallet and chain',
    async () => {
    const walletResponse = await request(app.getHttpServer())
      .post('/api/client/v1/wallets/create-mnemonic')
      .set('authorization', `Bearer ${accessToken}`)
      .send({
        walletName: 'Token Wallet',
        keySlots: [
          {
            slotCode: 'SOLANA_0',
            chainFamily: 'SOLANA',
            derivationType: 'MNEMONIC',
          },
        ],
        chainAccounts: [
          {
            slotCode: 'SOLANA_0',
            chainFamily: 'SOLANA',
            networkCode: 'SOLANA',
            address: validSolanaAddress,
            isEnabled: true,
            isDefaultReceive: true,
          },
        ],
      })
      .expect(201);

    const walletId = walletResponse.body.data.wallet.walletId as string;

    const createResponse = await request(app.getHttpServer())
      .post(`/api/client/v1/wallets/${walletId}/custom-tokens`)
      .set('authorization', `Bearer ${accessToken}`)
      .send({
        chainId: 'solana',
        tokenAddress: 'EPjFWdd5AufqSSqeM2qN1xzybapC8G4wEGGkZwyTDt1v',
        name: 'USD Coin',
        symbol: 'USDC',
        decimals: 6,
        iconUrl: 'https://example.com/usdc.png',
      })
      .expect(201);

    const customTokenId = createResponse.body.data.customTokenId as string;

    await request(app.getHttpServer())
      .get(`/api/client/v1/wallets/${walletId}/custom-tokens?chainId=solana`)
      .set('authorization', `Bearer ${accessToken}`)
      .expect(200)
      .expect((res) => {
        expect(res.body.data.items).toEqual([
          expect.objectContaining({
            customTokenId,
            chainId: 'solana',
            symbol: 'USDC',
          }),
        ]);
      });

    await request(app.getHttpServer())
      .get('/api/client/v1/wallet/custom-tokens/search?chainId=solana&query=EPjFWdd5AufqSSqeM2qN1xzybapC8G4wEGGkZwyTDt1v')
      .set('authorization', `Bearer ${accessToken}`)
      .expect(200)
      .expect((res) => {
        expect(res.body.data.items).toEqual(
          expect.arrayContaining([
            expect.objectContaining({
              tokenAddress: 'EPjFWdd5AufqSSqeM2qN1xzybapC8G4wEGGkZwyTDt1v',
              chainId: 'solana',
            }),
          ]),
        );
      });

    await request(app.getHttpServer())
      .delete(`/api/client/v1/wallets/${walletId}/custom-tokens/${customTokenId}`)
      .set('authorization', `Bearer ${accessToken}`)
      .expect(200);

    await request(app.getHttpServer())
      .get(`/api/client/v1/wallets/${walletId}/custom-tokens?chainId=solana`)
      .set('authorization', `Bearer ${accessToken}`)
      .expect(200)
      .expect((res) => {
        expect(res.body.data.items).toHaveLength(0);
      });
    },
    20000,
  );

  it(
    'search endpoint returns real provider-backed results across chains',
    async () => {
      await request(app.getHttpServer())
        .get('/api/client/v1/wallet/custom-tokens/search?chainId=base&query=0x833589fCD6EDB6E08f4c7C32D4f71b54bdA02913')
        .set('authorization', `Bearer ${accessToken}`)
        .expect(200)
        .expect((res) => {
          expect(res.body.data.items).toEqual(
            expect.arrayContaining([
              expect.objectContaining({
                chainId: 'base',
                tokenAddress: '0x833589fcd6edb6e08f4c7c32d4f71b54bda02913',
              }),
            ]),
          );
        });

      await request(app.getHttpServer())
        .get('/api/client/v1/wallet/custom-tokens/search?chainId=tron&query=TR7NHqjeKQxGTCi8q8ZY4pL8otSzgjLj6t')
        .set('authorization', `Bearer ${accessToken}`)
        .expect(200)
        .expect((res) => {
          expect(res.body.data.items).toEqual(
            expect.arrayContaining([
              expect.objectContaining({
                chainId: 'tron',
                tokenAddress: 'TR7NHqjeKQxGTCi8q8ZY4pL8otSzgjLj6t',
              }),
            ]),
          );
        });
    },
    20000,
  );

  it('wallet public addresses survive app restart and keep READY receive state', async () => {
    const address = validSolanaAddress;

    await request(app.getHttpServer())
      .post('/api/client/v1/wallet/lifecycle')
      .set('authorization', `Bearer ${accessToken}`)
      .send({
        action: 'CREATE',
        displayName: 'Primary Wallet',
      })
      .expect(201);

    const upsertResponse = await request(app.getHttpServer())
      .post('/api/client/v1/wallet/public-addresses')
      .set('authorization', `Bearer ${accessToken}`)
      .send({
        networkCode: 'SOLANA',
        assetCode: 'USDT',
        address,
        isDefault: true,
      })
      .expect(201);

    await app.close();
    await bootstrapApp();

    await request(app.getHttpServer())
      .get('/api/client/v1/wallet/public-addresses?networkCode=SOLANA&assetCode=USDT')
      .set('authorization', `Bearer ${accessToken}`)
      .expect(200)
      .expect((res) => {
        expect(res.body.data.items).toHaveLength(1);
        expect(res.body.data.items[0]).toEqual(
          expect.objectContaining({
            addressId: upsertResponse.body.data.addressId,
            address,
            networkCode: 'SOLANA',
            assetCode: 'USDT',
            isDefault: true,
          }),
        );
      });

    await request(app.getHttpServer())
      .get('/api/client/v1/wallet/lifecycle')
      .set('authorization', `Bearer ${accessToken}`)
      .expect(200)
      .expect((res) => {
        expect(res.body.data.walletExists).toBe(true);
        expect(res.body.data.receiveState).toBe('READY');
        expect(res.body.data.configuredAddressCount).toBe(1);
      });

    await request(app.getHttpServer())
      .get('/api/client/v1/wallet/receive-context?networkCode=SOLANA&assetCode=USDT')
      .set('authorization', `Bearer ${accessToken}`)
      .expect(200)
      .expect((res) => {
        expect(res.body.data.receiveState).toBe('READY');
        expect(res.body.data.defaultAddress).toBe(address);
        expect(res.body.data.canShare).toBe(true);
      });
  });

  it('asset catalog only marks Solana checkout as payable when Solana order capability is configured', async () => {
    await request(app.getHttpServer())
      .get('/api/client/v1/wallet/assets/catalog')
      .set('authorization', `Bearer ${accessToken}`)
      .expect(200)
      .expect((res) => {
        expect(
          res.body.data.items.find(
            (item: { networkCode: string; assetCode: string }) =>
              item.networkCode === 'SOLANA' && item.assetCode === 'SOL',
          )?.orderPayable,
        ).toBe(false);
        expect(
          res.body.data.items.find(
            (item: { networkCode: string; assetCode: string }) =>
              item.networkCode === 'SOLANA' && item.assetCode === 'USDT',
          )?.orderPayable,
        ).toBe(false);
        expect(
          res.body.data.items.find(
            (item: { networkCode: string; assetCode: string }) =>
              item.networkCode === 'TRON' && item.assetCode === 'USDT',
          )?.orderPayable,
        ).toBe(true);
      });

    process.env.SOLANA_ORDER_COLLECTION_ADDRESS =
      '7YttLkHDo1B4ezgm6KPDLJrVN6a8GN28AL5soMgqd7qV';
    process.env.SOLANA_CUSTOM_ORDER_ASSETS_JSON = JSON.stringify([
      {
        assetCode: 'USDC',
        displayName: 'USD Coin (Solana)',
        symbol: 'USDC',
        decimals: 6,
        contractAddress: 'EPjFWdd5AufqSSqeM2qN1xzybapC8G4wEGGkZwyTDt1v',
        usdPrice: '1',
      },
    ]);
    await app.close();
    await bootstrapApp();

    await request(app.getHttpServer())
      .get('/api/client/v1/wallet/assets/catalog')
      .set('authorization', `Bearer ${accessToken}`)
      .expect(200)
      .expect((res) => {
        expect(
          res.body.data.items.find(
            (item: { networkCode: string; assetCode: string }) =>
              item.networkCode === 'SOLANA' && item.assetCode === 'SOL',
          )?.orderPayable,
        ).toBe(true);
        expect(
          res.body.data.items.find(
            (item: { networkCode: string; assetCode: string }) =>
              item.networkCode === 'SOLANA' && item.assetCode === 'USDT',
          )?.orderPayable,
        ).toBe(true);
        expect(
          res.body.data.items.find(
            (item: { networkCode: string; assetCode: string }) =>
              item.networkCode === 'SOLANA' && item.assetCode === 'USDC',
          ),
        ).toEqual(
          expect.objectContaining({
            orderPayable: true,
            contractAddress: 'EPjFWdd5AufqSSqeM2qN1xzybapC8G4wEGGkZwyTDt1v',
          }),
        );
      });
  });

  it('wallet metadata and fallback flow', async () => {
    await request(app.getHttpServer())
      .get('/api/client/v1/wallet/lifecycle')
      .set('authorization', `Bearer ${accessToken}`)
      .expect(200)
      .expect((res) => {
        expect(res.body.data.walletExists).toBe(false);
        expect(res.body.data.nextAction).toBe('CREATE_OR_IMPORT');
      });

    await request(app.getHttpServer())
      .post('/api/client/v1/wallet/lifecycle')
      .set('authorization', `Bearer ${accessToken}`)
      .send({
        action: 'CREATE',
        displayName: 'Primary Wallet',
      })
      .expect(201)
      .expect((res) => {
        expect(res.body.data.walletExists).toBe(true);
        expect(res.body.data.status).toBe('CREATED_PENDING_BACKUP');
      });

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
        address: validSolanaAddress,
        isDefault: true,
      })
      .expect(201);

    await request(app.getHttpServer())
      .get('/api/client/v1/wallet/public-addresses')
      .set('authorization', `Bearer ${accessToken}`)
      .expect(200);

    await request(app.getHttpServer())
      .get('/api/client/v1/wallet/overview')
      .set('authorization', `Bearer ${accessToken}`)
      .expect(200)
      .expect((res) => {
        expect(res.body.data.chainItems).toEqual(
          expect.arrayContaining([
            expect.objectContaining({ networkCode: 'SOLANA' }),
            expect.objectContaining({ networkCode: 'TRON' }),
          ]),
        );
        expect(res.body.data.assetItems).toEqual(
          expect.arrayContaining([
            expect.objectContaining({ networkCode: 'SOLANA', assetCode: 'SOL' }),
            expect.objectContaining({ networkCode: 'TRON', assetCode: 'USDT' }),
          ]),
        );
      });

    await request(app.getHttpServer())
      .get('/api/client/v1/wallet/receive-context?networkCode=SOLANA&assetCode=USDT')
      .set('authorization', `Bearer ${accessToken}`)
      .expect(200)
      .expect((res) => {
        expect(res.body.data.selectedNetworkCode).toBe('SOLANA');
        expect(res.body.data.selectedAssetCode).toBe('USDT');
        expect(res.body.data.walletExists).toBe(true);
        expect(res.body.data.receiveState).toBe('READY');
        expect(res.body.data.status).toBe('已配置收款地址');
        expect(res.body.data.defaultAddress).toBe(
          validSolanaAddress,
        );
      });

    await request(app.getHttpServer())
      .get('/api/client/v1/referral/share-context')
      .set('authorization', `Bearer ${accessToken}`)
      .expect(200)
      .expect((res) => {
        expect(res.body.data.referralCode).toBeDefined();
        expect(res.body.data.shareLink).toContain(res.body.data.referralCode);
      });

    const shareContext = await request(app.getHttpServer())
      .get('/api/client/v1/referral/share-context')
      .set('authorization', `Bearer ${accessToken}`)
      .expect(200);

    await request(app.getHttpServer())
      .get(
        `/api/client/v1/referral/resolve-public?code=${encodeURIComponent(
          shareContext.body.data.referralCode,
        )}`,
      )
      .expect(200)
      .expect((res) => {
        expect(res.body.data.referralCode).toBe(shareContext.body.data.referralCode);
        expect(res.body.data.inviterLabel).toContain('@');
        expect(res.body.data.downloadUrl).not.toBeUndefined();
        expect(res.body.data.openAppUrl).not.toBeUndefined();
      });

    await app.close();
    await bootstrapApp();

    await request(app.getHttpServer())
      .get('/api/client/v1/wallet/public-addresses')
      .set('authorization', `Bearer ${accessToken}`)
      .expect(200)
      .expect((res) => {
        expect(res.body.data.items).toEqual(
          expect.arrayContaining([
            expect.objectContaining({
              networkCode: 'SOLANA',
              assetCode: 'USDT',
              address: validSolanaAddress,
              isDefault: true,
            }),
          ]),
        );
      });

    await request(app.getHttpServer())
      .get('/api/client/v1/wallet/receive-context?networkCode=SOLANA&assetCode=USDT')
      .set('authorization', `Bearer ${accessToken}`)
      .expect(200)
      .expect((res) => {
        expect(res.body.data.defaultAddress).toBe(
          validSolanaAddress,
        );
        expect(res.body.data.receiveState).toBe('READY');
      });

    await request(app.getHttpServer())
      .post('/api/client/v1/wallet/transfer/precheck')
      .set('authorization', `Bearer ${accessToken}`)
      .send({
        networkCode: 'SOLANA',
        assetCode: 'USDT',
        toAddress: validSolanaAddress,
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

  it('supports wallet list, watch-only wallets, and explicit payer-wallet binding', async () => {
    const createWallet = await request(app.getHttpServer())
      .post('/api/client/v1/wallets/create-mnemonic')
      .set('authorization', `Bearer ${accessToken}`)
      .send({
        walletName: 'Main Wallet',
        keySlots: [
          {
            slotCode: 'SOLANA_0',
            chainFamily: 'SOLANA',
            derivationType: 'MNEMONIC',
            derivationPath: "m/44'/501'/0'/0'",
          },
          {
            slotCode: 'TRON_0',
            chainFamily: 'TRON',
            derivationType: 'MNEMONIC',
            derivationPath: "m/44'/195'/0'/0/0",
          },
        ],
        chainAccounts: [
          {
            slotCode: 'SOLANA_0',
            chainFamily: 'SOLANA',
            networkCode: 'SOLANA',
            address: validSolanaAddress,
            isEnabled: true,
            isDefaultReceive: true,
          },
          {
            slotCode: 'TRON_0',
            chainFamily: 'TRON',
            networkCode: 'TRON',
            address: validTronAddress,
            isEnabled: true,
            isDefaultReceive: false,
          },
        ],
      })
      .expect(201);

    const secondWallet = await request(app.getHttpServer())
      .post('/api/client/v1/wallets/create-mnemonic')
      .set('authorization', `Bearer ${accessToken}`)
      .send({
        walletName: 'Second Wallet',
        keySlots: [
          {
            slotCode: 'SOLANA_0',
            chainFamily: 'SOLANA',
            derivationType: 'MNEMONIC',
            derivationPath: "m/44'/501'/1'/0'",
          },
          {
            slotCode: 'TRON_0',
            chainFamily: 'TRON',
            derivationType: 'MNEMONIC',
            derivationPath: "m/44'/195'/1'/0/0",
          },
        ],
        chainAccounts: [
          {
            slotCode: 'SOLANA_0',
            chainFamily: 'SOLANA',
            networkCode: 'SOLANA',
            address: '7YttLkHDo1B4ezgm6KPDLJrVN6a8GN28AL5soMgqd7qW',
            isEnabled: true,
            isDefaultReceive: true,
          },
          {
            slotCode: 'TRON_0',
            chainFamily: 'TRON',
            networkCode: 'TRON',
            address: 'TQn9Y2khEsLJW1ChVWFMSMeRDow5KcbLSE',
            isEnabled: true,
            isDefaultReceive: false,
          },
        ],
      })
      .expect(201);

    const watchWallet = await request(app.getHttpServer())
      .post('/api/client/v1/wallets/import/watch-only')
      .set('authorization', `Bearer ${accessToken}`)
      .send({
        walletName: 'Watch Wallet',
        chainFamily: 'TRON',
        networkCode: 'TRON',
        address: validTronAddress,
      })
      .expect(201);

    const listResponse = await request(app.getHttpServer())
      .get('/api/client/v1/wallets')
      .set('authorization', `Bearer ${accessToken}`)
      .expect(200);

    expect(listResponse.body.data.items).toEqual(
      expect.arrayContaining([
        expect.objectContaining({
          walletId: createWallet.body.data.wallet.walletId,
          walletKind: 'SELF_CUSTODY',
          isDefault: true,
        }),
        expect.objectContaining({
          walletId: secondWallet.body.data.wallet.walletId,
          walletKind: 'SELF_CUSTODY',
          isDefault: false,
        }),
        expect.objectContaining({
          walletId: watchWallet.body.data.wallet.walletId,
          walletKind: 'WATCH_ONLY',
          isDefault: false,
        }),
      ]),
    );
    expect(
      listResponse.body.data.items.filter(
        (item: { walletKind: string }) => item.walletKind === 'SELF_CUSTODY',
      ),
    ).toHaveLength(2);

    const watchDetail = await request(app.getHttpServer())
      .get(`/api/client/v1/wallets/${watchWallet.body.data.wallet.walletId}/chain-accounts`)
      .set('authorization', `Bearer ${accessToken}`)
      .expect(200);

    expect(watchDetail.body.data.items).toEqual([
      expect.objectContaining({
        walletId: watchWallet.body.data.wallet.walletId,
        capability: 'WATCH_ONLY',
        networkCode: 'TRON',
        address: validTronAddress,
      }),
    ]);

    await request(app.getHttpServer())
      .post(`/api/client/v1/wallets/${watchWallet.body.data.wallet.walletId}/set-default`)
      .set('authorization', `Bearer ${accessToken}`)
      .expect(201);

    const orderResponse = await request(app.getHttpServer())
      .post('/api/client/v1/orders')
      .set('authorization', `Bearer ${accessToken}`)
      .set('x-idempotency-key', `wallet-order-${Date.now()}`)
      .send({
        planCode: 'BASIC_1M',
        orderType: 'NEW',
        quoteAssetCode: 'USDT',
        quoteNetworkCode: 'TRON',
        payerWalletId: createWallet.body.data.wallet.walletId,
        payerChainAccountId: createWallet.body.data.chainAccounts[1].chainAccountId,
      });

    expect(orderResponse.status).toBe(201);
    expect(orderResponse.body.data.payerWalletId).toBe(
      createWallet.body.data.wallet.walletId,
    );
    expect(orderResponse.body.data.payerChainAccountId).toBe(
      createWallet.body.data.chainAccounts[1].chainAccountId,
    );

    await request(app.getHttpServer())
      .post(`/api/client/v1/orders/${orderResponse.body.data.orderNo}/submit-client-tx`)
      .set('authorization', `Bearer ${accessToken}`)
      .send({
        txHash: 'tron-tx-watch-only',
        networkCode: 'TRON',
        payerWalletId: watchWallet.body.data.wallet.walletId,
        payerChainAccountId: watchDetail.body.data.items[0].chainAccountId,
        submittedFromAddress: validTronAddress,
      })
      .expect(409)
      .expect((res) => {
        expect(res.body.code).toBe('ORDER_PAYER_WATCH_ONLY');
      });

    await request(app.getHttpServer())
      .post(`/api/client/v1/orders/${orderResponse.body.data.orderNo}/submit-client-tx`)
      .set('authorization', `Bearer ${accessToken}`)
      .send({
        txHash: 'tron-tx-2',
        networkCode: 'TRON',
        payerWalletId: createWallet.body.data.wallet.walletId,
        payerChainAccountId: createWallet.body.data.chainAccounts[1].chainAccountId,
        submittedFromAddress: validTronAddress,
      })
      .expect(201);
  });

  it('clears wallet domain records while preserving encrypted backup metadata', async () => {
    const mnemonic = Array(12).fill('abandon').join(' ');
    const mnemonicHash = createHash('sha256').update(mnemonic).digest('hex');

    await request(app.getHttpServer())
      .post('/api/client/v1/wallet/lifecycle')
      .set('authorization', `Bearer ${accessToken}`)
      .send({
        action: 'CREATE',
        displayName: 'Legacy Wallet',
      })
      .expect(201);

    await request(app.getHttpServer())
      .post('/api/client/v1/wallet/public-addresses')
      .set('authorization', `Bearer ${accessToken}`)
      .send({
        networkCode: 'SOLANA',
        assetCode: 'USDT',
        address: validSolanaAddress,
        isDefault: true,
      })
      .expect(201);

    await request(app.getHttpServer())
      .post('/api/client/v1/wallet/secret-backups')
      .set('authorization', `Bearer ${accessToken}`)
      .send({
        secretType: 'MNEMONIC',
        mnemonic,
        mnemonicHash,
        mnemonicWordCount: 12,
        walletName: 'Legacy Wallet',
        sourceType: 'CREATE',
        publicAddresses: [
          {
            networkCode: 'SOLANA',
            assetCode: 'USDT',
            address: validSolanaAddress,
            isDefault: true,
          },
        ],
      })
      .expect(201);

    await request(app.getHttpServer())
      .post('/api/client/v1/wallets/create-mnemonic')
      .set('authorization', `Bearer ${accessToken}`)
      .send({
        walletName: 'Main Wallet',
        keySlots: [
          {
            slotCode: 'SOLANA_0',
            chainFamily: 'SOLANA',
            derivationType: 'MNEMONIC',
            derivationPath: "m/44'/501'/0'/0'",
          },
        ],
        chainAccounts: [
          {
            slotCode: 'SOLANA_0',
            chainFamily: 'SOLANA',
            networkCode: 'SOLANA',
            address: validSolanaAddress,
            isEnabled: true,
            isDefaultReceive: true,
          },
        ],
      })
      .expect(201);

    await request(app.getHttpServer())
      .post('/api/client/v1/wallets/reset')
      .set('authorization', `Bearer ${accessToken}`)
      .expect(201)
      .expect((res) => {
        expect(res.body.data.clearedWalletCount).toBe(1);
        expect(res.body.data.clearedPublicAddressCount).toBeGreaterThanOrEqual(1);
        expect(res.body.data.clearedLegacyLifecycle).toBe(1);
        expect(res.body.data.retainedBackup).toBe(true);
      });

    await request(app.getHttpServer())
      .get('/api/client/v1/wallets')
      .set('authorization', `Bearer ${accessToken}`)
      .expect(200)
      .expect((res) => {
        expect(res.body.data.items).toEqual([]);
      });

    await request(app.getHttpServer())
      .get('/api/client/v1/wallet/lifecycle')
      .set('authorization', `Bearer ${accessToken}`)
      .expect(200)
      .expect((res) => {
        expect(res.body.data.walletExists).toBe(false);
        expect(res.body.data.receiveState).toBe('NO_WALLET');
      });

    await request(app.getHttpServer())
      .get('/api/client/v1/wallet/public-addresses')
      .set('authorization', `Bearer ${accessToken}`)
      .expect(200)
      .expect((res) => {
        expect(res.body.data.items).toEqual([]);
      });

    await request(app.getHttpServer())
      .get('/api/client/v1/wallet/secret-backups')
      .set('authorization', `Bearer ${accessToken}`)
      .expect(200)
      .expect((res) => {
        expect(res.body.data.exists).toBe(true);
        expect(res.body.data.walletId).toBeDefined();
      });

    await request(app.getHttpServer())
      .get('/api/client/v1/wallet/secret-backups/export')
      .set('authorization', `Bearer ${accessToken}`)
      .expect(200)
      .expect((res) => {
        expect(res.body.data.exists).toBe(true);
        expect(res.body.data.payload.ciphertext).toBeDefined();
      });
  });

  it('rejects known placeholder wallet public addresses', async () => {
    await request(app.getHttpServer())
      .post('/api/client/v1/wallet/lifecycle')
      .set('authorization', `Bearer ${accessToken}`)
      .send({
        action: 'CREATE',
        displayName: 'Reject Placeholder Wallet',
      })
      .expect(201);

    await request(app.getHttpServer())
      .post('/api/client/v1/wallet/public-addresses')
      .set('authorization', `Bearer ${accessToken}`)
      .send({
        networkCode: 'SOLANA',
        assetCode: 'USDT',
        address: placeholderSolanaAddress,
        isDefault: true,
      })
      .expect(400)
      .expect((res) => {
        const errorCode = res.body.error?.code ?? res.body.code;
        expect(errorCode).toBe('WALLET_PLACEHOLDER_ADDRESS_FORBIDDEN');
      });
  });

  it('wallet overview and receive-context stay consistent for persisted addresses and alias queries', async () => {
    const solanaAddress = validSolanaAddress;
    const tronAddress = validTronAddress;

    await request(app.getHttpServer())
      .post('/api/client/v1/wallet/lifecycle')
      .set('authorization', `Bearer ${accessToken}`)
      .send({
        action: 'CREATE',
        displayName: 'Consistency Wallet',
      })
      .expect(201);

    await request(app.getHttpServer())
      .post('/api/client/v1/wallet/public-addresses')
      .set('authorization', `Bearer ${accessToken}`)
      .send({
        networkCode: 'SOLANA',
        assetCode: 'USDT',
        address: solanaAddress,
        isDefault: true,
      })
      .expect(201);

    await request(app.getHttpServer())
      .post('/api/client/v1/wallet/public-addresses')
      .set('authorization', `Bearer ${accessToken}`)
      .send({
        networkCode: 'TRON',
        assetCode: 'USDT',
        address: tronAddress,
        isDefault: true,
      })
      .expect(201);

    await request(app.getHttpServer())
      .get('/api/client/v1/wallet/overview')
      .set('authorization', `Bearer ${accessToken}`)
      .expect(200)
      .expect((res) => {
        expect(res.body.data.receiveState).toBe('READY');
        expect(res.body.data.configuredAddressCount).toBe(2);
        expect(res.body.data.alerts).not.toContain('当前账号尚未配置任何服务端收款地址');
        expect(res.body.data.chainItems).toEqual(
          expect.arrayContaining([
            expect.objectContaining({
              networkCode: 'SOLANA',
              hasConfiguredAddress: true,
            }),
            expect.objectContaining({
              networkCode: 'TRON',
              hasConfiguredAddress: true,
            }),
          ]),
        );
      });

    await request(app.getHttpServer())
      .get('/api/client/v1/wallet/public-addresses?chainId=TRON&assetId=USDT')
      .set('authorization', `Bearer ${accessToken}`)
      .expect(200)
      .expect((res) => {
        expect(res.body.data.items).toHaveLength(1);
        expect(res.body.data.items[0]).toEqual(
          expect.objectContaining({
            networkCode: 'TRON',
            assetCode: 'USDT',
            address: tronAddress,
            isDefault: true,
          }),
        );
      });

    await request(app.getHttpServer())
      .get('/api/client/v1/wallet/receive-context?chainId=TRON&assetId=USDT')
      .set('authorization', `Bearer ${accessToken}`)
      .expect(200)
      .expect((res) => {
        expect(res.body.data.selectedNetworkCode).toBe('TRON');
        expect(res.body.data.selectedAssetCode).toBe('USDT');
        expect(res.body.data.defaultAddress).toBe(tronAddress);
        expect(res.body.data.receiveState).toBe('READY');
      });
  });

  it('wallet TRON flow uses the remote client when enabled', async () => {
    await app.close();
    process.env.TRON_SERVICE_ENABLED = 'true';

    await bootstrapApp({
      isEnabled: () => true,
      validateAddress: jest.fn().mockResolvedValue({
        address: 'TLa2f6VPqDgRE67v1736s7bJ8Ray5wYjU7',
        valid: true,
        type: 'tron',
      }),
      broadcastTransaction: jest.fn().mockResolvedValue({
        success: true,
        txHash: 'tron-remote-1',
        acceptedAt: '2026-04-07T00:00:00.000Z',
      }),
    });

    const tronEmail = `wallet-tron-${Date.now()}-${Math.random().toString(16).slice(2, 8)}@example.com`;
    await request(app.getHttpServer())
      .post('/api/client/v1/auth/register/email/request-code')
      .send({ email: tronEmail })
      .expect(200);

    const registerResponse = await request(app.getHttpServer())
      .post('/api/client/v1/auth/register/email')
      .set('x-idempotency-key', `wallet-tron-register-${Date.now()}`)
      .send({
        email: tronEmail,
        code: '123456',
        password: 'Passw0rd!',
      })
      .expect(200);

    accessToken = registerResponse.body.data.accessToken;

    await request(app.getHttpServer())
      .post('/api/client/v1/wallet/transfer/precheck')
      .set('authorization', `Bearer ${accessToken}`)
      .send({
        networkCode: 'TRON',
        assetCode: 'USDT',
        toAddress: 'TLa2f6VPqDgRE67v1736s7bJ8Ray5wYjU7',
        amount: '10.5',
      })
      .expect(201)
      .expect((res) => {
        expect(res.body.data.networkCode).toBe('TRON');
        expect(res.body.data.serviceEnabled).toBe(true);
      });

    await request(app.getHttpServer())
      .post('/api/client/v1/wallet/transfer/proxy-broadcast')
      .set('authorization', `Bearer ${accessToken}`)
      .send({
        networkCode: 'TRON',
        assetCode: 'USDT',
        signedPayload: 'deadbeef',
        toAddress: 'TLa2f6VPqDgRE67v1736s7bJ8Ray5wYjU7',
      })
      .expect(201)
      .expect((res) => {
        expect(res.body.data.networkCode).toBe('TRON');
        expect(res.body.data.serviceEnabled).toBe(true);
        expect(res.body.data.txHash).toBe('tron-remote-1');
      });
  });

  it('wallet TRON broadcast falls back when the remote client errors', async () => {
    await app.close();
    process.env.TRON_SERVICE_ENABLED = 'true';

    await bootstrapApp({
      isEnabled: () => true,
      validateAddress: jest.fn().mockResolvedValue({
        address: 'TLa2f6VPqDgRE67v1736s7bJ8Ray5wYjU7',
        valid: true,
        type: 'tron',
      }),
      broadcastTransaction: jest
        .fn()
        .mockRejectedValue(new Error('tron remote unavailable')),
    });

    const tronFallbackEmail = `wallet-tron-fallback-${Date.now()}-${Math.random().toString(16).slice(2, 8)}@example.com`;
    await request(app.getHttpServer())
      .post('/api/client/v1/auth/register/email/request-code')
      .send({ email: tronFallbackEmail })
      .expect(200);

    const registerResponse = await request(app.getHttpServer())
      .post('/api/client/v1/auth/register/email')
      .set('x-idempotency-key', `wallet-tron-register-${Date.now()}`)
      .send({
        email: tronFallbackEmail,
        code: '123456',
        password: 'Passw0rd!',
      })
      .expect(200);

    accessToken = registerResponse.body.data.accessToken;

    await request(app.getHttpServer())
      .post('/api/client/v1/wallet/transfer/proxy-broadcast')
      .set('authorization', `Bearer ${accessToken}`)
      .send({
        networkCode: 'TRON',
        assetCode: 'USDT',
        signedPayload: 'deadbeef',
        clientTxHash: 'tron-fallback-client-hash',
        toAddress: 'TLa2f6VPqDgRE67v1736s7bJ8Ray5wYjU7',
      })
      .expect(201)
      .expect((res) => {
        expect(res.body.data.networkCode).toBe('TRON');
        expect(res.body.data.serviceEnabled).toBe(false);
        expect(res.body.data.txHash).toBe('tron-fallback-client-hash');
      });
  });
});
