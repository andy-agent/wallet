# Bitget 登录态页面矩阵（统一基线）

本文合并 master 上以下两份源文档，并补入当前代码中的 `Routes` / `ShellTab` 常量，作为 `liaojiang-4j0.22.2` 的唯一输入基线：

- `/Users/cnyirui/git/projects/liaojiang/docs/BITGET_CAPTURE_INVENTORY.md`
- `/Users/cnyirui/git/projects/liaojiang/docs/BITGET_APP_PAGE_SLOT_MAP.md`

## 判读规则

- 只覆盖已登录态；截图统一为竖屏 `720 x 1600` PNG，XML 可见区域统一为 `720 x 1440`。
- 做像素级复刻时，以截图决定最终视觉真值，以 XML dump 决定节点名称、文案和相对 bounds。
- 共用视觉语法固定为：深色背景、大圆角卡片、亮青蓝主 CTA、底部圆角导航或工具 dock、底部 sheet 顶部带拖拽条。
- 总替换规则：Bitget `Home/Wallet/Discover/Profile` 保留为我方同名或近似壳层；Bitget `Market/Quote/Trade` 语法一律转译到我方 `VPN` 页面族，不再实现原行情业务。
- 后续任务消费关系：`4j0.22.3` 使用 Home + Wallet + Wallet Transaction；`4j0.22.4` 使用 VPN 替代基线；`4j0.22.5` 使用 Discover/Growth/Legal + Profile。

## Home 基线（供 4j0.22.3 使用）

| 页面族 | 具体页面/状态 | 绝对截图路径 | 绝对 dump 路径 | 推断入口路径 | 关键可见组件 | 我方对应槽位/路由 | 业务替换含义 |
|---|---|---|---|---|---|---|---|
| Home | 首页默认态 | `/tmp/liaojiang-screens/bitget_home.png` | `/tmp/bitget-uix/bitget_home.xml` | 应用启动默认落在首页，或底部导航切回首页 | 头像 + 全局搜索；资产总额 + `去充值`；`理财/赚币/DApp/全部` 四入口；纵向运营卡片流；5 槽悬浮底部导航 | `Routes.appShell(ShellTab.HOME)` (`app_shell?tab=home`) | 直接作为我方首页主参考；保留“资产总览 + 快捷分发 + 卡片流 + 浮动底栏”语法，把入口替换为 VPN、钱包、订单、邀请、支持等我方业务。 |
| Home | 全部功能 overlay | `/tmp/liaojiang-screens/bitget_home_all_actions_overlay.png` | `/tmp/bitget-uix/bitget_home_all_actions_overlay.xml` | 首页点击 `全部` | 暗化首页背景；大圆角底部 sheet；`热门功能` 双行宫格；`资产管理` 分组；图标落在深灰圆角方块内 | `Routes.appShell(ShellTab.HOME)` 的动作总表 / overlay state | 不复制 Bitget 功能项本身；保留“分组宫格 + 底部 sheet”语法，映射为套餐、订单、收款、提现、邀请、帮助等快捷动作总表。 |
| Home | 银行卡申请状态授权确认 | `/tmp/liaojiang-screens/bitget_home_bankcard_confirm_modal.png` | `/tmp/bitget-uix/bitget_home_bankcard_confirm_modal.xml` | 首页 `全部` 面板点击 `银行卡` 后出现确认弹窗 | 背景是银行卡落地页；前景是底部确认 sheet；标题 `提示`；正文授权说明；左右双按钮 `取消/确认` | 主归属 `Routes.WALLET_PAYMENT` (`wallet_payment`)；次归属 `Routes.WALLET_PAYMENT_CONFIRM` (`wallet_payment_confirm`) 的确认 modal 语法 | 不实现银行卡申请；保留“背景业务页 + 底部确认 sheet + 双按钮”模式，作为钱包支付确认、授权说明、二次确认弹窗的统一模板。 |
| Home | 理财 landing | `/tmp/liaojiang-screens/bitget_home_finance_landing.png` | `/tmp/bitget-uix/bitget_home_finance_landing.xml` | 首页点击 `理财`，或钱包页理财卡入口 | 蓝色渐变 Hero；横滑收益卡；`稳定币理财` 列表卡；大圆角与深蓝到深炭色过渡背景 | `Routes.appShell(ShellTab.HOME)` 的运营专区模块；次级可借给 `Routes.PLANS` (`plans`) | 不建立独立理财业务；保留“Hero + 横滑卡 + 列表卡”节奏，用作首页运营专区、套餐推荐区或活动专题区的视觉模板。 |
| Home | 理财产品详情 | `/tmp/liaojiang-screens/bitget_home_finance_product_detail.png` | `/tmp/bitget-uix/bitget_home_finance_product_detail.xml` | 理财首页点击 `USDC` 收益卡或列表项 | 产品头部；单条收益曲线；周期切换；`产品信息` 信息表；单主 CTA `申购` | `Routes.PLANS` (`plans`) / `Routes.orderCheckout(planId)` (`order_checkout/{planId}`) 的详情信息区，无独立 1:1 route | 不做理财申购；保留“头部摘要 + 单图表 + 信息表 + 单主 CTA”语法，转成套餐说明、下单前详情、支付前解释区。 |

