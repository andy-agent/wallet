# VPNUI 并入现有 V2rayNG 的组合方案

> **文档版本**: 1.0  
> **编写日期**: 2026-04-03  
> **依赖文档**: `docs/ANDROID_CURRENT_SCREEN_MAP.md`  
> **注**: `docs/VPNUI_DIRECTORY_DECISION.md` 尚未生成，本方案基于当前目录结构做草案，待 4j0.7 最终裁决后需同步更新。

---

## 1. 技术差异对比表

| 维度 | vpnui (新) | V2rayNG (现有) | 冲突等级 |
|------|-----------|----------------|---------|
| **包名** | `com.cryptovpn` | `com.v2ray.ang` | 🔴 高 |
| **UI 框架** | Jetpack Compose 100% | XML Layout + ViewBinding | 🔴 高 |
| **依赖注入** | Hilt (Dagger) | 无 DI，手动实例化 | 🟡 中 |
| **导航** | Navigation Compose (单 Activity) | 多 Activity + 原生 Fragment | 🔴 高 |
| **状态管理** | StateFlow + HiltViewModel | LiveData + AndroidViewModel | 🟡 中 |
| **构建配置** | 假设启用 Compose + Hilt | viewBinding=true，无 Compose | 🔴 高 |
| **主题系统** | Material3 深色主题强制 | AppCompat + 多主题 | 🟡 中 |
| **ViewModel 作用域** | `@HiltViewModel` + `viewModel()` | `by viewModels()` | 🟢 低 |

### 1.1 包名差异详细说明

```kotlin
// vpnui 包结构
package com.cryptovpn
package com.cryptovpn.ui.pages.vpn
package com.cryptovpn.ui.navigation

// V2rayNG 包结构  
package com.v2ray.ang
package com.v2ray.ang.ui
package com.v2ray.ang.viewmodel
```

**影响**: R 类引用、资源文件、Manifest 注册、深层链接全部不兼容。

### 1.2 Compose vs XML 详细说明

```kotlin
// vpnui: 纯 Compose
setContent {
    CryptoVPNTheme {
        AppNavigation(navController)
    }
}

// V2rayNG: XML + ViewBinding
val binding = ActivityMainBinding.inflate(layoutInflater)
setContentView(binding.root)
binding.viewPager.adapter = groupPagerAdapter
```

**影响**: 两套 UI 体系无法在同一页面混用，必须隔离。

### 1.3 DI 差异详细说明

```kotlin
// vpnui: Hilt
@HiltViewModel
class VPNHomeViewModel @Inject constructor(
    private val repository: VPNRepository
) : ViewModel()

// V2rayNG: 手动实例化
val paymentRepository = PaymentRepository(this)
```

**影响**: vpnui 的 ViewModel 需要 Hilt 环境才能创建。

### 1.4 导航差异详细说明

```kotlin
// vpnui: Navigation Compose
NavHost(navController, startDestination = AppRoutes.SPLASH) {
    composable(AppRoutes.VPN_HOME) { VPNHomePage(...) }
}

// V2rayNG: Activity 跳转
startActivity(Intent(this, PlansActivity::class.java))
pendingDestination = Intent(this, UserProfileActivity::class.java)
loginLauncher.launch(Intent(this, LoginActivity::class.java))
```

**影响**: 两套导航系统完全独立，需要桥接层。

---

## 2. 为什么不能直接替换现有 MainActivity

### 2.1 技术层面阻断

| 阻断点 | 说明 |
|-------|------|
| **VPN 服务生命周期** | V2rayNG 的 MainActivity 托管 V2RayService 绑定，替换会导致服务断开 |
| **MmkvManager 依赖** | 现有 MainActivity 直接操作 `MmkvManager` 管理服务器配置 |
| **Native 库加载** | libv2ray 等 native 库的初始化依赖现有 Activity 上下文 |
| **权限模型** | VPN 权限申请、通知权限与现有 Activity 紧耦合 |
| **侧边栏导航** | NavigationDrawer 包含支付模块入口，是现有业务的核心入口 |

### 2.2 业务层面阻断

