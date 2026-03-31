# 任务完成报告 - 客户端订单API

**任务ID**: liaojiang-khy  
**任务名称**: 客户端订单API（创建+查询）  
**完成时间**: 2026-03-31  

---

## 1. 修改的文件列表

| 文件路径 | 操作 | 说明 |
|---------|------|------|
| `app/api/client/orders.py` | 新建 | 订单API路由实现（创建订单 + 查询订单） |
| `app/api/client/__init__.py` | 修改 | 导出 orders_router |
| `app/main.py` | 修改 | 注册 client orders router |

---

## 2. API 端点说明

### POST /client/v1/orders - 创建订单

**请求参数:**
```json
{
  "plan_id": "01HQ1234567890ABCDEF123456",
  "purchase_type": "new",
  "asset_code": "USDT_TRC20",
  "client_device_id": "device_abc123",
  "client_version": "1.2.3"
}
```

**响应示例:**
```json
{
  "code": "SUCCESS",
  "message": "订单创建成功",
  "data": {
    "order_id": "01HQ...",
    "order_no": "ABCDEF123456",
    "purchase_type": "new",
    "plan_id": "01HQ1234567890ABCDEF123456",
    "plan_name": "月度套餐",
    "amount_usd": "9.99",
    "fx_rate_locked": "1.00000000",
    "created_at": "2026-03-31T13:14:49Z",
    "payment": {
      "receive_address": "TXYZ...",
      "amount_crypto": "9.990000",
      "asset_code": "USDT_TRC20",
      "chain": "tron",
      "qr_code_text": "tron:TXYZ...?amount=9.990000&token=USDT",
      "expires_at": "2026-03-31T13:29:49Z"
    },
    "status_info": {
      "status": "pending_payment",
      "status_label": "待支付",
      "is_payable": true,
      "is_terminal": false,
      "can_transition_to": ["seen_onchain", "expired"]
    },
    "subscription": null
  }
}
```

**错误码:**
- `INVALID_PLAN_ID`: 套餐不存在或已下架
- `UNSUPPORTED_ASSET`: 套餐不支持该支付方式
- `INVALID_PURCHASE_TYPE`: 购买类型无效（必须是new/renew）
- `ADDRESS_POOL_EMPTY`: 地址池不足（HTTP 409）
- `SERVICE_UNAVAILABLE`: 汇率服务暂时不可用

### GET /client/v1/orders/{order_id} - 查询订单

**响应示例（待支付）:**
```json
{
  "code": "SUCCESS",
  "message": "success",
  "data": {
    "order_id": "01HQ...",
    "order_no": "ABCDEF123456",
    "status_info": {
      "status": "pending_payment",
      "status_label": "待支付",
      "is_payable": true,
      "is_terminal": false,
      "can_transition_to": ["seen_onchain", "expired"]
    },
    "payment": { ... },
    "subscription": null
  }
}
```

**响应示例（已完成）:**
```json
{
  "code": "SUCCESS",
  "message": "success",
  "data": {
    "order_id": "01HQ...",
    "order_no": "ABCDEF123456",
    "status_info": {
      "status": "fulfilled",
      "status_label": "已完成",
      "is_payable": false,
      "is_terminal": true,
      "can_transition_to": []
    },
    "payment": { ... },
    "subscription": {
      "client_token": "abcd...wxyz",
      "subscription_url": "https://...",
      "traffic_bytes": 1099511627776,
      "duration_days": 30,
      "expires_at": "2026-04-30T13:14:49Z"
    }
  }
}
```

**错误码:**
- `ORDER_NOT_FOUND`: 订单不存在（HTTP 404）

---

## 3. curl测试命令

