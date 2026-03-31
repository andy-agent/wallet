# 任务完成报告：管理端套餐CRUD接口

**任务ID**: liaojiang-fcs  
**任务名称**: 管理端套餐CRUD接口  
**完成时间**: 2026-03-31  

---

## 1. 修改的文件列表

| 序号 | 文件路径 | 操作 | 说明 |
|------|----------|------|------|
| 1 | `app/api/admin/plans.py` | 创建 | 管理端套餐CRUD接口实现 |
| 2 | `app/api/admin/__init__.py` | 修改 | 注册 plans_router |
| 3 | `app/main.py` | 修改 | 导入并注册 admin_router |

---

## 2. 接口清单

### 2.1 接口概览

| 方法 | 路径 | 功能 | 认证 |
|------|------|------|------|
| POST | `/admin/v1/plans` | 创建套餐 | admin_token |
| GET | `/admin/v1/plans` | 查询套餐列表（分页） | admin_token |
| GET | `/admin/v1/plans/{id}` | 获取套餐详情 | admin_token |
| PUT | `/admin/v1/plans/{id}` | 更新套餐 | admin_token |
| PATCH | `/admin/v1/plans/{id}/enable` | 启用/禁用套餐 | admin_token |
| DELETE | `/admin/v1/plans/{id}` | 删除套餐 | admin_token |

### 2.2 请求/响应模型

**Plan 模型字段**:
- `id`: str - 套餐ID（自动生成ULID）
- `code`: str - 套餐代码（唯一）
- `name`: str - 套餐名称
- `description`: str - 套餐描述
- `traffic_bytes`: int - 流量字节数（必须>0）
- `duration_days`: int - 有效期天数（必须>0）
- `price_usd`: Numeric - 价格USD（必须>0）
- `supported_assets`: List[str] - 支持的支付方式
- `enabled`: bool - 是否启用
- `sort_order`: int - 排序顺序

---

## 3. curl 测试命令

### 前置条件：生成 Admin Token

需要先生成有效的 admin JWT token：

```python
import jwt
import datetime

# 使用与项目相同的 JWT_SECRET（从环境变量获取）
jwt_secret = "your-jwt-secret-here"  # 替换为实际的 JWT_SECRET

payload = {
    "sub": "admin_001",
    "type": "admin_access",
    "role": "admin",
    "permissions": ["*"],
    "exp": datetime.datetime.utcnow() + datetime.timedelta(hours=1)
}

token = jwt.encode(payload, jwt_secret, algorithm="HS256")
print(f"Bearer {token}")
```

将生成的 token 替换到以下命令中的 `YOUR_ADMIN_TOKEN_HERE`。

---

### 3.1 创建套餐

```bash
curl -X POST http://localhost:8000/admin/v1/plans \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_ADMIN_TOKEN_HERE" \
  -d '{
    "code": "BASIC_MONTHLY",
    "name": "基础月套餐",
    "description": "100GB流量，30天有效期",
    "traffic_bytes": 107374182400,
    "duration_days": 30,
    "price_usd": "9.99",
    "supported_assets": ["SOL", "USDT_TRC20"],
    "enabled": true,
    "sort_order": 0
  }'
```

**预期响应**:
```json
{
  "code": "SUCCESS",
  "message": "success",
  "data": {
    "id": "01JRQ...",
    "code": "BASIC_MONTHLY",
    "name": "基础月套餐",
    "description": "100GB流量，30天有效期",
    "traffic_bytes": 107374182400,
    "duration_days": 30,
    "price_usd": "9.99",
    "supported_assets": ["SOL", "USDT_TRC20"],
    "enabled": true,
    "sort_order": 0,
    "created_at": "2026-03-31T..."
  }
}
```

---

### 3.2 查询套餐列表

```bash
# 基础查询（分页）
curl -X GET "http://localhost:8000/admin/v1/plans?page=1&size=10" \
  -H "Authorization: Bearer YOUR_ADMIN_TOKEN_HERE"

# 按启用状态筛选
curl -X GET "http://localhost:8000/admin/v1/plans?enabled=true&page=1&size=10" \
  -H "Authorization: Bearer YOUR_ADMIN_TOKEN_HERE"
```

**预期响应**:
```json
{
  "code": "SUCCESS",
  "message": "success",
  "data": [
    {
      "id": "01JRQ...",
      "code": "BASIC_MONTHLY",
      "name": "基础月套餐",
      ...
    }
  ],
  "pagination": {
    "total": 1,
    "page": 1,
    "size": 10,
    "pages": 1
  }
}
```

---

### 3.3 获取套餐详情

```bash
curl -X GET http://localhost:8000/admin/v1/plans/01JRQ... \
  -H "Authorization: Bearer YOUR_ADMIN_TOKEN_HERE"
```

**预期响应**:
```json
{
  "code": "SUCCESS",
  "message": "success",
  "data": {
    "id": "01JRQ...",
    "code": "BASIC_MONTHLY",
    "name": "基础月套餐",
    "description": "100GB流量，30天有效期",
    "traffic_bytes": 107374182400,
    "duration_days": 30,
    "price_usd": "9.99",
    "supported_assets": ["SOL", "USDT_TRC20"],
    "enabled": true,
    "sort_order": 0,
    "created_at": "2026-03-31T...",
    "order_count": 0
  }
}
```

---

