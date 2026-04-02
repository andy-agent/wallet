# 05_ia_and_page_spec

## 1. 信息架构总览
### 1.1 Android 页面树
```text
Android
├── 启动 / 版本门禁
│   ├── 启动页
│   └── 强更页
├── 认证
│   ├── 邮箱登录页
│   ├── 邮箱注册页
│   └── 重置密码页
├── VPN
│   ├── VPN 首页
│   ├── 套餐页
│   ├── 区域选择页
│   ├── 订单收银台
│   └── 订单结果页
├── 钱包
│   ├── 钱包引导页
│   ├── 钱包首页
│   ├── 资产详情页
│   ├── 收款页
│   └── 发送页
├── 增长
│   ├── 邀请中心页
│   ├── 佣金账本页
│   └── 提现申请页
└── 我的
    ├── 我的页 / 设置
    └── 法务文档页
```

### 1.2 Admin 页面树
```text
Admin
├── 后台登录页
├── 仪表盘
├── 用户列表 / 详情
├── 套餐管理
├── 区域管理
├── 节点管理
├── 订单中心
├── 佣金账本页
├── 提现审核页
├── 版本管理
├── 法务文档管理
├── 系统配置页
└── 审计日志页
```

## 2. 页面跳转主链路
### 2.1 Android 主链路
启动页  
→ （版本通过）登录页 / VPN 首页  
→ 套餐页  
→ 订单收银台  
→ 订单结果页  
→ VPN 首页  
→ 区域选择页  
→ 连接成功

### 2.2 钱包主链路
钱包引导页  
→ 钱包首页  
→ 资产详情  
→ 收款页 / 发送页  
→ （订单场景）返回订单收银台

### 2.3 邀请提现主链路
邀请中心页  
→ 佣金账本页  
→ 提现申请页  
→ 提现记录展示

### 2.4 Admin 主链路
后台登录页  
→ 仪表盘  
→ 各模块列表  
→ 详情  
→ 操作  
→ 审计日志回查

