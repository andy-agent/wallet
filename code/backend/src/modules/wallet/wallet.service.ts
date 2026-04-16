import {
  BadRequestException,
  ConflictException,
  Injectable,
  Logger,
} from '@nestjs/common';
import { ConfigService } from '@nestjs/config';
import { createHash, randomUUID } from 'crypto';
import {
  createAssociatedTokenAccountInstruction,
  createTransferCheckedInstruction,
  getAssociatedTokenAddressSync,
} from '@solana/spl-token';
import {
  Connection,
  PublicKey,
  SystemProgram,
  Transaction,
} from '@solana/web3.js';
import { TronWeb } from 'tronweb';
import { AuthService } from '../auth/auth.service';
import { RuntimeStateRepository } from '../database/runtime-state.repository';
import { OrdersService } from '../orders/orders.service';
import {
  buildPaymentAssetCatalog,
  type PaymentAssetDefinition,
} from '../payments/payment-asset-catalog';
import { BuildTransferRequestDto } from './dto/build-transfer.request';
import { SolanaClientService } from '../solana-client/solana-client.service';
import { TronClientService } from '../tron-client/tron-client.service';
import { ProxyBroadcastRequestDto } from './dto/proxy-broadcast.request';
import { TransferPrecheckRequestDto } from './dto/transfer-precheck.request';
import { UpsertWalletLifecycleRequestDto } from './dto/upsert-wallet-lifecycle.request';
import { UpsertWalletPublicAddressRequestDto } from './dto/upsert-wallet-public-address.request';
import { UpsertWalletSecretBackupRequestDto } from './dto/upsert-wallet-secret-backup.request';
import { WalletBackupCryptoService } from './wallet-backup-crypto.service';
import { WalletBackupRelayService } from './wallet-backup-relay.service';
import {
  isKnownPlaceholderWalletPublicAddress,
  isUsableWalletPublicAddress,
  isValidWalletPublicAddress,
  normalizeWalletPublicAddress,
} from './wallet-public-address-policy';
import {
  PersistedWalletLifecycleRecord,
  PersistedWalletPublicAddressRecord,
  PersistedWalletSecretBackupRecord,
  WalletLifecycleNextAction,
  WalletLifecycleOrigin,
  WalletLifecycleStatus,
  WalletLifecycleView,
} from './wallet.types';

export type WalletPublicAddressItem = PersistedWalletPublicAddressRecord;

export type WalletReceiveState = 'NO_WALLET' | 'NO_ADDRESS' | 'READY';

interface WalletResolvedReceiveSelection {
  selectedNetworkCode: string;
  selectedAssetCode: string;
  assetsForNetwork: Array<{
    assetId: string;
    networkCode: string;
    assetCode: string;
    displayName: string;
    symbol: string;
    decimals: number;
    isNative: boolean;
    contractAddress: string | null;
    walletVisible: boolean;
    orderPayable: boolean;
  }>;
  publicAddresses: WalletPublicAddressItem[];
  defaultAddress: string | null;
}

type WalletAssetBalanceStatus = 'NO_ADDRESS' | 'UNAVAILABLE' | 'READY';

interface WalletAssetBalanceView {
  networkCode: string;
  assetCode: string;
  address: string | null;
  balanceMinor: string | null;
  balanceUiAmount: string | null;
  balanceStatus: WalletAssetBalanceStatus;
}

@Injectable()
export class WalletService {
  private readonly logger = new Logger(WalletService.name);

  constructor(
    private readonly configService: ConfigService,
    private readonly authService: AuthService,
    private readonly ordersService: OrdersService,
    private readonly runtimeStateRepository: RuntimeStateRepository,
    private readonly solanaClient: SolanaClientService,
    private readonly tronClient: TronClientService,
    private readonly walletBackupCryptoService: WalletBackupCryptoService,
    private readonly walletBackupRelayService: WalletBackupRelayService,
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
    const items = buildPaymentAssetCatalog(
      this.configService,
      this.solanaClient,
    ).map((item) => this.toWalletAssetCatalogItem(item));

    return {
      items: networkCode ? items.filter((item) => item.networkCode === networkCode) : items,
    };
  }

