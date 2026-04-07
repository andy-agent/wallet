import {
  BadRequestException,
  ConflictException,
  Injectable,
  Logger,
} from '@nestjs/common';
import { randomUUID } from 'crypto';
import { AuthService } from '../auth/auth.service';
import { SolanaClientService } from '../solana-client/solana-client.service';
import { TronClientService } from '../tron-client/tron-client.service';
import { ProxyBroadcastRequestDto } from './dto/proxy-broadcast.request';
import { TransferPrecheckRequestDto } from './dto/transfer-precheck.request';
import { UpsertWalletPublicAddressRequestDto } from './dto/upsert-wallet-public-address.request';

export interface WalletPublicAddressItem {
  addressId: string;
  accountId: string;
  networkCode: 'SOLANA' | 'TRON';
  assetCode: 'SOL' | 'TRX' | 'USDT';
  address: string;
  isDefault: boolean;
  createdAt: string;
}

@Injectable()
export class WalletService {
  private readonly logger = new Logger(WalletService.name);
  private readonly publicAddresses = new Map<string, WalletPublicAddressItem[]>();

  constructor(
    private readonly authService: AuthService,
    private readonly solanaClient: SolanaClientService,
    private readonly tronClient: TronClientService,
  ) {}

  getChains(accessToken: string) {
    this.authService.getMe(accessToken);
    return {
      items: [
        {
          networkCode: 'SOLANA',
          displayName: 'Solana Mainnet',
          nativeAssetCode: 'SOL',
          directBroadcastEnabled: true,
          proxyBroadcastEnabled: true,
          requiredConfirmations: 1,
          publicRpcUrl: 'https://api.mainnet-beta.solana.com',
        },
        {
          networkCode: 'TRON',
          displayName: 'TRON Mainnet',
          nativeAssetCode: 'TRX',
          directBroadcastEnabled: true,
          proxyBroadcastEnabled: true,
          requiredConfirmations: 20,
          publicRpcUrl: 'https://api.trongrid.io',
        },
      ],
    };
  }

  getAssetCatalog(accessToken: string, networkCode?: string) {
    this.authService.getMe(accessToken);
    const items = [
      {
        assetId: randomUUID(),
        networkCode: 'SOLANA',
        assetCode: 'SOL',
        displayName: 'Solana',
        symbol: 'SOL',
        decimals: 9,
        isNative: true,
        contractAddress: null,
        walletVisible: true,
        orderPayable: true,
      },
      {
        assetId: randomUUID(),
        networkCode: 'SOLANA',
        assetCode: 'USDT',
        displayName: 'Tether USD (Solana)',
        symbol: 'USDT',
        decimals: 6,
        isNative: false,
        contractAddress: '<SOLANA_USDT_CONTRACT>',
        walletVisible: true,
        orderPayable: true,
      },
      {
        assetId: randomUUID(),
        networkCode: 'TRON',
        assetCode: 'TRX',
        displayName: 'TRON',
        symbol: 'TRX',
        decimals: 6,
        isNative: true,
        contractAddress: null,
        walletVisible: true,
        orderPayable: false,
      },
      {
        assetId: randomUUID(),
        networkCode: 'TRON',
        assetCode: 'USDT',
        displayName: 'Tether USD (TRC20)',
        symbol: 'USDT',
        decimals: 6,
        isNative: false,
        contractAddress: '<TRON_USDT_CONTRACT>',
        walletVisible: true,
        orderPayable: true,
      },
    ];

    return {
      items: networkCode ? items.filter((item) => item.networkCode === networkCode) : items,
    };
  }

  upsertPublicAddress(accessToken: string, dto: UpsertWalletPublicAddressRequestDto) {
    const account = this.authService.getMe(accessToken);
    const existing = this.publicAddresses.get(account.accountId) ?? [];

    const found = existing.find(
      (item) =>
        item.networkCode === dto.networkCode &&
        item.assetCode === dto.assetCode &&
        item.address === dto.address,
    );

    if (dto.isDefault) {
      for (const item of existing) {
        if (item.networkCode === dto.networkCode && item.assetCode === dto.assetCode) {
          item.isDefault = false;
        }
      }
    }

    if (found) {
      found.isDefault = dto.isDefault;
      return found;
    }

    const created: WalletPublicAddressItem = {
      addressId: randomUUID(),
      accountId: account.accountId,
      networkCode: dto.networkCode,
      assetCode: dto.assetCode,
      address: dto.address,
      isDefault: dto.isDefault,
      createdAt: new Date().toISOString(),
    };

    existing.push(created);
    this.publicAddresses.set(account.accountId, existing);
    return created;
  }

