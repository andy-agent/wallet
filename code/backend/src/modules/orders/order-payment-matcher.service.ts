import { Injectable, Logger } from '@nestjs/common';
import { ConfigService } from '@nestjs/config';
import {
  PaymentScanCursorRecord,
  RuntimeStatePaymentContext,
  StoredOnchainReceiptRecord,
  StoredOrderRecord,
} from '../database/runtime-state.types';
import { RuntimeStateRepository } from '../database/runtime-state.repository';
import { ProvisioningService } from '../provisioning/provisioning.service';
import { SolanaClientService } from '../solana-client/solana-client.service';
import {
  NormalizedIncomingTransfer,
  ScanIncomingTransfersResponse,
} from '../solana-client/solana-client.types';
import { resolvePaymentAsset } from '../payments/payment-asset-catalog';
import { OrderStatus } from './orders.types';

const MATCHER_SCAN_STATUSES: OrderStatus[] = [
  'AWAITING_PAYMENT',
  'PAYMENT_DETECTED',
  'CONFIRMING',
  'UNDERPAID_REVIEW',
  'OVERPAID_REVIEW',
];

@Injectable()
export class OrderPaymentMatcherService {
  private readonly logger = new Logger(OrderPaymentMatcherService.name);
  private inFlight = false;

  constructor(
    private readonly configService: ConfigService,
    private readonly runtimeStateRepository: RuntimeStateRepository,
    private readonly solanaClient: SolanaClientService,
    private readonly provisioningService: ProvisioningService,
  ) {}

  isEnabled(): boolean {
    return (
      this.configService.get<string>('PAYMENT_MATCHER_ENABLED') === 'true'
    );
  }

  async scanActiveContextsOnce() {
    if (!this.isEnabled()) {
      return {
        enabled: false,
        scannedContexts: 0,
        storedEvents: 0,
      };
    }

    if (this.inFlight) {
      this.logger.debug('Order payment matcher tick skipped because a prior run is still active');
      return {
        enabled: true,
        skipped: true,
        scannedContexts: 0,
        storedEvents: 0,
      };
    }

    this.inFlight = true;
    try {
      const contexts =
        await this.runtimeStateRepository.listActivePaymentContexts({
          statuses: MATCHER_SCAN_STATUSES,
          activeAfter: Date.now(),
        });

      let storedEvents = 0;
      for (const context of contexts) {
        if (context.quoteNetworkCode !== 'SOLANA') {
          continue;
        }

        const candidateOrders =
          await this.runtimeStateRepository.listActiveOrdersForPaymentContext({
            collectionAddress: context.collectionAddress,
            quoteAssetCode: context.quoteAssetCode,
            quoteNetworkCode: context.quoteNetworkCode,
            statuses: MATCHER_SCAN_STATUSES,
            activeAfter: Date.now(),
          });
        const claimedOrderNos = new Set<string>();
        const cursor =
          await this.runtimeStateRepository.findPaymentScanCursor(context);
        const response = await this.scanContext(context, cursor);

        for (const event of response.events) {
          const receipt = this.toOnchainReceipt(context, event);
          const match = this.matchOrder(
            candidateOrders,
            event,
            context,
            claimedOrderNos,
          );

          if (match.kind === 'matched') {
            receipt.matchedOrderNo = match.order.orderNo;
            receipt.matchStatus = 'MATCHED';
            receipt.matcherRemark = 'AUTO_MATCHED_BY_SHARED_ADDRESS';
            const updatedOrder = await this.applyMatchedOrder(match.order, event);
            claimedOrderNos.add(updatedOrder.orderNo);
            const matchedIndex = candidateOrders.findIndex(
              (order) => order.orderNo === updatedOrder.orderNo,
            );
            if (matchedIndex >= 0) {
              candidateOrders[matchedIndex] = updatedOrder;
            }
          } else if (match.kind === 'ambiguous') {
            receipt.matchStatus = 'AMBIGUOUS';
            receipt.matcherRemark = 'MULTIPLE_PENDING_ORDERS_MATCHED';
          } else {
            receipt.matchStatus = 'UNMATCHED';
            receipt.matcherRemark = 'NO_PENDING_ORDER_MATCHED';
          }

          await this.runtimeStateRepository.upsertOnchainReceipt(receipt);
          storedEvents += 1;
        }

        const nextCursor = this.toCursorRecord(context, cursor, response);
        if (nextCursor) {
          await this.runtimeStateRepository.savePaymentScanCursor(nextCursor);
        }
      }

      return {
        enabled: true,
        scannedContexts: contexts.length,
        storedEvents,
      };
    } finally {
      this.inFlight = false;
    }
  }