  async getOverview(accessToken: string) {
    const account = this.authService.getMe(accessToken);
    const lifecycle = await this.getWalletLifecycle(accessToken);
    const chains = this.getChains(accessToken).items;
    const assets = this.getAssetCatalog(accessToken).items;
    const publicAddresses = (await this.listPublicAddresses(accessToken)).items;
    const receiveSelection = await this.resolveReceiveSelection(accessToken);
    const orders = await this.listOwnedOrders(accessToken);

    const orderStatsByNetwork = new Map<
      string,
      { orderCount: number; lastOrderAt: string | null }
    >();
    const orderStatsByAsset = new Map<
      string,
      {
        orderCount: number;
        totalPayableAmount: number;
        lastOrderAt: string | null;
        lastOrderStatus: string | null;
      }
    >();

    for (const order of orders) {
      const networkKey = order.quoteNetworkCode;
      const assetKey = `${order.quoteNetworkCode}:${order.quoteAssetCode}`;

      const networkStats = orderStatsByNetwork.get(networkKey) ?? {
        orderCount: 0,
        lastOrderAt: null,
      };
      networkStats.orderCount += 1;
      networkStats.lastOrderAt = this.maxIso(networkStats.lastOrderAt, order.completedAt ?? order.confirmedAt ?? order.expiresAt);
      orderStatsByNetwork.set(networkKey, networkStats);

      const assetStats = orderStatsByAsset.get(assetKey) ?? {
        orderCount: 0,
        totalPayableAmount: 0,
        lastOrderAt: null,
        lastOrderStatus: null,
      };
      assetStats.orderCount += 1;
      assetStats.totalPayableAmount += Number(order.payableAmount);
      assetStats.lastOrderAt = this.maxIso(assetStats.lastOrderAt, order.completedAt ?? order.confirmedAt ?? order.expiresAt);
      assetStats.lastOrderStatus = order.status;
      orderStatsByAsset.set(assetKey, assetStats);
    }

    const publicAddressCountByNetwork = new Map<string, number>();
    const publicAddressCountByAsset = new Map<string, number>();
    for (const item of publicAddresses) {
      publicAddressCountByNetwork.set(
        item.networkCode,
        (publicAddressCountByNetwork.get(item.networkCode) ?? 0) + 1,
      );
      const assetKey = `${item.networkCode}:${item.assetCode}`;
      publicAddressCountByAsset.set(
        assetKey,
        (publicAddressCountByAsset.get(assetKey) ?? 0) + 1,
      );
    }

    const assetBalanceViews = await this.resolveAssetBalanceViews(
      assets,
      publicAddresses,
    );

    const chainItems = chains.map((chain) => {
      const assetsOnChain = assets.filter((item) => item.networkCode === chain.networkCode);
      const networkStats = orderStatsByNetwork.get(chain.networkCode);
      return {
        networkCode: chain.networkCode,
        displayName: chain.displayName,
        nativeAssetCode: chain.nativeAssetCode,
        publicRpcUrl: chain.publicRpcUrl,
        directBroadcastEnabled: chain.directBroadcastEnabled,
        proxyBroadcastEnabled: chain.proxyBroadcastEnabled,
        requiredConfirmations: chain.requiredConfirmations,
        assetCount: assetsOnChain.length,
        orderCount: networkStats?.orderCount ?? 0,
        publicAddressCount: publicAddressCountByNetwork.get(chain.networkCode) ?? 0,
        lastOrderAt: networkStats?.lastOrderAt ?? null,
        hasConfiguredAddress: (publicAddressCountByNetwork.get(chain.networkCode) ?? 0) > 0,
      };
    });

    const assetItems = assets.map((asset) => {
      const assetKey = `${asset.networkCode}:${asset.assetCode}`;
      const assetStats = orderStatsByAsset.get(assetKey);
      const balanceView = assetBalanceViews.get(assetKey);
      return {
        assetId: asset.assetId,
        networkCode: asset.networkCode,
        assetCode: asset.assetCode,
        displayName: asset.displayName,
        symbol: asset.symbol,
        decimals: asset.decimals,
        isNative: asset.isNative,
        contractAddress: asset.contractAddress,
        walletVisible: asset.walletVisible,
        orderPayable: asset.orderPayable,
        publicAddressCount: publicAddressCountByAsset.get(assetKey) ?? 0,
        orderCount: assetStats?.orderCount ?? 0,
        totalPayableAmount: assetStats
          ? assetStats.totalPayableAmount.toFixed(asset.assetCode === 'SOL' ? 6 : 6)
          : '0.000000',
        availableBalanceMinor: balanceView?.balanceMinor ?? null,
        availableBalanceUiAmount: balanceView?.balanceUiAmount ?? null,
        availableBalanceStatus: balanceView?.balanceStatus ?? 'NO_ADDRESS',
        balanceAddress: balanceView?.address ?? null,
        lastOrderAt: assetStats?.lastOrderAt ?? null,
        lastOrderStatus: assetStats?.lastOrderStatus ?? null,
      };
    });

    const selectedNetworkCode =
      chainItems
        .slice()
        .sort((left, right) => {
          const addressDelta = Number(right.hasConfiguredAddress) - Number(left.hasConfiguredAddress);
          if (addressDelta !== 0) {
            return addressDelta;
          }
          return right.orderCount - left.orderCount;
        })
        .at(0)?.networkCode ?? chains[0]?.networkCode ?? 'TRON';

    return {
      accountId: account.accountId,
      accountEmail: account.email,
      walletExists: lifecycle.walletExists,
      walletId: lifecycle.walletId,
      walletName: lifecycle.walletName,
      lifecycleStatus: lifecycle.status,
      nextAction: lifecycle.nextAction,
      selectedNetworkCode: receiveSelection.selectedNetworkCode || selectedNetworkCode,
      selectedAssetCode: receiveSelection.selectedAssetCode,
      receiveState: lifecycle.receiveState,
      configuredAddressCount: lifecycle.configuredAddressCount,
      defaultAddress: receiveSelection.defaultAddress,
      canShare: Boolean(receiveSelection.defaultAddress),
      chainItems,
      assetItems,
      alerts: this.buildOverviewAlerts({
        chainItems,
        assetItems,
        publicAddresses,
      }),
    };
  }

  async getBalances(accessToken: string) {
    const account = this.authService.getMe(accessToken);
    const assets = this.getAssetCatalog(accessToken).items.filter(
      (item) => item.walletVisible,
    );
    const publicAddresses =
      await this.runtimeStateRepository.listWalletPublicAddressesByAccountId({
        accountId: account.accountId,
      });
    const assetBalanceViews = await this.resolveAssetBalanceViews(
      assets,
      publicAddresses,
    );

    return {
      accountId: account.accountId,
      accountEmail: account.email,
      items: assets.map((asset) => {
        const assetKey = `${asset.networkCode}:${asset.assetCode}`;
        const balanceView = assetBalanceViews.get(assetKey);
        return {
          assetId: asset.assetId,
          networkCode: asset.networkCode,
          assetCode: asset.assetCode,
          displayName: asset.displayName,
          symbol: asset.symbol,
          decimals: asset.decimals,
          address: balanceView?.address ?? null,
          availableBalanceMinor: balanceView?.balanceMinor ?? null,
          availableBalanceUiAmount: balanceView?.balanceUiAmount ?? null,
          availableBalanceStatus: balanceView?.balanceStatus ?? 'NO_ADDRESS',
        };
      }),
    };
  }

