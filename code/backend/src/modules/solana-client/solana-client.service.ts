/**
 * Solana Client Service
 *
 * HTTP client for interacting with sol/usdt chain-side service.
 * This is a skeleton implementation for wiring phase.
 *
 * TODO (liaojiang-rcb.11 follow-up):
 * - Implement real HTTP calls to sol/usdt service
 * - Add circuit breaker pattern
 * - Add proper error handling and retries
 * - Add metrics/logging
 */

import { HttpService } from '@nestjs/axios';
import {
  Injectable,
  Logger,
  ServiceUnavailableException,
} from '@nestjs/common';
import { firstValueFrom } from 'rxjs';
import { SolanaClientConfig } from './solana-client.config';
import type {
  BroadcastTransactionRequest,
  BroadcastTransactionResponse,
  GetBalanceRequest,
  GetBalanceResponse,
  GetTransactionStatusRequest,
  GetTransactionStatusResponse,
  NormalizedIncomingTransfer,
  SolanaServiceHealth,
  ScanIncomingTransfersRequest,
  ScanIncomingTransfersResponse,
  TransferPrecheckRequest,
  TransferPrecheckResponse,
  VerifyIncomingTransferRequest,
  VerifyIncomingTransferResponse,
} from './solana-client.types';
import type { AxiosResponse } from 'axios';

interface EnvelopeResponse<T> {
  data?: T;
}

interface LegacyPaymentDetectResponse {
  address?: string;
  status?: 'pending' | 'partial' | 'confirmed' | 'error';
  receivedAmount?: string;
  expectedAmount?: string | null;
  txHash?: string | null;
  recentTransactions?: string[];
  confirmations?: number;
  error?: string;
}

interface VerifyPaymentResponse {
  signature?: string;
  networkCode?: string;
  status?: 'verified' | 'mismatch' | 'failed' | 'pending';
  recipientAddress?: string;
  assetCode?: string;
  mintAddress?: string | null;
  expectedAmount?: string;
  receivedAmount?: string;
  recipientMatched?: boolean;
  amountSatisfied?: boolean;
  error?: string;
  blockTime?: number | null;
  slot?: number | null;
}

interface ScanIncomingApiItem {
  signature: string;
  slot?: number | null;
  blockTime?: number | null;
  confirmationStatus?: string | null;
  collectionAddress?: string;
  assetCode?: string;
  mintAddress?: string | null;
  decimals?: number;
  amount?: string;
  amountRaw?: string;
  matchedAccounts?: string[];
}

interface ScanIncomingApiResponse {
  networkCode?: string;
  collectionAddress?: string;
  assetCode?: string;
  mintAddress?: string | null;
  nextBeforeSignature?: string | null;
  nextMinSlotExclusive?: number | null;
  items?: ScanIncomingApiItem[];
}

@Injectable()
export class SolanaClientService {
  private readonly logger = new Logger(SolanaClientService.name);

  constructor(
    private readonly httpService: HttpService,
    private readonly config: SolanaClientConfig,
  ) {}

  /**
   * Check if real chain calls are enabled
   */
  isEnabled(): boolean {
    return this.config.isEnabled();
  }

  /**
   * Health check for sol/usdt service
   * Returns mock response when service is disabled
   */
  async health(): Promise<SolanaServiceHealth> {
    if (!this.isEnabled()) {
      this.logger.debug('Solana service disabled, returning mock health');
      return {
        status: 'healthy',
        version: 'mock-0.0.1',
      };
    }

    try {
      const response = await firstValueFrom(
        this.httpService.get(this.buildApiUrl('healthz'), {
          timeout: this.config.getTimeoutMs(),
          headers: this.getAuthHeaders(),
        }),
      );
      return this.unwrapResponse<SolanaServiceHealth>(
        response as AxiosResponse<SolanaServiceHealth | EnvelopeResponse<SolanaServiceHealth>>,
      );
    } catch (error) {
      this.logger.error('Solana service health check failed', error);
      throw new ServiceUnavailableException({
        code: 'SOLANA_SERVICE_UNHEALTHY',
        message: 'Solana service is unavailable',
      });
    }
  }

