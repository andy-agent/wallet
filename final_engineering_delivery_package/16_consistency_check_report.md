# 16_consistency_check_report

## 1. 自检结果
| 检查项 | 结果 | 说明 |
|---|---|---|
| PRD 与 API 是否一致 | 通过 | 所有主模块、路径前缀、认证方式、主业务流均已映射到 07/08 |
| API 与 DDL 是否一致 | 基本通过 | 主实体均有承载表；钱包普通历史未落服务端，已在 PRD/API/数据模型中统一说明 |
| 页面与 API 是否一致 | 通过 | 05 中每个页面都映射主 API 与主表 |
| 页面与状态机是否一致 | 通过 | 订单/订阅/提现/会话状态均可在 06 中找到事实状态 |
| 权限矩阵与接口鉴权是否一致 | 通过 | Client / Admin / Public 三域已统一，Admin 角色四分 |
| 错误码是否冲突 | 通过 | 07 中按通用/鉴权/业务/后台分层，不存在同码多义 |
| 状态枚举是否闭环 | 基本通过 | 主状态闭环；`expiring_soon` 明确为页面派生状态，不落库 |
| DDL 是否支撑首版启动 | 有条件通过 | 核心表、索引、唯一约束、审计表已齐备；首启仍需同目录 `10_postgresql_bootstrap_seed.sql` 中的最小 seed 数据 |
| Android 联调任务是否覆盖核心页面 | 通过 | 登录、收银台、VPN、钱包、邀请、提现、强更均覆盖 |
| Admin 任务是否覆盖配置/审核/查询/详情/操作 | 通过 | 用户、订单、套餐、节点、提现、版本、法务、配置、审计均覆盖 |
| CI/CD 与部署步骤是否可落地 | 基本通过 | 基于 Docker Compose 的 provider-agnostic 模板已给出，具体仓库/Runner 待确认 |

## 2. 发现并已修正的问题
1. **后端技术栈并行口径**：历史包中的 FastAPI / NestJS 并行方案已收敛为 NestJS。
2. **钱包广播边界不统一**：已统一为“客户端直连广播默认，后端代理广播兜底”。
3. **设备绑定与 session 互斥冲突**：已统一为单活跃 refresh session，不做显式设备绑定。
4. **计划-区域建模不一致**：已统一为 `plans.region_access_policy + plan_region_permissions`。
5. **expiring_soon 落库问题**：已改为页面派生状态，不再落库。
6. **钱包服务端持久化范围不一致**：已统一为仅保存可选公开地址，不保存普通钱包交易历史。
7. **API 前缀不一致**：已统一为 `/api/public/v1`、`/api/client/v1`、`/api/admin/v1`。
8. **Admin Refresh Token 口径断裂**：原假设包含 admin refresh token，但 API 和 DDL 未实现。已统一移除 admin refresh token，改为短期 access token，过期后重新登录。
9. **幂等键契约断裂**：注册、重置密码、创建订单、创建提现已统一为 `X-Idempotency-Key` header；OpenAPI 中可机器读取。
10. **OpenAPI 查询参数漂移**：
   - `/api/client/v1/plans` 已补 `channel` 查询参数
   - `/api/admin/v1/dashboard/summary` 已补 `dateRange` 查询参数
   - `/api/client/v1/withdrawals` 已移除 POST 方法误挂的 `status` 查询参数
11. **数据模型与 DDL 字段语义漂移**：
   - `chain_configs` 统一为 `public_rpc_url` / `proxy_rpc_url` / `direct_broadcast_enabled` / `proxy_broadcast_enabled` / `is_active`
   - `payment_addresses` 统一为 `is_active`
   - `asset_catalog` 统一为 `is_active` 并补齐 `display_name`、`symbol`
   - `vpn_nodes` 补齐协议与心跳字段
12. **首启 bootstrap seed 缺失**：已补充最小 seed 模板，并将首启执行要求写入迁移和部署基线。
13. **requestId 审计回查链路补齐**：统一为“所有请求写结构化日志；所有会落审计的敏感操作把 requestId 持久化到 `audit_logs.request_id`”，避免全表扩散。
14. **错误码总表补齐**：已将 07 中实际使用但未登记的错误码补入统一错误码字典，减少实现与测试各自补码的风险。

## 3. 仍需人工关注但不阻塞当前收敛的点
- RPC 提供商与确认阈值
- 钱包 SDK 最终选型
- 提现自动化等级
- 套餐价格与高级区域清单
- 生产 Runner / 仓库 / 域名 / 密钥管理方案

## 4. 高风险猜测项列表
| 设计点 | 为什么可能错 | 会影响什么 | 建议如何确认 |
|---|---|---|---|
| Solana / TRON 确认阈值默认值 | 真实生产确认阈值可能与默认假设不同 | 支付确认速度、订单完成率、提现确认 | 在支付负责人冻结前只作为 system_configs 默认值 |
| 钱包 SDK 统一适配方案 | TRON 与 Solana 的签名/代币发送成熟度不一定能被单一 SDK 覆盖 | Android 钱包排期、发送功能稳定性 | 先做双链 POC，再决定统一 SDK 还是链适配器 |
| 代理广播 fallback 的可用性 | 不同 RPC 提供商对 raw tx 接口与限流策略不同 | 发送页兜底体验、后端安全边界 | 先冻结 provider，再灰度启用 proxy broadcast |
| 共享地址 + 唯一金额策略的稳定性 | 未来支付量提升后对账复杂度会上升 | 扫描性能、异常单处理 | MVP 先用，量级提升后再切换到地址派生池 |
| Docker Compose 首发部署是否足够 | 若扫描/worker/节点控制增长过快，单机编排可能成为瓶颈 | 扩容、可观测性、回滚速度 | 按 staging 压测结果决定是否提前拆多 VM |

## 5. 结论
本开发启动包已经满足以下条件：
- PRD、DDL、OpenAPI、页面、状态机、测试、部署为唯一口径
- 不再存在历史工程包中的并行冲突方案
- 可直接作为多 Agent 自动化开发的主输入

上线前仍需要冻结高优先级待确认项，但这不会影响当前用 Kimi Code 或其他 AI 编码代理建立工程与拆分 BD 任务。
