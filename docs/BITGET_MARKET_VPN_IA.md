# Bitget Market + VPN IA 恢复提案

> 目标：在保留当前 Compose `BitgetAppShell` 和 VPN 一等业务位的前提下，恢复必须存在的 Bitget `Market / Quote` 内容，不回退到旧的占位壳。

## 0. 当前基线与问题

当前仓库里有两个已经成型但互相冲突的事实：

1. 文档基线把 Bitget `Market / Quote / Trade` 几乎整体转译到了 `VPN`。
   - 见 `docs/BITGET_APP_PAGE_SLOT_MAP.md`
   - 见 `docs/BITGET_PAGE_MATRIX.md`
   - 见 `docs/BITGET_UI_LAYOUT_SPEC.md`
2. 当前运行中的 Compose 壳层已经不是旧 placeholder，而是有独立 bottom tabs 的 `BitgetAppShell`。
   - `ShellTab` 当前为 `HOME / WALLET / VPN / DISCOVER / PROFILE`
   - `BitgetAppShell` 当前把 `DISCOVER` 直接渲染成 `InviteCenterPage`
   - `AppNavGraph` 仍把导航层定义为“compileable placeholder graph wiring”，说明 IA 还没最终收口

结果是：

- 我们保住了 VPN，但把 Bitget 壳层里必须存在的 `行情` 入口抹掉了。
- 当前 `Discover` 承载的是增长/法务，不是 Bitget 意义上的市场 family。
- `VPN` 现在同时承担“业务核心”和“Bitget 市场替身”两个职责，后续页面很容易继续混写。

本提案的结论是：**恢复 `Market` 为独立 tab；保留 `VPN` 为中心业务 tab；增长/法务从 bottom tab 下沉到 secondary pages。**

## 1. 必须恢复的 Bitget 市场内容

这里说的“必须恢复”，不是恢复 Bitget 真实交易业务，而是恢复它在壳层里不可缺少的市场浏览语法。

### 1.1 必须恢复

1. `Market / 行情` 的一级 bottom-tab 入口
   - 这是 Bitget 壳层语法的一部分，不能继续完全消失。
   - 当前最小可执行做法：把现有 `ShellTab.DISCOVER` 替换为 `ShellTab.MARKET`。

2. `Market` 的一级列表页
   - 必须有一个能承接“看行情/看标的”的壳层首页，而不是用户一进来就落到 `VPN_HOME`。
   - 首版不要求交易深度或真实撮合，但必须有：
     - 顶部搜索/筛选位
     - 至少一组行情分段或榜单切换
     - 可点击的 quote rows / cards
     - 进入详情页的稳定路径

3. `Quote Detail` 二级详情页
   - 直接使用仓库里已存在的 `bitget_route_home.*` 作为强输入。
   - 必须保留的结构：
     - `行情 / 详情` 顶部双 tabs
     - 主价格/KPI 位
     - 右侧关键指标列
     - 主图表区
     - 指标切换行
     - 底部主 CTA

4. `Market -> Quote Detail` 的明确跳转链路
   - 不能只剩一张详情图而没有市场入口。
   - 也不能继续把这条链路伪装成 `VPN` 套餐详情。

### 1.2 不需要恢复

1. Bitget 原生交易、合约、闪兑、股票真实业务
   - 这些不是当前产品核心，不进入首版 mandatory scope。

2. Bitget 的旧业务占位页
   - 不需要为了“有 Market”再造一个空壳 tab 或旧 `ShellModel` 列表。

3. 把 `VPN_HOME` 继续伪装成 `Market`
   - 这会继续污染信息架构，后面所有页面会更难拆。

## 2. 最终 IA 归位

## 2.1 Bottom Tabs

建议最终 bottom tabs 固定为：

1. `Home`
2. `Market`
3. `VPN`
4. `Wallet`
5. `Profile`

原因：

