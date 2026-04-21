# Wallet Local-First Cache And Price Refresh Implementation Plan

> **For Claude:** REQUIRED SUB-SKILL: Use `executing-plans` to implement this plan task-by-task.

**Goal:** 让钱包相关页面彻底改成“本地数据库优先显示、后端异步刷新”，同时把代币价格刷新节奏固定为“后端 5 分钟缓存、APP 本地 1 分钟刷新、每次打开 APP 立即向后端拉新”。

**Architecture:** 后端继续作为唯一业务真相和价格聚合层，负责市场价格缓存与 `wallet/overview` / `wallet/balances` 统一组装；Android 统一以 Room 作为业务数据的一线读取层，ViewModel 先读 Room 快照渲染，再异步刷新后端并回写 Room，禁止页面首屏直接依赖远端返回。所有价格和资产路由共用同一套缓存策略，避免首屏空白和上下文不一致。

**Tech Stack:** NestJS + MarketService/CoinGecko/DexScreener；Android Room + PaymentRepository + Compose ViewModel。

---

## 需求摘要

- 钱包页顶部的价格、代币管理、资产详情等价格数据必须先显示本地数据库缓存，不能因为等后端返回而空白。
- 后端服务器先缓存价格，缓存时间 5 分钟。
- APP 本地也缓存价格及 overview 数据，页面优先显示本地数据库结果。
- APP 每次打开时必须立即异步向后端更新一次价格和相关 overview 数据。
- APP 在活跃使用期间，对价格相关页面每 1 分钟向后端刷新一次，再回写本地数据库。
- 当前所有仍未进入本地数据库的业务数据，需要先盘点，再逐路由改成 local-first。

## 当前现状与未入库清单

### 已进入 Room 的业务数据

- 用户：`users`
- 订单：`orders`
- 支付历史：`payment_history`
- 钱包图谱：`local_wallets` / `local_wallet_chain_accounts`
- 自定义代币：`local_custom_tokens`
- 代币显示控制：`local_token_visibility_entries`
- 代币图标：`local_token_icon_cache`
- VPN 节点：`vpn_node_cache` / `vpn_node_runtime`
- 钱包公开地址：`wallet_public_address_cache`
- 收款上下文：`wallet_receive_context_cache`
- 钱包总览：`wallet_overview_cache`

### 仍未进入 Room，仅存在 Prefs/MMKV 的业务数据

- 套餐列表 `plans`
- 钱包资产目录 `wallet asset catalog`
- 订阅摘要元数据
  - `planCode`
  - `status`
  - `daysRemaining`
- VPN 选择摘要元数据
  - `lineName`
  - `nodeId`
  - `nodeName`
  - `sessionStatus`
- 当前订单 ID
- 订阅 URL / Marzban 用户名
- 最近签发配置相关元数据

### 当前完全未做本地数据库缓存的业务数据

- `wallet lifecycle`
- `wallet balances`
- `subscription` 完整响应
- `vpn status` 完整响应
- `vpn regions`
- `referral overview`
- `referral share context`
- `commission summary`
- `commission ledger`
- `withdrawals`
- `wallet detail`（单钱包详情）

### 当前各路由/页面的数据读取形态

| 路由/页面 | 当前首屏来源 | 问题 |
| --- | --- | --- |
| `wallet_home` | 部分 Room + 部分远端 | `wallet_overview_cache` 仅按 `userId` 缓存，非每钱包；顶价/资产在网络慢时仍会闪空 |
| `token_manager` | `wallet overview` 远端为主 + 本地 custom/visibility | 首屏常落远端，且上下文丢失时会空态 |
| `asset_detail` / `receive` / `send` | 远端为主，局部缓存 | 不能保证首屏本地快显 |
| `wallet_onboarding` | 远端 `me/subscription/lifecycle/listWallets` | 无独立本地态 |
| `vpn_home` | Room 节点 + Prefs 元数据 + 远端补全 | 订阅/VPN 状态不是 Room 主来源 |
| `plans` | Prefs JSON + 远端 | 不符合“本地数据库优先” |
| `order_checkout` | Prefs 缓存计划/资产目录 + Room 订单 + 远端 | 计划/资产目录未进 Room |
| `profile` | Room 用户 + 远端补充 | 统计项仍依赖远端 |
| `invite_center` / `invite_share` | 远端 | 无本地快显 |
| `commission_ledger` / `withdraw` | 远端 | 无本地快显 |

