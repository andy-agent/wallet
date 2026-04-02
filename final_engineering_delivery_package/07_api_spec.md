# 07_api_spec

## 1. 总则
### 1.1 路径规范
- 公共接口：`/api/public/v1/*`
- 客户端接口：`/api/client/v1/*`
- 后台接口：`/api/admin/v1/*`

### 1.2 鉴权规范
- Client：`Authorization: Bearer <access_token>`；refresh 接口使用 body 中的 `refreshToken`
- Admin：`Authorization: Bearer <admin_access_token>`；不提供 refresh token，过期后重新登录
- Public：无需鉴权

### 1.3 幂等规范
- **Header 统一机制**：`X-Idempotency-Key`
  - 邮箱注册：必须带 `X-Idempotency-Key`，按 `email + idempotencyKey` 防重
  - 重置密码：必须带 `X-Idempotency-Key`，按 `email + idempotencyKey` 防重
  - 创建订单：必须带 `X-Idempotency-Key`
  - 创建提现：必须带 `X-Idempotency-Key`
- 客户端提交 txHash：按 `orderNo + txHash` 幂等
- 后台通过/拒绝/重试等状态操作：服务端按状态机幂等处理

### 1.4 分页与排序
- 通用分页参数：`page`、`pageSize`
- 通用排序参数：`sortBy`、`sortOrder`
- 后台列表接口默认支持条件筛选，若未指定排序，则按 `createdAt desc`

### 1.5 统一响应格式
```json
{
  "requestId": "uuid",
  "code": "OK",
  "message": "ok",
  "data": {}
}
```

- `requestId` 是本次请求的统一追踪标识。
- 所有服务都必须把 `requestId` 写入结构化日志。
- 所有会改变资金、状态、权限或配置的后台/系统动作，若产生审计记录，必须把 `requestId` 写入 `audit_logs.request_id`，用于后台检索回查。

### 1.6 错误码规范
| 分层 | 错误码 | 含义 | HTTP |
|---|---|---|---|
| 通用 | VALIDATION_ERROR | 请求参数校验失败 | 400 |
| 通用 | UNAUTHORIZED | 缺失或无效访问凭证 | 401 |
| 通用 | FORBIDDEN_RESOURCE | 资源不属于当前用户或当前角色 | 403 |
| 通用 | NOT_FOUND | 资源不存在 | 404 |
| 通用 | CONFLICT | 状态冲突或唯一约束冲突 | 409 |
| 通用 | RATE_LIMITED | 频率受限 | 429 |
| 通用 | INTERNAL_ERROR | 内部错误 | 500 |
| 鉴权 | AUTH_INVALID_CREDENTIALS | 邮箱或密码错误 | 401 |
| 鉴权 | AUTH_REFRESH_INVALID | refresh token 无效 | 401 |
| 鉴权 | AUTH_SESSION_EVICTED | 会话已被新登录替换 | 401 |
| 鉴权 | AUTH_ACCOUNT_FROZEN | 账号冻结 | 403 |
| 鉴权 | EMAIL_ALREADY_EXISTS | 邮箱已存在 | 409 |
| 鉴权 | CODE_INVALID | 验证码错误 | 400 |
| 鉴权 | CODE_EXPIRED | 验证码已过期 | 400 |
| 鉴权 | ACCOUNT_NOT_FOUND | 账号不存在 | 404 |
| 业务 | PLAN_NOT_AVAILABLE | 套餐不可售 | 409 |
| 业务 | ORDER_CREATE_CONFLICT | 幂等键冲突或存在不可复用订单 | 409 |
| 业务 | ORDER_NOT_FOUND | 订单不存在 | 404 |
| 业务 | ORDER_STATUS_INVALID | 订单当前状态不允许该操作 | 409 |
| 业务 | VPN_REGION_FORBIDDEN | 当前套餐无该区域权限 | 403 |
| 业务 | VPN_REGION_UNAVAILABLE | 区域维护或节点不可用 | 409 |
| 业务 | SUBSCRIPTION_REQUIRED | 无有效订阅 | 403 |
| 业务 | REFERRAL_BINDING_LOCKED | 邀请码已锁定，不可再次绑定 | 409 |
| 业务 | REFERRAL_CODE_INVALID | 邀请码不存在或不可用 | 400 |
| 业务 | REFERRAL_SELF_BIND_FORBIDDEN | 不允许给自己绑定邀请码 | 400 |
| 业务 | WITHDRAW_MIN_AMOUNT_NOT_MET | 未达到最小提现门槛 | 400 |
| 业务 | WITHDRAW_INSUFFICIENT_AVAILABLE_BALANCE | 可提余额不足 | 409 |
| 业务 | WITHDRAW_ADDRESS_INVALID | 提现地址非法 | 400 |
| 业务 | WITHDRAW_NOT_FOUND | 提现申请不存在 | 404 |
| 业务 | WITHDRAW_STATUS_INVALID | 提现状态不允许该操作 | 409 |
| 业务 | WALLET_INVALID_ADDRESS | 钱包目标地址非法 | 400 |
| 业务 | WALLET_UNSUPPORTED_ASSET | 当前资产或网络不受支持 | 400 |
| 业务 | WALLET_PROXY_BROADCAST_DISABLED | 当前链不允许代理广播 | 409 |
| 业务 | WALLET_BROADCAST_FAILED | 广播失败 | 409 |
| 后台 | ADMIN_INVALID_CREDENTIALS | 管理员账号密码错误 | 401 |
| 后台 | ADMIN_DISABLED | 管理员账号已禁用 | 403 |
| 后台 | ADMIN_AUTH_INVALID | 管理员访问凭证无效 | 401 |
| 后台 | ADMIN_PERMISSION_DENIED | 管理员权限不足 | 403 |
| 后台 | APP_VERSION_NOT_FOUND | 版本记录不存在 | 404 |
| 后台 | LEGAL_DOC_NOT_FOUND | 法务文档不存在 | 404 |
| 后台 | PLAN_CODE_DUPLICATE | 套餐编码重复 | 409 |
| 后台 | REGION_CODE_DUPLICATE | 区域编码重复 | 409 |
| 后台 | NODE_CODE_DUPLICATE | 节点编码重复 | 409 |
| 后台 | APP_VERSION_DUPLICATE | 同渠道版本号重复 | 409 |
| 后台 | CONFIG_NOT_FOUND | 配置项不存在 | 404 |
| 后台 | CONFIG_VALUE_INVALID | 配置值非法 | 400 |

