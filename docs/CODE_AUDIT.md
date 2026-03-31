# V2rayNG 支付系统 - 代码审计文档

**版本**: 1.0  
**日期**: 2026-04-01  
**审计范围**: 服务端 + 客户端核心逻辑

---

## 目录

1. [系统架构概览](#1-系统架构概览)
2. [核心数据流](#2-核心数据流)
3. [关键模块审计](#3-关键模块审计)
4. [安全审计点](#4-安全审计点)
5. [监听机制详解](#5-监听机制详解)
6. [购买流程验证](#6-购买流程验证)
7. [已知问题与风险](#7-已知问题与风险)

---

## 1. 系统架构概览

### 1.1 整体架构

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                              Android Client                                   │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐  ┌──────────────────┐ │
│  │  MainActivity│  │   Login      │  │  Payment     │  │   OrderPolling   │ │
│  │  (购买入口)   │──│  Activity    │──│  Activity    │──│   UseCase        │ │
│  └──────────────┘  └──────────────┘  └──────────────┘  └──────────────────┘ │
│         │                  │                │                  │            │
│         └──────────────────┴────────────────┴──────────────────┘            │
│                              │                                              │
│                    PaymentRepository (Retrofit + Room)                      │
│                              │                                              │
└──────────────────────────────┼──────────────────────────────────────────────┘
                               │ HTTPS (自签名证书)
                               ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│                           Payment Bridge Server                               │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐  ┌──────────────────┐ │
│  │  Client API  │  │   Worker     │  │   Services   │  │  Integrations    │ │
│  │  /client/v1  │  │  (Scanner)   │  │  (Address    │  │  (Marzban,       │ │
│  │  /admin/v1   │  │  (Scheduler) │  │   Pool, FX)  │  │   Solana, Tron)  │ │
│  └──────┬───────┘  └──────┬───────┘  └──────┬───────┘  └──────────────────┘ │
│         │                  │                 │                               │
│  ┌──────▼──────────────────▼─────────────────▼───────────────────────────┐  │
│  │                    PostgreSQL + Redis                                   │  │
│  │  - users, orders, plans, payment_addresses, client_sessions           │  │
│  └───────────────────────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────────────────────┘
```

### 1.2 技术栈

| 层级 | 技术 | 版本 |
|------|------|------|
| 客户端 | Kotlin | 1.9.25 |
| 客户端 | Room | 2.6.1 |
| 客户端 | Retrofit | 2.11.0 |
| 服务端 | Python | 3.12 |
| 服务端 | FastAPI | 0.104 |
| 服务端 | SQLAlchemy | 2.0 |
| 数据库 | PostgreSQL | 15 |
| 缓存 | Redis | 7 |

---

## 2. 核心数据流

### 2.1 用户购买流程

```
┌─────────┐     ┌──────────┐     ┌─────────┐     ┌──────────┐     ┌─────────┐
│  Client │────►│  Server  │────►│   DB    │────►│  Worker  │────►│ Marzban │
└────┬────┘     └────┬─────┘     └────┬────┘     └────┬─────┘     └────┬────┘
     │               │                │               │                │
     │ 1. POST /auth/register         │               │                │
     │──────────────►│                │               │                │
     │               │ 2. bcrypt hash │               │                │
     │               │ 3. INSERT user │               │                │
     │               │───────────────►│               │                │
     │               │                │               │                │
     │ 4. Return JWT (access + refresh)               │                │
     │◄──────────────│                │               │                │
     │               │                │               │                │
     │ 5. POST /orders (with JWT)     │               │                │
     │──────────────►│                │               │                │
     │               │ 6. Verify JWT  │               │                │
     │               │ 7. Allocate address (from pool)│                │
     │               │ 8. INSERT order│               │                │
     │               │───────────────►│               │                │
     │               │                │               │                │
     │ 9. Return: receive_address, amount_crypto     │                │
     │◄──────────────│                │               │                │
     │               │                │               │                │
     │ 10. User pays to receive_address on blockchain│                │
     │               │                │               │                │
     │               │◄───────────────┘               │                │
     │               │ 11. Worker polls blockchain    │                │
     │               │    every 10 seconds            │                │
     │               │    (scanner.py)                │                │
     │               │                │               │                │
     │               │                │ 12. Detect payment             │
     │               │                │     Update status:             │
     │               │                │     pending_payment → seen_onchain
     │               │                │               │                │
     │               │                │ 13. confirm_seen_transactions  │
     │               │                │     every 10 seconds           │
     │               │                │     Wait for confirmations     │
     │               │                │     (SOL: 32, Tron: 19)        │
     │               │                │               │                │
     │               │                │ 14. Status: seen_onchain → paid_success
     │               │                │               │                │
     │               │                │ 15. fulfill_paid_orders        │
     │               │                │     every 5 seconds            │
     │               │                │               │                │
     │               │                │               │ 16. Call Marzban API
     │               │                │               │     Create user  │
     │               │                │               │     with subscription
     │               │                │               │───────────────►│
     │               │                │               │                │
     │               │                │               │ 17. Return     │
     │               │                │               │     subscription_url
     │               │◄───────────────┴───────────────┴────────────────│
     │               │                │               │                │
     │ 18. Client polls /orders/{id}  │               │                │
     │    Status: fulfilled           │               │                │
     │    With subscription_url       │               │                │
     │◄──────────────│                │               │                │
```

### 2.2 订单状态机

```
                    ┌─────────────────┐
                    │ pending_payment │ (初始状态, 15分钟过期)
                    │    (可支付)      │
                    └────────┬────────┘
                             │
           ┌─────────────────┼─────────────────┐
           │                 │                 │
           ▼                 ▼                 ▼
    ┌─────────────┐   ┌─────────────┐   ┌─────────────┐
    │   expired   │   │  cancelled  │   │seen_onchain │
    │   (过期)     │   │   (已取消)   │   │(链上检测到) │
    └─────────────┘   └─────────────┘   └──────┬──────┘
                                               │
                                               ▼
                                        ┌─────────────┐
                                        │ confirming  │
                                        │  (确认中)   │
                                        └──────┬──────┘
                                               │
                     ┌─────────────────────────┼─────────────────────────┐
                     │                         │                         │
                     ▼                         ▼                         ▼
              ┌─────────────┐           ┌─────────────┐           ┌─────────────┐
              │ paid_success│           │  underpaid  │           │  overpaid   │
              │ (已确认支付) │           │   (少付)    │           │   (多付)    │
              └──────┬──────┘           └─────────────┘           └─────────────┘
                     │
                     ▼
              ┌─────────────┐
              │  fulfilled  │
              │ (已开通服务) │
              └─────────────┘
```

---

## 3. 关键模块审计

### 3.1 认证模块 (app/api/client/auth.py)

**核心函数**:
```python
# 密码哈希 (bcrypt)
def get_password_hash(password: str) -> str:
    return pwd_context.hash(password[:72])  # bcrypt限制72字节

def verify_password(plain_password: str, hashed_password: str) -> bool:
    return pwd_context.verify(plain_password[:72], hashed_password)

# JWT Token 生成
def create_access_token(user_id: str, username: str) -> str:
    expire = datetime.now(timezone.utc) + timedelta(minutes=30)
    to_encode = {
        "sub": username,
        "user_id": user_id,
        "exp": expire,
        "type": "access"
    }
    return jwt.encode(to_encode, settings.jwt_secret, algorithm="HS256")
```

**审计点**:
- ✅ JWT过期时间30分钟
- ✅ Refresh Token过期时间7天
- ⚠️ bcrypt密码截断至72字节（已修复）
- ✅ 密码强度验证：8+字符，大小写+数字

### 3.2 订单模块 (app/api/client/orders.py)

**核心函数**:
```python
async def create_order(
    request: CreateOrderRequest,
    current_user: User = Depends(get_current_user),  # 强制登录
    ...
) -> Response[OrderResponseData]:
    # 1. 验证套餐
    # 2. 获取汇率
    # 3. 分配收款地址 (AddressPoolService)
    # 4. 创建订单记录
    # 5. 返回支付信息
```

**续费安全检查** (已添加):
```python
if request.purchase_type == "renew":
    # 查询当前用户的已履行订单
    last_order = await get_user_last_fulfilled_order(...)
    if not last_order:
        raise ForbiddenException("无历史订单，无法续费")
    
    # 验证marzban_username匹配
    if request.marzban_username != last_order.marzban_username:
        raise ForbiddenException("无法为其他账号续费")
```

**审计点**:
- ✅ 强制登录才能创建订单
- ✅ 续费时验证用户历史订单
- ✅ 防止水平越权攻击

### 3.3 地址池服务 (app/services/address_pool.py)

**核心逻辑**:
```python
class AddressPoolService:
    async def allocate_address(self, chain: str, asset_code: str, order_id: str):
        # 幂等检查：如果订单已有地址，直接返回
        existing = await self.get_address_by_order(order_id)
        if existing:
            return existing
        
        # SELECT FOR UPDATE SKIP LOCKED 防止竞争条件
        stmt = (
            select(PaymentAddress)
            .where(
                PaymentAddress.chain == chain,
                PaymentAddress.asset_code == asset_code,
                PaymentAddress.status == "available"
            )
            .limit(1)
            .with_for_update(skip_locked=True)
        )
        
        # 分配地址给订单
        address.status = "allocated"
        address.allocated_order_id = order_id
```

**审计点**:
- ✅ 幂等性保证：同一订单只能分配一个地址
- ✅ 数据库行锁防止并发问题
- ⚠️ 地址池耗尽时会返回错误 (需要监控)

---

## 4. 安全审计点

### 4.1 已修复的安全问题

| 问题 | 位置 | 修复措施 | 状态 |
|------|------|----------|------|
| 续费水平越权 | orders.py | 验证用户历史订单 | ✅ 已修复 |
| 字段类型错误 | orders.py | Optional[str] | ✅ 已修复 |
| 密码长度限制 | auth.py | 截断至72字节 | ✅ 已修复 |
| 数据库外键 | models/ | 添加user_id外键 | ✅ 已修复 |

### 4.2 剩余安全风险

| 风险 | 级别 | 说明 | 建议 |
|------|------|------|------|
| 自签名证书 | 🟡 中 | 客户端绕过证书验证 | 生产环境使用正规证书 |
| JWT密钥硬编码 | 🔴 高 | .env文件中的密钥 | 使用密钥管理服务 |
| 地址池耗尽 | 🟡 中 | 无地址时无法创建订单 | 添加监控和预警 |
| Mock模式 | 🔴 高 | 当前启用Mock模式 | 生产环境关闭 |

---

## 5. 监听机制详解

### 5.1 Worker架构

```
app/workers/scheduler.py
├── scan_pending_orders()      # 每10秒
├── confirm_seen_transactions() # 每10秒
├── expire_orders()            # 每60秒
├── fulfill_paid_orders()      # 每5秒
└── release_expired_addresses() # 每300秒
```

### 5.2 扫描流程 (scanner.py)

**Step 1: 扫描待支付订单**
```python
async def scan_pending_orders():
    # 查询 pending_payment 状态的订单
    orders = await db.execute(
        select(Order)
        .where(Order.status == "pending_payment")
        .where(Order.expires_at > datetime.now(timezone.utc))
    )
    
    for order in orders:
        # 获取对应链的客户端
        client = get_chain_client(order.chain, order.asset_code)
        
        # 检测支付
        await _detect_payment_for_order(session, order, client)
```

**Step 2: 检测支付 (Solana示例)**
```python
async def _detect_native_payment(session, order, client):
    # 调用 Solana RPC 查询地址交易
    detection_result = await client.detect_payment(
        address=order.receive_address,
        expected_amount=Decimal(order.amount_crypto)
    )
    
    if detection_result.found:
        # 状态转换：pending_payment → seen_onchain
        order.status = "seen_onchain"
        order.tx_hash = detection_result.tx_hash
        order.confirm_count = detection_result.confirmations
```

**Step 3: 确认交易**
```python
async def confirm_seen_transactions():
    # 查询 seen_onchain 或 confirming 状态的订单
    orders = await db.execute(
        select(Order)
        .where(Order.status.in_(["seen_onchain", "confirming"]))
    )
    
    for order in orders:
        # 获取最新确认数
        tx = await client.get_transaction(order.tx_hash)
        current_confirmations = tx.confirmations
        
        # 检查是否达到确认数要求
        required = get_required_confirmations(order.chain)
        # SOL: 32, Tron: 19
        
        if current_confirmations >= required:
            # 检查金额匹配（带容差）
            if min_acceptable <= actual_amount <= max_acceptable:
                order.status = "paid_success"
            elif actual_amount < min_acceptable:
                order.status = "underpaid"
            else:
                order.status = "overpaid"
```

**Step 4: 开通服务**
```python
async def fulfill_paid_orders():
    # 查询 paid_success 状态的订单
    orders = await db.execute(
        select(Order)
        .where(Order.status == "paid_success")
    )
    
    for order in orders:
        # 调用 Marzban API
        marzban_user = await marzban.create_user(
            username=generate_username(),
            expire=now + plan.duration_days,
            data_limit=plan.traffic_bytes
        )
        
        # 创建 ClientSession
        session = ClientSession(
            user_id=order.user_id,
            marzban_username=marzban_user.username,
            access_token=generate_access_token(),
            refresh_token=generate_refresh_token()
        )
        
        # 状态更新：paid_success → fulfilled
        order.status = "fulfilled"
        order.marzban_username = marzban_user.username
```

### 5.3 Mock模式 vs 真实模式

**当前配置状态** (2026-04-01):
```env
# ✅ Marzban - 真实模式已配置
MARZBAN_MOCK_MODE=false
MARZBAN_BASE_URL=https://vpn.residential-agent.com:8443/api

# ⚠️ 区块链 - 仍需要配置真实RPC
SOLANA_MOCK_MODE=true  # 待关闭
TRON_MOCK_MODE=true    # 待关闭
```

**Mock行为**:
- SolanaClient: 返回模拟交易数据（需要真实 RPC）
- TronClient: 返回模拟转账记录（需要真实 RPC）
- ✅ MarzbanClient: **已连接真实服务器** (38.58.59.142)

**生产环境需要**:
```env
SOLANA_MOCK_MODE=false
SOLANA_RPC_URL=https://api.mainnet-beta.solana.com

TRON_MOCK_MODE=false
TRON_FULL_NODE=https://api.trongrid.io
TRON_SOLIDITY_NODE=https://api.trongrid.io
TRON_EVENT_SERVER=https://api.trongrid.io

MARZBAN_MOCK_MODE=false
MARZBAN_BASE_URL=https://vpn.residential-agent.com:8443/api
```

---

## 6. 购买流程验证

### 6.1 已验证的API

| API | 方法 | 状态 | 备注 |
|-----|------|------|------|
| /client/v1/plans | GET | ✅ 正常 | 返回套餐列表 |
| /client/v1/auth/register | POST | ✅ 正常 | 用户注册 |
| /client/v1/auth/login | POST | ✅ 正常 | 返回JWT |
| /client/v1/auth/refresh | POST | 待测 | Token刷新 |
| /client/v1/orders | POST | 待测 | 创建订单 |

### 6.2 端到端测试步骤

```bash
# 1. 注册
TOKEN=$(curl -sk https://154.36.173.184:8080/client/v1/auth/register \
  -X POST \
  -H "Content-Type: application/json" \
  -d '{"username":"test","password":"Test1234"}' \
  | jq -r '.data.access_token')

# 2. 获取套餐
PLAN_ID=$(curl -sk https://154.36.173.184:8080/client/v1/plans \
  | jq -r '.data.plans[0].id')

# 3. 创建订单 (需要JWT)
curl -sk https://154.36.173.184:8080/client/v1/orders \
  -X POST \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -H "X-Client-Version: 1.0.0" \
  -d "{\"plan_id\":\"$PLAN_ID\",\"purchase_type\":\"new\",\"asset_code\":\"SOL\"}"

# 4. 查询订单状态
curl -sk https://154.36.173.184:8080/client/v1/orders/{order_id} \
  -H "Authorization: Bearer $TOKEN"
```

---

## 7. 已知问题与风险

### 7.1 当前配置问题

1. **Mock模式启用**: 区块链交互是模拟数据（Solana/Tron仍在Mock模式）
2. **Marzban已配置**: ✅ VPN服务器已部署 (38.58.59.142)
3. **Worker状态**: 待启动 - scheduler.py需要手动运行

---

## 8. Marzban 服务器部署记录

### 8.1 服务器信息

| 项目 | 值 |
|------|-----|
| IP 地址 | 38.58.59.142 |
| 域名 | vpn.residential-agent.com |
| 管理面板 | https://vpn.residential-agent.com:8443/dashboard |
| 管理员账号 | admin |
| 管理员密码 | MarzbanAdmin2024! |
| API 地址 | https://vpn.residential-agent.com:8443/api |

### 8.2 部署步骤

```bash
# 1. 安装 Docker
curl -fsSL https://get.docker.com | sh

# 2. 克隆 Marzban
cd /opt && git clone https://github.com/Gozargah/Marzban.git

# 3. 配置环境变量 (.env)
UVICORN_HOST=0.0.0.0
UVICORN_PORT=8000
UVICORN_SSL_CERTFILE=/var/lib/marzban/cert.crt
UVICORN_SSL_KEYFILE=/var/lib/marzban/key.key
SUDO_USERNAME=admin
SUDO_PASSWORD=MarzbanAdmin2024!
SQLALCHEMY_DATABASE_URL=sqlite:////var/lib/marzban/db.sqlite3

# 4. 上传 Cloudflare Origin Certificate
# - cert.crt -> /var/lib/marzban/cert.crt
# - key.key -> /var/lib/marzban/key.key

# 5. 下载 Xray 核心
cd /var/lib/marzban
wget https://github.com/XTLS/Xray-core/releases/download/v1.8.23/Xray-linux-64.zip
unzip Xray-linux-64.zip
mv xray-core xray && chmod +x xray

# 6. 初始化数据库
docker compose run --rm marzban bash -c "cd /code && alembic upgrade head"

# 7. 启动服务
docker compose up -d

# 8. 安装并配置 Nginx 反向代理
apt-get install -y nginx
cat > /etc/nginx/sites-available/marzban << 'EOF'
server {
    listen 8443 ssl http2;
    server_name vpn.residential-agent.com;
    ssl_certificate /var/lib/marzban/cert.crt;
    ssl_certificate_key /var/lib/marzban/key.key;
    location / {
        proxy_pass http://127.0.0.1:8000;
        proxy_http_version 1.1;
        proxy_set_header Upgrade $http_upgrade;
        proxy_set_header Connection "upgrade";
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }
}
EOF
ln -s /etc/nginx/sites-available/marzban /etc/nginx/sites-enabled/
systemctl restart nginx

# 9. 配置防火墙
ufw allow 22/tcp
ufw allow 80/tcp
ufw allow 443/tcp
ufw allow 8443/tcp
ufw allow 10000:65000/tcp
ufw allow 10000:65000/udp
ufw --force enable
```

### 8.3 Cloudflare DNS 配置

- **类型**: A 记录
- **名称**: vpn.residential-agent.com
- **内容**: 38.58.59.142
- **代理状态**: 已启用（橙云）
- **SSL/TLS**: Full (strict) - 使用 Origin Certificate

### 8.4 支付桥接服务器配置更新

在 `/opt/payment-bridge/code/deploy/.env` 中更新：

```env
# Marzban (新服务器)
MARZban_API_URL=https://vpn.residential-agent.com:8443/api
MARZban_USERNAME=admin
MARZban_PASSWORD=MarzbanAdmin2024!
MARZban_MOCK_MODE=false

# 区块链（待配置真实RPC）
SOLANA_MOCK_MODE=false
SOLANA_RPC_URL=https://api.mainnet-beta.solana.com
TRON_MOCK_MODE=false
TRON_FULL_NODE=https://api.trongrid.io
```

### 8.5 API 测试验证

```bash
# 获取管理员 Token
curl -X POST https://vpn.residential-agent.com:8443/api/admin/token \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "username=admin&password=MarzbanAdmin2024!"

# 响应示例
{"access_token":"eyJ...","token_type":"bearer"}
```

**状态**: ✅ API 测试通过

---

## 7. 已知问题与风险

### 7.1 当前配置问题

1. **Mock模式启用**: 区块链交互是模拟数据（Solana/Tron仍在Mock模式）
2. **Marzban已配置**: ✅ VPN服务器已部署 (38.58.59.142)
3. **Worker状态**: 待启动 - scheduler.py需要手动运行

### 7.2 代码逻辑问题

1. **订单创建需要device_id Header**: 服务端要求 X-Device-Id，但客户端可能未发送
2. **fulfillment.py 重复**: services/fulfillment.py 和 workers/fulfillment.py 功能重叠
3. **状态机不完整**: UNDERPAID/OVERPAID 状态没有后续处理逻辑

### 7.3 生产环境部署清单

#### 基础设施
- [x] 部署 Marzban VPN 服务器 (38.58.59.142)
- [x] 配置 Cloudflare DNS (vpn.residential-agent.com)
- [x] 配置 Cloudflare Origin Certificate
- [x] 配置 Nginx 反向代理 (8443端口)
- [x] 配置防火墙规则
- [ ] 配置 Solana 真实 RPC 节点
- [ ] 配置 Tron 真实 RPC 节点 (TronGrid)
- [ ] 替换自签名证书为正规 SSL 证书

#### 应用配置
- [x] 配置 Marzban API 凭据
- [ ] 关闭 Solana Mock 模式
- [ ] 关闭 Tron Mock 模式
- [ ] 启动 Worker 进程 (scheduler.py)
- [ ] 配置 JWT 密钥为随机强密码
- [ ] 添加地址池监控和预警
- [ ] 配置日志收集和告警

#### 测试验证
- [ ] 完整购买流程测试 (注册→支付→开通)
- [ ] 真实区块链支付测试
- [ ] VPN 连接测试
- [ ] 续费流程测试

---

## 附录: 关键文件清单

### 服务端关键文件
```
app/
├── api/client/
│   ├── auth.py          # 登录/注册/JWT
│   ├── orders.py        # 订单创建/查询
│   └── plans.py         # 套餐列表
├── workers/
│   ├── scanner.py       # 区块链扫描
│   ├── fulfillment.py   # 开通服务
│   └── scheduler.py     # 定时任务调度
├── services/
│   ├── address_pool.py  # 地址池管理
│   └── fx_rate.py       # 汇率服务
├── integrations/
│   ├── solana.py        # Solana RPC
│   ├── tron.py          # Tron RPC
│   └── marzban.py       # Marzban API
└── models/
    ├── user.py          # 用户模型
    ├── order.py         # 订单模型
    └── client_session.py # 会话模型
```

### 客户端关键文件
```
payment/
├── ui/activity/
│   ├── LoginActivity.kt       # 登录/注册
│   └── UserProfileActivity.kt # 用户中心
├── data/repository/
│   └── PaymentRepository.kt   # API调用/Token管理
└── ui/
    └── OrderPollingUseCase.kt # 订单轮询
```

---

*审计完成*
