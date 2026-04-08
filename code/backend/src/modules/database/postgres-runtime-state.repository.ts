import { Pool } from 'pg';
import {
  AuthAccount,
  AuthSession,
  VerificationCodeRecord,
} from '../auth/auth.types';
import { OrderStatus } from '../orders/orders.types';
import { PersistedSubscriptionRecord } from '../vpn/vpn.types';
import { RuntimeStateRepository } from './runtime-state.repository';
import {
  RuntimeStateListOrdersParams,
  RuntimeStateListOrdersResult,
  StoredOrderRecord,
} from './runtime-state.types';

const ACCOUNTS_TABLE = 'accounts';
const SESSIONS_TABLE = 'client_sessions';
const VERIFICATION_CODES_TABLE = 'verification_codes';
const ORDERS_TABLE = 'runtime_state_orders';
const SUBSCRIPTIONS_TABLE = 'runtime_state_subscriptions';
const ACCOUNT_COLUMNS = `
  account_id,
  email,
  password_hash,
  status,
  referral_code,
  created_at,
  updated_at
`;
const SESSION_COLUMNS = `
  session_id,
  account_id,
  installation_id,
  access_token,
  refresh_token,
  access_token_expires_at,
  refresh_token_expires_at,
  status,
  created_at,
  updated_at
`;
const VERIFICATION_CODE_COLUMNS = `
  email,
  purpose,
  code_hash,
  expires_at,
  created_at,
  updated_at
`;
const ORDER_COLUMNS = `
  order_id,
  order_no,
  account_id,
  plan_code,
  plan_name,
  order_type,
  quote_asset_code,
  quote_network_code,
  quote_usd_amount,
  payable_amount,
  status,
  expires_at,
  confirmed_at,
  completed_at,
  failure_reason,
  submitted_client_tx_hash,
  created_at,
  idempotency_key,
  collection_address,
  unique_amount_delta
`;
const SUBSCRIPTION_COLUMNS = `
  account_id,
  order_no,
  created_at,
  updated_at,
  subscription_id,
  plan_code,
  status,
  started_at,
  expire_at,
  days_remaining,
  is_unlimited_traffic,
  max_active_sessions
`;

interface RuntimeStateOrderRow {
  order_id: string;
  order_no: string;
  account_id: string;
  plan_code: string;
  plan_name: string;
  order_type: 'NEW' | 'RENEWAL';
  quote_asset_code: 'SOL' | 'USDT';
  quote_network_code: 'SOLANA' | 'TRON';
  quote_usd_amount: string;
  payable_amount: string;
  status: StoredOrderRecord['status'];
  expires_at: Date | string;
  confirmed_at: Date | string | null;
  completed_at: Date | string | null;
  failure_reason: string | null;
  submitted_client_tx_hash: string | null;
  created_at: Date | string;
  idempotency_key: string;
  collection_address: string;
  unique_amount_delta: string;
}

interface RuntimeStateSubscriptionRow {
  account_id: string;
  order_no: string;
  created_at: Date | string;
  updated_at: Date | string;
  subscription_id: string;
  plan_code: string;
  status: PersistedSubscriptionRecord['status'];
  started_at: Date | string | null;
  expire_at: Date | string | null;
  days_remaining: number | null;
  is_unlimited_traffic: boolean;
  max_active_sessions: number;
}

interface AuthAccountRow {
  account_id: string;
  email: string;
  password_hash: string;
  status: AuthAccount['status'];
  referral_code: string;
  created_at: Date | string;
  updated_at: Date | string;
}

interface AuthSessionRow {
  session_id: string;
  account_id: string;
  installation_id: string | null;
  access_token: string;
  refresh_token: string;
  access_token_expires_at: Date | string;
  refresh_token_expires_at: Date | string;
  status: AuthSession['status'];
  created_at: Date | string;
  updated_at: Date | string;
}

interface VerificationCodeRow {
  email: string;
  purpose: VerificationCodeRecord['purpose'];
  code_hash: string;
  expires_at: Date | string;
  created_at: Date | string;
  updated_at: Date | string;
}

