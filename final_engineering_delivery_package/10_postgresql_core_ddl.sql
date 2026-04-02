-- CryptoVPN Final Development Startup Package - PostgreSQL Core DDL
-- 说明：
-- 1. 本 DDL 是首版开发基线，配合 OpenAPI 与状态机使用。
-- 2. 业务冻结项已固化；未冻结参数（确认数、冷静期、订单时长等）通过 system_configs 和 chain_configs 配置。
-- 3. 钱包私钥/助记词不入库；服务端仅可选保存公开地址元数据。

BEGIN;

CREATE EXTENSION IF NOT EXISTS pgcrypto;
CREATE EXTENSION IF NOT EXISTS citext;

DO $$ BEGIN CREATE TYPE account_status_enum AS ENUM ('PENDING_VERIFY','ACTIVE','FROZEN','CLOSED'); EXCEPTION WHEN duplicate_object THEN NULL; END $$;
DO $$ BEGIN CREATE TYPE verification_purpose_enum AS ENUM ('REGISTER','RESET_PASSWORD'); EXCEPTION WHEN duplicate_object THEN NULL; END $$;
DO $$ BEGIN CREATE TYPE verification_status_enum AS ENUM ('PENDING','VERIFIED','EXPIRED','CONSUMED'); EXCEPTION WHEN duplicate_object THEN NULL; END $$;
DO $$ BEGIN CREATE TYPE installation_status_enum AS ENUM ('OBSERVED','RISKY','DISABLED'); EXCEPTION WHEN duplicate_object THEN NULL; END $$;
DO $$ BEGIN CREATE TYPE session_status_enum AS ENUM ('ACTIVE','EVICTED','REVOKED','EXPIRED'); EXCEPTION WHEN duplicate_object THEN NULL; END $$;
DO $$ BEGIN CREATE TYPE admin_role_enum AS ENUM ('SUPER_ADMIN','OPS_ADMIN','FINANCE_ADMIN','SUPPORT_ADMIN'); EXCEPTION WHEN duplicate_object THEN NULL; END $$;
DO $$ BEGIN CREATE TYPE admin_status_enum AS ENUM ('ACTIVE','DISABLED'); EXCEPTION WHEN duplicate_object THEN NULL; END $$;

DO $$ BEGIN CREATE TYPE plan_status_enum AS ENUM ('DRAFT','ACTIVE','DISABLED'); EXCEPTION WHEN duplicate_object THEN NULL; END $$;
DO $$ BEGIN CREATE TYPE region_access_policy_enum AS ENUM ('BASIC_ONLY','INCLUDE_ADVANCED','CUSTOM'); EXCEPTION WHEN duplicate_object THEN NULL; END $$;
DO $$ BEGIN CREATE TYPE region_tier_enum AS ENUM ('BASIC','ADVANCED'); EXCEPTION WHEN duplicate_object THEN NULL; END $$;
DO $$ BEGIN CREATE TYPE region_status_enum AS ENUM ('ACTIVE','MAINTENANCE','DISABLED'); EXCEPTION WHEN duplicate_object THEN NULL; END $$;
DO $$ BEGIN CREATE TYPE node_status_enum AS ENUM ('ACTIVE','MAINTENANCE','DISABLED','OFFLINE'); EXCEPTION WHEN duplicate_object THEN NULL; END $$;
DO $$ BEGIN CREATE TYPE node_health_status_enum AS ENUM ('UNKNOWN','HEALTHY','DEGRADED','OFFLINE'); EXCEPTION WHEN duplicate_object THEN NULL; END $$;
DO $$ BEGIN CREATE TYPE vpn_identity_status_enum AS ENUM ('ACTIVE','REVOKED','ROTATING'); EXCEPTION WHEN duplicate_object THEN NULL; END $$;
DO $$ BEGIN CREATE TYPE subscription_status_enum AS ENUM ('PENDING_ACTIVATION','ACTIVE','EXPIRED','SUSPENDED','CANCELED'); EXCEPTION WHEN duplicate_object THEN NULL; END $$;

DO $$ BEGIN CREATE TYPE network_code_enum AS ENUM ('SOLANA','TRON'); EXCEPTION WHEN duplicate_object THEN NULL; END $$;
DO $$ BEGIN CREATE TYPE asset_code_enum AS ENUM ('SOL','TRX','USDT'); EXCEPTION WHEN duplicate_object THEN NULL; END $$;
DO $$ BEGIN CREATE TYPE payment_address_strategy_enum AS ENUM ('SHARED_EXACT_AMOUNT','DEDICATED_ADDRESS'); EXCEPTION WHEN duplicate_object THEN NULL; END $$;
DO $$ BEGIN CREATE TYPE payment_target_status_enum AS ENUM ('ACTIVE','EXPIRED','CLOSED'); EXCEPTION WHEN duplicate_object THEN NULL; END $$;
DO $$ BEGIN CREATE TYPE order_type_enum AS ENUM ('NEW','RENEWAL'); EXCEPTION WHEN duplicate_object THEN NULL; END $$;
DO $$ BEGIN CREATE TYPE order_status_enum AS ENUM ('AWAITING_PAYMENT','PAYMENT_DETECTED','CONFIRMING','PAID','PROVISIONING','COMPLETED','EXPIRED','UNDERPAID_REVIEW','OVERPAID_REVIEW','FAILED','CANCELED'); EXCEPTION WHEN duplicate_object THEN NULL; END $$;
DO $$ BEGIN CREATE TYPE payment_event_status_enum AS ENUM ('DETECTED','MATCHED','PENDING_CONFIRMATION','CONFIRMED','DUPLICATE_TX','LATE_PAYMENT','WRONG_ASSET','WRONG_NETWORK','PARSE_FAILED'); EXCEPTION WHEN duplicate_object THEN NULL; END $$;