  /**
   * Broadcast a transaction to Solana
   * Currently returns mock response when service is disabled
   * TODO: Implement real broadcast when service is available
   */
  async broadcastTransaction(
    request: BroadcastTransactionRequest,
  ): Promise<BroadcastTransactionResponse> {
    if (!this.isEnabled()) {
      this.logger.debug('Solana service disabled, returning mock broadcast', {
        network: request.network,
      });
      // Mock response for wiring phase
      return {
        signature: `mock_sig_${Date.now()}`,
        confirmed: false,
      };
    }

    try {
      const network = this.getEffectiveNetwork(request.network);

      const response = await firstValueFrom(
        this.httpService.post(
          this.buildApiUrl('v1/transactions/broadcast'),
          {
            serializedTx: request.serializedTx,
            network,
            maxRetries:
              request.maxRetries ?? this.config.getMaxRetries(),
          },
          {
            timeout: this.config.getTimeoutMs(),
            headers: this.getAuthHeaders(),
          },
        ),
      );

      return this.unwrapResponse<BroadcastTransactionResponse>(
        response as AxiosResponse<
          BroadcastTransactionResponse | EnvelopeResponse<BroadcastTransactionResponse>
        >,
      );
    } catch (error) {
      this.logger.error('Broadcast transaction failed', error);
      throw new ServiceUnavailableException({
        code: 'SOLANA_BROADCAST_FAILED',
        message: 'Failed to broadcast transaction',
      });
    }
  }

  /**
   * Get transaction status
   * Currently returns mock response when service is disabled
   */
  async getTransactionStatus(
    request: GetTransactionStatusRequest,
  ): Promise<GetTransactionStatusResponse> {
    if (!this.isEnabled()) {
      this.logger.debug('Solana service disabled, returning mock status', {
        signature: request.signature,
      });
      // Mock response for wiring phase
      return {
        signature: request.signature,
        status: 'confirmed',
        confirmations: 1,
      };
    }

    try {
      const network = this.getEffectiveNetwork(request.network);

      const response = await firstValueFrom(
        this.httpService.get(
          this.buildApiUrl(`v1/transactions/${request.signature}`),
          {
            params: { network },
            timeout: this.config.getTimeoutMs(),
            headers: this.getAuthHeaders(),
          },
        ),
      );

      return this.unwrapResponse<GetTransactionStatusResponse>(
        response as AxiosResponse<
          GetTransactionStatusResponse | EnvelopeResponse<GetTransactionStatusResponse>
        >,
      );
    } catch (error) {
      this.logger.error('Get transaction status failed', error);
      throw new ServiceUnavailableException({
        code: 'SOLANA_STATUS_CHECK_FAILED',
        message: 'Failed to get transaction status',
      });
    }
  }

  async verifyIncomingTransfer(
    request: VerifyIncomingTransferRequest,
  ): Promise<VerifyIncomingTransferResponse> {
    if (!this.isEnabled()) {
      this.logger.debug(
        'Solana service disabled, returning test-safe verification response',
        {
          signature: request.signature,
          recipientAddress: request.recipientAddress,
          assetCode: request.assetCode,
        },
      );
      return {
        signature: request.signature,
        status: 'confirmed',
        confirmations: 1,
        verified: true,
        recipientAddress: request.recipientAddress.trim(),
        assetCode: request.assetCode,
        mint: request.mint,
        amount: request.expectedAmount,
      };
    }

    try {
      const network = this.getEffectiveNetwork(request.network);
      const networkCode = this.toNetworkCode(network);
      const payload = await this.verifyWithFallback({
        signature: request.signature,
        recipientAddress: request.recipientAddress,
        assetCode: request.assetCode,
        mint: request.mint,
        expectedAmount: request.expectedAmount,
        networkCode,
      });

      let txStatus: GetTransactionStatusResponse | null = null;
      try {
        txStatus = await this.getTransactionStatus({
          signature: request.signature,
          network,
        });
      } catch (error) {
        this.logger.warn(
          'Transaction status lookup failed during verifyIncomingTransfer; continuing with verify payload',
          error instanceof Error ? error.message : String(error),
        );
      }

      return this.normalizeVerifyIncomingTransferResponse(
        payload,
        request,
        txStatus,
      );
    } catch (error) {
      this.logger.error('Verify incoming transfer failed', error);
      throw new ServiceUnavailableException({
        code: 'SOLANA_VERIFY_TRANSFER_FAILED',
        message: 'Failed to verify incoming Solana transfer',
      });
    }
  }

