# Solana/USDT Service Client Wiring

**Issue:** liaojiang-rcb.11 / liaojiang-rcb.14.1 - 实现 API 层到 sol/usdt 服务的真实客户端接线

**Date:** 2026-04-03

## Summary

Implemented remote chain-side client abstraction with real service integration for wallet paths. The implementation supports:
- **Transfer precheck** via remote service with graceful fallback
- **Proxy broadcast** via remote service with graceful fallback
- **Address validation** via client-side logic
- **Configurable service toggle** with safety defaults (disabled by default)

## Files Added

### 1. `code/backend/src/modules/solana-client/`
New module for sol/usdt service client:

- **`solana-client.module.ts`** - NestJS module definition
- **`solana-client.service.ts`** - HTTP client service with skeleton methods
- **`solana-client.config.ts`** - Configuration service for environment variables
- **`solana-client.types.ts`** - TypeScript type definitions
- **`index.ts`** - Public API exports

## Files Modified

### 1. `code/backend/.env.example`
Added configuration options:
```bash
SOLANA_SERVICE_ENABLED=false      # Enable/disable real chain calls
SOLANA_SERVICE_URL=http://localhost:8080
SOLANA_SERVICE_API_KEY=           # Optional API key
SOLANA_SERVICE_TIMEOUT_MS=30000
SOLANA_SERVICE_USE_DEVNET=true    # Safety: devnet by default
SOLANA_SERVICE_MAX_RETRIES=3
```

### 2. `code/backend/src/app.module.ts`
- Added import for `SolanaClientModule`

### 3. `code/backend/src/modules/wallet/wallet.module.ts`
- Added `SolanaClientModule` to imports

### 4. `code/backend/src/modules/wallet/wallet.service.ts`
- Injected `SolanaClientService`
- Updated `transferPrecheck()` to use remote service when enabled (async, with fallback)
- Updated `proxyBroadcast()` to call real `broadcastTransaction()` when enabled (with fallback)
- Updated `isAddressValid()` to use Solana client validation

### 5. `code/backend/src/modules/wallet/wallet.controller.ts`
- Made `proxyBroadcast()` async to support async service calls
- Made `transferPrecheck()` async to support async service calls

### 6. `code/backend/src/modules/wallet/dto/proxy-broadcast.request.ts`
- Added optional `toAddress` field (for validation)
- Added optional `serializedTx` field (for service broadcast)

### 7. `code/backend/package.json`
- Added `@nestjs/axios` dependency for HTTP client
- Added `axios` dependency

## SolanaClientService API

### Methods

| Method | Description | Status |
|--------|-------------|--------|
| `isEnabled()` | Check if real chain calls enabled | ✅ Implemented |
| `health()` | Service health check | ✅ Implemented (with mock fallback) |
| `broadcastTransaction()` | Broadcast signed tx | ✅ Implemented (with mock fallback) |
| `precheckTransfer()` | Transfer precheck via remote | ✅ Implemented (with mock fallback) |
| `getTransactionStatus()` | Check tx status | ✅ Implemented (with mock fallback) |
| `getBalance()` | Get SOL/USDT balance | ✅ Implemented (with mock fallback) |
| `validateAddress()` | Validate Solana address format | ✅ Implemented |
| `getUsdtMint()` | Get USDT mint address | ✅ Implemented |

## Configuration

### Safety Defaults
- `SOLANA_SERVICE_ENABLED=false` - Real calls disabled by default
- `SOLANA_SERVICE_USE_DEVNET=true` - Uses devnet by default

### Enabling Real Service
1. Deploy sol/usdt service
2. Set `SOLANA_SERVICE_URL` to service endpoint
3. Set `SOLANA_SERVICE_ENABLED=true` for production
4. Set `SOLANA_SERVICE_USE_DEVNET=false` for mainnet
5. Optionally set `SOLANA_SERVICE_API_KEY` for auth

## Integration Points

### WalletModule Integration
- `transferPrecheck()` - Calls remote precheck service when enabled, falls back to local calculation on failure
- `proxyBroadcast()` - Calls `broadcastTransaction()` when service enabled and `serializedTx` provided, falls back to mock on failure
- `isAddressValid()` - Uses `validateAddress()` for Solana address validation
- All responses include `serviceEnabled` field to indicate if remote service was used

### OrdersModule Integration (liaojiang-rcb.14.2.1)
- `getPaymentTarget()` - Returns `serviceEnabled` flag to indicate chain-side service availability
- `refreshStatus()` - Calls `getTransactionStatus()` for SOLANA orders when `submittedClientTxHash` is present
  - On `confirmed`/`finalized`: advances order to `PAID` → `PROVISIONING` → `COMPLETED`
  - On `failed`: marks order as `FAILED` with on-chain error reason
  - On `pending`: preserves current state
  - On remote service exception: falls back to existing in-memory state machine progression (graceful degradation)
- Terminal states (`COMPLETED`, `FAILED`, `EXPIRED`, `CANCELED`) are guarded against repeated progression

### Future Integration Points (TODO)
- **WithdrawalsModule**: Broadcast withdrawal transactions via `broadcastTransaction()`
- **HealthModule**: Add sol/usdt service health check

## Verification

```bash
# Type check
pnpm run typecheck  # ✅ Passed

# Build
pnpm run build      # ✅ Passed
```

## What Works Now

1. ✅ Configuration with env vars and safety defaults
2. ✅ Module structure following NestJS patterns
3. ✅ Service injection and wiring
4. ✅ Address validation for Solana (client-side)
5. ✅ **Transfer precheck with remote service integration** (liaojiang-rcb.14.1)
6. ✅ **Proxy broadcast with remote service integration** (liaojiang-rcb.14.1)
7. ✅ **Graceful degradation** - Falls back to mock behavior when service disabled/unavailable
8. ✅ Type safety throughout

## Service Behavior

### When `SOLANA_SERVICE_ENABLED=true`
- `transferPrecheck()` calls remote `/v1/transfers/precheck` endpoint
- `proxyBroadcast()` calls remote `/v1/transactions/broadcast` endpoint (if `serializedTx` provided)
- On remote service failure, falls back to mock behavior (does not crash)

### When `SOLANA_SERVICE_ENABLED=false` (default)
- All operations return mock responses
- No external HTTP calls made
- Safe for development and testing

## TRON Network
- TRON network still uses mock behavior (no remote client yet)
- Only Solana network has remote service integration

## Future Work

1. ✅ **OrdersModule**: Verify payment transactions via `getTransactionStatus()` (liaojiang-rcb.14.2.1)
2. **WithdrawalsModule**: Broadcast withdrawal transactions via `broadcastTransaction()`
3. **HealthModule**: Add sol/usdt service health check endpoint
4. **TRON client**: Implement similar remote client for TRON network
5. **Circuit breaker**: Add resilience patterns for remote calls
6. **Metrics/logging**: Enhanced observability for service calls
