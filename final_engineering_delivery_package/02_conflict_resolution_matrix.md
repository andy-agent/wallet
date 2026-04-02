# 02_conflict_resolution_matrix

| 编号 | 冲突主题 | 来源文件 | 方案A | 方案B | 方案C | 采用方案 | 理由 | 影响范围 | 是否已同步修正所有下游文件 |
|---|---|---|---|---|---|---|---|---|---|
| C01 | 钱包定位 | A/B/C + v1.1 | 轻支付壳 | 多链自托管轻钱包 | 完整多链钱包但 MVP 收敛链范围 | 方案C | 满足最新冻结业务基线：钱包不是纯支付壳，同时保留 MVP 可落地性 | PRD、页面、钱包接口、测试 | 是 |
| C02 | 后端技术栈 | A/B/C | FastAPI + Alembic | NestJS + PostgreSQL + Redis + BullMQ | Go + PostgreSQL + Redis | 方案B | 更适合模块化单体、OpenAPI 契约驱动、多 Agent 并行开发与 CRUD/后台协同；FastAPI 仅保留为历史参考 | 工程骨架、部署、CI/CD、任务拆分 | 是 |
| C03 | 登录互斥策略 | 早期 PRD vs v1.1/B/C | 显式设备绑定/换绑 | 单活跃 refresh session | 多端并发 | 方案B | 与已冻结业务规则一致，MVP 成本最低，足以抑制共享 | auth、session、页面状态、测试 | 是 |
| C04 | 支付资产范围 | 原始需求 vs v1.1/B/C | SOL + USDT(TRC20) | SOL + USDT(Solana) + USDT(TRC20) | 更多链全开 | 方案B | v1.1 已冻结且覆盖钱包支付、提现与手续费场景 | plans、orders、asset_catalog、OpenAPI、测试 | 是 |
| C05 | 钱包签名与广播边界 | B/C 历史接口与用户补充规则 | 后端构造+后端广播 | 客户端签名 + 客户端直连广播；后端代理广播兜底 | 后端代签名/托管私钥 | 方案B | 与最新冻结安全边界一致；兼顾移动端可用性与后端兜底 | 钱包页面、wallet API、风险说明、测试 | 是 |
| C06 | 支付目标策略 | A/B/C DDL 与 PRD | 每单专属地址 | 共享收款地址 + 唯一金额 | 共享地址池 + 每单 payment_target 快照 | 方案C | C 兼顾 MVP 实现速度、可审计性与后续升级空间；最终执行策略为共享地址+唯一金额，且为每单固化快照 | orders、payment_addresses、order_payment_targets、扫描 worker | 是 |
| C07 | 计划-区域建模 | A/B/C DDL | allowed_regions_json | 只靠 includes_advanced_regions | region_access_policy + plan_region_permissions | 方案C | 最可测试，支持基本区、高级区、精确白名单三种策略 | plans、vpn_regions、页面权限、后台套餐页 | 是 |
| C08 | 订阅状态是否保存 expiring_soon | B/C | 持久化 expiring_soon | 仅作为页面派生状态 | 无即将到期概念 | 方案B | 避免冗余状态和定时任务，数据库仅保存真实服务态 | subscription 状态机、页面状态、测试 | 是 |
| C09 | 钱包元数据是否上云 | A/B/C | 服务端保存 wallet_profiles/wallet_tx | 仅保存公开地址（可选） | 完全不保存任何钱包元数据 | 方案B | 兼顾自托管边界与提现预填、运营观测；不保存助记词和私钥 | data model、wallet API、隐私说明 | 是 |
| C10 | 佣金规则 | 早期包 vs v1.1/B/C | 一级单层 15% | 一级25%/二级5% | 运营动态输入但默认不固定 | 方案B | v1.1 已冻结，且增长模型与页面、账本、测试均已围绕该规则展开 | commission_rules、ledger、withdraw、测试 | 是 |
| C11 | 提现默认资产与门槛 | v1.1/B/C | 按原支付币种提现 | 默认 USDT on Solana，最低 10 USDT | 允许多网络任选 | 方案B | 结算口径清晰，财务/风控最稳定 | 提现页面、审核、system_configs、测试 | 是 |
| C12 | Admin RBAC 粒度 | B/C | admin 单角色 | super/ops/finance/support 四角色 | 数据库可配置复杂 RBAC | 方案B | 满足 MVP 分权且不引入复杂权限设计 | admin_users、接口鉴权、测试 | 是 |
| C13 | 部署基线 | B/C 模板 | K8s | 单 VM + Docker Compose | 纯 systemd 手工部署 | 方案B | 适合无人值守 MVP、成本低、回滚可控；K8s 不作为首发必需 | 部署文档、CI/CD、运维任务 | 是 |
| C14 | API 前缀规范 | A/B/C | /api/v1 混合 client/admin | /api/client/v1、/api/admin/v1、/api/public/v1 | 无统一前缀 | 方案B | 边界最清晰，适合多端与多 Agent 协作 | OpenAPI、前端路由、鉴权中间件 | 是 |
| C15 | 数据库钱包交易表 | A/C | 服务端持久化全部钱包交易 | 服务端只持久化与订单/提现相关链事件 | 完全无钱包相关服务端记录 | 方案B | 订单与提现必须审计；普通钱包历史保持客户端与链查询职责 | DDL、隐私边界、测试 | 是 |
