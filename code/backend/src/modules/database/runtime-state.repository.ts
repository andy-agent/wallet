import { Injectable } from '@nestjs/common';
import {
  existsSync,
  mkdirSync,
  readFileSync,
  renameSync,
  writeFileSync,
} from 'fs';
import { dirname, resolve } from 'path';
import { OrderStatus } from '../orders/orders.types';
import { PersistedSubscriptionRecord } from '../vpn/vpn.types';
import {
  RuntimeStateSnapshot,
  StoredOrderRecord,
} from './runtime-state.types';

const EMPTY_RUNTIME_STATE: RuntimeStateSnapshot = {
  version: 1,
  orders: [],
  idempotencyIndex: {},
  subscriptions: [],
};

@Injectable()
export class RuntimeStateRepository {
  private readonly stateFilePath = resolve(
    process.env.RUNTIME_STATE_FILE ??
      `${process.cwd()}/.runtime/runtime-state.json`,
  );

  constructor() {
    this.ensureStateFile();
  }

  createOrder(
    order: StoredOrderRecord,
    compositeIdempotencyKey: string,
  ): StoredOrderRecord {
    const snapshot = this.readSnapshot();
    const existingOrderNo = snapshot.idempotencyIndex[compositeIdempotencyKey];

    if (existingOrderNo) {
      const existing = snapshot.orders.find(
        (item) => item.orderNo === existingOrderNo,
      );
      if (existing) {
        return existing;
      }
    }

    snapshot.orders.push(order);
    snapshot.idempotencyIndex[compositeIdempotencyKey] = order.orderNo;
    this.writeSnapshot(snapshot);
    return order;
  }

  findOrderByNo(orderNo: string): StoredOrderRecord | null {
    return (
      this.readSnapshot().orders.find((order) => order.orderNo === orderNo) ??
      null
    );
  }

  saveOrder(order: StoredOrderRecord): StoredOrderRecord {
    const snapshot = this.readSnapshot();
    const index = snapshot.orders.findIndex(
      (item) => item.orderNo === order.orderNo,
    );

    if (index === -1) {
      snapshot.orders.push(order);
    } else {
      snapshot.orders[index] = order;
    }

    this.writeSnapshot(snapshot);
    return order;
  }

  listOrders(params: {
    page?: number;
    pageSize?: number;
    orderNo?: string;
    status?: string;
    accountId?: string;
  }) {
    const page = Math.max(1, params.page ?? 1);
    const pageSize = Math.min(100, Math.max(1, params.pageSize ?? 20));

    let items = [...this.readSnapshot().orders];

    if (params.orderNo) {
      const keyword = params.orderNo.toLowerCase();
      items = items.filter((order) =>
        order.orderNo.toLowerCase().includes(keyword),
      );
    }

    if (params.status) {
      items = items.filter((order) => order.status === params.status);
    }

    if (params.accountId) {
      items = items.filter((order) => order.accountId === params.accountId);
    }

    items.sort((left, right) => right.createdAt.localeCompare(left.createdAt));

    const total = items.length;
    const start = (page - 1) * pageSize;
    const end = start + pageSize;

    return {
      items: items.slice(start, end),
      page: {
        page,
        pageSize,
        total,
      },
    };
  }

  countOrdersByStatus(statuses: OrderStatus[], confirmedSince?: number) {
    return this.readSnapshot().orders.filter((order) => {
      if (!statuses.includes(order.status)) {
        return false;
      }

      if (!confirmedSince) {
        return true;
      }

      return (
        !!order.confirmedAt &&
        new Date(order.confirmedAt).getTime() >= confirmedSince
      );
    }).length;
  }

  findCurrentSubscriptionByAccountId(
    accountId: string,
  ): PersistedSubscriptionRecord | null {
    return (
      this.readSnapshot().subscriptions
        .filter((subscription) => subscription.accountId === accountId)
        .sort((left, right) => right.updatedAt.localeCompare(left.updatedAt))[0] ??
      null
    );
  }

  upsertSubscription(
    subscription: PersistedSubscriptionRecord,
  ): PersistedSubscriptionRecord {
    const snapshot = this.readSnapshot();
    const nextSubscriptions = snapshot.subscriptions.filter(
      (item) =>
        item.accountId !== subscription.accountId &&
        item.orderNo !== subscription.orderNo,
    );

    nextSubscriptions.push(subscription);
    snapshot.subscriptions = nextSubscriptions;
    this.writeSnapshot(snapshot);
    return subscription;
  }

  countActiveSubscriptions() {
    return this.readSnapshot().subscriptions.filter(
      (subscription) => subscription.status === 'ACTIVE',
    ).length;
  }

  private ensureStateFile() {
    const directory = dirname(this.stateFilePath);
    if (!existsSync(directory)) {
      mkdirSync(directory, { recursive: true });
    }

    if (!existsSync(this.stateFilePath)) {
      this.writeSnapshot(EMPTY_RUNTIME_STATE);
    }
  }

  private readSnapshot(): RuntimeStateSnapshot {
    this.ensureStateFile();
    const raw = readFileSync(this.stateFilePath, 'utf8').trim();
    if (!raw) {
      return {
        version: 1,
        orders: [],
        idempotencyIndex: {},
        subscriptions: [],
      };
    }
    return JSON.parse(raw) as RuntimeStateSnapshot;
  }

  private writeSnapshot(snapshot: RuntimeStateSnapshot) {
    const directory = dirname(this.stateFilePath);
    if (!existsSync(directory)) {
      mkdirSync(directory, { recursive: true });
    }

    const tempPath = `${this.stateFilePath}.${process.pid}.tmp`;
    writeFileSync(tempPath, JSON.stringify(snapshot, null, 2), 'utf8');
    renameSync(tempPath, this.stateFilePath);
  }
}