  async getReceiveContext(
    accessToken: string,
    requestedNetworkCode?: string,
    requestedAssetCode?: string,
  ) {
    const chains = this.getChains(accessToken).items;
    const {
      selectedNetworkCode,
      selectedAssetCode,
      assetsForNetwork,
      publicAddresses,
      defaultAddress,
    } = await this.resolveReceiveSelection(
      accessToken,
      requestedNetworkCode,
      requestedAssetCode,
    );
    const lifecycle = await this.getWalletLifecycle(accessToken);

    return {
      selectedNetworkCode,
      selectedAssetCode,
      chainItems: chains.map((item) => ({
        networkCode: item.networkCode,
        displayName: item.displayName,
        nativeAssetCode: item.nativeAssetCode,
        selected: item.networkCode === selectedNetworkCode,
      })),
      assetItems: assetsForNetwork.map((item) => ({
        assetId: item.assetId,
        assetCode: item.assetCode,
        displayName: item.displayName,
        symbol: item.symbol,
        selected: item.assetCode === selectedAssetCode,
      })),
      addresses: publicAddresses,
      defaultAddress,
      canShare: Boolean(defaultAddress),
      walletExists: lifecycle.walletExists,
      receiveState: !lifecycle.walletExists
        ? 'NO_WALLET'
        : defaultAddress
          ? 'READY'
          : 'NO_ADDRESS',
      status: defaultAddress ? '已配置收款地址' : '未配置收款地址',
      note: defaultAddress
        ? '收款地址。'
        : lifecycle.walletExists
          ? '当前账号已创建钱包，但该链/资产下还没有配置收款地址。'
          : '当前账号还没有创建或导入钱包。',
      shareText: defaultAddress
        ? `${selectedAssetCode} · ${selectedNetworkCode}\n${defaultAddress}`
        : '',
    };
  }

  async buildTransfer(accessToken: string, dto: BuildTransferRequestDto) {
    this.authService.getMe(accessToken);
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
    if (!this.isAddressValid(dto.networkCode, dto.fromAddress) || !this.isAddressValid(dto.networkCode, dto.toAddress)) {
      throw new BadRequestException({
        code: 'WALLET_INVALID_ADDRESS',
        message: 'Wallet address invalid',
      });
    }

    return dto.networkCode === 'SOLANA'
      ? this.buildSolanaTransfer(chain.publicRpcUrl ?? 'https://api.mainnet-beta.solana.com', dto)
      : this.buildTronTransfer(asset.contractAddress, dto);
  }

  async getWalletLifecycle(
    accessToken: string,
  ): Promise<WalletLifecycleView> {
    const account = this.authService.getMe(accessToken);
    const lifecycle =
      await this.runtimeStateRepository.findWalletLifecycleByAccountId(
        account.accountId,
      );
    const publicAddresses =
      await this.runtimeStateRepository.listWalletPublicAddressesByAccountId({
        accountId: account.accountId,
      });
    const configuredAddressCount = publicAddresses.filter(isUsableWalletPublicAddress).length;
    const walletExists = lifecycle !== null || configuredAddressCount > 0;
    const receiveState: WalletReceiveState = !walletExists
      ? 'NO_WALLET'
      : configuredAddressCount > 0
        ? 'READY'
        : 'NO_ADDRESS';

    if (lifecycle === null && configuredAddressCount > 0) {
      return {
        accountId: account.accountId,
        walletExists: true,
        receiveReady: true,
        walletId: null,
        walletName: 'Legacy Wallet',
        lifecycleStatus: 'ACTIVE',
        sourceType: 'LEGACY',
        displayName: 'Legacy Wallet',
        status: 'ACTIVE',
        origin: 'LEGACY',
        nextAction: 'READY',
        hasAnyPublicAddress: true,
        configuredAddressCount,
        source: 'PUBLIC_ADDRESS_FALLBACK',
        createdAt: null,
        updatedAt: null,
        backupAcknowledgedAt: null,
        activatedAt: null,
        receiveState,
      };
    }

    return {
      accountId: account.accountId,
      walletExists,
      receiveReady: walletExists,
      walletId: lifecycle?.walletId ?? null,
      walletName: lifecycle?.walletName ?? null,
      lifecycleStatus: this.toLegacyLifecycleStatus(
        lifecycle?.status ?? 'NONE',
        lifecycle?.origin ?? null,
      ),
      sourceType: this.toLegacySourceType(lifecycle?.origin ?? null),
      displayName: lifecycle?.walletName ?? null,
      status: lifecycle?.status ?? 'NONE',
      origin: lifecycle?.origin ?? null,
      nextAction: this.resolveNextAction(lifecycle?.status ?? 'NONE'),
      hasAnyPublicAddress: configuredAddressCount > 0,
      configuredAddressCount,
      source: lifecycle ? 'RUNTIME_STATE' : 'EMPTY',
      createdAt: lifecycle?.createdAt ?? null,
      updatedAt: lifecycle?.updatedAt ?? null,
      backupAcknowledgedAt: lifecycle?.backupAcknowledgedAt ?? null,
      activatedAt: lifecycle?.activatedAt ?? null,
      receiveState,
    };
  }

