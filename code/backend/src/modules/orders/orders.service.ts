import {
  ConflictException,
  Injectable,
  Logger,
  NotFoundException,
  ServiceUnavailableException,
} from '@nestjs/common';
import { randomUUID } from 'crypto';
import { ConfigService } from '@nestjs/config';
import { AuthService } from '../auth/auth.service';
import { ClientCatalogService } from '../database/client-catalog.service';
import { RuntimeStateRepository } from '../database/runtime-state.repository';
import { StoredOrderRecord } from '../database/runtime-state.types';
import { MarketService } from '../market/market.service';
import { resolvePaymentAsset } from '../payments/payment-asset-catalog';
import { ProvisioningService } from '../provisioning/provisioning.service';
import { SolanaClientService } from '../solana-client/solana-client.service';
import { VerifyIncomingTransferResponse } from '../solana-client/solana-client.types';
import { CreateOrderRequestDto } from './dto/create-order.request';
import { RefreshOrderStatusRequestDto } from './dto/refresh-order-status.request';
import { SubmitClientTxRequestDto } from './dto/submit-client-tx.request';
import { OrderRecord } from './orders.types';

@Injectable()
export class OrdersService {
  private readonly logger = new Logger(OrdersService.name);

  constructor(
    private readonly configService: ConfigService,
    private readonly authService: AuthService,
    private readonly clientCatalogService: ClientCatalogService,
    private readonly runtimeStateRepository: RuntimeStateRepository,
    private readonly marketService: MarketService,
    private readonly provisioningService: ProvisioningService,
    private readonly solanaClient: SolanaClientService,
  ) {}

  async createOrder(
    accessToken: string,
    dto: CreateOrderRequestDto,
    idempotencyKey: string,
  ) {
    const account = this.authService.getMe(accessToken);
    const compositeKey = `${account.accountId}:${idempotencyKey}`;
    if (!idempotencyKey) {
      throw new ConflictException({
        code: 'ORDER_CREATE_CONFLICT',
        message: 'Missing idempotency key',
      });
    }

    const plan = await this.clientCatalogService.findPlanByCode(dto.planCode, {
      status: 'ACTIVE',
    });
    if (!plan) {
      throw new NotFoundException({
        code: 'PLAN_NOT_FOUND',
        message: 'Plan not found',
      });
    }
    const quoteAsset = this.resolveQuoteAssetDefinition(
      dto.quoteNetworkCode,
      dto.quoteAssetCode,
    );
    if (!quoteAsset?.orderPayable) {
      throw new NotFoundException({
        code: 'ORDER_ASSET_UNSUPPORTED',
        message: 'Payment asset is unavailable for ordering',
      });
    }
    if ((dto.payerWalletId && !dto.payerChainAccountId) || (!dto.payerWalletId && dto.payerChainAccountId)) {
      throw new ConflictException({
        code: 'ORDER_PAYER_WALLET_INVALID',
        message: 'payerWalletId and payerChainAccountId must be provided together',
      });
    }
    if (dto.payerWalletId && dto.payerChainAccountId) {
      await this.assertPayerWallet(account.accountId, dto.payerWalletId, dto.payerChainAccountId);
    }

    const orderId = randomUUID();
    const orderNo = `ORD-${Date.now()}-${orderId.slice(0, 8).toUpperCase()}`;
    const collectionAddress = this.resolveCollectionAddress(dto.quoteNetworkCode);
    const baseAmount = await this.resolveBaseAmount(quoteAsset, plan.priceUsd);
    const uniqueAmountDelta = await this.allocateUniqueAmountDelta({
      collectionAddress,
      quoteAssetCode: dto.quoteAssetCode,
      quoteNetworkCode: dto.quoteNetworkCode,
      baseAmount,
    });
    const payableAmount =
      dto.quoteNetworkCode === 'SOLANA'
        ? this.addDecimalAmounts(baseAmount, uniqueAmountDelta)
        : baseAmount;
    const storedOrder: StoredOrderRecord = {
      createdAt: new Date().toISOString(),
      idempotencyKey: compositeKey,
      collectionAddress,
      orderId,
      orderNo,
      accountId: account.accountId,
      payerWalletId: dto.payerWalletId ?? null,
      payerChainAccountId: dto.payerChainAccountId ?? null,
      submittedFromAddress: null,
      planCode: dto.planCode,
      planName: plan.name,
      orderType: dto.orderType,
      quoteAssetCode: dto.quoteAssetCode,
      quoteNetworkCode: dto.quoteNetworkCode,
      quoteUsdAmount: plan.priceUsd,
      baseAmount,
      uniqueAmountDelta,
      payableAmount,
      status: 'AWAITING_PAYMENT',
      expiresAt: new Date(Date.now() + 15 * 60 * 1000).toISOString(),
      confirmedAt: null,
      completedAt: null,
      failureReason: null,
      submittedClientTxHash: null,
      matchedOnchainTxHash: null,
      paymentMatchedAt: null,
      matcherRemark: null,
    };

    const order = await this.runtimeStateRepository.createOrder(
      storedOrder,
      compositeKey,
    );
    return this.toOrderRecord(order);
  }

