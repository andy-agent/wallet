import { Injectable, Logger } from '@nestjs/common';
import { ConfigService } from '@nestjs/config';
import { randomUUID } from 'crypto';
import { Pool, QueryResultRow } from 'pg';
import { resolveRuntimeStateConfig } from './runtime-state.config';

type PageQuery = {
  page?: number;
  pageSize?: number;
};

export interface PaginatedResult<T> {
  items: T[];
  page: number;
  pageSize: number;
  total: number;
}

type PlanRow = QueryResultRow & {
  plan_id: string;
  plan_code: string;
  name: string;
  description: string | null;
  billing_cycle_months: number;
  price_usd: string;
  max_active_sessions: number;
  region_access_policy: string;
  includes_advanced_regions: boolean;
  display_order: number;
  status: string;
};

type AuditLogRow = QueryResultRow & {
  audit_id: string;
  request_id: string | null;
  module: string;
  action: string;
  actor_type: string;
  actor_id: string | null;
  target_type: string | null;
  target_id: string | null;
  old_value: Record<string, unknown> | null;
  new_value: Record<string, unknown> | null;
  metadata_json: Record<string, unknown> | null;
  ip_address: string | null;
  user_agent: string | null;
  created_at: Date | string;
};

type LegalDocumentRow = QueryResultRow & {
  doc_id: string;
  doc_type: string;
  version_no: number;
  title: string;
  content: string;
  status: string;
  effective_at: Date | string | null;
  created_at: Date | string;
  updated_at: Date | string;
};

type SystemConfigRow = QueryResultRow & {
  scope: string;
  config_key: string;
  config_value: string;
  description: string | null;
  mutable_in_prod: boolean;
  updated_at: Date | string;
};

type AppVersionRow = QueryResultRow & {
  version_id: string;
  platform: string;
  channel: string;
  version_name: string;
  version_code: number;
  min_supported_code: number;
  download_url: string;
  force_update: boolean;
  release_notes: string;
  status: string;
  published_at: Date | string | null;
  created_at: Date | string;
  updated_at: Date | string;
};

type VpnRegionRow = QueryResultRow & {
  region_id: string;
  region_code: string;
  display_name: string;
  tier: string;
  status: string;
  sort_order: number;
  remark: string | null;
  created_at: Date | string;
  updated_at: Date | string;
};

type VpnNodeRow = QueryResultRow & {
  node_id: string;
  region_id: string;
  node_code: string;
  host: string;
  port: number;
  protocol: string;
  status: string;
  health_status: string;
  current_load: number;
  max_capacity: number;
  last_health_check_at: Date | string | null;
  created_at: Date | string;
  updated_at: Date | string;
};

type WithdrawalRow = QueryResultRow & {
  request_no: string;
  account_id: string;
  account_email: string | null;
  amount: string;
  asset_code: string;
  network_code: string;
  payout_address: string;
  status: string;
  fail_reason: string | null;
  tx_hash: string | null;
  created_at: Date | string;
  reviewed_at: Date | string | null;
  completed_at: Date | string | null;
};

type CountRow = QueryResultRow & { count: number };

@Injectable()
export class PostgresDataAccessService {
  private readonly logger = new Logger(PostgresDataAccessService.name);
  private readonly enabled: boolean;
  private readonly pool: Pool | null;

  constructor(configService: ConfigService) {
    const config = resolveRuntimeStateConfig(configService);
    this.enabled = config.backend === 'postgres' && Boolean(config.databaseUrl);
    this.pool = this.enabled
      ? new Pool({ connectionString: config.databaseUrl! })
      : null;
  }

  isEnabled() {
    return this.enabled && this.pool !== null;
  }

