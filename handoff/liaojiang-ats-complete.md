# 任务完成报告 - APP本地Room数据库存储用户支付历史

**任务ID**: liaojiang-ats  
**任务名称**: APP本地Room数据库存储用户支付历史  
**完成时间**: 2026-03-31  
**项目路径**: /Users/cnyirui/git/projects/liaojiang/code/Android/V2rayNG

---

## 已实现功能

### 1. Room依赖添加 ✅
**文件**: `app/build.gradle.kts`

添加了以下依赖：
- `implementation("androidx.room:room-runtime:2.6.1")`
- `implementation("androidx.room:room-ktx:2.6.1")`
- `kapt("androidx.room:room-compiler:2.6.1")`

同时添加了 `kotlin-kapt` 插件支持注解处理。

---

### 2. Entity实体类创建 ✅
**路径**: `app/src/main/java/com/v2ray/ang/payment/data/local/entity/`

#### UserEntity.kt
- `userId` (PrimaryKey) - 用户ID
- `username` - 用户名
- `email` - 邮箱
- `accessToken` - 访问令牌
- `refreshToken` - 刷新令牌
- `loginAt` - 登录时间戳

#### OrderEntity.kt
- `orderNo` (PrimaryKey) - 订单号
- `planName` - 套餐名称
- `planId` - 套餐ID
- `amount` - 金额
- `assetCode` - 资产代码
- `status` - 订单状态
- `createdAt` - 创建时间
- `paidAt` - 支付时间
- `fulfilledAt` - 履行时间
- `expiredAt` - 到期时间
- `subscriptionUrl` - 订阅URL
- `marzbanUsername` - Marzban用户名
- `userId` (ForeignKey) - 关联用户ID

#### PaymentHistoryEntity.kt
- `id` (PrimaryKey, autoGenerate) - 记录ID
- `orderNo` - 订单号
- `amount` - 金额
- `assetCode` - 资产代码
- `txHash` - 交易哈希
- `paidAt` - 支付时间

---

### 3. DAO接口创建 ✅
**路径**: `app/src/main/java/com/v2ray/ang/payment/data/local/dao/`

#### UserDao.kt
- `insert(user: UserEntity)` - 插入用户
- `update(user: UserEntity)` - 更新用户
- `getByUsername(username: String)` - 根据用户名查询
- `getByUserId(userId: String)` - 根据ID查询
- `getCurrentUser()` - 获取当前用户
- `delete(user: UserEntity)` - 删除用户
- `deleteAll()` - 删除所有用户

#### OrderDao.kt
- `insert(order: OrderEntity)` - 插入订单
- `update(order: OrderEntity)` - 更新订单
- `getByOrderNo(orderNo: String)` - 根据订单号查询
- `getAllByUserId(userId: String)` - 获取用户所有订单
- `getActiveOrders(userId: String)` - 获取有效订单
- `getExpiringOrders(userId: String, threshold: Long)` - 获取即将到期订单
- `deleteByOrderNo(orderNo: String)` - 根据订单号删除
- `deleteByUserId(userId: String)` - 根据用户ID删除

#### PaymentHistoryDao.kt
- `insert(paymentHistory: PaymentHistoryEntity)` - 插入支付记录
- `getByOrderNo(orderNo: String)` - 根据订单号查询
- `getAll()` - 获取所有记录
- `getAllByUserId(userId: String)` - 根据用户ID查询
- `deleteByOrderNo(orderNo: String)` - 根据订单号删除
- `deleteAll()` - 删除所有记录

---

### 4. Database创建 ✅
**文件**: `app/src/main/java/com/v2ray/ang/payment/data/local/database/PaymentDatabase.kt`

- 使用`@Database`注解，包含3个实体
- 数据库版本：1
- 单例模式获取实例
- 提供三个DAO访问方法

---

### 5. Repository创建 ✅
**文件**: `app/src/main/java/com/v2ray/ang/payment/data/repository/LocalPaymentRepository.kt`

封装所有数据库操作：
- 用户操作：保存、更新、查询、删除
- 订单操作：保存、更新、查询、删除、获取即将到期订单
- 支付历史操作：保存、查询、删除
- 数据同步：批量同步订单和支付历史
- 清除所有数据：退出登录时调用

所有操作使用`Dispatchers.IO`在后台线程执行。

---

### 6. PaymentRepository修改 ✅
**文件**: `app/src/main/java/com/v2ray/ang/payment/data/repository/PaymentRepository.kt`

集成本地数据库缓存：
- 新增`LocalPaymentRepository`实例
- `cacheUserInfo()` - 登录后缓存用户信息
- `cacheOrder()` - 创建订单后缓存订单信息
- `updateOrderStatus()` - 支付成功后更新订单状态
- `getCachedOrders()` - 获取本地缓存订单
- `getCachedPaymentHistory()` - 获取本地支付历史
- `getCachedCurrentUser()` - 获取当前缓存用户
- `logout()` - 清除本地数据并退出登录
- 在`createOrder()`和`getOrder()`中自动同步数据到本地

---

### 7. 到期提醒服务创建 ✅
**文件**: `app/src/main/java/com/v2ray/ang/payment/service/SubscriptionReminderWorker.kt`

使用WorkManager实现定期任务：
- 每12小时检查一次即将到期的订阅
- 检查3天内到期的订单
- 发送本地通知提醒：
  - 3天前提醒
  - 1天前提醒
  - 已过期提醒