DO $$ BEGIN CREATE TYPE referral_binding_status_enum AS ENUM ('BOUND','LOCKED','INVALIDATED'); EXCEPTION WHEN duplicate_object THEN NULL; END $$;
DO $$ BEGIN CREATE TYPE commission_rule_status_enum AS ENUM ('ACTIVE','INACTIVE'); EXCEPTION WHEN duplicate_object THEN NULL; END $$;
DO $$ BEGIN CREATE TYPE commission_level_enum AS ENUM ('LEVEL1','LEVEL2'); EXCEPTION WHEN duplicate_object THEN NULL; END $$;
DO $$ BEGIN CREATE TYPE commission_status_enum AS ENUM ('FROZEN','AVAILABLE','LOCKED_WITHDRAWAL','WITHDRAWN','REVERSED'); EXCEPTION WHEN duplicate_object THEN NULL; END $$;
DO $$ BEGIN CREATE TYPE withdraw_status_enum AS ENUM ('SUBMITTED','UNDER_REVIEW','APPROVED','REJECTED','BROADCASTING','CHAIN_CONFIRMING','COMPLETED','FAILED','CANCELED'); EXCEPTION WHEN duplicate_object THEN NULL; END $$;

DO $$ BEGIN CREATE TYPE app_version_status_enum AS ENUM ('DRAFT','PUBLISHED','DEPRECATED'); EXCEPTION WHEN duplicate_object THEN NULL; END $$;
DO $$ BEGIN CREATE TYPE legal_doc_type_enum AS ENUM ('USER_AGREEMENT','PRIVACY_POLICY','SERVICE_TERMS','RISK_DISCLOSURE','WALLET_SELF_CUSTODY_NOTICE','DOWNLOAD_DISCLAIMER','PAYMENT_POLICY','COMMISSION_POLICY'); EXCEPTION WHEN duplicate_object THEN NULL; END $$;
DO $$ BEGIN CREATE TYPE system_scope_enum AS ENUM ('AUTH','ORDER','PAYMENT','VPN','COMMISSION','WITHDRAWAL','APP','EMAIL','GENERAL'); EXCEPTION WHEN duplicate_object THEN NULL; END $$;
DO $$ BEGIN CREATE TYPE audit_actor_type_enum AS ENUM ('ADMIN','SYSTEM','ACCOUNT'); EXCEPTION WHEN duplicate_object THEN NULL; END $$;

CREATE OR REPLACE FUNCTION set_updated_at()
RETURNS trigger LANGUAGE plpgsql AS $$
BEGIN
  NEW.updated_at = NOW();
  RETURN NEW;
END;
$$;

-- Identity / Auth
CREATE TABLE IF NOT EXISTS accounts (
  id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  account_no varchar(32) NOT NULL,
  email citext NOT NULL,
  password_hash varchar(255) NOT NULL,
  status account_status_enum NOT NULL DEFAULT 'PENDING_VERIFY',
  referral_code varchar(32) NOT NULL,
  inviter_account_id uuid NULL REFERENCES accounts(id) ON DELETE SET NULL,
  email_verified_at timestamptz NULL,
  risk_level smallint NOT NULL DEFAULT 0,
  last_login_at timestamptz NULL,
  created_at timestamptz NOT NULL DEFAULT NOW(),
  updated_at timestamptz NOT NULL DEFAULT NOW()
);
CREATE UNIQUE INDEX IF NOT EXISTS uq_accounts_account_no ON accounts(account_no);
CREATE UNIQUE INDEX IF NOT EXISTS uq_accounts_email ON accounts(email);
CREATE UNIQUE INDEX IF NOT EXISTS uq_accounts_referral_code ON accounts(referral_code);
CREATE INDEX IF NOT EXISTS idx_accounts_status ON accounts(status);
CREATE INDEX IF NOT EXISTS idx_accounts_inviter_account_id ON accounts(inviter_account_id);
CREATE INDEX IF NOT EXISTS idx_accounts_last_login_at ON accounts(last_login_at DESC);
CREATE TRIGGER trg_accounts_updated_at BEFORE UPDATE ON accounts FOR EACH ROW EXECUTE FUNCTION set_updated_at();

CREATE TABLE IF NOT EXISTS verification_codes (
  id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  email citext NOT NULL,
  purpose verification_purpose_enum NOT NULL,
  code_hash varchar(255) NOT NULL,
  status verification_status_enum NOT NULL DEFAULT 'PENDING',
  request_ip inet NULL,
  expire_at timestamptz NOT NULL,
  consumed_at timestamptz NULL,
  created_at timestamptz NOT NULL DEFAULT NOW(),
  updated_at timestamptz NOT NULL DEFAULT NOW()
);
CREATE INDEX IF NOT EXISTS idx_verification_codes_email_purpose_status ON verification_codes(email, purpose, status);
CREATE INDEX IF NOT EXISTS idx_verification_codes_expire_at ON verification_codes(expire_at);
CREATE TRIGGER trg_verification_codes_updated_at BEFORE UPDATE ON verification_codes FOR EACH ROW EXECUTE FUNCTION set_updated_at();