## 3. Android 页面规格
### 3.1 启动页 / 版本门禁
| 页面目标 | 入口 | 核心字段 | 核心动作 | 状态 | API | 数据模型 | 权限 | 跳转 | 异常 |
|---|---|---|---|---|---|---|---|---|---|
| 校验版本、会话、路由去向 | App 冷启动 | 当前版本号、更新策略、会话摘要、加载状态 | 检查版本；尝试 refresh；跳转登录/首页/强更页 | loading / force_update / optional_update / route_login / route_home / error | ['GET /api/public/v1/app/version/latest', 'POST /api/client/v1/auth/refresh'] | ['app_versions', 'client_sessions', 'system_configs'] | public_guest | 登录页 / VPN 首页 / 强更页 | 网络异常时可重试；强更时禁止进入其他页 |
### 3.2 邮箱登录页
| 页面目标 | 入口 | 核心字段 | 核心动作 | 状态 | API | 数据模型 | 权限 | 跳转 | 异常 |
|---|---|---|---|---|---|---|---|---|---|
| 完成邮箱+密码登录 | 启动页、登出后、会话失效后 | email、password、错误提示 | 登录、跳注册、跳重置密码 | idle / validating / submitting / success / auth_failed / account_frozen / network_error | ['POST /api/client/v1/auth/login/password'] | ['accounts', 'client_sessions', 'account_installations'] | public_guest | VPN 首页 / 注册页 / 重置密码页 | 新端登录成功会挤下旧 refresh session |
### 3.3 邮箱注册页
| 页面目标 | 入口 | 核心字段 | 核心动作 | 状态 | API | 数据模型 | 权限 | 跳转 | 异常 |
|---|---|---|---|---|---|---|---|---|---|
| 邮箱验证码注册 | 登录页 | email、验证码、password、协议勾选 | 发送验证码、注册 | idle / code_sending / code_sent / submitting / success / duplicate_email / code_invalid / network_error | ['POST /api/client/v1/auth/register/email/request-code', 'POST /api/client/v1/auth/register/email'] | ['accounts', 'verification_codes', 'client_sessions'] | public_guest | VPN 首页 / 登录页 | 验证码过期需重新发送 |
### 3.4 重置密码页
| 页面目标 | 入口 | 核心字段 | 核心动作 | 状态 | API | 数据模型 | 权限 | 跳转 | 异常 |
|---|---|---|---|---|---|---|---|---|---|
| 通过邮箱验证码重置密码 | 登录页 | email、验证码、新密码、确认密码 | 发送验证码、重置密码 | idle / code_sent / submitting / success / code_invalid / code_expired / network_error | ['POST /api/client/v1/auth/password/forgot/request-code', 'POST /api/client/v1/auth/password/reset'] | ['verification_codes', 'accounts', 'client_sessions'] | public_guest | 登录页 | 重置成功后原 refresh token 全部失效 |
### 3.5 VPN 首页
| 页面目标 | 入口 | 核心字段 | 核心动作 | 状态 | API | 数据模型 | 权限 | 跳转 | 异常 |
|---|---|---|---|---|---|---|---|---|---|
| 展示订阅、连接状态与主入口 | 底部 Tab | 订阅状态、套餐名、到期时间、当前区域、连接模式、连接状态 | 连接/断开、切换全局/规则模式、去套餐页、去区域选择 | no_subscription / active_ready / connecting / connected / suspended / expired / session_evicted / error | ['GET /api/client/v1/subscriptions/current', 'GET /api/client/v1/vpn/status'] | ['vpn_subscriptions', 'plans', 'vpn_access_identities'] | user / subscribed_user | 区域选择页 / 套餐页 / 我的页 | 无订阅时仅展示购买入口 |
### 3.6 套餐页
| 页面目标 | 入口 | 核心字段 | 核心动作 | 状态 | API | 数据模型 | 权限 | 跳转 | 异常 |
|---|---|---|---|---|---|---|---|---|---|
| 展示可售套餐并发起下单 | VPN 首页、订阅到期引导 | planCode、name、description、priceUsd、billingCycleMonths、includesAdvancedRegions | 查看套餐、创建订单 | loading / loaded / empty / network_error | ['GET /api/client/v1/plans', 'POST /api/client/v1/orders'] | ['plans', 'plan_region_permissions'] | user | 订单收银台 | 下架套餐不可创建订单 |
### 3.7 区域选择页
| 页面目标 | 入口 | 核心字段 | 核心动作 | 状态 | API | 数据模型 | 权限 | 跳转 | 异常 |
|---|---|---|---|---|---|---|---|---|---|
| 按套餐权限选择可用区域 | VPN 首页 | regionCode、displayName、tier、status、isAllowed | 选择区域 | loading / loaded / no_permission / maintenance / network_error | ['GET /api/client/v1/vpn/regions', 'POST /api/client/v1/vpn/config/issue'] | ['vpn_regions', 'plans', 'plan_region_permissions', 'vpn_subscriptions'] | subscribed_user | VPN 首页 | 高级区无权限置灰；维护中区域不可签发 |
### 3.8 订单收银台
| 页面目标 | 入口 | 核心字段 | 核心动作 | 状态 | API | 数据模型 | 权限 | 跳转 | 异常 |
|---|---|---|---|---|---|---|---|---|---|
| 展示支付目标并驱动外部/内置钱包支付 | 套餐页 | orderNo、planName、quoteAssetCode、quoteNetworkCode、payableAmount、expiresAt、collectionAddress、qrText、paymentStatus | 复制地址、切换支付方式、提交 client tx hash、刷新状态 | awaiting_payment / payment_detected / confirming / expired / underpaid_review / overpaid_review / failed / completed | ['POST /api/client/v1/orders', 'GET /api/client/v1/orders/{orderNo}', 'GET /api/client/v1/orders/{orderNo}/payment-target', 'POST /api/client/v1/orders/{orderNo}/submit-client-tx', 'POST /api/client/v1/orders/{orderNo}/refresh-status'] | ['orders', 'order_payment_targets', 'order_payment_events', 'payment_addresses'] | user | 订单结果页 / 钱包支付确认页 / VPN 首页 | 过期后必须重新下单 |
### 3.9 订单结果页
| 页面目标 | 入口 | 核心字段 | 核心动作 | 状态 | API | 数据模型 | 权限 | 跳转 | 异常 |
|---|---|---|---|---|---|---|---|---|---|
| 展示订单最终状态和后续动作 | 订单收银台 | orderNo、status、confirmedAt、completedAt、failureReason | 返回 VPN 首页、重新下单、联系客服/查看说明 | completed / failed / expired / review_pending / loading | ['GET /api/client/v1/orders/{orderNo}'] | ['orders', 'vpn_subscriptions'] | user | VPN 首页 / 套餐页 | review 状态时提示等待后台处理 |
### 3.10 钱包引导页
| 页面目标 | 入口 | 核心字段 | 核心动作 | 状态 | API | 数据模型 | 权限 | 跳转 | 异常 |
|---|---|---|---|---|---|---|---|---|---|
| 创建或导入本地钱包 | 钱包 Tab 首次进入、收银台内置钱包支付入口 | 钱包存在状态、风险提示 | 创建钱包、导入钱包 | empty / creating / importing / ready / error | ['GET /api/client/v1/wallet/chains', 'GET /api/client/v1/wallet/assets/catalog'] | ['Android 本地 wallet_profile / wallet_address / wallet_tx_cache'] | user | 钱包首页 | 本地校验失败时不调用后端 |
### 3.11 钱包首页
| 页面目标 | 入口 | 核心字段 | 核心动作 | 状态 | API | 数据模型 | 权限 | 跳转 | 异常 |
|---|---|---|---|---|---|---|---|---|---|
| 展示链与资产总览 | 底部 Tab | 链列表、资产余额、默认地址、最近交易 | 切链、进入资产详情、收款、发送、订单支付 | loading / loaded / empty_wallet / syncing / error | ['GET /api/client/v1/wallet/chains', 'GET /api/client/v1/wallet/assets/catalog', 'GET /api/client/v1/wallet/public-addresses'] | ['asset_catalog', 'account_wallet_public_addresses', 'Android 本地 wallet cache'] | user | 资产详情 / 收款 / 发送 | 链 RPC 不可用时允许本地缓存展示 |
### 3.12 资产详情页
| 页面目标 | 入口 | 核心字段 | 核心动作 | 状态 | API | 数据模型 | 权限 | 跳转 | 异常 |
|---|---|---|---|---|---|---|---|---|---|
| 展示单链单资产的余额与历史 | 钱包首页 | networkCode、assetCode、balance、recentTxs、address | 收款、发送、刷新 | loading / loaded / syncing / empty_history / error | ['GET /api/client/v1/wallet/assets/catalog'] | ['asset_catalog', 'Android 本地 wallet tx cache'] | user | 收款页 / 发送页 | 历史默认本地缓存 + 链上刷新 |
### 3.13 收款页
| 页面目标 | 入口 | 核心字段 | 核心动作 | 状态 | API | 数据模型 | 权限 | 跳转 | 异常 |
|---|---|---|---|---|---|---|---|---|---|
| 展示地址与二维码 | 钱包首页、资产详情 | networkCode、assetCode、address、qrText | 复制地址、分享二维码、同步公开地址到服务端（可选） | ready / sync_public_address / error | ['POST /api/client/v1/wallet/public-addresses'] | ['account_wallet_public_addresses', 'Android 本地 wallet address'] | user | 资产详情 / 提现申请页 | 服务端同步失败不影响本地收款能力 |
### 3.14 发送页
| 页面目标 | 入口 | 核心字段 | 核心动作 | 状态 | API | 数据模型 | 权限 | 跳转 | 异常 |
|---|---|---|---|---|---|---|---|---|---|
| 完成链上发送或订单支付 | 钱包首页、资产详情、订单收银台 | networkCode、assetCode、toAddress、amount、estimatedFee、availableBalance、orderNo(optional) | 预检查、签名、直接广播、服务端代理广播兜底、回填 client tx hash | editing / precheck_failed / ready_to_sign / broadcasting_direct / broadcasting_proxy / pending / success / insufficient_balance / invalid_address / error | ['POST /api/client/v1/wallet/transfer/precheck', 'POST /api/client/v1/wallet/transfer/proxy-broadcast', 'POST /api/client/v1/orders/{orderNo}/submit-client-tx'] | ['chain_configs', 'asset_catalog', 'orders', 'order_payment_events'] | user | 交易结果 / 订单收银台 | 默认直连广播；仅在 direct broadcast 失败且用户同意时走 proxy fallback |
### 3.15 邀请中心页
| 页面目标 | 入口 | 核心字段 | 核心动作 | 状态 | API | 数据模型 | 权限 | 跳转 | 异常 |
|---|---|---|---|---|---|---|---|---|---|
| 展示邀请码、邀请概览、佣金总览 | 底部 Tab | referralCode、inviteCounts、level1Income、level2Income、availableAmount、frozenAmount、minWithdrawAmount | 复制邀请码、绑定邀请码（首单前）、查看账本、申请提现 | loading / loaded / withdraw_disabled / binding_locked / error | ['GET /api/client/v1/referral/overview', 'POST /api/client/v1/referral/bind', 'GET /api/client/v1/commissions/summary'] | ['accounts', 'referral_bindings', 'commission_balances', 'commission_rules'] | user | 佣金账本页 / 提现申请页 | 已首单或已有绑定关系时不可再绑定 |
### 3.16 佣金账本页
| 页面目标 | 入口 | 核心字段 | 核心动作 | 状态 | API | 数据模型 | 权限 | 跳转 | 异常 |
|---|---|---|---|---|---|---|---|---|---|
| 查看一级/二级佣金明细 | 邀请中心页 | entryNo、level、sourceOrderNo、sourceAccountMasked、settlementAmountUsdt、status、availableAt | 分页查询、筛选状态 | loading / loaded / empty / error | ['GET /api/client/v1/commissions/ledger'] | ['commission_ledger'] | user | 邀请中心页 | 仅显示当前账号账本 |
### 3.17 提现申请页
| 页面目标 | 入口 | 核心字段 | 核心动作 | 状态 | API | 数据模型 | 权限 | 跳转 | 异常 |
|---|---|---|---|---|---|---|---|---|---|
| 提交佣金提现申请并查看记录 | 邀请中心页 | availableAmount、amount、payoutAddress、assetCode、networkCode、requestStatus | 提交申请、查看详情 | idle / validating / submitting / submitted / under_review / completed / failed / insufficient_balance / invalid_address / error | ['POST /api/client/v1/withdrawals', 'GET /api/client/v1/withdrawals', 'GET /api/client/v1/withdrawals/{requestNo}'] | ['commission_balances', 'commission_withdraw_requests', 'account_wallet_public_addresses'] | user | 邀请中心页 | 默认 USDT on Solana；不足 10 USDT 禁止提交 |
### 3.18 我的页 / 设置
| 页面目标 | 入口 | 核心字段 | 核心动作 | 状态 | API | 数据模型 | 权限 | 跳转 | 异常 |
|---|---|---|---|---|---|---|---|---|---|
| 查看账号、订单、版本、法务、安全设置 | 底部 Tab | email、subscriptionSummary、appVersion、legalEntries、sessionStatus | 查看订单、查看法务、退出登录 | loading / loaded / session_evicted / error | ['GET /api/client/v1/me', 'GET /api/public/v1/app/version/latest', 'GET /api/public/v1/legal/{docType}', 'POST /api/client/v1/auth/logout'] | ['accounts', 'vpn_subscriptions', 'app_versions', 'legal_documents'] | user | 订单收银台 / 法务文档 / 登录页 | 会话失效时强制回登录 |
### 3.19 强更页
| 页面目标 | 入口 | 核心字段 | 核心动作 | 状态 | API | 数据模型 | 权限 | 跳转 | 异常 |
|---|---|---|---|---|---|---|---|---|---|
| 阻断低版本继续使用 | 启动页命中 force update | latestVersionName、releaseNotes、downloadUrl、sha256 | 前往官网下载安装 | force_update_only | ['GET /api/public/v1/app/version/latest'] | ['app_versions'] | public_guest | 外部浏览器/下载页 | 无可用下载地址时显示客服/官网主域名提示 |

