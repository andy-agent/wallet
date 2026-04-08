# Bitget UI Layout / Spec（统一交付版）

本文是 `4j0.22.3`、`4j0.22.4`、`4j0.22.5` 的单一 UI 输入文档，用来取代以下三份分散 master 文档的消费入口：

- `/Users/cnyirui/git/projects/liaojiang/docs/BITGET_PAGE_MATRIX.md`
- `/Users/cnyirui/git/projects/liaojiang/docs/BITGET_CHROME_TOKENS_SPEC.md`
- `/Users/cnyirui/git/projects/liaojiang/docs/BITGET_PAGE_RHYTHM_SPEC.md`

目标不是拼接原文，而是把全局视觉系统、页面族节奏、我方路由映射和任务交接统一为一份可直接落地的规范。

## 1. 适用范围与判读顺序

- 只覆盖已登录态页面与 overlay，不覆盖登录前认证流程。
- 截图统一为竖屏 `720 x 1600` PNG，XML 可见区域统一为 `720 x 1440`。
- 视觉真值优先级：
  1. Bitget 截图决定最终视觉层级、卡片重量、留白和组件关系。
  2. XML dump 决定节点命名、文案、相对 bounds 和可见信息结构。
  3. 本文档负责在精确像素不可证明时给出统一可执行的归一化规则。
- 业务替换总原则：
  - Bitget `Home / Wallet / Discover / Profile` 保留为我方同名或近似壳层。
  - Bitget `Market / Quote / Trade` 视觉语法统一转译到我方 `VPN` 页面族。
  - 不复制 Bitget 原业务名称，只复用版式、节奏和交互层级。
- 所有首版 Android Compose 实现都必须共享一套 `BitgetChromeTokens`，不能按 family 再发明第二套主题。

## 2. 全局 Tokens 与 Chrome

### 2.1 全局视觉语法

- 页面主背景是深炭灰 / 深黑绿，不是纯黑。
- 主 CTA、选中态、对勾、描边、正向强调统一使用亮青蓝色族，不升级为全局绿色体系。
- 文本层级稳定为白字主信息 + 灰字次信息；红色只用于风险/亏损，暖棕色只用于提醒/授权说明。
- 页面结构基本遵循 `顶部 chrome -> 摘要/标题 -> 大圆角卡片/列表组 -> 底部 dock 或底部 CTA`。
- 搜索框、输入框、列表组、图标容器都优先使用深色填充面，而不是浅底描边式 Material。
- 底部 sheet、浮动底部导航、超大圆角卡片是跨 family 的共同壳层语法。

### 2.2 颜色 Tokens

| Token | 归一化范围 | 用途 |
|---|---|---|
| `bg/base` | `H 185-205 / S 8-18% / L 7-12%` | 全局页面底色 |
| `bg/surface` | `bg/base` 基础上 `L +4 到 +8` | 卡片、输入框、列表组、dock、sheet 面板 |
| `bg/surface-strong` | `bg/base` 基础上 `L +8 到 +12` | 图标方块、局部功能块、双卡输入块 |
| `bg/scrim` | 黑色 `45-60%` alpha | sheet、modal、picker 背景暗化 |
| `accent/primary` | `H 188-198 / S 78-95% / L 56-68%` | 主按钮、选中描边、对勾、中间圆形主按钮 |
| `accent/primary-soft` | `accent/primary` 的 `20-32%` alpha | 轻强调背景、提示条、弱态高亮 |
| `text/primary` | `L 92-98%` | 标题、主金额、正文主信息 |
| `text/secondary` | `L 68-82%` | 副标题、说明、placeholder |
| `text/tertiary` | `L 48-64%` | 未选中导航、辅助信息、空状态 |
| `text/disabled` | `L 36-48%` | 禁用文本、禁用图标、禁用按钮 |
| `state/danger` | `H 350-10 / S 70-90% / L 58-68%` | 风险提示、危险按钮、亏损数值 |
| `state/warning-surface` | `H 32-44 / S 35-55% / L 24-36%` | 安全提醒、授权说明、注意事项卡 |
| `outline/subtle` | 白或灰 `10-18%` alpha | 拖拽条、弱分割、轻描边 |
| `outline/accent` | `accent/primary` 的 `70-100%` alpha | 当前钱包卡、当前网络、当前选择项 |

