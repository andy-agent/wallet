# Android 当前页面与代码映射表

> **用途**: 本文档作为"高保真设计稿 -> Compose 改造"的对照基础，记录当前代码真实状态与设计需求的差异。

## 1. 文档说明

### 1.1 来源优先级
1. 当前仓库 Android 代码真实现状 (V2rayNG/app/src/main)
2. `final_engineering_delivery_package/05_ia_and_page_spec.md`
3. `docs/ANDROID_APP_UI_REQUIREMENTS_FOR_AI.md`

### 1.2 代码位置
```
code/Android/V2rayNG/app/src/main/
├── java/com/v2ray/ang/
│   ├── ui/                           # 基础 UI 组件
│   ├── payment/ui/activity/          # 支付相关 Activity
│   ├── plans/                        # 套餐相关 Activity
│   ├── payment/data/
│   │   ├── api/                      # API 接口 (PaymentApi.kt)
│   │   ├── model/                    # 数据模型 (Order.kt, Plan.kt)
│   │   ├── repository/               # 数据仓库 (PaymentRepository.kt)
│   │   └── local/entity/             # Room 实体
│   └── viewmodel/                    # ViewModel
└── res/layout/                       # XML Layout 文件
```

---

## 2. 当前页面总表

### 2.1 页面存在状态矩阵

| PRD 页面 | 当前代码存在 | 完成度 | 状态说明 |
|---------|------------|-------|---------|
| 启动页 | ❌ | - | 使用 V2rayNG 原生启动，无 CryptoVPN 品牌页 |
| 强更页 | ❌ | - | 未实现 |
| 可选更新弹窗 | ❌ | - | 未实现 |
| 邮箱登录页 | ✅ | 80% | `LoginActivity` 存在，合并了注册功能 |
| 邮箱注册页 | ✅ | 50% | 与登录合并为同一页面，需拆分 |
| 重置密码页 | ❌ | - | 未实现 |
| VPN 首页 | ✅ | 60% | `MainActivity` 存在，但为 V2rayNG 原生前端 |
| 套餐页 | ✅ | 85% | `PlansActivity` 完整实现 |
| 区域选择页 | ❌ | - | 未实现，V2rayNG 原生有节点选择但非区域模型 |
| 订单收银台 | ✅ | 85% | `PaymentActivity` 完整实现 |
| 钱包支付确认页 | ❌ | - | 未实现 |
| 订单结果页 | ⚠️ | 30% | 支付成功有弹窗，无独立结果页 |
| 我的订单列表页 | ✅ | 70% | `UserProfileActivity` 内嵌订单列表 |
| 订单详情页 | ✅ | 40% | `UserProfileActivity` 内 AlertDialog 展示 |
| 钱包引导页 | ❌ | - | 未实现 |
| 钱包首页 | ❌ | - | 未实现 |
| 资产详情页 | ❌ | - | 未实现 |
| 收款页 | ❌ | - | 未实现 |
| 发送页 | ❌ | - | 未实现 |
| 发送结果页 | ❌ | - | 未实现 |
| 邀请中心页 | ✅ | 85% | `InvitationCenterActivity` 完整实现 |
| 佣金账本页 | ✅ | 85% | `CommissionLedgerActivity` 完整实现 |
| 提现申请页 | ✅ | 85% | `WithdrawalActivity` 完整实现 |
| 我的页/设置 | ✅ | 60% | `UserProfileActivity` + 原生 Settings |
| 法务文档列表页 | ❌ | - | 未实现 |
| 法务文档详情页 | ❌ | - | 未实现 |
| 全局会话失效弹窗 | ❌ | - | 未实现 |

---

## 3. 详细页面映射

### 3.1 认证模块

