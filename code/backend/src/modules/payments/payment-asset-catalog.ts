import type { ConfigService } from '@nestjs/config';
import type { SolanaClientService } from '../solana-client/solana-client.service';

export type SupportedQuoteNetworkCode =
  | 'ETHEREUM'
  | 'BSC'
  | 'POLYGON'
  | 'ARBITRUM'
  | 'BASE'
  | 'OPTIMISM'
  | 'AVALANCHE_C'
  | 'SOLANA'
  | 'TRON';

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
  iconUrl: string | null;
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
  iconUrl?: string;
}

const DEFAULT_TRON_USDT_CONTRACT = 'TR7NHqjeKQxGTCi8q8ZY4pL8otSzgjLj6t';
const DEFAULT_TRON_USDD_CONTRACT = 'TEkxiTehnzSmSe2XqrBj4w32RUN966rdz8';
const DEFAULT_ETHEREUM_USDT_CONTRACT = '0xdAC17F958D2ee523a2206206994597C13D831ec7';
const DEFAULT_ETHEREUM_USDC_CONTRACT = '0xA0b86991c6218b36c1d19D4a2e9Eb0cE3606eB48';
const DEFAULT_BSC_USDT_CONTRACT = '0x55d398326f99059fF775485246999027B3197955';
const DEFAULT_BSC_FDUSD_CONTRACT = '0xc5f0f7b66764f6ec8c8dff7ba683102295e16409';
const DEFAULT_POLYGON_USDC_CONTRACT = '0x3c499c542cef5e3811e1192ce70d8cc03d5c3359';
const DEFAULT_POLYGON_WETH_CONTRACT = '0x7ceB23fD6bC0adD59E62ac25578270cFf1b9f619';
const DEFAULT_ARBITRUM_USDC_CONTRACT = '0xaf88d065e77c8cC2239327C5EDb3A432268e5831';
const DEFAULT_ARBITRUM_ARB_CONTRACT = '0x912CE59144191C1204E64559FE8253a0e49E6548';
const DEFAULT_BASE_USDC_CONTRACT = '0x833589fCD6EDB6E08f4c7C32D4f71b54bdA02913';
const DEFAULT_BASE_CBBTC_CONTRACT = '0xcbb7c0000ab88b473b1f5afd9ef808440eed33bf';
const DEFAULT_OPTIMISM_USDC_CONTRACT = '0x0b2C639c533813f4Aa9D7837CaF62653d097Ff85';
const DEFAULT_OPTIMISM_OP_CONTRACT = '0x4200000000000000000000000000000000000042';
const DEFAULT_AVALANCHE_USDC_CONTRACT = '0xB97EF9Ef8734C71904D8002F8b6Bc66Dd9c48a6E';
const DEFAULT_AVALANCHE_USDT_CONTRACT = '0x9702230A8EA53601f5cD2dc00fDBc13d4dF4A8c7';
const DEFAULT_SOLANA_USDC_MINT = 'EPjFWdd5AufqSSqeM2qN1xzybapC8G4wEGGkZwyTDt1v';

const DEFAULT_SOLANA_ORDER_ASSETS: SolanaCustomAssetConfig[] = [
  {
    assetCode: 'ANDY',
    displayName: 'ANDY (Solana)',
    symbol: 'ANDY',
    decimals: 9,
    contractAddress: '8zFP8GeszFz7FvuHesguekTxDjm4KLsJEYBZTKyMLEoE',
    walletVisible: true,
    orderPayable: true,
  },
];

const SYMBOL_ICON_ALIASES: Record<string, string> = {
  CBBTC: 'btc',
  WETH: 'eth',
};

