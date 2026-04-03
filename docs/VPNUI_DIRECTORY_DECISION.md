# VPNUI 唯一页面目录裁决表

> **文档用途**: 明确 `vpnui/ui/pages` 目录中哪些目录保留为最终并入源，哪些目录只作参考，哪些目录逻辑上废弃。  
> **决策日期**: 2026-04-03  
> **决策者**: AI Agent (liaojiang-4j0.7)  
> **来源优先级**: `vpnui/ui/pages/**` > `vpnui/navigation/**` > `docs/ANDROID_APP_UI_REQUIREMENTS_FOR_AI.md` > `docs/ANDROID_CURRENT_SCREEN_MAP.md`

---

## 1. 目录结构总览

```
vpnui/ui/pages/
├── batch1/          # [历史批次] 启动 + 认证
├── batch2/          # [历史批次] VPN首页 + 套餐
├── batch2b/         # [历史批次] 订单收银台 + 区域选择
├── batch3/          # [历史批次] 钱包引导/首页 + 订单相关
├── batch4/          # [历史批次] 增长 + 钱包收发
├── batch5/          # [历史批次] 法务 + 我的 + 会话失效
├── splash/          # [业务域] 启动相关
├── auth/            # [业务域] 认证相关
├── vpn/             # [业务域] VPN核心流程
├── wallet/          # [业务域] 钱包功能
├── growth/          # [业务域] 邀请与提现
├── profile/         # [业务域] 个人中心
└── legal/           # [业务域] 法务文档
```

---

## 2. 页面全量盘点与重复关系

### 2.1 完整页面清单（按业务域组织）

| 序号 | 页面名称 | PRD来源 | batch*位置 | 业务域位置 | 状态 |
|------|---------|---------|-----------|-----------|------|
| 1 | SplashScreen (启动页) | 10.1 | batch1/ | splash/ | **重复** |
| 2 | ForceUpdatePage (强更页) | 10.2 | batch1/ | splash/ | **重复** |
| 3 | OptionalUpdateDialog (可选更新弹窗) | 10.3 | batch1/ | splash/ | **重复** |
| 4 | EmailLoginPage (邮箱登录) | 10.4 | batch1/ | auth/ | **重复** |
| 5 | EmailRegisterPage (邮箱注册) | 10.5 | batch1/ | auth/ | **重复** |
| 6 | ResetPasswordPage (重置密码) | 10.6 | batch1/ | auth/ | **重复** |
| 7 | VPNHomePage (VPN首页) | 10.7 | batch2/ | vpn/ | **重复** |
| 8 | PlansPage (套餐页) | 10.8 | batch2/ | vpn/ | **重复** |
| 9 | RegionSelectionPage (区域选择) | 10.9 | batch2b/ | vpn/ | **重复** |
| 10 | OrderCheckoutPage (订单收银台) | 10.10 | batch2b/ | vpn/ | **重复** |
| 11 | WalletPaymentConfirmPage (钱包支付确认) | 10.11 | batch3/ | vpn/ | **重复** |
| 12 | OrderResultPage (订单结果) | 10.12 | batch3/ | vpn/ | **重复** |
| 13 | OrderListPage (订单列表) | 10.13 | batch3/ | vpn/ | **重复** |
| 14 | OrderDetailPage (订单详情) | 10.14 | batch3/ | vpn/ | **重复** |
| 15 | WalletOnboardingPage (钱包引导) | 10.15 | batch3/ | wallet/ | **重复** |
| 16 | WalletHomePage (钱包首页) | 10.16 | batch3/ | wallet/ | **重复** |
| 17 | AssetDetailPage (资产详情) | 10.17 | batch3/ | wallet/ | **重复** |
| 18 | ReceivePage (收款页) | 10.18 | batch4/ | wallet/ | **重复** |
| 19 | SendPage (发送页) | 10.19 | batch4/ | wallet/ | **重复** |
| 20 | SendResultPage (发送结果) | 10.20 | batch4/ | wallet/ | **重复** |
| 21 | InviteCenterPage (邀请中心) | 10.21 | batch4/ | growth/ | **重复** |
| 22 | CommissionLedgerPage (佣金账本) | 10.22 | batch4/ | growth/ | **重复** |
| 23 | WithdrawPage (提现申请) | 10.23 | batch4/ + batch5/ | growth/ | **重复** |
| 24 | ProfilePage (我的页) | 10.24 | batch5/ | profile/ | **重复** |
| 25 | LegalDocumentsListPage (法务列表) | 10.25 | batch5/ | legal/ | **重复** |
| 26 | LegalDocumentDetailPage (法务详情) | 10.26 | batch5/ | legal/ | **重复** |
| 27 | SessionEvictedDialog (会话失效弹窗) | 10.27 | batch5/ | legal/ | **重复** |