实施约束：

- 不要把正向态全面改成绿色；全局品牌强调仍是青蓝。
- 不要把主背景做成纯黑；必须保留一点绿灰 / 炭灰色温。
- `选择网络 -> 添加网络` 中的白底按钮只能视为局部例外，不能外溢为全局按钮规范。

### 2.3 字体、圆角、间距、尺寸 Tokens

#### 字体层级

| Token | 范围 | 字重建议 | 用途 |
|---|---|---|---|
| `type/hero` | `26-32sp` | `Bold` | 首屏 Hero 标题、增长页大标题 |
| `type/amount-xl` | `28-36sp` | `SemiBold` | 资产总额、主价格、核心 KPI |
| `type/amount-l` | `22-28sp` | `SemiBold` | 详情页主值、卡内主数字 |
| `type/topbar-title` | `18-20sp` | `SemiBold` | 顶栏标题 |
| `type/section-title` | `16-18sp` | `SemiBold` | 分组标题、卡标题 |
| `type/card-title` | `14-16sp` | `Medium` | 列表主标题、卡片标题 |
| `type/body-m` | `14-15sp` | `Regular/Medium` | 正文、列表主文案 |
| `type/body-s` | `13-14sp` | `Regular` | 说明、副文本 |
| `type/caption` | `11-12sp` | `Regular/Medium` | 标签、辅助状态、底部导航文案 |
| `type/button` | `14-16sp` | `SemiBold` | 主按钮、次按钮、底部 CTA |
| `type/chip` | `12-13sp` | `Medium` | chip、tag、状态 pill |
| `type/tab` | `13-15sp` | `Medium` | tabs、时间粒度切换、筛选标签 |

#### 圆角与间距

| Token | 范围 | 用途 |
|---|---|---|
| `radius/sheet` | `28-32dp` | 底部 sheet 顶角 |
| `radius/card` | `24-32dp` | 主卡、列表组、运营卡 |
| `radius/button` | `22-28dp` | 主按钮、底部 CTA |
| `radius/input` | `20-24dp` | 搜索框、输入框、大地址输入框 |
| `radius/icon-box` | `12-16dp` | 图标方块容器 |
| `radius/pill` | `999dp` | chip、胶囊按钮、拖拽条 |
| `space/page-x` | `16-24dp` | 页面左右内边距 |
| `space/page-top` | `12-20dp` | 顶部 chrome 到首个内容块 |
| `space/section` | `16-24dp` | 模块与模块之间 |
| `space/card-padding` | `16-20dp` | 卡片主 padding |
| `space/card-gap` | `12-16dp` | 卡内竖向间距 |
| `space/row-x` | `14-18dp` | 列表行左右 padding |
| `space/row-y` | `12-16dp` | 列表行上下 padding |

#### 尺寸节奏

| Token | 范围 | 用途 |
|---|---|---|
| `size/topbar` | `56-64dp` | 顶栏可用高度 |
| `size/search` | `40-44dp` | 搜索框高度 |
| `size/button-primary` | `48-56dp` | 主按钮、底部 CTA |
| `size/button-pill` | `32-36dp` | 小胶囊动作 |
| `size/input-single` | `52-56dp` | 单行输入框 |
| `size/input-large` | `88-120dp` | 大地址输入区 |
| `size/list-row` | `56-72dp` | 普通列表行 |
| `size/list-row-rich` | `72-96dp` | 高信息密度列表行 |
| `size/icon-box` | `40-48dp` | 图标方块容器 |
| `size/icon-avatar` | `32-40dp` | 头像、币种图标 |
| `size/bottom-dock` | `64-72dp` | 浮动底部 dock |
| `size/bottom-center-action` | `56-64dp` | 中间圆形主按钮 |
| `size/drag-handle` | `32-40dp x 4-6dp` | sheet 顶部拖拽条 |

