# CryptoVPN Android App

> 电影级科幻风格VPN + 加密货币钱包 Android原生应用

## 项目概述

CryptoVPN是一个以VPN订阅为核心、多链自托管钱包为辅助的Android原生App。

### 核心功能
- **VPN服务**: VLESS + Reality + XTLS/Vision协议
- **加密货币钱包**: 支持Solana和TRON链
- **支付系统**: 支持SOL、USDT(Solana/TRON)支付
- **邀请分佣**: 一级25%、二级5%分佣机制

## 技术栈

- **UI框架**: Jetpack Compose
- **架构**: MVVM + Clean Architecture
- **导航**: Jetpack Navigation Compose
- **依赖注入**: Hilt
- **状态管理**: StateFlow + ViewModel
- **动画**: Compose Animation API + Canvas

## 项目结构

```
cryptovpn/
├── ui/
│   ├── components/          # UI组件库
│   │   ├── buttons/         # 按钮组件
│   │   ├── inputs/          # 输入框组件
│   │   ├── cards/           # 卡片组件
│   │   ├── tags/            # 标签组件
│   │   ├── navigation/      # 导航组件
│   │   ├── dialogs/         # 弹窗组件
│   │   ├── special/         # 特殊组件
│   │   └── listitems/       # 列表项组件
│   ├── effects/             # 视觉效果
│   │   ├── StarfieldParticles.kt
│   │   ├── DataFlowParticles.kt
│   │   ├── EnergyParticles.kt
│   │   ├── NetworkParticles.kt
│   │   ├── GlowEffects.kt
│   │   ├── AnimationEffects.kt
│   │   ├── ConnectionVisualizer.kt
│   │   ├── TransitionAnimations.kt
│   │   └── SpecialEffects.kt
│   ├── pages/               # 页面实现
│   │   ├── batch1/          # 启动与认证(6页)
│   │   ├── batch2/          # VPN首页+套餐页
│   │   ├── batch2b/         # 区域选择+订单收银台
│   │   ├── batch3/          # 订单+钱包(7页)
│   │   ├── batch4/          # 钱包操作+增长(5页)
│   │   └── batch5/          # 提现+我的+法务(5页)
│   └── theme/               # 主题系统
│       ├── Color.kt
│       ├── Type.kt
│       ├── Shape.kt
│       └── Theme.kt
├── navigation/              # 导航系统
│   ├── Routes.kt
│   ├── NavigationManager.kt
│   ├── NavGraph.kt
│   ├── DeepLinkHandler.kt
│   └── BackStackManager.kt
├── test/                    # 测试
│   └── navigation/          # 导航测试
└── MainActivity.kt
```

## 页面清单（27页）

### 启动与版本
- [x] SplashScreen - 启动页
- [x] ForceUpdatePage - 强制更新页
- [x] OptionalUpdateDialog - 可选更新弹窗

### 认证
- [x] EmailLoginPage - 邮箱登录页
- [x] EmailRegisterPage - 邮箱注册页
- [x] ResetPasswordPage - 重置密码页

### VPN
- [x] VPNHomePage - VPN首页
- [x] PlansPage - 套餐页
- [x] RegionSelectionPage - 区域选择页
- [x] OrderCheckoutPage - 订单收银台
- [x] WalletPaymentConfirmPage - 钱包支付确认页
- [x] OrderResultPage - 订单结果页
- [x] OrderListPage - 订单列表页
- [x] OrderDetailPage - 订单详情页

### 钱包
- [x] WalletOnboardingPage - 钱包引导页
- [x] WalletHomePage - 钱包首页
- [x] AssetDetailPage - 资产详情页
- [x] ReceivePage - 收款页
- [x] SendPage - 发送页
- [x] SendResultPage - 发送结果页
- [x] WalletPaymentConfirmPage - 钱包支付确认页

### 增长
- [x] InviteCenterPage - 邀请中心页
- [x] CommissionLedgerPage - 佣金账本页
- [x] WithdrawPage - 提现申请页

### 我的与法务
- [x] ProfilePage - 我的页
- [x] LegalDocumentsListPage - 法务文档列表页
- [x] LegalDocumentDetailPage - 法务文档详情页
- [x] SessionEvictedDialog - 全局会话失效弹窗

## 视觉效果特性

