# CryptoVPN UI 组件库

本文档描述了CryptoVPN Android应用的所有UI组件。

## 目录结构

```
ui/components/
├── buttons/           # 按钮组件
├── cards/             # 卡片组件
├── dialogs/           # 弹窗组件
├── inputs/            # 输入框组件
├── listitems/         # 列表项组件
├── navigation/        # 导航组件
├── special/           # 特殊组件
├── tags/              # 标签组件
└── theme/             # 主题配置
```

## 主题系统

### 颜色定义 (Color.kt)
- 背景色：BackgroundDeepest, BackgroundPrimary, BackgroundSecondary, BackgroundTertiary
- 主题色：Primary, PrimaryHover, PrimaryPressed
- 功能色：Success, Warning, Error, Info
- 文字色：TextPrimary, TextSecondary, TextTertiary

### 字体定义 (Type.kt)
- 标题：H1(28sp), H2(24sp), H3(20sp), H4(18sp)
- 正文：BodyLarge(16sp), Body(14sp), BodySmall(12sp)
- 标签：LabelLarge, LabelMedium, LabelSmall
- 数字：NumberLarge, NumberMedium, NumberSmall

### 形状定义 (Shape.kt)
- radius-sm: 4dp, radius-md: 8dp, radius-lg: 12dp
- radius-xl: 16dp, radius-2xl: 24dp, radius-full: 999dp

## 组件清单

### 1. 按钮组件 (buttons/)

| 组件 | 文件 | 功能 |
|------|------|------|
| PrimaryButton | PrimaryButton.kt | 主按钮，支持大/中/小尺寸，Normal/Pressed/Disabled/Loading状态 |
| SecondaryButton | SecondaryButton.kt | 次要按钮（边框样式） |
| DangerButton | DangerButton.kt | 危险按钮，支持Filled/Outlined/Text变体 |
| TextButton | TextButton.kt | 文字按钮和链接按钮 |
| IconButton | IconButton.kt | 图标按钮，支持多种变体和尺寸 |

### 2. 输入框组件 (inputs/)

| 组件 | 文件 | 功能 |
|------|------|------|
| TextInputField | TextInputField.kt | 普通输入框，带图标、清除按钮、错误提示 |
| PasswordInputField | PasswordInputField.kt | 密码输入框，带可见性切换 |
| VerificationCodeInput | VerificationCodeInput.kt | 验证码输入，带倒计时功能 |
| SearchInputField | SearchInputField.kt | 搜索输入框，带搜索历史 |

### 3. 卡片组件 (cards/)

| 组件 | 文件 | 功能 |
|------|------|------|
| BaseCard | BaseCard.kt | 基础卡片，支持点击和展开 |
| PlanCard | PlanCard.kt | 套餐卡片，带推荐标签和价格显示 |
| OrderInfoCard | OrderInfoCard.kt | 订单信息卡片 |
| SubscriptionCard | SubscriptionCard.kt | 订阅状态卡片，带进度条 |
| AssetCard | AssetCard.kt | 资产卡片，支持多种样式 |
| TransactionCard | TransactionCard.kt | 交易记录卡片 |

### 4. 标签组件 (tags/)

| 组件 | 文件 | 功能 |
|------|------|------|
| StatusTag | StatusTag.kt | 状态标签，支持多种状态类型 |
| VpnStatusTag | StatusTag.kt | VPN连接状态标签 |
| OrderStatusTag | StatusTag.kt | 订单状态标签 |
| PillTag | StatusTag.kt | 胶囊标签（用于筛选） |

### 5. 导航组件 (navigation/)

| 组件 | 文件 | 功能 |
|------|------|------|
| TopAppBar | AppBars.kt | 顶部应用栏 |
| CenterAlignedTopAppBar | AppBars.kt | 居中大标题应用栏 |
| BottomNavigationBar | AppBars.kt | 底部导航栏 |
| BackButton | AppBars.kt | 返回按钮 |

### 6. 弹窗组件 (dialogs/)

| 组件 | 文件 | 功能 |
|------|------|------|
| AlertDialog | Dialogs.kt | 警告弹窗 |
| BottomSheet | Dialogs.kt | 底部弹窗 |
| LoadingDialog | Dialogs.kt | 加载弹窗 |
| SuccessDialog | Dialogs.kt | 成功弹窗 |
| ErrorDialog | Dialogs.kt | 错误弹窗 |

### 7. 特殊组件 (special/)

| 组件 | 文件 | 功能 |
|------|------|------|
| QRCodeDisplay | SpecialComponents.kt | 二维码展示 |
| AmountInput | SpecialComponents.kt | 金额输入组件 |
| EmptyState | SpecialComponents.kt | 空状态组件 |
| ErrorState | SpecialComponents.kt | 错误状态组件 |
| SkeletonBox/List | SpecialComponents.kt | 骨架屏组件 |

### 8. 列表项组件 (listitems/)

| 组件 | 文件 | 功能 |
|------|------|------|
| RegionListItem | ListItems.kt | 区域列表项 |
| OrderListItem | ListItems.kt | 订单列表项 |
| CommissionListItem | ListItems.kt | 佣金列表项 |
| DocumentListItem | ListItems.kt | 文档列表项 |
| SettingsListItem | ListItems.kt | 设置列表项 |

## 使用示例

### 按钮使用
```kotlin
// 主按钮
PrimaryButton(
    text = "立即订阅",
    onClick = { /* 处理点击 */ },
    size = ButtonSize.LARGE
)

// 加载状态
PrimaryButton(
    text = "提交",
    onClick = { },
    loading = true
)
```

### 输入框使用
```kotlin
var text by remember { mutableStateOf("") }

TextInputField(
    value = text,
    onValueChange = { text = it },
    placeholder = "请输入邮箱",
    label = "邮箱地址",
    leadingIcon = Icons.Default.Email
)
```

### 卡片使用
```kotlin
PlanCard(
    name = "年度会员",
    price = "$59.99",
    originalPrice = "$99.99",
    duration = "年",
    features = listOf("无限流量", "全球50+节点"),
    isRecommended = true,
    onClick = { },
    onSubscribe = { }
)
```

### 状态标签使用
```kotlin
StatusTag(
    text = "已完成",
    type = StatusType.COMPLETED
)

VpnStatusTag(
    isConnected = true,
    serverName = "美国-纽约"
)
```

## 设计规范

### 颜色使用
- 背景：BackgroundDeepest (#0B1020) 用于启动页
- 主背景：BackgroundPrimary (#111827) 用于页面
- 卡片背景：BackgroundSecondary (#1F2937)
- 主色：Primary (#1D4ED8) 用于按钮和链接
- 成功：Success (#22C55E)
- 警告：Warning (#F59E0B)
- 错误：Error (#EF4444)

### 间距系统
- space-4: 4dp
- space-8: 8dp
- space-12: 12dp
- space-16: 16dp
- space-20: 20dp
- space-24: 24dp
- space-32: 32dp
- space-48: 48dp

### 组件状态
所有交互组件都支持以下状态：
- Normal：正常状态
- Pressed：按下状态
- Disabled：禁用状态
- Loading：加载状态
- Error：错误状态