  listPublicAddresses(
    accessToken: string,
    networkCode?: string,
    assetCode?: string,
  ) {
    const account = this.authService.getMe(accessToken);
    let items = this.publicAddresses.get(account.accountId) ?? [];
    if (networkCode) {
      items = items.filter((item) => item.networkCode === networkCode);
    }
    if (assetCode) {
      items = items.filter((item) => item.assetCode === assetCode);
    }
    return { items };
  }

  async transferPrecheck(accessToken: string, dto: TransferPrecheckRequestDto) {
    this.authService.getMe(accessToken);
    if (!this.isAddressValid(dto.networkCode, dto.toAddress)) {
      throw new BadRequestException({
        code: 'WALLET_INVALID_ADDRESS',
        message: 'Wallet address invalid',
      });
    }

    const chain = this.getChains(accessToken).items.find(
      (item) => item.networkCode === dto.networkCode,
    );
    const asset = this.getAssetCatalog(accessToken, dto.networkCode).items.find(
      (item) => item.assetCode === dto.assetCode,
    );

    if (!chain || !asset) {
      throw new BadRequestException({
        code: 'WALLET_UNSUPPORTED_ASSET',
        message: 'Unsupported asset',
      });
    }

    // For SOLANA network, use remote service when enabled
    if (dto.networkCode === 'SOLANA') {
      try {
        const mint = dto.assetCode === 'USDT'
          ? this.solanaClient.getUsdtMint()
          : null;

        const precheckResult = await this.solanaClient.precheckTransfer({
          network: this.solanaClient['config'].useDevnet() ? 'devnet' : 'mainnet',
          mint,
          toAddress: dto.toAddress,
          amount: dto.amount,
        });

        if (!precheckResult.valid) {
          throw new BadRequestException({
            code: precheckResult.errorCode ?? 'WALLET_TRANSFER_INVALID',
            message: precheckResult.errorMessage ?? 'Transfer precheck failed',
          });
        }

        return {
          networkCode: dto.networkCode,
          assetCode: dto.assetCode,
          toAddressNormalized: precheckResult.toAddressNormalized,
          amount: dto.amount,
          estimatedFee: precheckResult.estimatedFee,
          directBroadcastEnabled: chain.directBroadcastEnabled,
          proxyBroadcastEnabled: chain.proxyBroadcastEnabled,
          warnings: dto.orderNo ? ['ORDER_PAYMENT_CONTEXT'] : [],
          serviceEnabled: this.solanaClient.isEnabled(),
        };
      } catch (error) {
        // If it's already a BadRequestException, re-throw it
        if (error instanceof BadRequestException) {
          throw error;
        }
        // Log error and fall back to default behavior (graceful degradation)
        this.logger.warn(
          'Solana precheck service failed, falling back to default behavior',
          error,
        );
      }
    }

    if (dto.networkCode === 'TRON') {
      try {
        if (this.tronClient.isEnabled()) {
          const addressValidation = await this.tronClient.validateAddress(dto.toAddress);
          if (!addressValidation.valid) {
            throw new BadRequestException({
              code: 'WALLET_INVALID_ADDRESS',
              message: 'Wallet address invalid',
            });
          }

          return {
            networkCode: dto.networkCode,
            assetCode: dto.assetCode,
            toAddressNormalized: addressValidation.address.trim(),
            amount: dto.amount,
            estimatedFee: '1.000000',
            directBroadcastEnabled: chain.directBroadcastEnabled,
            proxyBroadcastEnabled: chain.proxyBroadcastEnabled,
            warnings: dto.orderNo ? ['ORDER_PAYMENT_CONTEXT'] : [],
            serviceEnabled: true,
          };
        }
      } catch (error) {
        if (error instanceof BadRequestException) {
          throw error;
        }
        this.logger.warn(
          'Tron precheck service failed, falling back to default behavior',
          error,
        );
      }
    }

    return {
      networkCode: dto.networkCode,
      assetCode: dto.assetCode,
      toAddressNormalized: dto.toAddress.trim(),
      amount: dto.amount,
      estimatedFee: dto.networkCode === 'SOLANA' ? '0.000005' : '1.000000',
      directBroadcastEnabled: chain.directBroadcastEnabled,
      proxyBroadcastEnabled: chain.proxyBroadcastEnabled,
      warnings: dto.orderNo ? ['ORDER_PAYMENT_CONTEXT'] : [],
      serviceEnabled:
        dto.networkCode === 'SOLANA'
          ? this.solanaClient.isEnabled()
          : this.tronClient.isEnabled(),
    };
  }