### 2.2 重复页面详细映射

```
batch1/                          业务域目录/
├── SplashScreen.kt      ──→   splash/SplashScreen.kt
├── ForceUpdatePage.kt   ──→   splash/ForceUpdatePage.kt
├── OptionalUpdateDialog.kt ──→ splash/OptionalUpdateDialog.kt
├── EmailLoginPage.kt    ──→   auth/EmailLoginPage.kt
├── EmailRegisterPage.kt ──→   auth/EmailRegisterPage.kt
└── ResetPasswordPage.kt ──→   auth/ResetPasswordPage.kt

batch2/
├── VPNHomePage.kt       ──→   vpn/VPNHomePage.kt
├── VPNHomeViewModel.kt  ──→   [整合到页面文件]
├── PlansPage.kt         ──→   vpn/PlansPage.kt
└── PlansViewModel.kt    ──→   [整合到页面文件]

batch2b/
├── OrderCheckoutPage.kt ──→   vpn/OrderCheckoutPage.kt
└── RegionSelectionPage.kt ──→ vpn/RegionSelectionPage.kt

batch3/
├── WalletOnboardingPage.kt    ──→   wallet/WalletOnboardingPage.kt
├── WalletHomePage.kt          ──→   wallet/WalletHomePage.kt
├── AssetDetailPage.kt         ──→   wallet/AssetDetailPage.kt
├── OrderListPage.kt           ──→   vpn/OrderListPage.kt
├── OrderDetailPage.kt         ──→   vpn/OrderDetailPage.kt
├── OrderResultPage.kt         ──→   vpn/OrderResultPage.kt
├── WalletPaymentConfirmPage.kt ──→  vpn/WalletPaymentConfirmPage.kt
└── Batch3ViewModels.kt        ──→   [分散到各页面]

batch4/
├── InviteCenterPage.kt        ──→   growth/InviteCenterPage.kt
├── CommissionLedgerPage.kt    ──→   growth/CommissionLedgerPage.kt
├── WithdrawPage.kt            ──→   growth/WithdrawPage.kt
├── SendPage.kt                ──→   wallet/SendPage.kt
├── SendResultPage.kt          ──→   wallet/SendResultPage.kt
├── ReceivePage.kt             ──→   wallet/ReceivePage.kt
└── *ViewModel.kt              ──→   [整合到页面或单独文件]

batch5/
├── ProfilePage.kt             ──→   profile/ProfilePage.kt
├── LegalDocumentsListPage.kt  ──→   legal/LegalDocumentsListPage.kt
├── LegalDocumentDetailPage.kt ──→   legal/LegalDocumentDetailPage.kt
├── SessionEvictedDialog.kt    ──→   legal/SessionEvictedDialog.kt
├── WithdrawPage.kt            ──→   [与batch4重复，growth/WithdrawPage.kt为准]
└── *ViewModel.kt              ──→   [整合到页面或单独文件]
```

---

## 3. 目录裁决

### 3.1 Final Source of Truth (最终并入源)

以下目录**保留**作为最终并入 Android 工程的源代码：

| 目录 | 包含页面数 | 裁决理由 |
|------|-----------|---------|
| `splash/` | 3 | 按功能域组织，使用 HiltViewModel 架构，符合 PRD 最新设计 |
| `auth/` | 3 | 按功能域组织，使用 HiltViewModel 架构，符合 PRD 最新设计 |
| `vpn/` | 8 | 按功能域组织，使用 HiltViewModel 架构，符合 PRD 最新设计 |
| `wallet/` | 7 | 按功能域组织，使用 HiltViewModel 架构，符合 PRD 最新设计 |
| `growth/` | 3 | 按功能域组织，使用 HiltViewModel 架构，符合 PRD 最新设计 |
| `profile/` | 1 | 按功能域组织，使用 HiltViewModel 架构，符合 PRD 最新设计 |
| `legal/` | 3 | 按功能域组织，使用 HiltViewModel 架构，符合 PRD 最新设计 |

**保留目录的共同特征**：
- 使用 `@HiltViewModel` 进行依赖注入
- 使用 MaterialTheme 3 设计系统
- 包名按功能域划分（如 `com.cryptovpn.ui.pages.auth`）
- 与 `navigation/` 路由定义保持一致

