# 支付桥接服务器 VNC 配置指南

## 服务器信息

| 项目 | 值 |
|------|-----|
| **IP 地址** | 154.36.173.184 |
| **端口** | 8080 (HTTPS via Nginx) |
| **SSH 问题** | 只接受密钥登录，当前无法远程 |
| **解决方式** | VNC 控制台操作 |

---

## 配置步骤

### 步骤 1: 登录 VNC 控制台
通过服务商控制台登录 VNC。

---

### 步骤 2: 更新 .env 配置

```bash
# 编辑配置文件
nano /opt/payment-bridge/code/deploy/.env
```

**完整配置内容：**

```env
# ============================================
# 数据库配置
# ============================================
DATABASE_URL=postgresql://payment_user:payment_pass_2024@localhost:5432/payment_db

# ============================================
# Redis 配置
# ============================================
REDIS_URL=redis://localhost:6379/0

# ============================================
# JWT 配置
# ============================================
JWT_SECRET=your-super-secret-jwt-key-change-in-production-2024
JWT_ALGORITHM=HS256
JWT_ACCESS_TOKEN_EXPIRE_MINUTES=30
JWT_REFRESH_TOKEN_EXPIRE_DAYS=90

# ============================================
# Solana 配置 (真实模式)
# ============================================
SOLANA_RPC_URL=https://api.mainnet-beta.solana.com
SOLANA_MOCK_MODE=false

# ============================================
# Tron 配置 (真实模式)
# ============================================
TRON_FULL_NODE=https://api.trongrid.io
TRON_SOLIDITY_NODE=https://api.trongrid.io
TRON_EVENT_SERVER=https://api.trongrid.io
TRON_API_KEY=your-tron-api-key-from-trongrid
TRON_MOCK_MODE=false

# ============================================
# Marzban 配置 (已部署 38.58.59.142)
# ============================================
MARZban_API_URL=https://vpn.residential-agent.com:8443/api
MARZban_USERNAME=admin
MARZban_PASSWORD=MarzbanAdmin2024!
MARZban_MOCK_MODE=false

# ============================================
# Worker 配置
# ============================================
WORKER_ENABLED=true

# ============================================
# 服务器配置
# ============================================
API_HOST=0.0.0.0
API_PORT=8000
```

**保存：** `Ctrl+O`, `Enter`, `Ctrl+X`

---

### 步骤 3: 获取 TronGrid API Key

1. 访问 https://www.trongrid.io/
2. 注册/登录账号
3. 创建 API Key
4. 更新 `.env` 中的 `TRON_API_KEY`

---

### 步骤 4: 重启 API 服务

```bash
# 进入目录
cd /opt/payment-bridge/code/server
source venv/bin/activate

# 重启服务
systemctl restart payment-bridge-api

# 检查状态
systemctl status payment-bridge-api
```

---

### 步骤 5: 启动 Worker

```bash
# 方式1: 前台运行（测试用）
cd /opt/payment-bridge/code/server
source venv/bin/activate
python -m app.workers.scheduler

# 方式2: 后台运行（生产用）
nohup python -m app.workers.scheduler > /tmp/worker.log 2>&1 &
echo $! > /tmp/worker.pid

# 查看日志
tail -f /tmp/worker.log
```

---

### 步骤 6: 验证 Worker 状态

```bash
# 检查进程
ps aux | grep scheduler

# 查看日志
tail -f /tmp/worker.log
```

应该看到类似输出：
```
Starting worker...
Worker started successfully!
Worker is alive...
```

---

## 全链路测试

### 测试 1: 注册用户

```bash
curl -sk https://154.36.173.184:8080/client/v1/auth/register \
  -X POST \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser1","password":"Test1234"}'
```

**预期响应：**
```json
{
  "code": "SUCCESS",
  "data": {
    "access_token": "eyJ...",
    "refresh_token": "eyJ...",
    "expires_at": 1234567890
  }
}
```

---

### 测试 2: 获取套餐列表