## Wallet 总览基线（供 4j0.22.3 使用）

| 页面族 | 具体页面/状态 | 绝对截图路径 | 绝对 dump 路径 | 推断入口路径 | 关键可见组件 | 我方对应槽位/路由 | 业务替换含义 |
|---|---|---|---|---|---|---|---|
| Wallet | 首页默认持仓态 | `/tmp/liaojiang-screens/bitget_wallet.png` | `/tmp/bitget-uix/bitget_wallet.xml` | 底部导航点击 `钱包` | 头像 + 搜索；钱包身份行；资产总额与当日盈亏；`转账/收款/交易历史` 三按钮；`银行卡/理财` 卡；代币列表；底部导航选中钱包 | `Routes.appShell(ShellTab.WALLET)` (`app_shell?tab=wallet`) -> `Routes.WALLET_HOME` (`wallet_home`) | 直接作为我方钱包首页主参考；保留“身份行 + 资产区 + 快捷动作 + 列表”结构，承接我方钱包和支付资产语义。 |
| Wallet | 首页概览空仓态 | `/tmp/liaojiang-screens/bitget_wallet_home_overview.png` | `/tmp/bitget-uix/bitget_wallet_home_overview.xml` | 切换到另一个资产很少的钱包后落在同一首页 | 与持仓态同 chrome；总资产仍为 `$0.00`；`银行卡/理财` 卡保留；代币列表变为零资产；底部露出 `代币管理` | `Routes.appShell(ShellTab.WALLET)` (`app_shell?tab=wallet`) -> `Routes.WALLET_HOME` (`wallet_home`) 的空仓 state | 这是钱包首页零状态模板；后续空数据页面不要另起版式，沿用同一 chrome 与卡片骨架，仅替换列表内容。 |
| Wallet | 选择钱包 overlay | `/tmp/liaojiang-screens/bitget_wallet_account_switcher.png` | `/tmp/bitget-uix/bitget_wallet_account_switcher.xml` | 钱包首页点击顶部邮箱/地址行的下拉箭头 | 暗化钱包首页背景；标题 `选择钱包`；资产总额；高亮当前钱包卡；底部整宽 `添加钱包` 按钮 | `Routes.WALLET_HOME` (`wallet_home`) 的钱包切换 overlay state | 直接转成我方账户/钱包切换器；保留“账户卡高亮 + 底部新增按钮 + sheet”模式。 |

## Wallet Transaction 基线（供 4j0.22.3 使用）

