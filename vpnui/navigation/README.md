# CryptoVPN 导航路由系统

## 概述

CryptoVPN Android App的完整导航路由系统，基于Jetpack Navigation Compose实现，支持27个页面的导航管理。

## 文件结构

```
navigation/
├── Routes.kt              # 路由常量定义
├── NavigationManager.kt   # 导航管理器
├── NavGraph.kt           # 导航图配置
├── DeepLinkHandler.kt    # 深层链接处理
├── BackStackManager.kt   # 返回栈管理
└── README.md             # 使用说明
```

## 快速开始

### 1. 在App中配置导航

```kotlin
@Composable
fun CryptoVPNApp() {
    val navController = rememberNavController()
    
    CryptoVPNNavGraph(
        navController = navController,
        startDestination = Routes.Splash.FULL_ROUTE
    )
}
```

### 2. 使用导航管理器

```kotlin
// 获取导航管理器
val navigationManager = NavigationManagerProvider.get()

// 导航到VPN首页
navigationManager.navigateToVpnHome()

// 导航到套餐页
navigationManager.navigateToPlans()

// 返回上一页
navigationManager.goBack()
```

### 3. 带参数的导航

```kotlin
// 订单收银台
navigationManager.navigateToOrderCheckout("plan123", 12)

// 资产详情
navigationManager.navigateToAssetDetail("BTC", "bitcoin")

// 发送页面
navigationManager.navigateToSend(
    assetId = "ETH",
    chainId = "ethereum",
    toAddress = "0x123...",
    amount = "1.5"
)
```

## 路由常量

### 启动与版本

| 路由 | 常量 |
|------|------|
| Splash | `Routes.Splash.ROUTE` |
| Force Update | `Routes.ForceUpdate.ROUTE` |
| Optional Update | `Routes.OptionalUpdate.ROUTE` |

### 认证

| 路由 | 常量 |
|------|------|
| Email Login | `Routes.EmailLogin.ROUTE` |
| Email Register | `Routes.EmailRegister.ROUTE` |
| Reset Password | `Routes.ResetPassword.ROUTE` |

### VPN

| 路由 | 常量 |
|------|------|
| VPN Home | `Routes.VpnHome.ROUTE` |
| Plans | `Routes.Plans.ROUTE` |
| Region Selection | `Routes.RegionSelection.ROUTE` |
| Order Checkout | `Routes.OrderCheckout.ROUTE` |
| Wallet Payment Confirm | `Routes.WalletPaymentConfirm.ROUTE` |
| Order Result | `Routes.OrderResult.ROUTE` |
| Order List | `Routes.OrderList.ROUTE` |
| Order Detail | `Routes.OrderDetail.ROUTE` |

### 钱包

| 路由 | 常量 |
|------|------|
| Wallet Onboarding | `Routes.WalletOnboarding.ROUTE` |
| Wallet Home | `Routes.WalletHome.ROUTE` |
| Asset Detail | `Routes.AssetDetail.ROUTE` |
| Receive | `Routes.Receive.ROUTE` |
| Send | `Routes.Send.ROUTE` |
| Send Result | `Routes.SendResult.ROUTE` |
| Wallet Payment | `Routes.WalletPayment.ROUTE` |

### 增长

| 路由 | 常量 |
|------|------|
| Invite Center | `Routes.InviteCenter.ROUTE` |
| Commission Ledger | `Routes.CommissionLedger.ROUTE` |
| Withdraw | `Routes.Withdraw.ROUTE` |

### 我的与法务

| 路由 | 常量 |
|------|------|
| Profile | `Routes.Profile.ROUTE` |
| Legal Documents | `Routes.LegalDocuments.ROUTE` |
| Legal Document Detail | `Routes.LegalDocumentDetail.ROUTE` |

## 深层链接

### 支持的URI格式

```
cryptovpn://app/{route}?{params}
https://cryptovpn.com/{path}?{params}
```

### 深层链接示例

```kotlin
// 打开VPN首页
cryptovpn://app/vpn_home

// 打开VPN首页并自动连接
cryptovpn://app/vpn_home?autoConnect=true

// 打开订单详情
cryptovpn://app/order_detail/order123

// 打开钱包首页
cryptovpn://app/wallet_home

// 打开发送页面
cryptovpn://app/send/ETH?chainId=ethereum&toAddress=0x123&amount=1.0
```