### 页面改造清单

#### A. 已有“缓存首屏”，但仍是本地 + 后端混读，必须统一成 Room-first

| 页面/路由 | 当前本地来源 | 当前远端来源 | 现状问题 | 目标改造 |
| --- | --- | --- | --- | --- |
| `wallet_home` | `wallet_overview_cache` + `users` + `orders` + `local_wallets` | `wallet/overview` + `wallets` + `wallet/lifecycle` | overview 仅按 `userId` 缓存；价格、上下文、钱包切换仍会闪空 | 改成 `walletId` 维度 Room-first，首屏只读 DB，异步刷新 overview/balances/lifecycle |
| `token_manager` | `local_custom_tokens` + `local_token_visibility_entries` + `local_token_icon_cache` | `wallet/overview` | 首屏仍依赖远端 overview；上下文丢失时会空态 | 先读 `wallet_overview_cache(walletId)` + 本地 custom/visibility，再后台刷新 |
| `vpn_home` | `vpn_node_cache` / `vpn_node_runtime` + Prefs 摘要 | `vpn/status` + `vpn/regions` + `subscription` + `wallet/overview` | 节点是 Room，订阅/VPN 状态不是；价格摘要会空白 | 订阅、VPN 状态、区域也进 Room，首页全部先读 DB |
| `plans` | Prefs JSON | `plans` | 不属于数据库优先，只是 Prefs 缓存 | 套餐列表改进 Room，ViewModel 先读 DB |
| `order_checkout` | Prefs 计划/资产目录 + Room 订单 | `plans` + `wallet asset catalog` + `wallet detail` | 本地数据来源碎片化，不是 Room-first | 计划/资产目录/钱包明细统一改 Room-first |
| `receive` | `wallet_receive_context_cache` + `wallet_public_address_cache` | `wallet/receive-context` | 已有缓存，但不是全量 Room-first，且 overview/balances 没联动 | 收款相关展示统一先读 DB，再异步刷新 |
| `wallet_manager` | `local_wallets` + `local_wallet_chain_accounts` | `wallets` + `wallet detail` | 钱包列表可本地起，但详情和统计仍靠远端刷新 | 钱包列表/链账户/默认钱包状态全部 Room-first |

#### B. 仍然是 remote-first，没有本地数据库快显，必须补缓存

| 页面/路由 | 当前远端来源 | 需要新增的本地缓存 |
| --- | --- | --- |
| `wallet_onboarding` | `me` + `subscription` + `wallet/lifecycle` + `wallets` | `wallet_lifecycle_cache`、`wallet list cache`、`subscription_cache` |
| `asset_detail` | `wallet overview` / `wallet balances` | `wallet_overview_cache(walletId)`、`wallet_balances_cache(walletId)` |
| `send` | `wallet receive/balance/fee` 组合态 | `wallet_balances_cache`、地址簿缓存、gas 缓存 |
| `order_list` | 订单远端/当前订单状态 | Room 订单表主读，远端异步同步 |
| `order_detail` | `getOrder(orderNo)` | Room 订单表主读 |
| `order_result` | `getOrder(orderId)` | Room 订单表主读 |
| `wallet_payment_confirm` | 订单 + 钱包支付上下文 | Room 订单 + wallet balances 缓存 |
| `profile` | `me` + `subscription` + `orders` 混合 | `subscription_cache`、`wallet_lifecycle_cache` |
| `invite_center` | `referral overview` | `referral_overview_cache` |
| `invite_share` | `referral share context` | `referral_share_context_cache` |
| `commission_ledger` | `commission summary` + `commission ledger` | `commission_summary_cache`、`commission_ledger_cache` |
| `withdraw` | `commission summary` + `withdrawals` + `referral overview` | `commission_summary_cache`、`withdrawal_cache`、`referral_overview_cache` |
| `subscription_detail` | `subscription` | `subscription_cache` |
| `region_selection` | `vpn regions` + `vpn status` | `vpn_regions_cache`、`vpn_status_cache` |

#### C. 本轮不纳入“本地业务缓存优先”改造

这些页面不是后端业务数据驱动，或主要是本地表单/静态内容，不列入本次 cache 改造主清单：

- `force_update`
- `optional_update`
- `email_login`
- `email_register`
- `reset_password`
- `create_wallet`
- `import_wallet_method`
- `import_mnemonic`
- `import_watch_wallet`
- `import_private_key`
- `backup_mnemonic`
- `confirm_mnemonic`
- `about_app`
- `legal_documents`
- `legal_document_detail`
- `session_evicted_dialog`