#### 登录/注册页 (合并实现)
```kotlin
// Activity
com.v2ray.ang.payment.ui.activity.LoginActivity
  ↳ 位置: code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/payment/ui/activity/LoginActivity.kt

// Layout
res/layout/activity_login.xml

// Repository
com.v2ray.ang.payment.data.repository.PaymentRepository
  ↳ 位置: code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/payment/data/repository/PaymentRepository.kt

// Model
com.v2ray.ang.payment.data.model.LoginRequest
com.v2ray.ang.payment.data.model.RegisterRequest
com.v2ray.ang.payment.data.model.AuthData
  ↳ 位置: code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/payment/data/model/Order.kt

// API
PaymentApi.login()      @ POST /api/client/v1/auth/login/password
PaymentApi.register()   @ POST /api/client/v1/auth/register/email
PaymentApi.requestRegisterCode() @ POST /api/client/v1/auth/register/email/request-code
```

**当前状态**: 
- ✅ 邮箱登录实现
- ✅ 验证码注册实现（与登录同一页面切换）
- ❌ 注册/登录未分页面
- ❌ 无重置密码功能
- ❌ 无协议勾选界面

**Compose 改造建议**: 需完全重写，拆分登录/注册为独立页面，增加重置密码

---

### 3.2 VPN 首页模块

#### MainActivity (当前 VPN 主界面)
```kotlin
// Activity
com.v2ray.ang.ui.MainActivity
  ↳ 位置: code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/ui/MainActivity.kt (702行)

// Layout
res/layout/activity_main.xml

// Fragment
com.v2ray.ang.ui.GroupServerFragment
  ↳ 位置: code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/ui/GroupServerFragment.kt

// ViewModel
com.v2ray.ang.viewmodel.MainViewModel
  ↳ 位置: code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/viewmodel/MainViewModel.kt

// Repository
com.v2ray.ang.payment.data.repository.PaymentRepository (用于登录检查)
```

**当前状态**:
- ✅ V2rayNG 原生 VPN 连接功能完整
- ✅ 侧边栏导航集成支付模块入口
- ❌ 非 PRD 设计的 VPN 首页（当前为节点列表页）
- ❌ 无订阅状态卡片
- ❌ 无区域选择入口（只有节点选择）
- ❌ 无全局/规则模式切换

**关键代码片段**:
```kotlin
// MainActivity 中的登录检查跳转
private fun checkLoginAndProceed(destination: Intent): Boolean {
    return if (paymentRepository.isTokenValid()) {
        startActivity(destination)
        true
    } else {
        pendingDestination = destination
        loginLauncher.launch(Intent(this, LoginActivity::class.java))
        false
    }
}

// 侧边栏导航项
R.id.nav_purchase -> PlansActivity
R.id.nav_user_profile -> UserProfileActivity
R.id.nav_invitation_center -> InvitationCenterActivity
R.id.nav_commission_ledger -> CommissionLedgerActivity
R.id.nav_withdrawal -> WithdrawalActivity
```

**Compose 改造建议**: 需完全重写，参考 PRD 设计为订阅状态驱动的首页

---

### 3.3 套餐与订单模块

#### 套餐页 (PlansActivity)
```kotlin
// Activity
com.v2ray.ang.plans.PlansActivity
  ↳ 位置: code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/plans/PlansActivity.kt

// Adapter
com.v2ray.ang.plans.PlansAdapter

// Layout
res/layout/activity_plans.xml
res/layout/item_plan.xml

// Model
com.v2ray.ang.payment.data.model.Plan
  ↳ billingCycleMonths, priceUsd, includesAdvancedRegions

// Repository
PaymentRepository.getPlans()

// API
PaymentApi.getPlans() @ GET /api/client/v1/plans
```

**当前状态**: 
- ✅ 套餐列表展示完整
- ✅ 套餐选择跳转到支付
- ⚠️ 无"是否含高级区域"明确标识
- ⚠️ 缺少 Empty/Error 状态设计