## 2. 接口清单

### 2.1 Public
| name | method | path | purpose | roles | auth | idempotency | request | response | errors | pages | tables |
|---|---|---|---|---|---|---|---|---|---|---|---|
| 获取最新版本 | GET | /api/public/v1/app/version/latest | 启动页版本检查与强更 | public_guest | None | N/A | Query: platform, channel, currentVersionCode | VersionCheckResponse | VALIDATION_ERROR, APP_VERSION_NOT_FOUND | 启动页/强更页 | app_versions |
| 获取法务文档 | GET | /api/public/v1/legal/{docType} | 展示官网/App 法务文档 | public_guest | None | N/A | Path: docType | LegalDocumentResponse | LEGAL_DOC_NOT_FOUND | 法务文档页 | legal_documents |

### 2.2 ClientAuth
| name | method | path | purpose | roles | auth | idempotency | request | response | errors | pages | tables |
|---|---|---|---|---|---|---|---|---|---|---|---|
| 发送注册验证码 | POST | /api/client/v1/auth/register/email/request-code | 邮箱注册验证码 | public_guest | None | 按邮箱+purpose 限流 | RegisterEmailCodeRequest | OperationResponse | RATE_LIMITED, EMAIL_ALREADY_EXISTS | 注册页 | verification_codes,accounts |
| 邮箱注册 | POST | /api/client/v1/auth/register/email | 创建账号并签发会话 | public_guest | None | X-Idempotency-Key required，按 email + idempotencyKey 防重 | RegisterEmailRequest + Header | TokenPairResponse | CODE_INVALID, CODE_EXPIRED, EMAIL_ALREADY_EXISTS | 注册页 | accounts,verification_codes,client_sessions,account_installations |
| 邮箱密码登录 | POST | /api/client/v1/auth/login/password | 登录并挤掉旧 session | public_guest | None | N/A | LoginPasswordRequest | TokenPairResponse | AUTH_INVALID_CREDENTIALS, AUTH_ACCOUNT_FROZEN | 登录页 | accounts,client_sessions,account_installations |
| 刷新会话 | POST | /api/client/v1/auth/refresh | 用 refresh token 换新 token pair | user | Refresh token in body | 旧 refresh 单次有效 | RefreshTokenRequest | TokenPairResponse | AUTH_REFRESH_INVALID, AUTH_SESSION_EVICTED, AUTH_ACCOUNT_FROZEN | 启动页/全局拦截 | client_sessions,accounts |
| 退出登录 | POST | /api/client/v1/auth/logout | 撤销当前 session | user | Client Bearer | 幂等 | LogoutRequest | OperationResponse | AUTH_INVALID_TOKEN | 我的页 | client_sessions |
| 发送重置密码验证码 | POST | /api/client/v1/auth/password/forgot/request-code | 重置密码前发送验证码 | public_guest | None | 按邮箱+purpose 限流 | PasswordResetCodeRequest | OperationResponse | RATE_LIMITED, ACCOUNT_NOT_FOUND | 重置密码页 | verification_codes,accounts |
| 重置密码 | POST | /api/client/v1/auth/password/reset | 重置密码并使旧 session 失效 | public_guest | None | X-Idempotency-Key required，按 email + idempotencyKey 防重 | PasswordResetRequest + Header | OperationResponse | CODE_INVALID, CODE_EXPIRED, ACCOUNT_NOT_FOUND | 重置密码页 | accounts,verification_codes,client_sessions |