export function buildPaymentAssetCatalog(
  configService: ConfigService,
  solanaClient: SolanaClientService,
): PaymentAssetDefinition[] {
  const solanaOrderPayable = hasSolanaOrderCapability(configService, solanaClient);
  const items: PaymentAssetDefinition[] = [
    nativeAsset('ETHEREUM', 'ETH', 'Ethereum', 'ethereum'),
    stableAsset('ETHEREUM', 'USDT', 'Tether USD (Ethereum)', DEFAULT_ETHEREUM_USDT_CONTRACT),
    stableAsset('ETHEREUM', 'USDC', 'USD Coin (Ethereum)', DEFAULT_ETHEREUM_USDC_CONTRACT),

    nativeAsset('BSC', 'BNB', 'BNB Smart Chain', 'binancecoin'),
    stableAsset('BSC', 'USDT', 'Tether USD (BSC)', DEFAULT_BSC_USDT_CONTRACT, 18),
    stableAsset('BSC', 'FDUSD', 'First Digital USD (BSC)', DEFAULT_BSC_FDUSD_CONTRACT, 18),

    nativeAsset('POLYGON', 'POL', 'POL (Polygon)', 'polygon-ecosystem-token'),
    stableAsset('POLYGON', 'USDC', 'USD Coin (Polygon)', DEFAULT_POLYGON_USDC_CONTRACT),
    tokenAsset({
      networkCode: 'POLYGON',
      assetCode: 'WETH',
      displayName: 'Wrapped Ether (Polygon)',
      symbol: 'WETH',
      decimals: 18,
      contractAddress: DEFAULT_POLYGON_WETH_CONTRACT,
      marketInstrumentId: 'ethereum',
    }),

    nativeAsset('ARBITRUM', 'ETH', 'Ether (Arbitrum)', 'ethereum'),
    stableAsset('ARBITRUM', 'USDC', 'USD Coin (Arbitrum)', DEFAULT_ARBITRUM_USDC_CONTRACT),
    tokenAsset({
      networkCode: 'ARBITRUM',
      assetCode: 'ARB',
      displayName: 'Arbitrum',
      symbol: 'ARB',
      decimals: 18,
      contractAddress: DEFAULT_ARBITRUM_ARB_CONTRACT,
      marketInstrumentId: 'arbitrum',
    }),

    nativeAsset('BASE', 'ETH', 'Ether (Base)', 'ethereum'),
    stableAsset('BASE', 'USDC', 'USD Coin (Base)', DEFAULT_BASE_USDC_CONTRACT),
    tokenAsset({
      networkCode: 'BASE',
      assetCode: 'CBBTC',
      displayName: 'Coinbase Wrapped BTC',
      symbol: 'cbBTC',
      decimals: 8,
      contractAddress: DEFAULT_BASE_CBBTC_CONTRACT,
      marketInstrumentId: 'bitcoin',
      iconSymbol: 'BTC',
    }),

    nativeAsset('OPTIMISM', 'ETH', 'Ether (Optimism)', 'ethereum'),
    stableAsset('OPTIMISM', 'USDC', 'USD Coin (Optimism)', DEFAULT_OPTIMISM_USDC_CONTRACT),
    tokenAsset({
      networkCode: 'OPTIMISM',
      assetCode: 'OP',
      displayName: 'Optimism',
      symbol: 'OP',
      decimals: 18,
      contractAddress: DEFAULT_OPTIMISM_OP_CONTRACT,
      marketInstrumentId: 'optimism',
    }),

    nativeAsset('AVALANCHE_C', 'AVAX', 'Avalanche', 'avalanche-2'),
    stableAsset('AVALANCHE_C', 'USDC', 'USD Coin (Avalanche)', DEFAULT_AVALANCHE_USDC_CONTRACT),
    stableAsset('AVALANCHE_C', 'USDT', 'Tether USD (Avalanche)', DEFAULT_AVALANCHE_USDT_CONTRACT),

    nativeAsset('SOLANA', 'SOL', 'Solana', 'solana', solanaOrderPayable),
    stableAsset('SOLANA', 'USDC', 'USD Coin (Solana)', DEFAULT_SOLANA_USDC_MINT),
    tokenAsset({
      networkCode: 'SOLANA',
      assetCode: 'USDT',
      displayName: 'Tether USD (Solana)',
      symbol: 'USDT',
      decimals: 6,
      contractAddress: solanaClient.getUsdtMint(),
      usdPriceValue: '1',
      orderPayable: solanaOrderPayable,
    }),

    nativeAsset('TRON', 'TRX', 'TRON', 'tron'),
    tokenAsset({
      networkCode: 'TRON',
      assetCode: 'USDT',
      displayName: 'Tether USD (TRC20)',
      symbol: 'USDT',
      decimals: 6,
      contractAddress:
        configService.get<string>('TRON_USDT_CONTRACT')?.trim() ||
        DEFAULT_TRON_USDT_CONTRACT,
      usdPriceValue: '1',
      orderPayable: true,
    }),
    tokenAsset({
      networkCode: 'TRON',
      assetCode: 'USDD',
      displayName: 'USDD (TRON)',
      symbol: 'USDD',
      decimals: 18,
      contractAddress:
        configService.get<string>('TRON_USDD_CONTRACT')?.trim() ||
        DEFAULT_TRON_USDD_CONTRACT,
      marketInstrumentId: 'usdd',
    }),
  ];

  items.push(
    ...mergeSolanaCustomAssets(configService)
      .map((item) => toSolanaCustomPaymentAsset(item, solanaOrderPayable))
      .filter((item): item is PaymentAssetDefinition => item !== null),
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
  const explicitToggle = configService
    .get<string>('SOLANA_ORDER_PAYMENT_ENABLED')
    ?.trim()
    .toLowerCase();

  if (explicitToggle === 'false') {
    return false;
  }

  const configured = configService
    .get<string>('SOLANA_ORDER_COLLECTION_ADDRESS')
    ?.trim();

  if (!configured) {
    return false;
  }

  if (typeof solanaClient.validateAddress !== 'function') {
    return explicitToggle === 'true' || explicitToggle == null;
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

function mergeSolanaCustomAssets(
  configService: ConfigService,
): SolanaCustomAssetConfig[] {
  const configured = parseSolanaCustomAssets(configService);
  const merged = new Map<string, SolanaCustomAssetConfig>();
  for (const item of DEFAULT_SOLANA_ORDER_ASSETS) {
    merged.set(item.assetCode.trim().toUpperCase(), item);
  }
  for (const item of configured) {
    merged.set(item.assetCode.trim().toUpperCase(), item);
  }
  return Array.from(merged.values());
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
  const usdPriceMode = marketInstrumentId || !usdPrice ? 'market' : 'fixed';
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
    iconUrl: item.iconUrl?.trim() || iconUrlForSymbol(item.symbol?.trim() || assetCode),
  };
}

function nativeAsset(
  networkCode: SupportedQuoteNetworkCode,
  assetCode: string,
  displayName: string,
  marketInstrumentId: string,
  orderPayable = false,
): PaymentAssetDefinition {
  return {
    networkCode,
    assetCode,
    displayName,
    symbol: assetCode,
    decimals: networkCode === 'TRON' ? 6 : networkCode === 'SOLANA' ? 9 : 18,
    isNative: true,
    contractAddress: null,
    walletVisible: true,
    orderPayable,
    usdPriceMode: 'market',
    usdPriceValue: null,
    marketInstrumentId,
    iconUrl: iconUrlForSymbol(assetCode),
  };
}

function stableAsset(
  networkCode: SupportedQuoteNetworkCode,
  assetCode: string,
  displayName: string,
  contractAddress: string,
  decimals = 6,
): PaymentAssetDefinition {
  return tokenAsset({
    networkCode,
    assetCode,
    displayName,
    symbol: assetCode,
    decimals,
    contractAddress,
    usdPriceValue: '1',
  });
}

function tokenAsset({
  networkCode,
  assetCode,
  displayName,
  symbol,
  decimals,
  contractAddress,
  marketInstrumentId = null,
  usdPriceValue = null,
  walletVisible = true,
  orderPayable = false,
  iconSymbol,
}: {
  networkCode: SupportedQuoteNetworkCode;
  assetCode: string;
  displayName: string;
  symbol: string;
  decimals: number;
  contractAddress: string;
  marketInstrumentId?: string | null;
  usdPriceValue?: string | null;
  walletVisible?: boolean;
  orderPayable?: boolean;
  iconSymbol?: string;
}): PaymentAssetDefinition {
  return {
    networkCode,
    assetCode,
    displayName,
    symbol,
    decimals,
    isNative: false,
    contractAddress,
    walletVisible,
    orderPayable,
    usdPriceMode: usdPriceValue ? 'fixed' : 'market',
    usdPriceValue,
    marketInstrumentId,
    iconUrl: iconUrlForSymbol(iconSymbol || symbol),
  };
}

function dedupeAssets(items: PaymentAssetDefinition[]): PaymentAssetDefinition[] {
  const deduped = new Map<string, PaymentAssetDefinition>();
  for (const item of items) {
    deduped.set(`${item.networkCode}:${item.assetCode.toUpperCase()}`, item);
  }
  return Array.from(deduped.values());
}

function iconUrlForSymbol(symbol: string): string {
  const normalized = symbol.trim().toUpperCase();
  const assetSlug = SYMBOL_ICON_ALIASES[normalized] ?? normalized.toLowerCase();
  return `https://cdn.jsdelivr.net/gh/spothq/cryptocurrency-icons@master/32/color/${assetSlug}.png`;
}