### 2.4 全局组件合同

| 组件 | 实现合同 |
|---|---|
| `TopBar/Search` | 左侧头像或返回，中间深色胶囊搜索框，右侧 1 到 2 个工具图标；不承载长说明文案。 |
| `TopBar/CenterTitle` | 左侧返回，中间标题，右侧帮助/编辑/更多；高度使用 `size/topbar`。 |
| `TopBar/CloseTitle` | 左侧关闭，中央标题，右侧可选动作；用于收款、选择器、工具页。 |
| `Card/Standard` | 深色 `bg/surface` + `radius/card` + `space/card-padding`；适用于摘要卡、信息卡、列表组。 |
| `Card/Selected` | 在 `Card/Standard` 上叠加 `outline/accent` 的 `1-2dp` 描边；用于当前钱包、当前网络、当前选择项。 |
| `Card/Warning` | 使用 `state/warning-surface`；只用于授权、风险、安全提醒。 |
| `Button/Primary` | `accent/primary` 实底、`radius/button`、`size/button-primary`；主 CTA 唯一高亮。 |
| `Button/SecondaryDark` | 深色实底、与 Primary 同高；用作取消、次级动作。 |
| `Button/PillSmall` | `size/button-pill` + `radius/pill`；用于 `扫一扫`、`粘贴` 等辅助动作。 |
| `Search / Input` | 深色填充面，不做白底描边；`placeholder` 用 `text/secondary`。 |
| `Bottom Dock` | 浮动深色大圆角 dock；左右常规 tab，中间独立圆形主按钮；必须显式处理 `WindowInsets.navigationBars`。 |
| `Bottom Sheet` | 深色面板、顶角 `radius/sheet`、浅灰拖拽条、固定底部 CTA 区；禁止保留默认浅底 Material 样式。 |

全局约束：

- 多行列表优先包在同一个大圆角组卡里，不把每一行拆成独立悬浮卡。
- CTA 的强弱关系必须稳定：每个页面或每个弹层只允许一个最高权重动作。
- 详情页的最终转化按钮优先在底部；确认类弹层使用左右双按钮；输入型页面先让位给输入区。

## 3. 页面族节奏

### 3.1 Home

固定阅读顺序：

`顶部搜索 chrome -> 资产总览 KPI -> 4 个快捷入口 -> 运营主卡 -> 次级卡片流 -> 悬浮底部导航`

规则：

- 资产总览区是首页唯一首屏 Hero，节奏固定为 `主数值 -> 变化值/状态 -> 单主 CTA`。
- 快捷入口只占一行，等权重，不能混入大量副文案。
- 首页卡片流采用 `1 张整宽主卡 + 连续次级卡片`，不做双列瀑布流。
- `理财 landing` 语法可转为我方首页运营专区：`主题 Hero -> Hero 内嵌信息条 -> 横向卡组 -> 纵向列表卡`。

Overlay / Modal：

- `全部功能` 是从首页主栈升起的动作总表，不是独立新页。
- 动作总表节奏固定为 `拖拽条 -> 分组标题 -> 宫格组 -> 下一分组标题 -> 下一组入口`。
- 首页确认弹层采用 `背景业务页 + 前景底部确认 sheet` 模式，背景上下文必须仍可辨认。

### 3.2 Wallet 总览

固定阅读顺序：

`顶部搜索 chrome -> 当前钱包身份行 -> 资产总额与盈亏 -> 3 个快捷动作 -> 2 张业务卡 -> 资产列表标题行 -> 资产列表`

规则：