  async scanIncomingTransfers(
    request: ScanIncomingTransfersRequest,
  ): Promise<ScanIncomingTransfersResponse> {
    const network = this.getEffectiveNetwork(request.network);
    const networkCode = this.toNetworkCode(network);

    if (!this.isEnabled()) {
      this.logger.debug('Solana service disabled, returning empty scan result', {
        collectionAddress: request.collectionAddress,
        assetCode: request.assetCode,
        networkCode,
      });
      return {
        networkCode,
        collectionAddress: request.collectionAddress,
        assetCode: request.assetCode,
        mint: request.mint ?? null,
        events: [],
        nextCursor: request.cursor ?? null,
        scannedAt: new Date().toISOString(),
      };
    }

    try {
      const response = await firstValueFrom(
        this.httpService.post(
          this.buildApiUrl('internal/v1/payment/scan-incoming'),
          {
            networkCode,
            collectionAddress: request.collectionAddress,
            assetCode: request.assetCode,
            mintAddress: request.mint ?? null,
            beforeSignature: request.cursor?.beforeSignature ?? null,
            minSlotExclusive: request.cursor?.minSlotExclusive ?? null,
            limit: request.limit ?? 50,
          },
          {
            timeout: this.config.getTimeoutMs(),
            headers: this.getAuthHeaders(),
          },
        ),
      );

      const payload = this.unwrapResponse<ScanIncomingApiResponse>(
        response as AxiosResponse<
          ScanIncomingApiResponse | EnvelopeResponse<ScanIncomingApiResponse>
        >,
      );

      return this.normalizeScanIncomingTransfersResponse(
        payload,
        request,
        networkCode,
      );
    } catch (error) {
      this.logger.error('Scan incoming transfers failed', error);
      throw new ServiceUnavailableException({
        code: 'SOLANA_SCAN_INCOMING_FAILED',
        message: 'Failed to scan incoming transfers from chain-side service',
      });
    }
  }

  /**
   * Get token/SOL balance for an address
   * Currently returns mock response when service is disabled
   */
  async getBalance(
    request: GetBalanceRequest,
  ): Promise<GetBalanceResponse> {
    if (!this.isEnabled()) {
      this.logger.debug('Solana service disabled, returning mock balance', {
        address: request.address,
        mint: request.mint,
      });
      // Mock response for wiring phase
      const isNative = !request.mint;
      return {
        address: request.address,
        mint: request.mint ?? null,
        balance: isNative ? '1000000000' : '1000000',
        decimals: isNative ? 9 : 6,
        uiAmount: isNative ? '1.0' : '1.0',
      };
    }

    try {
      const network = this.getEffectiveNetwork(request.network);

      const response = await firstValueFrom(
        this.httpService.get(this.buildApiUrl(`v1/balances/${request.address}`), {
          params: {
            network,
            mint: request.mint,
          },
          timeout: this.config.getTimeoutMs(),
          headers: this.getAuthHeaders(),
        }),
      );

      return this.unwrapResponse<GetBalanceResponse>(
        response as AxiosResponse<GetBalanceResponse | EnvelopeResponse<GetBalanceResponse>>,
      );
    } catch (error) {
      this.logger.error('Get balance failed', error);
      throw new ServiceUnavailableException({
        code: 'SOLANA_BALANCE_CHECK_FAILED',
        message: 'Failed to get balance',
      });
    }
  }