#### 订单收银台 (PaymentActivity)
```kotlin
// Activity
com.v2ray.ang.plans.PaymentActivity
  ↳ 位置: code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/plans/PaymentActivity.kt (380行)

// Layout
res/layout/activity_payment.xml

// UseCase
com.v2ray.ang.payment.ui.OrderPollingUseCase
  ↳ 位置: code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/payment/ui/OrderPollingUseCase.kt

// Model
com.v2ray.ang.payment.data.model.Order
com.v2ray.ang.payment.data.model.PaymentTarget

// Repository
PaymentRepository.createOrder()
PaymentRepository.getOrder()

// API
PaymentApi.createOrder()       @ POST /api/client/v1/orders
PaymentApi.getPaymentTarget()  @ GET /api/client/v1/orders/{orderNo}/payment-target
PaymentApi.refreshOrderStatus() @ POST /api/client/v1/orders/{orderNo}/refresh-status
```

**当前状态**:
- ✅ 订单创建完整
- ✅ 支付地址/二维码展示
- ✅ 支付方式切换 (SOL/USDT-Solana/USDT-TRON)
- ✅ 支付状态轮询
- ✅ 倒计时显示
- ⚠️ 支付成功后仅弹窗，无独立结果页
- ❌ 无"使用内置钱包支付"入口（需后续钱包模块）

#### 我的订单列表/详情 (内嵌在 UserProfileActivity)
```kotlin
// Activity
com.v2ray.ang.payment.ui.activity.UserProfileActivity

// Layout
res/layout/activity_user_profile.xml
res/layout/item_order_history.xml

// Adapter
UserProfileActivity.OrderHistoryAdapter (内部类)

// Model
com.v2ray.ang.payment.data.local.entity.OrderEntity
  ↳ orderNo, planName, amount, assetCode, status, createdAt, paidAt, expiredAt

// Repository
PaymentRepository.getCachedOrders()
```

**当前状态**:
- ✅ 订单列表展示
- ⚠️ 详情为 AlertDialog 弹窗，非独立页面
- ⚠️ 无订单状态时间线

---

### 3.4 邀请与提现模块

#### 邀请中心页 (InvitationCenterActivity)
```kotlin
// Activity
com.v2ray.ang.payment.ui.activity.InvitationCenterActivity
  ↳ 位置: code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/payment/ui/activity/InvitationCenterActivity.kt

// Layout
res/layout/activity_invitation_center.xml

// Model (API Response)
com.v2ray.ang.payment.data.api.ReferralOverviewData
  ↳ referralCode, level1InviteCount, level2InviteCount
  ↳ level1IncomeUsdt, level2IncomeUsdt
  ↳ availableAmountUsdt, frozenAmountUsdt, minWithdrawAmountUsdt

// API
PaymentApi.getReferralOverview() @ GET /api/client/v1/referral/overview
PaymentApi.bindReferralCode()    @ POST /api/client/v1/referral/bind
```

**当前状态**: 
- ✅ 邀请码展示
- ✅ 一级/二级人数统计
- ✅ 佣金总览
- ✅ 绑定邀请码功能
- ✅ 最低提现门槛提示

#### 佣金账本页 (CommissionLedgerActivity)
```kotlin
// Activity
com.v2ray.ang.payment.ui.activity.CommissionLedgerActivity

// Layout
res/layout/activity_commission_ledger.xml
res/layout/item_commission_ledger.xml

// Model
com.v2ray.ang.payment.data.api.CommissionLedgerItem
  ↳ entryNo, sourceOrderNo, sourceAccountMasked
  ↳ commissionLevel, settlementAmountUsdt, status, availableAt

// API
PaymentApi.getCommissionLedger() @ GET /api/client/v1/commissions/ledger
PaymentApi.getCommissionSummary() @ GET /api/client/v1/commissions/summary
```

**当前状态**:
- ✅ 佣金明细列表
- ✅ 层级标签 (LEVEL1/LEVEL2)
- ✅ 状态展示 (AVAILABLE/FROZEN/SETTLED)

