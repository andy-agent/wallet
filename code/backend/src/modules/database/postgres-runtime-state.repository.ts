import { createHash, randomUUID } from 'crypto';
import { Pool } from 'pg';
import {
  AuthAccount,
  AuthSession,
  VerificationCodeRecord,
} from '../auth/auth.types';
import { OrderStatus } from '../orders/orders.types';
import {
  PersistedWalletChainAccountRecord,
  PersistedWalletKeySlotRecord,
  PersistedWalletLifecycleRecord,
  PersistedWalletPublicAddressRecord,
  PersistedWalletSecretBackupRecord,
  PersistedWalletSecretBackupV2Record,
  PersistedWalletRecord,
} from '../wallet/wallet.types';
import { PersistedSubscriptionRecord } from '../vpn/vpn.types';
import { RuntimeStateRepository } from './runtime-state.repository';
import {
  PaymentScanCursorRecord,
  RuntimeStatePaymentContext,
  RuntimeStateListOrdersParams,
  RuntimeStateListOrdersResult,
  StoredOnchainReceiptRecord,
  StoredOrderRecord,
} from './runtime-state.types';

const ACCOUNTS_TABLE = 'accounts';
const ACCOUNT_INSTALLATIONS_TABLE = 'account_installations';
const SESSIONS_TABLE = 'client_sessions';
const VERIFICATION_CODES_TABLE = 'verification_codes';
const ORDERS_TABLE = 'runtime_state_orders';
const ONCHAIN_RECEIPTS_TABLE = 'runtime_state_onchain_receipts';
const PAYMENT_SCAN_CURSORS_TABLE = 'runtime_state_payment_scan_cursors';
const SUBSCRIPTIONS_TABLE = 'runtime_state_subscriptions';
const WALLET_LIFECYCLES_TABLE = 'runtime_state_wallet_lifecycles';
const WALLET_PUBLIC_ADDRESSES_TABLE = 'runtime_state_wallet_public_addresses';
const WALLET_SECRET_BACKUPS_TABLE = 'runtime_state_wallet_secret_backups';
const WALLETS_TABLE = 'runtime_state_wallets';
const WALLET_KEY_SLOTS_TABLE = 'runtime_state_wallet_key_slots';
const WALLET_CHAIN_ACCOUNTS_TABLE = 'runtime_state_wallet_chain_accounts';
const WALLET_SECRET_BACKUPS_V2_TABLE = 'runtime_state_wallet_secret_backups_v2';
const ACCOUNT_COLUMNS = `
  id::text AS account_id,
  email::text AS email,
  password_hash,
  status::text AS status,
  referral_code,
  created_at,
  updated_at
`;
const SESSION_COLUMNS = `
  id::text AS session_id,
  account_id::text AS account_id,
  installation_id,
  status::text AS status,
  issued_at AS created_at,
  expires_at AS refresh_token_expires_at,
  updated_at
`;
const VERIFICATION_CODE_COLUMNS = `
  email::text AS email,
  purpose::text AS purpose,
  code_hash,
  expire_at AS expires_at,
  created_at,
  updated_at
`;
const ORDER_COLUMNS = `
  order_id,
  order_no,
  account_id,
  payer_wallet_id,
  payer_chain_account_id,
  submitted_from_address,
  plan_code,
  plan_name,
  order_type,
  quote_asset_code,
  quote_network_code,
  quote_usd_amount,
  base_amount,
  unique_amount_delta,
  payable_amount,
  status,
  expires_at,
  confirmed_at,
  completed_at,
  failure_reason,
  submitted_client_tx_hash,
  matched_onchain_tx_hash,
  payment_matched_at,
  matcher_remark,
  created_at,
  idempotency_key,
  collection_address
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
  max_active_sessions,
  marzban_username,
  subscription_url,
  selected_line_code,
  selected_node_id
`;
const WALLET_LIFECYCLE_COLUMNS = `
  account_id,
  wallet_id,
  wallet_name,
  status,
  origin,
  mnemonic_hash,
  mnemonic_word_count,
  backup_acknowledged_at,
  activated_at,
  created_at,
  updated_at
`;
const WALLET_PUBLIC_ADDRESS_COLUMNS = `
  address_id,
  account_id,
  wallet_id,
  network_code,
  asset_code,
  address,
  is_default,
  created_at,
  updated_at
`;
const WALLET_SECRET_BACKUP_COLUMNS = `
  backup_id,
  account_id,
  wallet_id,
  secret_type,
  encryption_scheme,
  recovery_key_version,
  recipient_fingerprint,
  ciphertext,
  replicated_to_backup_server,
  backup_server_reference,
  last_replication_error,
  created_at,
  updated_at
`;
const WALLET_COLUMNS = `
  wallet_id,
  account_id,
  wallet_name,
  wallet_kind,
  source_type,
  is_default,
  is_archived,
  created_at,
  updated_at
`;
const WALLET_KEY_SLOT_COLUMNS = `
  key_slot_id,
  wallet_id,
  slot_code,
  chain_family,
  derivation_type,
  derivation_path,
  created_at,
  updated_at
`;
const WALLET_CHAIN_ACCOUNT_COLUMNS = `
  chain_account_id,
  wallet_id,
  key_slot_id,
  chain_family,
  network_code,
  address,
  capability,
  is_enabled,
  is_default_receive,
  created_at,
  updated_at
`;
const WALLET_SECRET_BACKUP_V2_COLUMNS = `
  backup_id,
  account_id,
  wallet_id,
  secret_type,
  encryption_scheme,
  recovery_key_version,
  recipient_fingerprint,
  ciphertext,
  replicated_to_backup_server,
  backup_server_reference,
  last_replication_error,
  created_at,
  updated_at
`;
const ONCHAIN_RECEIPT_COLUMNS = `
  receipt_id,
  network_code,
  asset_code,
  collection_address,
  tx_hash,
  event_index,
  recipient_token_account,
  from_address,
  mint,
  amount,
  amount_minor,
  confirmation_status,
  slot,
  block_time,
  observed_at,
  matched_order_no,
  match_status,
  matcher_remark,
  raw_payload
`;
const PAYMENT_SCAN_CURSOR_COLUMNS = `
  cursor_key,
  network_code,
  asset_code,
  collection_address,
  before_signature,
  last_signature,
  last_slot,
  updated_at
`;

interface RuntimeStateOrderRow {
  order_id: string;
  order_no: string;
  account_id: string;
  payer_wallet_id: string | null;
  payer_chain_account_id: string | null;
  submitted_from_address: string | null;
  plan_code: string;
  plan_name: string;
  order_type: 'NEW' | 'RENEWAL';
  quote_asset_code: 'SOL' | 'USDT';
  quote_network_code: 'SOLANA' | 'TRON';
  quote_usd_amount: string;
  base_amount: string;
  unique_amount_delta: string;
  payable_amount: string;
  status: StoredOrderRecord['status'];
  expires_at: Date | string;
  confirmed_at: Date | string | null;
  completed_at: Date | string | null;
  failure_reason: string | null;
  submitted_client_tx_hash: string | null;
  matched_onchain_tx_hash: string | null;
  payment_matched_at: Date | string | null;
  matcher_remark: string | null;
  created_at: Date | string;
  idempotency_key: string;
  collection_address: string;
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
  marzban_username: string | null;
  subscription_url: string | null;
  selected_line_code: string | null;
  selected_node_id: string | null;
}

interface RuntimeStateWalletLifecycleRow {
  account_id: string;
  wallet_id: string;
  wallet_name: string;
  status: PersistedWalletLifecycleRecord['status'];
  origin: PersistedWalletLifecycleRecord['origin'];
  mnemonic_hash: string | null;
  mnemonic_word_count: number | null;
  backup_acknowledged_at: Date | string | null;
  activated_at: Date | string | null;
  created_at: Date | string;
  updated_at: Date | string;
}

interface RuntimeStateWalletPublicAddressRow {
  address_id: string;
  account_id: string;
  wallet_id: string | null;
  network_code: PersistedWalletPublicAddressRecord['networkCode'];
  asset_code: PersistedWalletPublicAddressRecord['assetCode'];
  address: string;
  is_default: boolean;
  created_at: Date | string;
  updated_at: Date | string;
}

interface RuntimeStateWalletSecretBackupRow {
  backup_id: string;
  account_id: string;
  wallet_id: string;
  secret_type: PersistedWalletSecretBackupRecord['secretType'];
  encryption_scheme: PersistedWalletSecretBackupRecord['encryptionScheme'];
  recovery_key_version: string;
  recipient_fingerprint: string;
  ciphertext: string;
  replicated_to_backup_server: boolean;
  backup_server_reference: string | null;
  last_replication_error: string | null;
  created_at: Date | string;
  updated_at: Date | string;
}

interface RuntimeStateWalletRow {
  wallet_id: string;
  account_id: string;
  wallet_name: string;
  wallet_kind: PersistedWalletRecord['walletKind'];
  source_type: PersistedWalletRecord['sourceType'];
  is_default: boolean;
  is_archived: boolean;
  created_at: Date | string;
  updated_at: Date | string;
}