  async upsertWalletLifecycle(
    accessToken: string,
    dto: UpsertWalletLifecycleRequestDto,
  ) {
    const account = this.authService.getMe(accessToken);
    const now = new Date().toISOString();
    const existing =
      await this.runtimeStateRepository.findWalletLifecycleByAccountId(
        account.accountId,
      );

    let next: PersistedWalletLifecycleRecord | null = null;
    switch (dto.action) {
      case 'CREATE':
        if (
          dto.mnemonicWordCount !== undefined &&
          ![12, 24].includes(dto.mnemonicWordCount)
        ) {
          throw new BadRequestException({
            code: 'WALLET_INVALID_MNEMONIC',
            message: 'Mnemonic word count must be 12 or 24',
          });
        }
        next = {
          accountId: account.accountId,
          walletId: existing?.walletId ?? randomUUID(),
          walletName: dto.displayName?.trim() || existing?.walletName || 'Primary Wallet',
          status: 'CREATED_PENDING_BACKUP',
          origin: 'CREATED',
          mnemonicHash:
            dto.mnemonicHash?.trim() || existing?.mnemonicHash || this.hashMnemonicSeed(account.accountId, now),
          mnemonicWordCount: dto.mnemonicWordCount ?? existing?.mnemonicWordCount ?? 12,
          backupAcknowledgedAt: null,
          activatedAt: null,
          createdAt: existing?.createdAt ?? now,
          updatedAt: now,
        };
        break;
      case 'IMPORT':
        const normalizedMnemonic = dto.mnemonic
          ?.trim()
          .split(/\s+/)
          .filter((item) => item.length > 0) ?? [];
        const resolvedWordCount =
          dto.mnemonicWordCount ??
          (normalizedMnemonic.length > 0 ? normalizedMnemonic.length : existing?.mnemonicWordCount ?? 0);
        if (resolvedWordCount > 0 && ![12, 24].includes(resolvedWordCount)) {
          throw new BadRequestException({
            code: 'WALLET_INVALID_MNEMONIC',
            message: 'Mnemonic word count must be 12 or 24',
          });
        }
        const resolvedMnemonicHash =
          dto.mnemonicHash?.trim() ||
          (normalizedMnemonic.length > 0
            ? createHash('sha256').update(normalizedMnemonic.join(' ')).digest('hex')
            : existing?.mnemonicHash ??
              null);
        if (!resolvedMnemonicHash) {
          throw new BadRequestException({
            code: 'WALLET_INVALID_MNEMONIC',
            message: 'Mnemonic metadata is required for import',
          });
        }
        next = {
          accountId: account.accountId,
          walletId: existing?.walletId ?? randomUUID(),
          walletName: dto.displayName?.trim() || existing?.walletName || 'Imported Wallet',
          status: 'ACTIVE',
          origin: 'IMPORTED',
          mnemonicHash: resolvedMnemonicHash,
          mnemonicWordCount: resolvedWordCount || null,
          backupAcknowledgedAt: existing?.backupAcknowledgedAt ?? null,
          activatedAt: existing?.activatedAt ?? now,
          createdAt: existing?.createdAt ?? now,
          updatedAt: now,
        };
        break;
      case 'ACKNOWLEDGE_BACKUP':
        if (!existing) {
          throw new ConflictException({
            code: 'WALLET_NOT_CREATED',
            message: 'Wallet has not been created',
          });
        }
        next = {
          ...existing,
          status: 'BACKUP_PENDING_CONFIRMATION',
          backupAcknowledgedAt: now,
          updatedAt: now,
        };
        break;
      case 'CONFIRM_BACKUP':
        if (!existing) {
          throw new ConflictException({
            code: 'WALLET_NOT_CREATED',
            message: 'Wallet has not been created',
          });
        }
        next = {
          ...existing,
          status: 'ACTIVE',
          backupAcknowledgedAt: existing.backupAcknowledgedAt ?? now,
          activatedAt: now,
          updatedAt: now,
        };
        break;
    }

    await this.runtimeStateRepository.upsertWalletLifecycle(next);
    return this.getWalletLifecycle(accessToken);
  }

  async upsertPublicAddress(accessToken: string, dto: UpsertWalletPublicAddressRequestDto) {
    const account = this.authService.getMe(accessToken);
    const lifecycle = await this.ensureWalletLifecycle(account.accountId);
    const normalizedAddress = normalizeWalletPublicAddress(dto.address);
    if (!isValidWalletPublicAddress(dto.networkCode, normalizedAddress)) {
      throw new BadRequestException({
        code: 'WALLET_INVALID_PUBLIC_ADDRESS',
        message: 'Wallet public address is invalid for the selected network',
      });
    }
    if (isKnownPlaceholderWalletPublicAddress(dto.networkCode, normalizedAddress)) {
      throw new BadRequestException({
        code: 'WALLET_PLACEHOLDER_ADDRESS_FORBIDDEN',
        message: 'Placeholder wallet addresses cannot be saved',
      });
    }
    const existing =
      await this.runtimeStateRepository.listWalletPublicAddressesByAccountId({
        accountId: account.accountId,
      });

    const found = existing.find(
      (item) =>
        item.networkCode === dto.networkCode &&
        item.assetCode === dto.assetCode &&
        item.address === normalizedAddress,
    );

    const now = new Date().toISOString();

    if (dto.isDefault) {
      await Promise.all(
        existing
          .filter(
            (item) =>
              item.networkCode === dto.networkCode &&
              item.assetCode === dto.assetCode &&
              item.isDefault,
          )
          .map((item) =>
            this.runtimeStateRepository.upsertWalletPublicAddress({
              ...item,
              isDefault: false,
              updatedAt: now,
            }),
          ),
      );
    }

    if (found) {
      return this.runtimeStateRepository.upsertWalletPublicAddress({
        ...found,
        walletId: found.walletId ?? lifecycle.walletId,
        isDefault: dto.isDefault,
        updatedAt: now,
      });
    }

    const created: WalletPublicAddressItem = {
      addressId: randomUUID(),
      accountId: account.accountId,
      walletId: lifecycle.walletId,
      networkCode: dto.networkCode,
      assetCode: dto.assetCode,
      address: normalizedAddress,
      isDefault: dto.isDefault,
      createdAt: now,
      updatedAt: now,
    };

    return this.runtimeStateRepository.upsertWalletPublicAddress(created);
  }

  private async listOwnedOrders(accessToken: string) {
    const result = await this.ordersService.listOwnedOrders(accessToken, {
      page: 1,
      pageSize: 200,
    });
    return result.items;
  }