  async getOrder(accessToken: string, orderNo: string) {
    const account = this.authService.getMe(accessToken);
    const order = await this.mustGet(orderNo);
    if (order.accountId !== account.accountId) {
      throw new NotFoundException({
        code: 'ORDER_NOT_FOUND',
        message: 'Order not found',
      });
    }
    return this.toOrderRecord(order);
  }

  async getPaymentTarget(accessToken: string, orderNo: string) {
    const order = await this.mustGetOwned(accessToken, orderNo);
    return {
      orderNo: order.orderNo,
      networkCode: order.quoteNetworkCode,
      assetCode: order.quoteAssetCode,
      collectionAddress: order.collectionAddress,
      baseAmount: order.baseAmount,
      payableAmount: order.payableAmount,
      uniqueAmountDelta: order.uniqueAmountDelta,
      qrText: this.buildPaymentQrText(order),
      expiresAt: order.expiresAt,
      serviceEnabled:
        order.quoteNetworkCode === 'SOLANA' ? this.solanaClient.isEnabled() : false,
    };
  }

  async submitClientTx(
    accessToken: string,
    orderNo: string,
    dto: SubmitClientTxRequestDto,
  ) {
    const order = await this.mustGetOwned(accessToken, orderNo);
    if (dto.networkCode !== order.quoteNetworkCode) {
      throw new ConflictException({
        code: 'ORDER_TX_NETWORK_MISMATCH',
        message: 'Submitted transaction network does not match order network',
      });
    }
    if (!dto.payerWalletId || !dto.payerChainAccountId || !dto.submittedFromAddress) {
      throw new ConflictException({
        code: 'ORDER_PAYER_WALLET_REQUIRED',
        message: 'Payer wallet binding is required for transaction submission',
      });
    }
    const chainAccount = await this.assertPayerWallet(
      order.accountId,
      dto.payerWalletId,
      dto.payerChainAccountId,
    );
    if (chainAccount.networkCode !== order.quoteNetworkCode) {
      throw new ConflictException({
        code: 'ORDER_PAYER_NETWORK_MISMATCH',
        message: 'Selected payer wallet network does not match order network',
      });
    }
    if (chainAccount.address !== dto.submittedFromAddress) {
      throw new ConflictException({
        code: 'ORDER_PAYER_ADDRESS_MISMATCH',
        message: 'Submitted fromAddress does not match selected payer wallet',
      });
    }
    if (chainAccount.capability !== 'SIGN_AND_PAY') {
      throw new ConflictException({
        code: 'ORDER_PAYER_WATCH_ONLY',
        message: 'Watch-only wallet cannot submit payments',
      });
    }
    order.submittedClientTxHash = dto.txHash;
    order.payerWalletId = dto.payerWalletId;
    order.payerChainAccountId = dto.payerChainAccountId;
    order.submittedFromAddress = dto.submittedFromAddress;
    if (!this.isTerminalStatus(order.status)) {
      order.status = 'PAYMENT_DETECTED';
      order.failureReason = null;
    }
    await this.runtimeStateRepository.saveOrder(order);
    return {};
  }

