# Bitget App 页面槽位映射（像素级复刻执行版）

## 1. 冻结策略（必须遵守）

本期策略是**页面像素级复刻**，不是壳层复用导向：
- 页面 UI：按 Bitget 参考页做像素级重建（布局、间距、字号、层级、卡片节奏）。
- 业务逻辑：可桥接复用现有 `Routes`、导航跳转、Repository/API 调用与状态流。
- 核心替换位：Bitget `Market/Quote` 槽位在我方必须替换为 `VPN` 槽位（`ShellTab.VPN`）。

---

## 2. Tab/页面族映射（参考页 + 业务替换 + 复用边界）

| 我方 Tab/页面族 | Bitget 参考页（视觉） | 我方业务替换定义 | 逻辑桥接可复用 | UI 执行要求 |
|---|---|---|---|---|
| Home（`APP_SHELL?tab=home`） | Bitget Home 首页资产总览与快捷操作流 | 首页作为 VPN/钱包/订单分发入口 | `ShellTab`, `Routes.APP_SHELL`, 现有 action 回调 | **必须像素级重建** |
| Wallet（`WALLET_HOME/ASSET_DETAIL/RECEIVE/SEND/SEND_RESULT`） | Bitget Wallet 资产、收发、资产详情页 | 保留我方钱包与支付业务语义 | `Routes.WALLET_*`, 发送/收款流程路由 | **必须像素级重建** |
| VPN（`VPN_HOME/PLANS/REGION_SELECTION/ORDER_*`） | Bitget Market/Quote/Trade 列表与交易详情语法 | 以 VPN 替代 Market/Quote（套餐/订单/支付） | `Routes.VPN_*`, 订单与支付桥接 | **必须像素级重建** |
| Discover（增长与导流） | Bitget Discover/DApp 导流页信息密度与卡片节奏 | 不做 DApp 浏览器，承接邀请/佣金/提现/法务入口 | `Routes.INVITE_CENTER/COMMISSION_LEDGER/WITHDRAW/LEGAL_DOCUMENTS` | **必须像素级重建** |
| Profile（`PROFILE` + 法务入口） | Bitget Profile/Me 设置页 | 账户、设置、支持、法务归口 | `Routes.PROFILE`, 现有设置/支持跳转 | **必须像素级重建** |

---

## 3. 页面族执行清单（逐族可落地）

| 页面族 | Bitget 对标页 | 我方页面范围 | 业务替换说明 | 代码策略 |
|---|---|---|---|---|
| 首页+钱包族 | Bitget Home + Wallet 首页/资产页 | `APP_SHELL(home)`, `WALLET_HOME`, `ASSET_DETAIL`, `RECEIVE`, `SEND`, `SEND_RESULT`, `WALLET_PAYMENT` | 资产与收发仍是我方钱包与订单支付上下文 | 逻辑桥接复用，UI 全量重建 |
| VPN 交易族 | Bitget Market/Quote + 交易流程页 | `VPN_HOME`, `PLANS`, `REGION_SELECTION`, `ORDER_CHECKOUT`, `WALLET_PAYMENT_CONFIRM`, `ORDER_RESULT`, `ORDER_LIST`, `ORDER_DETAIL` | 用 VPN 套餐/区域/订单/支付取代币价行情语义 | 逻辑桥接复用，UI 全量重建 |
| Discover/Profile/Growth/Legal 族 | Bitget Discover + Profile + Growth + Legal 相关页 | `APP_SHELL(discover/profile)`, `INVITE_CENTER`, `COMMISSION_LEDGER`, `WITHDRAW`, `PROFILE`, `LEGAL_DOCUMENTS`, `LEGAL_DOCUMENT_DETAIL` | Discover 不做 DApp；承载增长与账户信息结构 | 逻辑桥接复用，UI 全量重建 |

---

## 4. 与真实 bd 任务的精确对齐

## 4j0.22.3 = 像素级复刻 Bitget 首页与 Wallet 页面
- 覆盖范围：`APP_SHELL(home)` + Wallet 页面族（`WALLET_HOME/ASSET_DETAIL/RECEIVE/SEND/SEND_RESULT/WALLET_PAYMENT`）。
- Bitget 参考：Home 资产 feed、Wallet 资产列表与收发页。
- 执行边界：保留现有路由与业务桥接；页面表面全部按像素级复刻重做。

## 4j0.22.4 = 像素级复刻 VPN/套餐/订单/支付页面
- 覆盖范围：`VPN_HOME/PLANS/REGION_SELECTION/ORDER_CHECKOUT/WALLET_PAYMENT_CONFIRM/ORDER_RESULT/ORDER_LIST/ORDER_DETAIL`。
- Bitget 参考：Market/Quote/Trade 及交易详情结果页。
- 执行边界：业务语义改为 VPN（替代行情）；逻辑桥接复用，页面 UI 全量像素级重建。

## 4j0.22.5 = 像素级复刻 Discover/Profile/Growth/Legal 页面
- 覆盖范围：`APP_SHELL(discover/profile)` + `INVITE_CENTER/COMMISSION_LEDGER/WITHDRAW/PROFILE/LEGAL_DOCUMENTS/LEGAL_DOCUMENT_DETAIL`。
- Bitget 参考：Discover 导流结构、Profile 账户设置结构、Growth 信息密度结构。
- 执行边界：不复制 DApp 浏览器业务，只复刻页面形态；逻辑桥接复用，页面 UI 全量像素级重建。

---

## 5. 最终口径

本项目采用“**Bitget 页面形态像素级复刻 + 我方业务语义替换**”策略：  
`Home/Wallet/Discover/Profile` 结构保留，`Market/Quote` 强制替换为 `VPN`；所有页面表面均按像素级重建执行，现有代码仅作为路由与业务逻辑桥接层复用。
