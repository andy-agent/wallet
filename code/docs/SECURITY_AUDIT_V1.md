# 🔐 代码安全审计报告 v1.0

**审计日期**: 2026-03-29  
**审计范围**: v2rayng Payment System v1.0  
**审计人员**: AI Code Reviewer  
**仓库**: https://github.com/andy-agent/wallet

---

## 📊 执行摘要

| 严重程度 | 数量 | 说明 |
|----------|------|------|
| 🔴 **CRITICAL** | 7 | 必须立即修复，存在安全风险或严重功能缺陷 |
| 🟠 **HIGH** | 5 | 高优先级修复，影响安全或稳定性 |
| 🟡 **MEDIUM** | 15 | 中优先级，建议修复 |
| 🟢 **LOW** | 11 | 低优先级，代码质量改进 |

**总体评级**: ⚠️ **需要修复后才能上线**

---

## 🔴 CRITICAL Issues (7)

### 1. HTTP 客户端资源泄漏 (scanner.py)
**文件**: `code/server/app/workers/scanner.py:134-153`  
**问题**: 当支付检测发生异常时，`client.close()` 被跳过，导致 HTTP 连接泄漏。

```python
# 问题代码
except Exception as e:
    logger.error(f"Error detecting payment for order {order.id}: {e}")
    continue  # ← 跳过了 client.close()
await client.close()
```

**影响**: 连接池耗尽，服务性能下降直至崩溃。

**修复**:
```python
client = _get_chain_client(chain)
try:
    for order in chain_orders:
        await _detect_payment_for_order(session, order, client)
finally:
    await client.close()
```

---

### 2. HTTP 客户端资源泄漏 (fulfillment.py)
**文件**: `code/server/app/workers/fulfillment.py:225-252`  
**问题**: Marzban 客户端在异常情况下未关闭。

**修复**: 使用 try/finally 确保关闭。

---

### 3. 金额精度问题 - 使用 float
**文件**: `code/server/app/workers/scanner.py:181, 332, 369-383`  
**问题**: 金融金额使用 Python `float` 计算，存在精度误差。

```python
# 问题代码
expected_amount = float(order.amount_crypto)  # Decimal → float
```

**影响**: 可能导致合法支付被拒绝，或金额计算错误。

**修复**:
```python
from decimal import Decimal
expected_amount = Decimal(str(order.amount_crypto))
tolerance = expected_amount * Decimal("0.001")
```

---

### 4. Android 内存泄漏 - Activity 引用
**文件**: `code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/payment/ui/OrderPollingUseCase.kt:15`  
**问题**: `PollingCallback` 持有 Activity 强引用，轮询可能阻止垃圾回收。

**影响**: 内存泄漏，Activity 无法被销毁。

**修复**:
```kotlin
class OrderPollingUseCase(
    private val repository: PaymentRepository,
    private val callback: WeakReference<PollingCallback>  // 使用弱引用
)
```

---

### 5. 缺少 SSL 证书固定
**文件**: `code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/payment/data/repository/PaymentRepository.kt:20-26`  
**问题**: Retrofit 未配置 SSL 证书固定，易受中间人攻击。

**修复**:
```kotlin
val client = OkHttpClient.Builder()
    .certificatePinner(
        CertificatePinner.Builder()
            .add("api-domain.com", "sha256/...")
            .build()
    )
    .build()
```

---

### 6. 静态 Admin Token 认证
**文件**: `code/server/app/api/admin/orders.py:28-32`, `orders_actions.py:25-29`  
**问题**: 使用单一静态 token，无用户身份追踪，无权限分级。

**影响**: 
- 所有管理员共享同一 token
- 无法审计谁执行了操作
- Token 泄漏风险高

**修复**: 实现基于 JWT 的多用户认证 + RBAC 权限控制。

---

### 7. 静默错误处理
**文件**: `code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/plans/PaymentActivity.kt:217-219`  
**问题**: 错误回调为空实现，用户无法感知错误。

```kotlin
override fun onError(error: String) {
    // 静默处理，继续轮询
}
```

**修复**: 显示错误提示，记录日志。

---

## 🟠 HIGH Priority Issues (5)

### 8. 缺少授权检查
**文件**: `code/server/app/api/admin/orders_actions.py`  
**问题**: 敏感操作（手动确认、退款）只验证 token，不检查权限。

**修复**: 添加角色/权限验证装饰器。

---

### 9. 金额输入未验证
**文件**: `code/server/app/api/admin/orders_actions.py:78-79`  
**问题**: `amount_crypto` 作为字符串直接存储，无数值验证。

**修复**: 使用 Pydantic Decimal 验证。

---

### 10. Tron 确认数逻辑错误
**文件**: `code/server/app/integrations/tron.py:202-203`  
**问题**: 确认数基于 `contractRet == "SUCCESS"`，非实际区块确认。