### 3.2 Reference Only (仅作参考)

以下目录**仅作参考**，不直接用于并入：

| 目录 | 包含页面数 | 参考用途 |
|------|-----------|---------|
| `batch2/` | 4 | ViewModel 分离实现方式参考，但业务域目录已整合 ViewModel |
| `batch2b/` | 2 | 早期订单流程设计参考 |
| `batch3/` | 9 | 钱包模块早期设计参考 |
| `batch4/` | 8 | 增长模块早期设计参考，含 README.md 有参考价值 |
| `batch5/` | 9 | 法务模块早期设计参考，WithdrawPage 与 batch4 重复 |

**参考用途说明**：
- 查看早期实现思路和状态管理方案
- 对比 ViewModel 分离 vs 整合的实现差异
- batch4/README.md 包含设计决策记录，有文档价值

### 3.3 Logically Deprecated (逻辑废弃)

以下目录**逻辑废弃**，不建议继续使用：

| 目录 | 包含页面数 | 废弃理由 |
|------|-----------|---------|
| `batch1/` | 6 | 所有页面已在 `splash/` 和 `auth/` 中重新实现，且新实现使用 HiltViewModel 架构 |

**废弃依据**：
- 与业务域目录内容完全重复
- 使用旧的 theme import（`CryptoVPNTheme`、`PrimaryBlue` 等）
- 未使用 Hilt 依赖注入
- ViewModel 实现方式落后

---

## 4. 为什么 batch1~5 不能继续作为开发目录

### 4.1 架构层面原因

| 问题 | batch* 目录 | 业务域目录 |
|------|------------|-----------|
| 依赖注入 | ❌ 未使用 Hilt | ✅ 使用 `@HiltViewModel` |
| 主题系统 | ❌ 使用旧 theme（CryptoVPNTheme） | ✅ 使用 MaterialTheme 3 |
| 包结构 | ❌ 扁平（com.cryptovpn.ui.pages） | ✅ 按域划分（com.cryptovpn.ui.pages.auth） |
| ViewModel | ❌ 手动创建或分离文件 | ✅ Hilt 注入，部分整合到页面 |

### 4.2 维护层面原因

1. **重复代码**: batch* 与业务域目录存在 27 个页面的完全重复
2. **导航不一致**: batch* 使用旧的路由参数传递方式
3. **状态管理混乱**: batch1 使用 `mutableStateOf`，batch3+ 使用 StateFlow，但实现方式各异
4. **缺乏统一规范**: 各 batch 开发时间不同，代码风格不一致

### 4.3 业务层面原因

1. **PRD 对齐**: 业务域目录更贴近 `ANDROID_APP_UI_REQUIREMENTS_FOR_AI.md` 的最新定义
2. **路由映射**: `navigation/Routes.kt` 定义的路由与业务域目录结构一致
3. **后续拆工**: beads 拆工需要清晰的目录边界，batch* 会造成混淆

---

## 5. 最终建议保留的唯一目录树

```
vpnui/
├── ui/
│   ├── pages/                    # 页面目录（最终并入源）
│   │   ├── splash/               # 启动与版本
│   │   │   ├── SplashScreen.kt
│   │   │   ├── ForceUpdatePage.kt
│   │   │   └── OptionalUpdateDialog.kt
│   │   ├── auth/                 # 认证
│   │   │   ├── EmailLoginPage.kt
│   │   │   ├── EmailRegisterPage.kt
│   │   │   └── ResetPasswordPage.kt
│   │   ├── vpn/                  # VPN核心
│   │   │   ├── VPNHomePage.kt
│   │   │   ├── PlansPage.kt
│   │   │   ├── RegionSelectionPage.kt
│   │   │   ├── OrderCheckoutPage.kt
│   │   │   ├── WalletPaymentConfirmPage.kt
│   │   │   ├── OrderResultPage.kt
│   │   │   ├── OrderListPage.kt
│   │   │   └── OrderDetailPage.kt
│   │   ├── wallet/               # 钱包
│   │   │   ├── WalletOnboardingPage.kt
│   │   │   ├── WalletHomePage.kt
│   │   │   ├── AssetDetailPage.kt
│   │   │   ├── ReceivePage.kt
│   │   │   ├── SendPage.kt
│   │   │   ├── SendResultPage.kt
│   │   │   └── WalletPaymentConfirmPage.kt
│   │   ├── growth/               # 增长与提现
│   │   │   ├── InviteCenterPage.kt
│   │   │   ├── CommissionLedgerPage.kt
│   │   │   └── WithdrawPage.kt
│   │   ├── profile/              # 个人中心
│   │   │   └── ProfilePage.kt
│   │   └── legal/                # 法务
│   │       ├── LegalDocumentsListPage.kt
│   │       ├── LegalDocumentDetailPage.kt
│   │       └── SessionEvictedDialog.kt
│   ├── components/               # 可复用组件
│   ├── theme/                    # 主题配置
│   └── effects/                  # 动画效果
├── navigation/                   # 导航路由系统
│   ├── Routes.kt                 # 路由常量
│   ├── NavGraph.kt              # 导航图
│   ├── NavigationManager.kt     # 导航管理器
│   ├── DeepLinkHandler.kt       # 深层链接
│   └── BackStackManager.kt      # 返回栈管理
└── test/                         # 测试代码
```