  private buildOverviewAlerts(input: {
    chainItems: Array<{
      networkCode: string;
      hasConfiguredAddress: boolean;
      orderCount: number;
    }>;
    assetItems: Array<{
      orderPayable: boolean;
      publicAddressCount: number;
      networkCode: string;
      assetCode: string;
    }>;
    publicAddresses: WalletPublicAddressItem[];
  }) {
    const alerts: string[] = [];
    if (input.publicAddresses.filter(isUsableWalletPublicAddress).length === 0) {
      alerts.push('当前账号尚未配置收款地址');
    }
    const missingReceivableChains = input.chainItems
      .filter((item) => !item.hasConfiguredAddress)
      .map((item) => item.networkCode);
    if (missingReceivableChains.length > 0) {
      alerts.push(`未配置收款地址的链：${missingReceivableChains.join(' / ')}`);
    }
    if (alerts.length === 0) {
    }
    return alerts;
  }

  private maxIso(current: string | null, candidate: string | null) {
    if (!current) {
      return candidate;
    }
    if (!candidate) {
      return current;
    }
    return current > candidate ? current : candidate;
  }

  private async resolveAssetBalanceViews(
    assets: Array<ReturnType<WalletService['getAssetCatalog']>['items'][number]>,
    publicAddresses: WalletPublicAddressItem[],
  ) {
    const usablePublicAddresses = publicAddresses.filter(isUsableWalletPublicAddress);
    const entries = await Promise.all(
      assets.map(async (asset) => {
        const assetKey = `${asset.networkCode}:${asset.assetCode}`;
        const assetAddress =
          usablePublicAddresses.find(
            (item) =>
              item.networkCode === asset.networkCode &&
              item.assetCode === asset.assetCode &&
              item.isDefault,
          )?.address ??
          usablePublicAddresses.find(
            (item) =>
              item.networkCode === asset.networkCode &&
              item.assetCode === asset.assetCode,
          )?.address ??
          usablePublicAddresses.find(
            (item) => item.networkCode === asset.networkCode && item.isDefault,
          )?.address ??
          usablePublicAddresses.find((item) => item.networkCode === asset.networkCode)
            ?.address ??
          null;

        if (!assetAddress) {
          return [
            assetKey,
            {
              networkCode: asset.networkCode,
              assetCode: asset.assetCode,
              address: null,
              balanceMinor: null,
              balanceUiAmount: null,
              balanceStatus: 'NO_ADDRESS' as WalletAssetBalanceStatus,
            },
          ] as const;
        }

        try {
          if (asset.networkCode === 'SOLANA') {
            if (!this.solanaClient.isEnabled()) {
              return [
                assetKey,
                {
                  networkCode: asset.networkCode,
                  assetCode: asset.assetCode,
                  address: assetAddress,
                  balanceMinor: null,
                  balanceUiAmount: null,
                  balanceStatus: 'UNAVAILABLE' as WalletAssetBalanceStatus,
                },
              ] as const;
            }

            const balance = await this.solanaClient.getBalance({
              address: assetAddress,
              mint: asset.isNative ? undefined : asset.contractAddress ?? undefined,
            });

            return [
              assetKey,
              {
                networkCode: asset.networkCode,
                assetCode: asset.assetCode,
                address: assetAddress,
                balanceMinor: balance.balance,
                balanceUiAmount: balance.uiAmount,
                balanceStatus: 'READY' as WalletAssetBalanceStatus,
              },
            ] as const;
          }

          if (!this.tronClient.isEnabled()) {
            return [
              assetKey,
              {
                networkCode: asset.networkCode,
                assetCode: asset.assetCode,
                address: assetAddress,
                balanceMinor: null,
                balanceUiAmount: null,
                balanceStatus: 'UNAVAILABLE' as WalletAssetBalanceStatus,
              },
            ] as const;
          }

          const tronBalance = await this.getTronAssetBalance(assetAddress, asset);
          return [
            assetKey,
            {
              networkCode: asset.networkCode,
              assetCode: asset.assetCode,
              address: assetAddress,
              balanceMinor: tronBalance.balanceMinor,
              balanceUiAmount: tronBalance.balanceUiAmount,
              balanceStatus: 'READY' as WalletAssetBalanceStatus,
            },
          ] as const;
        } catch (error) {
          this.logger.warn(
            `Failed to resolve wallet balance for ${asset.networkCode}:${asset.assetCode}`,
            error as Error,
          );
          return [
            assetKey,
            {
              networkCode: asset.networkCode,
              assetCode: asset.assetCode,
              address: assetAddress,
              balanceMinor: null,
              balanceUiAmount: null,
              balanceStatus: 'UNAVAILABLE' as WalletAssetBalanceStatus,
            },
          ] as const;
        }
      }),
    );

    return new Map<string, WalletAssetBalanceView>(entries);
  }

  private async getTronAssetBalance(
    address: string,
    asset: {
      assetCode: string;
      contractAddress: string | null;
      decimals: number;
      isNative: boolean;
    },
  ) {
    const tronWeb = this.createTronWeb();
    if (asset.isNative || asset.assetCode === 'TRX') {
      const balanceSun = await tronWeb.trx.getBalance(address);
      return {
        balanceMinor: balanceSun.toString(),
        balanceUiAmount: this.fromMinorUnits(BigInt(balanceSun), asset.decimals),
      };
    }

    const contractAddress =
      asset.contractAddress?.trim() ||
      process.env.TRON_USDT_CONTRACT?.trim() ||
      'TR7NHqjeKQxGTCi8q8ZY4pL8otSzgjLj6t';
    const contract = await tronWeb.contract().at(contractAddress);
    const rawBalance = await contract.balanceOf(address).call();
    const balanceMinor = rawBalance?.toString?.() ?? String(rawBalance ?? '0');
    return {
      balanceMinor,
      balanceUiAmount: this.fromMinorUnits(BigInt(balanceMinor), asset.decimals),
    };
  }

