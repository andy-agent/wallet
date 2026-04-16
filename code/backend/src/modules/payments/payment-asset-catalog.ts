import type { ConfigService } from '@nestjs/config';
import type { SolanaClientService } from '../solana-client/solana-client.service';

export type SupportedQuoteNetworkCode = 'SOLANA' | 'TRON';

export interface PaymentAssetDefinition {
  networkCode: SupportedQuoteNetworkCode;
  assetCode: string;
  displayName: string;
  symbol: string;
  decimals: number;
  isNative: boolean;
  contractAddress: string | null;
  walletVisible: boolean;
  orderPayable: boolean;
  usdPriceMode: 'fixed' | 'market';
  usdPriceValue: string | null;
  marketInstrumentId: string | null;
}

interface SolanaCustomAssetConfig {
  assetCode: string;
  displayName?: string;
  symbol?: string;
  decimals?: number;
  contractAddress: string;
  walletVisible?: boolean;
  orderPayable?: boolean;
  usdPrice?: string;
  marketInstrumentId?: string;
}

const DEFAULT_TRON_USDT_CONTRACT = 'TR7NHqjeKQxGTCi8q8ZY4pL8otSzgjLj6t';

export function buildPaymentAssetCatalog(
  configService: ConfigService,
  solanaClient: SolanaClientService,
): PaymentAssetDefinition[] {
  const solanaOrderPayable = hasSolanaOrderCapability(configService, solanaClient);
  const items: PaymentAssetDefinition[] = [
    {
      networkCode: 'SOLANA',
      assetCode: 'SOL',
      displayName: 'Solana',
      symbol: 'SOL',
      decimals: 9,
      isNative: true,
      contractAddress: null,
      walletVisible: true,
      orderPayable: solanaOrderPayable,
      usdPriceMode: 'market',
      usdPriceValue: null,
      marketInstrumentId: 'solana',
    },
    {
      networkCode: 'SOLANA',
      assetCode: 'USDT',
      displayName: 'Tether USD (Solana)',
      symbol: 'USDT',
      decimals: 6,
      isNative: false,
      contractAddress: solanaClient.getUsdtMint(),
      walletVisible: true,
      orderPayable: solanaOrderPayable,
      usdPriceMode: 'fixed',
      usdPriceValue: '1',
      marketInstrumentId: null,
    },
    {
      networkCode: 'TRON',
      assetCode: 'TRX',
      displayName: 'TRON',
      symbol: 'TRX',
      decimals: 6,
      isNative: true,
      contractAddress: null,
      walletVisible: true,
      orderPayable: false,
      usdPriceMode: 'market',
      usdPriceValue: null,
      marketInstrumentId: 'tron',
    },
    {
      networkCode: 'TRON',
      assetCode: 'USDT',
      displayName: 'Tether USD (TRC20)',
      symbol: 'USDT',
      decimals: 6,
      isNative: false,
      contractAddress:
        configService.get<string>('TRON_USDT_CONTRACT')?.trim() ||
        DEFAULT_TRON_USDT_CONTRACT,
      walletVisible: true,
      orderPayable: true,
      usdPriceMode: 'fixed',
      usdPriceValue: '1',
      marketInstrumentId: null,
    },
  ];

  items.push(
    ...parseSolanaCustomAssets(configService)
      .map((item) => toSolanaCustomPaymentAsset(item, solanaOrderPayable))
      .filter((item) => item !== null),
  );

  return dedupeAssets(items);
}

export function resolvePaymentAsset(
  configService: ConfigService,
  solanaClient: SolanaClientService,
  networkCode: string,
  assetCode: string,
): PaymentAssetDefinition | null {
  const normalizedNetwork = networkCode.trim().toUpperCase();
  const normalizedAssetCode = assetCode.trim().toUpperCase();
  return (
    buildPaymentAssetCatalog(configService, solanaClient).find(
      (item) =>
        item.networkCode === normalizedNetwork &&
        item.assetCode.toUpperCase() === normalizedAssetCode,
    ) ?? null
  );
}

export function hasSolanaOrderCapability(
  configService: ConfigService,
  solanaClient: SolanaClientService,
): boolean {
  const configured = configService
    .get<string>('SOLANA_ORDER_COLLECTION_ADDRESS')
    ?.trim();

  if (!configured) {
    return false;
  }

  if (typeof solanaClient.validateAddress !== 'function') {
    return true;
  }

  return solanaClient.validateAddress(configured);
}

function parseSolanaCustomAssets(
  configService: ConfigService,
): SolanaCustomAssetConfig[] {
  const raw = configService
    .get<string>('SOLANA_CUSTOM_ORDER_ASSETS_JSON')
    ?.trim();

  if (!raw) {
    return [];
  }

  try {
    const parsed = JSON.parse(raw);
    if (!Array.isArray(parsed)) {
      return [];
    }
    return parsed.filter((item): item is SolanaCustomAssetConfig => {
      return Boolean(
        item &&
          typeof item === 'object' &&
          typeof item.assetCode === 'string' &&
          typeof item.contractAddress === 'string',
      );
    });
  } catch {
    return [];
  }
}

function toSolanaCustomPaymentAsset(
  item: SolanaCustomAssetConfig,
  solanaOrderPayable: boolean,
): PaymentAssetDefinition | null {
  const assetCode = item.assetCode.trim().toUpperCase();
  const contractAddress = item.contractAddress.trim();
  if (!assetCode || !contractAddress) {
    return null;
  }

  const usdPrice = item.usdPrice?.trim() || null;
  const marketInstrumentId = item.marketInstrumentId?.trim() || null;
  const usdPriceMode = marketInstrumentId ? 'market' : 'fixed';
  if (usdPriceMode === 'fixed' && (!usdPrice || Number(usdPrice) <= 0)) {
    return null;
  }

  return {
    networkCode: 'SOLANA',
    assetCode,
    displayName: item.displayName?.trim() || `${assetCode} (Solana)`,
    symbol: item.symbol?.trim() || assetCode,
    decimals: item.decimals && item.decimals > 0 ? item.decimals : 6,
    isNative: false,
    contractAddress,
    walletVisible: item.walletVisible !== false,
    orderPayable: solanaOrderPayable && item.orderPayable !== false,
    usdPriceMode,
    usdPriceValue: usdPrice,
    marketInstrumentId,
  };
}

function dedupeAssets(items: PaymentAssetDefinition[]): PaymentAssetDefinition[] {
  const deduped = new Map<string, PaymentAssetDefinition>();
  for (const item of items) {
    deduped.set(`${item.networkCode}:${item.assetCode.toUpperCase()}`, item);
  }
  return Array.from(deduped.values());
}
