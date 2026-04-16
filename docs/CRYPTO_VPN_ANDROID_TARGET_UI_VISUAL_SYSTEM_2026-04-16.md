# CryptoVPN Android 目标 UI 视觉系统

> 定位说明：本文不是源码现状总结，而是未来所有 Android 页面都要逐步靠拢的目标视觉系统。
> 落地策略与阶段边界见 [UI_PHASE1_VISUAL_COMPONENT_STANDARD_2026-04-16.md](/Users/cnyirui/git/projects/liaojiang/docs/UI_PHASE1_VISUAL_COMPONENT_STANDARD_2026-04-16.md)。
> Android 组件命名与 Compose/XML 对应关系见 [ANDROID_UI_COMPONENT_MAPPING_2026-04-16.md](/Users/cnyirui/git/projects/liaojiang/docs/ANDROID_UI_COMPONENT_MAPPING_2026-04-16.md)。

## 1. 视觉关键词

- 轻科技
- 冷静金融感
- 浅色高透
- 蓝紫青渐变
- 圆润柔和
- 低压迫信息密度
- 轻玻璃感 / 轻拟态
- 大留白
- 弱边界、强层次
- 底部主操作明确

### 方向说明

不是传统深色 crypto 风，也不是纯白极简工具风；更接近浅色科技钱包、高可信的复合型 App，适用于 VPN + 钱包 + 订阅 + 资产的混合场景。

### 应避免的错误方向

- 颜色太艳、太“交易所”
- 阴影太黑太重
- 边框太硬
- 圆角不统一
- 字重过粗
- 卡片堆得太满
- 组件尺寸忽大忽小

## 2. 颜色体系

### 2.1 主色体系

- `Primary Blue`: `#4F7CFF`
- `Primary Cyan`: `#25D7FF`
- `Primary Gradient`: `#4F7CFF -> #25D7FF`

适用场景：

- CTA 按钮
- 高亮 tab
- 激活态胶囊
- 图表主线
- 首页关键指标强调

### 2.2 辅助色体系

#### Purple Accent

- `Accent Purple`: `#8C7CFF`
- `Soft Lavender`: `#EDE8FF`

#### Mint Accent

- `Mint Green`: `#2ED8A3`
- `Soft Mint Bg`: `#E8FFF6`

#### Sky Accent

- `Sky Blue`: `#A9E8FF`
- `Soft Sky Bg`: `#EFFBFF`

### 2.3 背景色体系

- `Background Base`: `#F7FAFF`
- `Background Elevated`: `#FBFDFF`
- `Background Soft Tint`: `#F2F8FF`

背景规则：

- 大面积白底
- 局部淡蓝/淡紫模糊光斑
- 星点纹理可选，但必须弱化，不能影响性能

### 2.4 卡片色体系

- `Card Base`: `#FFFFFF`
- `Card Soft`: `#FCFEFF`
- `Card Glow Overlay`: 白色透明层 `6%~14%`

卡片边界优先来自：

- 微阴影
- 极浅边框
- 内部渐变高光

不依赖重描边。

### 2.5 文本颜色体系

- `Text Primary`: `#16233B`
- `Text Secondary`: `#5E6C84`
- `Text Tertiary`: `#8C97AB`
- `Text OnPrimary`: `#FFFFFF`
- `Text Disabled`: `#B8C1D1`

### 2.6 状态色体系

- `Success`: `#2ED8A3`
- `Success Bg`: `#E8FFF6`
- `Warning`: `#FFB84D`
- `Warning Bg`: `#FFF6E8`
- `Error`: `#FF6B7A`
- `Error Bg`: `#FFF0F3`
- `Info`: `#4F7CFF`
- `Info Bg`: `#EEF4FF`

## 3. 字体层级

### 字体基调

- 中文：系统默认 / Noto Sans SC 风格
- 数字：与正文同字族即可
- 字重：整体偏轻，不要过多 Bold

### 文字层级定义

