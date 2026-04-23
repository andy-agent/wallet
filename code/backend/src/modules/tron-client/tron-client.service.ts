import { HttpService } from '@nestjs/axios';
import { Injectable, Logger, ServiceUnavailableException } from '@nestjs/common';
import type { AxiosResponse } from 'axios';
import { firstValueFrom } from 'rxjs';
import type { VerifyIncomingTransferResponse } from '../solana-client/solana-client.types';
import { TronClientConfig } from './tron-client.config';
import type {
  TronAddressValidationResponse,
  TronBlockInfo,
  TronBroadcastRequest,
  TronBroadcastResponse,
  TronCapabilitiesResponse,
  TronServiceHealth,
  TronTransactionEnvelope,
  TronTransactionQueryResponse,
  VerifyIncomingTronTransferRequest,
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

  getPreferredFullNodeUrl(): string {
    return this.config.getPreferredFullNodeUrl();
  }

  getOrderedFullNodeUrls(): string[] {
    return this.config.getOrderedFullNodeUrls();
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

  async getTransaction(
    txHash: string,
  ): Promise<TronTransactionQueryResponse | null> {
    if (!this.isEnabled()) {
      return null;
    }

    try {
      const response = await firstValueFrom(
        this.httpService.get(`${this.config.getBaseUrl()}/v1/chain/tx/${txHash}`, {
          timeout: this.config.getTimeoutMs(),
          headers: this.getAuthHeaders(),
        }),
      );

      const payload = this.unwrapResponse<TronTransactionEnvelope>(
        response as AxiosResponse<
          TronTransactionEnvelope | EnvelopeResponse<TronTransactionEnvelope>
        >,
      );
      return payload.transaction ?? null;
    } catch (error) {
      this.logger.error('Tron transaction query failed', error);
      throw new ServiceUnavailableException({
        code: 'TRON_TRANSACTION_QUERY_FAILED',
        message: 'Failed to query TRON transaction',
      });
    }
  }

  async verifyIncomingTransfer(
    request: VerifyIncomingTronTransferRequest,
  ): Promise<VerifyIncomingTransferResponse> {
    const transaction = await this.getTransaction(request.txHash);
    if (!transaction || transaction.status === 'not_found' || transaction.status === 'pending') {
      return {
        signature: request.txHash,
        status: 'pending',
        confirmations: transaction?.confirmations ?? 0,
        verified: false,
        recipientAddress: transaction?.to || request.recipientAddress,
        assetCode: transaction?.token || undefined,
        amount: transaction?.amount,
      };
    }

    if (transaction.status === 'failed') {
      return {
        signature: request.txHash,
        status: 'failed',
        confirmations: transaction.confirmations,
        verified: false,
        recipientAddress: transaction.to,
        assetCode: transaction.token,
        amount: transaction.amount,
        error: 'Transaction failed on TRON chain',
      };
    }

    const expectedRecipient = request.recipientAddress.trim();
    const observedRecipient = transaction.to.trim();
    const recipientMatched = observedRecipient === expectedRecipient;
    const assetMatched = transaction.token.toUpperCase() === request.assetCode.trim().toUpperCase();
    const decimals = request.assetDecimals ?? (transaction.token === 'TRX' ? 6 : 6);
    const expectedMinor = this.toMinorUnits(request.expectedAmount, decimals);
    const observedMinor = this.toMinorUnits(transaction.amount, decimals);
    const amountSatisfied = observedMinor === expectedMinor;

    let mismatchCode: VerifyIncomingTransferResponse['mismatchCode'];
    if (!recipientMatched) {
      mismatchCode = 'RECIPIENT_MISMATCH';
    } else if (!assetMatched) {
      mismatchCode = 'ASSET_MISMATCH';
    } else if (observedMinor > expectedMinor) {
      mismatchCode = 'AMOUNT_OVER';
    } else if (observedMinor < expectedMinor) {
      mismatchCode = 'AMOUNT_UNDER';
    }

    return {
      signature: request.txHash,
      status: 'confirmed',
      confirmations: transaction.confirmations,
      verified: !mismatchCode,
      recipientAddress: observedRecipient,
      assetCode: assetMatched ? transaction.token : undefined,
      amount: transaction.amount,
      mismatchCode,
      failureReason:
        mismatchCode === 'RECIPIENT_MISMATCH'
          ? 'Submitted transaction recipient does not match the configured collection address'
          : mismatchCode === 'ASSET_MISMATCH'
            ? 'Submitted transaction asset does not match the expected payment asset'
            : mismatchCode === 'AMOUNT_OVER'
              ? 'Submitted transaction amount exceeds the expected payment target'
              : mismatchCode === 'AMOUNT_UNDER'
                ? 'Submitted transaction amount is below the expected payment target'
                : undefined,
    };
  }

  validateAddressFormat(address: string): boolean {
    return /^T[0-9a-zA-Z]{33}$/.test(address.trim());
  }

  private toMinorUnits(amount: string, decimals: number) {
    const [wholePart, fractionPart = ''] = amount.trim().split('.');
    const whole = wholePart === '' ? '0' : wholePart;
    const fraction = fractionPart.padEnd(decimals, '0').slice(0, decimals);
    return BigInt(`${whole}${fraction}`);
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