CREATE TABLE IF NOT EXISTS account_installations (
  id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  account_id uuid NOT NULL REFERENCES accounts(id) ON DELETE CASCADE,
  installation_id varchar(128) NOT NULL,
  device_name varchar(128) NULL,
  brand varchar(64) NULL,
  model varchar(64) NULL,
  os_version varchar(32) NULL,
  app_version varchar(32) NULL,
  status installation_status_enum NOT NULL DEFAULT 'OBSERVED',
  first_seen_at timestamptz NOT NULL DEFAULT NOW(),
  last_seen_at timestamptz NULL,
  created_at timestamptz NOT NULL DEFAULT NOW(),
  updated_at timestamptz NOT NULL DEFAULT NOW()
);
CREATE UNIQUE INDEX IF NOT EXISTS uq_account_installations_account_installation ON account_installations(account_id, installation_id);
CREATE INDEX IF NOT EXISTS idx_account_installations_last_seen_at ON account_installations(last_seen_at DESC);
CREATE TRIGGER trg_account_installations_updated_at BEFORE UPDATE ON account_installations FOR EACH ROW EXECUTE FUNCTION set_updated_at();

CREATE TABLE IF NOT EXISTS client_sessions (
  id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  account_id uuid NOT NULL REFERENCES accounts(id) ON DELETE CASCADE,
  installation_id varchar(128) NULL,
  refresh_token_hash varchar(255) NOT NULL,
  access_jti varchar(64) NULL,
  status session_status_enum NOT NULL DEFAULT 'ACTIVE',
  invalidated_reason varchar(64) NULL,
  ip inet NULL,
  user_agent varchar(255) NULL,
  issued_at timestamptz NOT NULL DEFAULT NOW(),
  expires_at timestamptz NOT NULL,
  last_refresh_at timestamptz NULL,
  created_at timestamptz NOT NULL DEFAULT NOW(),
  updated_at timestamptz NOT NULL DEFAULT NOW()
);
CREATE UNIQUE INDEX IF NOT EXISTS uq_client_sessions_refresh_token_hash ON client_sessions(refresh_token_hash);
CREATE UNIQUE INDEX IF NOT EXISTS uq_client_sessions_single_active
  ON client_sessions(account_id) WHERE status = 'ACTIVE';
CREATE INDEX IF NOT EXISTS idx_client_sessions_account_status ON client_sessions(account_id, status);
CREATE INDEX IF NOT EXISTS idx_client_sessions_expires_at ON client_sessions(expires_at);
CREATE TRIGGER trg_client_sessions_updated_at BEFORE UPDATE ON client_sessions FOR EACH ROW EXECUTE FUNCTION set_updated_at();

CREATE TABLE IF NOT EXISTS admin_users (
  id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  username varchar(64) NOT NULL,
  password_hash varchar(255) NOT NULL,
  role admin_role_enum NOT NULL,
  status admin_status_enum NOT NULL DEFAULT 'ACTIVE',
  display_name varchar(64) NULL,
  email citext NULL,
  last_login_at timestamptz NULL,
  created_at timestamptz NOT NULL DEFAULT NOW(),
  updated_at timestamptz NOT NULL DEFAULT NOW()
);
CREATE UNIQUE INDEX IF NOT EXISTS uq_admin_users_username ON admin_users(username);
CREATE INDEX IF NOT EXISTS idx_admin_users_role_status ON admin_users(role, status);
CREATE TRIGGER trg_admin_users_updated_at BEFORE UPDATE ON admin_users FOR EACH ROW EXECUTE FUNCTION set_updated_at();

CREATE TABLE IF NOT EXISTS audit_logs (
  id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  actor_type audit_actor_type_enum NOT NULL,
  actor_id uuid NULL,
  module varchar(64) NOT NULL,
  action varchar(64) NOT NULL,
  target_type varchar(64) NULL,
  target_id varchar(64) NULL,
  before_json jsonb NOT NULL DEFAULT '{}'::jsonb,
  after_json jsonb NOT NULL DEFAULT '{}'::jsonb,
  metadata_json jsonb NOT NULL DEFAULT '{}'::jsonb,
  ip inet NULL,
  user_agent varchar(255) NULL,
  created_at timestamptz NOT NULL DEFAULT NOW()
);
CREATE INDEX IF NOT EXISTS idx_audit_logs_module_action ON audit_logs(module, action);
CREATE INDEX IF NOT EXISTS idx_audit_logs_target ON audit_logs(target_type, target_id);
CREATE INDEX IF NOT EXISTS idx_audit_logs_created_at ON audit_logs(created_at DESC);

