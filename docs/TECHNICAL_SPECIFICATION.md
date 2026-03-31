# V2rayNG 支付系统技术规格文档

**版本**: 1.0  
**日期**: 2026-03-31  
**状态**: 开发中

---

## 目录

1. [系统架构](#1-系统架构)
2. [服务依赖](#2-服务依赖)
3. [数据库模型](#3-数据库模型)
4. [API 契约](#4-api-契约)
5. [用户认证流程](#5-用户认证流程)
6. [订单创建流程](#6-订单创建流程)
7. [支付处理流程](#7-支付处理流程)
8. [已知问题与待修复项](#8-已知问题与待修复项)

---

## 1. 系统架构

### 1.1 整体架构

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                              Android Client                                   │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐  ┌──────────────────┐ │
│  │  UI Layer    │  │   Payment    │  │  Local DB    │  │   V2ray Core     │ │
│  │  Activities  │◄─┤ Repository   │◄─┤  (Room)      │  │   Integration    │ │
│  └──────────────┘  └──────┬───────┘  └──────────────┘  └──────────────────┘ │
│                           │                                                 │
│                           ▼ HTTPS (自签名证书)                                │
└───────────────────────────┬─────────────────────────────────────────────────┘
                            │
                            ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│                              Payment Bridge Server                            │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐  ┌──────────────────┐ │
│  │  Client API  │  │   Workers    │  │   Services   │  │  Integrations    │ │
│  │  /client/v1  │  │  (Scanner,   │  │  (Address    │  │  (Marzban,       │ │
│  │  /admin/v1   │  │   Scheduler) │  │   Pool, FX)  │  │   Solana, Tron)  │ │
│  └──────┬───────┘  └──────────────┘  └──────────────┘  └──────────────────┘ │
│         │                                                                   │
│  ┌──────▼──────────────────────────────────────────────────────────────────┐│
│  │                    PostgreSQL + Redis                                    ││
│  └──────────────────────────────────────────────────────────────────────────┘│
└─────────────────────────────────────────────────────────────────────────────┘
```

### 1.2 技术栈

| 组件 | 技术 |
|------|------|
| Android Client | Kotlin 1.9.25, Room 2.6.1, Retrofit 2.11.0, kapt |
| Server | Python 3.11, FastAPI, SQLAlchemy 2.0, Pydantic v2 |
| Database | PostgreSQL 15 |
| Cache | Redis 7 |
| Blockchain | Solana (SPL Token), Tron (USDT TRC20) |
| VPN Panel | Marzban |

---

## 2. 服务依赖

### 2.1 服务端依赖

```yaml
# 核心依赖
python: "3.11"
fastapi: "^0.104"
uvicorn: "^0.24"
pydantic: "^2.5"
sqlalchemy: "^2.0"
asyncpg: "^0.29"        # PostgreSQL 异步驱动
redis: "^5.0"           # Redis 客户端
alembic: "^1.12"        # 数据库迁移

# 区块链
solders: "^0.21"        # Solana
solana: "^0.30"
tronpy: "^1.0"          # Tron

# 工具
ulid-py: "^1.0"         # ULID 唯一ID
pyjwt: "^2.8"           # JWT
bcrypt: "^4.1"          # 密码哈希
python-multipart: "^0.0.6"
```

### 2.2 Android 依赖

```kotlin
// 网络
implementation("com.squareup.retrofit2:retrofit:2.11.0")
implementation("com.squareup.retrofit2:converter-gson:2.11.0")
implementation("com.squareup.okhttp3:okhttp:4.12.0")

// 本地存储
implementation("androidx.room:room-runtime:2.6.1")
kapt("androidx.room:room-compiler:2.6.1")
implementation("androidx.room:room-ktx:2.6.1")

// Kotlin (降级以兼容 Room)
kotlin_version = "1.9.25"
classpath("org.jetbrains.kotlinx:kotlinx-metadata-jvm:0.9.0")
```

---

## 3. 数据库模型

### 3.1 用户模型 (users)

```python
class User(Base):
    __tablename__ = "users"
    
    id: String(32) PK          # ULID
    username: String(64) UQ    # 登录名
    password_hash: String(256) # bcrypt 哈希
    email: String(256) nullable
    is_active: Boolean default=True
    created_at: DateTime
    updated_at: DateTime
```

### 3.2 订单模型 (orders)

```python
class Order(Base):
    __tablename__ = "orders"
    
    # 主键
    id: String(32) PK              # ULID
    order_no: String(32) UQ        # 展示用订单号
    
    # 购买类型
    purchase_type: String(10)      # new | renew
    
    # 关联
    plan_id: String(32) FK → plans.id
    user_id: String(32) FK → users.id    # 关联登录用户
    client_user_id: String(32) nullable   # 续费时用的客户端用户ID
    marzban_username: String(64) nullable # Marzban 用户名
    
    # 支付信息
    chain: String(20)              # solana | tron
    asset_code: String(20)         # SOL | USDT_TRC20 | SPL_TOKEN
    receive_address: String(64)    # 收款地址
    amount_crypto: Numeric(36,18)  # 加密货币金额
    amount_usd_locked: Numeric(10,2)
    fx_rate_locked: Numeric(18,8)  # 锁定汇率
    
    # 状态 (状态机管理)
    status: String(20)             # pending_payment → paid → confirmed → fulfilled
    expires_at: DateTime           # 订单过期时间
    
    # 链上信息
    tx_hash: String(128) nullable
    tx_from: String(64) nullable   # 付款方地址
    confirm_count: Integer default=0
    paid_at: DateTime nullable
    confirmed_at: DateTime nullable
    fulfilled_at: DateTime nullable
    
    # 客户端信息
    client_version: String(16)     # 客户端版本
    
    # 错误信息
    error_code: String(32) nullable
    error_message: String(256) nullable
    
    created_at: DateTime
    updated_at: DateTime
```

### 3.3 套餐模型 (plans)

```python
class Plan(Base):
    __tablename__ = "plans"
    
    id: String(32) PK
    code: String(32) UQ
    name: String(128)
    description: String(512)
    
    # 套餐内容
    traffic_bytes: BigInteger
    duration_days: Integer
    price_usd: Numeric(10,2)
    
    # 支持的支付方式 (JSON)
    supported_assets: JSON default=["SOL", "USDT_TRC20"]
    
    enabled: Boolean default=True
    sort_order: Integer default=0
```

### 3.4 收款地址池 (payment_addresses)

```python
class PaymentAddress(Base):
    __tablename__ = "payment_addresses"
    
    id: Integer PK
    chain: String(20)              # solana | tron
    asset_code: String(20)
    address: String(64) UQ         # 收款地址
    encrypted_private_key: String(512) nullable
    
    status: String(20)             # available | allocated | expired | swept | disabled
    allocated_order_id: String(32) FK → orders.id nullable
    allocated_at: DateTime nullable
    
    last_seen_tx_hash: String(128) nullable
    created_at: DateTime
    updated_at: DateTime
```

---

## 4. API 契约

### 4.1 基础信息

| 项目 | 值 |
|------|-----|
| 基础 URL | `https://154.36.173.184:8080` |
| API 版本 | `/client/v1` |
| 完整 URL | `https://154.36.173.184:8080/client/v1` |
| 认证方式 | Bearer Token (JWT) |

### 4.2 通用响应格式

```json
{
  "code": "SUCCESS",
  "message": "操作成功",
  "data": { ... }
}
```

错误码:
- `SUCCESS` - 成功
- `UNAUTHORIZED` - 未授权
- `NOT_FOUND` - 资源不存在
- `VALIDATION_ERROR` - 参数验证失败
- `SERVICE_UNAVAILABLE` - 服务不可用

### 4.3 认证 API

#### POST `/client/v1/auth/register`
注册新用户

**请求:**
```json
{
  "username": "string (3-64字符, 字母数字下划线)",
  "password": "string (8+字符, 大小写+数字)",
  "email": "string? (可选)"
}
```

**响应:**
```json
{
  "code": "SUCCESS",
  "data": {
    "user_id": "string",
    "username": "string",
    "access_token": "string (JWT)",
    "refresh_token": "string (JWT)",
    "expires_at": "2026-03-31T12:00:00Z"
  }
}
```

#### POST `/client/v1/auth/login`
用户登录

**请求:**
```json
{
  "username": "string",
  "password": "string"
}
```

**响应:** 同注册

#### POST `/client/v1/auth/refresh`
刷新 Token

**请求头:**
```
Authorization: Bearer {refresh_token}
```

**响应:**
```json
{
  "code": "SUCCESS",
  "data": {
    "access_token": "string",
    "expires_at": "2026-03-31T12:00:00Z"
  }
}
```

### 4.4 订单 API

#### POST `/client/v1/orders`
创建订单

**请求头:**
```
Authorization: Bearer {access_token}
X-Client-Version: 1.0.0
Content-Type: application/json
```

**请求体:**
```json
{
  "plan_id": "string (ULID)",
  "purchase_type": "new | renew",
  "asset_code": "SOL | USDT_TRC20 | SPL_TOKEN",
  "client_device_id": "string?",
  "client_version": "string?",
  "client_token": "string?",
  "client_user_id": "string? (renew时)",
  "marzban_username": "string? (renew时)"
}
```

**响应:**
```json
{
  "code": "SUCCESS",
  "data": {
    "order_id": "string",
    "order_no": "string",
    "plan_id": "string",
    "purchase_type": "new | renew",
    "chain": "solana | tron",
    "asset_code": "SOL | USDT_TRC20",
    "receive_address": "string",
    "amount_crypto": "0.123456789",
    "amount_usd": "10.00",
    "fx_rate": "80.50",
    "status": "pending_payment",
    "expires_at": "2026-03-31T12:15:00Z",
    "created_at": "2026-03-31T12:00:00Z",
    "client_user_id": "string?",
    "marzban_username": "string?"
  }
}
```

#### GET `/client/v1/orders/{orderId}`
查询订单状态

**响应:** 同创建订单响应

### 4.5 套餐 API

#### GET `/client/v1/plans`
获取套餐列表

**响应:**
```json
{
  "code": "SUCCESS",
  "data": {
    "plans": [
      {
        "id": "string",
        "code": "monthly",
        "name": "月度套餐",
        "description": "string",
        "traffic_bytes": 1099511627776,
        "duration_days": 30,
        "price_usd": "10.00",
        "supported_assets": ["SOL", "USDT_TRC20"]
      }
    ]
  }
}
```

### 4.6 客户端-服务端字段对照表

| 功能 | 客户端字段 | 服务端字段 | 类型 | 必需 | 说明 |
|------|-----------|-----------|------|------|------|
| **创建订单请求** |
| 套餐ID | `planId` | `plan_id` | String | ✅ | ULID |
| 购买类型 | `purchaseType` | `purchase_type` | String | ✅ | new/renew |
| 资产代码 | `assetCode` | `asset_code` | String | ✅ | SOL/USDT_TRC20 |
| 设备ID | `clientDeviceId` | `client_device_id` | String | ❌ | 追踪用 |
| 客户端版本 | `clientVersion` | `client_version` | String | ❌ | 从Header也可获取 |
| 客户端Token | `clientToken` | `client_token` | String | ❌ | 匿名用户可为null |
| 用户ID | `clientUserId` | `client_user_id` | String | ❌ | renew时必需 |
| Marzban用户名 | `marzbanUsername` | `marzban_username` | String | ❌ | renew时必需 |
| **订单响应** |
| 订单ID | `orderId` | `order_id` | String | ✅ | ULID |
| 订单号 | `orderNo` | `order_no` | String | ✅ | 展示用 |
| 链 | - | `chain` | String | ✅ | solana/tron |
| 收款地址 | `payment.receiveAddress` | `receive_address` | String | ✅ | 区块链地址 |
| 加密货币金额 | `payment.amountCrypto` | `amount_crypto` | String | ✅ | 字符串格式 |
| USD金额 | - | `amount_usd` | String | ✅ | 原始价格 |
| 锁定汇率 | - | `fx_rate` | String | ✅ | 下单时汇率 |
| 状态 | `status` | `status` | String | ✅ | 见状态机 |
| 过期时间 | `expiresAt` | `expires_at` | String | ✅ | ISO 8601 |
| 创建时间 | `createdAt` | `created_at` | String | ✅ | ISO 8601 |

---

## 5. 用户认证流程

### 5.1 匿名购买 vs 注册购买

系统支持两种购买方式：

| 模式 | 流程 | 数据保存 |
|------|------|---------|
| **匿名购买** | 不登录 → 创建订单 → 支付 → 首次创建 Marzban 账号 → 返回 Token | Token 保存在客户端 |
| **注册购买** | 注册/登录 → 创建订单 → 支付 → 关联到已有 Marzban 账号 | 用户数据在服务端 |

### 5.2 注册流程

```
┌─────────┐     ┌──────────┐     ┌─────────┐     ┌──────────┐
│ Client  │────►│  Server  │────►│  DB     │────►│ Marzban │
└─────────┘     └──────────┘     └─────────┘     └──────────┘
     │               │               │               │
     │ 1. POST /register              │               │
     │──────────────►│               │               │
     │   {username,  │               │               │
     │    password}  │               │               │
     │               │               │               │
     │               │ 2. 验证唯一性  │               │
     │               │ 3. bcrypt哈希 │               │
     │               │──────────────►│               │
     │               │               │               │
     │               │ 4. 创建User    │               │
     │               │ 5. 创建Session │               │
     │               │◄──────────────│               │
     │               │               │               │
     │ 6. 返回Tokens  │               │               │
     │◄──────────────│               │               │
     │               │               │               │
     │ 7. 保存到Room  │               │               │
```

### 5.3 登录流程

```
Client                              Server
  │                                   │
  │  POST /auth/login                 │
  │  {username, password}            │
  │──────────────────────────────────►│
  │                                   │
  │                                   │ 验证密码 (bcrypt)
  │                                   │ 查询用户
  │                                   │ 创建新 Session
  │                                   │
  │  返回 {access_token, refresh_token}
  │◄──────────────────────────────────│
  │                                   │
  │  保存到 SharedPreferences         │
  │  缓存到 Room 数据库               │
```

### 5.4 当前客户端实现状态

| 功能 | 服务端 | 客户端 | 状态 |
|------|--------|--------|------|
| 注册 | ✅ 完整 | ❌ 未实现 | **缺失** |
| 登录 | ✅ 完整 | ❌ 占位符 | **缺失** |
| Token 刷新 | ✅ 完整 | ❌ 未实现 | **缺失** |
| 自动登录 | ❌ | ❌ 未实现 | **缺失** |

**关键发现**: `LoginActivity.kt` 目前只是一个占位符，没有实际的登录功能实现。

---

## 6. 订单创建流程

### 6.1 流程图

```
┌─────────────────────────────────────────────────────────────────────────┐
│                          订单创建时序图                                   │
└─────────────────────────────────────────────────────────────────────────┘

Client                              Server                    External
  │                                   │                         │
  │  1. 选择套餐和支付方式             │                         │
  │                                   │                         │
  │  2. POST /orders                  │                         │
  │     Authorization: Bearer {token} │                         │
  │     X-Client-Version: 1.0.0       │                         │
  │     {                             │                         │
  │       plan_id,                    │                         │
  │       purchase_type: "new",       │                         │
  │       asset_code: "SOL",          │                         │
  │       client_device_id,           │                         │
  │       client_version              │                         │
  │     }                             │                         │
  │──────────────────────────────────►│                         │
  │                                   │                         │
  │                                   │  3. 验证用户登录状态      │
  │                                   │  4. 验证套餐可用性        │
  │                                   │  5. 获取实时汇率         │
  │                                   │     ┌─────────────────────┤
  │                                   │     │ CoinGecko/Jupiter   │
  │                                   │     │ (带缓存+故障转移)    │
  │                                   │◄────┘                     │
  │                                   │                         │
  │                                   │  6. 计算加密货币金额      │
  │                                   │     amount_usd / fx_rate │
  │                                   │                         │
  │                                   │  7. 分配收款地址          │
  │                                   │     从地址池取可用地址    │
  │                                   │     状态: available → allocated
  │                                   │                         │
  │                                   │  8. 创建订单记录          │
  │                                   │     INSERT INTO orders   │
  │                                   │     status: pending_payment
  │                                   │                         │
  │  9. 返回订单信息                   │                         │
  │     {                             │                         │
  │       order_id,                   │                         │
  │       order_no,                   │                         │
  │       receive_address, ◄──────────┤  用户支付到这个地址      │
  │       amount_crypto,              │                         │
  │       expires_at (15分钟)          │                         │
  │     }                             │                         │
  │◄──────────────────────────────────│                         │
  │                                   │                         │
  │  10. 显示支付二维码                │                         │
  │      (地址 + 金额)                │                         │
```

### 6.2 订单状态机

```
                    ┌─────────────────┐
                    │  pending_payment │◄──────── 初始状态
                    │    (15分钟过期)   │
                    └────────┬────────┘
                             │
           ┌─────────────────┼─────────────────┐
           │                 │                 │
           ▼                 ▼                 ▼
    ┌─────────────┐   ┌─────────────┐   ┌─────────────┐
    │   expired   │   │   cancelled │   │    paid     │
    │   (过期)     │   │   (已取消)   │   │  (已支付)   │
    └─────────────┘   └─────────────┘   └──────┬──────┘
                                               │
                                               ▼
                                        ┌─────────────┐
                                        │  confirmed  │
                                        │ (链上确认)   │
                                        └──────┬──────┘
                                               │
                                               ▼
                                        ┌─────────────┐
                                        │  fulfilled  │
                                        │ (已开通服务) │
                                        └─────────────┘
```

状态流转:
- `pending_payment` → `paid`: 检测到链上付款交易
- `paid` → `confirmed`: 达到确认数 (SOL: 32, Tron: 19)
- `confirmed` → `fulfilled`: 调用 Marzban API 创建/续费账号
- `pending_payment` → `expired`: 超过15分钟未支付
- `pending_payment` → `cancelled`: 用户主动取消或创建新订单

---

## 7. 支付处理流程

### 7.1 区块链监听

```
┌─────────────────────────────────────────────────────────────────┐
│                     Scanner Worker (后台任务)                    │
│                                                                 │
│  ┌──────────────┐    ┌──────────────┐    ┌──────────────┐      │
│  │  Solana RPC  │    │  Tron Grid   │    │   Database   │      │
│  │  轮询监听     │    │  轮询监听     │    │   更新状态    │      │
│  └──────┬───────┘    └──────┬───────┘    └──────────────┘      │
│         │                   │                                   │
│         └───────────────────┘                                   │
│                     │                                           │
│                     ▼                                           │
│         检测到待处理订单的地址有入账                              │
│                     │                                           │
│         ┌───────────┴───────────┐                               │
│         ▼                       ▼                               │
│    金额 >= 订单金额         金额 < 订单金额                       │
│         │                       │                               │
│         ▼                       ▼                               │
│   状态→paid               状态→partial_paid                    │
│   记录tx_hash              (需人工处理)                         │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

### 7.2 支付确认后处理

```
confirmed 订单 ──► Fulfillment Worker ──► Marzban API ──► 用户可用
                                              │
                                              ▼
                                       ┌──────────────┐
                                       │ 新购 (new)   │
                                       │ - 创建用户    │
                                       │ - 分配套餐    │
                                       │ - 生成订阅链接│
                                       ├──────────────┤
                                       │ 续费 (renew) │
                                       │ - 查找用户    │
                                       │ - 延长有效期  │
                                       │ - 更新流量    │
                                       └──────────────┘
```

### 7.3 收款地址池管理

```
┌───────────────────────────────────────────────────────────────┐
│                      地址池状态流转                            │
├───────────────────────────────────────────────────────────────┤
│                                                               │
│   ┌─────────────┐      allocate       ┌─────────────┐        │
│   │  available  │ ──────────────────► │  allocated  │        │
│   │   (可用)     │                     │  (已分配)    │        │
│   └─────────────┘                     └──────┬──────┘        │
│        ▲                                     │                │
│        │         expire/release             │ sweep         │
│        │    (订单过期或完成)                 │ (资金归集)     │
│   ┌────┴─────┐                              │                │
│   │  expired │◄─────────────────────────────┘                │
│   │ swept    │────────────────────► ┌─────────────┐          │
│   └──────────┘   (资金已转出)        │   swept     │          │
│                                     │  (已归集)    │          │
│                                     └─────────────┘          │
│                                                               │
└───────────────────────────────────────────────────────────────┘
```

---

## 8. 已知问题与待修复项

### 8.1 服务端问题

| # | 问题 | 位置 | 严重程度 | 修复方案 |
|---|------|------|---------|---------|
| 1 | **订单创建需要登录** | `orders.py` line 127 | 🔴 高 | 支持匿名创建订单 |
| 2 | **CreateOrderRequest 字段缺失** | `orders.py` line 49-71 | 🔴 高 | 添加 client_device_id, client_version, client_token |
| 3 | **字段验证不接受 null** | Pydantic 模型 | 🔴 高 | 使用 `Optional[str] = Field(default=None)` |
| 4 | **需要 X-Device-Id Header** | 订单端点 | 🟡 中 | 改为可选或从 body 获取 |
| 5 | **地址池为空** | 数据库 | 🟡 中 | 初始化 Solana/Tron 地址 |

### 8.2 客户端问题

| # | 问题 | 位置 | 严重程度 | 修复方案 |
|---|------|------|---------|---------|
| 1 | **LoginActivity 是占位符** | `LoginActivity.kt` | 🔴 高 | 实现完整登录/注册 UI |
| 2 | **缺少注册功能** | 客户端 | 🔴 高 | 添加注册页面 |
| 3 | **用户无法主动购买** | UI | 🔴 高 | 添加购买按钮/入口 |
| 4 | **区块链地址显示** | `UserProfileActivity` | 🟡 中 | 检查地址是否正确显示 |
| 5 | **支付二维码生成** | UI | 🟡 中 | 集成二维码生成 |
| 6 | **订单轮询状态** | `OrderPollingUseCase` | 🟡 中 | 验证轮询逻辑 |

### 8.3 集成问题

| # | 问题 | 说明 | 修复方案 |
|---|------|------|---------|
| 1 | **自签名证书** | 开发环境使用自签名证书 | 客户端已配置 TrustAllCerts |
| 2 | **端口防火墙** | 8080 端口需要开放 | 已配置 nftables |
| 3 | **Marzban 集成** | 需要配置 Marzban API 地址 | 检查环境变量 |

### 8.4 优先级排序

**P0 (阻断性问题)**
1. 服务端支持匿名创建订单 或 客户端实现登录
2. 修复 CreateOrderRequest 模型字段
3. 修复 null 值验证

**P1 (功能缺失)**
4. 客户端实现 LoginActivity
5. 客户端实现注册功能
6. 初始化地址池数据

**P2 (体验优化)**
7. 支付二维码显示
8. 订单状态实时更新
9. 错误处理优化

---

## 附录 A: 环境配置

### 服务端环境变量

```bash
# 数据库
DATABASE_URL=postgresql+asyncpg://user:pass@localhost:5432/payment

# Redis
REDIS_URL=redis://localhost:6379/0

# JWT
JWT_SECRET=your-secret-key
JWT_ALGORITHM=HS256
JWT_ACCESS_TOKEN_EXPIRE_MINUTES=30
JWT_REFRESH_TOKEN_EXPIRE_DAYS=7

# Marzban
MARZBAN_API_URL=https://marzban.example.com/api
MARZBAN_API_TOKEN=your-token

# Solana
SOLANA_RPC_URL=https://api.mainnet-beta.solana.com
SOLANA_TOKEN_MINT=EPjFWdd5AufqSSqeM2qN1xzybapC8G4wEGGkZwyTDt1v  # USDC

# Tron
TRON_GRID_API_KEY=your-api-key
TRON_USDT_CONTRACT=TR7NHqjeKQxGTCi8q8ZY4pL8otSzgjLj6t
```

### 客户端配置

```kotlin
// PaymentConfig.kt
object PaymentConfig {
    const val API_BASE_URL = "https://154.36.173.184:8080"
    const val API_VERSION = "/client/v1"
    const val ORDER_EXPIRE_TIME_MS = 15 * 60 * 1000L
}
```

---

## 附录 B: 数据库初始化

### 初始化套餐

```sql
INSERT INTO plans (id, code, name, description, traffic_bytes, duration_days, price_usd, supported_assets) VALUES
('01JQVQHKQW6V1N2N0TNWVT1G6J', 'monthly', '月度套餐', '30天 1TB流量', 1099511627776, 30, 10.00, '["SOL", "USDT_TRC20"]'),
('01JQVQHKQW6V1N2N0TNWVT1G6K', 'quarterly', '季度套餐', '90天 3TB流量', 3298534883328, 90, 27.00, '["SOL", "USDT_TRC20"]'),
('01JQVQHKQW6V1N2N0TNWVT1G6L', 'yearly', '年度套餐', '365天 12TB流量', 13194139533312, 365, 96.00, '["SOL", "USDT_TRC20"]');
```

### 初始化收款地址

```python
# 使用管理命令添加地址
python -m app.cli address add --chain solana --address <address> --private-key <encrypted>
python -m app.cli address add --chain tron --address <address> --private-key <encrypted>
```

---

*文档结束*