  async refreshStatus(
    accessToken: string,
    orderNo: string,
    _dto: RefreshOrderStatusRequestDto,
  ) {
    const order = await this.mustGetOwned(accessToken, orderNo);

    // Terminal states: no further progression
    if (
      order.status === 'COMPLETED' ||
      order.status === 'FAILED' ||
      order.status === 'EXPIRED' ||
      order.status === 'CANCELED'
    ) {
      return this.toOrderRecord(order);
    }

    if (new Date(order.expiresAt).getTime() <= Date.now() && order.status === 'AWAITING_PAYMENT') {
      order.status = 'EXPIRED';
      await this.runtimeStateRepository.saveOrder(order);
      return this.toOrderRecord(order);
    }

    if (!order.submittedClientTxHash) {
      return this.toOrderRecord(order);
    }

    if (order.quoteNetworkCode === 'SOLANA') {
      try {
        const quoteAsset = this.resolveQuoteAssetDefinition(
          order.quoteNetworkCode,
          order.quoteAssetCode,
        );
        const verification = await this.solanaClient.verifyIncomingTransfer({
          signature: order.submittedClientTxHash,
          recipientAddress: order.collectionAddress,
          assetCode: order.quoteAssetCode,
          mint: quoteAsset?.isNative ? null : quoteAsset?.contractAddress ?? null,
          expectedAmount: order.payableAmount,
        });

        if (
          verification.status === 'confirmed' ||
          verification.status === 'finalized'
        ) {
          if (verification.verified) {
            order.failureReason = null;
            return this.markPaidAndProvision(order);
          }

          order.status = this.mapVerificationFailureStatus(verification);
          order.failureReason =
            verification.failureReason ??
            'Submitted transaction does not satisfy the expected payment target';
          await this.runtimeStateRepository.saveOrder(order);
          return this.toOrderRecord(order);
        }

        if (verification.status === 'failed') {
          order.status = 'FAILED';
          order.failureReason =
            verification.error ?? 'Transaction failed on chain';
          await this.runtimeStateRepository.saveOrder(order);
          return this.toOrderRecord(order);
        }

        if (verification.status === 'pending') {
          if (order.status === 'PAYMENT_DETECTED') {
            order.status = 'CONFIRMING';
            await this.runtimeStateRepository.saveOrder(order);
          }
          return this.toOrderRecord(order);
        }

        return this.toOrderRecord(order);
      } catch (error) {
        this.logger.warn(
          `Solana chain-side status check failed for order ${orderNo}, preserving persisted status`,
          error instanceof Error ? error.message : error,
        );
        try {
          const txStatus = await this.solanaClient.getTransactionStatus({
            signature: order.submittedClientTxHash,
          });
          if (
            txStatus.status === 'confirmed' ||
            txStatus.status === 'finalized'
          ) {
            order.failureReason = null;
            order.matcherRemark =
              'Chain-side verify unavailable; promoted by confirmed submitted tx fallback';
            return this.markPaidAndProvision(order);
          }
        } catch (statusError) {
          this.logger.warn(
            `Solana submitted tx fallback status check failed for order ${orderNo}`,
            statusError instanceof Error ? statusError.message : statusError,
          );
        }
        return this.toOrderRecord(order);
      }
    }

    return this.toOrderRecord(order);
  }

  async getAwaitingOrderCount() {
    return this.runtimeStateRepository.countOrdersByStatus(['AWAITING_PAYMENT']);
  }

  async listOwnedOrders(
    accessToken: string,
    params: {
      page?: number;
      pageSize?: number;
      orderNo?: string;
      status?: string;
    },
  ) {
    const account = this.authService.getMe(accessToken);
    await this.runtimeStateRepository.purgeExpiredOrders(account.accountId, Date.now());
    const result = await this.runtimeStateRepository.listOrders({
      ...params,
      accountId: account.accountId,
    });
    return {
      items: result.items.map((order) => this.toOrderRecord(order)),
      page: result.page,
    };
  }

