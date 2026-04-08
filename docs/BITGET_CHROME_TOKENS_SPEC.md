# Bitget 全局 Chrome / Tokens 规范

## 1. 范围与输入

- 本文只使用以下两份 master 源文档：
  - `/Users/cnyirui/git/projects/liaojiang/docs/BITGET_CAPTURE_INVENTORY.md`
  - `/Users/cnyirui/git/projects/liaojiang/docs/BITGET_PAGE_MATRIX.md`
- 本文只覆盖全局视觉系统，不覆盖页面族实现顺序、业务映射、单页内容编排或页面级优先级。
- 可确认的统一基线只有这些：
  - 截图统一为竖屏 `720 x 1600` PNG，XML 可见区域统一为 `720 x 1440`。
  - 全局主题为深色底、大圆角卡片、大圆角按钮、亮青蓝主 CTA、浮动底部 dock、底部 sheet 顶部拖拽条。
  - 搜索框、输入框、列表组、图标容器都偏“深色填充面”，不是浅底描边式 Material。
- 本文对 token 使用两种表达：
  - `明确事实`：源文档已经直接说明。
  - `归一化范围`：为 Android Compose 首版落地给出的可执行区间。无法证明精确值时，必须在区间内选一个固定落点并全局复用。

## 2. 可确认的全局视觉语法

- 页面主背景接近黑绿 / 深炭色，不是纯黑。
- 主 CTA、选中态、选中描边、对勾、正向收益、强调提示共用亮青蓝色族，不走亮绿色。
- 文本层级以白字和灰字为主；红色主要用于亏损 / 风险；黄棕色主要用于安全提醒或强调说明。
- 结构节奏稳定：顶部栏 -> 搜索 / 标题 / 摘要 -> 大圆角卡或卡组列表 -> 底部浮动导航 / 底部主按钮。
- 底部 sheet 是跨页面族共用语法：深色面板、超大顶角、浅灰拖拽条、标题 + 内容区 + 底部 CTA。
- 图标容器以“深灰圆角方块”优先，圆形更多用于中间主按钮、切换按钮、主题色圆点。

## 3. Compose 落地原则

- 建一套共享 `BitgetChromeTokens`，不要按页面族拆成多套主题。
- 优先用自定义 `Surface`、`Row`、`Box` 组合，而不是直接套默认 Material 3 `NavigationBar`、`SearchBar`、`ModalBottomSheet` 外观。
- 所有无法从源文档直接证明的精确值，都只能在本文给出的区间内收敛，不得额外扩出另一套视觉语法。
- 首版允许先用语义 token，不要求先做像素采样 hex；但颜色角色、圆角等级、间距节奏必须先统一。

## 4. 颜色 Tokens

说明：源文档只提供颜色语义，不提供精确采样值。这里统一用语义 token + HSL / alpha 归一化范围表示。