### 2.3 ClientAccount
| name | method | path | purpose | roles | auth | idempotency | request | response | errors | pages | tables |
|---|---|---|---|---|---|---|---|---|---|---|---|
| 获取当前用户概要 | GET | /api/client/v1/me | 我的页基础信息 | user | Client Bearer | N/A | - | MeResponse | AUTH_INVALID_TOKEN | 我的页 | accounts,vpn_subscriptions,plans |
| 获取当前 session 摘要 | GET | /api/client/v1/me/session | 启动页与全局拦截读取 session 状态 | user | Client Bearer | N/A | - | SessionSummaryResponse | AUTH_INVALID_TOKEN | 启动页 | client_sessions |

### 2.4 Plans
| name | method | path | purpose | roles | auth | idempotency | request | response | errors | pages | tables |
|---|---|---|---|---|---|---|---|---|---|---|---|
| 获取套餐列表 | GET | /api/client/v1/plans | 套餐页展示可售套餐 | user | Client Bearer | N/A | Query: channel(optional) | PlanListResponse | AUTH_INVALID_TOKEN | 套餐页 | plans,plan_region_permissions |

### 2.5 Subscription
| name | method | path | purpose | roles | auth | idempotency | request | response | errors | pages | tables |
|---|---|---|---|---|---|---|---|---|---|---|---|
| 获取当前订阅 | GET | /api/client/v1/subscriptions/current | VPN 首页展示订阅 | user | Client Bearer | N/A | - | SubscriptionResponse | AUTH_INVALID_TOKEN | VPN 首页 | vpn_subscriptions,plans |

### 2.6 VPN
| name | method | path | purpose | roles | auth | idempotency | request | response | errors | pages | tables |
|---|---|---|---|---|---|---|---|---|---|---|---|
| 获取区域列表 | GET | /api/client/v1/vpn/regions | 区域选择页展示可用区域 | subscribed_user | Client Bearer | N/A | - | RegionListResponse | SUBSCRIPTION_REQUIRED, AUTH_INVALID_TOKEN | 区域选择页 | vpn_regions,plans,vpn_subscriptions |
| 签发 VPN 配置 | POST | /api/client/v1/vpn/config/issue | 为当前账号+区域生成短时效配置 | subscribed_user | Client Bearer | 按 account+region+mode 短时幂等 | IssueVpnConfigRequest | IssueVpnConfigResponse | VPN_REGION_FORBIDDEN, VPN_REGION_UNAVAILABLE, SUBSCRIPTION_REQUIRED | 区域选择页/VPN 首页 | vpn_access_identities,vpn_nodes,vpn_regions,vpn_subscriptions |
| 获取 VPN 连接状态摘要 | GET | /api/client/v1/vpn/status | 客户端显示连接授权状态 | user | Client Bearer | N/A | - | VpnStatusResponse | AUTH_INVALID_TOKEN | VPN 首页 | vpn_subscriptions,vpn_access_identities |