  async getReviewOrderCount() {
    return this.runtimeStateRepository.countOrdersByStatus([
      'UNDERPAID_REVIEW',
      'OVERPAID_REVIEW',
    ]);
  }

  async getTodayPaidOrderCount() {
    const startOfDay = new Date();
    startOfDay.setHours(0, 0, 0, 0);
    return this.runtimeStateRepository.countOrdersByStatus(
      ['PAID', 'PROVISIONING', 'COMPLETED'],
      startOfDay.getTime(),
    );
  }

  private async mustGet(orderNo: string) {
    const order = await this.runtimeStateRepository.findOrderByNo(orderNo);
    if (!order) {
      throw new NotFoundException({
        code: 'ORDER_NOT_FOUND',
        message: 'Order not found',
      });
    }
    return order;
  }

  async listOrders(params: {
    page?: number;
    pageSize?: number;
    orderNo?: string;
    status?: string;
    accountId?: string;
  }) {
    const result = await this.runtimeStateRepository.listOrders(params);
    return {
      items: result.items.map((order) => this.toOrderRecord(order)),
      page: result.page,
    };
  }

  async getOrderByNo(orderNo: string) {
    return this.toOrderRecord(await this.mustGet(orderNo));
  }

  private async mustGetOwned(accessToken: string, orderNo: string) {
    const account = this.authService.getMe(accessToken);
    const order = await this.mustGet(orderNo);
    if (order.accountId !== account.accountId) {
      throw new NotFoundException({
        code: 'ORDER_NOT_FOUND',
        message: 'Order not found',
      });
    }
    return order;
  }

  private async assertPayerWallet(
    accountId: string,
    payerWalletId: string,
    payerChainAccountId: string,
  ) {
    const wallet = await this.runtimeStateRepository.findWalletById(payerWalletId);
    if (!wallet || wallet.accountId !== accountId || wallet.isArchived) {
      throw new ConflictException({
        code: 'ORDER_PAYER_WALLET_INVALID',
        message: 'Selected payer wallet is unavailable',
      });
    }
    const chainAccount =
      await this.runtimeStateRepository.findWalletChainAccountById(
        payerChainAccountId,
      );
    if (!chainAccount || chainAccount.walletId !== payerWalletId) {
      throw new ConflictException({
        code: 'ORDER_PAYER_CHAIN_ACCOUNT_INVALID',
        message: 'Selected payer chain account is unavailable',
      });
    }
    return chainAccount;
  }

  private async markPaidAndProvision(
    order: StoredOrderRecord,
  ): Promise<OrderRecord> {
    if (order.status !== 'PAID' && order.status !== 'PROVISIONING' && order.status !== 'COMPLETED') {
      order.status = 'PAID';
      order.confirmedAt = new Date().toISOString();
      order.paymentMatchedAt = order.paymentMatchedAt ?? order.confirmedAt;
      order.matchedOnchainTxHash =
        order.matchedOnchainTxHash ?? order.submittedClientTxHash;
      await this.runtimeStateRepository.saveOrder(order);
    }

    if (order.status === 'PAID') {
      order.status = 'PROVISIONING';
      await this.runtimeStateRepository.saveOrder(order);
      await this.provisioningService.provisionPaidOrder({
        accountId: order.accountId,
        planCode: order.planCode,
        orderNo: order.orderNo,
        sourceAssetCode: order.quoteAssetCode,
        sourceAmount: order.quoteUsdAmount,
      });
      order.status = 'COMPLETED';
      order.completedAt = new Date().toISOString();
      await this.runtimeStateRepository.saveOrder(order);
    }

    return this.toOrderRecord(order);
  }

  private toOrderRecord(order: StoredOrderRecord): OrderRecord {
    const { collectionAddress: _collectionAddress, idempotencyKey: _idempotencyKey, ...publicOrder } =
      order;
    return publicOrder;
  }