| Token | 明确事实 | 归一化范围 | Compose 用途 |
|---|---|---|---|
| `bg/base` | “主背景接近黑绿 / 深炭色” | `H 185-205 / S 8-18% / L 7-12%` | 全局页面底色 |
| `bg/surface` | 卡片、搜索框、输入框、sheet 都是深色填充面 | 在 `bg/base` 基础上 `L +4 到 +8` | 卡片、输入框、列表组、底部 dock |
| `bg/surface-strong` | 图标方块、局部功能块比普通 surface 略亮 | 在 `bg/base` 基础上 `L +8 到 +12` | 图标容器、操作块、分段输入卡 |
| `bg/scrim` | overlay 会把背景暗化 | 黑色 `45-60%` alpha | sheet、modal、picker |
| `accent/primary` | 主 CTA 与高亮选中态为亮青蓝色 | `H 188-198 / S 78-95% / L 56-68%` | 主按钮、选中描边、对勾、中间圆按钮 |
| `accent/primary-soft` | 描边提示、浅强调提示仍使用青蓝族 | `accent/primary` 的 `20-32%` alpha 或降饱和版本 | 提示条、弱强调背景、轻状态底 |
| `text/primary` | 标题与主要金额为高亮浅色 | `L 92-98%` | 标题、主金额、正文主信息 |
| `text/secondary` | 次级文案和说明多为浅灰 | `L 68-82%` | 副标题、说明、placeholder |
| `text/tertiary` | 未选中导航、辅助信息、空状态更弱 | `L 48-64%` | 弱态标签、未选中图标、辅助值 |
| `text/disabled` | 禁用态明显变灰 | `L 36-48%` | 禁用按钮、禁用图标、禁用文本 |
| `state/danger` | 亏损、风险、低安全级别使用红色 | `H 350-10 / S 70-90% / L 58-68%` | 亏损数值、危险提示、危险动作文案 |
| `state/warning-surface` | 安全提醒或强调说明使用暖色面 | `H 32-44 / S 35-55% / L 24-36%` | 提醒卡、说明卡、警示块 |
| `outline/subtle` | 少量弱分割和拖拽条存在 | 白或灰 `10-18%` alpha | 拖拽条、弱分割、轻描边 |
| `outline/accent` | 当前态卡片和提示条会用亮青蓝描边 | `accent/primary` 的 `70-100%` alpha | 当前钱包卡、当前筛选、当前选择项 |

### 颜色实施约束

- 不要把“正向 / 高亮”改成绿色；当前全局语法是青蓝品牌强调。
- 不要把页面主背景做成纯黑，必须保留一点绿灰 / 炭灰色温。
- 白底大按钮只在 `选择网络 -> 添加网络` 一例出现，不能升级为全局主 CTA 规则；全局主 CTA 仍以亮青蓝实底为准。

## 5. 字体层级 Tokens

源文档没有给出字体家族，因此这里只定义字号层级、字重和使用语义。

| Token | 归一化范围 | 字重建议 | 用途 |
|---|---|---|---|
| `type/hero` | `26-32sp` | `Bold` | Hero 标题、运营首屏大字 |
| `type/amount-xl` | `28-36sp` | `SemiBold` | 资产总额、主价格、核心金额 |
| `type/amount-l` | `22-28sp` | `SemiBold` | 详情页主值、卡内主数字 |
| `type/topbar-title` | `18-20sp` | `SemiBold` | 居中标题栏 |
| `type/section-title` | `16-18sp` | `SemiBold` | 分组标题、卡标题 |
| `type/card-title` | `14-16sp` | `Medium` | 卡片标题、列表主标题 |
| `type/body-m` | `14-15sp` | `Regular/Medium` | 正文、列表主行文案 |
| `type/body-s` | `13-14sp` | `Regular` | 说明文案、副文本 |
| `type/caption` | `11-12sp` | `Regular/Medium` | 标签、辅助状态、底部导航标签 |
| `type/button` | `14-16sp` | `SemiBold` | 主按钮、次按钮、底部 CTA |
| `type/chip` | `12-13sp` | `Medium` | chip、tag、状态 pill |
| `type/tab` | `13-15sp` | `Medium` | tabs、时间粒度切换、筛选标签 |

### 字体层级实施约束

- 资产金额和主价格必须至少比 section title 高一个层级。
- 顶栏标题、按钮文案、卡片标题都偏中高字重，不宜做轻字重极简风。
- placeholder、未选中导航、辅助文案统一走 `text/secondary` 或 `text/tertiary`，不要只靠透明度做层级。

## 6. 圆角、间距与尺寸节奏

### 6.1 圆角 Tokens

| Token | 归一化范围 | 用途 |
|---|---|---|
| `radius/sheet` | `28-32dp` | 底部 sheet 顶角 |
| `radius/card` | `24-32dp` | 主卡、运营卡、列表组外框 |
| `radius/button` | `22-28dp` | 主按钮、次按钮、底部 CTA |
| `radius/input` | `20-24dp` | 搜索框、输入框、大地址输入框 |
| `radius/icon-box` | `12-16dp` | 图标方块容器 |
| `radius/pill` | `999dp` | chip、tag、小型动作按钮、拖拽条两端 |
| `radius/fab-circle` | `50%` | 中间主按钮、切换按钮、主题圆点 |