  /**
   * Perform transfer precheck via remote service
   * Returns mock response when service is disabled
   */
  async precheckTransfer(
    request: TransferPrecheckRequest,
  ): Promise<TransferPrecheckResponse> {
    // Always perform basic address validation first
    if (!this.validateAddress(request.toAddress)) {
      return {
        valid: false,
        toAddressNormalized: request.toAddress.trim(),
        estimatedFee: '0',
        errorCode: 'INVALID_ADDRESS',
        errorMessage: 'Invalid Solana address format',
      };
    }

    if (!this.isEnabled()) {
      this.logger.debug('Solana service disabled, returning mock precheck', {
        address: request.toAddress,
      });
      // Mock response for wiring phase - simulate success
      return {
        valid: true,
        toAddressNormalized: request.toAddress.trim(),
        estimatedFee: '5000', // 0.000005 SOL in lamports
      };
    }

    try {
      const response = await firstValueFrom(
        this.httpService.post(
          this.buildApiUrl('v1/transfers/precheck'),
          {
            network: request.network,
            mint: request.mint,
            toAddress: request.toAddress,
            amount: request.amount,
          },
          {
            timeout: this.config.getTimeoutMs(),
            headers: this.getAuthHeaders(),
          },
        ),
      );

      return this.unwrapResponse<TransferPrecheckResponse>(
        response as AxiosResponse<
          TransferPrecheckResponse | EnvelopeResponse<TransferPrecheckResponse>
        >,
      );
    } catch (error) {
      this.logger.error('Transfer precheck failed', error);
      // Return invalid response on error (graceful degradation)
      return {
        valid: false,
        toAddressNormalized: request.toAddress.trim(),
        estimatedFee: '0',
        errorCode: 'PRECHECK_FAILED',
        errorMessage: 'Failed to perform transfer precheck',
      };
    }
  }

  /**
   * Validate a Solana address format
   * Basic validation without RPC call
   */
  validateAddress(address: string): boolean {
    // Basic Solana address validation (base58, 32-44 chars)
    if (!address || typeof address !== 'string') {
      return false;
    }
    const trimmed = address.trim();
    // Solana addresses are base58 encoded and typically 32-44 characters
    if (trimmed.length < 32 || trimmed.length > 44) {
      return false;
    }
    // Basic base58 check (no 0, O, I, l characters)
    const base58Regex = /^[1-9A-HJ-NP-Za-km-z]+$/;
    return base58Regex.test(trimmed);
  }

  /**
   * Get USDT mint address for network
   */
  getUsdtMint(network?: 'mainnet' | 'devnet'): string {
    // USDT mint on Solana
    // Mainnet: Es9vMFrzaCERmJfrF4H2FYD4KCoNkY11McCe8BenwNYB
    // Devnet: Use a known devnet mint or the same (depending on service setup)
    const effectiveNetwork = network ?? (this.config.useDevnet() ? 'devnet' : 'mainnet');
    return effectiveNetwork === 'mainnet'
      ? 'Es9vMFrzaCERmJfrF4H2FYD4KCoNkY11McCe8BenwNYB'
      : 'Es9vMFrzaCERmJfrF4H2FYD4KCoNkY11McCe8BenwNYB'; // TODO: devnet mint
  }

  /**
   * Get authentication headers if API key is configured
   */
  private getAuthHeaders(): Record<string, string> {
    const apiKey = this.config.getApiKey();
    if (apiKey) {
      return {
        'X-API-Key': apiKey,
        'X-Internal-Auth': `Bearer ${apiKey}`,
      };
    }
    return {};
  }