-- VPN / Plans
CREATE TABLE IF NOT EXISTS plans (
  id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  plan_code varchar(32) NOT NULL,
  name varchar(64) NOT NULL,
  description text NULL,
  billing_cycle_months integer NOT NULL CHECK (billing_cycle_months > 0),
  price_usd numeric(20,8) NOT NULL CHECK (price_usd > 0),
  is_unlimited_traffic boolean NOT NULL DEFAULT true,
  max_active_sessions integer NOT NULL DEFAULT 1 CHECK (max_active_sessions > 0),
  region_access_policy region_access_policy_enum NOT NULL DEFAULT 'BASIC_ONLY',
  includes_advanced_regions boolean NOT NULL DEFAULT false,
  status plan_status_enum NOT NULL DEFAULT 'DRAFT',
  display_order integer NOT NULL DEFAULT 0,
  created_at timestamptz NOT NULL DEFAULT NOW(),
  updated_at timestamptz NOT NULL DEFAULT NOW()
);
CREATE UNIQUE INDEX IF NOT EXISTS uq_plans_plan_code ON plans(plan_code);
CREATE INDEX IF NOT EXISTS idx_plans_status_display_order ON plans(status, display_order);
CREATE TRIGGER trg_plans_updated_at BEFORE UPDATE ON plans FOR EACH ROW EXECUTE FUNCTION set_updated_at();

CREATE TABLE IF NOT EXISTS vpn_regions (
  id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  region_code varchar(32) NOT NULL,
  display_name varchar(64) NOT NULL,
  tier region_tier_enum NOT NULL DEFAULT 'BASIC',
  status region_status_enum NOT NULL DEFAULT 'ACTIVE',
  sort_order integer NOT NULL DEFAULT 0,
  icon_url text NULL,
  remark text NULL,
  created_at timestamptz NOT NULL DEFAULT NOW(),
  updated_at timestamptz NOT NULL DEFAULT NOW()
);
CREATE UNIQUE INDEX IF NOT EXISTS uq_vpn_regions_region_code ON vpn_regions(region_code);
CREATE INDEX IF NOT EXISTS idx_vpn_regions_tier_status_sort ON vpn_regions(tier, status, sort_order);
CREATE TRIGGER trg_vpn_regions_updated_at BEFORE UPDATE ON vpn_regions FOR EACH ROW EXECUTE FUNCTION set_updated_at();

CREATE TABLE IF NOT EXISTS plan_region_permissions (
  id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  plan_id uuid NOT NULL REFERENCES plans(id) ON DELETE CASCADE,
  region_id uuid NOT NULL REFERENCES vpn_regions(id) ON DELETE CASCADE,
  created_at timestamptz NOT NULL DEFAULT NOW()
);
CREATE UNIQUE INDEX IF NOT EXISTS uq_plan_region_permissions ON plan_region_permissions(plan_id, region_id);
CREATE INDEX IF NOT EXISTS idx_plan_region_permissions_region_id ON plan_region_permissions(region_id);

CREATE TABLE IF NOT EXISTS vpn_nodes (
  id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  region_id uuid NOT NULL REFERENCES vpn_regions(id) ON DELETE CASCADE,
  node_code varchar(32) NOT NULL,
  host varchar(255) NOT NULL,
  port integer NOT NULL CHECK (port > 0),
  protocol varchar(16) NOT NULL DEFAULT 'VLESS',
  transport_protocol varchar(16) NOT NULL DEFAULT 'tcp',
  security_type varchar(32) NOT NULL DEFAULT 'REALITY',
  reality_public_key varchar(255) NULL,
  server_name varchar(128) NULL,
  short_id varchar(32) NULL,
  flow varchar(32) NOT NULL DEFAULT 'XTLS_VISION',
  weight integer NOT NULL DEFAULT 100,
  status node_status_enum NOT NULL DEFAULT 'ACTIVE',
  health_status node_health_status_enum NOT NULL DEFAULT 'UNKNOWN',
  last_heartbeat_at timestamptz NULL,
  created_at timestamptz NOT NULL DEFAULT NOW(),
  updated_at timestamptz NOT NULL DEFAULT NOW()
);
CREATE UNIQUE INDEX IF NOT EXISTS uq_vpn_nodes_node_code ON vpn_nodes(node_code);
CREATE INDEX IF NOT EXISTS idx_vpn_nodes_region_status ON vpn_nodes(region_id, status);
CREATE INDEX IF NOT EXISTS idx_vpn_nodes_health_status ON vpn_nodes(health_status);
CREATE TRIGGER trg_vpn_nodes_updated_at BEFORE UPDATE ON vpn_nodes FOR EACH ROW EXECUTE FUNCTION set_updated_at();

CREATE TABLE IF NOT EXISTS vpn_access_identities (
  id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  account_id uuid NOT NULL REFERENCES accounts(id) ON DELETE CASCADE,
  uuid uuid NOT NULL,
  email_tag varchar(128) NOT NULL,
  status vpn_identity_status_enum NOT NULL DEFAULT 'ACTIVE',
  last_rotated_at timestamptz NULL,
  created_at timestamptz NOT NULL DEFAULT NOW(),
  updated_at timestamptz NOT NULL DEFAULT NOW()
);
CREATE UNIQUE INDEX IF NOT EXISTS uq_vpn_access_identities_account_id ON vpn_access_identities(account_id);
CREATE UNIQUE INDEX IF NOT EXISTS uq_vpn_access_identities_uuid ON vpn_access_identities(uuid);
CREATE UNIQUE INDEX IF NOT EXISTS uq_vpn_access_identities_email_tag ON vpn_access_identities(email_tag);
CREATE INDEX IF NOT EXISTS idx_vpn_access_identities_status ON vpn_access_identities(status);
CREATE TRIGGER trg_vpn_access_identities_updated_at BEFORE UPDATE ON vpn_access_identities FOR EACH ROW EXECUTE FUNCTION set_updated_at();

