# README

## 交付包用途
本包是 CryptoVPN 项目的 **最终开发启动包**。  
它用于统一 3 套历史工程包的差异，并为多 Agent 自动化开发提供唯一事实源。

## 面向对象
- PM / Tech Lead / 架构师
- Backend / Android / Admin 开发
- QA / 运维 / DevOps
- Kimi Code 或其他 AI 编码代理

## 使用方式
1. 先阅读 `00_master_delivery_index.md`。
2. 再阅读 `02_conflict_resolution_matrix.md`，理解所有历史冲突已经如何裁决。
3. 然后按顺序读取 `03_final_engineering_prd.md`、`06_state_machine_and_business_rules.md`、`09_data_model.md`、`08_openapi_v1.yaml`、`10_postgresql_core_ddl.sql`。
4. 各研发角色再分别读取页面、测试、任务、部署文档。

## 多 Agent 推荐执行顺序
1. Contract Freeze Agent：冻结 PRD / 状态机 / DDL / OpenAPI
2. Backend Foundation Agent：工程骨架、鉴权、中间件、数据库迁移
3. Domain Agents：Auth、Orders/Payments、Subscription/VPN、Referral/Withdraw
4. Android UI Agents：页面和 UIState
5. Android Integration Agent：钱包/VPN/支付联调
6. Admin Agent：后台页面与权限路由
7. QA Agent：Contract Test、状态流与冒烟
8. DevOps Agent：环境、迁移、部署、回滚
9. Consistency Guard Agent：最终回扫

## 关键约束
- 后端不得托管私钥或代签名。
- 钱包默认客户端直连广播；仅保留后端代理广播兜底。
- 所有状态机以 `06_state_machine_and_business_rules.md` 为准。
- 所有接口以 `08_openapi_v1.yaml` 为准。
- 所有建表与索引以 `10_postgresql_core_ddl.sql` 为准。
- 若发现页面字段/API 字段/DDL 字段不一致，优先以 `16_consistency_check_report.md` 指出的修正结果为准。
