# V2RayNG 支付桥部署指南

## 系统架构

```
┌─────────────────────────────────────────────────────────────────────┐
│                          Cloudflare DNS                              │
│  vpn.residential-agent.com → 38.58.59.142 (直连)                    │
│                            → 154.37.208.72 (直连)                   │
└─────────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────────┐
│                     Marzban VPN 服务器 (38.58.59.142)               │
│  ┌──────────────────────────────────────────────────────────────┐  │
│  │  Nginx (443/8443) ←→ XRay (VLESS fallback)                   │  │
│  │       ↓                                                      │  │
│  │  Marzban API (127.0.0.1:8000)                                │  │
│  └──────────────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────────────┘
                              │ HTTPS (IP直连, verify=False)
                              ▼
┌─────────────────────────────────────────────────────────────────────┐
│                   支付桥服务器 (154.36.173.184)                      │
│  ┌──────────────────────────────────────────────────────────────┐  │
│  │  nginx (8080) ←→ FastAPI (127.0.0.1:8000)                   │  │
│  └──────────────────────────────────────────────────────────────┘  │
│  ┌──────────────────────────────────────────────────────────────┐  │
│  │  payment-worker (systemd)                                    │  │
│  │    - 扫描区块链支付                                          │  │
│  │    - 确认交易                                                │  │
│  │    - 创建 Marzban 用户                                       │  │
│  └──────────────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────────┐
│                      基础设施                                        │
│  PostgreSQL (Docker)    Redis (Docker)                             │
└─────────────────────────────────────────────────────────────────────┘
```

## 组件接口协作

### 1. 支付桥 API (154.36.173.184)
- **外部访问**: `http://154.36.173.184:8080` (nginx 反向代理)
- **内部端口**: `127.0.0.1:8000` (FastAPI)
- **依赖**: PostgreSQL, Redis

### 2. Marzban API (38.58.59.142)
- **HTTPS**: `https://38.58.59.142/api`
- **认证**: POST `/api/admin/token`
- **创建用户**: POST `/api/user`
- **Inbound**: 仅支持 `vless_tcp` (需配置)

### 3. Worker 定时任务
| 任务 | 间隔 | 功能 |
|------|------|------|
| scan_pending_orders | 10s | 扫描区块链待支付订单 |
| confirm_seen_transactions | 10s | 确认交易达到确认数 |
| fulfill_paid_orders | 5s | 为已支付订单创建 VPN 用户 |
| expire_orders | 60s | 过期超时订单 |
| release_expired_addresses | 300s | 释放过期地址 |

## 部署步骤

### 一、Marzban 服务器配置 (38.58.59.142)

#### 1.1 基础配置
```bash
# 1. 配置 XRay 使用 443 端口 + fallback
vi /usr/local/etc/xray/config.json
```

```json
{
  "inbounds": [{
    "tag": "vless_tcp",
    "listen": "0.0.0.0",
    "port": 443,
    "protocol": "vless",
    "settings": {
      "clients": [...],
      "decryption": "none",
      "fallbacks": [{
        "dest": 8000,
        "xver": 0
      }]
    },
    "streamSettings": {
      "network": "tcp",
      "security": "tls",
      "tlsSettings": {
        "certificates": [{
          "certificateFile": "/var/lib/marzban/cert.crt",
          "keyFile": "/var/lib/marzban/key.key"
        }]
      }
    }
  }]
}
```

#### 1.2 配置 Nginx
```bash
vi /etc/nginx/sites-enabled/marzban
```

```nginx
server {
    listen 443 ssl http2;
    server_name vpn.residential-agent.com;
    ssl_certificate /var/lib/marzban/cert.crt;
    ssl_certificate_key /var/lib/marzban/key.key;
    
    location / {
        proxy_pass http://127.0.0.1:8000;
        proxy_http_version 1.1;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }
}

server {
    listen 8443 ssl http2;
    # 同上配置...
}
```

#### 1.3 检查 Inbound 配置
```bash
# 获取当前可用 inbounds
curl -s -k -X GET "https://38.58.59.142/api/inbounds" \
  -H "Authorization: Bearer <token>"
```

**重要**: 确保只有 `vless_tcp`，支付桥代码需相应配置。

### 二、支付桥服务器配置 (154.36.173.184)

#### 2.1 基础部署
```bash
# 1. 克隆代码
cd /opt
mkdir -p payment-bridge
cd payment-bridge
git clone <repository> code

# 2. 创建虚拟环境
cd code/server
python3 -m venv venv
source venv/bin/activate
pip install -r requirements.txt

# 3. 配置环境变量
vi /opt/payment-bridge/.env.production
```