### 2.7 Orders
| name | method | path | purpose | roles | auth | idempotency | request | response | errors | pages | tables |
|---|---|---|---|---|---|---|---|---|---|---|---|
| 创建订单 | POST | /api/client/v1/orders | 新购/续费下单 | user | Client Bearer | X-Idempotency-Key required | CreateOrderRequest + Header | OrderResponse | PLAN_NOT_AVAILABLE, ORDER_CREATE_CONFLICT, AUTH_INVALID_TOKEN | 套餐页 | orders,order_payment_targets,payment_addresses,plans |
| 获取订单详情 | GET | /api/client/v1/orders/{orderNo} | 订单详情与支付状态查询 | user | Client Bearer | N/A | Path: orderNo | OrderResponse | ORDER_NOT_FOUND, FORBIDDEN_RESOURCE | 订单收银台/订单结果页 | orders,order_payment_targets |
| 获取订单支付目标 | GET | /api/client/v1/orders/{orderNo}/payment-target | 收银台展示支付地址、金额、二维码 | user | Client Bearer | N/A | Path: orderNo | PaymentTargetResponse | ORDER_NOT_FOUND, ORDER_STATUS_INVALID | 订单收银台 | order_payment_targets,payment_addresses |
| 提交客户端广播 txHash | POST | /api/client/v1/orders/{orderNo}/submit-client-tx | 辅助支付扫描定位 tx | user | Client Bearer | orderNo + txHash | SubmitClientTxRequest | OperationResponse | ORDER_NOT_FOUND, ORDER_STATUS_INVALID | 发送页/订单收银台 | orders,order_payment_events |
| 刷新订单状态 | POST | /api/client/v1/orders/{orderNo}/refresh-status | 主动触发最新状态读取 | user | Client Bearer | 幂等 | RefreshOrderStatusRequest | OrderResponse | ORDER_NOT_FOUND | 订单收银台 | orders,order_payment_events |

### 2.8 Wallet
| name | method | path | purpose | roles | auth | idempotency | request | response | errors | pages | tables |
|---|---|---|---|---|---|---|---|---|---|---|---|
| 获取链配置目录 | GET | /api/client/v1/wallet/chains | 客户端展示支持链及直连/代理广播策略 | user | Client Bearer | N/A | - | ChainListResponse | AUTH_INVALID_TOKEN | 钱包首页/发送页 | chain_configs |
| 获取资产目录 | GET | /api/client/v1/wallet/assets/catalog | 展示可见资产与精度 | user | Client Bearer | N/A | Query: networkCode(optional) | AssetCatalogResponse | AUTH_INVALID_TOKEN | 钱包首页/发送页/收银台 | asset_catalog |
| 同步公开地址 | POST | /api/client/v1/wallet/public-addresses | 将本地公开地址同步到服务端作为默认收款/提现候选 | user | Client Bearer | account+network+asset+address | UpsertWalletPublicAddressRequest | WalletPublicAddressResponse | AUTH_INVALID_TOKEN, VALIDATION_ERROR | 收款页 | account_wallet_public_addresses |
| 获取已同步公开地址 | GET | /api/client/v1/wallet/public-addresses | 提现页预填与钱包首页展示 | user | Client Bearer | N/A | Query: networkCode(optional), assetCode(optional) | WalletPublicAddressListResponse | AUTH_INVALID_TOKEN | 钱包首页/提现页 | account_wallet_public_addresses |
| 转账预检查 | POST | /api/client/v1/wallet/transfer/precheck | 校验地址、资产、网络、手续费与可广播能力 | user | Client Bearer | 客户端本地幂等 | TransferPrecheckRequest | TransferPrecheckResponse | WALLET_INVALID_ADDRESS, WALLET_UNSUPPORTED_ASSET | 发送页 | chain_configs,asset_catalog |
| 代理广播（兜底） | POST | /api/client/v1/wallet/transfer/proxy-broadcast | 仅在 direct broadcast 失败且链配置允许时兜底广播 | user | Client Bearer | network+signedTxHash | ProxyBroadcastRequest | ProxyBroadcastResponse | WALLET_PROXY_BROADCAST_DISABLED, WALLET_BROADCAST_FAILED | 发送页 | chain_configs |

### 2.9 Referral
| name | method | path | purpose | roles | auth | idempotency | request | response | errors | pages | tables |
|---|---|---|---|---|---|---|---|---|---|---|---|
| 获取邀请概览 | GET | /api/client/v1/referral/overview | 邀请中心页显示邀请码和邀请概况 | user | Client Bearer | N/A | - | ReferralOverviewResponse | AUTH_INVALID_TOKEN | 邀请中心页 | accounts,referral_bindings,commission_balances |
| 绑定邀请码 | POST | /api/client/v1/referral/bind | 首单前绑定邀请关系 | user | Client Bearer | accountId once | ReferralBindRequest | OperationResponse | REFERRAL_BINDING_LOCKED, REFERRAL_CODE_INVALID, REFERRAL_SELF_BIND_FORBIDDEN | 邀请中心页 | referral_bindings,accounts,orders |

