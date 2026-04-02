# 09_data_model

## 1. 设计原则
1. 服务端只存储服务身份、订单、支付、订阅、节点、邀请、佣金、提现、版本、法务、配置与审计。
2. 服务端不存储用户私钥、助记词、seed。
3. 钱包普通交易历史以客户端本地缓存 + 链查询为主，服务端只保存与订单、提现相关的链事件。
4. 关键资金状态必须既有明细账本，又有余额汇总。
5. 所有关键状态字段必须与状态机文档一致。

## 2. 实体关系概览
- `accounts` 1:N `client_sessions`
- `accounts` 1:1 `vpn_subscriptions`
- `accounts` 1:1 `vpn_access_identities`
- `plans` 1:N `orders`
- `plans` N:N `vpn_regions` （通过 `plan_region_permissions`）
- `orders` 1:1 `order_payment_targets`
- `orders` 1:N `order_payment_events`
- `accounts` 1:1 `referral_bindings`（作为 invitee）
- `orders` 1:N `commission_ledger`
- `accounts` 1:N `commission_ledger`（作为 beneficiary）
- `accounts` 1:N `commission_withdraw_requests`
- `accounts` 1:N `account_wallet_public_addresses`

## 3. 服务端核心表清单
| 表 | 域 | 用途 | 关键字段 | 主键/唯一 | 核心索引 | 页面/API | 说明 |
|---|---|---|---|---|---|---|---|
| accounts | 身份 | 平台账号主体 | id,email,password_hash,status,referral_code,inviter_account_id | PK(id), UQ(lower(email)), UQ(referral_code) | status,last_login_at | 登录、注册、我的、后台用户 | 服务身份，不等于钱包身份 |
| verification_codes | 身份 | 邮箱验证码 | email,purpose,code_hash,status,expire_at | PK(id) | email+purpose+status | 注册、重置密码 | 只存 hash，不存明文验证码 |
| account_installations | 身份 | 安装实例观测，不做硬绑定 | account_id,installation_id,app_version,status,last_seen_at | PK(id), UQ(account_id,installation_id) | account_id,last_seen_at | 会话风控、后台用户详情 | 用于观测与未来扩展 |
| client_sessions | 身份 | 客户端 refresh session | account_id,refresh_token_hash,status,expires_at,invalidated_reason | PK(id), UQ(refresh_token_hash), partial UQ(account_id where active) | account_id,status,expires_at | 启动页、登录、登出、会话驱逐 | 实现单活跃 session |
| admin_users | 后台 | 后台管理员账号 | username,password_hash,role,status | PK(id), UQ(username) | role,status | 后台登录 | RBAC 以固定角色枚举实现 |
| audit_logs | 审计 | 后台与关键系统动作审计 | actor_type,actor_id,module,action,target_type,target_id,before_json,after_json | PK(id) | module+action,target,created_at | 后台审计日志 | 资金、状态、配置类操作必写 |
| plans | 套餐 | 可售套餐定义 | plan_code,name,price_usd,billing_cycle_months,region_access_policy,max_active_sessions,status | PK(id), UQ(plan_code) | status,display_order | 套餐页、后台套餐 | 一期默认不限流量 |
| plan_region_permissions | 套餐 | 自定义白名单区域映射 | plan_id,region_id | PK(id), UQ(plan_id,region_id) | plan_id,region_id | 套餐页、区域权限 | 仅当 region_access_policy=CUSTOM 时使用 |
| vpn_regions | VPN | 区域定义 | region_code,display_name,tier,status,sort_order | PK(id), UQ(region_code) | tier,status,sort_order | 区域选择、后台区域 | tier 区分 basic / advanced |
| vpn_nodes | VPN | 节点定义 | region_id,node_code,host,port,reality_public_key,server_name,short_id,flow,status,health_status | PK(id), UQ(node_code) | region_id,status,health_status | 后台节点 | 客户端不直接面对节点，仅面对区域 |
| vpn_access_identities | VPN | 账号到 VLESS UUID 映射 | account_id,uuid,email_tag,status,last_rotated_at | PK(id), UQ(account_id), UQ(uuid), UQ(email_tag) | status | VPN 配置签发、订阅开通 | 每账号一个活跃身份 |
| vpn_subscriptions | VPN | 当前订阅状态 | account_id,plan_id,status,started_at,expire_at,max_active_sessions,total_traffic | PK(id), UQ(account_id) | status,expire_at | VPN 首页、后台用户详情 | 即将到期为派生状态，不落库 |
| chain_configs | 支付/钱包 | 链 RPC 与策略配置 | network_code,rpc_public_url,rpc_proxy_enabled,required_confirmations,status | PK(id), UQ(network_code) | status | 钱包 precheck、支付扫描 | 确认数与广播兜底策略来自此表 |
| asset_catalog | 支付/钱包 | 支持资产目录 | network_code,asset_code,is_native,contract_address,decimals,status,order_payable,wallet_visible | PK(id), UQ(network_code,asset_code,contract_address) | network_code,status | 钱包首页、收银台、后台配置 | 首期只开放 SOL/TRX/USDT |
| payment_addresses | 支付 | 平台收款地址池 | network_code,asset_code,address,strategy,status,label | PK(id), UQ(network_code,asset_code,address) | network_code,asset_code,status | 订单支付目标 | 当前采用共享地址 + 唯一金额 delta |
| order_payment_targets | 支付 | 每单支付目标快照 | order_id,payment_address_id,collection_address,payable_amount,unique_amount_delta,expires_at,status | PK(id), UQ(order_id) | status,expires_at | 订单收银台 | 避免地址池策略变更影响历史单 |
| orders | 支付 | 购买/续费订单 | order_no,account_id,plan_id,order_type,quote_asset_code,quote_network_code,quote_usd_amount,payable_amount,status,submitted_client_tx_hash | PK(id), UQ(order_no) | account_id,status,created_at | 订单收银台、订单结果、后台订单 | 服务开通仅由订单驱动 |
| order_payment_events | 支付 | 链上支付事件审计 | order_id,chain,asset_code,tx_hash,event_index,from_address,to_address,amount,status,confirmations,raw_payload | PK(id), UQ(chain,tx_hash,event_index) | order_id,status,tx_hash | 后台订单详情 | 同一 tx 重复扫描必须幂等 |
| account_wallet_public_addresses | 钱包 | 账户可选同步的公开地址 | account_id,network_code,asset_code,address,is_default | PK(id), UQ(account_id,network_code,asset_code,address) | account_id,network_code | 收款页、提现预填 | 仅公开地址，不含私钥/助记词 |
| referral_bindings | 增长 | 一级/二级邀请关系锁定 | invitee_account_id,inviter_level1_account_id,inviter_level2_account_id,code_used,status,bound_at,locked_at | PK(id), UQ(invitee_account_id) | inviter_level1_account_id,inviter_level2_account_id,status | 邀请中心、后台用户详情 | 首单完成后锁定 |
| commission_rules | 增长 | 分佣规则版本 | rule_code,level1_rate_pct,level2_rate_pct,cooldown_days,min_withdraw_amount,settlement_asset_code,settlement_network_code,status,effective_at | PK(id), UQ(rule_code) | status,effective_at | 后台系统配置、佣金计算 | MVP 只启用一个 active 规则 |
| commission_ledger | 增长 | 佣金账本明细 | entry_no,beneficiary_account_id,source_order_id,commission_level,source_asset_code,source_amount,fx_rate_snapshot,settlement_amount,status,available_at | PK(id), UQ(entry_no), UQ(source_order_id,beneficiary_account_id,commission_level) | beneficiary_account_id,status,available_at | 邀请中心、后台佣金账本 | 账本为资金事实源 |
| commission_balances | 增长 | 佣金余额汇总 | account_id,settlement_asset_code,frozen_amount,available_amount,withdrawing_amount,withdrawn_total | PK(id), UQ(account_id,settlement_asset_code) | account_id | 邀请中心、提现申请 | 由 ledger 驱动更新 |
| commission_withdraw_requests | 增长 | 佣金提现申请 | request_no,account_id,amount,asset_code,network_code,payout_address,status,reviewer_admin_id,tx_hash,fail_reason | PK(id), UQ(request_no) | account_id,status,created_at | 提现申请、后台审核 | 默认资产网络 USDT on Solana |
| app_versions | 分发 | APK 版本与强更策略 | platform,channel,version_name,version_code,min_supported_code,force_update,download_url,sha256,status,published_at | PK(id), UQ(platform,channel,version_code) | platform,channel,status,published_at | 启动页、后台版本管理 | 仅 Android official channel |
| legal_documents | 法务 | 官网/App 法务文档 | doc_type,title,version_no,markdown_content,status,published_at | PK(id), UQ(doc_type,version_no) | doc_type,status | 法务页、后台法务管理 | 仅 published 文档对外可见 |
| system_configs | 配置 | 运行期配置项 | scope,config_key,config_value,description,mutable_in_prod | PK(id), UQ(scope,config_key) | scope | 后台系统配置、worker | 确认数、过期时间、冷静期等放这里 |

