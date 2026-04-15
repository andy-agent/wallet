export type WalletLifecycleStatus =
  | 'NONE'
  | 'CREATED_PENDING_BACKUP'
  | 'BACKUP_PENDING_CONFIRMATION'
  | 'ACTIVE';

export type WalletLifecycleOrigin = 'CREATED' | 'IMPORTED' | 'LEGACY';

export type WalletLifecycleNextAction =
  | 'CREATE_OR_IMPORT'
  | 'BACKUP_MNEMONIC'
  | 'CONFIRM_MNEMONIC'
  | 'READY';

export interface PersistedWalletLifecycleRecord {
  accountId: string;
  walletId: string;
  walletName: string;
  status: Exclude<WalletLifecycleStatus, 'NONE'>;
  origin: WalletLifecycleOrigin;
  mnemonicHash: string | null;
  mnemonicWordCount: number | null;
  backupAcknowledgedAt: string | null;
  activatedAt: string | null;
  createdAt: string;
  updatedAt: string;
}

export interface PersistedWalletPublicAddressRecord {
  addressId: string;
  accountId: string;
  walletId: string | null;
  networkCode: 'SOLANA' | 'TRON';
  assetCode: 'SOL' | 'TRX' | 'USDT';
  address: string;
  isDefault: boolean;
  createdAt: string;
  updatedAt: string;
}

export interface PersistedWalletSecretBackupRecord {
  backupId: string;
  accountId: string;
  walletId: string;
  secretType: 'MNEMONIC';
  encryptionScheme: 'AGE';
  recoveryKeyVersion: string;
  recipientFingerprint: string;
  ciphertext: string;
  replicatedToBackupServer: boolean;
  backupServerReference: string | null;
  lastReplicationError: string | null;
  createdAt: string;
  updatedAt: string;
}

export interface WalletLifecycleView {
  accountId: string;
  walletExists: boolean;
  receiveReady: boolean;
  receiveState: 'NO_WALLET' | 'NO_ADDRESS' | 'READY';
  walletId: string | null;
  walletName: string | null;
  lifecycleStatus?: WalletLifecycleStatus | 'NOT_CREATED' | 'CREATED' | 'IMPORTED';
  sourceType?: WalletLifecycleOrigin | 'CREATE' | 'IMPORT' | null;
  displayName?: string | null;
  status: WalletLifecycleStatus;
  origin: WalletLifecycleOrigin | null;
  nextAction: WalletLifecycleNextAction;
  hasAnyPublicAddress: boolean;
  configuredAddressCount: number;
  source: 'RUNTIME_STATE' | 'PUBLIC_ADDRESS_FALLBACK' | 'EMPTY';
  createdAt: string | null;
  updatedAt: string | null;
  backupAcknowledgedAt: string | null;
  activatedAt: string | null;
}