  async listPlans(params: {
    page?: number;
    pageSize?: number;
    status?: string;
  }): Promise<PaginatedResult<Record<string, unknown>>> {
    if (!this.pool) {
      return this.emptyResult(params);
    }

    const { page, pageSize } = this.normalizePage(params);
    const where: string[] = [];
    const values: Array<string | number> = [];
    if (params.status) {
      values.push(params.status);
      where.push(`UPPER(status::text) = UPPER($${values.length})`);
    }
    const whereClause = where.length > 0 ? `WHERE ${where.join(' AND ')}` : '';

    const total = await this.fetchTotal(
      `SELECT COUNT(*)::int AS count FROM plans ${whereClause}`,
      values,
    );
    const pagedValues = [...values, pageSize, (page - 1) * pageSize];
    const rows = await this.fetchRows<PlanRow & { allowed_region_ids: string[] | null }>(
      `
        SELECT
          id::text AS plan_id,
          plan_code,
          name,
          description,
          billing_cycle_months,
          price_usd::text AS price_usd,
          max_active_sessions,
          region_access_policy::text AS region_access_policy,
          includes_advanced_regions,
          display_order,
          status::text AS status
        FROM plans
        ${whereClause}
        ORDER BY display_order ASC, created_at DESC
        LIMIT $${pagedValues.length - 1} OFFSET $${pagedValues.length}
      `,
      pagedValues,
    );

    const permissionsByPlanId = new Map<string, string[]>();
    if (rows.length > 0) {
      const permissionRows = await this.fetchRows<
        QueryResultRow & { plan_id: string; region_id: string }
      >(
        `
          SELECT plan_id::text AS plan_id, region_id::text AS region_id
          FROM plan_region_permissions
          WHERE plan_id::text = ANY($1)
        `,
        [rows.map((row) => row.plan_id)],
      );

      for (const row of permissionRows) {
        const current = permissionsByPlanId.get(row.plan_id) ?? [];
        current.push(row.region_id);
        permissionsByPlanId.set(row.plan_id, current);
      }
    }

    return {
      items: rows.map((row) => ({
        planId: row.plan_id,
        planCode: row.plan_code,
        name: row.name,
        description: row.description,
        billingCycleMonths: row.billing_cycle_months,
        priceUsd: row.price_usd,
        maxActiveSessions: row.max_active_sessions,
        regionAccessPolicy: row.region_access_policy,
        includesAdvancedRegions: row.includes_advanced_regions,
        allowedRegionIds: permissionsByPlanId.get(row.plan_id) ?? [],
        displayOrder: row.display_order,
        status: row.status,
      })),
      page,
      pageSize,
      total,
    };
  }

  async listAuditLogs(params: {
    module?: string;
    actorType?: string;
    targetType?: string;
    page?: number;
    pageSize?: number;
  }): Promise<PaginatedResult<Record<string, unknown>>> {
    if (!this.pool) {
      return this.emptyResult(params);
    }

    const { page, pageSize } = this.normalizePage(params);
    const where: string[] = [];
    const values: Array<string | number> = [];
    if (params.module) {
      values.push(params.module);
      where.push(`module = $${values.length}`);
    }
    if (params.actorType) {
      values.push(
        params.actorType.toUpperCase() === 'USER'
          ? 'ACCOUNT'
          : params.actorType.toUpperCase(),
      );
      where.push(`actor_type::text = $${values.length}`);
    }
    if (params.targetType) {
      values.push(params.targetType);
      where.push(`target_type = $${values.length}`);
    }
    const whereClause = where.length > 0 ? `WHERE ${where.join(' AND ')}` : '';

    const total = await this.fetchTotal(
      `SELECT COUNT(*)::int AS count FROM audit_logs ${whereClause}`,
      values,
    );
    const pagedValues = [...values, pageSize, (page - 1) * pageSize];
    const rows = await this.fetchRows<AuditLogRow>(
      `
        SELECT
          id::text AS audit_id,
          request_id::text AS request_id,
          module,
          action,
          actor_type::text AS actor_type,
          actor_id::text AS actor_id,
          target_type,
          target_id,
          before_json AS old_value,
          after_json AS new_value,
          metadata_json,
          ip::text AS ip_address,
          user_agent,
          created_at
        FROM audit_logs
        ${whereClause}
        ORDER BY created_at DESC
        LIMIT $${pagedValues.length - 1} OFFSET $${pagedValues.length}
      `,
      pagedValues,
    );

    return {
      items: rows.map((row) => ({
        auditId: row.audit_id,
        requestId: row.request_id,
        module: row.module,
        action: row.action,
        actorType: row.actor_type === 'ACCOUNT' ? 'USER' : row.actor_type,
        actorId: row.actor_id,
        targetType: row.target_type,
        targetId: row.target_id,
        oldValue: row.old_value,
        newValue: row.new_value,
        ipAddress: row.ip_address,
        userAgent: row.user_agent,
        metadata: row.metadata_json,
        createdAt: this.toIsoString(row.created_at),
      })),
      page,
      pageSize,
      total,
    };
  }

