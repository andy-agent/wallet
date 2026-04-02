# 15_assumptions_and_decisions

## 1. 默认假设
| 假设内容 | 适用范围 | 原因 | 风险等级 | 影响 |
|---|---|---|---|---|
| 客户端 Access Token TTL = 2 小时，Refresh Token TTL = 30 天 | Client 鉴权 | 实现单活跃 session 且降低频繁登录 | 中 | API/Android/测试 |
| 管理员 Access Token TTL = 2 小时，Refresh Token TTL = 12 小时 | Admin 鉴权 | 后台风险高，缩短会话时长 | 中 | API/Admin/测试 |
| 订单锁价时长默认 900 秒 | 订单与支付页 | 兼顾链上支付操作时间与资金风险 | 低 | DDL/API/页面/测试 |
| 佣金冷静期默认 7 天 | 分佣与提现 | 给异常订单和风控留缓冲 | 中 | DDL/API/测试 |
| 提现手续费默认 0，由平台承担 | 提现 | 原始材料未冻结具体手续费策略 | 中 | DDL/API/财务 |
| VPN 配置签发的有效期默认不超过 15 分钟 | VPN 配置 | 降低离线长期复用风险 | 中 | API/VPN/测试 |
| Android 采用 Kotlin + Compose + MVVM + Hilt + v2rayNG 二开 | Android 工程 | 历史材料与 AI 代码生成都更适配 | 低 | Android/联调 |
| Backend 采用 NestJS 模块化单体 + PostgreSQL + Redis + BullMQ | 后端工程 | 本轮唯一技术基线决策 | 中 | 后端/部署/CICD |
| 钱包广播优先 direct RPC；fallback 仅在 direct 失败且 proxy enabled 时可用 | 钱包/支付 | 遵守私钥边界同时保留兜底 | 中 | API/Android/安全/测试 |

## 2. 最终决策
| 决策 | 最终口径 | 来源 | 风险 | 是否待确认 |
|---|---|---|---|---|
| 产品定位 | VPN 为核心收入，多链钱包为关键辅助模块；钱包不是纯支付壳 | v1.1 + 用户最新冻结规则 | 低 | 否 |
| 邮箱优先注册 | MVP 只做邮箱注册/登录/重置密码 | v1.1 + 用户最新冻结规则 | 低 | 否 |
| 单活跃 session | 不做显式设备绑定，单账号仅一个 active refresh session | v1.1 + 用户最新冻结规则 | 低 | 否 |
| 支付资产 | SOL on Solana、USDT on Solana、USDT on TRON/TRC20 | v1.1 + 包 B/C 共识 | 低 | 否 |
| 佣金规则 | 一级25%，二级5%，按 USDT 账本记账 | v1.1 + 包 B/C 共识 | 低 | 否 |
| 提现规则 | 最低 10 USDT，默认 USDT on Solana | v1.1 + 包 B/C 共识 | 低 | 否 |
| 钱包安全边界 | 客户端生成/导入助记词，本地签名；服务端不保存私钥、不代签名 | 用户最新冻结规则 | 低 | 否 |
| 广播边界 | 客户端 direct broadcast，后端 proxy broadcast 仅为兜底能力 | 用户最新冻结规则 | 中 | 否 |
| 后端技术栈 | NestJS | 本轮冲突裁决 | 中 | 否 |
| 部署基线 | 单 VM / 多 VM + Docker Compose，K8s 不作为首发硬要求 | 本轮冲突裁决 | 中 | 否 |

## 3. 采用理由汇总
### 3.1 为什么最终采用 NestJS
- 与用户本轮“Go / NestJS 优先”的技术基线一致
- 更适合模块化单体、OpenAPI 契约驱动、DTO/校验与后台 CRUD 协作
- 更适合多 Agent 并行产出 controller/service/dto/test

### 3.2 为什么未采用 FastAPI 作为最终定稿
- FastAPI 虽然在历史包中工程资产最丰富，但属于历史参考栈
- 若继续保留 FastAPI 与 NestJS 并行，会破坏最终开发启动包的唯一性
- 因此本包保留 FastAPI 仅作为“历史来源”，不再作为最终事实源

### 3.3 为什么未采用 Go 作为首发唯一栈
- Go 非常适合高并发服务，但本项目首发更看重：契约一致性、后台 CRUD 速度、DTO/校验与快速联调
- 对多 Agent 协作而言，NestJS 的模块边界与 OpenAPI 贴合度更高
- 若未来订单扫描、节点控制或高吞吐任务独立演进，可将 worker 或链模块再演进为 Go 服务

## 4. 仍需产品/技术确认但不阻塞当前收敛的事项
- 确认数、RPC 提供商、钱包 SDK、套餐定价、提现自动化等级