### 创建订单
```bash
# 创建 USDT_TRC20 订单
curl -X POST http://localhost:8000/client/v1/orders \
  -H "Content-Type: application/json" \
  -d '{
    "plan_id": "01HQ1234567890ABCDEF123456",
    "purchase_type": "new",
    "asset_code": "USDT_TRC20",
    "client_device_id": "test_device_001",
    "client_version": "1.0.0"
  }'

# 创建 SOL 订单
curl -X POST http://localhost:8000/client/v1/orders \
  -H "Content-Type: application/json" \
  -d '{
    "plan_id": "01HQ1234567890ABCDEF123456",
    "purchase_type": "new",
    "asset_code": "SOL",
    "client_device_id": "test_device_001",
    "client_version": "1.0.0"
  }'
```

### 查询订单
```bash
# 查询订单详情
curl http://localhost:8000/client/v1/orders/{order_id}

# 示例
curl http://localhost:8000/client/v1/orders/01HQABCDEF1234567890ABCDEF
```

---

## 4. 与状态机的集成说明

### 4.1 订单创建时的状态

创建订单时，订单状态自动设置为 `pending_payment`（待支付）：

```python
order.status = OrderStatus.PENDING_PAYMENT.value
```

### 4.2 状态机信息返回

查询订单时，返回完整的状态机信息：

| 字段 | 说明 |
|-----|------|
| `status` | 当前状态值 |
| `status_label` | 状态中文标签（通过 `state_machine.get_status_label()` 获取） |
| `is_payable` | 是否可支付（通过 `state_machine.is_payable()` 判断） |
| `is_terminal` | 是否为终态（通过 `state_machine.is_terminal()` 判断） |
| `can_transition_to` | 允许转换的下一状态列表 |

### 4.3 状态转换规则

根据 `app/core/state_machine.py` 的定义：

```
pending_payment → seen_onchain (检测到交易)
pending_payment → expired (超时15分钟)
seen_onchain → confirming (确认中)
seen_onchain → underpaid/overpaid/failed (金额/币种错误)
confirming → paid_success (达到确认数)
paid_success → fulfilled (开通账号成功)
any → late_paid (过期后检测到支付)
```

### 4.4 订单过期处理

- 订单创建时设置 `expires_at` = 当前时间 + 15分钟
- Worker 应定期检查过期订单并调用状态机转换到 `expired` 状态
- 过期后地址应被回收（通过 `AddressPoolService.release_address()`）

### 4.5 终态订单处理

当订单状态为 `fulfilled` 时：
- `is_terminal` = true
- `is_payable` = false
- 返回 `subscription` 信息（client_token已脱敏）

---

## 5. 待办事项（后续任务）

1. **替换Mock汇率服务**: 等待 `liaojiang-jfp` 完成后，替换 `MockExchangeRateService` 为实际的汇率服务调用
2. **集成续费逻辑**: 目前 `purchase_type=renew` 时未处理客户端用户关联
3. **完善错误处理**: 增加更多边界情况的错误码
4. **添加限流**: 考虑对创建订单接口添加频率限制
5. **Webhook通知**: 订单状态变化时通知客户端

---

## 6. 代码验收标准检查

| 验收项 | 状态 | 说明 |
|-------|------|------|
| 客户端可成功创建订单并获得支付信息 | ✅ | POST /client/v1/orders 完整实现 |
| 订单15分钟内价格锁定 | ✅ | `expires_at` = now + 15分钟 |
| 地址池不足时返回明确错误码 ADDRESS_POOL_EMPTY | ✅ | HTTP 409，code=ADDRESS_POOL_EMPTY |
| 查询订单返回完整状态机信息 | ✅ | 包含 status, status_label, is_payable, is_terminal, can_transition_to |
| 创建订单时写入 fx_rate_locked、amount_crypto、receive_address | ✅ | 订单创建时完整写入 |
| 若已 fulfilled，返回 client_token 和订阅信息 | ✅ | 从 ClientSession 表查询并脱敏显示 |

---

## 7. 相关文件参考

- `app/core/state_machine.py` - 订单状态机定义
- `app/services/address_pool.py` - 地址池服务
- `app/models/order.py` - 订单模型
- `app/models/plan.py` - 套餐模型
- `app/models/client_session.py` - 客户端会话模型（用于fulfilled后返回订阅信息）