**影响**: 可能过早确认交易，存在双花风险。

---

### 11. 竞态条件 - 状态检查与履行
**文件**: `code/server/app/workers/fulfillment.py:144-151`  
**问题**: 状态检查和履行非原子操作，状态可能在期间改变。

---

### 12. CancellationException 被吞没
**文件**: `code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/payment/ui/OrderPollingUseCase.kt:127-129`  
**问题**: 捕获所有 Exception 包括 CancellationException，影响协程取消。

**修复**:
```kotlin
catch (e: CancellationException) {
    throw e  // 重新抛出
} catch (e: Exception) {
    // 处理其他错误
}
```

---

## 🟡 MEDIUM Priority Issues (15)

### 服务端
13. **缺少限流** - Admin 接口无 rate limiting (`orders.py`, `orders_actions.py`)
14. **datetime.utcnow() 已弃用** - 应使用 timezone-aware datetime (`address_pool.py:92,130`)
15. **不安全的字典访问** - `tx.get("ret", [{}])[0]` 可能崩溃 (`tron.py:202`)
16. **静默错误返回默认值** - RPC 失败返回 0.0，可能误判 (`tron.py:137`)
17. **竞态条件 - 确认追踪** - Tron 使用 detect_payment 而非查询特定交易 (`scanner.py:323-343`)
18. **JWT 过期时间过长** - 30 天 access token 风险高 (`config.py:40`)
19. **缺少输入长度验证** - plan_id, order_no 无长度限制
20. **信息泄露** - 详细错误信息可能暴露内部结构

### Android
21. **Handler 无 WeakReference** - 可能持有外部类引用
22. **MMKV 为空时 UUID 重复生成** - 设备追踪失效 (`PaymentRepository.kt:34-38`)
23. **ISO 日期解析 stub** - 始终返回固定值 (`PaymentActivity.kt:185-188`)
24. **SupervisorJob 缺失** - 子协程失败会取消整个 scope
25. **缺少 Security Headers** - API 请求无安全头部
26. **默认 Trust Manager** - 依赖系统默认 TLS 配置
27. **占位符 API URL** - `PaymentConfig.kt:12` 使用占位符

---

## 🟢 LOW Priority Issues (11)

28. Mock 模式返回随机值 - 测试不确定性 (`solana.py:146`)
29. 硬编码确认阈值 - 应配置化 (`solana.py:357`)
30. 缺少 context manager 使用 - 手动调用 close()
31. 未使用 import - `orders_actions.py:331`
32. Token 部分日志 - 前 8 字符可能被利用
33. 缺少分页限制 - 地址池查询可能返回大量数据
34. 可变状态未标记 @Volatile
35. 默认 RPC 端点 - 可能误用测试网
36. `extra="ignore"` 静默配置错误
37. 重复 safe call 链 - 代码可读性差
38. 注释拼写错误 - 多处 minor issues

---

## 📋 修复优先级

### 🔴 上线前必须修复 (P0)
1. HTTP 客户端资源泄漏 (scanner.py, fulfillment.py)
2. 金额精度问题 (float → Decimal)
3. Android 内存泄漏修复
4. SSL 证书固定
5. 实现多用户认证 (替代静态 token)

### 🟠 高优先级 (P1)
6. 添加 RBAC 权限检查
7. 金额输入验证
8. Tron 确认数逻辑修复
9. 竞态条件处理
10. CancellationException 正确处理

### 🟡 中优先级 (P2)
11. 添加限流
12. 使用 timezone-aware datetime
13. 修复不安全字典访问
14. 错误处理改进（不静默失败）
15. ISO 日期解析实现

### 🟢 低优先级 (P3)
16. Mock 模式确定值
17. 配置化确认阈值
18. 代码清理和优化

---

## 🛠️ 修复工作量估算

| 优先级 | 预计时间 | 工作量 |
|--------|----------|--------|
| P0 | 4-6 小时 | 2-3 个文件修改 |
| P1 | 6-8 小时 | 4-5 个文件修改 |
| P2 | 4-6 小时 | 3-4 个文件修改 |
| P3 | 2-4 小时 | 代码清理 |

**总计**: 16-24 小时

---

## ✅ 审计结论

**当前状态**: ⚠️ **不建议直接上线**

必须修复 P0 级别的 7 个严重问题后才能部署到生产环境，特别是：
- 资源泄漏问题会导致服务崩溃
- 金额精度问题会导致财务错误
- 静态 token 存在严重安全隐患

修复后建议进行：
1. 安全渗透测试
2. 压力测试（验证资源泄漏修复）
3. 财务准确性测试（验证 Decimal 修复）

---

**审计报告版本**: 1.0  
**生成时间**: 2026-03-29  
**下次审计**: 修复 P0 问题后
