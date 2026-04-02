# 13_agent_execution_plan

## 1. Agent 目标
本文件定义多 Agent 自动化开发的输入、输出、顺序、并行关系和失败回传规则。  
目标是让多个 Agent 在 **不互相覆盖、不重复发明模型、不产生契约漂移** 的前提下协作。

## 2. Agent 矩阵
| Agent | 输入 | 输出 | 先后顺序 | 并行关系 | 失败回传 | DoD |
|---|---|---|---|---|---|---|
| A0 Architecture/PM Agent | 00_master_delivery_index.md, 02_conflict_resolution_matrix.md, 03_final_engineering_prd.md | 冻结业务边界、冻结模块边界、冻结术语 | 最先 | 独占 | 若发现边界冲突，退回契约冻结层 | 不再新增并行方案 |
| A1 Data Model Agent | 09_data_model.md, 10_postgresql_core_ddl.sql | migration 脚本、种子数据模板、ER 图 | A0 后 | 可与 A2 并行 | 字段/索引无法落地时回传 A0 | DDL 可迁移、关键索引完整 |
| A2 API Contract Agent | 07_api_spec.md, 08_openapi_v1.yaml | DTO、校验器、契约测试、错误码定义 | A0 后 | 可与 A1 并行 | 字段与状态不一致时回传 A0 | Contract Test 可通过 |
| A3 Backend Auth Agent | 03/06/07/08/10 | 注册、登录、refresh、logout、me、session API | A1+A2 后 | 可与 A4/A5 并行 | 若 session 模型冲突回传 A1/A0 | 单活跃 session 验证通过 |
| A4 Backend Order/Payment Agent | 03/06/07/08/10 | 订单、payment target、scanner、confirm worker | A1+A2 后 | 可与 A3/A5 并行 | 支付策略冲突回传 A0 | 订单状态机跑通 |
| A5 Backend Subscription/VPN Agent | 03/06/07/08/10 | 套餐、订阅、区域、配置签发、VPN identity | A1+A2 后 | 可与 A3/A4 并行 | 区域/节点模型不一致回传 A1 | active 订阅可签发配置 |
| A6 Backend Wallet Metadata Agent | 03/05/07/08/09/10 | 链配置、资产目录、公开地址、proxy broadcast 接口 | A1+A2 后 | 可与 A3/A4/A5 并行 | 钱包边界冲突回传 A0 | 发送页元数据齐备 |
| A7 Backend Referral/Withdraw Agent | 03/06/07/08/09/10 | 邀请码绑定、账本、余额、提现、审核打款流程 | A4 后 | 与 A8 部分并行 | 账本与余额不一致回传 A1/A0 | 提现闭环通过 |
| A8 Android UI Agent | 05_ia_and_page_spec.md | Compose 页面、导航、UIState | A0 后 | 可提前与后端并行 | 页面字段缺口回传 A2/A0 | 静态流程可演示 |
| A9 Android Integration Agent | 05/07/08/11 | 登录、收银台、VPN、钱包、邀请提现联调 | A3/A4/A5/A6/A8 后 | 部分并行 | 契约差异回传 Consistency Guard | 主链路 E2E 通过 |
| A10 Admin Frontend Agent | 05/07/08 | Admin 页面、权限路由、表格筛选、审核表单 | A2 后 | 与后端并行 | 权限矩阵冲突回传 A0 | 核心后台可演示 |
| A11 QA Agent | 11_test_cases.md, 08_openapi_v1.yaml, 06_state_machine_and_business_rules.md | 接口自动化、状态机测试、回归集 | A2 后持续介入 | 全程并行 | 失败归类到对应域 Agent | P0 用例通过 |
| A12 DevOps Agent | 14_deployment_and_cicd.md | Dockerfile/compose 模板、CI 阶段、迁移与回滚脚本模板 | A1 后 | 可与后端/UI 并行 | 部署依赖不明确回传 A0 | staging 可发布 |
| A13 Consistency Guard Agent | 全包 | 字段/状态/接口/DDL/页面一致性报告 | 每阶段末 | 监督型 | 发现断裂即回传对应 Agent 与 A0 | 16_consistency_check_report.md 为通过 |

## 3. 执行顺序
1. A0 冻结契约与边界
2. A1/A2 建立数据与 API 事实源
3. A3/A4/A5/A6 建设核心后端域
4. A8/A10 并行建设 Android UI / Admin UI
5. A7 在订单/支付闭环稳定后接入分佣提现
6. A9 进行 Android 联调
7. A11 全程介入自动化测试
8. A12 建设部署与流水线
9. A13 在每个里程碑做一致性回扫

## 4. 并行关系
- `A1 Data Model Agent` 与 `A2 API Contract Agent` 可并行，但不得互相修改事实源文件，只能通过 A0 提交变更申请。
- `A3/A4/A5/A6` 四个后端域 Agent 可并行，前提是只消费已冻结的 DDL 和 OpenAPI。
- `A8 Android UI Agent` 可在后端未完成前使用 Mock 数据推进。
- `A10 Admin Frontend Agent` 同理。
- `A7 Referral/Withdraw Agent` 不得先于 `A4 Order/Payment Agent` 稳定完成。

## 5. 失败回传规则
1. **字段不一致**：回传 A2 + A13。
2. **状态机无法落地**：回传 A0 + A13。
3. **DDL 无法支撑接口**：回传 A1 + A0。
4. **页面字段缺失**：回传 A8/A10 + A2。
5. **部署依赖未定义**：回传 A12 + A0。
6. **安全边界冲突（如代签名）**：立即回传 A0 并阻断后续任务。

## 6. 一致性校验规则
- 任何 Agent 不允许新增未在 DDL/OpenAPI/状态机中定义的持久化字段。
- 任何 Agent 不允许引入新的主状态枚举而不经过 A0 审核。
- API 响应字段必须由 `08_openapi_v1.yaml` 生成或验证。
- Android/Admin 页面依赖接口必须能在 `07_api_spec.md` 中查到。
- 测试 Agent 发现的契约漂移必须先修契约，再修实现。

## 7. 最少人工介入点
- 初次冻结高优先级待确认项
- 钱包 SDK 最终选型
- RPC 提供商与安全配置
- 提现出款自动化等级
- 预生产上线放行