### 5.1 页面与 PRD 对照表

| PRD章节 | 页面名称 | 所在目录 | 文件路径 |
|---------|---------|---------|---------|
| 10.1 | 启动页 | splash/ | `ui/pages/splash/SplashScreen.kt` |
| 10.2 | 强更页 | splash/ | `ui/pages/splash/ForceUpdatePage.kt` |
| 10.3 | 可选更新弹窗 | splash/ | `ui/pages/splash/OptionalUpdateDialog.kt` |
| 10.4 | 邮箱登录页 | auth/ | `ui/pages/auth/EmailLoginPage.kt` |
| 10.5 | 邮箱注册页 | auth/ | `ui/pages/auth/EmailRegisterPage.kt` |
| 10.6 | 重置密码页 | auth/ | `ui/pages/auth/ResetPasswordPage.kt` |
| 10.7 | VPN首页 | vpn/ | `ui/pages/vpn/VPNHomePage.kt` |
| 10.8 | 套餐页 | vpn/ | `ui/pages/vpn/PlansPage.kt` |
| 10.9 | 区域选择页 | vpn/ | `ui/pages/vpn/RegionSelectionPage.kt` |
| 10.10 | 订单收银台 | vpn/ | `ui/pages/vpn/OrderCheckoutPage.kt` |
| 10.11 | 钱包支付确认页 | vpn/ | `ui/pages/vpn/WalletPaymentConfirmPage.kt` |
| 10.12 | 订单结果页 | vpn/ | `ui/pages/vpn/OrderResultPage.kt` |
| 10.13 | 我的订单列表页 | vpn/ | `ui/pages/vpn/OrderListPage.kt` |
| 10.14 | 订单详情页 | vpn/ | `ui/pages/vpn/OrderDetailPage.kt` |
| 10.15 | 钱包引导页 | wallet/ | `ui/pages/wallet/WalletOnboardingPage.kt` |
| 10.16 | 钱包首页 | wallet/ | `ui/pages/wallet/WalletHomePage.kt` |
| 10.17 | 资产详情页 | wallet/ | `ui/pages/wallet/AssetDetailPage.kt` |
| 10.18 | 收款页 | wallet/ | `ui/pages/wallet/ReceivePage.kt` |
| 10.19 | 发送页 | wallet/ | `ui/pages/wallet/SendPage.kt` |
| 10.20 | 发送结果页 | wallet/ | `ui/pages/wallet/SendResultPage.kt` |
| 10.21 | 邀请中心页 | growth/ | `ui/pages/growth/InviteCenterPage.kt` |
| 10.22 | 佣金账本页 | growth/ | `ui/pages/growth/CommissionLedgerPage.kt` |
| 10.23 | 提现申请页 | growth/ | `ui/pages/growth/WithdrawPage.kt` |
| 10.24 | 我的页/设置 | profile/ | `ui/pages/profile/ProfilePage.kt` |
| 10.25 | 法务文档列表页 | legal/ | `ui/pages/legal/LegalDocumentsListPage.kt` |
| 10.26 | 法务文档详情页 | legal/ | `ui/pages/legal/LegalDocumentDetailPage.kt` |
| 10.27 | 全局会话失效弹窗 | legal/ | `ui/pages/legal/SessionEvictedDialog.kt` |

---

## 6. 后续并入现有 Android 工程时的使用规则

### 6.1 并入范围

**并入目录** (按优先级排序):
1. `vpnui/ui/pages/` 下的 7 个业务域目录
2. `vpnui/navigation/` 导航路由系统
3. `vpnui/ui/components/` 可复用组件
4. `vpnui/ui/theme/` 主题配置

