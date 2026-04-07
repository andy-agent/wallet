import { HttpService } from '@nestjs/axios';
import { Injectable, Logger, ServiceUnavailableException } from '@nestjs/common';
import type { AxiosResponse } from 'axios';
import { firstValueFrom } from 'rxjs';
import { TronClientConfig } from './tron-client.config';
import type {
  TronAddressValidationResponse,
  TronBlockInfo,
  TronBroadcastRequest,
  TronBroadcastResponse,
  TronCapabilitiesResponse,
  TronServiceHealth,
} from './tron-client.types';

interface EnvelopeResponse<T> {
  data?: T;
}

@Injectable()
export class TronClientService {
  private readonly logger = new Logger(TronClientService.name);

  constructor(
    private readonly httpService: HttpService,
    private readonly config: TronClientConfig,
  ) {}

  isEnabled(): boolean {
    return this.config.isEnabled();
  }

  async health(): Promise<TronServiceHealth> {
    if (!this.isEnabled()) {
      return {
        status: 'healthy',
        service: 'mock-chain-usdt',
        version: 'mock-0.0.1',
        checks: {
          chain: {
            status: 'mock',
            network: 'tron-mainnet',
          },
        },
      };
    }

    try {
      const response = await firstValueFrom(
        this.httpService.get(`${this.config.getBaseUrl()}/healthz`, {
          timeout: this.config.getTimeoutMs(),
          headers: this.getAuthHeaders(),
        }),
      );

      return this.unwrapResponse<TronServiceHealth>(
        response as AxiosResponse<TronServiceHealth | EnvelopeResponse<TronServiceHealth>>,
      );
    } catch (error) {
      this.logger.error('Tron service health check failed', error);
      throw new ServiceUnavailableException({
        code: 'TRON_SERVICE_UNHEALTHY',
        message: 'TRON service is unavailable',
      });
    }
  }

  async getCapabilities(): Promise<TronCapabilitiesResponse> {
    if (!this.isEnabled()) {
      return {
        network: 'tron',
        mockMode: true,
      };
    }

    try {
      const response = await firstValueFrom(
        this.httpService.get(`${this.config.getBaseUrl()}/v1/chain/capabilities`, {
          timeout: this.config.getTimeoutMs(),
          headers: this.getAuthHeaders(),
        }),
      );

      return this.unwrapResponse<TronCapabilitiesResponse>(
        response as AxiosResponse<
          TronCapabilitiesResponse | EnvelopeResponse<TronCapabilitiesResponse>
        >,
      );
    } catch (error) {
      this.logger.error('Tron service capabilities request failed', error);
      throw new ServiceUnavailableException({
        code: 'TRON_CAPABILITIES_FAILED',
        message: 'Failed to query TRON service capabilities',
      });
    }
  }

  async getCurrentBlock(): Promise<TronBlockInfo> {
    if (!this.isEnabled()) {
      return {
        height: 0,
        hash: 'mock-tron-block',
        timestamp: new Date().toISOString(),
        txCount: 0,
      };
    }

    try {
      const response = await firstValueFrom(
        this.httpService.get(`${this.config.getBaseUrl()}/v1/chain/block/current`, {
          timeout: this.config.getTimeoutMs(),
          headers: this.getAuthHeaders(),
        }),
      );

      return this.unwrapResponse<TronBlockInfo>(
        response as AxiosResponse<TronBlockInfo | EnvelopeResponse<TronBlockInfo>>,
      );
    } catch (error) {
      this.logger.error('Tron service current block request failed', error);
      throw new ServiceUnavailableException({
        code: 'TRON_BLOCK_QUERY_FAILED',
        message: 'Failed to query TRON current block',
      });
    }
  }

  async validateAddress(address: string): Promise<TronAddressValidationResponse> {
    const normalizedAddress = address.trim();
    if (!this.isEnabled()) {
      return {
        address: normalizedAddress,
        valid: this.validateAddressFormat(normalizedAddress),
        type: this.validateAddressFormat(normalizedAddress) ? 'tron' : 'invalid',
      };
    }

    try {
      const response = await firstValueFrom(
        this.httpService.get(`${this.config.getBaseUrl()}/v1/chain/address/validate`, {
          params: { address: normalizedAddress },
          timeout: this.config.getTimeoutMs(),
          headers: this.getAuthHeaders(),
        }),
      );

      return this.unwrapResponse<TronAddressValidationResponse>(
        response as AxiosResponse<
          TronAddressValidationResponse | EnvelopeResponse<TronAddressValidationResponse>
        >,
      );
    } catch (error) {
      this.logger.error('Tron address validation failed', error);
      throw new ServiceUnavailableException({
        code: 'TRON_ADDRESS_VALIDATION_FAILED',
        message: 'Failed to validate TRON address',
      });
    }
  }

  async broadcastTransaction(
    request: TronBroadcastRequest,
  ): Promise<TronBroadcastResponse> {
    if (!this.isEnabled()) {
      return {
        success: true,
        txHash: `tron_mock_${Date.now()}`,
        acceptedAt: new Date().toISOString(),
      };
    }

    try {
      const response = await firstValueFrom(
        this.httpService.post(
          `${this.config.getBaseUrl()}/v1/chain/broadcast`,
          request,
          {
            timeout: this.config.getTimeoutMs(),
            headers: this.getAuthHeaders(),
          },
        ),
      );

      return this.unwrapResponse<TronBroadcastResponse>(
        response as AxiosResponse<
          TronBroadcastResponse | EnvelopeResponse<TronBroadcastResponse>
        >,
      );
    } catch (error) {
      this.logger.error('Tron broadcast failed', error);
      throw new ServiceUnavailableException({
        code: 'TRON_BROADCAST_FAILED',
        message: 'Failed to broadcast TRON transaction',
      });
    }
  }

  validateAddressFormat(address: string): boolean {
    return /^T[0-9a-zA-Z]{33}$/.test(address.trim());
  }

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
