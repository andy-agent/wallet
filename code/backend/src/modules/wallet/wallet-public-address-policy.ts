import { PublicKey } from '@solana/web3.js';
import { TronWeb } from 'tronweb';
import type { PersistedWalletPublicAddressRecord } from './wallet.types';

const PLACEHOLDER_ADDRESSES: Record<
  PersistedWalletPublicAddressRecord['networkCode'],
  Set<string>
> = {
  SOLANA: new Set([
    'So11111111111111111111111111111111111111112',
  ]),
  TRON: new Set([
    'TQn9Y2khEsLJW1ChVWFMSMeRDow5KcbLSE',
  ]),
};

export function normalizeWalletPublicAddress(address: string) {
  return address.trim();
}

export function isKnownPlaceholderWalletPublicAddress(
  networkCode: PersistedWalletPublicAddressRecord['networkCode'],
  address: string,
) {
  return PLACEHOLDER_ADDRESSES[networkCode].has(normalizeWalletPublicAddress(address));
}

export function isValidWalletPublicAddress(
  networkCode: PersistedWalletPublicAddressRecord['networkCode'],
  address: string,
) {
  const normalizedAddress = normalizeWalletPublicAddress(address);
  if (!normalizedAddress) {
    return false;
  }

  if (networkCode === 'SOLANA') {
    try {
      new PublicKey(normalizedAddress);
      return true;
    } catch {
      return false;
    }
  }

  return TronWeb.isAddress(normalizedAddress);
}

export function isUsableWalletPublicAddress(
  item: Pick<PersistedWalletPublicAddressRecord, 'networkCode' | 'address'>,
) {
  return (
    isValidWalletPublicAddress(item.networkCode, item.address) &&
    !isKnownPlaceholderWalletPublicAddress(item.networkCode, item.address)
  );
}