| 阻断点 | 说明 |
|-------|------|
| **节点管理** | MainActivity 展示节点列表，用户已习惯此交互 |
| **订阅更新** | 订阅链接更新、节点测速功能在 MainActivity 实现 |
| **配置导入** | 扫码、剪贴板导入配置依赖现有 MainActivity 的处理链 |

### 2.3 包名冲突示例

```kotlin
// 如果强行替换，以下代码全部失效：
import com.v2ray.ang.R  // 资源找不到
import com.v2ray.ang.databinding.ActivityMainBinding  // ViewBinding 失效
import com.v2ray.ang.handler.V2RayServiceManager  // 包访问权限问题
```

---

## 3. 推荐接入方案

### 3.1 方案选型：ComposeContainerActivity（推荐）

```
┌─────────────────────────────────────────────────────────────┐
│                    现有 V2rayNG App                         │
│  ┌─────────────────────────────────────────────────────┐   │
│  │  MainActivity (保留)                               │   │
│  │  - 节点管理                                        │   │
│  │  - 订阅更新                                        │   │
│  │  - VPN 控制                                        │   │
│  └──────────────┬──────────────────────────────────────┘   │
│                 │ Intent 跳转                              │
│  ┌──────────────▼──────────────────────────────────────┐   │
│  │  ComposeContainerActivity (新增)                   │   │
│  │  - setContent { CryptoVPNTheme { ... } }           │   │
│  │  - 内嵌 Compose Navigation                         │   │
│  │  - 27个 vpnui 页面在此运行                          │   │
│  └─────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────┘
```

**不采用 ComposeContainerFragment 的原因**:
- V2rayNG 没有 Fragment 容器架构，引入 Fragment 改造成本更高
- Activity 级别隔离更清晰，便于权限管理和生命周期控制

### 3.2 页面接入优先级矩阵

| 优先级 | 页面 | 接入理由 | 风险 |
|-------|------|---------|------|
| **P0 (第一批)** | SplashScreen | 接管启动流程，品牌统一入口 | 低 |
| **P0** | ForceUpdatePage | 阻断低版本，业务安全 | 低 |
| **P0** | EmailLoginPage/Register | 现有登录页需拆分登录/注册 | 中 |
| **P1 (第二批)** | VPNHomePage (新) | PRD 核心页面，订阅驱动设计 | 高 |
| **P1** | PlansPage | 现有 PlansActivity 可并行保留 | 中 |
| **P1** | RegionSelectionPage | 区域模型需后端配合 | 中 |
| **P2 (第三批)** | OrderCheckoutPage | 现有 PaymentActivity 可桥接 | 中 |
| **P2** | OrderResultPage | 替代现有支付成功弹窗 | 低 |
| **P2** | OrderList/Detail | 现有 UserProfileActivity 内嵌订单 | 中 |
| **P3 (第四批)** | WalletOnboarding/Home | 全新模块，无历史包袱 | 低 |
| **P3** | AssetDetail/Send/Receive | 钱包完整功能 | 中 |
| **P4 (第五批)** | InviteCenter/Commission/Withdraw | 现有 XML 页面功能完整，延后迁移 | 低 |
| **P4** | ProfilePage | 统一"我的"入口 | 中 |
| **P4** | LegalDocuments | 法务合规页面 | 低 |

---

## 4. 分阶段并入步骤

### 阶段一：架构搭建 (Week 1)

**目标**: 让 Compose 页面能在 V2rayNG 中运行

```kotlin
// 1. build.gradle.kts 添加依赖
implementation("androidx.compose.ui:ui:1.6.0")
implementation("androidx.compose.material3:material3:1.2.0")
implementation("androidx.navigation:navigation-compose:2.7.6")
implementation("com.google.dagger:hilt-android:2.50")
kapt("com.google.dagger:hilt-compiler:2.50")
implementation("androidx.hilt:hilt-navigation-compose:1.1.0")

// 2. 新建 ComposeContainerActivity
package com.v2ray.ang.ui

class ComposeContainerActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val startRoute = intent.getStringExtra(EXTRA_START_ROUTE) ?: AppRoutes.SPLASH
        setContent {
            CryptoVPNTheme {
                AppNavigation(
                    navController = rememberNavController(),
                    startDestination = startRoute
                )
            }
        }
    }
}

// 3. Manifest 注册
<activity 
    android:name=".ui.ComposeContainerActivity"
    android:exported="false"
    android:theme="@style/AppTheme.NoActionBar" />
```

