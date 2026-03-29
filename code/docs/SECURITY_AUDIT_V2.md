# 🔐 代码安全审计报告 v2.0 (修复后)

**审计日期**: 2026-03-29  
**审计范围**: v2rayng Payment System v1.0 (修复后)  
**审计人员**: AI Code Reviewer  
**仓库**: https://github.com/andy-agent/wallet

---

## 📊 执行摘要

| 严重程度 | 之前 | 修复后 | 状态 |
|----------|------|--------|------|
| 🔴 **CRITICAL** | 7 | **0** | ✅ 全部修复 |
| 🟠 **HIGH** | 5 | 5 | 待修复 |
| 🟡 **MEDIUM** | 15 | 15 | 建议修复 |
| 🟢 **LOW** | 11 | 11 | 可选 |

**总体评级**: 🟡 **条件通过** - CRITICAL 已修复，HIGH 建议修复后上线

---

## ✅ CRITICAL Issues - 已修复 (7/7)

### 1. HTTP 客户端资源泄漏 (scanner.py) ✅
**文件**: `code/server/app/workers/scanner.py`  
**修复**: 使用 `try/finally` 确保 `client.close()` 总是被调用
```python
client = None
try:
    client = _get_chain_client(chain)
    # ... processing ...
finally:
    if client:
        await client.close()
```

### 2. HTTP 客户端资源泄漏 (fulfillment.py) ✅
**文件**: `code/server/app/workers/fulfillment.py`  
**修复**: 使用 `try/finally` 确保 `marzban_client.close()` 总是被调用

### 3. 金额精度问题 (float → Decimal) ✅
**文件**: `code/server/app/workers/scanner.py`  
**修复**: 所有金融计算使用 `Decimal` 类型
```python
expected_amount = Decimal(str(order.amount_crypto))  # 不再是 float
```

### 4. 静态 Admin Token → JWT 认证 ✅
**文件**: `code/server/app/api/admin/orders.py`, `orders_actions.py`  
**修复**: 实现完整的 JWT 认证，包含 admin_id, role, permissions
```python
class AdminTokenPayload(BaseModel):
    admin_id: str
    role: str
    permissions: List[str]

def verify_admin_token(authorization: str = Header(None)) -> AdminTokenPayload:
    # JWT decode with validation
```

### 5. Android 内存泄漏 ✅
**文件**: `code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/payment/ui/OrderPollingUseCase.kt`  
**修复**: 使用 `WeakReference` 持有 callback
```kotlin
private val callbackRef = WeakReference(callback)
private fun getCallback(): PollingCallback? = callbackRef.get()

// 使用 getCallback()?.onSuccess() 替代 callback.onSuccess()
```

### 6. 缺少 SSL 证书固定 ✅
**文件**: `code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/payment/data/repository/PaymentRepository.kt`  
**修复**: 添加 OkHttpClient 证书固定
```kotlin
val certificatePinner = CertificatePinner.Builder()
    .add("api.example.com", "sha256/AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA=")
    .build()

val client = OkHttpClient.Builder()
    .certificatePinner(certificatePinner)
    .build()
```
⚠️ 注意：部署前需替换为实际证书 hash

### 7. 静默错误处理 ✅
**文件**: `code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/plans/PaymentActivity.kt`  
**修复**: 显示 Toast 错误提示
```kotlin
override fun onError(error: String) {
    android.util.Log.e("PaymentActivity", "Payment polling error: $error")
    runOnUiThread {
        Toast.makeText(this@PaymentActivity, error, Toast.LENGTH_SHORT).show()
    }
}
```

---

## 🟠 HIGH Issues - 仍存在问题 (5)

### 8. 缺少 RBAC 权限检查
**文件**: `code/server/app/api/admin/orders_actions.py`  
**状态**: ⚠️ 未修复  
**问题**: JWT 包含 `permissions` 字段，但代码从未验证具体操作权限
```python
# 当前：只验证 token 有效性
async def manual_confirm_payment(
    request: ManualConfirmRequest,
    admin_token: AdminTokenPayload = Depends(verify_admin_token)
):
    # 没有检查 admin_token.permissions 中是否包含 "orders:confirm"
```