## 4. 后台页面规格
### 4.1 后台登录页
| 页面用途 | 查询条件 | 列表字段 | 详情字段 | 操作按钮 | 权限 | API | 异常 | 验收点 |
|---|---|---|---|---|---|---|---|---|
| 管理员登录 | - | - | username、password | 登录 | public_guest | ['POST /api/admin/v1/auth/login'] | 账号禁用/密码错误 | 登录成功返回 admin token |
### 4.2 仪表盘
| 页面用途 | 查询条件 | 列表字段 | 详情字段 | 操作按钮 | 权限 | API | 异常 | 验收点 |
|---|---|---|---|---|---|---|---|---|
| 查看核心运营指标 | 日期 | activeUsers、activeSubscriptions、pendingOrders、pendingWithdrawals | 指标明细 | 跳转各模块 | ops_admin/finance_admin/super_admin | ['GET /api/admin/v1/dashboard/summary'] | 无权限/接口失败 | 指标与数据源一致 |
### 4.3 用户列表 / 详情
| 页面用途 | 查询条件 | 列表字段 | 详情字段 | 操作按钮 | 权限 | API | 异常 | 验收点 |
|---|---|---|---|---|---|---|---|---|
| 查用户、冻结、解冻、驱逐会话 | email、status、planCode、dateRange | accountNo、email、status、planCode、expireAt、lastLoginAt | 账号、会话、订阅、邀请、佣金摘要 | 冻结、解冻、驱逐会话 | support_admin(read)/ops_admin(write)/super_admin | ['GET /api/admin/v1/accounts', 'GET /api/admin/v1/accounts/{accountId}', 'POST /api/admin/v1/accounts/{accountId}/freeze', 'POST /api/admin/v1/accounts/{accountId}/unfreeze', 'POST /api/admin/v1/accounts/{accountId}/evict-sessions'] | 状态冲突 | 冻结后客户端 refresh 失效 |
### 4.4 套餐管理
| 页面用途 | 查询条件 | 列表字段 | 详情字段 | 操作按钮 | 权限 | API | 异常 | 验收点 |
|---|---|---|---|---|---|---|---|---|
| CRUD 套餐与发布 | status | planCode、name、priceUsd、billingCycleMonths、status、displayOrder | 全部套餐配置、区域策略 | 创建、编辑、发布、禁用 | ops_admin/super_admin | ['GET /api/admin/v1/plans', 'POST /api/admin/v1/plans', 'PUT /api/admin/v1/plans/{planId}', 'POST /api/admin/v1/plans/{planId}/publish', 'POST /api/admin/v1/plans/{planId}/disable'] | planCode 重复 | 客户端 plans 接口同步生效 |
### 4.5 区域管理
| 页面用途 | 查询条件 | 列表字段 | 详情字段 | 操作按钮 | 权限 | API | 异常 | 验收点 |
|---|---|---|---|---|---|---|---|---|
| 维护普通区/高级区 | tier、status | regionCode、displayName、tier、status、sortOrder | 区域定义与备注 | 创建、编辑、启停 | ops_admin/super_admin | ['GET /api/admin/v1/vpn/regions', 'POST /api/admin/v1/vpn/regions', 'PUT /api/admin/v1/vpn/regions/{regionId}'] | regionCode 重复 | 区域列表与客户端一致 |
### 4.6 节点管理
| 页面用途 | 查询条件 | 列表字段 | 详情字段 | 操作按钮 | 权限 | API | 异常 | 验收点 |
|---|---|---|---|---|---|---|---|---|
| 维护节点与健康状态 | regionId、status、healthStatus | nodeCode、regionCode、host、port、status、healthStatus、weight | Reality 参数、heartbeat、flow | 创建、编辑、禁用 | ops_admin/super_admin | ['GET /api/admin/v1/vpn/nodes', 'POST /api/admin/v1/vpn/nodes', 'PUT /api/admin/v1/vpn/nodes/{nodeId}', 'POST /api/admin/v1/vpn/nodes/{nodeId}/disable'] | 禁用后仍被签发 | 禁用节点不再出现在配置签发结果 |
### 4.7 订单中心
| 页面用途 | 查询条件 | 列表字段 | 详情字段 | 操作按钮 | 权限 | API | 异常 | 验收点 |
|---|---|---|---|---|---|---|---|---|
| 查看订单、支付事件、异常处理 | orderNo、accountEmail、status、asset、network、dateRange | orderNo、accountEmail、planCode、asset/network、payableAmount、status、expiresAt | paymentTarget、paymentEvents、txHash、reviewNotes | 标记异常、重试开通 | support_admin(read)/ops_admin(write)/finance_admin(read)/super_admin | ['GET /api/admin/v1/orders', 'GET /api/admin/v1/orders/{orderNo}', 'POST /api/admin/v1/orders/{orderNo}/mark-exception', 'POST /api/admin/v1/orders/{orderNo}/retry-provision'] | 重复重试 | 异常单可见且有审计 |
### 4.8 佣金账本页
| 页面用途 | 查询条件 | 列表字段 | 详情字段 | 操作按钮 | 权限 | API | 异常 | 验收点 |
|---|---|---|---|---|---|---|---|---|
| 查看一级/二级佣金 | beneficiaryEmail、level、status、dateRange | entryNo、beneficiary、sourceOrderNo、level、amountUsdt、status、availableAt | 汇率快照、冲销原因 | 查看详情 | finance_admin/super_admin | ['GET /api/admin/v1/commissions/ledger'] | 账本与余额不一致 | 与 commission_balances 可核对 |
### 4.9 提现审核页
| 页面用途 | 查询条件 | 列表字段 | 详情字段 | 操作按钮 | 权限 | API | 异常 | 验收点 |
|---|---|---|---|---|---|---|---|---|
| 审核提现与录入打款哈希 | status、accountEmail、dateRange | requestNo、accountEmail、amountUsdt、network、status、createdAt | 地址、审核记录、txHash、failReason | 通过、拒绝、录入打款哈希、重试广播 | finance_admin/super_admin | ['GET /api/admin/v1/withdrawals', 'POST /api/admin/v1/withdrawals/{requestNo}/approve', 'POST /api/admin/v1/withdrawals/{requestNo}/reject', 'POST /api/admin/v1/withdrawals/{requestNo}/record-payout', 'POST /api/admin/v1/withdrawals/{requestNo}/retry-broadcast'] | 拒绝原因必填 | 状态流转完整 |
### 4.10 版本管理
| 页面用途 | 查询条件 | 列表字段 | 详情字段 | 操作按钮 | 权限 | API | 异常 | 验收点 |
|---|---|---|---|---|---|---|---|---|
| 发布 APK 版本与强更策略 | status、channel | versionName、versionCode、minSupportedCode、forceUpdate、status | downloadUrl、sha256、releaseNotes | 创建、发布、弃用 | ops_admin/super_admin | ['GET /api/admin/v1/app-versions', 'POST /api/admin/v1/app-versions', 'POST /api/admin/v1/app-versions/{versionId}/publish'] | 版本号回退 | public version 接口与后台配置一致 |
### 4.11 法务文档管理
| 页面用途 | 查询条件 | 列表字段 | 详情字段 | 操作按钮 | 权限 | API | 异常 | 验收点 |
|---|---|---|---|---|---|---|---|---|
| 维护官网/App 法务文档 | docType、status | docType、versionNo、status、publishedAt | title、markdownContent、versionNo | 保存、发布 | ops_admin/super_admin | ['GET /api/admin/v1/legal-documents', 'PUT /api/admin/v1/legal-documents/{docType}', 'POST /api/admin/v1/legal-documents/{docType}/publish'] | 空内容不可发布 | public legal 接口返回最新 published 版本 |
### 4.12 系统配置页
| 页面用途 | 查询条件 | 列表字段 | 详情字段 | 操作按钮 | 权限 | API | 异常 | 验收点 |
|---|---|---|---|---|---|---|---|---|
| 维护确认数、过期时长、分佣冷静期等 | scope | scope、configKey、configValue、updatedAt | description、mutableInProd | 更新配置 | ops_admin/super_admin | ['GET /api/admin/v1/system-configs', 'PUT /api/admin/v1/system-configs/{configKey}'] | 非法值 | 配置写入后新请求读取新值 |
### 4.13 审计日志页
| 页面用途 | 查询条件 | 列表字段 | 详情字段 | 操作按钮 | 权限 | API | 异常 | 验收点 |
|---|---|---|---|---|---|---|---|---|
| 追踪后台敏感操作 | module、actor、dateRange | createdAt、actor、module、action、targetType、targetId | beforeJson、afterJson、metadataJson | 仅查看 | super_admin/ops_admin(finance 只读部分) | ['GET /api/admin/v1/audit-logs'] | 无权限 | 关键后台动作均可查询 |

## 5. 页面与 API / 数据模型映射规则
1. 每个页面至少映射一个主 API 和一个主数据模型。
2. 页面展示字段必须能在对应接口响应中直接取得，或明确标注为本地派生字段。
3. 页面状态必须能映射到后端状态或客户端 UIState，不允许“前端有状态、后端无依据”。
4. Android 钱包页中的交易历史优先从本地缓存 + 链查询聚合，不以服务端持久化普通钱包交易为前置条件。
5. Admin 页面的操作按钮必须有对应 RBAC 角色与审计日志。

## 6. 页面状态总则
- `loading`：首次请求或强制刷新
- `empty`：接口成功但无数据
- `error`：接口失败或依赖不可用
- `forbidden`：无权限
- `maintenance`：区域/节点/版本等暂不可用
- `session_evicted`：旧会话被新登录替换
- `force_update`：版本门禁阻断

## 7. 页面验收总则
- 展示字段与 OpenAPI 响应字段一一对应
- 页面跳转不跨越权限边界
- 关键失败态有明确按钮与文案
- 所有资金/状态变化页面都可通过 requestId 与后台审计回查
  说明：若该动作产生审计记录，则 requestId 必须写入 `audit_logs.request_id`；否则至少要能在结构化日志系统中检索
