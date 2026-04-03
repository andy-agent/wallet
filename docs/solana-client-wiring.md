# Solana/USDT Service Client Wiring

**Issue:** liaojiang-rcb.11 - 实现 API 层到 sol/usdt 服务的真实客户端接线

**Date:** 2026-04-03

## Summary

Implemented minimal client wiring for sol/usdt chain-side service integration. This is a skeleton implementation that prepares the codebase for real chain integration while maintaining safety defaults.

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
- Updated `proxyBroadcast()` to use Solana client for SOLANA network
- Updated `isAddressValid()` to use Solana client validation

### 5. `code/backend/src/modules/wallet/wallet.controller.ts`
- Made `proxyBroadcast()` async to support async service calls

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
| `health()` | Service health check | ✅ Skeleton (mock when disabled) |
| `broadcastTransaction()` | Broadcast signed tx | ✅ Skeleton (mock when disabled) |
| `getTransactionStatus()` | Check tx status | ✅ Skeleton (mock when disabled) |
| `getBalance()` | Get SOL/USDT balance | ✅ Skeleton (mock when disabled) |
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
- `proxyBroadcast()` now checks `SolanaClientService.isEnabled()`
- Uses `validateAddress()` for Solana address validation
- Returns service status in response (`serviceEnabled` field)

### Future Integration Points (TODO)
- **OrdersModule**: Verify payment transactions via `getTransactionStatus()`
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

1. ✅ Configuration skeleton with env vars
2. ✅ Module structure following NestJS patterns
3. ✅ Service injection and wiring
4. ✅ Address validation for Solana
5. ✅ Mock responses when service disabled
6. ✅ Type safety throughout

## What's Still Skeleton (Not Real Chain)

1. ❌ `broadcastTransaction()` - Returns mock signature
2. ❌ `getTransactionStatus()` - Returns mock status
3. ❌ `getBalance()` - Returns mock balance
4. ❌ `health()` - Returns mock health when disabled
5. ❌ Real HTTP calls to sol/usdt service

## Next Steps (Future Issues)

1. **Deploy sol/usdt service** - Service needs to be running
2. **Implement real HTTP calls** - Remove mock responses when enabled
3. **Add error handling** - Circuit breaker, retries, timeouts
4. **Add metrics/logging** - Monitor service health and performance
5. **Integration tests** - E2E tests with real/devnet service
6. **Order payment verification** - Wire into order lifecycle
7. **Withdrawal broadcasting** - Wire into withdrawal lifecycle