  private async scanContext(
    context: RuntimeStatePaymentContext,
    cursor: PaymentScanCursorRecord | null,
  ) {
    return this.solanaClient.scanIncomingTransfers({
      collectionAddress: context.collectionAddress,
      assetCode: context.quoteAssetCode,
      mint: this.resolveSolanaMint(
        context.quoteNetworkCode,
        context.quoteAssetCode,
      ),
      cursor: cursor
        ? {
            // Drive incremental scans forward by slot floor.
            // `beforeSignature` is intentionally not reused from persisted cursor.
            beforeSignature: null,
            minSlotExclusive: cursor.lastSlot,
          }
        : null,
      limit: 50,
    });
  }

  private resolveSolanaMint(networkCode: string, assetCode: string) {
    const asset = resolvePaymentAsset(
      this.configService,
      this.solanaClient,
      networkCode,
      assetCode,
    );
    return asset?.isNative ? null : asset?.contractAddress ?? null;
  }

  private toOnchainReceipt(
    context: RuntimeStatePaymentContext,
    event: NormalizedIncomingTransfer,
  ): StoredOnchainReceiptRecord {
    return {
      receiptId: `${context.quoteNetworkCode}:${event.signature}:${event.eventIndex}`,
      quoteNetworkCode: context.quoteNetworkCode,
      quoteAssetCode: context.quoteAssetCode,
      collectionAddress: context.collectionAddress,
      txHash: event.signature,
      eventIndex: event.eventIndex,
      recipientTokenAccount: event.recipientTokenAccount ?? null,
      fromAddress: event.fromAddress ?? null,
      mint: event.mint ?? null,
      amount: event.amount,
      amountMinor: event.amountRaw,
      confirmationStatus: event.confirmationStatus,
      slot: event.slot,
      blockTime:
        typeof event.blockTime === 'number'
          ? new Date(event.blockTime * 1000).toISOString()
          : null,
      observedAt: new Date().toISOString(),
      matchedOrderNo: null,
      matchStatus: 'UNMATCHED',
      matcherRemark: null,
      rawPayload: event.rawPayload ?? null,
    };
  }

  private toCursorRecord(
    context: RuntimeStatePaymentContext,
    previous: PaymentScanCursorRecord | null,
    response: ScanIncomingTransfersResponse,
  ): PaymentScanCursorRecord | null {
    const oldestEvent = response.events[response.events.length - 1];
    const newestEvent = response.events[0];
    const beforeSignature =
      response.nextCursor?.beforeSignature ??
      oldestEvent?.signature ??
      previous?.beforeSignature ??
      null;
    const minSlotExclusive =
      response.nextCursor?.minSlotExclusive ??
      previous?.lastSlot ??
      null;

    if (
      beforeSignature === null &&
      minSlotExclusive === null &&
      !newestEvent &&
      !previous
    ) {
      return null;
    }

    return {
      cursorKey: this.buildCursorKey(context),
      quoteNetworkCode: context.quoteNetworkCode,
      quoteAssetCode: context.quoteAssetCode,
      collectionAddress: context.collectionAddress,
      beforeSignature,
      lastSignature: newestEvent?.signature ?? previous?.lastSignature ?? null,
      lastSlot: newestEvent?.slot ?? minSlotExclusive ?? previous?.lastSlot ?? null,
      updatedAt: new Date().toISOString(),
    };
  }