| 页面族 | 具体页面/状态 | 绝对截图路径 | 绝对 dump 路径 | 推断入口路径 | 关键可见组件 | 我方对应槽位/路由 | 业务替换含义 |
|---|---|---|---|---|---|---|---|
| Wallet Transaction | 资产详情 | `/tmp/liaojiang-screens/bitget_asset_detail.png` | `/tmp/bitget-uix/bitget_asset_detail.xml` | 钱包首页点击 `USD24` 资产行 | 资产头部与链名；主数量 + 法币等值；亮青蓝 `交易` 按钮；`链上行情` 卡；`交易历史`；底部 `收款/转账`；`安全检测` | `Routes.assetDetail(assetId)` (`asset_detail/{assetId}`)；次级影响 `Routes.WALLET_PAYMENT` (`wallet_payment`) 的资产上下文区 | 直接作为资产详情模板；截图负责版式与 chrome，XML 负责文案和节点命名。 |
| Wallet Transaction | 收款 | `/tmp/liaojiang-screens/bitget_receive.png` | `/tmp/bitget-uix/bitget_receive.xml` | 资产详情页点击 `收款` | 顶部关闭；资产图标；收款标题与网络说明；大二维码；地址卡；Gas 福利提示条；底部 `分享给 TA` | `Routes.RECEIVE` (`receive`) | 直接作为我方收款页参考；保留“二维码 + 地址卡 + 网络提示 + 单主 CTA”语法。 |
| Wallet Transaction | 转账地址输入 | `/tmp/liaojiang-screens/bitget_send.png` | `/tmp/bitget-uix/bitget_send.xml` | 资产详情页点击 `转账` | 返回 + 标题 `收款地址`；大输入框；`扫一扫/粘贴` 双胶囊按钮；`我的钱包/地址本` 双标签；空状态提示 | `Routes.send(symbol)` (`send?symbol=...`) / `Routes.SEND` (`send`) | 直接作为我方转账输入页参考；`Routes.SEND_RESULT` (`send_result`) 没有单独截图时，应继承本组的标题区、按钮节奏和结果反馈语法。 |

## VPN 替代基线（供 4j0.22.4 使用）

说明：本组虽然源截图分别出现在 `Home family` 与 `Discover` 下，但对我方来说一律视为 Bitget `Market/Quote/Trade` 语法的替代输入，统一落到 `VPN` 页面族。

| 页面族 | 具体页面/状态 | 绝对截图路径 | 绝对 dump 路径 | 推断入口路径 | 关键可见组件 | 我方对应槽位/路由 | 业务替换含义 |
|---|---|---|---|---|---|---|---|
| VPN | 行情详情页（CRWVON / CoreWeave） | `/tmp/liaojiang-screens/bitget_route_home.png` | `/tmp/bitget-uix/bitget_route_home.xml` | 首页或行情进入标的详情；更像股票/行情详情而非首页内容 | 返回 + 标题/副标题；`行情/详情` tabs；主价格与涨跌；右侧指标列；时间粒度条；K 线图 + 十字光标信息卡；底部 `交易` CTA | 主参考 `Routes.appShell(ShellTab.VPN)` (`app_shell?tab=vpn`)、`Routes.VPN_HOME` (`vpn_home`)、`Routes.orderDetail(orderId)` (`order_detail/{orderId}`) | Bitget 行情详情语法在我方替换为 VPN 套餐详情、连接详情或订单详情；价格/涨跌/指标改写为套餐名、区域、到期时间、状态、配额、节点表现等业务字段。 |
| VPN | 跨链闪兑默认态 | `/tmp/liaojiang-screens/bitget_discover.png` | `/tmp/bitget-uix/bitget_discover.xml` | 资产详情 `交易`、底部中间主入口，或交易工具集合页进入 `跨链闪兑` | 顶部标题与工具图标；风险提示条；上下双深色输入卡；中间切换按钮；主 CTA；底部工具 dock（`闪兑/限价/合约/股票` + 关闭） | 主参考 `Routes.VPN_HOME` (`vpn_home`)、`Routes.PLANS` (`plans`)、`Routes.orderCheckout(planId)` (`order_checkout/{planId}`)、`Routes.WALLET_PAYMENT_CONFIRM` (`wallet_payment_confirm`) | 不保留 swap 业务；把“双卡 + 切换 + 主 CTA + 工具 dock”转成套餐选择、区域选择、支付方式、下单确认和 VPN 次级导航语法。 |
| VPN | 选择网络 | `/tmp/liaojiang-screens/bitget_discover_globe.png` | `/tmp/bitget-uix/bitget_discover_globe.xml` | 跨链闪兑页点击右上网络筛选/地球入口 | 关闭 + 标题；搜索框；单选网络列表；右侧对勾；底部 `添加网络` 主按钮 | `Routes.REGION_SELECTION` (`region_selection`) | 网络列表直接替换为区域/节点列表；保留“搜索 + 单选列表 + 底部主按钮”语法。 |