| 层级 | 尺寸 | 字重 | 行高 | 用途 |
|---|---:|---|---:|---|
| `Page Title` | 28sp | SemiBold / Bold | 34sp | 页面顶部主标题 |
| `Section Title` | 18sp | SemiBold | 24sp | 卡片区标题、模块标题 |
| `Display Value` | 28sp | Bold | 32sp | 资产金额、余额、汇总指标 |
| `Value M` | 16sp | SemiBold | 22sp | 列表数值、标签值行右侧值 |
| `Body` | 14sp | Medium / Regular | 20sp | 主要正文、表单文本 |
| `Caption` | 12sp | Regular | 16sp | 次级说明、小提示 |
| `Label` | 11sp ~ 12sp | Medium | 14sp | chip、字段标题 |
| `Nav Label` | 10sp ~ 11sp | Medium | 12sp | 底部导航、小 tab |

## 4. 圆角体系

### 推荐圆角 Token

- `Radius XS`: 8dp
- `Radius S`: 12dp
- `Radius M`: 16dp
- `Radius L`: 20dp
- `Radius XL`: 24dp
- `Radius Pill`: 999dp

### 使用规则

- 页面主卡片：20dp
- 列表卡片/列表容器：16dp
- 输入框：12dp
- 主按钮：`999dp` 或 `16dp` 二选一，整套系统不能混用
- 圆形图标外圈：999dp
- 二维码容器/图表容器：20dp

## 5. 间距体系

### 推荐 Spacing Token

- `Space 4`: 4dp
- `Space 8`: 8dp
- `Space 12`: 12dp
- `Space 16`: 16dp
- `Space 20`: 20dp
- `Space 24`: 24dp
- `Space 32`: 32dp

### 页面级规则

- 页面左右边距：20dp
- 页面顶部主内容起点：16dp ~ 24dp
- 卡片内边距：16dp
- 区块与区块之间：12dp ~ 16dp
- 标题与副标题之间：4dp ~ 8dp
- 输入框上下间距：12dp
- 底部固定按钮与屏幕边缘：16dp ~ 20dp
- 列表 item 高度内边距：12dp ~ 16dp

### 自适应规则

#### Compact

- 左右：16dp 或 20dp

#### Medium

- 左右：24dp
- 内容最大宽度建议限制在 `560dp~640dp`

#### Expanded

- 内容居中，主内容列限宽
- 列表/表单可双列，但主表单仍优先单列

## 6. 阴影 / 描边 / 渐变 / 光晕

### 6.1 阴影规则

- Y 偏移：4dp ~ 8dp
- 模糊：16dp ~ 24dp
- 颜色：低透明蓝灰色
- 禁止黑色重阴影

目标效果：

- 轻漂浮
- 边界柔化
- 不是“Material 黑影”

### 6.2 描边规则

- 宽度：1dp
- 颜色：白色透明或极浅冷灰蓝

建议风格：

- `rgba(255,255,255,0.7)` 风格高光描边
- 或极浅冷灰边框

### 6.3 渐变规则

- 主按钮：`Primary Blue -> Primary Cyan`
- 背景光斑：淡紫到淡蓝柔和 blur
- 图表高光：主蓝线条 + 低透明蓝填充

### 6.4 光晕规则

科技圆环 + 外圈发光只用于品牌识别位：

- 启动页主图标
- 登录页顶角图标
- 钱包引导页主图标
- 节点页右侧主状态圆环

禁止全 App 滥用。

## 7. 基础组件清单

### 7.1 Button

#### Primary Button

- 职责：页面唯一主操作
- 规则：
  - 高度 `48dp ~ 52dp`
  - 圆角 `999dp`
  - 渐变填充
  - 白色文字
  - 轻阴影

#### Secondary Button

- 职责：次要操作
- 规则：
  - 白底或浅蓝底
  - 边框或弱阴影
  - 蓝色文字
  - 高度与主按钮一致

#### Ghost Button / Text Button

- 职责：最轻操作
- 规则：
  - 无背景
  - 蓝色/次级文字
  - 点击态弱高亮

### 7.2 Card

#### App Card

- 职责：整套系统最核心的信息容器
- 规则：
  - 白色或近白色
  - 圆角 `16dp / 20dp`
  - 内边距 `16dp`
  - 轻阴影 + 极浅边框

#### Glass Highlight Card

- 职责：重点展示模块
- 规则：
  - 比普通卡片更亮
  - 带轻微渐变或光晕
  - 高光边缘更明显