  async proxyBroadcast(accessToken: string, dto: ProxyBroadcastRequestDto) {
    this.authService.getMe(accessToken);
    const chain = this.getChains(accessToken).items.find(
      (item) => item.networkCode === dto.networkCode,
    );

    if (!chain?.proxyBroadcastEnabled) {
      throw new ConflictException({
        code: 'WALLET_PROXY_BROADCAST_DISABLED',
        message: 'Proxy broadcast disabled',
      });
    }

    // Use SolanaClientService for SOLANA network
    if (dto.networkCode === 'SOLANA') {
      this.logger.debug('Using SolanaClientService for proxy broadcast');

      // Validate address using SolanaClientService
      if (
        dto.toAddress &&
        !this.solanaClient.validateAddress(dto.toAddress)
      ) {
        throw new BadRequestException({
          code: 'WALLET_INVALID_ADDRESS',
          message: 'Invalid Solana address format',
        });
      }

      // Check if real service is enabled and has serializedTx for broadcast
      if (this.solanaClient.isEnabled() && dto.serializedTx) {
        try {
          this.logger.debug('Calling real Solana service for broadcast');
          const result = await this.solanaClient.broadcastTransaction({
            serializedTx: dto.serializedTx,
            network: this.solanaClient['config'].useDevnet() ? 'devnet' : 'mainnet',
          });

          return {
            networkCode: dto.networkCode,
            broadcasted: result.confirmed ?? true,
            txHash: result.signature,
            acceptedAt: new Date().toISOString(),
            serviceEnabled: true,
          };
        } catch (error) {
          this.logger.error('Solana broadcast failed, falling back to mock', error);
          // Fall through to mock behavior (graceful degradation)
        }
      }

      // Service disabled or unavailable - use mock behavior with proper logging
      if (!this.solanaClient.isEnabled()) {
        this.logger.warn(
          'Solana service is disabled, using mock broadcast. ' +
            'Set SOLANA_SERVICE_ENABLED=true to enable real chain calls.',
        );
      } else if (!dto.serializedTx) {
        this.logger.warn(
          'serializedTx not provided, using mock broadcast. ' +
            'Provide serializedTx for real chain broadcast.',
        );
      }

      return {
        networkCode: dto.networkCode,
        broadcasted: true,
        txHash: dto.clientTxHash ?? `sol_proxy_${randomUUID().slice(0, 16)}`,
        acceptedAt: new Date().toISOString(),
        serviceEnabled: this.solanaClient.isEnabled(),
        note: !this.solanaClient.isEnabled()
          ? 'Mock mode - set SOLANA_SERVICE_ENABLED=true for real calls'
          : !dto.serializedTx
            ? 'Mock mode - provide serializedTx for real broadcast'
            : 'Mock mode - service unavailable',
      };
    }

    if (dto.networkCode === 'TRON') {
      if (
        dto.toAddress &&
        !this.isTronAddressValid(dto.toAddress)
      ) {
        throw new BadRequestException({
          code: 'WALLET_INVALID_ADDRESS',
          message: 'Invalid TRON address format',
        });
      }

      if (this.tronClient.isEnabled()) {
        try {
          if (dto.toAddress) {
            const addressValidation = await this.tronClient.validateAddress(dto.toAddress);
            if (!addressValidation.valid) {
              throw new BadRequestException({
                code: 'WALLET_INVALID_ADDRESS',
                message: 'Invalid TRON address format',
              });
            }
          }

          const result = await this.tronClient.broadcastTransaction({
            signedTx: dto.serializedTx ?? dto.signedPayload,
          });

          if (result.success) {
            return {
              networkCode: dto.networkCode,
              broadcasted: true,
              txHash:
                result.txHash ?? dto.clientTxHash ?? `tron_proxy_${randomUUID().slice(0, 16)}`,
              acceptedAt: result.acceptedAt ?? new Date().toISOString(),
              serviceEnabled: true,
            };
          }

          this.logger.warn(
            'Tron broadcast rejected by remote service, falling back to mock behavior',
            result,
          );
        } catch (error) {
          if (error instanceof BadRequestException) {
            throw error;
          }
          this.logger.warn('Tron broadcast failed, falling back to mock behavior', error);
        }
      }

      return {
        networkCode: dto.networkCode,
        broadcasted: true,
        txHash: dto.clientTxHash ?? `tron_proxy_${randomUUID()}`,
        acceptedAt: new Date().toISOString(),
        serviceEnabled: false,
        note: this.tronClient.isEnabled()
          ? 'Mock mode - TRON service unavailable'
          : 'Mock mode - set TRON_SERVICE_ENABLED=true for real calls',
      };
    }

    return {
      networkCode: dto.networkCode,
      broadcasted: true,
      txHash: dto.clientTxHash ?? `proxy_${randomUUID()}`,
      acceptedAt: new Date().toISOString(),
    };
  }

  private isAddressValid(networkCode: 'SOLANA' | 'TRON', address: string) {
    const trimmed = address.trim();
    if (networkCode === 'SOLANA') {
      // Use SolanaClientService for validation if available
      return this.solanaClient.validateAddress(trimmed);
    }
    return this.isTronAddressValid(trimmed);
  }

  private isTronAddressValid(address: string) {
    return /^T[0-9a-zA-Z]{33}$/.test(address.trim());
  }
}