- 先表达“当前看的是哪个钱包”，再表达“这个钱包有什么资产”。
- 快捷动作必须保持同层同权重的 3 按钮横排。
- 中段双卡是钱包首页的稳定中层，不拆成普通长列表。
- 空仓态不切成独立空页面，必须沿用同一 chrome、同一资产总览区、同一快捷动作和同一双卡结构，只替换列表内容。
- `代币管理` 等尾部动作只出现在列表尾声，不抢首屏层级。

Wallet 切换器：

- 节奏固定为 `标题栏 -> 总资产摘要 -> 当前钱包高亮卡 -> 其他钱包卡 -> 底部单主 CTA`。
- 当前钱包通过描边和先后顺序表达选中，不靠额外解释文字。

### 3.3 Wallet Transaction

资产详情固定顺序：

`资产图标与名称/链名 -> 主数量与法币等值 -> 头部主 CTA -> 信息卡/行情卡 -> 交易历史 -> 底部双动作 -> 弱层级尾部入口`

规则：

- 头部只服务单个资产对象，同层只保留一个高优先级 CTA。
- `收款 / 转账` 闭环统一下沉到底部双动作区。
- `链上行情`、KPI 列、`产品信息` 一类信息都应采用 key/value 信息表，而不是长段落说明。
- `安全检测` 属于尾部弱层级入口，不能与底部双动作抢主权重。

收款页固定顺序：

`关闭 -> 资产图标 -> 标题与网络说明 -> 大二维码 -> 地址卡 -> 提示条 -> 底部单主 CTA`

转账页固定顺序：

`标题 -> 大输入框 -> 两个快捷按钮 -> 页内 tabs -> 空状态或候选内容`

规则：

- 收款页以二维码为主视觉中心，地址卡和提示条只能服务二维码。
- 转账页是输入优先结构，在无有效地址时不应让 CTA 抢在输入区前面。
- `send_result` 没有独立基线时，应继承本 family 的标题区、按钮节奏和结果反馈语法。

### 3.4 VPN

VPN 详情型页面固定顺序：

`返回 + 标题/副标题 -> tabs -> 主 KPI -> 侧边 KPI 列 -> 主可视化区 -> 信息表 -> 底部主 CTA`

规则：

- 这是对象详情页，不是列表页；顶部必须同时容纳主标题和副标题。
- 主 KPI 先给出一个最大的核心数值或状态，右侧一列承载 4 到 5 个关键指标。
- 主可视化区可从 K 线图转译为套餐表现图、节点质量图或订单状态图，但版式角色不变。
- 页面最终转化点仍在底部主 CTA，不把最终 CTA 上移到头部摘要旁边。

VPN 选择 / 下单型页面固定顺序：

`标题栏 -> 风险/说明提示条 -> 上下双卡 -> 中间切换控件 -> 单主 CTA -> 底部模式 dock`

规则：

- 上下双卡是骨架，上卡表达当前选择或输入，下卡表达目标选择或结果。
- 中间圆形切换按钮必须位于双卡之间，即使业务不是完全对称也保留这个节奏点。
- 主 CTA 紧跟双卡之后，不要下沉到长说明区之后。
- 底部工具 dock 属于模式切换层，不等同系统底部导航。

VPN 选择器页固定顺序：

`关闭 -> 标题 -> 搜索框 -> 单选列表 -> 底部单主 CTA`

规则：

- 单选列表整行可点，选中态通过右侧对勾和高亮表达。
- 搜索框必须位于列表之前，过滤是一级动作。

### 3.5 Discover / Growth / Legal

容器首帧规则：

- 最小容器只保留返回箭头和深色背景，表达“容器尚未装载完成”。
- WebView 或远端内容页在加载期不能误用 Profile 设置列表或中心插画空状态。

增长页固定顺序：

`顶部返回/辅助动作 -> 大标题与副标题 -> 首屏 Hero 插画 -> 表单卡 -> tabs 卡 -> 长内容区`

规则：

- Hero 在前，先解释页面主题，再承接邀请码/奖励行为。
- 表单卡是首个操作密集区，必须保持独立大圆角卡身份。
- tabs 卡在表单卡之后，不提前到最顶。
- 长内容区与前面的 Hero/表单卡保持同一纵向滚动流，不做嵌套滚动。

