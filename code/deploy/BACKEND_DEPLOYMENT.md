# Backend Deployment

这套文件用于当前 `code/backend` NestJS 服务的单机 / 多机 Compose 基线部署。

## 文件

- `docker-compose.backend.yml`
- `.env.backend.example`

## 启动

```bash
cd code/deploy
cp .env.backend.example .env.backend
docker compose --env-file .env.backend -f docker-compose.backend.yml up -d --build
```

## 健康检查

```bash
curl http://localhost:3000/api/healthz
```

## 当前限制

- 当前 compose 只覆盖 backend + postgres + redis
- worker / admin / android 分发 / nginx 还未接入这套新栈
- 数据库 baseline / seed 执行仍需在真实环境阶段接 PostgreSQL 客户端完成

## 当前真实环境临时方案

在 `154.37.208.72` 上，当前已验证的最小方案为：

1. 代码同步到 `/opt/cryptovpn/backend`
2. 使用宿主机 `node + pnpm` 执行：
   - `pnpm install --frozen-lockfile`
   - `pnpm build`
   - `pnpm start:prod`
3. 复用现有 PostgreSQL 容器 `vpn-panel-db`
4. 单独创建测试库 `cryptovpn_test`
5. 执行：
   - `migrations/baseline/0001_init.up.sql`
   - `migrations/seeds/0001_bootstrap_seed.filled.sql`

采用该临时方案的原因：

- 远端容器内访问 npm registry 存在 `EAI_AGAIN`
- `docker-compose.backend.yml` 中 backend 镜像暂未在该机器稳定构建
- 但宿主机 Node 方案已能支撑当前真实环境联调