interface CountRow {
  count: number;
}

interface TotalRow {
  total: number;
}

export class PostgresRuntimeStateRepository extends RuntimeStateRepository {
  private readyPromise: Promise<void> | null = null;

  constructor(
    private readonly options: { connectionString: string },
    private readonly pool: Pool = new Pool({
      connectionString: options.connectionString,
    }),
  ) {
    super();
  }

  async initialize(): Promise<void> {
    if (!this.readyPromise) {
      this.readyPromise = this.ensureSchema();
    }
    await this.readyPromise;
  }

  async listAccounts(): Promise<AuthAccount[]> {
    await this.ensureReady();
    const result = await this.pool.query<AuthAccountRow>(
      `
        SELECT ${ACCOUNT_COLUMNS}
        FROM ${ACCOUNTS_TABLE}
      `,
    );
    return result.rows.map((row) => this.mapAccount(row));
  }

  async saveAccount(account: AuthAccount): Promise<AuthAccount> {
    await this.ensureReady();
    const result = await this.pool.query<AuthAccountRow>(
      `
        INSERT INTO ${ACCOUNTS_TABLE} (
          account_id,
          email,
          password_hash,
          status,
          referral_code,
          created_at,
          updated_at
        )
        VALUES ($1, $2, $3, $4, $5, $6, $7)
        ON CONFLICT (account_id) DO UPDATE
        SET
          email = EXCLUDED.email,
          password_hash = EXCLUDED.password_hash,
          status = EXCLUDED.status,
          referral_code = EXCLUDED.referral_code,
          created_at = EXCLUDED.created_at,
          updated_at = EXCLUDED.updated_at
        RETURNING ${ACCOUNT_COLUMNS}
      `,
      [
        account.accountId,
        account.email,
        account.passwordHash,
        account.status,
        account.referralCode,
        account.createdAt,
        account.updatedAt,
      ],
    );
    return this.mapAccount(result.rows[0]);
  }

  async listSessions(): Promise<AuthSession[]> {
    await this.ensureReady();
    const result = await this.pool.query<AuthSessionRow>(
      `
        SELECT ${SESSION_COLUMNS}
        FROM ${SESSIONS_TABLE}
      `,
    );
    return result.rows.map((row) => this.mapSession(row));
  }

  async saveSession(session: AuthSession): Promise<AuthSession> {
    await this.ensureReady();
    const result = await this.pool.query<AuthSessionRow>(
      `
        INSERT INTO ${SESSIONS_TABLE} (
          session_id,
          account_id,
          installation_id,
          access_token,
          refresh_token,
          access_token_expires_at,
          refresh_token_expires_at,
          status,
          created_at,
          updated_at
        )
        VALUES ($1, $2, $3, $4, $5, $6, $7, $8, $9, $10)
        ON CONFLICT (session_id) DO UPDATE
        SET
          account_id = EXCLUDED.account_id,
          installation_id = EXCLUDED.installation_id,
          access_token = EXCLUDED.access_token,
          refresh_token = EXCLUDED.refresh_token,
          access_token_expires_at = EXCLUDED.access_token_expires_at,
          refresh_token_expires_at = EXCLUDED.refresh_token_expires_at,
          status = EXCLUDED.status,
          created_at = EXCLUDED.created_at,
          updated_at = EXCLUDED.updated_at
        RETURNING ${SESSION_COLUMNS}
      `,
      [
        session.sessionId,
        session.accountId,
        session.installationId,
        session.accessToken,
        session.refreshToken,
        session.accessTokenExpiresAt,
        session.refreshTokenExpiresAt,
        session.status,
        session.createdAt,
        session.updatedAt,
      ],
    );
    return this.mapSession(result.rows[0]);
  }