#### 提现申请页 (WithdrawalActivity)
```kotlin
// Activity
com.v2ray.ang.payment.ui.activity.WithdrawalActivity

// Layout
res/layout/activity_withdrawal.xml
res/layout/item_withdrawal.xml

// Model
com.v2ray.ang.payment.data.api.WithdrawalItem
  ↳ requestNo, amount, assetCode, networkCode
  ↳ payoutAddress, status, txHash, createdAt

// API
PaymentApi.createWithdrawal() @ POST /api/client/v1/withdrawals
PaymentApi.getWithdrawals()   @ GET /api/client/v1/withdrawals
```

**当前状态**:
- ✅ 可提现余额展示
- ✅ 提现金额输入
- ✅ 提现地址输入
- ✅ 历史记录列表
- ⚠️ 资产/网络为固定 USDT/Solana，无切换选项

---

### 3.5 基础组件

#### BaseActivity (页面基类)
```kotlin
com.v2ray.ang.ui.BaseActivity
  ↳ 位置: code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/ui/BaseActivity.kt (216行)

// 功能
- Toolbar 自动配置
- 加载进度条 (showLoading/hideLoading)
- 返回键处理
- 语言环境适配
```

#### 数据仓库 (PaymentRepository)
```kotlin
com.v2ray.ang.payment.data.repository.PaymentRepository
  ↳ 位置: code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/payment/data/repository/PaymentRepository.kt (745行)

// 功能模块
- Token 管理 (Access/Refresh)
- 用户认证 (登录/注册/Token刷新)
- 套餐获取
- 订单管理 (创建/查询/缓存)
- 订阅查询
- 邀请/佣金/提现
- 本地数据库 (Room) 操作
```

#### 本地数据库实体
```kotlin
// UserEntity - 用户缓存
com.v2ray.ang.payment.data.local.entity.UserEntity
  ↳ userId, username, email, accessToken, refreshToken, loginAt

// OrderEntity - 订单缓存
com.v2ray.ang.payment.data.local.entity.OrderEntity
  ↳ orderNo, planName, planId, amount, assetCode, status
  ↳ createdAt, paidAt, fulfilledAt, expiredAt
  ↳ subscriptionUrl, userId
```

---

## 4. 缺失页面清单 (需 Compose 新建)

### 4.1 高优先级缺失

| 页面 | 优先级 | 依赖 | 说明 |
|-----|-------|-----|------|
| 启动页 | P0 | - | App 品牌入口，需版本检查与会话路由 |
| 强更页 | P0 | 启动页 | 阻断低版本使用 |
| VPN 首页(新) | P0 | 订阅 API | PRD 核心页面，需展示订阅状态 |
| 区域选择页 | P0 | VPN 首页 | 按套餐权限选择区域 |
| 钱包引导页 | P1 | - | 首次使用钱包时引导 |
| 钱包首页 | P1 | 钱包模块 | 链与资产总览 |
| 发送页 | P1 | 钱包首页 | 转账/支付 |

### 4.2 中优先级缺失

| 页面 | 优先级 | 依赖 | 说明 |
|-----|-------|-----|------|
| 邮箱注册页(独立) | P1 | - | 当前与登录合并 |
| 重置密码页 | P1 | - | 邮箱验证码找回密码 |
| 钱包支付确认页 | P1 | 钱包模块 | 内置钱包支付前确认 |
| 订单结果页(独立) | P1 | 支付页 | 支付成功/失败结果 |
| 资产详情页 | P2 | 钱包首页 | 单资产详情与历史 |
| 收款页 | P2 | 钱包首页 | 地址与二维码展示 |
| 发送结果页 | P2 | 发送页 | 转账结果展示 |

### 4.3 低优先级缺失