### 2.10 Commissions
| name | method | path | purpose | roles | auth | idempotency | request | response | errors | pages | tables |
|---|---|---|---|---|---|---|---|---|---|---|---|
| 获取佣金汇总 | GET | /api/client/v1/commissions/summary | 邀请页余额概览 | user | Client Bearer | N/A | - | CommissionSummaryResponse | AUTH_INVALID_TOKEN | 邀请中心页 | commission_balances,commission_rules |
| 获取佣金账本 | GET | /api/client/v1/commissions/ledger | 查看账本明细 | user | Client Bearer | N/A | Query: page,pageSize,status | CommissionLedgerPageResponse | AUTH_INVALID_TOKEN | 佣金账本页 | commission_ledger |

### 2.11 Withdrawals
| name | method | path | purpose | roles | auth | idempotency | request | response | errors | pages | tables |
|---|---|---|---|---|---|---|---|---|---|---|---|
| 创建提现申请 | POST | /api/client/v1/withdrawals | 提交佣金提现申请 | user | Client Bearer | X-Idempotency-Key required | CreateWithdrawalRequest + Header | WithdrawalResponse | WITHDRAW_MIN_AMOUNT_NOT_MET, WITHDRAW_INSUFFICIENT_AVAILABLE_BALANCE, WITHDRAW_ADDRESS_INVALID | 提现申请页 | commission_balances,commission_withdraw_requests,commission_ledger |
| 获取提现列表 | GET | /api/client/v1/withdrawals | 提现历史分页 | user | Client Bearer | N/A | Query: page,pageSize,status | WithdrawalPageResponse | AUTH_INVALID_TOKEN | 提现申请页 | commission_withdraw_requests |
| 获取提现详情 | GET | /api/client/v1/withdrawals/{requestNo} | 查看某笔提现详情 | user | Client Bearer | N/A | Path: requestNo | WithdrawalResponse | WITHDRAW_NOT_FOUND | 提现申请页 | commission_withdraw_requests |

### 2.12 AdminAuth
| name | method | path | purpose | roles | auth | idempotency | request | response | errors | pages | tables |
|---|---|---|---|---|---|---|---|---|---|---|---|
| 管理员登录 | POST | /api/admin/v1/auth/login | 后台登录，返回短期 access token | public_guest | None | N/A | AdminLoginRequest | AdminLoginResponse | ADMIN_INVALID_CREDENTIALS, ADMIN_DISABLED | 后台登录页 | admin_users |
| 管理员登出 | POST | /api/admin/v1/auth/logout | 后台登出 | support_admin/ops_admin/finance_admin/super_admin | Admin Bearer | 幂等 | AdminLogoutRequest | OperationResponse | ADMIN_AUTH_INVALID | 后台全局 | audit_logs |

### 2.13 AdminDashboard
| name | method | path | purpose | roles | auth | idempotency | request | response | errors | pages | tables |
|---|---|---|---|---|---|---|---|---|---|---|---|
| 获取仪表盘摘要 | GET | /api/admin/v1/dashboard/summary | 运营概览 | ops_admin/finance_admin/super_admin | Admin Bearer | N/A | Query: dateRange(optional) | AdminDashboardSummaryResponse | ADMIN_PERMISSION_DENIED | 仪表盘 | accounts,vpn_subscriptions,orders,commission_withdraw_requests |

### 2.14 AdminAccounts
| name | method | path | purpose | roles | auth | idempotency | request | response | errors | pages | tables |
|---|---|---|---|---|---|---|---|---|---|---|---|
| 账号列表 | GET | /api/admin/v1/accounts | 后台查账号 | support_admin/ops_admin/super_admin | Admin Bearer | N/A | Query: page,pageSize,email,status,planCode | AdminAccountPageResponse | ADMIN_PERMISSION_DENIED | 用户列表 | accounts,vpn_subscriptions |
| 账号详情 | GET | /api/admin/v1/accounts/{accountId} | 账号详情 | support_admin/ops_admin/finance_admin/super_admin | Admin Bearer | N/A | Path: accountId | AdminAccountDetailResponse | ACCOUNT_NOT_FOUND | 用户详情 | accounts,client_sessions,vpn_subscriptions,commission_balances,referral_bindings |
| 冻结账号 | POST | /api/admin/v1/accounts/{accountId}/freeze | 冻结用户 | ops_admin/super_admin | Admin Bearer | 幂等 | AdminAccountStatusChangeRequest | OperationResponse | ACCOUNT_STATUS_CONFLICT | 用户详情 | accounts,audit_logs |
| 解冻账号 | POST | /api/admin/v1/accounts/{accountId}/unfreeze | 解冻用户 | ops_admin/super_admin | Admin Bearer | 幂等 | AdminAccountStatusChangeRequest | OperationResponse | ACCOUNT_STATUS_CONFLICT | 用户详情 | accounts,audit_logs |
| 驱逐会话 | POST | /api/admin/v1/accounts/{accountId}/evict-sessions | 强制用户重新登录 | ops_admin/super_admin | Admin Bearer | 幂等 | AdminEvictSessionsRequest | OperationResponse | ACCOUNT_NOT_FOUND | 用户详情 | client_sessions,audit_logs |