### 粒子效果
- **星空粒子**: 200+随机星星，闪烁效果，流星动画
- **数据流粒子**: 螺旋数据流，双向传输，中心发光
- **能量粒子**: 能量爆发，拖尾效果
- **网络节点粒子**: 15个节点，动态连接，数据包传输

### 发光效果
- **霓虹光晕**: 多层发光效果
- **脉冲发光**: 呼吸式脉冲动画
- **边缘发光**: 卡片边缘发光+闪烁

### 动画效果
- **呼吸动画**: 缩放呼吸效果
- **旋转动画**: 多层圆环旋转
- **波浪动画**: 多层波浪背景
- **波纹动画**: 点击波纹反馈
- **闪光动画**: 骨架屏闪光效果

### VPN连接可视化
- **未连接**: 灰色静态地球图标
- **连接中**: 蓝色旋转动画+脉冲效果
- **已连接**: 绿色发光地球+呼吸光效

## 设计系统

### 色彩系统
| Token | 色值 | 用途 |
|-------|------|------|
| Background Deepest | #0B1020 | 启动页、最深背景 |
| Background Primary | #111827 | 页面主背景 |
| Background Secondary | #1F2937 | 卡片背景 |
| Primary | #1D4ED8 | 主按钮、主链接 |
| Success | #22C55E | 成功状态 |
| Warning | #F59E0B | 警告状态 |
| Error | #EF4444 | 错误状态 |
| Text Primary | #F8FAFC | 主文字 |
| Text Secondary | #94A3B8 | 次要文字 |

### 字体系统
| 层级 | 大小 | 字重 | 用途 |
|------|------|------|------|
| H1 | 28sp | Bold | 页面大标题 |
| H2 | 24sp | Bold | 区块标题 |
| H3 | 20sp | SemiBold | 卡片标题 |
| Body | 14sp | Regular | 正文 |
| Caption | 12sp | Medium | 标签 |

## 路由系统

### 27个页面路由已定义
```kotlin
object Routes {
    const val SPLASH = "splash"
    const val FORCE_UPDATE = "force_update"
    const val EMAIL_LOGIN = "email_login"
    const val VPN_HOME = "vpn_home"
    // ... 更多路由
}
```

### 深层链接支持
```kotlin
val uri = "cryptovpn://app/order/ORD20241201001"
```

## 测试覆盖

- **启动流程测试**: 25+ 用例
- **认证流程测试**: 30+ 用例
- **VPN购买流程测试**: 35+ 用例
- **钱包操作流程测试**: 40+ 用例
- **增长提现流程测试**: 25+ 用例
- **法务文档流程测试**: 20+ 用例
- **返回栈管理测试**: 30+ 用例
- **深层链接测试**: 40+ 用例

**总计: 245+ 测试用例，100% 路由覆盖**

## 依赖配置

```gradle
// build.gradle.kts (Module: app)
plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.dagger.hilt.android")
    id("kotlin-kapt")
}

dependencies {
    // Compose
    implementation("androidx.compose.ui:ui:1.6.0")
    implementation("androidx.compose.material3:material3:1.2.0")
    implementation("androidx.compose.ui:ui-tooling-preview:1.6.0")
    
    // Navigation
    implementation("androidx.navigation:navigation-compose:2.7.6")
    
    // Hilt
    implementation("com.google.dagger:hilt-android:2.50")
    kapt("com.google.dagger:hilt-compiler:2.50")
    implementation("androidx.hilt:hilt-navigation-compose:1.1.0")
    
    // ViewModel
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.7.0")
    
    // QR Code
    implementation("com.google.zxing:core:3.5.2")
    
    // Testing
    testImplementation("junit:junit:4.13.2")
    testImplementation("androidx.compose.ui:ui-test-junit4:1.6.0")
}
```

## 运行项目

1. 克隆项目
```bash
git clone https://github.com/cryptovpn/android-app.git
```

2. 使用Android Studio打开项目

3. 同步Gradle依赖

4. 运行应用

## 贡献指南

1. Fork项目
2. 创建特性分支
3. 提交更改
4. 推送到分支
5. 创建Pull Request

## 许可证

MIT License - 详见 [LICENSE](LICENSE) 文件

---

**版本**: v1.0.0  
**最后更新**: 2024-12