  async listVerificationCodes(): Promise<VerificationCodeRecord[]> {
    await this.ensureReady();
    const result = await this.pool.query<VerificationCodeRow>(
      `
        SELECT ${VERIFICATION_CODE_COLUMNS}
        FROM ${VERIFICATION_CODES_TABLE}
      `,
    );
    return result.rows.map((row) => this.mapVerificationCode(row));
  }

  async saveVerificationCode(
    record: VerificationCodeRecord,
  ): Promise<VerificationCodeRecord> {
    await this.ensureReady();
    const result = await this.pool.query<VerificationCodeRow>(
      `
        INSERT INTO ${VERIFICATION_CODES_TABLE} (
          email,
          purpose,
          code_hash,
          expires_at,
          created_at,
          updated_at
        )
        VALUES ($1, $2, $3, $4, $5, $6)
        ON CONFLICT (email, purpose) DO UPDATE
        SET
          code_hash = EXCLUDED.code_hash,
          expires_at = EXCLUDED.expires_at,
          created_at = EXCLUDED.created_at,
          updated_at = EXCLUDED.updated_at
        RETURNING ${VERIFICATION_CODE_COLUMNS}
      `,
      [
        record.email,
        record.purpose,
        record.codeHash,
        new Date(record.expiresAt).toISOString(),
        record.createdAt,
        record.updatedAt,
      ],
    );
    return this.mapVerificationCode(result.rows[0]);
  }

  async createOrder(
    order: StoredOrderRecord,
    compositeIdempotencyKey: string,
  ): Promise<StoredOrderRecord> {
    await this.ensureReady();

    const insertResult = await this.pool.query<RuntimeStateOrderRow>(
      `
        INSERT INTO ${ORDERS_TABLE} (
          order_id,
          order_no,
          account_id,
          plan_code,
          plan_name,
          order_type,
          quote_asset_code,
          quote_network_code,
          quote_usd_amount,
          payable_amount,
          status,
          expires_at,
          confirmed_at,
          completed_at,
          failure_reason,
          submitted_client_tx_hash,
          created_at,
          updated_at,
          idempotency_key,
          collection_address,
          unique_amount_delta
        )
        VALUES (
          $1,
          $2,
          $3,
          $4,
          $5,
          $6,
          $7,
          $8,
          $9,
          $10,
          $11,
          $12,
          $13,
          $14,
          $15,
          $16,
          $17,
          $18,
          $19,
          $20,
          $21
        )
        ON CONFLICT (idempotency_key) DO NOTHING
        RETURNING ${ORDER_COLUMNS}
      `,
      this.toOrderValues(order, compositeIdempotencyKey),
    );

    if (insertResult.rows[0]) {
      return this.mapOrder(insertResult.rows[0]);
    }

    const existing = await this.pool.query<RuntimeStateOrderRow>(
      `
        SELECT ${ORDER_COLUMNS}
        FROM ${ORDERS_TABLE}
        WHERE idempotency_key = $1
        LIMIT 1
      `,
      [compositeIdempotencyKey],
    );

    if (!existing.rows[0]) {
      throw new Error(
        `Runtime state order insert lost idempotency row for ${compositeIdempotencyKey}`,
      );
    }

    return this.mapOrder(existing.rows[0]);
  }

  async findOrderByNo(orderNo: string): Promise<StoredOrderRecord | null> {
    await this.ensureReady();
    const result = await this.pool.query<RuntimeStateOrderRow>(
      `
        SELECT ${ORDER_COLUMNS}
        FROM ${ORDERS_TABLE}
        WHERE order_no = $1
        LIMIT 1
      `,
      [orderNo],
    );
    return result.rows[0] ? this.mapOrder(result.rows[0]) : null;
  }

