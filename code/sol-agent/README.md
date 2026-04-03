# Sol-Agent

Solana 链侧最小服务骨架，位于 `code/sol-agent`。

## 能力边界

- **Health**: `GET /api/healthz`
- **地址生成/占位**: `POST /api/internal/v1/address`、`GET /api/internal/v1/address/:accountId`
- **支付检测/占位**: `GET /api/internal/v1/payment/:address/status`、`POST /api/internal/v1/payment/detect`

## 内部鉴权约定

所有 `/internal/*` 接口需在 Header 中携带：

```
X-Internal-Auth: Bearer <INTERNAL_AUTH_TOKEN>
```

## 本地运行

```bash
cd code/sol-agent
cp .env.example .env.local
pnpm install
pnpm start:dev
```

## 测试

```bash
# 健康检查
curl http://localhost:4000/api/healthz

# 地址生成（需替换 token）
curl -X POST http://localhost:4000/api/internal/v1/address \
  -H "Content-Type: application/json" \
  -H "X-Internal-Auth: Bearer change-me-in-production" \
  -d '{"accountId":"user_123"}'
```

## 部署

参见 `code/deploy/SOL_AGENT_DEPLOYMENT.md`。