  /**
   * Get effective network (respects devnet setting)
   */
  private getEffectiveNetwork(
    requested?: 'mainnet' | 'devnet',
  ): 'mainnet' | 'devnet' {
    if (requested) {
      return requested;
    }
    return this.config.useDevnet() ? 'devnet' : 'mainnet';
  }

  private toNetworkCode(network: 'mainnet' | 'devnet') {
    return network === 'devnet' ? 'solana-devnet' : 'solana-mainnet';
  }

  private buildApiUrl(path: string) {
    const baseUrl = this.config.getBaseUrl().replace(/\/+$/, '');
    const apiBaseUrl = baseUrl.endsWith('/api') ? baseUrl : `${baseUrl}/api`;
    return `${apiBaseUrl}/${path.replace(/^\/+/, '')}`;
  }

  private normalizeScanIncomingTransfersResponse(
    payload: ScanIncomingApiResponse,
    request: ScanIncomingTransfersRequest,
    fallbackNetworkCode: string,
  ): ScanIncomingTransfersResponse {
    const items = payload.items ?? [];
    const events = items.map((item, index) => ({
      signature: item.signature,
      eventIndex: index,
      slot: item.slot ?? null,
      blockTime: item.blockTime ?? null,
      confirmationStatus: this.normalizeScanConfirmationStatus(
        item.confirmationStatus,
      ),
      recipientOwnerAddress:
        item.collectionAddress ??
        payload.collectionAddress ??
        request.collectionAddress,
      recipientTokenAccount: item.matchedAccounts?.[0] ?? null,
      fromAddress: null,
      assetCode: item.assetCode ?? payload.assetCode ?? request.assetCode,
      mint: item.mintAddress ?? payload.mintAddress ?? request.mint ?? null,
      decimals: item.decimals ?? (request.mint ? 6 : 9),
      amount: item.amount ?? '0',
      amountRaw: item.amountRaw ?? '0',
      rawPayload: {
        matchedAccounts: item.matchedAccounts ?? [],
      },
    }));
    const maxObservedSlot = events.reduce<number | null>((maxSlot, event) => {
      if (event.slot === null) {
        return maxSlot;
      }
      if (maxSlot === null) {
        return event.slot;
      }
      return event.slot > maxSlot ? event.slot : maxSlot;
    }, null);

    return {
      networkCode: payload.networkCode ?? fallbackNetworkCode,
      collectionAddress:
        payload.collectionAddress ?? request.collectionAddress,
      assetCode: payload.assetCode ?? request.assetCode,
      mint: payload.mintAddress ?? request.mint ?? null,
      events,
      nextCursor: {
        beforeSignature: payload.nextBeforeSignature ?? null,
        minSlotExclusive:
          payload.nextMinSlotExclusive ??
          maxObservedSlot ??
          request.cursor?.minSlotExclusive ??
          null,
      },
      scannedAt: new Date().toISOString(),
    };
  }

  private normalizeScanConfirmationStatus(
    status: string | null | undefined,
  ): NormalizedIncomingTransfer['confirmationStatus'] {
    switch (status) {
      case 'processed':
      case 'confirmed':
      case 'finalized':
      case 'failed':
        return status;
      default:
        return 'unknown';
    }
  }

