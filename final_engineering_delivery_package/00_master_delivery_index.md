# CryptoVPN 最终开发启动包总索引

## 1. 项目目标
本交付包用于把 3 套历史工程化交付包收敛为 **一套唯一口径、可直接驱动多 Agent 自动化开发** 的启动基线。  
目标不是展示材料，而是让后端、Android、Admin、测试、运维、CI/CD 能在统一契约上并行推进。

## 2. 最终采用范围
- Android：账号、VPN、钱包、订单支付、邀请、提现、版本门禁
- Backend：认证、会话、套餐、订单、支付扫描、订阅、VPN 配置签发、分佣、提现、版本、法务、审计
- Admin：用户、订单、套餐、区域、节点、账本、提现、版本、法务、系统配置、审计
- QA：主流程、异常流程、权限、状态机、回归、发布验收
- DevOps：环境、迁移、CI/CD、回滚、监控

## 3. 已冻结业务规则
1. 钱包定位：**完整多链钱包**，但 MVP 链/资产范围收敛到 Solana + TRON。
2. 注册方式：**邮箱优先**，手机号不进 MVP。
3. 设备策略：**单活跃 session**，不做显式设备绑定。
4. VPN 协议：**VLESS + Reality + XTLS/Vision**。
5. 支付资产：**SOL on Solana、USDT on Solana、USDT on TRON/TRC20**。
6. 钱包签名：**客户端本地签名**。
7. 广播边界：**客户端直连广播为默认，后端代理广播为兜底**。
8. 严禁：**后端代签名、后端托管用户私钥**。
9. 分佣：**一级 25%，二级 5%**。
10. 提现：**最低 10 USDT，默认 USDT on Solana**。
11. 分佣策略：**链上支付、平台记账、提现上链**。
12. 官网分发：**单主域名**。

## 4. 已冻结技术基线
- Android：Kotlin + Jetpack Compose + MVVM + Hilt + v2rayNG 二开
- Backend：NestJS 模块化单体 + PostgreSQL + Redis + BullMQ
- Admin：Next.js/React + TypeScript + Ant Design
- 数据契约：`08_openapi_v1.yaml`
- 数据库契约：`10_postgresql_core_ddl.sql`

## 5. 最终事实源说明
- **业务与工程事实源**：`03_final_engineering_prd.md`
- **冲突裁决事实源**：`02_conflict_resolution_matrix.md`
- **API 事实源**：`08_openapi_v1.yaml`
- **数据库事实源**：`10_postgresql_core_ddl.sql`
- **页面事实源**：`05_ia_and_page_spec.md`
- **状态机事实源**：`06_state_machine_and_business_rules.md`
- **未冻结项事实源**：`04_information_gaps.md` + `15_assumptions_and_decisions.md`

## 6. 待读阅读顺序
1. `00_master_delivery_index.md`
2. `02_conflict_resolution_matrix.md`
3. `03_final_engineering_prd.md`
4. `06_state_machine_and_business_rules.md`
5. `09_data_model.md`
6. `10_postgresql_core_ddl.sql`
7. `07_api_spec.md`
8. `08_openapi_v1.yaml`
9. `05_ia_and_page_spec.md`
10. `11_test_cases.md`
11. `12_dev_task_breakdown.md`
12. `13_agent_execution_plan.md`
13. `14_deployment_and_cicd.md`

## 7. Agent 消费顺序
1. **架构/PM Agent**：先读本文件、冲突裁决、最终 PRD。
2. **Data Agent**：再读数据模型与 DDL。
3. **API Contract Agent**：再读 API 规范与 OpenAPI。
4. **Backend Domain Agents**：按 auth → order/payment → subscription/vpn → commission/withdraw 顺序开发。
5. **Android Agents**：页面规格 → OpenAPI → 联调任务。
6. **Admin Agents**：页面规格 → OpenAPI → 权限矩阵。
7. **QA Agent**：测试用例 + 状态机 + API 契约。
8. **DevOps Agent**：部署与 CI/CD。
9. **Consistency Guard Agent**：最后用自检报告回扫全包。

## 8. 文件导航
| 文件 | 职责 | 是否事实源 |
|---|---|---|
| 00_master_delivery_index.md | 唯一主入口；说明最终口径、阅读顺序、Agent 消费顺序 | 是 |
| 01_diff_analysis.md | 三套工程包差异、优劣、收敛策略 | 否，供决策追溯 |
| 02_conflict_resolution_matrix.md | 所有冲突的唯一裁决与下游影响 | 是（冲突裁决事实源） |
| 03_final_engineering_prd.md | 最终业务与工程 PRD | 是 |
| 04_information_gaps.md | 仍需确认的信息缺口与默认处理 | 是（未冻结项事实源） |
| 05_ia_and_page_spec.md | Android + Admin 页面定义、状态、跳转、API/数据映射 | 是 |
| 06_state_machine_and_business_rules.md | 状态机、业务规则、Mermaid 时序图 | 是 |
| 07_api_spec.md | 接口规范、鉴权、错误码、幂等、分页与排序规则 | 是 |
| 08_openapi_v1.yaml | 机器可读 API 契约 | 是（API 最终事实源） |
| 09_data_model.md | 实体、字段、索引、约束、映射关系 | 是 |
| 10_postgresql_core_ddl.sql | PostgreSQL 开发起始 DDL | 是（数据库最终事实源） |
| 11_test_cases.md | 功能/异常/权限/集成/回归测试用例 | 是 |
| 12_dev_task_breakdown.md | 研发阶段、依赖、并行策略、完成定义 | 是 |
| 13_agent_execution_plan.md | 多 Agent 协作计划、输入输出、失败回传 | 是 |
| 14_deployment_and_cicd.md | 部署环境、CI/CD、迁移、回滚、门禁 | 是 |
| 15_assumptions_and_decisions.md | 默认假设和最终决策记录 | 是 |
| 16_consistency_check_report.md | PRD/API/DDL/页面/测试/部署一致性自检 | 是（自检事实源） |

## 9. 哪些内容仍待确认
仅以下内容仍未冻结，但已给默认处理方式：
- Solana / TRON 确认阈值
- 钱包 SDK 最终落地方案
- RPC 提供商与广播 SLA
- 提现自动化等级
- 套餐价格与高级区域清单

以上内容统一收口于 `04_information_gaps.md` 与 `15_assumptions_and_decisions.md`。
