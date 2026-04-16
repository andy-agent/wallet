# Android UI 第一阶段视觉组件规范与执行清单

> 本文是第一阶段执行清单，不再承担“目标视觉系统”定义本体。
> 目标视觉系统以 [CRYPTO_VPN_ANDROID_TARGET_UI_VISUAL_SYSTEM_2026-04-16.md](/Users/cnyirui/git/projects/liaojiang/docs/CRYPTO_VPN_ANDROID_TARGET_UI_VISUAL_SYSTEM_2026-04-16.md) 为准。
> Android 组件命名与 Compose/XML 映射以 [ANDROID_UI_COMPONENT_MAPPING_2026-04-16.md](/Users/cnyirui/git/projects/liaojiang/docs/ANDROID_UI_COMPONENT_MAPPING_2026-04-16.md) 为准。

> 范围限定：仅针对 `code/Android/V2rayNG/app` 当前主 Android 工程的 UI 视觉收敛。
> 本文只记录第一阶段低风险工作，不扩扫描范围，不改业务逻辑、导航逻辑、表单逻辑。

## 1. 第一阶段目标

只建立统一的 UI 视觉 token 与 wrapper 层，并在 1 到 2 个低风险 Compose 页面验证视觉收敛；不做 XML 页面批量改造，不处理启动链路、Server 配置页、Settings 页，也不做导航切换。

## 2. 第一阶段边界

### 本阶段只做

- 建立第一批 Compose 视觉 token
- 建立第一批 XML token 资源占位
- 建立统一卡片、状态徽标、标签值行、复制分享动作组等公共视觉组件
- 用 wrapper 方式收口三套 Compose 骨架的视觉层
- 选择 2 到 4 个试点页面做替换验证，其中优先包含低风险页，并补充网络/资产总览页作为扩展试点

### 本阶段不做

- 不改业务逻辑
- 不改页面事件协议
- 不改路由与页面跳转
- 不改启动页和启动动画
- 不改 `ServerActivity` 及各协议表单布局
- 不改 `SettingsActivity` 的 Preference 体系
- 不做 XML 页面的大面积统一替换
- 不删除现有 Compose 骨架

### 高风险冻结区

- `code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/ui/compose/LaunchSplashActivity.kt`
- `code/Android/V2rayNG/app/src/main/res/layout/activity_launch_splash.xml`
- `code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/ui/MainActivity.kt`
- `code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/ui/ServerActivity.kt`
- `code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/ui/SettingsActivity.kt`

## 3. 第一阶段要新增的文件

### Compose token

- `code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/composeui/theme/tokens/AppColorTokens.kt`
  - 用途：统一品牌色、文本色、背景色、状态色、描边色
  - 是否全新：是
- `code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/composeui/theme/tokens/AppSpacingTokens.kt`
  - 用途：统一常用间距、布局 padding、section gap
  - 是否全新：是
- `code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/composeui/theme/tokens/AppShapeTokens.kt`
  - 用途：统一卡片、输入框、pill 的圆角定义
  - 是否全新：是
- `code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/composeui/theme/tokens/AppTextTokens.kt`
  - 用途：对现有 typography 做语义别名层，先不重做字体系统
  - 是否全新：是
- `code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/composeui/theme/tokens/AppSurfaceTokens.kt`
  - 用途：统一卡片背景、描边、阴影、状态面板表面
  - 是否全新：是

### Compose 公共视觉组件

- `code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/composeui/components/layout/AppPageScaffold.kt`
  - 用途：统一页骨架外层接口，作为三套骨架的收口点
  - 是否全新：是
- `code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/composeui/components/surfaces/AppCard.kt`
  - 用途：统一卡片背景、圆角、描边、阴影
  - 是否全新：是
- `code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/composeui/components/chips/AppStatusChip.kt`
  - 用途：统一 badge / pill / status 标签
  - 是否全新：是
- `code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/composeui/components/rows/AppLabelValueRow.kt`
  - 用途：统一“标签 - 数值”行
  - 是否全新：是
- `code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/composeui/components/actions/AppCopyShareActions.kt`
  - 用途：统一复制 / 分享按钮组外观
  - 是否全新：是

### XML token 预留

- `code/Android/V2rayNG/app/src/main/res/values/ui_tokens_colors.xml`
  - 用途：为 XML 侧建立颜色 token 入口
  - 是否全新：是