  async listLegalDocuments(params: {
    docType?: string;
    status?: string;
    page?: number;
    pageSize?: number;
  }): Promise<PaginatedResult<Record<string, unknown>>> {
    if (!this.pool) {
      return this.emptyResult(params);
    }

    const { page, pageSize } = this.normalizePage(params);
    const where: string[] = [];
    const values: Array<string | number> = [];
    if (params.docType) {
      const rawTypes = this.expandLegalDocTypes(params.docType);
      values.push(...rawTypes);
      where.push(
        `UPPER(doc_type::text) IN (${rawTypes
          .map((_, index) => `$${values.length - rawTypes.length + index + 1}`)
          .join(', ')})`,
      );
    }
    if (params.status) {
      values.push(
        params.status.toUpperCase() === 'ARCHIVED'
          ? 'DEPRECATED'
          : params.status.toUpperCase(),
      );
      where.push(`UPPER(status::text) = UPPER($${values.length})`);
    }
    const whereClause = where.length > 0 ? `WHERE ${where.join(' AND ')}` : '';

    const total = await this.fetchTotal(
      `SELECT COUNT(*)::int AS count FROM legal_documents ${whereClause}`,
      values,
    );
    const pagedValues = [...values, pageSize, (page - 1) * pageSize];
    const rows = await this.fetchRows<LegalDocumentRow>(
      `
        SELECT
          id::text AS doc_id,
          doc_type::text AS doc_type,
          version_no,
          title,
          markdown_content AS content,
          status::text AS status,
          published_at AS effective_at,
          created_at,
          updated_at
        FROM legal_documents
        ${whereClause}
        ORDER BY updated_at DESC
        LIMIT $${pagedValues.length - 1} OFFSET $${pagedValues.length}
      `,
      pagedValues,
    );

    return {
      items: rows.map((row) => ({
        docId: row.doc_id,
        docType: this.normalizeLegalDocType(row.doc_type),
        versionNo: String(row.version_no),
        title: row.title,
        content: row.content,
        status: row.status.toUpperCase() === 'DEPRECATED' ? 'ARCHIVED' : row.status,
        effectiveAt: row.effective_at ? this.toIsoString(row.effective_at) : null,
        createdAt: this.toIsoString(row.created_at),
        updatedAt: this.toIsoString(row.updated_at),
        updatedBy: null,
      })),
      page,
      pageSize,
      total,
    };
  }

