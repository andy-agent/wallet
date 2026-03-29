-- Initial Schema for v2rayng-payment-bridge
-- Compatible with PostgreSQL and SQLite

-- Plans table
CREATE TABLE IF NOT EXISTS plans (
    id VARCHAR(32) PRIMARY KEY,
    code VARCHAR(32) UNIQUE NOT NULL,
    name VARCHAR(128) NOT NULL,
    description VARCHAR(512) DEFAULT '',
    traffic_bytes BIGINT NOT NULL,
    duration_days INTEGER NOT NULL,
    price_usd DECIMAL(10, 2) NOT NULL,
    supported_assets TEXT DEFAULT '["SOL", "USDT_TRC20"]',  -- JSON array
    enabled BOOLEAN DEFAULT TRUE,
    sort_order INTEGER DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Orders table
CREATE TABLE IF NOT EXISTS orders (
    id VARCHAR(32) PRIMARY KEY,
    order_no VARCHAR(32) UNIQUE NOT NULL,
    purchase_type VARCHAR(10) NOT NULL CHECK (purchase_type IN ('new', 'renew')),
    plan_id VARCHAR(32) NOT NULL REFERENCES plans(id),
    client_user_id VARCHAR(32),
    marzban_username VARCHAR(64),
    
    -- Payment info
    chain VARCHAR(20) NOT NULL,
    asset_code VARCHAR(20) NOT NULL,
    receive_address VARCHAR(64) NOT NULL,
    amount_crypto DECIMAL(36, 18) NOT NULL,
    amount_usd_locked DECIMAL(10, 2) NOT NULL,
    fx_rate_locked DECIMAL(18, 8) NOT NULL,
    
    -- Status
    status VARCHAR(20) NOT NULL DEFAULT 'pending_payment',
    expires_at TIMESTAMP NOT NULL,
    
    -- On-chain info
    tx_hash VARCHAR(128) UNIQUE,
    tx_from VARCHAR(64),
    confirm_count INTEGER DEFAULT 0,
    paid_at TIMESTAMP,
    confirmed_at TIMESTAMP,
    fulfilled_at TIMESTAMP,
    
    -- Client info
    client_device_id VARCHAR(64) NOT NULL,
    client_version VARCHAR(16) NOT NULL,
    
    -- Error info
    error_code VARCHAR(32),
    error_message VARCHAR(256),
    
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Indexes for orders
CREATE INDEX IF NOT EXISTS idx_orders_order_no ON orders(order_no);
CREATE INDEX IF NOT EXISTS idx_orders_status ON orders(status);
CREATE INDEX IF NOT EXISTS idx_orders_marzban_username ON orders(marzban_username);
CREATE INDEX IF NOT EXISTS idx_orders_device_id ON orders(client_device_id);
CREATE INDEX IF NOT EXISTS idx_orders_address ON orders(receive_address);
CREATE INDEX IF NOT EXISTS idx_orders_status_created ON orders(status, created_at);
CREATE INDEX IF NOT EXISTS idx_orders_tx_hash ON orders(tx_hash);

-- Payment addresses table
CREATE TABLE IF NOT EXISTS payment_addresses (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    chain VARCHAR(20) NOT NULL,
    asset_code VARCHAR(20) NOT NULL,
    address VARCHAR(64) UNIQUE NOT NULL,
    encrypted_private_key VARCHAR(512),
    status VARCHAR(20) DEFAULT 'available' CHECK (status IN ('available', 'allocated', 'expired', 'swept', 'disabled')),
    allocated_order_id VARCHAR(32) REFERENCES orders(id),
    allocated_at TIMESTAMP,
    last_seen_tx_hash VARCHAR(128),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_payment_addr_chain ON payment_addresses(chain);
CREATE INDEX IF NOT EXISTS idx_payment_addr_status ON payment_addresses(status);
CREATE INDEX IF NOT EXISTS idx_payment_addr_address ON payment_addresses(address);

-- Client sessions table
CREATE TABLE IF NOT EXISTS client_sessions (
    id VARCHAR(32) PRIMARY KEY,
    order_id VARCHAR(32) NOT NULL REFERENCES orders(id),
    marzban_username VARCHAR(64) NOT NULL,
    access_token VARCHAR(512) NOT NULL,
    refresh_token VARCHAR(512) NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    revoked_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_client_sessions_username ON client_sessions(marzban_username);
CREATE INDEX IF NOT EXISTS idx_client_sessions_expires ON client_sessions(expires_at);

-- Audit logs table
CREATE TABLE IF NOT EXISTS audit_logs (
    id VARCHAR(32) PRIMARY KEY,
    entity_type VARCHAR(32) NOT NULL,
    entity_id VARCHAR(32) NOT NULL,
    action VARCHAR(64) NOT NULL,
    operator_type VARCHAR(20) NOT NULL CHECK (operator_type IN ('system', 'admin', 'worker', 'client')),
    operator_id VARCHAR(64),
    payload_json TEXT,
    client_ip VARCHAR(64),
    user_agent VARCHAR(512),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_audit_entity ON audit_logs(entity_type, entity_id);
CREATE INDEX IF NOT EXISTS idx_audit_created ON audit_logs(created_at);

-- Default plans
INSERT INTO plans (id, code, name, description, traffic_bytes, duration_days, price_usd, supported_assets, enabled, sort_order) VALUES
('plan_monthly_100g', 'monthly_100g', '月度套餐 100GB', '100GB流量，30天有效期，美国/日本/新加坡节点', 107374182400, 30, 5.00, '["SOL", "USDT_TRC20"]', TRUE, 1),
('plan_yearly_1t', 'yearly_1t', '年度套餐 1TB', '1TB流量，365天有效期，全节点', 1099511627776, 365, 45.00, '["SOL", "USDT_TRC20"]', TRUE, 2)
ON CONFLICT(id) DO NOTHING;