| 页面 | 优先级 | 依赖 | 说明 |
|-----|-------|-----|------|
| 可选更新弹窗 | P2 | 启动页 | 非强制更新提示 |
| 法务文档列表页 | P3 | - | 协议/政策入口 |
| 法务文档详情页 | P3 | 列表页 | 文档正文展示 |
| 全局会话失效弹窗 | P2 | 全局 | 会话被挤下提示 |

---

## 5. 可复用组件

### 5.1 完全可复用 (逻辑层)

```kotlin
// Repository 层 - 完全复用
com.v2ray.ang.payment.data.repository.PaymentRepository

// API 接口 - 完全复用
com.v2ray.ang.payment.data.api.PaymentApi

// 数据模型 - 完全复用
com.v2ray.ang.payment.data.model.*

// Room 数据库 - 完全复用
com.v2ray.ang.payment.data.local.*

// 配置常量 - 完全复用
com.v2ray.ang.payment.PaymentConfig
```

### 5.2 可复用需调整 (UI 层)

```kotlin
// Adapter 模式 - 参考但需改为 Compose
com.v2ray.ang.plans.PlansAdapter          → LazyColumn + items
com.v2ray.ang.payment.ui.activity.CommissionLedgerActivity.CommissionLedgerAdapter  → LazyColumn

// 轮询逻辑 - 复用业务逻辑
com.v2ray.ang.payment.ui.OrderPollingUseCase

// Toolbar 处理 - 参考实现
com.v2ray.ang.ui.BaseActivity.setupToolbar()
```

### 5.3 不可复用 (需重写)

```xml
<!-- XML Layout 文件 - 全部废弃 -->
res/layout/activity_*.xml
res/layout/item_*.xml
res/layout/fragment_*.xml
```

```kotlin
// Activity/Fragment UI - 全部废弃
// 仅保留业务逻辑调用，重写为 @Composable
```

---

## 6. 与 PRD 差异对照

### 6.1 设计差异汇总

| 项目 | PRD 要求 | 当前实现 | 差异说明 |
|-----|---------|---------|---------|
| 导航结构 | 底部 Tab (VPN/钱包/邀请/我的) | 侧边栏导航 | 需改为 BottomNavigation |
| VPN 首页 | 订阅卡片 + 连接入口 | 节点列表 | 完全不同的视觉结构 |
| 登录注册 | 分页面 | 合并单页 | 需拆分 |
| 订单结果 | 独立结果页 | 成功弹窗 | 需独立页面 |
| 钱包模块 | 完整钱包 | 未实现 | 完全缺失 |
| 区域选择 | 区域模型 | 节点选择 | 概念不同 |

### 6.2 API 对接状态

| API 端点 | 状态 | 使用位置 |
|---------|-----|---------|
| `POST /api/client/v1/auth/login/password` | ✅ 已对接 | LoginActivity |
| `POST /api/client/v1/auth/register/email` | ✅ 已对接 | LoginActivity |
| `POST /api/client/v1/auth/register/email/request-code` | ✅ 已对接 | LoginActivity |
| `POST /api/client/v1/auth/refresh` | ✅ 已对接 | PaymentRepository.refreshTokenIfNeeded() |
| `GET /api/client/v1/plans` | ✅ 已对接 | PlansActivity |
| `POST /api/client/v1/orders` | ✅ 已对接 | PaymentActivity |
| `GET /api/client/v1/orders/{orderNo}` | ✅ 已对接 | PaymentRepository.getOrder() |
| `GET /api/client/v1/orders/{orderNo}/payment-target` | ✅ 已对接 | PaymentActivity |
| `POST /api/client/v1/orders/{orderNo}/refresh-status` | ✅ 已对接 | OrderPollingUseCase |
| `GET /api/client/v1/subscriptions/current` | ✅ 已对接 | PaymentRepository.getSubscription() |
| `GET /api/client/v1/me` | ✅ 已对接 | UserProfileActivity |
| `GET /api/client/v1/referral/overview` | ✅ 已对接 | InvitationCenterActivity |
| `POST /api/client/v1/referral/bind` | ✅ 已对接 | InvitationCenterActivity |
| `GET /api/client/v1/commissions/summary` | ✅ 已对接 | CommissionLedgerActivity |
| `GET /api/client/v1/commissions/ledger` | ✅ 已对接 | CommissionLedgerActivity |
| `POST /api/client/v1/withdrawals` | ✅ 已对接 | WithdrawalActivity |
| `GET /api/client/v1/withdrawals` | ✅ 已对接 | WithdrawalActivity |
| `GET /api/client/v1/vpn/regions` | ❌ 未对接 | - |
| `POST /api/client/v1/vpn/config/issue` | ❌ 未对接 | - |
| `GET /api/client/v1/wallet/chains` | ❌ 未对接 | - |
| `GET /api/client/v1/wallet/assets/catalog` | ❌ 未对接 | - |
| `POST /api/client/v1/wallet/transfer/precheck` | ❌ 未对接 | - |