缺图页面派生规则：

- `commission_ledger`、`withdraw`、`legal_documents`、`legal_document_detail/{documentId}` 优先复用 `极简容器 + 单个首屏摘要区 + 后续连续内容区` 的节奏。
- 法务详情更接近 `简单顶部 chrome + 长文内容容器`，CTA 稀少且后置。

### 3.6 Profile

设置主页固定顺序：

`顶部标题栏 -> 单列设置卡列表 -> 关于卡 -> 底部帮助入口`

规则：

- Profile 是设置总表，不是社交 feed，也不是钱包资产页。
- 设置卡列表是主内容区，版本与品牌信息属于次级层，帮助入口是尾声动作。

钱包安全页固定顺序：

`身份摘要 -> 风险等级提示 -> 高亮安全设置卡 -> 普通列表项 -> 危险操作按钮`

规则：

- 先展示账户本体，再进入配置项。
- 风险提示和高亮安全卡必须连续出现，形成安全页核心提醒区。
- `退出钱包` 必须单独沉到底部，与普通列表项留出明显节奏断层。

头像选择器 overlay 固定顺序：

`拖拽条 -> 大头像预览 -> 主题色圆点 -> 头像分组宫格 -> 底部保存按钮`

规则：

- 预览必须先于色板和宫格出现。
- 主题色圆点紧跟预览，不能下沉到宫格之后。
- 保存按钮固定底部，只有真正变更后才可点击。

### 3.7 Overlay / Modal 分层总规则

| 类型 | 典型结构 | CTA 规则 |
|---|---|---|
| 动作总表 sheet | 拖拽条 -> 分组标题 -> 宫格组 -> 下一分组 | 可无 footer CTA，由宫格项直接触发 |
| 选择器 sheet | 标题栏 -> 摘要 -> 选项卡片 -> 底部主 CTA | 只保留一个底部主 CTA |
| 确认 sheet | 标题 -> 正文 -> 双按钮 | 左取消、右确认 |
| 高层 picker sheet | 预览 -> 色板 -> 宫格 -> 底部保存 | 底部保存固定，随变更态启用 |
| 全屏选择页 | 顶部关闭/标题 -> 搜索 -> 单选列表 -> 底部主 CTA | 列表点击负责选择，底部 CTA 负责提交 |

统一约束：

- 所有 overlay 都必须保留父页面上下文可辨识度。
- 单次任务流内不要连续叠三层以上 sheet。
- 滚动内容和底部 CTA 必须分层，CTA 不随内容滚出屏幕。

## 4. 页面族到我方 Routes / Tabs 的映射

