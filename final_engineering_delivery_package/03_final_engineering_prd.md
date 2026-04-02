# 03_final_engineering_prd

## 1. 项目背景
CryptoVPN 是一个以 VPN 订阅为核心收入、以多链自托管钱包作为关键辅助模块的 Android 项目。  
它服务于已经具备加密货币使用经验的用户，通过官网 APK 分发、账号登录、链上支付、自动开通、区域选择、邀请返佣和链上提现，形成完整的商业闭环。

**来源口径**
- 业务冻结规则：v1.1 确认版
- 工程细化：历史工程包 B/C 的 DDL/OpenAPI/页面/任务资产
- 本轮技术裁决：本交付包的冲突矩阵与主文档

## 2. 产品目标
1. 建立“下载 → 登录 → 购买 → 支付 → 开通 → 连接 → 续费”的最短收入链路。
2. 建立“邀请 → 记账 → 冷静期 → 提现 → 链上打款”的增长闭环。
3. 建立“创建/导入钱包 → 收款 → 发送 → 订单支付”的钱包闭环。
4. 建立“套餐、节点、订单、提现、版本、法务、审计”可运营的后台能力。
5. 用统一的 PRD、状态机、OpenAPI、DDL、测试和 Agent 任务包驱动自动化开发。

## 3. 成功标准
| 指标 | 定义 | 验收口径 |
|---|---|---|
| 注册转付费主链路可用 | 用户可完成邮箱注册→下单→支付→订阅开通→连接成功 | 测试环境 100% 跑通，预生产演练 3 次以上无阻断 |
| 支付与开通幂等 | 同一订单/同一 tx 不会重复开通与重复记佣 | 回归测试覆盖同 tx 重扫、重复开通、重复提现锁定 |
| 钱包核心闭环可用 | 创建/导入、收款、发送、订单内支付可正常执行 | Solana 与 TRON 两链均通过 POC 与集成测试 |
| 后台可运营 | 用户、订单、套餐、区域、节点、提现、版本、法务、审计可日常操作 | 后台关键页面具备查询、详情、必要操作与审计 |
| 无人值守开发可推进 | 多 Agent 可以按固定输入输出并行开发 | 任务包、OpenAPI、DDL、状态机足以拆仓内任务 |

## 4. 范围定义
### 4.1 本期范围
- Android App：邮箱注册/登录、VPN、钱包、订单支付、邀请与提现、法务、版本门禁
- Backend：认证、会话、套餐、订单、支付扫描、订阅、VPN 配置签发、分佣、提现、版本、法务、审计
- Admin：用户、订单、套餐、区域、节点、佣金账本、提现审核、版本、法务、系统配置、审计
- 官网：单主域名、APK 下载、版本说明、法务文档

### 4.2 增强范围（不纳入首发交付）
- 更多公链、更多资产
- 套餐升级/降级
- 多并发会话高级套餐
- Swap / WalletConnect / DApp
- 更复杂的风控、活动、消息中心

### 4.3 明确不做
- iOS
- 应用商店上架
- 用户导入外部 VPN 节点或订阅
- 后端托管用户私钥
- 后端代签名
- 实时链上自动分账
- 多签、硬件钱包、NFT、DeFi、跨链桥
- K8s 作为首发强依赖

## 5. 用户角色
| 角色 | 职责 | 数据边界 | 接口边界 |
|---|---|---|---|
| public_guest | 启动、版本检查、阅读法务、注册/登录 | 无登录用户数据 | 仅公共接口和认证接口 |
| user | 查看套餐、下单、支付、查看订阅、钱包收发、查看邀请与账本、提交提现 | 仅本人账号/订单/订阅/佣金/提现 | Client Bearer 接口 |
| subscribed_user | 拥有 user 全部能力，且可连接 VPN、申请签发配置 | 仅本人有效订阅与区域权限 | Client Bearer + 订阅校验 |
| support_admin | 查询用户、订单、订阅、查看异常、客服备注 | 可读运营数据，不可写资金结论 | Admin Bearer + 只读/低风险操作 |
| ops_admin | 管理套餐、区域、节点、版本、系统配置、账户冻结/解冻、会话驱逐 | 可写运营配置，不可执行财务打款 | Admin Bearer + 运营写接口 |
| finance_admin | 查看佣金账本、审核提现、录入打款哈希、重试打款 | 财务相关数据 | Admin Bearer + 财务接口 |
| super_admin | 全量权限与应急纠偏 | 全量 | 全部接口 |