  private normalizeVerifyIncomingTransferResponse(
    payload:
      | VerifyIncomingTransferResponse
      | VerifyPaymentResponse
      | LegacyPaymentDetectResponse,
    request: VerifyIncomingTransferRequest,
    txStatus: GetTransactionStatusResponse | null,
  ): VerifyIncomingTransferResponse {
    if ('verified' in payload && typeof payload.verified === 'boolean') {
      return {
        signature: payload.signature ?? request.signature,
        status: payload.status,
        confirmations: payload.confirmations,
        verified: payload.verified,
        recipientAddress: payload.recipientAddress,
        assetCode: payload.assetCode,
        mint: payload.mint,
        amount: payload.amount,
        mismatchCode: payload.mismatchCode,
        error: payload.error,
        failureReason: payload.failureReason,
        blockTime: payload.blockTime ?? txStatus?.blockTime,
        slot: payload.slot ?? txStatus?.slot,
      };
    }

    if (
      'status' in payload &&
      (payload.status === 'verified' ||
        payload.status === 'mismatch' ||
        payload.status === 'pending' ||
        payload.status === 'failed')
    ) {
      const verifyPayload = payload as VerifyPaymentResponse;
      if (verifyPayload.status === 'pending' || verifyPayload.status === 'failed') {
        return {
          signature: request.signature,
          status: verifyPayload.status,
          confirmations: txStatus?.confirmations ?? 0,
          verified: false,
          error: verifyPayload.error ?? txStatus?.error,
          blockTime: (verifyPayload.blockTime ?? undefined) ?? txStatus?.blockTime,
          slot: (verifyPayload.slot ?? undefined) ?? txStatus?.slot,
        };
      }

      const receivedAmount = Number(verifyPayload.receivedAmount ?? '0');
      const expectedAmount = Number(request.expectedAmount);
      const hasAmount = Number.isFinite(receivedAmount) && Number.isFinite(expectedAmount);
      const amountOver = hasAmount && receivedAmount > expectedAmount;
      const amountUnder = hasAmount && receivedAmount < expectedAmount;
      const recipientMatched = verifyPayload.recipientMatched !== false;
      const amountSatisfied = verifyPayload.amountSatisfied !== false && !amountUnder;
      const verified = verifyPayload.status === 'verified' && recipientMatched && amountSatisfied;
      const status =
        txStatus?.status === 'finalized'
          ? 'finalized'
          : 'confirmed';

      let mismatchCode: VerifyIncomingTransferResponse['mismatchCode'];
      if (!verified) {
        if (!recipientMatched) {
          mismatchCode = 'RECIPIENT_MISMATCH';
        } else if (amountOver) {
          mismatchCode = 'AMOUNT_OVER';
        } else if (amountUnder) {
          mismatchCode = 'AMOUNT_UNDER';
        } else {
          mismatchCode = 'ASSET_MISMATCH';
        }
      }

      return {
        signature: verifyPayload.signature ?? request.signature,
        status,
        confirmations: txStatus?.confirmations ?? 1,
        verified,
        recipientAddress: verifyPayload.recipientAddress ?? request.recipientAddress,
        assetCode: verified ? request.assetCode : undefined,
        mint: verified ? (verifyPayload.mintAddress ?? request.mint ?? null) : undefined,
        amount: verifyPayload.receivedAmount,
        mismatchCode,
        failureReason: verified
          ? undefined
          : mismatchCode === 'RECIPIENT_MISMATCH'
            ? 'Submitted transaction recipient does not match the configured collection address'
            : mismatchCode === 'AMOUNT_OVER'
              ? 'Submitted transaction amount exceeds the expected payment target'
              : mismatchCode === 'AMOUNT_UNDER'
                ? 'Submitted transaction amount is below the expected payment target'
                : 'Submitted transaction asset does not match the expected payment asset',
        error: verifyPayload.error,
        blockTime: (verifyPayload.blockTime ?? undefined) ?? txStatus?.blockTime,
        slot: (verifyPayload.slot ?? undefined) ?? txStatus?.slot,
      };
    }

    const detectPayload = payload as LegacyPaymentDetectResponse;
    const signatureSeen =
      detectPayload.txHash === request.signature ||
      (detectPayload.recentTransactions ?? []).includes(request.signature);
    const receivedAmount = Number(detectPayload.receivedAmount ?? '0');
    const expectedAmount = Number(request.expectedAmount);
    const hasAmount = Number.isFinite(receivedAmount) && Number.isFinite(expectedAmount);
    const amountSatisfied = hasAmount && receivedAmount >= expectedAmount;

    const verified =
      request.assetCode === 'SOL' &&
      signatureSeen &&
      amountSatisfied &&
      detectPayload.status === 'confirmed';

    return {
      signature: request.signature,
      status: txStatus?.status === 'finalized' ? 'finalized' : 'confirmed',
      confirmations: txStatus?.confirmations ?? 1,
      verified,
      recipientAddress: detectPayload.address ?? request.recipientAddress,
      assetCode: verified ? request.assetCode : undefined,
      mint: verified ? request.mint : undefined,
      amount: detectPayload.receivedAmount,
      mismatchCode: signatureSeen
        ? amountSatisfied
          ? request.assetCode === 'SOL'
            ? undefined
            : 'ASSET_MISMATCH'
          : 'AMOUNT_UNDER'
        : 'RECIPIENT_MISMATCH',
      failureReason: verified
        ? undefined
        : request.assetCode === 'SOL'
          ? signatureSeen
            ? 'Detected recipient transaction amount is below the expected payment target'
            : 'Submitted transaction was not detected on the configured collection address'
          : 'Chain-side detect response does not include asset-level verification for this payment',
      error: detectPayload.error,
      blockTime: txStatus?.blockTime,
      slot: txStatus?.slot,
    };
  }