### 处理深层链接

```kotlin
// 在Activity中处理
override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    
    intent?.data?.let { uri ->
        val handled = navigationManager.handleDeepLink(uri)
        if (!handled) {
            // 处理未识别的深层链接
        }
    }
}
```

## 返回栈管理

### 基本返回操作

```kotlin
val backStackManager = BackStackManager(navController)

// 返回上一页
backStackManager.goBack()

// 返回到指定路由
backStackManager.popBackTo(Routes.VpnHome.FULL_ROUTE, false)

// 返回到起始目的地
backStackManager.popToStart()

// 清空返回栈
backStackManager.clearStack()
```

### 返回拦截器

```kotlin
// 注册返回拦截器
backStackManager.registerInterceptor {
    // 返回true拦截返回，false不拦截
    if (shouldIntercept) {
        showConfirmDialog()
        true
    } else {
        false
    }
}

// 注销拦截器
backStackManager.unregisterInterceptor(interceptor)
```

### 返回行为配置

```kotlin
val behaviorManager = BackBehaviorManager()

// 设置页面的返回行为
behaviorManager.setBehavior(
    Routes.OrderCheckout.ROUTE,
    BackBehavior.PopTo(Routes.VpnHome.FULL_ROUTE)
)

// 使用确认对话框
behaviorManager.setBehavior(
    Routes.Send.ROUTE,
    BackBehavior.Confirm("确定要取消发送吗？")
)
```

## 路由分组

### 预定义路由组

```kotlin
// 认证路由
Routes.Groups.AUTH_ROUTES

// VPN路由
Routes.Groups.VPN_ROUTES

// 钱包路由
Routes.Groups.WALLET_ROUTES

// 增长路由
Routes.Groups.GROWTH_ROUTES

// 个人中心路由
Routes.Groups.PROFILE_ROUTES

// 需要登录的路由
Routes.Groups.PROTECTED_ROUTES
```

## 路由参数

### 参数数据类

```kotlin
// 订单收银台参数
val orderParams = RouteParams.OrderCheckoutParams(
    planId = "premium_yearly",
    duration = 12,
    couponCode = "SAVE20"
)

// 发送参数
val sendParams = RouteParams.SendParams(
    assetId = "BTC",
    chainId = "bitcoin",
    toAddress = "1A1zP1eP5QGefi2DMPTfTL5SLmv7DivfNa",
    amount = "0.5"
)

// 提现参数
val withdrawParams = RouteParams.WithdrawParams(
    currency = "USDT",
    maxAmount = "1000.00",
    minAmount = "10.00"
)
```

## 测试

### 运行测试

```bash
# 运行所有导航测试
./gradlew test

# 运行特定测试类
./gradlew test --tests "com.cryptovpn.test.navigation.LaunchFlowTest"
```

### 测试覆盖

- 启动流程测试
- 认证流程测试
- VPN购买流程测试
- 钱包操作流程测试
- 增长提现流程测试
- 法务文档流程测试
- 返回栈管理测试
- 深层链接测试

## 最佳实践

### 1. 使用类型安全的导航

```kotlin
// 推荐
navigationManager.navigateToVpnHome()

// 不推荐
navigationManager.navigateTo("vpn_home")
```

### 2. 处理返回结果

```kotlin
// 设置返回结果
navigationManager.setResult("key", value)

// 获取返回结果
val result = navigationManager.getResult<String>("key")
```

### 3. 深层链接工厂方法

```kotlin
// 使用工厂方法创建深层链接
val uri = DeepLinkFactory.vpnHome(autoConnect = true)
val uri = DeepLinkFactory.orderDetail("order123")
val uri = DeepLinkFactory.send("BTC", toAddress = "...", amount = "1.0")
```

### 4. 返回栈清理

```kotlin
// 购买流程完成后清理栈
navigationManager.navigateToVpnHome()

// 或使用专门的清理方法
backStackManager.popToVpnHome()
```

## 依赖

```kotlin
// Navigation Compose
implementation("androidx.navigation:navigation-compose:2.7.7")

// Kotlin Coroutines (用于状态流)
implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
```

## 许可证

Copyright © 2024 CryptoVPN. All rights reserved.
