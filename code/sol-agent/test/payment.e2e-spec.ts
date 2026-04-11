import { Test, TestingModule } from '@nestjs/testing';
import { INestApplication, ValidationPipe } from '@nestjs/common';
import { Keypair } from '@solana/web3.js';
import * as request from 'supertest';
import { AppModule } from '../src/app.module';
import { ResponseEnvelopeInterceptor } from '../src/common/interceptors/response-envelope.interceptor';
import { AllExceptionsFilter } from '../src/common/filters/all-exceptions.filter';
import { SolanaRpcService } from '../src/modules/solana/solana.rpc.service';

const VALID_SOLANA_ADDRESS = '11111111111111111111111111111111';
const MAINNET_USDT_MINT = 'Es9vMFrzaCERmJfrF4H2FYD4KCoNkY11McCe8BenwNYB';

describe('PaymentController (e2e)', () => {
  let app: INestApplication;
  let solanaRpc: SolanaRpcService;
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

    solanaRpc = app.get(SolanaRpcService);
  }, 30000);

  afterEach(() => {
    jest.restoreAllMocks();
  });

  afterAll(async () => {
    await app.close();
  }, 10000);

  it('GET /api/internal/v1/payment/:address/status - 无鉴权应返回 401', () => {
    return request(app.getHttpServer())
      .get(`/api/internal/v1/payment/${VALID_SOLANA_ADDRESS}/status`)
      .expect(401);
  });

  it('GET /api/internal/v1/payment/:address/status - 有鉴权应返回状态', async () => {
    jest.spyOn(solanaRpc, 'getBalance').mockResolvedValue({
      address: VALID_SOLANA_ADDRESS,
      balance: 1_000_000_000,
      balanceInSOL: '1.000000000',
      networkCode: 'solana-mainnet',
    });
    jest.spyOn(solanaRpc, 'getRecentTransactions').mockResolvedValue({
      address: VALID_SOLANA_ADDRESS,
      networkCode: 'solana-mainnet',
      signatures: ['status-signature-1'],
    });

    const response = await request(app.getHttpServer())
      .get(`/api/internal/v1/payment/${VALID_SOLANA_ADDRESS}/status`)
      .set('X-Internal-Auth', `Bearer ${token}`)
      .expect(200);

    expect(response.body.code).toBe('OK');
    expect(response.body.data.address).toBe(VALID_SOLANA_ADDRESS);
    expect(response.body.data.status).toBe('received');
    expect(response.body.data.txHash).toBe('status-signature-1');
    expect(response.body.data.networkCode).toBe('solana-mainnet');
    expect(response.body.data.receivedAmount).toBe('1.000000000');
    expect(response.body.data.updatedAt).toBeDefined();
  }, 30000);

  it('POST /api/internal/v1/payment/detect - 有鉴权应返回检测结果', async () => {
    jest.spyOn(solanaRpc, 'getBalance').mockResolvedValue({
      address: VALID_SOLANA_ADDRESS,
      balance: 1_250_000_000,
      balanceInSOL: '1.250000000',
      networkCode: 'solana-mainnet',
    });
    jest.spyOn(solanaRpc, 'getRecentTransactions').mockResolvedValue({
      address: VALID_SOLANA_ADDRESS,
      networkCode: 'solana-mainnet',
      signatures: ['detect-signature-1', 'detect-signature-2'],
    });

    const response = await request(app.getHttpServer())
      .post('/api/internal/v1/payment/detect')
      .set('X-Internal-Auth', `Bearer ${token}`)
      .send({ address: VALID_SOLANA_ADDRESS, expectedAmount: '1.0' })
      .expect(200);

    expect(response.body.code).toBe('OK');
    expect(response.body.data.address).toBe(VALID_SOLANA_ADDRESS);
    expect(response.body.data.status).toBe('confirmed');
    expect(response.body.data.expectedAmount).toBe('1.0');
    expect(response.body.data.receivedAmount).toBe('1.250000000');
    expect(response.body.data.recentTransactions).toEqual([
      'detect-signature-1',
      'detect-signature-2',
    ]);
  }, 30000);

  it('POST /api/internal/v1/payment/detect - RPC 错误时返回 error 状态', async () => {
    jest
      .spyOn(solanaRpc, 'getBalance')
      .mockRejectedValue(new Error('invalid address'));

    const response = await request(app.getHttpServer())
      .post('/api/internal/v1/payment/detect')
      .set('X-Internal-Auth', `Bearer ${token}`)
      .send({ address: 'invalid_address', expectedAmount: '1.0' })
      .expect(200);

    expect(response.body.code).toBe('OK');
    expect(response.body.data.address).toBe('invalid_address');
    expect(response.body.data.status).toBe('error');
    expect(response.body.data.error).toContain('invalid address');
  }, 30000);

  it('POST /api/internal/v1/payment/scan-incoming - 返回标准化共享地址入账结果', async () => {
    const recipient = Keypair.generate().publicKey.toBase58();
    const otherAddress = Keypair.generate().publicKey.toBase58();

    jest.spyOn(solanaRpc, 'getSignatureInfos').mockResolvedValue({
      address: recipient,
      networkCode: 'solana-mainnet',
      signatures: [
        {
          signature: 'scan-sol-signature',
          slot: 42345,
          err: null,
          memo: null,
          blockTime: 1712800300,
          confirmationStatus: 'confirmed',
        },
        {
          signature: 'scan-ignored-signature',
          slot: 42346,
          err: null,
          memo: null,
          blockTime: 1712800301,
          confirmationStatus: 'confirmed',
        },
      ],
    });
    jest.spyOn(solanaRpc, 'getParsedTransactions').mockResolvedValue({
      networkCode: 'solana-mainnet',
      transactions: [
        {
          signature: 'scan-sol-signature',
          transaction: {
            slot: 42345,
            blockTime: 1712800300,
            meta: {
              err: null,
              preBalances: [1_500_000_000, 250_000_000],
              postBalances: [750_000_000, 1_000_000_000],
              preTokenBalances: [],
              postTokenBalances: [],
            },
            transaction: {
              message: {
                accountKeys: [{ pubkey: otherAddress }, { pubkey: recipient }],
              },
            },
          } as never,
        },
        {
          signature: 'scan-ignored-signature',
          transaction: {
            slot: 42346,
            blockTime: 1712800301,
            meta: {
              err: null,
              preBalances: [500_000_000, 100_000_000],
              postBalances: [450_000_000, 100_000_000],
              preTokenBalances: [],
              postTokenBalances: [],
            },
            transaction: {
              message: {
                accountKeys: [{ pubkey: otherAddress }, { pubkey: recipient }],
              },
            },
          } as never,
        },
      ],
    });

    const response = await request(app.getHttpServer())
      .post('/api/internal/v1/payment/scan-incoming')
      .set('X-Internal-Auth', `Bearer ${token}`)
      .send({
        collectionAddress: recipient,
        assetCode: 'SOL',
        limit: 10,
      })
      .expect(200);

    expect(response.body.code).toBe('OK');
    expect(response.body.data.collectionAddress).toBe(recipient);
    expect(response.body.data.assetCode).toBe('SOL');
    expect(response.body.data.assetKind).toBe('NATIVE_SOL');
    expect(response.body.data.scannedSignatures).toBe(2);
    expect(response.body.data.matchedTransfers).toBe(1);
    expect(response.body.data.nextBeforeSignature).toBe(
      'scan-ignored-signature',
    );
    expect(response.body.data.items).toEqual([
      expect.objectContaining({
        signature: 'scan-sol-signature',
        amount: '0.75',
        amountRaw: '750000000',
        matchedAccounts: [recipient],
        confirmationStatus: 'confirmed',
      }),
    ]);
  }, 30000);

  it('POST /api/internal/v1/payment/scan-incoming - USDT 扫描覆盖 owner+ATA 并按 signature 去重', async () => {
    const recipientOwner = Keypair.generate().publicKey.toBase58();
    const sourceTokenAccount = Keypair.generate().publicKey.toBase58();
    const ataAddress = solanaRpc.deriveAssociatedTokenAddress(
      recipientOwner,
      MAINNET_USDT_MINT,
    );

    jest.spyOn(solanaRpc, 'getSignatureInfos').mockImplementation(
      async (address: string) => {
        if (address === recipientOwner) {
          return {
            address,
            networkCode: 'solana-mainnet',
            signatures: [
              {
                signature: 'shared-signature',
                slot: 52345,
                err: null,
                memo: null,
                blockTime: 1712800400,
                confirmationStatus: 'confirmed',
              },
            ],
          };
        }

        if (address === ataAddress) {
          return {
            address,
            networkCode: 'solana-mainnet',
            signatures: [
              {
                signature: 'ata-only-signature',
                slot: 52344,
                err: null,
                memo: null,
                blockTime: 1712800399,
                confirmationStatus: 'confirmed',
              },
              {
                signature: 'shared-signature',
                slot: 52345,
                err: null,
                memo: null,
                blockTime: 1712800400,
                confirmationStatus: 'confirmed',
              },
            ],
          };
        }

        return {
          address,
          networkCode: 'solana-mainnet',
          signatures: [],
        };
      },
    );

    jest.spyOn(solanaRpc, 'getParsedTransactions').mockResolvedValue({
      networkCode: 'solana-mainnet',
      transactions: [
        {
          signature: 'shared-signature',
          transaction: {
            slot: 52345,
            blockTime: 1712800400,
            meta: {
              err: null,
              preBalances: [0, 0],
              postBalances: [0, 0],
              preTokenBalances: [],
              postTokenBalances: [],
            },
            transaction: {
              message: {
                accountKeys: [{ pubkey: sourceTokenAccount }, { pubkey: ataAddress }],
              },
            },
          } as never,
        },
        {
          signature: 'ata-only-signature',
          transaction: {
            slot: 52344,
            blockTime: 1712800399,
            meta: {
              err: null,
              preBalances: [0, 0],
              postBalances: [0, 0],
              preTokenBalances: [
                {
                  accountIndex: 1,
                  mint: MAINNET_USDT_MINT,
                  owner: recipientOwner,
                  uiTokenAmount: {
                    amount: '1000000',
                    decimals: 6,
                  },
                },
              ],
              postTokenBalances: [
                {
                  accountIndex: 1,
                  mint: MAINNET_USDT_MINT,
                  owner: recipientOwner,
                  uiTokenAmount: {
                    amount: '2500000',
                    decimals: 6,
                  },
                },
              ],
            },
            transaction: {
              message: {
                accountKeys: [{ pubkey: sourceTokenAccount }, { pubkey: ataAddress }],
              },
            },
          } as never,
        },
      ],
    });

    const response = await request(app.getHttpServer())
      .post('/api/internal/v1/payment/scan-incoming')
      .set('X-Internal-Auth', `Bearer ${token}`)
      .send({
        collectionAddress: recipientOwner,
        assetCode: 'USDT',
        limit: 10,
      })
      .expect(200);

    expect(response.body.code).toBe('OK');
    expect(response.body.data.collectionAddress).toBe(recipientOwner);
    expect(response.body.data.assetCode).toBe('USDT');
    expect(response.body.data.assetKind).toBe('SPL_TOKEN');
    expect(response.body.data.mintAddress).toBe(MAINNET_USDT_MINT);
    expect(response.body.data.scannedSignatures).toBe(2);
    expect(response.body.data.matchedTransfers).toBe(1);
    expect(response.body.data.items).toEqual([
      expect.objectContaining({
        signature: 'ata-only-signature',
        amount: '1.5',
        amountRaw: '1500000',
        matchedAccounts: [ataAddress],
      }),
    ]);
  }, 30000);

  it('POST /api/internal/v1/payment/verify - 可校验原生 SOL 到账', async () => {
    const sender = Keypair.generate().publicKey.toBase58();
    const recipient = Keypair.generate().publicKey.toBase58();

    jest.spyOn(solanaRpc, 'getParsedTransaction').mockResolvedValue({
      signature: 'sol-signature',
      networkCode: 'solana-mainnet',
      transaction: {
        slot: 12345,
        blockTime: 1712800000,
        meta: {
          err: null,
          preBalances: [2_000_000_000, 100_000_000],
          postBalances: [1_500_000_000, 600_000_000],
          preTokenBalances: [],
          postTokenBalances: [],
        },
        transaction: {
          message: {
            accountKeys: [{ pubkey: sender }, { pubkey: recipient }],
          },
        },
      } as never,
    });

    const response = await request(app.getHttpServer())
      .post('/api/internal/v1/payment/verify')
      .set('X-Internal-Auth', `Bearer ${token}`)
      .send({
        signature: 'sol-signature',
        recipientAddress: recipient,
        assetCode: 'SOL',
        expectedAmount: '0.5',
      })
      .expect(200);

    expect(response.body.code).toBe('OK');
    expect(response.body.data.status).toBe('verified');
    expect(response.body.data.assetKind).toBe('NATIVE_SOL');
    expect(response.body.data.receivedAmount).toBe('0.5');
    expect(response.body.data.recipientMatched).toBe(true);
    expect(response.body.data.amountSatisfied).toBe(true);
    expect(response.body.data.matchedAccounts).toEqual([recipient]);
  }, 30000);

  it('POST /api/internal/v1/payment/verify - 可校验 mainnet SPL USDT 到 owner 地址', async () => {
    const sourceTokenAccount = Keypair.generate().publicKey.toBase58();
    const recipientTokenAccount = Keypair.generate().publicKey.toBase58();
    const recipientOwner = Keypair.generate().publicKey.toBase58();

    jest.spyOn(solanaRpc, 'getParsedTransaction').mockResolvedValue({
      signature: 'usdt-signature',
      networkCode: 'solana-mainnet',
      transaction: {
        slot: 22345,
        blockTime: 1712800100,
        meta: {
          err: null,
          preBalances: [0, 0],
          postBalances: [0, 0],
          preTokenBalances: [
            {
              accountIndex: 1,
              mint: MAINNET_USDT_MINT,
              owner: recipientOwner,
              uiTokenAmount: {
                amount: '1000000',
                decimals: 6,
              },
            },
          ],
          postTokenBalances: [
            {
              accountIndex: 1,
              mint: MAINNET_USDT_MINT,
              owner: recipientOwner,
              uiTokenAmount: {
                amount: '2250000',
                decimals: 6,
              },
            },
          ],
        },
        transaction: {
          message: {
            accountKeys: [
              { pubkey: sourceTokenAccount },
              { pubkey: recipientTokenAccount },
            ],
          },
        },
      } as never,
    });

    const response = await request(app.getHttpServer())
      .post('/api/internal/v1/payment/verify')
      .set('X-Internal-Auth', `Bearer ${token}`)
      .send({
        signature: 'usdt-signature',
        recipientAddress: recipientOwner,
        assetCode: 'USDT',
        expectedAmount: '1.25',
      })
      .expect(200);

    expect(response.body.code).toBe('OK');
    expect(response.body.data.status).toBe('verified');
    expect(response.body.data.assetKind).toBe('SPL_TOKEN');
    expect(response.body.data.mintAddress).toBe(MAINNET_USDT_MINT);
    expect(response.body.data.receivedAmount).toBe('1.25');
    expect(response.body.data.amountSatisfied).toBe(true);
    expect(response.body.data.matchedAccounts).toEqual([recipientTokenAccount]);
  }, 30000);

  it('POST /api/internal/v1/payment/verify - 金额不足时返回 mismatch', async () => {
    const sender = Keypair.generate().publicKey.toBase58();
    const recipient = Keypair.generate().publicKey.toBase58();

    jest.spyOn(solanaRpc, 'getParsedTransaction').mockResolvedValue({
      signature: 'mismatch-signature',
      networkCode: 'solana-mainnet',
      transaction: {
        slot: 32345,
        blockTime: 1712800200,
        meta: {
          err: null,
          preBalances: [1_000_000_000, 0],
          postBalances: [850_000_000, 150_000_000],
          preTokenBalances: [],
          postTokenBalances: [],
        },
        transaction: {
          message: {
            accountKeys: [{ pubkey: sender }, { pubkey: recipient }],
          },
        },
      } as never,
    });

    const response = await request(app.getHttpServer())
      .post('/api/internal/v1/payment/verify')
      .set('X-Internal-Auth', `Bearer ${token}`)
      .send({
        signature: 'mismatch-signature',
        recipientAddress: recipient,
        assetCode: 'SOL',
        expectedAmount: '0.2',
      })
      .expect(200);

    expect(response.body.code).toBe('OK');
    expect(response.body.data.status).toBe('mismatch');
    expect(response.body.data.recipientMatched).toBe(true);
    expect(response.body.data.amountSatisfied).toBe(false);
    expect(response.body.data.receivedAmount).toBe('0.15');
  }, 30000);
});