- 这和 Bitget 的底部壳层语法最接近。
- 保住现有五 tab 结构，不需要改成六 tab。
- `VPN` 继续占中心业务位，不退回“只是 Market 替身”。
- `Discover` 当前承载的是增长/法务，更适合下沉到二级页。

### 2.2 Shell 内各 tab 职责

#### Home

- 继续保留当前 `BitgetHome` 风格首页。
- 负责总览、快捷分发、运营卡片流。
- 继续放：
  - 套餐入口
  - 钱包入口
  - 订单入口
  - 邀请/帮助等 action sheet 入口

#### Market

- 新增独立市场 family。
- 只承担“看市场、进详情”的壳层职责。
- 不承担 VPN 购买、区域选择、订单支付。

#### VPN

- 保留为中心业务 family。
- 只承担：
  - VPN 首页
  - 套餐
  - 区域选择
  - 订单 checkout / result / detail
- 不再承担 Bitget `Market / Quote` 的替身职责。

#### Wallet

- 维持当前钱包 family，不挪出 bottom tabs。

#### Profile

- 维持设置/账户/支持总表。
- 增长、法务、帮助统一从这里和 Home 分发进入。

### 2.3 下沉到 Secondary Pages 的内容

原来挂在 `Discover` tab 的内容，不再占一级 tab，改为 secondary pages：

- `InviteCenterPage`
- `CommissionLedgerPage`
- `WithdrawPage`
- `LegalDocumentsListPage`
- `LegalDocumentDetailPage`
- `Support / About / Settings`

入口放置：

1. `Home` 的 actions sheet
2. `Profile` 的设置总表
3. 必要时从 `Wallet` 或 `VPN` 内部的次级入口跳转

## 3. Market 与 VPN 如何共存

核心原则：**Market 是壳层内容，VPN 是业务核心；二者并存，但不互相伪装。**

### 3.1 不采用的做法

1. 不把 `Market` 再次整体映射为 `VPN`
2. 不把 `Discover` 继续当作市场的伪替身
3. 不回到旧的 placeholder shell / `ShellGuestTabContent` 逻辑做长期方案

### 3.2 采用的做法

1. 在当前 `BitgetAppShell` 上直接改位，不重做总壳
   - 保留现有 Compose shell、背景、bottom bar、Home 首屏
   - 只调整 tab 枚举、顺序、渲染目标和 secondary entry

2. `Market` 做公开可浏览，`VPN` 保持业务 gated
   - `Market` 可以允许访客浏览市场列表和详情
   - `VPN` 继续对连接、订单、区域选择保持登录/订阅门槛
   - 这样 IA 上更清晰，也更符合“看市场是公共内容，VPN 是产品业务”的分层

3. `Market` 与 `VPN` 通过卡片/CTA 互链，而不是共享同一页面
   - `Market` 详情页底部 CTA 可以是“去 VPN 订阅”或“查看可用线路”
   - `VPN` 首页可以有“市场热点”卡片，但它只是 cross-link，不是主结构

### 3.3 首版页面树

```text
APP_SHELL
├── Home
├── Market
│   ├── MarketOverviewPage
│   └── MarketQuoteDetailPage
├── VPN
│   ├── VPNHomePage
│   ├── PlansPage
│   ├── RegionSelectionPage
│   ├── OrderCheckoutPage
│   ├── OrderResultPage
│   └── OrderDetailPage
├── Wallet
└── Profile
    ├── InviteCenterPage
    ├── CommissionLedgerPage
    ├── WithdrawPage
    ├── LegalDocumentsListPage
    └── LegalDocumentDetailPage
```

## 4. 可执行落地方案

### 4.1 第一阶段：先改壳层归位

1. `ShellTab.DISCOVER` 改为 `ShellTab.MARKET`
2. bottom nav 顺序改成 `HOME / MARKET / VPN / WALLET / PROFILE`
3. `BitgetAppShell` 的 tab 渲染改成：
   - `MARKET -> MarketOverviewPage`
   - `VPN -> VPNHomePage`
   - `PROFILE -> ProfilePage`