### 6.2 间距 Tokens

| Token | 归一化范围 | 用途 |
|---|---|---|
| `space/page-x` | `16-24dp` | 页面左右内边距 |
| `space/page-top` | `12-20dp` | 状态栏下首段起始间距 |
| `space/section` | `16-24dp` | 模块与模块之间 |
| `space/card-padding` | `16-20dp` | 卡片内部主 padding |
| `space/card-gap` | `12-16dp` | 卡内元素竖向间距 |
| `space/row-x` | `14-18dp` | 列表行左右 padding |
| `space/row-y` | `12-16dp` | 列表行上下 padding |
| `space/icon-gap` | `8-12dp` | icon 与文字间距 |
| `space/chip-gap` | `8-12dp` | chip / 快捷动作横向间距 |
| `space/button-stack` | `12-16dp` | 按钮堆叠间距 |

### 6.3 尺寸 Tokens

| Token | 归一化范围 | 用途 |
|---|---|---|
| `size/topbar` | `56-64dp` | 标题栏、搜索顶栏可用高度 |
| `size/search` | `40-44dp` | 全局搜索框高度 |
| `size/button-primary` | `48-56dp` | 主按钮、底部 CTA |
| `size/button-pill` | `32-36dp` | 小胶囊按钮 |
| `size/input-single` | `52-56dp` | 单行输入框 |
| `size/input-large` | `88-120dp` | 大地址输入框 |
| `size/list-row` | `56-72dp` | 标准列表行 |
| `size/list-row-rich` | `72-96dp` | 双行账户 / 资产 / 设置卡行 |
| `size/icon-box` | `40-48dp` | 图标方块容器 |
| `size/icon-avatar` | `32-40dp` | 头像、币种图标、品牌图标 |
| `size/bottom-dock` | `64-72dp` | 浮动底部导航 / 工具 dock |
| `size/bottom-center-action` | `56-64dp` | 底部中间圆形主按钮 |
| `size/drag-handle` | `32-40dp x 4-6dp` | sheet 顶部拖拽条 |

## 7. 全局组件规范

### 7.1 Cards

- `Card/Standard`
  - 深色 `bg/surface`。
  - 使用 `radius/card`。
  - 使用 `space/card-padding` 和 `space/card-gap`。
  - 适用于资产摘要、信息卡、列表组、设置组。
- `Card/Highlighted`
  - 允许蓝色渐变或暖色实拍 / 运营图。
  - 仍保持大圆角与深色页面底的强对比。
  - 适用于运营 Hero、专题卡、主推广卡。
- `Card/Selected`
  - 用 `outline/accent` 的 `1-2dp` 描边表示当前态。
  - 当前账户卡、当前网络、当前选择项优先用这个模式。
- `Card/Warning`
  - 使用 `state/warning-surface`。
  - 只用于安全、授权、风险提醒，不扩散为普通信息卡。

Compose 约束：

- 以填充面对比为主，不依赖阴影塑形。
- 多个列表行优先包在同一个大圆角组卡里，而不是每行独立悬浮。

### 7.2 Buttons

| 变体 | 归一化规格 | 用法 |
|---|---|---|
| `Button/Primary` | `accent/primary` 实底，`size/button-primary`，`radius/button` | 主 CTA、确认、交易、分享、添加钱包 |
| `Button/SecondaryDark` | 深色实底，和 Primary 同高 | `取消`、次按钮、次级确认 |
| `Button/OutlineAccent` | 深色底或透明底 + `outline/accent` | 提示条、弱强调按钮、选中辅助动作 |
| `Button/DangerOutline` | 深色底 + 危险色文字或描边 | 危险操作 |
| `Button/PillSmall` | `size/button-pill`，`radius/pill` | `扫一扫`、`粘贴` 等小动作 |

Compose 约束：

- 双按钮确认弹窗中，主按钮放右侧，次按钮放左侧。
- 禁用态优先降低到底色和文字灰度，不做低透明度亮青蓝实底。