### 2.15 AdminPlans
| name | method | path | purpose | roles | auth | idempotency | request | response | errors | pages | tables |
|---|---|---|---|---|---|---|---|---|---|---|---|
| 套餐列表 | GET | /api/admin/v1/plans | 套餐管理 | ops_admin/super_admin | Admin Bearer | N/A | Query: status | AdminPlanPageResponse | ADMIN_PERMISSION_DENIED | 套餐管理 | plans |
| 创建套餐 | POST | /api/admin/v1/plans | 新建套餐 | ops_admin/super_admin | Admin Bearer | planCode unique | AdminPlanUpsertRequest | AdminPlanResponse | PLAN_CODE_DUPLICATE | 套餐管理 | plans,plan_region_permissions,audit_logs |
| 更新套餐 | PUT | /api/admin/v1/plans/{planId} | 编辑套餐 | ops_admin/super_admin | Admin Bearer | planId | AdminPlanUpsertRequest | AdminPlanResponse | PLAN_NOT_FOUND | 套餐管理 | plans,plan_region_permissions,audit_logs |
| 发布套餐 | POST | /api/admin/v1/plans/{planId}/publish | 将套餐状态改为 active | ops_admin/super_admin | Admin Bearer | 幂等 | AdminPublishPlanRequest | OperationResponse | PLAN_NOT_FOUND | 套餐管理 | plans,audit_logs |
| 禁用套餐 | POST | /api/admin/v1/plans/{planId}/disable | 下架套餐 | ops_admin/super_admin | Admin Bearer | 幂等 | AdminDisablePlanRequest | OperationResponse | PLAN_NOT_FOUND | 套餐管理 | plans,audit_logs |

### 2.16 AdminVPN
| name | method | path | purpose | roles | auth | idempotency | request | response | errors | pages | tables |
|---|---|---|---|---|---|---|---|---|---|---|---|
| 区域列表 | GET | /api/admin/v1/vpn/regions | 查区域 | ops_admin/super_admin | Admin Bearer | N/A | Query: tier,status | AdminRegionPageResponse | ADMIN_PERMISSION_DENIED | 区域管理 | vpn_regions |
| 创建区域 | POST | /api/admin/v1/vpn/regions | 新建区域 | ops_admin/super_admin | Admin Bearer | regionCode unique | AdminRegionUpsertRequest | AdminRegionResponse | REGION_CODE_DUPLICATE | 区域管理 | vpn_regions,audit_logs |
| 更新区域 | PUT | /api/admin/v1/vpn/regions/{regionId} | 编辑区域 | ops_admin/super_admin | Admin Bearer | regionId | AdminRegionUpsertRequest | AdminRegionResponse | REGION_NOT_FOUND | 区域管理 | vpn_regions,audit_logs |
| 节点列表 | GET | /api/admin/v1/vpn/nodes | 查节点 | ops_admin/super_admin | Admin Bearer | N/A | Query: regionId,status,healthStatus | AdminNodePageResponse | ADMIN_PERMISSION_DENIED | 节点管理 | vpn_nodes |
| 创建节点 | POST | /api/admin/v1/vpn/nodes | 新建节点 | ops_admin/super_admin | Admin Bearer | nodeCode unique | AdminNodeUpsertRequest | AdminNodeResponse | NODE_CODE_DUPLICATE | 节点管理 | vpn_nodes,audit_logs |
| 更新节点 | PUT | /api/admin/v1/vpn/nodes/{nodeId} | 编辑节点 | ops_admin/super_admin | Admin Bearer | nodeId | AdminNodeUpsertRequest | AdminNodeResponse | NODE_NOT_FOUND | 节点管理 | vpn_nodes,audit_logs |
| 禁用节点 | POST | /api/admin/v1/vpn/nodes/{nodeId}/disable | 下线节点 | ops_admin/super_admin | Admin Bearer | 幂等 | AdminDisableNodeRequest | OperationResponse | NODE_NOT_FOUND | 节点管理 | vpn_nodes,audit_logs |