  async saveOrder(order: StoredOrderRecord): Promise<StoredOrderRecord> {
    await this.ensureReady();
    const result = await this.pool.query<RuntimeStateOrderRow>(
      `
        INSERT INTO ${ORDERS_TABLE} (
          order_id,
          order_no,
          account_id,
          plan_code,
          plan_name,
          order_type,
          quote_asset_code,
          quote_network_code,
          quote_usd_amount,
          payable_amount,
          status,
          expires_at,
          confirmed_at,
          completed_at,
          failure_reason,
          submitted_client_tx_hash,
          created_at,
          updated_at,
          idempotency_key,
          collection_address,
          unique_amount_delta
        )
        VALUES (
          $1,
          $2,
          $3,
          $4,
          $5,
          $6,
          $7,
          $8,
          $9,
          $10,
          $11,
          $12,
          $13,
          $14,
          $15,
          $16,
          $17,
          $18,
          $19,
          $20,
          $21
        )
        ON CONFLICT (order_no) DO UPDATE
        SET
          order_id = EXCLUDED.order_id,
          account_id = EXCLUDED.account_id,
          plan_code = EXCLUDED.plan_code,
          plan_name = EXCLUDED.plan_name,
          order_type = EXCLUDED.order_type,
          quote_asset_code = EXCLUDED.quote_asset_code,
          quote_network_code = EXCLUDED.quote_network_code,
          quote_usd_amount = EXCLUDED.quote_usd_amount,
          payable_amount = EXCLUDED.payable_amount,
          status = EXCLUDED.status,
          expires_at = EXCLUDED.expires_at,
          confirmed_at = EXCLUDED.confirmed_at,
          completed_at = EXCLUDED.completed_at,
          failure_reason = EXCLUDED.failure_reason,
          submitted_client_tx_hash = EXCLUDED.submitted_client_tx_hash,
          created_at = EXCLUDED.created_at,
          updated_at = EXCLUDED.updated_at,
          idempotency_key = EXCLUDED.idempotency_key,
          collection_address = EXCLUDED.collection_address,
          unique_amount_delta = EXCLUDED.unique_amount_delta
        RETURNING ${ORDER_COLUMNS}
      `,
      this.toOrderValues(order, order.idempotencyKey),
    );

    return this.mapOrder(result.rows[0]);
  }

  async listOrders(
    params: RuntimeStateListOrdersParams,
  ): Promise<RuntimeStateListOrdersResult> {
    await this.ensureReady();

    const page = Math.max(1, params.page ?? 1);
    const pageSize = Math.min(100, Math.max(1, params.pageSize ?? 20));
    const filters = this.buildOrderFilters(params);
    const pagingValues = [...filters.values, pageSize, (page - 1) * pageSize];

    const [itemsResult, totalResult] = await Promise.all([
      this.pool.query<RuntimeStateOrderRow>(
        `
          SELECT ${ORDER_COLUMNS}
          FROM ${ORDERS_TABLE}
          ${filters.whereClause}
          ORDER BY created_at DESC
          LIMIT $${filters.values.length + 1}
          OFFSET $${filters.values.length + 2}
        `,
        pagingValues,
      ),
      this.pool.query<TotalRow>(
        `
          SELECT COUNT(*)::int AS total
          FROM ${ORDERS_TABLE}
          ${filters.whereClause}
        `,
        filters.values,
      ),
    ]);

    return {
      items: itemsResult.rows.map((row) => this.mapOrder(row)),
      page: {
        page,
        pageSize,
        total: totalResult.rows[0]?.total ?? 0,
      },
    };
  }

  async countOrdersByStatus(
    statuses: OrderStatus[],
    confirmedSince?: number,
  ): Promise<number> {
    await this.ensureReady();

    const values: Array<string[] | string> = [statuses];
    let sql = `
      SELECT COUNT(*)::int AS count
      FROM ${ORDERS_TABLE}
      WHERE status = ANY($1::text[])
    `;

    if (typeof confirmedSince === 'number') {
      values.push(new Date(confirmedSince).toISOString());
      sql += ` AND confirmed_at >= $2`;
    }

    const result = await this.pool.query<CountRow>(sql, values);
    return result.rows[0]?.count ?? 0;
  }