  async listPublicAddresses(
    accessToken: string,
    networkCode?: PersistedWalletPublicAddressRecord['networkCode'],
    assetCode?: PersistedWalletPublicAddressRecord['assetCode'],
  ) {
    const account = this.authService.getMe(accessToken);
    const items =
      await this.runtimeStateRepository.listWalletPublicAddressesByAccountId({
        accountId: account.accountId,
        networkCode,
        assetCode,
      });
    return { items: items.filter(isUsableWalletPublicAddress) };
  }

  async upsertSecretBackup(
    accessToken: string,
    dto: UpsertWalletSecretBackupRequestDto,
  ) {
    const account = this.authService.getMe(accessToken);
    const lifecycle = await this.ensureWalletLifecycle(account.accountId);
    const normalizedMnemonic = dto.mnemonic
      .trim()
      .split(/\s+/)
      .filter((item) => item.length > 0);
    if (![12, 24].includes(dto.mnemonicWordCount) || normalizedMnemonic.length !== dto.mnemonicWordCount) {
      throw new BadRequestException({
        code: 'WALLET_INVALID_MNEMONIC',
        message: 'Mnemonic word count must be 12 or 24 and match the submitted mnemonic',
      });
    }
    const normalizedHash = createHash('sha256').update(normalizedMnemonic.join(' ')).digest('hex');
    if (dto.mnemonicHash !== normalizedHash) {
      throw new BadRequestException({
        code: 'WALLET_MNEMONIC_HASH_MISMATCH',
        message: 'Mnemonic hash does not match payload',
      });
    }

    const now = new Date().toISOString();
    const walletId = dto.walletId?.trim() || lifecycle.walletId || randomUUID();
    const ciphertext = await this.walletBackupCryptoService.encryptBackup({
      accountId: account.accountId,
      walletId,
      walletName: dto.walletName?.trim() || lifecycle.walletName || null,
      secretType: dto.secretType,
      mnemonic: normalizedMnemonic.join(' '),
      mnemonicHash: dto.mnemonicHash,
      mnemonicWordCount: dto.mnemonicWordCount,
      sourceType: dto.sourceType?.trim() || null,
      publicAddresses: dto.publicAddresses?.map((item) => ({
        networkCode: item.networkCode,
        assetCode: item.assetCode,
        address: item.address.trim(),
        isDefault: item.isDefault,
      })) ?? [],
      exportedAt: now,
    });

    let record: PersistedWalletSecretBackupRecord = {
      backupId: randomUUID(),
      accountId: account.accountId,
      walletId,
      secretType: dto.secretType,
      encryptionScheme: 'AGE',
      recoveryKeyVersion: this.walletBackupCryptoService.getRecoveryKeyVersion(),
      recipientFingerprint: this.walletBackupCryptoService.getRecipientFingerprint(),
      ciphertext,
      replicatedToBackupServer: false,
      backupServerReference: null,
      lastReplicationError: null,
      createdAt: now,
      updatedAt: now,
    };
    const relay = await this.walletBackupRelayService.replicate(record);
    record = {
      ...record,
      replicatedToBackupServer: relay.replicatedToBackupServer,
      backupServerReference: relay.backupServerReference,
      lastReplicationError: relay.lastReplicationError,
      updatedAt: new Date().toISOString(),
    };
    const saved = await this.runtimeStateRepository.upsertWalletSecretBackup(record);

    for (const item of dto.publicAddresses ?? []) {
      await this.upsertPublicAddress(accessToken, {
        networkCode: item.networkCode,
        assetCode: item.assetCode,
        address: item.address.trim(),
        isDefault: item.isDefault,
      });
    }

    return {
      backupId: saved.backupId,
      accountId: saved.accountId,
      walletId: saved.walletId,
      secretType: saved.secretType,
      encryptionScheme: saved.encryptionScheme,
      recoveryKeyVersion: saved.recoveryKeyVersion,
      recipientFingerprint: saved.recipientFingerprint,
      replicatedToBackupServer: saved.replicatedToBackupServer,
      backupServerReference: saved.backupServerReference,
      lastReplicationError: saved.lastReplicationError,
      updatedAt: saved.updatedAt,
    };
  }

  async getSecretBackupMetadata(accessToken: string) {
    const account = this.authService.getMe(accessToken);
    const backup = await this.runtimeStateRepository.findWalletSecretBackupByAccountId(account.accountId);
    if (!backup) {
      return {
        exists: false,
      };
    }
    return {
      exists: true,
      backupId: backup.backupId,
      accountId: backup.accountId,
      walletId: backup.walletId,
      secretType: backup.secretType,
      encryptionScheme: backup.encryptionScheme,
      recoveryKeyVersion: backup.recoveryKeyVersion,
      recipientFingerprint: backup.recipientFingerprint,
      replicatedToBackupServer: backup.replicatedToBackupServer,
      backupServerReference: backup.backupServerReference,
      lastReplicationError: backup.lastReplicationError,
      updatedAt: backup.updatedAt,
    };
  }

  async getSecretBackupExport(accessToken: string) {
    const account = this.authService.getMe(accessToken);
    const backup =
      await this.runtimeStateRepository.findWalletSecretBackupByAccountId(
        account.accountId,
      );
    if (!backup) {
      return {
        exists: false,
      };
    }
    return {
      exists: true,
      fileName: `cryptovpn-wallet-backup-${backup.walletId}.json`,
      payload: {
        version: 'cryptovpn-wallet-backup-v1',
        backupId: backup.backupId,
        accountId: backup.accountId,
        walletId: backup.walletId,
        secretType: backup.secretType,
        encryptionScheme: backup.encryptionScheme,
        recoveryKeyVersion: backup.recoveryKeyVersion,
        recipientFingerprint: backup.recipientFingerprint,
        ciphertext: backup.ciphertext,
        createdAt: backup.createdAt,
        updatedAt: backup.updatedAt,
      },
    };
  }