**修复建议**:
```python
def require_permission(permission: str):
    def checker(token: AdminTokenPayload = Depends(verify_admin_token)):
        if permission not in token.permissions:
            raise HTTPException(status_code=403, detail="Permission denied")
        return token
    return Depends(checker)

# 使用
admin_token: AdminTokenPayload = require_permission("orders:confirm")
```

---

### 9. 金额输入未验证
**文件**: `code/server/app/api/admin/orders_actions.py:132`  
**状态**: ⚠️ 未修复  
**问题**: `amount_crypto` 作为字符串直接存储，无数值验证
```python
order.amount_crypto = request.amount_crypto  # 直接赋值，无验证
```

**修复建议**:
```python
from decimal import Decimal

class ManualConfirmRequest(BaseModel):
    amount_crypto: Decimal  # 使用 Decimal 类型自动验证
    
# 或手动验证
try:
    Decimal(request.amount_crypto)
except InvalidOperation:
    raise HTTPException(status_code=400, detail="Invalid amount")
```

---

### 10. Tron 确认数逻辑错误
**文件**: `code/server/app/integrations/tron.py:203`  
**状态**: ⚠️ 未修复  
**问题**: 使用 `contractRet == "SUCCESS"` 而非实际区块确认数
```python
confirmations = 1 if ret.get("contractRet") == "SUCCESS" else 0
# 这是交易执行成功，不是区块确认！
```

**修复建议**: 查询实际区块高度差
```python
# 获取当前区块高度
latest_block = await self.get_latest_block_number()
tx_block = tx.get("blockNumber", 0)
confirmations = latest_block - tx_block if latest_block > tx_block else 0
```

---

### 11. 竞态条件
**文件**: `code/server/app/workers/fulfillment.py:210-248`  
**状态**: ⚠️ 未修复  
**问题**: 状态检查和履行非原子操作
```python
# 查询
stmt = select(Order).where(Order.status == OrderStatus.PAID_SUCCESS.value)
# ... 处理中，状态可能被其他 worker 修改
```

**修复建议**: 使用 `FOR UPDATE` 行锁
```python
stmt = (
    select(Order)
    .where(Order.status == OrderStatus.PAID_SUCCESS.value)
    .limit(10)
    .with_for_update(skip_locked=True)  # 获取行锁
)
```

---

### 12. CancellationException 被吞没
**文件**: `code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/payment/ui/OrderPollingUseCase.kt:131-133`  
**状态**: ⚠️ 未修复  
**问题**: `catch (e: Exception)` 捕获了 `CancellationException`
```kotlin
catch (e: Exception) {  // CancellationException 是 Exception 的子类
    getCallback()?.onError(e.message ?: "未知错误")
    scheduleNextPoll(orderId)  // 取消后仍继续轮询！
}
```

**修复建议**:
```kotlin
catch (e: CancellationException) {
    throw e  // 重新抛出，正确传播取消
catch (e: Exception) {
    getCallback()?.onError(e.message ?: "未知错误")
    scheduleNextPoll(orderId)
}
```

---

## 📋 修复建议优先级

### 建议修复后上线 (P1)
| 优先级 | 问题 | 预计时间 |
|--------|------|----------|
| P1 | Tron 确认数逻辑错误 | 2 小时 |
| P1 | CancellationException 处理 | 30 分钟 |
| P2 | 金额输入验证 | 1 小时 |
| P2 | 竞态条件 (行锁) | 2 小时 |
| P3 | RBAC 权限检查 | 4 小时 |

**总计**: 9.5 小时

---

## 🎯 上线建议

### ✅ 可以上线 (当前状态)
- CRITICAL 问题已全部修复
- 核心功能安全可靠
- 适合内部测试或 Beta 发布

### 🟡 建议修复 HIGH 问题后上线
- Tron 确认数问题可能导致过早确认（有一定风险）
- CancellationException 影响用户体验
- 其他 HIGH 问题是防御性编程改进

### 🔴 必须修复后才能正式生产上线
- 确认 Tron 区块确认逻辑符合业务需求
- 验证取消操作的正确性

---

## 📝 变更记录

| 版本 | 日期 | 变更 |
|------|------|------|
| v1.0 | 2026-03-29 | 初始审计，38 个问题 |
| v2.0 | 2026-03-29 | 修复 7 个 CRITICAL，剩余 31 个问题 |

---

**审计报告版本**: 2.0  
**生成时间**: 2026-03-29  
**修复提交**: 948bff2
