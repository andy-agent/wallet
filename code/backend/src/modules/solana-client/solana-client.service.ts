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
  SolanaServiceHealth,
} from './solana-client.types';

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
      const baseUrl = this.config.getBaseUrl();
      const response = await firstValueFrom(
        this.httpService.get(`${baseUrl}/health`, {
          timeout: this.config.getTimeoutMs(),
          headers: this.getAuthHeaders(),
        }),
      );
      return response.data as SolanaServiceHealth;
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
      const baseUrl = this.config.getBaseUrl();
      const network = this.getEffectiveNetwork(request.network);

      const response = await firstValueFrom(
        this.httpService.post(
          `${baseUrl}/v1/transactions/broadcast`,
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

      return response.data as BroadcastTransactionResponse;
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
      const baseUrl = this.config.getBaseUrl();
      const network = this.getEffectiveNetwork(request.network);

      const response = await firstValueFrom(
        this.httpService.get(`${baseUrl}/v1/transactions/${request.signature}`, {
          params: { network },
          timeout: this.config.getTimeoutMs(),
          headers: this.getAuthHeaders(),
        }),
      );

      return response.data as GetTransactionStatusResponse;
    } catch (error) {
      this.logger.error('Get transaction status failed', error);
      throw new ServiceUnavailableException({
        code: 'SOLANA_STATUS_CHECK_FAILED',
        message: 'Failed to get transaction status',
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
      const baseUrl = this.config.getBaseUrl();
      const network = this.getEffectiveNetwork(request.network);

      const response = await firstValueFrom(
        this.httpService.get(`${baseUrl}/v1/balances/${request.address}`, {
          params: {
            network,
            mint: request.mint,
          },
          timeout: this.config.getTimeoutMs(),
          headers: this.getAuthHeaders(),
        }),
      );

      return response.data as GetBalanceResponse;
    } catch (error) {
      this.logger.error('Get balance failed', error);
      throw new ServiceUnavailableException({
        code: 'SOLANA_BALANCE_CHECK_FAILED',
        message: 'Failed to get balance',
      });
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
      return { 'X-API-Key': apiKey };
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
}