```bash
curl -sk https://154.36.173.184:8080/client/v1/plans
```

**预期响应：**
```json
{
  "code": "SUCCESS",
  "data": {
    "plans": [
      {
        "id": "plan_xxx",
        "name": "月度套餐",
        "price_cny": 29.9,
        "duration_days": 30,
        "traffic_gb": 100
      }
    ]
  }
}
```

---

### 测试 3: 创建订单

使用测试 1 返回的 `access_token`：

```bash
TOKEN="eyJ..."  # 替换为实际 token

curl -sk https://154.36.173.184:8080/client/v1/orders \
  -X POST \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -H "X-Client-Version: 1.0.0" \
  -H "X-Device-Id: test-device" \
  -d '{
    "plan_id": "plan_xxx",
    "purchase_type": "new",
    "asset_code": "SOL"
  }'
```

**预期响应：**
```json
{
  "code": "SUCCESS",
  "data": {
    "order_id": "order_xxx",
    "receive_address": "SolanaAddress...",
    "amount_crypto": "0.5",
    "status": "pending_payment",
    "expires_at": "2026-04-01T12:00:00Z"
  }
}
```

---

### 测试 4: 查询订单状态

```bash
curl -sk https://154.36.173.184:8080/client/v1/orders/order_xxx \
  -H "Authorization: Bearer $TOKEN"
```

---

### 测试 5: 模拟支付（Mock 模式）

如果区块链仍在 Mock 模式，可以手动触发支付检测：

```bash
# 查看待处理订单
psql -U payment_user -d payment_db -c "SELECT id, status, receive_address FROM orders WHERE status='pending_payment';"
```

---

### 测试 6: 验证 VPN 账号开通

等待订单状态变为 `fulfilled` 后，检查 Marzban：

```bash
# 在 Marzban 服务器上 (38.58.59.142)
curl -X GET https://vpn.residential-agent.com:8443/api/users \
  -H "Authorization: Bearer $MARZBAN_TOKEN"
```

或在面板中查看：https://vpn.residential-agent.com:8443/dashboard

---

## 故障排查

### API 无法启动

```bash
# 检查日志
journalctl -u payment-bridge-api -n 50

# 检查配置
cd /opt/payment-bridge/code/server
source venv/bin/activate
python -c "from app.core.config import get_settings; print(get_settings().marzban_api_url)"
```

### Worker 未运行

```bash
# 检查进程
ps aux | grep scheduler

# 如果未运行，手动启动
cd /opt/payment-bridge/code/server
source venv/bin/activate
python -m app.workers.scheduler
```

### 数据库连接失败

```bash
# 检查 PostgreSQL
systemctl status postgresql

# 检查连接
psql -U payment_user -d payment_db -c "SELECT 1;"
```

### Redis 连接失败

```bash
# 检查 Redis
systemctl status redis

# 测试连接
redis-cli ping
```

### Marzban 连接失败

```bash
# 测试连接
curl -X POST https://vpn.residential-agent.com:8443/api/admin/token \
  -d "username=admin&password=MarzbanAdmin2024!"
```

---

## 监控检查清单

- [ ] API 服务运行中：`systemctl is-active payment-bridge-api`
- [ ] Worker 运行中：`ps aux | grep scheduler`
- [ ] PostgreSQL 运行中：`systemctl is-active postgresql`
- [ ] Redis 运行中：`systemctl is-active redis`
- [ ] Marzban 可访问：`curl https://vpn.residential-agent.com:8443/api/admin/token`
- [ ] 端口监听：`ss -tlnp | grep 8000`

---

## 生产环境切换

完成测试后，切换到生产环境：

1. **关闭 Mock 模式**（需要真实区块链交互）
2. **替换自签名证书**
3. **修改 JWT 密钥**
4. **配置日志收集**
5. **设置监控告警**

---

## 相关文档

- [Marzban 部署指南](./MARZBAN_DEPLOYMENT.md)
- [代码审计文档](./CODE_AUDIT.md)