```bash
DEBUG=false
APP_ENV=production
DATABASE_URL=postgresql://payment:payment_pass@localhost:5432/payment_db
REDIS_URL=redis://localhost:6379/0
JWT_SECRET=<generate-32-char-random-string>
ENCRYPTION_MASTER_KEY=<generate-32-char-random-string>
MARZBAN_BASE_URL=https://38.58.59.142
MARZBAN_ADMIN_USERNAME=admin
MARZBAN_ADMIN_PASSWORD=<marzban-admin-password>
MARZBAN_MOCK_MODE=false
TRON_API_KEY=<trongrid-api-key>
TRON_MOCK_MODE=false
SOLANA_MOCK_MODE=false
WORKER_ENABLED=true
```

#### 2.2 修改代码适配环境

**修改 1**: Marzban 客户端禁用 SSL 验证（IP 访问时使用）
```python
# app/integrations/marzban.py
async def _get_client(self) -> httpx.AsyncClient:
    if self._client is None or self._client.is_closed:
        self._client = httpx.AsyncClient(
            verify=False,  # 添加这行
            base_url=self.base_url,
            timeout=30.0,
            headers={"Accept": "application/json"}
        )
    return self._client
```

**修改 2**: 适配 Inbound 配置
```python
# 根据 Marzban 实际 inbounds 修改
if proxies is None:
    proxies = {
        "vless": {}  # 只保留 vless，移除 vmess
    }

payload = {
    "username": username,
    "proxies": proxies,
    "inbounds": {
        "vless": ["vless_tcp"]  # 只保留 vless_tcp
    }
}
```

**修改 3**: 确保 mock_mode 正确读取
```python
# app/core/config.py
marzban_mock_mode: bool = Field(default=False, alias="MARZBAN_MOCK_MODE")
```

#### 2.3 创建 systemd 服务（关键！）

**错误教训**: 使用 `nohup` + `export` 方式环境变量传递失败，Worker 反复崩溃。

**正确做法**: 使用 systemd service。

```bash
vi /etc/systemd/system/payment-worker.service
```

```ini
[Unit]
Description=Payment Bridge Worker
After=network.target postgresql.service redis.service

[Service]
Type=simple
User=root
WorkingDirectory=/opt/payment-bridge/code/server

# 环境变量明确定义
Environment="DEBUG=false"
Environment="APP_ENV=production"
Environment="DATABASE_URL=postgresql://payment:payment_pass@localhost:5432/payment_db"
Environment="REDIS_URL=redis://localhost:6379/0"
Environment="JWT_SECRET=<your-jwt-secret>"
Environment="ENCRYPTION_MASTER_KEY=<your-encryption-key>"
Environment="MARZBAN_BASE_URL=https://38.58.59.142"
Environment="MARZBAN_ADMIN_USERNAME=admin"
Environment="MARZBAN_ADMIN_PASSWORD=<marzban-password>"
Environment="MARZBAN_MOCK_MODE=false"
Environment="TRON_API_KEY=<trongrid-key>"
Environment="TRON_MOCK_MODE=false"
Environment="SOLANA_MOCK_MODE=false"
Environment="WORKER_ENABLED=true"

ExecStart=/opt/payment-bridge/code/server/venv/bin/python -m app.workers
Restart=always
RestartSec=5
StandardOutput=append:/var/log/payment-worker.log
StandardError=append:/var/log/payment-worker.log

[Install]
WantedBy=multi-user.target
```

#### 2.4 启动服务
```bash
# 重新加载 systemd
systemctl daemon-reload

# 清除 Python 缓存
find /opt/payment-bridge/code/server -type d -name __pycache__ -exec rm -rf {} +
find /opt/payment-bridge/code/server -name "*.pyc" -delete

# 启动 Worker
systemctl start payment-worker
systemctl enable payment-worker  # 开机自启

# 查看状态
systemctl status payment-worker
tail -f /var/log/payment-worker.log
```

### 三、API 服务配置

```bash
# 配置 nginx 反向代理
vi /etc/nginx/sites-available/payment-bridge
```

```nginx
server {
    listen 8080;
    server_name _;
    
    location / {
        proxy_pass http://127.0.0.1:8000;
        proxy_http_version 1.1;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }
}
```

```bash
ln -s /etc/nginx/sites-available/payment-bridge /etc/nginx/sites-enabled/
systemctl restart nginx
```

## 常见错误与解决方案

### 错误 1: Worker 启动后立即退出
**症状**: `systemctl status` 显示 `inactive (dead)`

**原因**: 环境变量未正确传递，Pydantic 验证失败
```
pydantic_core._pydantic_core.ValidationError: MARZBAN_BASE_URL Field required
```

**解决**: 使用 systemd service 的 `[Service]` 区块明确定义环境变量，不要用 `nohup` + `export`。

### 错误 2: SSL 证书验证失败
**症状**: 
```
[SSL: CERTIFICATE_VERIFY_FAILED] certificate verify failed
```

**原因**: 使用 IP 地址访问 HTTPS，证书是为域名颁发的