  private buildCursorKey(context: RuntimeStatePaymentContext) {
    return [
      context.quoteNetworkCode,
      context.quoteAssetCode,
      context.collectionAddress,
    ].join(':');
  }

  private matchOrder(
    orders: StoredOrderRecord[],
    event: NormalizedIncomingTransfer,
    context: RuntimeStatePaymentContext,
    claimedOrderNos: Set<string>,
  ) {
    if (
      event.confirmationStatus !== 'confirmed' &&
      event.confirmationStatus !== 'finalized'
    ) {
      return {
        kind: 'unmatched' as const,
      };
    }

    const observedAtMs =
      typeof event.blockTime === 'number'
        ? event.blockTime * 1000
        : Date.now();

    const candidates = orders.filter((order) => {
      if (claimedOrderNos.has(order.orderNo)) {
        return false;
      }

      const payableAmountMinor = this.toMinorUnits(
        order.payableAmount,
        event.decimals,
      );
      const createdAtMs = new Date(order.createdAt).getTime();
      const expiresAtMs = new Date(order.expiresAt).getTime();

      return (
        order.collectionAddress === context.collectionAddress &&
        order.quoteAssetCode === context.quoteAssetCode &&
        order.quoteNetworkCode === context.quoteNetworkCode &&
        MATCHER_SCAN_STATUSES.includes(order.status) &&
        order.matchedOnchainTxHash === null &&
        createdAtMs <= observedAtMs &&
        observedAtMs <= expiresAtMs &&
        payableAmountMinor === BigInt(event.amountRaw)
      );
    });

    if (candidates.length === 1) {
      return {
        kind: 'matched' as const,
        order: candidates[0],
      };
    }

    if (candidates.length > 1) {
      return {
        kind: 'ambiguous' as const,
      };
    }

    return {
      kind: 'unmatched' as const,
    };
  }

  private async applyMatchedOrder(
    order: StoredOrderRecord,
    event: NormalizedIncomingTransfer,
  ): Promise<StoredOrderRecord> {
    const matchedAt =
      typeof event.blockTime === 'number'
        ? new Date(event.blockTime * 1000).toISOString()
        : new Date().toISOString();
    const nextOrder: StoredOrderRecord = {
      ...order,
      matchedOnchainTxHash: event.signature,
      paymentMatchedAt: matchedAt,
      matcherRemark: 'AUTO_MATCHED_BY_SHARED_ADDRESS',
      failureReason: null,
    };

    if (
      event.confirmationStatus === 'confirmed' ||
      event.confirmationStatus === 'finalized'
    ) {
      nextOrder.status = 'PAID';
      nextOrder.confirmedAt = matchedAt;
      await this.runtimeStateRepository.saveOrder(nextOrder);

      nextOrder.status = 'PROVISIONING';
      await this.runtimeStateRepository.saveOrder(nextOrder);
      await this.provisioningService.provisionPaidOrder({
        accountId: nextOrder.accountId,
        planCode: nextOrder.planCode,
        orderNo: nextOrder.orderNo,
        sourceAssetCode: nextOrder.quoteAssetCode,
        sourceAmount: nextOrder.quoteUsdAmount,
      });
      nextOrder.status = 'COMPLETED';
      nextOrder.completedAt = new Date().toISOString();
      await this.runtimeStateRepository.saveOrder(nextOrder);
      return nextOrder;
    }

    nextOrder.status = 'CONFIRMING';
    await this.runtimeStateRepository.saveOrder(nextOrder);
    return nextOrder;
  }

  private toMinorUnits(amount: string, decimals: number) {
    const [wholePart, fractionPart = ''] = amount.trim().split('.');
    const whole = wholePart === '' ? '0' : wholePart;
    const fraction = fractionPart.padEnd(decimals, '0').slice(0, decimals);
    return BigInt(`${whole}${fraction}`);
  }
}
