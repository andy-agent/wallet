# 14_deployment_and_cicd

## 1. 环境划分
- `local`：开发者本地，允许 Mock、直连本地数据库
- `dev`：共享开发环境，用于前后端基础联调
- `staging`：预发布环境，用于完整链路演练与回归
- `prod`：生产环境

## 2. 部署基线
### 2.1 最终采用方案
- 首发采用 **单 VM / 多 VM + Docker Compose** 基线
- API、Worker、Admin、Website、PostgreSQL、Redis、VPN Control Service 分服务部署
- 不要求首发使用 K8s

### 2.2 未采用方案说明
- 未采用 K8s：MVP 阶段运维复杂度与资源成本过高
- 未采用纯 systemd 手工发布：缺少统一编排、迁移和回滚门禁

## 3. 服务清单
1. `api`：NestJS HTTP API
2. `worker`：BullMQ workers（payment scan, confirm, provision, commission, withdrawal confirm, notify）
3. `admin-web`：Admin 前端
4. `public-site`：官网/下载站
5. `postgres`：主数据库
6. `redis`：缓存与队列
7. `vpn-control`：VPN 配置签发/节点控制辅助服务（与 Xray/v2rayNG 相关）
8. `monitoring`：日志、指标与告警（实现可按团队现状选择）

## 4. 环境变量清单
| 变量 | 说明 | 示例 |
|---|---|---|
| APP_ENV | 运行环境标识 | dev / staging / prod |
| APP_PORT | API 监听端口 | <PORT> |
| DATABASE_URL | PostgreSQL 连接串 | postgresql://<USER>:<PASS>@<HOST>:<PORT>/<DB> |
| REDIS_URL | Redis 连接串 | redis://<HOST>:<PORT>/<DB> |
| JWT_ACCESS_SECRET | Client access token 签名秘钥 | <SECRET> |
| JWT_REFRESH_SECRET | Client refresh token 签名/加密秘钥 | <SECRET> |
| ADMIN_JWT_ACCESS_SECRET | Admin access token 秘钥 | <SECRET> |
| ADMIN_JWT_REFRESH_SECRET | Admin refresh token 秘钥 | <SECRET> |
| SMTP_HOST | 邮件服务地址 | <SMTP_HOST> |
| SMTP_USER | 邮件账号 | <SMTP_USER> |
| SMTP_PASS | 邮件密码 | <SMTP_PASS> |
| SOLANA_PUBLIC_RPC_URL | Solana 公共/直连 RPC | <SOLANA_RPC> |
| TRON_PUBLIC_RPC_URL | TRON 公共/直连 RPC | <TRON_RPC> |
| SOLANA_PROXY_RPC_URL | Solana 代理广播/扫描 RPC | <SOLANA_PROXY_RPC> |
| TRON_PROXY_RPC_URL | TRON 代理广播/扫描 RPC | <TRON_PROXY_RPC> |
| ADMIN_BASE_URL | 后台域名 | https://<ADMIN_HOST> |
| PUBLIC_SITE_URL | 官网主域名 | https://<SITE_HOST> |
| DOWNLOAD_BASE_URL | APK 下载基础地址 | https://<DOWNLOAD_HOST> |

## 5. CI 阶段（建议模板）
1. `lint`
   - Backend：eslint + prettier
   - Admin：eslint + prettier
   - Android：ktlint / detekt
2. `unit-test`
   - Backend 单元测试
   - Android ViewModel / UIState 测试
   - Admin 组件测试
3. `contract-test`
   - `08_openapi_v1.yaml` 语法校验
   - DTO / 实现返回体与契约对比
4. `build`
   - Backend 镜像
   - Worker 镜像
   - Admin 构建产物
   - Android APK
5. `security-scan`
   - 依赖漏洞扫描
   - 容器镜像扫描
6. `package`
   - 产出镜像 tag、APK、校验值、release notes