interface RuntimeStateWalletKeySlotRow {
  key_slot_id: string;
  wallet_id: string;
  slot_code: string;
  chain_family: PersistedWalletKeySlotRecord['chainFamily'];
  derivation_type: PersistedWalletKeySlotRecord['derivationType'];
  derivation_path: string | null;
  created_at: Date | string;
  updated_at: Date | string;
}

interface RuntimeStateWalletChainAccountRow {
  chain_account_id: string;
  wallet_id: string;
  key_slot_id: string | null;
  chain_family: PersistedWalletChainAccountRecord['chainFamily'];
  network_code: PersistedWalletChainAccountRecord['networkCode'];
  address: string;
  capability: PersistedWalletChainAccountRecord['capability'];
  is_enabled: boolean;
  is_default_receive: boolean;
  created_at: Date | string;
  updated_at: Date | string;
}

interface RuntimeStateWalletSecretBackupV2Row {
  backup_id: string;
  account_id: string;
  wallet_id: string;
  secret_type: PersistedWalletSecretBackupV2Record['secretType'];
  encryption_scheme: PersistedWalletSecretBackupV2Record['encryptionScheme'];
  recovery_key_version: string;
  recipient_fingerprint: string;
  ciphertext: string;
  replicated_to_backup_server: boolean;
  backup_server_reference: string | null;
  last_replication_error: string | null;
  created_at: Date | string;
  updated_at: Date | string;
}

interface RuntimeStateOnchainReceiptRow {
  receipt_id: string;
  network_code: StoredOrderRecord['quoteNetworkCode'];
  asset_code: StoredOrderRecord['quoteAssetCode'];
  collection_address: string;
  tx_hash: string;
  event_index: number;
  recipient_token_account: string | null;
  from_address: string | null;
  mint: string | null;
  amount: string;
  amount_minor: string;
  confirmation_status: StoredOnchainReceiptRecord['confirmationStatus'];
  slot: string | number | null;
  block_time: Date | string | null;
  observed_at: Date | string;
  matched_order_no: string | null;
  match_status: StoredOnchainReceiptRecord['matchStatus'];
  matcher_remark: string | null;
  raw_payload: Record<string, unknown> | null;
}