  private resolveCollectionAddress(
    networkCode: StoredOrderRecord['quoteNetworkCode'],
  ) {
    if (networkCode === 'SOLANA') {
      const configured = this.configService
        .get<string>('SOLANA_ORDER_COLLECTION_ADDRESS')
        ?.trim();
      if (configured) {
        if (!this.solanaClient.validateAddress(configured)) {
          throw new ServiceUnavailableException({
            code: 'SOLANA_COLLECTION_ADDRESS_INVALID',
            message: 'Configured Solana collection address is invalid',
          });
        }
        return configured;
      }

      if (this.configService.get<string>('NODE_ENV') === 'test') {
        return 'So11111111111111111111111111111111111111112';
      }

      throw new ServiceUnavailableException({
        code: 'SOLANA_COLLECTION_ADDRESS_MISSING',
        message: 'Solana collection address is not configured',
      });
    }

    return 'TR7NhqjeKQxGTCi8q8ZY4pL8otSzgjLj6t';
  }

  private async allocateUniqueAmountDelta(input: {
    collectionAddress: string;
    quoteAssetCode: StoredOrderRecord['quoteAssetCode'];
    quoteNetworkCode: StoredOrderRecord['quoteNetworkCode'];
    baseAmount: string;
  }) {
    if (input.quoteNetworkCode !== 'SOLANA') {
      return this.zeroMinorUnits(input.quoteAssetCode);
    }

    const decimals = this.getAssetDecimals(
      input.quoteNetworkCode,
      input.quoteAssetCode,
    );
    const baseAmountMinor = this.toMinorUnits(input.baseAmount, decimals);
    const activeOrders =
      await this.runtimeStateRepository.listActiveOrdersForPaymentContext({
        collectionAddress: input.collectionAddress,
        quoteAssetCode: input.quoteAssetCode,
        quoteNetworkCode: input.quoteNetworkCode,
        statuses: [
          'AWAITING_PAYMENT',
          'PAYMENT_DETECTED',
          'CONFIRMING',
          'UNDERPAID_REVIEW',
          'OVERPAID_REVIEW',
        ],
        activeAfter: Date.now(),
      });

    const reservedPayableMinor = new Set(
      activeOrders.map((order) =>
        this.toMinorUnits(order.payableAmount, decimals).toString(),
      ),
    );

    for (let deltaMinor = 1n; deltaMinor <= 9999n; deltaMinor++) {
      const payableMinor = baseAmountMinor + deltaMinor;
      if (!reservedPayableMinor.has(payableMinor.toString())) {
        return this.fromMinorUnits(deltaMinor, decimals);
      }
    }

    throw new ConflictException({
      code: 'ORDER_DELTA_POOL_EXHAUSTED',
      message: 'No unique payable amount is available in the current payment window',
    });
  }

  private buildPaymentQrText(order: StoredOrderRecord) {
    if (order.quoteNetworkCode === 'SOLANA') {
      const quoteAsset = this.resolveQuoteAssetDefinition(
        order.quoteNetworkCode,
        order.quoteAssetCode,
      );
      const params = new URLSearchParams({
        amount: order.payableAmount,
      });

      if (quoteAsset && !quoteAsset.isNative && quoteAsset.contractAddress) {
        params.set('spl-token', quoteAsset.contractAddress);
      }

      return `solana:${order.collectionAddress}?${params.toString()}`;
    }
    return `${order.quoteNetworkCode}:${order.orderNo}:${order.payableAmount}`;
  }

  private isTerminalStatus(status: OrderRecord['status']) {
    return (
      status === 'COMPLETED' ||
      status === 'FAILED' ||
      status === 'EXPIRED' ||
      status === 'CANCELED'
    );
  }

  private mapVerificationFailureStatus(
    verification: VerifyIncomingTransferResponse,
  ): OrderRecord['status'] {
    return verification.mismatchCode === 'AMOUNT_OVER'
      ? 'OVERPAID_REVIEW'
      : 'UNDERPAID_REVIEW';
  }