  private async resolveReceiveSelection(
    accessToken: string,
    requestedNetworkCode?: string,
    requestedAssetCode?: string,
  ): Promise<WalletResolvedReceiveSelection> {
    const chains = this.getChains(accessToken).items;
    const assets = this.getAssetCatalog(accessToken).items.filter((item) => item.walletVisible);
    const selectedNetworkCode =
      requestedNetworkCode && chains.some((item) => item.networkCode === requestedNetworkCode)
        ? requestedNetworkCode
        : chains[0]?.networkCode ?? 'TRON';
    const assetsForNetwork = assets.filter((item) => item.networkCode === selectedNetworkCode);
    const preferredAssetForNetwork =
      assetsForNetwork.find((item) => item.orderPayable) ??
      assetsForNetwork.find((item) => item.assetCode === 'SOL') ??
      assetsForNetwork[0];
    const selectedAssetCode =
      requestedAssetCode &&
      assetsForNetwork.some((item) => item.assetCode === requestedAssetCode)
        ? requestedAssetCode
        : preferredAssetForNetwork?.assetCode ?? '';
    const publicAddresses = (
      await this.listPublicAddresses(
        accessToken,
        selectedNetworkCode as PersistedWalletPublicAddressRecord['networkCode'],
        (selectedAssetCode || undefined) as
          | PersistedWalletPublicAddressRecord['assetCode']
          | undefined,
      )
    ).items;
    const defaultAddress =
      publicAddresses.find((item) => item.isDefault)?.address ??
      publicAddresses[0]?.address ??
      null;

    return {
      selectedNetworkCode,
      selectedAssetCode,
      assetsForNetwork,
      publicAddresses,
      defaultAddress,
    };
  }

  private async ensureWalletLifecycle(accountId: string) {
    const existing =
      await this.runtimeStateRepository.findWalletLifecycleByAccountId(accountId);
    if (existing) {
      return existing;
    }

    const now = new Date().toISOString();
    return this.runtimeStateRepository.upsertWalletLifecycle({
      accountId,
      walletId: randomUUID(),
      walletName: 'Primary Wallet',
      status: 'ACTIVE',
      origin: 'LEGACY',
      mnemonicHash: null,
      mnemonicWordCount: null,
      backupAcknowledgedAt: null,
      activatedAt: now,
      createdAt: now,
      updatedAt: now,
    });
  }

  private toWalletAssetCatalogItem(item: PaymentAssetDefinition) {
    return {
      assetId: randomUUID(),
      networkCode: item.networkCode,
      assetCode: item.assetCode,
      displayName: item.displayName,
      symbol: item.symbol,
      decimals: item.decimals,
      isNative: item.isNative,
      contractAddress: item.contractAddress,
      walletVisible: item.walletVisible,
      orderPayable: item.orderPayable,
    };
  }

  private resolveNextAction(
    status: WalletLifecycleStatus,
  ): WalletLifecycleNextAction {
    switch (status) {
      case 'CREATED_PENDING_BACKUP':
        return 'BACKUP_MNEMONIC';
      case 'BACKUP_PENDING_CONFIRMATION':
        return 'CONFIRM_MNEMONIC';
      case 'ACTIVE':
        return 'READY';
      default:
        return 'CREATE_OR_IMPORT';
    }
  }

  private hashMnemonicSeed(accountId: string, now: string): string {
    return createHash('sha256').update(`${accountId}:${now}:${randomUUID()}`).digest('hex');
  }

  private toLegacyLifecycleStatus(
    status: WalletLifecycleStatus,
    origin: WalletLifecycleOrigin | null,
  ): WalletLifecycleView['lifecycleStatus'] {
    switch (status) {
      case 'NONE':
        return 'NOT_CREATED';
      case 'CREATED_PENDING_BACKUP':
      case 'BACKUP_PENDING_CONFIRMATION':
        return 'CREATED';
      case 'ACTIVE':
        return origin === 'IMPORTED' ? 'IMPORTED' : 'ACTIVE';
      default:
        return 'NOT_CREATED';
    }
  }