interface PaymentScanCursorRow {
  cursor_key: string;
  network_code: StoredOrderRecord['quoteNetworkCode'];
  asset_code: StoredOrderRecord['quoteAssetCode'];
  collection_address: string;
  before_signature: string | null;
  last_signature: string | null;
  last_slot: string | number | null;
  updated_at: Date | string;
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
          id,
          account_no,
          email,
          password_hash,
          status,
          referral_code,
          email_verified_at,
          created_at,
          updated_at
        )
        VALUES ($1, $2, $3, $4, $5, $6, $7, $8, $9)
        ON CONFLICT (id) DO UPDATE
        SET
          email = EXCLUDED.email,
          password_hash = EXCLUDED.password_hash,
          status = EXCLUDED.status,
          referral_code = EXCLUDED.referral_code,
          email_verified_at = COALESCE(
            ${ACCOUNTS_TABLE}.email_verified_at,
            EXCLUDED.email_verified_at
          ),
          updated_at = EXCLUDED.updated_at
        RETURNING ${ACCOUNT_COLUMNS}
      `,
      [
        account.accountId,
        this.buildAccountNo(account.accountId),
        account.email,
        account.passwordHash,
        account.status,
        account.referralCode,
        account.status === 'ACTIVE' ? account.createdAt : null,
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
    const result =
      session.accessToken && session.refreshToken
        ? await this.pool.query<AuthSessionRow>(
            `
              INSERT INTO ${SESSIONS_TABLE} (
                id,
                account_id,
                installation_id,
                refresh_token_hash,
                access_jti,
                status,
                invalidated_reason,
                issued_at,
                expires_at,
                last_refresh_at,
                created_at,
                updated_at
              )
              VALUES ($1, $2, $3, $4, $5, $6, $7, $8, $9, $10, $11, $12)
              ON CONFLICT (id) DO UPDATE
              SET
                account_id = EXCLUDED.account_id,
                installation_id = EXCLUDED.installation_id,
                refresh_token_hash = EXCLUDED.refresh_token_hash,
                access_jti = EXCLUDED.access_jti,
                status = EXCLUDED.status,
                invalidated_reason = EXCLUDED.invalidated_reason,
                issued_at = EXCLUDED.issued_at,
                expires_at = EXCLUDED.expires_at,
                last_refresh_at = EXCLUDED.last_refresh_at,
                updated_at = EXCLUDED.updated_at
              RETURNING ${SESSION_COLUMNS}
            `,
            [
              session.sessionId,
              session.accountId,
              session.installationId,
              this.buildRefreshTokenHash(session.refreshToken),
              this.buildSessionAccessJti(session.accessToken),
              session.status,
              this.buildSessionInvalidatedReason(session.status),
              session.createdAt,
              session.refreshTokenExpiresAt,
              null,
              session.createdAt,
              session.updatedAt,
            ],
          )
        : await this.pool.query<AuthSessionRow>(
            `
              UPDATE ${SESSIONS_TABLE}
              SET
                account_id = $2,
                installation_id = $3,
                status = $4,
                invalidated_reason = $5,
                issued_at = $6,
                expires_at = $7,
                updated_at = $8
              WHERE id = $1
              RETURNING ${SESSION_COLUMNS}
            `,
            [
              session.sessionId,
              session.accountId,
              session.installationId,
              session.status,
              this.buildSessionInvalidatedReason(session.status),
              session.createdAt,
              session.refreshTokenExpiresAt,
              session.updatedAt,
            ],
          );

    if (!result.rows[0]) {
      throw new Error(`Missing auth session row for ${session.sessionId}`);
    }

    if (session.installationId) {
      await this.touchInstallation(session);
    }

    return this.mapSession(result.rows[0], session);
  }

  async listVerificationCodes(): Promise<VerificationCodeRecord[]> {
    await this.ensureReady();
    const result = await this.pool.query<VerificationCodeRow>(
      `
        SELECT ${VERIFICATION_CODE_COLUMNS}
        FROM ${VERIFICATION_CODES_TABLE}
        WHERE status = 'PENDING'
        ORDER BY created_at ASC
      `,
    );
    return result.rows.map((row) => this.mapVerificationCode(row));
  }

  async saveVerificationCode(
    record: VerificationCodeRecord,
  ): Promise<VerificationCodeRecord> {
    await this.ensureReady();
    await this.pool.query(
      `
        DELETE FROM ${VERIFICATION_CODES_TABLE}
        WHERE email = $1 AND purpose = $2
      `,
      [record.email, record.purpose],
    );

    const result = await this.pool.query<VerificationCodeRow>(
      `
        INSERT INTO ${VERIFICATION_CODES_TABLE} (
          id,
          email,
          purpose,
          code_hash,
          expire_at,
          status,
          created_at,
          updated_at
        )
        VALUES ($1, $2, $3, $4, $5, 'PENDING', $6, $7)
        RETURNING ${VERIFICATION_CODE_COLUMNS}
      `,
      [
        randomUUID(),
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
          payer_wallet_id,
          payer_chain_account_id,
          submitted_from_address,
          plan_code,
          plan_name,
          order_type,
          quote_asset_code,
          quote_network_code,
          quote_usd_amount,
          base_amount,
          unique_amount_delta,
          payable_amount,
          status,
          expires_at,
          confirmed_at,
          completed_at,
          failure_reason,
          submitted_client_tx_hash,
          matched_onchain_tx_hash,
          payment_matched_at,
          matcher_remark,
          created_at,
          updated_at,
          idempotency_key,
          collection_address
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
          $21,
          $22,
          $23,
          $24,
          $25,
          $26,
          $27,
          $28
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
          payer_wallet_id,
          payer_chain_account_id,
          submitted_from_address,
          plan_code,
          plan_name,
          order_type,
          quote_asset_code,
          quote_network_code,
          quote_usd_amount,
          base_amount,
          unique_amount_delta,
          payable_amount,
          status,
          expires_at,
          confirmed_at,
          completed_at,
          failure_reason,
          submitted_client_tx_hash,
          matched_onchain_tx_hash,
          payment_matched_at,
          matcher_remark,
          created_at,
          updated_at,
          idempotency_key,
          collection_address
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
          $21,
          $22,
          $23,
          $24,
          $25,
          $26,
          $27,
          $28
        )
        ON CONFLICT (order_no) DO UPDATE
        SET
          order_id = EXCLUDED.order_id,
          account_id = EXCLUDED.account_id,
          payer_wallet_id = EXCLUDED.payer_wallet_id,
          payer_chain_account_id = EXCLUDED.payer_chain_account_id,
          submitted_from_address = EXCLUDED.submitted_from_address,
          plan_code = EXCLUDED.plan_code,
          plan_name = EXCLUDED.plan_name,
          order_type = EXCLUDED.order_type,
          quote_asset_code = EXCLUDED.quote_asset_code,
          quote_network_code = EXCLUDED.quote_network_code,
          quote_usd_amount = EXCLUDED.quote_usd_amount,
          base_amount = EXCLUDED.base_amount,
          unique_amount_delta = EXCLUDED.unique_amount_delta,
          payable_amount = EXCLUDED.payable_amount,
          status = EXCLUDED.status,
          expires_at = EXCLUDED.expires_at,
          confirmed_at = EXCLUDED.confirmed_at,
          completed_at = EXCLUDED.completed_at,
          failure_reason = EXCLUDED.failure_reason,
          submitted_client_tx_hash = EXCLUDED.submitted_client_tx_hash,
          matched_onchain_tx_hash = EXCLUDED.matched_onchain_tx_hash,
          payment_matched_at = EXCLUDED.payment_matched_at,
          matcher_remark = EXCLUDED.matcher_remark,
          created_at = EXCLUDED.created_at,
          updated_at = EXCLUDED.updated_at,
          idempotency_key = EXCLUDED.idempotency_key,
          collection_address = EXCLUDED.collection_address
        RETURNING ${ORDER_COLUMNS}
      `,
      this.toOrderValues(order, order.idempotencyKey),
    );

    return this.mapOrder(result.rows[0]);
  }

  async listActiveOrdersForPaymentContext(params: {
    collectionAddress: string;
    quoteAssetCode: StoredOrderRecord['quoteAssetCode'];
    quoteNetworkCode: StoredOrderRecord['quoteNetworkCode'];
    statuses: OrderStatus[];
    activeAfter: number;
  }): Promise<StoredOrderRecord[]> {
    await this.ensureReady();
    const result = await this.pool.query<RuntimeStateOrderRow>(
      `
        SELECT ${ORDER_COLUMNS}
        FROM ${ORDERS_TABLE}
        WHERE collection_address = $1
          AND quote_asset_code = $2
          AND quote_network_code = $3
          AND status = ANY($4::text[])
          AND expires_at > $5
        ORDER BY created_at ASC
      `,
      [
        params.collectionAddress,
        params.quoteAssetCode,
        params.quoteNetworkCode,
        params.statuses,
        new Date(params.activeAfter).toISOString(),
      ],
    );
    return result.rows.map((row) => this.mapOrder(row));
  }

  async listActivePaymentContexts(params: {
    statuses: OrderStatus[];
    activeAfter: number;
  }): Promise<RuntimeStatePaymentContext[]> {
    await this.ensureReady();
    const result = await this.pool.query<{
      collection_address: string;
      quote_asset_code: StoredOrderRecord['quoteAssetCode'];
      quote_network_code: StoredOrderRecord['quoteNetworkCode'];
    }>(
      `
        SELECT DISTINCT
          collection_address,
          quote_asset_code,
          quote_network_code
        FROM ${ORDERS_TABLE}
        WHERE status = ANY($1::text[])
          AND expires_at > $2
        ORDER BY collection_address, quote_asset_code, quote_network_code
      `,
      [params.statuses, new Date(params.activeAfter).toISOString()],
    );

    return result.rows.map((row) => ({
      collectionAddress: row.collection_address,
      quoteAssetCode: row.quote_asset_code,
      quoteNetworkCode: row.quote_network_code,
    }));
  }

  async findPaymentScanCursor(
    context: RuntimeStatePaymentContext,
  ): Promise<PaymentScanCursorRecord | null> {
    await this.ensureReady();
    const result = await this.pool.query<PaymentScanCursorRow>(
      `
        SELECT ${PAYMENT_SCAN_CURSOR_COLUMNS}
        FROM ${PAYMENT_SCAN_CURSORS_TABLE}
        WHERE network_code = $1
          AND asset_code = $2
          AND collection_address = $3
        LIMIT 1
      `,
      [
        context.quoteNetworkCode,
        context.quoteAssetCode,
        context.collectionAddress,
      ],
    );

    return result.rows[0] ? this.mapPaymentScanCursor(result.rows[0]) : null;
  }

  async savePaymentScanCursor(
    cursor: PaymentScanCursorRecord,
  ): Promise<PaymentScanCursorRecord> {
    await this.ensureReady();
    const result = await this.pool.query<PaymentScanCursorRow>(
      `
        INSERT INTO ${PAYMENT_SCAN_CURSORS_TABLE} (
          cursor_key,
          network_code,
          asset_code,
          collection_address,
          before_signature,
          last_signature,
          last_slot,
          updated_at
        )
        VALUES ($1, $2, $3, $4, $5, $6, $7, $8)
        ON CONFLICT (cursor_key) DO UPDATE
        SET
          network_code = EXCLUDED.network_code,
          asset_code = EXCLUDED.asset_code,
          collection_address = EXCLUDED.collection_address,
          before_signature = EXCLUDED.before_signature,
          last_signature = EXCLUDED.last_signature,
          last_slot = EXCLUDED.last_slot,
          updated_at = EXCLUDED.updated_at
        RETURNING ${PAYMENT_SCAN_CURSOR_COLUMNS}
      `,
      [
        cursor.cursorKey,
        cursor.quoteNetworkCode,
        cursor.quoteAssetCode,
        cursor.collectionAddress,
        cursor.beforeSignature,
        cursor.lastSignature,
        cursor.lastSlot,
        cursor.updatedAt,
      ],
    );

    return this.mapPaymentScanCursor(result.rows[0]);
  }

  async upsertOnchainReceipt(
    receipt: StoredOnchainReceiptRecord,
  ): Promise<StoredOnchainReceiptRecord> {
    await this.ensureReady();
    const result = await this.pool.query<RuntimeStateOnchainReceiptRow>(
      `
        INSERT INTO ${ONCHAIN_RECEIPTS_TABLE} (
          receipt_id,
          network_code,
          asset_code,
          collection_address,
          tx_hash,
          event_index,
          recipient_token_account,
          from_address,
          mint,
          amount,
          amount_minor,
          confirmation_status,
          slot,
          block_time,
          observed_at,
          matched_order_no,
          match_status,
          matcher_remark,
          raw_payload,
          created_at,
          updated_at
        )
        VALUES (
          $1, $2, $3, $4, $5, $6, $7, $8, $9, $10,
          $11, $12, $13, $14, $15, $16, $17, $18,
          $19::jsonb, NOW(), NOW()
        )
        ON CONFLICT (network_code, tx_hash, event_index) DO UPDATE
        SET
          asset_code = EXCLUDED.asset_code,
          collection_address = EXCLUDED.collection_address,
          recipient_token_account = EXCLUDED.recipient_token_account,
          from_address = EXCLUDED.from_address,
          mint = EXCLUDED.mint,
          amount = EXCLUDED.amount,
          amount_minor = EXCLUDED.amount_minor,
          confirmation_status = EXCLUDED.confirmation_status,
          slot = EXCLUDED.slot,
          block_time = EXCLUDED.block_time,
          observed_at = EXCLUDED.observed_at,
          matched_order_no = EXCLUDED.matched_order_no,
          match_status = EXCLUDED.match_status,
          matcher_remark = EXCLUDED.matcher_remark,
          raw_payload = EXCLUDED.raw_payload,
          updated_at = NOW()
        RETURNING ${ONCHAIN_RECEIPT_COLUMNS}
      `,
      [
        receipt.receiptId,
        receipt.quoteNetworkCode,
        receipt.quoteAssetCode,
        receipt.collectionAddress,
        receipt.txHash,
        receipt.eventIndex,
        receipt.recipientTokenAccount,
        receipt.fromAddress,
        receipt.mint,
        receipt.amount,
        receipt.amountMinor,
        receipt.confirmationStatus,
        receipt.slot,
        receipt.blockTime,
        receipt.observedAt,
        receipt.matchedOrderNo,
        receipt.matchStatus,
        receipt.matcherRemark,
        receipt.rawPayload ? JSON.stringify(receipt.rawPayload) : null,
      ],
    );

    return this.mapOnchainReceipt(result.rows[0]);
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
          max_active_sessions,
          marzban_username,
          subscription_url,
          selected_line_code,
          selected_node_id
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
          $16
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
          max_active_sessions = EXCLUDED.max_active_sessions,
          marzban_username = EXCLUDED.marzban_username,
          subscription_url = EXCLUDED.subscription_url,
          selected_line_code = EXCLUDED.selected_line_code,
          selected_node_id = EXCLUDED.selected_node_id
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
        subscription.marzbanUsername,
        subscription.subscriptionUrl,
        subscription.selectedLineCode,
        subscription.selectedNodeId,
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

  async findWalletLifecycleByAccountId(
    accountId: string,
  ): Promise<PersistedWalletLifecycleRecord | null> {
    await this.ensureReady();
    const result = await this.pool.query<RuntimeStateWalletLifecycleRow>(
      `
        SELECT ${WALLET_LIFECYCLE_COLUMNS}
        FROM ${WALLET_LIFECYCLES_TABLE}
        WHERE account_id = $1
        LIMIT 1
      `,
      [accountId],
    );

    return result.rows[0] ? this.mapWalletLifecycle(result.rows[0]) : null;
  }

  async upsertWalletLifecycle(
    record: PersistedWalletLifecycleRecord,
  ): Promise<PersistedWalletLifecycleRecord> {
    await this.ensureReady();
    const result = await this.pool.query<RuntimeStateWalletLifecycleRow>(
      `
        INSERT INTO ${WALLET_LIFECYCLES_TABLE} (
          account_id,
          wallet_id,
          wallet_name,
          status,
          origin,
          mnemonic_hash,
          mnemonic_word_count,
          backup_acknowledged_at,
          activated_at,
          created_at,
          updated_at
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
          $11
        )
        ON CONFLICT (account_id) DO UPDATE
        SET
          wallet_id = EXCLUDED.wallet_id,
          wallet_name = EXCLUDED.wallet_name,
          status = EXCLUDED.status,
          origin = EXCLUDED.origin,
          mnemonic_hash = EXCLUDED.mnemonic_hash,
          mnemonic_word_count = EXCLUDED.mnemonic_word_count,
          backup_acknowledged_at = EXCLUDED.backup_acknowledged_at,
          activated_at = EXCLUDED.activated_at,
          created_at = EXCLUDED.created_at,
          updated_at = EXCLUDED.updated_at
        RETURNING ${WALLET_LIFECYCLE_COLUMNS}
      `,
      [
        record.accountId,
        record.walletId,
        record.walletName,
        record.status,
        record.origin,
        record.mnemonicHash,
        record.mnemonicWordCount,
        record.backupAcknowledgedAt,
        record.activatedAt,
        record.createdAt,
        record.updatedAt,
      ],
    );

    return this.mapWalletLifecycle(result.rows[0]);
  }

  async listWalletPublicAddressesByAccountId(params: {
    accountId: string;
    networkCode?: PersistedWalletPublicAddressRecord['networkCode'];
    assetCode?: PersistedWalletPublicAddressRecord['assetCode'];
  }): Promise<PersistedWalletPublicAddressRecord[]> {
    await this.ensureReady();

    const values: string[] = [params.accountId];
    const conditions = [`account_id = $1`];

    if (params.networkCode) {
      values.push(params.networkCode);
      conditions.push(`network_code = $${values.length}`);
    }

    if (params.assetCode) {
      values.push(params.assetCode);
      conditions.push(`asset_code = $${values.length}`);
    }

    const result = await this.pool.query<RuntimeStateWalletPublicAddressRow>(
      `
        SELECT ${WALLET_PUBLIC_ADDRESS_COLUMNS}
        FROM ${WALLET_PUBLIC_ADDRESSES_TABLE}
        WHERE ${conditions.join(' AND ')}
        ORDER BY is_default DESC, created_at ASC
      `,
      values,
    );

    return result.rows.map((row) => this.mapWalletPublicAddress(row));
  }

  async countWalletPublicAddressesByAccountId(accountId: string): Promise<number> {
    await this.ensureReady();
    const result = await this.pool.query<CountRow>(
      `
        SELECT COUNT(*)::int AS count
        FROM ${WALLET_PUBLIC_ADDRESSES_TABLE}
        WHERE account_id = $1
      `,
      [accountId],
    );
    return result.rows[0]?.count ?? 0;
  }

  async upsertWalletPublicAddress(
    record: PersistedWalletPublicAddressRecord,
  ): Promise<PersistedWalletPublicAddressRecord> {
    await this.ensureReady();
    const client = await this.pool.connect();
    try {
      await client.query('BEGIN');

      if (record.isDefault) {
        await client.query(
          `
            UPDATE ${WALLET_PUBLIC_ADDRESSES_TABLE}
            SET
              is_default = false,
              updated_at = $5
            WHERE account_id = $1
              AND network_code = $2
              AND asset_code = $3
              AND address <> $4
          `,
          [
            record.accountId,
            record.networkCode,
            record.assetCode,
            record.address,
            record.updatedAt,
          ],
        );
      }

      const result = await client.query<RuntimeStateWalletPublicAddressRow>(
        `
          INSERT INTO ${WALLET_PUBLIC_ADDRESSES_TABLE} (
            address_id,
            account_id,
            wallet_id,
            network_code,
            asset_code,
            address,
            is_default,
            created_at,
            updated_at
          )
          VALUES ($1, $2, $3, $4, $5, $6, $7, $8, $9)
          ON CONFLICT (account_id, network_code, asset_code, address) DO UPDATE
          SET
            wallet_id = EXCLUDED.wallet_id,
            is_default = EXCLUDED.is_default,
            updated_at = EXCLUDED.updated_at
          RETURNING ${WALLET_PUBLIC_ADDRESS_COLUMNS}
        `,
        [
          record.addressId,
          record.accountId,
          record.walletId,
          record.networkCode,
          record.assetCode,
          record.address,
          record.isDefault,
          record.createdAt,
          record.updatedAt,
        ],
      );

      await client.query('COMMIT');
      return this.mapWalletPublicAddress(result.rows[0]);
    } catch (error) {
      await client.query('ROLLBACK');
      throw error;
    } finally {
      client.release();
    }
  }

  async findWalletSecretBackupByAccountId(
    accountId: string,
  ): Promise<PersistedWalletSecretBackupRecord | null> {
    await this.ensureReady();
    const result = await this.pool.query<RuntimeStateWalletSecretBackupRow>(
      `
        SELECT ${WALLET_SECRET_BACKUP_COLUMNS}
        FROM ${WALLET_SECRET_BACKUPS_TABLE}
        WHERE account_id = $1
        LIMIT 1
      `,
      [accountId],
    );

    return result.rows[0] ? this.mapWalletSecretBackup(result.rows[0]) : null;
  }

  async upsertWalletSecretBackup(
    record: PersistedWalletSecretBackupRecord,
  ): Promise<PersistedWalletSecretBackupRecord> {
    await this.ensureReady();
    const result = await this.pool.query<RuntimeStateWalletSecretBackupRow>(
      `
        INSERT INTO ${WALLET_SECRET_BACKUPS_TABLE} (
          backup_id,
          account_id,
          wallet_id,
          secret_type,
          encryption_scheme,
          recovery_key_version,
          recipient_fingerprint,
          ciphertext,
          replicated_to_backup_server,
          backup_server_reference,
          last_replication_error,
          created_at,
          updated_at
        )
        VALUES (
          $1,$2,$3,$4,$5,$6,$7,$8,$9,$10,$11,$12,$13
        )
        ON CONFLICT (account_id) DO UPDATE
        SET
          backup_id = EXCLUDED.backup_id,
          wallet_id = EXCLUDED.wallet_id,
          secret_type = EXCLUDED.secret_type,
          encryption_scheme = EXCLUDED.encryption_scheme,
          recovery_key_version = EXCLUDED.recovery_key_version,
          recipient_fingerprint = EXCLUDED.recipient_fingerprint,
          ciphertext = EXCLUDED.ciphertext,
          replicated_to_backup_server = EXCLUDED.replicated_to_backup_server,
          backup_server_reference = EXCLUDED.backup_server_reference,
          last_replication_error = EXCLUDED.last_replication_error,
          created_at = EXCLUDED.created_at,
          updated_at = EXCLUDED.updated_at
        RETURNING ${WALLET_SECRET_BACKUP_COLUMNS}
      `,
      [
        record.backupId,
        record.accountId,
        record.walletId,
        record.secretType,
        record.encryptionScheme,
        record.recoveryKeyVersion,
        record.recipientFingerprint,
        record.ciphertext,
        record.replicatedToBackupServer,
        record.backupServerReference,
        record.lastReplicationError,
        record.createdAt,
        record.updatedAt,
      ],
    );

    return this.mapWalletSecretBackup(result.rows[0]);
  }

  async listWalletsByAccountId(
    accountId: string,
  ): Promise<PersistedWalletRecord[]> {
    await this.ensureReady();
    const result = await this.pool.query<RuntimeStateWalletRow>(
      `
        SELECT ${WALLET_COLUMNS}
        FROM ${WALLETS_TABLE}
        WHERE account_id = $1
        ORDER BY is_default DESC, created_at ASC
      `,
      [accountId],
    );
    return result.rows.map((row) => this.mapWallet(row));
  }

  async findWalletById(walletId: string): Promise<PersistedWalletRecord | null> {
    await this.ensureReady();
    const result = await this.pool.query<RuntimeStateWalletRow>(
      `
        SELECT ${WALLET_COLUMNS}
        FROM ${WALLETS_TABLE}
        WHERE wallet_id = $1
        LIMIT 1
      `,
      [walletId],
    );
    return result.rows[0] ? this.mapWallet(result.rows[0]) : null;
  }

  async insertWallet(wallet: PersistedWalletRecord): Promise<PersistedWalletRecord> {
    await this.ensureReady();
    const result = await this.pool.query<RuntimeStateWalletRow>(
      `
        INSERT INTO ${WALLETS_TABLE} (
          wallet_id,
          account_id,
          wallet_name,
          wallet_kind,
          source_type,
          is_default,
          is_archived,
          created_at,
          updated_at
        )
        VALUES ($1,$2,$3,$4,$5,$6,$7,$8,$9)
        ON CONFLICT (wallet_id) DO UPDATE
        SET
          account_id = EXCLUDED.account_id,
          wallet_name = EXCLUDED.wallet_name,
          wallet_kind = EXCLUDED.wallet_kind,
          source_type = EXCLUDED.source_type,
          is_default = EXCLUDED.is_default,
          is_archived = EXCLUDED.is_archived,
          created_at = EXCLUDED.created_at,
          updated_at = EXCLUDED.updated_at
        RETURNING ${WALLET_COLUMNS}
      `,
      [
        wallet.walletId,
        wallet.accountId,
        wallet.walletName,
        wallet.walletKind,
        wallet.sourceType,
        wallet.isDefault,
        wallet.isArchived,
        wallet.createdAt,
        wallet.updatedAt,
      ],
    );
    return this.mapWallet(result.rows[0]);
  }

  async updateWallet(wallet: PersistedWalletRecord): Promise<PersistedWalletRecord> {
    return this.insertWallet(wallet);
  }

  async setDefaultWallet(
    accountId: string,
    walletId: string,
  ): Promise<PersistedWalletRecord> {
    await this.ensureReady();
    await this.pool.query(
      `
        UPDATE ${WALLETS_TABLE}
        SET is_default = false
        WHERE account_id = $1
      `,
      [accountId],
    );
    const result = await this.pool.query<RuntimeStateWalletRow>(
      `
        UPDATE ${WALLETS_TABLE}
        SET is_default = true, updated_at = NOW()
        WHERE account_id = $1 AND wallet_id = $2
        RETURNING ${WALLET_COLUMNS}
      `,
      [accountId, walletId],
    );
    if (!result.rows[0]) {
      throw new Error(`Wallet ${walletId} not found for account ${accountId}`);
    }
    return this.mapWallet(result.rows[0]);
  }

  async listWalletKeySlotsByWalletId(
    walletId: string,
  ): Promise<PersistedWalletKeySlotRecord[]> {
    await this.ensureReady();
    const result = await this.pool.query<RuntimeStateWalletKeySlotRow>(
      `
        SELECT ${WALLET_KEY_SLOT_COLUMNS}
        FROM ${WALLET_KEY_SLOTS_TABLE}
        WHERE wallet_id = $1
        ORDER BY slot_code ASC
      `,
      [walletId],
    );
    return result.rows.map((row) => this.mapWalletKeySlot(row));
  }

  async insertWalletKeySlot(
    keySlot: PersistedWalletKeySlotRecord,
  ): Promise<PersistedWalletKeySlotRecord> {
    await this.ensureReady();
    const result = await this.pool.query<RuntimeStateWalletKeySlotRow>(
      `
        INSERT INTO ${WALLET_KEY_SLOTS_TABLE} (
          key_slot_id,
          wallet_id,
          slot_code,
          chain_family,
          derivation_type,
          derivation_path,
          created_at,
          updated_at
        )
        VALUES ($1,$2,$3,$4,$5,$6,$7,$8)
        ON CONFLICT (key_slot_id) DO UPDATE
        SET
          wallet_id = EXCLUDED.wallet_id,
          slot_code = EXCLUDED.slot_code,
          chain_family = EXCLUDED.chain_family,
          derivation_type = EXCLUDED.derivation_type,
          derivation_path = EXCLUDED.derivation_path,
          created_at = EXCLUDED.created_at,
          updated_at = EXCLUDED.updated_at
        RETURNING ${WALLET_KEY_SLOT_COLUMNS}
      `,
      [
        keySlot.keySlotId,
        keySlot.walletId,
        keySlot.slotCode,
        keySlot.chainFamily,
        keySlot.derivationType,
        keySlot.derivationPath,
        keySlot.createdAt,
        keySlot.updatedAt,
      ],
    );
    return this.mapWalletKeySlot(result.rows[0]);
  }

  async listWalletChainAccountsByWalletId(
    walletId: string,
  ): Promise<PersistedWalletChainAccountRecord[]> {
    await this.ensureReady();
    const result = await this.pool.query<RuntimeStateWalletChainAccountRow>(
      `
        SELECT ${WALLET_CHAIN_ACCOUNT_COLUMNS}
        FROM ${WALLET_CHAIN_ACCOUNTS_TABLE}
        WHERE wallet_id = $1
        ORDER BY network_code ASC, created_at ASC
      `,
      [walletId],
    );
    return result.rows.map((row) => this.mapWalletChainAccount(row));
  }

  async findWalletChainAccountById(
    chainAccountId: string,
  ): Promise<PersistedWalletChainAccountRecord | null> {
    await this.ensureReady();
    const result = await this.pool.query<RuntimeStateWalletChainAccountRow>(
      `
        SELECT ${WALLET_CHAIN_ACCOUNT_COLUMNS}
        FROM ${WALLET_CHAIN_ACCOUNTS_TABLE}
        WHERE chain_account_id = $1
        LIMIT 1
      `,
      [chainAccountId],
    );
    return result.rows[0] ? this.mapWalletChainAccount(result.rows[0]) : null;
  }

  async insertWalletChainAccount(
    chainAccount: PersistedWalletChainAccountRecord,
  ): Promise<PersistedWalletChainAccountRecord> {
    await this.ensureReady();
    const result = await this.pool.query<RuntimeStateWalletChainAccountRow>(
      `
        INSERT INTO ${WALLET_CHAIN_ACCOUNTS_TABLE} (
          chain_account_id,
          wallet_id,
          key_slot_id,
          chain_family,
          network_code,
          address,
          capability,
          is_enabled,
          is_default_receive,
          created_at,
          updated_at
        )
        VALUES ($1,$2,$3,$4,$5,$6,$7,$8,$9,$10,$11)
        ON CONFLICT (chain_account_id) DO UPDATE
        SET
          wallet_id = EXCLUDED.wallet_id,
          key_slot_id = EXCLUDED.key_slot_id,
          chain_family = EXCLUDED.chain_family,
          network_code = EXCLUDED.network_code,
          address = EXCLUDED.address,
          capability = EXCLUDED.capability,
          is_enabled = EXCLUDED.is_enabled,
          is_default_receive = EXCLUDED.is_default_receive,
          created_at = EXCLUDED.created_at,
          updated_at = EXCLUDED.updated_at
        RETURNING ${WALLET_CHAIN_ACCOUNT_COLUMNS}
      `,
      [
        chainAccount.chainAccountId,
        chainAccount.walletId,
        chainAccount.keySlotId,
        chainAccount.chainFamily,
        chainAccount.networkCode,
        chainAccount.address,
        chainAccount.capability,
        chainAccount.isEnabled,
        chainAccount.isDefaultReceive,
        chainAccount.createdAt,
        chainAccount.updatedAt,
      ],
    );
    return this.mapWalletChainAccount(result.rows[0]);
  }

  async findWalletSecretBackupByWalletId(
    walletId: string,
  ): Promise<PersistedWalletSecretBackupV2Record | null> {
    await this.ensureReady();
    const result = await this.pool.query<RuntimeStateWalletSecretBackupV2Row>(
      `
        SELECT ${WALLET_SECRET_BACKUP_V2_COLUMNS}
        FROM ${WALLET_SECRET_BACKUPS_V2_TABLE}
        WHERE wallet_id = $1
        LIMIT 1
      `,
      [walletId],
    );
    return result.rows[0] ? this.mapWalletSecretBackupV2(result.rows[0]) : null;
  }

  async upsertWalletSecretBackupV2(
    record: PersistedWalletSecretBackupV2Record,
  ): Promise<PersistedWalletSecretBackupV2Record> {
    await this.ensureReady();
    const result = await this.pool.query<RuntimeStateWalletSecretBackupV2Row>(
      `
        INSERT INTO ${WALLET_SECRET_BACKUPS_V2_TABLE} (
          backup_id,
          account_id,
          wallet_id,
          secret_type,
          encryption_scheme,
          recovery_key_version,
          recipient_fingerprint,
          ciphertext,
          replicated_to_backup_server,
          backup_server_reference,
          last_replication_error,
          created_at,
          updated_at
        )
        VALUES ($1,$2,$3,$4,$5,$6,$7,$8,$9,$10,$11,$12,$13)
        ON CONFLICT (wallet_id) DO UPDATE
        SET
          backup_id = EXCLUDED.backup_id,
          account_id = EXCLUDED.account_id,
          secret_type = EXCLUDED.secret_type,
          encryption_scheme = EXCLUDED.encryption_scheme,
          recovery_key_version = EXCLUDED.recovery_key_version,
          recipient_fingerprint = EXCLUDED.recipient_fingerprint,
          ciphertext = EXCLUDED.ciphertext,
          replicated_to_backup_server = EXCLUDED.replicated_to_backup_server,
          backup_server_reference = EXCLUDED.backup_server_reference,
          last_replication_error = EXCLUDED.last_replication_error,
          created_at = EXCLUDED.created_at,
          updated_at = EXCLUDED.updated_at
        RETURNING ${WALLET_SECRET_BACKUP_V2_COLUMNS}
      `,
      [
        record.backupId,
        record.accountId,
        record.walletId,
        record.secretType,
        record.encryptionScheme,
        record.recoveryKeyVersion,
        record.recipientFingerprint,
        record.ciphertext,
        record.replicatedToBackupServer,
        record.backupServerReference,
        record.lastReplicationError,
        record.createdAt,
        record.updatedAt,
      ],
    );
    return this.mapWalletSecretBackupV2(result.rows[0]);
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
        id text PRIMARY KEY,
        account_no text NOT NULL UNIQUE,
        email text NOT NULL UNIQUE,
        password_hash text NOT NULL,
        status text NOT NULL DEFAULT 'PENDING_VERIFY',
        referral_code text NOT NULL UNIQUE,
        inviter_account_id text NULL,
        email_verified_at timestamptz NULL,
        risk_level integer NOT NULL DEFAULT 0,
        last_login_at timestamptz NULL,
        created_at timestamptz NOT NULL,
        updated_at timestamptz NOT NULL
      )
    `);
    await this.pool.query(`
      CREATE INDEX IF NOT EXISTS idx_accounts_status
      ON ${ACCOUNTS_TABLE} (status)
    `);

    await this.pool.query(`
      CREATE TABLE IF NOT EXISTS ${ACCOUNT_INSTALLATIONS_TABLE} (
        id text PRIMARY KEY,
        account_id text NOT NULL REFERENCES ${ACCOUNTS_TABLE} (id) ON DELETE CASCADE,
        installation_id text NOT NULL,
        device_name text NULL,
        brand text NULL,
        model text NULL,
        os_version text NULL,
        app_version text NULL,
        status text NOT NULL DEFAULT 'OBSERVED',
        first_seen_at timestamptz NOT NULL,
        last_seen_at timestamptz NULL,
        created_at timestamptz NOT NULL,
        updated_at timestamptz NOT NULL,
        UNIQUE (account_id, installation_id)
      )
    `);
    await this.pool.query(`
      CREATE INDEX IF NOT EXISTS idx_account_installations_last_seen_at
      ON ${ACCOUNT_INSTALLATIONS_TABLE} (last_seen_at)
    `);

    await this.pool.query(`
      CREATE TABLE IF NOT EXISTS ${SESSIONS_TABLE} (
        id text PRIMARY KEY,
        account_id text NOT NULL REFERENCES ${ACCOUNTS_TABLE} (id) ON DELETE CASCADE,
        installation_id text NULL,
        refresh_token_hash text NOT NULL UNIQUE,
        access_jti text NULL,
        status text NOT NULL DEFAULT 'ACTIVE',
        invalidated_reason text NULL,
        ip text NULL,
        user_agent text NULL,
        issued_at timestamptz NOT NULL,
        expires_at timestamptz NOT NULL,
        last_refresh_at timestamptz NULL,
        created_at timestamptz NOT NULL,
        updated_at timestamptz NOT NULL
      )
    `);
    await this.pool.query(`
      CREATE INDEX IF NOT EXISTS idx_client_sessions_account_status
      ON ${SESSIONS_TABLE} (account_id, status)
    `);
    await this.pool.query(`
      CREATE INDEX IF NOT EXISTS idx_client_sessions_expires_at
      ON ${SESSIONS_TABLE} (expires_at)
    `);

    await this.pool.query(`
      CREATE TABLE IF NOT EXISTS ${VERIFICATION_CODES_TABLE} (
        id text PRIMARY KEY,
        email text NOT NULL,
        purpose text NOT NULL,
        code_hash text NOT NULL,
        status text NOT NULL DEFAULT 'PENDING',
        request_ip text NULL,
        expire_at timestamptz NOT NULL,
        consumed_at timestamptz NULL,
        created_at timestamptz NOT NULL,
        updated_at timestamptz NOT NULL
      )
    `);
    await this.pool.query(`
      CREATE INDEX IF NOT EXISTS idx_verification_codes_email_purpose_status
      ON ${VERIFICATION_CODES_TABLE} (email, purpose, status)
    `);
    await this.pool.query(`
      CREATE INDEX IF NOT EXISTS idx_verification_codes_expires_at
      ON ${VERIFICATION_CODES_TABLE} (expire_at)
    `);

    await this.pool.query(`
      CREATE TABLE IF NOT EXISTS ${ORDERS_TABLE} (
        order_no text PRIMARY KEY,
        order_id text NOT NULL UNIQUE,
        account_id text NOT NULL,
        payer_wallet_id text NULL,
        payer_chain_account_id text NULL,
        submitted_from_address text NULL,
        plan_code text NOT NULL,
        plan_name text NOT NULL,
        order_type text NOT NULL,
        quote_asset_code text NOT NULL,
        quote_network_code text NOT NULL,
        quote_usd_amount text NOT NULL,
        base_amount text NOT NULL DEFAULT '0',
        unique_amount_delta text NOT NULL DEFAULT '0',
        payable_amount text NOT NULL,
        status text NOT NULL,
        expires_at timestamptz NOT NULL,
        confirmed_at timestamptz NULL,
        completed_at timestamptz NULL,
        failure_reason text NULL,
        submitted_client_tx_hash text NULL,
        matched_onchain_tx_hash text NULL,
        payment_matched_at timestamptz NULL,
        matcher_remark text NULL,
        created_at timestamptz NOT NULL,
        updated_at timestamptz NOT NULL DEFAULT NOW(),
        idempotency_key text NOT NULL UNIQUE,
        collection_address text NOT NULL
      )
    `);
    await this.pool.query(`
      ALTER TABLE ${ORDERS_TABLE}
      ADD COLUMN IF NOT EXISTS payer_wallet_id text NULL
    `);
    await this.pool.query(`
      ALTER TABLE ${ORDERS_TABLE}
      ADD COLUMN IF NOT EXISTS payer_chain_account_id text NULL
    `);
    await this.pool.query(`
      ALTER TABLE ${ORDERS_TABLE}
      ADD COLUMN IF NOT EXISTS submitted_from_address text NULL
    `);
    await this.pool.query(`
      ALTER TABLE ${ORDERS_TABLE}
      ADD COLUMN IF NOT EXISTS base_amount text NOT NULL DEFAULT '0'
    `);
    await this.pool.query(`
      ALTER TABLE ${ORDERS_TABLE}
      ADD COLUMN IF NOT EXISTS unique_amount_delta text NOT NULL DEFAULT '0'
    `);
    await this.pool.query(`
      ALTER TABLE ${ORDERS_TABLE}
      ADD COLUMN IF NOT EXISTS matched_onchain_tx_hash text NULL
    `);
    await this.pool.query(`
      ALTER TABLE ${ORDERS_TABLE}
      ADD COLUMN IF NOT EXISTS payment_matched_at timestamptz NULL
    `);
    await this.pool.query(`
      ALTER TABLE ${ORDERS_TABLE}
      ADD COLUMN IF NOT EXISTS matcher_remark text NULL
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
      CREATE INDEX IF NOT EXISTS idx_runtime_state_orders_payment_context
      ON ${ORDERS_TABLE} (collection_address, quote_asset_code, quote_network_code, status, expires_at)
    `);

    await this.pool.query(`
      CREATE TABLE IF NOT EXISTS ${ONCHAIN_RECEIPTS_TABLE} (
        receipt_id text PRIMARY KEY,
        network_code text NOT NULL,
        asset_code text NOT NULL,
        collection_address text NOT NULL,
        tx_hash text NOT NULL,
        event_index integer NOT NULL DEFAULT 0,
        recipient_token_account text NULL,
        from_address text NULL,
        mint text NULL,
        amount text NOT NULL,
        amount_minor text NOT NULL,
        confirmation_status text NOT NULL,
        slot bigint NULL,
        block_time timestamptz NULL,
        observed_at timestamptz NOT NULL,
        matched_order_no text NULL,
        match_status text NOT NULL,
        matcher_remark text NULL,
        raw_payload jsonb NULL,
        created_at timestamptz NOT NULL DEFAULT NOW(),
        updated_at timestamptz NOT NULL DEFAULT NOW(),
        UNIQUE (network_code, tx_hash, event_index)
      )
    `);
    await this.pool.query(`
      CREATE INDEX IF NOT EXISTS idx_runtime_state_onchain_receipts_match
      ON ${ONCHAIN_RECEIPTS_TABLE} (collection_address, asset_code, match_status, observed_at DESC)
    `);
    await this.pool.query(`
      CREATE INDEX IF NOT EXISTS idx_runtime_state_onchain_receipts_tx
      ON ${ONCHAIN_RECEIPTS_TABLE} (tx_hash, event_index)
    `);

    await this.pool.query(`
      CREATE TABLE IF NOT EXISTS ${PAYMENT_SCAN_CURSORS_TABLE} (
        cursor_key text PRIMARY KEY,
        network_code text NOT NULL,
        asset_code text NOT NULL,
        collection_address text NOT NULL,
        before_signature text NULL,
        last_signature text NULL,
        last_slot bigint NULL,
        updated_at timestamptz NOT NULL,
        UNIQUE (network_code, asset_code, collection_address)
      )
    `);
    await this.pool.query(`
      CREATE INDEX IF NOT EXISTS idx_runtime_state_payment_scan_cursors_context
      ON ${PAYMENT_SCAN_CURSORS_TABLE} (network_code, asset_code, collection_address)
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
        max_active_sessions integer NOT NULL,
        marzban_username text NULL,
        subscription_url text NULL,
        selected_line_code text NULL,
        selected_node_id text NULL
      )
    `);
    await this.pool.query(`
      ALTER TABLE ${SUBSCRIPTIONS_TABLE}
      ADD COLUMN IF NOT EXISTS marzban_username text NULL
    `);
    await this.pool.query(`
      ALTER TABLE ${SUBSCRIPTIONS_TABLE}
      ADD COLUMN IF NOT EXISTS subscription_url text NULL
    `);
    await this.pool.query(`
      ALTER TABLE ${SUBSCRIPTIONS_TABLE}
      ADD COLUMN IF NOT EXISTS selected_line_code text NULL
    `);
    await this.pool.query(`
      ALTER TABLE ${SUBSCRIPTIONS_TABLE}
      ADD COLUMN IF NOT EXISTS selected_node_id text NULL
    `);
    await this.pool.query(`
      CREATE INDEX IF NOT EXISTS idx_runtime_state_subscriptions_status
      ON ${SUBSCRIPTIONS_TABLE} (status)
    `);
    await this.pool.query(`
      CREATE TABLE IF NOT EXISTS ${WALLET_LIFECYCLES_TABLE} (
        account_id text PRIMARY KEY,
        wallet_id text NOT NULL,
        wallet_name text NOT NULL,
        status text NOT NULL,
        origin text NOT NULL,
        mnemonic_hash text NULL,
        mnemonic_word_count integer NULL,
        backup_acknowledged_at timestamptz NULL,
        activated_at timestamptz NULL,
        created_at timestamptz NOT NULL,
        updated_at timestamptz NOT NULL
      )
    `);
    await this.pool.query(`
      CREATE INDEX IF NOT EXISTS idx_runtime_state_wallet_lifecycles_status
      ON ${WALLET_LIFECYCLES_TABLE} (status)
    `);
    await this.pool.query(`
      CREATE TABLE IF NOT EXISTS ${WALLET_PUBLIC_ADDRESSES_TABLE} (
        address_id text PRIMARY KEY,
        account_id text NOT NULL,
        wallet_id text NULL,
        network_code text NOT NULL,
        asset_code text NOT NULL,
        address text NOT NULL,
        is_default boolean NOT NULL DEFAULT false,
        created_at timestamptz NOT NULL,
        updated_at timestamptz NOT NULL,
        UNIQUE (account_id, network_code, asset_code, address)
      )
    `);
    await this.pool.query(`
      CREATE INDEX IF NOT EXISTS idx_runtime_state_wallet_public_addresses_account_network_asset
      ON ${WALLET_PUBLIC_ADDRESSES_TABLE} (account_id, network_code, asset_code)
    `);
    await this.pool.query(`
      CREATE TABLE IF NOT EXISTS ${WALLET_SECRET_BACKUPS_TABLE} (
        backup_id text PRIMARY KEY,
        account_id text NOT NULL UNIQUE,
        wallet_id text NOT NULL,
        secret_type text NOT NULL,
        encryption_scheme text NOT NULL,
        recovery_key_version text NOT NULL,
        recipient_fingerprint text NOT NULL,
        ciphertext text NOT NULL,
        replicated_to_backup_server boolean NOT NULL DEFAULT false,
        backup_server_reference text NULL,
        last_replication_error text NULL,
        created_at timestamptz NOT NULL,
        updated_at timestamptz NOT NULL
      )
    `);
    await this.pool.query(`
      CREATE INDEX IF NOT EXISTS idx_runtime_state_wallet_secret_backups_wallet
      ON ${WALLET_SECRET_BACKUPS_TABLE} (wallet_id)
    `);
    await this.pool.query(`
      CREATE TABLE IF NOT EXISTS ${WALLETS_TABLE} (
        wallet_id text PRIMARY KEY,
        account_id text NOT NULL,
        wallet_name text NOT NULL,
        wallet_kind text NOT NULL,
        source_type text NOT NULL,
        is_default boolean NOT NULL DEFAULT false,
        is_archived boolean NOT NULL DEFAULT false,
        created_at timestamptz NOT NULL,
        updated_at timestamptz NOT NULL
      )
    `);
    await this.pool.query(`
      CREATE INDEX IF NOT EXISTS idx_runtime_state_wallets_account
      ON ${WALLETS_TABLE} (account_id)
    `);
    await this.pool.query(`
      CREATE INDEX IF NOT EXISTS idx_runtime_state_wallets_default
      ON ${WALLETS_TABLE} (account_id, is_default)
    `);
    await this.pool.query(`
      CREATE TABLE IF NOT EXISTS ${WALLET_KEY_SLOTS_TABLE} (
        key_slot_id text PRIMARY KEY,
        wallet_id text NOT NULL,
        slot_code text NOT NULL,
        chain_family text NOT NULL,
        derivation_type text NOT NULL,
        derivation_path text NULL,
        created_at timestamptz NOT NULL,
        updated_at timestamptz NOT NULL,
        UNIQUE (wallet_id, slot_code)
      )
    `);
    await this.pool.query(`
      CREATE INDEX IF NOT EXISTS idx_runtime_state_wallet_key_slots_wallet
      ON ${WALLET_KEY_SLOTS_TABLE} (wallet_id)
    `);
    await this.pool.query(`
      CREATE TABLE IF NOT EXISTS ${WALLET_CHAIN_ACCOUNTS_TABLE} (
        chain_account_id text PRIMARY KEY,
        wallet_id text NOT NULL,
        key_slot_id text NULL,
        chain_family text NOT NULL,
        network_code text NOT NULL,
        address text NOT NULL,
        capability text NOT NULL,
        is_enabled boolean NOT NULL DEFAULT true,
        is_default_receive boolean NOT NULL DEFAULT false,
        created_at timestamptz NOT NULL,
        updated_at timestamptz NOT NULL,
        UNIQUE (wallet_id, network_code, address)
      )
    `);
    await this.pool.query(`
      CREATE INDEX IF NOT EXISTS idx_runtime_state_wallet_chain_accounts_wallet
      ON ${WALLET_CHAIN_ACCOUNTS_TABLE} (wallet_id)
    `);
    await this.pool.query(`
      CREATE INDEX IF NOT EXISTS idx_runtime_state_wallet_chain_accounts_network
      ON ${WALLET_CHAIN_ACCOUNTS_TABLE} (network_code)
    `);
    await this.pool.query(`
      CREATE TABLE IF NOT EXISTS ${WALLET_SECRET_BACKUPS_V2_TABLE} (
        backup_id text PRIMARY KEY,
        account_id text NOT NULL,
        wallet_id text NOT NULL UNIQUE,
        secret_type text NOT NULL,
        encryption_scheme text NOT NULL,
        recovery_key_version text NOT NULL,
        recipient_fingerprint text NOT NULL,
        ciphertext text NOT NULL,
        replicated_to_backup_server boolean NOT NULL DEFAULT false,
        backup_server_reference text NULL,
        last_replication_error text NULL,
        created_at timestamptz NOT NULL,
        updated_at timestamptz NOT NULL
      )
    `);
    await this.pool.query(`
      CREATE INDEX IF NOT EXISTS idx_runtime_state_wallet_secret_backups_v2_wallet
      ON ${WALLET_SECRET_BACKUPS_V2_TABLE} (wallet_id)
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

  private mapSession(row: AuthSessionRow, rawSession?: AuthSession): AuthSession {
    const createdAt = this.toIsoString(row.created_at)!;
    return {
      sessionId: row.session_id,
      accountId: row.account_id,
      installationId: row.installation_id,
      accessToken: rawSession?.accessToken ?? '',
      refreshToken: rawSession?.refreshToken ?? '',
      accessTokenExpiresAt:
        rawSession?.accessTokenExpiresAt ??
        new Date(new Date(createdAt).getTime() + 2 * 60 * 60 * 1000).toISOString(),
      refreshTokenExpiresAt:
        rawSession?.refreshTokenExpiresAt ??
        this.toIsoString(row.refresh_token_expires_at)!,
      status: row.status,
      createdAt,
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

  private buildAccountNo(accountId: string) {
    return `ACC-${accountId.replace(/-/g, '').slice(0, 12).toUpperCase()}`;
  }

  private buildSessionAccessJti(accessToken: string) {
    return createHash('sha256').update(accessToken).digest('hex').slice(0, 64);
  }

  private buildRefreshTokenHash(refreshToken: string) {
    return createHash('sha256').update(refreshToken).digest('hex');
  }

  private buildSessionInvalidatedReason(status: AuthSession['status']) {
    return status === 'ACTIVE' ? null : status;
  }

  private async touchInstallation(session: AuthSession) {
    await this.pool.query(
      `
        INSERT INTO ${ACCOUNT_INSTALLATIONS_TABLE} (
          id,
          account_id,
          installation_id,
          status,
          first_seen_at,
          last_seen_at,
          created_at,
          updated_at
        )
        VALUES ($1, $2, $3, 'OBSERVED', $4, $5, $6, $7)
        ON CONFLICT (account_id, installation_id) DO UPDATE
        SET
          last_seen_at = EXCLUDED.last_seen_at,
          updated_at = EXCLUDED.updated_at
      `,
      [
        randomUUID(),
        session.accountId,
        session.installationId,
        session.createdAt,
        session.updatedAt,
        session.createdAt,
        session.updatedAt,
      ],
    );
  }

  private toOrderValues(
    order: StoredOrderRecord,
    compositeIdempotencyKey: string,
  ) {
    return [
      order.orderId,
      order.orderNo,
      order.accountId,
      order.payerWalletId,
      order.payerChainAccountId,
      order.submittedFromAddress,
      order.planCode,
      order.planName,
      order.orderType,
      order.quoteAssetCode,
      order.quoteNetworkCode,
      order.quoteUsdAmount,
      order.baseAmount,
      order.uniqueAmountDelta,
      order.payableAmount,
      order.status,
      order.expiresAt,
      order.confirmedAt,
      order.completedAt,
      order.failureReason,
      order.submittedClientTxHash,
      order.matchedOnchainTxHash,
      order.paymentMatchedAt,
      order.matcherRemark,
      order.createdAt,
      new Date().toISOString(),
      compositeIdempotencyKey,
      order.collectionAddress,
    ];
  }

  private mapOrder(row: RuntimeStateOrderRow): StoredOrderRecord {
    return {
      orderId: row.order_id,
      orderNo: row.order_no,
      accountId: row.account_id,
      payerWalletId: row.payer_wallet_id,
      payerChainAccountId: row.payer_chain_account_id,
      submittedFromAddress: row.submitted_from_address,
      planCode: row.plan_code,
      planName: row.plan_name,
      orderType: row.order_type,
      quoteAssetCode: row.quote_asset_code,
      quoteNetworkCode: row.quote_network_code,
      quoteUsdAmount: row.quote_usd_amount,
      baseAmount: row.base_amount,
      uniqueAmountDelta: row.unique_amount_delta,
      payableAmount: row.payable_amount,
      status: row.status,
      expiresAt: this.toIsoString(row.expires_at)!,
      confirmedAt: this.toIsoString(row.confirmed_at),
      completedAt: this.toIsoString(row.completed_at),
      failureReason: row.failure_reason,
      submittedClientTxHash: row.submitted_client_tx_hash,
      matchedOnchainTxHash: row.matched_onchain_tx_hash,
      paymentMatchedAt: this.toIsoString(row.payment_matched_at),
      matcherRemark: row.matcher_remark,
      createdAt: this.toIsoString(row.created_at)!,
      idempotencyKey: row.idempotency_key,
      collectionAddress: row.collection_address,
    };
  }

  private mapPaymentScanCursor(
    row: PaymentScanCursorRow,
  ): PaymentScanCursorRecord {
    return {
      cursorKey: row.cursor_key,
      quoteNetworkCode: row.network_code,
      quoteAssetCode: row.asset_code,
      collectionAddress: row.collection_address,
      beforeSignature: row.before_signature,
      lastSignature: row.last_signature,
      lastSlot: this.toNumber(row.last_slot),
      updatedAt: this.toIsoString(row.updated_at)!,
    };
  }

  private mapOnchainReceipt(
    row: RuntimeStateOnchainReceiptRow,
  ): StoredOnchainReceiptRecord {
    return {
      receiptId: row.receipt_id,
      quoteNetworkCode: row.network_code,
      quoteAssetCode: row.asset_code,
      collectionAddress: row.collection_address,
      txHash: row.tx_hash,
      eventIndex: row.event_index,
      recipientTokenAccount: row.recipient_token_account,
      fromAddress: row.from_address,
      mint: row.mint,
      amount: row.amount,
      amountMinor: row.amount_minor,
      confirmationStatus: row.confirmation_status,
      slot: this.toNumber(row.slot),
      blockTime: this.toIsoString(row.block_time),
      observedAt: this.toIsoString(row.observed_at)!,
      matchedOrderNo: row.matched_order_no,
      matchStatus: row.match_status,
      matcherRemark: row.matcher_remark,
      rawPayload: row.raw_payload,
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
      marzbanUsername: row.marzban_username,
      subscriptionUrl: row.subscription_url,
      selectedLineCode: row.selected_line_code,
      selectedNodeId: row.selected_node_id,
    };
  }

  private mapWalletLifecycle(
    row: RuntimeStateWalletLifecycleRow,
  ): PersistedWalletLifecycleRecord {
    return {
      accountId: row.account_id,
      walletId: row.wallet_id,
      walletName: row.wallet_name,
      status: row.status,
      origin: row.origin,
      mnemonicHash: row.mnemonic_hash,
      mnemonicWordCount: row.mnemonic_word_count,
      backupAcknowledgedAt: this.toIsoString(row.backup_acknowledged_at),
      activatedAt: this.toIsoString(row.activated_at),
      createdAt: this.toIsoString(row.created_at)!,
      updatedAt: this.toIsoString(row.updated_at)!,
    };
  }

  private mapWalletPublicAddress(
    row: RuntimeStateWalletPublicAddressRow,
  ): PersistedWalletPublicAddressRecord {
    return {
      addressId: row.address_id,
      accountId: row.account_id,
      walletId: row.wallet_id,
      networkCode: row.network_code,
      assetCode: row.asset_code,
      address: row.address,
      isDefault: row.is_default,
      createdAt: this.toIsoString(row.created_at)!,
      updatedAt: this.toIsoString(row.updated_at)!,
    };
  }

  private mapWalletSecretBackup(
    row: RuntimeStateWalletSecretBackupRow,
  ): PersistedWalletSecretBackupRecord {
    return {
      backupId: row.backup_id,
      accountId: row.account_id,
      walletId: row.wallet_id,
      secretType: row.secret_type,
      encryptionScheme: row.encryption_scheme,
      recoveryKeyVersion: row.recovery_key_version,
      recipientFingerprint: row.recipient_fingerprint,
      ciphertext: row.ciphertext,
      replicatedToBackupServer: row.replicated_to_backup_server,
      backupServerReference: row.backup_server_reference,
      lastReplicationError: row.last_replication_error,
      createdAt: this.toIsoString(row.created_at)!,
      updatedAt: this.toIsoString(row.updated_at)!,
    };
  }

  private mapWallet(
    row: RuntimeStateWalletRow,
  ): PersistedWalletRecord {
    return {
      walletId: row.wallet_id,
      accountId: row.account_id,
      walletName: row.wallet_name,
      walletKind: row.wallet_kind,
      sourceType: row.source_type,
      isDefault: row.is_default,
      isArchived: row.is_archived,
      createdAt: this.toIsoString(row.created_at)!,
      updatedAt: this.toIsoString(row.updated_at)!,
    };
  }

  private mapWalletKeySlot(
    row: RuntimeStateWalletKeySlotRow,
  ): PersistedWalletKeySlotRecord {
    return {
      keySlotId: row.key_slot_id,
      walletId: row.wallet_id,
      slotCode: row.slot_code,
      chainFamily: row.chain_family,
      derivationType: row.derivation_type,
      derivationPath: row.derivation_path,
      createdAt: this.toIsoString(row.created_at)!,
      updatedAt: this.toIsoString(row.updated_at)!,
    };
  }

  private mapWalletChainAccount(
    row: RuntimeStateWalletChainAccountRow,
  ): PersistedWalletChainAccountRecord {
    return {
      chainAccountId: row.chain_account_id,
      walletId: row.wallet_id,
      keySlotId: row.key_slot_id,
      chainFamily: row.chain_family,
      networkCode: row.network_code,
      address: row.address,
      capability: row.capability,
      isEnabled: row.is_enabled,
      isDefaultReceive: row.is_default_receive,
      createdAt: this.toIsoString(row.created_at)!,
      updatedAt: this.toIsoString(row.updated_at)!,
    };
  }

  private mapWalletSecretBackupV2(
    row: RuntimeStateWalletSecretBackupV2Row,
  ): PersistedWalletSecretBackupV2Record {
    return {
      backupId: row.backup_id,
      accountId: row.account_id,
      walletId: row.wallet_id,
      secretType: row.secret_type,
      encryptionScheme: row.encryption_scheme,
      recoveryKeyVersion: row.recovery_key_version,
      recipientFingerprint: row.recipient_fingerprint,
      ciphertext: row.ciphertext,
      replicatedToBackupServer: row.replicated_to_backup_server,
      backupServerReference: row.backup_server_reference,
      lastReplicationError: row.last_replication_error,
      createdAt: this.toIsoString(row.created_at)!,
      updatedAt: this.toIsoString(row.updated_at)!,
    };
  }

  private toIsoString(value: Date | string | null): string | null {
    if (!value) {
      return null;
    }
    return value instanceof Date ? value.toISOString() : new Date(value).toISOString();
  }

  private toNumber(value: string | number | null): number | null {
    if (value === null || value === undefined) {
      return null;
    }
    return typeof value === 'number' ? value : Number(value);
  }
}