### 3.4 更新套餐

```bash
curl -X PUT http://localhost:8000/admin/v1/plans/01JRQ... \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_ADMIN_TOKEN_HERE" \
  -d '{
    "name": "基础月套餐（更新）",
    "price_usd": "12.99",
    "traffic_bytes": 214748364800
  }'
```

**预期响应**: 返回更新后的套餐信息

---

### 3.5 启用/禁用套餐

```bash
# 禁用套餐
curl -X PATCH http://localhost:8000/admin/v1/plans/01JRQ.../enable \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_ADMIN_TOKEN_HERE" \
  -d '{"enabled": false}'

# 启用套餐
curl -X PATCH http://localhost:8000/admin/v1/plans/01JRQ.../enable \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_ADMIN_TOKEN_HERE" \
  -d '{"enabled": true}'
```

**预期响应**: 返回更新后的套餐信息

---

### 3.6 删除套餐

```bash
curl -X DELETE http://localhost:8000/admin/v1/plans/01JRQ... \
  -H "Authorization: Bearer YOUR_ADMIN_TOKEN_HERE"
```

**预期响应**（成功）:
```json
{
  "code": "SUCCESS",
  "message": "success",
  "data": {
    "deleted": true,
    "plan_id": "01JRQ..."
  }
}
```

**预期响应**（有关联订单时，HTTP 409）:
```json
{
  "code": "DUPLICATE_ORDER",
  "message": "无法删除：该套餐存在 5 个关联订单",
  "data": {}
}
```

---

## 4. 错误处理测试

### 4.1 未授权访问

```bash
# 不带 token
curl -X GET http://localhost:8000/admin/v1/plans

# 或带无效 token
curl -X GET http://localhost:8000/admin/v1/plans \
  -H "Authorization: Bearer invalid_token"
```

**预期响应**（HTTP 401）:
```json
{
  "code": "UNAUTHORIZED",
  "message": "Missing or invalid Authorization header. Expected: Bearer <token>",
  "data": {}
}
```

### 4.2 数据验证错误

```bash
# price_usd <= 0
curl -X POST http://localhost:8000/admin/v1/plans \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_ADMIN_TOKEN_HERE" \
  -d '{
    "code": "TEST",
    "name": "测试套餐",
    "traffic_bytes": 1000000,
    "duration_days": 30,
    "price_usd": "0"
  }'
```

**预期响应**（HTTP 422）:
```json
{
  "detail": [
    {
      "loc": ["body", "price_usd"],
      "msg": "price_usd must be greater than 0",
      "type": "value_error"
    }
  ]
}
```

### 4.3 重复 code

```bash
# 创建已存在的 code
curl -X POST http://localhost:8000/admin/v1/plans \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_ADMIN_TOKEN_HERE" \
  -d '{
    "code": "BASIC_MONTHLY",
    "name": "另一个套餐",
    "traffic_bytes": 1000000,
    "duration_days": 30,
    "price_usd": "5.00"
  }'
```

**预期响应**（HTTP 409）:
```json
{
  "code": "DUPLICATE_ORDER",
  "message": "套餐代码 'BASIC_MONTHLY' 已存在",
  "data": {}
}
```

---

## 5. 遇到的问题和解决方案

### 问题 1: main.py 文件状态与初始预期不同

**问题描述**: 初始检查时 main.py 中的 router 注册部分都是注释状态，但实际文件已经包含了一些 client router 的注册代码。

**解决方案**: 重新读取文件内容，根据实际情况进行增量修改：
- 保留已有的 client router 导入和注册
- 添加 admin router 导入
- 取消注释 admin router 注册

### 问题 2: 需要验证 price_usd > 0 和 duration_days > 0

**问题描述**: 任务要求创建/更新时验证 price_usd > 0 和 duration_days > 0。

**解决方案**: 使用 Pydantic validator 进行验证：

```python
@validator('price_usd')
def validate_price_usd(cls, v):
    if v <= 0:
        raise ValueError('price_usd must be greater than 0')
    return v

@validator('duration_days')
def validate_duration_days(cls, v):
    if v is not None and v <= 0:
        raise ValueError('duration_days must be greater than 0')
    return v
```

### 问题 3: 删除套餐时需要检查关联订单

**问题描述**: 任务要求删除套餐前检查是否有关联订单。

**解决方案**: 在删除前查询订单表：

```python
order_count_result = await db.execute(
    select(func.count(Order.id)).where(Order.plan_id == plan_id)
)
order_count = order_count_result.scalar() or 0

if order_count > 0:
    raise ConflictException(
        message=f"无法删除：该套餐存在 {order_count} 个关联订单"
    )
```

---

## 6. 实现要点总结

1. **认证**: 所有接口使用 `verify_admin_token` 依赖进行 JWT 认证
2. **限流**: 使用 `admin_rate_limit` 依赖防止暴力请求
3. **响应格式**: 统一使用 `Response[T]` 或 `PaginatedResponse[T]` 格式
4. **ID 生成**: 使用 ULID 生成唯一 ID
5. **数据验证**: 
   - Pydantic 模型自动验证字段类型和约束
   - 自定义 validator 验证业务规则（price_usd>0, duration_days>0）
6. **业务逻辑**:
   - code 字段全局唯一
   - 删除前检查关联订单
   - 列表接口支持按 enabled 状态筛选
