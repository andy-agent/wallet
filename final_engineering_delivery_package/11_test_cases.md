# 11_test_cases

## 1. 测试范围
- 主流程
- 异常流程
- 权限测试
- 状态切换
- API Contract Test
- 集成测试
- 回归测试
- 发布验收

## 2. 用例清单
| 编号 | 模块 | 前置条件 | 步骤 | 预期结果 | 优先级 |
|---|---|---|---|---|---|
| TC-001 | Auth | 无账号 | 发送注册验证码并完成邮箱注册（带 X-Idempotency-Key） | 账号创建成功，返回 token pair，session 为 ACTIVE | P0 |
| TC-001A | Auth | 同请求已注册成功 | 使用相同 X-Idempotency-Key 再次注册 | 返回相同结果（幂等），不重复创建账号 | P0 |
| TC-002 | Auth | 已有账号 | 邮箱+密码登录 | 登录成功，新 session 生效 | P0 |
| TC-003 | Auth | 同账号已在旧设备登录 | 新设备再次登录 | 旧 session 变 EVICTED，旧端 refresh 失败 | P0 |
| TC-004 | Plans | 已登录 | 查询套餐列表 | 仅返回 ACTIVE 且可售套餐 | P1 |
| TC-005 | Orders | 已登录且存在可售套餐 | 创建新购订单（带 X-Idempotency-Key） | 生成 orderNo、payment target、expiresAt | P0 |
| TC-005A | Orders | 已使用某 idempotencyKey 创建订单 | 使用相同 X-Idempotency-Key 再次下单 | 返回相同订单（幂等），不创建新订单 | P0 |
| TC-006 | Payment | 存在待支付订单 | 外部钱包按正确网络和金额转账 | 订单从 AWAITING_PAYMENT -> PAYMENT_DETECTED -> CONFIRMING -> PAID | P0 |
| TC-007 | Provision | 订单已 PAID | 触发开通 worker | 订阅 ACTIVE，订单 COMPLETED，VPN identity 就绪 | P0 |
| TC-008 | VPN | 订阅 ACTIVE | 请求区域列表并签发配置 | 仅返回可用且有权限区域；配置签发成功 | P0 |
| TC-009 | Wallet | 用户已登录 | 创建本地钱包并同步公开地址 | 本地钱包创建成功；服务端仅保存公开地址 | P1 |
| TC-010 | Wallet | 本地钱包存在且余额足够 | 发送 SOL / TRX / USDT | precheck 成功，直连广播成功或允许 fallback | P0 |
| TC-011 | Orders+Wallet | 存在待支付订单且本地钱包余额足够 | 使用内置钱包支付订单 | 提交 client tx hash 后，最终仍以扫描结果推进订单完成 | P0 |
| TC-012 | Referral | 新用户首单前 | 绑定邀请码 | 绑定成功，首单后关系锁定 | P1 |
| TC-013 | Commission | 来源订单 COMPLETED 且存在邀请关系 | 运行佣金生成任务 | 生成一级/二级账本，状态 FROZEN | P0 |
| TC-014 | Commission | 佣金 FROZEN 且已过冷静期 | 运行释放任务 | 账本转 AVAILABLE，余额汇总更新 | P0 |
| TC-015 | Withdrawal | 可提余额 >= 10 USDT | 提交提现申请（带 X-Idempotency-Key） | 申请状态 SUBMITTED，账本锁定 | P0 |
| TC-015A | Withdrawal | 已使用某 idempotencyKey 提交提现 | 使用相同 X-Idempotency-Key 再次提交 | 返回相同提现申请（幂等），不重复扣减余额 | P0 |
| TC-016 | Withdrawal | 存在提现申请 | 财务审核通过并录入 txHash | 状态 APPROVED -> BROADCASTING/CHAIN_CONFIRMING -> COMPLETED | P0 |
| TC-017 | Version | 当前版本低于最小支持版本 | 启动 App | 命中 force update，阻断进入首页 | P0 |
| TC-018 | Auth | 账号被冻结 | 尝试登录 | 返回 AUTH_ACCOUNT_FROZEN | P0 |
| TC-019 | Orders | 订单已过期 | 再次查询支付目标 | 订单状态 EXPIRED，不再允许继续支付 | P0 |
| TC-020 | Payment | 待支付订单 | 少付金额到账 | 进入 UNDERPAID_REVIEW，不自动开通 | P0 |
| TC-021 | Payment | 待支付订单 | 多付金额到账 | 进入 OVERPAID_REVIEW，不自动升级套餐 | P0 |
| TC-022 | Payment | 已过期订单 | 过期后到账 | 保留订单 EXPIRED，支付事件记为 LATE_PAYMENT 或 review | P1 |
| TC-023 | Payment | 同一 tx 被 scanner 重复扫描 | 重复处理同 tx | 不重复开通、不重复记账 | P0 |
| TC-024 | VPN | 区域为 ADVANCED，套餐仅 BASIC_ONLY | 请求签发高级区域配置 | 返回 VPN_REGION_FORBIDDEN | P0 |
| TC-025 | Wallet | 输入无效地址 | 发送页 precheck | 返回 WALLET_INVALID_ADDRESS | P0 |
| TC-026 | Wallet | 直连 RPC 不可用且 proxy disabled | 尝试广播 | 返回 WALLET_PROXY_BROADCAST_DISABLED 或广播失败，不自动代签 | P1 |
| TC-027 | Referral | 用户已完成首单 | 再次绑定邀请码 | 返回 REFERRAL_BINDING_LOCKED | P1 |
| TC-028 | Withdrawal | availableAmount < 10 USDT | 提交提现 | 返回 WITHDRAW_MIN_AMOUNT_NOT_MET | P0 |
| TC-029 | Withdrawal | 提现 UNDER_REVIEW | 财务拒绝并填写原因 | 状态 REJECTED，锁定账本回滚 | P0 |
| TC-030 | Withdrawal | 提现已 APPROVED | 录入错误 txHash 或打款失败 | 状态 FAILED，可 retry-broadcast 或退回 under_review | P1 |
| TC-031 | Admin | support_admin 登录 | 尝试访问提现审批接口 | 返回 ADMIN_PERMISSION_DENIED | P0 |
| TC-032 | Admin | ops_admin 修改套餐 | 查看 audit_logs | 存在对应 before/after 审计记录 | P0 |
| TC-033 | Deployment | staging 环境已部署 | 执行数据库迁移并冒烟 | 迁移成功，健康检查通过 | P0 |
| TC-034 | Regression | 执行一次完整主链路后 | 重复执行登录、下单、支付、开通、提现 | 无回归阻断，状态机闭环 | P0 |

## 3. 验收门禁
### 3.1 P0 门禁
- TC-001 ~ TC-017
- TC-018 ~ TC-031 中所有 P0
- TC-033、TC-034

### 3.2 不通过标准
- 同一订单重复开通
- 同一 tx 重复记账
- 会话互斥失效
- 钱包私钥/助记词进入服务端日志或数据库
- 提现拒绝/失败未回滚账本
- force update 失效
- Admin 权限越权

## 4. 自动化建议
- API Contract Test：对 `08_openapi_v1.yaml` 做 schema 校验
- Worker 状态流测试：支付确认、开通、佣金释放、提现确认
- Android UIState 测试：登录、收银台、发送页、邀请页、提现页、强更页
- Admin 权限回归：support / ops / finance / super 各角色矩阵
