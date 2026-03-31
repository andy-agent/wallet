# 逻辑漏洞与缺失分析

**版本**: 1.0  
**日期**: 2026-03-31  
**状态**: 深度分析完成

---

## 目录

1. [架构级问题](#1-架构级问题)
2. [服务端逻辑漏洞](#2-服务端逻辑漏洞)
3. [客户端逻辑漏洞](#3-客户端逻辑漏洞)
4. [数据流不一致](#4-数据流不一致)
5. [未实现功能](#5-未实现功能)
6. [安全风险](#6-安全风险)

---

## 1. 架构级问题

### 1.1 用户系统与订单系统断裂

```
当前架构：
┌─────────────┐      ┌─────────────┐      ┌─────────────┐
│  客户端      │─────►│  服务端      │─────►│  Marzban    │
│  设备ID      │      │  订单系统     │      │  用户系统    │
└─────────────┘      └─────────────┘      └─────────────┘
        │                    │                    │
        ▼                    ▼                    ▼
   无需登录              需要登录！             自动创建
```

**问题**: 订单创建需要登录，但客户端没有实现登录功能

| 组件 | 设计 | 实现状态 |
|------|------|----------|
| 用户注册 | 有 | ✅ 服务端完成 |
| 用户登录 | 有 | ✅ 服务端完成 |
| 客户端登录UI | 无 | ❌ LoginActivity是占位符 |
| 匿名购买 | 无 | ❌ 未设计 |

**影响**: 用户无法创建订单，整个购买流程阻断

---

### 1.2 状态机与业务逻辑不匹配

```python
# state_machine.py 定义的状态
OrderStatus.PENDING_PAYMENT → SEEN_ONCHAIN → CONFIRMING → PAID_SUCCESS → FULFILLED

# 但 workers/fulfillment.py 中:
if order.status != OrderStatus.PAID_SUCCESS.value:
    raise FulfillmentError("Order status must be 'paid_success'")

# 而 workers/scanner.py 中:
if min_acceptable <= actual_amount <= max_acceptable:
    order.status = OrderStatus.PAID_SUCCESS.value
elif actual_amount < min_acceptable:
    order.status = OrderStatus.UNDERPAID.value  # 不再继续处理
```

**问题**: 
- `UNDERPAID`/`OVERPAID` 状态没有后续处理逻辑
- 用户少付后没有补缴机制
- 多付后没有退款逻辑

---

### 1.3 两个 fulfillment 系统冲突

```
app/services/fulfillment.py      app/workers/fulfillment.py
        │                                │
        ├── 724行，完整逻辑              ├── 346行，简化逻辑
        ├── 支持新购和续费               ├── 也支持新购和续费
        ├── 生成ClientToken              ├── 不生成ClientToken
        └── 被谁调用？                    └── 被worker调用
```

**问题**: 
- 两个文件功能重叠但实现不同
- `services/fulfillment.py` 生成 ClientToken，但 `workers/fulfillment.py` 不生成
- Worker 调用的是 `workers/fulfillment.py`，ClientToken 不会被创建

---

## 2. 服务端逻辑漏洞

### 2.1 订单创建强制依赖登录

**位置**: `app/api/client/orders.py:127`

```python
async def create_order(
    request: CreateOrderRequest,
    current_user: User = Depends(get_current_user),  # ← 强制登录
    ...
):
    order = Order(
        ...
        user_id=current_user.id,  # ← 使用登录用户ID
        ...
    )
```

**问题**: 匿名用户无法创建订单

**修复方案**:
1. 方案A: 支持匿名购买（推荐）
2. 方案B: 强制登录（需要客户端实现登录UI）

---

### 2.2 CreateOrderRequest 字段缺失

**位置**: `app/api/client/orders.py:49-71`

```python
class CreateOrderRequest(BaseModel):
    plan_id: str
    purchase_type: str
    asset_code: str
    # 缺失字段:
    # - client_device_id
    # - client_version  
    # - client_token
    # 续费字段类型错误:
    client_user_id: str = Field(default=None)  # 应该是 Optional[str]
    marzban_username: str = Field(default=None)  # 应该是 Optional[str]
```

**问题**:
1. 服务端不接受的字段客户端在发送
2. 字段类型声明错误，null 值会验证失败

---

### 2.3 地址释放逻辑不完整

**位置**: `app/workers/scanner.py:609-672`

```python
async def release_expired_addresses():
    # 查询 expired/underpaid/overpaid/failed 状态的订单
    # 释放地址...
    
    # 问题1: 没有检查地址是否还有资金
    # 问题2: 如果订单是 LATE_PAID 状态，地址不应该被释放
    # 问题3: 没有记录地址释放原因
```

**问题**: 
- 过期订单的地址可能被释放，但资金可能已经到账（late_paid）
- 地址和资金的追踪不完整

---

### 2.4 汇率服务单点故障

**位置**: `app/api/client/orders.py:159-183`

```python
fx_rate, rate_error = await _get_fx_rate_safe(asset_code)
if fx_rate is None:
    raise AppException(
        code=ErrorCode.SERVICE_UNAVAILABLE,
        message=f"汇率服务暂不可用: {rate_error}",
        ...
    )
```

**问题**: 
- 汇率获取失败时订单无法创建
- 没有使用缓存汇率作为fallback
- 没有降级到固定汇率的机制

---

### 2.5 支付确认数配置与链特性不匹配

**位置**: `app/core/config.py` (推测)

```python
# scanner.py 中读取
required_confirmations = _get_required_confirmations(chain)

# solana: 32 确认
# tron: 19 确认
```

**问题**:
- Solana 确认数 32 可能过多（通常 1-32 之间）
- Tron 确认数 19 是合理的
- 没有考虑交易最终性的时间差异

---

### 2.6 ClientSession 关联问题

**位置**: `app/services/fulfillment.py:326-335`

```python
client_session = ClientSession(
    id=str(ulid.new().str),
    order_id=order_id,
    user_id=order.user_id,  # ← 匿名用户时 user_id 是什么？
    ...
)
```

**问题**: 
- 如果是匿名购买，user_id 是登录用户ID还是空？
- ClientSession 模型要求 user_id 非空
- 但匿名用户没有 user_id

---

### 2.7 缺少订单取消API

**问题**: 
- 用户创建订单后无法主动取消
- 只能等待15分钟过期
- 浪费地址池资源

---

### 2.8 续费逻辑不完整

**位置**: `app/api/client/orders.py`

```python
# 创建订单时接收 client_user_id 和 marzban_username
# 但没有验证这些字段是否属于当前登录用户

# 问题: 用户可以伪造其他用户的 client_user_id 续费别人的账号
```

**安全风险**: 水平越权 - 用户A可以为用户B续费

---

## 3. 客户端逻辑漏洞

### 3.1 LoginActivity 完全未实现

**位置**: `payment/ui/activity/LoginActivity.kt`

```kotlin
class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // TODO: 实现登录逻辑
    }
}
```

**位置**: `res/layout/activity_login.xml`

```xml
<TextView
    android:text="TODO: Implement login UI" />
```

**影响**: 用户无法登录，无法创建订单

---

### 3.2 PaymentActivity 未处理无登录情况

**位置**: `plans/PaymentActivity.kt:102-119`

```kotlin
private fun createOrder(planId: String, assetCode: String) {
    lifecycleScope.launch {
        val result = repository.createOrder(planId, assetCode)
        result.onSuccess { order ->
            // ...
        }.onFailure { error ->
            Toast.makeText(this@PaymentActivity, "创建订单失败: ${error.message}", Toast.LENGTH_LONG).show()
            finish()
        }
    }
}
```

**问题**: 
- 没有检查是否登录
- 服务端返回 401 时只显示 Toast，没有引导用户登录
- 没有登录重试机制

---

### 3.3 硬编码价格显示

**位置**: `plans/PlansAdapter.kt:53`

```kotlin
textPrice.text = "3 USDT 等值"  // ← 硬编码！
```

**位置**: `plans/PaymentActivity.kt:154-158`

```kotlin
private fun formatAmountDisplay(assetCode: String, amountCrypto: String): String {
    return when (assetCode) {
        "SOL" -> "$amountCrypto SOL (等值3 USDT)"  // ← 硬编码！
        ...
    }
}
```

**问题**: 
- 价格应该从服务端获取
- 套餐价格是 USD，不是固定的 3 USDT

---

### 3.4 订单轮询状态处理不完整

**位置**: `payment/ui/OrderPollingUseCase.kt:102-125`

```kotlin
when (order.status) {
    PaymentConfig.OrderStatus.FULFILLED -> { ... }
    PaymentConfig.OrderStatus.EXPIRED, FAILED, LATE_PAID -> { ... }
    PaymentConfig.OrderStatus.UNDERPAID -> { ... }
    PaymentConfig.OrderStatus.OVERPAID -> { 
        // 多付但仍成功，继续轮询直到 fulfilled
        scheduleNextPoll(orderId)
    }
    else -> { scheduleNextPoll(orderId) }
}
```

**问题**:
- `SEEN_ONCHAIN` 和 `CONFIRMING` 状态没有特殊处理
- 用户不知道交易已被发现
- 没有显示确认进度

---

### 3.5 缺少续费入口

**问题**:
- UserProfileActivity 只显示历史订单
- 没有「续费」按钮
- 续费需要重新选择套餐，但套餐选择页面没有标记「当前套餐」

---

### 3.6 二维码生成硬编码

**位置**: `plans/PaymentActivity.kt:136`

```kotlin
val qrBitmap = generateQRCode(order.payment.qrText)
```

**问题**:
- 依赖服务端返回的 qrText
- 如果服务端没有返回，二维码为空
- 应该根据地址和金额本地生成

---

### 3.7 时间解析可能失败

**位置**: `plans/PaymentActivity.kt:215-236`

```kotlin
private fun parseIsoDate(dateStr: String): Long {
    return try {
        val isoFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
        ...
    } catch (e: Exception) {
        try {
            val isoFormatNoMs = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US)
            ...
        } catch (e2: Exception) {
            System.currentTimeMillis() + 15 * 60 * 1000  // 硬编码fallback
        }
    }
}
```

**问题**:
- 只处理了两种时间格式
- 如果服务端返回其他格式，倒计时失效

---

## 4. 数据流不一致

### 4.1 订单响应字段映射

**服务端返回** (`orders.py:235-251`):
```python
data=OrderResponseData(
    order_id=order_id,
    order_no=order_no,
    plan_id=request.plan_id,  # 只返回plan_id
    ...
    receive_address=address.address,
    amount_crypto=str(amount_crypto),
    ...
)
```

**客户端期望** (`data/model/Order.kt:8-43`):
```kotlin
data class Order(
    val orderId: String,
    val orderNo: String,
    val status: String,
    val statusText: String,  # ← 服务端没有返回！
    val plan: PlanInfo,      # ← 服务端只返回plan_id
    val payment: PaymentInfo,
    ...
)
```

**问题**:
- 服务端返回扁平结构
- 客户端期望嵌套结构
- 字段名不匹配（snake_case vs camelCase）通过 @SerializedName 解决，但结构不匹配

---

### 4.2 用户ID不一致

```
服务端:
- users.id (ULID) - 注册用户ID
- orders.user_id - 关联注册用户
- orders.client_user_id - 续费时的标识

客户端:
- UserEntity.userId - 从 username 设置
- PaymentRepository.getCurrentUserId() - 从缓存获取
```

**问题**:
- 客户端 userId 是 username
- 服务端 user_id 是 ULID
- 两者不一致，查询时会失败

---

### 4.3 状态定义不一致

**服务端** (`state_machine.py`):
```python
PENDING_PAYMENT, SEEN_ONCHAIN, CONFIRMING, PAID_SUCCESS, 
FULFILLED, EXPIRED, UNDERPAID, OVERPAID, FAILED, LATE_PAID
```

**客户端** (`PaymentConfig.kt`):
```kotlin
const val PENDING_PAYMENT = "pending_payment"
const val PAID_SUCCESS = "paid_success"  # 少了 CONFIRMING
const val FULFILLED = "fulfilled"
// 其他状态可能没有定义
```

**问题**:
- 客户端没有 `SEEN_ONCHAIN` 和 `CONFIRMING` 状态定义
- 轮询时这些状态被归类为 `else`

---

## 5. 未实现功能

### 5.1 服务端未实现

| 功能 | 优先级 | 说明 |
|------|--------|------|
| 匿名购买 | P0 | 核心功能缺失 |
| 订单取消API | P1 | 资源优化 |
| 补缴流程 | P2 | 少付订单处理 |
| 退款流程 | P2 | 多付订单处理 |
| 地址初始化工具 | P1 | 需要预生成地址 |
| 资金归集 | P2 | 收款地址资金归集到主钱包 |
| 支付通知 | P2 | WebSocket 或推送 |
| 订单超时前提醒 | P3 | 5分钟前提醒 |

### 5.2 客户端未实现

| 功能 | 优先级 | 说明 |
|------|--------|------|
| 登录UI | P0 | 阻断核心流程 |
| 注册UI | P0 | 阻断核心流程 |
| 自动登录 | P1 | Token持久化 |
| 购买入口 | P0 | MainActivity无入口 |
| 续费入口 | P1 | 用户中心无续费按钮 |
| 支付状态详情 | P2 | 显示确认进度 |
| 订单分享 | P3 | 分享订单信息 |

---

## 6. 安全风险

### 6.1 水平越权风险

**问题**: 用户A可以为用户B续费

**位置**: `orders.py`

```python
# 创建订单时接收 client_user_id，但没有验证是否属于当前用户
client_user_id: str = Field(default=None)
```

---

### 6.2 Token 验证不一致

**问题**: 
- `auth.py` 使用 JWT 验证
- `fulfillment.py` 也使用 JWT 验证，但密钥和逻辑可能不同
- 没有统一的 Token 验证中间件

---

### 6.3 地址池耗尽风险

**问题**:
- 如果大量订单创建但不支付，地址被占用
- 15分钟后才释放
- 没有地址池预警机制

---

### 6.4 SSL 证书验证被禁用（开发环境）

**位置**: `PaymentRepository.kt:34-48`

```kotlin
val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
    override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {}
    override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {}
    override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
})
hostnameVerifier { _, _ -> true }
```

**风险**: 中间人攻击

---

## 7. 修复建议优先级

### P0 - 阻断性问题（无法使用）

1. **服务端**: 支持匿名购买 OR 强制登录但提供登录API
2. **客户端**: 实现 LoginActivity 和注册功能
3. **服务端**: 修复 CreateOrderRequest 字段验证
4. **客户端**: 在 MainActivity 添加购买入口

### P1 - 严重问题（功能缺陷）

5. **服务端**: 统一 fulfillment 逻辑，删除重复代码
6. **服务端**: 添加订单取消API
7. **服务端**: 初始化地址池
8. **客户端**: 修复价格显示（从服务端获取）
9. **服务端**: 添加汇率缓存fallback

### P2 - 中等问题（体验优化）

10. **客户端**: 显示支付确认进度
11. **服务端**: 处理 UNDERPAID/OVERPAID 状态
12. **客户端**: 添加续费入口
13. **服务端**: 修复地址释放逻辑

### P3 - 低优先级（完善功能）

14. **服务端**: 添加支付通知（WebSocket）
15. **客户端**: 订单分享功能
16. **服务端**: 订单超时前提醒

---

*分析完成*
