# 12_dev_task_breakdown

## 1. 阶段划分与依赖
| 阶段 | 任务包 | 依赖 | 可并行 | 输入 | 输出 | 完成标准 |
|---|---|---|---|---|---|---|
| S0 | 契约冻结 | 无 | 否 | 00/02/03/06/07/09/10 | 冻结版状态机、DDL、OpenAPI | 不得再修改表名、主状态、接口前缀 |
| S1 | Backend Foundation | S0 | 部分 | 10_postgresql_core_ddl.sql、08_openapi_v1.yaml | NestJS 工程骨架、鉴权中间件、错误码、日志、配置 | 服务可启动，健康检查通过 |
| S1 | Database Migration | S0 | 是 | 10_postgresql_core_ddl.sql | SQL migration、seed template、回滚说明 | staging 可执行迁移 |
| S2 | Auth/Account Domain | S1 | 是 | 03/06/07/08/09/10 | 注册、登录、refresh、logout、me、session 接口 | 单活跃 session 测试通过 |
| S2 | Plan/Subscription/VPN Domain | S1 | 是 | 03/05/06/07/08/09/10 | 套餐、订阅、区域列表、配置签发接口 | 订阅 ACTIVE 时可签发配置 |
| S2 | Order/Payment Domain | S1 | 否 | 03/06/07/08/09/10 | 订单、payment target、scanner、confirm worker | 订单可从 AWAITING 到 PAID |
| S3 | Provision / VPN Identity | Order/Payment + Plan/Subscription/VPN | 否 | 03/06/09/10 | 开通 worker、vpn identity、order completed | 支付成功后自动激活订阅 |
| S3 | Wallet Metadata / Fallback Broadcast | S1 | 是 | 03/05/07/08/09 | chain configs、asset catalog、public addresses、proxy broadcast 接口 | 发送页所需元数据齐备 |
| S4 | Referral / Commission / Withdrawal | Order/Payment + Auth | 否 | 03/06/07/08/09/10 | 邀请码绑定、账本、余额、提现、审核与打款记录 | 提现链路可闭环 |
| S4 | Admin Web | S1 + 各域接口 | 是 | 05/07/08 | 后台页面、RBAC 路由、操作表单 | 核心管理页面可演示 |
| S4 | Android UI | S0 | 是 | 05_ia_and_page_spec.md | 页面骨架、导航、UIState | 静态流程可演示 |
| S5 | Android Integration | Android UI + 各域接口 | 否 | 05/07/08/11 | 登录、收银台、发送页、邀请提现联调 | 主流程端到端跑通 |
| S5 | QA / Contract / Regression | 各域接口 + UI | 是 | 11_test_cases.md | 接口自动化、集成回归、发布报告 | P0 全通过 |
| S6 | Deployment / CI/CD | S1 后即可提前推进，最终依赖各域健康检查 | 是 | 14_deployment_and_cicd.md | 构建、迁移、部署、回滚、冒烟脚本模板 | staging/prod 发布流程可演练 |

## 2. 并行开发原则
1. S0 必须先冻结契约。
2. S1 的 Backend Foundation、Database Migration 可并行。
3. Android UI 可在 S0 完成后用 Mock 并行推进。
4. Admin Web 依赖接口契约，但页面骨架可提前并行。
5. 佣金提现必须依赖订单完成状态，不可先于 Order/Payment 域独立联调。
6. Deployment / CI/CD 可从 S1 开始同步准备，不必等全部功能完成。

## 3. 输入输出约束
- 输入优先级：DDL = OpenAPI = 状态机 > 页面规格 > 任务文档
- 输出要求：每个任务包都必须附带接口自测、状态机覆盖说明、错误码覆盖说明
- 任何任务包若试图新增字段/状态/错误码，必须先回到契约冻结层审查

## 4. 完成定义（DoD）
- 代码可编译/可启动
- OpenAPI Contract Test 通过
- 与 DDL 对应的 migration 可执行
- 关键状态机单元测试通过
- 关键页面联调通过
- 审计日志与错误码可回查
