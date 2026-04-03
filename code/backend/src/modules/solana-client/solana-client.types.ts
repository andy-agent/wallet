/**
 * Solana Client Types
 *
 * Types for interacting with sol/usdt chain-side service.
 * These are skeleton types for the initial wiring phase.
 */

export interface SolanaServiceConfig {
  /** Base URL for the sol/usdt service */
  baseUrl: string;
  /** API key for authentication (optional) */
  apiKey?: string;
  /** Request timeout in milliseconds */
  timeoutMs: number;
  /** Enable/disable real chain calls */
  enabled: boolean;
}

export interface BroadcastTransactionRequest {
  /** Serialized transaction (base64) */
  serializedTx: string;
  /** Network to broadcast to (mainnet/devnet) */
  network?: 'mainnet' | 'devnet';
  /** Maximum retries */
  maxRetries?: number;
}

export interface BroadcastTransactionResponse {
  /** Transaction signature/hash */
  signature: string;
  /** Slot when transaction was processed */
  slot?: number;
  /** Whether transaction is confirmed */
  confirmed: boolean;
}

export interface GetTransactionStatusRequest {
  /** Transaction signature */
  signature: string;
  /** Network */
  network?: 'mainnet' | 'devnet';
}

export interface GetTransactionStatusResponse {
  /** Transaction signature */
  signature: string;
  /** Current status */
  status: 'pending' | 'confirmed' | 'finalized' | 'failed';
  /** Confirmation count */
  confirmations: number;
  /** Error message if failed */
  error?: string;
  /** Block time if available */
  blockTime?: number;
  /** Slot */
  slot?: number;
}

export interface GetBalanceRequest {
  /** Wallet address */
  address: string;
  /** Token mint (null for SOL) */
  mint?: string;
  /** Network */
  network?: 'mainnet' | 'devnet';
}

export interface GetBalanceResponse {
  /** Wallet address */
  address: string;
  /** Token mint */
  mint: string | null;
  /** Balance in smallest unit (lamports for SOL) */
  balance: string;
  /** Decimal places */
  decimals: number;
  /** Balance as human-readable string */
  uiAmount: string;
}

/** Health check response from sol/usdt service */
export interface SolanaServiceHealth {
  /** Service status */
  status: 'healthy' | 'degraded' | 'unhealthy';
  /** Current block height */
  blockHeight?: number;
  /** RPC latency in ms */
  rpcLatencyMs?: number;
  /** Version info */
  version?: string;
}