  private async resolveBaseAmount(
    asset: ReturnType<OrdersService['resolveQuoteAssetDefinition']>,
    planPriceUsd: string,
  ) {
    if (!asset) {
      throw new ServiceUnavailableException({
        code: 'ORDER_ASSET_UNSUPPORTED',
        message: 'Payment asset is unavailable for ordering',
      });
    }

    if (asset.usdPriceMode === 'market') {
      try {
        const usdPrice = asset.contractAddress
          ? Number(
              (
                await this.marketService.getOnchainTokenQuote(
                  asset.networkCode.toLowerCase(),
                  asset.contractAddress,
                )
              )?.currentPrice ?? NaN,
            )
          : Number(
              (
                await this.marketService.getInstrumentDetail(
                  asset.marketInstrumentId ?? 'solana',
                )
              ).ticker24h.lastPrice ?? NaN,
            );
        if (Number.isFinite(usdPrice) && usdPrice > 0) {
          return (Number(planPriceUsd) / usdPrice).toFixed(9);
        }
      } catch (error) {
        this.logger.warn(
          `Failed to resolve live ${asset.assetCode}/USD price for order amount conversion, falling back to static amount`,
          error instanceof Error ? error.message : `${error}`,
        );
      }
      return asset.assetCode === 'SOL'
        ? '0.045000000'
        : Number(planPriceUsd).toFixed(asset.decimals);
    }

    if (asset.usdPriceValue) {
      const usdPrice = Number(asset.usdPriceValue);
      if (Number.isFinite(usdPrice) && usdPrice > 0) {
        return (Number(planPriceUsd) / usdPrice).toFixed(asset.decimals);
      }
    }

    return Number(planPriceUsd).toFixed(asset.decimals);
  }

  private getAssetDecimals(
    networkCode: StoredOrderRecord['quoteNetworkCode'],
    assetCode: StoredOrderRecord['quoteAssetCode'],
  ) {
    return (
      this.resolveQuoteAssetDefinition(networkCode, assetCode)?.decimals ?? 6
    );
  }

  private toMinorUnits(amount: string, decimals: number) {
    const normalized = amount.trim();
    const negative = normalized.startsWith('-');
    const unsigned = negative ? normalized.slice(1) : normalized;
    const [wholePart, fractionPart = ''] = unsigned.split('.');
    const whole = wholePart === '' ? '0' : wholePart;
    const fraction = fractionPart.padEnd(decimals, '0').slice(0, decimals);
    const minor = BigInt(`${whole}${fraction}`);
    return negative ? minor * -1n : minor;
  }

  private fromMinorUnits(amountMinor: bigint, decimals: number) {
    const negative = amountMinor < 0n;
    const normalized = negative ? amountMinor * -1n : amountMinor;
    const raw = normalized.toString().padStart(decimals + 1, '0');
    const whole = raw.slice(0, raw.length - decimals);
    const fraction = raw.slice(raw.length - decimals);
    return `${negative ? '-' : ''}${whole}.${fraction}`;
  }

  private zeroMinorUnits(assetCode: StoredOrderRecord['quoteAssetCode']) {
    return this.fromMinorUnits(
      0n,
      this.getAssetDecimals('SOLANA', assetCode),
    );
  }

  private resolveQuoteAssetDefinition(
    networkCode: StoredOrderRecord['quoteNetworkCode'],
    assetCode: StoredOrderRecord['quoteAssetCode'],
  ) {
    return resolvePaymentAsset(
      this.configService,
      this.solanaClient,
      networkCode,
      assetCode,
    );
  }

  private addDecimalAmounts(left: string, right: string) {
    const decimals = Math.max(
      left.split('.')[1]?.length ?? 0,
      right.split('.')[1]?.length ?? 0,
    );
    return this.fromMinorUnits(
      this.toMinorUnits(left, decimals) + this.toMinorUnits(right, decimals),
      decimals,
    );
  }
}