**验收标准**:
- [ ] ComposeContainerActivity 可正常启动
- [ ] SplashScreen 正常显示
- [ ] 主题颜色与 CryptoVPN 设计一致

### 阶段二：启动与认证 (Week 1-2)

**目标**: 接管 App 启动流程

```kotlin
// MainActivity.kt 修改
override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    
    // 新增：判断是否已展示过 Splash
    if (!hasShownSplash()) {
        startComposeContainer(AppRoutes.SPLASH)
        finish()
        return
    }
    
    // 原有逻辑...
}

// SplashScreen 修改
@Composable
fun SplashScreen(
    onNavigateToHome: () -> Unit = {
        // 跳转到原生 MainActivity
        context.startActivity(Intent(context, MainActivity::class.java))
        (context as? Activity)?.finish()
    },
    onNavigateToLogin: () -> Unit = { /* ... */ }
)
```

**页面清单**:
- SplashScreen → VPN_HOME (跳转到原生 MainActivity)
- ForceUpdatePage (阻断逻辑)
- EmailLoginPage (复用 PaymentRepository 登录)
- EmailRegisterPage (拆分现有登录页)
- ResetPasswordPage (新增)

**验收标准**:
- [ ] 冷启动显示 CryptoVPN Splash
- [ ] 版本检查正常
- [ ] 登录/注册流程完整
- [ ] Token 正常写入现有 PaymentRepository

### 阶段三：VPN 核心页面 (Week 2-3)

**目标**: 新 VPN 首页与现有节点管理共存

```kotlin
// 方案：双首页并行
// 入口 1: 原生 MainActivity (节点管理专业版)
// 入口 2: Compose VPNHomePage (订阅驱动简洁版)

// MainActivity 侧边栏新增入口
R.id.nav_vpn_home_compose -> {
    startComposeContainer(AppRoutes.VPN_HOME)
}
```

**页面清单**:
- VPNHomePage (Compose 版，订阅状态驱动)
- PlansPage (Compose 版，可替代现有 PlansActivity)
- RegionSelectionPage (新增区域模型)

**关键桥接代码**:
```kotlin
// VPNHomeViewModel 需要与现有服务通信
class VPNHomeViewModel @Inject constructor(
    private val v2rayServiceManager: V2RayServiceManager  // 桥接层
) : ViewModel() {
    fun toggleConnection() {
        // 调用现有服务
        v2rayServiceManager.startV2ray()
    }
}
```

**验收标准**:
- [ ] 订阅状态正常显示
- [ ] 连接/断开按钮调用现有服务
- [ ] 区域选择后配置正确下发

### 阶段四：订单与支付 (Week 3-4)

**目标**: 订单流程完整迁移

**页面清单**:
- OrderCheckoutPage (替代 PaymentActivity)
- WalletPaymentConfirmPage (新增钱包支付)
- OrderResultPage (替代成功弹窗)
- OrderListPage (独立页面)
- OrderDetailPage (独立页面)

**复用策略**:
```kotlin
// OrderCheckoutViewModel 复用现有 Repository
@HiltViewModel
class OrderCheckoutViewModel @Inject constructor(
    private val paymentRepository: PaymentRepository  // 现有类
) : ViewModel() {
    fun createOrder(planId: String) {
        viewModelScope.launch {
            // 调用现有 API
            paymentRepository.createOrder(planId)
        }
    }
}
```

**验收标准**:
- [ ] 创建订单 API 正常
- [ ] 支付地址展示正常
- [ ] 支付状态轮询正常
- [ ] 支付结果页跳转正常

### 阶段五：钱包模块 (Week 4-5)

**目标**: 全新模块接入

**页面清单**:
- WalletOnboardingPage
- WalletHomePage
- AssetDetailPage
- ReceivePage
- SendPage
- SendResultPage