  async findCurrentSubscriptionByAccountId(
    accountId: string,
  ): Promise<PersistedSubscriptionRecord | null> {
    await this.ensureReady();
    const result = await this.pool.query<RuntimeStateSubscriptionRow>(
      `
        SELECT ${SUBSCRIPTION_COLUMNS}
        FROM ${SUBSCRIPTIONS_TABLE}
        WHERE account_id = $1
        LIMIT 1
      `,
      [accountId],
    );

    return result.rows[0] ? this.mapSubscription(result.rows[0]) : null;
  }

  async upsertSubscription(
    subscription: PersistedSubscriptionRecord,
  ): Promise<PersistedSubscriptionRecord> {
    await this.ensureReady();
    const result = await this.pool.query<RuntimeStateSubscriptionRow>(
      `
        INSERT INTO ${SUBSCRIPTIONS_TABLE} (
          account_id,
          order_no,
          created_at,
          updated_at,
          subscription_id,
          plan_code,
          status,
          started_at,
          expire_at,
          days_remaining,
          is_unlimited_traffic,
          max_active_sessions
        )
        VALUES (
          $1,
          $2,
          $3,
          $4,
          $5,
          $6,
          $7,
          $8,
          $9,
          $10,
          $11,
          $12
        )
        ON CONFLICT (account_id) DO UPDATE
        SET
          order_no = EXCLUDED.order_no,
          updated_at = EXCLUDED.updated_at,
          subscription_id = EXCLUDED.subscription_id,
          plan_code = EXCLUDED.plan_code,
          status = EXCLUDED.status,
          started_at = EXCLUDED.started_at,
          expire_at = EXCLUDED.expire_at,
          days_remaining = EXCLUDED.days_remaining,
          is_unlimited_traffic = EXCLUDED.is_unlimited_traffic,
          max_active_sessions = EXCLUDED.max_active_sessions
        RETURNING ${SUBSCRIPTION_COLUMNS}
      `,
      [
        subscription.accountId,
        subscription.orderNo,
        subscription.createdAt,
        subscription.updatedAt,
        subscription.subscriptionId,
        subscription.planCode,
        subscription.status,
        subscription.startedAt,
        subscription.expireAt,
        subscription.daysRemaining,
        subscription.isUnlimitedTraffic,
        subscription.maxActiveSessions,
      ],
    );

    return this.mapSubscription(result.rows[0]);
  }

  async countActiveSubscriptions(): Promise<number> {
    await this.ensureReady();
    const result = await this.pool.query<CountRow>(
      `
        SELECT COUNT(*)::int AS count
        FROM ${SUBSCRIPTIONS_TABLE}
        WHERE status = 'ACTIVE'
      `,
    );
    return result.rows[0]?.count ?? 0;
  }

  async onModuleDestroy(): Promise<void> {
    await this.pool.end();
  }

  private async ensureReady() {
    await this.initialize();
  }