## 6. CD 阶段（建议模板）
1. 选择环境（dev/staging/prod）
2. 拉取指定构建产物
3. 先执行数据库迁移
4. 再滚动发布 API / Worker
5. 发布 Admin Web
6. 更新 Public Site 的 APK 下载元数据
7. 执行健康检查与冒烟测试
8. 记录发布结果与版本号

## 7. 数据库迁移策略
### 7.1 基线
- DDL 以 `10_postgresql_core_ddl.sql` 为起点
- 迁移采用 **expand / backfill / contract** 思路
- 首发版本只允许增量迁移，不允许上线同批次做破坏性 drop
- 首启 schema 就绪后，必须执行 `10_postgresql_bootstrap_seed.sql` 注入最小运营数据（链配置、资产目录、管理员、套餐、区域、节点、佣金规则、系统配置、法务文档、版本占位）

### 7.2 发布时序
1. 先备份数据库
2. 执行 schema migration
3. 首版或新环境执行 `10_postgresql_bootstrap_seed.sql`
4. 验证 schema version
5. 发布 API / Worker
6. 运行冒烟测试
7. 再切换流量

### 7.3 回滚
- 代码回滚：回退到上一稳定镜像
- 数据回滚：仅回滚非破坏性迁移；破坏性变更必须通过补偿脚本，不依赖自动 downgrade
- 资金状态相关数据禁止直接物理回退，必须用补偿记录

## 8. 发布门禁
- OpenAPI 语法通过
- 数据库 migration 在 staging 成功
- P0 测试用例通过
- 登录、下单、支付、开通、提现、强更冒烟通过
- 安全检查通过：私钥不落库、审计日志写入正常

## 9. 冒烟测试流程
1. 新用户注册并登录
2. 创建订单并查看 payment target
3. 模拟支付扫描推进到已完成
4. 订阅 ACTIVE，可签发 VPN 配置
5. 邀请绑定、佣金释放、提现提交流程可执行
6. Admin 能审核提现并查看审计日志
7. 强更策略生效

## 10. 监控与日志要求
- API / Worker / Admin / Site 都输出 requestId
- 订单、提现、配置变更、审计类日志必须结构化
- 会写入 `audit_logs` 的敏感操作必须同时写入 `audit_logs.request_id`
- 后台检索 requestId 时，应可通过日志系统或审计表回查到对应动作
- 监控指标最少包括：
  - 登录成功率
  - 订单创建成功率
  - 支付确认延迟
  - 订单开通失败数
  - 提现待审数量
  - 强更命中率
- 告警最少包括：
  - 支付扫描失败
  - 提现确认失败
  - 订单 failed 增长
  - 节点健康异常
  - 版本接口异常

## 11. 示例 pipeline 结构（伪代码模板）
```yaml
stages:
  - lint
  - test
  - contract
  - build
  - package
  - deploy
  - smoke

variables:
  APP_ENV: "<ENV>"
  IMAGE_TAG: "<TAG>"
  DATABASE_URL: "<DATABASE_URL>"

lint:
  script:
    - run-backend-lint
    - run-admin-lint
    - run-android-lint

contract:
  script:
    - validate-openapi 08_openapi_v1.yaml
    - validate-ddl 10_postgresql_core_ddl.sql

deploy_staging:
  script:
    - backup-db
    - apply-migrations
    - apply-bootstrap-seed-if-first-deploy
    - deploy-api
    - deploy-worker
    - deploy-admin
    - deploy-site
    - run-smoke-tests
```

## 12. 安全检查点
- 不在 CI/CD 日志中打印任何密钥
- APK、镜像、数据库备份都要有权限隔离
- 管理员与生产数据库访问应受网络和身份限制
- 提现出款密钥不纳入应用容器镜像

## 13. requestId 回查约束
- requestId 由入口层统一生成并透传
- 结构化日志必须以 requestId 为主检索键
- 对有审计记录的操作，requestId 必须持久化到 `audit_logs.request_id`
- 后台若提供 requestId 检索，优先从 `audit_logs` 命中，再结合日志系统补全上下文
