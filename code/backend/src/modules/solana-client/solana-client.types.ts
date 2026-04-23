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

export interface VerifyIncomingTransferRequest {
  /** Transaction signature */
  signature: string;
  /** Network */
  network?: 'mainnet' | 'devnet';
  /** Expected recipient address */
  recipientAddress: string;
  /** Expected asset code */
  assetCode: string;
  /** Expected token mint (for SPL tokens) */
  mint?: string | null;
  /** Token decimals for custom SPL assets */
  assetDecimals?: number;
  /** Expected amount in UI units */
  expectedAmount: string;
}

export interface VerifyIncomingTransferResponse {
  /** Transaction signature */
  signature: string;
  /** Canonical chain status */
  status: 'pending' | 'confirmed' | 'finalized' | 'failed';
  /** Confirmation count */
  confirmations: number;
  /** Verification result */
  verified: boolean;
  /** Matched recipient address */
  recipientAddress?: string;
  /** Matched asset code */
  assetCode?: string;
  /** Matched mint */
  mint?: string | null;
  /** Received amount in UI units */
  amount?: string;
  /** Mismatch code when verification fails */
  mismatchCode?:
    | 'RECIPIENT_MISMATCH'
    | 'ASSET_MISMATCH'
    | 'AMOUNT_UNDER'
    | 'AMOUNT_OVER';
  /** Human-readable failure reason */
  failureReason?: string;
  /** Error message if tx failed */
  error?: string;
  /** Block time if available */
  blockTime?: number;
  /** Slot */
  slot?: number;
}

export interface ScanIncomingTransfersCursor {
  beforeSignature?: string | null;
  minSlotExclusive?: number | null;
}

export interface ScanIncomingTransfersRequest {
  collectionAddress: string;
  assetCode: string;
  mint?: string | null;
  network?: 'mainnet' | 'devnet';
  cursor?: ScanIncomingTransfersCursor | null;
  limit?: number;
}

export interface NormalizedIncomingTransfer {
  signature: string;
  eventIndex: number;
  slot: number | null;
  blockTime: number | null;
  confirmationStatus: 'processed' | 'confirmed' | 'finalized' | 'failed' | 'unknown';
  recipientOwnerAddress: string;
  recipientTokenAccount?: string | null;
  fromAddress?: string | null;
  assetCode: string;
  mint?: string | null;
  decimals: number;
  amount: string;
  amountRaw: string;
  rawPayload?: Record<string, unknown> | null;
}

export interface ScanIncomingTransfersResponse {
  networkCode: string;
  collectionAddress: string;
  assetCode: string;
  mint?: string | null;
  events: NormalizedIncomingTransfer[];
  nextCursor: ScanIncomingTransfersCursor | null;
  scannedAt: string;
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

/** Transfer precheck request */
export interface TransferPrecheckRequest {
  /** Target network */
  network: 'mainnet' | 'devnet';
  /** Token mint address (null for SOL) */
  mint: string | null;
  /** Recipient address */
  toAddress: string;
  /** Amount in smallest unit */
  amount: string;
}

/** Transfer precheck response */
export interface TransferPrecheckResponse {
  /** Whether the transfer is valid */
  valid: boolean;
  /** Normalized address */
  toAddressNormalized: string;
  /** Estimated fee in smallest unit */
  estimatedFee: string;
  /** Error code if invalid */
  errorCode?: string;
  /** Error message if invalid */
  errorMessage?: string;
}