| 页面族 | Bitget 来源状态 | 我方 routes / tabs | 落地说明 |
|---|---|---|---|
| Home | 首页默认态 | `Routes.appShell(ShellTab.HOME)` | 直接作为我方首页主参考，复用 `资产总览 + 快捷入口 + 卡片流 + 浮动底栏`。 |
| Home | 全部功能 overlay | `Routes.appShell(ShellTab.HOME)` 的动作总表 state | 复用底部 sheet 的分组宫格语法，映射为套餐、订单、收款、提现、邀请、帮助等快捷动作。 |
| Home / Wallet | 银行卡确认 modal | `Routes.WALLET_PAYMENT`、`Routes.WALLET_PAYMENT_CONFIRM` | 作为支付确认、授权说明、二次确认的统一确认 sheet 模板。 |
| Home / Plans | 理财 landing | `Routes.appShell(ShellTab.HOME)` 运营专区、`Routes.PLANS` | 复用 `Hero + 横滑卡 + 列表卡` 顺序，不建立独立理财业务。 |
| Plans / Checkout | 理财产品详情 | `Routes.PLANS`、`Routes.orderCheckout(planId)` | 复用 `头部摘要 + 单图表 + 信息表 + 单主 CTA` 的详情结构。 |
| Wallet | 钱包持仓态 | `Routes.appShell(ShellTab.WALLET)`、`Routes.WALLET_HOME` | 钱包首页主参考，强调身份行、资产区、3 快捷动作、双卡、资产列表。 |
| Wallet | 钱包空仓态 | `Routes.appShell(ShellTab.WALLET)`、`Routes.WALLET_HOME` | 与持仓态共享同一布局，只替换列表内容和尾部动作。 |
| Wallet | 选择钱包 overlay | `Routes.WALLET_HOME` 的切换器 state | 复用 `摘要 + 当前高亮卡 + 底部新增按钮` 的选择器 sheet。 |
| Wallet Transaction | 资产详情 | `Routes.assetDetail(assetId)` | 直接作为资产详情模板，截图定视觉层级，XML 定节点和文案。 |
| Wallet Transaction | 收款 | `Routes.RECEIVE` | 复用 `二维码 + 地址卡 + 网络提示 + 单主 CTA`。 |
| Wallet Transaction | 转账地址输入 | `Routes.SEND`、`Routes.send(symbol)` | 复用 `大输入框 + 双胶囊动作 + tabs + 候选内容`；`Routes.SEND_RESULT` 从本 family 派生。 |
| VPN 详情 | 行情详情页 | `Routes.appShell(ShellTab.VPN)`、`Routes.VPN_HOME`、`Routes.orderDetail(orderId)` | 转译为 VPN 套餐详情、连接详情或订单详情；保留详情页头部、KPI 列、信息表、底部 CTA。 |
| VPN 选择 / 下单 | 跨链闪兑默认态 | `Routes.VPN_HOME`、`Routes.PLANS`、`Routes.orderCheckout(planId)`、`Routes.WALLET_PAYMENT_CONFIRM` | 转译为套餐选择、区域选择、支付方式、下单确认的双卡骨架。 |
| VPN 选择器 | 选择网络 | `Routes.REGION_SELECTION` | 网络列表直接替换为区域/节点列表，保留搜索 + 单选列表 + 底部主按钮。 |
| Discover / Growth / Legal | 空白容器首帧 | `Routes.appShell(ShellTab.DISCOVER)`、`Routes.INVITE_CENTER`、`Routes.COMMISSION_LEDGER`、`Routes.WITHDRAW`、`Routes.LEGAL_DOCUMENTS`、`Routes.legalDocumentDetail(documentId)` | 作为增长页、法务页、WebView 容器的加载骨架，不误用为 Profile 主页。 |
| Discover / Growth / Legal | 邀请中心 loaded | `Routes.appShell(ShellTab.DISCOVER)`、`Routes.INVITE_CENTER` | Discover 不做 DApp 浏览器，改为增长分发；复用 Hero + 表单卡 + tabs + 长内容区。 |
| Profile | 设置 | `Routes.appShell(ShellTab.PROFILE)`、`Routes.PROFILE` | 直接作为我方 Profile 设置总表模板。 |
| Profile | 钱包安全 | `Routes.PROFILE` 的安全子页 / 内部 state | 复用 `身份摘要 + 风险提示 + 高亮安全卡 + 列表 + 危险操作`。 |
| Profile | 头像选择器 overlay | `Routes.PROFILE` 的头像/主题 overlay state | 复用 `大图预览 + 色板 + 宫格 + 保存` 的高层 picker。 |

## 5. 实现交接

### 5.1 交给 `4j0.22.3`

目标范围：

- `Routes.appShell(ShellTab.HOME)`
- `Routes.appShell(ShellTab.WALLET)`
- `Routes.WALLET_HOME`
- `Routes.assetDetail(assetId)`
- `Routes.RECEIVE`
- `Routes.SEND`
- `Routes.WALLET_PAYMENT`
- `Routes.WALLET_PAYMENT_CONFIRM`

实现顺序：

