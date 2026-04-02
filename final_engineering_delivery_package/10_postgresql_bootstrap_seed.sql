-- CryptoVPN Final Development Startup Package - PostgreSQL Bootstrap Seed Template
-- 说明：
-- 1. 本文件为首启必备的最小 seed / bootstrap 数据模板，必须与 10_postgresql_core_ddl.sql 配套使用。
-- 2. 执行时机：schema 迁移完成后，由 Tech Lead / 运维 / 产品按占位符说明替换后执行。
-- 3. 严禁将本模板中的占位符直接用于生产环境。
-- 4. ON CONFLICT 语句保证重复执行安全，可用于首次部署与增量补齐。

BEGIN;

-- 1. 系统配置
INSERT INTO system_configs (scope, config_key, config_value, description, mutable_in_prod)
VALUES
  ('PAYMENT', 'SOLANA_CONFIRMATIONS', '1', 'Solana 订单确认阈值', true),
  ('PAYMENT', 'TRON_CONFIRMATIONS', '20', 'TRON/TRC20 订单确认阈值', true),
  ('ORDER', 'ORDER_EXPIRE_SECONDS', '900', '订单锁价时长（秒）', true),
  ('COMMISSION', 'COOLDOWN_DAYS', '7', '佣金冷静期天数', true),
  ('AUTH', 'ACCESS_TOKEN_TTL_SECONDS', '7200', 'Client Access Token 有效期（秒）', true),
  ('AUTH', 'REFRESH_TOKEN_TTL_SECONDS', '2592000', 'Client Refresh Token 有效期（秒）', true),
  ('GENERAL', 'INVITE_CODE_LENGTH', '8', '邀请码长度', false)
ON CONFLICT (scope, config_key) DO NOTHING;

-- 2. 链配置
INSERT INTO chain_configs (network_code, display_name, public_rpc_url, proxy_rpc_url, required_confirmations, explorer_tx_url_template, is_active)
VALUES
  ('SOLANA', 'Solana Mainnet', '<SOLANA_PUBLIC_RPC_URL>', '<SOLANA_PROXY_RPC_URL>', 1, 'https://solscan.io/tx/{txHash}', true),
  ('TRON', 'TRON Mainnet', '<TRON_PUBLIC_RPC_URL>', '<TRON_PROXY_RPC_URL>', 20, 'https://tronscan.org/#/transaction/{txHash}', true)
ON CONFLICT (network_code) DO NOTHING;

-- 3. 资产目录
INSERT INTO asset_catalog (network_code, asset_code, display_name, symbol, decimals, is_native, contract_address, wallet_visible, order_payable, is_active)
VALUES
  ('SOLANA', 'SOL', 'Solana', 'SOL', 9, true, NULL, true, true, true),
  ('SOLANA', 'USDT', 'Tether USD (Solana)', 'USDT', 6, false, '<SOLANA_USDT_CONTRACT>', true, true, true),
  ('TRON', 'TRX', 'TRON', 'TRX', 6, true, NULL, true, false, true),
  ('TRON', 'USDT', 'Tether USD (TRC20)', 'USDT', 6, false, '<TRON_USDT_CONTRACT>', true, true, true)
ON CONFLICT (network_code, asset_code, contract_address) DO NOTHING;

-- 4. 佣金规则
INSERT INTO commission_rules (rule_code, level1_rate_pct, level2_rate_pct, cooldown_days, min_withdraw_amount, settlement_asset_code, settlement_network_code, status, effective_at)
VALUES
  ('DEFAULT_V1', 25.00, 5.00, 7, 10.00000000, 'USDT', 'SOLANA', 'ACTIVE', NOW())
ON CONFLICT (rule_code) DO NOTHING;

-- 5. 初始管理员
INSERT INTO admin_users (username, password_hash, role, status, display_name, email)
VALUES
  ('superadmin', '<PASSWORD_HASH_BCRYPT>', 'SUPER_ADMIN', 'ACTIVE', 'Super Admin', 'admin@example.com')
ON CONFLICT (username) DO NOTHING;

-- 6. 套餐占位
INSERT INTO plans (plan_code, name, description, billing_cycle_months, price_usd, max_active_sessions, region_access_policy, includes_advanced_regions, status, display_order)
VALUES
  ('BASIC_1M', '基础版-1个月', '<DESCRIPTION>', 1, 9.99, 1, 'BASIC_ONLY', false, 'ACTIVE', 1)
ON CONFLICT (plan_code) DO NOTHING;

-- 7. 区域占位
INSERT INTO vpn_regions (region_code, display_name, tier, status, sort_order, icon_url, remark)
VALUES
  ('JP_BASIC', '日本-基础线路', 'BASIC', 'ACTIVE', 1, NULL, '首启占位区域')
ON CONFLICT (region_code) DO NOTHING;

-- 8. 套餐-区域权限占位
INSERT INTO plan_region_permissions (plan_id, region_id)
SELECT p.id, r.id
FROM plans p, vpn_regions r
WHERE p.plan_code = 'BASIC_1M' AND r.region_code = 'JP_BASIC'
ON CONFLICT (plan_id, region_id) DO NOTHING;

-- 9. 法务文档占位
INSERT INTO legal_documents (doc_type, title, version_no, markdown_content, status, published_at)
VALUES
  ('USER_AGREEMENT', '用户协议', 1, '<MARKDOWN_CONTENT>', 'PUBLISHED', NOW()),
  ('PRIVACY_POLICY', '隐私政策', 1, '<MARKDOWN_CONTENT>', 'PUBLISHED', NOW())
ON CONFLICT (doc_type, version_no) DO NOTHING;

-- 10. App 版本占位
INSERT INTO app_versions (platform, channel, version_name, version_code, min_supported_code, force_update, download_url, sha256, release_notes, status, published_at)
VALUES
  ('android', 'official', '1.0.0', 100, 100, false, '<DOWNLOAD_BASE_URL>/app-1.0.0.apk', '<SHA256>', '首版发布', 'PUBLISHED', NOW())
ON CONFLICT (platform, channel, version_code) DO NOTHING;

-- 11. 节点占位
INSERT INTO vpn_nodes (region_id, node_code, host, port, protocol, transport_protocol, security_type, reality_public_key, server_name, short_id, flow, weight, status, health_status)
SELECT
  r.id,
  'NODE_JP_01',
  '<NODE_HOST>',
  443,
  'VLESS',
  'tcp',
  'REALITY',
  '<REALITY_PUBLIC_KEY>',
  '<SERVER_NAME>',
  '<SHORT_ID>',
  'XTLS_VISION',
  100,
  'ACTIVE',
  'UNKNOWN'
FROM vpn_regions r
WHERE r.region_code = 'JP_BASIC'
ON CONFLICT (node_code) DO NOTHING;

COMMIT;
