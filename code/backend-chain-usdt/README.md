# USDT Chain Service (TRON/TRC20)

TRON/TRC20 链侧服务骨架，为 `usdt.residential-agent.com` 提供最小可运行实现。

## 架构定位

```
┌─────────────────────────────────────────────────────────────────┐
│                        Main Backend API                         │
│                   (api.residential-agent.com)                   │
│                                                                 │
│  ┌──────────────┐    X-API-Key    ┌──────────────────────────┐ │
│  │  Orders      │ ───────────────►│   Chain-USDT Service     │ │
│  │  Wallet      │   Internal      │  (usdt.residential-agent)│ │
│  └──────────────┘   Auth          └──────────────────────────┘ │
│                                            │                    │
└────────────────────────────────────────────┼────────────────────┘
                                             │
                              ┌──────────────┴──────────────┐
                              │     TRON Mainnet RPC        │
                              │   (api.trongrid.io)         │
                              └─────────────────────────────┘
```

## 职责边界

**Chain-USDT 服务负责:**
- 查询 TRON 链上交易状态
- 广播已签名交易
- 提供区块信息
- 验证 TRON 地址格式

**Chain-USDT 服务不负责:**
- ❌ 交易签名（在主后端或客户端完成）
- ❌ 多链聚合（每个链有独立服务）
- ❌ 自动提现逻辑（由主后端触发）
- ❌ 代签名服务

## 快速开始

```bash
# 安装依赖
pnpm install

# 配置环境变量
cp .env.example .env
# 编辑 .env 设置 INTERNAL_API_KEY

# 开发模式启动
pnpm run start:dev
```

## API 端点

### 健康检查 (Public)
```
GET /api/healthz
GET /api/healthz/ready
```

### 链操作 (Requires X-API-Key)
```
# 查询交易
GET  /api/v1/chain/tx/:hash
POST /api/v1/chain/tx/batch

# 广播已签名交易
POST /api/v1/chain/broadcast
  Body: { "signedTx": "0x..." }

# 区块信息
GET /api/v1/chain/block/current

# 地址验证
GET /api/v1/chain/address/validate?address=T...

# USDT 合约信息
GET /api/v1/chain/contract/usdt

# 服务能力发现
GET /api/v1/chain/capabilities
```

### 内部服务 (Partially Protected)
```
# 服务发现 (Public)
GET /api/internal/discovery

# 配置查看 (Protected)
GET /api/internal/config
```

## 认证约定

内部服务间通信使用 `X-API-Key` Header:

```
X-API-Key: your-internal-api-key-here
```

此密钥需在 `.env` 中配置 `INTERNAL_API_KEY`，并在主后端 API 中同步设置。

## 当前状态

| 能力 | 状态 | 说明 |
|------|------|------|
| Health | ✅ 可用 | 基础健康检查 |
| Tx 查询 | 🟡 占位 | 返回 mock 数据 |
| Tx 广播 | 🟡 占位 | 接受请求，返回 mock txHash |
| 地址验证 | ✅ 可用 | 基础格式校验 |
| 区块查询 | 🟡 占位 | 返回 mock 数据 |

## 下一步

1. **接入真实 TRON RPC**
   - 集成 tronweb 或 trongrid API
   - 实现真实交易查询
   - 实现真实广播

2. **监控与日志**
   - 添加链上操作 metrics
   - 配置 alerting

3. **缓存层**
   - 对频繁查询添加 Redis 缓存
   - 区块高度缓存

## 部署

```bash
# 构建
pnpm run build

# 生产启动
pnpm run start:prod
```

默认监听端口: `3001`

## 与主后端集成

主后端需要配置:
```env
# 指向 chain-usdt 服务
CHAIN_USDT_BASE_URL=http://localhost:3001/api
CHAIN_USDT_API_KEY=your-internal-api-key-here
```