## 4. 状态枚举清单
### 4.1 身份
- `account_status_enum`: PENDING_VERIFY / ACTIVE / FROZEN / CLOSED
- `session_status_enum`: ACTIVE / EVICTED / REVOKED / EXPIRED

### 4.2 套餐与 VPN
- `plan_status_enum`: DRAFT / ACTIVE / DISABLED
- `region_tier_enum`: BASIC / ADVANCED
- `region_status_enum`: ACTIVE / MAINTENANCE / DISABLED
- `node_status_enum`: ACTIVE / MAINTENANCE / DISABLED / OFFLINE
- `subscription_status_enum`: PENDING_ACTIVATION / ACTIVE / EXPIRED / SUSPENDED / CANCELED

### 4.3 支付
- `order_status_enum`: AWAITING_PAYMENT / PAYMENT_DETECTED / CONFIRMING / PAID / PROVISIONING / COMPLETED / EXPIRED / UNDERPAID_REVIEW / OVERPAID_REVIEW / FAILED / CANCELED
- `payment_event_status_enum`: DETECTED / MATCHED / PENDING_CONFIRMATION / CONFIRMED / DUPLICATE_TX / LATE_PAYMENT / WRONG_ASSET / WRONG_NETWORK / PARSE_FAILED
- `payment_target_status_enum`: ACTIVE / EXPIRED / CLOSED

