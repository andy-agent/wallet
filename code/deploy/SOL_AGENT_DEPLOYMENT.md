# Sol-Agent Deployment

Solana 链侧最小服务骨架的部署文档。

## 文件

- `docker-compose.sol-agent.yml`
- `.env.sol-agent.example`

## 本地启动

```bash
cd code/deploy
cp .env.sol-agent.example .env.sol-agent
docker compose --env-file .env.sol-agent -f docker-compose.sol-agent.yml up -d --build
```

## 健康检查

```bash
curl http://localhost:4000/api/healthz
```

## 接口速查

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/healthz` | 健康检查（公开） |
| POST | `/api/internal/v1/address` | 生成地址（需内部鉴权） |
| GET | `/api/internal/v1/address/:accountId` | 查询地址（需内部鉴权） |
| GET | `/api/internal/v1/payment/:address/status` | 查询收款状态（需内部鉴权） |
| POST | `/api/internal/v1/payment/detect` | 主动检测收款（需内部鉴权） |

## 内部鉴权

调用 `/internal/*` 接口时，Header 需携带：

```
X-Internal-Auth: Bearer <INTERNAL_AUTH_TOKEN>
```

## 当前限制

- 地址生成与支付检测均为占位实现，未接入真实 Solana RPC
- 不含 DEX、Swap、全节点、自动归集等能力
- 当前仅用于接口约定与最小服务骨架验证
