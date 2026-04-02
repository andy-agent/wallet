export interface ReferralBindingRecord {
  inviteeAccountId: string;
  inviterLevel1AccountId: string | null;
  inviterLevel2AccountId: string | null;
  codeUsed: string;
  status: 'BOUND' | 'LOCKED' | 'INVALIDATED';
  boundAt: string;
  lockedAt: string | null;
}

export interface CommissionLedgerRecord {
  entryNo: string;
  beneficiaryAccountId: string;
  sourceOrderNo: string;
  sourceAccountId: string;
  commissionLevel: 'LEVEL1' | 'LEVEL2';
  sourceAssetCode: 'SOL' | 'USDT';
  sourceAmount: string;
  fxRateSnapshot: string;
  settlementAmountUsdt: string;
  status: 'FROZEN' | 'AVAILABLE' | 'LOCKED_WITHDRAWAL' | 'WITHDRAWN' | 'REVERSED';
  createdAt: string;
  availableAt: string | null;
}