---

## 7. Compose 改造拆工建议

### 7.1 阶段一：基础架构 (Week 1)

```
任务:
1. 引入 Jetpack Compose 依赖
2. 创建 Compose 主题 (颜色/字体/形状)
3. 实现基础组件库 (Button/TextField/Card)
4. 迁移 BaseActivity → BaseComposeActivity
```

### 7.2 阶段二：认证模块 (Week 1-2)

```
任务:
1. 新建 SplashScreen (Compose)
2. 重写 LoginScreen (Compose) - 拆分登录
3. 新建 RegisterScreen (Compose) - 独立注册
4. 新建 ResetPasswordScreen (Compose)
5. 保持使用 PaymentRepository 进行 API 调用
```

### 7.3 阶段三：核心流程 (Week 2-3)

```
任务:
1. 新建 HomeScreen (VPN 首页) - PRD 设计
2. 新建 RegionSelectionScreen (区域选择)
3. 迁移 PlansScreen (参考现有逻辑)
4. 迁移 PaymentScreen (参考现有逻辑)
5. 新建 OrderResultScreen (支付结果)
```

### 7.4 阶段四：钱包模块 (Week 3-4)

```
任务:
1. 新建 WalletGuideScreen (钱包引导)
2. 新建 WalletHomeScreen (钱包首页)
3. 新建 AssetDetailScreen (资产详情)
4. 新建 ReceiveScreen (收款)
5. 新建 SendScreen (发送)
6. 新建 SendResultScreen (发送结果)
```

### 7.5 阶段五：邀请与设置 (Week 4)

```
任务:
1. 迁移 InvitationCenterScreen
2. 迁移 CommissionLedgerScreen
3. 迁移 WithdrawalScreen
4. 新建 MyProfileScreen (我的页)
5. 迁移 SettingsScreen
```

### 7.6 阶段六：全局与优化 (Week 5)

```
任务:
1. 实现 BottomNavigation 导航
2. 新建 ForceUpdateScreen
3. 新建 OptionalUpdateDialog
4. 新建 SessionExpiredDialog
5. 全局错误处理/网络错误页
```

---

## 8. 参考文件清单

### 8.1 核心实现文件 (保留)

```
code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/payment/
├── data/
│   ├── api/PaymentApi.kt                    # API 接口定义
│   ├── model/Order.kt                       # 订单/登录模型
│   ├── model/Plan.kt                        # 套餐模型
│   ├── repository/PaymentRepository.kt      # 数据仓库
│   ├── repository/LocalPaymentRepository.kt # 本地数据
│   └── local/
│       ├── database/PaymentDatabase.kt      # Room DB
│       ├── entity/UserEntity.kt             # 用户实体
│       ├── entity/OrderEntity.kt            # 订单实体
│       └── dao/*.kt                         # DAO 接口
└── service/SubscriptionReminderWorker.kt    # 订阅提醒
```

### 8.2 待废弃文件 (Compose 后删除)