CREATE TABLE IF NOT EXISTS vpn_subscriptions (
  id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  account_id uuid NOT NULL REFERENCES accounts(id) ON DELETE CASCADE,
  plan_id uuid NULL REFERENCES plans(id) ON DELETE SET NULL,
  status subscription_status_enum NOT NULL DEFAULT 'PENDING_ACTIVATION',
  started_at timestamptz NULL,
  expire_at timestamptz NULL,
  last_renewed_at timestamptz NULL,
  renewal_count integer NOT NULL DEFAULT 0,
  is_unlimited_traffic boolean NOT NULL DEFAULT true,
  total_traffic_up bigint NOT NULL DEFAULT 0,
  total_traffic_down bigint NOT NULL DEFAULT 0,
  total_traffic bigint NOT NULL DEFAULT 0,
  max_active_sessions integer NOT NULL DEFAULT 1,
  region_access_snapshot jsonb NOT NULL DEFAULT '[]'::jsonb,
  suspended_reason text NULL,
  created_at timestamptz NOT NULL DEFAULT NOW(),
  updated_at timestamptz NOT NULL DEFAULT NOW()
);
CREATE UNIQUE INDEX IF NOT EXISTS uq_vpn_subscriptions_account_id ON vpn_subscriptions(account_id);
CREATE INDEX IF NOT EXISTS idx_vpn_subscriptions_status_expire_at ON vpn_subscriptions(status, expire_at);
CREATE TRIGGER trg_vpn_subscriptions_updated_at BEFORE UPDATE ON vpn_subscriptions FOR EACH ROW EXECUTE FUNCTION set_updated_at();

-- Payment / Wallet Metadata
CREATE TABLE IF NOT EXISTS chain_configs (
  id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  network_code network_code_enum NOT NULL,
  display_name varchar(32) NOT NULL,
  public_rpc_url text NULL,
  proxy_rpc_url text NULL,
  direct_broadcast_enabled boolean NOT NULL DEFAULT true,
  proxy_broadcast_enabled boolean NOT NULL DEFAULT false,
  required_confirmations integer NOT NULL CHECK (required_confirmations >= 0),
  explorer_tx_url_template text NULL,
  is_active boolean NOT NULL DEFAULT true,
  created_at timestamptz NOT NULL DEFAULT NOW(),
  updated_at timestamptz NOT NULL DEFAULT NOW()
);
CREATE UNIQUE INDEX IF NOT EXISTS uq_chain_configs_network_code ON chain_configs(network_code);
CREATE INDEX IF NOT EXISTS idx_chain_configs_is_active ON chain_configs(is_active);
CREATE TRIGGER trg_chain_configs_updated_at BEFORE UPDATE ON chain_configs FOR EACH ROW EXECUTE FUNCTION set_updated_at();

CREATE TABLE IF NOT EXISTS asset_catalog (
  id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  network_code network_code_enum NOT NULL,
  asset_code asset_code_enum NOT NULL,
  display_name varchar(64) NOT NULL,
  symbol varchar(16) NOT NULL,
  decimals integer NOT NULL CHECK (decimals >= 0),
  is_native boolean NOT NULL DEFAULT false,
  contract_address varchar(255) NULL,
  wallet_visible boolean NOT NULL DEFAULT true,
  order_payable boolean NOT NULL DEFAULT false,
  is_active boolean NOT NULL DEFAULT true,
  created_at timestamptz NOT NULL DEFAULT NOW(),
  updated_at timestamptz NOT NULL DEFAULT NOW()
);
CREATE UNIQUE INDEX IF NOT EXISTS uq_asset_catalog_network_asset_contract ON asset_catalog(network_code, asset_code, contract_address);
CREATE INDEX IF NOT EXISTS idx_asset_catalog_network_active ON asset_catalog(network_code, is_active);
CREATE TRIGGER trg_asset_catalog_updated_at BEFORE UPDATE ON asset_catalog FOR EACH ROW EXECUTE FUNCTION set_updated_at();

CREATE TABLE IF NOT EXISTS payment_addresses (
  id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  network_code network_code_enum NOT NULL,
  asset_code asset_code_enum NOT NULL,
  address varchar(255) NOT NULL,
  strategy payment_address_strategy_enum NOT NULL DEFAULT 'SHARED_EXACT_AMOUNT',
  is_active boolean NOT NULL DEFAULT true,
  label varchar(64) NULL,
  created_at timestamptz NOT NULL DEFAULT NOW(),
  updated_at timestamptz NOT NULL DEFAULT NOW()
);
CREATE UNIQUE INDEX IF NOT EXISTS uq_payment_addresses_network_asset_address ON payment_addresses(network_code, asset_code, address);
CREATE INDEX IF NOT EXISTS idx_payment_addresses_lookup ON payment_addresses(network_code, asset_code, is_active);
CREATE TRIGGER trg_payment_addresses_updated_at BEFORE UPDATE ON payment_addresses FOR EACH ROW EXECUTE FUNCTION set_updated_at();

