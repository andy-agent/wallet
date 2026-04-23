export interface TronServiceConfig {
  baseUrl: string;
  apiKey?: string;
  timeoutMs: number;
  enabled: boolean;
}

export interface TronServiceHealth {
  status: 'healthy' | 'degraded' | 'unhealthy';
  service?: string;
  version?: string;
  timestamp?: string;
  checks?: {
    chain?: {
      status?: 'connected' | 'disconnected' | 'mock';
      network?: string;
      blockHeight?: number;
      rpcUrl?: string;
    };
  };
}

export interface TronCapabilitiesResponse {
  network: string;
  chainId?: string;
  supportedTokens?: string[];
  capabilities?: {
    query?: boolean;
    broadcast?: boolean;
    batchQuery?: boolean;
    addressValidation?: boolean;
  };
  limits?: {
    maxBatchSize?: number;
    rateLimitPerMinute?: number;
  };
  mockMode?: boolean;
}

export interface TronBlockInfo {
  height: number;
  hash: string;
  timestamp: string;
  txCount: number;
}

export interface TronAddressValidationResponse {
  address: string;
  valid: boolean;
  type: string;
}

export interface TronBroadcastRequest {
  signedTx: string;
}

export interface TronBroadcastResponse {
  success: boolean;
  txHash?: string;
  errorCode?: string;
  errorMessage?: string;
  acceptedAt?: string;
}

export interface TronTransactionQueryResponse {
  txHash: string;
  status: 'pending' | 'confirmed' | 'failed' | 'not_found';
  from: string;
  to: string;
  amount: string;
  token: 'USDT' | 'TRX';
  blockHeight?: number;
  confirmations: number;
  timestamp?: string;
  fee?: string;
}

export interface TronTransactionEnvelope {
  found: boolean;
  transaction: TronTransactionQueryResponse | null;
}

export interface VerifyIncomingTronTransferRequest {
  txHash: string;
  recipientAddress: string;
  assetCode: string;
  contractAddress?: string | null;
  assetDecimals?: number;
  expectedAmount: string;
}