## 6. 术语表
| 术语 | 定义 |
|---|---|
| Account | 平台服务身份主体，承载登录、订阅、邀请、佣金与提现归属 |
| Wallet | 客户端本地自托管资产容器，承载助记词、地址、签名与广播 |
| Session | 账号的 refresh token 会话，单账号同一时刻仅允许一个 active |
| Plan | 平台售卖的 VPN 套餐定义 |
| Subscription | 账号当前持有的 VPN 服务状态 |
| Region | 用户可选择的 VPN 区域，不直接暴露节点细节 |
| Node | 区域下的实际 VPN 节点 |
| Order | 一次购买/续费的待支付请求与履约载体 |
| Payment Target | 每单支付目标快照，包含收款地址、网络、精确金额与过期时间 |
| Payment Event | 链上扫描产生的支付事件记录 |
| Referral Binding | 邀请关系绑定记录，仅锁到两级 |
| Commission Ledger | 佣金账本事实源，按订单与受益人逐笔记账 |
| Withdrawal | 佣金提现申请与链上打款记录 |

## 7. 核心业务定义
### 7.1 账号与钱包边界
- 账号是 **平台服务身份**，用于订阅归属、邀请关系、提现申请和后台审计。
- 钱包是 **客户端资产身份**，用于本地签名、链上发送、订单支付和提现接收。
- 两者相关联但不合并：**订阅归属账号，不归属钱包地址**。

### 7.2 钱包定位
- 钱包是“完整多链钱包”的产品定位。
- MVP 链范围收敛到 Solana 与 TRON，资产范围收敛到 SOL、TRX、USDT(Solana)、USDT(TRON/TRC20)。
- 钱包必须支持：创建/导入、解锁、收款、发送、订单支付。
- 钱包不做：DApp、Swap、WalletConnect、多签、硬件钱包。

### 7.3 支付定位
- 订单支付只支持链上支付。
- 服务端只负责：下单、支付目标生成、扫描、确认、开通、审计。
- 服务端不负责：保存助记词、替用户签名、替用户管理日常钱包资产。

### 7.4 分佣定位
- 一期只做两级邀请关系。
- 一级佣金 25%，二级佣金 5%。
- 佣金统一按 USDT 账本记账。
- 提现最低 10 USDT，默认 USDT on Solana。
- 规则路径：**支付上链 → 佣金记账 → 提现申请 → 链上打款**。

## 8. 业务流程
### 8.1 主流程：新用户购买并连接 VPN
1. 用户安装 App，启动页检查版本与会话。
2. 用户邮箱注册/登录。
3. 用户进入套餐页，选择套餐和支付资产/网络。
4. 系统创建订单和 payment target。
5. 用户使用外部钱包，或使用内置钱包本地签名后直连广播。
6. 支付扫描命中订单，确认数达到阈值后订单进入 paid。
7. 开通 worker 创建/续期订阅并签发/同步 VPN 身份。
8. 用户选择区域，客户端申请短时效配置并连接。
9. 用户在订阅期内正常使用，到期前收到提醒并可续费。

### 8.2 分支流程：内置钱包支付
1. 用户先在本地创建/导入钱包。
2. 订单收银台选择“内置钱包支付”。
3. 发送页完成 precheck。
4. 客户端本地签名，默认 direct broadcast。
5. 若直连广播失败且链配置允许，可进入后端 proxy broadcast fallback。
6. 客户端把 txHash 作为提示提交给订单系统；订单成败仍以扫描与确认为准。

### 8.3 分支流程：邀请与提现
1. 新用户在首单前绑定邀请码。
2. 来源订单完成后生成一级/二级账本。
3. 冷静期结束，账本转 available。
4. 用户在提现页提交 USDT on Solana 提现申请。
5. 财务审核并记录打款 txHash。
6. 链确认达阈值后提现完成，账本余额同步扣减。