  private async ensureSchema() {
    await this.pool.query(`
      CREATE TABLE IF NOT EXISTS ${ACCOUNTS_TABLE} (
        account_id text PRIMARY KEY,
        email text NOT NULL UNIQUE,
        password_hash text NOT NULL,
        status text NOT NULL,
        referral_code text NOT NULL UNIQUE,
        created_at timestamptz NOT NULL,
        updated_at timestamptz NOT NULL
      )
    `);
    await this.pool.query(`
      CREATE INDEX IF NOT EXISTS idx_accounts_status
      ON ${ACCOUNTS_TABLE} (status)
    `);

    await this.pool.query(`
      CREATE TABLE IF NOT EXISTS ${SESSIONS_TABLE} (
        session_id text PRIMARY KEY,
        account_id text NOT NULL REFERENCES ${ACCOUNTS_TABLE} (account_id),
        installation_id text NULL,
        access_token text NOT NULL UNIQUE,
        refresh_token text NOT NULL UNIQUE,
        access_token_expires_at timestamptz NOT NULL,
        refresh_token_expires_at timestamptz NOT NULL,
        status text NOT NULL,
        created_at timestamptz NOT NULL,
        updated_at timestamptz NOT NULL
      )
    `);
    await this.pool.query(`
      CREATE INDEX IF NOT EXISTS idx_client_sessions_account_status
      ON ${SESSIONS_TABLE} (account_id, status)
    `);
    await this.pool.query(`
      CREATE INDEX IF NOT EXISTS idx_client_sessions_refresh_token_expires_at
      ON ${SESSIONS_TABLE} (refresh_token_expires_at)
    `);

    await this.pool.query(`
      CREATE TABLE IF NOT EXISTS ${VERIFICATION_CODES_TABLE} (
        email text NOT NULL,
        purpose text NOT NULL,
        code_hash text NOT NULL,
        expires_at timestamptz NOT NULL,
        created_at timestamptz NOT NULL,
        updated_at timestamptz NOT NULL,
        PRIMARY KEY (email, purpose)
      )
    `);
    await this.pool.query(`
      CREATE INDEX IF NOT EXISTS idx_verification_codes_expires_at
      ON ${VERIFICATION_CODES_TABLE} (expires_at)
    `);

    await this.pool.query(`
      CREATE TABLE IF NOT EXISTS ${ORDERS_TABLE} (
        order_no text PRIMARY KEY,
        order_id text NOT NULL UNIQUE,
        account_id text NOT NULL,
        plan_code text NOT NULL,
        plan_name text NOT NULL,
        order_type text NOT NULL,
        quote_asset_code text NOT NULL,
        quote_network_code text NOT NULL,
        quote_usd_amount text NOT NULL,
        payable_amount text NOT NULL,
        status text NOT NULL,
        expires_at timestamptz NOT NULL,
        confirmed_at timestamptz NULL,
        completed_at timestamptz NULL,
        failure_reason text NULL,
        submitted_client_tx_hash text NULL,
        created_at timestamptz NOT NULL,
        updated_at timestamptz NOT NULL DEFAULT NOW(),
        idempotency_key text NOT NULL UNIQUE,
        collection_address text NOT NULL,
        unique_amount_delta text NOT NULL
      )
    `);

    await this.pool.query(`
      CREATE INDEX IF NOT EXISTS idx_runtime_state_orders_account_created
      ON ${ORDERS_TABLE} (account_id, created_at DESC)
    `);
    await this.pool.query(`
      CREATE INDEX IF NOT EXISTS idx_runtime_state_orders_status
      ON ${ORDERS_TABLE} (status)
    `);
    await this.pool.query(`
      CREATE INDEX IF NOT EXISTS idx_runtime_state_orders_confirmed_at
      ON ${ORDERS_TABLE} (confirmed_at)
    `);

    await this.pool.query(`
      CREATE TABLE IF NOT EXISTS ${SUBSCRIPTIONS_TABLE} (
        account_id text PRIMARY KEY,
        order_no text NOT NULL UNIQUE,
        created_at timestamptz NOT NULL,
        updated_at timestamptz NOT NULL,
        subscription_id text NOT NULL,
        plan_code text NOT NULL,
        status text NOT NULL,
        started_at timestamptz NULL,
        expire_at timestamptz NULL,
        days_remaining integer NULL,
        is_unlimited_traffic boolean NOT NULL,
        max_active_sessions integer NOT NULL
      )
    `);
    await this.pool.query(`
      CREATE INDEX IF NOT EXISTS idx_runtime_state_subscriptions_status
      ON ${SUBSCRIPTIONS_TABLE} (status)
    `);
  }

  private buildOrderFilters(params: RuntimeStateListOrdersParams) {
    const conditions: string[] = [];
    const values: string[] = [];

    if (params.orderNo) {
      values.push(`%${params.orderNo}%`);
      conditions.push(`order_no ILIKE $${values.length}`);
    }

    if (params.status) {
      values.push(params.status);
      conditions.push(`status = $${values.length}`);
    }

    if (params.accountId) {
      values.push(params.accountId);
      conditions.push(`account_id = $${values.length}`);
    }

    return {
      values,
      whereClause: conditions.length
        ? `WHERE ${conditions.join(' AND ')}`
        : '',
    };
  }