  async listSystemConfigs(params: {
    page?: number;
    pageSize?: number;
    scope?: string;
  }): Promise<PaginatedResult<Record<string, unknown>>> {
    if (!this.pool) {
      return this.emptyResult(params);
    }

    const { page, pageSize } = this.normalizePage(params);
    const where: string[] = [];
    const values: Array<string | number> = [];
    if (params.scope) {
      const rawScopes = this.expandConfigScopes(params.scope);
      values.push(...rawScopes);
      where.push(
        `UPPER(scope::text) IN (${rawScopes
          .map((_, index) => `$${values.length - rawScopes.length + index + 1}`)
          .join(', ')})`,
      );
    }
    const whereClause = where.length > 0 ? `WHERE ${where.join(' AND ')}` : '';

    const total = await this.fetchTotal(
      `SELECT COUNT(*)::int AS count FROM system_configs ${whereClause}`,
      values,
    );
    const pagedValues = [...values, pageSize, (page - 1) * pageSize];
    const rows = await this.fetchRows<SystemConfigRow>(
      `
        SELECT
          scope::text AS scope,
          config_key,
          config_value,
          description,
          mutable_in_prod,
          updated_at
        FROM system_configs
        ${whereClause}
        ORDER BY scope ASC, config_key ASC
        LIMIT $${pagedValues.length - 1} OFFSET $${pagedValues.length}
      `,
      pagedValues,
    );

    return {
      items: rows.map((row) => ({
        configKey: row.config_key,
        configValue: row.config_value,
        valueType: this.inferValueType(row.config_value),
        description: row.description ?? '',
        scope: this.normalizeConfigScope(row.scope),
        isEditable: row.mutable_in_prod,
        updatedAt: this.toIsoString(row.updated_at),
        updatedBy: null,
      })),
      page,
      pageSize,
      total,
    };
  }

  async listAppVersions(params: {
    page?: number;
    pageSize?: number;
    status?: string;
    channel?: string;
  }): Promise<PaginatedResult<Record<string, unknown>>> {
    if (!this.pool) {
      return this.emptyResult(params);
    }

    const { page, pageSize } = this.normalizePage(params);
    const where: string[] = [];
    const values: Array<string | number> = [];
    if (params.status) {
      values.push(params.status);
      where.push(`status::text = $${values.length}`);
    }
    if (params.channel) {
      values.push(params.channel.toLowerCase());
      where.push(`LOWER(channel) = $${values.length}`);
    }
    const whereClause = where.length > 0 ? `WHERE ${where.join(' AND ')}` : '';

    const total = await this.fetchTotal(
      `SELECT COUNT(*)::int AS count FROM app_versions ${whereClause}`,
      values,
    );
    const pagedValues = [...values, pageSize, (page - 1) * pageSize];
    const rows = await this.fetchRows<AppVersionRow>(
      `
        SELECT
          id::text AS version_id,
          platform,
          channel,
          version_name,
          version_code,
          min_supported_code,
          download_url,
          force_update,
          release_notes,
          status::text AS status,
          published_at,
          created_at,
          updated_at
        FROM app_versions
        ${whereClause}
        ORDER BY created_at DESC
        LIMIT $${pagedValues.length - 1} OFFSET $${pagedValues.length}
      `,
      pagedValues,
    );

    return {
      items: rows.map((row) => ({
        versionId: row.version_id,
        platform: this.normalizeVersionPlatform(row.platform),
        channel: this.normalizeVersionChannel(row.channel),
        versionName: row.version_name,
        versionCode: row.version_code,
        minAndroidVersionCode:
          row.platform.toLowerCase() === 'android' ? row.min_supported_code : null,
        minIosVersionCode:
          row.platform.toLowerCase() === 'ios' ? row.min_supported_code : null,
        downloadUrl: row.download_url,
        forceUpdate: row.force_update,
        releaseNotes: row.release_notes,
        status: row.status,
        publishedAt: row.published_at ? this.toIsoString(row.published_at) : null,
        createdAt: this.toIsoString(row.created_at),
        updatedAt: this.toIsoString(row.updated_at),
      })),
      page,
      pageSize,
      total,
    };
  }