**解决**: 在 `marzban.py` 的 `_get_client()` 中添加 `verify=False`:
```python
self._client = httpx.AsyncClient(
    verify=False,  # 禁用 SSL 验证
    base_url=self.base_url,
    ...
)
```

### 错误 3: Marzban API 404
**症状**: 
```
POST https://vpn.residential-agent.com/api/api/admin/token "HTTP/1.1 404"
```

**原因**: `MARZBAN_BASE_URL` 包含 `/api`，代码会自动添加 `/api` 前缀

**解决**: 
```bash
# 错误
MARZBAN_BASE_URL=https://38.58.59.142/api

# 正确
MARZBAN_BASE_URL=https://38.58.59.142
```

### 错误 4: Inbound 不存在
**症状**:
```
{"detail":{"inbounds":"Value error, Inbound VMess TCP doesn't exist"}}
```

**原因**: Marzban 服务器只配置了 `vless_tcp`，代码尝试创建 `vmess` 用户

**解决**: 修改 `marzban.py` 只使用存在的 inbound:
```python
proxies = {"vless": {}}
inbounds = {"vless": ["vless_tcp"]}
```

### 错误 5: Cloudflare 525 SSL 错误
**症状**: 通过域名访问返回 `error code: 525`

**原因**: Cloudflare SSL/TLS 设置为 `Full (strict)`，但源服务器使用自签名证书

**解决**: 
1. 登录 Cloudflare 面板
2. SSL/TLS → Overview
3. 将 `Full (strict)` 改为 `Full`

### 错误 6: 端口不通
**症状**: 
```
Connection timeout to 38.58.59.142:8443
```

**原因**: 
- VPS 提供商网络防火墙阻塞
- 或 ufw/iptables 未放行

**解决**:
```bash
# 检查本地防火墙
ufw allow 443/tcp
ufw allow 8443/tcp

# 如果仍不通，使用 443 端口（通常开放）
# 配置 XRay/Nginx 共享 443 端口
```

## 验证部署

### 1. 测试 Marzban API
```bash
# 直接访问（绕过 Cloudflare）
curl -s -k -X POST "https://38.58.59.142/api/admin/token" \
  -d "username=admin&password=<password>" \
  -H "Content-Type: application/x-www-form-urlencoded"
# 应返回 access_token
```

### 2. 测试 Worker
```bash
# 查看日志
tail -f /var/log/payment-worker.log

# 检查状态
systemctl status payment-worker

# 检查订单处理
docker exec payment_postgres psql -U payment -d payment_db \
  -c "SELECT order_no, status FROM orders WHERE status='paid_success';"
```

### 3. 测试完整流程
```bash
# 1. 创建订单
curl -X POST http://154.36.173.184:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{"plan_id":"1","chain":"solana","asset_code":"SOL"}'

# 2. 模拟支付（更新数据库）
docker exec payment_postgres psql -U payment -d payment_db \
  -c "UPDATE orders SET status='paid_success', tx_hash='test', paid_at=NOW() WHERE order_no='ORD...';"

# 3. 等待 10-15 秒，检查订单变为 fulfilled
```

## 关键配置清单

### Marzban 服务器 (38.58.59.142)
- [ ] XRay 配置: 443 端口 + fallback 到 8000
- [ ] Nginx: 443/8443 监听 + SSL 证书
- [ ] 防火墙: 443/tcp 开放
- [ ] Inbound: 确认可用 inbounds (`vless_tcp`)

### 支付桥服务器 (154.36.173.184)
- [ ] 环境变量文件: `/opt/payment-bridge/.env.production`
- [ ] 代码修改: `verify=False` in `marzban.py`
- [ ] 代码修改: Inbound 配置匹配 Marzban
- [ ] systemd 服务: `/etc/systemd/system/payment-worker.service`
- [ ] 服务启动: `systemctl start payment-worker && systemctl enable payment-worker`
- [ ] API 服务: nginx 反向代理到 127.0.0.1:8000

### Cloudflare
- [ ] DNS 记录: vpn.residential-agent.com → 38.58.59.142 (proxied)
- [ ] SSL/TLS: 设置为 `Full` (不是 Full strict)

## 日志位置

| 组件 | 日志路径 |
|------|----------|
| Worker | `/var/log/payment-worker.log` |
| API | `/var/log/nginx/access.log` |
| Marzban | `/var/log/marzban/` |
| XRay | `/var/log/xray/` |

## 重启命令速查

```bash
# 重启 Worker
systemctl restart payment-worker

# 重启 API
cd /opt/payment-bridge/code/server && pkill -f uvicorn && nohup venv/bin/uvicorn app.main:app --host 127.0.0.1 --port 8000 &

# 重启 Marzban
systemctl restart xray
systemctl restart nginx

# 重启数据库
docker restart payment_postgres payment_redis
```

---

**文档版本**: v1.0  
**最后更新**: 2026-04-01  
**维护者**: System Admin