## Discover / Growth / Legal 基线（供 4j0.22.5 使用）

说明：当前没有 Bitget 原生 DApp/Discover 主页截图，后续 Discover tab 先以增长页、WebView 容器和法务容器的语法来落地。

| 页面族 | 具体页面/状态 | 绝对截图路径 | 绝对 dump 路径 | 推断入口路径 | 关键可见组件 | 我方对应槽位/路由 | 业务替换含义 |
|---|---|---|---|---|---|---|---|
| Discover / Growth / Legal | 空白容器或首帧加载态 | `/tmp/liaojiang-screens/bitget_profile.png` | `/tmp/bitget-uix/bitget_profile.xml` | 高概率来自 `设置 -> 邀请中心` 或某个 Profile/WebView 容器启动瞬间 | 整屏深灰底；唯一明确 chrome 是左上返回箭头；XML 无其他业务节点 | `Routes.appShell(ShellTab.DISCOVER)` (`app_shell?tab=discover`) 的首帧容器；共享给 `Routes.INVITE_CENTER` (`invite_center`)、`Routes.COMMISSION_LEDGER` (`commission_ledger`)、`Routes.WITHDRAW` (`withdraw`)、`Routes.LEGAL_DOCUMENTS` (`legal_documents`)、`Routes.legalDocumentDetail(documentId)` (`legal_document_detail/{documentId}`) | 这是增长页/法务页/WebView 容器的加载骨架参考，不应误当作 Profile 主列表。 |
| Discover / Growth / Legal | 邀请中心 loaded | `/tmp/liaojiang-screens/bitget_profile_loaded.png` | `/tmp/bitget-uix/bitget_profile_loaded.xml` | `设置 -> 邀请中心` | 返回 + `奖励记录`；大标题与副标题；首屏彩色插画 Hero；邀请码创建卡；`推荐奖励` tabs；WebView 承载的规则和分享内容 | `Routes.appShell(ShellTab.DISCOVER)` (`app_shell?tab=discover`) 的增长分发风格；主归属 `Routes.INVITE_CENTER` (`invite_center`)；次级参考 `Routes.COMMISSION_LEDGER` / `Routes.WITHDRAW` | Discover 不做 DApp 浏览器，改为增长分发；保留“Hero + 表单卡 + tabs + 规则区”的高信息密度语法，承接邀请、佣金、提现入口。 |

## Profile 基线（供 4j0.22.5 使用）

