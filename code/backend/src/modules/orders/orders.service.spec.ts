import { OrdersService } from './orders.service';

function buildOrder(overrides: Record<string, unknown> = {}) {
  return {
    createdAt: '2026-04-23T00:00:00.000Z',
    idempotencyKey: 'idempotency-key',
    collectionAddress: 'TR7NhqjeKQxGTCi8q8ZY4pL8otSzgjLj6t',
    orderId: 'order-1',
    orderNo: 'ORD-1',
    accountId: 'acc-1',
    payerWalletId: 'wallet-1',
    payerChainAccountId: 'chain-1',
    submittedFromAddress: 'TLa2f6VPqDgRE67v1736s7bJ8Ray5wYjU7',
    planCode: 'BASIC_1M',
    planName: '基础套餐-1个月',
    productTier: 'BASIC',
    termMonths: 1,
    selectedRegionCode: 'HK_BASIC',
    orderType: 'NEW',
    quoteAssetCode: 'USDT',
    quoteNetworkCode: 'TRON',
    quoteUsdAmount: '3.00',
    baseAmount: '3.000000',
    uniqueAmountDelta: '0.000000',
    payableAmount: '3.000000',
    status: 'PAYMENT_DETECTED',
    expiresAt: '2026-04-23T00:15:00.000Z',
    confirmedAt: null,
    completedAt: null,
    failureReason: null,
    submittedClientTxHash: 'tron-good-tx',
    matchedOnchainTxHash: null,
    paymentMatchedAt: null,
    matcherRemark: null,
    ...overrides,
  } as any;
}

describe('OrdersService', () => {
  const runtimeStateRepository = {
    saveOrder: jest.fn(),
  };
  const tronClient = {
    verifyIncomingTransfer: jest.fn(),
  };

  const service = new OrdersService(
    {} as never,
    {} as never,
    {} as never,
    runtimeStateRepository as never,
    {} as never,
    {} as never,
    {} as never,
    tronClient as never,
  );

  beforeEach(() => {
    jest.clearAllMocks();
    jest.spyOn(service as any, 'toOrderRecord').mockImplementation((order: any) => order);
    jest.spyOn(service as any, 'resolveQuoteAssetDefinition').mockReturnValue({
      isNative: false,
      contractAddress: 'TR7NHqjeKQxGTCi8q8ZY4pL8otSzgjLj6t',
      decimals: 6,
    });
  });

  it('marks TRON order underpaid when verification reports amount under', async () => {
    const order = buildOrder({
      submittedClientTxHash: 'tron-underpaid-tx',
    });
    jest.spyOn(service as any, 'mustGetOwned').mockResolvedValue(order);
    tronClient.verifyIncomingTransfer.mockResolvedValue({
      signature: 'tron-underpaid-tx',
      status: 'confirmed',
      confirmations: 3,
      verified: false,
      mismatchCode: 'AMOUNT_UNDER',
      failureReason: 'Submitted transaction amount is below the expected payment target',
    });

    const result = await service.refreshStatus('token', 'ORD-1', {} as never);

    expect(result.status).toBe('UNDERPAID_REVIEW');
    expect(result.failureReason).toBe(
      'Submitted transaction amount is below the expected payment target',
    );
    expect(runtimeStateRepository.saveOrder).toHaveBeenCalledWith(
      expect.objectContaining({
        status: 'UNDERPAID_REVIEW',
      }),
    );
  });

  it('marks TRON order failed when verification reports failed transaction', async () => {
    const order = buildOrder({
      submittedClientTxHash: 'tron-failed-tx',
    });
    jest.spyOn(service as any, 'mustGetOwned').mockResolvedValue(order);
    tronClient.verifyIncomingTransfer.mockResolvedValue({
      signature: 'tron-failed-tx',
      status: 'failed',
      confirmations: 0,
      verified: false,
      error: 'Transaction failed on chain',
    });

    const result = await service.refreshStatus('token', 'ORD-1', {} as never);

    expect(result.status).toBe('FAILED');
    expect(result.failureReason).toBe('Transaction failed on chain');
    expect(runtimeStateRepository.saveOrder).toHaveBeenCalledWith(
      expect.objectContaining({
        status: 'FAILED',
      }),
    );
  });

  it('provisions TRON order when verification succeeds', async () => {
    const order = buildOrder({
      submittedClientTxHash: 'tron-good-tx',
    });
    jest.spyOn(service as any, 'mustGetOwned').mockResolvedValue(order);
    const markedOrder = {
      ...order,
      status: 'COMPLETED',
      completedAt: '2026-04-23T00:05:00.000Z',
    };
    jest
      .spyOn(service as any, 'markPaidAndProvision')
      .mockResolvedValue(markedOrder);
    tronClient.verifyIncomingTransfer.mockResolvedValue({
      signature: 'tron-good-tx',
      status: 'confirmed',
      confirmations: 12,
      verified: true,
      recipientAddress: 'TR7NhqjeKQxGTCi8q8ZY4pL8otSzgjLj6t',
      assetCode: 'USDT',
      amount: '3.000000',
    });

    const result = await service.refreshStatus('token', 'ORD-1', {} as never);

    expect(result.status).toBe('COMPLETED');
    expect((service as any).markPaidAndProvision).toHaveBeenCalledWith(
      expect.objectContaining({
        matchedOnchainTxHash: 'tron-good-tx',
      }),
    );
  });
});