- 支持启动/停止任务
- 通知点击跳转到用户中心页面

---

### 8. 用户中心页面创建 ✅
**Activity文件**: `app/src/main/java/com/v2ray/ang/payment/ui/activity/UserProfileActivity.kt`
**布局文件**: `app/src/main/res/layout/activity_user_profile.xml`
**列表项**: `app/src/main/res/layout/item_order_history.xml`

功能：
- 显示用户信息（用户名、邮箱、登录时间）
- 显示订阅状态（到期时间、剩余天数）
- 订单历史列表（从本地数据库读取）
- 订单详情弹窗
- 刷新数据（从服务器同步）
- 退出登录（清除本地数据）
- 未登录状态提示

---

### 9. 登录页面占位符 ✅
**文件**: 
- `app/src/main/java/com/v2ray/ang/payment/ui/activity/LoginActivity.kt`
- `app/src/main/res/layout/activity_login.xml`

基础占位符实现，需要后续根据实际认证流程完善。

---

### 10. 资源文件创建 ✅
**图标**: 
- `ic_person_24dp.xml` - 用户图标
- `ic_logout_24dp.xml` - 退出图标
- `ic_refresh_24dp.xml` - 刷新图标
- `ic_login_24dp.xml` - 登录图标
- `ic_stat_name.xml` - 通知图标

**字符串资源**: 添加到`strings.xml`
- 用户中心相关字符串
- 订单状态字符串
- 提示信息字符串

---

### 11. AndroidManifest.xml更新 ✅
注册新Activity：
- `UserProfileActivity`
- `LoginActivity`

---

## 文件清单

### 新增文件
```
app/src/main/java/com/v2ray/ang/payment/data/local/entity/UserEntity.kt
app/src/main/java/com/v2ray/ang/payment/data/local/entity/OrderEntity.kt
app/src/main/java/com/v2ray/ang/payment/data/local/entity/PaymentHistoryEntity.kt
app/src/main/java/com/v2ray/ang/payment/data/local/dao/UserDao.kt
app/src/main/java/com/v2ray/ang/payment/data/local/dao/OrderDao.kt
app/src/main/java/com/v2ray/ang/payment/data/local/dao/PaymentHistoryDao.kt
app/src/main/java/com/v2ray/ang/payment/data/local/database/PaymentDatabase.kt
app/src/main/java/com/v2ray/ang/payment/data/repository/LocalPaymentRepository.kt
app/src/main/java/com/v2ray/ang/payment/service/SubscriptionReminderWorker.kt
app/src/main/java/com/v2ray/ang/payment/ui/activity/UserProfileActivity.kt
app/src/main/java/com/v2ray/ang/payment/ui/activity/LoginActivity.kt
app/src/main/res/layout/activity_user_profile.xml
app/src/main/res/layout/activity_login.xml
app/src/main/res/layout/item_order_history.xml
app/src/main/res/drawable/ic_person_24dp.xml
app/src/main/res/drawable/ic_logout_24dp.xml
app/src/main/res/drawable/ic_refresh_24dp.xml
app/src/main/res/drawable/ic_login_24dp.xml
app/src/main/res/drawable/ic_stat_name.xml
```

### 修改文件
```
app/build.gradle.kts
app/src/main/java/com/v2ray/ang/payment/data/repository/PaymentRepository.kt
app/src/main/AndroidManifest.xml
app/src/main/res/values/strings.xml
```

---

## 验收标准检查

| 验收项 | 状态 |
|--------|------|
| Room数据库正常初始化 | ✅ 通过`PaymentDatabase`单例实现 |
| 用户登录后信息本地存储 | ✅ `cacheUserInfo()`方法 |
| 订单历史本地缓存 | ✅ `cacheOrder()`方法 |
| 离线可查看历史订单 | ✅ 从`LocalPaymentRepository`读取 |
| 到期前本地通知提醒 | ✅ `SubscriptionReminderWorker`实现 |

---

## 使用说明

### 1. 初始化数据库
数据库会在首次访问时自动初始化，无需手动操作。

### 2. 缓存用户数据
```kotlin
val paymentRepository = PaymentRepository(context)
paymentRepository.cacheUserInfo(userInfo, accessToken)
```

### 3. 启动到期提醒
```kotlin
SubscriptionReminderWorker.startReminderWork(context)
```

### 4. 获取本地订单历史
```kotlin
lifecycleScope.launch {
    val orders = paymentRepository.getCachedOrders(userId)
}
```

### 5. 退出登录
```kotlin
paymentRepository.logout()
SubscriptionReminderWorker.stopReminderWork(context)
```

---

## 后续建议

1. **登录功能完善**: LoginActivity需要实现实际的登录UI和认证逻辑
2. **数据迁移**: 如果后续需要修改数据库结构，请添加Migration
3. **加密敏感数据**: 考虑使用SQLCipher对敏感数据（如token）加密存储
4. **数据备份**: 考虑添加本地数据导出/导入功能
5. **清理策略**: 定期清理过期的历史订单数据

---

## 技术要点

- 使用Room 2.6.1版本
- 支持协程和Flow（已为后续扩展预留）
- 使用外键约束保证数据完整性
- WorkManager用于后台定期任务
- 通知渠道适配Android 8.0+
- 适配深色模式