  async listVpnRegions(params: {
    page?: number;
    pageSize?: number;
    tier?: string;
    status?: string;
  }): Promise<PaginatedResult<Record<string, unknown>>> {
    if (!this.pool) {
      return this.emptyResult(params);
    }

    const { page, pageSize } = this.normalizePage(params);
    const where: string[] = [];
    const values: Array<string | number> = [];
    if (params.tier) {
      values.push(params.tier);
      where.push(`UPPER(tier::text) = UPPER($${values.length})`);
    }
    if (params.status) {
      values.push(this.rawRegionStatus(params.status));
      where.push(`UPPER(status::text) = UPPER($${values.length})`);
    }
    const whereClause = where.length > 0 ? `WHERE ${where.join(' AND ')}` : '';

    const total = await this.fetchTotal(
      `SELECT COUNT(*)::int AS count FROM vpn_regions ${whereClause}`,
      values,
    );
    const pagedValues = [...values, pageSize, (page - 1) * pageSize];
    const rows = await this.fetchRows<VpnRegionRow>(
      `
        SELECT
          id::text AS region_id,
          region_code,
          display_name,
          tier::text AS tier,
          status::text AS status,
          sort_order,
          remark,
          created_at,
          updated_at
        FROM vpn_regions
        ${whereClause}
        ORDER BY sort_order ASC, created_at DESC
        LIMIT $${pagedValues.length - 1} OFFSET $${pagedValues.length}
      `,
      pagedValues,
    );

    return {
      items: rows.map((row) => ({
        regionId: row.region_id,
        regionCode: row.region_code,
        displayName: row.display_name,
        tier: row.tier,
        status: this.normalizeRegionStatus(row.status),
        sortOrder: row.sort_order,
        remark: row.remark,
        createdAt: this.toIsoString(row.created_at),
        updatedAt: this.toIsoString(row.updated_at),
      })),
      page,
      pageSize,
      total,
    };
  }

  async listVpnNodes(params: {
    page?: number;
    pageSize?: number;
    regionId?: string;
    status?: string;
    healthStatus?: string;
  }): Promise<PaginatedResult<Record<string, unknown>>> {
    if (!this.pool) {
      return this.emptyResult(params);
    }

    const { page, pageSize } = this.normalizePage(params);
    const where: string[] = [];
    const values: Array<string | number> = [];
    if (params.regionId) {
      values.push(params.regionId);
      where.push(`n.region_id::text = $${values.length}`);
    }
    if (params.status) {
      values.push(this.rawNodeStatus(params.status));
      where.push(`UPPER(n.status::text) = UPPER($${values.length})`);
    }
    if (params.healthStatus) {
      values.push(this.rawNodeHealthStatus(params.healthStatus));
      where.push(`UPPER(n.health_status::text) = UPPER($${values.length})`);
    }
    const whereClause = where.length > 0 ? `WHERE ${where.join(' AND ')}` : '';

    const total = await this.fetchTotal(
      `SELECT COUNT(*)::int AS count FROM vpn_nodes n ${whereClause}`,
      values,
    );
    const pagedValues = [...values, pageSize, (page - 1) * pageSize];
    const rows = await this.fetchRows<VpnNodeRow>(
      `
        SELECT
          n.id::text AS node_id,
          n.region_id::text AS region_id,
          n.node_code,
          n.host,
          n.port,
          n.protocol,
          n.status::text AS status,
          n.health_status::text AS health_status,
          0::int AS current_load,
          100::int AS max_capacity,
          n.last_heartbeat_at AS last_health_check_at,
          n.created_at,
          n.updated_at
        FROM vpn_nodes n
        ${whereClause}
        ORDER BY n.created_at DESC
        LIMIT $${pagedValues.length - 1} OFFSET $${pagedValues.length}
      `,
      pagedValues,
    );

    return {
      items: rows.map((row) => ({
        nodeId: row.node_id,
        regionId: row.region_id,
        nodeCode: row.node_code,
        displayName: row.node_code,
        host: row.host,
        port: row.port,
        protocol: row.protocol,
        status: this.normalizeNodeStatus(row.status),
        healthStatus: this.normalizeNodeHealthStatus(row.health_status),
        currentLoad: row.current_load,
        maxCapacity: row.max_capacity,
        lastHealthCheckAt: row.last_health_check_at
          ? this.toIsoString(row.last_health_check_at)
          : null,
        createdAt: this.toIsoString(row.created_at),
        updatedAt: this.toIsoString(row.updated_at),
      })),
      page,
      pageSize,
      total,
    };
  }