4. 现有增长/法务入口从 `DISCOVER` tab 下沉到：
   - Home action sheet
   - Profile list

### 4.2 第二阶段：补 Market family

1. 新增 `MarketOverviewPage`
   - 先做列表壳层和跳转，不等真实市场数据接入
2. 新增 `MarketQuoteDetailPage`
   - 直接消费 `bitget_route_home.*` 语法
3. 定义独立 route
   - `Routes.MARKET_HOME`
   - `Routes.MARKET_QUOTE_DETAIL`
   - 如有需要，再补 `Routes.MARKET_SEARCH`

### 4.3 第三阶段：清理文档口径

当前文档里“Bitget `Market / Quote / Trade` 一律转译到 VPN”这条规则必须废止，改成：

- `Market / Quote` 恢复为独立 market family
- `Trade` 相关视觉语法只在可证明需要时借给 VPN 下单 flow
- `Discover` 不再承担市场替身职责

## 5. 需要 follow-up 的现有任务

### 5.1 必须续做的任务

1. `liaojiang-4j0.22.4`
   - 当前定义是“像素级复刻 VPN/套餐/订单/支付页面”
   - 需要拆口径：
     - `VPN` family 继续做套餐/订单/支付
     - 从中剥离 `Market / Quote`，不再把 market detail 写进 VPN

2. `liaojiang-4j0.22.5`
   - 当前定义里 `Discover` 承载增长/法务
   - 需要改成“增长/法务/Profile 的 secondary surfaces”
   - 不再拥有 bottom-tab 一级位置

3. `liaojiang-4j0.12`
   - 当前负责桥接 VPN/订单 Compose 页面到现有逻辑
   - 新 IA 下仍然要做，但只负责 VPN family，不再混入 market family

4. `liaojiang-4j0.13`
   - 当前负责钱包/增长/个人/法务桥接
   - 需要吸收原 `Discover` 下沉后的入口调整

### 5.2 建议新增但可挂靠现有串的工作

如果不新开 beads 子任务，最少也要在 `4j0.22.4` 下面明确新增两个 implementation slice：

1. `MarketOverview`
2. `MarketQuoteDetail`

否则实现时还会继续把 market 内容塞回 VPN。

## 6. 需要 follow-up 的代码/文档文件

### 6.1 代码文件

必须修改：

- `code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/composeui/navigation/ShellTab.kt`
- `code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/composeui/navigation/Routes.kt`
- `code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/composeui/navigation/AppNavGraph.kt`
- `code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/ui/compose/BitgetAppShell.kt`

大概率新增：

- `code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/composeui/pages/market/MarketOverviewPage.kt`
- `code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/composeui/pages/market/MarketQuoteDetailPage.kt`

可能联动：

- `code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/composeui/navigation/DeepLinkHandler.kt`
- `code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/composeui/pages/profile/ProfilePage.kt`
- `code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/composeui/pages/growth/InviteCenterPage.kt`
- `code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/composeui/pages/legal/LegalDocumentsListPage.kt`

### 6.2 文档文件

必须回写：

- `docs/BITGET_APP_PAGE_SLOT_MAP.md`
- `docs/BITGET_PAGE_MATRIX.md`
- `docs/BITGET_UI_LAYOUT_SPEC.md`

建议同步：

- `docs/BITGET_APK_FEATURE_COMPARISON.md`
- `docs/current-status.md`

## 7. 最终决策口径

最终 IA 口径固定为：

1. **Bitget 必须恢复的内容是 `Market / Quote` 壳层，不是 Bitget 真实交易业务。**
2. **`Market` 必须回到 bottom tabs。**
3. **`VPN` 必须保留为独立一等业务 tab，不能继续充当市场替身。**
4. **增长/法务/支持继续保留，但下沉到 Home/Profile 的 secondary pages。**
5. **实现基于当前 `BitgetAppShell` 演进，不回退到旧 placeholder shell。**

这是一条最小改动、最容易落地、也最不容易在后续实现里继续把 Market 与 VPN 混写的 IA 方案。