### 2.17 AdminOrders
| name | method | path | purpose | roles | auth | idempotency | request | response | errors | pages | tables |
|---|---|---|---|---|---|---|---|---|---|---|---|
| 订单列表 | GET | /api/admin/v1/orders | 查询订单 | support_admin/ops_admin/finance_admin/super_admin | Admin Bearer | N/A | Query: page,pageSize,orderNo,email,status,networkCode,assetCode | AdminOrderPageResponse | ADMIN_PERMISSION_DENIED | 订单中心 | orders,order_payment_targets |
| 订单详情 | GET | /api/admin/v1/orders/{orderNo} | 查看订单详情与支付事件 | support_admin/ops_admin/finance_admin/super_admin | Admin Bearer | N/A | Path: orderNo | AdminOrderDetailResponse | ORDER_NOT_FOUND | 订单中心 | orders,order_payment_events |
| 标记异常/人工备注 | POST | /api/admin/v1/orders/{orderNo}/mark-exception | 记录异常订单处置 | ops_admin/finance_admin/super_admin | Admin Bearer | 幂等 | AdminMarkOrderExceptionRequest | OperationResponse | ORDER_STATUS_INVALID | 订单中心 | orders,audit_logs |
| 重试开通 | POST | /api/admin/v1/orders/{orderNo}/retry-provision | 对 failed/provisioning 异常单重试开通 | ops_admin/super_admin | Admin Bearer | 幂等 | AdminRetryProvisionRequest | OperationResponse | ORDER_STATUS_INVALID | 订单中心 | orders,vpn_subscriptions,vpn_access_identities |

### 2.18 AdminCommissions
| name | method | path | purpose | roles | auth | idempotency | request | response | errors | pages | tables |
|---|---|---|---|---|---|---|---|---|---|---|---|
| 佣金账本列表 | GET | /api/admin/v1/commissions/ledger | 查一级/二级佣金明细 | finance_admin/super_admin | Admin Bearer | N/A | Query: page,pageSize,beneficiaryEmail,level,status | AdminCommissionLedgerPageResponse | ADMIN_PERMISSION_DENIED | 佣金账本页 | commission_ledger,accounts |

### 2.19 AdminWithdrawals
| name | method | path | purpose | roles | auth | idempotency | request | response | errors | pages | tables |
|---|---|---|---|---|---|---|---|---|---|---|---|
| 提现列表 | GET | /api/admin/v1/withdrawals | 提现审核列表 | finance_admin/super_admin | Admin Bearer | N/A | Query: page,pageSize,status,accountEmail | AdminWithdrawalPageResponse | ADMIN_PERMISSION_DENIED | 提现审核页 | commission_withdraw_requests,accounts |
| 通过提现 | POST | /api/admin/v1/withdrawals/{requestNo}/approve | 审核通过 | finance_admin/super_admin | Admin Bearer | 幂等 | AdminApproveWithdrawalRequest | OperationResponse | WITHDRAW_STATUS_INVALID | 提现审核页 | commission_withdraw_requests,audit_logs |
| 拒绝提现 | POST | /api/admin/v1/withdrawals/{requestNo}/reject | 审核拒绝 | finance_admin/super_admin | Admin Bearer | 幂等 | AdminRejectWithdrawalRequest | OperationResponse | WITHDRAW_STATUS_INVALID | 提现审核页 | commission_withdraw_requests,commission_ledger,commission_balances,audit_logs |
| 录入打款哈希 | POST | /api/admin/v1/withdrawals/{requestNo}/record-payout | 记录打款 txHash 并进入确认 | finance_admin/super_admin | Admin Bearer | requestNo+txHash | AdminRecordPayoutRequest | OperationResponse | WITHDRAW_STATUS_INVALID | 提现审核页 | commission_withdraw_requests,audit_logs |
| 重试打款 | POST | /api/admin/v1/withdrawals/{requestNo}/retry-broadcast | 对 failed 提现重试打款流程 | finance_admin/super_admin | Admin Bearer | 幂等 | AdminRetryBroadcastRequest | OperationResponse | WITHDRAW_STATUS_INVALID | 提现审核页 | commission_withdraw_requests,audit_logs |

