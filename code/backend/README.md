# CryptoVPN Backend

NestJS 模块化单体基础工程，作为 `final_engineering_delivery_package` 的正式后端落地方向。

## 当前范围

- 应用入口与基础工程
- `/api/healthz` 健康检查
- `.env` 配置读取
- `requestId` 中间件
- 统一成功响应 envelope
- 统一异常响应 envelope
- 结构化日志基础设施
- 业务模块目录骨架

当前还没有进入业务域实现。`auth / accounts / plans / orders / vpn / wallet / referral / withdrawals / admin / database` 目录仅提供边界占位。

## 启动

```bash
pnpm install
pnpm run start:dev
```

默认端口:

- `3000`

健康检查:

```bash
curl http://localhost:3000/api/healthz
```

Swagger:

- [http://localhost:3000/api/docs](http://localhost:3000/api/docs)

## 校验命令

```bash
pnpm run typecheck
pnpm run build
pnpm run test:e2e
```

## 环境变量

参考 [`.env.example`](/Users/cnyirui/git/projects/liaojiang/code/backend/.env.example)。

## 下一步

- 接入数据库连接与迁移执行器
- 按 `final_engineering_delivery_package` 落地 Auth / Orders / VPN 等业务域
- 建立 Contract Test 与迁移/seed 执行脚本
