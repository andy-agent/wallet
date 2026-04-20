export type SupportedCustomTokenChainId =
  | 'solana'
  | 'ethereum'
  | 'bsc'
  | 'polygon'
  | 'arbitrum'
  | 'base'
  | 'optimism'
  | 'avalanche'
  | 'tron';

export type SearchMode = 'keyword' | 'address';

export interface CustomTokenSearchCandidate {
  tokenAddress: string;
  name: string;
  symbol: string;
  decimals: number;
  iconUrl: string | null;
  chainId: SupportedCustomTokenChainId;
}

export interface ChainTokenSearchProvider {
  readonly providerName: string;
  searchByKeyword(
    chainId: SupportedCustomTokenChainId,
    query: string,
  ): Promise<CustomTokenSearchCandidate[]>;
  resolveByAddress(
    chainId: SupportedCustomTokenChainId,
    address: string,
  ): Promise<CustomTokenSearchCandidate | null>;
}

export function normalizeCustomTokenChainId(
  chainId?: string,
): SupportedCustomTokenChainId | null {
  const normalized = chainId?.trim().toLowerCase();
  if (!normalized) {
    return null;
  }
  switch (normalized) {
    case 'solana':
    case 'ethereum':
    case 'bsc':
    case 'polygon':
    case 'arbitrum':
    case 'base':
    case 'optimism':
    case 'tron':
      return normalized;
    case 'avalanche':
    case 'avalanche_c':
      return 'avalanche';
    default:
      return null;
  }
}

export function isEvmCustomTokenChainId(
  chainId: SupportedCustomTokenChainId,
): boolean {
  return [
    'ethereum',
    'bsc',
    'polygon',
    'arbitrum',
    'base',
    'optimism',
    'avalanche',
  ].includes(chainId);
}

export function detectCustomTokenSearchMode(
  chainId: SupportedCustomTokenChainId,
  query: string,
): SearchMode {
  const normalized = query.trim();
  if (chainId === 'solana') {
    return /^[1-9A-HJ-NP-Za-km-z]{32,44}$/.test(normalized)
      ? 'address'
      : 'keyword';
  }
  if (chainId === 'tron') {
    return /^T[0-9A-Za-z]{33}$/.test(normalized) ? 'address' : 'keyword';
  }
  return /^0x[a-fA-F0-9]{40}$/.test(normalized) ? 'address' : 'keyword';
}

export function dedupeCustomTokenCandidates(
  items: CustomTokenSearchCandidate[],
): CustomTokenSearchCandidate[] {
  const seen = new Set<string>();
  const deduped: CustomTokenSearchCandidate[] = [];
  for (const item of items) {
    const key = item.tokenAddress.trim().toLowerCase();
    if (!key || seen.has(key)) {
      continue;
    }
    seen.add(key);
    deduped.push(item);
  }
  return deduped;
}