CREATE TABLE IF NOT EXISTS orders (
  id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  order_no varchar(32) NOT NULL,
  account_id uuid NOT NULL REFERENCES accounts(id) ON DELETE CASCADE,
  plan_id uuid NOT NULL REFERENCES plans(id) ON DELETE RESTRICT,
  order_type order_type_enum NOT NULL,
  quote_asset_code asset_code_enum NOT NULL,
  quote_network_code network_code_enum NOT NULL,
  quote_usd_amount numeric(20,8) NOT NULL CHECK (quote_usd_amount > 0),
  payable_amount numeric(36,18) NOT NULL CHECK (payable_amount > 0),
  status order_status_enum NOT NULL DEFAULT 'AWAITING_PAYMENT',
  submitted_client_tx_hash varchar(255) NULL,
  failure_reason text NULL,
  expires_at timestamptz NOT NULL,
  paid_at timestamptz NULL,
  confirmed_at timestamptz NULL,
  completed_at timestamptz NULL,
  created_at timestamptz NOT NULL DEFAULT NOW(),
  updated_at timestamptz NOT NULL DEFAULT NOW()
);
CREATE UNIQUE INDEX IF NOT EXISTS uq_orders_order_no ON orders(order_no);
CREATE INDEX IF NOT EXISTS idx_orders_account_status_created ON orders(account_id, status, created_at DESC);
CREATE INDEX IF NOT EXISTS idx_orders_status_expires_at ON orders(status, expires_at);
CREATE TRIGGER trg_orders_updated_at BEFORE UPDATE ON orders FOR EACH ROW EXECUTE FUNCTION set_updated_at();

CREATE TABLE IF NOT EXISTS order_payment_targets (
  id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  order_id uuid NOT NULL REFERENCES orders(id) ON DELETE CASCADE,
  payment_address_id uuid NOT NULL REFERENCES payment_addresses(id) ON DELETE RESTRICT,
  collection_address varchar(255) NOT NULL,
  payable_amount numeric(36,18) NOT NULL,
  unique_amount_delta numeric(36,18) NOT NULL DEFAULT 0,
  qr_text text NOT NULL,
  status payment_target_status_enum NOT NULL DEFAULT 'ACTIVE',
  expires_at timestamptz NOT NULL,
  created_at timestamptz NOT NULL DEFAULT NOW(),
  updated_at timestamptz NOT NULL DEFAULT NOW()
);
CREATE UNIQUE INDEX IF NOT EXISTS uq_order_payment_targets_order_id ON order_payment_targets(order_id);
CREATE INDEX IF NOT EXISTS idx_order_payment_targets_status_expires_at ON order_payment_targets(status, expires_at);
CREATE TRIGGER trg_order_payment_targets_updated_at BEFORE UPDATE ON order_payment_targets FOR EACH ROW EXECUTE FUNCTION set_updated_at();

CREATE TABLE IF NOT EXISTS order_payment_events (
  id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  order_id uuid NULL REFERENCES orders(id) ON DELETE SET NULL,
  chain network_code_enum NOT NULL,
  asset_code asset_code_enum NOT NULL,
  tx_hash varchar(255) NOT NULL,
  event_index integer NOT NULL DEFAULT 0,
  from_address varchar(255) NULL,
  to_address varchar(255) NULL,
  amount numeric(36,18) NOT NULL CHECK (amount >= 0),
  status payment_event_status_enum NOT NULL,
  confirmations integer NOT NULL DEFAULT 0 CHECK (confirmations >= 0),
  detected_at timestamptz NOT NULL DEFAULT NOW(),
  confirmed_at timestamptz NULL,
  raw_payload jsonb NOT NULL DEFAULT '{}'::jsonb,
  created_at timestamptz NOT NULL DEFAULT NOW(),
  updated_at timestamptz NOT NULL DEFAULT NOW()
);
CREATE UNIQUE INDEX IF NOT EXISTS uq_order_payment_events_chain_tx_event_index ON order_payment_events(chain, tx_hash, event_index);
CREATE INDEX IF NOT EXISTS idx_order_payment_events_order_status ON order_payment_events(order_id, status);
CREATE INDEX IF NOT EXISTS idx_order_payment_events_tx_hash ON order_payment_events(tx_hash);
CREATE TRIGGER trg_order_payment_events_updated_at BEFORE UPDATE ON order_payment_events FOR EACH ROW EXECUTE FUNCTION set_updated_at();

CREATE TABLE IF NOT EXISTS account_wallet_public_addresses (
  id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  account_id uuid NOT NULL REFERENCES accounts(id) ON DELETE CASCADE,
  network_code network_code_enum NOT NULL,
  asset_code asset_code_enum NOT NULL,
  address varchar(255) NOT NULL,
  is_default boolean NOT NULL DEFAULT false,
  created_at timestamptz NOT NULL DEFAULT NOW(),
  updated_at timestamptz NOT NULL DEFAULT NOW()
);
CREATE UNIQUE INDEX IF NOT EXISTS uq_account_wallet_public_addresses ON account_wallet_public_addresses(account_id, network_code, asset_code, address);
CREATE INDEX IF NOT EXISTS idx_account_wallet_public_addresses_account_network ON account_wallet_public_addresses(account_id, network_code);
CREATE TRIGGER trg_account_wallet_public_addresses_updated_at BEFORE UPDATE ON account_wallet_public_addresses FOR EACH ROW EXECUTE FUNCTION set_updated_at();

