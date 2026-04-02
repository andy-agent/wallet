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