  private mapAccount(row: AuthAccountRow): AuthAccount {
    return {
      accountId: row.account_id,
      email: row.email,
      passwordHash: row.password_hash,
      status: row.status,
      referralCode: row.referral_code,
      createdAt: this.toIsoString(row.created_at)!,
      updatedAt: this.toIsoString(row.updated_at)!,
    };
  }

  private mapSession(row: AuthSessionRow): AuthSession {
    return {
      sessionId: row.session_id,
      accountId: row.account_id,
      installationId: row.installation_id,
      accessToken: row.access_token,
      refreshToken: row.refresh_token,
      accessTokenExpiresAt: this.toIsoString(row.access_token_expires_at)!,
      refreshTokenExpiresAt: this.toIsoString(row.refresh_token_expires_at)!,
      status: row.status,
      createdAt: this.toIsoString(row.created_at)!,
      updatedAt: this.toIsoString(row.updated_at)!,
    };
  }

  private mapVerificationCode(
    row: VerificationCodeRow,
  ): VerificationCodeRecord {
    return {
      email: row.email,
      purpose: row.purpose,
      codeHash: row.code_hash,
      expiresAt: new Date(row.expires_at).getTime(),
      createdAt: this.toIsoString(row.created_at)!,
      updatedAt: this.toIsoString(row.updated_at)!,
    };
  }

  private toOrderValues(
    order: StoredOrderRecord,
    compositeIdempotencyKey: string,
  ) {
    return [
      order.orderId,
      order.orderNo,
      order.accountId,
      order.planCode,
      order.planName,
      order.orderType,
      order.quoteAssetCode,
      order.quoteNetworkCode,
      order.quoteUsdAmount,
      order.payableAmount,
      order.status,
      order.expiresAt,
      order.confirmedAt,
      order.completedAt,
      order.failureReason,
      order.submittedClientTxHash,
      order.createdAt,
      new Date().toISOString(),
      compositeIdempotencyKey,
      order.collectionAddress,
      order.uniqueAmountDelta,
    ];
  }

  private mapOrder(row: RuntimeStateOrderRow): StoredOrderRecord {
    return {
      orderId: row.order_id,
      orderNo: row.order_no,
      accountId: row.account_id,
      planCode: row.plan_code,
      planName: row.plan_name,
      orderType: row.order_type,
      quoteAssetCode: row.quote_asset_code,
      quoteNetworkCode: row.quote_network_code,
      quoteUsdAmount: row.quote_usd_amount,
      payableAmount: row.payable_amount,
      status: row.status,
      expiresAt: this.toIsoString(row.expires_at)!,
      confirmedAt: this.toIsoString(row.confirmed_at),
      completedAt: this.toIsoString(row.completed_at),
      failureReason: row.failure_reason,
      submittedClientTxHash: row.submitted_client_tx_hash,
      createdAt: this.toIsoString(row.created_at)!,
      idempotencyKey: row.idempotency_key,
      collectionAddress: row.collection_address,
      uniqueAmountDelta: row.unique_amount_delta,
    };
  }

  private mapSubscription(
    row: RuntimeStateSubscriptionRow,
  ): PersistedSubscriptionRecord {
    return {
      accountId: row.account_id,
      orderNo: row.order_no,
      createdAt: this.toIsoString(row.created_at)!,
      updatedAt: this.toIsoString(row.updated_at)!,
      subscriptionId: row.subscription_id,
      planCode: row.plan_code,
      status: row.status,
      startedAt: this.toIsoString(row.started_at),
      expireAt: this.toIsoString(row.expire_at),
      daysRemaining: row.days_remaining,
      isUnlimitedTraffic: row.is_unlimited_traffic,
      maxActiveSessions: row.max_active_sessions,
    };
  }

  private toIsoString(value: Date | string | null): string | null {
    if (!value) {
      return null;
    }
    return value instanceof Date ? value.toISOString() : new Date(value).toISOString();
  }
}