```
code/Android/V2rayNG/app/src/main/res/layout/
├── activity_login.xml
├── activity_plans.xml
├── activity_payment.xml
├── activity_user_profile.xml
├── activity_invitation_center.xml
├── activity_commission_ledger.xml
├── activity_withdrawal.xml
└── item_*.xml (所有列表项)

code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/payment/ui/activity/
├── LoginActivity.kt
└── (其他 Activity 文件)

code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/plans/
├── PlansActivity.kt
├── PaymentActivity.kt
└── PlansAdapter.kt
```

---

## 9. 与 `ANDROID_APP_UI_REQUIREMENTS_FOR_AI.md` 对照

### 9.1 已冻结业务规则 - 代码合规检查

| 规则 | 代码实现 | 状态 |
|-----|---------|-----|
| 邮箱优先注册/登录 | LoginActivity 实现 | ✅ 合规 |
| 单活跃 session | PaymentRepository Token 管理 | ✅ 合规 |
| VPN 协议 VLESS+Reality | V2rayNG 原生 | ✅ 合规 |
| 支付资产 SOL/USDT | PaymentActivity 支持 | ✅ 合规 |
| 客户端本地签名 | ❌ 未实现 | ⚠️ 需钱包模块 |
| 一级 25%/二级 5% 分佣 | API 返回 | ✅ 合规 |
| 提现最低 10 USDT | WithdrawalActivity 校验 | ✅ 合规 |

### 9.2 页面清单对照

参见第 2 节 [页面存在状态矩阵](#21-页面存在状态矩阵)

### 9.3 组件清单 - 实现状态

| 组件 | 代码状态 | Compose 需求 |
|-----|---------|-------------|
| 主/次/危险按钮 | XML 实现 | 需 Compose 重写 |
| 输入框/验证码输入 | XML 实现 | 需 Compose 重写 |
| 套餐卡片 | XML 实现 | 需 Compose 重写 |
| 订单信息卡片 | XML 实现 | 需 Compose 重写 |
| 钱包资产卡片 | ❌ 未实现 | 需 Compose 新建 |
| 状态标签 | 代码实现 | 需 Compose 重写 |
| 底部 Tab | ❌ 未实现 | 需 Compose 新建 |
| 列表 Cell | XML 实现 | 需 Compose 重写 |
| 空/错误状态 | 部分实现 | 需 Compose 完善 |
| Skeleton | ❌ 未实现 | 需 Compose 新建 |
| 弹窗/Bottom Sheet | 部分实现 | 需 Compose 完善 |
| QR 展示 | 已实现 | 需 Compose 迁移 |
| 金额输入 | 部分实现 | 需 Compose 完善 |

---

## 10. 附录

### 10.1 代码统计

```bash
# Kotlin 文件统计
code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/payment/**/*.kt
- Activity: 6 个
- Repository: 2 个
- Model: 4 个
- API: 1 个
- Entity: 3 个
- DAO: 3 个

# Layout 文件统计
code/Android/V2rayNG/app/src/main/res/layout/activity_*.xml
- Activity Layout: 24 个

# 与 PRD 相关 Layout
- activity_login.xml
- activity_plans.xml
- activity_payment.xml
- activity_user_profile.xml
- activity_invitation_center.xml
- activity_commission_ledger.xml
- activity_withdrawal.xml
```

### 10.2 关键常量

```kotlin
// PaymentConfig.kt
object PaymentConfig {
    const val API_BASE_URL = "https://api.cryptovpn.example.com/" // 实际为配置值
    const val API_VERSION = "api/client/v1"
    
    object AssetCode {
        const val SOL = "SOL"
        const val USDT = "USDT"
    }
    
    object NetworkCode {
        const val SOLANA = "SOLANA"
        const val TRON = "TRON"
    }
}
```

---

**文档版本**: 1.0  
**生成时间**: 2026-04-03  
**基于代码版本**: V2rayNG + CryptoVPN Payment Module