### 4.4 分佣与提现
- `commission_status_enum`: FROZEN / AVAILABLE / LOCKED_WITHDRAWAL / WITHDRAWN / REVERSED
- `withdraw_status_enum`: SUBMITTED / UNDER_REVIEW / APPROVED / REJECTED / BROADCASTING / CHAIN_CONFIRMING / COMPLETED / FAILED / CANCELED

## 5. 资金精度策略
- 所有链上金额字段使用 `numeric` 存储，避免浮点误差。
- API 层统一以 **decimal string** 返回。
- `asset_catalog.decimals` 是客户端格式化展示与签名构造的事实源。
- 佣金结算统一使用 USDT 计价，若来源订单为 SOL，则使用 `fx_rate_snapshot` 进行折算。

## 6. 唯一键策略
1. 账号：`email`、`referral_code`
2. 会话：`refresh_token_hash`、`active session per account`
3. 套餐：`plan_code`
4. 区域：`region_code`
5. 节点：`node_code`
6. 订单：`order_no`
7. 支付事件：`chain + tx_hash + event_index`
8. 账本：`source_order_id + beneficiary_account_id + commission_level`
9. 提现：`request_no`

## 7. 索引策略
### 7.1 查询主场景
- 登录与鉴权：按 email、session status 查找
- 订单中心：按 account/status/createdAt 查询
- 提现中心：按 status/createdAt 查询
- 节点签发：按 region/status/health 查询
- 审计：按 module/action/target/createdAt 查询

### 7.2 关键索引理由
- `uq_client_sessions_single_active`：强制单活跃 session
- `idx_orders_status_expires_at`：支持过期任务和状态筛选
- `uq_commission_ledger_source_order_beneficiary_level`：防重复记佣
- `idx_commission_withdraw_requests_status_created_at`：支撑财务审核列表
- `idx_vpn_nodes_region_status`：支撑区域签发与节点切换

## 8. 软删除 / 归档策略
- MVP 不使用通用软删除字段。
- 用户、订单、账本、提现等关键资金对象不做物理删除。
- 被禁用/下架通过 `status` 控制，而不是删除记录。
- 历史归档在二期通过冷热表或对象存储扩展。

## 9. 审计字段统一规范
- 所有可更新表统一包含 `created_at`、`updated_at`。
- 审计日志单独保留 `created_at`，不做 `updated_at`。
- 后台敏感操作必须在 `audit_logs` 记录 before/after 与 metadata。

## 10. Android 本地模型
| 模型 | 位置 | 说明 | 关键字段 |
|---|---|---|---|
| wallet_profiles_local | Android Room / SQLCipher | 本地加密保存钱包 profile 与助记词密文引用 | profileId, encryptedSeedBlob, createdAt |
| wallet_addresses_local | Android Room | 本地派生地址缓存 | profileId, networkCode, assetCode, address, path |
| wallet_tx_cache_local | Android Room | 本地交易缓存与状态更新 | txHash, networkCode, assetCode, amount, direction, status |

## 11. 与 API / 页面 / 状态机映射
- `accounts` / `client_sessions` → 登录页、启动页、我的页、Admin 用户
- `plans` / `plan_region_permissions` → 套餐页、Admin 套餐
- `vpn_regions` / `vpn_nodes` / `vpn_access_identities` / `vpn_subscriptions` → VPN 首页、区域选择、Admin 区域/节点
- `orders` / `order_payment_targets` / `order_payment_events` → 订单收银台、订单中心、支付 worker
- `account_wallet_public_addresses` / `chain_configs` / `asset_catalog` → 钱包首页、收款页、发送页
- `referral_bindings` / `commission_ledger` / `commission_balances` / `commission_withdraw_requests` → 邀请中心、账本页、提现页、Admin 财务页
- `app_versions` / `legal_documents` / `system_configs` → 启动页、强更页、法务页、Admin 配置页