**不并入目录**:
- `vpnui/ui/pages/batch1~5/` - 逻辑废弃，仅本地保留作参考
- `vpnui/test/` - 需根据 Android 工程测试结构重新组织

### 6.2 文件映射规则

将 vpnui 页面文件映射到 Android 工程路径:

```
vpnui/ui/pages/{domain}/          →   code/Android/V2rayNG/app/src/main/java/com/cryptovpn/ui/pages/{domain}/
vpnui/navigation/                 →   code/Android/V2rayNG/app/src/main/java/com/cryptovpn/navigation/
vpnui/ui/components/              →   code/Android/V2rayNG/app/src/main/java/com/cryptovpn/ui/components/
vpnui/ui/theme/                   →   code/Android/V2rayNG/app/src/main/java/com/cryptovpn/ui/theme/
```

### 6.3 依赖接入规则

**必须添加的依赖**:
```kotlin
// Navigation Compose
implementation("androidx.navigation:navigation-compose:2.7.7")

// Hilt for Compose
implementation("androidx.hilt:hilt-navigation-compose:1.1.0")

// Material 3
implementation("androidx.compose.material3:material3:1.2.0")
```

**与现有代码的集成点**:
1. 在 `MainActivity.kt` 中替换原有 XML Layout 为 Compose Navigation
2. 保留 `PaymentRepository` 等已有 Repository 层
3. 保留 `PaymentApi` 等已有 API 接口
4. 废弃 `activity_*.xml` Layout 文件

### 6.4 beads 拆工输入规范

当使用此文档作为 beads 拆工输入时:

1. **任务粒度**: 按业务域目录拆分为独立任务
   - `vpnui-splash-pages`: SplashScreen + ForceUpdate + OptionalUpdate
   - `vpnui-auth-pages`: Login + Register + ResetPassword
   - `vpnui-vpn-pages`: VPNHome + Plans + Region + Orders
   - `vpnui-wallet-pages`: WalletOnboarding + WalletHome + Asset + Send/Receive
   - `vpnui-growth-pages`: Invite + Commission + Withdraw
   - `vpnui-profile-pages`: Profile
   - `vpnui-legal-pages`: LegalDocs + SessionEvicted

2. **验收标准**: 每个任务需确认
   - 页面使用业务域目录下的文件
   - 不引用 batch* 目录文件
   - 与 `navigation/Routes.kt` 路由定义对齐

3. **禁止事项**:
   - ❌ 不要将 batch* 文件复制到 Android 工程
   - ❌ 不要混合使用 batch* 和业务域目录文件
   - ❌ 不要修改业务域目录的包结构

### 6.5 版本控制建议

```bash
# 并入前清理
git rm -r vpnui/ui/pages/batch1/
git rm -r vpnui/ui/pages/batch2/
git rm -r vpnui/ui/pages/batch2b/
git rm -r vpnui/ui/pages/batch3/
git rm -r vpnui/ui/pages/batch4/
git rm -r vpnui/ui/pages/batch5/

# 保留最终目录
git add vpnui/ui/pages/splash/
git add vpnui/ui/pages/auth/
git add vpnui/ui/pages/vpn/
git add vpnui/ui/pages/wallet/
git add vpnui/ui/pages/growth/
git add vpnui/ui/pages/profile/
git add vpnui/ui/pages/legal/
```

---

## 7. 附录

### 7.1 文档引用关系

```
VPNUI_DIRECTORY_DECISION.md (本文件)
    ├── sources:
    │   ├── vpnui/ui/pages/** (目录盘点)
    │   ├── vpnui/navigation/Routes.kt (路由定义)
    │   ├── docs/ANDROID_APP_UI_REQUIREMENTS_FOR_AI.md (PRD)
    │   └── docs/ANDROID_CURRENT_SCREEN_MAP.md (现状映射)
    └── outputs:
        └── beads 拆工任务定义
```

### 7.2 变更记录

| 版本 | 日期 | 变更内容 |
|------|------|---------|
| 1.0 | 2026-04-03 | 初始裁决表，完成目录盘点与裁决 |

### 7.3 关键术语

- **batch***: 按开发批次组织的临时目录，现已 deprecated
- **业务域目录**: 按功能域（splash/auth/vpn/wallet/growth/profile/legal）组织的正式目录
- **HiltViewModel**: 使用 Dagger Hilt 进行依赖注入的 ViewModel 模式
- **beads 拆工**: 基于 beads issue tracker 的任务拆分