### 2.20 AdminVersions
| name | method | path | purpose | roles | auth | idempotency | request | response | errors | pages | tables |
|---|---|---|---|---|---|---|---|---|---|---|---|
| 版本列表 | GET | /api/admin/v1/app-versions | 版本管理列表 | ops_admin/super_admin | Admin Bearer | N/A | Query: status,channel | AdminAppVersionPageResponse | ADMIN_PERMISSION_DENIED | 版本管理 | app_versions |
| 创建版本 | POST | /api/admin/v1/app-versions | 创建版本元数据 | ops_admin/super_admin | Admin Bearer | versionCode unique per channel | AdminAppVersionCreateRequest | AdminAppVersionResponse | APP_VERSION_DUPLICATE | 版本管理 | app_versions,audit_logs |
| 发布版本 | POST | /api/admin/v1/app-versions/{versionId}/publish | 发布版本/强更策略 | ops_admin/super_admin | Admin Bearer | 幂等 | AdminAppVersionPublishRequest | OperationResponse | APP_VERSION_NOT_FOUND | 版本管理 | app_versions,audit_logs |

### 2.21 AdminLegal
| name | method | path | purpose | roles | auth | idempotency | request | response | errors | pages | tables |
|---|---|---|---|---|---|---|---|---|---|---|---|
| 法务文档列表 | GET | /api/admin/v1/legal-documents | 法务文档管理列表 | ops_admin/super_admin | Admin Bearer | N/A | Query: docType,status | AdminLegalDocumentPageResponse | ADMIN_PERMISSION_DENIED | 法务文档管理 | legal_documents |
| 更新法务文档草稿 | PUT | /api/admin/v1/legal-documents/{docType} | 保存草稿 | ops_admin/super_admin | Admin Bearer | docType + versionNo | AdminLegalDocumentUpsertRequest | AdminLegalDocumentResponse | LEGAL_DOC_NOT_FOUND | 法务文档管理 | legal_documents,audit_logs |
| 发布法务文档 | POST | /api/admin/v1/legal-documents/{docType}/publish | 发布法务文档 | ops_admin/super_admin | Admin Bearer | 幂等 | AdminPublishLegalDocumentRequest | OperationResponse | LEGAL_DOC_NOT_FOUND | 法务文档管理 | legal_documents,audit_logs |

### 2.22 AdminConfig
| name | method | path | purpose | roles | auth | idempotency | request | response | errors | pages | tables |
|---|---|---|---|---|---|---|---|---|---|---|---|
| 系统配置列表 | GET | /api/admin/v1/system-configs | 查看运行配置 | ops_admin/super_admin | Admin Bearer | N/A | Query: scope | AdminSystemConfigPageResponse | ADMIN_PERMISSION_DENIED | 系统配置页 | system_configs |
| 更新系统配置 | PUT | /api/admin/v1/system-configs/{configKey} | 更新运行配置 | ops_admin/super_admin | Admin Bearer | configKey | AdminSystemConfigUpdateRequest | OperationResponse | CONFIG_NOT_FOUND, CONFIG_VALUE_INVALID | 系统配置页 | system_configs,audit_logs |

### 2.23 AdminAudit
| name | method | path | purpose | roles | auth | idempotency | request | response | errors | pages | tables |
|---|---|---|---|---|---|---|---|---|---|---|---|
| 审计日志列表 | GET | /api/admin/v1/audit-logs | 查看审计日志 | ops_admin/finance_admin/super_admin | Admin Bearer | N/A | Query: module,actorType,targetType,page,pageSize,dateRange | AdminAuditLogPageResponse | ADMIN_PERMISSION_DENIED | 审计日志页 | audit_logs |

## 3. 字段与页面映射原则
1. 页面展示字段必须在接口 `data` 内可直接获取，或在页面规格中明确为本地派生字段。
2. 列表页所需的列表字段必须全部出现在对应 list response item 中。
3. 详情页所需详情字段不得只存在于列表接口中。
4. 状态字段必须与 `06_state_machine_and_business_rules.md` 保持一致。
5. 对资金相关接口，错误码不能复用模糊的 `FAILED`，必须返回可测试的业务错误码。

## 4. 与 DDL / 页面 / 状态机的映射规则
- `accounts` / `client_sessions` → Auth / Me / Admin Accounts
- `plans` / `plan_region_permissions` → Plans / Admin Plans
- `vpn_subscriptions` / `vpn_access_identities` / `vpn_regions` / `vpn_nodes` → VPN 接口 / 区域页 / 节点页
- `orders` / `order_payment_targets` / `order_payment_events` → Orders / 收银台 / 订单中心
- `account_wallet_public_addresses` / `chain_configs` / `asset_catalog` → Wallet 元数据接口
- `referral_bindings` / `commission_ledger` / `commission_balances` / `commission_withdraw_requests` → 邀请 / 佣金 / 提现
- `app_versions` / `legal_documents` / `system_configs` → Public/Version/Admin 配置接口
