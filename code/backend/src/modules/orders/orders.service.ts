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

    const orderId = randomUUID();
    const orderNo = `ORD-${Date.now()}-${orderId.slice(0, 8).toUpperCase()}`;
    const storedOrder: StoredOrderRecord = {
      createdAt: new Date().toISOString(),
      idempotencyKey: compositeKey,
      collectionAddress: this.resolveCollectionAddress(dto.quoteNetworkCode),
      uniqueAmountDelta: this.buildUniqueAmountDelta(orderId),
      orderId,
      orderNo,
      accountId: account.accountId,
      planCode: dto.planCode,
      planName: plan.name,
      orderType: dto.orderType,
      quoteAssetCode: dto.quoteAssetCode,
      quoteNetworkCode: dto.quoteNetworkCode,
      quoteUsdAmount: plan.priceUsd,
      payableAmount:
        dto.quoteAssetCode === 'SOL'
          ? '0.04500000'
          : Number(plan.priceUsd).toFixed(6),
      status: 'AWAITING_PAYMENT',
      expiresAt: new Date(Date.now() + 15 * 60 * 1000).toISOString(),
      confirmedAt: null,
      completedAt: null,
      failureReason: null,
      submittedClientTxHash: null,
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
    order.submittedClientTxHash = dto.txHash;
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
        const verification = await this.solanaClient.verifyIncomingTransfer({
          signature: order.submittedClientTxHash,
          recipientAddress: order.collectionAddress,
          assetCode: order.quoteAssetCode,
          mint:
            order.quoteAssetCode === 'USDT'
              ? this.solanaClient.getUsdtMint()
              : null,
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
        return this.toOrderRecord(order);
      }
    }

    return this.toOrderRecord(order);
  }

  async getAwaitingOrderCount() {
    return this.runtimeStateRepository.countOrdersByStatus(['AWAITING_PAYMENT']);
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

  private async markPaidAndProvision(
    order: StoredOrderRecord,
  ): Promise<OrderRecord> {
    if (order.status !== 'PAID' && order.status !== 'PROVISIONING' && order.status !== 'COMPLETED') {
      order.status = 'PAID';
      order.confirmedAt = new Date().toISOString();
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
    const { collectionAddress: _collectionAddress, createdAt: _createdAt, idempotencyKey: _idempotencyKey, uniqueAmountDelta: _uniqueAmountDelta, ...publicOrder } =
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

  private buildUniqueAmountDelta(seed: string) {
    let accumulator = 0;
    for (const char of seed) {
      accumulator = (accumulator + char.charCodeAt(0)) % 999999;
    }
    return `0.${String(accumulator + 1).padStart(6, '0')}`;
  }

  private buildPaymentQrText(order: StoredOrderRecord) {
    if (order.quoteNetworkCode === 'SOLANA') {
      return `solana:${order.collectionAddress}?amount=${order.payableAmount}`;
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
}
