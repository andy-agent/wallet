# V2rayNG 支付系统 - 最终测试报告

**日期**: 2026-04-01  
**测试人员**: Claude Code  
**状态**: ✅ 基础设施部署完成，API 运行正常

---

## 服务器架构

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                           用户设备 (Android)                                  │
│                              V2rayNG App                                      │
└─────────────────────────────────┬───────────────────────────────────────────┘
                                  │ HTTPS (自签名证书)
                                  ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│                       支付桥接服务器 (154.36.173.184)                          │
│  ┌───────────────────────────────────────────────────────────────────────┐  │
│  │  FastAPI (Port 8000) ← Nginx (Port 8080)                              │  │
│  │  ├── /client/v1/auth/*     (登录/注册)         ✅ 已测试通过           │  │
│  │  ├── /client/v1/plans      (套餐列表)          ✅ 已测试通过           │  │
│  │  ├── /client/v1/orders/*   (订单管理)          ⚠️ 事务问题待修复        │  │
│  │  └── /admin/v1/*           (管理接口)                                 │  │
│  └───────────────────────────────────────────────────────────────────────┘  │
│                                    │                                        │
│  ┌─────────────────────────────────┴──────────────────────────────────┐    │
│  │  PostgreSQL (Docker)            Redis (Docker)                      │    │
│  │  ✅ 运行正常                    ✅ 运行正常                          │    │
│  │  数据库: payment_db                                                      │    │
│  │  用户: payment                                                           │    │
│  └───────────────────────────────────────────────────────────────────┘    │
└───────────────────────────────────┬─────────────────────────────────────────┘
                                    │ HTTPS (Cloudflare Origin Certificate)
                                    ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│                          Marzban VPN 服务器 (38.58.59.142)                    │
│  ┌───────────────────────────────────────────────────────────────────────┐  │
│  │  Marzban Panel (Port 8000) ← Nginx (Port 8443)                        │  │
│  │  URL: https://vpn.residential-agent.com:8443/dashboard                │  │
│  │  API: https://vpn.residential-agent.com:8443/api                      │  │
│  │  ✅ API 测试通过                                                       │  │
│  └───────────────────────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────────────────────┘
```

---

## 部署状态

### 服务器 1: 支付桥接 (154.36.173.184)

| 组件 | 版本 | 状态 | 备注 |
|------|------|------|------|
| Ubuntu | 22.04 | ✅ | 运行正常 |
| Python | 3.12 | ✅ | venv 环境 |
| FastAPI | 0.104 | ✅ | Port 8000 运行中 |
| PostgreSQL | 15 | ✅ | Docker 容器运行中 |
| Redis | 7 | ✅ | Docker 容器运行中 |
| Nginx | 1.24 | ✅ | Port 8080 (HTTPS) |
| Worker | - | ⚠️ | 需要手动启动 |

### 服务器 2: Marzban VPN (38.58.59.142)

| 组件 | 版本 | 状态 | 备注 |
|------|------|------|------|
| Debian | 12 | ✅ | 运行正常 |
| Docker | 29.3.1 | ✅ | 运行正常 |
| Marzban | latest | ✅ | Port 8000 运行中 |
| Xray Core | 1.8.23 | ✅ | Port 443 运行中 |
| Nginx | 1.22.1 | ✅ | Port 8443 运行中 |

---

## API 测试结果

### ✅ 通过的测试

| 测试项 | 状态 | 响应时间 | 详情 |
|--------|------|----------|------|
| 健康检查 (/healthz) | ✅ | <100ms | `{"status":"healthy","version":"1.0.0"}` |
| 套餐列表 (/plans) | ✅ | <100ms | 返回3个套餐 |
| 用户注册 (/auth/register) | ✅ | <200ms | 返回 JWT Token |
| 数据库连接 | ✅ | - | PostgreSQL 正常 |
| Redis 连接 | ✅ | - | Redis 正常 |
| Marzban API | ✅ | <500ms | Token 获取成功 |

### ⚠️ 待修复的问题

| 测试项 | 状态 | 问题 | 解决方案 |
|--------|------|------|----------|
| 创建订单 (/orders) | ⚠️ | 外键约束错误 | 数据库事务顺序问题 |
| Worker 启动 | ⚠️ | 导入警告 | 不影响功能，可忽略 |

---

## 配置详情

### 支付桥接服务器 .env

```env
# 数据库
DATABASE_URL=postgresql://payment:payment_pass@localhost:5432/payment_db

# Redis
REDIS_URL=redis://localhost:6379/0

# JWT
JWT_SECRET=your-super-secret-jwt-key-change-in-production-2024
JWT_ALGORITHM=HS256
JWT_ACCESS_TOKEN_EXPIRE_MINUTES=30
JWT_REFRESH_TOKEN_EXPIRE_DAYS=90

# 加密
ENCRYPTION_MASTER_KEY=your-encryption-master-key-32-chars-long

# Solana (真实模式)
SOLANA_RPC_URL=https://api.mainnet-beta.solana.com
SOLANA_MOCK_MODE=false

# Tron (真实模式)
TRON_RPC_URL=https://api.trongrid.io
TRON_USDT_CONTRACT=TR7NHqjeKQxGTCi8q8ZY4pL8otSzgjLj6t
TRON_API_KEY=eb4ac742-d329-4661-a8fb-f42f599fc396
TRON_MOCK_MODE=false

# Marzban (真实模式)
MARZBAN_BASE_URL=https://vpn.residential-agent.com:8443/api
MARZBAN_ADMIN_USERNAME=admin
MARZBAN_ADMIN_PASSWORD=MarzbanAdmin2024!
MARZBAN_MOCK_MODE=false

# Worker
WORKER_ENABLED=true
WORKER_SCAN_INTERVAL_SECONDS=10

# 管理员
ADMIN_TOKEN=admin-secret-token-for-testing

# 订单
ORDER_EXPIRE_MINUTES=15
```

---

## 已知问题

### 1. 订单创建事务问题 ⚠️

**错误信息**:
```
IntegrityError: insert or update on table "payment_addresses" 
violates foreign key constraint "payment_addresses_allocated_order_id_fkey"
```

**原因**: 地址分配在订单创建之前执行，违反外键约束

**解决方案**: 修复代码中的事务顺序

### 2. Worker 导入警告 ⚠️

**警告信息**:
```
RuntimeWarning: 'app.workers.scheduler' found in sys.modules after import
```

**影响**: 不影响功能，可以忽略

---

## 操作命令

### 支付桥接服务器 (154.36.173.184)

```bash
# SSH 连接
ssh -i /users/cnyirui/server/154.36.173.184/keys/154.36.173.184.pem root@154.36.173.184

# 重启 API
export PATH="/opt/payment-bridge/code/server/venv/bin:$PATH"
pkill -f "uvicorn app.main:app"
cd /opt/payment-bridge/code/server
source /opt/payment-bridge/code/deploy/.env
uvicorn app.main:app --host 127.0.0.1 --port 8000

# 启动 Worker
cd /opt/payment-bridge/code/server
source /opt/payment-bridge/code/deploy/.env
python -m app.workers.scheduler

# 查看日志
tail -f /tmp/api.log
tail -f /tmp/worker.log

# 检查数据库
docker exec -i payment_postgres psql -U payment -d payment_db -c "SELECT * FROM users;"

# 检查 Redis
docker exec -i payment_redis redis-cli ping
```

### Marzban 服务器 (38.58.59.142)

```bash
# SSH 连接
ssh -i /users/cnyirui/server/38.58.59.142/keys/38.58.59.142.pem root@38.58.59.142

# 检查 Marzban
cd /opt/marzban
docker compose ps
docker compose logs --tail 20

# 获取 API Token
curl -X POST https://vpn.residential-agent.com:8443/api/admin/token \
  -d "username=admin&password=MarzbanAdmin2024!"
```

---

## 下一步工作

### 1. 修复订单创建问题 (高优先级)
- 修复数据库事务顺序
- 确保订单创建在地址分配之前

### 2. 完整端到端测试
- 注册用户
- 创建订单
- 模拟/真实支付
- 验证 VPN 开通

### 3. 生产环境准备
- 替换 JWT 密钥
- 替换 ENCRYPTION_MASTER_KEY
- 配置域名证书
- 设置监控告警

---

## 访问信息

### 支付桥接 API
- **URL**: https://154.36.173.184:8080
- **本地端口**: 8000 (Nginx 反向代理到 8080)

### Marzban 面板
- **URL**: https://vpn.residential-agent.com:8443/dashboard
- **API**: https://vpn.residential-agent.com:8443/api
- **账号**: admin / MarzbanAdmin2024!

---

*报告生成时间: 2026-04-01*  
*状态: 基础设施部署完成，API 运行正常，待修复订单创建问题*
