import {
  BadRequestException,
  ConflictException,
  Injectable,
  Logger,
} from '@nestjs/common';
import { randomUUID } from 'crypto';
import { AuthService } from '../auth/auth.service';
import { SolanaClientService } from '../solana-client/solana-client.service';
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
          proxyBroadcastEnabled: false,
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

  transferPrecheck(accessToken: string, dto: TransferPrecheckRequestDto) {
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

    return {
      networkCode: dto.networkCode,
      assetCode: dto.assetCode,
      toAddressNormalized: dto.toAddress.trim(),
      amount: dto.amount,
      estimatedFee: dto.networkCode === 'SOLANA' ? '0.000005' : '1.000000',
      directBroadcastEnabled: chain.directBroadcastEnabled,
      proxyBroadcastEnabled: chain.proxyBroadcastEnabled,
      warnings: dto.orderNo ? ['ORDER_PAYMENT_CONTEXT'] : [],
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

      // Check if real service is enabled
      if (!this.solanaClient.isEnabled()) {
        this.logger.warn(
          'Solana service is disabled, using mock broadcast. ' +
            'Set SOLANA_SERVICE_ENABLED=true to enable real chain calls.',
        );
      }

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

      // TODO (liaojiang-rcb.11 follow-up):
      // When service is fully implemented, call:
      // const result = await this.solanaClient.broadcastTransaction({
      //   serializedTx: dto.serializedTx!, // client-signed transaction
      //   network: this.solanaClient['config'].useDevnet() ? 'devnet' : 'mainnet',
      // });

      // For now, return mock response that simulates service behavior
      return {
        networkCode: dto.networkCode,
        broadcasted: true,
        txHash: dto.clientTxHash ?? `sol_proxy_${randomUUID().slice(0, 16)}`,
        acceptedAt: new Date().toISOString(),
        serviceEnabled: this.solanaClient.isEnabled(),
        note: this.solanaClient.isEnabled()
          ? 'Real service enabled but not yet implemented'
          : 'Mock mode - set SOLANA_SERVICE_ENABLED=true for real calls',
      };
    }

    // TRON and other networks - use existing mock behavior
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
    return trimmed.startsWith('T') && trimmed.length >= 20;
  }
}