-- Growth / Commission / Withdrawal
CREATE TABLE IF NOT EXISTS referral_bindings (
  id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  invitee_account_id uuid NOT NULL REFERENCES accounts(id) ON DELETE CASCADE,
  inviter_level1_account_id uuid NULL REFERENCES accounts(id) ON DELETE SET NULL,
  inviter_level2_account_id uuid NULL REFERENCES accounts(id) ON DELETE SET NULL,
  code_used varchar(32) NOT NULL,
  status referral_binding_status_enum NOT NULL DEFAULT 'BOUND',
  bound_at timestamptz NOT NULL DEFAULT NOW(),
  locked_at timestamptz NULL,
  created_at timestamptz NOT NULL DEFAULT NOW(),
  updated_at timestamptz NOT NULL DEFAULT NOW()
);
CREATE UNIQUE INDEX IF NOT EXISTS uq_referral_bindings_invitee_account_id ON referral_bindings(invitee_account_id);
CREATE INDEX IF NOT EXISTS idx_referral_bindings_level1 ON referral_bindings(inviter_level1_account_id);
CREATE INDEX IF NOT EXISTS idx_referral_bindings_level2 ON referral_bindings(inviter_level2_account_id);
CREATE TRIGGER trg_referral_bindings_updated_at BEFORE UPDATE ON referral_bindings FOR EACH ROW EXECUTE FUNCTION set_updated_at();

CREATE TABLE IF NOT EXISTS commission_rules (
  id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  rule_code varchar(32) NOT NULL,
  level1_rate_pct numeric(5,2) NOT NULL CHECK (level1_rate_pct >= 0),
  level2_rate_pct numeric(5,2) NOT NULL CHECK (level2_rate_pct >= 0),
  cooldown_days integer NOT NULL CHECK (cooldown_days >= 0),
  min_withdraw_amount numeric(20,8) NOT NULL CHECK (min_withdraw_amount >= 0),
  settlement_asset_code asset_code_enum NOT NULL DEFAULT 'USDT',
  settlement_network_code network_code_enum NOT NULL DEFAULT 'SOLANA',
  status commission_rule_status_enum NOT NULL DEFAULT 'ACTIVE',
  effective_at timestamptz NOT NULL DEFAULT NOW(),
  expired_at timestamptz NULL,
  created_at timestamptz NOT NULL DEFAULT NOW(),
  updated_at timestamptz NOT NULL DEFAULT NOW()
);
CREATE UNIQUE INDEX IF NOT EXISTS uq_commission_rules_rule_code ON commission_rules(rule_code);
CREATE INDEX IF NOT EXISTS idx_commission_rules_status_effective_at ON commission_rules(status, effective_at DESC);
CREATE TRIGGER trg_commission_rules_updated_at BEFORE UPDATE ON commission_rules FOR EACH ROW EXECUTE FUNCTION set_updated_at();

CREATE TABLE IF NOT EXISTS commission_ledger (
  id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  entry_no varchar(32) NOT NULL,
  beneficiary_account_id uuid NOT NULL REFERENCES accounts(id) ON DELETE CASCADE,
  source_order_id uuid NOT NULL REFERENCES orders(id) ON DELETE RESTRICT,
  source_account_id uuid NULL REFERENCES accounts(id) ON DELETE SET NULL,
  commission_level commission_level_enum NOT NULL,
  source_asset_code asset_code_enum NOT NULL,
  source_amount numeric(36,18) NOT NULL CHECK (source_amount >= 0),
  fx_rate_snapshot numeric(20,8) NOT NULL CHECK (fx_rate_snapshot >= 0),
  settlement_amount numeric(20,8) NOT NULL CHECK (settlement_amount >= 0),
  status commission_status_enum NOT NULL DEFAULT 'FROZEN',
  created_at timestamptz NOT NULL DEFAULT NOW(),
  available_at timestamptz NULL,
  updated_at timestamptz NOT NULL DEFAULT NOW()
);
CREATE UNIQUE INDEX IF NOT EXISTS uq_commission_ledger_entry_no ON commission_ledger(entry_no);
CREATE UNIQUE INDEX IF NOT EXISTS uq_commission_ledger_source_order_beneficiary_level
  ON commission_ledger(source_order_id, beneficiary_account_id, commission_level);
CREATE INDEX IF NOT EXISTS idx_commission_ledger_beneficiary_status ON commission_ledger(beneficiary_account_id, status);
CREATE INDEX IF NOT EXISTS idx_commission_ledger_available_at ON commission_ledger(available_at);
CREATE TRIGGER trg_commission_ledger_updated_at BEFORE UPDATE ON commission_ledger FOR EACH ROW EXECUTE FUNCTION set_updated_at();

CREATE TABLE IF NOT EXISTS commission_balances (
  id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  account_id uuid NOT NULL REFERENCES accounts(id) ON DELETE CASCADE,
  settlement_asset_code asset_code_enum NOT NULL DEFAULT 'USDT',
  settlement_network_code network_code_enum NOT NULL DEFAULT 'SOLANA',
  frozen_amount numeric(20,8) NOT NULL DEFAULT 0,
  available_amount numeric(20,8) NOT NULL DEFAULT 0,
  withdrawing_amount numeric(20,8) NOT NULL DEFAULT 0,
  withdrawn_total numeric(20,8) NOT NULL DEFAULT 0,
  created_at timestamptz NOT NULL DEFAULT NOW(),
  updated_at timestamptz NOT NULL DEFAULT NOW()
);
CREATE UNIQUE INDEX IF NOT EXISTS uq_commission_balances_account_asset_network
  ON commission_balances(account_id, settlement_asset_code, settlement_network_code);