  async listWithdrawRequests(params: {
    page?: number;
    pageSize?: number;
    status?: string;
    accountId?: string;
    accountEmail?: string;
  }): Promise<PaginatedResult<Record<string, unknown>>> {
    if (!this.pool) {
      return this.emptyResult(params);
    }

    const { page, pageSize } = this.normalizePage(params);
    const where: string[] = [];
    const values: Array<string | number> = [];

    if (params.status) {
      values.push(params.status);
      where.push(`UPPER(w.status::text) = UPPER($${values.length})`);
    }
    if (params.accountId) {
      values.push(params.accountId);
      where.push(`w.account_id::text = $${values.length}`);
    }
    if (params.accountEmail) {
      values.push(`%${params.accountEmail.toLowerCase()}%`);
      where.push(`LOWER(a.email) LIKE $${values.length}`);
    }

    const whereClause = where.length > 0 ? `WHERE ${where.join(' AND ')}` : '';
    const fromClause = `
      FROM commission_withdraw_requests w
      JOIN accounts a ON a.id = w.account_id
      ${whereClause}
    `;

    const total = await this.fetchTotal(
      `SELECT COUNT(*)::int AS count ${fromClause}`,
      values,
    );
    const pagedValues = [...values, pageSize, (page - 1) * pageSize];
    const rows = await this.fetchRows<WithdrawalRow>(
      `
        SELECT
          w.request_no,
          w.account_id::text AS account_id,
          a.email AS account_email,
          w.amount::text AS amount,
          w.asset_code::text AS asset_code,
          w.network_code::text AS network_code,
          w.payout_address,
          w.status::text AS status,
          w.fail_reason,
          w.tx_hash,
          w.created_at,
          w.reviewed_at,
          w.completed_at
        ${fromClause}
        ORDER BY w.created_at DESC
        LIMIT $${pagedValues.length - 1} OFFSET $${pagedValues.length}
      `,
      pagedValues,
    );

    return {
      items: rows.map((row) => this.mapWithdrawalRow(row)),
      page,
      pageSize,
      total,
    };
  }

  async findWithdrawalByAccountAndRequestNo(
    accountId: string,
    requestNo: string,
  ): Promise<Record<string, unknown> | null> {
    if (!this.pool) {
      return null;
    }

    const rows = await this.fetchRows<WithdrawalRow>(
      `
        SELECT
          w.request_no,
          w.account_id::text AS account_id,
          a.email AS account_email,
          w.amount::text AS amount,
          w.asset_code::text AS asset_code,
          w.network_code::text AS network_code,
          w.payout_address,
          w.status::text AS status,
          w.fail_reason,
          w.tx_hash,
          w.created_at,
          w.reviewed_at,
          w.completed_at
        FROM commission_withdraw_requests w
        JOIN accounts a ON a.id = w.account_id
        WHERE w.account_id::text = $1 AND w.request_no = $2
        LIMIT 1
      `,
      [accountId, requestNo],
    );

    if (rows.length === 0) {
      return null;
    }

    return this.mapWithdrawalRow(rows[0]);
  }

