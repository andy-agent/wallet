-- CryptoVPN baseline rollback
-- 说明：
-- 1. 仅用于本地或 staging 的首版回滚验证。
-- 2. 生产环境禁止直接对资金数据执行物理回滚，需按补偿策略处理。

BEGIN;

DROP TABLE IF EXISTS system_configs CASCADE;
DROP TABLE IF EXISTS legal_documents CASCADE;
DROP TABLE IF EXISTS app_versions CASCADE;
DROP TABLE IF EXISTS commission_withdraw_requests CASCADE;
DROP TABLE IF EXISTS commission_balances CASCADE;
DROP TABLE IF EXISTS commission_ledger CASCADE;
DROP TABLE IF EXISTS commission_rules CASCADE;
DROP TABLE IF EXISTS referral_bindings CASCADE;
DROP TABLE IF EXISTS account_wallet_public_addresses CASCADE;
DROP TABLE IF EXISTS order_payment_events CASCADE;
DROP TABLE IF EXISTS order_payment_targets CASCADE;
DROP TABLE IF EXISTS orders CASCADE;
DROP TABLE IF EXISTS payment_addresses CASCADE;
DROP TABLE IF EXISTS asset_catalog CASCADE;
DROP TABLE IF EXISTS chain_configs CASCADE;
DROP TABLE IF EXISTS vpn_subscriptions CASCADE;
DROP TABLE IF EXISTS vpn_access_identities CASCADE;
DROP TABLE IF EXISTS vpn_nodes CASCADE;
DROP TABLE IF EXISTS plan_region_permissions CASCADE;
DROP TABLE IF EXISTS vpn_regions CASCADE;
DROP TABLE IF EXISTS plans CASCADE;
DROP TABLE IF EXISTS audit_logs CASCADE;
DROP TABLE IF EXISTS admin_users CASCADE;
DROP TABLE IF EXISTS client_sessions CASCADE;
DROP TABLE IF EXISTS account_installations CASCADE;
DROP TABLE IF EXISTS verification_codes CASCADE;
DROP TABLE IF EXISTS accounts CASCADE;

DROP FUNCTION IF EXISTS set_updated_at();

DROP TYPE IF EXISTS audit_actor_type_enum CASCADE;
DROP TYPE IF EXISTS system_scope_enum CASCADE;
DROP TYPE IF EXISTS legal_doc_type_enum CASCADE;
DROP TYPE IF EXISTS app_version_status_enum CASCADE;
DROP TYPE IF EXISTS withdraw_status_enum CASCADE;
DROP TYPE IF EXISTS commission_status_enum CASCADE;
DROP TYPE IF EXISTS commission_level_enum CASCADE;
DROP TYPE IF EXISTS commission_rule_status_enum CASCADE;
DROP TYPE IF EXISTS referral_binding_status_enum CASCADE;
DROP TYPE IF EXISTS payment_event_status_enum CASCADE;
DROP TYPE IF EXISTS order_status_enum CASCADE;
DROP TYPE IF EXISTS order_type_enum CASCADE;
DROP TYPE IF EXISTS payment_target_status_enum CASCADE;
DROP TYPE IF EXISTS payment_address_strategy_enum CASCADE;
DROP TYPE IF EXISTS asset_code_enum CASCADE;
DROP TYPE IF EXISTS network_code_enum CASCADE;
DROP TYPE IF EXISTS subscription_status_enum CASCADE;
DROP TYPE IF EXISTS vpn_identity_status_enum CASCADE;
DROP TYPE IF EXISTS node_health_status_enum CASCADE;
DROP TYPE IF EXISTS node_status_enum CASCADE;
DROP TYPE IF EXISTS region_status_enum CASCADE;
DROP TYPE IF EXISTS region_tier_enum CASCADE;
DROP TYPE IF EXISTS region_access_policy_enum CASCADE;
DROP TYPE IF EXISTS plan_status_enum CASCADE;
DROP TYPE IF EXISTS admin_status_enum CASCADE;
DROP TYPE IF EXISTS admin_role_enum CASCADE;
DROP TYPE IF EXISTS session_status_enum CASCADE;
DROP TYPE IF EXISTS installation_status_enum CASCADE;
DROP TYPE IF EXISTS verification_status_enum CASCADE;
DROP TYPE IF EXISTS verification_purpose_enum CASCADE;
DROP TYPE IF EXISTS account_status_enum CASCADE;

COMMIT;
