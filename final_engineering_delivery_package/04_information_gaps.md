# 04_information_gaps

| 优先级 | 缺口 | 影响 | 默认处理 | 需要确认角色 | 是否阻塞 |
|---|---|---|---|---|---|
| P0 | Solana 订单确认阈值 | 订单状态推进、测试、SLA | system_configs 预留，初始化默认 1（默认假设）；首启占位见 `10_postgresql_bootstrap_seed.sql` | 支付负责人 / Tech Lead | 否，阻塞上线前配置冻结 |
| P0 | TRON/TRC20 订单确认阈值 | 订单状态推进、支付扫描 | system_configs 预留，初始化默认 20（默认假设）；首启占位见 `10_postgresql_bootstrap_seed.sql` | 支付负责人 / Tech Lead | 否，阻塞上线前配置冻结 |
| P0 | Solana / TRON RPC 提供商与 SLA | 钱包直连广播、支付扫描、提现确认 | 接口与配置按 provider-agnostic 设计，生产域名/Key 待确认；首启占位见 `10_postgresql_bootstrap_seed.sql` | Tech Lead / 运维 | 部分阻塞联调环境准备 |
| P0 | 钱包 SDK 最终实现方案 | Android 钱包、签名、TRON 代币发送 | 先以链适配器架构设计，SDK 选型通过 POC 确认 | Android Lead | 不阻塞契约，但阻塞钱包编码 |
| P0 | 提现出款自动化等级 | 后台流程、运维、安全审计 | MVP 采用人工审核 + 人工/半自动广播 | 财务负责人 / 安全负责人 | 不阻塞 MVP 设计 |
| P1 | 高级区域清单 | 套餐页、区域权限、节点运营 | 先配置为日本专线/美国低延迟示例，占位不写死；首启占位见 `10_postgresql_bootstrap_seed.sql` | 产品 / 运营 | 否 |
| P1 | 套餐定价与命名 | plans seed、前端展示、测试数据 | 文档只定义字段与规则，不写死价格；首启占位见 `10_postgresql_bootstrap_seed.sql` | 产品 / 运营 | 阻塞生产 seed，不阻塞开发骨架 |
| P1 | 邮件模板与寄件域名 | 注册、重置密码、到期提醒 | 接口与模板 key 预留，内容待运营确认 | 产品 / 运营 | 不阻塞接口开发 |
| P1 | 订单锁价时长 | orders.expires_at、支付页倒计时 | 默认 900 秒（默认假设） | 产品 / 财务 | 否 |
| P1 | 佣金冷静期天数 | commission_rules、释放任务、提现时点 | 默认 7 天（默认假设） | 产品 / 财务 | 否 |
| P2 | 管理员二次认证是否启用 | 提现审核安全 | MVP 先用强密码 + 审计 + IP 白名单（建议方案） | 安全负责人 | 否 |
| P2 | 官网是否需要多语言 | 法务与下载页 | 首发仅中文 | 产品 | 否 |
