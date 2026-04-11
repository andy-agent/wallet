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
| POST | `/api/internal/v1/payment/verify` | 按签名校验 SOL/SPL USDT 是否向目标地址完成支付（需内部鉴权） |

## 内部鉴权

调用 `/internal/*` 接口时，Header 需携带：

```
X-Internal-Auth: Bearer <INTERNAL_AUTH_TOKEN>
```

## 生产环境配置

服务通过 `ConfigModule.forRoot({ envFilePath: ['.env.local', '.env'] })` 加载配置。

已知线上部署约定：

- 服务器：`38.58.59.119`
- 目录：`/opt/sol-agent`
- systemd：`sol-agent.service`
- 建议把私有 RPC 和内部鉴权配置写到 `/opt/sol-agent/.env.local`

建议至少配置：

```dotenv
PORT=4000
NODE_ENV=production
INTERNAL_AUTH_TOKEN=<strong-random-token>
SOLANA_RPC_URL_MAINNET=<private-mainnet-rpc-url>
SOLANA_RPC_URL_DEVNET=<optional-devnet-rpc-url>
```

## 当前限制

- 地址生成仍为进程内存存储，不适合作为正式钱包托管方案
- `/api/internal/v1/payment/verify` 已支持基于真实 Solana RPC 的签名校验：
  - 原生 SOL 使用账户 lamports delta 校验
  - SPL Token 使用 `preTokenBalances/postTokenBalances` delta 校验
  - mainnet USDT 可不传 mint，默认使用官方 mint `Es9vMFrzaCERmJfrF4H2FYD4KCoNkY11McCe8BenwNYB`
- 不含 DEX、Swap、自动归集、热冷钱包隔离等更高阶链侧能力