- `code/Android/V2rayNG/app/src/main/res/values/ui_tokens_dimens.xml`
  - 用途：为 XML 侧建立尺寸 token 入口
  - 是否全新：是
- `code/Android/V2rayNG/app/src/main/res/values/styles_ui_components.xml`
  - 用途：为 XML 侧建立组件样式挂点
  - 是否全新：是

## 4. 第一阶段要修改的文件

| 文件 | 修改目的 | 风险 | wrapper/适配层优先 |
|---|---|---|---|
| `code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/composeui/theme/Color.kt` | 旧颜色常量改为转发到新 token | 低 | 是 |
| `code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/composeui/theme/Shape.kt` | 旧 shape 改为转发到新 token | 低 | 是 |
| `code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/composeui/theme/Theme.kt` | 将新 token 接到 `MaterialTheme` | 低 | 是 |
| `code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/composeui/components/cards/TechCards.kt` | 统一卡片表面定义 | 低 | 是 |
| `code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/composeui/components/buttons/GlowButtons.kt` | 收口按钮颜色、圆角、高度 | 低 | 是 |
| `code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/composeui/components/feature/FeaturePageTemplate.kt` | 用统一卡片与统一外层骨架包住 | 低 | 是 |
| `code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/composeui/p0/ui/P01Chrome.kt` | 主骨架接入 token，不改业务 API | 中 | 是 |
| `code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/composeui/pages/p2/P2CoreH5Scaffold.kt` | 用统一卡片/chip/button 外观替换内联样式 | 中 | 是 |
| `code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/composeui/pages/p2extended/P2ExtendedH5Scaffold.kt` | 先包统一外层接口，再替换公共视觉基元 | 中 | 是 |
| `code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/composeui/pages/p2/InviteCenterPage.kt` | 第一批试点页面 | 低 | 是 |
| `code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/composeui/pages/p2/InviteSharePage.kt` | 第一批试点页面 | 低 | 是 |
| `code/Android/V2rayNG/app/src/main/res/values/colors.xml` | 增加 XML token alias，不删旧资源名 | 低 | 是 |
| `code/Android/V2rayNG/app/src/main/res/values/dimens.xml` | 增加 XML token alias，不删旧 dimen | 低 | 是 |
| `code/Android/V2rayNG/app/src/main/res/values/themes.xml` | 增加统一组件样式挂点 | 低 | 是 |

## 5. 建议建立的第一批 token

### 颜色

- `PageBg`
- `SurfaceCard`
- `SurfaceCardAlt`
- `SurfaceAccent`
- `TextPrimary`
- `TextSecondary`
- `TextTertiary`
- `BorderSubtle`
- `BorderStrong`
- `BrandPrimary`
- `BrandSecondary`
- `StatusSuccess`
- `StatusSuccessBg`
- `StatusWarning`
- `StatusWarningBg`
- `StatusDanger`
- `StatusDangerBg`
- `StatusInfo`
- `StatusInfoBg`

### 圆角

- `RadiusSm`
- `RadiusMd`
- `RadiusLg`
- `RadiusXl`
- `RadiusPill`

### 间距

- `Space2`
- `Space4`
- `Space8`
- `Space12`
- `Space16`
- `Space20`
- `Space24`
- `Space32`

### 字体层级

- `TextLabelS`
- `TextLabelM`
- `TextBodyS`
- `TextBodyM`
- `TextTitleS`
- `TextTitleM`
- `TextHeadlineS`
- `TextHeadlineM`

### 阴影 / 描边

- `BorderWidthThin`
- `BorderWidthDefault`
- `ShadowCardSm`
- `ShadowCardMd`
- `ShadowGlowBrand`

## 6. 三套 Compose 骨架的第一阶段处理策略

### 主骨架

- `P01PhoneScaffold`
- 原因：调用范围最大，结构最稳，最适合作为第一阶段视觉收口主入口

### 保留为兼容层

- `P2CorePageScaffold`
- 策略：内部不重构，只把卡片、chip、button、间距优先接入 token 和 wrapper

### 先不删除，只包一层统一接口

- `P2ExtendedPageScaffold`
- 策略：先通过 `AppPageScaffold` 包装，后续阶段再拆内部巨型组件

### 补充说明

- `FeaturePageTemplate` 不视为第四套骨架，第一阶段直接改为消费统一卡片与统一 scaffold

