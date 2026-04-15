# Backend Deployment

这套文件用于当前 `code/backend` NestJS 服务的单机 / 多机 Compose 基线部署。

## 文件

- `docker-compose.backend.yml`
- `.env.backend.example`
- `backend-ci.github-actions.yml`

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
- GitHub 当前推送凭证未开放 `workflow` scope，因此 CI 配置先以模板文件 `backend-ci.github-actions.yml` 形式保留，待具备权限后再落到 `.github/workflows/`

## 当前真实环境基线

在 `154.37.208.72` 上，当前已验证的最小方案为：

1. 代码同步到 `/opt/cryptovpn/backend`
2. 使用宿主机 `node + pnpm` 执行：
   - `pnpm install --frozen-lockfile`
   - `pnpm build`
   - `pnpm start:prod`
3. 生产运行时统一使用：
   - `/opt/cryptovpn/backend/.env.local`
   - `systemd` 通过 `EnvironmentFile=/opt/cryptovpn/backend/.env.local` 注入环境
4. 运行时状态统一落到唯一真实数据库：
   - `postgresql://cryptovpn@127.0.0.1:15432/cryptovpn`
5. 仅在该真实库上执行初始化/seed：
   - `migrations/baseline/0001_init.up.sql`
   - `migrations/seeds/0001_bootstrap_seed.filled.sql`

采用该方案的原因：

- 远端容器内访问 npm registry 存在 `EAI_AGAIN`
- `docker-compose.backend.yml` 中 backend 镜像暂未在该机器稳定构建
- 但宿主机 Node 方案已能支撑当前真实环境联调
- 已移除旧的 `cryptovpn_test` 运行时路径，避免 live API 读到测试库