### 8.4 异常流程
- 登录：新端登录挤下旧端，旧端 refresh 失败后回登录页。
- 支付：错网/错币/多付/少付/晚付进入 review，不自动开通。
- 钱包：直连广播失败仅能在允许条件下走代理广播，不自动代签。
- 提现：审批拒绝需写原因；打款失败可重试或退回 under_review。
- 版本：命中 force update 时阻断进入首页。

## 9. 功能模块
### 9.1 认证与会话模块
- 邮箱注册、登录、重置密码
- refresh token 轮换
- 单活跃 refresh session
- 会话驱逐和账号冻结联动

### 9.2 VPN 模块
- 区域列表
- 短时效配置签发
- 连接授权状态
- 套餐控制高级区域可见/可用
- 协议固定 VLESS + Reality + XTLS/Vision
- 不允许导入外部节点或外部订阅

### 9.3 套餐与订阅模块
- 套餐定义、上下架、价格、周期、区域策略
- 当前订阅展示
- 续费只支持同套餐续期
- 即将到期状态由页面派生，不落库

### 9.4 订单与支付模块
- 创建订单
- 共享收款地址 + 唯一金额 delta
- payment target 快照
- 支付扫描、确认推进、幂等开通
- 异常单分类与后台处理

### 9.5 钱包模块
- 本地助记词与地址
- 收款、发送、订单支付
- 公开地址可选同步到服务端
- direct broadcast 默认，proxy broadcast fallback

### 9.6 邀请/佣金/提现模块
- 邀请码绑定与锁定
- 一级/二级佣金计算
- 冷静期释放
- 可提余额汇总
- 提现申请、审核、打款和确认

### 9.7 Admin 模块
- 用户
- 套餐
- 区域 / 节点
- 订单 / 支付事件
- 佣金账本
- 提现审核
- 版本
- 法务
- 系统配置
- 审计日志

## 10. 页面需求摘要
> 页面完整定义见 `05_ia_and_page_spec.md`。

### Android 核心页面
- 启动页/强更页
- 登录/注册/重置密码
- VPN 首页、套餐页、区域选择页
- 订单收银台、订单结果页
- 钱包引导、钱包首页、资产详情、收款、发送
- 邀请中心、佣金账本、提现申请
- 我的页、法务页

### Admin 核心页面
- 登录页、仪表盘
- 用户列表/详情
- 套餐管理
- 区域管理、节点管理
- 订单中心
- 佣金账本、提现审核
- 版本管理、法务文档管理、系统配置、审计日志

## 11. 关键字段定义
### 11.1 身份域
- `accounts.email`：登录主标识
- `client_sessions.refresh_token_hash`：唯一 active 会话控制点
- `account_installations.installation_id`：安装实例观测标识，不做硬绑定

### 11.2 订单支付域
- `orders.order_no`：用户可见业务订单号
- `orders.quote_asset_code` / `quote_network_code`：报价资产与网络
- `order_payment_targets.payable_amount`：应付精确金额
- `order_payment_targets.unique_amount_delta`：唯一金额扰动值
- `order_payment_events.tx_hash`：链上事件追踪主键之一

### 11.3 订阅与 VPN 域
- `vpn_subscriptions.expire_at`：服务终止时间
- `vpn_access_identities.uuid`：VLESS 用户标识
- `plans.region_access_policy`：区域权限策略
- `vpn_regions.tier`：basic/advanced

### 11.4 分佣提现域
- `referral_bindings.inviter_level1_account_id`
- `referral_bindings.inviter_level2_account_id`
- `commission_ledger.status`
- `commission_balances.available_amount`
- `commission_withdraw_requests.status`

## 12. 权限规则
### Client
- 所有 Client Bearer 接口都只允许访问当前账号数据。
- 无有效订阅时不可请求 VPN 配置签发。
- 提现接口仅允许对本人可提余额操作。

### Admin
- support_admin：只读查询与客服备注，不可做资金结论。
- ops_admin：管理套餐/区域/节点/版本/配置/用户状态，不可执行提现打款。
- finance_admin：处理账本与提现，不可改节点/版本/套餐。
- super_admin：应急和最终纠偏。

## 13. 业务状态
> 以 `06_state_machine_and_business_rules.md` 为最终事实源。