## 7. 第一批公共组件

| 组件名 | 来源 | 优先级 | 试点页面 |
|---|---|---|---|
| `AppPageScaffold` | Compose | P0 | `InviteCenterPage`, `InviteSharePage`, `VpnHomePage`, `WalletHomePage` |
| `AppCard` | Compose | P0 | `InviteCenterPage`, `InviteSharePage`, `VpnHomePage`, `WalletHomePage` |
| `AppStatusChip` | Compose | P0 | `InviteCenterPage`, `InviteSharePage`, `VpnHomePage`, `WalletHomePage` |
| `AppCopyShareActions` | Compose | P0 | `InviteCenterPage`, `InviteSharePage` |
| `AppLabelValueRow` | Compose，后续可镜像到 XML | P1 | `InviteCenterPage`, `WalletHomePage`，下一步扩到 `CommissionLedger/Profile` 类页面 |

## 8. 推荐试点页面

### 试点 1

- `code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/composeui/pages/p2/InviteCenterPage.kt`
- 原因：
  - 不涉及 VPN 启停
  - 不涉及钱包密钥导出
  - 不涉及复杂表单
  - 包含卡片、badge、指标区、复制和分享动作，覆盖第一批公共视觉组件

### 试点 2

- `code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/composeui/pages/p2/InviteSharePage.kt`
- 原因：
  - 逻辑很薄，主要是展示、复制、分享
  - 与 `InviteCenterPage` 处于同一骨架体系，适合验证同骨架下的视觉收敛效果

### 试点 3

- `code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/composeui/pages/p0/VpnHomePage.kt`
- 原因：
  - 属于“网络总览页”，能验证 Dashboard 型页面在新视觉系统下的节奏与层次
  - 覆盖主指标卡、快捷操作区、状态块、顶部摘要等高复用模块
  - 虽然复杂度高于邀请页，但能尽早暴露总览页组件的不足

### 试点 4

- `code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/composeui/pages/p0/WalletHomePage.kt`
- 原因：
  - 属于“资产总览页”，能验证资产卡片、资产行、摘要区、操作入口的通用性
  - 与 `VpnHomePage` 共同构成 Dashboard 级试点，可以补齐试点覆盖面
  - 后续 `AssetRow / MetricCard / ActionCluster` 的复用价值在该页最明显

## 9. 第一阶段验收标准

- 试点页面业务行为完全不变：
  - 复制仍复制原内容
  - 分享仍调用原系统分享
  - Toast 文案和触发时机不变
- `InviteCenterPage`、`InviteSharePage`、`VpnHomePage`、`WalletHomePage` 的以下视觉项应明显统一：
  - 卡片圆角
  - 按钮高度
  - badge 样式
  - 间距节奏
  - 标签值行表现
  - 总览卡与列表卡的层级关系
- 三套 Compose 骨架的页面调用方式基本不变：
  - 页面仍传原参数
  - 只增加 wrapper / 适配层
  - 不新增路由和事件协议改动
- 试点页面不再新增新的散落视觉常量：
  - 优先从 `App*Tokens` 读取
  - 优先使用 `App*` 公共视觉组件
- 以下高风险文件在本阶段保持不动：
  - `LaunchSplashActivity.kt`
  - `activity_launch_splash.xml`
  - `MainActivity.kt`
  - `ServerActivity.kt`
  - `SettingsActivity.kt`
- XML 侧第一阶段只建立 token 资源和样式挂点，不发生大面积页面替换

## 10. 推荐执行顺序

1. 先加 token 文件，不改页面
2. 接着把 `Theme.kt / Color.kt / Shape.kt` 接到 token
3. 再做 `AppCard / AppStatusChip / AppPageScaffold / AppCopyShareActions`
4. 然后修改 `TechCards / GlowButtons / FeaturePageTemplate`
5. 再给 `P01Chrome / P2CoreH5Scaffold / P2ExtendedH5Scaffold` 接 wrapper
6. 最后先改 `InviteCenterPage / InviteSharePage`，再把 `VpnHomePage / WalletHomePage` 作为扩展试点接入验证

## 11. 本文用途

本文作为“第一阶段 UI 视觉组件规范”的仓库内基线文档。后续开始统一 UI 时，默认按本文边界、优先级、试点范围推进；超出本文范围的页面和逻辑，视为第二阶段及以后处理。