## 架构设计

### 缓存策略总则

- **唯一规则**：页面先读 Room，后端异步刷新，后端数据回写 Room 后再更新 UI。
- 认证令牌、设备 ID、当前订单 ID 等会话/控制字段继续放 `SharedPreferences/MMKV`，但**业务数据**统一以 Room 为主缓存。
- 价格相关页面的刷新分两层：
  - 后端价格缓存 TTL：`5 分钟`
  - APP 价格刷新节奏：`1 分钟`
- APP 冷启动或回到前台时：
  - 先用 Room 快照渲染
  - 然后立即触发一次异步刷新，不受 1 分钟节流限制
- 页面可见期间：
  - 钱包首页、代币管理页、资产详情页按 `1 分钟` 节奏异步刷新

### 后端改造

#### 1. 价格缓存

- `MarketConfig.getCacheTtlMs()` 默认值固定为 `300000` 毫秒。
- `MarketService` / `CoinGeckoMarketDataProvider` 继续作为后端价格缓存入口。
- 对自定义 token：
  - 先走 CoinGecko onchain quote
  - CoinGecko 返回空时，Sol token 走 DexScreener fallback
- `wallet/overview` / `wallet/balances` 不暴露第三方 provider 细节，只继续返回：
  - `unitPriceUsd`
  - `valueUsd`
  - `priceChangePct24h`
  - `priceStatus`
  - `priceUpdatedAt`

#### 2. walletId 作用域统一

- `wallet/overview` / `wallet/balances` / `receive-context` 传 `walletId` 时，顶层摘要与资产明细必须统一绑定该 wallet。
- 禁止出现：
  - 顶层 `walletId/walletName` 来自 lifecycle 默认钱包
  - `assetItems` 来自请求的 wallet
  这种混搭结果。

#### 3. 余额地址解析

- Sol/Tron 余额解析允许 `publicAddresses` 缺失时回退到 `wallet chain accounts`。
- 对 selected wallet：
  - 若链账户存在且可用，`balanceAddress` 应返回该地址
  - `availableBalanceStatus` 至少应为 `READY` 或 `UNAVAILABLE`，不能再误报 `NO_ADDRESS`

### Android 改造

#### 1. 新增/调整 Room 缓存实体

- `wallet_overview_cache`
  - 主键从仅 `userId` 扩成 `userId + walletId`
  - 保存：
    - 顶层 `walletId`
    - `walletName`
    - `receiveState`
    - `configuredAddressCount`
    - `defaultAddress`
    - `chainItemsJson`
    - `assetItemsJson`
    - `priceUpdatedAt`
    - `updatedAt`
- 新增 `wallet_balances_cache`
  - 主键：`userId + walletId`
  - 保存 `itemsJson + priceUpdatedAt + updatedAt`
- 新增 `wallet_lifecycle_cache`
  - 主键：`userId`
- 新增 `plan_cache`（替代 Prefs JSON）
- 新增 `wallet_asset_catalog_cache`（替代 Prefs JSON）
- 新增 `subscription_cache`
- 新增 `vpn_status_cache`
- 新增 `vpn_regions_cache`
- 新增 `referral_overview_cache`
- 新增 `referral_share_context_cache`
- 新增 `commission_summary_cache`
- 新增 `commission_ledger_cache`
- 新增 `withdrawal_cache`

#### 2. Repository 统一 local-first contract

- 每个业务接口都拆成两层：
  - `getCachedXFromDb()`
  - `syncXFromServer(force: Boolean)`
- `getX()` 统一规则：
  - 先返回 Room 快照
  - 若无快照，再同步后端
  - 返回快照后异步刷新
- 价格相关 `syncXFromServer()` 节流：
  - 默认 `1 分钟`
  - 冷启动/回前台可 `force = true`

#### 3. 路由级改造顺序

##### Phase A：价格与钱包核心路由

- `wallet_home`
- `token_manager`
- `asset_detail`
- `receive`
- `send`

要求：
- 首屏仅读 Room
- 页面启动后异步刷新
- 钱包首页顶部价格与代币单价统一来自 Room 的 overview/balances 快照

##### Phase B：钱包状态与支付流程路由

- `wallet_onboarding`
- `wallet_manager`
- `create_wallet`
- `import_mnemonic`
- `import_watch_wallet`
- `order_checkout`
- `plans`
- `region_selection`