1. 先落共享 chrome：深色背景、大圆角卡片、亮青蓝主按钮、浮动底部 dock、底部 sheet、深色搜索框。
2. 再做 Home 主栈：`资产总览 -> 4 快捷入口 -> 单列卡片流 -> Home 动作总表 overlay`。
3. 再做 Wallet 固定堆叠：持仓态与空仓态必须共享同一布局。
4. 最后补 Wallet Transaction family：`asset_detail -> receive -> send -> confirm modal / overlay`。

直接要求：

- 首页动作总表和支付确认都必须复用底部 sheet 语法，不另做全屏弹窗。
- `send_result` 没有独立截图时，只能从 `send` family 派生，不重新设计。
- Home 与 Wallet 的壳层 chrome 必须保持同一套 token，不做 per-screen 主题漂移。

### 5.2 交给 `4j0.22.4`

目标范围：

- `Routes.appShell(ShellTab.VPN)`
- `Routes.VPN_HOME`
- `Routes.PLANS`
- `Routes.REGION_SELECTION`
- `Routes.orderCheckout(planId)`
- `Routes.orderDetail(orderId)`

实现顺序：

1. 先实现 `Routes.REGION_SELECTION`，统一 VPN 选择器语法。
2. 再实现 `VPN_HOME / PLANS / orderCheckout` 共享的 `双卡 + 中间切换 + 单主 CTA + 模式 dock`。
3. 最后把 `行情详情页` 和 `理财产品详情` 共同转译为 `orderDetail` / 套餐详情的详情型结构。

直接要求：

- `bitget_discover.*` 在我方只属于 VPN 工具流，不属于 Discover tab。
- `bitget_route_home.*` 是详情型页面输入，不能当作 VPN 首页 feed。
- 套餐详情、连接详情、订单详情必须共享 `主 KPI + 侧边 KPI 列 + 信息表 + 底部 CTA` 的同一家族结构。

### 5.3 交给 `4j0.22.5`

目标范围：

- `Routes.appShell(ShellTab.DISCOVER)`
- `Routes.INVITE_CENTER`
- `Routes.COMMISSION_LEDGER`
- `Routes.WITHDRAW`
- `Routes.LEGAL_DOCUMENTS`
- `Routes.legalDocumentDetail(documentId)`
- `Routes.appShell(ShellTab.PROFILE)`
- `Routes.PROFILE`

实现顺序：

1. 先做 Discover/Growth/Legal 的极简容器首帧，再做增长页 loaded state。
2. 再完成 `invite_center` 的 `Hero -> 表单卡 -> tabs 卡 -> 长内容区` 四段主结构。
3. 然后从同 family 派生 `commission_ledger`、`withdraw`、`legal_documents`、`legal_document_detail`。
4. 最后完成 Profile 设置主页、安全子页和头像选择器 overlay。

直接要求：

- Discover tab 先做增长分发和法务容器，不做 DApp 浏览器。
- Profile 主页只做设置总表，不做社交主页式头图或资产概览。
- 头像或主题选择器必须走高层 sheet，不做独立新页。

## 6. 缺口与假设

- `bitget_route_home.*` 更像股票/行情详情页；在我方应只服务 VPN 详情型页面。
- `bitget_discover.*` 实际更接近交易工具页；因此归入 VPN family，而不是我方 Discover tab。
- `bitget_profile.png` 只能证明 `返回箭头 + 深灰容器` 的首帧骨架，不能推出完整页面内容。
- `bitget_asset_detail.png` 的截图与 XML 明显不一致；落地时必须以截图定视觉层级，以 XML 定节点命名和业务文案。
- `Routes.ORDER_RESULT`、`Routes.ORDER_LIST`、`Routes.COMMISSION_LEDGER`、`Routes.WITHDRAW`、`Routes.LEGAL_DOCUMENTS`、`Routes.legalDocumentDetail(documentId)` 没有 1:1 独立截图，只能从所属 family 的版式语法派生。
- 以上截图和 dump 依赖 `/tmp` 下的绝对路径；若任务跨 session 执行，需先确认素材仍存在。