  private async verifyWithFallback(request: {
    signature: string;
    recipientAddress: string;
    assetCode: string;
    mint?: string | null;
    expectedAmount: string;
    networkCode: string;
  }) {
    const body = {
      signature: request.signature,
      recipientAddress: request.recipientAddress,
      assetCode: request.assetCode,
      mintAddress: request.mint,
      expectedAmount: request.expectedAmount,
      networkCode: request.networkCode,
    };

    try {
      const response = await firstValueFrom(
        this.httpService.post(this.buildApiUrl('internal/v1/payment/verify'), body, {
          timeout: this.config.getTimeoutMs(),
          headers: this.getAuthHeaders(),
        }),
      );
      return this.unwrapResponse<
        VerifyIncomingTransferResponse | VerifyPaymentResponse | LegacyPaymentDetectResponse
      >(
        response as AxiosResponse<
          | VerifyIncomingTransferResponse
          | VerifyPaymentResponse
          | LegacyPaymentDetectResponse
          | EnvelopeResponse<
              VerifyIncomingTransferResponse | VerifyPaymentResponse | LegacyPaymentDetectResponse
            >
        >,
      );
    } catch (error) {
      this.logger.warn(
        'Primary verify endpoint failed, attempting legacy detect fallback',
        error instanceof Error ? error.message : String(error),
      );

      const response = await firstValueFrom(
        this.httpService.post(
          this.buildApiUrl('internal/v1/payment/detect'),
          {
            address: request.recipientAddress,
            expectedAmount: request.expectedAmount,
            signature: request.signature,
            networkCode: request.networkCode,
          },
          {
            timeout: this.config.getTimeoutMs(),
            headers: this.getAuthHeaders(),
          },
        ),
      );
      return this.unwrapResponse<
        VerifyIncomingTransferResponse | VerifyPaymentResponse | LegacyPaymentDetectResponse
      >(
        response as AxiosResponse<
          | VerifyIncomingTransferResponse
          | VerifyPaymentResponse
          | LegacyPaymentDetectResponse
          | EnvelopeResponse<
              VerifyIncomingTransferResponse | VerifyPaymentResponse | LegacyPaymentDetectResponse
            >
        >,
      );
    }
  }

  private unwrapResponse<T>(
    response: AxiosResponse<T | EnvelopeResponse<T>>,
  ): T {
    const payload = response.data;
    if (
      payload &&
      typeof payload === 'object' &&
      'data' in payload &&
      payload.data !== undefined
    ) {
      return payload.data;
    }
    return payload as T;
  }
}