要求：
- 计划、资产目录、wallet lifecycle、wallet list 都要先从 Room 读

##### Phase C：用户中心与推广/资金路由

- `profile`
- `invite_center`
- `invite_share`
- `commission_ledger`
- `withdraw`
- `security_center`

要求：
- 推荐数据、佣金、提现、推广上下文全部先走 Room

##### Phase D：VPN 辅助态

- `vpn_home`
- `vpn status`
- `vpn regions`

要求：
- 用 Room 替换当前 Prefs 元数据做主来源

## 实现任务

### Task 1: 固定价格缓存节奏
**依赖**: 无
**文件**:
- 修改: `code/backend/src/modules/market/market.config.ts`
- 修改: `code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/payment/data/repository/PaymentRepository.kt`

**步骤**:
1. 后端价格缓存 TTL 改成 5 分钟。
2. Android 价格类数据 sync throttle 改成 1 分钟。
3. 增加“冷启动/回前台强制刷新”入口，不受 1 分钟限制。

### Task 2: Room 缓存模型补齐
**依赖**: Task 1
**文件**:
- 修改: `code/Android/V2rayNG/app/src/main/java/com/v2ray/ang/payment/data/local/database/PaymentDatabase.kt`
- 创建/修改: `entity/dao` 若干

**步骤**:
1. 把 `wallet_overview_cache` 扩成 per-wallet。
2. 新增 `wallet_balances_cache`、`wallet_lifecycle_cache`。
3. 把 plans、asset catalog、subscription、vpn status、vpn regions、invite/commission/withdraw 都补到 Room。
4. 写 migration。

### Task 3: PaymentRepository 统一 local-first
**依赖**: Task 2
**文件**:
- 修改: `PaymentRepository.kt`
- 修改: `LocalPaymentRepository.kt`

**步骤**:
1. 每个 `getX()` 拆成 `getCachedXFromDb + syncXFromServer`。
2. 移除业务态对 Prefs JSON 的主依赖。
3. 所有 ViewModel 可见业务数据都保证有 DB 读取入口。

### Task 4: 路由逐步切换到 Room-first
**依赖**: Task 3
**文件**:
- 修改: `RealP0Repository.kt`
- 修改: `RealCryptoVpnRepository.kt`
- 修改: 各 `ViewModel`

**步骤**:
1. 先改钱包价格相关路由。
2. 再改支付/钱包创建相关路由。
3. 最后改用户中心和推广/佣金/提现路由。

### Task 5: 启动与前台刷新编排
**依赖**: Task 3
**文件**:
- 修改: `RealP0Repository.kt`
- 修改: App 前台/启动同步入口

**步骤**:
1. APP 打开时先读 Room。
2. 冷启动后立即异步刷新：
   - wallet overview
   - wallet balances
   - wallet lifecycle
   - wallet list
   - subscription
   - vpn status
3. 钱包页可见期间每 1 分钟刷新一次价格相关数据。

## 验证清单

- [ ] 后端价格缓存 TTL 为 5 分钟
- [ ] Android 价格同步节流为 1 分钟
- [ ] 冷启动时首屏只靠本地数据库也能完整渲染钱包首页
- [ ] 钱包首页顶部价格、代币列表、代币管理页都先显示本地数据，再异步更新
- [ ] `wallet_overview_cache` 已改为按 `walletId` 缓存
- [ ] plans / asset catalog / lifecycle / balances / referral / commission / withdrawals 已有 Room 实体
- [ ] 旧 Prefs JSON 缓存不再作为主来源
- [ ] 真机断网重开 APP 时，钱包首页、代币管理页、邀请中心、佣金页仍有本地快照可看
- [ ] 真机联网后，价格相关页面 1 分钟内自动更新

## 风险与回滚

**已知风险**
- Room migration 多，容易引发老用户升级失败
- `wallet_overview_cache` 主键变更会影响旧缓存读取
- 路由切换到 local-first 后，若异步刷新回写不完整，可能出现“老缓存长期停留”

**缓解措施**
- migration 必须有本地升级测试
- 每个新缓存表都提供 clear/rebuild 路径
- 对每条核心路由增加“缓存命中 + 后台刷新成功”日志

**回滚方案**
- 保留远端强刷兜底开关
- 若某类缓存迁移失败，可先在该路由回退到“读老缓存 + 异步远端”模式