- Account：PENDING_VERIFY / ACTIVE / FROZEN / CLOSED
- Session：ACTIVE / EVICTED / REVOKED / EXPIRED
- Order：AWAITING_PAYMENT / PAYMENT_DETECTED / CONFIRMING / PAID / PROVISIONING / COMPLETED / EXPIRED / UNDERPAID_REVIEW / OVERPAID_REVIEW / FAILED / CANCELED
- Subscription：PENDING_ACTIVATION / ACTIVE / EXPIRED / SUSPENDED / CANCELED
- Commission：FROZEN / AVAILABLE / LOCKED_WITHDRAWAL / WITHDRAWN / REVERSED
- Withdrawal：SUBMITTED / UNDER_REVIEW / APPROVED / REJECTED / BROADCASTING / CHAIN_CONFIRMING / COMPLETED / FAILED / CANCELED

## 14. 异常处理与补偿
1. 同一 tx 重扫：不得重复开通，不得重复记账。
2. 同一订单 retry-provision：不得重复创建第二份活跃订阅。
3. 提现拒绝或失败：需解锁或回滚对应账本。
4. 新登录挤旧登录：只保留最新 ACTIVE refresh session。
5. 法务/版本接口失败：版本检查失败时可提示稍后重试；force update 命中时必须阻断。

## 15. 非功能要求
- **安全**：私钥不入服务端，敏感操作留审计，Admin 关键动作必留痕。
- **幂等**：订单创建、支付事件处理、佣金生成、提现创建与打款记录都要幂等。
- **性能**：核心列表接口支持分页；支付与提现 worker 支持断点重跑。
- **可观测性**：API、worker、scanner、provisioner 均输出结构化日志和 requestId。
- **可部署性**：支持 dev / staging / prod 三环境，支持数据库迁移与回滚。

## 16. 安全边界
1. 严禁服务端保存助记词、私钥、seed。
2. 严禁服务端代签名。
3. 严禁客户端导入外部 VPN 节点。
4. 默认 direct broadcast；proxy broadcast 只接收已签名原始交易。
5. 支付、提现、账户冻结、配置变更必须写审计日志。
6. 提现出款必须有审批与 txHash 记录。

## 17. 技术基线
### 17.1 Backend
- NestJS 模块化单体
- PostgreSQL
- Redis
- BullMQ
- SQL 迁移以 `10_postgresql_core_ddl.sql` 为基线
- OpenAPI 作为契约先行

### 17.2 Android
- Kotlin
- Jetpack Compose
- MVVM + Hilt
- v2rayNG 二开
- 钱包底层走本地签名 + 链适配器

### 17.3 Admin
- Next.js / React
- TypeScript
- Ant Design
- RBAC 路由守卫

### 17.4 未采用历史方案的理由
- **未采用 FastAPI**：虽历史资产完整，但用户已明确 Go/NestJS 优先；NestJS 更适配当前 Admin/Backend/契约一体化与多 Agent 并行。
- **未采用 Go 作为首发最终栈**：Go 更适合高性能服务，但在本项目的后台 CRUD、RBAC、OpenAPI、DTO/校验与多 Agent 协作上，NestJS 落地更快。

## 18. 验收标准
### 18.1 业务验收
- 邮箱注册/登录/重置密码可用
- 单活跃 session 生效
- 订单支付 → 订阅开通 → VPN 连接闭环可跑通
- 钱包创建/导入/收款/发送/支付可用
- 邀请绑定、佣金生成、提现申请与审核可用
- 后台核心页面具备查询、详情、操作、审计

### 18.2 技术验收
- OpenAPI 与实际响应字段对齐
- DDL 支持首版启动
- 关键状态机无断裂
- 订单、佣金、提现关键流程幂等
- 强更与会话失效处理可验证

### 18.3 上线验收
- 版本检查与官网下载正常
- 法务文档可访问
- 监控、日志、告警已接入
- 回滚方案可演练
- 高优先级待确认项已冻结

## 19. 待确认项
- 订单/提现链确认阈值最终值
- RPC 提供商与 SLA
- 钱包 SDK 最终选型
- 套餐价格与高级区域清单
- 提现自动化等级