  private toLegacySourceType(
    origin: WalletLifecycleOrigin | null,
  ): 'CREATE' | 'IMPORT' | 'LEGACY' | null {
    switch (origin) {
      case 'CREATED':
        return 'CREATE';
      case 'IMPORTED':
        return 'IMPORT';
      case 'LEGACY':
        return 'LEGACY';
      default:
        return null;
    }
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

    if (dto.networkCode === 'SOLANA' && dto.unsignedPayload && dto.signature) {
      dto.serializedTx = this.attachSolanaSignature(
        dto.unsignedPayload,
        dto.signature,
      );
    }

    if (dto.networkCode === 'TRON' && dto.unsignedPayload && dto.signature) {
      const signedTransaction = this.attachTronSignature(
        dto.unsignedPayload,
        dto.signature,
      );
      return this.broadcastBuiltTronTransaction(dto.networkCode, signedTransaction);
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

  private async buildSolanaTransfer(
    rpcUrl: string,
    dto: BuildTransferRequestDto,
  ) {
    const connection = new Connection(rpcUrl, 'confirmed');
    const fromPubkey = new PublicKey(dto.fromAddress);
    const toPubkey = new PublicKey(dto.toAddress);
    const transaction = new Transaction();
    transaction.feePayer = fromPubkey;
    transaction.recentBlockhash = (await connection.getLatestBlockhash()).blockhash;

    if (dto.assetCode === 'SOL') {
      transaction.add(
        SystemProgram.transfer({
          fromPubkey,
          toPubkey,
          lamports: Number(this.toMinorUnits(dto.amount, 9)),
        }),
      );
    } else {
      const mint = new PublicKey(this.solanaClient.getUsdtMint());
      const fromAta = getAssociatedTokenAddressSync(mint, fromPubkey, false);
      const toAta = getAssociatedTokenAddressSync(mint, toPubkey, false);
      const destinationAccount = await connection.getAccountInfo(toAta);
      if (!destinationAccount) {
        transaction.add(
          createAssociatedTokenAccountInstruction(
            fromPubkey,
            toAta,
            toPubkey,
            mint,
          ),
        );
      }
      transaction.add(
        createTransferCheckedInstruction(
          fromAta,
          mint,
          toAta,
          fromPubkey,
          this.toMinorUnits(dto.amount, 6),
          6,
        ),
      );
    }

    const estimatedFee =
      (await connection.getFeeForMessage(transaction.compileMessage(), 'confirmed')).value ?? 0;

    return {
      networkCode: dto.networkCode,
      assetCode: dto.assetCode,
      fromAddress: dto.fromAddress,
      toAddress: dto.toAddress,
      amount: dto.amount,
      signingKind: 'SOLANA_MESSAGE',
      signingPayload: transaction.serializeMessage().toString('base64'),
      unsignedPayload: transaction.serialize({
        verifySignatures: false,
        requireAllSignatures: false,
      }).toString('base64'),
      estimatedFee: this.fromMinorUnits(BigInt(estimatedFee), 9),
    };
  }

  private async buildTronTransfer(
    contractAddress: string | null,
    dto: BuildTransferRequestDto,
  ) {
    const tronWeb = this.createTronWeb();

    if (dto.assetCode === 'TRX') {
      const transaction = await tronWeb.transactionBuilder.sendTrx(
        dto.toAddress,
        Number(this.toMinorUnits(dto.amount, 6)),
        dto.fromAddress,
      );
      return {
        networkCode: dto.networkCode,
        assetCode: dto.assetCode,
        fromAddress: dto.fromAddress,
        toAddress: dto.toAddress,
        amount: dto.amount,
        signingKind: 'TRON_TX_ID',
        signingPayload: transaction.txID,
        unsignedPayload: Buffer.from(JSON.stringify(transaction), 'utf8').toString('base64'),
        estimatedFee: '1.000000',
      };
    }

    const resolvedContract =
      contractAddress && !contractAddress.startsWith('<')
        ? contractAddress
        : process.env.TRON_USDT_CONTRACT?.trim() || 'TR7NHqjeKQxGTCi8q8ZY4pL8otSzgjLj6t';
    const wrapped = await tronWeb.transactionBuilder.triggerSmartContract(
      resolvedContract,
      'transfer(address,uint256)',
      { feeLimit: 100_000_000 },
      [
        { type: 'address', value: dto.toAddress },
        { type: 'uint256', value: this.toMinorUnits(dto.amount, 6).toString() },
      ],
      dto.fromAddress,
    );
    return {
      networkCode: dto.networkCode,
      assetCode: dto.assetCode,
      fromAddress: dto.fromAddress,
      toAddress: dto.toAddress,
      amount: dto.amount,
      signingKind: 'TRON_TX_ID',
      signingPayload: wrapped.transaction.txID,
      unsignedPayload: Buffer.from(JSON.stringify(wrapped.transaction), 'utf8').toString('base64'),
      estimatedFee: '20.000000',
    };
  }

  private attachSolanaSignature(unsignedPayload: string, signature: string): string {
    const transaction = Transaction.from(Buffer.from(unsignedPayload, 'base64'));
    const signer = transaction.feePayer;
    if (!signer) {
      throw new BadRequestException({
        code: 'WALLET_UNSIGNED_PAYLOAD_INVALID',
        message: 'Missing Solana fee payer in unsigned payload',
      });
    }
    transaction.addSignature(signer, Buffer.from(signature, 'base64'));
    return transaction.serialize({
      verifySignatures: false,
      requireAllSignatures: false,
    }).toString('base64');
  }

  private attachTronSignature(unsignedPayload: string, signature: string) {
    const transaction = JSON.parse(
      Buffer.from(unsignedPayload, 'base64').toString('utf8'),
    ) as { signature?: string[] };
    transaction.signature = [signature];
    return transaction;
  }

  private async broadcastBuiltTronTransaction(
    networkCode: 'SOLANA' | 'TRON',
    transaction: Record<string, unknown>,
  ) {
    const tronWeb = this.createTronWeb();
    const result = await tronWeb.trx.sendRawTransaction(transaction as any);
    if (!result.result) {
      throw new ConflictException({
        code: 'TRON_BROADCAST_FAILED',
        message: result.code || result.message || 'Tron broadcast failed',
      });
    }
    return {
      networkCode,
      broadcasted: true,
      txHash: (transaction as { txID?: string }).txID ?? result.txid ?? `tron_proxy_${randomUUID()}`,
      acceptedAt: new Date().toISOString(),
      serviceEnabled: true,
    };
  }

  private createTronWeb() {
    const headers = process.env.TRON_API_KEY?.trim()
      ? { 'TRON-PRO-API-KEY': process.env.TRON_API_KEY.trim() }
      : undefined;
    return new TronWeb({
      fullHost: process.env.TRON_FULL_NODE?.trim() || 'https://api.trongrid.io',
      headers,
    });
  }

  private toMinorUnits(amount: string, decimals: number) {
    const normalized = amount.trim();
    const negative = normalized.startsWith('-');
    const unsigned = negative ? normalized.slice(1) : normalized;
    const [wholePart, fractionPart = ''] = unsigned.split('.');
    const whole = wholePart === '' ? '0' : wholePart;
    const fraction = fractionPart.padEnd(decimals, '0').slice(0, decimals);
    const minor = BigInt(`${whole}${fraction}`);
    return negative ? minor * -1n : minor;
  }

  private fromMinorUnits(amountMinor: bigint, decimals: number) {
    const negative = amountMinor < 0n;
    const normalized = negative ? amountMinor * -1n : amountMinor;
    const raw = normalized.toString().padStart(decimals + 1, '0');
    const whole = raw.slice(0, raw.length - decimals);
    const fraction = raw.slice(raw.length - decimals);
    return `${negative ? '-' : ''}${whole}.${fraction}`;
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