**注意**: 钱包模块依赖 Solana/TRON SDK，需要独立验证。

### 阶段六：增长与设置 (Week 5-6)

**目标**: 剩余页面迁移

**页面清单**:
- InviteCenterPage (替换现有 InvitationCenterActivity)
- CommissionLedgerPage (替换现有 CommissionLedgerActivity)
- WithdrawPage (替换现有 WithdrawalActivity)
- ProfilePage (统一"我的"入口)
- LegalDocumentsList/Detail

---

## 5. 每阶段风险与缓解

| 阶段 | 风险 | 影响 | 缓解措施 |
|------|------|------|---------|
| **阶段一** | Hilt 与现有代码冲突 | 编译失败 | 在独立分支验证，逐步引入 |
| **阶段一** | Compose 依赖体积增加 | APK +2~3MB | 启用 R8 压缩，仅添加必要依赖 |
| **阶段二** | 启动流程改变 | 用户感知 | 保持原有加载速度，动画可配置关闭 |
| **阶段二** | Token 存储不兼容 | 登录态丢失 | 复用现有 PaymentRepository 存储逻辑 |
| **阶段三** | VPN 服务状态不同步 | 连接状态错乱 | 通过 BroadcastReceiver 监听服务状态 |
| **阶段三** | 区域模型与节点模型冲突 | 配置下发错误 | 后端配合，区域→节点映射层 |
| **阶段四** | 支付流程中断 | 收入损失 | 保留现有 PaymentActivity 作为 fallback |
| **阶段五** | 钱包 SDK 冲突 | 崩溃 | 独立测试环境验证，沙盒隔离 |
| **阶段六** | 邀请码分享失效 | 增长受损 | 分享 Intent 保持与现有逻辑一致 |

---

## 6. 明确禁止项

### 6.1 代码层面禁止

| 禁止项 | 原因 | 替代方案 |
|-------|------|---------|
| ❌ 直接修改 `com.c2ray.ang.ui.MainActivity` 包名 | 将导致整个 App 包结构混乱 | ✅ 保留 MainActivity，新增 ComposeContainerActivity |
| ❌ 删除现有 XML Layout 文件 | 仍有 Activity 依赖这些布局 | ✅ 保留，待对应页面完全迁移后再删除 |
| ❌ 修改 `PaymentRepository` 构造函数 | 已有多个 Activity 依赖 | ✅ 使用 Adapter 模式桥接 |
| ❌ 在 vpnui 页面直接引用 `com.v2ray.ang.R` | 包名不同，编译失败 | ✅ 通过桥接层传递必要参数 |
| ❌ 强制关闭现有 VPN 服务重启 | 用户体验差，可能触发系统限制 | ✅ 复用现有服务连接 |
| ❌ 同时展示原生和 Compose 两个首页 | 用户困惑，维护成本高 | ✅ 通过配置或 A/B 测试切换 |

### 6.2 流程层面禁止

| 禁止项 | 原因 |
|-------|------|
| ❌ 跳过单元测试直接合入 | 支付流程高风险 |
| ❌ 在没有 fallback 的情况下全量替换 | 一旦崩溃无法回退 |
| ❌ 修改现有数据库 Schema | 升级用户数据丢失风险 |
| ❌ 在阶段一就接入钱包功能 | 依赖过多，问题难以定位 |

---

## 7. 后续 Kimi 实施任务拆分建议

### 7.1 beads 任务建议结构