### 7.3 Chips / Tags

| 变体 | 归一化规格 | 用法 |
|---|---|---|
| `Chip/StatusSoft` | 深色底 + 浅色字，`28-32dp` 高 | 普通状态 pill |
| `Chip/StatusAccent` | `accent/primary-soft` 背景或 accent 描边 | 福利、已安全运行、当前筛选 |
| `Chip/InlineMini` | `20-24dp` 高 | 紧贴文本或数值的小标签 |
| `Chip/Filter` | `28-32dp` 高，当前项用亮字或指示线 | tabs、双标签、周期切换 |

Compose 约束：

- 过滤 tabs 与状态 pill 可以共用圆角体系，但不要共用颜色优先级。
- 小标签优先做成胶囊或紧凑圆角矩形，不要做成方角角标。

### 7.4 Top Bars

定义三种全局样式：

- `TopBar/Search`
  - 左侧头像或返回。
  - 中央为 `size/search` 的全局搜索框。
  - 右侧 1-2 个工具图标。
- `TopBar/CenterTitle`
  - 左侧返回。
  - 中央标题。
  - 右侧帮助、编辑、更多或个人入口。
- `TopBar/CloseTitle`
  - 左侧关闭 `X`。
  - 中央标题。
  - 右侧可选文字动作或图标动作。

统一约束：

- 顶栏高度使用 `size/topbar`。
- 图标视觉尺寸控制在 `20-24dp`，点击热区至少 `40dp`。
- 顶栏和内容区之间保留 `space/page-top` 到 `space/section` 的过渡，不要贴死。

### 7.5 Search Bars

- 搜索框必须是深色填充胶囊，不是浅底描边输入框。
- 高度使用 `size/search`，圆角使用 `radius/input` 或 `radius/pill`。
- placeholder 使用 `text/secondary`，输入值使用 `text/primary`。
- leading icon 常驻；trailing 功能位可放扫码、复制、过滤或网络入口。

Compose 约束：

- 不建议直接使用默认 M3 `SearchBar` 视觉；更接近 `Surface + Row + Icon + Text` 的自绘结构。

### 7.6 Bottom Navigation / Tool Dock

- 浮动在页面底部上方，而不是和系统导航区直接粘连。
- 整体是深色大圆角 dock，使用 `size/bottom-dock`。
- 标准结构是左右常规 tab + 中间独立圆形主按钮。
- 中间主按钮使用 `accent/primary` 实底和 `size/bottom-center-action`。
- 非中间 tab：
  - 当前态：文字 / 图标提升到 `text/primary`，必要时辅以 accent 点缀。
  - 非当前态：使用 `text/tertiary`。

Compose 约束：

- 必须显式处理 `WindowInsets.navigationBars`。
- 优先做自定义浮动 dock，不要直接用贴边式系统样式 `NavigationBar`。

### 7.7 Bottom Sheets

统一语法：

- 背景使用 `bg/scrim`。
- sheet 面板使用 `bg/surface`。
- 顶部拖拽条使用 `size/drag-handle` + `outline/subtle`。
- 顶角使用 `radius/sheet`，重点是“从底部抬起”的大圆角感。

建议分三类：

| 类型 | 可见高度范围 | 典型场景 |
|---|---|---|
| `Sheet/Confirm` | `28-40%` | 授权确认、双按钮确认 |
| `Sheet/List` | `52-72%` | 功能总表、账户切换、网络列表 |
| `Sheet/Tall` | `78-88%` | 超长选择器、头像 / 主题选择器 |

Compose 约束：

- 如果使用 `ModalBottomSheet`，必须覆盖 shape、container、scrim、drag handle；不能保留默认浅底样式。
- 选择器类 sheet 中，被选中项优先用 accent 描边或对勾表示，不要整卡改成亮底。

### 7.8 Input Fields

- 输入框统一使用深色填充面，不做白底。
- 单行输入框使用 `size/input-single`，大地址输入框使用 `size/input-large`。
- 圆角使用 `radius/input`。
- placeholder 使用 `text/secondary`，真实输入使用 `text/primary`。
- 可在输入框下外挂 `Button/PillSmall` 型辅助动作。