CREATE TRIGGER trg_commission_balances_updated_at BEFORE UPDATE ON commission_balances FOR EACH ROW EXECUTE FUNCTION set_updated_at();

CREATE TABLE IF NOT EXISTS commission_withdraw_requests (
  id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  request_no varchar(32) NOT NULL,
  account_id uuid NOT NULL REFERENCES accounts(id) ON DELETE CASCADE,
  amount numeric(20,8) NOT NULL CHECK (amount > 0),
  asset_code asset_code_enum NOT NULL DEFAULT 'USDT',
  network_code network_code_enum NOT NULL DEFAULT 'SOLANA',
  payout_address varchar(255) NOT NULL,
  fee_amount numeric(20,8) NOT NULL DEFAULT 0,
  status withdraw_status_enum NOT NULL DEFAULT 'SUBMITTED',
  reviewer_admin_id uuid NULL REFERENCES admin_users(id) ON DELETE SET NULL,
  reviewed_at timestamptz NULL,
  tx_hash varchar(255) NULL,
  fail_reason text NULL,
  completed_at timestamptz NULL,
  created_at timestamptz NOT NULL DEFAULT NOW(),
  updated_at timestamptz NOT NULL DEFAULT NOW()
);
CREATE UNIQUE INDEX IF NOT EXISTS uq_commission_withdraw_requests_request_no ON commission_withdraw_requests(request_no);
CREATE INDEX IF NOT EXISTS idx_commission_withdraw_requests_account_status ON commission_withdraw_requests(account_id, status);
CREATE INDEX IF NOT EXISTS idx_commission_withdraw_requests_status_created_at ON commission_withdraw_requests(status, created_at DESC);
CREATE TRIGGER trg_commission_withdraw_requests_updated_at BEFORE UPDATE ON commission_withdraw_requests FOR EACH ROW EXECUTE FUNCTION set_updated_at();

-- Distribution / Legal / Config
CREATE TABLE IF NOT EXISTS app_versions (
  id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  platform varchar(16) NOT NULL DEFAULT 'android',
  channel varchar(32) NOT NULL DEFAULT 'official',
  version_name varchar(32) NOT NULL,
  version_code integer NOT NULL CHECK (version_code > 0),
  min_supported_code integer NOT NULL CHECK (min_supported_code > 0),
  force_update boolean NOT NULL DEFAULT false,
  download_url text NOT NULL,
  sha256 varchar(128) NOT NULL,
  release_notes text NOT NULL,
  status app_version_status_enum NOT NULL DEFAULT 'DRAFT',
  published_at timestamptz NULL,
  created_at timestamptz NOT NULL DEFAULT NOW(),
  updated_at timestamptz NOT NULL DEFAULT NOW()
);
CREATE UNIQUE INDEX IF NOT EXISTS uq_app_versions_platform_channel_version_code
  ON app_versions(platform, channel, version_code);
CREATE INDEX IF NOT EXISTS idx_app_versions_platform_channel_status ON app_versions(platform, channel, status);
CREATE TRIGGER trg_app_versions_updated_at BEFORE UPDATE ON app_versions FOR EACH ROW EXECUTE FUNCTION set_updated_at();

CREATE TABLE IF NOT EXISTS legal_documents (
  id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  doc_type legal_doc_type_enum NOT NULL,
  title varchar(128) NOT NULL,
  version_no integer NOT NULL CHECK (version_no > 0),
  markdown_content text NOT NULL,
  status app_version_status_enum NOT NULL DEFAULT 'DRAFT',
  published_at timestamptz NULL,
  created_at timestamptz NOT NULL DEFAULT NOW(),
  updated_at timestamptz NOT NULL DEFAULT NOW()
);
CREATE UNIQUE INDEX IF NOT EXISTS uq_legal_documents_doc_type_version_no ON legal_documents(doc_type, version_no);
CREATE INDEX IF NOT EXISTS idx_legal_documents_doc_type_status ON legal_documents(doc_type, status);
CREATE TRIGGER trg_legal_documents_updated_at BEFORE UPDATE ON legal_documents FOR EACH ROW EXECUTE FUNCTION set_updated_at();

CREATE TABLE IF NOT EXISTS system_configs (
  id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  scope system_scope_enum NOT NULL,
  config_key varchar(64) NOT NULL,
  config_value text NOT NULL,
  description text NULL,
  mutable_in_prod boolean NOT NULL DEFAULT true,
  created_at timestamptz NOT NULL DEFAULT NOW(),
  updated_at timestamptz NOT NULL DEFAULT NOW()
);
CREATE UNIQUE INDEX IF NOT EXISTS uq_system_configs_scope_key ON system_configs(scope, config_key);
CREATE INDEX IF NOT EXISTS idx_system_configs_scope ON system_configs(scope);
CREATE TRIGGER trg_system_configs_updated_at BEFORE UPDATE ON system_configs FOR EACH ROW EXECUTE FUNCTION set_updated_at();

COMMIT;