### 7.3 Tag

#### Status Chip

- 职责：表达状态
- 规则：
  - 胶囊形
  - 轻背景 + 对应文字色
  - 内边距 `6dp / 10dp`

#### Filter Chip

- 职责：筛选或切换
- 规则：
  - 默认浅底
  - 选中后蓝底或淡蓝底
  - 边界清晰但不重

### 7.4 Input

#### App Text Field

- 职责：统一表单输入视觉
- 规则：
  - 高度 `44dp ~ 48dp`
  - 圆角 `12dp`
  - 背景浅灰白
  - 边框极浅
  - Focus 时蓝边或蓝光

#### Token Selector Field

- 职责：币种 / 链路选择的组合控件
- 规则：
  - 左侧 token 标识
  - 右侧下拉箭头
  - 外壳沿用 `App Text Field`

### 7.5 Top Bar

#### Page Top Bar

- 职责：统一页面标题、返回、右侧动作
- 规则：
  - 高度 56dp
  - 透明或浅底
  - 标题左对齐
  - 图标轻量化

#### Hero Header

- 职责：带品牌装饰的头部区域
- 规则：
  - 大标题 + 副标题
  - 右上或右侧有科技圆环徽标
  - 背景带淡紫蓝氛围

### 7.6 List Item

#### Standard List Item

- 职责：标准列表信息容器
- 结构：
  - 左：图标 / 名称
  - 中：副标题 / 标签
  - 右：数值 / 箭头 / 状态
- 规则：
  - 高度 `56dp ~ 72dp`
  - 行内 padding `12dp~16dp`
  - 分隔弱化

#### Metric List Item

- 职责：更高信息密度的列表项
- 场景：
  - 节点测速
  - 套餐比较
  - 资产明细
  - 订单行

### 7.7 Empty State

#### Empty State Card

- 职责：空列表、无数据、待开通状态提示
- 规则：
  - 居中图标
  - 标题 + 说明
  - 可带一个轻按钮
  - 卡片化呈现

## 8. 组合组件清单

- `LabelValueRow`
- `Metric Card`
- `Action Cluster`
- `Info Section`

典型业务组合组件：

- `AssetRow`
- `NodeRow`
- `PaymentSummaryCard`

## 9. 页面骨架类型

页面骨架不再按 `p0 / p1 / p2` 分类，而按页面类型分类：

- `Splash / Brand Page`
- `Hero Form Page`
- `Dashboard Page`
- `Selection List Page`
- `Subscription / Pricing Page`
- `Asset Detail Page`
- `Transaction Form Page`
- `Confirmation Page`

## 10. 安卓自适应落地规则

### 尺寸适配原则

- 所有尺寸使用 `dp / sp`
- 不写死 px
- 文本最小建议 `11sp`
- 主按钮高度 `48dp~52dp`
- 触控最小区域 `44dp~48dp`

### 屏幕分级建议

#### 手机窄屏

- 左右 padding：16dp
- 单列布局
- 底部按钮固定

#### 常规手机

- 左右 padding：20dp
- 单列布局
- 卡片垂直堆叠

#### 大屏手机 / 小平板

- 左右 padding：24dp
- 核心内容限宽
- 指标区可 2 列

#### 平板 / 横屏

- 页面内容居中
- Dashboard / Detail 页可左右双栏
- 表单页仍建议主列优先

### 自适应行为建议

- 图表卡、指标卡允许变高不变窄
- 底部导航在大屏上保持紧凑
- 大金额数字优先调整布局，不要缩得太小
- 二维码、圆形徽标、主图标容器保持固定比例

## 11. 最终工程分层建议

### 第一层：Design Tokens

- `ColorTokens`
- `TypographyTokens`
- `ShapeTokens`
- `SpacingTokens`
- `ElevationTokens`
- `GradientTokens`

### 第二层：基础组件

- `AppButton`
- `AppCard`
- `AppChip`
- `AppTextField`
- `AppTopBar`
- `AppListItem`
- `EmptyStateCard`

### 第三层：业务组合组件

- `LabelValueRow`
- `MetricCard`
- `ActionCluster`
- `InfoSection`
- `AssetRow`
- `NodeRow`
- `PaymentSummaryCard`
