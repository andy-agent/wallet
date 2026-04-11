import { IMemoryDb } from 'pg-mem';

export function ensureClientCatalogSchema(db: IMemoryDb) {
  db.public.none(`
    CREATE TABLE IF NOT EXISTS plans (
      id text PRIMARY KEY,
      plan_code text NOT NULL UNIQUE,
      name text NOT NULL,
      description text NULL,
      billing_cycle_months integer NOT NULL,
      price_usd numeric(20,8) NOT NULL,
      is_unlimited_traffic boolean NOT NULL DEFAULT true,
      max_active_sessions integer NOT NULL,
      region_access_policy text NOT NULL,
      includes_advanced_regions boolean NOT NULL DEFAULT false,
      status text NOT NULL,
      display_order integer NOT NULL DEFAULT 0,
      created_at timestamptz NOT NULL,
      updated_at timestamptz NOT NULL
    );

    CREATE TABLE IF NOT EXISTS vpn_regions (
      id text PRIMARY KEY,
      region_code text NOT NULL UNIQUE,
      display_name text NOT NULL,
      tier text NOT NULL,
      status text NOT NULL,
      sort_order integer NOT NULL DEFAULT 0,
      icon_url text NULL,
      remark text NULL,
      created_at timestamptz NOT NULL,
      updated_at timestamptz NOT NULL
    );

    CREATE TABLE IF NOT EXISTS plan_region_permissions (
      id text PRIMARY KEY,
      plan_id text NOT NULL,
      region_id text NOT NULL,
      created_at timestamptz NOT NULL
    );

    CREATE TABLE IF NOT EXISTS vpn_nodes (
      id text PRIMARY KEY,
      region_id text NOT NULL,
      node_code text NOT NULL UNIQUE,
      host text NOT NULL,
      port integer NOT NULL,
      protocol text NOT NULL,
      transport_protocol text NOT NULL,
      security_type text NOT NULL,
      reality_public_key text NULL,
      server_name text NULL,
      short_id text NULL,
      flow text NULL,
      weight integer NOT NULL DEFAULT 100,
      status text NOT NULL,
      health_status text NOT NULL,
      last_heartbeat_at timestamptz NULL,
      created_at timestamptz NOT NULL,
      updated_at timestamptz NOT NULL
    );

    CREATE TABLE IF NOT EXISTS system_configs (
      id text PRIMARY KEY,
      scope text NOT NULL,
      config_key text NOT NULL,
      config_value text NOT NULL,
      description text NULL,
      mutable_in_prod boolean NOT NULL DEFAULT true,
      created_at timestamptz NOT NULL,
      updated_at timestamptz NOT NULL
    );
  `);
}

export function seedClientCatalogData(db: IMemoryDb) {
  db.public.none(`
    INSERT INTO plans (
      id,
      plan_code,
      name,
      description,
      billing_cycle_months,
      price_usd,
      is_unlimited_traffic,
      max_active_sessions,
      region_access_policy,
      includes_advanced_regions,
      status,
      display_order,
      created_at,
      updated_at
    )
    VALUES
      (
        'plan-basic-db',
        'BASIC_1M',
        '数据库基础版-1个月',
        '来自 PostgreSQL 的基础套餐',
        1,
        11.50,
        true,
        2,
        'BASIC_ONLY',
        false,
        'ACTIVE',
        1,
        '2026-04-01T00:00:00.000Z',
        '2026-04-09T00:00:00.000Z'
      ),
      (
        'plan-pro-db',
        'PRO_12M_DB',
        '数据库专业版-12个月',
        '来自 PostgreSQL 的高级套餐',
        12,
        99.99,
        true,
        5,
        'CUSTOM',
        false,
        'ACTIVE',
        2,
        '2026-04-02T00:00:00.000Z',
        '2026-04-10T00:00:00.000Z'
      )
    ON CONFLICT (id) DO NOTHING;

    INSERT INTO vpn_regions (
      id,
      region_code,
      display_name,
      tier,
      status,
      sort_order,
      icon_url,
      remark,
      created_at,
      updated_at
    )
    VALUES
      (
        'region-jp-db',
        'JP_DB_BASIC',
        '日本-数据库基础线路',
        'BASIC',
        'ACTIVE',
        1,
        NULL,
        '来自 PostgreSQL 的基础区域',
        '2026-04-01T00:00:00.000Z',
        '2026-04-10T01:00:00.000Z'
      ),
      (
        'region-us-db',
        'US_DB_ADV',
        '美国-数据库高速线路',
        'ADVANCED',
        'ACTIVE',
        2,
        NULL,
        '来自 PostgreSQL 的高级区域',
        '2026-04-02T00:00:00.000Z',
        '2026-04-10T02:00:00.000Z'
      )
    ON CONFLICT (id) DO NOTHING;

    INSERT INTO plan_region_permissions (id, plan_id, region_id, created_at)
    VALUES
      (
        'perm-basic-jp-db',
        'plan-basic-db',
        'region-jp-db',
        '2026-04-10T02:30:00.000Z'
      ),
      (
        'perm-pro-jp-db',
        'plan-pro-db',
        'region-jp-db',
        '2026-04-10T02:31:00.000Z'
      ),
      (
        'perm-pro-us-db',
        'plan-pro-db',
        'region-us-db',
        '2026-04-10T02:32:00.000Z'
      )
    ON CONFLICT (id) DO NOTHING;

    INSERT INTO vpn_nodes (
      id,
      region_id,
      node_code,
      host,
      port,
      protocol,
      transport_protocol,
      security_type,
      reality_public_key,
      server_name,
      short_id,
      flow,
      weight,
      status,
      health_status,
      last_heartbeat_at,
      created_at,
      updated_at
    )
    VALUES
      (
        'node-jp-db',
        'region-jp-db',
        'JP-DB-01',
        'jp-db-01.example.com',
        443,
        'VLESS',
        'tcp',
        'REALITY',
        'public-key-jp-db',
        'jp-db.example.com',
        'jpdb01',
        'XTLS_VISION',
        100,
        'ACTIVE',
        'HEALTHY',
        '2026-04-10T02:45:00.000Z',
        '2026-04-01T00:00:00.000Z',
        '2026-04-10T02:45:00.000Z'
      ),
      (
        'node-us-db',
        'region-us-db',
        'US-DB-01',
        'us-db-01.example.com',
        443,
        'VLESS',
        'tcp',
        'REALITY',
        'public-key-us-db',
        'us-db.example.com',
        'usdb01',
        'XTLS_VISION',
        90,
        'ACTIVE',
        'HEALTHY',
        '2026-04-10T02:50:00.000Z',
        '2026-04-02T00:00:00.000Z',
        '2026-04-10T02:50:00.000Z'
      )
    ON CONFLICT (id) DO NOTHING;

    INSERT INTO system_configs (
      id,
      scope,
      config_key,
      config_value,
      description,
      mutable_in_prod,
      created_at,
      updated_at
    )
    VALUES
      (
        'cfg-vpn-db',
        'VPN',
        'CONFIG_ISSUE_MINUTES',
        '20',
        'VPN 配置签发分钟数',
        true,
        '2026-04-10T04:01:00.000Z',
        '2026-04-10T04:01:00.000Z'
      )
    ON CONFLICT (id) DO NOTHING;
  `);
}

export function cleanupClientCatalogTables(db: IMemoryDb) {
  const tables = [
    'plan_region_permissions',
    'vpn_nodes',
    'system_configs',
    'vpn_regions',
    'plans',
  ];

  for (const table of tables) {
    try {
      db.public.none(`DELETE FROM ${table}`);
    } catch {}
  }
}
