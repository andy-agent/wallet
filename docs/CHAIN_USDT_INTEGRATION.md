# Chain-USDT 服务集成指南

本文档描述主后端 API 如何与 Chain-USDT 服务集成。

## 服务发现

Chain-USDT 服务提供发现端点（无需认证）：

```bash
curl http://localhost:3001/api/internal/discovery
```

响应：
```json
{
  "service": "chain-usdt",
  "type": "tron-trc20",
  "version": "0.0.1",
  "endpoints": {
    "base": "/api/v1/chain",
    "health": "/api/healthz",
    "docs": "/api/docs"
  },
  "network": {
    "name": "tron-mainnet",
    "chainId": "728126428",
    "nativeCurrency": "TRX",
    "supportedTokens": ["USDT"]
  }
}
```

## 认证配置

在主后端 `.env` 中添加：

```env
# Chain-USDT 服务配置
CHAIN_USDT_BASE_URL=http://localhost:3001/api
CHAIN_USDT_API_KEY=your-internal-api-key-here
```

确保此 `CHAIN_USDT_API_KEY` 与 Chain-USDT 服务中配置的 `INTERNAL_API_KEY` 一致。

## 主后端封装示例

```typescript
// src/modules/wallet/chain-usdt.client.ts
import { Injectable } from '@nestjs/common';
import { ConfigService } from '@nestjs/config';

interface ChainTxResult {
  found: boolean;
  transaction?: {
    txHash: string;
    status: 'pending' | 'confirmed' | 'failed';
    from: string;
    to: string;
    amount: string;
    confirmations: number;
  };
}

@Injectable()
export class ChainUsdtClient {
  private readonly baseUrl: string;
  private readonly apiKey: string;

  constructor(private readonly configService: ConfigService) {
    this.baseUrl = this.configService.getOrThrow<string>('CHAIN_USDT_BASE_URL');
    this.apiKey = this.configService.getOrThrow<string>('CHAIN_USDT_API_KEY');
  }

  async queryTransaction(txHash: string): Promise<ChainTxResult> {
    const response = await fetch(`${this.baseUrl}/v1/chain/tx/${txHash}`, {
      headers: { 'X-API-Key': this.apiKey },
    });
    return response.json();
  }

  async validateAddress(address: string): Promise<boolean> {
    const response = await fetch(
      `${this.baseUrl}/v1/chain/address/validate?address=${address}`,
      { headers: { 'X-API-Key': this.apiKey } },
    );
    const result = await response.json();
    return result.data.valid;
  }
}
```

## 典型调用流程

### 1. 支付确认检查

```
Main Backend                     Chain-USDT Service
   │                                    │
   │  GET /v1/chain/tx/{txHash}         │
   │  X-API-Key: ***                    │
   │ ──────────────────────────────────>│
   │                                    │
   │  { found: true, transaction: {...} }│
   │<───────────────────────────────────│
   │                                    │
```

### 2. 提现广播

```
Main Backend                     Chain-USDT Service
   │                                    │
   │  POST /v1/chain/broadcast          │
   │  { signedTx: "0x..." }             │
   │  X-API-Key: ***                    │
   │ ──────────────────────────────────>│
   │                                    │
   │  { success: true, txHash: "0x..." } │
   │<───────────────────────────────────│
   │                                    │
```

## 错误处理

Chain-USDT 服务使用统一错误格式：

```json
{
  "requestId": "uuid",
  "code": "INTERNAL_AUTH_INVALID_KEY",
  "message": "Invalid API key",
  "data": null
}
```

主后端应处理以下错误码：

| 错误码 | 说明 | 处理方式 |
|--------|------|----------|
| `INTERNAL_AUTH_MISSING_KEY` | 缺少 X-API-Key | 检查配置 |
| `INTERNAL_AUTH_INVALID_KEY` | API Key 不匹配 | 检查配置 |
| `SERVICE_UNAVAILABLE` | 链服务不可用 | 重试或降级 |

## 部署拓扑

```
Production:
┌─────────────────┐     ┌──────────────────┐
│  Main Backend   │────►│  Chain-USDT      │
│  (api.*)        │     │  (usdt.*)        │
│  Port: 3000     │     │  Port: 3001      │
└─────────────────┘     └──────────────────┘
        │                        │
        ▼                        ▼
   PostgreSQL               TronGrid RPC
```

## 健康检查集成

主后端可在自己的 healthz 端点中检查 Chain-USDT 状态：

```typescript
async getHealth() {
  const chainHealth = await this.chainUsdtClient.checkHealth();
  return {
    status: chainHealth.status === 'healthy' ? 'healthy' : 'degraded',
    dependencies: {
      chainUsdt: chainHealth,
    },
  };
}
```