  async createWithdrawalRequest(input: {
    requestNo: string;
    accountId: string;
    amount: string;
    assetCode: string;
    networkCode: string;
    payoutAddress: string;
  }): Promise<Record<string, unknown> | null> {
    if (!this.pool) {
      return null;
    }

    const rows = await this.fetchRows<WithdrawalRow>(
      `
        INSERT INTO commission_withdraw_requests (
          id,
          request_no,
          account_id,
          amount,
          asset_code,
          network_code,
          payout_address,
          status,
          created_at,
          updated_at
        )
        VALUES ($1, $2, $3, $4, $5, $6, $7, 'SUBMITTED', $8, $9)
        RETURNING
          request_no,
          account_id::text AS account_id,
          NULL::text AS account_email,
          amount::text AS amount,
          asset_code::text AS asset_code,
          network_code::text AS network_code,
          payout_address,
          status::text AS status,
          fail_reason,
          tx_hash,
          created_at,
          reviewed_at,
          completed_at
      `,
      [
        randomUUID(),
        input.requestNo,
        input.accountId,
        input.amount,
        input.assetCode,
        input.networkCode,
        input.payoutAddress,
        new Date().toISOString(),
        new Date().toISOString(),
      ],
    );

    if (rows.length === 0) {
      return null;
    }
    return this.mapWithdrawalRow(rows[0]);
  }

  async countPendingWithdrawalRequests(): Promise<number> {
    if (!this.pool) {
      return 0;
    }

    return this.fetchTotal(
      `
        SELECT COUNT(*)::int AS count
        FROM commission_withdraw_requests
        WHERE status IN ('SUBMITTED', 'UNDER_REVIEW')
      `,
      [],
    );
  }

  private async fetchRows<T extends QueryResultRow>(
    text: string,
    values: unknown[],
  ): Promise<T[]> {
    if (!this.pool) {
      return [];
    }
    try {
      const result = await this.pool.query<T>(text, values);
      return result.rows;
    } catch (error) {
      this.logger.warn('Postgres query failed');
      this.logger.debug(String(error));
      return [];
    }
  }

  private async fetchTotal(
    text: string,
    values: unknown[],
  ): Promise<number> {
    const rows = await this.fetchRows<CountRow>(text, values);
    return rows[0]?.count ?? 0;
  }

  private normalizePage(params: PageQuery) {
    const page = Math.max(1, params.page ?? 1);
    const pageSize = Math.min(100, Math.max(1, params.pageSize ?? 20));
    return { page, pageSize };
  }

  private emptyResult(params: PageQuery = {}): PaginatedResult<Record<string, unknown>> {
    const { page, pageSize } = this.normalizePage(params);
    return {
      items: [],
      page,
      pageSize,
      total: 0,
    };
  }

  private toIsoString(value: Date | string) {
    return value instanceof Date ? value.toISOString() : new Date(value).toISOString();
  }

  private inferValueType(value: string): 'STRING' | 'NUMBER' | 'BOOLEAN' | 'JSON' {
    if (value === 'true' || value === 'false') {
      return 'BOOLEAN';
    }
    if (!Number.isNaN(Number(value)) && value.trim() !== '') {
      return 'NUMBER';
    }
    if (
      (value.startsWith('{') && value.endsWith('}')) ||
      (value.startsWith('[') && value.endsWith(']'))
    ) {
      return 'JSON';
    }
    return 'STRING';
  }

  private normalizeVersionChannel(channel: string) {
    const lowered = channel.toLowerCase();
    if (lowered === 'official') {
      return 'OFFICIAL';
    }
    if (lowered === 'google_play') {
      return 'GOOGLE_PLAY';
    }
    if (lowered === 'app_store') {
      return 'APP_STORE';
    }
    return channel.toUpperCase();
  }

  private normalizeVersionPlatform(platform: string) {
    return platform.toLowerCase() === 'ios' ? 'IOS' : 'ANDROID';
  }

  private normalizeLegalDocType(docType: string) {
    const normalized = docType.toUpperCase();
    if (normalized === 'PRIVACY_POLICY') {
      return 'PRIVACY_POLICY';
    }
    if (normalized === 'REFUND_POLICY' || normalized === 'PAYMENT_POLICY') {
      return 'REFUND_POLICY';
    }
    if (
      normalized === 'RISK_DISCLOSURE' ||
      normalized === 'DOWNLOAD_DISCLAIMER' ||
      normalized === 'WALLET_SELF_CUSTODY_NOTICE' ||
      normalized === 'COMMISSION_POLICY'
    ) {
      return 'RISK_DISCLOSURE';
    }
    if (normalized === 'TERMS_OF_SERVICE' || normalized === 'SERVICE_TERMS' || normalized === 'USER_AGREEMENT') {
      return 'TERMS_OF_SERVICE';
    }
    return 'TERMS_OF_SERVICE';
  }