Compose 约束：

- 大地址输入页属于“大输入框 + 辅助动作 + tabs + 空状态”的组合，不要退化成普通表单列表。

### 7.9 List Rows

统一结构：

- 左侧：头像 / 币种 / 功能图标，尺寸使用 `size/icon-avatar` 或 `size/icon-box`。
- 中间：标题 + 副信息双层文字。
- 右侧：数值、箭头、对勾、辅助链接或状态。
- 行高使用 `size/list-row` 或 `size/list-row-rich`。

常见全局行类型：

- `Row/Setting`
  - 左文案，右箭头或辅助链接。
- `Row/Asset`
  - 左币种图标，中间代币名与价格 / 涨跌，右持仓与折合法币。
- `Row/Selector`
  - 左品牌 / 网络图标，中间名称，右当前对勾。
- `Row/Account`
  - 左头像，中间账户信息，右余额 / 当前态。

Compose 约束：

- 列表优先做卡组内连续行；组与组之间用 `space/section` 拉开。
- 右侧数值列应允许右对齐，避免资产列表和详情指标视觉跳动。

### 7.10 Icon Container Shapes

| 形状 | 归一化规格 | 用法 |
|---|---|---|
| `IconBox/RoundedSquare` | `size/icon-box` + `radius/icon-box` | 功能宫格、工具图标容器、列表首图标 |
| `IconBox/Circle` | `32-40dp` 直径 | 切换按钮、主题圆点、局部小动作 |
| `IconBox/FabCircle` | `size/bottom-center-action` | 底部主按钮、中间切换按钮 |

Compose 约束：

- 宫格入口默认使用 `RoundedSquare`，不要统一换成圆形 icon button。
- 主题色圆点需要“当前态描边”，描边优先用 accent 或浅色高对比。

## 8. Compose 首版默认落点

以下不是源文档精确值，而是首版可以直接落地的单点建议；后续允许回看截图时，只能在前述范围内微调。

```kotlin
object BitgetChromeDp {
    val PageHorizontal = 20.dp
    val PageTop = 16.dp
    val SectionGap = 20.dp
    val CardPadding = 18.dp
    val CardGap = 14.dp
    val CardRadius = 28.dp
    val SheetRadius = 30.dp
    val ButtonRadius = 24.dp
    val InputRadius = 22.dp
    val IconBoxRadius = 14.dp
    val TopBarHeight = 60.dp
    val SearchHeight = 42.dp
    val PrimaryButtonHeight = 52.dp
    val PillButtonHeight = 34.dp
    val SingleInputHeight = 54.dp
    val LargeInputHeight = 104.dp
    val StandardRowHeight = 64.dp
    val RichRowMinHeight = 84.dp
    val IconBoxSize = 44.dp
    val AvatarSize = 36.dp
    val BottomDockHeight = 68.dp
    val BottomCenterAction = 60.dp
    val DragHandleWidth = 36.dp
    val DragHandleHeight = 4.dp
}
```

```kotlin
object BitgetChromeType {
    val Hero = 30.sp
    val AmountXL = 32.sp
    val AmountL = 24.sp
    val TopBarTitle = 19.sp
    val SectionTitle = 17.sp
    val CardTitle = 15.sp
    val BodyM = 14.sp
    val BodyS = 13.sp
    val Caption = 12.sp
    val Button = 15.sp
    val Chip = 12.sp
    val Tab = 14.sp
}
```

## 9. 守卫规则

- 不要给不同页面族各自发明不同的主背景、主按钮、搜索框和底部 dock 风格。
- 不要把默认 Material 组件样式原样带入；Bitget chrome 更接近定制化深色 `Surface` 系统。
- 不要把圆角方块图标容器统一替换成圆形按钮。
- 不要把列表拆成大量散点悬浮卡；优先维持“组卡 + 连续行”的结构。
- 不要因为某一页存在白底按钮，就把白底按钮提升为全局主按钮规则。
