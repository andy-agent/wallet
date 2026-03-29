# API Contract v1.0

**版本**: v1.0  
**状态**: 已冻结  
**日期**: 2026-03-29  
**适用范围**: 一期（SOL + USDT-TRC20）

---

## 目录

1. [通用规范](#1-通用规范)
2. [客户端接口](#2-客户端接口)
3. [管理端接口](#3-管理端接口)
4. [数据模型](#4-数据模型)
5. [错误码](#5-错误码)

---

## 1. 通用规范

### 1.1 基础 URL

```
客户端接口: {BASE_URL}/client/v1
管理端接口: {BASE_URL}/admin/v1
```

### 1.2 认证方式

```
客户端: Authorization: Bearer {client_token}
管理端: Authorization: Bearer {admin_token}
```

### 1.3 响应格式

```json
{
  "code": "SUCCESS",
  "message": "操作成功",
  "data": { }
}
```

错误响应：

```json
{
  "code": "ORDER_NOT_FOUND",
  "message": "订单不存在",
  "data": null
}
```

### 1.4 HTTP 状态码

| 状态码 | 含义 |
|--------|------|
| 200 | 成功 |
| 400 | 请求参数错误 |
| 401 | 未认证/Token 过期 |
| 403 | 无权限（客户端访问管理接口） |
| 404 | 资源不存在 |
| 409 | 冲突（幂等性违反） |
| 429 | 请求过于频繁 |
| 500 | 服务器内部错误 |

---

## 2. 客户端接口

### 2.1 获取套餐列表

```http
GET /client/v1/plans
```

**请求参数**: 无（公开接口，无需认证）

**响应示例**:

```json
{
  "code": "SUCCESS",
  "message": "success",
  "data": {
    "plans": [
      {
        "id": "monthly_100g",
        "name": "月度套餐 100GB",
        "description": "100GB流量，30天有效期，美国/日本/新加坡节点",
        "traffic_bytes": 107374182400,
        "duration_days": 30,
        "price_usd": "5.00",
        "supported_assets": ["SOL", "USDT_TRC20"],
        "badge": "HOT"
      },
      {
        "id": "yearly_1t",
        "name": "年度套餐 1TB",
        "description": "1TB流量，365天有效期，全节点",
        "traffic_bytes": 1099511627776,
        "duration_days": 365,
        "price_usd": "45.00",
        "supported_assets": ["SOL", "USDT_TRC20"],
        "badge": null
      }
    ]
  }
}
```

### 2.2 创建订单

```http
POST /client/v1/orders
Content-Type: application/json
```

**请求体**（新购）:

```json
{
  "plan_id": "monthly_100g",
  "purchase_type": "new",
  "asset_code": "SOL",
  "client_device_id": "550e8400-e29b-41d4-a716-446655440000",
  "client_version": "2.0.15"
}
```

**请求体**（续费）:

```json
{
  "plan_id": "monthly_100g",
  "purchase_type": "renew",
  "asset_code": "USDT_TRC20",
  "client_device_id": "550e8400-e29b-41d4-a716-446655440000",
  "client_version": "2.0.15",
  "client_token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

**字段说明**:

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| plan_id | string | 是 | 套餐 ID |
| purchase_type | string | 是 | `new` 或 `renew` |
| asset_code | string | 是 | `SOL` 或 `USDT_TRC20` |
| client_device_id | string | 是 | 设备唯一标识 |
| client_version | string | 是 | App 版本 |
| client_token | string | 续费时必填 | 现有用户 token |

**响应示例**（成功）:

```json
{
  "code": "SUCCESS",
  "message": "订单创建成功",
  "data": {
    "order_id": "ord_2v8rN3kLmP5w",
    "order_no": "ORD202603290001",
    "status": "pending_payment",
    "plan": {
      "id": "monthly_100g",
      "name": "月度套餐 100GB",
      "duration_days": 30
    },
    "payment": {
      "asset_code": "SOL",
      "chain": "solana",
      "amount_crypto": "0.025345",
      "amount_usd": "5.00",
      "exchange_rate": "197.28",
      "receive_address": "7xKXtg2CW87d97TXJSDpbD5jBkheTqA83TZRuJosgAsU",
      "memo": null,
      "qr_text": "solana:7xKXtg2CW87d97TXJSDpbD5jBkheTqA83TZRuJosgAsU?amount=0.025345&label=v2rayNG"
    },
    "expires_at": "2026-03-29T20:29:59Z",
    "created_at": "2026-03-29T20:14:59Z",
    "polling_after_seconds": 3
  }
}
```

**错误响应**:

```json
// 400 - 参数错误
{
  "code": "INVALID_PLAN_ID",
  "message": "套餐不存在或已下架"
}

// 400 - 不支持币种
{
  "code": "UNSUPPORTED_ASSET",
  "message": "该套餐不支持此支付方式"
}

// 409 - 重复创建（幂等）
{
  "code": "DUPLICATE_ORDER",
  "message": "您有未完成的订单，请完成或等待过期后再试",
  "data": {
    "existing_order_id": "ord_2v8rN3kLmP5w",
    "expires_at": "2026-03-29T20:29:59Z"
  }
}

// 401 - 续费时 token 无效
{
  "code": "INVALID_TOKEN",
  "message": "登录已过期，请重新购买"
}

// 503 - 地址池不足
{
  "code": "ADDRESS_POOL_EMPTY",
  "message": "系统繁忙，请稍后重试"
}
```

### 2.3 查询订单状态

```http
GET /client/v1/orders/{order_id}
```

**路径参数**:
- `order_id`: 订单 ID

**响应示例**（待支付）:

```json
{
  "code": "SUCCESS",
  "message": "success",
  "data": {
    "order_id": "ord_2v8rN3kLmP5w",
    "order_no": "ORD202603290001",
    "status": "pending_payment",
    "status_text": "待支付",
    "plan": {
      "id": "monthly_100g",
      "name": "月度套餐 100GB"
    },
    "payment": {
      "asset_code": "SOL",
      "amount_crypto": "0.025345",
      "receive_address": "7xKXtg2CW87d97TXJSDpbD5jBkheTqA83TZRuJosgAsU",
      "qr_text": "solana:...",
      "expires_at": "2026-03-29T20:29:59Z"
    },
    "expires_at": "2026-03-29T20:29:59Z",
    "created_at": "2026-03-29T20:14:59Z"
  }
}
```

**响应示例**（支付成功，已开通）:

```json
{
  "code": "SUCCESS",
  "message": "success",
  "data": {
    "order_id": "ord_2v8rN3kLmP5w",
    "order_no": "ORD202603290001",
    "status": "fulfilled",
    "status_text": "已完成",
    "plan": {
      "id": "monthly_100g",
      "name": "月度套餐 100GB"
    },
    "payment": {
      "asset_code": "SOL",
      "amount_crypto": "0.025345",
      "tx_hash": "5xK...abc",
      "confirmed_at": "2026-03-29T20:18:22Z"
    },
    "fulfillment": {
      "marzban_username": "user_a1b2c3d4",
      "client_token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
      "token_expires_at": "2026-04-28T20:18:22Z",
      "subscription_url": "https://marzban.example.com/sub/user_a1b2c3d4",
      "expired_at": "2026-04-28T20:18:22Z"
    },
    "created_at": "2026-03-29T20:14:59Z",
    "fulfilled_at": "2026-03-29T20:18:25Z"
  }
}
```

**状态说明**:

| 状态 | 状态文本 | 客户端处理 |
|------|----------|------------|
| pending_payment | 待支付 | 显示二维码和倒计时 |
| seen_onchain | 检测到交易 | 显示"确认中"，展示 tx_hash |
| confirming | 确认中 | 显示确认数进度 |
| paid_success | 已支付 | 显示"开通中" |
| fulfilled | 已完成 | 显示成功，保存 token，跳转订阅 |
| expired | 已过期 | 提示订单过期，重新创建 |
| underpaid | 少付 | 提示少付，联系客服 |
| overpaid | 多付 | 提示多付，正常开通 |
| failed | 支付失败 | 提示错误原因 |
| late_paid | 延迟到账 | 提示联系客服处理 |

### 2.4 拉取订阅

```http
GET /client/v1/subscription
Authorization: Bearer {client_token}
```

**响应示例**:

```json
{
  "code": "SUCCESS",
  "message": "success",
  "data": {
    "user": {
      "username": "user_a1b2c3d4",
      "status": "active",
      "expired_at": "2026-04-28T20:18:22Z",
      "traffic_total": 107374182400,
      "traffic_used": 0,
      "traffic_remaining": 107374182400
    },
    "subscription": {
      "url": "https://marzban.example.com/sub/user_a1b2c3d4?token=xyz",
      "expires_at": "2026-04-28T20:18:22Z"
    },
    "servers": [
      {
        "protocol": "vless",
        "config": "vless://uuid@us.node.example.com:443?security=tls&sni=example.com...",
        "remark": "美国-01",
        "region": "US"
      },
      {
        "protocol": "vless",
        "config": "vless://uuid@jp.node.example.com:443?security=reality...",
        "remark": "日本-01",
        "region": "JP"
      }
    ]
  }
}
```

### 2.5 刷新 Token（可选）

```http
POST /client/v1/auth/refresh
Authorization: Bearer {refresh_token}
```

**响应示例**:

```json
{
  "code": "SUCCESS",
  "message": "success",
  "data": {
    "access_token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "refresh_token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "expires_at": "2026-04-28T20:18:22Z"
  }
}
```

---

## 3. 管理端接口

### 3.1 套餐管理

#### 获取套餐列表

```http
GET /admin/v1/plans
Authorization: Bearer {admin_token}
```

#### 创建套餐

```http
POST /admin/v1/plans
Authorization: Bearer {admin_token}
Content-Type: application/json

{
  "code": "monthly_200g",
  "name": "月度套餐 200GB",
  "description": "200GB流量，30天有效期",
  "traffic_bytes": 214748364800,
  "duration_days": 30,
  "price_usd": "9.00",
  "supported_assets": ["SOL", "USDT_TRC20"],
  "enabled": true,
  "sort_order": 1
}
```

#### 更新套餐

```http
PUT /admin/v1/plans/{plan_id}
Authorization: Bearer {admin_token}
```

#### 删除/禁用套餐

```http
DELETE /admin/v1/plans/{plan_id}
Authorization: Bearer {admin_token}
```

### 3.2 订单管理

#### 订单列表

```http
GET /admin/v1/orders?status=&page=1&size=20
Authorization: Bearer {admin_token}
```

**查询参数**:
- `status`: 筛选状态
- `page`: 页码
- `size`: 每页数量
- `order_no`: 订单号搜索
- `tx_hash`: 交易哈希搜索

**响应示例**:

```json
{
  "code": "SUCCESS",
  "message": "success",
  "data": {
    "total": 156,
    "page": 1,
    "size": 20,
    "orders": [
      {
        "order_id": "ord_2v8rN3kLmP5w",
        "order_no": "ORD202603290001",
        "status": "fulfilled",
        "plan_name": "月度套餐 100GB",
        "purchase_type": "new",
        "amount_usd": "5.00",
        "asset_code": "SOL",
        "amount_crypto": "0.025345",
        "receive_address": "7xKX...",
        "tx_hash": "5xK...abc",
        "client_device_id": "550e8400...",
        "marzban_username": "user_a1b2c3d4",
        "created_at": "2026-03-29T20:14:59Z",
        "fulfilled_at": "2026-03-29T20:18:25Z"
      }
    ]
  }
}
```

#### 订单详情

```http
GET /admin/v1/orders/{order_id}
Authorization: Bearer {admin_token}
```

#### 人工确认支付

```http
POST /admin/v1/orders/{order_id}/manual-confirm
Authorization: Bearer {admin_token}
Content-Type: application/json

{
  "tx_hash": "5xK...abc",
  "amount_crypto": "0.025345",
  "note": "客户已支付，链上确认"
}
```

#### 重试开通

```http
POST /admin/v1/orders/{order_id}/retry-fulfill
Authorization: Bearer {admin_token}
```

#### 标记忽略

```http
POST /admin/v1/orders/{order_id}/mark-ignore
Authorization: Bearer {admin_token}
Content-Type: application/json

{
  "reason": "测试订单"
}
```

### 3.3 地址池管理

#### 获取地址池状态

```http
GET /admin/v1/address-pool/status
Authorization: Bearer {admin_token}
```

**响应示例**:

```json
{
  "code": "SUCCESS",
  "message": "success",
  "data": {
    "solana": {
      "total": 100,
      "available": 45,
      "allocated": 50,
      "swept": 5
    },
    "tron": {
      "total": 100,
      "available": 60,
      "allocated": 35,
      "swept": 5
    }
  }
}
```

#### 批量导入地址

```http
POST /admin/v1/address-pool/import
Authorization: Bearer {admin_token}
Content-Type: application/json

{
  "chain": "solana",
  "addresses": [
    {
      "address": "7xKXtg2CW87d97TXJSDpbD5jBkheTqA83TZRuJosgAsU",
      "encrypted_private_key": "U2FsdGVkX1+..."
    }
  ]
}
```

### 3.4 审计日志

```http
GET /admin/v1/audit-logs?entity_type=order&entity_id=ord_xxx&page=1
Authorization: Bearer {admin_token}
```

---

## 4. 数据模型

### 4.1 Order 订单

```typescript
interface Order {
  order_id: string;           // 订单唯一ID
  order_no: string;           // 订单号（展示用）
  purchase_type: "new" | "renew";
  plan_id: string;
  client_user_id?: string;    // 续费时关联
  marzban_username?: string;  // 关联的Marzban用户
  
  // 支付信息
  chain: "solana" | "tron";
  asset_code: "SOL" | "USDT_TRC20";
  receive_address: string;
  amount_crypto: string;      // 字符串避免精度问题
  amount_usd_locked: string;
  fx_rate_locked: string;
  
  // 状态
  status: OrderStatus;
  expires_at: string;         // ISO 8601
  
  // 链上信息
  tx_hash?: string;
  tx_from?: string;
  confirm_count: number;
  paid_at?: string;
  confirmed_at?: string;
  fulfilled_at?: string;
  
  // 客户端信息
  client_device_id: string;
  client_version: string;
  
  // 错误信息
  error_code?: string;
  error_message?: string;
  
  // 时间戳
  created_at: string;
  updated_at: string;
}

type OrderStatus = 
  | "pending_payment"
  | "seen_onchain"
  | "confirming"
  | "paid_success"
  | "fulfilled"
  | "expired"
  | "underpaid"
  | "overpaid"
  | "failed"
  | "late_paid";
```

### 4.2 Plan 套餐

```typescript
interface Plan {
  id: string;
  code: string;
  name: string;
  description: string;
  traffic_bytes: number;
  duration_days: number;
  price_usd: string;
  supported_assets: string[];
  enabled: boolean;
  sort_order: number;
  created_at: string;
  updated_at: string;
}
```

### 4.3 ClientSession 客户端会话

```typescript
interface ClientSession {
  id: string;
  order_id: string;
  marzban_username: string;
  access_token: string;
  refresh_token: string;
  expires_at: string;
  revoked_at?: string;
  created_at: string;
}
```

---

## 5. 错误码

### 5.1 通用错误码

| 错误码 | HTTP 状态 | 说明 |
|--------|-----------|------|
| SUCCESS | 200 | 成功 |
| INVALID_REQUEST | 400 | 请求格式错误 |
| UNAUTHORIZED | 401 | 未认证 |
| FORBIDDEN | 403 | 无权限 |
| NOT_FOUND | 404 | 资源不存在 |
| INTERNAL_ERROR | 500 | 服务器内部错误 |
| SERVICE_UNAVAILABLE | 503 | 服务暂不可用 |

### 5.2 业务错误码

| 错误码 | HTTP 状态 | 说明 |
|--------|-----------|------|
| INVALID_PLAN_ID | 400 | 套餐不存在或已下架 |
| UNSUPPORTED_ASSET | 400 | 该套餐不支持此支付方式 |
| INVALID_PURCHASE_TYPE | 400 | 购买类型错误 |
| INVALID_TOKEN | 401 | Token 无效或已过期 |
| DUPLICATE_ORDER | 409 | 存在未完成的订单 |
| ORDER_NOT_FOUND | 404 | 订单不存在 |
| ORDER_EXPIRED | 400 | 订单已过期 |
| ADDRESS_POOL_EMPTY | 503 | 地址池不足 |
| FULFILL_FAILED | 500 | 开通账号失败 |
| INSUFFICIENT_FUNDS | 400 | 地址池资金不足（归集相关）|

---

**冻结日期**: 2026-03-29  
**变更控制**: 接口变更需更新版本号，维护兼容性