```
app-compose-migration/
├── phase1-architecture/
│   ├── add-compose-dependencies        # P0
│   ├── create-compose-container        # P0
│   ├── setup-cryptovpn-theme           # P0
│   └── bridge-payment-repository       # P0
├── phase2-launch-auth/
│   ├── integrate-splash-screen         # P0
│   ├── integrate-force-update          # P0
│   ├── migrate-login-page              # P0
│   ├── migrate-register-page           # P0
│   └── migrate-reset-password          # P1
├── phase3-vpn-core/
│   ├── migrate-vpn-home-page           # P0
│   ├── migrate-plans-page              # P1
│   ├── migrate-region-selection        # P1
│   └── bridge-vpn-service              # P0
├── phase4-order-payment/
│   ├── migrate-order-checkout          # P1
│   ├── migrate-order-result            # P1
│   ├── migrate-order-list              # P2
│   └── migrate-order-detail            # P2
├── phase5-wallet/
│   ├── integrate-wallet-onboarding     # P2
│   ├── integrate-wallet-home           # P2
│   ├── integrate-send-receive          # P3
│   └── integrate-asset-detail          # P3
└── phase6-growth-profile/
    ├── migrate-invite-center           # P3
    ├── migrate-commission-ledger       # P3
    ├── migrate-withdraw                # P3
    ├── migrate-profile                 # P3
    └── migrate-legal-documents         # P4
```

### 7.2 每个 beads 任务应包含

1. **前置条件**: 依赖的 beads ID
2. **验收标准**: 具体可验证的条目
3. **回滚方案**: 如何快速回退
4. **测试建议**: 需覆盖的场景

### 7.3 关键 beads 依赖链

```
add-compose-dependencies
    ↓
create-compose-container
    ↓
bridge-payment-repository
    ↓
integrate-splash-screen → migrate-login-page
                              ↓
                    migrate-vpn-home-page ← bridge-vpn-service
                              ↓
                    migrate-order-checkout
```

### 7.4 建议的 beads 元数据

```yaml
# 示例：migrate-vpn-home-page
id: app-compose-migration/phase3-vpn-core/migrate-vpn-home-page
title: 迁移 VPN 首页到 Compose
priority: P0
dependencies:
  - create-compose-container
  - bridge-vpn-service
  - bridge-payment-repository
acceptance_criteria:
  - VPNHomePage 正常渲染
  - 订阅状态与现有 API 同步
  - 连接按钮调用 V2RayServiceManager
  - 返回键行为正确
rollback_plan: 保留原生 MainActivity 作为默认入口，通过配置开关切换
test_scope:
  - 连接/断开流程
  - 订阅状态变更
  - 区域选择跳转
```

---

## 8. 附录

### 8.1 关键文件映射

| vpnui 文件 | V2rayNG 对应位置 | 说明 |
|-----------|-----------------|------|
| `MainActivity.kt` | `MainActivity.kt` | 不替换，参考实现 |
| `ui/navigation/AppNavigation.kt` | 无 | 全新引入 |
| `ui/pages/vpn/VPNHomePage.kt` | `ui/MainActivity.kt` | 功能替代 |
| `ui/pages/auth/*` | `payment/ui/activity/LoginActivity.kt` | 功能替代 |
| `ui/theme/*` | `res/values/themes.xml` | 主题覆盖 |

### 8.2 依赖版本参考

```kotlin
// build.gradle.kts (app) 需要添加
composeOptions {
    kotlinCompilerExtensionVersion = "1.5.8"
}

dependencies {
    // Compose BOM
    val composeBom = platform("androidx.compose:compose-bom:2024.02.00")
    implementation(composeBom)
    androidTestImplementation(composeBom)
    
    // Core Compose
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.ui:ui-tooling-preview")
    debugImplementation("androidx.compose.ui:ui-tooling")
    
    // Navigation
    implementation("androidx.navigation:navigation-compose:2.7.7")
    
    // Hilt
    implementation("com.google.dagger:hilt-android:2.50")
    kapt("com.google.dagger:hilt-compiler:2.50")
    implementation("androidx.hilt:hilt-navigation-compose:1.1.0")
    
    // ViewModel
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.7.0")
}
```

### 8.3 参考文档

- `docs/ANDROID_CURRENT_SCREEN_MAP.md` - 现有代码映射
- `docs/ANDROID_APP_UI_REQUIREMENTS_FOR_AI.md` - 业务需求
- `vpnui/PROJECT_SUMMARY.md` - vpnui 实现总结
- `vpnui/README.md` - vpnui 技术栈说明

---

**文档维护**: 本方案需在 `VPNUI_DIRECTORY_DECISION.md` 最终裁决后同步更新目录相关部分。
