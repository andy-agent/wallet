# 开发日志

## 2026-03-31

### 今日工作

1. **整理技术文档**
   - 创建了完整的 [技术规格文档](TECHNICAL_SPECIFICATION.md)
   - 记录了系统架构、数据库模型、API 契约
   - 整理了已知问题和待修复项清单

2. **问题识别**
   - 发现服务端订单创建需要登录，但客户端未实现登录功能
   - 发现 CreateOrderRequest 字段不匹配
   - 发现 LoginActivity 只是一个占位符

3. **关键决策点**
   - 需要决定: 支持匿名购买 vs 强制先登录
   - 匿名购买: 首次购买自动创建账号
   - 强制登录: 必须先注册/登录才能购买

### 技术债务

- 服务端 Pydantic 模型需要修复 null 值验证
- 客户端需要完整的用户认证 UI
- 需要初始化区块链收款地址

### 下一步

参考 [当前状态](current-status.md) 中的优先级进行修复。

## 2026-04-07

### 今日工作

1. **完成 backend TRON 远程接线收尾**
   - 新增 `code/backend/src/modules/tron-client/`，补齐 `TRON_SERVICE_*` 配置
   - `wallet` TRON 主路径已支持远程地址校验与远程 proxy broadcast
   - 远程服务不可用时保留明确 fallback，不破坏现有 SOLANA 路径

2. **完成 backend 链侧聚合健康验收**
   - `/api/healthz` 现聚合 Solana + TRON 两个链侧健康摘要
   - 覆盖 disabled / healthy / degraded 三种可解释输出
   - `app.e2e-spec.ts` 与 `wallet.e2e-spec.ts` 已补齐对应验收覆盖

3. **完成 `liaojiang-rcb.17` 里程碑验收**
   - `liaojiang-rcb.17.2` / `liaojiang-rcb.17.3` / `liaojiang-rcb.17.4` 已在主线验收
   - 验证命令：
     - `pnpm --dir code/backend typecheck`
     - `pnpm --dir code/backend build`
     - `pnpm --dir code/backend test:e2e`
   - 均通过

### 当前结论

- `liaojiang-rcb.17` 的 backend follow-up 已完成，本轮没有新的 `bd ready` 工程任务
- 当前剩余主阻塞回到 Android `liaojiang-4j0.2`，仍需要真实回归账号或可取验证码邮箱
