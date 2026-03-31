# 任务完成报告：客户端订阅接口

**任务ID**: liaojiang-ajk  
**任务名称**: 客户端订阅接口  
**完成时间**: 2026-03-31

---

## 1. 修改的文件列表

| 文件路径 | 修改类型 | 说明 |
|---------|---------|------|
| `app/api/client/subscription.py` | 新增 | 客户端订阅接口实现，包含 GET /client/v1/subscription 和 verify_client_token 依赖函数 |
| `app/api/client/__init__.py` | 修改 | 导出 subscription_router（已存在 plans_router 和 orders_router） |
| `app/main.py` | 修改 | 注册 subscription_router 到 /client/v1 前缀 |
| `app/core/rate_limit.py` | 修改 | 添加 client_rate_limit 限流函数（60 requests/minute） |

---

## 2. 接口信息

### GET /client/v1/subscription

拉取订阅信息，返回用户的订阅链接、过期时间、流量使用情况。

**请求头**:
```
Authorization: Bearer {client_token}
```

**响应数据**:
```json
{
  "code": "SUCCESS",
  "message": "success",
  "data": {
    "subscription_url": "https://marzban.example.com/sub/xxxxx",
    "expires_at": "2026-04-30T12:00:00",
    "traffic_total": 107374182400,
    "traffic_used": 2147483648,
    "traffic_remaining": 105226698752,
    "nodes": null
  }
}
```

**错误响应**:
- 401: Token 过期或无效
- 403: Token 类型错误或已被吊销
- 404: 用户在 Marzban 中不存在
- 429: 请求过于频繁（限流）

---

## 3. curl 测试命令

### 3.1 测试有效 Token（需要先创建一个有效的 client_token）

```bash
# 设置变量
CLIENT_TOKEN="your_valid_client_token_here"
BASE_URL="http://localhost:8000"

# 测试获取订阅信息
curl -X GET "${BASE_URL}/client/v1/subscription" \
  -H "Authorization: Bearer ${CLIENT_TOKEN}" \
  -H "Content-Type: application/json" | jq
```

### 3.2 测试无效 Token

```bash
# 测试无效 token（应返回 401 或 403）
curl -X GET "${BASE_URL}/client/v1/subscription" \
  -H "Authorization: Bearer invalid_token_here" \
  -H "Content-Type: application/json" | jq
```

### 3.3 测试过期 Token

```bash
# 使用一个过期的 JWT token 测试（应返回 401）
EXPIRED_TOKEN="eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
curl -X GET "${BASE_URL}/client/v1/subscription" \
  -H "Authorization: Bearer ${EXPIRED_TOKEN}" \
  -H "Content-Type: application/json" | jq
```

### 3.4 测试缺少 Authorization Header

```bash
# 不携带 Authorization header（应返回 401）
curl -X GET "${BASE_URL}/client/v1/subscription" \
  -H "Content-Type: application/json" | jq
```

### 3.5 测试被吊销的 Token

```bash
# 1. 先获取一个有效的 token
CLIENT_TOKEN="your_valid_token"

# 2. 调用续费接口（续费会吊销旧 token，生成新 token）
# 3. 使用旧 token 访问订阅接口（应返回 403 Token has been revoked）
curl -X GET "${BASE_URL}/client/v1/subscription" \
  -H "Authorization: Bearer ${OLD_TOKEN}" \
  -H "Content-Type: application/json" | jq
```

---

## 4. Token 验证逻辑说明

### 4.1 验证流程

`verify_client_token()` 依赖函数实现了完整的 Token 验证流程：

```
┌─────────────────────────────────────────────────────────────────┐
│  1. 提取 Token                                                   │
│     - 从 Authorization header 提取 "Bearer <token>"             │
│     - 如果格式不正确，返回 401                                   │
└─────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│  2. JWT 解码与验证                                               │
│     - 使用 JWT_SECRET 和 HS256 算法解码                         │
│     - 验证签名是否有效                                          │
│     - 如果签名无效，返回 403                                     │
└─────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│  3. 过期时间验证                                                 │
│     - 检查 exp 字段是否过期                                      │
│     - 如果过期，返回 401 "Token has expired"                    │
└─────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│  4. Token 类型验证                                               │
│     - 验证 payload.type == "access"                             │
│     - 如果不匹配，返回 403                                       │
└─────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│  5. 数据库会话验证                                               │
│     - 查询 ClientSession 表，匹配 access_token 和 username      │
│     - 如果记录不存在，返回 403 "session not found"              │
│     - 如果 revoked_at 不为 null，返回 403 "Token has been revoked"
│     - 如果 expires_at < now，返回 401 "Session has expired"     │
└─────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│  6. 返回 Token Payload                                           │
│     - username: Marzban 用户名                                   │
│     - exp: 过期时间                                              │
│     - type: token 类型                                           │
└─────────────────────────────────────────────────────────────────┘
```

### 4.2 Token 结构

```python
# JWT Payload 结构
{
  "sub": "user_xxxxx",           # Marzban 用户名
  "type": "access",              # Token 类型
  "iat": 1711900800,             # 签发时间
  "exp": 1714502400,             # 过期时间
  "jti": "uuid-xxxx"             # Token 唯一标识
}
```

### 4.3 安全特性

1. **双重验证**: JWT 签名验证 + 数据库会话验证
2. **吊销机制**: 支持通过 revoked_at 字段吊销 Token
3. **过期检查**: JWT exp + 数据库 expires_at 双重检查
4. **限流保护**: 60 requests/minute 限流

### 4.4 错误码对照

| HTTP Status | Error Code | 场景 |
|------------|-----------|------|
| 401 | UNAUTHORIZED | Authorization header 缺失或格式错误 |
| 401 | UNAUTHORIZED | JWT Token 过期 |
| 401 | UNAUTHORIZED | 数据库会话过期 |
| 403 | FORBIDDEN | JWT 签名无效 |
| 403 | FORBIDDEN | Token 类型不匹配 |
| 403 | FORBIDDEN | Session 不存在 |
| 403 | FORBIDDEN | Token 已被吊销 |
| 404 | NOT_FOUND | Marzban 用户不存在 |
| 429 | RATE_LIMITED | 请求过于频繁 |

---

## 5. 后续建议

1. **集成测试**: 建议编写自动化测试用例，覆盖各种 Token 验证场景
2. **监控告警**: 对 401/403 错误率进行监控，检测异常访问
3. **缓存优化**: 考虑对 Marzban 用户查询结果进行短期缓存，减少 API 调用
4. **节点信息**: 如需返回 nodes 字段，需要扩展 MarzbanClient 解析节点配置
