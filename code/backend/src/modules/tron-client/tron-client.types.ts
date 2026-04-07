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