  private normalizeConfigScope(scope: string) {
    const normalized = scope.toUpperCase();
    if (normalized === 'VPN') {
      return 'VPN';
    }
    if (normalized === 'PAYMENT' || normalized === 'ORDER') {
      return 'PAYMENT';
    }
    if (normalized === 'COMMISSION' || normalized === 'WITHDRAWAL') {
      return 'REFERRAL';
    }
    return 'GLOBAL';
  }

  private expandLegalDocTypes(docType: string) {
    const normalized = docType.toUpperCase();
    if (normalized === 'PRIVACY_POLICY') {
      return ['PRIVACY_POLICY'];
    }
    if (normalized === 'REFUND_POLICY') {
      return ['REFUND_POLICY', 'PAYMENT_POLICY'];
    }
    if (normalized === 'RISK_DISCLOSURE') {
      return [
        'RISK_DISCLOSURE',
        'DOWNLOAD_DISCLAIMER',
        'WALLET_SELF_CUSTODY_NOTICE',
        'COMMISSION_POLICY',
      ];
    }
    return ['TERMS_OF_SERVICE', 'SERVICE_TERMS', 'USER_AGREEMENT'];
  }

  private expandConfigScopes(scope: string) {
    const normalized = scope.toUpperCase();
    if (normalized === 'VPN') {
      return ['VPN'];
    }
    if (normalized === 'PAYMENT') {
      return ['PAYMENT', 'ORDER'];
    }
    if (normalized === 'REFERRAL') {
      return ['COMMISSION', 'WITHDRAWAL'];
    }
    return ['AUTH', 'APP', 'EMAIL', 'GENERAL'];
  }

  private normalizeRegionStatus(status: string) {
    const normalized = status.toUpperCase();
    if (normalized === 'ACTIVE') {
      return 'ACTIVE';
    }
    if (normalized === 'MAINTENANCE') {
      return 'MAINTENANCE';
    }
    return 'INACTIVE';
  }

  private rawRegionStatus(status: string) {
    return status.toUpperCase() === 'INACTIVE' ? 'DISABLED' : status;
  }

  private normalizeNodeStatus(status: string) {
    return this.normalizeRegionStatus(status);
  }

  private rawNodeStatus(status: string) {
    return status.toUpperCase() === 'INACTIVE' ? 'DISABLED' : status;
  }

  private normalizeNodeHealthStatus(status: string) {
    const normalized = status.toUpperCase();
    if (normalized === 'HEALTHY') {
      return 'HEALTHY';
    }
    if (normalized === 'DEGRADED') {
      return 'DEGRADED';
    }
    return 'UNHEALTHY';
  }

  private rawNodeHealthStatus(status: string) {
    return status.toUpperCase() === 'UNHEALTHY' ? 'UNKNOWN' : status;
  }

  private mapWithdrawalRow(row: WithdrawalRow) {
    return {
      requestNo: row.request_no,
      accountId: row.account_id,
      accountEmail: row.account_email,
      amount: row.amount,
      assetCode: row.asset_code,
      networkCode: row.network_code,
      payoutAddress: row.payout_address,
      status: row.status,
      failReason: row.fail_reason,
      txHash: row.tx_hash,
      createdAt: this.toIsoString(row.created_at),
      reviewedAt: row.reviewed_at ? this.toIsoString(row.reviewed_at) : null,
      completedAt: row.completed_at ? this.toIsoString(row.completed_at) : null,
    };
  }
}