| 页面族 | 具体页面/状态 | 绝对截图路径 | 绝对 dump 路径 | 推断入口路径 | 关键可见组件 | 我方对应槽位/路由 | 业务替换含义 |
|---|---|---|---|---|---|---|---|
| Profile | 设置 | `/tmp/liaojiang-screens/bitget_profile_settings_page.png` | `/tmp/bitget-uix/bitget_profile_settings_page.xml` | 首页或钱包页右上角个人/设置入口 | 返回 + 标题 `设置`；大圆角列表卡；`钱包安全/地址簿/消息推送/偏好设置/节点与线路/邀请中心`；版本卡；底部帮助入口 | `Routes.appShell(ShellTab.PROFILE)` (`app_shell?tab=profile`) / `Routes.PROFILE` (`profile`) | 直接作为我方 Profile 主列表模板；保留“设置汇总 + 帮助/关于”归口语义。 |
| Profile | 钱包安全 | `/tmp/liaojiang-screens/bitget_profile_security_page.png` | `/tmp/bitget-uix/bitget_profile_security_page.xml` | 设置页点击 `钱包安全` | 头像 + 邮箱 + ID；安全等级提示；高亮 `安全设置` 卡；多条安全入口；底部 `退出钱包` 危险按钮 | `Routes.PROFILE` (`profile`) 的安全子页 / 内部 state，目前无独立公开 route 常量 | 直接作为账户安全详情页模板；保留“头部身份摘要 + 风险提示卡 + 分组列表 + 危险操作”结构。 |
| Profile | 头像选择器 overlay | `/tmp/liaojiang-screens/bitget_profile_avatar_picker_overlay.png` | `/tmp/bitget-uix/bitget_profile_avatar_picker_overlay.xml` | 钱包安全页点击顶部头像 | 暗化背景；超高底部 sheet；当前头像大图；主题色圆点；头像宫格分组；底部 `保存` 按钮 | `Routes.PROFILE` (`profile`) 的头像/主题 overlay state | 直接作为我方头像或主题选择器模板；保留“大图预览 + 色板 + 宫格 + 保存”语法。 |

## 实现优先级 / 顺序

1. 先做共享 chrome 和 tokens：深色背景、亮青蓝主 CTA、大圆角卡片、浮动底栏、底部 sheet、搜索框和列表行，这是 4j0.22.3/4/5 的共用基础。
2. `4j0.22.3` 按 `Home 首页默认态 -> Wallet 持仓态 -> Wallet 空仓态 -> 资产详情 -> 收款/转账 -> overlay/modal` 的顺序推进，先拿下壳层和主交互路径。
3. `4j0.22.4` 先做 `选择网络`，再做 `跨链闪兑默认态` 的双卡下单语法，最后把 `行情详情页` 的详情结构翻译到 VPN 套餐/订单详情。
4. `4j0.22.5` 先做 Discover/Growth/Legal 的容器与邀请页，再做 Profile 主列表、安全子页和头像选择器；法务、佣金、提现缺少直拍时先复用同 family 语法。

## 已知缺口 / 不确定映射

- `bitget_route_home.*` 更像股票/行情详情页，不应被误用为 Home 内容页；它在我方应优先服务 `VPN` 详情型页面。
- `bitget_discover.*` 实际更接近交易工具页而非传统 Discover；因此被归入 `VPN` 基线，而我方 Discover 仍缺一张真正对标 Bitget DApp/发现页的截图。
- `bitget_profile.png` 只能确认“返回箭头 + 深灰容器”的首帧加载骨架，无法从产物本身确定最终页面标题或骨架细节。
- `bitget_asset_detail.png` 的视觉内容与 XML 明显不一致；落地时必须以截图定视觉层级，以 XML 定节点名称和业务文案。
- `Routes.ORDER_RESULT`、`Routes.ORDER_LIST`、`Routes.LEGAL_DOCUMENTS`、`Routes.LEGAL_DOCUMENT_DETAIL`、`Routes.COMMISSION_LEDGER`、`Routes.WITHDRAW` 没有 1:1 独立截图，后续需从同 family 共享语法派生。
- 推断：所有截图和 dump 都在 `/tmp` 下；若 `4j0.22.2/3/4/5` 跨 session 执行，需先确认这些绝对路径上的产物仍然存在。
